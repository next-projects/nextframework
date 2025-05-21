package org.nextframework.view;

import java.beans.PropertyEditor;
import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.nextframework.bean.editors.CalendarEditor;
import org.nextframework.bean.editors.MoneyPropertyEditor;
import org.nextframework.bean.editors.SimpleTimePropertyEditor;
import org.nextframework.types.Money;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;

public class BaseTagPropertyEditorsManager {

	private Map<Class<?>, PropertyEditor> propertyEditors = new HashMap<Class<?>, PropertyEditor>();

	private Map<Class<? extends Annotation>, InputListener<? extends Annotation>> inputListeners = new HashMap<Class<? extends Annotation>, InputListener<? extends Annotation>>();

	public Map<Class<?>, PropertyEditor> getPropertyEditors() {
		return propertyEditors;
	}

	public Map<Class<? extends Annotation>, InputListener<? extends Annotation>> getInputListeners() {
		return inputListeners;
	}

	public void registerPropertyEditor(Class<?> class1, PropertyEditor editor) {
		propertyEditors.put(class1, editor);
	}

	public void registerInputListener(InputListener<? extends Annotation> inputListener) {
		inputListeners.put(inputListener.getAnnotationType(), inputListener);
	}

	@SuppressWarnings("all")
	public <A extends Annotation> InputListener<A> getInputListener(A annotation) {
		InputListener<A> inputListener = (InputListener<A>) getInputListeners().get(annotation.annotationType());
		if (inputListener == null) {
			return new InputListener() {

				public void onRender(InputTag input, Annotation annotation) {
				}

				public Class getAnnotationType() {
					return null;
				}

			};
		}
		return inputListener;
	}

	public BaseTagPropertyEditorsManager() {
		init();
	}

	protected void init() {
		final DecimalFormat numberFormat = new DecimalFormat("#.##############");

		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		final boolean allowEmpty = true;

		registerPropertyEditor(Boolean.class, new CustomBooleanEditor(false));
		registerPropertyEditor(Short.class, new CustomNumberEditor(Short.class, false));
		registerPropertyEditor(Integer.class, new CustomNumberEditor(Integer.class, false));
		registerPropertyEditor(Long.class, new CustomNumberEditor(Long.class, false));
		registerPropertyEditor(BigInteger.class, new CustomNumberEditor(BigInteger.class, false));
		registerPropertyEditor(Float.class, new CustomNumberEditor(Float.class, numberFormat, false));
		registerPropertyEditor(Double.class, new CustomNumberEditor(Double.class, numberFormat, false));
		registerPropertyEditor(BigDecimal.class, new CustomNumberEditor(BigDecimal.class, numberFormat, false));

		registerPropertyEditor(Date.class, new CustomDateEditor(simpleDateFormat, allowEmpty));
		registerPropertyEditor(Calendar.class, new CalendarEditor(simpleDateFormat, allowEmpty));
		registerPropertyEditor(GregorianCalendar.class, new CalendarEditor(simpleDateFormat, allowEmpty));
		registerPropertyEditor(java.sql.Date.class, new CustomDateEditor(simpleDateFormat, allowEmpty));
		registerPropertyEditor(Timestamp.class, new CustomDateEditor(simpleDateFormat, allowEmpty));

		registerPropertyEditor(Time.class, new org.nextframework.bean.editors.TimePropertyEditor());
		registerPropertyEditor(org.nextframework.types.SimpleTime.class, new SimpleTimePropertyEditor());

		registerPropertyEditor(Money.class, new MoneyPropertyEditor());

		registerInputListener(new MaxLengthInputListener());
		registerInputListener(new YearInputListener());
	}

}
