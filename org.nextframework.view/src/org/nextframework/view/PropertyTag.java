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
package org.nextframework.view;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.hibernate.LazyInitializationException;
import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.PropertyDescriptor;
import org.nextframework.core.web.NextWeb;
import org.nextframework.util.Util;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

/**
 * @author rogelgarcia | marcusabreu
 * @since 26/01/2006
 * @version 1.1
 */
public class PropertyTag extends BaseTag implements LogicalTag {

	protected String name;
	protected String varValue = "value";
	protected String varError = "error";
	protected String varLabel = "label";
	protected String varName = "name";
	protected String varType = "type";
	protected String varParameterizedTypes = "parameterizedTypes";
	protected String varPropertySetter = "propertySetter";
	protected String varAnnotations = "annotations";
	protected String varPropertyDescriptor = "propertyDescriptor";

	/** Name para ser utilizado no input completo */
	//protected String fullName;  //nome do input
	/** nome da propriedade começando do bean */
	//protected String fullNestedName; //nome da propriedade começando do bean

	public String getFullNestedName() {
		return montarFullNestedName(this, name);
	}

	@Override
	protected void doComponent() throws Exception {

		String fullName = montarFullPropertyName();
		final String fullNestedName = montarFullNestedName(this, name);

		final BeanDescriptor beanDescriptor = getBeanDescriptor(this);

		PropertyDescriptor propertyDescriptor = getPropertyDescriptor(fullNestedName, beanDescriptor);
		FieldError error = NextWeb.getRequestContext().getBindException().getFieldError(fullName);

		setTypeAndValue(beanDescriptor, propertyDescriptor, error);
		setLabel(beanDescriptor, propertyDescriptor);
		setNameAttribute(fullName);
		setPropertySetter(fullNestedName, beanDescriptor, propertyDescriptor);
		setAnnotations(propertyDescriptor);
		setPropertyDescriptor(propertyDescriptor);

		doBody();

		popAttributes(propertyDescriptor, checkErrors(beanDescriptor, error));

	}

	public static PropertyDescriptor getPropertyDescriptor(String fullNestedName, final BeanDescriptor beanDescriptor) {
		PropertyDescriptor propertyDescriptor = null;
		if (!"".equals(fullNestedName)) {
			propertyDescriptor = beanDescriptor.getPropertyDescriptor(fullNestedName);
		}
		return propertyDescriptor;
	}

	private void setPropertyDescriptor(PropertyDescriptor propertyDescriptor) {
		pushAttribute(varPropertyDescriptor, propertyDescriptor);
	}

	private void setPropertySetter(final String fullNestedName, final BeanDescriptor beanDescriptor, PropertyDescriptor propertyDescriptor) {

		if (propertyDescriptor != null) {

			PropertySetter propertySetter = new PropertySetter() {

				public void set(Object value) {
					Object targetBean = beanDescriptor.getTargetBean();
					if (targetBean == null) {
						log.error("TargetBean is null, cannot set property");
					}
					BeanWrapperImpl beanWrapperImpl = new BeanWrapperImpl(targetBean);
					beanWrapperImpl.setPropertyValue(fullNestedName, value);
				}

			};

			pushAttribute(varPropertySetter, propertySetter);

		}

	}

	private void setNameAttribute(String fullName) {
		pushAttribute(varName, fullName);//TODO FAZER SUPORTE A MAPAS.. QUANDO É MAPA O NOME PODE SER MODIFICADO
	}

	private void setAnnotations(PropertyDescriptor propertyDescriptor) {
		if (propertyDescriptor != null) {
			pushAttribute(varAnnotations, propertyDescriptor.getAnnotations());
		} else {
			pushAttribute(varAnnotations, new Annotation[0]);
		}
	}

	private void popAttributes(PropertyDescriptor propertyDescriptor, boolean containError) {
		if (containError) {
			popAttribute(varError);
		}
		if (propertyDescriptor != null) {
			popAttribute(varPropertySetter);
		}
		popAttribute(varPropertyDescriptor);
		popAttribute(varValue);
		popAttribute(varLabel);
		popAttribute(varName);
		popAttribute(varType);
		popAttribute(varAnnotations);
		popAttribute(varParameterizedTypes);
	}

	public void setLabel(final BeanDescriptor beanDescriptor, PropertyDescriptor propertyDescriptor) {
		pushAttribute(varLabel, getLabel(beanDescriptor, propertyDescriptor));
	}

	public static String getLabel(final BeanDescriptor beanDescriptor, PropertyDescriptor propertyDescriptor) {
		String viewCode = ViewUtils.getMessageCodeViewPrefix();
		if (propertyDescriptor != null) {
			return Util.beans.getDisplayName(propertyDescriptor, viewCode, NextWeb.getRequestContext().getLocale());
		}
		return Util.beans.getDisplayName(beanDescriptor, viewCode, NextWeb.getRequestContext().getLocale());
	}

	public void setTypeAndValue(final BeanDescriptor beanDescriptor, PropertyDescriptor propertyDescriptor, FieldError error) {

		Object value = getPropertyValue(beanDescriptor, propertyDescriptor, error);
		pushAttribute(varValue, value);

		Type type;
		if (propertyDescriptor != null) {
			type = propertyDescriptor.getType();
		} else {
			type = beanDescriptor.getTargetClass();
		}

		Type[] actualTypeArguments = new Type[0];
		if (checkErrors(beanDescriptor, error)) {
			pushAttribute(varError, error.getDefaultMessage());
			pushAttribute(varType, type);
			if (type instanceof ParameterizedType) {
				actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
			}
			pushAttribute(varParameterizedTypes, actualTypeArguments);
		} else {
			try {
				pushAttribute(varType, type);
				if (type instanceof ParameterizedType) {
					actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
				} else if (type instanceof Class<?> && ((Class<?>) type).isArray()) {
					actualTypeArguments = new Type[] { ((Class<?>) type).getComponentType() };
				}
				pushAttribute(varParameterizedTypes, actualTypeArguments);
			} catch (LazyInitializationException e) {
				pushAttribute(varType, String.class);
			}
		}

	}

	public static Object getPropertyValue(final BeanDescriptor beanDescriptor, PropertyDescriptor propertyDescriptor, FieldError error) {
		Object value;
		if (checkErrors(beanDescriptor, error)) {
			value = error.getRejectedValue();// se tiver algum valor.. vale o valor setado
		} else {
			try {
				if (propertyDescriptor != null) {
					value = propertyDescriptor.getValue();
				} else {
					value = beanDescriptor.getTargetBean();
				}
			} catch (LazyInitializationException e) {
				value = "[Could not initializate proxy] " + propertyDescriptor.getName();
			}
		}
		return value;
	}

	public static BeanDescriptor getBeanDescriptor(BaseTag fromTag) {
		BeanDescriptor beanDescriptor = fromTag.findParent(BeanTag.class, true).getBeanDescriptor();
		//removed in 2012-08-08
//		beanDescriptor.setIndexValueResolver(new PageContextIndexResolver(fromTag.getPageContext()));
		return beanDescriptor;
	}

	private static boolean checkErrors(BeanDescriptor beanDescriptor, FieldError error) {
		if (error == null) {
			return false;
		}
		BindException errors = NextWeb.getRequestContext().getBindException();
		return errors.hasErrors() && errors.getTarget().getClass().equals(beanDescriptor.getTargetClass());
	}

	@SuppressWarnings("unchecked")
	public static String montarFullNestedName(BaseTag fromTag, String name) {
//		PropertyTag propertyTag = findParent(PropertyTag.class);
//		if(propertyTag != null){
//			String parentFullNestedName = propertyTag.getFullNestedName();
//			if(!name.startsWith("[")){
//				name = "." +name;
//			}
//			fullNestedName = parentFullNestedName + name;
//		} else {
//			if("this".equals(name)){
//				fullNestedName = "";
//				return;
//			}
//			fullNestedName = name;
//		}
		String fullNestedName;
		BaseTag firstParent = fromTag.findFirst(ForEachBeanTag.class, PropertyTag.class, BeanTag.class);
		String separator = name.startsWith("[") ? "" : ".";
		if (firstParent instanceof PropertyTag) {
			fullNestedName = ((PropertyTag) firstParent).getFullNestedName() + separator + name;
		} else {
			fullNestedName = name;
		}
		return fullNestedName;
	}

	@SuppressWarnings("unchecked")
	protected String montarFullPropertyName() {
		String fullName;
		BaseTag firstParent = findFirst(ForEachBeanTag.class, PropertyTag.class, BeanTag.class);
		String separator = name.startsWith("[") ? "" : ".";
		if (firstParent instanceof PropertyTag) {
			fullName = ((PropertyTag) firstParent).getFullName() + separator + name;
		} else {
			BeanTag parentBean = ((BeanTag) firstParent);
			if (parentBean == null) {
				throw new NullPointerException("Tag property (name=\"" + name + "\") não está aninhada a uma outra tag Property ou Bean");
			}
			String propertyPrefix = parentBean.getPropertyPrefix();
			String propertyIndex = parentBean.getPropertyIndex();
			String prefix = "";
			if (Util.strings.isNotEmpty(propertyPrefix)) {
				prefix = propertyPrefix;
			}
			if (Util.strings.isNotEmpty(propertyIndex)) {
				prefix += "[" + propertyIndex + "]";
			}
			if (prefix.length() != 0 && name != null && !name.equals("this")) {
				prefix += ".";
			}
			if ("this".equals(name) || name == null) {
				fullName = prefix;
				return fullName;
			}
			fullName = prefix + name;
		}
		return fullName;
	}

	public String getFullName() {
		return montarFullPropertyName();
	}

	public String getVarAnnotations() {
		return varAnnotations;
	}

	public String getVarLabel() {
		return varLabel;
	}

	public String getName() {
		return name;
	}

	public String getVarName() {
		return varName;
	}

	public String getVarValue() {
		return varValue;
	}

	public void setVarAnnotations(String annotationsVar) {
		this.varAnnotations = annotationsVar;
	}

	public void setVarLabel(String displayNameVar) {
		this.varLabel = displayNameVar;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setVarName(String nameVar) {
		this.varName = nameVar;
	}

	public void setVarValue(String valueVar) {
		this.varValue = valueVar;
	}

	public String getVarParameterizedTypes() {
		return varParameterizedTypes;
	}

	public String getVarType() {
		return varType;
	}

	public void setVarParameterizedTypes(String varParameterizedTypes) {
		this.varParameterizedTypes = varParameterizedTypes;
	}

	public void setVarType(String varType) {
		this.varType = varType;
	}

}
