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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;

import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.controller.MultiActionController;
import org.nextframework.core.config.ViewConfig;
import org.nextframework.core.config.ViewConfig.RequiredMarkMode;
import org.nextframework.core.standard.Next;
import org.nextframework.core.web.WebRequestContext;
import org.nextframework.service.ServiceFactory;
import org.nextframework.util.Util;
import org.nextframework.view.code.DebugInputsTag;
import org.nextframework.view.components.InputTagComponent;
import org.springframework.beans.BeanUtils;

/**
 * @author rogelgarcia | marcusabreu
 * @since 26/01/2006
 * @version 1.1
 */
public class InputTag extends BaseTag {

	public static Class<? extends InputTagHelper> inputTagHelperClass = InputTagHelper.class;

	InputTagHelper inputTagHelper = BeanUtils.instantiate(inputTagHelperClass);

	// atributos
	protected String name;

	protected String label;

	protected Object type;
	private Object autowiredType;

	protected Object value;

	protected String pattern;

	protected Boolean autowire = true;

	protected Boolean required;

	protected Boolean showLabel = false;

	protected Annotation[] annotations;

	protected Boolean reloadOnChange = false;

	protected Boolean write = false;

	// checkbox
	protected String trueFalseNullLabels;
	protected Boolean booleanValue = null;

	protected boolean forceValidation = false;

	// select-one-button
	protected String selectOnePath;
	protected String selectOnePathParameters;
	protected String selectOneWindowSize;

	// select-one-insert
	protected String insertPath;

	// select-one ou select-many
	protected Object itens;
	protected String selectLabelProperty;
	private Boolean useAjax;
	protected Boolean autoSugestUniqueItem;
	protected String optionalParams = "";
	protected Boolean holdValue;

	private PropertySetter propertySetter;

	// ajax - somente utilizado se userAjax = true;
	// executado quando termina-se de atualizar os itens do combo
	protected String onLoadItens = "";

	// select-one
	protected Boolean includeBlank = true;
	protected String blankLabel = " ";

	// text-area
	protected Integer cols;

	protected Integer rows;

	// button do file upload
	protected boolean showDeleteButton = true;

	// extra
	protected InputTagType selectedType = InputTagType.TEXT;

	private String selectoneblankoption;

	private Boolean checked;

	private String checkboxValue = "true";

	// estilos
	private String labelStyle;
	private String labelStyleClass;
	private String requiredStyleClass;

	// arquivo
	protected Boolean transientFile;

	private InputTagComponent inputComponent;

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

	public void setCheckboxValue(String checkboxValue) {
		this.checkboxValue = checkboxValue;
	}

	public void setSelectedType(InputTagType selectedType) {
		this.selectedType = selectedType;
	}

	public InputTagComponent getInputComponent() {
		return inputComponent;
	}

	public void setInputComponent(InputTagComponent inputComponent) {
		this.inputComponent = inputComponent;
	}

	@Override
	protected void applyDefaultStyleClasses() throws JspException {
		//Não aplica no fluxo natural.
	}

	@Override
	protected String getSubComponentName() {
		return selectedType != null ? selectedType.toString() : null;
	}

	@Override
	protected void doComponent() throws Exception {

		// boolean valueExplicito = value != null;
		inputTagHelper.autowireAttributes(this);
		selectedType = inputTagHelper.chooseType(this);

		//Aplica estilos padrão apenas após resolver o 'selectedType'
		super.applyDefaultStyleClasses();

		inputComponent = InputTagComponentManager.getInstance().getInputComponent(selectedType);

		inputComponent.setTag(this);

		inputComponent.setHelper(inputTagHelper);

		inputComponent.validateTag();

		inputComponent.prepare();

		if (Util.booleans.isTrue(showLabel) && Util.strings.isNotEmpty(label)) {
			String id = getId() != null ? getId() : generateUniqueId();
			String styleClass = Util.strings.isNotEmpty(labelStyleClass) ? " class=\"" + labelStyleClass + "\"" : "";
			String style = Util.strings.isNotEmpty(labelStyle) ? " style=\"" + labelStyle + "\"" : "";
			getOut().print("<label for=\"" + id + "\"" + styleClass + style + ">");
			getOut().print(label);
			getOut().print("</label>");
		}

		// fazer os listeners
		for (Annotation annotation : getAnnotations()) {
			inputTagHelper.getInputListener(this, annotation).onRender(this, annotation);
		}

		DebugInputsTag debugInputsTag = findParent(DebugInputsTag.class, false);
		if (debugInputsTag != null) {
			boolean add = debugInputsTag.addProperty(name);
			if (!add) {
				getDynamicAttributesMap().put("style", "border: 1px solid red");
				getDynamicAttributesMap().put("title", "Propriedade duplicada: " + name);
			}
			try {
				Class<?> class1 = debugInputsTag.getCommandClass();
				BeanDescriptorFactory.forClass(class1).getPropertyDescriptor(name);
			} catch (Exception e) {
				getDynamicAttributesMap().put("style", "border: 1px solid red");
				getDynamicAttributesMap().put("title", "Propriedade inválida: " + name);
			}
		}

		RequiredMarkMode requiredMarkMode = ServiceFactory.getService(ViewConfig.class).getRequiredMarkMode();
		if (!inputComponent.isToPrintRequired()) {
			requiredMarkMode = null;
		}

		if (requiredMarkMode == RequiredMarkMode.STYLECLASS) {
			addRequiredStyle();
		} else if (requiredMarkMode == RequiredMarkMode.BEFORE) {
			printRequired();
		}

		includeTemplate();

		if (requiredMarkMode == RequiredMarkMode.AFTER) {
			printRequired();
		}

		inputComponent.afterPrint();

	}

	public boolean isRequiredResolved() {
		return Util.booleans.isTrue(required) && !isReadOnlyOrDisabled();
	}

	public boolean isReadOnlyOrDisabled() {
		return getDynamicAttributesMap().containsKey("readonly") || getDynamicAttributesMap().containsKey("disabled");
	}

	protected void addRequiredStyle() {
		if (requiredStyleClass != null) {
			String sc = (String) getDAAtribute("class", false);
			sc = sc != null ? sc + " " + requiredStyleClass : requiredStyleClass;
			getDynamicAttributesMap().put("class", sc);
		}
	}

	protected void printRequired() throws IOException {
		String requiredMark = ServiceFactory.getService(ViewConfig.class).getRequiredMarkString();
		if (requiredMark != null) {
			String sc = requiredStyleClass != null ? " class=\"" + requiredStyleClass + "\"" : "";
			getOut().println("<span" + sc + ">" + requiredMark + "</span>");
		}
	}

	protected void includeTemplate() throws ServletException, IOException {
		includeJspTemplate(selectedType.toString().toLowerCase());
	}

	public Object getOnKeyPress() {
		return getDAAtribute("onKeyPress", true);
	}

	public Object getOnKeyUp() {
		return getDAAtribute("onKeyUp", true);
	}

	public Object getDAAtribute(String key, boolean remove) {
		Set<String> keySet = getDynamicAttributesMap().keySet();
		for (String key2 : keySet) {
			if (key2.equalsIgnoreCase(key)) {
				if (remove) {
					return getDynamicAttributesMap().remove(key2);
				} else {
					return getDynamicAttributesMap().get(key2);
				}
			}
		}
		return null;
	}

	public String getReloadOnChangeString() {
		String onchangestring = "";
		if (reloadOnChange != null && reloadOnChange) {
			// MODIFICADO EM 10/08/2006
			FormTag form = findParent(FormTag.class, true);
			String lastAction = ((WebRequestContext) Next.getRequestContext()).getLastAction();
			onchangestring = form.getName() + "." + MultiActionController.ACTION_PARAMETER + ".value = '" + (lastAction != null ? lastAction : "") + "';" +
					form.getName() + ".validate = 'false';" +
					form.getName() + ".suppressErrors.value = 'true';" +
					form.getName() + ".suppressValidation.value = 'true';" +
					form.getSubmitFunction() + "()";
		} else {
			ComboReloadGroupTag comboReloadGroupTag = findParent(ComboReloadGroupTag.class);
			if (comboReloadGroupTag != null) {
				FormTag form = findParent(FormTag.class, true);
				if (selectedType == InputTagType.SELECT_ONE_BUTTON) {
					onchangestring = comboReloadGroupTag.getFunctionName() + "('" + getName() + "', " + form.getName() + "['" + name + "'].value);";
				} else {
					onchangestring = comboReloadGroupTag.getFunctionName() + "(this.name, this.value, extrairNumeroDeIndexedProperty(this.name) );";
				}
			}
		}
		String daOnChange = (String) getDAAtribute("onChange", true);
		if (daOnChange != null) {
			onchangestring = daOnChange + ";" + onchangestring;
		}
		return onchangestring;
	}

	public String getValueToString() {
		return escapeStringValueForAttribute(computeValueToString());
	}

	private String computeValueToString() {
		String valueAsString = inputComponent.getValueAsString();
		if (valueAsString != null) {
			return valueAsString;
		}
		if (value instanceof String) {
			return (String) value;
		}
		return TagUtils.getObjectValueToString(value, false, pattern);
	}

	@Deprecated
	public String getEscapeValueToString() {
		if (value == null) {
			if (selectedType == InputTagType.HIDDEN) {
				return "<null>";
			} else if (selectedType == InputTagType.TEXT) {
				return "";
			}
		}
		String opValue;
		if (selectedType == InputTagType.TEXT && TagUtils.hasId(value.getClass())) {
			opValue = TagUtils.getObjectDescriptionToString(value, pattern, pattern, pattern);
		} else {
			opValue = TagUtils.getObjectValueToString(value, false, null);
		}
		return escapeStringValueForAttribute(opValue);
	}

	private String escapeStringValueForAttribute(String value) {
		if (value == null)
			return null;
		return value.replaceAll("\\\\", "\\\\").replaceAll("\"", "&#34;");
	}

	public String getValueWithDescriptionToString() {
		if (value == null) {
			return "<null>";
		}
		return TagUtils.getObjectValueToString(value, true, null);
	}

	public Boolean getWrite() {
		return write;
	}

	public void setWrite(Boolean write) {
		this.write = write;
	}

	public String getDescriptionToString() {
		return TagUtils.getObjectDescriptionToString(value, pattern, pattern, pattern);
	}

	public String getChecked() {
		if (checked != null) {
			return checked ? "checked" : null;
		}
		if (value != null && new Boolean(value.toString())) {
			return "checked";
		}
		return null;
	}

	public boolean isLoadAllItems() {
		if (isReadOnlyOrDisabled()) {
			return getViewConfig().isInputSelectLoadAllItemsIfDisabled();
		}
		return true;
	}

	public Boolean getAutowire() {
		return autowire;
	}

	public Integer getCols() {
		return cols;
	}

	public Boolean getIncludeBlank() {
		return includeBlank;
	}

	public Object getItens() {
		return itens;
	}

	public String getName() {
		return name;
	}

	public Boolean getRequired() {
		return required;
	}

	public Integer getRows() {
		return rows;
	}

	public Object getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}

	public Class<?> getValueClass() {
		return value.getClass();
	}

	public void setAutowire(Boolean autowire) {
		this.autowire = autowire;
	}

	public void setCols(Integer cols) {
		this.cols = cols;
	}

	public void setIncludeBlank(Boolean includeBlank) {
		this.includeBlank = includeBlank;
	}

	public void setItens(Object itens) {
		this.itens = itens;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}

	public void setType(Object type) {
		this.type = type;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getSelectoneblankoption() {
		return selectoneblankoption;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Boolean getShowLabel() {
		return showLabel;
	}

	public void setShowLabel(Boolean showLabel) {
		this.showLabel = showLabel;
	}

	public String getSelectOnePath() {
		return selectOnePath;
	}

	public void setSelectOnePath(String selectOnePath) {
		this.selectOnePath = selectOnePath;
	}

	public Boolean getReloadOnChange() {
		return reloadOnChange;
	}

	public void setReloadOnChange(Boolean reloadOnChange) {
		this.reloadOnChange = reloadOnChange;
	}

	public String getTrueFalseNullLabels() {
		return trueFalseNullLabels;
	}

	public void setTrueFalseNullLabels(String trueFalseNullValues) {
		this.trueFalseNullLabels = trueFalseNullValues;
	}

	public String getSelectLabelProperty() {
		return selectLabelProperty;
	}

	public void setSelectLabelProperty(String selectLabelProperty) {
		this.selectLabelProperty = selectLabelProperty;
	}

	public String getCheckboxValue() {
		return checkboxValue;
	}

	public String getLabelStyle() {
		return labelStyle;
	}

	public void setLabelStyle(String labelStyle) {
		this.labelStyle = labelStyle;
	}

	public String getLabelStyleClass() {
		return labelStyleClass;
	}

	public void setLabelStyleClass(String labelStyleClass) {
		this.labelStyleClass = labelStyleClass;
	}

	public String getRequiredStyleClass() {
		return requiredStyleClass;
	}

	public void setRequiredStyleClass(String requiredStyleClass) {
		this.requiredStyleClass = requiredStyleClass;
	}

	public Boolean getUseAjax() {
		if (useAjax == null) {
			ComboReloadGroupTag comboReload = findParent(ComboReloadGroupTag.class);
			if (comboReload != null) {
				setUseAjax(comboReload.getUseAjax());
			}
		}
		if (useAjax == null) {
			useAjax = false;
		}
		return useAjax;
	}

	public void setUseAjax(Boolean useAjax) {
		this.useAjax = useAjax;
	}

	public String getOnLoadItens() {
		return onLoadItens;
	}

	public void setOnLoadItens(String onLoadItens) {
		this.onLoadItens = onLoadItens;
	}

	public Boolean getAutoSugestUniqueItem() {
		return autoSugestUniqueItem;
	}

	public String getBlankLabel() {
		return blankLabel;
	}

	public void setAutoSugestUniqueItem(Boolean autoSugestUniqueItem) {
		this.autoSugestUniqueItem = autoSugestUniqueItem;
	}

	public Boolean getBooleanValue() {
		return booleanValue;
	}

	public Boolean getTransientFile() {
		return transientFile;
	}

	public void setTransientFile(Boolean transientFile) {
		this.transientFile = transientFile;
	}

	public void setBlankLabel(String blankLabel) {
		this.blankLabel = blankLabel;
	}

	public String getOptionalParams() {
		return optionalParams;
	}

	public void setOptionalParams(String optionalParams) {
		this.optionalParams = optionalParams;
	}

	public boolean isForceValidation() {
		return forceValidation;
	}

	public void setForceValidation(boolean forceValidation) {
		this.forceValidation = forceValidation;
	}

	public boolean isShowDeleteButton() {
		return showDeleteButton;
	}

	@Deprecated
	public boolean isShowRemoverButton() {
		return showDeleteButton;
	}

	public void setShowDeleteButton(boolean showDeleteButton) {
		this.showDeleteButton = showDeleteButton;
	}

	@Deprecated
	public void setShowRemoverButton(boolean showDeleteButton) {
		this.showDeleteButton = showDeleteButton;
	}

	public Boolean getHoldValue() {
		return holdValue;
	}

	public void setHoldValue(Boolean holdValue) {
		this.holdValue = holdValue;
	}

	public String getInsertPath() {
		return insertPath;
	}

	public String getPattern() {
		return pattern;
	}

	public String getSelectOnePathParameters() {
		return selectOnePathParameters;
	}

	public void setInsertPath(String insertPath) {
		this.insertPath = insertPath;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public void setSelectOnePathParameters(String selectOnePathParameters) {
		this.selectOnePathParameters = selectOnePathParameters;
	}

	public String getSelectOneWindowSize() {
		return selectOneWindowSize;
	}

	public void setSelectOneWindowSize(String selectOnePathWindowSize) {
		this.selectOneWindowSize = selectOnePathWindowSize;
	}

	public void setSelectoneblankoption(String selectoneblankoption) {
		this.selectoneblankoption = selectoneblankoption;
	}

	public void setAnnotations(Annotation[] annotations) {
		this.annotations = annotations;
	}

	public Annotation[] getAnnotations() {
		return annotations;
	}

	public void setAutowiredType(Object autowiredType) {
		this.autowiredType = autowiredType;
	}

	public Object getAutowiredType() {
		return autowiredType;
	}

	public void setPropertySetter(PropertySetter propertySetter) {
		this.propertySetter = propertySetter;
	}

	public PropertySetter getPropertySetter() {
		return propertySetter;
	}

}
