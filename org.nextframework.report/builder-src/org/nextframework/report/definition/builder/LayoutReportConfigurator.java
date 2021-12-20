package org.nextframework.report.definition.builder;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.bean.BeanDescriptorUtils;
import org.nextframework.report.definition.ReportSection;
import org.nextframework.report.definition.ReportSectionRow;
import org.nextframework.report.definition.builder.BaseReportBuilder.FieldConfig;
import org.nextframework.report.definition.elements.ReportGrid;
import org.nextframework.report.definition.elements.ReportLabel;
import org.nextframework.report.definition.elements.ReportTextField;
import org.nextframework.report.definition.elements.style.ReportAlignment;

public class LayoutReportConfigurator {

	public void configure(String groupName, GroupSetup groupSetup, int index) {
		groupSetup.getReportGroup().getSectionHeader().getRow(0).setStyleClass("groupLabelRow_"+index);	
	}

	public void configureSections(LayoutReportBuilder layoutReportBuilder) {
	}

	public void afterLayout(LayoutReportBuilder layoutReportBuilder) {
		Map<String, Object> parameters = layoutReportBuilder.getDefinition().getParameters();
		Object logo = parameters.get("LOGO");
		if(logo == null){
			logo = getLogo(layoutReportBuilder);
			parameters.put("LOGO", logo);
		}
		
		configureSectionWithStyle(layoutReportBuilder.getDefinition().getSectionDetailHeader(), "detailHeader");
		configureSectionWithStyle(layoutReportBuilder.getDefinition().getSectionSummaryDataHeader(), "detailHeader");
		configureSectionWithStyle(layoutReportBuilder.getDefinition().getSectionSummaryDataDetail(), "summaryDetail");
		
		Object filter = layoutReportBuilder.getFilter();
		Collection<String> properties = new TreeSet<String>();
		if(filter instanceof AutoReportFilter){
			Method[] declaredMethods = filter.getClass().getDeclaredMethods();
			for (Method method : declaredMethods) {
				if(BeanDescriptorUtils.isGetter(method)){
					String property = BeanDescriptorUtils.getPropertyFromGetter(method.getName());
					properties.add(property);
				}
			}
			
			properties = new ArrayList<String>(properties);
			
			List<String> rangeProperties = new ArrayList<String>();
			
			for (String property : properties) {
				if(property.endsWith("_end") || property.endsWith("_begin")){
					rangeProperties.add(property);
				}
			}
			
			properties.removeAll(rangeProperties);
			
			((List<String>)properties).addAll(0, rangeProperties);
			
			for (String property : properties) {
				try {
					layoutReportBuilder.filter(property);
//					PropertyDescriptor propertyDescriptor = bd.getPropertyDescriptor(property);
//					Object value = propertyDescriptor.getValue();
//					
//					ReportTextField textField = new ReportTextField("param.autofilter."+property);
//					if(value instanceof Calendar){
//						value = ((Calendar)value).getTime();
//					}
//					if(value instanceof Date){
//						textField.setPattern("dd/MM/yyyy");
//					}
//					layoutReportBuilder.getDefinition().getParameters().put("autofilter."+property, Util.strings.toStringDescription(value));
//					
//					String label = propertyDescriptor.getDisplayName();
//					if(label.endsWith("_begin")){
//						label = label.substring(0, label.length() - "_begin".length()) + " de";
//					}
//					if(label.endsWith("_end")){
//						label = label.substring(0, label.length() - "_end".length()) + " até";
//					}
//					
//					layoutReportBuilder.addLabelAndFieldToFilterGrid(textField, new ReportLabel(label+ ":"));
				} catch (Exception e) {
					layoutReportBuilder.addLabelAndFieldToFilterGrid(new ReportLabel("ERROR"), new ReportLabel(property));
				}
			}
		}
	}

	private void configureSectionWithStyle(ReportSection sectionDetailHeader, String style) {
		for (ReportSectionRow reportSectionRow : sectionDetailHeader.getRows()) {
			if(reportSectionRow.getStyleClass() == null){
				reportSectionRow.setStyleClass(style);
			}
		}
	}

	public InputStream getLogo(LayoutReportBuilder layoutReportBuilder) {
		return getClass().getClassLoader().getResourceAsStream("org/nextframework/report/renderer/jasper/logonextframework.png");
	}

	public void configureReport(BaseReportBuilder layoutReportBuilder) {
	}

	public void configureLabelForGroup(String groupName, GroupSetup groupSetup, int index, ReportTextField field) {
	}

	public ReportGrid getReportFilterGrid(BaseReportBuilder layoutReportBuilder) {
		ReportGrid reportGrid = (ReportGrid) layoutReportBuilder.getDefinition().getRenderParameters().get("REPORT_FILTER_GRID");
		if(reportGrid == null){
			reportGrid = new ReportGrid(4);
			layoutReportBuilder.getDefinition().getRenderParameters().put("REPORT_FILTER_GRID", reportGrid);
		}
		return reportGrid;
	}

	public void loadValue(Object object, String propertyName) {
		//FIXME REFACTOR REBUILD THIS TEMPORARY SOLUTION (DEPENDS ON CLASSES THAT ARE NOT ON THE COMPILER CLASSPATH) (makes it backward compatible)
		//DAOUtils.getDAOForClass(object.getClass()).loadDescriptionProperty(object);
		try {
			Class<?> clazz = Class.forName("org.nextframework.persistence.DAOUtils");
			Object beanFactoryUtils = clazz.newInstance();
			Object dao = clazz.getMethod("getDAOForClass", Class.class).invoke(beanFactoryUtils, object.getClass());
			dao.getClass().getMethod("loadDescriptionProperty", Object.class).invoke(dao, object);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public FieldConfig createFieldConfig(BaseReportBuilder builder, BeanDescriptor beanDescriptor, String fieldName, String fieldPreffix, String label, String pattern, ReportAlignment alignment, String suffix, boolean isEntity, boolean callToString) {
		return new FieldConfig(label, fieldName, buildName(fieldPreffix, fieldName, suffix), suffix, alignment, pattern, isEntity, callToString);
	}
	
	private String buildName(String fieldPreffix, String fieldName, String suffix) {
		String name = fieldName;
		if(fieldPreffix != null){
			name = fieldPreffix + fieldName;
		}
		if(suffix != null){
			name += suffix;
		}
		return name;
	}
	
	protected void setFilterParameter(LayoutReportBuilder layoutReportBuilder, String parameter, String property) {
		Object filter = layoutReportBuilder.getFilter();
		if(filter != null){
			BeanDescriptor filterBeanDescriptor = BeanDescriptorFactory.forBean(filter);
			if(layoutReportBuilder.getDefinition().getParameters().get(parameter) == null){
				Object value = filterBeanDescriptor.getPropertyDescriptor(property).getValue();
				if(value instanceof Calendar){
					value = ((Calendar) value).getTime();
				}
				layoutReportBuilder.getDefinition().getParameters().put(parameter, value);
			}
		}
	}

	public int getChartDefaultHeight() {
		return 125;
	}
}
