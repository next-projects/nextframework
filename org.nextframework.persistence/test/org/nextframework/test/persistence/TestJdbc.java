package org.nextframework.test.persistence;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;

public class TestJdbc {

	@Before
	public void setUp() throws ClassNotFoundException, SQLException {
		TestJdbcUtils.createConnection();
		createDb();
	}

	protected void createDb() throws SQLException {
	}

	protected boolean ddl(String ddl) throws SQLException, ClassNotFoundException {
		return TestJdbcUtils.prepareStatement(ddl);
	}

	@After
	public void tearDown() throws SQLException {
		TestJdbcUtils.stopConnection();
	}

}
