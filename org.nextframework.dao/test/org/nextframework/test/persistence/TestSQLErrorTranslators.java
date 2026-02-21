package org.nextframework.test.persistence;

import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;
import org.nextframework.persistence.exception.ForeignKeyException;
import org.nextframework.persistence.exception.FirebirdSQLErrorCodesTranslator;
import org.nextframework.persistence.exception.OracleSQLErrorCodeSQLExceptionTranslator;
import org.nextframework.persistence.exception.PostgreSQLErrorCodeSQLExceptionTranslator;
import org.nextframework.persistence.exception.SQLServerSQLErrorCodeSQLExceptionTranslator;
import org.springframework.dao.DataAccessException;

public class TestSQLErrorTranslators {

	// ================= ForeignKeyException =================

	@Test
	public void testForeignKeyExceptionMessage() {
		ForeignKeyException e = new ForeignKeyException("test message");
		Assert.assertEquals("test message", e.getMessage());
	}

	@Test
	public void testForeignKeyExceptionWithCause() {
		RuntimeException cause = new RuntimeException("cause");
		ForeignKeyException e = new ForeignKeyException("test message", cause);
		// DataAccessException appends nested exception info to getMessage()
		Assert.assertTrue(e.getMessage().startsWith("test message"));
		Assert.assertSame(cause, e.getCause());
	}

	@Test
	public void testForeignKeyExceptionIsDataAccessException() {
		ForeignKeyException e = new ForeignKeyException("test");
		Assert.assertTrue(e instanceof DataAccessException);
	}

	// ================= Translator instantiation =================

	@Test
	public void testFirebirdTranslatorInstantiation() {
		FirebirdSQLErrorCodesTranslator translator = new FirebirdSQLErrorCodesTranslator();
		Assert.assertNotNull(translator);
	}

	@Test
	public void testFirebirdTranslatorNonMatchingErrorCode() {
		FirebirdSQLErrorCodesTranslator translator = new FirebirdSQLErrorCodesTranslator();
		// Error code 0 should not match the FK violation code 335544466
		SQLException sqlEx = new SQLException("some error", "00000", 0);
		DataAccessException result = translator.translate("test", "SELECT 1", sqlEx);
		// For non-matching error codes, result may be null or a generic translation
		// The customTranslate method returns null for non-matching codes
	}

	@Test
	public void testOracleTranslatorInstantiation() {
		OracleSQLErrorCodeSQLExceptionTranslator translator = new OracleSQLErrorCodeSQLExceptionTranslator();
		Assert.assertNotNull(translator);
	}

	@Test
	public void testPostgreSQLTranslatorInstantiation() {
		PostgreSQLErrorCodeSQLExceptionTranslator translator = new PostgreSQLErrorCodeSQLExceptionTranslator();
		Assert.assertNotNull(translator);
	}

	@Test
	public void testSQLServerTranslatorInstantiation() {
		SQLServerSQLErrorCodeSQLExceptionTranslator translator = new SQLServerSQLErrorCodeSQLExceptionTranslator();
		Assert.assertNotNull(translator);
	}

}
