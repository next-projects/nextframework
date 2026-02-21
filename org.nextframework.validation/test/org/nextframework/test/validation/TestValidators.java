package org.nextframework.test.validation;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.nextframework.validation.ObjectAnnotationValidator;
import org.nextframework.validation.annotation.Email;
import org.nextframework.validation.annotation.MaxLength;
import org.nextframework.validation.annotation.MaxValue;
import org.nextframework.validation.annotation.MinLength;
import org.nextframework.validation.annotation.MinValue;
import org.nextframework.validation.annotation.Required;
import org.nextframework.validation.validators.EmailValidator;
import org.nextframework.validation.validators.MaxLengthValidator;
import org.nextframework.validation.validators.MaxValueValidator;
import org.nextframework.validation.validators.MinLengthValidator;
import org.nextframework.validation.validators.MinValueValidator;
import org.nextframework.validation.validators.RequiredValidator;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

public class TestValidators {

	// ================= RequiredValidator =================

	@Test
	public void testRequiredValidatorRejectsNull() {
		RequiredValidator v = new RequiredValidator();
		Errors errors = createErrors();
		v.validate(new Object(), null, "field", "Field", null, errors, null);
		Assert.assertTrue("Null should be rejected", errors.hasErrors());
	}

	@Test
	public void testRequiredValidatorRejectsEmptyString() {
		RequiredValidator v = new RequiredValidator();
		Errors errors = createErrors();
		v.validate(new Object(), "", "field", "Field", null, errors, null);
		Assert.assertTrue("Empty string should be rejected", errors.hasErrors());
	}

	@Test
	public void testRequiredValidatorRejectsBlankString() {
		RequiredValidator v = new RequiredValidator();
		Errors errors = createErrors();
		v.validate(new Object(), "   ", "field", "Field", null, errors, null);
		Assert.assertTrue("Blank string should be rejected", errors.hasErrors());
	}

	@Test
	public void testRequiredValidatorAcceptsNonEmptyString() {
		RequiredValidator v = new RequiredValidator();
		Errors errors = createErrors();
		v.validate(new Object(), "value", "field", "Field", null, errors, null);
		Assert.assertFalse("Non-empty string should be accepted", errors.hasErrors());
	}

	@Test
	public void testRequiredValidatorRejectsEmptyList() {
		RequiredValidator v = new RequiredValidator();
		Errors errors = createErrors();
		v.validate(new Object(), new ArrayList<Object>(), "field", "Field", null, errors, null);
		Assert.assertTrue("Empty list should be rejected", errors.hasErrors());
	}

	@Test
	public void testRequiredValidatorAcceptsNonEmptyList() {
		RequiredValidator v = new RequiredValidator();
		Errors errors = createErrors();
		List<String> list = new ArrayList<String>();
		list.add("item");
		v.validate(new Object(), list, "field", "Field", null, errors, null);
		Assert.assertFalse("Non-empty list should be accepted", errors.hasErrors());
	}

	@Test
	public void testRequiredValidatorAcceptsNonNullObject() {
		RequiredValidator v = new RequiredValidator();
		Errors errors = createErrors();
		v.validate(new Object(), new Object(), "field", "Field", null, errors, null);
		Assert.assertFalse("Non-null object should be accepted", errors.hasErrors());
	}

	@Test
	public void testRequiredValidatorName() {
		RequiredValidator v = new RequiredValidator();
		Assert.assertEquals("required", v.getValidationName());
		Assert.assertEquals("Required", v.getValidationFunctionName());
	}

	// ================= EmailValidator =================

	@Test
	public void testEmailValidatorAcceptsValidEmail() {
		EmailValidator v = new EmailValidator();
		Errors errors = createErrors();
		v.validate(new Object(), "test@example.com", "field", "Field", null, errors, null);
		Assert.assertFalse("Valid email should be accepted", errors.hasErrors());
	}

	@Test
	public void testEmailValidatorRejectsInvalidEmail() {
		EmailValidator v = new EmailValidator();
		Errors errors = createErrors();
		v.validate(new Object(), "not-an-email", "field", "Field", null, errors, null);
		Assert.assertTrue("Invalid email should be rejected", errors.hasErrors());
	}

	@Test
	public void testEmailValidatorAcceptsNull() {
		EmailValidator v = new EmailValidator();
		Errors errors = createErrors();
		v.validate(new Object(), null, "field", "Field", null, errors, null);
		Assert.assertFalse("Null should be skipped", errors.hasErrors());
	}

	@Test
	public void testEmailValidatorAcceptsEmpty() {
		EmailValidator v = new EmailValidator();
		Errors errors = createErrors();
		v.validate(new Object(), "", "field", "Field", null, errors, null);
		Assert.assertFalse("Empty should be skipped", errors.hasErrors());
	}

	@Test
	public void testEmailValidatorName() {
		EmailValidator v = new EmailValidator();
		Assert.assertEquals("email", v.getValidationName());
		Assert.assertEquals("Email", v.getValidationFunctionName());
	}

	// ================= MaxLengthValidator =================

	@Test
	public void testMaxLengthValidatorAcceptsWithinLimit() {
		MaxLengthValidator v = new MaxLengthValidator();
		Errors errors = createErrors();
		MaxLength annotation = getAnnotation("maxLengthField", MaxLength.class);
		v.validate(new Object(), "short", "field", "Field", annotation, errors, null);
		Assert.assertFalse("String within limit should be accepted", errors.hasErrors());
	}

	@Test
	public void testMaxLengthValidatorRejectsExceedingLimit() {
		MaxLengthValidator v = new MaxLengthValidator();
		Errors errors = createErrors();
		MaxLength annotation = getAnnotation("maxLengthField", MaxLength.class);
		// maxLengthField has value=10
		v.validate(new Object(), "this string is longer than 10 characters", "field", "Field", annotation, errors, null);
		Assert.assertTrue("String exceeding limit should be rejected", errors.hasErrors());
	}

	@Test
	public void testMaxLengthValidatorSkipsNull() {
		MaxLengthValidator v = new MaxLengthValidator();
		Errors errors = createErrors();
		MaxLength annotation = getAnnotation("maxLengthField", MaxLength.class);
		v.validate(new Object(), null, "field", "Field", annotation, errors, null);
		Assert.assertFalse("Null should be skipped", errors.hasErrors());
	}

	@Test
	public void testMaxLengthValidatorSkipsEmpty() {
		MaxLengthValidator v = new MaxLengthValidator();
		Errors errors = createErrors();
		MaxLength annotation = getAnnotation("maxLengthField", MaxLength.class);
		v.validate(new Object(), "", "field", "Field", annotation, errors, null);
		Assert.assertFalse("Empty should be skipped", errors.hasErrors());
	}

	// ================= MinLengthValidator =================

	@Test
	public void testMinLengthValidatorAcceptsAboveLimit() {
		MinLengthValidator v = new MinLengthValidator();
		Errors errors = createErrors();
		MinLength annotation = getAnnotation("minLengthField", MinLength.class);
		// minLengthField has value=5
		v.validate(new Object(), "long enough text", "field", "Field", annotation, errors, null);
		Assert.assertFalse("String above limit should be accepted", errors.hasErrors());
	}

	@Test
	public void testMinLengthValidatorRejectsBelowLimit() {
		MinLengthValidator v = new MinLengthValidator();
		Errors errors = createErrors();
		MinLength annotation = getAnnotation("minLengthField", MinLength.class);
		// minLengthField has value=5
		v.validate(new Object(), "ab", "field", "Field", annotation, errors, null);
		Assert.assertTrue("String below limit should be rejected", errors.hasErrors());
	}

	@Test
	public void testMinLengthValidatorSkipsNull() {
		MinLengthValidator v = new MinLengthValidator();
		Errors errors = createErrors();
		MinLength annotation = getAnnotation("minLengthField", MinLength.class);
		v.validate(new Object(), null, "field", "Field", annotation, errors, null);
		Assert.assertFalse("Null should be skipped", errors.hasErrors());
	}

	// ================= MaxValueValidator =================

	@Test
	public void testMaxValueValidatorAcceptsWithinLimit() {
		MaxValueValidator v = new MaxValueValidator();
		Errors errors = createErrors();
		MaxValue annotation = getAnnotation("maxValueField", MaxValue.class);
		// maxValueField has value=100.0
		v.validate(new Object(), "50", "field", "Field", annotation, errors, null);
		Assert.assertFalse("Value within limit should be accepted", errors.hasErrors());
	}

	@Test
	public void testMaxValueValidatorRejectsExceedingLimit() {
		MaxValueValidator v = new MaxValueValidator();
		Errors errors = createErrors();
		MaxValue annotation = getAnnotation("maxValueField", MaxValue.class);
		v.validate(new Object(), "150", "field", "Field", annotation, errors, null);
		Assert.assertTrue("Value exceeding limit should be rejected", errors.hasErrors());
	}

	@Test
	public void testMaxValueValidatorAcceptsExactLimit() {
		MaxValueValidator v = new MaxValueValidator();
		Errors errors = createErrors();
		MaxValue annotation = getAnnotation("maxValueField", MaxValue.class);
		v.validate(new Object(), "100", "field", "Field", annotation, errors, null);
		Assert.assertFalse("Value at exact limit should be accepted", errors.hasErrors());
	}

	@Test
	public void testMaxValueValidatorSkipsNull() {
		MaxValueValidator v = new MaxValueValidator();
		Errors errors = createErrors();
		MaxValue annotation = getAnnotation("maxValueField", MaxValue.class);
		v.validate(new Object(), null, "field", "Field", annotation, errors, null);
		Assert.assertFalse("Null should be skipped", errors.hasErrors());
	}

	// ================= MinValueValidator =================

	@Test
	public void testMinValueValidatorAcceptsAboveLimit() {
		MinValueValidator v = new MinValueValidator();
		Errors errors = createErrors();
		MinValue annotation = getAnnotation("minValueField", MinValue.class);
		// minValueField has value=10
		v.validate(new Object(), "50", "field", "Field", annotation, errors, null);
		Assert.assertFalse("Value above limit should be accepted", errors.hasErrors());
	}

	@Test
	public void testMinValueValidatorRejectsBelowLimit() {
		MinValueValidator v = new MinValueValidator();
		Errors errors = createErrors();
		MinValue annotation = getAnnotation("minValueField", MinValue.class);
		v.validate(new Object(), "5", "field", "Field", annotation, errors, null);
		Assert.assertTrue("Value below limit should be rejected", errors.hasErrors());
	}

	@Test
	public void testMinValueValidatorAcceptsExactLimit() {
		MinValueValidator v = new MinValueValidator();
		Errors errors = createErrors();
		MinValue annotation = getAnnotation("minValueField", MinValue.class);
		v.validate(new Object(), "10", "field", "Field", annotation, errors, null);
		Assert.assertFalse("Value at exact limit should be accepted", errors.hasErrors());
	}

	@Test
	public void testMinValueValidatorSkipsNull() {
		MinValueValidator v = new MinValueValidator();
		Errors errors = createErrors();
		MinValue annotation = getAnnotation("minValueField", MinValue.class);
		v.validate(new Object(), null, "field", "Field", annotation, errors, null);
		Assert.assertFalse("Null should be skipped", errors.hasErrors());
	}

	// --- Helpers ---

	private Errors createErrors() {
		return new BeanPropertyBindingResult(new ValidationTestBean(), "bean");
	}

	@SuppressWarnings("unchecked")
	private <T extends Annotation> T getAnnotation(String fieldName, Class<T> annotationType) {
		try {
			return ValidationTestBean.class.getDeclaredField(fieldName).getAnnotation(annotationType);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Bean with annotated fields used to source annotation instances for validator tests.
	 */
	public static class ValidationTestBean {

		private String field;

		@MaxLength(10)
		private String maxLengthField;

		@MinLength(5)
		private String minLengthField;

		@MaxValue(100.0)
		private double maxValueField;

		@MinValue(10)
		private long minValueField;

		@Required
		private String requiredField;

		@Email
		private String emailField;

		public String getField() {
			return field;
		}

		public void setField(String field) {
			this.field = field;
		}

		public String getMaxLengthField() {
			return maxLengthField;
		}

		public void setMaxLengthField(String maxLengthField) {
			this.maxLengthField = maxLengthField;
		}

		public String getMinLengthField() {
			return minLengthField;
		}

		public void setMinLengthField(String minLengthField) {
			this.minLengthField = minLengthField;
		}

		public double getMaxValueField() {
			return maxValueField;
		}

		public void setMaxValueField(double maxValueField) {
			this.maxValueField = maxValueField;
		}

		public long getMinValueField() {
			return minValueField;
		}

		public void setMinValueField(long minValueField) {
			this.minValueField = minValueField;
		}

		public String getRequiredField() {
			return requiredField;
		}

		public void setRequiredField(String requiredField) {
			this.requiredField = requiredField;
		}

		public String getEmailField() {
			return emailField;
		}

		public void setEmailField(String emailField) {
			this.emailField = emailField;
		}

	}

}
