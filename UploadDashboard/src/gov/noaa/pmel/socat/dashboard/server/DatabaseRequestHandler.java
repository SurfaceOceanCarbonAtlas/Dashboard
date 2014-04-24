/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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

	private static final String GET_REVIEWER_ID_SQL = 
			"SELECT `reviewer_id` FROM `reviewers` where `username` = ?;";
	private static final String SET_QC_FLAG_SQL = 
			"INSERT INTO `qcflags` (`qc_flag`, `expocode`, `socat_version`, " +
			"`region_id`, `flag_date`, `reviewer_id`, `qc_comment`) " +
			"VALUES(?, ?, ?, ?, ?, ?, ?)";

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
		String grants;
		Connection selectConn = makeConnection(false);
		try {
			ResultSet result = selectConn.prepareStatement("SHOW GRANTS;").executeQuery();
			try {
				if ( ! result.first() )
					throw new SQLException("No grants results");
				grants = result.getString(1);
				if ( grants == null )
					throw new SQLException("No grants results");
			} finally {
				result.close();
			}
		} finally {
			selectConn.close();
		}
		if ( ! grants.contains("SELECT") )
			throw new IllegalArgumentException(
					"The select-only user does not have SELECT privileges");

		selectConn = makeConnection(true);
		try {
			ResultSet result = selectConn.prepareStatement("SHOW GRANTS;").executeQuery();
			try {
				if ( ! result.first() )
					throw new SQLException("No grants results");
				grants = result.getString(1);
				if ( grants == null )
					throw new SQLException("No grants results");
			} finally {
				result.close();
			}
		} finally {
			selectConn.close();
		}
		if ( ! grants.contains("SELECT") )
			throw new IllegalArgumentException(
					"The update user does not have SELECT privileges");
		if ( ! grants.contains("UPDATE") )
			throw new IllegalArgumentException(
					"The update user does not have UPDATE privileges");
		if ( ! grants.contains("INSERT") )
			throw new IllegalArgumentException(
					"The update user does not have INSERT privileges");
		if ( ! grants.contains("DELETE") )
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

	/**
	 * Assigns a QC flag along with a comment for a cruise region
	 * 
	 * @param qcFlag
	 * 		QC flag to assign
	 * @param expocode
	 * 		expocode of the cruise
	 * @param socatVersion
	 * 		SOCAT version of the cruise
	 * @param regionID
	 * 		region ID for this QC flag
	 * @param flagDate
	 * 		date/time of this QC flag assignment;
	 * 		if null, the current time is used
	 * @param username
	 * 		username of the reviewer assigning this QC flag
	 * @param comment
	 * 		comment to go with this QC flag
	 * @return
	 * 		true if successful
	 * @throws SQLException
	 * 		if accessing or updating the database throws one, or
	 * 		if the reviewer username cannot be found in the reviewers table
	 */
	public boolean addQCFlag(String qcFlag, String expocode, String socatVersion,
			String regionID, Timestamp flagDate, String username, String comment) 
															throws SQLException {
		boolean wasSuccessful = false;
		Connection catConn = makeConnection(true);
		try {
			// Get the reviewer_id from the username
			PreparedStatement prepStmt = 
					catConn.prepareStatement(GET_REVIEWER_ID_SQL);
			prepStmt.setString(1, username);
			ResultSet results = prepStmt.executeQuery();
			if ( ! results.first() )
				throw new SQLException(
						"Reviewer username '" + username + "' not found");
			int reviewerID = results.getInt(1);
			if ( reviewerID <= 0 )
				throw new SQLException("Reviewer ID for username '" + 
						username + "' not found");
			results.close();
			// Assign the QC flag
			prepStmt = catConn.prepareStatement(SET_QC_FLAG_SQL);
			prepStmt.setString(1, qcFlag);
			prepStmt.setString(2, expocode);
			prepStmt.setString(3, socatVersion);
			prepStmt.setString(4, regionID);
			if ( flagDate != null )
				prepStmt.setTimestamp(5, flagDate);
			else
				prepStmt.setTimestamp(5, 
						new Timestamp(System.currentTimeMillis()));
			prepStmt.setInt(6, reviewerID);
			prepStmt.setString(7, comment);
			prepStmt.execute();
			if ( prepStmt.getUpdateCount() == 1 )
				wasSuccessful = true;
		} finally {
			catConn.close();
		}
		return wasSuccessful;
	}

}
