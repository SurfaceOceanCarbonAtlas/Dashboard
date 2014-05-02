/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;
import gov.noaa.pmel.socat.dashboard.shared.SocatQCFlag;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;

/**
 * @author Karl Smith
 */
public class DatabaseRequestHandler {

	private static final String CATALOG_NAME_TAG = "catalogname";
	private static final String SELECT_USER_TAG = "selectuser";
	private static final String SELECT_PASS_TAG = "selectpass";
	private static final String UPDATE_USER_TAG = "updateuser";
	private static final String UPDATE_PASS_TAG = "updatepass";

	String catalogName;
	String selectUser;
	String selectPass;
	String updateUser;
	String updatePass;

	/**
	 * Create using the given configuration properties file.
	 * 
	 * @param configFilename
	 * 		name of the configuration properties file
	 * @throws FileNotFoundException
	 * 		if the properties file does not exist
	 * @throws IOException
	 * 		if the properties file cannot cannot be read 
	 * @throws IllegalArgumentException 
	 * 		if the properties file has missing or invalid values
	 * @throws SQLException 
	 * 		if there are problems connecting
	 * 		to the database
	 */
	DatabaseRequestHandler(String configFilename) throws FileNotFoundException, 
						IOException, IllegalArgumentException, SQLException {
		// Read the configuration properties file
		Properties configProps = new Properties();
		FileReader reader = new FileReader(configFilename);
		try {
			configProps.load(reader);
		} finally {
			reader.close();
		}

		// Get the values given in the configuration properties file
		catalogName = configProps.getProperty(CATALOG_NAME_TAG);
		if ( catalogName == null )
			throw new IllegalArgumentException("Value for " + CATALOG_NAME_TAG + 
					" not given in " + configFilename);
		selectUser = configProps.getProperty(SELECT_USER_TAG);
		if ( selectUser == null )
			throw new IllegalArgumentException("Value for " + SELECT_USER_TAG + 
					" not given in " + configFilename);
		selectPass = configProps.getProperty(SELECT_PASS_TAG);
		if ( selectPass == null )
			throw new IllegalArgumentException("Value for " + SELECT_PASS_TAG + 
					" not given in " + configFilename);
		updateUser = configProps.getProperty(UPDATE_USER_TAG);
		if ( updateUser == null )
			throw new IllegalArgumentException("Value for " + UPDATE_USER_TAG + 
					" not given in " + configFilename);
		updatePass = configProps.getProperty(UPDATE_PASS_TAG);
		if ( updatePass == null )
			throw new IllegalArgumentException("Value for " + UPDATE_PASS_TAG + 
					" not given in " + configFilename);

		// Register the MySQL driver - no harm if already registered
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			throw new SQLException(
					"Unable to register the MySQL driver\n" + ex.getMessage());
		}

		// Verify the values by making the database connections
		boolean canSelect = false;
		Connection selectConn = makeConnection(false);
		try {
			ResultSet result = 
					selectConn.prepareStatement("SHOW GRANTS;").executeQuery();
			try {
				while ( result.next() ) {
					String grants = result.getString(1);
					if ( grants.contains("ON `" + catalogName + "`.*") || 
						 grants.contains("ON *.*") ) { 
						if ( grants.contains("ALL PRIVILEGES") || 
							 grants.contains("SELECT") ) {
							canSelect = true;
							break;
						}
					}
				}
			} finally {
				result.close();
			}
		} finally {
			selectConn.close();
		}
		if ( ! canSelect )
			throw new IllegalArgumentException(
					"The select-only user does not have SELECT privileges");

		canSelect = false;
		boolean canUpdate = false;
		boolean canInsert = false;
		boolean canDelete = false;
		selectConn = makeConnection(true);
		try {
			ResultSet result = 
					selectConn.prepareStatement("SHOW GRANTS;").executeQuery();
			try {
				while ( result.next() ) {
					String grants = result.getString(1);
					if ( grants.contains("ON `" + catalogName + "`.*") || 
						 grants.contains("ON *.*") ) { 
						if ( grants.contains("ALL PRIVILEGES") ) {
							canSelect = true;
							canUpdate = true;
							canInsert = true;
							canDelete = true;
							break;
						}
						if ( grants.contains("SELECT") )
							canSelect = true;
						if ( grants.contains("UPDATE") )
							canUpdate = true;
						if ( grants.contains("INSERT") )
							canInsert = true;
						if ( grants.contains("DELETE") )
							canDelete = true;
					}
				}
			} finally {
				result.close();
			}
		} finally {
			selectConn.close();
		}
		if ( ! canSelect )
			throw new IllegalArgumentException(
					"The update user does not have SELECT privileges");
		if ( ! canUpdate )
			throw new IllegalArgumentException(
					"The update user does not have UPDATE privileges");
		if ( ! canInsert )
			throw new IllegalArgumentException(
					"The update user does not have INSERT privileges");
		if ( ! canDelete )
			throw new IllegalArgumentException(
					"The update user does not have DELETE privileges");
	}

	/**
	 * Creates a connection to the associated database catalog.
	 * 
	 * @param canUpdate
	 * 		if true, the connection will be made using updateUser;
	 * 		otherwise, the connection will be made using selectUser
	 * @return
	 * 		the database catalog connection
	 * @throws SQLException
	 * 		if connecting to the database catalog throws one or
	 * 		if a null connection is returned
	 */
	private Connection makeConnection(boolean canUpdate) throws SQLException {
		// Open a connection to the database
		String databaseUrl = "jdbc:mysql://localhost:3306/" + catalogName;
		Connection catConn;
		if ( canUpdate ) {
			catConn = DriverManager.getConnection(databaseUrl, updateUser, updatePass);
		}
		else { 	
			catConn = DriverManager.getConnection(databaseUrl, selectUser, selectPass);
		}
		if ( catConn == null )
			throw new SQLException("null MySQL connection returned");
		return catConn;
	}

	private static final String GET_REVIEWER_ID_SQL = 
			"SELECT `reviewer_id` FROM `reviewers` where `username` = ?;";

	private static final String SET_QC_FLAG_SQL = 
			"INSERT INTO `qcflags` (`qc_flag`, `expocode`, `socat_version`, " +
			"`region_id`, `flag_date`, `reviewer_id`, `qc_comment`) " +
			"VALUES(?, ?, ?, ?, ?, ?, ?)";

	/**
	 * Assigns a cruise QC flag
	 * 
	 * @param qcFlag
	 * 		QC flag to assign
	 * @return
	 * 		true if successful
	 * @throws SQLException
	 * 		if accessing or updating the database throws one, or
	 * 		if the reviewer cannot be found in the reviewers table
	 */
	public boolean addQCFlag(SocatQCFlag qcFlag) throws SQLException {
		boolean wasSuccessful = false;
		Connection catConn = makeConnection(true);
		try {
			// Get the reviewer_id from the reviewer name
			String reviewer = qcFlag.getReviewer();
			PreparedStatement prepStmt = 
					catConn.prepareStatement(GET_REVIEWER_ID_SQL);
			prepStmt.setString(1, reviewer);
			ResultSet results = prepStmt.executeQuery();
			if ( ! results.first() )
				throw new SQLException(
						"Reviewer '" + reviewer + "' not found");
			int reviewerID = results.getInt(1);
			if ( reviewerID <= 0 )
				throw new SQLException(
						"ID for reviewer '" + reviewer + "' not found");
			results.close();

			// Assign the QC flag
			prepStmt = catConn.prepareStatement(SET_QC_FLAG_SQL);
			prepStmt.setString(1, qcFlag.getFlag().toString());
			prepStmt.setString(2, qcFlag.getExpocode());
			prepStmt.setDouble(3, qcFlag.getSocatVersion());
			prepStmt.setString(4, qcFlag.getRegionID().toString());
			Date flagDate = qcFlag.getFlagDate();
			if ( flagDate.equals(SocatMetadata.DATE_MISSING_VALUE) )
				prepStmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
			else
				prepStmt.setTimestamp(5, new Timestamp(flagDate.getTime()));
			prepStmt.setInt(6, reviewerID);
			prepStmt.setString(7, qcFlag.getComment());
			prepStmt.execute();
			if ( prepStmt.getUpdateCount() == 1 )
				wasSuccessful = true;
		} finally {
			catConn.close();
		}
		return wasSuccessful;
	}

}
