package org.nextframework.test.types;

import org.junit.Assert;
import org.junit.Test;
import org.nextframework.types.Cnpj;

public class CnpjTest {

	@Test
	public void testNumericCnpjRemainsValid() {
		Assert.assertTrue(Cnpj.cnpjValido("11.222.333/0001-81"));
	}

	@Test
	public void testAlphanumericCnpjWithMaskIsValid() {
		Assert.assertTrue(Cnpj.cnpjValido("GG.AVE.NJZ/0001-02"));
	}

	@Test
	public void testAlphanumericCnpjNormalizesToUpperCase() {
		Cnpj cnpj = new Cnpj("gg.ave.njz/0001-02");
		Assert.assertEquals("GGAVENJZ000102", cnpj.getValue());
		Assert.assertEquals("GG.AVE.NJZ/0001-02", cnpj.toString());
	}

	@Test
	public void testAlphanumericCnpjWithoutMaskIsValid() {
		Cnpj cnpj = new Cnpj("GGAVENJZ000102");
		Assert.assertEquals("GG.AVE.NJZ/0001-02", cnpj.toString());
	}

	@Test
	public void testLegacyLeadingZeroIsNormalized() {

		Cnpj cnpj = new Cnpj("000621311700130", false);
		Assert.assertEquals("00621311700130", cnpj.getValue());
		Assert.assertEquals("00.621.311/7001-30", cnpj.toString());

		cnpj = new Cnpj("61.186.888/0065-58", false);
		Assert.assertEquals("61186888006558", cnpj.getValue());
		Assert.assertEquals("61.186.888/0065-58", cnpj.toString());

	}

	@Test(expected = IllegalArgumentException.class)
	public void testRejectsInvalidAlphanumericDv() {
		new Cnpj("GG.AVE.NJZ/0001-03");
	}

}
