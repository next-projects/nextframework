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

import java.util.ArrayList;
import java.util.List;

import org.nextframework.controller.MultiActionController;
import org.nextframework.util.Util;
import org.nextframework.view.components.InputTagSelectComponent;
import org.nextframework.view.util.FunctionCall;
import org.nextframework.view.util.FunctionParameter;
import org.nextframework.view.util.ParameterType;

/**
 * @author rogelgarcia | marcusabreu
 * @since 06/02/2006
 * @version 1.1
 */
public class ComboReloadGroupTag extends BaseTag implements LogicalTag {

	public static final String CLASS_SEPARATOR = ";";
	public static final String PARAMETER_SEPARATOR = "#";

	private List<PropertyCall> propertyCalls = new ArrayList<PropertyCall>();

	protected String functionName;

	protected Boolean useAjax = true;

	protected InputTag lastInput;

	public Boolean getUseAjax() {
		return useAjax;
	}

	public void setUseAjax(Boolean useAjax) {
		this.useAjax = useAjax;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	/**
	 * Retorna o input anterior a <i>tag</i> e troca o last input para o parametro passado
	 * @param tag
	 * @return
	 */
	public InputTag getLastInput(InputTag tag) {
		InputTag retorno = lastInput;
		lastInput = tag;
		return retorno;
	}

	public void registerProperty(String name, FunctionCall call, Boolean includeBlank) {
		List<String> dependencies = new ArrayList<String>();
		if (call == null) {
			//Quando é sem call, pega o nome do controle anterior, 
			if (lastInput != null) {
				String lastInputName = lastInput.getName();
				//mas deve remover o prefixo, pois já é colocado no proximo passo
				String prefix = getPrefixFromBeanTag();
				if (Util.strings.isNotEmpty(prefix)) {
					lastInputName = lastInputName.substring(prefix.length());
				}
				dependencies.add(lastInputName);
			}
		} else {
			FunctionParameter[] parameterArray = call.getParameterArray();
			for (FunctionParameter parameter : parameterArray) {
				if (parameter.getParameterType() == ParameterType.REFERENCE) {
					dependencies.add(parameter.getParameterValue());
				}
			}
		}
		PropertyCall propertyCall = new PropertyCall(name, call, includeBlank, dependencies.toArray(new String[dependencies.size()]));
		propertyCalls.add(propertyCall);
	}

	@Override
	protected void doComponent() throws Exception {

		functionName = "comboReload_" + generateUniqueId();
		BeanTag beanTag = findParent(BeanTag.class);
		if (beanTag != null && beanTag.getPropertyIndex() != null) {
			if (beanTag.getPropertyIndex().contains("{")) {
				functionName += "_{indexSequence}";
			} else {
				functionName += "_" + beanTag.getPropertyIndex();
			}
		}

		doBody();

		FormTag formTag = findParent(FormTag.class, true);
		String form = formTag.getName();

		StringBuilder builder = new StringBuilder();

		//IE7: precisa de ter esse <table></table> nao retirar!
		builder.append("<table style='display:none'></table>\n");
		builder.append("<script language='javascript'>\n");
		builder.append("	function " + functionName + "(prop, value, currentIndex) {\n");
		for (PropertyCall propertyCall : propertyCalls) {
			builder.append("		if(" + ifDependencies(propertyCall).replaceAll("\\{index\\}", "'+currentIndex+'") + "){\n");
			if (useAjax) {
				String property = propertyCall.property;
				String currentIndexVar = "currentIndex";
				builder.append("			" + InputTagSelectComponent.getDynamicProperty(form, property, currentIndexVar) + ".loadItens();\n");
			} else {
				builder.append("			" + functionName + "_reload();\n");
			}
			builder.append("		}\n");
		}
		builder.append("	}\n");

		String url = formTag.getUrl();
		String action;
		if (formTag.getAction() != null) {
			action = formTag.getAction();
		} else {
			action = "";
		}

		builder.append("	function " + functionName + "_reload() {\n");
		builder.append("		" + formTag.getName() + ".action = '" + url + "';\n");
		builder.append("		" + formTag.getName() + "." + MultiActionController.ACTION_PARAMETER + ".value = '" + action + "';\n");
		builder.append("		" + formTag.getName() + ".validate = 'false';\n");
		builder.append("		" + formTag.getName() + ".suppressErrors.value = 'true';\n");
		builder.append("		" + formTag.getName() + ".suppressValidation.value = 'true';\n");
		builder.append("		" + formTag.getSubmitFunction() + "();\n");
		builder.append("	}\n");
		builder.append("</script>");

		getOut().println(builder.toString());

	}

	private String ifDependencies(PropertyCall propertyCall) {

		String expression = "";

		for (String dependencia : propertyCall.dependencies) {

			boolean bypass = false;

			//se a propriedade em questao tiver outra dependencia (sem ser a em questao)
			//e essa outra dependencia também depender da dependencia em questao
			//e essa outra dependencia for includeBlank false... passar essa dependencia
			//exemplo
			// temos propriedade A
			// temos propriedade B que depende de A e includeBlank = false
			// temos propriedade C que depende de B e depende de A 
			// quando a propriedade A for mudada não deve ser recarregado o combo C
			// isso, porque o combo A modificará o combo B que por ser includeBlank = false já irá modificar o combo C
			for (String dependencia2 : propertyCall.dependencies) {
				if (dependencia2 != dependencia) {
					PropertyCall propertyCallDependencia2 = getPropertyCall(dependencia2);
					if (propertyCallDependencia2 != null) {
						if (Boolean.FALSE.equals(propertyCallDependencia2.includeBlank)) {
							String[] dependencies2 = propertyCallDependencia2.dependencies;
							for (String dependencie2 : dependencies2) {
								if (dependencie2.equals(dependencia)) {
									bypass = true;
								}
							}
						}
					}
				}
			}

			if (!bypass) {
				String prefix = getPrefixFromBeanTag();
				String dependenciaCompleta = prefix + dependencia;
				expression += "prop == '" + dependenciaCompleta + "' || ";
			}

		}

		expression += " 0 == 1";
		return expression;
	}

	private String getPrefixFromBeanTag() {
		BeanTag beanTag = findParent(BeanTag.class);
		String prefix = "";
		if (beanTag != null) {
			if (Util.strings.isNotEmpty(beanTag.getPropertyPrefix())) {
				prefix += beanTag.getPropertyPrefix();
			}
			if (Util.strings.isNotEmpty(beanTag.getPropertyIndex())) {
				prefix += "[" + beanTag.getPropertyIndex() + "]";
			}
			if (prefix.length() != 0) {
				prefix += ".";
			}
		}
		return prefix;
	}

	private PropertyCall getPropertyCall(String dependencia) {
		PropertyCall propertyCallDependencia2 = null;
		for (PropertyCall teste : propertyCalls) {
			if (teste.property.equals(dependencia)) {
				propertyCallDependencia2 = teste;
				break;
			}
		}
		return propertyCallDependencia2;
	}

	class PropertyCall {

		String property;
		FunctionCall call;
		String[] dependencies;
		Boolean includeBlank;

		public PropertyCall(String property, FunctionCall call, Boolean includeBlank, String... dependencies) {
			super();
			this.property = property;
			this.call = call;
			this.dependencies = dependencies;
			this.includeBlank = includeBlank;
		}

	}

}
