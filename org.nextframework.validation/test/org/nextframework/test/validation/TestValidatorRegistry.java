package org.nextframework.test.validation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextframework.types.Cep;
import org.nextframework.types.Cnpj;
import org.nextframework.types.Cpf;
import org.nextframework.types.Phone;
import org.nextframework.types.PhoneBrazil;
import org.nextframework.types.SimpleTime;
import org.nextframework.validation.PropertyValidator;
import org.nextframework.validation.ValidatorRegistryImpl;
import org.nextframework.validation.annotation.Email;
import org.nextframework.validation.annotation.MaxLength;
import org.nextframework.validation.annotation.MaxValue;
import org.nextframework.validation.annotation.MinLength;
import org.nextframework.validation.annotation.MinValue;
import org.nextframework.validation.annotation.Required;

public class TestValidatorRegistry {

	private ValidatorRegistryImpl registry;

	@Before
	public void setUp() {
		registry = new ValidatorRegistryImpl();
	}

	// --- Annotation-based validator lookup ---

	@Test
	public void testRequiredValidatorRegistered() {
		PropertyValidator v = registry.getPropertyValidator(Required.class);
		Assert.assertNotNull("Required validator should be registered", v);
		Assert.assertEquals("required", v.getValidationName());
	}

	@Test
	public void testEmailValidatorRegistered() {
		PropertyValidator v = registry.getPropertyValidator(Email.class);
		Assert.assertNotNull("Email validator should be registered", v);
		Assert.assertEquals("email", v.getValidationName());
	}

	@Test
	public void testMaxLengthValidatorRegistered() {
		PropertyValidator v = registry.getPropertyValidator(MaxLength.class);
		Assert.assertNotNull("MaxLength validator should be registered", v);
		Assert.assertEquals("maxlength", v.getValidationName());
	}

	@Test
	public void testMinLengthValidatorRegistered() {
		PropertyValidator v = registry.getPropertyValidator(MinLength.class);
		Assert.assertNotNull("MinLength validator should be registered", v);
		Assert.assertEquals("minlength", v.getValidationName());
	}

	@Test
	public void testMaxValueValidatorRegistered() {
		PropertyValidator v = registry.getPropertyValidator(MaxValue.class);
		Assert.assertNotNull("MaxValue validator should be registered", v);
		Assert.assertEquals("floatMaxValue", v.getValidationName());
	}

	@Test
	public void testMinValueValidatorRegistered() {
		PropertyValidator v = registry.getPropertyValidator(MinValue.class);
		Assert.assertNotNull("MinValue validator should be registered", v);
		Assert.assertEquals("floatMinValue", v.getValidationName());
	}

	// --- Type-based validator lookup ---

	@Test
	public void testIntegerTypeValidatorRegistered() {
		PropertyValidator v = registry.getTypeValidator(Integer.class);
		Assert.assertNotNull("Integer type validator should be registered", v);
	}

	@Test
	public void testLongTypeValidatorRegistered() {
		PropertyValidator v = registry.getTypeValidator(Long.class);
		Assert.assertNotNull("Long type validator should be registered", v);
	}

	@Test
	public void testFloatTypeValidatorRegistered() {
		PropertyValidator v = registry.getTypeValidator(Float.class);
		Assert.assertNotNull("Float type validator should be registered", v);
	}

	@Test
	public void testDoubleTypeValidatorRegistered() {
		PropertyValidator v = registry.getTypeValidator(Double.class);
		Assert.assertNotNull("Double type validator should be registered", v);
	}

	@Test
	public void testDateTypeValidatorRegistered() {
		PropertyValidator v = registry.getTypeValidator(java.sql.Date.class);
		Assert.assertNotNull("Date type validator should be registered", v);
	}

	@Test
	public void testUtilDateTypeValidatorRegistered() {
		PropertyValidator v = registry.getTypeValidator(java.util.Date.class);
		Assert.assertNotNull("java.util.Date type validator should be registered", v);
	}

	@Test
	public void testTimeTypeValidatorRegistered() {
		PropertyValidator v = registry.getTypeValidator(java.sql.Time.class);
		Assert.assertNotNull("Time type validator should be registered", v);
	}

	@Test
	public void testSimpleTimeTypeValidatorRegistered() {
		PropertyValidator v = registry.getTypeValidator(SimpleTime.class);
		Assert.assertNotNull("SimpleTime type validator should be registered", v);
	}

	@Test
	public void testCpfTypeValidatorRegistered() {
		PropertyValidator v = registry.getTypeValidator(Cpf.class);
		Assert.assertNotNull("Cpf type validator should be registered", v);
	}

	@Test
	public void testCnpjTypeValidatorRegistered() {
		PropertyValidator v = registry.getTypeValidator(Cnpj.class);
		Assert.assertNotNull("Cnpj type validator should be registered", v);
	}

	@Test
	public void testCepTypeValidatorRegistered() {
		PropertyValidator v = registry.getTypeValidator(Cep.class);
		Assert.assertNotNull("Cep type validator should be registered", v);
	}

	@Test
	public void testPhoneTypeValidatorRegistered() {
		PropertyValidator v = registry.getTypeValidator(Phone.class);
		Assert.assertNotNull("Phone type validator should be registered", v);
	}

	@Test
	public void testPhoneBrazilTypeValidatorRegistered() {
		PropertyValidator v = registry.getTypeValidator(PhoneBrazil.class);
		Assert.assertNotNull("PhoneBrazil type validator should be registered", v);
	}

	// --- String-based type lookup ---

	@Test
	public void testStringTypeIntegerValidatorRegistered() {
		PropertyValidator v = registry.getTypeValidator("integer");
		Assert.assertNotNull("String type 'integer' validator should be registered", v);
	}

	@Test
	public void testStringTypeLongValidatorRegistered() {
		PropertyValidator v = registry.getTypeValidator("long");
		Assert.assertNotNull("String type 'long' validator should be registered", v);
	}

	@Test
	public void testStringTypeFloatValidatorRegistered() {
		PropertyValidator v = registry.getTypeValidator("float");
		Assert.assertNotNull("String type 'float' validator should be registered", v);
	}

	@Test
	public void testStringTypeDateValidatorRegistered() {
		PropertyValidator v = registry.getTypeValidator("date");
		Assert.assertNotNull("String type 'date' validator should be registered", v);
	}

	// --- Unknown type returns null ---

	@Test
	public void testUnregisteredAnnotationReturnsNull() {
		PropertyValidator v = registry.getPropertyValidator(Override.class);
		Assert.assertNull("Unregistered annotation should return null", v);
	}

	@Test
	public void testUnregisteredTypeReturnsNull() {
		PropertyValidator v = registry.getTypeValidator(String.class);
		Assert.assertNull("Unregistered type should return null", v);
	}

	@Test
	public void testUnregisteredStringTypeReturnsNull() {
		PropertyValidator v = registry.getTypeValidator("nonexistent");
		Assert.assertNull("Unregistered string type should return null", v);
	}

	// --- Extractor ---

	@Test
	public void testExtractorNotNull() {
		Assert.assertNotNull("Extractor should not be null", registry.getExtractor());
	}

	// --- Clear ---

	@Test
	public void testClearRemovesAllValidators() {
		registry.clear();
		Assert.assertNull(registry.getPropertyValidator(Required.class));
		Assert.assertNull(registry.getTypeValidator(Integer.class));
	}

}
