package org.nextframework.test.controller;

import java.sql.Time;
import java.text.SimpleDateFormat;

import org.junit.Assert;
import org.junit.Test;
import org.nextframework.bean.editors.CepPropertyEditor;
import org.nextframework.bean.editors.CnpjPropertyEditor;
import org.nextframework.bean.editors.CpfPropertyEditor;
import org.nextframework.bean.editors.CustomSqlDateEditor;
import org.nextframework.bean.editors.MoneyPropertyEditor;
import org.nextframework.bean.editors.PhoneBrazilPropertyEditor;
import org.nextframework.bean.editors.PhonePropertyEditor;
import org.nextframework.bean.editors.SimpleTimePropertyEditor;
import org.nextframework.bean.editors.TimePropertyEditor;
import org.nextframework.types.Cep;
import org.nextframework.types.Cnpj;
import org.nextframework.types.Cpf;
import org.nextframework.types.Money;
import org.nextframework.types.Phone;
import org.nextframework.types.PhoneBrazil;
import org.nextframework.types.SimpleTime;

public class TestPropertyEditors {

	// ================= CpfPropertyEditor =================

	@Test
	public void testCpfSetAsTextValid() {
		CpfPropertyEditor editor = new CpfPropertyEditor();
		editor.setAsText("07357279618");
		Assert.assertNotNull(editor.getValue());
		Assert.assertTrue(editor.getValue() instanceof Cpf);
	}

	@Test
	public void testCpfSetAsTextFormatted() {
		CpfPropertyEditor editor = new CpfPropertyEditor();
		editor.setAsText("073.572.796-18");
		Assert.assertNotNull(editor.getValue());
		Assert.assertTrue(editor.getValue() instanceof Cpf);
	}

	@Test
	public void testCpfSetAsTextEmpty() {
		CpfPropertyEditor editor = new CpfPropertyEditor();
		editor.setAsText("");
		Assert.assertNull(editor.getValue());
	}

	@Test
	public void testCpfSetAsTextNull() {
		CpfPropertyEditor editor = new CpfPropertyEditor();
		editor.setAsText(null);
		Assert.assertNull(editor.getValue());
	}

	@Test
	public void testCpfGetAsText() {
		CpfPropertyEditor editor = new CpfPropertyEditor();
		editor.setAsText("07357279618");
		String text = editor.getAsText();
		Assert.assertNotNull(text);
		Assert.assertEquals("073.572.796-18", text);
	}

	@Test
	public void testCpfGetAsTextWhenNull() {
		CpfPropertyEditor editor = new CpfPropertyEditor();
		Assert.assertEquals("", editor.getAsText());
	}

	// ================= CnpjPropertyEditor =================

	@Test
	public void testCnpjSetAsTextValid() {
		CnpjPropertyEditor editor = new CnpjPropertyEditor();
		editor.setAsText("11222333000181");
		Assert.assertNotNull(editor.getValue());
		Assert.assertTrue(editor.getValue() instanceof Cnpj);
	}

	@Test
	public void testCnpjSetAsTextFormatted() {
		CnpjPropertyEditor editor = new CnpjPropertyEditor();
		editor.setAsText("11.222.333/0001-81");
		Assert.assertNotNull(editor.getValue());
	}

	@Test
	public void testCnpjSetAsTextEmpty() {
		CnpjPropertyEditor editor = new CnpjPropertyEditor();
		editor.setAsText("");
		Assert.assertNull(editor.getValue());
	}

	@Test
	public void testCnpjGetAsText() {
		// Use a valid CNPJ
		CnpjPropertyEditor editor = new CnpjPropertyEditor();
		editor.setAsText("11222333000181");
		String text = editor.getAsText();
		Assert.assertNotNull(text);
		Assert.assertEquals("11.222.333/0001-81", text);
	}

	@Test
	public void testCnpjGetAsTextWhenNull() {
		CnpjPropertyEditor editor = new CnpjPropertyEditor();
		Assert.assertEquals("", editor.getAsText());
	}

	// ================= CepPropertyEditor =================

	@Test
	public void testCepSetAsTextValid() {
		CepPropertyEditor editor = new CepPropertyEditor();
		editor.setAsText("01310100");
		Assert.assertNotNull(editor.getValue());
		Assert.assertTrue(editor.getValue() instanceof Cep);
	}

	@Test
	public void testCepSetAsTextWithDash() {
		CepPropertyEditor editor = new CepPropertyEditor();
		editor.setAsText("01310-100");
		Assert.assertNotNull(editor.getValue());
		Assert.assertTrue(editor.getValue() instanceof Cep);
	}

	@Test
	public void testCepSetAsTextEmpty() {
		CepPropertyEditor editor = new CepPropertyEditor();
		editor.setAsText("");
		Assert.assertNull(editor.getValue());
	}

	@Test
	public void testCepSetAsTextNull() {
		CepPropertyEditor editor = new CepPropertyEditor();
		editor.setAsText(null);
		Assert.assertNull(editor.getValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCepSetAsTextInvalidLength() {
		CepPropertyEditor editor = new CepPropertyEditor();
		editor.setAsText("1234");
	}

	@Test
	public void testCepGetAsText() {
		CepPropertyEditor editor = new CepPropertyEditor();
		editor.setAsText("01310100");
		String text = editor.getAsText();
		Assert.assertEquals("01310-100", text);
	}

	@Test
	public void testCepGetAsTextWhenNull() {
		CepPropertyEditor editor = new CepPropertyEditor();
		Assert.assertEquals("", editor.getAsText());
	}

	// ================= MoneyPropertyEditor =================

	@Test
	public void testMoneySetAsTextSimple() {
		MoneyPropertyEditor editor = new MoneyPropertyEditor();
		editor.setAsText("100.50");
		Assert.assertNotNull(editor.getValue());
		Assert.assertTrue(editor.getValue() instanceof Money);
	}

	@Test
	public void testMoneySetAsTextBrazilianFormat() {
		MoneyPropertyEditor editor = new MoneyPropertyEditor();
		// Brazilian format: 1.234,56 -> replace . with empty, , with . -> 1234.56
		editor.setAsText("1.234,56");
		Assert.assertNotNull(editor.getValue());
		Money money = (Money) editor.getValue();
		Assert.assertEquals("1234.56", money.getValue().toPlainString());
	}

	@Test
	public void testMoneySetAsTextEmpty() {
		MoneyPropertyEditor editor = new MoneyPropertyEditor();
		editor.setAsText("");
		Assert.assertNull(editor.getValue());
	}

	@Test
	public void testMoneySetAsTextNull() {
		MoneyPropertyEditor editor = new MoneyPropertyEditor();
		editor.setAsText(null);
		Assert.assertNull(editor.getValue());
	}

	@Test
	public void testMoneyGetAsText() {
		MoneyPropertyEditor editor = new MoneyPropertyEditor();
		editor.setAsText("1234,56");
		String text = editor.getAsText();
		Assert.assertNotNull(text);
		// DecimalFormat "#,##0.00" formats as "1,234.56" in English locale
		Assert.assertEquals("1,234.56", text);
	}

	@Test
	public void testMoneyGetAsTextWhenNull() {
		MoneyPropertyEditor editor = new MoneyPropertyEditor();
		Assert.assertNull(editor.getAsText());
	}

	// ================= PhonePropertyEditor =================

	@Test
	public void testPhoneSetAsTextValid() {
		PhonePropertyEditor editor = new PhonePropertyEditor();
		editor.setAsText("(31) 9898-0909");
		Assert.assertNotNull(editor.getValue());
		Assert.assertTrue(editor.getValue() instanceof Phone);
	}

	@Test
	public void testPhoneSetAsTextEmpty() {
		PhonePropertyEditor editor = new PhonePropertyEditor();
		editor.setAsText("");
		Assert.assertNull(editor.getValue());
	}

	@Test
	public void testPhoneGetAsText() {
		PhonePropertyEditor editor = new PhonePropertyEditor();
		editor.setAsText("(31) 9898-0909");
		String text = editor.getAsText();
		Assert.assertNotNull(text);
		Assert.assertFalse(text.isEmpty());
	}

	@Test
	public void testPhoneGetAsTextWhenNull() {
		PhonePropertyEditor editor = new PhonePropertyEditor();
		Assert.assertEquals("", editor.getAsText());
	}

	// ================= PhoneBrazilPropertyEditor =================

	@Test
	public void testPhoneBrazilSetAsTextValid() {
		PhoneBrazilPropertyEditor editor = new PhoneBrazilPropertyEditor();
		editor.setAsText("(31) 98765-4321");
		Assert.assertNotNull(editor.getValue());
		Assert.assertTrue(editor.getValue() instanceof PhoneBrazil);
	}

	@Test
	public void testPhoneBrazilSetAsTextSimple() {
		PhoneBrazilPropertyEditor editor = new PhoneBrazilPropertyEditor();
		editor.setAsText("01230123");
		Assert.assertNotNull(editor.getValue());
	}

	@Test
	public void testPhoneBrazilSetAsTextEmpty() {
		PhoneBrazilPropertyEditor editor = new PhoneBrazilPropertyEditor();
		editor.setAsText("");
		Assert.assertNull(editor.getValue());
	}

	@Test
	public void testPhoneBrazilGetAsText() {
		PhoneBrazilPropertyEditor editor = new PhoneBrazilPropertyEditor();
		editor.setAsText("(31) 98765-4321");
		String text = editor.getAsText();
		Assert.assertNotNull(text);
		Assert.assertFalse(text.isEmpty());
	}

	@Test
	public void testPhoneBrazilGetAsTextWhenNull() {
		PhoneBrazilPropertyEditor editor = new PhoneBrazilPropertyEditor();
		Assert.assertEquals("", editor.getAsText());
	}

	// ================= TimePropertyEditor =================

	@Test
	public void testTimeSetAsTextValid() {
		TimePropertyEditor editor = new TimePropertyEditor();
		editor.setAsText("14:30");
		Assert.assertNotNull(editor.getValue());
		Assert.assertTrue(editor.getValue() instanceof Time);
	}

	@Test
	public void testTimeSetAsTextEmpty() {
		TimePropertyEditor editor = new TimePropertyEditor();
		editor.setAsText("");
		Assert.assertNull(editor.getValue());
	}

	@Test
	public void testTimeSetAsTextNull() {
		TimePropertyEditor editor = new TimePropertyEditor();
		editor.setAsText(null);
		Assert.assertNull(editor.getValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testTimeSetAsTextInvalid() {
		TimePropertyEditor editor = new TimePropertyEditor();
		editor.setAsText("invalid");
	}

	@Test
	public void testTimeGetAsText() {
		TimePropertyEditor editor = new TimePropertyEditor();
		editor.setAsText("14:30");
		String text = editor.getAsText();
		Assert.assertEquals("14:30", text);
	}

	@Test
	public void testTimeGetAsTextWhenNull() {
		TimePropertyEditor editor = new TimePropertyEditor();
		Assert.assertEquals("", editor.getAsText());
	}

	@Test
	public void testTimeRoundTrip() {
		TimePropertyEditor editor = new TimePropertyEditor();
		editor.setAsText("09:15");
		String text = editor.getAsText();
		Assert.assertEquals("09:15", text);
	}

	@Test
	public void testTimeCustomPattern() {
		TimePropertyEditor editor = new TimePropertyEditor("HH:mm:ss");
		editor.setAsText("14:30:45");
		String text = editor.getAsText();
		Assert.assertEquals("14:30:45", text);
	}

	// ================= SimpleTimePropertyEditor =================

	@Test
	public void testSimpleTimeSetAsTextValid() {
		SimpleTimePropertyEditor editor = new SimpleTimePropertyEditor();
		editor.setAsText("14:30");
		Assert.assertNotNull(editor.getValue());
		Assert.assertTrue(editor.getValue() instanceof SimpleTime);
	}

	@Test
	public void testSimpleTimeSetAsTextEmpty() {
		SimpleTimePropertyEditor editor = new SimpleTimePropertyEditor();
		editor.setAsText("");
		Assert.assertNull(editor.getValue());
	}

	@Test
	public void testSimpleTimeSetAsTextNull() {
		SimpleTimePropertyEditor editor = new SimpleTimePropertyEditor();
		editor.setAsText(null);
		Assert.assertNull(editor.getValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSimpleTimeSetAsTextInvalid() {
		SimpleTimePropertyEditor editor = new SimpleTimePropertyEditor();
		editor.setAsText("not-a-time");
	}

	@Test
	public void testSimpleTimeGetAsText() {
		SimpleTimePropertyEditor editor = new SimpleTimePropertyEditor();
		editor.setAsText("14:30");
		String text = editor.getAsText();
		Assert.assertEquals("14:30", text);
	}

	@Test
	public void testSimpleTimeGetAsTextWhenNull() {
		SimpleTimePropertyEditor editor = new SimpleTimePropertyEditor();
		Assert.assertEquals("", editor.getAsText());
	}

	@Test
	public void testSimpleTimeRoundTrip() {
		SimpleTimePropertyEditor editor = new SimpleTimePropertyEditor();
		editor.setAsText("09:15");
		String text = editor.getAsText();
		Assert.assertEquals("09:15", text);
	}

	// ================= CustomSqlDateEditor =================

	@Test
	public void testCustomSqlDateSetAsText() {
		CustomSqlDateEditor editor = new CustomSqlDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true);
		editor.setAsText("25/12/2023");
		Assert.assertNotNull(editor.getValue());
		Assert.assertTrue(editor.getValue() instanceof java.sql.Date);
	}

	@Test
	public void testCustomSqlDateSetAsTextEmpty() {
		CustomSqlDateEditor editor = new CustomSqlDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true);
		editor.setAsText("");
		Assert.assertNull(editor.getValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCustomSqlDateSetAsTextEmptyNotAllowed() {
		CustomSqlDateEditor editor = new CustomSqlDateEditor(new SimpleDateFormat("dd/MM/yyyy"), false);
		editor.setAsText("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCustomSqlDateSetAsTextInvalid() {
		CustomSqlDateEditor editor = new CustomSqlDateEditor(new SimpleDateFormat("dd/MM/yyyy"), false);
		editor.setAsText("not-a-date");
	}

	@Test
	public void testCustomSqlDateGetAsText() {
		CustomSqlDateEditor editor = new CustomSqlDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true);
		editor.setAsText("25/12/2023");
		String text = editor.getAsText();
		Assert.assertEquals("25/12/2023", text);
	}

	@Test
	public void testCustomSqlDateGetAsTextWhenNull() {
		CustomSqlDateEditor editor = new CustomSqlDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true);
		Assert.assertEquals("", editor.getAsText());
	}

	@Test
	public void testCustomSqlDateRoundTrip() {
		CustomSqlDateEditor editor = new CustomSqlDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true);
		editor.setAsText("01/06/2024");
		String text = editor.getAsText();
		Assert.assertEquals("01/06/2024", text);
		Assert.assertTrue(editor.getValue() instanceof java.sql.Date);
	}

}
