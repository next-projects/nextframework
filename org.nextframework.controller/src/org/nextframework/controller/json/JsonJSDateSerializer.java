package org.nextframework.controller.json;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class JsonJSDateSerializer extends StdSerializer<Date> {

	private static final long serialVersionUID = 1L;
	public static DateFormat customDateFormat = new SimpleDateFormat("'new Date('yyyy','MM','dd','HH','mm','ss','SSS')'");

	public JsonJSDateSerializer() {
		this(null);
	}

	@SuppressWarnings("all")
	public JsonJSDateSerializer(Class t) {
		super(t);
	}

	@Override
	public void serialize(Date value, JsonGenerator jgen, SerializerProvider arg2) throws IOException, JsonProcessingException {
		if (value == null) {
			jgen.writeNull();
		} else {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(value);
			calendar.add(Calendar.MONTH, -1); // the month is zero based
			jgen.writeRawValue(customDateFormat.format(calendar.getTime()));
		}
	}

}