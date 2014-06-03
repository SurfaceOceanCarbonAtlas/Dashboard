/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DataColumnType;
import gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData;
import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;
import gov.noaa.pmel.socat.dashboard.shared.SocatQCEvent;
import gov.noaa.pmel.socat.dashboard.shared.DataLocation;
import gov.noaa.pmel.socat.dashboard.shared.SocatWoceEvent;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

/**
 * @author Karl Smith
 */
public class DatabaseRequestHandler {

	private static final String SQL_DRIVER_CLASS_TAG = "sqldriverclass";
	private static final String DATABASE_URL_TAG = "databaseurl";
	private static final String CATALOG_NAME_TAG = "catalogname";
	private static final String SELECT_USER_TAG = "selectuser";
	private static final String SELECT_PASS_TAG = "selectpass";
	private static final String UPDATE_USER_TAG = "updateuser";
	private static final String UPDATE_PASS_TAG = "updatepass";

	String databaseUrl;
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
		String driverClassName = configProps.getProperty(SQL_DRIVER_CLASS_TAG);
		if ( driverClassName == null )
			driverClassName = "com.mysql.jdbc.Driver";
		databaseUrl = configProps.getProperty(DATABASE_URL_TAG);
		if ( databaseUrl == null )
			databaseUrl = "jdbc:mysql://localhost:3306/";
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

		// Register the SQL driver - no harm if already registered
		try {
			Class.forName(driverClassName).newInstance();
		} catch (Exception ex) {
			throw new SQLException("Unable to register the SQL driver " + 
					driverClassName + "\n" + ex.getMessage());
		}

		// Verify the values by making the database connections
		boolean canSelect = false;
		Connection selectConn = makeConnection(false);
		try {
			ResultSet result = selectConn.createStatement().executeQuery("SHOW GRANTS;");
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
			ResultSet result = selectConn.createStatement().executeQuery("SHOW GRANTS;");
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
		Connection catConn;
		if ( canUpdate ) {
			catConn = DriverManager.getConnection(
					databaseUrl + catalogName, updateUser, updatePass);
		}
		else { 	
			catConn = DriverManager.getConnection(
					databaseUrl + catalogName, selectUser, selectPass);
		}
		if ( catConn == null )
			throw new SQLException("null SQL connection returned");
		return catConn;
	}

	/**
	 * Get the ID for a reviewer from the reviewers database table.
	 * Users the username for the reviewer unless it is empty, in 
	 * which case it uses the realname of the reviewer.
	 * 
	 * @param catConn
	 * 		connection to the database
	 * @param username
	 * 		username of the reviewer
	 * @param realname
	 * 		realname of the reviewer
	 * @return
	 * 		ID of the reviewer
	 * @throws SQLException
	 * 		if accessing the database throws one, or
	 * 		if the reviewer name cannot be found
	 */
	private int getReviewerId(Connection catConn, 
						String username, String realname) throws SQLException {
		int reviewerId;
		PreparedStatement prepStmt;
		if ( ! username.isEmpty() ) {
			prepStmt = catConn.prepareStatement(
				"SELECT `reviewer_id` FROM `Reviewers` WHERE `username` = ?");
			prepStmt.setString(1, username);
		}
		else {
			prepStmt = catConn.prepareStatement(
					"SELECT `reviewer_id` FROM `Reviewers` WHERE `realname` = ?");
			prepStmt.setString(1, realname);
		}
		ResultSet results = prepStmt.executeQuery();
		try {
			if ( ! results.first() ) {
				if ( ! username.isEmpty() )
					throw new SQLException(
							"Reviewer username '" + username + "' not found");
				else 
					throw new SQLException(
							"Reviewer realname '" + realname + "' not found");
			}
			reviewerId = results.getInt(1);
			if ( reviewerId <= 0 ) {
				if ( ! username.isEmpty() )
					throw new SQLException(
							"ID for reviewer username '" + username + "' not found");
				else
					throw new SQLException(
							"ID for reviewer realname '" + realname + "' not found");
			}
		} finally {
			results.close();
		}
		return reviewerId;
	}

	/**
	 * Retrieves the actual name for a reviewer from the Reviewers table
	 * using the reviewer's username.
	 * 
	 * @param username
	 * 		username for a reviewer
	 * @return
	 * 		actual name of the reviewer
	 * @throws SQLException
	 * 		if accessing the database throws one 
	 */
	public String getReviewerRealname(String username) throws SQLException {
		String realname = null;
		Connection catConn = makeConnection(false);
		try {
			PreparedStatement prepStmt = catConn.prepareStatement(
					"SELECT `realname` FROM `Reviewers` WHERE `username` = ?");
			prepStmt.setString(1, username);
			ResultSet results = prepStmt.executeQuery();
			try {
				while ( results.next() ) {
					if ( realname != null ) 
						throw new SQLException(
								"More than one realname for " + username);
					realname = results.getString(1);
				}
			} finally {
				results.close();
			}
		} finally {
			catConn.close();
		}
		return realname;
	}

	/**
	 * Retrieves the username for a reviewer from the Reviewers table
	 * using the reviewer's actual name.
	 * 
	 * @param realname
	 * 		actual name of the reviewer
	 * @return
	 * 		username for a reviewer
	 * @throws SQLException
	 * 		if accessing the database throws one 
	 */
	public String getReviewerUsername(String realname) throws SQLException {
		String username = null;
		Connection catConn = makeConnection(false);
		try {
			PreparedStatement prepStmt = catConn.prepareStatement(
					"SELECT `username` FROM `Reviewers` WHERE `realname` = ?");
			prepStmt.setString(1, realname);
			ResultSet results = prepStmt.executeQuery();
			try {
				while ( results.next() ) {
					if ( username != null ) 
						throw new SQLException(
								"More than one username for " + realname);
					username = results.getString(1);
				}
			} finally {
				results.close();
			}
		} finally {
			catConn.close();
		}
		return username;
	}

	/**
	 * Adds a new QC event for a dataset.
	 * 
	 * @param qcEvent
	 * 		the QC event to add
	 * @throws SQLException
	 * 		if accessing or updating the database throws one, or
	 * 		if the reviewer cannot be found in the reviewers table, or
	 * 		if a problem occurs with adding the QC event.
	 */
	public void addQCEvent(SocatQCEvent qcEvent) throws SQLException {
		Connection catConn = makeConnection(true);
		try {
			int reviewerId = getReviewerId(catConn, 
					qcEvent.getUsername(), qcEvent.getRealname());
			// Add the QC event
			PreparedStatement prepStmt = catConn.prepareStatement(
					"INSERT INTO `QCEvents` (`qc_flag`, `qc_time`, `expocode`, " +
					"`socat_version`, `region_id`, `reviewer_id`, `qc_comment`) " +
					"VALUES(?, ?, ?, ?, ?, ?, ?)");
			prepStmt.setString(1, qcEvent.getFlag().toString());
			Date flagDate = qcEvent.getFlagDate();
			if ( flagDate.equals(SocatMetadata.DATE_MISSING_VALUE) )
				prepStmt.setLong(2, Math.round(System.currentTimeMillis() / 1000.0));
			else
				prepStmt.setLong(2, Math.round(flagDate.getTime() / 1000.0));
			prepStmt.setString(3, qcEvent.getExpocode());
			prepStmt.setDouble(4, qcEvent.getSocatVersion());
			prepStmt.setString(5, qcEvent.getRegionID().toString());
			prepStmt.setInt(6, reviewerId);
			prepStmt.setString(7, qcEvent.getComment());
			prepStmt.execute();
			if ( prepStmt.getUpdateCount() != 1 )
				throw new SQLException("Adding the QC event was unsuccessful");
		} finally {
			catConn.close();
		}
	}

	/**
	 * Creates a SocatQCEvent object from the values in the current row 
	 * of a ResultSet.
	 * 
	 * @param results
	 * 		assign values from the current row of this ResultSet; must 
	 * 		include columns with names qc_flag, qc_time, expocode, 
	 * 		socat_version, region_id, username, realname, and qc_comment.
	 * @returns
	 * 		the created QC flag
	 * @throws SQLException
	 * 		if getting values from the ResultSet throws one
	 */
	private SocatQCEvent createQCEvent(ResultSet results) throws SQLException {
		SocatQCEvent qcEvent = new SocatQCEvent();
		try {
			qcEvent.setFlag(results.getString("qc_flag").charAt(0));
		} catch (NullPointerException ex) {
			throw new SQLException("Unexpected NULL qc_flag");
		} catch (IndexOutOfBoundsException ex) {
			throw new SQLException("Unexpected empty qc_flag");
		}
		qcEvent.setFlagDate(new Date(results.getLong("qc_time") * 1000L));
		if ( results.wasNull() )
			qcEvent.setFlagDate(null);
		qcEvent.setExpocode(results.getString("expocode"));
		qcEvent.setSocatVersion(results.getDouble("socat_version"));
		if ( results.wasNull() )
			qcEvent.setSocatVersion(null);
		try {
			qcEvent.setRegionID(results.getString("region_id").charAt(0));
		} catch (NullPointerException ex) {
			throw new SQLException("Unexpected NULL region_id");
		} catch (IndexOutOfBoundsException ex) {
			throw new SQLException("Unexpected empty region_id");
		}
		qcEvent.setUsername(results.getString("username"));
		qcEvent.setRealname(results.getString("realname"));
		qcEvent.setComment(results.getString("qc_comment"));
		return qcEvent;
	}

	/**
	 * Retrieves the current list of QC events for a dataset.
	 * 
	 * @param expocode
	 * 		get the QC events for the dataset with this expocode 
	 * @return
	 * 		the list of QC events for the dataset, ordered by the date
	 * 		of the event (latest event first)
	 * @throws SQLException
	 * 		if accessing the database or reading the results throws one
	 */
	public ArrayList<SocatQCEvent> getQCEvents(String expocode) throws SQLException {
		ArrayList<SocatQCEvent> eventsList = new ArrayList<SocatQCEvent>();
		Connection catConn = makeConnection(false);
		try {
			PreparedStatement prepStmt = catConn.prepareStatement(
					"SELECT * FROM `QCEvents` JOIN `Reviewers` " +
							"ON QCEvents.reviewer_id = Reviewers.reviewer_id " +
					"WHERE QCEvents.expocode = ? ORDER BY QCEvents.qc_time DESC;");
			prepStmt.setString(1, expocode);
			ResultSet results = prepStmt.executeQuery();
			try {
				while ( results.next() ) {
					eventsList.add(createQCEvent(results));
				}
			} finally {
				results.close();
			}
		} finally {
			catConn.close();
		}
		return eventsList;
	}

	/**
	 * Adds a new WOCE event for a dataset.  This includes assigning
	 * the DatumLocations to the WOCELocations table.
	 * 
	 * @param woceEvent
	 * 		the WOCE event to add
	 * @throws SQLException
	 * 		if accessing or updating the database throws one, or
	 * 		if the reviewer cannot be found in the reviewers table, or
	 * 		if a problem occurs adding the WOCE event
	 */
	public void addWoceEvent(SocatWoceEvent woceEvent) throws SQLException {
		Connection catConn = makeConnection(true);
		try {
			int reviewerId = getReviewerId(catConn, 
					woceEvent.getUsername(), woceEvent.getRealname());
			// Add the WOCE event
			PreparedStatement prepStmt = catConn.prepareStatement(
					"INSERT INTO `WOCEEvents` (`woce_flag`, `woce_time`, " +
					"`expocode`, `socat_version`, `data_type`, " +
					"`data_name`, `reviewer_id`, `woce_comment`) " +
					"VALUES(?, ?, ?, ?, ?, ?, ?, ?);");
			prepStmt.setString(1, woceEvent.getFlag().toString());
			Date flagDate = woceEvent.getFlagDate();
			if ( flagDate.equals(SocatMetadata.DATE_MISSING_VALUE) )
				prepStmt.setLong(2, Math.round(System.currentTimeMillis() / 1000.0));
			else
				prepStmt.setLong(2, Math.round(flagDate.getTime() / 1000.0));
			prepStmt.setString(3, woceEvent.getExpocode());
			prepStmt.setDouble(4, woceEvent.getSocatVersion());
			prepStmt.setString(5, woceEvent.getDataType().toString());
			prepStmt.setString(6, woceEvent.getColumnName());
			prepStmt.setInt(7, reviewerId);
			prepStmt.setString(8, woceEvent.getComment());
			prepStmt.execute();
			if ( prepStmt.getUpdateCount() != 1 )
				throw new SQLException("Adding the WOCE event was unsuccessful");
			// Get the woce_id for the added WOCE event
			long woceId;
			ResultSet results = catConn.createStatement().executeQuery("SELECT LAST_INSERT_ID();");
			try {
				if ( ! results.first() )
					throw new SQLException("Unexpected failure to get the woce_id for an added WOCE event");
				woceId = results.getLong(1);
				if ( woceId <= 0 )
					throw new SQLException("Unexpected invalid woce_id for an added WOCE event");
			} finally {
				results.close();
			}
			// Add the DatumLocations to the WOCELocations table
			prepStmt = catConn.prepareStatement("INSERT INTO `WOCELocations` " +
					"(`woce_id`, `region_id`, `row_num`, `longitude`, " +
					"`latitude`, `data_time`, `data_value`) " +
					"VALUES (?, ?, ?, ?, ?, ?, ?);");
			for (DataLocation location : woceEvent.getLocations() ) {
				prepStmt.setLong(1, woceId);
				prepStmt.setString(2, location.getRegionID().toString());
				Integer intVal = location.getRowNumber();
				if ( intVal.equals(SocatCruiseData.INT_MISSING_VALUE) )
					prepStmt.setNull(3, java.sql.Types.INTEGER);
				else
					prepStmt.setInt(3, intVal);
				Double dblVal = location.getLongitude();
				if ( dblVal.equals(SocatCruiseData.FP_MISSING_VALUE) )
					prepStmt.setNull(4, java.sql.Types.DOUBLE);
				else
					prepStmt.setDouble(4, dblVal);
				dblVal = location.getLatitude();
				if ( dblVal.equals(SocatCruiseData.FP_MISSING_VALUE) )
					prepStmt.setNull(5, java.sql.Types.DOUBLE);
				else
					prepStmt.setDouble(5, dblVal);
				Date dateVal = location.getDataDate();
				if ( dateVal.equals(SocatMetadata.DATE_MISSING_VALUE) )
					prepStmt.setNull(6, java.sql.Types.BIGINT);
				else
					prepStmt.setLong(6, Math.round(dateVal.getTime() / 1000.0));
				dblVal = location.getDataValue();
				if ( dblVal.equals(SocatCruiseData.FP_MISSING_VALUE) )
					prepStmt.setNull(7, java.sql.Types.DOUBLE);
				else
					prepStmt.setDouble(7, dblVal);
				prepStmt.execute();
				if ( prepStmt.getUpdateCount() != 1 ) 
					throw new SQLException("Adding the WOCE location was unsuccessful");
			}
		} finally {
			catConn.close();
		}
	}

	/**
	 * Creates a SocatWoceEvent without any locations 
	 * from the values in the current row of a ResultSet.
	 * 
	 * @param results
	 * 		assign values from the current row of this ResultSet; must 
	 * 		include columns with names woce_flag, woce_time, expocode, 
	 * 		socat_version, data_type, data_name, username, realname, 
	 * 		and woce_comment.
	 * @returns
	 * 		the created WOCE event
	 * @throws SQLException
	 * 		if getting values from the ResultSet throws one
	 */
	private SocatWoceEvent createWoceEvent(ResultSet results) throws SQLException {
		SocatWoceEvent woceEvent = new SocatWoceEvent();
		try {
			woceEvent.setFlag(results.getString("woce_flag").charAt(0));
		} catch (NullPointerException ex) {
			throw new SQLException("Unexpected NULL woce_flag");
		} catch (IndexOutOfBoundsException ex) {
			throw new SQLException("Unexpected empty woce_flag");
		}
		woceEvent.setFlagDate(new Date(results.getLong("woce_time") * 1000L));
		if ( results.wasNull() )
			woceEvent.setFlagDate(null);
		woceEvent.setExpocode(results.getString("expocode"));
		woceEvent.setSocatVersion(results.getDouble("socat_version"));
		if ( results.wasNull() )
			woceEvent.setSocatVersion(null);
		try {
			woceEvent.setDataType(DataColumnType.valueOf(results.getString("data_type")));
		} catch (NullPointerException ex) {
			woceEvent.setDataType(null);
		} catch (IllegalArgumentException ex) {
			throw new SQLException("Unexpected unknown data type '" + 
					results.getString("data_type") + "'");
		}
		woceEvent.setColumnName(results.getString("data_name"));
		woceEvent.setUsername(results.getString("username"));
		woceEvent.setRealname(results.getString("realname"));
		woceEvent.setComment(results.getString("woce_comment"));
		return woceEvent;
	}

	/**
	 * Creates a DataLocation from the values in the current row of a ResultSet.
	 * 
	 * @param results
	 * 		assign values from the current row of this ResultSet; must include 
	 * 		columns with names region_id, row_num, longitude, latitude, data_time, 
	 * 		and data_value.
	 * @return
	 * 		the created DataLocation 
	 * @throws SQLException
	 */
	private DataLocation createWoceLocation(ResultSet results) throws SQLException {
		DataLocation location = new DataLocation();
		try {
			location.setRegionID(results.getString("region_id").charAt(0));
		} catch (NullPointerException ex) {
			throw new SQLException("Unexpected NULL region_id");
		} catch (IndexOutOfBoundsException ex) {
			throw new SQLException("Unexpected empty region_id");
		}
		location.setRowNumber(results.getInt("row_num"));
		if ( results.wasNull() )
			location.setRowNumber(null);
		location.setLongitude(results.getDouble("longitude"));
		if ( results.wasNull() )
			location.setLongitude(null);
		location.setLatitude(results.getDouble("latitude"));
		if ( results.wasNull() ) 
			location.setLatitude(null);
		location.setDataDate(new Date(results.getLong("data_time") * 1000L));
		if ( results.wasNull() )
			location.setDataDate(null);
		location.setDataValue(results.getDouble("data_value"));
		if ( results.wasNull() ) 
			location.setDataValue(null);
		return location;
	}

	/**
	 * Retrieves the current list of WOCE events for a dataset.
	 * 
	 * @param expocode
	 * 		get the WOCE events for the dataset with this expocode 
	 * @return
	 * 		list of WOCE events for the dataset, ordered by the date
	 * 		of the event (latest event first)
	 * @throws SQLException
	 * 		if accessing the database or reading the results throws one
	 */
	public ArrayList<SocatWoceEvent> getWoceFlags(String expocode) throws SQLException {
		ArrayList<SocatWoceEvent> eventsList = new ArrayList<SocatWoceEvent>();
		Connection catConn = makeConnection(false);
		try {
			PreparedStatement prepStmt = catConn.prepareStatement(
					"SELECT * FROM `WOCEEvents` JOIN `Reviewers` " +
					"ON WOCEEvents.reviewer_id = Reviewers.reviewer_id " +
					"WHERE WOCEEvents.expocode = ? ORDER BY WOCEEvents.woce_time DESC;");
			prepStmt.setString(1, expocode);
			ArrayList<Long> woceIds = new ArrayList<Long>();
			ResultSet results = prepStmt.executeQuery();
			try {
				while ( results.next() ) {
					eventsList.add(createWoceEvent(results));
					woceIds.add(results.getLong("woce_id"));
				}
			} finally {
				results.close();
			}
			prepStmt = catConn.prepareStatement("SELECT * FROM `WOCELocations` " +
					"WHERE `woce_id` = ? ORDER BY `row_num`;");
			for (int k = 0; k < woceIds.size(); k++) {
				// Directly modify the list of locations in the WOCE event
				ArrayList<DataLocation> locations = eventsList.get(k).getLocations();
				prepStmt.setLong(1, woceIds.get(k));
				results = prepStmt.executeQuery();
				try {
					while ( results.next() ) {
						locations.add(createWoceLocation(results));
					}
				} finally {
					results.close();
				}
			}
		} finally {
			catConn.close();
		}
		return eventsList;
	}

}
