/**
 * 
 */
package gov.noaa.pmel.dashboard.handlers;

import gov.noaa.pmel.dashboard.server.SocatTypes;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataLocation;
import gov.noaa.pmel.dashboard.shared.QCEvent;
import gov.noaa.pmel.dashboard.shared.WoceEvent;

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
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * Handles database requests for dealing with SOCAT flag events
 * 
 * @author Karl Smith
 */
public class DatabaseRequestHandler {

	private static final String REVIEWERS_TABLE_NAME = "Reviewers";
	private static final String QCEVENTS_TABLE_NAME = "QCEvents";
	private static final String WOCEEVENTS_TABLE_NAME = "WOCEEvents";
	private static final String WOCELOCATIONS_TABLE_NAME = "WOCELocations";

	private static final String SQL_DRIVER_TAG = "sqldriver";
	private static final String DATABASE_URL_TAG = "databaseurl";
	private static final String SELECT_USER_TAG = "selectuser";
	private static final String SELECT_PASS_TAG = "selectpass";
	private static final String UPDATE_USER_TAG = "updateuser";
	private static final String UPDATE_PASS_TAG = "updatepass";

	String databaseUrl;
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
	 * 		if there are problems connecting to or executing a query on the database
	 */
	public DatabaseRequestHandler(String configFilename) throws FileNotFoundException, 
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
		String sqlDriverName = configProps.getProperty(SQL_DRIVER_TAG);
		if ( (sqlDriverName == null) || sqlDriverName.trim().isEmpty() )
			throw new IllegalArgumentException("Value for " + SQL_DRIVER_TAG + 
					", such as com.mysql.jdbc.Driver, must be given in " + configFilename);
		sqlDriverName = sqlDriverName.trim();
		databaseUrl = configProps.getProperty(DATABASE_URL_TAG);
		if ( (databaseUrl == null) || databaseUrl.trim().isEmpty() )
			throw new IllegalArgumentException("Value for " + DATABASE_URL_TAG + 
					", such as jdbc:mysql://localhost:3306/SOCATFlags, must be given in " + configFilename);
		databaseUrl = databaseUrl.trim();
		selectUser = configProps.getProperty(SELECT_USER_TAG);
		if ( (selectUser == null) || selectUser.trim().isEmpty() )
			throw new IllegalArgumentException("Value for " + SELECT_USER_TAG + 
					" must be given in " + configFilename);
		selectUser = selectUser.trim();
		selectPass = configProps.getProperty(SELECT_PASS_TAG);
		if ( (selectPass == null) || selectPass.trim().isEmpty() )
			throw new IllegalArgumentException("Value for " + SELECT_PASS_TAG + 
					" must be given in " + configFilename);
		selectPass = selectPass.trim();
		updateUser = configProps.getProperty(UPDATE_USER_TAG);
		if ( (updateUser == null) || updateUser.trim().isEmpty() )
			throw new IllegalArgumentException("Value for " + UPDATE_USER_TAG + 
					" must be given in " + configFilename);
		updateUser = updateUser.trim();
		updatePass = configProps.getProperty(UPDATE_PASS_TAG);
		if ( (updatePass == null) || updatePass.trim().isEmpty() )
			throw new IllegalArgumentException("Value for " + UPDATE_PASS_TAG + 
					" must be given in " + configFilename);
		updatePass = updatePass.trim();
		testConnections(sqlDriverName);
	}

	/**
	 * Create using the given parameters
	 * 
	 * @param sqlDriverName
	 * 		SQL driver class name, such as "com.mysql.jdbc.Driver"
	 * @param databaseUrl
	 * 		database URL, such as "jdbc:mysql://localhost:3306/SOCATFlags"
	 * @param selectUser
	 * 		name of database user with SELECT privileges (read-only user)
	 * @param selectPass
	 * 		password of database user with SELECT privileges (read-only user)
	 * @param updateUser
	 * 		name of database user with SELECT, UPDATE, INSERT, DELETE privileges (read-write user)
	 * @param updatePass
	 * 		password of database user with SELECT, UPDATE, INSERT, DELETE privileges (read-write user)
	 * @throws IllegalArgumentException 
	 * 		if a given value is invalid (null or empty)
	 * @throws SQLException 
	 * 		if there are problems connecting to or executing a query on the database
	 */
	public DatabaseRequestHandler(String sqlDriverName, String databaseUrl, 
			String selectUser, String selectPass, String updateUser, String updatePass) 
					throws IllegalArgumentException, SQLException {
		if ( (sqlDriverName == null) || sqlDriverName.trim().isEmpty() )
			throw new IllegalArgumentException("an SQL driver class name, " +
					"such as com.mysql.jdbc.Driver, must be given");
		if ( (databaseUrl == null) || databaseUrl.isEmpty() )
			throw new IllegalArgumentException("an SQL database URL, " +
					"such as jdbc:mysql://localhost:3306/SOCATFlags, must be given");
		this.databaseUrl = databaseUrl.trim();
		if ( (selectUser == null) || selectUser.trim().isEmpty() )
			throw new IllegalArgumentException("username for select user must be given");
		this.selectUser = selectUser.trim();
		if ( (selectPass == null) || selectPass.trim().isEmpty() )
			throw new IllegalArgumentException("password for select user must be given");
		this.selectPass = selectPass.trim();
		if ( (updateUser == null) || updateUser.trim().isEmpty() )
			throw new IllegalArgumentException("username for update user must be given");
		this.updateUser = updateUser.trim();
		if ( (updatePass == null) || updatePass.trim().isEmpty() )
			throw new IllegalArgumentException("password for update user must be given");
		this.updatePass = updatePass.trim();
		testConnections(sqlDriverName.trim());
	}

	/**
	 * Validates the parameters in this handler.
	 * 
	 * @param sqlDriverName
	 * 		SQL driver class name, such as "com.mysql.jdbc.Driver"
	 * @throws IllegalArgumentException
	 * 		if either database user does not have adequate privileges
	 * @throws SQLException
	 * 		if registering the SQL driver fails, or 
	 * 		if connecting to the database throws one
	 */
	private void testConnections(String sqlDriverName) throws SQLException {
		// Register the SQL driver - no harm if already registered
		try {
			Class.forName(sqlDriverName).newInstance();
		} catch (Exception ex) {
			throw new SQLException("Unable to register the SQL driver " + 
					sqlDriverName + "\n" + ex.getMessage());
		}

		// Verify the values by making the database connections
		Connection catConn = makeConnection(false);
		catConn.close();
		catConn = makeConnection(true);
		catConn.close();
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
			catConn = DriverManager.getConnection(databaseUrl, updateUser, updatePass);
		}
		else { 	
			catConn = DriverManager.getConnection(databaseUrl, selectUser, selectPass);
		}
		if ( catConn == null )
			throw new SQLException("null SQL connection returned");
		return catConn;
	}

	/**
	 * Get the ID for a reviewer from the reviewers database table.
	 * Uses the username for the reviewer unless it is empty, in 
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
			prepStmt = catConn.prepareStatement("SELECT `reviewer_id` FROM `" + 
					REVIEWERS_TABLE_NAME + "` WHERE `username` = ?;");
			prepStmt.setString(1, username);
		}
		else {
			prepStmt = catConn.prepareStatement("SELECT `reviewer_id` FROM `" + 
					REVIEWERS_TABLE_NAME + "` WHERE `realname` = ?;");
			prepStmt.setString(1, realname);
		}
		ResultSet results = prepStmt.executeQuery();
		try {
			if ( ! results.first() ) {
				if ( ! username.isEmpty() )
					throw new SQLException("Reviewer username '" + username + "' not found");
				else 
					throw new SQLException("Reviewer realname '" + realname + "' not found");
			}
			reviewerId = results.getInt(1);
			if ( reviewerId <= 0 ) {
				if ( ! username.isEmpty() )
					throw new SQLException("ID for reviewer username '" + username + "' not found");
				else
					throw new SQLException("ID for reviewer realname '" + realname + "' not found");
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
	 * 		actual name of the reviewer; may be null if not found
	 * @throws SQLException
	 * 		if accessing the database throws one 
	 */
	public String getReviewerRealname(String username) throws SQLException {
		String realname = null;
		Connection catConn = makeConnection(false);
		try {
			PreparedStatement prepStmt = catConn.prepareStatement("SELECT `realname` FROM `" + 
					REVIEWERS_TABLE_NAME + "` WHERE `username` = ?;");
			prepStmt.setString(1, username);
			ResultSet results = prepStmt.executeQuery();
			try {
				while ( results.next() ) {
					if ( realname != null ) 
						throw new SQLException("More than one realname for " + username);
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
	 * 		username for a reviewer; may be null if not found
	 * @throws SQLException
	 * 		if accessing the database throws one 
	 */
	public String getReviewerUsername(String realname) throws SQLException {
		String username = null;
		Connection catConn = makeConnection(false);
		try {
			PreparedStatement prepStmt = catConn.prepareStatement("SELECT `username` FROM `" + 
					REVIEWERS_TABLE_NAME + "` WHERE `realname` = ?;");
			prepStmt.setString(1, realname);
			ResultSet results = prepStmt.executeQuery();
			try {
				while ( results.next() ) {
					if ( username != null ) 
						throw new SQLException("More than one username for " + realname);
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
	 * Retrieves the e-mail address for a reviewer from the Reviewers table
	 * using the reviewer's username.
	 * 
	 * @param username
	 * 		username for a reviewer
	 * @return
	 * 		e-mail address of the reviewer; may be null if not found
	 * @throws SQLException
	 * 		if accessing the database throws one 
	 */
	public String getReviewerEmail(String username) throws SQLException {
		String userEmail = null;
		Connection catConn = makeConnection(false);
		try {
			PreparedStatement prepStmt = catConn.prepareStatement("SELECT `email` FROM `" + 
					REVIEWERS_TABLE_NAME + "` WHERE `username` = ?;");
			prepStmt.setString(1, username);
			ResultSet results = prepStmt.executeQuery();
			try {
				while ( results.next() ) {
					if ( userEmail != null ) 
						throw new SQLException("More than one e-mail address for " + username);
					userEmail = results.getString(1);
				}
			} finally {
				results.close();
			}
		} finally {
			catConn.close();
		}
		return userEmail;
	}

	/**
	 * Adds a new QC event for a data set.
	 * 
	 * @param qcEvent
	 * 		the QC event to add
	 * @throws SQLException
	 * 		if accessing or updating the database throws one, or
	 * 		if the reviewer cannot be found in the reviewers table.
	 */
	public void addQCEvent(QCEvent qcEvent) throws SQLException {
		Connection catConn = makeConnection(true);
		try {
			int reviewerId = getReviewerId(catConn, 
					qcEvent.getUsername(), qcEvent.getRealname());
			PreparedStatement addPrepStmt = catConn.prepareStatement("INSERT INTO `" + 
					QCEVENTS_TABLE_NAME + "` (`qc_flag`, `qc_time`, `expocode`, " +
					"`socat_version`, `region_id`, `reviewer_id`, `qc_comment`) " +
					"VALUES(?, ?, ?, ?, ?, ?, ?);");
			addPrepStmt.setString(1, qcEvent.getFlag().toString());
			Date flagDate = qcEvent.getFlagDate();
			if ( flagDate.equals(DashboardUtils.DATE_MISSING_VALUE) )
				addPrepStmt.setLong(2, Math.round(System.currentTimeMillis() / 1000.0));
			else
				addPrepStmt.setLong(2, Math.round(flagDate.getTime() / 1000.0));
			addPrepStmt.setString(3, qcEvent.getExpocode());
			addPrepStmt.setString(4, qcEvent.getVersion());
			addPrepStmt.setString(5, qcEvent.getRegionID().toString());
			addPrepStmt.setInt(6, reviewerId);
			addPrepStmt.setString(7, qcEvent.getComment());
			addPrepStmt.executeUpdate();
		} finally {
			catConn.close();
		}
	}

	/**
	 * Removed any QC events that:
	 * (1) are for the given expocode,
	 * (2) are for the coastal region,
	 * (3) have new (N) or update (U) flags,
	 * (4) have a comment starting with "Initial".
	 * 
	 * This is for removing initial QC regional flags for cruises that were
	 * mistakenly assigned coastal region in the Southern or Arctic Oceans
	 * (which do not have coastal regions).
	 * 
	 * @param expocode
	 * 		remove initial coastal QC flags for the cruise with this expocode
	 * @returns 
	 * 		the row count returned from the DELETE SQL statement
	 * @throws SQLException
	 * 		if removing the QC flags throws one
	 */
	public int removeInitialCoastalQCEvent(String expocode) throws SQLException {
		int numDeleted = 0;
		Connection catConn = makeConnection(true);
		try {
			PreparedStatement removeCoastalPrepStmt = catConn.prepareStatement("DELETE FROM `" + 
					QCEVENTS_TABLE_NAME + "` WHERE `expocode` = ? AND `qc_flag` IN ('" + 
					DashboardUtils.QC_NEW_FLAG + "','" + DashboardUtils.QC_UPDATED_FLAG + 
					"') AND `region_id` = '" + DashboardUtils.COASTAL_REGION_ID + 
					"' AND `qc_comment` LIKE 'Initial%'");
			removeCoastalPrepStmt.setString(1, expocode);
			numDeleted = removeCoastalPrepStmt.executeUpdate();
		} finally {
			catConn.close();
		}
		return numDeleted;
	}

	private static final long MIN_FLAG_SEC_TIME = Math.round(30.0 * 365.2425 * 24.0 * 60.0 * 60.0);
	/**
	 * Get "the" QC flag for a dataset.  If the latest QC flag for different 
	 * regions are in conflict, and a global flag does not later resolve
	 * this conflict, the {@link DashboardUtils#QC_CONFLICT_FLAG} flag is 
	 * returned.
	 * 
	 * @param expocode
	 * 		get the QC flag for the dataset with this expocode.
	 * @return
	 * 		the QC flag for the dataset; never null
	 * @throws SQLException
	 * 		if a problem occurs getting all QC events for the data set, or
	 * 		if the QC flags in the dataset are corrupt.
	 */
	public Character getQCFlag(String expocode) throws SQLException {
		HashMap<Character,QCEvent> regionFlags = new HashMap<Character,QCEvent>();
		long lastUpdateTime = MIN_FLAG_SEC_TIME * 1000L;
		Connection catConn = makeConnection(false);
		try {
			// Get all the QC events for this data set, ordered so the latest are last
			PreparedStatement getPrepStmt = catConn.prepareStatement(
					"SELECT `qc_flag`, `qc_time`, `region_id` FROM `" + QCEVENTS_TABLE_NAME + 
					"` WHERE `expocode` = ? ORDER BY `qc_time` ASC;");
			getPrepStmt.setString(1, expocode);
			ResultSet rslts = getPrepStmt.executeQuery();
			try {
				while ( rslts.next() ) {
					String flagStr = rslts.getString(1);
					if ( flagStr == null )
						throw new SQLException("Unexpected null QC flag");
					flagStr = flagStr.trim();
					// Should not be blank, but might be from older code for a comment
					if ( (flagStr.length() < 1) || 
						 flagStr.equals(DashboardUtils.QC_COMMENT.toString()) ||
						 flagStr.equals(DashboardUtils.QC_RENAMED_FLAG.toString()) )
						continue;
					Character flag = flagStr.charAt(0);
					if ( (flagStr.length() > 1) ||
						 (DashboardUtils.FLAG_STATUS_MAP.get(flag) == null) ) 
						throw new SQLException("Unexpected QC flag of '" + flagStr + "'");
					long time = rslts.getLong(2);
					if ( time < MIN_FLAG_SEC_TIME )
						throw new SQLException("Unexpected null or invalid flag time of " + Long.toString(time));
					time *= 1000L;
					String region = rslts.getString(3);
					if ( region == null )
						throw new SQLException("Unexpected null region ID");
					region = region.trim();
					// Ignore missing region IDs
					if ( region.length() < 1 )
						continue;
					Character regionID = region.charAt(0);
					if ( (region.length() > 1) ||
						 (DashboardUtils.REGION_NAMES.get(regionID) == null) )
						throw new SQLException("Unexpected region ID of '" + region + "'");
					if ( DashboardUtils.GLOBAL_REGION_ID.equals(regionID) && 
						 ( DashboardUtils.QC_NEW_FLAG.equals(flag) || 
						   DashboardUtils.QC_UPDATED_FLAG.equals(flag) ) ) {
						lastUpdateTime = time;
					}
					QCEvent qcFlag = new QCEvent();
					qcFlag.setFlag(flag);
					qcFlag.setFlagDate(new Date(time));
					qcFlag.setRegionID(regionID);
					// last are latest, so no need to check the return value
					regionFlags.put(regionID, qcFlag);
				}
			} finally {
				rslts.close();
			}
		} finally {
			catConn.close();
		}

		// Should always have a global 'N' flag; maybe also a 'U', and maybe an override flag
		QCEvent globalEvent = regionFlags.get(DashboardUtils.GLOBAL_REGION_ID);
		Character globalFlag;
		long globalTime;
		if ( globalEvent == null ) {
			// Some v1 cruises do not have global flags
			globalFlag = DashboardUtils.QC_NEW_FLAG;
			globalTime = lastUpdateTime;
		}
		else {
			globalFlag = globalEvent.getFlag();
			globalTime = globalEvent.getFlagDate().getTime();
		}

		// Go through the latest flags for each region, making sure:
		// (1) global flag is last, or 
		// (2) all region flags are after global flag and match, or
		// (3) region flags after global flag match global flag  
		Character latestFlag = null;
		for ( Entry<Character,QCEvent> regionEntry : regionFlags.entrySet() ) {
			// Just compare non-global entries
			if ( DashboardUtils.GLOBAL_REGION_ID.equals(regionEntry.getKey()) )
				continue;
			// Ignore regional flags assigned (more than 1 s) before the last update
			QCEvent qcEvent = regionEntry.getValue();
			long time = qcEvent.getFlagDate().getTime();
			if  ( time - lastUpdateTime < -1000L )
				continue;
			Character flag;
			if ( time >  globalTime ) {
				// last flag for this region set after the last global flag; its flag applies
				flag = qcEvent.getFlag();
			}
			else { 
				// last flag for this region set before the last global flag; the global flag applies
				flag = globalFlag;
			}
			if ( latestFlag == null ) {
				latestFlag = flag;
			}
			else if ( ! latestFlag.equals(flag) ) {
				return DashboardUtils.QC_CONFLICT_FLAG;
			}
		}
		if ( latestFlag == null ) {
			// should not happen as region N/U assigned at same time as global N/U
			latestFlag = globalFlag;
		}
		return latestFlag;
	}

	/**
	 * Returns the SOCAT version number String for a dataset appended with
	 * and 'N', indicating the dataset is new to this version, or a 'U',
	 * indicating the dataset is an update to a dataset from a previous 
	 * SOCAT version.  Updates within a SOCAT version do NOT change an 'N' 
	 * to a 'U'.  The SOCAT version number used is the largest SOCAT version 
	 * number of global new and update QC flags for this dataset in the database.
	 * 
	 * @param expocode
	 * 		get the SOCAT version status String for the dataset with this expocode 
	 * @return
	 * 		the SOCAT version number status String; never null but may be empty
	 * 		if no global new or update QC flags exist.
	 * @throws SQLException
	 * 		if an error occurs retrieving the SOCAT version numbers
	 */
	public String getSocatVersionStatus(String expocode) throws SQLException {
		Double versionNum = null;
		Character status = null;
		Connection catConn = makeConnection(false);
		try {
			// Get all the QC events for this data set, ordered so the latest are last
			PreparedStatement getPrepStmt = catConn.prepareStatement(
					"SELECT `socat_version` FROM `" + QCEVENTS_TABLE_NAME + 
					"` WHERE `expocode` = ? AND `region_id` = '" + DashboardUtils.GLOBAL_REGION_ID +
					"' AND `qc_flag` IN ('" + DashboardUtils.QC_NEW_FLAG + 
					"', '" + DashboardUtils.QC_UPDATED_FLAG + "');");
			getPrepStmt.setString(1, expocode);
			ResultSet rslts = getPrepStmt.executeQuery();
			try {
				while ( rslts.next() ) {
					String versionStr = rslts.getString(1);
					if ( (versionStr == null) || versionStr.trim().isEmpty() ) {
						throw new SQLException("Unexpected missing SOCAT version");
					}
					try {
						Double version = Math.floor(Double.valueOf(versionStr) * 10.0) / 10.0;
						if ( versionNum == null ) {
							versionNum = version;
							status = 'N';
						}
						else if ( versionNum < version ) {
							versionNum = version;
							status = 'U';
						}
						else if ( versionNum > version ) {
							status = 'U';
						}
					} catch (NumberFormatException ex) {
						throw new SQLException("Unexpected non-numeric SOCAT version '" + versionStr + "'");
					}
				}
			} finally {
				rslts.close();
			}

		} finally {
			catConn.close();
		}
		if ( (versionNum == null) || (status == null) )
			return "";
		return String.format("%.1f%c", versionNum, status);
	}

	/**
	 * Creates a QCEvent object from the values in the current row 
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
	private QCEvent createQCEvent(ResultSet results) throws SQLException {
		QCEvent qcEvent = new QCEvent();
		Long id = results.getLong("qc_id");
		if ( id < 1L )
			throw new SQLException("Unexpected invalid qc_id");
		qcEvent.setId(id);
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
		qcEvent.setVersion(results.getString("socat_version"));
		if ( results.wasNull() )
			qcEvent.setVersion(null);
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
	public ArrayList<QCEvent> getQCEvents(String expocode) throws SQLException {
		ArrayList<QCEvent> eventsList = new ArrayList<QCEvent>();
		Connection catConn = makeConnection(false);
		try {
			PreparedStatement prepStmt = catConn.prepareStatement(
					"SELECT * FROM `" + QCEVENTS_TABLE_NAME + "` JOIN `" + 
					REVIEWERS_TABLE_NAME + "` ON " + QCEVENTS_TABLE_NAME + 
					".reviewer_id = " + REVIEWERS_TABLE_NAME + ".reviewer_id WHERE " + 
					QCEVENTS_TABLE_NAME + ".expocode = ? ORDER BY " + 
					QCEVENTS_TABLE_NAME + ".qc_time DESC;");
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
	 * the DataLocations to the WOCELocations table.
	 * 
	 * @param woceEvent
	 * 		the WOCE event to add; 
	 * 		the ID for the WOCE event will be assigned on normal return
	 * @throws SQLException
	 * 		if accessing or updating the database throws one, or
	 * 		if the reviewer cannot be found in the reviewers table, or
	 * 		if a problem occurs adding the WOCE event
	 */
	public void addWoceEvent(WoceEvent woceEvent) throws SQLException {
		Connection catConn = makeConnection(true);
		try {
			int reviewerId = getReviewerId(catConn, 
					woceEvent.getUsername(), woceEvent.getRealname());
			// Add the WOCE event
			PreparedStatement prepStmt = catConn.prepareStatement("INSERT INTO `" + 
					WOCEEVENTS_TABLE_NAME + "` (`woce_name`, `woce_flag`, `woce_time`, " +
					"`expocode`, `socat_version`, `data_name`, `reviewer_id`, " +
					"`woce_comment`) VALUES(?, ?, ?, ?, ?, ?, ?, ?);");
			prepStmt.setString(1, woceEvent.getWoceName());
			prepStmt.setString(2, woceEvent.getFlag().toString());
			Date flagDate = woceEvent.getFlagDate();
			if ( flagDate.equals(DashboardUtils.DATE_MISSING_VALUE) )
				prepStmt.setLong(3, Math.round(System.currentTimeMillis() / 1000.0));
			else
				prepStmt.setLong(3, Math.round(flagDate.getTime() / 1000.0));
			prepStmt.setString(4, woceEvent.getExpocode());
			prepStmt.setString(5, woceEvent.getVersion());
			prepStmt.setString(6, woceEvent.getVarName());
			prepStmt.setInt(7, reviewerId);
			prepStmt.setString(8, woceEvent.getComment());
			if ( prepStmt.executeUpdate() != 1 )
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
			woceEvent.setId(woceId);
			// Add the DataLocations to the WOCELocations table
			prepStmt = catConn.prepareStatement("INSERT INTO `" + WOCELOCATIONS_TABLE_NAME + 
					"` (`woce_id`, `region_id`, `row_num`, `longitude`, `latitude`, " +
					"`data_time`, `data_value`) VALUES (?, ?, ?, ?, ?, ?, ?);");
			for (DataLocation location : woceEvent.getLocations() ) {
				prepStmt.setLong(1, woceId);
				prepStmt.setString(2, location.getRegionID().toString());
				Integer intVal = location.getRowNumber();
				if ( intVal.equals(DashboardUtils.INT_MISSING_VALUE) )
					prepStmt.setNull(3, java.sql.Types.INTEGER);
				else
					prepStmt.setInt(3, intVal);
				Double dblVal = location.getLongitude();
				if ( dblVal.equals(DashboardUtils.FP_MISSING_VALUE) )
					prepStmt.setNull(4, java.sql.Types.DOUBLE);
				else
					prepStmt.setDouble(4, dblVal);
				dblVal = location.getLatitude();
				if ( dblVal.equals(DashboardUtils.FP_MISSING_VALUE) )
					prepStmt.setNull(5, java.sql.Types.DOUBLE);
				else
					prepStmt.setDouble(5, dblVal);
				Date dateVal = location.getDataDate();
				if ( dateVal.equals(DashboardUtils.DATE_MISSING_VALUE) )
					prepStmt.setNull(6, java.sql.Types.BIGINT);
				else
					prepStmt.setLong(6, Math.round(dateVal.getTime() / 1000.0));
				dblVal = location.getDataValue();
				if ( dblVal.equals(DashboardUtils.FP_MISSING_VALUE) )
					prepStmt.setNull(7, java.sql.Types.DOUBLE);
				else
					prepStmt.setDouble(7, dblVal);
				prepStmt.executeUpdate();
				if ( prepStmt.getUpdateCount() != 1 ) 
					throw new SQLException("Adding the WOCE location was unsuccessful");
			}
		} finally {
			catConn.close();
		}
	}

	/**
	 * Creates a WoceEvent without any locations 
	 * from the values in the current row of a ResultSet.
	 * 
	 * @param results
	 * 		assign values from the current row of this ResultSet; must 
	 * 		include columns with names woce_flag, woce_time, expocode, 
	 * 		socat_version, data_name, username, realname, 
	 * 		and woce_comment.
	 * @returns
	 * 		the created WOCE event
	 * @throws SQLException
	 * 		if getting values from the ResultSet throws one
	 */
	private WoceEvent createWoceEvent(ResultSet results) throws SQLException {
		WoceEvent woceEvent = new WoceEvent();
		Long id = results.getLong("woce_id");
		if ( id < 1 )
			throw new SQLException("Unexpected invalid woce_id");
		woceEvent.setId(id);
		woceEvent.setWoceName(results.getString("woce_name"));
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
		woceEvent.setVersion(results.getString("socat_version"));
		woceEvent.setVarName(results.getString("data_name"));
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
	 * @param latestFirst
	 * 		order with the latest first?
	 * @return
	 * 		list of WOCE events for the dataset, ordered by the dates
	 * 		of the events (either latest first or latest last);
	 * 		never null but may be empty
	 * @throws SQLException
	 * 		if accessing the database or reading the results throws one
	 */
	public ArrayList<WoceEvent> getWoceEvents(String expocode, 
									boolean latestFirst) throws SQLException {
		ArrayList<WoceEvent> eventsList = new ArrayList<WoceEvent>();
		Connection catConn = makeConnection(false);
		try {
			String order;
			if ( latestFirst )
				order = "DESC;";
			else
				order = "ASC;";
			PreparedStatement prepStmt = catConn.prepareStatement("SELECT * FROM `" + 
					WOCEEVENTS_TABLE_NAME + "` JOIN `" + REVIEWERS_TABLE_NAME + 
					"` ON " + WOCEEVENTS_TABLE_NAME + ".reviewer_id = " + 
					REVIEWERS_TABLE_NAME + ".reviewer_id WHERE " + 
					WOCEEVENTS_TABLE_NAME + ".expocode = ? ORDER BY " + 
					WOCEEVENTS_TABLE_NAME + ".woce_time " + order);
			prepStmt.setString(1, expocode);
			ResultSet results = prepStmt.executeQuery();
			try {
				while ( results.next() ) {
					eventsList.add(createWoceEvent(results));
				}
			} finally {
				results.close();
			}
			prepStmt = catConn.prepareStatement("SELECT * FROM `" + WOCELOCATIONS_TABLE_NAME + 
					"` WHERE `woce_id` = ? ORDER BY `row_num`;");
			for (WoceEvent event : eventsList) {
				// Directly modify the list of locations in the WOCE event
				ArrayList<DataLocation> locations = event.getLocations();
				prepStmt.setLong(1, event.getId());
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

	/**
	 * Resets WOCE flags for all WOCE events (if any) of a cruise to the 
	 * corresponding "old" WOCE flag values.  This should be called prior 
	 * to adding WOCE events for an updated cruise.
	 * 
	 * @param expocode
	 * 		reset the WOCE events for the cruise with this expocode
	 * @throws SQLException
	 * 		if modifying the WOCE events in the database throws one
	 */
	public void resetWoceEvents(String expocode) throws SQLException {
		Connection catConn = makeConnection(true);
		try {
			PreparedStatement modifyWocePrepStmt = catConn.prepareStatement(
					"UPDATE `" + WOCEEVENTS_TABLE_NAME + "` SET `woce_flag` = ? " +
					"WHERE `expocode` = ? AND `woce_flag` = ?;");
			modifyWocePrepStmt.setString(2, expocode);

			modifyWocePrepStmt.setString(1, DashboardUtils.OLD_WOCE_GOOD.toString());
			modifyWocePrepStmt.setString(3, DashboardUtils.WOCE_GOOD.toString());
			modifyWocePrepStmt.executeUpdate();

			modifyWocePrepStmt.setString(1, DashboardUtils.OLD_WOCE_NOT_CHECKED.toString());
			modifyWocePrepStmt.setString(3, DashboardUtils.WOCE_NOT_CHECKED.toString());
			modifyWocePrepStmt.executeUpdate();

			modifyWocePrepStmt.setString(1, DashboardUtils.OLD_WOCE_QUESTIONABLE.toString());
			modifyWocePrepStmt.setString(3, DashboardUtils.WOCE_QUESTIONABLE.toString());
			modifyWocePrepStmt.executeUpdate();

			modifyWocePrepStmt.setString(1, DashboardUtils.OLD_WOCE_BAD.toString());
			modifyWocePrepStmt.setString(3, DashboardUtils.WOCE_BAD.toString());
			modifyWocePrepStmt.executeUpdate();

			modifyWocePrepStmt.setString(1, DashboardUtils.OLD_WOCE_NO_DATA.toString());
			modifyWocePrepStmt.setString(3, DashboardUtils.WOCE_NO_DATA.toString());
			modifyWocePrepStmt.executeUpdate();
		} finally {
			catConn.close();
		}
	}

	/**
	 * Restores the WOCE flags in the database associated with the given WOCE event.
	 * This is done by changing the "old" WOCE flag value back to the corresponding 
	 * "regular" WOCE flag value.
	 *  
	 * @param woceEvent
	 * 		WOCE event to restore
	 * @return
	 * 		the "regular" WOCE flag for reassigned to this event 
	 * @throws IllegalArgumentException
	 * 		if the WOCE flag in the event is not a "old" WOCE flag, or
	 * 		if WOCE event does not match an existing WOCE flag
	 * 		(woce_id, expocode, old woce_flag)
	 * @throws SQLException
	 * 		if modifying the WOCE event in the database throws one
	 */
	public Character restoreWoceEvent(WoceEvent woceEvent) 
								throws IllegalArgumentException, SQLException {
		if ( woceEvent.getId() < 1 )
			throw new IllegalArgumentException("Invalid ID for WOCE event");

		Character newFlag;
		Character oldFlag = woceEvent.getFlag();
		if ( oldFlag.equals(DashboardUtils.OLD_WOCE_GOOD) ) {
			newFlag = DashboardUtils.WOCE_GOOD;
		}
		else if ( oldFlag.equals(DashboardUtils.OLD_WOCE_NOT_CHECKED) ) {
			newFlag = DashboardUtils.WOCE_NOT_CHECKED;
		}
		else if ( oldFlag.equals(DashboardUtils.OLD_WOCE_QUESTIONABLE) ) {
			newFlag = DashboardUtils.WOCE_QUESTIONABLE;
		}
		else if ( oldFlag.equals(DashboardUtils.OLD_WOCE_BAD) ) {
			newFlag = DashboardUtils.WOCE_BAD;
		}
		else if ( oldFlag.equals(DashboardUtils.OLD_WOCE_NO_DATA) ) {
			newFlag = DashboardUtils.WOCE_NO_DATA;
		}
		else {
			throw new IllegalArgumentException("Invalid \"old\" WOCE flag of '" + oldFlag + "'");
		}

		Connection catConn = makeConnection(true);
		try {
			PreparedStatement prepStmt = catConn.prepareStatement("UPDATE `" + 
					WOCEEVENTS_TABLE_NAME + "` SET `woce_flag` = ? WHERE " +
					"`woce_id` = ? AND `expocode` = ? AND `woce_flag` = ?;");
			prepStmt.setString(1, newFlag.toString());
			prepStmt.setLong(2, woceEvent.getId());
			prepStmt.setString(3, woceEvent.getExpocode());
			prepStmt.setString(4, oldFlag.toString());
			if ( prepStmt.executeUpdate() != 1 ) {
				// woce_id is a unique key, so could not be more than one
				throw new IllegalArgumentException("Unable to match the given WOCE event");
			}
		} finally {
			catConn.close();
		}

		// Success; return the WOCE flag that was reassigned 
		return newFlag;
	}

	/**
	 * Adds rename QC and WOCE events and appropriately renames 
	 * the expocode for flags in the database.  If the cruise has
	 * never been submitted for QC, or was already renamed, (i.e.,
	 * has no QC events except maybe renames), returns without 
	 * making any changes.
	 * 
	 * @param oldExpocode
	 * 		standardized old expocode 
	 * @param newExpocode
	 * 		standardized new expocode
	 * @param version
	 * 		SOCAT version to associate with the rename QC and WOCE events
	 * @param username
	 * 		name of the user to associate with the rename QC and WOCE events 
	 * @throws SQLException
	 * 		if username is not a known user, or
	 * 		if accessing or updating the database throws one
	 */
	public void renameCruiseFlags(String oldExpocode, String newExpocode, 
			String socatVersion, String username) throws SQLException {
		Connection catConn = makeConnection(true);
		try {
			long nowSec = Math.round(System.currentTimeMillis() / 1000.0);
			int reviewerId = getReviewerId(catConn, username, "");
			String renameComment = "Rename from " + oldExpocode + " to " + newExpocode;

			// Update the old expocode to the new expocode in the appropriate QC events
			PreparedStatement modifyQcPrepStmt = catConn.prepareStatement(
					"UPDATE `" + QCEVENTS_TABLE_NAME + "` SET `expocode` = ? " +
					"WHERE `expocode` = ? AND `qc_flag` <> ?;");
			modifyQcPrepStmt.setString(1, newExpocode);
			modifyQcPrepStmt.setString(2, oldExpocode);
			modifyQcPrepStmt.setString(3, DashboardUtils.QC_RENAMED_FLAG.toString());
			modifyQcPrepStmt.executeUpdate();
			int updateCount = modifyQcPrepStmt.getUpdateCount();
			if ( updateCount < 0 )
				throw new SQLException("Unexpected update count from renaming QC expocodes");
			if ( updateCount == 0 ) {
				// If no QC flags with the old expocode, cruise has never been submitted 
				// or was already renamed; in either case, nothing to do. 
				return;
			}

			// Add two rename QC events; one for the old expocode and one for the new expocode
			PreparedStatement addQcPrepStmt = catConn.prepareStatement(
					"INSERT INTO `" + QCEVENTS_TABLE_NAME + "` (`qc_flag`, `qc_time`, " +
					"`expocode`, `socat_version`, `region_id`, `reviewer_id`, `qc_comment`) " +
					"VALUES (?, ?, ?, ?, ?, ?, ?), (?, ?, ?, ?, ?, ?, ?), (?, ?, ?, ?, ?, ?, ?);");
			addQcPrepStmt.setString(1, DashboardUtils.QC_RENAMED_FLAG.toString());
			addQcPrepStmt.setString(8, DashboardUtils.QC_RENAMED_FLAG.toString());
			addQcPrepStmt.setString(15, DashboardUtils.QC_COMMENT.toString());
			addQcPrepStmt.setLong(2, nowSec);
			addQcPrepStmt.setLong(9, nowSec);
			addQcPrepStmt.setLong(16, nowSec);
			addQcPrepStmt.setString(3, oldExpocode);
			addQcPrepStmt.setString(10, newExpocode);
			addQcPrepStmt.setString(17, newExpocode);
			addQcPrepStmt.setString(4, socatVersion);
			addQcPrepStmt.setString(11, socatVersion);
			addQcPrepStmt.setString(18, socatVersion);
			addQcPrepStmt.setString(5, DashboardUtils.GLOBAL_REGION_ID.toString());
			addQcPrepStmt.setString(12, DashboardUtils.GLOBAL_REGION_ID.toString());
			addQcPrepStmt.setString(19, DashboardUtils.GLOBAL_REGION_ID.toString());
			addQcPrepStmt.setInt(6, reviewerId);
			addQcPrepStmt.setInt(13, reviewerId);
			addQcPrepStmt.setInt(20, reviewerId);
			addQcPrepStmt.setString(7, renameComment);
			addQcPrepStmt.setString(14, renameComment);
			addQcPrepStmt.setString(21, renameComment);
			addQcPrepStmt.executeUpdate();

			// Update the old expocode to the new expocode in the appropriate WOCE events
			PreparedStatement modifyWocePrepStmt = catConn.prepareStatement(
					"UPDATE `" + WOCEEVENTS_TABLE_NAME + "` SET `expocode` = ? " +
					"WHERE `expocode` = ? AND `woce_flag` <> ?;");
			modifyWocePrepStmt.setString(1, newExpocode);
			modifyWocePrepStmt.setString(2, oldExpocode);
			modifyWocePrepStmt.setString(3, DashboardUtils.WOCE_RENAME.toString());
			modifyWocePrepStmt.executeUpdate();

			// Add two rename WOCE events; one for the old expocode and one for the new expocode
			PreparedStatement addWocePrepStmt = catConn.prepareStatement("INSERT INTO `" + 
					WOCEEVENTS_TABLE_NAME + "` (`woce_name`, `woce_flag`, `woce_time`, " +
					"`expocode`, `socat_version`, `reviewer_id`, `woce_comment`) " +
					"VALUES (?, ?, ?, ?, ?, ?, ?), (?, ?, ?, ?, ?, ?, ?);");
			addWocePrepStmt.setString(1, SocatTypes.WOCE_CO2_WATER.getVarName());
			addWocePrepStmt.setString(8, SocatTypes.WOCE_CO2_WATER.getVarName());
			addWocePrepStmt.setString(2, DashboardUtils.WOCE_RENAME.toString());
			addWocePrepStmt.setString(9, DashboardUtils.WOCE_RENAME.toString());
			addWocePrepStmt.setLong(3, nowSec);
			addWocePrepStmt.setLong(10, nowSec);
			addWocePrepStmt.setString(4, oldExpocode);
			addWocePrepStmt.setString(11, newExpocode);
			addWocePrepStmt.setString(5, socatVersion);
			addWocePrepStmt.setString(12, socatVersion);
			addWocePrepStmt.setInt(6, reviewerId);
			addWocePrepStmt.setInt(13, reviewerId);
			addWocePrepStmt.setString(7, renameComment);
			addWocePrepStmt.setString(14, renameComment);
			addWocePrepStmt.executeUpdate();
		} finally {
			catConn.close();
		}
	}

	/**
	 * Removes all QC and WOCE flags that have the given expocode and socat version.
	 * 
	 * @param expocode
	 * 		expocode to use
	 * @param version
	 * 		socat version to use
	 * @throws SQLException
	 * 		if generating the prepared statements or deleting the flags throws one
	 */
	public void removeFlagsForCruiseVersion(String expocode, String socatVersion) throws SQLException {
		Connection catConn = makeConnection(true);
		try {
			// Remove any QC events for this cruise version
			PreparedStatement deleteQcPrepStmt = catConn.prepareStatement("DELETE FROM `" + 
					QCEVENTS_TABLE_NAME + "` WHERE `expocode` = ? AND `socat_version` = ?;");
			deleteQcPrepStmt.setString(1, expocode);
			deleteQcPrepStmt.setString(2, socatVersion);
			deleteQcPrepStmt.executeUpdate();

			// Remove any WOCE events and associated locations for this cruise version
			PreparedStatement deleteWoceLocPrepStmt = catConn.prepareStatement("DELETE FROM `" +
					WOCEEVENTS_TABLE_NAME + "`, `" + WOCELOCATIONS_TABLE_NAME + "` USING `" +
					WOCEEVENTS_TABLE_NAME + "` JOIN `" + WOCELOCATIONS_TABLE_NAME + "` ON " + 
					WOCEEVENTS_TABLE_NAME + ".woce_id = " + WOCELOCATIONS_TABLE_NAME + ".woce_id WHERE " + 
					WOCEEVENTS_TABLE_NAME + ".expocode = ? AND " + 
					WOCEEVENTS_TABLE_NAME + ".socat_version = ?;");
			deleteWoceLocPrepStmt.setString(1, expocode);
			deleteWoceLocPrepStmt.setString(2, socatVersion);
			deleteWoceLocPrepStmt.executeUpdate();
		} finally {
			catConn.close();
		}		
	}

}
