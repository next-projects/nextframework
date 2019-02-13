package org.nextframework.report.generator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.nextframework.exception.NextException;
import org.nextframework.report.generator.chart.ChartElement;
import org.nextframework.report.generator.chart.ChartSerieElement;
import org.nextframework.report.generator.chart.ChartsElement;
import org.nextframework.report.generator.data.CalculatedFieldElement;
import org.nextframework.report.generator.data.DataElement;
import org.nextframework.report.generator.data.FilterElement;
import org.nextframework.report.generator.data.GroupElement;
import org.nextframework.report.generator.datasource.DataSourceProvider;
import org.nextframework.report.generator.datasource.HibernateDataSourceProvider;
import org.nextframework.report.generator.layout.LayoutElement;
import org.nextframework.report.generator.layout.LayoutItem;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.util.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;


public class ReportReader {
	
	public static Map<String, Class<? extends DataSourceProvider<?>>> dataSourceProviders = new LinkedHashMap<String, Class<? extends DataSourceProvider<?>>>();
	
	static {
		dataSourceProviders.put("hibernateDataProvider", HibernateDataSourceProvider.class);
	}

	private InputStream in;

	public ReportReader(String xml){
		this.in = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
	}
	
	public ReportReader(InputStream in){
		this.in = in;
	}
	
	public ReportElement getReportElement() throws SAXException, IOException{
		DOMParser domParser = new DOMParser();
		domParser.parse(new InputSource(in));
		
		Document document = domParser.getDocument();
		
		Node reportNode = getReportNode(document);
		
		ReportElement reportElement = new ReportElement(getReportName(reportNode));
		
		Node dataNode = getChild(reportNode, "data");
		
		Node dataSourceProviderNode = getChild(dataNode, "dataSourceProvider");
		
		
		
		
		NodeList groups = getChild(dataNode, "groups").getChildNodes();
		List<GroupElement> groupElements = readGroups(groups);
		
		NodeList filters = getChild(dataNode, "filters").getChildNodes();
		List<FilterElement> filterElements = readFilters(filters);
		
		List<CalculatedFieldElement> calculatedFieldElements = getCalculatedFieldList(dataNode);
		
		DataSourceProvider<?> dataSourceProvider = getDataSourceProvider(dataSourceProviderNode);
		
		Node layout = getChild(reportNode, "layout");
		
		Node charts = getChild(reportNode, "charts");
		
		LayoutElement layoutElement = readLayoutElements(layout);
		
		ChartsElement chartsElement = null;
		if(charts != null){
			chartsElement = readChartElements(charts);
		}
		
		DataElement dataElement = new DataElement();
		dataElement.getGroups().addAll(groupElements);
		dataElement.getFilters().addAll(filterElements);
		dataElement.getCalculatedFields().addAll(calculatedFieldElements);
		dataElement.setDataSourceProvider(dataSourceProvider);
		reportElement.setData(dataElement);
		reportElement.setLayout(layoutElement);
		reportElement.setCharts(chartsElement);
		return reportElement;
	}
	private List<CalculatedFieldElement> getCalculatedFieldList(Node dataNode) {
		Node calculatedFieldsElement = getChild(dataNode, "calculatedFields");
		if(calculatedFieldsElement == null){
			return new ArrayList<CalculatedFieldElement>();
		}
		NodeList calculatedFields = calculatedFieldsElement.getChildNodes();
		List<CalculatedFieldElement> calculatedFieldElements = readCalculatedFields(calculatedFields);
		return calculatedFieldElements;
	}

	private List<FilterElement> readFilters(NodeList filters) {
		List<FilterElement> filterElements = new ArrayList<FilterElement>();
		for(int i = 0 ; i < filters.getLength(); i++){
			Node filterNode = filters.item(i);
			if(filterNode.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			filterElements.add(new FilterElement(
					getAttribute(filterNode, "name"),
					getAttribute(filterNode, "filterDisplayName", true),
					getAttribute(filterNode, "filterSelectMultiple", true),
					getAttribute(filterNode, "preSelectDate", true),
					getAttribute(filterNode, "preSelectEntity", true),
					getAttribute(filterNode, "fixedCriteria", true),
					getAttribute(filterNode, "requiredFilter", true)
					));
		}
		return filterElements;
	}
	
	private List<CalculatedFieldElement> readCalculatedFields(NodeList filters) {
		List<CalculatedFieldElement> calculatedFieldElements = new ArrayList<CalculatedFieldElement>();
		for(int i = 0 ; i < filters.getLength(); i++){
			Node filterNode = filters.item(i);
			if(filterNode.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			//String formatWithDecimal = getAttribute(filterNode, "formatWithDecimal", true);
			calculatedFieldElements.add(new CalculatedFieldElement(
						getAttribute(filterNode, "name"),
						getAttribute(filterNode, "expression"),
						getAttribute(filterNode, "displayName"),
						getAttribute(filterNode, "formatAs", true),
						getAttribute(filterNode, "formatTimeDetail", true),
						getAttribute(filterNode, "processors", true)
					));
		}
		return calculatedFieldElements;
	}
	
	private List<GroupElement> readGroups(NodeList groups) {
		List<GroupElement> groupElements = new ArrayList<GroupElement>();
		for(int i = 0 ; i < groups.getLength(); i++){
			Node groupNode = groups.item(i);
			if(groupNode.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			String pattern = getAttribute(groupNode, "pattern", true);
			if(pattern != null && pattern.equals("")){
				pattern = null;
			}
			groupElements.add(new GroupElement(getAttribute(groupNode, "name"), pattern));
		}
		return groupElements;
	}

	private ChartsElement readChartElements(Node charts) {
		ChartsElement chartsElement = new ChartsElement();
		NodeList childNodes = charts.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node item = childNodes.item(i);
			if(item.getNodeType() == Node.ELEMENT_NODE){
				ChartElement element = new ChartElement();
				copyAttributesToInstance(item, element, null);
				readSeries(element, item);
				chartsElement.getItems().add(element);
			}
		}
		return chartsElement;
	}
	
	private void readSeries(ChartElement element, Node node) {
		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node item = childNodes.item(i);
			if(item.getNodeType() == Node.ELEMENT_NODE){
				ChartSerieElement serie = new ChartSerieElement();
				copyAttributesToInstance(item, serie, null);
				element.getSeries().add(serie);
			}
		}
	}
	private LayoutElement readLayoutElements(Node layout) {
		LayoutElement layoutElement = new LayoutElement();
		NodeList childNodes = layout.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node item = childNodes.item(i);
			if(item.getNodeType() == Node.ELEMENT_NODE){
				String nodeName = item.getNodeName();
				String className = "org.nextframework.report.generator.layout."+StringUtils.capitalize(nodeName)+"Element";
				LayoutItem element;
				try {
					element = (LayoutItem)BeanUtils.instantiate(Class.forName(className));
				} catch (Exception e) {
					throw new RuntimeException("Could not instanciate layout element "+nodeName, e);
				}
				copyAttributesToInstance(item, element, null);
				layoutElement.getItems().add(element);
			}
		}
		return layoutElement;
	}

	private DataSourceProvider<?> getDataSourceProvider(Node dataSourceProviderNode) {
		String type = getAttribute(dataSourceProviderNode, "type");
		DataSourceProvider<?> instance = getDataSourceProviderForType(type);
		copyAttributesToInstance(dataSourceProviderNode, instance, "type");
		return instance;
	}
	public static DataSourceProvider<?> getDataSourceProviderForType(String type) {
		Class<? extends DataSourceProvider<?>> class1 = dataSourceProviders.get(type);
		if(class1 == null){
			throw new RuntimeException("No data source provider found for "+type);
		}
		DataSourceProvider<?> instance = BeanUtils.instantiate(class1);
		return instance;
	}

	private void copyAttributesToInstance(Node node, Object instance, String ignoreAttribute) {
		NamedNodeMap attributes = node.getAttributes();
		for(int i = 0; i < attributes.getLength(); i++){
			Attr attribute = (Attr) attributes.item(i);
			if(attribute.getName().equals(ignoreAttribute)){
				continue;
			}
			setValue(instance, attribute.getName(), attribute.getValue());
		}
	}

	private void setValue(Object instance, String name, String value) {
		try {
			PropertyAccessorFactory.forBeanPropertyAccess(instance).setPropertyValue(name, value);
		} catch (Exception e) {
			throw new RuntimeException("Could not set property "+name+" for "+instance, e);
		}
	}

	private String getAttribute(Node node, String param) {
		return getAttribute(node, param, false);
	}
	private String getAttribute(Node node, String param, boolean ignoreAbsent) {
		Node attribute = node.getAttributes().getNamedItem(param);
		if(attribute == null){
			if(!ignoreAbsent){
				throw new NextException("No attribute '"+param+"' found for tag "+node.getNodeName());
			} else {
				return null;
			}
		}
		return attribute.getNodeValue();
	}

	private Node getChild(Node reportNode, String nodeName) {
		Node node = null;
		NodeList childNodes = reportNode.getChildNodes();
		for(int i = 0; i < childNodes.getLength(); i++){
			Node item = childNodes.item(i);
			if(nodeName.equals(item.getNodeName())){
				node = item;
			}
		}
		return node;
	}

	private String getReportName(Node report) {
		return getAttribute(report, "name");
	}

	private Node getReportNode(Document document) {
		Node report = document.getElementsByTagName("report").item(0);
		return report;
	}
	
}
