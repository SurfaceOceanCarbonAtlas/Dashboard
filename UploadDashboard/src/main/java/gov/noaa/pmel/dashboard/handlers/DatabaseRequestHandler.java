/**
 * 
 */
package gov.noaa.pmel.dashboard.handlers;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Handles database requests for reading user/reviewer information.
 * Eventually will also be used for dealing with QC and data QC events.
 * 
 * @author Karl Smith
 */
public class DatabaseRequestHandler {

	private static final String REVIEWERS_TABLE_NAME = "Reviewers";

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

}
