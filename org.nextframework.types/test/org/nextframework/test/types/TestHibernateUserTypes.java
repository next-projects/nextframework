package org.nextframework.test.types;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.junit.Assert;
import org.junit.Test;
import org.nextframework.types.Cep;
import org.nextframework.types.Cnpj;
import org.nextframework.types.Cpf;
import org.nextframework.types.Phone;
import org.nextframework.types.PhoneBrazil;
import org.nextframework.types.hibernate.CepUserType;
import org.nextframework.types.hibernate.CnpjUserType;
import org.nextframework.types.hibernate.CpfUserType;
import org.nextframework.types.hibernate.PhoneBrazilUserType;
import org.nextframework.types.hibernate.PhoneUserType;

import static org.mockito.Mockito.*;

public class TestHibernateUserTypes {

	// ================= CpfUserType =================

	@Test
	public void testCpfUserTypeSqlTypes() {
		CpfUserType ut = new CpfUserType();
		int[] types = ut.sqlTypes();
		Assert.assertEquals(1, types.length);
		Assert.assertEquals(Types.VARCHAR, types[0]);
	}

	@Test
	public void testCpfUserTypeReturnedClass() {
		CpfUserType ut = new CpfUserType();
		Assert.assertEquals(Cpf.class, ut.returnedClass());
	}

	@Test
	public void testCpfUserTypeNullSafeGet() throws SQLException {
		CpfUserType ut = new CpfUserType();
		ResultSet rs = mock(ResultSet.class);
		SessionImplementor session = mock(SessionImplementor.class);
		// Valid CPF: 07357279618
		when(rs.getString("cpf")).thenReturn("07357279618");
		Object result = ut.nullSafeGet(rs, new String[] { "cpf" }, session, null);
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof Cpf);
		Assert.assertEquals("07357279618", ((Cpf) result).getValue());
	}

	@Test
	public void testCpfUserTypeNullSafeGetNull() throws SQLException {
		CpfUserType ut = new CpfUserType();
		ResultSet rs = mock(ResultSet.class);
		SessionImplementor session = mock(SessionImplementor.class);
		when(rs.getString("cpf")).thenReturn(null);
		Object result = ut.nullSafeGet(rs, new String[] { "cpf" }, session, null);
		Assert.assertNull(result);
	}

	@Test
	public void testCpfUserTypeNullSafeGetEmpty() throws SQLException {
		CpfUserType ut = new CpfUserType();
		ResultSet rs = mock(ResultSet.class);
		SessionImplementor session = mock(SessionImplementor.class);
		when(rs.getString("cpf")).thenReturn("");
		Object result = ut.nullSafeGet(rs, new String[] { "cpf" }, session, null);
		Assert.assertNull(result);
	}

	@Test
	public void testCpfUserTypeNullSafeSet() throws SQLException {
		CpfUserType ut = new CpfUserType();
		PreparedStatement ps = mock(PreparedStatement.class);
		SessionImplementor session = mock(SessionImplementor.class);
		Cpf cpf = new Cpf("07357279618", false);
		ut.nullSafeSet(ps, cpf, 1, session);
		verify(ps).setString(1, "07357279618");
	}

	@Test
	public void testCpfUserTypeNullSafeSetNull() throws SQLException {
		CpfUserType ut = new CpfUserType();
		PreparedStatement ps = mock(PreparedStatement.class);
		SessionImplementor session = mock(SessionImplementor.class);
		ut.nullSafeSet(ps, null, 1, session);
		verify(ps).setNull(1, Types.VARCHAR);
	}

	@Test
	public void testCpfUserTypeDeepCopy() {
		CpfUserType ut = new CpfUserType();
		Cpf cpf = new Cpf("07357279618", false);
		Assert.assertSame(cpf, ut.deepCopy(cpf));
	}

	@Test
	public void testCpfUserTypeIsMutable() {
		CpfUserType ut = new CpfUserType();
		Assert.assertFalse(ut.isMutable());
	}

	@Test
	public void testCpfUserTypeEqualsWithNulls() {
		CpfUserType ut = new CpfUserType();
		Assert.assertTrue(ut.equals(null, null));
	}

	@Test
	public void testCpfUserTypeEqualsWithValues() {
		CpfUserType ut = new CpfUserType();
		Cpf a = new Cpf("07357279618", false);
		Cpf b = new Cpf("07357279618", false);
		Assert.assertTrue(ut.equals(a, b));
	}

	// ================= CnpjUserType =================

	@Test
	public void testCnpjUserTypeSqlTypes() {
		CnpjUserType ut = new CnpjUserType();
		int[] types = ut.sqlTypes();
		Assert.assertEquals(1, types.length);
		Assert.assertEquals(Types.VARCHAR, types[0]);
	}

	@Test
	public void testCnpjUserTypeReturnedClass() {
		CnpjUserType ut = new CnpjUserType();
		Assert.assertEquals(Cnpj.class, ut.returnedClass());
	}

	@Test
	public void testCnpjUserTypeNullSafeGet() throws SQLException {
		CnpjUserType ut = new CnpjUserType();
		ResultSet rs = mock(ResultSet.class);
		SessionImplementor session = mock(SessionImplementor.class);
		when(rs.getString("cnpj")).thenReturn("11222333000181");
		Object result = ut.nullSafeGet(rs, new String[] { "cnpj" }, session, null);
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof Cnpj);
	}

	@Test
	public void testCnpjUserTypeNullSafeGetNull() throws SQLException {
		CnpjUserType ut = new CnpjUserType();
		ResultSet rs = mock(ResultSet.class);
		SessionImplementor session = mock(SessionImplementor.class);
		when(rs.getString("cnpj")).thenReturn(null);
		Object result = ut.nullSafeGet(rs, new String[] { "cnpj" }, session, null);
		Assert.assertNull(result);
	}

	@Test
	public void testCnpjUserTypeNullSafeSet() throws SQLException {
		CnpjUserType ut = new CnpjUserType();
		PreparedStatement ps = mock(PreparedStatement.class);
		SessionImplementor session = mock(SessionImplementor.class);
		Cnpj cnpj = new Cnpj("00621311700130", false);
		ut.nullSafeSet(ps, cnpj, 1, session);
		verify(ps).setString(1, "00621311700130");
	}

	@Test
	public void testCnpjUserTypeNullSafeSetNull() throws SQLException {
		CnpjUserType ut = new CnpjUserType();
		PreparedStatement ps = mock(PreparedStatement.class);
		SessionImplementor session = mock(SessionImplementor.class);
		ut.nullSafeSet(ps, null, 1, session);
		verify(ps).setNull(1, Types.VARCHAR);
	}

	@Test
	public void testCnpjUserTypeDeepCopy() {
		CnpjUserType ut = new CnpjUserType();
		Cnpj cnpj = new Cnpj("00621311700130", false);
		Assert.assertSame(cnpj, ut.deepCopy(cnpj));
	}

	@Test
	public void testCnpjUserTypeIsMutable() {
		CnpjUserType ut = new CnpjUserType();
		Assert.assertFalse(ut.isMutable());
	}

	// ================= CepUserType =================

	@Test
	public void testCepUserTypeSqlTypes() {
		CepUserType ut = new CepUserType();
		int[] types = ut.sqlTypes();
		Assert.assertEquals(1, types.length);
		Assert.assertEquals(Types.VARCHAR, types[0]);
	}

	@Test
	public void testCepUserTypeReturnedClass() {
		CepUserType ut = new CepUserType();
		Assert.assertEquals(Cep.class, ut.returnedClass());
	}

	@Test
	public void testCepUserTypeNullSafeGet() throws SQLException {
		CepUserType ut = new CepUserType();
		ResultSet rs = mock(ResultSet.class);
		SessionImplementor session = mock(SessionImplementor.class);
		when(rs.getString("cep")).thenReturn("01310100");
		Object result = ut.nullSafeGet(rs, new String[] { "cep" }, session, null);
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof Cep);
		Assert.assertEquals("01310100", ((Cep) result).getValue());
	}

	@Test
	public void testCepUserTypeNullSafeGetNull() throws SQLException {
		CepUserType ut = new CepUserType();
		ResultSet rs = mock(ResultSet.class);
		SessionImplementor session = mock(SessionImplementor.class);
		when(rs.getString("cep")).thenReturn(null);
		Object result = ut.nullSafeGet(rs, new String[] { "cep" }, session, null);
		Assert.assertNull(result);
	}

	@Test
	public void testCepUserTypeNullSafeSet() throws SQLException {
		CepUserType ut = new CepUserType();
		PreparedStatement ps = mock(PreparedStatement.class);
		SessionImplementor session = mock(SessionImplementor.class);
		Cep cep = new Cep("01310100");
		ut.nullSafeSet(ps, cep, 1, session);
		verify(ps).setString(1, "01310100");
	}

	@Test
	public void testCepUserTypeNullSafeSetNull() throws SQLException {
		CepUserType ut = new CepUserType();
		PreparedStatement ps = mock(PreparedStatement.class);
		SessionImplementor session = mock(SessionImplementor.class);
		ut.nullSafeSet(ps, null, 1, session);
		verify(ps).setNull(1, Types.VARCHAR);
	}

	@Test
	public void testCepUserTypeDeepCopy() {
		CepUserType ut = new CepUserType();
		Cep cep = new Cep("01310100");
		Assert.assertSame(cep, ut.deepCopy(cep));
	}

	@Test
	public void testCepUserTypeIsMutable() {
		CepUserType ut = new CepUserType();
		Assert.assertFalse(ut.isMutable());
	}

	@Test
	public void testCepUserTypeEqualsWithNulls() {
		CepUserType ut = new CepUserType();
		Assert.assertTrue(ut.equals(null, null));
	}

	@Test
	public void testCepUserTypeEqualsWithValues() {
		CepUserType ut = new CepUserType();
		Cep a = new Cep("01310100");
		Cep b = new Cep("01310100");
		Assert.assertTrue(ut.equals(a, b));
	}

	@Test
	public void testCepUserTypeEqualsOneNull() {
		CepUserType ut = new CepUserType();
		Cep a = new Cep("01310100");
		Assert.assertFalse(ut.equals(a, null));
		Assert.assertFalse(ut.equals(null, a));
	}

	// ================= PhoneUserType =================

	@Test
	public void testPhoneUserTypeSqlTypes() {
		PhoneUserType ut = new PhoneUserType();
		int[] types = ut.sqlTypes();
		Assert.assertEquals(1, types.length);
		Assert.assertEquals(Types.VARCHAR, types[0]);
	}

	@Test
	public void testPhoneUserTypeReturnedClass() {
		PhoneUserType ut = new PhoneUserType();
		Assert.assertEquals(Phone.class, ut.returnedClass());
	}

	@Test
	public void testPhoneUserTypeNullSafeGet() throws SQLException {
		PhoneUserType ut = new PhoneUserType();
		ResultSet rs = mock(ResultSet.class);
		SessionImplementor session = mock(SessionImplementor.class);
		when(rs.getString("phone")).thenReturn("3198980909");
		Object result = ut.nullSafeGet(rs, new String[] { "phone" }, session, null);
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof Phone);
	}

	@Test
	public void testPhoneUserTypeNullSafeGetNull() throws SQLException {
		PhoneUserType ut = new PhoneUserType();
		ResultSet rs = mock(ResultSet.class);
		SessionImplementor session = mock(SessionImplementor.class);
		when(rs.getString("phone")).thenReturn(null);
		Object result = ut.nullSafeGet(rs, new String[] { "phone" }, session, null);
		Assert.assertNull(result);
	}

	@Test
	public void testPhoneUserTypeNullSafeSet() throws SQLException {
		PhoneUserType ut = new PhoneUserType();
		PreparedStatement ps = mock(PreparedStatement.class);
		SessionImplementor session = mock(SessionImplementor.class);
		Phone phone = new Phone("3198980909");
		ut.nullSafeSet(ps, phone, 1, session);
		verify(ps).setString(1, "3198980909");
	}

	@Test
	public void testPhoneUserTypeNullSafeSetNull() throws SQLException {
		PhoneUserType ut = new PhoneUserType();
		PreparedStatement ps = mock(PreparedStatement.class);
		SessionImplementor session = mock(SessionImplementor.class);
		ut.nullSafeSet(ps, null, 1, session);
		verify(ps).setNull(1, Types.VARCHAR);
	}

	@Test
	public void testPhoneUserTypeDeepCopy() {
		PhoneUserType ut = new PhoneUserType();
		Phone phone = new Phone("3198980909");
		Assert.assertSame(phone, ut.deepCopy(phone));
	}

	@Test
	public void testPhoneUserTypeIsMutable() {
		PhoneUserType ut = new PhoneUserType();
		Assert.assertFalse(ut.isMutable());
	}

	// ================= PhoneBrazilUserType =================

	@Test
	public void testPhoneBrazilUserTypeSqlTypes() {
		PhoneBrazilUserType ut = new PhoneBrazilUserType();
		int[] types = ut.sqlTypes();
		Assert.assertEquals(1, types.length);
		Assert.assertEquals(Types.VARCHAR, types[0]);
	}

	@Test
	public void testPhoneBrazilUserTypeReturnedClass() {
		PhoneBrazilUserType ut = new PhoneBrazilUserType();
		Assert.assertEquals(PhoneBrazil.class, ut.returnedClass());
	}

	@Test
	public void testPhoneBrazilUserTypeNullSafeGet() throws SQLException {
		PhoneBrazilUserType ut = new PhoneBrazilUserType();
		ResultSet rs = mock(ResultSet.class);
		SessionImplementor session = mock(SessionImplementor.class);
		when(rs.getString("phone")).thenReturn("3198980909");
		Object result = ut.nullSafeGet(rs, new String[] { "phone" }, session, null);
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof PhoneBrazil);
	}

	@Test
	public void testPhoneBrazilUserTypeNullSafeGetNull() throws SQLException {
		PhoneBrazilUserType ut = new PhoneBrazilUserType();
		ResultSet rs = mock(ResultSet.class);
		SessionImplementor session = mock(SessionImplementor.class);
		when(rs.getString("phone")).thenReturn(null);
		Object result = ut.nullSafeGet(rs, new String[] { "phone" }, session, null);
		Assert.assertNull(result);
	}

	@Test
	public void testPhoneBrazilUserTypeNullSafeSet() throws SQLException {
		PhoneBrazilUserType ut = new PhoneBrazilUserType();
		PreparedStatement ps = mock(PreparedStatement.class);
		SessionImplementor session = mock(SessionImplementor.class);
		PhoneBrazil phone = new PhoneBrazil("3198980909");
		ut.nullSafeSet(ps, phone, 1, session);
		verify(ps).setString(1, "3198980909");
	}

	@Test
	public void testPhoneBrazilUserTypeNullSafeSetNull() throws SQLException {
		PhoneBrazilUserType ut = new PhoneBrazilUserType();
		PreparedStatement ps = mock(PreparedStatement.class);
		SessionImplementor session = mock(SessionImplementor.class);
		ut.nullSafeSet(ps, null, 1, session);
		verify(ps).setNull(1, Types.VARCHAR);
	}

	@Test
	public void testPhoneBrazilUserTypeDeepCopyNull() {
		PhoneBrazilUserType ut = new PhoneBrazilUserType();
		Object result = ut.deepCopy(null);
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof PhoneBrazil);
	}

	@Test
	public void testPhoneBrazilUserTypeDeepCopyValue() {
		PhoneBrazilUserType ut = new PhoneBrazilUserType();
		PhoneBrazil phone = new PhoneBrazil("3198980909");
		Assert.assertSame(phone, ut.deepCopy(phone));
	}

	@Test
	public void testPhoneBrazilUserTypeIsMutable() {
		PhoneBrazilUserType ut = new PhoneBrazilUserType();
		Assert.assertFalse(ut.isMutable());
	}

	@Test
	public void testPhoneBrazilUserTypeEqualsWithNulls() {
		PhoneBrazilUserType ut = new PhoneBrazilUserType();
		Assert.assertTrue(ut.equals(null, null));
	}

	@Test
	public void testPhoneBrazilUserTypeEqualsWithValues() {
		PhoneBrazilUserType ut = new PhoneBrazilUserType();
		PhoneBrazil a = new PhoneBrazil("3198980909");
		PhoneBrazil b = new PhoneBrazil("3198980909");
		Assert.assertTrue(ut.equals(a, b));
	}

}
