package org.nextframework.report.generator.mvc.resource;

import static org.nextframework.js.NextGlobalJs.next;
import static org.stjs.javascript.JSCollections.$array;

import org.nextframework.js.NextGlobalJs;
import org.stjs.javascript.Array;
import org.stjs.javascript.Global;
import org.stjs.javascript.JSCollections;
import org.stjs.javascript.Map;
import org.stjs.javascript.dom.Button;
import org.stjs.javascript.dom.DOMEvent;
import org.stjs.javascript.dom.Div;
import org.stjs.javascript.dom.Element;
import org.stjs.javascript.dom.HTMLCollection;
import org.stjs.javascript.dom.HTMLList;
import org.stjs.javascript.dom.Input;
import org.stjs.javascript.dom.Option;
import org.stjs.javascript.dom.Select;
import org.stjs.javascript.dom.SelectOptionsCollection;
import org.stjs.javascript.functions.Function1;

public class ChartWizzard {

	private static final String AGGREGATE_FUNCTION = "aggregateFunction";
	private static final String SERIE_LABEL = "label";
	private static final String ATTR_ISDATE = "data-isdate";
	private Div mainDiv;
	private Button nextButton;
	
	private int currentPage = 110;
	
	Array<Div> pages;
	private ReportDesigner designer;
	
	Select chartGroupBy;
	Select chartGroupByLevel;
	
	Select chartSeries;
	
	Select chartValue;
	
	Select chartAggregateType;
	
	Input chartTitle;
	Input chartGroupTitle;
	Input chartSeriesTitle;
	Input chartIgnoreEmptySeriesAndGroups;
	
	Input chartCountTrue;
	Input chartCountFalse;
	
	Input propertyTypeDefault;
	Input propertyTypeAsSeries;
	
	Input chartConfigLimitSeriesShowAll;
	Input chartConfigLimitSeriesLimit;
	Input chartConfigLimitSeriesGroup;
	
	Div chartSeriesSection;
	
	ChartConfiguration editing;
	ReportGeneratorSelectManyBoxView propertiesAsSeries;
	Select chartAggregateTypeSerie;
	Input chartLabelSerie;
	
	public static ChartWizzard createInstance(){
		return new ChartWizzard(Global.window.document.getElementById("chartWizzard"), ReportDesigner.getInstance());
	}
	
	public static ChartWizzard setup(String id, ReportDesigner designer){
		ChartWizzard chartWizzard = new ChartWizzard(Global.window.document.getElementById(id), designer);
		return chartWizzard;
	}
	
	public ChartWizzard(Element element, ReportDesigner designer) {
		this.mainDiv = (Div) element;
		this.nextButton = (Button) NextGlobalJs.next.dom.getInnerElementById(mainDiv, "nextButton");
		this.pages = $array();
		this.designer = designer;
		
		this.chartGroupBy = (Select) NextGlobalJs.next.dom.getInnerElementById(mainDiv, "chartGroupBy");
		this.chartGroupByLevel = (Select) NextGlobalJs.next.dom.getInnerElementById(mainDiv, "chartGroupByLevel");
		this.chartSeries = (Select) NextGlobalJs.next.dom.getInnerElementById(mainDiv, "chartSeries");
		this.chartValue = (Select) NextGlobalJs.next.dom.getInnerElementById(mainDiv, "chartValue");
		this.chartAggregateType = (Select) NextGlobalJs.next.dom.getInnerElementById(mainDiv, "chartAggregateType");
		this.chartTitle = (Input) NextGlobalJs.next.dom.getInnerElementById(mainDiv, "chartTitle");
		this.chartGroupTitle = (Input) NextGlobalJs.next.dom.getInnerElementById(mainDiv, "chartGroupTitle");
		this.chartSeriesTitle = (Input) NextGlobalJs.next.dom.getInnerElementById(mainDiv, "chartSeriesTitle");
		this.chartIgnoreEmptySeriesAndGroups = (Input) NextGlobalJs.next.dom.getInnerElementById(mainDiv, "chartIgnoreEmptySeriesAndGroups");
		
		this.chartCountTrue = (Input) NextGlobalJs.next.dom.getInnerElementById(mainDiv, "chartCountTrue");
		this.chartCountFalse = (Input) NextGlobalJs.next.dom.getInnerElementById(mainDiv, "chartCountFalse");
		
		this.propertyTypeAsSeries = (Input) NextGlobalJs.next.dom.getInnerElementById(mainDiv, "propertyTypeAsSeries");
		this.propertyTypeDefault = (Input) NextGlobalJs.next.dom.getInnerElementById(mainDiv, "propertyTypeDefault");
		
		this.chartConfigLimitSeriesShowAll = (Input) NextGlobalJs.next.dom.getInnerElementById(mainDiv, "chartConfigLimitSeriesShowAll");
		this.chartConfigLimitSeriesLimit = (Input) NextGlobalJs.next.dom.getInnerElementById(mainDiv, "chartConfigLimitSeriesLimit");
		this.chartConfigLimitSeriesGroup = (Input) NextGlobalJs.next.dom.getInnerElementById(mainDiv, "chartConfigLimitSeriesGroup");
		
		this.chartSeriesSection = (Div) NextGlobalJs.next.dom.getInnerElementById(mainDiv, "chartSeriesSection");
		
		this.propertiesAsSeries = new ReportGeneratorSelectManyBoxView((Select) next.dom.toElement("chartPropertiesAsSeries_from_"));
		
		final ChartWizzard bigThis = this;
		
		this.chartLabelSerie = (Input) NextGlobalJs.next.dom.getInnerElementById(mainDiv, "chartLabelSerie");
		this.chartLabelSerie.onchange = new Function1<DOMEvent, Boolean>() {
			public Boolean $invoke(DOMEvent p1) {
				bigThis.configureSerieLabel();
				return true;
			}
		};
		
		this.chartAggregateTypeSerie = (Select) NextGlobalJs.next.dom.getInnerElementById(mainDiv, "chartAggregateTypeSerie");
		this.chartAggregateTypeSerie.onchange = new Function1<DOMEvent, Boolean>() {
			public Boolean $invoke(DOMEvent p1) {
				bigThis.configureSerieAggregateType();
				return true;
			}
		};
		
		this.propertiesAsSeries.getTo().onchange = new Function1<DOMEvent, Boolean>() {
			public Boolean $invoke(DOMEvent p1) {
				bigThis.resetSerieLabel();
				bigThis.resetAggregateType();
				return true;
			}
		};
		this.propertyTypeAsSeries.onclick = this.propertyTypeDefault.onclick = new Function1<DOMEvent, Boolean>() {

			@Override
			public Boolean $invoke(DOMEvent p1) {
				bigThis.configurePropertyType();
				return true;
			}
		};
		
		addPage("chartWizzard_page_1");
		addPage("chartWizzard_page_2");
		addPage("chartWizzard_page_3");
	}
	
	protected void resetSerieLabel() {
		if(propertiesAsSeries.getTo().selectedIndex >= 0){
			Option optionSelected = propertiesAsSeries.getTo().options.$get(propertiesAsSeries.getTo().selectedIndex);
			Map<String, Object> properties = getPropertiesFromOption(optionSelected);
			String label = (String) properties.$get(SERIE_LABEL);
			if(label != null){
				chartLabelSerie.value = label;
			} else {
				chartLabelSerie.value = ReportPropertyConfigUtils.getDisplayName(properties);
			}
		} else {
			chartLabelSerie.value = "";
		}
	}
	protected void resetAggregateType() {
		if(propertiesAsSeries.getTo().selectedIndex >= 0){
			Option optionSelected = propertiesAsSeries.getTo().options.$get(propertiesAsSeries.getTo().selectedIndex);
			Map<String, Object> properties = getPropertiesFromOption(optionSelected);
			String aggregateFunction = (String) properties.$get(AGGREGATE_FUNCTION);
			if(aggregateFunction == null){
				chartAggregateTypeSerie.selectedIndex = 0;
			} else {
				next.dom.setSelectedValue(chartAggregateTypeSerie, aggregateFunction);
			}
		} else {
			chartAggregateTypeSerie.selectedIndex = 0;
		}
	}

	protected void configureSerieLabel() {
		if(propertiesAsSeries.getTo().selectedIndex >= 0){
			Option optionSelected = propertiesAsSeries.getTo().options.$get(propertiesAsSeries.getTo().selectedIndex);
			Map<String, Object> properties = getPropertiesFromOption(optionSelected);
			properties.$put(SERIE_LABEL, chartLabelSerie.value);
//			Global.alert("changing "+selectedOption+ " to "+aggregateFunction);
		}
		
	}
	protected void configureSerieAggregateType() {
		if(propertiesAsSeries.getTo().selectedIndex >= 0){
			String aggregateFunction = next.dom.getSelectedValue(chartAggregateTypeSerie);
			String selectedOption = next.dom.getSelectedText(propertiesAsSeries.getTo());
			
			Option optionSelected = propertiesAsSeries.getTo().options.$get(propertiesAsSeries.getTo().selectedIndex);
			Map<String, Object> properties = getPropertiesFromOption(optionSelected);
			properties.$put(AGGREGATE_FUNCTION, aggregateFunction);
//			Global.alert("changing "+selectedOption+ " to "+aggregateFunction);
		}
	}

	public Map<String, Object> getPropertiesFromOption(Option optionSelected) {
		Map<String, Object> optionObject = (Map<String, Object>)optionSelected;
		Map<String, Object> properties = (Map<String, Object>) optionObject.$get("properties");
		if(properties == null){
			properties = JSCollections.$map();
			((Map<String, Object>)optionSelected).$put("properties", properties);
		}
		return properties;
	}

	private void configurePropertyType(){
		if(propertyTypeDefault.checked){
			Global.window.document.getElementById("propertiesDefaultBlock").style.display = "";
			Global.window.document.getElementById("propertiesAsSeriesBlock").style.display = "none";
		} else {
			Global.window.document.getElementById("propertiesDefaultBlock").style.display = "none";
			Global.window.document.getElementById("propertiesAsSeriesBlock").style.display = "";
		}
	}

	private void configureValues() {
		chartCountFalse.disabled = true;
		chartValue.innerHTML = "";
		for(String key : designer.fields){
			Map<String, Object> properties = designer.fields.$get(key);
			if(ReportPropertyConfigUtils.isAggregatable(properties)){
				Option option = (Option) Global.window.document.createElement("option");
				option.value = key;
				option.innerHTML = (String) properties.$get("displayName");
				chartValue.appendChild(option);
				chartCountFalse.disabled = false;
			}
		}
	}

	private void configureSeries() {
		chartSeries.innerHTML = "";
		
		for(String key : designer.fields){
			Map<String, Object> properties = designer.fields.$get(key);
			if(ReportPropertyConfigUtils.isGroupable(properties) && !ReportPropertyConfigUtils.isNumber(properties)
					&& !ReportPropertyConfigUtils.isDate(properties)){
				Option option = (Option) Global.window.document.createElement("option");
				option.value = key;
				option.innerHTML = (String) properties.$get("displayName");
				chartSeries.appendChild(option);
			}
		}
	}
	private void configureGroupItens() {
		propertiesAsSeries.clearAll();
		propertiesAsSeries.setUsePropertyAsLabel("displayName");
		
		chartGroupBy.innerHTML = "";
		chartGroupByLevel.selectedIndex = 1;
		ReportPropertyConfigUtils.hideElement(chartGroupByLevel);
		for(String key : designer.fields){
			Map<String, Object> properties = designer.fields.$get(key);
			if(ReportPropertyConfigUtils.isNumber(properties)){
				propertiesAsSeries.add(key, properties);
			}
			if(ReportPropertyConfigUtils.isGroupable(properties) && !ReportPropertyConfigUtils.isNumber(properties)){
				Option option = (Option) Global.window.document.createElement("option");
				option.value = key;
				option.innerHTML = (String) properties.$get("displayName");
				if(ReportPropertyConfigUtils.isDate(properties)){
					option.setAttribute(ATTR_ISDATE, "true");
				}
				chartGroupBy.appendChild(option);
			}
		}
		
		final ChartWizzard bigThis = this;
		chartGroupBy.onchange = new Function1<DOMEvent, Boolean>() {
			@Override
			public Boolean $invoke(DOMEvent p1) {
				bigThis.configureGroupDateLevel();
				return true;
			}

		};
		configureGroupDateLevel();
	}
	
	private void configureGroupDateLevel() {
		String isdate = chartGroupBy.options.item(chartGroupBy.selectedIndex).getAttribute(ATTR_ISDATE);
		if(isdate != null && isdate.equals("true")){
			ReportPropertyConfigUtils.showElement(chartGroupByLevel);
		} else {
			ReportPropertyConfigUtils.hideElement(chartGroupByLevel);
		}
	}


	private Div addPage(String pageName) {
		Div page = (Div) NextGlobalJs.next.dom.getInnerElementById(mainDiv, pageName);
		this.pages.push(page);
		return page;
	}
	
	public String getSelectedChartTypeCapitalized(){
		String selectedChartType = getSelectedChartType();
		if(selectedChartType != null){
			
			selectedChartType = selectedChartType.substring(0, 1).toUpperCase() + selectedChartType.substring(1);
		}
		return selectedChartType;
	}
	public String getSelectedChartType(){
		HTMLList<Element> chartTypeRadios = Global.window.document.getElementsByName("chartType");
		for (int i = 0; i < chartTypeRadios.length; i++) {
			Input radio = (Input) chartTypeRadios.$get(i);
			if(radio.checked){
				return radio.value;
			}
		}		
		Global.alert("Erro: Nenhum tipo de gráfico selecionado");
		return null;
	}

	protected void next() {
		switch (currentPage) {
		case 110:
			gotoChartConfig();
			break;
		case 121:
			gotoConfirmationView();
			break;
		}
	}

	private void gotoConfirmationView() {
		hideAllPages();
		showPage("chartWizzard_page_3");
		currentPage = 131;
		
		Option groupByProperty = chartGroupBy.options.item(chartGroupBy.selectedIndex);
		
		Option groupByLevelProperty = chartGroupByLevel.options.item(chartGroupByLevel.selectedIndex);
		
		boolean useCount = chartCountTrue.checked;
		
		Option valueProperty = chartValue.options.$get(chartValue.selectedIndex);
		
		Option aggregate = chartAggregateType.options.$get(chartAggregateType.selectedIndex);
		
		String title = chartTitle.value;
		String groupTitle = chartGroupTitle.value;
		String seriesTitle = chartSeriesTitle.value;
		
		String seriesProperty = "";
		
		if(chartSeries.selectedIndex >= 0){
			seriesProperty = chartSeries.options.item(chartSeries.selectedIndex).value;
		}
		
		final ChartConfiguration configuration = new ChartConfiguration(getSelectedChartType(), 
				groupByProperty.value, 
				groupByLevelProperty.value,
				getSelectedChartType().equals("pie")? "" : (propertyTypeDefault.checked? seriesProperty:""),
				useCount? "count" : valueProperty.value, 
				aggregate.value, 
				title, 
				groupTitle,
				seriesTitle,
				propertyTypeAsSeries.checked?"true":"false",
				getSeriesLimitType(),
				getChartIgnoreEmptySeriesAndGroups());
		
		HTMLCollection<Option> options = propertiesAsSeries.getTo().options;
		for (int i = 0; i < options.length; i++) {
			Option option = options.$get(i);
			Map<String, Object> properties = getPropertiesFromOption(option);
			String aggregateFunction = (String) properties.$get(AGGREGATE_FUNCTION);
			String label = (String) properties.$get(SERIE_LABEL);
			configuration.addSerie(option.value, aggregateFunction, label);
		}
		
		setSpanText("chartTitleSpan", configuration.title);
		setSpanText("chartGroupSpan", configuration.groupProperty);
		setSpanText("chartValueSpan", configuration.valueProperty.equals("count")? "Contagem dos itens": configuration.valueProperty +" ("+configuration.valueAggregate+")");
		
		nextButton.innerHTML = "Concluido";
		
		final ChartWizzard bigThis = this;

		nextButton.onclick = new Function1<DOMEvent, Boolean>() {
			@Override
			public Boolean $invoke(DOMEvent p1) {
				bigThis.saveConfiguration(configuration);
				bigThis.dismiss();
				return true;
			}
		};
	}

	private boolean getChartIgnoreEmptySeriesAndGroups() {
		return chartIgnoreEmptySeriesAndGroups.checked;
	}

	private String getSeriesLimitType() {
		HTMLList<Element> chartTypeRadios = Global.window.document.getElementsByName("chartConfigLimitSeries");
		for (int i = 0; i < chartTypeRadios.length; i++) {
			Input radio = (Input) chartTypeRadios.$get(i);
			if(radio.checked){
				return radio.value;
			}
		}		
//		Global.alert("Erro: Nenhum tipo de limitador de séries selecionado");
		return "showall";
	}
	
	protected void saveConfiguration(ChartConfiguration configuration) {
		if(editing == null){
			designer.addChart(configuration);
		} else {
			designer.updateChart(editing.option, configuration);
		}
	}

	private void setSpanText(String id, String text) {
		Element span = NextGlobalJs.next.dom.getInnerElementById(mainDiv, id);
		span.innerHTML = text;
	}

	private void configureChartSeriesSectionType(){
		if(getSelectedChartType().equals("pie")){
			Global.window.document.getElementById("chartSeriesSectionType").style.display = "none";
		} else {
			Global.window.document.getElementById("chartSeriesSectionType").style.display = "";
		}
	}
	
	private void gotoChartConfig() {
		hideAllPages();
		
		NextGlobalJs.next.effects.hide("chartLabelTypePie");
		NextGlobalJs.next.effects.hide("chartLabelTypeColumn");
		NextGlobalJs.next.effects.hide("chartLabelTypeLine");
		
		propertyTypeDefault.checked = true;
		configurePropertyType();
		
		configureChartSeriesSectionType();
		
		NextGlobalJs.next.effects.show("chartLabelType"+getSelectedChartTypeCapitalized());
		
		if(getSelectedChartType().equals("pie")){
			chartSeriesSection.style.display = "none";
		} else {
			chartSeriesSection.style.display = "";
		}
		
		showPage("chartWizzard_page_2");
		currentPage = 121;
		
		chartTitle.value = "Grafico "+(designer.charts.options.length+1);
		
		chartAggregateTypeSerie.selectedIndex = 0;
		chartLabelSerie.value = "";
	}


	private void hideAllPages() {
		for (String key : pages) {
			pages.$get(key).style.display = "none";
		}
	}


	private void showPage(String string) {
		for (String key : pages) {
			Div page = pages.$get(key);
			if(page.id.equals(string)){
				page.style.display = "";
			}
		}
	}

	public void show(){
		this.editing = null;
		nextButton.innerHTML = "Proximo";
		final ChartWizzard bigThis = this;

		nextButton.onclick = new Function1<DOMEvent, Boolean>() {
			@Override
			public Boolean $invoke(DOMEvent p1) {
				bigThis.next();
				return true;
			}
		};
		
		configureGroupItens();
		configureSeries();
		configureValues();
		
		hideAllPages();
		showPage("chartWizzard_page_1");
		currentPage = 110;
		mainDiv.style.visibility = "";
		
		((Input)Global.window.document.getElementById("chartTypePie")).checked = true;
	}
	
	public void edit(ChartConfiguration chartConfiguration){
		if(chartConfiguration != null){
			show();
			checkChartType(chartConfiguration);
			checkLimitSeriesType(chartConfiguration);
			next();
			
			if("true".equals(chartConfiguration.propertiesAsSeries)){
				propertyTypeAsSeries.checked = true;
				Array<SerieConfiguration> series = chartConfiguration.series;
				for (String key : series) {
					SerieConfiguration serie = series.$get(key);
					propertiesAsSeries.select(serie.property, null);
					SelectOptionsCollection options = propertiesAsSeries.getTo().options;
					String displayName = ReportPropertyConfigUtils.getDisplayName(getPropertiesFromOption(options.$get(options.length-1)));
					String label = "";
					getPropertiesFromOption(options.$get(options.length-1))
						.$put(AGGREGATE_FUNCTION, (Object)serie.aggregateFunction);
					if(serie.label != null  && !serie.label.equals("")){
						label = serie.label;
					} else {
						label = displayName;
					}
					getPropertiesFromOption(options.$get(options.length-1))
						.$put(SERIE_LABEL, label);
				}
			} else {
				propertyTypeAsSeries.checked = false;
				
			}
			configurePropertyType();
			
			Option chartGroup = setValue(chartGroupBy, chartConfiguration.groupProperty); 
			if(chartGroup != null){
				String isdate = chartGroup.getAttribute(ATTR_ISDATE);
				if(isdate != null && isdate.equals("true")){
					ReportPropertyConfigUtils.showElement(chartGroupByLevel);
					setValue(chartGroupByLevel, chartConfiguration.groupLevel);
				} else {
					ReportPropertyConfigUtils.hideElement(chartGroupByLevel);
				}
			}
			setValue(chartSeries, chartConfiguration.seriesProperty); 
			setValue(chartValue, chartConfiguration.valueProperty);
			setValue(chartAggregateType, chartConfiguration.valueAggregate);
			chartTitle.value = chartConfiguration.title;
			chartGroupTitle.value = chartConfiguration.groupTitle;
			chartSeriesTitle.value = chartConfiguration.seriesTitle;
			
			if(chartConfiguration.valueProperty.equals("count")){
				chartCountTrue.checked = true;
			} else {
				chartCountFalse.checked = true;
			}
			chartIgnoreEmptySeriesAndGroups.checked = chartConfiguration.ignoreEmptySeriesAndGroups;
			
			this.editing = chartConfiguration;
		}
	}

	public void checkLimitSeriesType(ChartConfiguration chartConfiguration) {
		chartConfigLimitSeriesShowAll.checked = true;
		String seriesLimitType = chartConfiguration.seriesLimitType;
		HTMLList<Element> chartTypeRadios = Global.window.document.getElementsByName("chartConfigLimitSeries");
		for (int i = 0; i < chartTypeRadios.length; i++) {
			Input radio = (Input) chartTypeRadios.$get(i);
			if(radio.value == seriesLimitType){
				radio.checked = true;
			}
		}
	}


	private Option setValue(Select select, Object value) {
		HTMLCollection<Option> options = select.options;
		for (int i = 0; i < options.length; i++) {
			Option op = options.$get(i);
			if(op.value.equals(value)){
				op.selected = true;
				return op;
			}
		}
		return null;
	}


	private void checkChartType(ChartConfiguration chartConfiguration) {
		String type = chartConfiguration.type;
		HTMLList<Element> chartTypeRadios = Global.window.document.getElementsByName("chartType");
		for (int i = 0; i < chartTypeRadios.length; i++) {
			Input radio = (Input) chartTypeRadios.$get(i);
			if(radio.value.equals(type)){
				radio.checked = true;
			}
		}
	}
	
	public void dismiss(){
		mainDiv.style.visibility = "hidden";
	}
}

class ChartConfiguration {

	String type;
	String groupProperty;
	String groupLevel = "0";
	String seriesProperty;
	String valueProperty;
	String valueAggregate;
	String title;
	String groupTitle;
	String seriesTitle;
	String propertiesAsSeries;
	String seriesLimitType;
	boolean ignoreEmptySeriesAndGroups;
	
	Array<SerieConfiguration> series;
	
	Option option;


	public ChartConfiguration(String type, String groupProperty, String groupLevel, String seriesProperty, String valueProperty, String valueAggregate,	String title, String groupTitle, String seriesTitle, String propertiesAsSeries, String seriesLimitType, boolean ignoreEmptySeriesAndGroups) {
		this.series = JSCollections.$array();
		this.type = type;
		this.groupProperty = groupProperty;
		this.groupLevel = groupLevel;
		this.seriesProperty = seriesProperty;
		this.valueProperty = valueProperty;
		this.valueAggregate = valueAggregate;
		this.title = title;
		this.groupTitle = groupTitle;
		this.seriesTitle = seriesTitle;
		this.propertiesAsSeries = propertiesAsSeries;
		this.seriesLimitType = seriesLimitType;
		this.ignoreEmptySeriesAndGroups = ignoreEmptySeriesAndGroups;
	}

	public void addSerie(String property, String aggregateFunction, String label){
		series.push(new SerieConfiguration(property, aggregateFunction, label));
	}

	public String toXmlString() {
		String tag = "<chart type=\""+type+"\" " +
							"groupProperty=\""+groupProperty+"\" groupLevel=\""+groupLevel+"\" " +
							("true".equals(propertiesAsSeries) ? "seriesProperty=\"\" " : "seriesProperty=\""+seriesProperty+"\" ") +
							"valueProperty=\""+valueProperty+"\" valueAggregate=\""+valueAggregate+"\" " +
							"title=\""+title+"\"" + " groupTitle=\""+groupTitle+"\"" + " seriesTitle=\""+seriesTitle+"\"";
		if("true".equals(propertiesAsSeries)){
			tag += " propertiesAsSeries=\"true\"";
		}
		if(seriesLimitType != null){
			tag += " seriesLimitType=\""+seriesLimitType+"\"";
		}
		if(ignoreEmptySeriesAndGroups){
			tag += " ignoreEmptySeriesAndGroups=\""+true+"\"";
		}
		if(series.$length() == 0){
			tag += "/>";
		} else {
			tag += ">\n";
			for (String key : series) {
				tag += "            "+series.$get(key).toXmlString() + "\n";
			}
			tag += "        </chart>";
		}
		return tag;
	}
	
}
class SerieConfiguration {
	
	String property;
	String aggregateFunction;
	String label;

	public SerieConfiguration(String property, String aggregateFunction, String label) {
		this.property = property;
		this.aggregateFunction = aggregateFunction;
		this.label = label;
	}

	public String toXmlString() {
		String xml = "<serie property=\""+property+"\"";
		if(aggregateFunction != null && !aggregateFunction.equals("SUM")){
			xml += " aggregateFunction=\""+aggregateFunction+"\"";
		}
		if(label != null && !label.equals("")){
			xml+= " label=\""+label+"\"";
		}
		xml += "/>";
		return xml;
	}
	
}