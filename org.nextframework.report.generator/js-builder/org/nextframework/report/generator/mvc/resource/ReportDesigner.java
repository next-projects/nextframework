package org.nextframework.report.generator.mvc.resource;

import static org.nextframework.js.NextGlobalJs.next;
import static org.stjs.javascript.Global.alert;
import static org.stjs.javascript.Global.confirm;
import static org.stjs.javascript.JSCollections.$array;

import org.nextframework.js.NextGlobalJs;
import org.nextframework.resource.NextDialogs;
import org.nextframework.resource.NextDialogs.MessageDialog;
import org.stjs.javascript.Array;
import org.stjs.javascript.Global;
import org.stjs.javascript.JSCollections;
import org.stjs.javascript.Map;
import org.stjs.javascript.dom.Button;
import org.stjs.javascript.dom.DOMEvent;
import org.stjs.javascript.dom.Div;
import org.stjs.javascript.dom.Element;
import org.stjs.javascript.dom.HTMLCollection;
import org.stjs.javascript.dom.Input;
import org.stjs.javascript.dom.Option;
import org.stjs.javascript.dom.Select;
import org.stjs.javascript.dom.Table;
import org.stjs.javascript.dom.TableCell;
import org.stjs.javascript.dom.TextArea;
import org.stjs.javascript.functions.Callback0;
import org.stjs.javascript.functions.Callback1;
import org.stjs.javascript.functions.Function1;

public class ReportDesigner {

	private static ReportDesigner instance;

	public synchronized static ReportDesigner getInstance() {
		if (instance == null) {
			instance = new ReportDesigner("designerArea", "xml");
		}
		return instance;
	}

	String controllerPath;

	String reportTitle;
	Boolean reportPublic;

	Div mainDiv;
	TextArea outputXml;

	Map<String, Map<String, Object>> fields;

	Array<String> avaiableProperties;

	ReportGeneratorSelectManyBoxView fieldSelect;
	ReportGeneratorSelectManyBoxView groupSelect;
	ReportGeneratorSelectManyBoxView filterSelect;
	Select calculatedFieldsSelect;

	ReportDefinition definition;

	ReportCalculatedFieldsManager calculatedFieldsManager;
	ReportLayoutManager layoutManager;
	ReportGroupManager groupManager;
	ReportFilterManager filterManager;

	private Input reportTitleInput;
	private ReportData reportData;

	Input labelInput;
	Select patternDateInput;
	Select patternNumberInput;
	Input aggregateInput;
	Select aggregateTypeInput;

	Select patternDateInputGroup;

	Input filterLabel;
	Select filterFixedCriteria;
	Select filterPreSelectDate;
	Select filterPreSelectEntity;
	Input filterSelectMultiple;
	Input filterRequired;

	Select charts;

	Array<Selectable> selectables;

	private ReportDesigner(String divId, String textAreaId) {

		final ReportDesigner bigThis = this;
		selectables = $array();
		avaiableProperties = $array();

		mainDiv = (Div) next.dom.toElement(divId);
		outputXml = (TextArea) next.dom.toElement(textAreaId);

		fields = JSCollections.$map();

		definition = new ReportDefinition(this, (Table) next.dom.getInnerElementById(mainDiv, "designTable"));
		selectables.push(definition);

		reportTitleInput = (Input) next.dom.getInnerElementById(mainDiv, "reportTitle");
		reportTitleInput.onblur = new Function1<DOMEvent, Boolean>() {

			@Override
			public Boolean $invoke(DOMEvent p1) {
				bigThis.updateTitle();
				return true;
			}

		};

		fieldSelect = addSelectManyComponent("fields");
		groupSelect = addSelectManyComponent("groups");
		filterSelect = addSelectManyComponent("filters");

		labelInput = (Input) next.dom.getInnerElementById(mainDiv, "label");
		patternDateInput = (Select) next.dom.getInnerElementById(mainDiv, "patternDate");
		patternNumberInput = (Select) next.dom.getInnerElementById(mainDiv, "patternNumber");
		aggregateInput = (Input) next.dom.getInnerElementById(mainDiv, "aggregate");
		aggregateTypeInput = (Select) next.dom.getInnerElementById(mainDiv, "aggregateType");

		patternDateInputGroup = (Select) next.dom.getInnerElementById(mainDiv, "patternDateGroup");

		filterLabel = (Input) next.dom.getInnerElementById(mainDiv, "filterLabel");
		filterFixedCriteria = (Select) next.dom.getInnerElementById(mainDiv, "filterFixedCriteria");
		filterPreSelectDate = (Select) next.dom.getInnerElementById(mainDiv, "filterPreSelectDate");
		filterPreSelectEntity = (Select) next.dom.getInnerElementById(mainDiv, "filterPreSelectEntity");
		filterSelectMultiple = (Input) next.dom.getInnerElementById(mainDiv, "filterSelectMultiple");
		filterRequired = (Input) next.dom.getInnerElementById(mainDiv, "filterRequired");

		calculatedFieldsSelect = next.dom.toElement("calculatedFields");

		charts = (Select) next.dom.getInnerElementById(mainDiv, "charts");

		groupManager = new ReportGroupManager(this, this.groupSelect);
		filterManager = new ReportFilterManager(this, this.filterSelect);
		layoutManager = new ReportLayoutManager(this, this.fieldSelect);
		calculatedFieldsManager = new ReportCalculatedFieldsManager(this, this.calculatedFieldsSelect);

		reportData = new ReportData(this);

		hideAll();

	}

	private ReportGeneratorSelectManyBoxView addSelectManyComponent(String name) {

		final ReportGeneratorSelectManyBoxView result = new ReportGeneratorSelectManyBoxView((Select) next.dom.toElement(name + "_from_"));
		result.setUsePropertyAsLabel("displayName");

		final ReportDesigner bigThis = this;

		result.onselect = new Callback1<Option>() {

			public void $invoke(Option p1) {
				bigThis.blurAllBut(result);
			}

		};

		selectables.push(result);

		return result;
	}

	public void blurAllBut(Selectable select) {
		for (String key : selectables) {
			Selectable value = selectables.$get(key);
			if (value != select) {
				value.blur();
			}
		}
	}

	private void hideAll() {
		hideInputLabel();
		hideInputDatePattern();
		hideInputNumberPattern();
		hideAggregate();
		hideInputPatternGroup();
		hideFilterLabel();
		hideFilterFixedCriteria();
		hideFilterPreSelectDate();
		hideFilterPreSelectEntity();
		hideFilterSelectMultiple();
		hideFilterRequired();
	}

	public void removeSelectedCalculatedProperty() {
		int selectedIndex = calculatedFieldsSelect.selectedIndex;
		if (selectedIndex >= 0) {
			String fieldName = calculatedFieldsSelect.options.$get(selectedIndex).value;
			calculatedFieldsManager.remove(fieldName);
			fieldSelect.unselect(fieldName);

			next.dom.removeSelectValue(fieldSelect.getFrom(), fieldName);
			calculatedFieldsSelect.removeChild(calculatedFieldsSelect.options.$get(selectedIndex));
		}
		writeXml();
		Global.alert("O campo calculado foi removido. É necessário remover também (se houver) todas as referências para esse campo no relatório.");
	}

	public void editCalculatedProperty() {

		final Select calculatedFields = next.dom.toElement("calculatedFields");
		String calculatedFieldSelected = next.dom.getSelectedValue(calculatedFields);
		if (calculatedFieldSelected == null) {
			return;
		}

		showAddCalculatedProperty();

		final Input calculationExpression = next.dom.toElement("calculationExpression");
		final Input calculationDisplayName = next.dom.toElement("calculationDisplayName");
		final Input calculationName = next.dom.toElement("calculationName");
		final Select calculationProcessor = next.dom.toElement("calculationProcessor");
		final Input calculationFormatAsNumber = next.dom.toElement("calculationFormatAsNumber");
		final Input calculationFormatAsTime = next.dom.toElement("calculationFormatAsTime");
		final Select calculationFormatAsTimeDetail = next.dom.toElement("calculationFormatAsTimeDetail");

		SimpleNamedObject obj = this.calculatedFieldsManager.getByName(calculatedFieldSelected);

		Object exp = obj.value.$get("expression");
		Object display = obj.value.$get("displayName");
		Object formatAs = obj.value.$get("formatAs");
		Object formatTimeDetail = obj.value.$get("formatTimeDetail");

		Array<String> processors = ReportPropertyConfigUtils.getProcessors(obj.value);

		calculationName.value = calculatedFieldSelected;
		calculationDisplayName.disabled = true;
		calculationDisplayName.readOnly = true;
		next.style.addClass(calculationDisplayName, "readOnly");

		calculationExpression.value = (String) exp;
		calculationDisplayName.value = (String) display;
		calculationFormatAsNumber.checked = !formatAs.equals("time");
		calculationFormatAsTime.checked = formatAs.equals("time");
		next.dom.setSelectedValue(calculationFormatAsTimeDetail, (String) formatTimeDetail);
		next.dom.setSelectedValues(calculationProcessor, processors);

	}

	public void showAddCalculatedProperty() {

		final ReportDesigner bigThis = this;

		final Input calculationExpression = next.dom.toElement("calculationExpression");
		final Input calculationDisplayName = next.dom.toElement("calculationDisplayName");
		final Input calculationName = next.dom.toElement("calculationName");
		final Select calculationProcessor = next.dom.toElement("calculationProcessor");
		final Input calculationFormatAsNumber = next.dom.toElement("calculationFormatAsNumber");
		final Select calculationFormatAsTimeDetail = next.dom.toElement("calculationFormatAsTimeDetail");

		Div varDiv = next.dom.toElement("varDiv");
		varDiv.innerHTML = "";

		calculationDisplayName.disabled = false;
		calculationDisplayName.readOnly = false;
		next.style.removeClass(calculationDisplayName, "readOnly");

		calculationExpression.value = "";
		calculationDisplayName.value = "";
		calculationName.value = "";
		calculationProcessor.selectedIndex = 0;
		calculationFormatAsNumber.checked = true;
		calculationFormatAsTimeDetail.selectedIndex = 1;

		for (String key : avaiableProperties) {
			String property = avaiableProperties.$get(key);
			Map<String, Object> options = fields.$get(property);
			//Global.console.info(property + " " + options);
			if (options != null && (ReportPropertyConfigUtils.isNumber(options) || ReportPropertyConfigUtils.isDate(options))) {
				Button b = createPropertyButton(property, (String) options.$get("displayName"));
				varDiv.appendChild(b);
			}
		}

		if (calculationDisplayName.getAttribute("data-configured") == null) {
			next.events.attachEvent(calculationDisplayName, "change", new Callback1<DOMEvent>() {

				public void $invoke(DOMEvent p1) {
					bigThis.onChangeCalculationVarName(calculationDisplayName, calculationName);
				}

			});
		}

		final Element panelDiv = Global.window.document.getElementById("calculatedPropertiesWizzard");
		final Element panelDivParent = panelDiv.parentNode;

		final MessageDialog dialog = new MessageDialog();
		dialog.setSize(NextDialogs.SIZE_LARGE);
		dialog.setTitle("Configurar campo calculado");
		dialog.appendToBody(panelDiv);
		dialog.setCallback(new NextDialogs.DialogCallback() {

			public boolean onClick(String command, Object value, Element button) {
				if (command.equals("OK")) {
					bigThis.saveCalculatedProperty();
				}
				panelDivParent.appendChild(panelDiv);
				return true;
			}

		});

		dialog.show();

	}

	private Button createPropertyButton(final String property, String displayName) {
		final ReportDesigner bigThis = this;
		Button b = next.dom.newElement("button");
		b.className = next.globalMap.get("NextDialogs.button", "button");
		b.innerHTML = displayName;
		b.onclick = new Function1<DOMEvent, Boolean>() {

			@Override
			public Boolean $invoke(DOMEvent p1) {
				bigThis.appendToExpression(property);
				return true;
			}

		};
		return b;
	}

	public void appendNumberToExpression() {
		final ReportDesigner bigThis = this;
		MessageDialog dialog = next.dialogs.showInputNumberDialog("Inserir Número", "Digite o número que deseja inserir na fórmula:");
		dialog.setCallback(new NextDialogs.DialogCallback() {

			@Override
			public boolean onClick(String command, Object value, Element button) {
				if (command == NextDialogs.OK) {
					bigThis.appendToExpression("" + value);
				}
				return true;
			}

		});
	}

	public void appendToExpression(String varText) {
		Input calculationExpression = next.dom.toElement("calculationExpression");
		if (varText.charAt(0) == '$') {
			char c = varText.charAt(1);
			if (c == 'B') {
				String result = calculationExpression.value;
				result = result.substring(0, result.length() - 1);
				int space = result.lastIndexOf(' ');
				if (space <= 0) {
					result = "";
				} else {
					result = result.substring(0, space) + " ";
				}
				calculationExpression.value = result;
			} else if (c == 'C') {
				calculationExpression.value = "";
			} else if (c == 'N') {
				calculationExpression.value += "$now" + " ";
			}
		} else {
			calculationExpression.value += varText + " ";
		}
		String errorMessage = ReportPropertyConfigUtils.validateExpression(calculationExpression.value);
		if (errorMessage != null) {
			next.dom.toElement("validationExpressionError").innerHTML = errorMessage;
		} else {
			next.dom.toElement("validationExpressionError").innerHTML = "";
		}
	}

	public void onChangeCalculationVarName(Input calculationDisplayName, Input calculationName) {
		String value = calculationDisplayName.value;
		String result = "";
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (result == "" && ReportPropertyConfigUtils.isDigit(c)) {
				result += "_";
			} else if (ReportPropertyConfigUtils.isLetter(c) || ReportPropertyConfigUtils.isDigit(c)) {
				result += c;
			} else {
				result += "_";
			}
		}
		calculationName.value = result;
	}

	public void showConfigureProperties() {

		for (String key : avaiableProperties) {
			String field = avaiableProperties.$get(key);
			Input checkbox = (Input) Global.window.document.getElementById("selProp_" + field);
			if (checkbox != null && !checkbox.disabled) {
				checkbox.checked = false;
			}
		}

		MessageDialog dialog = new MessageDialog();
		dialog.setSize(NextDialogs.SIZE_LARGE);
		dialog.setTitle("Configurar campos");

		final Element panelDiv = Global.window.document.getElementById("propertiesWizzard");
		final Element panelDivParent = panelDiv.parentNode;
		dialog.appendToBody(panelDiv);

		final ReportDesigner bigThis = this;
		dialog.setCallback(new NextDialogs.DialogCallback() {

			public boolean onClick(String command, Object value, Element button) {
				panelDivParent.appendChild(panelDiv);
				if (command.equals("OK")) {
					bigThis.saveConfigureProperties();
				}
				return true;
			}

		});

		dialog.show();

	}

	@SuppressWarnings("all")
	public void saveConfigureProperties() {
		for (String key : avaiableProperties) {
			String field = avaiableProperties.$get(key);
			Input checkbox = (Input) Global.window.document.getElementById("selProp_" + field);
			if (checkbox != null && !checkbox.disabled && checkbox.checked) {
				addField(checkbox.value, (Map<String, Object>) ((Map) checkbox).$get("propertyMetadata"));
				checkbox.disabled = true;
			}
		}
	}

	public void saveCalculatedProperty() {

		Input calculationExpression = next.dom.toElement("calculationExpression");
		Input calculationDisplayName = next.dom.toElement("calculationDisplayName");
		Input calculationName = next.dom.toElement("calculationName");
		Select calculationProcessor = next.dom.toElement("calculationProcessor");
		Input calculationFormatAsNumber = next.dom.toElement("calculationFormatAsNumber");
		Select calculationFormatAsTimeDetail = next.dom.toElement("calculationFormatAsTimeDetail");

		boolean disabled = calculationDisplayName.disabled;

		String expressionMessage = getValidationErrorMessage(calculationExpression);
		if (expressionMessage != null) {
			Global.alert(expressionMessage);
			return;
		}
		if (ReportPropertyConfigUtils.isEmpty(calculationName.value)) {
			Global.alert("É necessário dar um nome para a variável");
			next.effects.blink(calculationDisplayName);
			calculationDisplayName.focus();
			return;
		}
		if (!disabled) {
			addAvaiableProperty(calculationName.value);
		}
		String formatAs = calculationFormatAsNumber.checked ? "number" : "time";
		String formatTimeDetail = next.dom.getSelectedValue(calculationFormatAsTimeDetail);
		Map<String, Object> calculationProperties = JSCollections.$map("displayName", calculationDisplayName.value,
				"expression", calculationExpression.value,
				"filterable", (Object) false,
				"columnable", (Object) true,
				"numberType", (Object) true,
				"calculated", (Object) true,
				"formatAs", formatAs,
				"formatTimeDetail", formatTimeDetail,
				"processors", next.util.join(next.dom.getSelectedValues(calculationProcessor), ","));
		if (!disabled) {
			addField(calculationName.value, calculationProperties);
		}
		addCalculation(calculationName.value, calculationProperties);

	}

	private String getValidationErrorMessage(Input calculationExpression) {
		String exp = calculationExpression.value;
		return ReportPropertyConfigUtils.validateExpression(exp);
	}

	private void addCalculation(String value, Map<String, Object> calculationProperties) {
		calculatedFieldsManager.add(value, calculationProperties);
	}

	public void addAvaiableProperty(String prop) {
		this.avaiableProperties.push(prop);
	}

	protected void moveItem(int i) {
		ReportElement selectedElement = definition.selectedElement;
		moveElement(i, selectedElement);
	}

	protected void moveElement(int i, ReportElement selectedElement) {
		if (selectedElement != null) {
			if (selectedElement.layoutItem != null) {
				if (selectedElement.layoutItem instanceof FieldDetail) {
					layoutManager.moveFieldDetail((FieldDetail) selectedElement.layoutItem, i);
				}
			}
		}
	}

	protected void removeSelectedDefinitionItem() {
		if (definition.selectedElement != null) {
			if (definition.selectedElement.layoutItem != null) {
				layoutManager.selectAndRemove(definition.selectedElement.layoutItem);
			} else {
				if (definition.selectedElement != null) {
					boolean isGroupHeader = definition.selectedElement.row.section.sectionType == SectionType.GROUP_HEADER;
					if (isGroupHeader) {
						if (confirm("Deseja excluir o grupo?")) {
							groupManager.remove(definition.selectedElement.row.section.group);
						}
					} else {
						definition.remove(definition.selectedElement);
					}
				}
			}
		}
	}

	protected void removeFilter() {
		writeXml();
	}

	protected void removeGroup() {
		writeXml();
	}

	protected void filterCurrentSelectedField() {
		writeXml();
	}

	public void groupCurrentSelectedField() {
		writeXml();
	}

	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
		reportTitleInput.value = reportTitle;
		updateTitle();
	}

	private void updateTitle() {
		reportTitle = reportTitleInput.value;
		definition.sectionTitle.getRow(0).row.cells.$get(0).innerHTML = reportTitle;
		definition.sectionTitle.getRow(0).row.cells.$get(0).onclick = new Function1<DOMEvent, Boolean>() {

			@Override
			public Boolean $invoke(DOMEvent p1) {
				Global.eval("showdesignerTab('designerTab_0', 0, 'designerTab_link_0'); ");
				return true;
			}

		};
		writeXml();
	}

	public void addField(String name, Map<String, Object> properties) {
		if (ReportPropertyConfigUtils.isColumnable(properties)) {
			fields.$put(name, properties);
			fieldSelect.add(name, properties);
		}
		if (this.groupManager.accept(name, properties)) {
			this.groupSelect.add(name, properties);
		}
		if (this.filterManager.accept(name, properties)) {
			this.filterSelect.add(name, properties);
		}
	}

	public void setDataSourceHibernate(String from) {
		this.reportData.dataSourceProvider = new HibernateDataSourceProvider(from);
	}

	public void writeXml() {
		String value = "<report name='" + reportTitle + "'>\n";
		value += reportData.toString();
		value += layoutManager.toString();
		value += getChartsXmlString();
		value += "</report>";
		outputXml.value = value;
	}

	private String getChartsXmlString() {
		String chartXml = "    <charts>\n";
		HTMLCollection<Option> options = charts.options;
		for (int i = 0; i < options.length; i++) {
			chartXml += "        " + options.$get(i).value + "\n";
		}
		chartXml += "    </charts>\n";
		return chartXml;
	}

	public void showInputLabel() {
		next.effects.showProperty(labelInput);
	}

	public void hideInputLabel() {
		next.effects.hideProperty(labelInput);
	}

	public void showInputDatePattern() {
		next.effects.showProperty(patternDateInput);
	}

	public void hideInputDatePattern() {
		next.effects.hideProperty(patternDateInput);
	}

	public void showInputNumberPattern() {
		next.effects.showProperty(patternNumberInput);
	}

	public void hideInputNumberPattern() {
		next.effects.hideProperty(patternNumberInput);
	}

	public void showAggregate() {
		next.effects.showProperty(aggregateInput);
		next.effects.showProperty(aggregateTypeInput);
	}

	public void hideAggregate() {
		next.effects.hideProperty(aggregateInput);
		next.effects.hideProperty(aggregateTypeInput);
	}

	public void hideInputPatternGroup() {
		next.effects.hideProperty(patternDateInputGroup);
	}

	public void showFilterLabel() {
		next.effects.showProperty(filterLabel);
	}

	public void hideFilterLabel() {
		next.effects.hideProperty(filterLabel);
	}

	public void showFilterFixedCriteria() {
		next.effects.showProperty(filterFixedCriteria);
	}

	public void hideFilterFixedCriteria() {
		next.effects.hideProperty(filterFixedCriteria);
	}

	public void showFilterPreSelectDate() {
		next.effects.showProperty(filterPreSelectDate);
	}

	public void hideFilterPreSelectDate() {
		next.effects.hideProperty(filterPreSelectDate);
	}

	public void showFilterPreSelectEntity() {
		next.effects.showProperty(filterPreSelectEntity);
	}

	public void hideFilterPreSelectEntity() {
		next.effects.hideProperty(filterPreSelectEntity);
	}

	public void showFilterSelectMultiple() {
		next.effects.showProperty(filterSelectMultiple);
	}

	public void hideFilterSelectMultiple() {
		next.effects.hideProperty(filterSelectMultiple);
	}

	public void showFilterRequired() {
		next.effects.showProperty(filterRequired);
	}

	public void hideFilterRequired() {
		next.effects.hideProperty(filterRequired);
	}

	public void addChart(ChartConfiguration configuration) {
		Option op = new Option(configuration.title, configuration.toXmlString());
		op.innerHTML = configuration.title;
		updateOptionWithConfiguration(configuration, op);
		charts.appendChild(op);
		writeXml();
	}

	public void updateChart(Option option, ChartConfiguration configuration) {
		updateOptionWithConfiguration(configuration, option);
		option.value = configuration.toXmlString();
		option.innerHTML = configuration.title;
		writeXml();
	}

	@SuppressWarnings("unchecked")
	private void updateOptionWithConfiguration(ChartConfiguration configuration, Option op) {
		Map<String, Object> mapOp = (Map<String, Object>) op;
		mapOp.$put("configuration", configuration);
		configuration.option = op;
	}

	@SuppressWarnings("unchecked")
	public ChartConfiguration getSelectedChartConfiguration() {
		int selectedIndex = charts.selectedIndex;
		if (selectedIndex >= 0) {
			Option op = charts.options.$get(selectedIndex);
			Map<String, Object> mapOp = (Map<String, Object>) op;
			ChartConfiguration configuration = (ChartConfiguration) mapOp.$get("configuration");
			return configuration;
		}
		return null;
	}

	public void removeSelectedChart() {
		int selectedIndex = charts.selectedIndex;
		if (selectedIndex >= 0) {
			charts.removeChild(charts.options.$get(selectedIndex));
		}
		writeXml();
	}

}

class ReportData {

	ReportDesigner designer;

	ReportDataSourceProvider dataSourceProvider;

	public ReportData(ReportDesigner designer) {
		this.designer = designer;
	}

	@Override
	public String toString() {
		String value = "    <data>\n";
		if (dataSourceProvider != null) {
			value += "        " + dataSourceProvider.toString() + "\n";
		}
		value += designer.groupManager.toString();
		value += designer.filterManager.toString();
		value += designer.calculatedFieldsManager.toString();
		value += "    </data>\n";
		return value;
	}

}

interface ReportDataSourceProvider {

	String toString();

}

class HibernateDataSourceProvider implements ReportDataSourceProvider {

	String fromClass;

	public HibernateDataSourceProvider(String fromClass) {
		this.fromClass = fromClass;
	}

	@Override
	public String toString() {
		return "<dataSourceProvider type='hibernateDataProvider' fromClass='" + fromClass + "'/>";
	}

}

@SuppressWarnings({ "unused", "unchecked" })
class ReportLayoutManager {

	ReportDesigner designer;

	Array<LayoutItem> items;

	private ReportGeneratorSelectManyBoxView fieldSelect;

	public ReportLayoutManager(ReportDesigner designer, ReportGeneratorSelectManyBoxView fieldSelect) {

		this.items = $array();
		this.designer = designer;
		this.fieldSelect = fieldSelect;

		final ReportLayoutManager bigThis = this;

		((Map<String, Object>) fieldSelect.getFrom()).$put("onAdd", new Callback1<Option>() {

			@Override
			public void $invoke(Option p1) {
				Map<String, Object> properties = (Map<String, Object>) ((Map<String, Object>) p1).$get("properties");
				bigThis.addFieldDetail(p1.value, properties);
			}

		});

		((Map<String, Object>) fieldSelect.getFrom()).$put("onRemove", new Callback1<Option>() {

			@Override
			public void $invoke(Option p1) {
				Map<String, Object> properties = (Map<String, Object>) ((Map<String, Object>) p1).$get("properties");
				bigThis.removeByName(p1.value);
			}

		});

		fieldSelect.onselectto = new Callback1<Option>() {

			@Override
			public void $invoke(Option p1) {
				if (p1 != null) {
					bigThis.selectElementByName(p1.value, true);
				}
			}

		};

	}

	protected void selectElementByName(String name, boolean cascade) {
		select(getElementByName(name), cascade);
	}

	private FieldDetail getElementByName(String name) {
		for (String key : items) {
			LayoutItem li = items.$get(key);
			if (li instanceof FieldDetail) {
				FieldDetail fd = (FieldDetail) li;
				if (fd.name.equals(name)) {
					return fd;
				}
			}
		}
		return null;
	}

	public void select(final FieldDetail fd, boolean cascadeToDefinition) {

		if (fd == null) {
			return;
		}

		fieldSelect.markSelected(fd.name);

		if (cascadeToDefinition) {
			designer.definition.selectItem(fd.label, false);
		}

		Global.eval("showdesignerTab('designerTab_1', 1, 'designerTab_link_1');"); //passar para a aba de fields

		next.effects.showProperty(designer.labelInput);
		ReportPropertyConfigUtils.configureInputToLabel(fd.label, designer.labelInput);

		if (fd.isDate()) {
			next.effects.showProperty(designer.patternDateInput);
			ReportPropertyConfigUtils.configurePatternInputToField(fd.field, designer.patternDateInput);
		}

		if (fd.isNumber()) {
			next.effects.showProperty(designer.patternNumberInput);
			ReportPropertyConfigUtils.configurePatternInputToField(fd.field, designer.patternNumberInput);
		}

		if (fd.isAggregatable()) {
			next.effects.showProperty(designer.aggregateInput);
			next.effects.showProperty(designer.aggregateTypeInput);
			ReportPropertyConfigUtils.configureFieldToAggregateInputs(fd, designer.aggregateInput, designer.aggregateTypeInput);
		}

	}

	public void selectAndRemove(LayoutItem layoutItem) {
		if (layoutItem instanceof FieldDetail) {
			fieldSelect.unselect(((FieldDetail) layoutItem).name);
		}
	}

	public void selectElement(String name, Map<String, Object> value, String label, String pattern, boolean aggregate, String aggregateType) {
		this.fieldSelect.select(name, null);//to select in the selectmanybox the properties are not necessary
		for (String key : items) {
			LayoutItem li = items.$get(key);
			if (li instanceof FieldDetail) {
				FieldDetail fd = (FieldDetail) li;
				if (fd.name.equals(name)) {
					if (label.length() > 0) {
						fd.label.label = label;
						fd.label.changed = true;
						fd.label.getNode().innerHTML = label;
					}
					fd.aggregateType = aggregateType;
					fd.setAggregate(aggregate);
					fd.field.pattern = pattern;
					break;
				}
			}
		}
	}

	protected void removeByName(String value) {
		for (String key : items) {
			LayoutItem li = items.$get(key);
			if (li instanceof FieldDetail) {
				FieldDetail fd = (FieldDetail) li;
				if (fd.name.equals(value)) {
					remove(li);
					break;
				}
			}
		}
	}

	public void moveFieldDetail(FieldDetail layoutItem, int i) {

		if (i != 1 && i != -1) {
			alert("It is not possible to move fieldDetail more than one column");
			return;
		}

		ReportColumn oldColumn = layoutItem.label.column;
		int newColumnIndex = layoutItem.label.column.getIndex() + i;
		if (newColumnIndex < 0 || newColumnIndex >= designer.definition.columns.$length()) {
			return;
		}
		ReportColumn newColumn = designer.definition.getColumnByIndex(newColumnIndex);
		ReportElement headerItem = designer.definition.getElementForRowAndColumn(designer.definition.sectionDetailHeader.getLastRow(), newColumn);
		ReportElement detailItem = designer.definition.getElementForRowAndColumn(designer.definition.sectionDetail.getLastRow(), newColumn);

		TableCell headerCell = designer.definition.sectionDetailHeader.getLastRow().getTdForColumn(newColumn);
		TableCell detailCell = designer.definition.sectionDetail.getLastRow().getTdForColumn(newColumn);
		TableCell oldHeaderCell = layoutItem.label.getCell();
		TableCell oldDetailCell = layoutItem.field.getCell();

		if (headerItem != null) {
			headerCell.removeChild(headerItem.getNode());
		}
		if (detailItem != null) {
			detailCell.removeChild(detailItem.getNode());
		}

		oldHeaderCell.removeChild(layoutItem.label.getNode());
		oldDetailCell.removeChild(layoutItem.field.getNode());

		headerCell.appendChild(layoutItem.label.getNode());
		detailCell.appendChild(layoutItem.field.getNode());

		layoutItem.label.column = newColumn;
		layoutItem.field.column = newColumn;

		if (headerItem != null) {
			oldHeaderCell.appendChild(headerItem.getNode());
			headerItem.column = oldColumn;
		}
		if (detailItem != null) {
			oldDetailCell.appendChild(detailItem.getNode());
			detailItem.column = oldColumn;
		}

		if (headerItem != null && headerItem.layoutItem != null) {
			//int index = items.indexOf(layoutItem);
			int index = NextGlobalJs.next.util.indexOf(items, layoutItem);
			items.splice(index, 1);
			items.splice(index + i, 0, layoutItem);
		}

		writeXML();

	}

	public FieldDetail addFieldDetail(String fieldName, Map<String, Object> value) {

		ReportDefinition definition = designer.definition;
		int atColumn = definition.columns.$length();
		while (atColumn > 0) {
			if (definition.getElementForRowAndColumn(definition.sectionDetailHeader.getLastRow(), definition.getColumnByIndex(atColumn - 1)) == null
					&& definition.getElementForRowAndColumn(definition.sectionDetail.getLastRow(), definition.getColumnByIndex(atColumn - 1)) == null) {
				atColumn--;
			} else {
				break;
			}
		}

		LabelReportElement label = new LabelReportElement(fieldName, value);
		FieldReportElement field = new FieldReportElement(fieldName, value);

		final FieldDetail fd = new FieldDetail(this, fieldName, label, field, value);

		label.layoutItem = fd;
		field.layoutItem = fd;

		final ReportLayoutManager bigThis = this;
		label.onFocus = new Callback0() {

			public void $invoke() {
				bigThis.select(fd, false);
			}

		};

		items.push(fd);

		definition.addElement(label, definition.sectionDetailHeader, atColumn);
		definition.addElement(field, definition.sectionDetail, atColumn);

		writeXML();

		return fd;
	}

	public void writeXML() {
		designer.writeXml();
	}

	public String toString() {
		String result = "    <layout>\n";
		for (String key : items) {
			LayoutItem item = items.$get(key);
			result += "        " + item.toString();
			result += "\n";
		}
		result += "    </layout>\n";
		return result;
	}

	public void remove(LayoutItem layoutItem) {
		int columnIndex = -1;
		if (layoutItem instanceof FieldDetail) {
			columnIndex = ((FieldDetail) layoutItem).label.column.getIndex() + 1;
		}
		Array<ReportElement> elements = layoutItem.getElements();
		layoutItem.clearElements();
		for (String key : elements) {
			designer.definition.remove(elements.$get(key));
		}
		if (layoutItem instanceof FieldDetail) {
			for (int i = columnIndex; i < designer.definition.columns.$length(); i++) {
				Array<ReportSection> allSections = designer.definition.getAllSections();
				for (int j = 0; j < allSections.$length(); j++) {
					ReportSection section = allSections.$get(j);
					ReportElement elementForRowAndColumn = designer.definition.getElementForRowAndColumn(section.getLastRow(), designer.definition.getColumnByIndex(i));
					designer.moveElement(-1, elementForRowAndColumn);
				}
			}

			int columnToRemove = designer.definition.columns.$length() - 1;
			if (columnToRemove != 0 || designer.groupManager.objects.$length() == 0) {
				designer.definition.removeColumn(columnToRemove);
			}
		}
		next.util.removeItem(items, layoutItem);
		writeXML();
	}

}
