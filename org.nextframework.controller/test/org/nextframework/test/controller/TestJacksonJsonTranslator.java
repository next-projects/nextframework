package org.nextframework.test.controller;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextframework.controller.json.JacksonJsonTranslator;
import org.nextframework.types.Cep;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestJacksonJsonTranslator {

	private JacksonJsonTranslator translator;

	@Before
	public void setUp() {
		translator = new JacksonJsonTranslator();
	}

	// --- ObjectMapper creation ---

	@Test
	public void testCreateObjectMapper() {
		ObjectMapper mapper = translator.createObjectMapper();
		Assert.assertNotNull(mapper);
	}

	// --- POJO serialization/deserialization ---

	@Test
	public void testSerializeSimplePojo() {
		SamplePojo pojo = new SamplePojo();
		pojo.setName("test");
		pojo.setAge(25);
		String json = translator.toJson(pojo);
		Assert.assertNotNull(json);
		Assert.assertTrue(json.contains("\"name\":\"test\""));
		Assert.assertTrue(json.contains("\"age\":25"));
	}

	@Test
	public void testDeserializeSimplePojo() {
		String json = "{\"name\":\"test\",\"age\":25}";
		SamplePojo pojo = translator.fromJson(json, SamplePojo.class);
		Assert.assertNotNull(pojo);
		Assert.assertEquals("test", pojo.getName());
		Assert.assertEquals(25, pojo.getAge());
	}

	@Test
	public void testSerializeDeserializeRoundTrip() {
		SamplePojo original = new SamplePojo();
		original.setName("roundtrip");
		original.setAge(42);
		String json = translator.toJson(original);
		SamplePojo restored = translator.fromJson(json, SamplePojo.class);
		Assert.assertEquals(original.getName(), restored.getName());
		Assert.assertEquals(original.getAge(), restored.getAge());
	}

	// --- Null handling (NON_NULL inclusion) ---

	@Test
	public void testNullFieldsExcluded() {
		SamplePojo pojo = new SamplePojo();
		pojo.setName(null);
		pojo.setAge(10);
		String json = translator.toJson(pojo);
		Assert.assertFalse("Null fields should be excluded", json.contains("\"name\""));
		Assert.assertTrue(json.contains("\"age\":10"));
	}

	// --- Cep custom serialization/deserialization ---

	@Test
	public void testCepSerialization() {
		CepHolder holder = new CepHolder();
		holder.setCep(new Cep("12345678"));
		String json = translator.toJson(holder);
		Assert.assertNotNull(json);
		Assert.assertTrue("Cep should be serialized as formatted string", json.contains("12345-678"));
	}

	@Test
	public void testCepDeserialization() {
		String json = "{\"cep\":\"12345678\"}";
		CepHolder holder = translator.fromJson(json, CepHolder.class);
		Assert.assertNotNull(holder.getCep());
		Assert.assertEquals("12345678", holder.getCep().getValue());
	}

	@Test
	public void testCepRoundTrip() {
		CepHolder original = new CepHolder();
		original.setCep(new Cep("01310100"));
		String json = translator.toJson(original);
		CepHolder restored = translator.fromJson(json, CepHolder.class);
		Assert.assertNotNull(restored.getCep());
		Assert.assertEquals(original.getCep().getValue(), restored.getCep().getValue());
	}

	// --- Throwable serialization ---

	@Test
	public void testThrowableSerialization() {
		ThrowableHolder holder = new ThrowableHolder();
		holder.setError(new RuntimeException("test error"));
		String json = translator.toJson(holder);
		Assert.assertNotNull(json);
		Assert.assertTrue("Should contain error message", json.contains("test error"));
	}

	@Test
	public void testThrowableWithCauseSerialization() {
		ThrowableHolder holder = new ThrowableHolder();
		holder.setError(new RuntimeException("outer", new IllegalArgumentException("inner")));
		String json = translator.toJson(holder);
		Assert.assertNotNull(json);
		Assert.assertTrue("Should contain outer message", json.contains("outer"));
		Assert.assertTrue("Should contain inner cause message", json.contains("inner"));
	}

	// --- List deserialization ---

	@Test
	public void testFromJsonAsList() {
		String json = "[{\"name\":\"a\",\"age\":1},{\"name\":\"b\",\"age\":2}]";
		List<SamplePojo> list = translator.fromJsonAsList(json, SamplePojo.class);
		Assert.assertNotNull(list);
		Assert.assertEquals(2, list.size());
		Assert.assertEquals("a", list.get(0).getName());
		Assert.assertEquals("b", list.get(1).getName());
	}

	@Test
	public void testFromJsonAsEmptyList() {
		String json = "[]";
		List<SamplePojo> list = translator.fromJsonAsList(json, SamplePojo.class);
		Assert.assertNotNull(list);
		Assert.assertTrue(list.isEmpty());
	}

	// --- Date formatting (no timestamps) ---

	@Test
	public void testDateNotSerializedAsTimestamp() {
		DateHolder holder = new DateHolder();
		holder.setDate(new Date(0));
		String json = translator.toJson(holder);
		Assert.assertNotNull(json);
		// With WRITE_DATES_AS_TIMESTAMPS disabled, dates should be ISO strings not numbers
		Assert.assertFalse("Date should not be a plain number", json.matches(".*\"date\":\\d+.*"));
	}

	// --- Helper POJOs ---

	public static class SamplePojo {

		private String name;
		private int age;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

	}

	public static class CepHolder {

		private Cep cep;

		public Cep getCep() {
			return cep;
		}

		public void setCep(Cep cep) {
			this.cep = cep;
		}

	}

	public static class ThrowableHolder {

		private Throwable error;

		public Throwable getError() {
			return error;
		}

		public void setError(Throwable error) {
			this.error = error;
		}

	}

	public static class DateHolder {

		private Date date;

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

	}

}
