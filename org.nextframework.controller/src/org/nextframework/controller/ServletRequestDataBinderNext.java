/*
 * Next Framework http://www.nextframework.org
 * Copyright (C) 2009 the original author or authors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * You may obtain a copy of the license at
 * 
 *     http://www.gnu.org/copyleft/lesser.html
 * 
 */
package org.nextframework.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.nextframework.core.standard.Next;
import org.nextframework.exception.NextException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.validation.AbstractPropertyBindingResult;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author rogelgarcia | marcusareu
 * @since 31/01/2006
 * @version 1.1
 */
public class ServletRequestDataBinderNext extends ServletRequestDataBinder {

	public static final char VALUE_OBJECT_ATTR_SEPARATOR = ';';
	public static final char VALUE_OBJECT_CLOSE_CLASS_ATTR = ']';
	public static final char VALUE_OBJECT_OPEN_CLASS_ATTR = '[';

	//copied from ComboReloadGroupTag
	public static final String PARAMETRO_SEPARATOR = "#";

	//propriedade especial.. seta o campo para null
	public static final String EXCLUDE = "_excludeField";

	//salva o objeto em disco e depois recupera (util em uploads)
	public static final String TEMP = "_tempField";
	public static final String FILE = "_fileObject";
	public static final String DATE_PATTERN = "_datePattern";
	public static final String NULL_VALUE = "<null>";

	BeanPropertyBindingResult beanPropertyBindingResult;

	public ServletRequestDataBinderNext(Object target, String objectName) {
		super(target, objectName);
	}

	//isso foi modificado e retornado ao estado original 
	private static final String OBJECT_VALUE_REGEX = "((.*\\.)*\\w*)\\[(.*)\\]";

	@Deprecated
	protected BindException createErrors(Object target, String objectName) {
		return new BindExceptionNext(target, objectName);
	}

	@Override
	protected AbstractPropertyBindingResult getInternalBindingResult() {

		if (beanPropertyBindingResult == null) {

			beanPropertyBindingResult = new BeanPropertyBindingResult(getTarget(), getObjectName()) {

				private static final long serialVersionUID = 1L;

				@Override
				protected BeanWrapper createBeanWrapper() {
					return new ExtendedBeanWrapper(getTarget());
				}

			};

		}

		return beanPropertyBindingResult;
	}

	@Override
	protected void doBind(MutablePropertyValues mpvs) {

		PropertyValue[] propertyValues = mpvs.getPropertyValues();

		for (int i = 0; i < propertyValues.length; i++) {
			PropertyValue propertyValue = propertyValues[i];
			if (propertyValue.getName().endsWith(DATE_PATTERN)) {
				extractDatePattern(mpvs, propertyValue);
			}
		}

		//traduz parametros do tipo meupacote.MinhaClasse[id=1], para os objetos correspondentes		
		for (int i = 0; i < propertyValues.length; i++) {
			PropertyValue value = propertyValues[i];
			if (isObjectValue(value.getValue())) {
				Object translatedObjectValue = translateObjectValue(value.getName(), value.getValue(), mpvs);
				mpvs.addPropertyValue(value.getName(), translatedObjectValue);
				if (translatedObjectValue == null) {
					//alguns propertyValue podem ter sido removidos
					//resetar os propertyValues
					//TODO melhorar forma de fazer isso
					propertyValues = mpvs.getPropertyValues();
					i = -1;
					continue;
				}
			} else if (isObjectArrayValue(value.getValue())) {
				mpvs.addPropertyValue(value.getName(), translateObjectArrayValue(value.getName(), value.getValue()));
			}
		}

		super.doBind(mpvs);

	}

	/**
	 * Faz a tradução de parametros do tipo meupacote.MinhaClasse[id=1] para o objeto esperado<BR>
	 * 
	 * @param name
	 * @param value
	 * @param mpvs 
	 * @return
	 */
	public static Object translateObjectValue(Object value) {
		return translateObjectValue(null, value, new MutablePropertyValues());
	}

	/**
	 * Faz a tradução de parametros do tipo meupacote.MinhaClasse[id=1] para o objeto esperado<BR>
	 * 
	 * @param name
	 * @param value
	 * @param mpvs 
	 * @return
	 */
	public static Object translateObjectValue(String name, Object value, MutablePropertyValues mpvs) {

		if (NULL_VALUE.equals(value)) {

			//temos que remover todos os nomes subsequentes ao objeto nulo..
			//ex.: se tivermoos municipio = <null>
			//municipio.uf também tem que ser nulo
			//TODO MOVER ESSA CHECAGEM PARA OUTRO LUGAR

			//modificado por Pedro em 16/10/2007
			//também é possível dar um translate com o mpvs null, neste caso apenas retorna o objeto como null.

			if (mpvs == null) {
				//System.out.println("Found null MPVS in translateObjectValue");
				return null;
			}

			PropertyValue[] propertyValues = mpvs.getPropertyValues();
			for (int i = 0; i < propertyValues.length; i++) {
				if (propertyValues[i].getName().startsWith(name + ".")) {
					mpvs.removePropertyValue(propertyValues[i]);
				}
			}

			return null;
		}

		if (value instanceof String[]) {//2014.10.16
			String[] stringValues = (String[]) value;
			Object[] values = new Object[stringValues.length];
			for (int i = 0; i < stringValues.length; i++) {
				String string = stringValues[i];
				values[i] = translateObjectValue(name, string, mpvs);
			}
			return values;
		}

		String valueString = value.toString();
//		Pattern pattern = Pattern.compile(OBJECT_VALUE_REGEX);
//		Matcher matcher = pattern.matcher(valueString);
		String[] objectValueParts = getObjectValueParts(valueString);

		if (objectValueParts != null) {

			String nomeClasse = objectValueParts[0];

			String nameValuesString = objectValueParts[1];
			PropertyValues properties = getPropertyValues(nameValuesString);

			Class<?> clazz;
			try {
				clazz = Class.forName(nomeClasse);
			} catch (ClassNotFoundException e) {
				throw new NextException("Não foi possível instanciar classe [" + nomeClasse + "] da propriedade " + name, e);
			}

//			if(mpvs == null){
//				resultado = createObject(properties, clazz);	
//			} else {
//				PropertyValue[] propertyValues = properties.getPropertyValues();
//				for (PropertyValue pv : propertyValues) {
//					PropertyValue propertyValue = new PropertyValue(name+"."+pv.getName(), pv.getValue());
//					mpvs.addPropertyValue(propertyValue);
//				}
//				resultado = createObject(properties, clazz);
//			}
			//Modificado em 16/02/2014.. Não deve adicionar as subpropriedades ao mpvs
			//pois o objeto retornado já foi montado de maneira completa

			Object resultado = createObject(properties, clazz);
			return resultado;

		}

		return value;
	}

	private static String[] getObjectValueParts(String valueString) {
		if (valueString == null) {
			return null;
		}
		boolean hasClosingBrackets = valueString.endsWith("]");
		int indexOFBeginningBrackets = valueString.indexOf('[');
		if (indexOFBeginningBrackets < 0 || !hasClosingBrackets) {
			return null;
		}
		String className = valueString.substring(0, indexOFBeginningBrackets);
		boolean containsPackage = className.indexOf('.') > 0;
		if (!containsPackage) {
			return null;
		}
		String[] parts = new String[2];
		parts[0] = className;
		parts[1] = valueString.substring(indexOFBeginningBrackets + 1, valueString.length() - 1);
		return parts;
	}

	public static Object createObject(PropertyValues properties, Class<?> clazz) {
		Object resultado = BeanUtils.instantiateClass(clazz);
		ServletRequestDataBinder binder = new ServletRequestDataBinderNext(resultado, clazz + " obj");
		binder.bind(properties);
		return resultado;
	}

	static ValueStringSpliter valueStringSpliter = new ValueStringSpliter();

	private static PropertyValues getPropertyValues(String nameValuesString) {
		return valueStringSpliter.getPropertyValues(nameValuesString);
	}

	public static boolean isObjectValue(Object value) {

		if (value instanceof String[]) { // 2014/10/16
			String[] values = (String[]) value;
			for (String string : values) {
				if (!isObjectValue(string)) {
					return false;
				}
			}
			return true;
		}

		if (value instanceof String) {

			String string = (String) value;
			if (string.equals(NULL_VALUE)) {
				return true;
			}

			//fazer uma verificacao rápida para evitar o matches que deu pau em determinadas strings
			char[] toCharArray = string.toCharArray();

			if (toCharArray.length > 3) {

				boolean temColchetes = false;
				for (int i = 0; i < toCharArray.length; i++) {
					if (toCharArray[i] == '[') {
						temColchetes = true;
						//se tem abre colchetes tem grandes chances de ser um objectValue entao vamos dar o brake para cair no matches
						break;
					}
					if (toCharArray[i] == ' ') {
						//se achou espaco em branco antes de um colchetes nao é objectvalue. Pode retornar falso
						return false;
					}
				}

				//verifcar se termina com ']'
				int i = toCharArray.length - 1;
				while (toCharArray[i] == ' ') {
					i--;
				}
				if (toCharArray[i] != ']') {
					return false; // se nao termina com ] pode retornar falso porque nao é objectValue
				}

				if (!temColchetes) {
					return false;
				}

			} else {
				//se tiver menos de 3 caracteres pode retornar falso
				return false;
			}

			if ((string.matches(OBJECT_VALUE_REGEX) || NULL_VALUE.equals(string)) && !string.contains(PARAMETRO_SEPARATOR)) {
				return true;
			}

		}

		return false;
	}

	private boolean isObjectArrayValue(Object value) {
		if (value != null && value.getClass().isArray()) {
			if (((Object[]) value).length > 0) {
				Object svalue = ((Object[]) value)[0];
				if (svalue instanceof String && (svalue.toString().matches(OBJECT_VALUE_REGEX) || NULL_VALUE.equals(svalue))) {
					return true;
				}
			}
		}
		return false;
	}

	private Object translateObjectArrayValue(String name, Object value) {
		List<Object> list = new ArrayList<Object>();
		Object[] array = (Object[]) value;
		for (Object object : array) {
			list.add(translateObjectValue(name, object, null));
		}
		return list.toArray();
	}

	@Override
	protected void checkFieldMarkers(MutablePropertyValues mpvs) {

		super.checkFieldMarkers(mpvs);

		// checar se existe algum campo com remove
		// se existir o valor será setado para null
		PropertyValue[] propertyValues = mpvs.getPropertyValues();

		for (PropertyValue propertyValue : propertyValues) {
			if (propertyValue.getName().endsWith(FILE)) {
				String fieldName = propertyValue.getName().substring(0, propertyValue.getName().length() - FILE.length());
				if (!mpvs.contains(fieldName)) {
					mpvs.addPropertyValue(new PropertyValue(fieldName, propertyValue.getValue()));
				} else if (mpvs.contains(fieldName) && mpvs.getPropertyValue(fieldName).getValue() instanceof MultipartFile) {
					if (((MultipartFile) mpvs.getPropertyValue(fieldName).getValue()).getSize() == 0) {
						mpvs.addPropertyValue(new PropertyValue(fieldName, propertyValue.getValue()));
					}
				}
			}
		}

		for (PropertyValue propertyValue : propertyValues) {
			if (propertyValue.getName().endsWith(EXCLUDE) && new Boolean((String) propertyValue.getValue())) {
				String fieldName = propertyValue.getName().substring(0, propertyValue.getName().length() - EXCLUDE.length());
				mpvs.removePropertyValue(fieldName);
				mpvs.removePropertyValue(fieldName + TEMP);
				//excluir as propriedades subsequentes também
				for (int i = 0; i < propertyValues.length; i++) {
					if (propertyValues[i].getName().startsWith(fieldName)) {
						mpvs.removePropertyValue(propertyValues[i]);
					}
				}
			}
		}

		propertyValues = mpvs.getPropertyValues();
		for (PropertyValue propertyValue : propertyValues) {
			if (propertyValue.getName().endsWith(TEMP)) {
				String fieldName = propertyValue.getName().substring(0, propertyValue.getName().length() - TEMP.length());
				if (!mpvs.contains(fieldName)) {
					mpvs.addPropertyValue(new PropertyValue(fieldName, loadObject((String) mpvs.getPropertyValue(propertyValue.getName()).getValue())));
				} else if (mpvs.contains(fieldName) && mpvs.getPropertyValue(fieldName).getValue() instanceof MultipartFile) {
					if (((MultipartFile) mpvs.getPropertyValue(fieldName).getValue()).getSize() == 0) {
						mpvs.addPropertyValue(new PropertyValue(fieldName, loadObject((String) mpvs.getPropertyValue(propertyValue.getName()).getValue())));
					}
				}
			}
		}

	}

	private Object loadObject(String value) {
		//TODO UNIFICAR O LOCAL DE SALVAR E LER OS ARQUIVOS TEMPORARIOS
		java.io.File file = new java.io.File(System.getProperty("java.io.tmpdir"), Next.getApplicationName() + value);
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
			Object obj = in.readObject();
			in.close();
			return obj;
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private void extractDatePattern(MutablePropertyValues mpvs, PropertyValue propertyValue) {
		String dateProperty = propertyValue.getName().substring(0, propertyValue.getName().length() - DATE_PATTERN.length());
		PropertyValue pv = mpvs.getPropertyValue(dateProperty);
		if (pv != null && pv.getValue() != null && pv.getValue() instanceof String) {
			String dataString = (String) pv.getValue();
			SimpleDateFormat dateFormat = new SimpleDateFormat((String) propertyValue.getValue());
			try {
				mpvs.addPropertyValue(new PropertyValue(dateProperty, dateFormat.parse(dataString)));
			} catch (ParseException e) {
				// se não conseguir converter não fazer nada.. deixa a exceção vazar
			}
		}
	}

	private static class ValueStringSpliter {

		enum State {
			IN_PROPERTY_NAME,
			IN_PROPERTY_VALUE
		}

		private PropertyValues getPropertyValues(String nameValuesString) {

			State state = State.IN_PROPERTY_NAME;
			StringBuilder propertyName = new StringBuilder();
			StringBuilder propertyValue = new StringBuilder(26);

			MutablePropertyValues properties = new MutablePropertyValues();

			int bracketsStack = 0;
			for (int i = 0; i < nameValuesString.length(); i++) {
				char c = nameValuesString.charAt(i);
				switch (state) {
					case IN_PROPERTY_NAME:
						if (c == '=') {
							state = State.IN_PROPERTY_VALUE;
						} else {
							propertyName.append(c);
						}
						break;
					case IN_PROPERTY_VALUE:
						switch (c) {
							case VALUE_OBJECT_OPEN_CLASS_ATTR:
								bracketsStack++;
								propertyValue.append(c);
								break;
							case VALUE_OBJECT_CLOSE_CLASS_ATTR:
								bracketsStack--;
								propertyValue.append(c);
								break;
							case VALUE_OBJECT_ATTR_SEPARATOR:
								if (bracketsStack > 0) {
									propertyValue.append(c);
								} else {
									state = State.IN_PROPERTY_NAME;
									properties.add(propertyName.toString(), propertyValue.toString());
									propertyName = new StringBuilder();
									propertyValue = new StringBuilder(26);
								}
								break;
							default:
								propertyValue.append(c);
								break;
						}
					default:
						break;
				}
			}

			properties.add(propertyName.toString(), propertyValue.toString());

			return properties;
		}

//		private PropertyValues getPropertyValues(String nameValuesString) {
//			//TODO MELHORAR A FORMA DE DIVIDIR A STRING
//			MutablePropertyValues properties = new MutablePropertyValues();
//			
//			StringTokenizer tokenizer = new StringTokenizer(nameValuesString, ",");
//			while (tokenizer.hasMoreTokens()){
//				String token = tokenizer.nextToken();
//				String nameValuePair[] = token.trim().split("=");
//				PropertyValue propertyValue = new PropertyValue(nameValuePair[0], nameValuePair[1]);
//				properties.addPropertyValue(propertyValue);
//			}
//			return properties;
//		}

	}

}
