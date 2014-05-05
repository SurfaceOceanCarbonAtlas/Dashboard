/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DataColumnType;
import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;
import gov.noaa.pmel.socat.dashboard.shared.SocatQCFlag;
import gov.noaa.pmel.socat.dashboard.shared.SocatWoceFlag;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
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

	/**
	 * Get the ID for a reviewer from the reviewers database table.
	 * 
	 * @param catConn
	 * 		connection to the database
	 * @param reviewerName
	 * 		name of the reviewer
	 * @return
	 * 		ID of the reviewer
	 * @throws SQLException
	 * 		if accessing the database throws one, or
	 * 		if the reviewer name cannot be found
	 */
	private int getReviewerId(Connection catConn, String reviewerName) 
													throws SQLException {
		PreparedStatement prepStmt = catConn.prepareStatement(
				"SELECT `reviewer_id` FROM `reviewers` WHERE `username` = ?");
		prepStmt.setString(1, reviewerName);
		ResultSet results = prepStmt.executeQuery();
		if ( ! results.first() )
			throw new SQLException(
					"Reviewer '" + reviewerName + "' not found");
		int reviewerId = results.getInt(1);
		if ( reviewerId <= 0 )
			throw new SQLException(
					"ID for reviewer '" + reviewerName + "' not found");
		results.close();
		return reviewerId;
	}

	/**
	 * Adds a new QC flag for a dataset.
	 * 
	 * @param qcFlag
	 * 		QC flag to add
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
			int reviewerId = getReviewerId(catConn, qcFlag.getUsername());
			PreparedStatement prepStmt = catConn.prepareStatement(
					"INSERT INTO `qcflags` (`qc_flag`, `expocode`, " +
					"`socat_version`, `region_id`, " +
					"`flag_date`, `reviewer_id`, `qc_comment`) " +
					"VALUES(?, ?, ?, ?, ?, ?, ?)");
			prepStmt.setString(1, qcFlag.getFlag().toString());
			prepStmt.setString(2, qcFlag.getExpocode());
			prepStmt.setDouble(3, qcFlag.getSocatVersion());
			prepStmt.setString(4, qcFlag.getRegionID().toString());
			Date flagDate = qcFlag.getFlagDate();
			if ( flagDate.equals(SocatMetadata.DATE_MISSING_VALUE) )
				prepStmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
			else
				prepStmt.setTimestamp(5, new Timestamp(flagDate.getTime()));
			prepStmt.setInt(6, reviewerId);
			prepStmt.setString(7, qcFlag.getComment());
			prepStmt.execute();
			if ( prepStmt.getUpdateCount() == 1 )
				wasSuccessful = true;
		} finally {
			catConn.close();
		}
		return wasSuccessful;
	}

	/**
	 * Creates a SocatQCFlag from the values in the current row of a ResultSet.
	 * 
	 * @param results
	 * 		assign values from the current row of this ResultSet; must 
	 * 		include columns with names qc_flag, expocode, socat_version, 
	 * 		region_id, flag_date, username, realname, and qc_comment.
	 * @returns
	 * 		created QC flag
	 * @throws SQLException
	 * 		if getting values from the ResultSet throws one
	 */
	private SocatQCFlag createQCFlag(ResultSet results) throws SQLException {
		SocatQCFlag qcFlag = new SocatQCFlag();
		try {
			qcFlag.setFlag(results.getString("qc_flag").charAt(0));
		} catch (NullPointerException ex) {
			throw new SQLException("Unexpected NULL qc_flag");
		} catch (IndexOutOfBoundsException ex) {
			throw new SQLException("Unexpected empty qc_flag");
		}
		qcFlag.setExpocode(results.getString("expocode"));
		qcFlag.setSocatVersion(results.getDouble("socat_version"));
		try {
			qcFlag.setRegionID(results.getString("region_id").charAt(0));
		} catch (NullPointerException ex) {
			throw new SQLException("Unexpected NULL region_id");
		} catch (IndexOutOfBoundsException ex) {
			throw new SQLException("Unexpected empty region_id");
		}
		try {
			qcFlag.setFlagDate(
					new Date(results.getTimestamp("flag_date").getTime()));
		} catch (NullPointerException ex) {
			throw new SQLException("Unexpected NULL flag_date");
		}
		qcFlag.setUsername(results.getString("username"));
		qcFlag.setRealname(results.getString("realname"));
		qcFlag.setComment(results.getString("qc_comment"));
		return qcFlag;
	}

	/**
	 * Retrieves the current list of QC flags for a dataset.
	 * 
	 * @param expocode
	 * 		get the flags for the dataset with this expocode 
	 * @return
	 * 		list of QC flags for the dataset
	 * @throws SQLException
	 * 		if accessing the database or reading the results throws one
	 */
	public ArrayList<SocatQCFlag> getQCFlags(String expocode) throws SQLException {
		Connection catConn = makeConnection(false);
		PreparedStatement prepStmt = catConn.prepareStatement(
				"SELECT * FROM `qcflags` JOIN `reviewers` " +
				"ON qcflags.reviewer_id = reviewers.reviewer_id " +
				"WHERE qcflags.expocode = ? ORDER BY qcflags.qc_id;");
		prepStmt.setString(1, expocode);
		ResultSet results = prepStmt.executeQuery();
		ArrayList<SocatQCFlag> flagsList = new ArrayList<SocatQCFlag>();
		while ( results.next() ) {
			flagsList.add(createQCFlag(results));
		}
		return flagsList;
	}

	/**
	 * Adds a new WOCE flag for a datapoint.
	 * 
	 * @param woceFlag
	 * 		WOCE flag to add
	 * @return
	 * 		true if successful
	 * @throws SQLException
	 * 		if accessing or updating the database throws one, or
	 * 		if the reviewer cannot be found in the reviewers table
	 */
	public boolean addWoceFlag(SocatWoceFlag woceFlag) throws SQLException {
		boolean wasSuccessful = false;
		Connection catConn = makeConnection(true);
		try {
			int reviewerId = getReviewerId(catConn, woceFlag.getUsername());
			PreparedStatement prepStmt = catConn.prepareStatement(
					"INSERT INTO `woceflags` (`woce_flag`, `expocode`, " +
					"`socat_version`, `region_id`, " +
					"`data_row`, `data_longitude`, `data_latitude`, " +
					"`data_time`, `data_name`, `data_value`, " +
					"`flag_date`, `reviewer_id`, `qc_comment`) " +
					"VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			prepStmt.setString(1, woceFlag.getFlag().toString());
			prepStmt.setString(2, woceFlag.getExpocode());
			prepStmt.setDouble(3, woceFlag.getSocatVersion());
			prepStmt.setString(4, woceFlag.getRegionID().toString());
			prepStmt.setInt(5, woceFlag.getRowNumber());
			prepStmt.setDouble(6, woceFlag.getLongitude());
			prepStmt.setDouble(7, woceFlag.getLatitude());
			prepStmt.setLong(8, Math.round(woceFlag.getDataDate().getTime() / 1000.0));
			prepStmt.setString(9, woceFlag.getColumnName());
			prepStmt.setDouble(10, woceFlag.getDataValue());
			Date flagDate = woceFlag.getFlagDate();
			if ( flagDate.equals(SocatMetadata.DATE_MISSING_VALUE) )
				prepStmt.setTimestamp(11, new Timestamp(System.currentTimeMillis()));
			else
				prepStmt.setTimestamp(11, new Timestamp(flagDate.getTime()));
			prepStmt.setInt(12, reviewerId);
			prepStmt.setString(13, woceFlag.getComment());
			prepStmt.execute();
			if ( prepStmt.getUpdateCount() == 1 )
				wasSuccessful = true;
		} finally {
			catConn.close();
		}
		return wasSuccessful;
	}

	/**
	 * Creates a SocatQCFlag from the values in the current row of a ResultSet.
	 * 
	 * @param results
	 * 		assign values from the current row of this ResultSet; must 
	 * 		include columns with names woce_flag, expocode, socat_version, 
	 * 		region_id, data_row, data_longitude, data_latitude, data_time,
	 * 		data_type, data_name, data_value, flag_date, username, realname, 
	 * 		and woce_comment.
	 * @returns
	 * 		created QC flag
	 * @throws SQLException
	 * 		if getting values from the ResultSet throws one
	 */
	private SocatWoceFlag createWoceFlag(ResultSet results) throws SQLException {
		SocatWoceFlag woceFlag = new SocatWoceFlag();
		try {
			woceFlag.setFlag(results.getString("woce_flag").charAt(0));
		} catch (NullPointerException ex) {
			throw new SQLException("Unexpected NULL woce_flag");
		} catch (IndexOutOfBoundsException ex) {
			throw new SQLException("Unexpected empty woce_flag");
		}
		woceFlag.setExpocode(results.getString("expocode"));
		woceFlag.setSocatVersion(results.getDouble("socat_version"));
		try {
			woceFlag.setRegionID(results.getString("region_id").charAt(0));
		} catch (NullPointerException ex) {
			throw new SQLException("Unexpected NULL region_id");
		} catch (IndexOutOfBoundsException ex) {
			throw new SQLException("Unexpected empty region_id");
		}
		woceFlag.setRowNumber(results.getInt("data_row"));
		if ( results.wasNull() )
			woceFlag.setRowNumber(null);
		woceFlag.setLongitude(results.getDouble("data_longitude"));
		if ( results.wasNull() )
			woceFlag.setLongitude(null);
		woceFlag.setLatitude(results.getDouble("data_latitude"));
		if ( results.wasNull() )
			woceFlag.setLatitude(null);
		woceFlag.setDataDate(new Date(results.getLong("data_time") * 1000));
		if ( results.wasNull() )
			woceFlag.setDataDate(null);
		try {
			woceFlag.setDataType(
					DataColumnType.valueOf(results.getString("data_type")));
		} catch (NullPointerException ex) {
			woceFlag.setDataType(null);
		} catch (IllegalArgumentException ex) {
			throw new SQLException("Unexpected unknown data type '" + 
					results.getString("data_type") + "'");
		}
		woceFlag.setColumnName(results.getString("data_name"));
		woceFlag.setDataValue(results.getDouble("data_value"));
		if ( results.wasNull() )
			woceFlag.setDataValue(null);
		try {
			woceFlag.setFlagDate(
					new Date(results.getTimestamp("flag_date").getTime()));
		} catch (NullPointerException ex) {
			throw new SQLException("Unexpected NULL flag_date");
		}
		woceFlag.setUsername(results.getString("username"));
		woceFlag.setRealname(results.getString("realname"));
		woceFlag.setComment(results.getString("woce_comment"));
		return woceFlag;
	}

	/**
	 * Retrieves the current list of WOCE flags for a dataset.
	 * 
	 * @param expocode
	 * 		get the flags for the dataset with this expocode 
	 * @return
	 * 		list of WOCE flags for the dataset
	 * @throws SQLException
	 * 		if accessing the database or reading the results throws one
	 */
	public ArrayList<SocatWoceFlag> getWoceFlags(String expocode) throws SQLException {
		Connection catConn = makeConnection(false);
		PreparedStatement prepStmt = catConn.prepareStatement(
				"SELECT * FROM `woceflags` JOIN `reviewers` " +
				"ON woceflags.reviewer_id = reviewers.reviewer_id " +
				"WHERE woceflags.expocode = ? ORDER BY woceflags.woce_id;");
		prepStmt.setString(1, expocode);
		ResultSet results = prepStmt.executeQuery();
		ArrayList<SocatWoceFlag> flagsList = new ArrayList<SocatWoceFlag>();
		while ( results.next() ) {
			flagsList.add(createWoceFlag(results));
		}
		return flagsList;
	}

}
