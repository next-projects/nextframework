package org.nextframework.test.types;

import org.junit.Test;
import org.nextframework.types.PhoneBrazil;

public class PhoneBrazilTest {

	@Test
	public void test1() {
		new PhoneBrazil("01230123");
	}

	@Test
	public void test12() {
		new PhoneBrazil("901230123");
	}

	@Test
	public void test2() {
		new PhoneBrazil("0001230123");
	}

	@Test
	public void test3() {
		new PhoneBrazil("00901230123");
	}

	@Test
	public void test11() {
		new PhoneBrazil("0123-0123");
	}

	@Test
	public void test21() {
		new PhoneBrazil("000123-0123");
	}

	@Test
	public void test31() {
		new PhoneBrazil("0090123-0123");
	}

	@Test
	public void test211() {
		new PhoneBrazil("(00)0123-0123");
	}

	@Test
	public void test311() {
		new PhoneBrazil("(00)90123-0123");
	}

	@Test
	public void test212() {
		new PhoneBrazil("(00) 0123-0123");
	}

	@Test
	public void test312() {
		new PhoneBrazil("(00) 90123-0123");
	}

	@Test(expected = IllegalArgumentException.class)
	public void test4() {
		new PhoneBrazil("(00) a90123-0123");
	}

	@Test(expected = IllegalArgumentException.class)
	public void test41() {
		new PhoneBrazil("(00)a90123-0123");
	}

}
