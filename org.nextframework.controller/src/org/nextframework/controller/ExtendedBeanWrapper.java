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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextframework.bean.editors.CalendarEditor;
import org.nextframework.bean.editors.CepPropertyEditor;
import org.nextframework.bean.editors.CnpjPropertyEditor;
import org.nextframework.bean.editors.CpfPropertyEditor;
import org.nextframework.bean.editors.NextCustomDateEditor;
import org.nextframework.bean.editors.NextCustomNumberEditor;
import org.nextframework.bean.editors.CustomSqlDateEditor;
import org.nextframework.bean.editors.InscricaoEstadualPropertyEditor;
import org.nextframework.bean.editors.MoneyPropertyEditor;
import org.nextframework.bean.editors.PhoneBrazilPropertyEditor;
import org.nextframework.bean.editors.PhonePropertyEditor;
import org.nextframework.bean.editors.SimpleTimePropertyEditor;
import org.nextframework.bean.editors.TimePropertyEditor;
import org.nextframework.bean.editors.TimestampPropertyEditor;
import org.nextframework.exception.NextException;
import org.nextframework.exception.NotParameterizedTypeException;
import org.nextframework.types.Cep;
import org.nextframework.types.Cnpj;
import org.nextframework.types.Cpf;
import org.nextframework.types.InscricaoEstadual;
import org.nextframework.types.ListSet;
import org.nextframework.types.Money;
import org.nextframework.types.Phone;
import org.nextframework.types.PhoneBrazil;
import org.nextframework.types.SimpleTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.MethodInvocationException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.NullValueInNestedPathException;
import org.springframework.beans.PropertyAccessException;
import org.springframework.beans.PropertyBatchUpdateException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.propertyeditors.ByteArrayPropertyEditor;
import org.springframework.beans.propertyeditors.CharacterEditor;
import org.springframework.beans.propertyeditors.ClassEditor;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.beans.propertyeditors.FileEditor;
import org.springframework.beans.propertyeditors.InputStreamEditor;
import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.beans.propertyeditors.PropertiesEditor;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.beans.propertyeditors.URLEditor;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceArrayPropertyEditor;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author rogelgarcia
 * @since 31/01/2006
 * @version 1.1
 */
@SuppressWarnings("all")
public class ExtendedBeanWrapper implements BeanWrapper {

	/**
	 * We'll create a lot of these objects, so we don't want a new logger every time
	 */
	private static final Log logger = LogFactory.getLog(ExtendedBeanWrapper.class);

	//---------------------------------------------------------------------
	// Instance data
	//---------------------------------------------------------------------

	/**
	 * The wrapped object
	 */
	private Object object;

	private String nestedPath = "";

	private Object rootObject;

	private boolean extractOldValueForEditor = false;

	private final Map defaultEditors;

	private Map customEditors;

	/**
	 * Cached introspections results for this object, to prevent encountering
	 * the cost of JavaBeans introspection every time.
	 */
	private CachedIntrospectionResults cachedIntrospectionResults;

	/**
	 * Map with cached nested BeanWrappers: nested path -> BeanWrapper instance.
	 */
	private Map nestedBeanWrappers;

	//---------------------------------------------------------------------
	// Constructors
	//---------------------------------------------------------------------

	/**
	 * Create new empty BeanWrapperImpl. Wrapped instance needs to be set afterwards.
	 * Registers default editors.
	 * @see #setWrappedInstance
	 */
	public ExtendedBeanWrapper() {
		this(true);
	}

	/**
	 * Create new empty BeanWrapperImpl. Wrapped instance needs to be set afterwards.
	 * @param registerDefaultEditors whether to register default editors
	 * (can be suppressed if the BeanWrapper won't need any type conversion)
	 * @see #setWrappedInstance
	 */
	public ExtendedBeanWrapper(boolean registerDefaultEditors) {
		if (registerDefaultEditors) {
			this.defaultEditors = new HashMap(32);
			registerDefaultEditors();
		} else {
			this.defaultEditors = Collections.EMPTY_MAP;
		}
	}

	/**
	 * Create new BeanWrapperImpl for the given object.
	 * @param object object wrapped by this BeanWrapper
	 */
	public ExtendedBeanWrapper(Object object) {
		this();
		setWrappedInstance(object);
	}

	/**
	 * Create new BeanWrapperImpl, wrapping a new instance of the specified class.
	 * @param clazz class to instantiate and wrap
	 */
	public ExtendedBeanWrapper(Class clazz) {
		this();
		setWrappedInstance(BeanUtils.instantiateClass(clazz));
	}

	/**
	 * Create new BeanWrapperImpl for the given object,
	 * registering a nested path that the object is in.
	 * @param object object wrapped by this BeanWrapper.
	 * @param nestedPath the nested path of the object
	 * @param rootObject the root object at the top of the path
	 */
	public ExtendedBeanWrapper(Object object, String nestedPath, Object rootObject) {
		this();
		setWrappedInstance(object, nestedPath, rootObject);
	}

	/**
	 * Create new BeanWrapperImpl for the given object,
	 * registering a nested path that the object is in.
	 * @param object object wrapped by this BeanWrapper.
	 * @param nestedPath the nested path of the object
	 * @param superBw the containing BeanWrapper (must not be <code>null</code>)
	 */
	private ExtendedBeanWrapper(Object object, String nestedPath, ExtendedBeanWrapper superBw) {
		this.defaultEditors = superBw.defaultEditors;
		setWrappedInstance(object, nestedPath, superBw.getWrappedInstance());
	}

	private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	{
		simpleDateFormat.setLenient(false);
	}

	/**
	 * Register default editors in this class, for restricted environments.
	 * We're not using the JRE's PropertyEditorManager to avoid potential
	 * SecurityExceptions when running in a SecurityManager.
	 * <p>Registers a <code>CustomNumberEditor</code> for all primitive number types,
	 * their corresponding wrapper types, <code>BigInteger</code> and <code>BigDecimal</code>.
	 */
	protected void registerDefaultEditors() {

		// Simple editors, without parameterization capabilities.
		// The JDK does not contain a default editor for any of these target types.
		this.defaultEditors.put(byte[].class, new ByteArrayPropertyEditor());
		this.defaultEditors.put(Class.class, new ClassEditor());
		this.defaultEditors.put(File.class, new FileEditor());
		this.defaultEditors.put(InputStream.class, new InputStreamEditor());
		this.defaultEditors.put(Locale.class, new LocaleEditor());
		this.defaultEditors.put(Properties.class, new PropertiesEditor());
		this.defaultEditors.put(Resource[].class, new ResourceArrayPropertyEditor());
		this.defaultEditors.put(String[].class, new StringArrayPropertyEditor());
		this.defaultEditors.put(URL.class, new URLEditor());

		// Default instances of collection editors.
		// Can be overridden by registering custom instances of those as custom editors.
		this.defaultEditors.put(Collection.class, new CustomCollectionEditor(Collection.class));
		this.defaultEditors.put(Set.class, new CustomCollectionEditor(Set.class));
		this.defaultEditors.put(SortedSet.class, new CustomCollectionEditor(SortedSet.class));
		this.defaultEditors.put(List.class, new CustomCollectionEditor(List.class));

		// Default instances of character and boolean editors.
		// Can be overridden by registering custom instances of those as custom editors.
		PropertyEditor characterEditor = new CharacterEditor(true);
		PropertyEditor booleanEditor = new CustomBooleanEditor(true);

		// The JDK does not contain a default editor for char!
		this.defaultEditors.put(char.class, characterEditor);
		this.defaultEditors.put(Character.class, characterEditor);

		// Spring's CustomBooleanEditor accepts more flag values than the JDK's default editor.
		this.defaultEditors.put(boolean.class, booleanEditor);
		this.defaultEditors.put(Boolean.class, booleanEditor);

		// The JDK does not contain default editors for number wrapper types!
		// Override JDK primitive number editors with our own CustomNumberEditor.
		PropertyEditor byteEditor = new NextCustomNumberEditor(Byte.class, true);
		PropertyEditor shortEditor = new NextCustomNumberEditor(Short.class, true);
		PropertyEditor integerEditor = new NextCustomNumberEditor(Integer.class, true);
		PropertyEditor longEditor = new NextCustomNumberEditor(Long.class, true);
		PropertyEditor floatEditor = new NextCustomNumberEditor(Float.class, true);
		PropertyEditor doubleEditor = new NextCustomNumberEditor(Double.class, true);

		this.defaultEditors.put(byte.class, byteEditor);
		this.defaultEditors.put(Byte.class, byteEditor);

		this.defaultEditors.put(short.class, shortEditor);
		this.defaultEditors.put(Short.class, shortEditor);

		this.defaultEditors.put(int.class, integerEditor);
		this.defaultEditors.put(Integer.class, integerEditor);

		this.defaultEditors.put(long.class, longEditor);
		this.defaultEditors.put(Long.class, longEditor);

		this.defaultEditors.put(float.class, floatEditor);
		this.defaultEditors.put(Float.class, floatEditor);

		this.defaultEditors.put(double.class, doubleEditor);
		this.defaultEditors.put(Double.class, doubleEditor);

		this.defaultEditors.put(BigDecimal.class, new NextCustomNumberEditor(BigDecimal.class, false));
		this.defaultEditors.put(BigInteger.class, new NextCustomNumberEditor(BigInteger.class, false));

		//============================================================================================

		// Date, Time, Hora e Timestamp.

		boolean allowEmpty = true;

		registerCustomEditor(Calendar.class, new CalendarEditor(simpleDateFormat, allowEmpty));
		registerCustomEditor(Date.class, new NextCustomDateEditor(simpleDateFormat, allowEmpty));
		registerCustomEditor(java.sql.Date.class, new CustomSqlDateEditor(simpleDateFormat, allowEmpty));
		registerCustomEditor(Time.class, new TimePropertyEditor());
		registerCustomEditor(SimpleTime.class, new SimpleTimePropertyEditor());
		registerCustomEditor(Timestamp.class, new TimestampPropertyEditor());

		// Tipos personalizados.
		registerCustomEditor(Cpf.class, new CpfPropertyEditor());
		registerCustomEditor(Cnpj.class, new CnpjPropertyEditor());
		registerCustomEditor(InscricaoEstadual.class, new InscricaoEstadualPropertyEditor());
		registerCustomEditor(Money.class, new MoneyPropertyEditor());
		registerCustomEditor(Cep.class, new CepPropertyEditor());
		registerCustomEditor(PhoneBrazil.class, new PhoneBrazilPropertyEditor());
		registerCustomEditor(Phone.class, new PhonePropertyEditor());

	}

	//---------------------------------------------------------------------
	// Implementation of BeanWrapper
	//---------------------------------------------------------------------

	/**
	 * Switch the target object, replacing the cached introspection results only
	 * if the class of the new object is different to that of the replaced object.
	 * @param object new target
	 */
	public void setWrappedInstance(Object object) {
		setWrappedInstance(object, "", null);
	}

	/**
	 * Switch the target object, replacing the cached introspection results only
	 * if the class of the new object is different to that of the replaced object.
	 * @param object new target
	 * @param nestedPath the nested path of the object
	 * @param rootObject the root object at the top of the path
	 */
	public void setWrappedInstance(Object object, String nestedPath, Object rootObject) {
		if (object == null) {
			throw new IllegalArgumentException("Cannot set BeanWrapperImpl target to a null object");
		}
		this.object = object;
		this.nestedPath = (nestedPath != null ? nestedPath : "");
		this.rootObject = (!"".equals(this.nestedPath) ? rootObject : object);
		this.nestedBeanWrappers = null;
		setIntrospectionClass(object.getClass());
	}

	public Object getWrappedInstance() {
		return this.object;
	}

	public Class getWrappedClass() {
		return this.object.getClass();
	}

	/**
	 * Return the nested path of the object wrapped by this BeanWrapper.
	 */
	public String getNestedPath() {
		return this.nestedPath;
	}

	/**
	 * Return the root object at the top of the path of this BeanWrapper.
	 * @see #getNestedPath
	 */
	public Object getRootInstance() {
		return this.rootObject;
	}

	/**
	 * Return the class of the root object at the top of the path of this BeanWrapper.
	 * @see #getNestedPath
	 */
	public Class getRootClass() {
		return (this.rootObject != null ? this.rootObject.getClass() : null);
	}

	/**
	 * Set the class to introspect.
	 * Needs to be called when the target object changes.
	 * @param clazz the class to introspect
	 */
	protected void setIntrospectionClass(Class clazz) {
		if (this.cachedIntrospectionResults == null ||
				!this.cachedIntrospectionResults.getBeanClass().equals(clazz)) {
			this.cachedIntrospectionResults = CachedIntrospectionResults.forClass(clazz);
		}
	}

	public void setExtractOldValueForEditor(boolean extractOldValueForEditor) {
		this.extractOldValueForEditor = extractOldValueForEditor;
	}

	@Override
	public void registerCustomEditor(Class<?> requiredType, PropertyEditor propertyEditor) {
		registerCustomEditor(requiredType, null, propertyEditor);
	}

	@Override
	public void registerCustomEditor(Class<?> requiredType, String propertyPath, PropertyEditor propertyEditor) {
		if (requiredType == null && propertyPath == null) {
			throw new IllegalArgumentException("Either requiredType or propertyPath is required");
		}
		if (this.customEditors == null) {
			this.customEditors = new LinkedHashMap();
		}
		if (propertyPath != null) {
			this.customEditors.put(propertyPath, new CustomEditorHolder(propertyEditor, requiredType));
		} else {
			this.customEditors.put(requiredType, propertyEditor);
		}
	}

	@Override
	public PropertyEditor findCustomEditor(Class<?> requiredType, String propertyPath) {
		if (this.customEditors == null) {
			return null;
		}
		if (propertyPath != null) {
			// Check property-specific editor first.
			PropertyEditor editor = getCustomEditor(propertyPath, requiredType);
			if (editor == null) {
				List strippedPaths = new LinkedList();
				addStrippedPropertyPaths(strippedPaths, "", propertyPath);
				for (Iterator it = strippedPaths.iterator(); it.hasNext() && editor == null;) {
					String strippedPath = (String) it.next();
					editor = getCustomEditor(strippedPath, requiredType);
				}
			}
			if (editor != null) {
				return editor;
			} else if (requiredType == null) {
				requiredType = getPropertyType(propertyPath);
			}
		}
		// No property-specific editor -> check type-specific editor.
		return getCustomEditor(requiredType);
	}

	/**
	 * Get custom editor that has been registered for the given property.
	 * @return the custom editor, or <code>null</code> if none specific for this property
	 */
	private PropertyEditor getCustomEditor(String propertyName, Class requiredType) {
		CustomEditorHolder holder = (CustomEditorHolder) this.customEditors.get(propertyName);
		return (holder != null ? holder.getPropertyEditor(requiredType) : null);
	}

	/**
	 * Get custom editor for the given type. If no direct match found,
	 * try custom editor for superclass (which will in any case be able
	 * to render a value as String via <code>getAsText</code>).
	 * @return the custom editor, or <code>null</code> if none found for this type
	 * @see java.beans.PropertyEditor#getAsText
	 */
	private PropertyEditor getCustomEditor(Class requiredType) {
		if (requiredType != null) {
			PropertyEditor editor = (PropertyEditor) this.customEditors.get(requiredType);
			if (editor == null) {
				for (Iterator it = this.customEditors.keySet().iterator(); it.hasNext();) {
					Object key = it.next();
					if (key instanceof Class && ((Class) key).isAssignableFrom(requiredType)) {
						editor = (PropertyEditor) this.customEditors.get(key);
					}
				}
			}
			return editor;
		}
		return null;
	}

	/**
	 * Add property paths with all variations of stripped keys and/or indexes.
	 * Invokes itself recursively with nested paths
	 * @param strippedPaths the result list to add to
	 * @param nestedPath the current nested path
	 * @param propertyPath the property path to check for keys/indexes to strip
	 */
	private void addStrippedPropertyPaths(List strippedPaths, String nestedPath, String propertyPath) {
		int startIndex = propertyPath.indexOf(PROPERTY_KEY_PREFIX_CHAR);
		if (startIndex != -1) {
			int endIndex = propertyPath.indexOf(PROPERTY_KEY_SUFFIX_CHAR);
			if (endIndex != -1) {
				String prefix = propertyPath.substring(0, startIndex);
				String key = propertyPath.substring(startIndex, endIndex + 1);
				String suffix = propertyPath.substring(endIndex + 1, propertyPath.length());
				// strip the first key
				strippedPaths.add(nestedPath + prefix + suffix);
				// search for further keys to strip, with the first key stripped
				addStrippedPropertyPaths(strippedPaths, nestedPath + prefix, suffix);
				// search for further keys to strip, with the first key not stripped
				addStrippedPropertyPaths(strippedPaths, nestedPath + prefix + key, suffix);
			}
		}
	}

	/**
	 * Determine the first (or last) nested property separator in the
	 * given property path, ignoring dots in keys (like "map[my.key]").
	 * @param propertyPath the property path to check
	 * @param last whether to return the last separator rather than the first
	 * @return the index of the nested property separator, or -1 if none
	 */
	private int getNestedPropertySeparatorIndex(String propertyPath, boolean last) {
		boolean inKey = false;
		int i = (last ? propertyPath.length() - 1 : 0);
		while ((last && i >= 0) || i < propertyPath.length()) {
			switch (propertyPath.charAt(i)) {
				case PROPERTY_KEY_PREFIX_CHAR:
				case PROPERTY_KEY_SUFFIX_CHAR:
					inKey = !inKey;
					break;
				case NESTED_PROPERTY_SEPARATOR_CHAR:
					if (!inKey) {
						return i;
					}
			}
			if (last)
				i--;
			else
				i++;
		}
		return -1;
	}

	/**
	 * Get the last component of the path. Also works if not nested.
	 * @param bw BeanWrapper to work on
	 * @param nestedPath property path we know is nested
	 * @return last component of the path (the property on the target bean)
	 */
	private String getFinalPath(BeanWrapper bw, String nestedPath) {
		if (bw == this) {
			return nestedPath;
		}
		return nestedPath.substring(getNestedPropertySeparatorIndex(nestedPath, true) + 1);
	}

	/**
	 * Recursively navigate to return a BeanWrapper for the nested property path.
	 * @param propertyPath property property path, which may be nested
	 * @return a BeanWrapper for the target bean
	 */
	protected ExtendedBeanWrapper getBeanWrapperForPropertyPath(String propertyPath) throws BeansException {
		int pos = getNestedPropertySeparatorIndex(propertyPath, false);
		// handle nested properties recursively
		if (pos > -1) {
			String nestedProperty = propertyPath.substring(0, pos);
			String nestedPath = propertyPath.substring(pos + 1);
			ExtendedBeanWrapper nestedBw = getNestedBeanWrapper(nestedProperty);
			return nestedBw.getBeanWrapperForPropertyPath(nestedPath);
		} else {
			return this;
		}
	}

	/**
	 * Retrieve a BeanWrapper for the given nested property.
	 * Create a new one if not found in the cache.
	 * <p>Note: Caching nested BeanWrappers is necessary now,
	 * to keep registered custom editors for nested properties.
	 * @param nestedProperty property to create the BeanWrapper for
	 * @return the BeanWrapper instance, either cached or newly created
	 */
	private ExtendedBeanWrapper getNestedBeanWrapper(String nestedProperty) throws BeansException {

		if (this.nestedBeanWrappers == null) {
			this.nestedBeanWrappers = new HashMap();
		}

		// get value of bean property
		PropertyTokenHolder tokens = getPropertyNameTokens(nestedProperty);
		Object propertyValue = getPropertyValue(tokens);
		String canonicalName = tokens.canonicalName;
		String propertyName = tokens.actualName;
		if (propertyValue == null) {
			//tentar instanciar o objeto
			String errorMsg = "";
			try {
				Type returnType = getPropertyType(tokens);
				Class clazz = null;
				if (returnType instanceof Class) {
					clazz = (Class) returnType;
				} else if (returnType instanceof ParameterizedType) {
					clazz = (Class) ((ParameterizedType) returnType).getRawType();
				}
				if (clazz != null) {
					errorMsg = "O erro pode ser evitado se a classe " + clazz.getName() + " possuir um construtor público sem argumentos";
					propertyValue = clazz.newInstance();
					setPropertyValue(nestedProperty, propertyValue);
				}
			} catch (Exception e) {
				throw new NullValueInNestedPathException(getRootClass(), this.nestedPath + canonicalName, errorMsg);
			}
		}

		// lookup cached sub-BeanWrapper, create new one if not found
		ExtendedBeanWrapper nestedBw = (ExtendedBeanWrapper) this.nestedBeanWrappers.get(canonicalName);
		if (nestedBw == null || nestedBw.getWrappedInstance() != propertyValue) {
			if (logger.isDebugEnabled()) {
				logger.debug("Creating new nested BeanWrapper for property '" + canonicalName + "'");
			}
			nestedBw = new ExtendedBeanWrapper(propertyValue, this.nestedPath + canonicalName + NESTED_PROPERTY_SEPARATOR, this);
			// inherit all type-specific PropertyEditors
			if (this.customEditors != null) {
				for (Iterator it = this.customEditors.entrySet().iterator(); it.hasNext();) {
					Map.Entry entry = (Map.Entry) it.next();
					if (entry.getKey() instanceof Class) {
						Class requiredType = (Class) entry.getKey();
						PropertyEditor editor = (PropertyEditor) entry.getValue();
						nestedBw.registerCustomEditor(requiredType, editor);
					} else if (entry.getKey() instanceof String) {
						String editorPath = (String) entry.getKey();
						int pos = getNestedPropertySeparatorIndex(editorPath, false);
						if (pos != -1) {
							String editorNestedProperty = editorPath.substring(0, pos);
							String editorNestedPath = editorPath.substring(pos + 1);
							if (editorNestedProperty.equals(canonicalName) || editorNestedProperty.equals(propertyName)) {
								CustomEditorHolder editorHolder = (CustomEditorHolder) entry.getValue();
								nestedBw.registerCustomEditor(
										editorHolder.getRegisteredType(), editorNestedPath, editorHolder.getPropertyEditor());
							}
						}
					}
				}
			}
			this.nestedBeanWrappers.put(canonicalName, nestedBw);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Using cached nested BeanWrapper for property '" + canonicalName + "'");
			}
		}

		return nestedBw;
	}

	private PropertyTokenHolder getPropertyNameTokens(String propertyName) {
		PropertyTokenHolder tokens = new PropertyTokenHolder();
		String actualName = null;
		List<Object> keys = new ArrayList<Object>(2);
		int searchIndex = 0;
		while (searchIndex != -1) {
			int keyStart = propertyName.indexOf(PROPERTY_KEY_PREFIX, searchIndex);
			searchIndex = -1;
			if (keyStart != -1) {
				int keyEnd = propertyName.indexOf(PROPERTY_KEY_SUFFIX, keyStart + PROPERTY_KEY_PREFIX.length());
				if (keyEnd != -1) {
					if (actualName == null) {
						actualName = propertyName.substring(0, keyStart);
					}
					String key = propertyName.substring(keyStart + PROPERTY_KEY_PREFIX.length(), keyEnd);
					if (key.startsWith("'") && key.endsWith("'")) {
						key = key.substring(1, key.length() - 1);
					} else if (key.startsWith("\"") && key.endsWith("\"")) {
						key = key.substring(1, key.length() - 1);
					}
					keys.add(key);
					searchIndex = keyEnd + PROPERTY_KEY_SUFFIX.length();
				}
			}
		}
		tokens.actualName = (actualName != null ? actualName : propertyName);
		tokens.canonicalName = tokens.actualName;
		if (!keys.isEmpty()) {
			tokens.canonicalName += PROPERTY_KEY_PREFIX +
					StringUtils.collectionToDelimitedString(keys, PROPERTY_KEY_SUFFIX + PROPERTY_KEY_PREFIX) +
					PROPERTY_KEY_SUFFIX;
			tokens.keys = (String[]) keys.toArray(new String[keys.size()]);
		}
		return tokens;
	}

	public Object getPropertyValue(String propertyName) throws BeansException {
		ExtendedBeanWrapper nestedBw = getBeanWrapperForPropertyPath(propertyName);
		PropertyTokenHolder tokens = getPropertyNameTokens(getFinalPath(nestedBw, propertyName));
		return nestedBw.getPropertyValue(tokens);
	}

	protected Object getPropertyValue(PropertyTokenHolder tokens) throws BeansException {
		String propertyName = tokens.canonicalName;
		String actualName = tokens.actualName;
		PropertyDescriptor pd = getPropertyDescriptorInternal(tokens.actualName);
		if (pd == null || pd.getReadMethod() == null) {
			throw new NotReadablePropertyException(getRootClass(), this.nestedPath + propertyName);
		}
		if (logger.isDebugEnabled())
			logger.debug("About to invoke read method [" + pd.getReadMethod() + "] on object of class [" + this.object.getClass().getName() + "]");
		try {
			Object value = pd.getReadMethod().invoke(this.object, (Object[]) null);
			Type genericReturnType = pd.getReadMethod().getGenericReturnType();
			Class rawReturnType = pd.getReadMethod().getReturnType();
			if (tokens.keys != null) {
				// apply indexes and map keys
				for (int i = 0; i < tokens.keys.length; i++) {
					String key = tokens.keys[i];
					//cria listas sob demanda.. não é mais necessário utilizar o ListSet no pojo
					Class originalClass = null;
					if (value != null) {
						originalClass = value.getClass();
					}
					if (value == null && rawReturnType != null && genericReturnType instanceof ParameterizedType) {
						ParameterizedType parameterizedType = (ParameterizedType) genericReturnType;
						if (Map.class.isAssignableFrom(rawReturnType)) {
							value = new LinkedHashMap();
							pd.getWriteMethod().invoke(this.object, value);
						} else if (List.class.isAssignableFrom(rawReturnType) || Set.class.isAssignableFrom(rawReturnType)) {
							Type type = parameterizedType.getActualTypeArguments()[0];
							value = new ListSet(type instanceof Class ? (Class) type : (Class) ((ParameterizedType) type).getRawType());
							pd.getWriteMethod().invoke(this.object, value);
						}
					}
					//fim da criacao sob demanda
					if (value == null) {
						throw new NullValueInNestedPathException(getRootClass(), this.nestedPath + propertyName,
								"Cannot access indexed value of property referenced in indexed " +
										"property path '" + propertyName + "': returned null");
					} else if (value.getClass().isArray()) {
						value = Array.get(value, Integer.parseInt(key));
					} else if (value instanceof List) {
						List list = (List) value;
						try {
							value = list.get(Integer.parseInt(key));
						} catch (IndexOutOfBoundsException e) {
							//tentar instanciar um bean da lista
							String extraMessage = "";
							if (genericReturnType instanceof ParameterizedType) {
								ParameterizedType parameterizedType = (ParameterizedType) genericReturnType;
								Type type = parameterizedType.getActualTypeArguments()[0];
								if (type instanceof Class) {
									Class clazz = (Class) type;
									extraMessage = "A classe " + clazz.getName() + " não possui um construtor publico sem argumentos";
									try {
										value = clazz.newInstance();
										int index = Integer.parseInt(key);
										int insertNulls = index - list.size();
										while (insertNulls > 0) { // 11/06/2012
											list.add(null);
											insertNulls--;
										}
										list.add(index, value); // CÓDIGO 15/01/2007
									} catch (InstantiationException e1) {
										throw new RuntimeException("Aconteceu um erro ao acessar um elemento da classe " + originalClass.getName()
												+ " propriedade " + propertyName + "  Não foi possível instanciar um bean para preencher a lista. " + extraMessage, e);
									}
								}
							} else if (originalClass != null) {
								throw new RuntimeException("Aconteceu um erro ao acessar um elemento da classe " + originalClass.getName()
										+ " propriedade " + propertyName + "  Sugestão: Utilize uma lista que cresça quando for necessário como o ListSet ou não instancie nenhum objeto para essa propriedade", e);
							} else {
								throw e;
							}
						}
					} else if (value instanceof Set) {
						// apply index to Iterator in case of a Set
						//TODO CRIAR AUTOMATICAMENTE O BEAN DO SET
						Set set = (Set) value;
						int index = Integer.parseInt(key);
						if (index < 0 || index >= set.size()) {
							throw new InvalidPropertyException(getRootClass(), this.nestedPath + propertyName,
									"Cannot get element with index " + index + " from Set of size " +
											set.size() + ", accessed using property path '" + propertyName + "'" + "  Sugestão: Utilize o ListSet ou não instancie nenhum objeto");
						}
						Iterator it = set.iterator();
						for (int j = 0; it.hasNext(); j++) {
							Object elem = it.next();
							if (j == index) {
								value = elem;
								break;
							}
						}
					} else if (value instanceof Map) {
						if (!(genericReturnType instanceof ParameterizedType)) {
							throw new NotParameterizedTypeException("Path direciona a um Map não parameterizado com generics. " +
									" Propriedade '" + this.nestedPath + propertyName + "' da classe [" + this.rootObject.getClass().getName() + "]");
						}
						ParameterizedType parameterizedType = ((ParameterizedType) genericReturnType);
						Type mapKeyType = parameterizedType.getActualTypeArguments()[0];
						Type mapValueType = parameterizedType.getActualTypeArguments()[1];
						Class rawKeyType = mapKeyType instanceof Class ? (Class) mapKeyType : (Class) ((ParameterizedType) mapKeyType).getRawType();
						Class rawValueType = mapValueType instanceof Class ? (Class) mapValueType : (Class) ((ParameterizedType) mapValueType).getRawType();
						Object objectKey = doTypeConversionIfNecessary(key, rawKeyType);
						Map map = (Map) value;
						value = map.get(objectKey);
						if (value == null && List.class.isAssignableFrom(rawValueType)) {
							List mapValue;
							try {
								Type listType = ((ParameterizedType) mapValueType).getActualTypeArguments()[0];
								mapValue = new ListSet(listType instanceof Class ? (Class) listType : (Class) ((ParameterizedType) listType).getRawType());
							} catch (ClassCastException e) {
								throw new RuntimeException("Na path " + propertyName + " um mapa contém uma lista não parametrizada");
							}
							map.put(objectKey, mapValue);
							value = mapValue;
						}
					} else {
						throw new InvalidPropertyException(getRootClass(), this.nestedPath + propertyName,
								"Property referenced in indexed property path '" + propertyName +
										"' is neither an array nor a List nor a Set nor a Map; returned value was [" + value + "]");
					}
				}
			}
			return value;
		} catch (InvocationTargetException ex) {
			throw new InvalidPropertyException(getRootClass(), this.nestedPath + propertyName,
					"Getter for property '" + actualName + "' threw exception", ex);
		} catch (IllegalAccessException ex) {
			throw new InvalidPropertyException(getRootClass(), this.nestedPath + propertyName,
					"Illegal attempt to get property '" + actualName + "' threw exception", ex);
		} catch (IndexOutOfBoundsException ex) {
			throw new InvalidPropertyException(getRootClass(), this.nestedPath + propertyName,
					"Index of out of bounds in property path '" + propertyName + "'", ex);
		} catch (NumberFormatException ex) {
			throw new InvalidPropertyException(getRootClass(), this.nestedPath + propertyName,
					"Invalid index in property path '" + propertyName + "'", ex);
		}
	}

	public void setPropertyValue(String propertyName, Object value) throws BeansException {
		ExtendedBeanWrapper nestedBw = null;
		try {
			nestedBw = getBeanWrapperForPropertyPath(propertyName);
		} catch (NotReadablePropertyException ex) {
			throw new NotWritablePropertyException(getRootClass(), this.nestedPath + propertyName,
					"Nested property in path '" + propertyName + "' does not exist", ex);
		}
		PropertyTokenHolder tokens = getPropertyNameTokens(getFinalPath(nestedBw, propertyName));
		nestedBw.setPropertyValue(tokens, value);
	}

	protected void setPropertyValue(PropertyTokenHolder tokens, Object newValue) throws BeansException {
		String propertyName = tokens.canonicalName;
		if (tokens.keys != null) {
			// apply indexes and map keys: fetch value for all keys but the last one
			PropertyTokenHolder getterTokens = new PropertyTokenHolder();
			getterTokens.canonicalName = tokens.canonicalName;
			getterTokens.actualName = tokens.actualName;
			getterTokens.keys = new String[tokens.keys.length - 1];
			System.arraycopy(tokens.keys, 0, getterTokens.keys, 0, tokens.keys.length - 1);
			Object propValue = null;
			Type type;
			try {
				propValue = getPropertyValue(getterTokens);
				type = getPropertyType(getterTokens);
				if (propValue == null) {
					Class rawType = null;
					if (type instanceof ParameterizedType) {
						if (((ParameterizedType) type).getRawType() instanceof Class) {
							rawType = (Class) ((ParameterizedType) type).getRawType();
						}
					} else if (type instanceof Class) {
						rawType = (Class) type;
					}
					if (rawType != null && List.class.isAssignableFrom(rawType)) {
						PropertyTokenHolder propertyTokenHolder = new PropertyTokenHolder();
						propertyTokenHolder.actualName = getterTokens.actualName;
						propertyTokenHolder.canonicalName = getterTokens.canonicalName;
						setPropertyValue(propertyTokenHolder, new ArrayList());
					}
				}
			} catch (NotReadablePropertyException ex) {
				throw new NotWritablePropertyException(getRootClass(), this.nestedPath + propertyName,
						"Cannot access indexed value in property referenced " +
								"in indexed property path '" + propertyName + "'",
						ex);
			}
			// set value for last key
			String key = tokens.keys[tokens.keys.length - 1];
			if (propValue == null) {
				throw new NullValueInNestedPathException(getRootClass(), this.nestedPath + propertyName,
						"Cannot access indexed value in property referenced " +
								"in indexed property path '" + propertyName + "': returned null");
			} else if (propValue.getClass().isArray()) {
				Class requiredType = propValue.getClass().getComponentType();
				int arrayIndex = Integer.parseInt(key);
				Object oldValue = null;
				try {
					if (this.extractOldValueForEditor) {
						oldValue = Array.get(propValue, arrayIndex);
					}
					Object convertedValue = doTypeConversionIfNecessary(propertyName, propertyName, oldValue, newValue, requiredType);
					Array.set(propValue, Integer.parseInt(key), convertedValue);
				} catch (IllegalArgumentException ex) {
					PropertyChangeEvent pce = new PropertyChangeEvent(this.rootObject, this.nestedPath + propertyName, oldValue, newValue);
					throw new TypeMismatchException(pce, requiredType, ex);
				} catch (IndexOutOfBoundsException ex) {
					throw new InvalidPropertyException(getRootClass(), this.nestedPath + propertyName,
							"Invalid array index in property path '" + propertyName + "'", ex);
				}
			} else if (propValue instanceof List) {
				List list = (List) propValue;
				int index = Integer.parseInt(key);
				Object oldValue = null;
				if (this.extractOldValueForEditor && index < list.size()) {
					oldValue = list.get(index);
				}
				Object convertedValue = doTypeConversionIfNecessary(propertyName, propertyName, oldValue, newValue, null);
				if (index < list.size()) {
					list.set(index, convertedValue);
				} else if (index >= list.size()) {
					for (int i = list.size(); i < index; i++) {
						try {
							list.add(null);
						} catch (NullPointerException ex) {
							throw new InvalidPropertyException(getRootClass(), this.nestedPath + propertyName,
									"Cannot set element with index " + index + " in List of size " +
											list.size() + ", accessed using property path '" + propertyName +
											"': List does not support filling up gaps with null elements");
						}
					}
					list.add(convertedValue);
				}
			} else if (propValue instanceof Map) {
				Map map = (Map) propValue;
				propValue.getClass().getGenericSuperclass();
				Object oldValue = null;
				if (this.extractOldValueForEditor) {
					oldValue = map.get(key);
				}
				Type type2 = ((ParameterizedType) type).getActualTypeArguments()[1];
				Type type3 = ((ParameterizedType) type).getActualTypeArguments()[0];
				Class reqClass = null;
				Class keyReqClass = null;
				if (type2 instanceof Class) {
					reqClass = (Class) type2;
				} else if (type2 instanceof ParameterizedType) {
					reqClass = (Class) ((ParameterizedType) type2).getRawType();
				}
				if (type3 instanceof Class) {
					keyReqClass = (Class) type3;
				} else if (type3 instanceof ParameterizedType) {
					keyReqClass = (Class) ((ParameterizedType) type3).getRawType();
				}
				Object convertedValue = doTypeConversionIfNecessary(propertyName, propertyName, oldValue, newValue, reqClass);
				Object convertedKey = doTypeConversionIfNecessary(key, keyReqClass);
				map.put(convertedKey, convertedValue);
			} else {
				throw new InvalidPropertyException(getRootClass(), this.nestedPath + propertyName,
						"Property referenced in indexed property path '" + propertyName +
								"' is neither an array nor a List nor a Map; returned value was [" + newValue + "]");
			}
		} else {
			PropertyDescriptor pd = getPropertyDescriptorInternal(propertyName);
			if (pd == null || pd.getWriteMethod() == null) {
				throw new NotWritablePropertyException(getRootClass(), this.nestedPath + propertyName);
			}
			Method readMethod = pd.getReadMethod();
			Method writeMethod = pd.getWriteMethod();
			Object oldValue = null;
			if (this.extractOldValueForEditor && readMethod != null) {
				try {
					oldValue = readMethod.invoke(this.object, new Object[0]);
				} catch (Exception ex) {
					logger.debug("Could not read previous value of property '" + this.nestedPath + propertyName, ex);
				}
			}
			try {
				Object convertedValue = doTypeConversionIfNecessary(propertyName, propertyName, oldValue, newValue, pd.getPropertyType());
				if (pd.getPropertyType().isPrimitive() && (convertedValue == null || "".equals(convertedValue))) {
					throw new IllegalArgumentException("Invalid value [" + newValue + "] for property '" +
							pd.getName() + "' of primitive type [" + pd.getPropertyType() + "]");
				}
				if (logger.isDebugEnabled()) {
					logger.debug("About to invoke write method [" + writeMethod + "] on object of class [" +
							this.object.getClass().getName() + "]");
				}
				writeMethod.invoke(this.object, new Object[] { convertedValue });
				if (logger.isDebugEnabled()) {
					logger.debug("Invoked write method [" + writeMethod + "] with value of type [" +
							pd.getPropertyType().getName() + "]");
				}
			} catch (InvocationTargetException ex) {
				PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(this.rootObject, this.nestedPath + propertyName, oldValue, newValue);
				if (ex.getTargetException() instanceof ClassCastException) {
					throw new TypeMismatchException(propertyChangeEvent, pd.getPropertyType(), ex.getTargetException());
				} else {
					throw new MethodInvocationException(propertyChangeEvent, ex.getTargetException());
				}
			} catch (IllegalArgumentException ex) {
				PropertyChangeEvent pce = new PropertyChangeEvent(this.rootObject, this.nestedPath + propertyName, oldValue, newValue);
				throw new TypeMismatchException(pce, pd.getPropertyType(), ex);
			} catch (IllegalAccessException ex) {
				PropertyChangeEvent pce = new PropertyChangeEvent(this.rootObject, this.nestedPath + propertyName, oldValue, newValue);
				throw new MethodInvocationException(pce, ex);
			}
		}
	}

	private Type getPropertyType(PropertyTokenHolder tokens) {
		String propertyName = tokens.canonicalName;
		PropertyDescriptor pd = getPropertyDescriptorInternal(tokens.actualName);
		if (pd == null || pd.getReadMethod() == null) {
			throw new NotReadablePropertyException(getRootClass(), this.nestedPath + propertyName);
		}
		if (logger.isDebugEnabled())
			logger.debug("About to invoke read method [" + pd.getReadMethod() + "] on object of class [" +
					this.object.getClass().getName() + "]");
		try {
			Type type = pd.getReadMethod().getGenericReturnType();
			if (tokens.keys != null) {
				// apply indexes and map keys
				for (int i = 0; i < tokens.keys.length; i++) {
					if (type == null) {
						throw new NullValueInNestedPathException(getRootClass(), this.nestedPath + propertyName,
								"Cannot access indexed value of property referenced in indexed " +
										"property path '" + propertyName + "': returned null");
					} else if (type instanceof ParameterizedType) {
						ParameterizedType parameterizedType = (ParameterizedType) type;
						Class clazz = (Class) parameterizedType.getRawType();
						if (Map.class.isAssignableFrom(clazz)) {
							type = parameterizedType.getActualTypeArguments()[1];
						} else if (Collection.class.isAssignableFrom(clazz)) {
							type = parameterizedType.getActualTypeArguments()[0];
						} else {
							throw new RuntimeException("Tipo desconhecido " + parameterizedType);
						}
					} else if (type instanceof Class && ((Class) type).isArray()) {
						return type;
					} else {
						throw new RuntimeException("Implementar conversão!!!");
					}
				}
			}
			return type;
		} catch (IndexOutOfBoundsException ex) {
			throw new InvalidPropertyException(getRootClass(), this.nestedPath + propertyName,
					"Index of out of bounds in property path '" + propertyName + "'", ex);
		} catch (NumberFormatException ex) {
			throw new InvalidPropertyException(getRootClass(), this.nestedPath + propertyName,
					"Invalid index in property path '" + propertyName + "'", ex);
		}
	}

	public void setPropertyValue(PropertyValue pv) throws BeansException {
		setPropertyValue(pv.getName(), pv.getValue());
	}

	/**
	 * Bulk update from a Map.
	 * Bulk updates from PropertyValues are more powerful: this method is
	 * provided for convenience.
	 * @param map map containing properties to set, as name-value pairs.
	 * The map may include nested properties.
	 * @throws BeansException if there's a fatal, low-level exception
	 */
	@Override
	public void setPropertyValues(Map<?, ?> map) throws BeansException {
		setPropertyValues(new MutablePropertyValues(map));
	}

	public void setPropertyValues(PropertyValues pvs) throws BeansException {
		setPropertyValues(pvs, false);
	}

	public void setPropertyValues(PropertyValues propertyValues, boolean ignoreUnknown) throws BeansException {
		List propertyAccessExceptions = new ArrayList();
		PropertyValue[] pvs = propertyValues.getPropertyValues();
		for (int i = 0; i < pvs.length; i++) {
			try {
				// This method may throw any BeansException, which won't be caught
				// here, if there is a critical failure such as no matching field.
				// We can attempt to deal only with less serious exceptions.
				setPropertyValue(pvs[i]);
			} catch (NotWritablePropertyException ex) {
				if (!ignoreUnknown) {
					throw ex;
				}
				// Otherwise, just ignore it and continue...
			} catch (PropertyAccessException ex) {
				propertyAccessExceptions.add(ex);
			}
		}
		// If we encountered individual exceptions, throw the composite exception.
		if (!propertyAccessExceptions.isEmpty()) {
			Object[] paeArray = propertyAccessExceptions.toArray(new PropertyAccessException[propertyAccessExceptions.size()]);
			throw new PropertyBatchUpdateException((PropertyAccessException[]) paeArray);
		}
	}

	private PropertyChangeEvent createPropertyChangeEvent(String propertyName, Object oldValue, Object newValue) {
		return new PropertyChangeEvent((this.rootObject != null ? this.rootObject : "constructor"),
				(propertyName != null ? this.nestedPath + propertyName : null),
				oldValue, newValue);
	}

	/**
	 * Convert the value to the required type (if necessary from a String).
	 * <p>Conversions from String to any type use the <code>setAsText</code> method
	 * of the PropertyEditor class. Note that a PropertyEditor must be registered
	 * for the given class for this to work; this is a standard JavaBeans API.
	 * A number of PropertyEditors are automatically registered by BeanWrapperImpl.
	 * @param newValue proposed change value
	 * @param requiredType the type we must convert to
	 * @return the new value, possibly the result of type conversion
	 * @throws TypeMismatchException if type conversion failed
	 * @see java.beans.PropertyEditor#setAsText(String)
	 * @see java.beans.PropertyEditor#getValue()
	 */
	public Object doTypeConversionIfNecessary(Object newValue, Class requiredType) throws TypeMismatchException {
		return doTypeConversionIfNecessary(null, null, null, newValue, requiredType);
	}

	/**
	 * Convert the value to the required type (if necessary from a String),
	 * for the specified property.
	 * @param propertyName name of the property
	 * @param oldValue previous value, if available (may be <code>null</code>)
	 * @param newValue proposed change value
	 * @param requiredType the type we must convert to
	 * (or <code>null</code> if not known, for example in case of a collection element)
	 * @return the new value, possibly the result of type conversion
	 * @throws TypeMismatchException if type conversion failed
	 */
	protected Object doTypeConversionIfNecessary(String propertyName, String fullPropertyName,
			Object oldValue, Object newValue, Class requiredType) throws TypeMismatchException {

		Object convertedValue = newValue;
		if (convertedValue != null) {

			// Custom editor for this type?
			PropertyEditor pe = findCustomEditor(requiredType, fullPropertyName);

			// Value not of required type?
			if (pe != null ||
					(requiredType != null &&
							(requiredType.isArray() || !requiredType.isAssignableFrom(convertedValue.getClass())))) {

				if (requiredType != null) {
					if (pe == null) {
						// No custom editor -> check BeanWrapperImpl's default editors.
						pe = (PropertyEditor) this.defaultEditors.get(requiredType);
						if (pe == null) {
							// No BeanWrapper default editor -> check standard JavaBean editors.
							pe = PropertyEditorManager.findEditor(requiredType);
						}
					}
				}

				if (pe != null && !(convertedValue instanceof String)) {
					// Not a String -> use PropertyEditor's setValue.
					// With standard PropertyEditors, this will return the very same object;
					// we just want to allow special PropertyEditors to override setValue
					// for type conversion from non-String values to the required type.
					try {
						pe.setValue(convertedValue);
						Object newConvertedValue = pe.getValue();
						if (newConvertedValue != convertedValue) {
							convertedValue = newConvertedValue;
							// Reset PropertyEditor: It already did a proper conversion.
							// Don't use it again for a setAsText call.
							pe = null;
						}
					} catch (IllegalArgumentException ex) {
						throw new TypeMismatchException(
								createPropertyChangeEvent(fullPropertyName, oldValue, newValue), requiredType, ex);
					}
				}

				if (requiredType != null && !requiredType.isArray() && convertedValue instanceof String[]) {
					// Convert String array to a comma-separated String.
					// Only applies if no PropertyEditor converted the String array before.
					// The CSV String will be passed into a PropertyEditor's setAsText method, if any.
					if (logger.isDebugEnabled()) {
						logger.debug("Converting String array to comma-delimited String [" + convertedValue + "]");
					}
					convertedValue = StringUtils.arrayToCommaDelimitedString((String[]) convertedValue);
				}

				if (pe != null && convertedValue instanceof String) {
					// Use PropertyEditor's setAsText in case of a String value.
					if (logger.isDebugEnabled()) {
						logger.debug("Converting String to [" + requiredType + "] using property editor [" + pe + "]");
					}
					try {
						pe.setValue(oldValue);
						pe.setAsText((String) convertedValue);
						convertedValue = pe.getValue();
					} catch (IllegalArgumentException ex) {
						throw new TypeMismatchException(
								createPropertyChangeEvent(fullPropertyName, oldValue, newValue), requiredType, ex);
					}
				}

				if (requiredType != null) {
					// Array required -> apply appropriate conversion of elements.
					if (requiredType.isArray()) {
						Class componentType = requiredType.getComponentType();
						if (convertedValue instanceof Collection) {
							// Convert Collection elements to array elements.
							Collection coll = (Collection) convertedValue;
							Object result = Array.newInstance(componentType, coll.size());
							int i = 0;
							for (Iterator it = coll.iterator(); it.hasNext(); i++) {
								Object value = doTypeConversionIfNecessary(
										propertyName, propertyName + PROPERTY_KEY_PREFIX + i + PROPERTY_KEY_SUFFIX,
										null, it.next(), componentType);
								Array.set(result, i, value);
							}
							return result;
						} else if (convertedValue != null && convertedValue.getClass().isArray()) {
							// Convert Collection elements to array elements.
							int arrayLength = Array.getLength(convertedValue);
							Object result = Array.newInstance(componentType, arrayLength);
							for (int i = 0; i < arrayLength; i++) {
								Object value = doTypeConversionIfNecessary(
										propertyName, propertyName + PROPERTY_KEY_PREFIX + i + PROPERTY_KEY_SUFFIX,
										null, Array.get(convertedValue, i), componentType);
								Array.set(result, i, value);
							}
							return result;
						} else {
							// A plain value: convert it to an array with a single component.
							Object result = Array.newInstance(componentType, 1);
							Object value = doTypeConversionIfNecessary(
									propertyName, propertyName + PROPERTY_KEY_PREFIX + 0 + PROPERTY_KEY_SUFFIX,
									null, convertedValue, componentType);
							Array.set(result, 0, value);
							return result;
						}
					}

					// If the resulting value definitely doesn't match the required type,
					// try field lookup as fallback. If no matching field found,
					// throw explicit TypeMismatchException with full context information.
					if (convertedValue != null && !requiredType.isPrimitive() &&
							!requiredType.isAssignableFrom(convertedValue.getClass())) {

						// In case of String value, try to find matching field (for JDK 1.5
						// enum or custom enum with values defined as static fields).
						if (convertedValue instanceof String) {
							try {
								Field enumField = requiredType.getField((String) convertedValue);
								return enumField.get(null);
							} catch (Exception ex) {
								logger.debug("Field [" + convertedValue + "] isn't an enum value", ex);
							}
						}

						// Definitely doesn't match: throw TypeMismatchException.
						throw new TypeMismatchException(
								createPropertyChangeEvent(fullPropertyName, oldValue, newValue), requiredType);
					}
				}

			}

		}

		if (fullPropertyName != null && requiredType != null && List.class.isAssignableFrom(requiredType)) { //treat conventions of enum lists
			Type genericReturnType = getPropertyDescriptorInternal(fullPropertyName).getReadMethod().getGenericReturnType();
			if (genericReturnType instanceof ParameterizedType) {
				Type actualType = ((ParameterizedType) genericReturnType).getActualTypeArguments()[0];
				if (actualType instanceof Class && convertedValue != null) {
					List list = (List) convertedValue;
					for (int i = 0; i < list.size(); i++) {
						Object o = list.remove(i);
						o = doTypeConversionIfNecessary(o, (Class) actualType);
						list.add(i, o);
					}
				}
			}
		}

		return convertedValue;
	}

	public PropertyDescriptor[] getPropertyDescriptors() {
		return this.cachedIntrospectionResults.getBeanInfo().getPropertyDescriptors();
	}

	public PropertyDescriptor getPropertyDescriptor(String propertyName) throws BeansException {
		if (propertyName == null) {
			throw new IllegalArgumentException("Can't find property descriptor for <code>null</code> property");
		}
		PropertyDescriptor pd = getPropertyDescriptorInternal(propertyName);
		if (pd != null) {
			return pd;
		} else {
			throw new InvalidPropertyException(getRootClass(), this.nestedPath + propertyName,
					"No property '" + propertyName + "' found");
		}
	}

	/**
	 * Internal version of getPropertyDescriptor:
	 * Returns null if not found rather than throwing an exception.
	 */
	protected PropertyDescriptor getPropertyDescriptorInternal(String propertyName) throws BeansException {
		Assert.state(this.object != null, "BeanWrapper does not hold a bean instance");
		ExtendedBeanWrapper nestedBw = getBeanWrapperForPropertyPath(propertyName);
		return nestedBw.cachedIntrospectionResults.getPropertyDescriptor(getFinalPath(nestedBw, propertyName));
	}

	public Class getPropertyType(String propertyName) throws BeansException {
		try {
			PropertyDescriptor pd = getPropertyDescriptorInternal(propertyName);
			if (pd != null) {
				return pd.getPropertyType();
			} else {
				// Maybe an indexed/mapped property...
				Object value = getPropertyValue(propertyName);
				if (value != null) {
					return value.getClass();
				}
				// Check to see if there is a custom editor,
				// which might give an indication on the desired target type.
				if (this.customEditors != null) {
					CustomEditorHolder editorHolder = (CustomEditorHolder) this.customEditors.get(propertyName);
					if (editorHolder == null) {
						List strippedPaths = new LinkedList();
						addStrippedPropertyPaths(strippedPaths, "", propertyName);
						for (Iterator it = strippedPaths.iterator(); it.hasNext() && editorHolder == null;) {
							String strippedName = (String) it.next();
							editorHolder = (CustomEditorHolder) this.customEditors.get(strippedName);
						}
					}
					if (editorHolder != null) {
						return editorHolder.getRegisteredType();
					}
				}
			}
		} catch (InvalidPropertyException ex) {
			// Consider as not determinable.
		}
		return null;
	}

	public boolean isReadableProperty(String propertyName) {
		// This is a programming error, although asking for a property
		// that doesn't exist is not.
		if (propertyName == null) {
			throw new IllegalArgumentException("Can't find readability status for <code>null</code> property");
		}
		try {
			PropertyDescriptor pd = getPropertyDescriptorInternal(propertyName);
			if (pd != null) {
				if (pd.getReadMethod() != null) {
					return true;
				}
			} else {
				// maybe an indexed/mapped property
				getPropertyValue(propertyName);
				return true;
			}
		} catch (InvalidPropertyException ex) {
			// cannot be evaluated, so can't be readable
		}
		return false;
	}

	public boolean isWritableProperty(String propertyName) {
		// This is a programming error, although asking for a property
		// that doesn't exist is not.
		if (propertyName == null) {
			throw new IllegalArgumentException("Can't find writability status for <code>null</code> property");
		}
		try {
			PropertyDescriptor pd = getPropertyDescriptorInternal(propertyName);
			if (pd != null) {
				if (pd.getWriteMethod() != null) {
					return true;
				}
			} else {
				// maybe an indexed/mapped property
				getPropertyValue(propertyName);
				return true;
			}
		} catch (InvalidPropertyException ex) {
			// cannot be evaluated, so can't be writable
		}
		return false;
	}

	//---------------------------------------------------------------------
	// Diagnostics
	//---------------------------------------------------------------------

	public String toString() {
		StringBuffer sb = new StringBuffer("BeanWrapperImpl: wrapping class [");
		sb.append(getWrappedClass().getName()).append("]");
		return sb.toString();
	}

	/**
	 * Holder for a registered custom editor with property name.
	 * Keeps the PropertyEditor itself plus the type it was registered for.
	 */
	private static class CustomEditorHolder {

		private final PropertyEditor propertyEditor;

		private final Class registeredType;

		private CustomEditorHolder(PropertyEditor propertyEditor, Class registeredType) {
			this.propertyEditor = propertyEditor;
			this.registeredType = registeredType;
		}

		private PropertyEditor getPropertyEditor() {
			return propertyEditor;
		}

		private Class getRegisteredType() {
			return registeredType;
		}

		private PropertyEditor getPropertyEditor(Class requiredType) {
			// Special case: If no required type specified, which usually only happens for
			// Collection elements, or required type is not assignable to registered type,
			// which usually only happens for generic properties of type Object -
			// then return PropertyEditor if not registered for Collection or array type.
			// (If not registered for Collection or array, it is assumed to be intended
			// for elements.)
			/*
			if (this.registeredType == null ||
					(requiredType != null &&
					(BeanUtils.isAssignable(this.registeredType, requiredType) ||
					BeanUtils.isAssignable(requiredType, this.registeredType))) ||
					(requiredType == null &&
					(!Collection.class.isAssignableFrom(this.registeredType) && !this.registeredType.isArray()))) {
				return this.propertyEditor;
			}
			else {
				return null;
			}
			*/
			return null;
		}

	}

	private static class PropertyTokenHolder {

		private String canonicalName;

		private String actualName;

		private String[] keys;

	}

	@Override
	public <T> T convertIfNecessary(Object value, Class<T> requiredType, MethodParameter methodParam) throws TypeMismatchException {
		return (T) doTypeConversionIfNecessary(value, requiredType);
	}

	public boolean isExtractOldValueForEditor() {
		return false;
	}

	public void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown, boolean ignoreInvalid) throws BeansException {
		setPropertyValues(pvs, ignoreUnknown);
	}

	@Override
	public <T> T convertIfNecessary(Object value, Class<T> requiredType) throws TypeMismatchException {
		return (T) doTypeConversionIfNecessary(value, requiredType);
	}

	public boolean isAutoGrowNestedPaths() {
		return true;
	}

	public void setAutoGrowNestedPaths(boolean autoGrowNestedPaths) {
		//o extended bean wrapper sempre utiliza o autoGrowNestedPaths
	}

	public ConversionService getConversionService() {
		return null;
	}

	public void setConversionService(ConversionService conversionService) {
		throw new NextException("O ExtendedBeanWrapper não suporta ConversionService");
	}

	public TypeDescriptor getPropertyTypeDescriptor(String propertyName) throws BeansException {
		throw new NextException("Not supported");
	}

	@Override
	public int getAutoGrowCollectionLimit() {
		return Integer.MAX_VALUE;
	}

	@Override
	public void setAutoGrowCollectionLimit(int autoGrowCollectionLimit) {

	}

	@Override
	public <T> T convertIfNecessary(Object value, Class<T> requiredType, Field field) throws TypeMismatchException {
		return doConvert(value, requiredType, null, field);
	}

	private <T> T doConvert(Object value, Class<T> requiredType, MethodParameter methodParam, Field field)
			throws TypeMismatchException {
		try {
			if (field != null) {
				return (T) doTypeConversionIfNecessary(value, requiredType);
			} else {
				return (T) doTypeConversionIfNecessary(value, requiredType);
			}
		} catch (ConverterNotFoundException ex) {
			throw new ConversionNotSupportedException(value, requiredType, ex);
		} catch (ConversionException ex) {
			throw new TypeMismatchException(value, requiredType, ex);
		} catch (IllegalStateException ex) {
			throw new ConversionNotSupportedException(value, requiredType, ex);
		} catch (IllegalArgumentException ex) {
			throw new TypeMismatchException(value, requiredType, ex);
		}
	}

}