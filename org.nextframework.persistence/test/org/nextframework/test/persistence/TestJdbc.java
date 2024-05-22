package org.nextframework.test.persistence;

import static org.nextframework.test.persistence.TestJdbcUtils.connection;
import static org.nextframework.test.persistence.TestJdbcUtils.createConnection;
import static org.nextframework.test.persistence.TestJdbcUtils.stopConnection;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;

public class TestJdbc {

	@Before
	public void setUp() throws ClassNotFoundException, SQLException {
		createConnection();
		createDb();
	}

	protected void createDb() throws SQLException {
	}

	protected boolean ddl(String ddl) throws SQLException, ClassNotFoundException {
		createConnection();
		return connection.prepareStatement(ddl).execute();
	}

	@After
	public void tearDown() throws SQLException {
		stopConnection();
	}

}
