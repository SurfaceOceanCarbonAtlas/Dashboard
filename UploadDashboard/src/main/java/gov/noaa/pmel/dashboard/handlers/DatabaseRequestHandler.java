package gov.noaa.pmel.dashboard.handlers;

import gov.noaa.pmel.dashboard.datatype.SocatTypes;
import gov.noaa.pmel.dashboard.qc.DataLocation;
import gov.noaa.pmel.dashboard.qc.DataQCEvent;
import gov.noaa.pmel.dashboard.qc.QCEvent;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DatasetQCFlag;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * Handles database requests for dealing with flag events
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
     *         name of the configuration properties file
     *
     * @throws FileNotFoundException
     *         if the properties file does not exist
     * @throws IOException
     *         if the properties file cannot cannot be read
     * @throws IllegalArgumentException
     *         if the properties file has missing or invalid values
     * @throws SQLException
     *         if there are problems connecting to or executing a query on the database
     */
    public DatabaseRequestHandler(String configFilename)
            throws FileNotFoundException, IOException, IllegalArgumentException, SQLException {
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
     *         SQL driver class name, such as "com.mysql.jdbc.Driver"
     * @param databaseUrl
     *         database URL, such as "jdbc:mysql://localhost:3306/SOCATFlags"
     * @param selectUser
     *         name of database user with SELECT privileges (read-only user)
     * @param selectPass
     *         password of database user with SELECT privileges (read-only user)
     * @param updateUser
     *         name of database user with SELECT, UPDATE, INSERT, DELETE privileges (read-write user)
     * @param updatePass
     *         password of database user with SELECT, UPDATE, INSERT, DELETE privileges (read-write user)
     *
     * @throws IllegalArgumentException
     *         if a given value is invalid (null or empty)
     * @throws SQLException
     *         if there are problems connecting to or executing a query on the database
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
     *         SQL driver class name, such as "com.mysql.jdbc.Driver"
     *
     * @throws IllegalArgumentException
     *         if either database user does not have adequate privileges
     * @throws SQLException
     *         if registering the SQL driver fails, or if connecting to the database throws one
     */
    private void testConnections(String sqlDriverName) throws SQLException {
        // Register the SQL driver - no harm if already registered
        try {
            Class.forName(sqlDriverName).newInstance();
        } catch ( Exception ex ) {
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
     *         if true, the connection will be made using updateUser; otherwise, the connection will be made using
     *         selectUser
     *
     * @return the database catalog connection
     *
     * @throws SQLException
     *         if connecting to the database catalog throws one or if a null connection is returned
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
     * Get the ID for a reviewer from the reviewers database table. Uses the username for the reviewer unless it is
     * empty, in which case it uses the realname of the reviewer.
     *
     * @param catConn
     *         connection to the database
     * @param username
     *         username of the reviewer
     * @param realname
     *         realname of the reviewer
     *
     * @return ID of the reviewer
     *
     * @throws SQLException
     *         if accessing the database throws one, or if the reviewer name cannot be found
     */
    private int getReviewerId(Connection catConn, String username, String realname) throws SQLException {
        int reviewerId;
        PreparedStatement prepStmt;
        if ( !username.isEmpty() ) {
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
            if ( !results.first() ) {
                if ( !username.isEmpty() )
                    throw new SQLException("Reviewer username '" + username + "' not found");
                else
                    throw new SQLException("Reviewer realname '" + realname + "' not found");
            }
            reviewerId = results.getInt(1);
            if ( reviewerId <= 0 ) {
                if ( !username.isEmpty() )
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
     * Retrieves the actual name for a reviewer from the Reviewers table using the reviewer's username.
     *
     * @param username
     *         username for a reviewer
     *
     * @return actual name of the reviewer; may be null if not found
     *
     * @throws SQLException
     *         if accessing the database throws one
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
     * Retrieves the username for a reviewer from the Reviewers table using the reviewer's actual name.
     *
     * @param realname
     *         actual name of the reviewer
     *
     * @return username for a reviewer; may be null if not found
     *
     * @throws SQLException
     *         if accessing the database throws one
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
     * Retrieves the e-mail address for a reviewer from the Reviewers table using the reviewer's username.
     *
     * @param username
     *         username for a reviewer
     *
     * @return e-mail address of the reviewer; may be null if not found
     *
     * @throws SQLException
     *         if accessing the database throws one
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
     * Adds a new dataset QC event for a dataset.  The QC flag name and id is ignored.
     *
     * @param qcEvents
     *         the QC event to add
     *
     * @throws SQLException
     *         if accessing or updating the database throws one, or
     *         if the reviewer cannot be found in the reviewers table.
     */
    public void addDatasetQCEvents(Collection<QCEvent> qcEvents) throws SQLException {
        Connection catConn = makeConnection(true);
        try {
            PreparedStatement addPrepStmt = catConn.prepareStatement("INSERT INTO `" +
                    QCEVENTS_TABLE_NAME + "` (`qc_flag`, `qc_time`, `expocode`, " +
                    "`socat_version`, `region_id`, `reviewer_id`, `qc_comment`) " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?);");
            for (QCEvent event : qcEvents) {
                int reviewerId = getReviewerId(catConn, event.getUsername(), event.getRealname());
                addPrepStmt.setString(1, event.getFlagValue());
                Date flagDate = event.getFlagDate();
                if ( flagDate.equals(DashboardUtils.DATE_MISSING_VALUE) )
                    addPrepStmt.setLong(2, Math.round(System.currentTimeMillis() / 1000.0));
                else
                    addPrepStmt.setLong(2, Math.round(flagDate.getTime() / 1000.0));
                addPrepStmt.setString(3, event.getDatasetId());
                addPrepStmt.setString(4, event.getVersion());
                addPrepStmt.setString(5, event.getRegionId());
                addPrepStmt.setInt(6, reviewerId);
                addPrepStmt.setString(7, event.getComment());
                addPrepStmt.executeUpdate();
            }
        } finally {
            catConn.close();
        }
    }

    private static final long MIN_FLAG_SEC_TIME = Math.round(30.0 * 365.2425 * 24.0 * 60.0 * 60.0);

    /**
     * Get the dataset QC flag for the given dataset.  If the latest actual QC flag for different regions
     * are in conflict, and a global flag does not later resolve this conflict, a conflict flag is returned.
     *
     * @param expocode
     *         get the dataset QC flag for the dataset with this ID.
     *
     * @return the QC flag for the dataset; never null
     *
     * @throws SQLException
     *         if a problem occurs getting all QC events for the data set, or
     *         if the QC flags in the dataset are corrupt.
     */
    public DatasetQCFlag getDatasetQCFlag(String expocode) throws SQLException {
        HashMap<String,QCEvent> regionFlags = new HashMap<String,QCEvent>();
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
                    String flagValue = rslts.getString(1);
                    if ( flagValue == null )
                        throw new SQLException("Unexpected null QC flag");
                    flagValue = flagValue.trim();
                    // Should not be blank, but might be from older code for a comment
                    if ( flagValue.length() < 1 )
                        continue;
                    DatasetQCFlag flag = DatasetQCFlag.fromString(flagValue);
                    if ( flag.isCommentFlag() || flag.isRenameFlag() )
                        continue;
                    long time = rslts.getLong(2);
                    if ( time < MIN_FLAG_SEC_TIME )
                        throw new SQLException("Unexpected null or invalid flag time of " + Long.toString(time));
                    time *= 1000L;
                    String regionID = rslts.getString(3);
                    if ( regionID == null )
                        throw new SQLException("Unexpected null region ID");
                    regionID = regionID.trim();
                    // Ignore missing region IDs
                    if ( regionID.length() < 1 )
                        continue;
                    if ( DashboardUtils.REGION_ID_GLOBAL.equals(regionID) &&
                            (flag.isNewAwaitingQC() || flag.isUpdatedAwaitingQC()) ) {
                        lastUpdateTime = time;
                    }
                    QCEvent qcFlag = new QCEvent();
                    qcFlag.setFlagValue(flagValue);
                    qcFlag.setFlagDate(new Date(time));
                    qcFlag.setRegionId(regionID);
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
        QCEvent globalEvent = regionFlags.get(DashboardUtils.REGION_ID_GLOBAL);
        DatasetQCFlag globalFlag;
        long globalTime;
        if ( globalEvent == null ) {
            // Some v1 cruises do not have global flags
            globalFlag = new DatasetQCFlag(DatasetQCFlag.Status.NEW_AWAITING_QC);
            globalTime = lastUpdateTime;
        }
        else {
            globalFlag = DatasetQCFlag.fromString(globalEvent.getFlagValue());
            globalTime = globalEvent.getFlagDate().getTime();
        }

        // Go through the latest flags for each region, making sure:
        // (1) global flag is last, or
        // (2) all region flags are after global flag and match, or
        // (3) region flags after global flag match global flag
        DatasetQCFlag latestFlag = null;
        for (Entry<String,QCEvent> regionEntry : regionFlags.entrySet()) {
            // Just compare non-global entries
            if ( DashboardUtils.REGION_ID_GLOBAL.equals(regionEntry.getKey()) )
                continue;
            // Ignore regional flags assigned (more than 1 s) before the last update
            QCEvent qcEvent = regionEntry.getValue();
            long time = qcEvent.getFlagDate().getTime();
            if ( time - lastUpdateTime < -1000L )
                continue;
            DatasetQCFlag flag;
            if ( time > globalTime ) {
                // last flag for this region set after the last global flag; its flag applies
                flag = DatasetQCFlag.fromString(qcEvent.getFlagValue());
            }
            else {
                // last flag for this region set before the last global flag; the global flag applies
                flag = globalFlag;
            }
            if ( latestFlag == null ) {
                latestFlag = flag;
            }
            else if ( !latestFlag.getActualFlag().equals(flag.getActualFlag()) ) {
                // conflicts only occur with mismatches in the actual flag
                return new DatasetQCFlag(DatasetQCFlag.Status.CONFLICTED);
            }
        }
        if ( latestFlag == null ) {
            // should not happen as region N/U assigned at same time as global N/U
            latestFlag = globalFlag;
        }
        return latestFlag;
    }

    /**
     * Returns the version number String for a dataset appended with and 'N', indicating the dataset is new to this
     * version, or a 'U', indicating the dataset is an update to a dataset from a previous version.  Updates within a
     * version do NOT change an 'N' to a 'U'.  The version number used is the largest version number of global new and
     * update QC flags for this dataset in the database.
     *
     * @param expocode
     *         get the version status String for the dataset with this dataset ID
     *
     * @return the version number status String; never null but may be empty if no global new or update QC flags exist.
     *
     * @throws SQLException
     *         if an error occurs retrieving the version numbers
     */
    public String getVersionStatus(String expocode) throws SQLException {
        Double versionNum = null;
        Character status = null;
        Connection catConn = makeConnection(false);
        try {
            // Get all the QC events for this data set, ordered so the latest are last
            PreparedStatement getPrepStmt = catConn.prepareStatement(
                    "SELECT `socat_version` FROM `" + QCEVENTS_TABLE_NAME +
                            "` WHERE `expocode` = ? AND `region_id` = '" + DashboardUtils.REGION_ID_GLOBAL +
                            "' AND ( `qc_flag` LIKE '" + DatasetQCFlag.FLAG_NEW_AWAITING_QC +
                            "%' OR `qc_flag` LIKE '" + DatasetQCFlag.FLAG_UPDATED_AWAITING_QC + "%' );");
            getPrepStmt.setString(1, expocode);
            ResultSet rslts = getPrepStmt.executeQuery();
            try {
                while ( rslts.next() ) {
                    String versionStr = rslts.getString(1);
                    if ( (versionStr == null) || versionStr.trim().isEmpty() ) {
                        throw new SQLException("Unexpected missing version");
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
                    } catch ( NumberFormatException ex ) {
                        throw new SQLException("Unexpected non-numeric version '" + versionStr + "'");
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
     * Creates a dataset QCEvent object from the values in the current row of a ResultSet.
     *
     * @param results
     *         assign values from the current row of this ResultSet; must include columns with names qc_flag, qc_time,
     *         expocode, socat_version, region_id, username, realname, and qc_comment.
     *
     * @throws SQLException
     *         if getting values from the ResultSet throws one
     */
    private QCEvent createDatasetQCEvent(ResultSet results) throws SQLException {
        QCEvent qcEvent = new QCEvent();
        qcEvent.setFlagName(QCEvent.DATASET_QCFLAG_NAME);
        Long id = results.getLong("qc_id");
        if ( id < 1L )
            throw new SQLException("Unexpected invalid qc_id");
        qcEvent.setId(id);
        qcEvent.setFlagValue(results.getString("qc_flag"));
        qcEvent.setFlagDate(new Date(results.getLong("qc_time") * 1000L));
        if ( results.wasNull() )
            qcEvent.setFlagDate(null);
        qcEvent.setDatasetId(results.getString("expocode"));
        qcEvent.setVersion(results.getString("socat_version"));
        qcEvent.setRegionId(results.getString("region_id"));
        qcEvent.setUsername(results.getString("username"));
        qcEvent.setRealname(results.getString("realname"));
        qcEvent.setComment(results.getString("qc_comment"));
        return qcEvent;
    }

    /**
     * Retrieves the current list of dataset QC events for a dataset.
     *
     * @param expocode
     *         get the QC events for the dataset with this expocode
     *
     * @return the list of QC events for the dataset, ordered by the date of the event (latest event first)
     *
     * @throws SQLException
     *         if accessing the database or reading the results throws one
     */
    public ArrayList<QCEvent> getDatasetQCEvents(String expocode) throws SQLException {
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
                    eventsList.add(createDatasetQCEvent(results));
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
     * Adds a new data QC event for a dataset.  This includes assigning the DataLocations to the WOCELocations table.
     *
     * @param woceEvents
     *         the data QC events to add; the ID in each data QC event will be assigned for those successfully added
     *
     * @throws SQLException
     *         if accessing or updating the database throws one,
     *         if the reviewer cannot be found in the reviewers table, or
     *         if a problem occurs adding the data QC event
     */
    public void addDataQCEvent(Collection<DataQCEvent> woceEvents) throws SQLException {
        Connection catConn = makeConnection(true);
        try {
            PreparedStatement eventPrepStmt = catConn.prepareStatement("INSERT INTO `" +
                    WOCEEVENTS_TABLE_NAME + "` (`woce_name`, `woce_flag`, `woce_time`, " +
                    "`expocode`, `socat_version`, `data_name`, `reviewer_id`, " +
                    "`woce_comment`) VALUES(?, ?, ?, ?, ?, ?, ?, ?);");
            PreparedStatement locPrepStmt = catConn.prepareStatement("INSERT INTO `" + WOCELOCATIONS_TABLE_NAME +
                    "` (`woce_id`, `row_num`, `longitude`, `latitude`, `data_time`, `data_value`) " +
                    "VALUES (?, ?, ?, ?, ?, ?);");
            for (DataQCEvent event : woceEvents) {
                int reviewerId = getReviewerId(catConn, event.getUsername(), event.getRealname());
                // Add the WOCE event
                eventPrepStmt.setString(1, event.getFlagName());
                eventPrepStmt.setString(2, event.getFlagValue());
                Date flagDate = event.getFlagDate();
                if ( flagDate.equals(DashboardUtils.DATE_MISSING_VALUE) )
                    eventPrepStmt.setLong(3, Math.round(System.currentTimeMillis() / 1000.0));
                else
                    eventPrepStmt.setLong(3, Math.round(flagDate.getTime() / 1000.0));
                eventPrepStmt.setString(4, event.getDatasetId());
                eventPrepStmt.setString(5, event.getVersion());
                eventPrepStmt.setString(6, event.getVarName());
                eventPrepStmt.setInt(7, reviewerId);
                eventPrepStmt.setString(8, event.getComment());
                if ( eventPrepStmt.executeUpdate() != 1 )
                    throw new SQLException("Adding the data QC event was unsuccessful");

                // Get the woce_id for the added WOCE event
                long woceId;
                ResultSet results = catConn.createStatement().executeQuery("SELECT LAST_INSERT_ID();");
                try {
                    if ( !results.first() )
                        throw new SQLException("Unexpected failure to get the woce_id for an added data QC event");
                    woceId = results.getLong(1);
                    if ( woceId <= 0 )
                        throw new SQLException("Unexpected invalid woce_id for an added data QC event");
                } finally {
                    results.close();
                }

                // Add the DataLocations to the WOCELocations table
                for (DataLocation location : event.getLocations()) {
                    locPrepStmt.setLong(1, woceId);
                    Integer intVal = location.getRowNumber();
                    if ( intVal.equals(DashboardUtils.INT_MISSING_VALUE) )
                        locPrepStmt.setNull(2, java.sql.Types.INTEGER);
                    else
                        locPrepStmt.setInt(2, intVal);
                    Double dblVal = location.getLongitude();
                    if ( dblVal.equals(DashboardUtils.FP_MISSING_VALUE) )
                        locPrepStmt.setNull(3, java.sql.Types.DOUBLE);
                    else
                        locPrepStmt.setDouble(3, dblVal);
                    dblVal = location.getLatitude();
                    if ( dblVal.equals(DashboardUtils.FP_MISSING_VALUE) )
                        locPrepStmt.setNull(4, java.sql.Types.DOUBLE);
                    else
                        locPrepStmt.setDouble(4, dblVal);
                    Date dateVal = location.getDataDate();
                    if ( dateVal.equals(DashboardUtils.DATE_MISSING_VALUE) )
                        locPrepStmt.setNull(5, java.sql.Types.BIGINT);
                    else
                        locPrepStmt.setLong(5, Math.round(dateVal.getTime() / 1000.0));
                    dblVal = location.getDataValue();
                    if ( dblVal.equals(DashboardUtils.FP_MISSING_VALUE) )
                        locPrepStmt.setNull(6, java.sql.Types.DOUBLE);
                    else
                        locPrepStmt.setDouble(6, dblVal);
                    locPrepStmt.executeUpdate();
                    if ( locPrepStmt.getUpdateCount() != 1 )
                        throw new SQLException("Adding a data QC location was unsuccessful");
                }

                // Success - assign the ID
                event.setId(woceId);
            }
        } finally {
            catConn.close();
        }
    }

    /**
     * Creates a WoceEvent without any locations from the values in the current row of a ResultSet.
     *
     * @param results
     *         assign values from the current row of this ResultSet; must include columns with names woce_flag,
     *         woce_time, expocode, socat_version, data_name, username, realname, and woce_comment.
     *
     * @throws SQLException
     *         if getting values from the ResultSet throws one
     */
    private DataQCEvent createDataQCEvent(ResultSet results) throws SQLException {
        DataQCEvent woceEvent = new DataQCEvent();
        Long id = results.getLong("woce_id");
        if ( id < 1 )
            throw new SQLException("Unexpected invalid woce_id");
        woceEvent.setId(id);
        woceEvent.setFlagName(results.getString("woce_name"));
        woceEvent.setFlagValue(results.getString("woce_flag"));
        woceEvent.setFlagDate(new Date(results.getLong("woce_time") * 1000L));
        if ( results.wasNull() )
            woceEvent.setFlagDate(null);
        woceEvent.setDatasetId(results.getString("expocode"));
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
     *         assign values from the current row of this ResultSet; must include columns with names row_num, longitude,
     *         latitude, data_time, and data_value.
     *
     * @return the created DataLocation
     */
    private DataLocation createDataQCLocation(ResultSet results) throws SQLException {
        DataLocation location = new DataLocation();
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
     *         get the WOCE events for the dataset with this ID
     * @param latestFirst
     *         order with the latest first?
     *
     * @return list of data QC events for the dataset, ordered by the dates of the events (either latest first or latest
     *         last); never null but may be empty
     *
     * @throws SQLException
     *         if accessing the database or reading the results throws one
     */
    public ArrayList<DataQCEvent> getDataQCEvents(String expocode, boolean latestFirst) throws SQLException {
        ArrayList<DataQCEvent> eventsList = new ArrayList<DataQCEvent>();
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
                    eventsList.add(createDataQCEvent(results));
                }
            } finally {
                results.close();
            }
            prepStmt = catConn.prepareStatement("SELECT * FROM `" + WOCELOCATIONS_TABLE_NAME +
                    "` WHERE `woce_id` = ? ORDER BY `row_num`;");
            for (DataQCEvent event : eventsList) {
                // Directly modify the list of locations in the WOCE event
                ArrayList<DataLocation> locations = event.getLocations();
                prepStmt.setLong(1, event.getId());
                results = prepStmt.executeQuery();
                try {
                    while ( results.next() ) {
                        locations.add(createDataQCLocation(results));
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
     * Resets data QC flags for all data QC events (if any) of a dataset to the corresponding "old" data QC flag values.
     * This should be called prior to adding data QC events for an updated dataset.
     *
     * @param expocode
     *         reset the data QC events for the dataset with this ID
     *
     * @throws SQLException
     *         if modifying the data QC events in the database throws one
     */
    public void resetDataQCEvents(String expocode) throws SQLException {
        Connection catConn = makeConnection(true);
        try {
            PreparedStatement modifyWocePrepStmt = catConn.prepareStatement(
                    "UPDATE `" + WOCEEVENTS_TABLE_NAME + "` SET `woce_flag` = ? " +
                            "WHERE `expocode` = ? AND `woce_flag` = ?;");
            modifyWocePrepStmt.setString(2, expocode);

            modifyWocePrepStmt.setString(1, DashboardServerUtils.OLD_WOCE_ACCEPTABLE.toString());
            modifyWocePrepStmt.setString(3, DashboardServerUtils.WOCE_ACCEPTABLE.toString());
            modifyWocePrepStmt.executeUpdate();

            modifyWocePrepStmt.setString(1, DashboardServerUtils.OLD_WOCE_QUESTIONABLE.toString());
            modifyWocePrepStmt.setString(3, DashboardServerUtils.WOCE_QUESTIONABLE.toString());
            modifyWocePrepStmt.executeUpdate();

            modifyWocePrepStmt.setString(1, DashboardServerUtils.OLD_WOCE_BAD.toString());
            modifyWocePrepStmt.setString(3, DashboardServerUtils.WOCE_BAD.toString());
            modifyWocePrepStmt.executeUpdate();
        } finally {
            catConn.close();
        }
    }

    /**
     * Adds rename dataset QC and data QC events and appropriately renames the dataset ID for flags in the database. If
     * the dataset has never been submitted for QC, or was already renamed, (i.e., has no QC events except maybe
     * renames), returns without making any changes.
     *
     * @param oldExpocode
     *         standardized old dataset ID
     * @param newExpocode
     *         standardized new dataset ID
     * @param version
     *         version to associate with the rename events
     * @param username
     *         name of the user to associate with the rename events
     *
     * @throws SQLException
     *         if username is not a known user, or if accessing or updating the database throws one
     */
    public void renameQCFlags(String oldExpocode, String newExpocode, String version, String username)
            throws SQLException {
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
            modifyQcPrepStmt.setString(3, DatasetQCFlag.FLAG_RENAMED);
            modifyQcPrepStmt.executeUpdate();
            int updateCount = modifyQcPrepStmt.getUpdateCount();
            if ( updateCount < 0 )
                throw new SQLException("Unexpected update count from renaming QC datasetIds");
            if ( updateCount == 0 ) {
                // If no QC flags with the old expocode, cruise has never been submitted
                // or was already renamed; in either case, nothing to do.
                return;
            }

            // Add two dataset QC rename events; one for the old expocode and one for the new expocode
            // Also add a dataset QC comment so the rename is seen by QC-ers
            PreparedStatement addQcPrepStmt = catConn.prepareStatement(
                    "INSERT INTO `" + QCEVENTS_TABLE_NAME + "` (`qc_flag`, `qc_time`, " +
                            "`expocode`, `socat_version`, `region_id`, `reviewer_id`, `qc_comment`) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?), (?, ?, ?, ?, ?, ?, ?), (?, ?, ?, ?, ?, ?, ?);");
            addQcPrepStmt.setString(1, DatasetQCFlag.FLAG_RENAMED);
            addQcPrepStmt.setString(8, DatasetQCFlag.FLAG_RENAMED);
            addQcPrepStmt.setString(15, DatasetQCFlag.FLAG_COMMENT);
            addQcPrepStmt.setLong(2, nowSec);
            addQcPrepStmt.setLong(9, nowSec);
            addQcPrepStmt.setLong(16, nowSec);
            addQcPrepStmt.setString(3, oldExpocode);
            addQcPrepStmt.setString(10, newExpocode);
            addQcPrepStmt.setString(17, newExpocode);
            addQcPrepStmt.setString(4, version);
            addQcPrepStmt.setString(11, version);
            addQcPrepStmt.setString(18, version);
            addQcPrepStmt.setString(5, DashboardUtils.REGION_ID_GLOBAL);
            addQcPrepStmt.setString(12, DashboardUtils.REGION_ID_GLOBAL);
            addQcPrepStmt.setString(19, DashboardUtils.REGION_ID_GLOBAL);
            addQcPrepStmt.setInt(6, reviewerId);
            addQcPrepStmt.setInt(13, reviewerId);
            addQcPrepStmt.setInt(20, reviewerId);
            addQcPrepStmt.setString(7, renameComment);
            addQcPrepStmt.setString(14, renameComment);
            addQcPrepStmt.setString(21, renameComment);
            addQcPrepStmt.executeUpdate();

            // Update the old expocode to the new expocode in the appropriate data QC events
            PreparedStatement modifyWocePrepStmt = catConn.prepareStatement(
                    "UPDATE `" + WOCEEVENTS_TABLE_NAME + "` SET `expocode` = ? " +
                            "WHERE `expocode` = ? AND `woce_flag` <> ?;");
            modifyWocePrepStmt.setString(1, newExpocode);
            modifyWocePrepStmt.setString(2, oldExpocode);
            modifyWocePrepStmt.setString(3, DatasetQCFlag.FLAG_RENAMED);
            modifyWocePrepStmt.executeUpdate();

            // Add two rename WOCE events; one for the old expocode and one for the new expocode
            PreparedStatement addWocePrepStmt = catConn.prepareStatement("INSERT INTO `" +
                    WOCEEVENTS_TABLE_NAME + "` (`woce_name`, `woce_flag`, `woce_time`, " +
                    "`expocode`, `socat_version`, `reviewer_id`, `woce_comment`) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?), (?, ?, ?, ?, ?, ?, ?);");
            addWocePrepStmt.setString(1, SocatTypes.WOCE_CO2_WATER.getVarName());
            addWocePrepStmt.setString(8, SocatTypes.WOCE_CO2_WATER.getVarName());
            addWocePrepStmt.setString(2, DatasetQCFlag.FLAG_RENAMED);
            addWocePrepStmt.setString(9, DatasetQCFlag.FLAG_RENAMED);
            addWocePrepStmt.setLong(3, nowSec);
            addWocePrepStmt.setLong(10, nowSec);
            addWocePrepStmt.setString(4, oldExpocode);
            addWocePrepStmt.setString(11, newExpocode);
            addWocePrepStmt.setString(5, version);
            addWocePrepStmt.setString(12, version);
            addWocePrepStmt.setInt(6, reviewerId);
            addWocePrepStmt.setInt(13, reviewerId);
            addWocePrepStmt.setString(7, renameComment);
            addWocePrepStmt.setString(14, renameComment);
            addWocePrepStmt.executeUpdate();
        } finally {
            catConn.close();
        }
    }

}
