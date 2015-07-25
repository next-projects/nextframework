package org.nextframework.report.generator.mvc.resource;

import static org.nextframework.js.NextGlobalJs.next;
import static org.stjs.javascript.Global.alert;
import static org.stjs.javascript.Global.confirm;
import static org.stjs.javascript.JSCollections.$array;

import org.nextframework.js.NextGlobalJs;
import org.nextframework.resource.NextDialogs;
import org.nextframework.resource.NextDialogs.InputNumberMessageDialog;
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
	
	public static ReportDesigner getInstance(){
		if(instance == null){
			instance = new ReportDesigner("designerArea", "xml");
		}
		return instance;
	}
	
	String controllerPath;
	
	String reportTitle;
	Boolean reportPublic;
	
	Div mainDiv;
	TextArea outputXml;
	
//	ReportGeneratorSelectView fieldArea;
	//ReportGeneratorSelectView groupArea;
//	ReportGeneratorSelectView filterArea;
	
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
	
//	private Button addFieldButton;
//	private Button clearReportButton;
//	private Button groupFieldButton;
//	private Button removeFilterButton;
//	private Button removeGroupButton;
//	private Button removeDefinitionItemButton;
//	private Button filterFieldButton;
	
//	private Button moveLeftButton;
//	private Button moveRightButton;
	
	private Input reportTitleInput;
	private ReportData reportData;
	
	Input labelInput;
	Input filterLabel;
	Input filterSelectMultiple;
	Input filterRequired;
	Select filterPreSelectDate;
	Select filterPreSelectEntity;
	Select patternDateInput;
	Select patternNumberInput;
	Select patternDateInputGroup;
	Input aggregateInput;
	Select aggregateTypeInput;
	
	Select charts;

	Array<Selectable> selectables;
	

	public ReportDesigner(String divId, String textAreaId){
		final ReportDesigner bigThis = this;
		selectables = $array();
		avaiableProperties = $array();
		 
		mainDiv = (Div) next.dom.toElement(divId);
		outputXml = (TextArea) next.dom.toElement(textAreaId);

//		fieldArea = new ReportGeneratorSelectView((Div)next.dom.getInnerElementById(mainDiv, "fieldAreaBody"));
//		fieldArea.setUsePropertyAsLabel("displayName");
//		fieldArea.onselect = new Callback1<ReportGeneratorSelectViewItem>() {
//			public void $invoke(ReportGeneratorSelectViewItem p1) {
//				bigThis.groupArea.blur();
//				bigThis.filterArea.blur();
//				bigThis.definition.blur();
//			}
//		};
//		
//		groupArea = new ReportGeneratorSelectView((Div)next.dom.getInnerElementById(mainDiv, "groupAreaBody"));
//		groupArea.setUsePropertyAsLabel("displayName");
//		groupArea.onselect = new Callback1<ReportGeneratorSelectViewItem>() {
//			public void $invoke(ReportGeneratorSelectViewItem p1) {
//				bigThis.fieldArea.blur();
//				bigThis.filterArea.blur();
//				bigThis.definition.blur();
//			}
//		};
//		
//		filterArea = new ReportGeneratorSelectView((Div)next.dom.getInnerElementById(mainDiv, "filterAreaBody"));
//		filterArea.setUsePropertyAsLabel("displayName");
//		filterArea.onselect = new Callback1<ReportGeneratorSelectViewItem>() {
//			public void $invoke(ReportGeneratorSelectViewItem p1) {
//				bigThis.fieldArea.blur();
//				bigThis.groupArea.blur();
//				bigThis.definition.blur();
//			}
//		};
		
//		fieldArea = addSelectComponent("fieldArea");
		//groupArea = addSelectComponent("groupArea");
//		filterArea = addSelectComponent("filterArea");
		
		fields = JSCollections.$map();
		
		groupSelect = addSelectManyComponent("groups");
		filterSelect = addSelectManyComponent("filters");
		fieldSelect = addSelectManyComponent("fields");
		
		calculatedFieldsSelect = next.dom.toElement("calculatedFields");
		
		
//		fieldArea.onDblClick = new ReportGeneratorSelectViewEvent() {
//			public void invoke(ReportGeneratorSelectViewItem selectedItem) {
//				bigThis.layoutManager.addFieldDetail(selectedItem.name, selectedItem.value);
//			}
//		};
		
		
		labelInput = (Input) next.dom.getInnerElementById(mainDiv, "label");
		filterLabel = (Input) next.dom.getInnerElementById(mainDiv, "filterLabel"); 
		filterSelectMultiple = (Input) next.dom.getInnerElementById(mainDiv, "filterSelectMultiple"); 
		filterRequired = (Input) next.dom.getInnerElementById(mainDiv, "filterRequired"); 
		filterPreSelectDate = (Select) next.dom.getInnerElementById(mainDiv, "filterPreSelectDate");
		filterPreSelectEntity = (Select) next.dom.getInnerElementById(mainDiv, "filterPreSelectEntity");
		patternDateInput = (Select) next.dom.getInnerElementById(mainDiv, "patternDate");
		patternNumberInput = (Select) next.dom.getInnerElementById(mainDiv, "patternNumber");
		patternDateInputGroup = (Select) next.dom.getInnerElementById(mainDiv, "patternDateGroup");
		
		reportTitleInput = (Input) next.dom.getInnerElementById(mainDiv, "reportTitle");
		reportTitleInput.onkeyup = new Function1<DOMEvent, Boolean>() {
			@Override
			public Boolean $invoke(DOMEvent p1) {
				bigThis.updateTitle();
				return true;
			}
		};
		
		aggregateInput = (Input) next.dom.getInnerElementById(mainDiv, "aggregate");
		aggregateTypeInput = (Select) next.dom.getInnerElementById(mainDiv, "aggregateType");
		
		charts = (Select) next.dom.getInnerElementById(mainDiv, "charts");

		definition = new ReportDefinition(this, (Table) next.dom.getInnerElementById(mainDiv, "designTable"));
		selectables.push(definition);
		
		groupManager = new ReportGroupManager(this, this.groupSelect);
		filterManager = new ReportFilterManager(this, this.filterSelect);
		layoutManager = new ReportLayoutManager(this, this.fieldSelect);
		calculatedFieldsManager = new ReportCalculatedFieldsManager(this, this.calculatedFieldsSelect);
		
		reportData = new ReportData(this);
	}

	private ReportGeneratorSelectManyBoxView addSelectManyComponent(String name) {
		final ReportGeneratorSelectManyBoxView result = new ReportGeneratorSelectManyBoxView((Select) next.dom.toElement(name+"_from_"));
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
	
	public void selectTypeCalculatedProperty(String type) {
		if(type == "custom") {
			next.dom.toElement("c_customized").style.display = "";
			next.dom.toElement("c_system").style.display = "none";
		} else {
			next.dom.toElement("c_customized").style.display = "none";
			next.dom.toElement("c_system").style.display = "";
		}
	}
	
	public void removeSelectedCalculatedProperty(){
		int selectedIndex = calculatedFieldsSelect.selectedIndex;
		if(selectedIndex >= 0){
			String fieldName = calculatedFieldsSelect.options.$get(selectedIndex).value;
			calculatedFieldsManager.remove(fieldName);
			fieldSelect.unselect(fieldName);
			
			next.dom.removeSelectValue(fieldSelect.getFrom(), fieldName);
			calculatedFieldsSelect.removeChild(calculatedFieldsSelect.options.$get(selectedIndex));
		}
		
		writeXml();
		Global.alert("O campo calculado foi removido. É necessário remover também (se houver) todas as referências para esse campo no relatório.");
	}
	
	public void editCalculatedProperty(){
		final Select calculatedFields = next.dom.toElement("calculatedFields");
		String calculatedFieldSelected = next.dom.getSelectedValue(calculatedFields);
//		Global.alert(calculatedFieldSelected);
		if(calculatedFieldSelected == null){
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
//		calculationFormatAsTimeDetail
//		next.dom.getSelectedText(el)
		
	}
	
	public void showAddCalculatedProperty(){
		Global.window.document.getElementById("calculatedPropertiesWizzard").style.visibility = "";
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
		
		final ReportDesigner bigThis = this;
		
		for (String key : avaiableProperties) {
			String property = avaiableProperties.$get(key);
			Map<String, Object> options = fields.$get(property);
			Global.console.info(property + " "+options);
			if(options != null && 
					(ReportPropertyConfigUtils.isNumber(options)
							|| ReportPropertyConfigUtils.isDate(options))){
				Button b = next.dom.newElement("button");
				b.className = "calculationButton calculationPropertyButton";
				b.innerHTML = (String) options.$get("displayName");
				configureButtonAppendCalculatedVar(b, property, options);
				varDiv.appendChild(b);
			}
		}
		
		if(calculationDisplayName.getAttribute("data-configured") == null){
			next.events.attachEvent(calculationDisplayName, "change", new Callback1<DOMEvent>() {
				public void $invoke(DOMEvent p1) {
					bigThis.onChangeCalculationVarName(calculationDisplayName, calculationName);
				}
			});
		}
		
//		ReportDesigner.getInstance().selectTypeCalculatedProperty("custom");
	}
	
	private void configureButtonAppendCalculatedVar(final Button b, final String property, Map<String, Object> options) {
		final ReportDesigner bigThis = this;
		b.onclick = new Function1<DOMEvent, Boolean>() {

			@Override
			public Boolean $invoke(DOMEvent p1) {
				bigThis.appendToExpression(property);
				return true;
			}
		};
	}

	public void appendNumberToExpression(){
		final ReportDesigner bigThis = this;
		InputNumberMessageDialog dialog = next.dialogs.showInputNumberDialog("Inserir Número", "Digite o número que deseja inserir na fórmula:");
		dialog.setCallback(new NextDialogs.DialogCallback() {
			@Override
			public void onClose(String command, Object value) {
				if(command == NextDialogs.OK){
					bigThis.appendToExpression(""+value);
				}
			}
		});
	}
	
	public void appendToExpression(String varText){
		Input calculationExpression = next.dom.toElement("calculationExpression");
		if(varText.charAt(0) == '$'){
			char c = varText.charAt(1);
			switch (c) {
			case 'B':
				String result = calculationExpression.value;
				result = result.substring(0, result.length()-1);
				int space = result.lastIndexOf(' ');
				if(space <= 0){
					result = "";
				} else {
					result = result.substring(0, space) + " ";
				}
				calculationExpression.value = result;
				break;
			case 'C':
				calculationExpression.value = "";
				break;
			}
		} else {
			calculationExpression.value = calculationExpression.value + varText + " ";
		}
		String errorMessage = ReportPropertyConfigUtils.validateExpression(calculationExpression.value);
		if(errorMessage != null){
			next.dom.toElement("validationExpressionError").innerHTML = errorMessage;
		} else {
			next.dom.toElement("validationExpressionError").innerHTML = "";
		}
	}
	
	public void onChangeCalculationVarName(Input calculationDisplayName, Input calculationName){
		String value = calculationDisplayName.value;
		String result = "";
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if(result == "" && ReportPropertyConfigUtils.isDigit(c)){
				result += "_";
			} else if(ReportPropertyConfigUtils.isLetter(c) || ReportPropertyConfigUtils.isDigit(c)){
				result += c;
			} else {
				result += "_";
			}
		}
		calculationName.value = result;
	}
	
	public void showConfigureProperties() {
		Global.window.document.getElementById("propertiesWizzard").style.visibility = "";
		for(String key: avaiableProperties){
			String field = avaiableProperties.$get(key);
			Input checkbox = (Input) Global.window.document.getElementById("selProp_"+field);
			if(!checkbox.disabled){
				checkbox.checked = false;
			}
		}
	}
	
	public void hideConfigureProperties(){
		Global.window.document.getElementById("propertiesWizzard").style.visibility = "hidden";
	}
	
	public void hideAddCalculatedProperty(){
		Global.window.document.getElementById("calculatedPropertiesWizzard").style.visibility = "hidden";
	}
	
	public void saveCalculatedProperty(){
//		Input calculatedCustom = next.dom.toElement("c_c");
//		Input calculatedSystem = next.dom.toElement("c_s");
//		if(calculatedCustom.checked){
		Input calculationExpression = next.dom.toElement("calculationExpression");
		Input calculationDisplayName = next.dom.toElement("calculationDisplayName");
		Input calculationName = next.dom.toElement("calculationName");
		Select calculationProcessor = next.dom.toElement("calculationProcessor");
		
		Input calculationFormatAsNumber = next.dom.toElement("calculationFormatAsNumber");
		Select calculationFormatAsTimeDetail = next.dom.toElement("calculationFormatAsTimeDetail");
		
		boolean editing = calculationDisplayName.disabled;
		
		String expressionMessage = getValidationErrorMessage(calculationExpression);
		if(expressionMessage != null){
			Global.alert(expressionMessage);
			return;
		}
		if(ReportPropertyConfigUtils.isEmpty(calculationName.value)){
			Global.alert("É necessário dar um nome para a variável");
			next.effects.blink(calculationDisplayName);
			calculationDisplayName.focus();
			return;
		}
		if(!editing){
			addAvaiableProperty(calculationName.value);
		}
		String formatAs = calculationFormatAsNumber.checked? "number" : "time";
		String formatTimeDetail = next.dom.getSelectedValue(calculationFormatAsTimeDetail);
		Map<String, Object> calculationProperties = 
				JSCollections.$map(	"displayName", 	calculationDisplayName.value,
									"expression", 	calculationExpression.value,
									"filterable", 	(Object)false,
									"numberType", 	(Object)true,
									"calculated",	(Object)true,
									"formatAs",		formatAs,
									"formatTimeDetail", formatTimeDetail,
									"processors", 	next.util.join(next.dom.getSelectedValues(calculationProcessor), ","));
		if(!editing){
			addField(calculationName.value, calculationProperties);
		}
		addCalculation(calculationName.value, calculationProperties);
//		}
//		if(calculatedSystem.checked){
//			
//		}
		
		hideAddCalculatedProperty();
	}
	
	private String getValidationErrorMessage(Input calculationExpression) {
		String exp = calculationExpression.value;
		return ReportPropertyConfigUtils.validateExpression(exp);
	}

	private void addCalculation(String value, Map<String, Object> calculationProperties) {
		calculatedFieldsManager.add(value, calculationProperties);
	}

	@SuppressWarnings("unchecked")
	public void saveConfigureProperties(){
		hideConfigureProperties();
		for(String key: avaiableProperties){
			String field = avaiableProperties.$get(key);
			Input checkbox = (Input) Global.window.document.getElementById("selProp_"+field);
			if(!checkbox.disabled){
				if(checkbox.checked){
					addField(checkbox.value, (Map<String, Object>)((Map)checkbox).$get("propertyMetadata"));
					checkbox.disabled = true;
				}
			}
		}
	}
	
	public void addAvaiableProperty(String prop){
		this.avaiableProperties.push(prop);
	}
	
//	private ReportGeneratorSelectView addSelectComponent(String field){
//		final ReportGeneratorSelectView select = new ReportGeneratorSelectView((Div)next.dom.getInnerElementById(mainDiv, field+"Body"));
//		select.setUsePropertyAsLabel("displayName");
//		
//		final ReportDesigner bigThis = this;
//
//		select.onselect = new Callback1<ReportGeneratorSelectViewItem>() {
//			public void $invoke(ReportGeneratorSelectViewItem p1) {
//				bigThis.blurAllBut(select);
//			}
//		};
//		
//		selectables.push(select);
//		
//		return select;
//	}
	

	protected void moveItem(int i) {
		ReportElement selectedElement = definition.selectedElement;
		moveElement(i, selectedElement);
	}

	protected void moveElement(int i, ReportElement selectedElement) {
		if(selectedElement != null){
			if(selectedElement.layoutItem != null){
				if(selectedElement.layoutItem instanceof FieldDetail){
					layoutManager.moveFieldDetail((FieldDetail)selectedElement.layoutItem, i);
				}
			}
		}
	}

	protected void removeSelectedDefinitionItem() {
		if(definition.selectedElement != null){
			if(definition.selectedElement.layoutItem != null){
				layoutManager.selectAndRemove(definition.selectedElement.layoutItem);
			} else {
				if(definition.selectedElement != null){
					boolean isGroupHeader = definition.selectedElement.row.section.sectionType == SectionType.GROUP_HEADER;
					if(isGroupHeader){
						if(confirm("Deseja excluir o grupo?")){
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
//		ReportGeneratorSelectViewItem selectedItem = filterArea.selectedItem;
//		if(selectedItem != null){
//			filterManager.remove(selectedItem.name);
//		}
		writeXml();
	}
	
	protected void removeGroup() {
//		ReportGeneratorSelectViewItem selectedItem = groupArea.selectedItem;
//		if(selectedItem != null){
//			groupManager.remove(selectedItem.name);
//		}
		writeXml();
	}

	protected void filterCurrentSelectedField() {
//		ReportGeneratorSelectViewItem selectedItem = fieldArea.selectedItem;
//		if(selectedItem != null){
//			if(filterManager.contains(selectedItem.name)){
//				return;
//			}
//			filterManager.addElement(selectedItem.name, selectedItem.value);
//		}
		writeXml();
	}
	
	public void groupCurrentSelectedField() {
//		ReportGeneratorSelectViewItem selectedItem = fieldArea.selectedItem;
//		if(selectedItem != null){
//			if(groupManager.contains(selectedItem.name)){
//				return;
//			}
//			groupManager.addElement(selectedItem.name, selectedItem.value);
//		}
		writeXml();
	}
	
	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
		reportTitleInput.value = reportTitle;
		updateTitle();
	}
	
	private void updateTitle(){
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
	 

	public void addField(String name, Map<String, Object> properties){
		//fieldArea.select(name, properties);
		fields.$put(name, properties);
		fieldSelect.add(name, properties);
		
		if(this.groupManager.accept(name, properties)){
			this.groupSelect.add(name, properties);
		}
		if(this.filterManager.accept(name, properties)){
			this.filterSelect.add(name, properties);
			if(ReportPropertyConfigUtils.isFilterRequired(properties)){
				this.filterSelect.select(name, null);
			}
		}
	}
	
	public void setDataSourceHibernate(String from){
		this.reportData.dataSourceProvider = new HibernateDataSourceProvider(from);
	}
	
	public void writeXml(){
		String value = "<report name='"+reportTitle+"'>\n";
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
			chartXml += "        "+options.$get(i).value + "\n";
		}
		chartXml += "    </charts>\n";
		return chartXml;
	}

	public void showInputDatePattern() {
		ReportPropertyConfigUtils.showElement(patternDateInput);
	}
	public void showInputNumberPattern() {
		ReportPropertyConfigUtils.showElement(patternNumberInput);
	}
	public void hideInputNumberPattern() {
		ReportPropertyConfigUtils.hideElement(patternNumberInput);
	}	
	public void showInputLabel() {
		((Element)labelInput.parentNode.parentNode).style.display = "";		
	}
	public void showFilterSelectMultiple() {
		((Element)filterSelectMultiple.parentNode.parentNode).style.display = "";		
	}
	public void showFilterRequired() {
		((Element)filterRequired.parentNode.parentNode).style.display = "";		
	}
	public void showFilterPreSelectDate() {
		((Element)filterPreSelectDate.parentNode.parentNode).style.display = "";		
	}
	public void showFilterPreSelectEntity() {
		((Element)filterPreSelectEntity.parentNode.parentNode).style.display = "";		
	}
	public void showFilterLabel() {
		((Element)filterLabel.parentNode.parentNode).style.display = "";		
	}
	public void hideInputDatePattern() {
		((Element)patternDateInput.parentNode.parentNode).style.display = "none";
	}
	public void hideInputPatternGroup() {
		ReportPropertyConfigUtils.hideElement(patternDateInputGroup);
	}

	public void hideInputLabel() {
		((Element)labelInput.parentNode.parentNode).style.display = "none";
	}
	public void hideFilterSelectMultiple() {
		((Element)filterSelectMultiple.parentNode.parentNode).style.display = "none";
	}
	public void hideFilterRequired() {
		((Element)filterRequired.parentNode.parentNode).style.display = "none";
	}
	public void hideFilterPreSelectDate() {
		((Element)filterPreSelectDate.parentNode.parentNode).style.display = "none";		
	}
	public void hideFilterPreSelectEntity() {
		((Element)filterPreSelectEntity.parentNode.parentNode).style.display = "none";		
	}
	public void hideFilterLabel() {
		((Element)filterLabel.parentNode.parentNode).style.display = "none";
	}
	
	public void showAggregate() {
		((Element)aggregateInput.parentNode.parentNode).style.display = "";		
		((Element)aggregateTypeInput.parentNode.parentNode).style.display = "";		
	}
	
	public void hideAggregate() {
		((Element)aggregateInput.parentNode.parentNode).style.display = "none";		
		((Element)aggregateTypeInput.parentNode.parentNode).style.display = "none";		
	}

	public void blurAllBut(Selectable select) {
		for(String key : selectables){
			Selectable value = selectables.$get(key);
			if(value != select){
				value.blur();
			}
		}
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
	public ChartConfiguration getSelectedChartConfiguration(){
		int selectedIndex = charts.selectedIndex;
		if(selectedIndex >= 0){
			Option op = charts.options.$get(selectedIndex);
			Map<String, Object> mapOp = (Map<String, Object>) op;
			ChartConfiguration configuration = (ChartConfiguration) mapOp.$get("configuration");
			return configuration;
		}
		return null;
	}
	
	public void removeSelectedChart(){
		int selectedIndex = charts.selectedIndex;
		if(selectedIndex >= 0){
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
		if(dataSourceProvider != null){
			value += "        "+dataSourceProvider.toString() + "\n";
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
		return "<dataSourceProvider type='hibernateDataProvider' fromClass='"+fromClass+"'/>";
	}
}

@SuppressWarnings({"unused", "unchecked"})
class ReportLayoutManager {
	
	ReportDesigner designer;
	
	Array<LayoutItem> items;

	private ReportGeneratorSelectManyBoxView fieldSelect;

	public ReportLayoutManager(ReportDesigner designer, ReportGeneratorSelectManyBoxView fieldSelect) {
		this.items = $array();
		this.designer = designer;
		this.fieldSelect = fieldSelect;
		
		final ReportLayoutManager bigThis = this;
		
		((Map<String, Object>)fieldSelect.getFrom()).$put("onAdd", new Callback1<Option>() {
			@Override
			public void $invoke(Option p1) {
				Map<String, Object> properties = (Map<String, Object>) ((Map<String, Object>)p1).$get("properties");
				bigThis.addFieldDetail(p1.value, properties);
				
//				if(bigThis.fieldSelect.getTo().options.length > 10){
//					Global.alert("O maximo de campos para o relatorio e 10.");
//					bigThis.fieldSelect.unselect(p1.value);
//				}
			}
		});
		((Map<String, Object>)fieldSelect.getFrom()).$put("onRemove", new Callback1<Option>() {
			@Override
			public void $invoke(Option p1) {
				Map<String, Object> properties = (Map<String, Object>) ((Map<String, Object>)p1).$get("properties");
				bigThis.removeByName(p1.value);
			}
		});
		
		fieldSelect.onselectto = new Callback1<Option>() {
			@Override
			public void $invoke(Option p1) {
				if(p1 != null){
					bigThis.selectElementByName(p1.value, true);
				}
			}
		};
	}
	
	protected void selectElementByName(String name, boolean cascade) {
		select(getElementByName(name), cascade);
	}

	private FieldDetail getElementByName(String name){
		for (String key : items) {
			LayoutItem li = items.$get(key);
			if(li instanceof FieldDetail){
				FieldDetail fd = (FieldDetail) li;
				if(fd.name.equals(name)){
					return fd;
				}
			}
		}
		return null;
	}
	
	public void select(final FieldDetail fd, boolean cascadeToDefinition) {
		if(fd == null){
			return;
		}
		
		fieldSelect.markSelected(fd.name);
		
		if(cascadeToDefinition){
			designer.definition.selectItem(fd.label, false);
		}
		
		Global.eval("showdesignerTab('designerTab_1', 1, 'designerTab_link_1');"); //passar para a aba de fields
		
		ReportPropertyConfigUtils.configureInputToLabel(fd.label, designer.labelInput);
		
//		Global.console.info("select(...)");
//		Global.console.info("fd.name = "+fd.name);
//		Global.console.info("fd.isAggregatable() = "+fd.isAggregatable());
		
		if(fd.isAggregatable()){
			ReportPropertyConfigUtils.configureFieldToAggregateInputs(fd, designer.aggregateInput, designer.aggregateTypeInput);
		}
		
		if(fd.isDate()){
			ReportPropertyConfigUtils.configurePatternInputToField(fd.field, designer.patternDateInput);
		}
		if(fd.isNumber()) {
			ReportPropertyConfigUtils.configurePatternInputToField(fd.field, designer.patternNumberInput);
		}
	
	}

	public void selectAndRemove(LayoutItem layoutItem) {
		if(layoutItem instanceof FieldDetail){
			fieldSelect.unselect(((FieldDetail)layoutItem).name);
		}
	}

	/*
	 * Called from the view
	 * Select a property and set the saved attributes
	 */
	public void selectElement(String name, Map<String,Object> value, String label, String pattern, boolean aggregate, String aggregateType){
		this.fieldSelect.select(name, null);//to select in the selectmanybox the properties are not necessary
		for (String key : items) {
			LayoutItem li = items.$get(key);
			if(li instanceof FieldDetail){
				FieldDetail fd = (FieldDetail) li;
				if(fd.name.equals(name)){
					if(label.length() > 0){
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
	
//	public FieldDetail addFieldDetailWithConfig(String fieldName, Map<String,Object> value, String label, String pattern, boolean aggregate, String aggregateType){
//		FieldDetail fieldDetail = addFieldDetail(fieldName, value);
//		if(label.length() > 0){
//			fieldDetail.label.label = label;
//			fieldDetail.label.changed = true;
//			fieldDetail.label.getNode().innerHTML = label;
//		}
//		fieldDetail.aggregateType = aggregateType;
//		fieldDetail.setAggregate(aggregate);
//		fieldDetail.field.pattern = pattern;
//		
//		designer.fieldSelect.select(fieldName, null);// the properties are not necessary for selectmany
//		return fieldDetail;
//	}
	
	protected void removeByName(String value) {
		for (String key : items) {
			LayoutItem li = items.$get(key);
			if(li instanceof FieldDetail){
				FieldDetail fd = (FieldDetail) li;
				if(fd.name.equals(value)){
					remove(li);
					break;
				}
			}
		}
	}

	public void moveFieldDetail(FieldDetail layoutItem, int i) {
		if(i != 1 && i != -1){
			alert("It is not possible to move fieldDetail more than one column");
			return;
		}
		
		ReportColumn oldColumn = layoutItem.label.column;
		int newColumnIndex = layoutItem.label.column.getIndex() + i;
		if(newColumnIndex < 0 || newColumnIndex >= designer.definition.columns.$length()){
			return;
		}
		ReportColumn newColumn = designer.definition.getColumnByIndex(newColumnIndex);
		ReportElement headerItem = designer.definition.getElementForRowAndColumn(designer.definition.sectionDetailHeader.getLastRow(), newColumn);
		ReportElement detailItem = designer.definition.getElementForRowAndColumn(designer.definition.sectionDetail.getLastRow(), newColumn);
		
		TableCell headerCell = designer.definition.sectionDetailHeader.getLastRow().getTdForColumn(newColumn);
		TableCell detailCell = designer.definition.sectionDetail.getLastRow().getTdForColumn(newColumn);
		TableCell oldHeaderCell = layoutItem.label.getCell();
		TableCell oldDetailCell = layoutItem.field.getCell();
		
		if(headerItem != null){
			headerCell.removeChild(headerItem.getNode());
		} 
		if(detailItem != null){
			detailCell.removeChild(detailItem.getNode());
		}
		
		oldHeaderCell.removeChild(layoutItem.label.getNode());
		oldDetailCell.removeChild(layoutItem.field.getNode());
		
		headerCell.appendChild(layoutItem.label.getNode());
		detailCell.appendChild(layoutItem.field.getNode());
		
		layoutItem.label.column = newColumn;
		layoutItem.field.column = newColumn;
		
		if(headerItem != null){
			oldHeaderCell.appendChild(headerItem.getNode());
			headerItem.column = oldColumn;
		}
		if(detailItem != null){
			oldDetailCell.appendChild(detailItem.getNode());
			detailItem.column = oldColumn;
		}
		
		if(headerItem != null && headerItem.layoutItem != null){
			//int index = items.indexOf(layoutItem);
			int index = NextGlobalJs.next.util.indexOf(items, layoutItem);
			items.splice(index, 1);
			items.splice(index + i, 0, layoutItem);
		}
		
//		if(layoutItem.isAggregatable()){
//			configureFieldToAggregateInputs(layoutItem, designer.aggregateInput, designer.aggregateTypeInput);
//		}
		writeXML();
	}

	public FieldDetail addFieldDetail(String fieldName, Map<String,Object> value){
		ReportDefinition definition = designer.definition;
		int atColumn = definition.columns.$length();
		while(atColumn > 0){ 
			if(definition.getElementForRowAndColumn(definition.sectionDetailHeader.getLastRow(), definition.getColumnByIndex(atColumn-1)) == null
					&& definition.getElementForRowAndColumn(definition.sectionDetail.getLastRow(), definition.getColumnByIndex(atColumn-1)) == null){
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
	
	public void writeXML(){
		designer.writeXml();
	}

	public String toString() {
		String result = "    <layout>\n";
		for (String key : items) {
			LayoutItem item = items.$get(key);
			result += "        "+item.toString();
			result += "\n";
		}
		result += "    </layout>\n";
		return result;
	}

	public void remove(LayoutItem layoutItem) {
		
		int columnIndex = -1;
		if(layoutItem instanceof FieldDetail){
			columnIndex = ((FieldDetail) layoutItem).label.column.getIndex() + 1;
		}
		Array<ReportElement> elements = layoutItem.getElements();
		layoutItem.clearElements();
		for (String key : elements) {
			designer.definition.remove(elements.$get(key));
		}
		if(layoutItem instanceof FieldDetail){
			for (int i = columnIndex; i < designer.definition.columns.$length(); i++) {
				Array<ReportSection> allSections = designer.definition.getAllSections();
				for (int j = 0; j < allSections.$length(); j++) {
					ReportSection section = allSections.$get(j);
					ReportElement elementForRowAndColumn = designer.definition.getElementForRowAndColumn(section.getLastRow(), designer.definition.getColumnByIndex(i));
					designer.moveElement(-1, elementForRowAndColumn);
				}
			}
			
			int columnToRemove = designer.definition.columns.$length() - 1;
			if(columnToRemove != 0 || designer.groupManager.objects.$length() == 0){
				designer.definition.removeColumn(columnToRemove);
			}
		}
		
		next.util.removeItem(items, layoutItem);
		writeXML();
	}
}
