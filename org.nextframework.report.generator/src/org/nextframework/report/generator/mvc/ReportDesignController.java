package org.nextframework.report.generator.mvc;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.controller.ClasspathModelAndView;
import org.nextframework.controller.DefaultAction;
import org.nextframework.controller.Input;
import org.nextframework.controller.MultiActionController;
import org.nextframework.controller.ResourceModelAndView;
import org.nextframework.controller.resource.Resource;
import org.nextframework.core.standard.MessageType;
import org.nextframework.core.web.NextWeb;
import org.nextframework.exception.NextException;
import org.nextframework.persistence.DAOUtils;
import org.nextframework.persistence.GenericDAO;
import org.nextframework.report.definition.ReportDefinition;
import org.nextframework.report.definition.ReportSectionRow;
import org.nextframework.report.definition.elements.ReportLabel;
import org.nextframework.report.generator.ReportElement;
import org.nextframework.report.generator.ReportGenerator;
import org.nextframework.report.generator.ReportReader;
import org.nextframework.report.generator.chart.ChartElement;
import org.nextframework.report.generator.data.CalculatedFieldElement;
import org.nextframework.report.generator.data.FilterElement;
import org.nextframework.report.generator.data.GroupElement;
import org.nextframework.report.generator.datasource.DataSourceProvider;
import org.nextframework.report.generator.datasource.HibernateDataSourceProvider;
import org.nextframework.report.generator.generated.ReportSpec;
import org.nextframework.report.generator.layout.DynamicBaseReportDefinition;
import org.nextframework.report.generator.layout.FieldDetailElement;
import org.nextframework.report.generator.layout.LayoutItem;
import org.nextframework.report.renderer.html.HtmlReportRenderer;
import org.nextframework.report.renderer.jasper.JasperReportsRenderer;
import org.nextframework.util.Util;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.ModelAndView;
import org.xml.sax.SAXException;

public abstract class ReportDesignController extends MultiActionController {
	
	ReportDesignControllerUtil util = new ReportDesignControllerUtil();

	@DefaultAction
	public ModelAndView index(){
		Class<?>[] entities = util.getAvaiableEntities();
		Map<Class, String> displayNames = util.getDisplayNameForEntities(entities);
		setAttribute("crudPath", getPathForReportCrud());
		setAttribute("entities", entities);
		setAttribute("displayNames", displayNames);
		
		return new ClasspathModelAndView("org.nextframework.report.generator.mvc.design1");
	}

	public ModelAndView selectProperties(ReportDesignModel model) throws SAXException, IOException{
		ReportElement reportElement = null;
		if(model.getReportXml() != null){
			reportElement = new ReportReader(model.getReportXml()).getReportElement();
		}
		Class<?> selectedGeneratedType = getReportType(model);
		Set<String> avaiableProperties = util.getAvailablePropertiesForClass(selectedGeneratedType, 2);
		
		if(!model.getProperties().contains("id")){
			model.getProperties().add("id");
		}
		
		if(getRequest().getParameter("visualizarDados") != null){
			List preview = util.getPreviewData(model);
			setAttribute("preview", preview);
		}
		
		setAttribute("model", model);
		setAttribute("avaiableProperties", avaiableProperties);
		Map<String, Map<String, Object>> propertiesMetadata = util.getPropertiesMetadata(reportElement, selectedGeneratedType, avaiableProperties);
		setAttribute("propertyMetadata", propertiesMetadata);
		setAttribute("crudPath", getPathForReportCrud());
		setAttribute("reportTypeDisplayName", BeanDescriptorFactory.forClass(ClassUtils.getUserClass(model.getSelectedType())).getDisplayName());
		
		for(String property: propertiesMetadata.keySet()){
			Map<String, Object> map = propertiesMetadata.get(property);
			if(map.get("requiredFilter") != null && Boolean.TRUE.equals(map.get("requiredFilter"))){
				if(!model.getProperties().contains(property)){
					model.getProperties().add(property);
				}
			}
		}
		return new ClasspathModelAndView("org.nextframework.report.generator.mvc.design2");
	}

	private Class<?> getReportType(ReportDesignModel model) {
		//TODO update this code if there is more than one datasourceprovider
		if(model.getSelectedGeneratedType() != null){
			return model.getSelectedGeneratedType();
		}
		try {
			String defaultProvider = ReportReader.dataSourceProviders.keySet().iterator().next();
			DataSourceProvider<?> dataProvider = ReportReader.getDataSourceProviderForType(defaultProvider);
			BeanWrapper bw = new BeanWrapperImpl(dataProvider);
			Class<?> selectedGeneratedType = model.getSelectedType();
			bw.setPropertyValue("fromClass", selectedGeneratedType.getName());
			return dataProvider.getMainType();
		} catch (Exception e) {
			throw new RuntimeException("Verify datasourceprovider code", e);
		}
	}


	public ModelAndView designReport(ReportDesignModel model) throws SAXException, IOException{
		ReportElement reportElement = null;
		if(model.getReportXml() != null){
			reportElement = new ReportReader(model.getReportXml()).getReportElement();
		}
		setAttribute("emptyList", new ArrayList<Object>());
		setAttribute("model", model);
		Class<?> reportType = getReportType(model);
		Set<String> avaiablePropertiesForClass = util.getAvailablePropertiesForClass(reportType, 2);
		Set<String> properties = new HashSet<String>();
		properties.addAll(model.getProperties());
		properties.addAll(avaiablePropertiesForClass);
		
		List<CalculatedFieldElement> calculatedFields = new ArrayList<CalculatedFieldElement>();
		if(model.getReportElement()!= null){
			calculatedFields = model.getReportElement().getData().getCalculatedFields();
			for (CalculatedFieldElement calculatedFieldElement : calculatedFields) {
				properties.remove(calculatedFieldElement.getName());
			}
		}
		Map<String, Map<String, Object>> propertiesMetadata = util.getPropertiesMetadata(reportElement, reportType, properties);
		//complete metadata with calculated fields
		for (CalculatedFieldElement calculatedFieldElement : calculatedFields) {
			Map<String, Object> map = util.getPropertiesMapForCalculatedField(calculatedFieldElement);
			
			propertiesMetadata.put(calculatedFieldElement.getName(), map);
			model.getProperties().add(calculatedFieldElement.getName());
		}
		
		setAttribute("reportTypeDisplayName", BeanDescriptorFactory.forClass(ClassUtils.getUserClass(reportType)).getDisplayName());
		setAttribute("propertyMetadata", propertiesMetadata);
		setAttribute("crudPath", getPathForReportCrud());
		setAttribute("avaiableProperties", avaiablePropertiesForClass);
		setAttribute("controllerPath", "/"+NextWeb.getApplicationContext().getApplicationName()+NextWeb.getRequestContext().getFirstRequestUrl());
		return new ClasspathModelAndView("org.nextframework.report.generator.mvc.design3");
	}

	public ModelAndView editXMLOnError(){
		ReportDesignModel model = loadDesignModelForId(new Integer(getParameter("id")));
		setAttribute("model", model);
		setAttribute("crudPath", getPathForReportCrud());
		return new ClasspathModelAndView("org.nextframework.report.generator.mvc.editXML");
	}

	@Input("editXMLOnError")
	public ModelAndView editDesignForId() throws SAXException, IOException {
		ReportDesignModel model = loadDesignModelForId(new Integer(getParameter("id")));
		
		Set<String> properties = new HashSet<String>();
		
		ReportReader reader = new ReportReader(model.getReportXml());
		ReportElement reportElement = reader.getReportElement();		
		model.setSelectedType(ClassUtils.getUserClass(reportElement.getData().getMainType()));
		model.setSelectedGeneratedType(reportElement.getData().getMainType());
		model.setReportElement(reportElement);
		
		List<LayoutItem> items = reportElement.getLayout().getItems();
		for (LayoutItem layoutItem : items) {
			if(layoutItem instanceof FieldDetailElement){
				String fieldName = ((FieldDetailElement) layoutItem).getName();
				properties.add(fieldName);
			}
		}
		List<GroupElement> groups = reportElement.getData().getGroups();
		for (GroupElement groupElement : groups) {
			properties.add(groupElement.getName());
		}
		
		List<FilterElement> filters = reportElement.getData().getFilters();
		for (FilterElement filterElement : filters) {
			properties.add(filterElement.getName());
		}
		
		List<ChartElement> charts = reportElement.getCharts().getItems();
		for (ChartElement chartElement : charts) {
			String groupProperty = chartElement.getGroupProperty();
			String valueProperty = chartElement.getValueProperty();
			if(groupProperty != null){
				properties.add(groupProperty);
			}
			if(valueProperty != null && ! valueProperty.equals("count")){
				properties.add(valueProperty);
			}
		}
		
		setAttribute("groups", groups);
		setAttribute("filters", filters);
		setAttribute("items", reportElement.getLayout().getItems());
		setAttribute("charts", reportElement.getCharts() != null? reportElement.getCharts().getItems() : null);
		
		model.getProperties().addAll(properties);
		model.setReportTitle(reportElement.getReportTitle());
		
		return designReport(model);
	}

	public ModelAndView showFilterView(ReportDesignModel model) throws SAXException, IOException {
		ReportElement reportElement = new ReportReader(model.getReportXml()).getReportElement();
		List<FilterElement> filters = reportElement.getData().getFilters();
		Map<String, Map<String, Object>> filterProperties = new LinkedHashMap<String, Map<String,Object>>();
		for (FilterElement filterElement : filters) {
			HashMap<String, Object> properties = new HashMap<String, Object>();
			if(Util.strings.isNotEmpty(filterElement.getFilterDisplayName())){
				properties.put("displayName", filterElement.getFilterDisplayName());
			}
			properties.put("filterSelectMultiple", filterElement.isFilterSelectMultiple());
			filterProperties.put(filterElement.getName(), properties);
		}
		
		model.setReportTitle(reportElement.getReportTitle());
		
		Class<?> mainType = reportElement.getData().getMainType();
		setAttribute("filters", util.reorganizeFilters(mainType, filterProperties.keySet()));
		setAttribute("filterMetadata", util.getPropertiesMetadata(reportElement, mainType, filterProperties));
		setAttribute("model", model);
		setAttribute("reportElement", reportElement);
		setAttribute("crudPath", getPathForReportCrud());
		
		Map<String, Object> filterMap = (Map<String, Object>) getUserAttribute(ReportDesignController.class.getSimpleName()+"_"+reportElement.getName());
		if(filterMap == null){
			filterMap = util.getFilterMap(reportElement);
		}
		setAttribute("filterValuesMap", filterMap);
		
		return new ClasspathModelAndView("org.nextframework.report.generator.mvc.filter");
	}
	
	
	public ModelAndView downloadPdf(ReportDesignModel model) throws SAXException, IOException {
		ReportDefinition definition = getDesignFromModel(model, getMaxResults());
		if(definition.getData().isEmpty()){
			getRequest().addMessage("Nenhum resultado encontrado. Verifique o filtro.", MessageType.WARN);
			return showFilterView(model);
		} else {
			definition.getParameters().put("renderPDF", Boolean.TRUE);
			onDownloadDefinitionPDF(definition);
			return new ResourceModelAndView(new Resource("application/pdf", definition.getReportName()+".pdf", JasperReportsRenderer.renderAsPDF(definition)));
		}
	}

	public ModelAndView showResults(final ReportDesignModel model) throws SAXException, IOException {
		ReportDefinition definition = getDesignFromModel(model, getMaxResults());
		
		if(definition.getData().isEmpty()){
			getRequest().addMessage("Nenhum resultado encontrado. Verifique o filtro.", MessageType.WARN);
		} else {
			setAttribute("html", HtmlReportRenderer.renderAsHtml(definition));
		}
		setAttribute("crudPath", getPathForReportCrud());
		
		return showFilterView(model);
	}
	
	protected ReportDefinition getDesignFromModel(ReportDesignModel model, int limitResults) throws SAXException, IOException {
		ReportElement reportElement = new ReportReader(model.getReportXml()).getReportElement();
		Map<String, Object> filterMap = util.getFilterMap(reportElement);
		setUserAttribute(ReportDesignController.class.getSimpleName()+"_"+reportElement.getName(), filterMap);
		setAttribute("filterValuesMap", filterMap);
		
		ReportGenerator rg = new ReportGenerator(reportElement);
		
		ReportSpec spec = rg.generateReportSpec(filterMap, limitResults);
		
		if(debugMode()){
			debugSource(rg.getSourceCode(), spec.getSummary().getSourceCode());
		}
		
		ReportDefinition definition = spec.getReportBuilder().getDefinition();
		
		boolean avaiable = false;
		for (Class<?> c : util.getAvaiableEntities()) {
			if(ClassUtils.getUserClass(reportElement.getData().getMainType()).equals(c)){
				avaiable = true;
				break;
			}
		}
		if(!avaiable){
			throw new NextException("The type "+reportElement.getData().getMainType()+" is not avaiable for reports.");
		}
		
		int total = definition.getData().size();
		if (definition instanceof DynamicBaseReportDefinition) {
			DynamicBaseReportDefinition d2 = (DynamicBaseReportDefinition) definition;
			total = d2.getSummarizedData().getItems().size();
		}
		
		if(total == getMaxResults()) {
			ReportLabel label = new ReportLabel("Obs: Apenas os "+getMaxResults()+" primeiros registros estão sendo mostrados.");
			label.getStyle().setForegroundColor(Color.RED);
			label.getStyle().setFontSize(6);
			label.getStyle().setItalic(true);
			label.setColspan(definition.getColumns().size());
			ReportSectionRow row = definition.getSectionFirstPageHeader().insertRow(0);
			definition.addItem(label, row, 0);
		}
		return definition;
	}

	protected int getMaxResults() {
		return HibernateDataSourceProvider.MAXIMUM_RESULTS;
	}

	public ModelAndView showFilterViewForId() {
		ReportDesignModel model = loadDesignModelForId(new Integer(getParameter("id")));
		try {
			return showFilterView(model);
		} catch (Exception e) {
			throw new RuntimeException("Não foi possível executar o relatório", e);
		}
	}
	
	public ModelAndView saveReport(ReportDesignModel model){
		persistReport(model);
		return new ModelAndView("redirect:"+getPathForReportCrud());
	}
	
	public List<Object[]> getFilterList(){
		ArrayList<Object[]> result = new ArrayList<Object[]>();
		try {
			Class type = Class.forName(getParameter("type"));
			GenericDAO dao = DAOUtils.getDAOForClass(type);
			List list = dao.findAll();
			for (Object object : list) {
				String id = Util.strings.toStringIdStyled(object);
				String label = Util.strings.toStringDescription(object);
				Object[] obj = new Object[]{
						id, label
				};
				result.add(obj);
			}
		} catch (Exception e) {
			Object[] msg = (Object[])new Object[]{"error", "error "+e.getMessage()};
			result.add(msg);
		}
		return result;
	}
	
	public abstract void persistReport(ReportDesignModel model);
	protected abstract ReportDesignModel loadDesignModelForId(Integer id);
	public abstract String getPathForReportCrud();
	
	protected void onDownloadDefinitionPDF(ReportDefinition definition) {
	}

	protected boolean debugMode() {
		return false;
	}
	protected void debugSource(String sourceCodeReport, String sourceCodeSummary) {
	}	
}
