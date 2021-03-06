package org.nextframework.view.components;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.nextframework.authorization.User;
import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.exception.NextException;
import org.nextframework.persistence.DAOUtils;
import org.nextframework.persistence.GenericDAO;
import org.nextframework.util.Util;
import org.nextframework.view.BeanTag;
import org.nextframework.view.ComboReloadGroupTag;
import org.nextframework.view.FormTag;
import org.nextframework.view.InputTag;
import org.nextframework.view.InputTagType;
import org.nextframework.view.TagUtils;
import org.nextframework.view.ajax.ComboCallback;
import org.nextframework.view.util.FunctionCall;
import org.nextframework.view.util.FunctionParameter;
import org.nextframework.view.util.ParameterType;
import org.nextframework.web.WebContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class InputTagSelectComboComponent extends InputTagSelectComponent {

	
	private String loadItensFunction;
	
	String selectItensString;
	
	public String getSelectItensString() {
		return selectItensString;
	}
	
	public void setSelectItensString(String selectItensString) {
		this.selectItensString = selectItensString;
	}

	@Override
	public void prepare() {
		super.prepare();
	}
	
	@Override
	public void afterPrint() {
		super.afterPrint();
		if (Util.strings.isNotEmpty(loadItensFunction)) {
			try {
				inputTag.getOut().println("<script language='javascript'>");
				inputTag.getOut().println(loadItensFunction);
				inputTag.getOut().println("</script>");
			} catch (IOException e) {
				throw new NextException(e);
			}
		}
	}
	
	@Override
	protected void prepareItems(InputTag lastInput) {
		Object itemsValue = getItemsValue(lastInput);
		
		setSelectItensString(toString(organizeItens(inputTag, inputTag.getValue(), itemsValue)));
	}
	
	protected String toString(List<String> organizeItens) {
		StringBuilder builder = new StringBuilder();
		for (String string : organizeItens) {
			builder.append(string);
			builder.append("\n");
		}
		return builder.toString();
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Object getItemsValue(InputTag lastInput) {
		Object itemsValue;
		
		if (inputTag.getItens() instanceof String) { //user defined function
			if (inputTag.getUseAjax() == null || inputTag.getUseAjax() == false) {
				String expression = (String) inputTag.getItens();
				CacheControl cacheControl = null;
				Object value = null;
				if(isCacheable()){
					//try cache
					cacheControl = CacheControl.get(inputTag.getRequest());
					value = cacheControl.getCache().get(expression);
				}
				if(value == null){
					value = inputTag.getOgnlValue(expression);
					if(cacheControl != null){
						cacheControl.getCache().put(expression, value);
					}
				}
				//organizeItens = organizeItens(inputTag, value);
				itemsValue = value;
			} else {
				FunctionCall call = getFunctionCall();
				// String expression = (String) itens;
				if (call  == null) {
					throw new NullPointerException("Resultado inesperado. call nulo. O algoritmo do framework n�o est� correto");
				}
				String functionName = call.getFunctionName();
				Object bean = WebApplicationContextUtils.getRequiredWebApplicationContext(WebContext.getServletContext()).getBean(call.getObject());
				Class<?>[] classes = null;
				Object[] values = null;
				boolean ignorecall = false;
				FunctionParameter[] callParameterArray = call.getParameterArray();
				if (callParameterArray.length == 0 && !call.getCall().endsWith("()")) {
					if (lastInput.getValue() == null) {
						//organizeItens = new ArrayList<String>();
						itemsValue = new ArrayList<Object>();
						values = new Object[0];
						ignorecall = true;
					} else {
						classes = new Class[] { lastInput.getValue().getClass() };
						values = new Object[] { lastInput.getValue() };
					}
				} else {
					classes = new Class[callParameterArray.length];
					values = new Object[callParameterArray.length];
				}
				for (int i = 0; i < callParameterArray.length; i++) {
					classes[i] = call.getCallInfo().getType(callParameterArray[i]);
					values[i] = call.getCallInfo().getValue(callParameterArray[i]);
				}
				boolean anynull = false;
				HashSet<String> optionalParametersSet = getOptionalParametersSet();
				int i = 0;
				for (Object object : values) {
					if (!optionalParametersSet.contains(callParameterArray[i].getParameterValue())) {
						// se for obrigatorio e for null n�o fazer a chamada
						if (object == null) {
							anynull = true;
							break;
						}
					}
					i++;
				}

				// registrar a chamada
				ComboCallback.register(call.getObject(), call.getFunctionName(), classes);

				ignorecall = (ignorecall || anynull) && (callParameterArray.length != 0);
				if (!ignorecall) {
					Object lista = null;
					CacheControl cacheControl = null;
					if(classes.length == 0){
						//try cache
						cacheControl = CacheControl.get(inputTag.getRequest());
						lista = cacheControl.getCache().get(inputTag.getItens());
					}
					if(lista == null){
						lista = Util.objects.findAndInvokeMethod(bean, functionName, values, classes);
						if (!(lista instanceof List)) {
							throw new RuntimeException("O retorno do m�todo " + functionName + " n�o foi uma lista");
						}
						if(cacheControl != null){
							cacheControl.getCache().put((String) inputTag.getItens(), lista);
						}
					}
					//organizeItens = organizeItens(inputTag, (List<String>) lista);
					itemsValue = lista;
				} else {
					//organizeItens = new ArrayList<String>();
					itemsValue = new ArrayList<Object>();
				}

			}
		} else if (inputTag.getItens() != null) {
			//organizeItens = organizeItens(inputTag, inputTag.getItens());
			itemsValue = inputTag.getItens();
		} else {
			if (Enum.class.isAssignableFrom(getRawClassType())) {
				Method method;
				try {
					method = getRawClassType().getMethod("values");
					Enum<?>[] enumValues = (Enum[]) method.invoke(null);
					Map<Object, Object> values = new LinkedHashMap<Object, Object>();
					for (Enum<?> enumValue : enumValues) {
						values.put(Enum.valueOf((Class)getRawClassType(), enumValue.name()), enumValue.toString());
					}
					//organizeItens = organizeItens(inputTag, values);
					itemsValue = values;
				} catch (Exception e) {
					throw new NextException(e);
				}
			} else {
				//organizeItens = organizeItens(inputTag, doSelectAllFromService(lastInput, getRawClassType()));
				itemsValue = doSelectAllFromService(lastInput, getRawClassType());
			}
		}
		
		if (Util.strings.isNotEmpty(inputTag.getTrueFalseNullLabels())) {
//			if (selectedType == InputTagType.CHECKBOX) {
//				selectedType = InputTagType.SELECT_ONE;
//			}
			String[] split = inputTag.getTrueFalseNullLabels().split(",");
			Map<Boolean, String> mapa = new LinkedHashMap<Boolean, String>();
			if (split.length == 3) {
				inputTag.setSelectoneblankoption(createOption("<null>", split[2], inputTag.getValue() == null));
			} else if (isIncludeBlank()) {
				inputTag.setSelectoneblankoption(createOption("<null>", inputTag.getBlankLabel(), inputTag.getValue() == null));
			}
			mapa.put(Boolean.TRUE, split[0]);
			mapa.put(Boolean.FALSE, split[1]);
			//setSelectItensString(toString(organizeItens(inputTag, mapa)));
			itemsValue = mapa;
		}
		
		return itemsValue;
	}
	
	private boolean isCacheable() {
		if(inputTag.getItens() instanceof String){
			String itensCall = (String) inputTag.getItens();
			if(itensCall.endsWith("()")){
				int indexOfPoint = itensCall.indexOf('.');
				if(indexOfPoint > 0 && itensCall.indexOf('.', indexOfPoint+1) < 0){
					//has the following format bean.method()
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Monta a lista de itens para ser colocado no combo (cada posicao do array
	 * � um option ou um optgroup)
	 * @param input 
	 * @param itens
	 * @return
	 */
	@SuppressWarnings("all")
	protected List<String> organizeItens(InputTag input, Object value, Object itens) {
		boolean sugest = false;
		boolean first = true;
		if (Util.booleans.isTrue(input.getAutoSugestUniqueItem())) {
			if (value == null) {
				sugest = true;
			}
		}
		List<String> valores = new ArrayList<String>();
		if (itens == null) {
			return valores;
		}
		if(itens.getClass().isArray()){
			itens = Arrays.asList((Object[])itens);
		}
		if (itens instanceof Map) {
			Map map = (Map) itens;
			Set keySet = map.keySet();
			sugest = sugest && keySet.size() == 1;
			for (Object key : keySet) {
				Object mapValue = map.get(key);
				if (mapValue instanceof Collection || mapValue instanceof Map || mapValue.getClass().isArray()) {
					if (selectedType != InputTagType.SELECT_ONE_RADIO)
						valores.add("<optgroup label=\"" + key + "\">");
					valores.addAll(organizeItens(input, value, mapValue));
					if (selectedType != InputTagType.SELECT_ONE_RADIO)
						valores.add("</optgroup>");
				} else {
					setSelectedValueIfNeeded(input, value, sugest, first, key);
					String opDesc = getSelectLabel(mapValue);
					String opValue = getSelectVaue(key);
					opValue = TagUtils.escapeSingleQuotes(opValue);
					String selected = getSelectedString(key);
					valores.add(createOption(opValue, opDesc, selected)); // "<option
					// value='"+opValue+"'"+selected+">"+opDesc+"</option>")
				}
				first = false;
			}
		} else if (itens instanceof Collection) {
			Collection collection = (Collection) itens;
			sugest = collection.size() == 1 && sugest;
			for (Object object : (Iterable)itens) {
				setSelectedValueIfNeeded(input, value, sugest, first, object);
				String opDesc = getSelectLabel(object);
				String opValue = getSelectVaue(object);
				opValue = TagUtils.escapeSingleQuotes(opValue);
				String selected = getSelectedString(object);
				valores.add(createOption(opValue, opDesc, selected));
				first = false;
			}
		}
		return valores;
	}

	protected void setSelectedValueIfNeeded(InputTag input, Object value, boolean sugest, boolean first, Object object) {
		if (sugest || (first && value == null && !isIncludeBlank() && !isSelectMany())) {
			input.setValue(object); 
			try {
				input.getPropertySetter().set(object);
			} catch (Exception e) {
				throw new RuntimeException("Could not set value for property "+input.getName()+", using autoSelectUniqueItem. Check if bean is null.");
			}
		}
	}

	protected boolean isSelectMany() {
		return selectedType == InputTagType.SELECT_MANY || 
				selectedType == InputTagType.SELECT_MANY_POPUP || 
				selectedType == InputTagType.SELECT_MANY_BOX;
	}

	protected String getSelectVaue(Object key) {
		return TagUtils.getObjectValueToString(key, false, null);
	}
	
	private String getSelectedString(Object optionValue) {
		return isValueSelected(optionValue)? " selected='selected'" : "";
	}

	private String getSelectLabel(Object value) {
		if (inputTag.getSelectLabelProperty() != null && inputTag.getSelectLabelProperty().trim().length() != 0 && value != null) {
			return TagUtils.getObjectDescriptionToString(BeanDescriptorFactory.forBean(value).getPropertyDescriptor(inputTag.getSelectLabelProperty()).getValue());
		} else {
			return TagUtils.getObjectDescriptionToString(value);
		}
	}
	
	protected boolean isValueSelected(Object optionValue){
		Iterable<?> value = getValueAsIterable();
		if(value != null){
			for(Object object: value){
				if(inputTag.areEqual(optionValue, object)){
					return true;
				}
			}
		}
		return false;
	}	
	
	private Iterable<?> cacheValueAsIterable = null;
	private Iterable<?> getValueAsIterable() {
		if(inputTag.getValue() == null){
			return null;
		}
		if(cacheValueAsIterable == null){
			cacheValueAsIterable =  getObjectAsIterable(inputTag.getValue());
		}
		return cacheValueAsIterable;
	}	
	
	protected Object doSelectAllFromService(InputTag lastInput, Class<?> usingType) {
		GenericDAO<?> dao;
		String parentProperty = "";
		if (lastInput != null) {
			parentProperty = "by_" + lastInput.getName();
		}
		Class<?> rawClassType = usingType;
		String finAllAttribute = rawClassType.getSimpleName() + "FINDALL_" + parentProperty;
		try {
			Object attribute = inputTag.getRequest().getAttribute(finAllAttribute);
			if (attribute == null) {
				dao = DAOUtils.getDAOForClass(rawClassType);//Next.getApplicationContext().getBean(beanName + "DAO");
			} else {
				return attribute;
			}
		} catch (Exception e) {
			return null;
		}
		if (dao != null) {
			String[] extraFields = null;
			if (inputTag.getSelectLabelProperty() != null && inputTag.getSelectLabelProperty().trim().length() > 0) {
				extraFields = new String[] { inputTag.getSelectLabelProperty() };
			}
			if (lastInput == null) {
				List<?> findAll;
				try {
					findAll = dao.findForCombo(extraFields);
				} catch (Exception e) {
					// se acontecer algum erro no findForCombo.. tentar o
					// findAll
					//inputTag.log.warn("findForCombo (propriedade: " + name + ") jogou exe��o: " + e.getMessage() + "   Utilizando findAll()");
					e.printStackTrace();
					findAll = dao.findAll();
				}
				inputTag.getRequest().setAttribute(finAllAttribute, findAll);
				return findAll;
			} else {
				List<?> findAll;
				if (lastInput.getValue() != null && ( lastInput.getValue().getClass().isEnum() || !DAOUtils.isTransient(lastInput.getValue()) ) ) {
					try {
						findAll = dao.findBy(lastInput.getValue(), true, extraFields);
					} catch (Exception e) {
						// se acontecer algum erro no findForCombo.. tentar o findAll
						//log.warn("findBy(" + lastInput.getValue().getClass().getSimpleName() + ") (propriedade: " + name + ") jogou exe��o: " + e.getMessage() + "   Utilizando findBy sem especificar os campos");
//						e.printStackTrace();
//						findAll = dao.findBy(lastInput.getValue(), false, extraFields);
						throw new NextException("Could not execute findBy "+lastInput.getValue()+" for property "+inputTag.getName(), e);
					}
				} else {
					findAll = new ArrayList<Object>();
				}
				inputTag.getRequest().setAttribute(finAllAttribute, findAll);
				return findAll;
			}
		} else {
			if(rawClassType != null){
				//TODO TROCAR POR LOG
				System.out.println("GenericDAO n�o encontrado para o tipo "+rawClassType.getName()+". Ignorando busca de itens de combo.");
			}
		}
		return null;
	}
	
	@Override
	protected InputTag prepareComboReload(ComboReloadGroupTag comboReload) {
		FunctionCall call;
		String itensFunction = getItensFunction();

		call = getFunctionCall();
		InputTag lastInput = null;
		
		if (!Enum.class.isAssignableFrom(getRawClassType()) || (itensFunction != null && itensFunction.contains("("))) {
			if (!isSelectAll()) {
				comboReload.registerProperty(inputTag.getName(), call, isIncludeBlank());
				// comboReload.registerProperty(usingType,
				// getName(), this.useAjax, itensFunction,
				// selectLabelProperty, includeBlank, call,
				// callInfo, onLoadItens, autoSugestUniqueItem);
				lastInput = comboReload.getLastInput(inputTag);
			} else {
				comboReload.getLastInput(inputTag);
			}
			if (call == null && lastInput != null) {
				// registrar a chamada (o registro quando call !=
				// null � feita depois
				ComboCallback.register(getRawClassType().getSimpleName(), "findBy", new Class[0]);
			}
		} else {
			// para enums o lastInput sempre � ignorado (a n�o ser que itensFunction estiver definido)
			comboReload.getLastInput(inputTag);
		}

		// c�digo removido em 30/11/2006
		// if(lastInput == null){
		// useAjax = false;
		// }
		
		this.loadItensFunction = createLoadItensFunction(getFunctionCall(), lastInput , getRawClassType());
		
		return lastInput;
	}
	
	@SuppressWarnings("deprecation")
	private String createLoadItensFunction(FunctionCall call, InputTag lastInput, Class<?> usingType) {
		if (inputTag.getUseAjax() == null || !inputTag.getUseAjax()) {
			return null;
		}
		
		FunctionParameter[] parameterArray = call != null ? call.getParameterArray() : null;
		
		//Define caminho absoluto no HTML
		BeanTag beanTag = inputTag.findParent(BeanTag.class);
		if (parameterArray != null && beanTag != null) {
			for (FunctionParameter parameter : parameterArray) {
				if (parameter.getParameterType() == ParameterType.REFERENCE ) {
					String prefix = "";
					if( Util.strings.isNotEmpty(beanTag.getPropertyPrefix()) ){
						prefix += beanTag.getPropertyPrefix();
					} 
					if( Util.strings.isNotEmpty(beanTag.getPropertyIndex()) ){
						if(beanTag.getPropertyIndex().contains("{index}")){
							prefix += "[{indexSequence}]";
						} else {
							prefix += "["+beanTag.getPropertyIndex()+"]";
						}
					} 
					if(prefix.length()!=0){
						prefix += ".";
					}
					String fullName = prefix + parameter.getParameterValue();
					parameter.setAbsoluteParameterValue(fullName);
				}
			}
		}
		
		FormTag formTag = inputTag.findParent(FormTag.class, true);
		String form = formTag.getName();
		String ifcode = "";
		HashSet<String> optionalParametersSet = getOptionalParametersSet();
		if (parameterArray != null) {
			for (FunctionParameter parameter : parameterArray) {
				if (parameter.getParameterType() == ParameterType.REFERENCE && !optionalParametersSet.contains(parameter.getParameterValue())) {
					//ifcode += form + "['" + parameter.getAbsoluteParameterValue() + "'].value != '<null>' && ";
					ifcode += InputTagSelectComponent.getDynamicProperty(form, parameter.getAbsoluteParameterValue(), "extrairNumeroDeIndexedProperty(this.name)") + ".value != '<null>' && ";
				}
			}
		} else if (lastInput != null) {
			//ifcode = form + "['" + lastInput.getName() + "'].value != '<null>' && ";
			ifcode = InputTagSelectComponent.getDynamicProperty(form, lastInput.getName(), "extrairNumeroDeIndexedProperty(this.name)") + ".value != '<null>' && ";
		}

		String parameterListFunction = "var parameterList = '";
		String classesListFunction = "var classesList = '";
		if (parameterArray != null && parameterArray.length > 0) {
			for (int j = 0; j < parameterArray.length; j++) {
				String param = parameterArray[j].getParameterValue();
				String absParam = parameterArray[j].getAbsoluteParameterValue();
				switch (parameterArray[j].getParameterType()) {
				case REFERENCE:
					//listaParametrosFuncao += "'+getInputValue(" + formTag.getName() + "['" + absParam + "'])+'";
					parameterListFunction += "'+getInputValue(" + InputTagSelectComponent.getDynamicProperty(formTag.getName(), absParam, "extrairNumeroDeIndexedProperty(this.name)")+")+'";
					classesListFunction += call.getCallInfo().getType(parameterArray[j]).getName();
					classesListFunction += ComboReloadGroupTag.CLASS_SEPARATOR;
					parameterListFunction += ComboReloadGroupTag.PARAMETER_SEPARATOR;
					break;
				case STRING:
					parameterListFunction += param;
					classesListFunction += String.class.getName();
					classesListFunction += ComboReloadGroupTag.CLASS_SEPARATOR;
					parameterListFunction += ComboReloadGroupTag.PARAMETER_SEPARATOR;
					break;
				case BOOLEAN:
					parameterListFunction += param;
					classesListFunction += Boolean.class.getName();
					classesListFunction += ComboReloadGroupTag.CLASS_SEPARATOR;
					parameterListFunction += ComboReloadGroupTag.PARAMETER_SEPARATOR;
					break;
				case USER:
					parameterListFunction += param;
					classesListFunction += User.class.getName();
					classesListFunction += ComboReloadGroupTag.CLASS_SEPARATOR;
					parameterListFunction += ComboReloadGroupTag.PARAMETER_SEPARATOR;
					break;
				default:
					throw new RuntimeException("Tipo n�o suportado: " + parameterArray[j].getParameterType());
				}
			}
		}
		parameterListFunction += "';";
		classesListFunction += "';";
		String parentValue = "";
		if (call == null && lastInput != null) {
			//parentValue = form + "['" + lastInput.getName() + "'].value";
			parentValue =  getDynamicProperty(form, lastInput.getName(), "extrairNumeroDeIndexedProperty(this.name)") + ".value";
		} else {
			parentValue = "''";
		}
		
		/*
		String absoluteCall = "";
		if (parameterArray != null && parameterArray.length > 0) {
			absoluteCall = call.getObject() + "." + call.getFunctionName() + "(";
			for (int i = 0 ; i < parameterArray.length ; i++) {
				FunctionParameter parameter = parameterArray[i];
				absoluteCall += parameter.getAbsoluteParameterValue() + (i == parameterArray.length - 1? "" : ",");
			}
			absoluteCall += ")";
		}
		*/
		String absoluteCall = call != null ? call.getCall() : "";

		//String comp = form + "['" + getName() + "']";
		String comp = getDynamicProperty(form, inputTag.getName(), "extrairNumeroDeIndexedProperty(this.name)");
		
		String holdValue = Util.booleans.isTrue(inputTag.getHoldValue()) ? ", " + comp + ".value" : "";
		String addItensHoldValue = Util.booleans.isTrue(inputTag.getHoldValue()) ? "true" : "false";
		ifcode += "1 == 1";
		String functionCode = "";
		functionCode += InputTagSelectComponent.getDynamicProperty(form, inputTag.getName(), "'"+beanTag.getPropertyIndex()+"'") + ".loadItens = function(){\n";
		//functionCode += "    alert('loading itens '+this.name);\n";
		functionCode += "    var executeOnchange = " + comp + ".value != '<null>' && " + comp + ".value != '';\n";
		functionCode += "    " + comp + ".wasEmpty = !executeOnchange;\n";

		functionCode += "    if(" + ifcode + "){\n";
		functionCode += "        limparCombo(" + comp + ", " + isIncludeBlank() + " " + holdValue + ", '', '" + inputTag.getBlankLabel() + "');\n";
		functionCode += "        " + parameterListFunction + "\n";
		functionCode += "        " + classesListFunction + "\n";
		functionCode += "        ajaxLoadCombo('" + inputTag.getRequest().getContextPath() + "', " + comp + ", '" + usingType.getName() + "', '" + Util.strings.escape(absoluteCall) 
					 + "', classesList, parameterList, '" + inputTag.getSelectLabelProperty() + "', " + parentValue	+ ");\n";
		functionCode += "    }\n";
		functionCode += "    else {\n";
		functionCode += "        limparCombo(" + comp + ", " + isIncludeBlank() + ", '', '" + inputTag.getBlankLabel() + "');\n";
		functionCode += "        if(executeOnchange) " + comp + ".onchange();\n";
		functionCode += "    }\n";

		functionCode += "};\n\n";

		functionCode += InputTagSelectComponent.getDynamicProperty(form, inputTag.getName(), "'"+beanTag.getPropertyIndex()+"'") + ".setItens = function(lista){\n";
		//functionCode += "    alert('seting itens '+this.name);\n";
		functionCode += "      var valorMantido = addItensToCombo(" + comp + ", lista, " + addItensHoldValue + ");\n";

		if (Util.booleans.isTrue(inputTag.getAutoSugestUniqueItem())) {
			String indice = isIncludeBlank() ? "1" : "0";
			functionCode += "        if(lista.length >= 1){";
			functionCode += "            if(lista.length == 1){" + comp + ".selectedIndex = " + indice + ";}/*AUTO SUGEST UNIQUE ITEM*/\n";
			functionCode += "        }";
		}

		functionCode += "       if(!" + comp + ".wasEmpty && !valorMantido){" + comp + ".onchange();}\n";
		functionCode += "        " + inputTag.getOnLoadItens() + "\n";

		functionCode += "};\n";
		return functionCode;
	}
	
	
	private static class CacheControl {
		
		Map<String, Object> cache = new HashMap<String, Object>();
		
		public static CacheControl get(HttpServletRequest request){
			String id = CacheControl.class.getName();
			CacheControl cc = null;
			if((cc = (CacheControl) request.getAttribute(id)) == null){
				request.setAttribute(id, cc = new CacheControl());
			}
			return cc;
		}
		
		public Map<String, Object> getCache() {
			return cache;
		}
	}
}
