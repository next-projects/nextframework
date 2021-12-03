package org.nextframework.view.components;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.nextframework.exception.NextException;
import org.nextframework.util.Util;
import org.nextframework.view.BeanTag;
import org.nextframework.view.ComboReloadGroupTag;
import org.nextframework.view.InputTag;
import org.nextframework.view.util.FunctionCall;

public class InputTagSelectComponent extends InputTagComponent {

	@Override
	public void validateTag() {
		if (Util.strings.isNotEmpty(inputTag.getOnLoadItens()) && !inputTag.getUseAjax()) {
			throw new NextException("Não é possível utilizar onLoadItens se useAjax não for true");
		}
	}
	
	@Override
	public void prepare() {
		super.prepare();
		prepareBlankOption();

		ComboReloadGroupTag comboReload = getComboReload();
		InputTag lastInput = null;
		if (comboReload != null) {
			lastInput = prepareComboReload(comboReload);
		}

		prepareItems(lastInput);
	}

	protected void prepareItems(InputTag lastInput) {
	}

	protected ComboReloadGroupTag getComboReload() {
		return inputTag.findParent(ComboReloadGroupTag.class);
	}

	protected InputTag prepareComboReload(ComboReloadGroupTag comboReload) {
		return null;
	}

	protected HashSet<String> getOptionalParametersSet() {
		String[] split = inputTag.getOptionalParams().split("( )*?,( )*?");
		for (int i = 0; i < split.length; i++) {
			split[i] = split[i].trim();
		}
		HashSet<String> optionalParametersSet = new HashSet<String>(Arrays.asList(split));
		return optionalParametersSet;
	}
	
	protected boolean isSelectAll() {
		return inputTag.getItens() instanceof String && "all".equalsIgnoreCase((String) inputTag.getItens());
	}

	private boolean cacheFunctionCallExecuted = false;
	private FunctionCall cacheFunctionCall = null;
	protected FunctionCall getFunctionCall() {
		if(!cacheFunctionCallExecuted){
			String itensFunction = getItensFunction();
			if (itensFunction != null) {
				BeanTag beanTag = inputTag.findParent(BeanTag.class);
				if (beanTag == null && inputTag.getUseAjax()) {
					throw new RuntimeException("A tag input deve estar aninhada a uma tag bean para poder utilizar useAjax=true");
				} else {
					if (beanTag != null && inputTag.getUseAjax() && !"all".equalsIgnoreCase(itensFunction)) { 
						cacheFunctionCall = new FunctionCall(itensFunction, beanTag.getBeanDescriptor());
					}
				}
			}
			cacheFunctionCallExecuted = true;
		}
		return cacheFunctionCall;
	}

	private boolean cacheItensFunctionExecuted = false;
	private String cacheItensFunction = null;
	protected String getItensFunction() {
		if(!cacheItensFunctionExecuted){
			Object inputTagItems = inputTag.getItens();
			cacheItensFunction = null;
			if(inputTagItems instanceof String){
				String itensFunction = (String) inputTagItems;
				cacheItensFunction = itensFunction.trim();
			}
			cacheItensFunctionExecuted = true;
		}
		return cacheItensFunction;
	}
	
	
	
	protected String createOption(String value, String label, boolean selected) {
		return createOption(value, label, (selected ? "selected=\"selected\"" : ""));
	}

	protected String createOption(String value, String label, String selected) {
		return "<option value='" + (value != null ? value : "<null>") + "' " + selected + ">" + label + "</option>";
	}

	protected Iterable<?> getObjectAsIterable(Object value) {
		if(value instanceof Iterable<?>){
			value = (Iterable<?>) value;
		} else if(value != null && value.getClass().isArray()){
			value = Arrays.asList((Object[])value);
		} else if(value != null){
			value = Arrays.asList(value);
		}
		return (Iterable<?>) value;
	}
	
	private Class<?> cacheRawClassType = null; 
	public Class<?> getRawClassType() {
		if(cacheRawClassType == null){
			cacheRawClassType = getRawClassType(inputTag.getType(), inputTag.getItens(), inputTag.getAutowiredType()); 
		}
		return cacheRawClassType; 
	}

	protected Class<?> getRawClassType(Object type, Object items, Object autowiredtype) {
		if(type instanceof String){
			type = autowiredtype;
		}
		if(type == null){ //type is null or String and autowired is null
			//read attribute
			type = inputTag.getDAAtribute("useType", true);
			if(type != null){
				if(type instanceof String){
					try {
						type = Class.forName((String) type);
					} catch (ClassNotFoundException e) {
						throw new NextException("Cannot convert "+type+" to class", e);
					}
				}
			}
		}
		if(type == null){
			throw new NextException("Cannot determine type for input"+(inputTag.getName() != null?" with name '"+inputTag.getName()+"'":"")+". Set useType attribute with a value of type java.lang.Class");
		}
		Class<?> class1 = getRawType(type, autowiredtype);
		if (class1 == null) {
			// verificar pelo itens
			if (items instanceof Collection<?>) {
				if (((Collection<?>) items).size() > 0) {
					Object o = ((Collection<?>) items).iterator().next();
					if (o != null) {
						return o.getClass();
					}
				}
			}
		}
		return class1;
	}

	@SuppressWarnings("unchecked")
	private Class<? extends Object> getRawType(Object type, Object autowiredtype) {
		if(type instanceof Class){
			return (Class<? extends Object>) type;
		} else {
			if(type instanceof ParameterizedType){
				ParameterizedType pt = (ParameterizedType) type;
				type = pt.getActualTypeArguments()[0]; //first item of the collection
				if(type instanceof Class){
					return (Class<? extends Object>) type;
				} else {
					return (Class<? extends Object>) ((ParameterizedType)type).getRawType();
				}
			} else {
				return (Class<? extends Object>) autowiredtype;
			}
		}
	}

	protected void prepareBlankOption() {
		if(isIncludeBlank()){
			String value = "<null>";
			String label = inputTag.getBlankLabel();
			inputTag.setSelectoneblankoption(createOption(value, label, inputTag.getValue() == null));
		}
	}


	public boolean isIncludeBlank() {
		return Util.booleans.isTrue(inputTag.getIncludeBlank());
	}
	
	public static String getDynamicProperty(String form, String property, String currentIndexVar) {
		String code;
		if(property.contains("[")){
			String open = property.substring(0, property.indexOf('[')+1);
			String close = property.substring(property.indexOf(']'));
			code = form+"['"+open+"'+ "+currentIndexVar+" +'"+close+"']";
		} else {
			code = form+"['"+property+"']";	
		}
		return code;
	}

	public void afterPrint(InputTag lastInput) {
		
	}
}
