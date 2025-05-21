package org.nextframework.test.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.hsqldb.Server;

class TestJdbcUtils {

	private static Server hsqlServer;
	private static Connection connection;

	public synchronized static void createConnection() throws ClassNotFoundException, SQLException {

		if (connection != null) {
			return;
		}

		// Getting a connection to the newly started database
		Class.forName("org.hsqldb.jdbcDriver");
		// Default user of the HSQLDB is 'sa'
		// with an empty password
		connection = DriverManager.getConnection("jdbc:hsqldb:mem:memdb", "sa", "");

	}

	public synchronized static void stopConnection() throws SQLException {
		if (connection != null) {
			connection.close();
			connection = null;
		}
	}

	public static boolean prepareStatement(String ddl) throws ClassNotFoundException, SQLException {
		createConnection();
		return connection.prepareStatement(ddl).execute();
	}

	public synchronized static void startServer() {

		if (hsqlServer != null) {
			return;
		}

		// 'Server' is a class of HSQLDB representing
		// the database server
		hsqlServer = new Server();

		// HSQLDB prints out a lot of informations when
		// starting and closing, which we don't need now.
		// Normally you should point the setLogWriter
		// to some Writer object that could store the logs.
		hsqlServer.setLogWriter(null);
		hsqlServer.setSilent(true);

		// The actual database will be named 'xdb' and its
		// settings and data will be stored in files
		// testdb.properties and testdb.script
		hsqlServer.setDatabaseName(0, "xdb");
		hsqlServer.setDatabasePath(0, "file:db/testdb");

		// Start the database!
		hsqlServer.start();

	}

	public synchronized static void stopServer() {
		// Closing the server
		if (hsqlServer != null) {
			hsqlServer.stop();
			hsqlServer = null;
		}
	}

}
