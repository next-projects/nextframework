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
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.nextframework.bean.editors.CalendarEditor;
import org.nextframework.bean.editors.MoneyPropertyEditor;
import org.nextframework.bean.editors.SimpleTimePropertyEditor;
import org.nextframework.types.Cep;
import org.nextframework.types.Cnpj;
import org.nextframework.types.Cpf;
import org.nextframework.types.File;
import org.nextframework.types.InscricaoEstadual;
import org.nextframework.types.Money;
import org.nextframework.types.Phone;
import org.nextframework.types.PhoneBrazil;
import org.nextframework.types.SimpleTime;
import org.nextframework.util.Util;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;

public class BaseTagManager {

	private Map<Class<?>, PropertyEditor> propertyEditors = new HashMap<>();
	private Map<Class<?>, InputTagType> inputTypes = new HashMap<>();
	private Map<Class<? extends Annotation>, InputListener<? extends Annotation>> inputListeners = new HashMap<>();

	public BaseTagManager() {
		init();
	}

	protected void init() {

		DecimalFormat numberFormat = new DecimalFormat("#.##############");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		boolean allowEmpty = true;

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

		registerInputType(Boolean.class, InputTagType.CHECKBOX);
		registerInputType(boolean.class, InputTagType.CHECKBOX);
		registerInputType(Byte.class, InputTagType.INTEGER);
		registerInputType(Short.class, InputTagType.INTEGER);
		registerInputType(Integer.class, InputTagType.INTEGER);
		registerInputType(Long.class, InputTagType.INTEGER);
		registerInputType(BigInteger.class, InputTagType.INTEGER);
		registerInputType(Float.class, InputTagType.FLOAT);
		registerInputType(Double.class, InputTagType.FLOAT);
		registerInputType(BigDecimal.class, InputTagType.FLOAT);
		registerInputType(Date.class, InputTagType.DATE);
		registerInputType(Calendar.class, InputTagType.DATE);
		registerInputType(java.sql.Date.class, InputTagType.DATE);
		registerInputType(Timestamp.class, InputTagType.TIME);
		registerInputType(Time.class, InputTagType.TIME);
		registerInputType(SimpleTime.class, InputTagType.TIME);
		registerInputType(Money.class, InputTagType.MONEY);
		registerInputType(Collection.class, InputTagType.SELECT_MANY);
		registerInputType(File.class, InputTagType.FILE);
		registerInputType(Cpf.class, InputTagType.CPF);
		registerInputType(Cnpj.class, InputTagType.CNPJ);
		registerInputType(InscricaoEstadual.class, InputTagType.INSCRICAO_ESTADUAL);
		registerInputType(Cep.class, InputTagType.CEP);
		registerInputType(PhoneBrazil.class, InputTagType.PHONE);
		registerInputType(Phone.class, InputTagType.PHONE);

		registerInputListener(new MaxLengthInputListener());
		registerInputListener(new YearInputListener());

	}

	public void registerPropertyEditor(Class<?> class1, PropertyEditor editor) {
		propertyEditors.put(class1, editor);
	}

	public void registerInputType(Class<?> clazz, InputTagType inputType) {
		inputTypes.put(clazz, inputType);
	}

	public void registerInputListener(InputListener<? extends Annotation> inputListener) {
		inputListeners.put(inputListener.getAnnotationType(), inputListener);
	}

	public PropertyEditor getPropertyEditor(Class<?> clazz) {
		clazz = Util.objects.getRealClass(clazz);
		return propertyEditors.get(clazz);
	}

	public InputTagType getInputType(Class<?> clazz) {
		clazz = Util.objects.getRealClass(clazz);
		return inputTypes.get(clazz);
	}

	@SuppressWarnings("all")
	public <A extends Annotation> InputListener<A> getInputListener(A annotation) {
		InputListener<A> inputListener = (InputListener<A>) inputListeners.get(annotation.annotationType());
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

}
