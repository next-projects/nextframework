package org.nextframework.report.renderer.jasper.builder;

import java.awt.Color;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.base.JRBoxPen;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JRDesignElementGroup;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignFrame;
import net.sf.jasperreports.engine.design.JRDesignGroup;
import net.sf.jasperreports.engine.design.JRDesignLine;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignStyle;
import net.sf.jasperreports.engine.design.JRDesignTextElement;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.LineStyleEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.PositionTypeEnum;
import net.sf.jasperreports.engine.type.StretchTypeEnum;
import net.sf.jasperreports.engine.type.WhenNoDataTypeEnum;

import org.nextframework.chart.Chart;
import org.nextframework.report.definition.ReportColumn;
import org.nextframework.report.definition.ReportGroup;
import org.nextframework.report.definition.ReportGroupSection;
import org.nextframework.report.definition.ReportSection;
import org.nextframework.report.definition.ReportSectionRow;
import org.nextframework.report.definition.ReportSectionType;
import org.nextframework.report.definition.builder.BaseReportBuilder;
import org.nextframework.report.definition.builder.LayoutReportBuilder;
import org.nextframework.report.definition.elements.ReportBlock;
import org.nextframework.report.definition.elements.ReportChart;
import org.nextframework.report.definition.elements.ReportComposite;
import org.nextframework.report.definition.elements.ReportConstants;
import org.nextframework.report.definition.elements.ReportGrid;
import org.nextframework.report.definition.elements.ReportImage;
import org.nextframework.report.definition.elements.ReportItem;
import org.nextframework.report.definition.elements.ReportLabel;
import org.nextframework.report.definition.elements.ReportOverlapComposite;
import org.nextframework.report.definition.elements.ReportTextField;
import org.nextframework.report.definition.elements.Subreport;
import org.nextframework.report.definition.elements.style.Border;

public class JasperDesignBuilderImpl extends AbstractJasperDesignBuilder {
	
	JasperDesign originalTemplate;
	
	int elementKeySequence = 0;
	
	Map<String, ReportItem> mappedKeys = new LinkedHashMap<String, ReportItem>();
	Map<String, JRDesignElement> mappedKeysJRElements = new LinkedHashMap<String, JRDesignElement>();
	
	JasperDesignBuilderImplComponentMapper componentMapper = new JasperDesignBuilderImplComponentMapper(this);
	
	public Map<String, JRDesignElement> getMappedKeysJRElements() {
		return mappedKeysJRElements;
	}
	public Map<String, ReportItem> getMappedKeys() {
		return mappedKeys;
	}

	@Override
	public JasperDesign getJasperDesign() {
		try {
			configureTemplate();
			overwriteDefaultSettings();
			createBands();
			
			configureParameters();
			configureColumnWidths();
			
			configureGroups();
			configureTitleBand();
			configurePageHeader();
			configureFirstPageHeader();
			configureSummaryDataHeader();
			configureSummaryDataDetail();
			configureColumnHeader();
			configureDetailHeader();
			configureDetail();
			configurePageFooter();
			configureSummary();
			configureLastPageFooter();
			configureColumnFooter();
			configureVirtualBands();
			
			fillParametersNotSet();
		} catch (JRException e) {
			throw new RuntimeException(e);
		}
		return template;
	}

	private void configureVirtualBands() {
		configureVirtualBand(definition.getSectionFirstPageHeader(),  definition.getSectionPageHeader(), true);
		configureVirtualBand(definition.getSectionSummaryDataHeader(), definition.getSectionPageHeader(), false);
		configureVirtualBand(definition.getSectionSummaryDataDetail(), definition.getSectionPageHeader(), false);
	}
	private void configureSummaryDataDetail() throws JRException {
		configureSection(definition.getSectionSummaryDataDetail(), null);
	}
	private void configureSummaryDataHeader() throws JRException {
		configureSection(definition.getSectionSummaryDataHeader(), null);
	}
	private void configureFirstPageHeader() throws JRException {
		configureSection(definition.getSectionFirstPageHeader(), new ItemConfigurer(){

			@Override
			public void configureItem(JRDesignElement element) {
				String text = "$V{PAGE_NUMBER}.equals(1)";
				if(element.getPrintWhenExpression() != null && org.springframework.util.StringUtils.hasText(element.getPrintWhenExpression().getText())){
					text += " && ((Boolean)("+ element.getPrintWhenExpression().getText()+"))";
				}
				element.setPrintWhenExpression(new JRDesignExpression(text));
				element.setRemoveLineWhenBlank(true);
			}
		});
	}
	private void configureColumnFooter() throws JRException {
		configureSection(definition.getSectionColumnFooter(), null);
	}

	private void configurePageFooter() throws JRException {
		configureSection(definition.getSectionPageFooter(), null);
	}
	private void configureLastPageFooter() throws JRException {
		configureSection(definition.getSectionLastPageFooter(), null);
	}
	private void configureSummary() throws JRException {
		configureSection(definition.getSectionSummary(), null);
	}

	private void configureParameters() throws JRException {
		for (ReportImage reportItem : definition.getImages()) {
			if(reportItem.isRendered()){
				createParameter("image"+(definition.getImageIndex(reportItem)), InputStream.class);
			}
		}
		for (ReportChart reportItem : definition.getCharts()) {
			if(reportItem.isRendered()){
				createParameter("chart"+(definition.getChartIndex(reportItem)), Chart.class);
			}
		}
		for (Subreport subreport : definition.getSubreports()) {
			createParameter("subreport"+(definition.getSubreportIndex(subreport))+"_params", Map.class);
			createParameter("subreport"+(definition.getSubreportIndex(subreport))+"_ds", JRDataSource.class);
			createParameter("subreport"+(definition.getSubreportIndex(subreport))+"_ds_map", Map.class);
		}

	}
	
	private void fillParametersNotSet() throws JRException {
		Set<String> parameters = definition.getParameters().keySet();
		for (String parameterKey : parameters) {
			if(template.getParametersMap().get(parameterKey) == null){
				JRDesignParameter parameter = new JRDesignParameter();
				parameter.setForPrompting(false);
				parameter.setName(parameterKey);
				parameter.setValueClass(Object.class);
				template.addParameter(parameter);
			}
		}
	}

	private void createParameter(String paramName, Class<?> paramType) throws JRException {
		JRDesignParameter parameter = new JRDesignParameter();
		parameter.setForPrompting(false);
		parameter.setName(paramName);
		parameter.setValueClass(paramType);
		template.addParameter(parameter);
	}

	private void configureGroups() throws JRException {
		List<ReportGroup> groups = definition.getGroups();
		for (ReportGroup reportGroup : groups) {
			String expression = reportGroup.getExpression();
			JRDesignGroup jrGroup = new JRDesignGroup();
			jrGroup.setExpression(createFieldOrParameterExpression(expression, null, true));
			jrGroup.setName(createGroupName(expression));
			
			jrGroup.setMinHeightToStartNewPage((groups.size() - template.getGroups().length + 2)*16);
			
			template.addGroup(jrGroup);
			
			//adicionar a banda do grupo
			JRDesignBand groupHeaderBand = new JRDesignBand();
			((JRDesignSection)jrGroup.getGroupHeaderSection()).addBand(groupHeaderBand);
			((JRDesignSection)jrGroup.getGroupFooterSection()).addBand(new JRDesignBand());
			
			//configurar as seçoes
			configureSection(reportGroup.getSectionHeader(), null);
			configureSection(reportGroup.getSectionFooter(), null);
			configureSection(reportGroup.getSectionDetail(), null);
			
			configureVirtualBand(getVirtualSectionForGroup(template, reportGroup, ReportSectionType.GROUP_DETAIL), groupHeaderBand, false);
			
		}
	}

	private void configureColumnWidths() {
		int itemCalculatedWidth = getBodyWidth();
		int numberOfCalculatedItens = 0;
		for (ReportColumn reportColumn : definition.getColumns()) {
			if(!reportColumn.isWidthAuto()){
				itemCalculatedWidth -= reportColumn.getWidth();
			} else {
				numberOfCalculatedItens += 1;
			}
		}
		if(numberOfCalculatedItens > 0){
			itemCalculatedWidth = itemCalculatedWidth / numberOfCalculatedItens;
		}
		if(itemCalculatedWidth < 0){
			throw new RuntimeException("The sum of the widths of the columns is greater than the report avaiable width");
		}
		for (ReportColumn reportColumn : definition.getColumns()) {
			if(reportColumn.isWidthAuto()){
				reportColumn.setWidth(itemCalculatedWidth);
			}
		}
	}

	private int getBodyWidth() {
		return getPageInnerWidth();
	}
	
	private String createGroupName(String expression) {
		return "group: "+expression;
	}

	JRDesignExpression createFieldOrParameterExpression(String expression, Class<?> class1, boolean callToString) throws JRException {
		//verificar se existe um field para a expression
		if(!expression.startsWith("param.") && !isLiteral(expression)){
			JRDesignField field = getField(expression);
			if(field == null){
				field = new JRDesignField();
				field.setName(expression);
				if(class1 != null){
					field.setValueClass(class1);
					field.setValueClassName(class1.getName());
				} else {
					field.setValueClass(Object.class);
					field.setValueClassName("java.lang.Object");
				}
				template.addField(field);
			}
		} else if(!isLiteral(expression)) {
			String exp = expression.substring(6);
			JRDesignParameter param = getParameter(exp);
			if(param == null){
				param = new JRDesignParameter();
				param.setName(exp);
				if(class1 != null){
					param.setValueClass(class1);
					param.setValueClassName(class1.getName());
				} else {
					param.setValueClass(Object.class);
					param.setValueClassName("java.lang.Object");
				}
				template.addParameter(param);
			}
		}
		JRDesignExpression expression2 = createExpression(expression, class1, callToString);
		return expression2;
	}

	private boolean isLiteral(String expression) {
		return expression.contains("\"") || expression.contains("\'");
	}

	@SuppressWarnings("deprecation") JRDesignExpression createExpression(String expression, Class<?> class1, boolean callToString) {
		if(expression.startsWith("param.")){
			JRDesignExpression expression2 = new JRDesignExpression();
			if (expression.contains(LayoutReportBuilder.FILTER_PARAMETER)) {
				expression2.setText("org.nextframework.util.Util.strings.toStringDescription($P{"+expression.substring(6)+"}, (java.util.Locale) $P{" + BaseReportBuilder.LOCALE + "})");
			}else{
				expression2.setText("$P{"+expression.substring(6)+"}");
			}
			if(class1 != null){
				expression2.setValueClass(class1);
				expression2.setValueClassName(class1.getName());
			} else {
				expression2.setValueClass(Object.class);
				expression2.setValueClassName("java.lang.Object");
			}
			return expression2;
		} else if(isLiteral(expression)) {
			if(expression.startsWith("\'")){
				expression = "\""+expression.substring(1, expression.length()-1)+"\"";
			}
			JRDesignExpression expression2 = new JRDesignExpression();
			expression2.setText(expression);
			return expression2;
		} else if(expression.startsWith("$")){
			JRDesignExpression expression2 = new JRDesignExpression();
			expression2.setText(expression);
			if(class1 != null){
				expression2.setValueClass(class1);
				expression2.setValueClassName(class1.getName());
			} else {
				expression2.setValueClass(Object.class);
				expression2.setValueClassName("java.lang.Object");
			}
			return expression2;
		} else {
			JRDesignExpression expression2 = new JRDesignExpression();
			if (callToString) {
				expression2.setText("org.nextframework.util.Util.strings.toStringDescription($F{"+expression+"}, (java.util.Locale) $P{" + BaseReportBuilder.LOCALE + "})");
			}else{
				expression2.setText("$F{"+expression+"}");
			}
			if(class1 != null){
				expression2.setValueClass(class1);
				expression2.setValueClassName(class1.getName());
			} else {
				expression2.setValueClass(Object.class);
				expression2.setValueClassName("java.lang.Object");
			}
			return expression2;
		}
	}

	private JRDesignParameter getParameter(String expression){
		JRParameter[] parameters = template.getParameters();
		JRDesignParameter parameter = null;
		for (JRParameter jrParameter : parameters) {
			if(jrParameter.getName().equals(expression)){
				parameter = (JRDesignParameter) jrParameter;
				break;
			}
		}
		return parameter;
	}
	
	private JRDesignField getField(String expression) {
		JRField[] fields = template.getFields();
		JRDesignField field = null;
		for (JRField jrField : fields) {
			if(jrField.getName().equals(expression)){
				field = (JRDesignField) jrField;
				break;
			}
		}
		return field;
	}

	private void configureDetailHeader() throws JRException {
		configureSection(definition.getSectionDetailHeader(), new ItemConfigurer(){
			@Override
			public void configureItem(JRDesignElement element) {
				element.setPrintRepeatedValues(false);
				element.setRemoveLineWhenBlank(true);
				element.setPrintInFirstWholeBand(true);
				JRGroup[] groups = template.getGroups();
				if(groups.length > 0){
					element.setPrintWhenGroupChanges(groups[groups.length-1]);
				}
			}
		});
	}
	private void configureDetail() throws JRException {
		configureSection(definition.getSectionDetail(), null);
	}
	private void configurePageHeader() throws JRException {
		configureSection(definition.getSectionPageHeader(), null);
	}
	private void configureColumnHeader() throws JRException {
		configureSection(definition.getSectionColumnHeader(), null);
	}

	private void createBands() {
		template.setTitle(new JRDesignBand());
		template.setPageHeader(new JRDesignBand());
		template.setColumnHeader(new JRDesignBand());
		((JRDesignSection)template.getDetailSection()).addBand(new JRDesignBand());
		((JRDesignSection)template.getDetailSection()).addBand(new JRDesignBand());
	}

	interface ItemConfigurer {
		void configureItem(JRDesignElement element);
	}
	
	private void configureVirtualBand(ReportSection origin, ReportSection dest, boolean invert) {
		if(origin.getRowsWithElements().size() == 0){
			return;
		}
		JRDesignBand originBand = getReportBandFor(template, origin);
		JRDesignBand destinyBand = getReportBandFor(template, dest);
		configureVirtualBand(originBand, destinyBand, invert);
	}
	private void configureVirtualBand(JRDesignBand originBand, JRDesignBand destinyBand, boolean invert) {
		if(invert){
			int height = originBand.getHeight();
			int height2 = destinyBand.getHeight();
			List<JRChild> children = destinyBand.getChildren();
			for (JRChild jrChild : children) {
				((JRDesignElement)jrChild).setY(((JRDesignElement)jrChild).getY() + height);
			}
			children = originBand.getChildren();
			for (JRChild jrChild : children) {
				destinyBand.addElement(((JRDesignElement)jrChild));
			}
			if(height2 > 0){
				destinyBand.addElement(createSeparatorLine(height));
			}
		} else {
			int height = destinyBand.getHeight();
			List<JRChild> children = originBand.getChildren();
			for (JRChild jrChild : children) {
				JRDesignElement element = (JRDesignElement)jrChild;
				element.setY(element.getY()+height);
				destinyBand.addElement(element);
			}
			if(children.size() != 0){
				destinyBand.addElement(createSeparatorLine(height));
			}
		}
		destinyBand.setHeight(originBand.getHeight() + destinyBand.getHeight() + 1);
	}
	
	private void configureSection(ReportSection section, ItemConfigurer configurer) throws JRException {
		if(configurer == null){
			configurer = new ItemConfigurer(){
				public void configureItem(JRDesignElement element) {
				}};
		}
		if(!section.isRender()){
			return;
		}
//		getReportBandFor(template, section).setSplitType(SplitTypeEnum.PREVENT);
		
		int paddingTop = 0;
		JRDesignFrame backgroundFrame = null;
		if(section.getStyle().getBackgroundColor() != null){
			backgroundFrame = new JRDesignFrame();
			backgroundFrame.setKey(BACKGROUND_FRAME_KEY+" "+(elementKeySequence++));
			backgroundFrame.setBackcolor(section.getStyle().getBackgroundColor());
			backgroundFrame.setStretchType(StretchTypeEnum.RELATIVE_TO_BAND_HEIGHT);
			configurer.configureItem(backgroundFrame);
			getReportBandFor(template, section).addElement(backgroundFrame);
		}
		if(section.getType() != ReportSectionType.GROUP_HEADER && section.getType() != ReportSectionType.GROUP_FOOTER && section.getType() != ReportSectionType.GROUP_DETAIL){
			JRDesignBand reportBand = getReportBandFor(originalTemplate, section);
			JRDesignFrame staticFrame = getStaticFrameInBand(reportBand, true);
			if(staticFrame != null && staticFrame.getWidth() <= template.getPageWidth()){
				paddingTop = staticFrame.getHeight();
				JRDesignBand toReportBand = getReportBandFor(template, section);
				configurer.configureItem(staticFrame);
				toReportBand.addElement(staticFrame);
				toReportBand.setHeight(staticFrame.getHeight());
			}
		}
		List<ReportSectionRow> rowsWithElements = section.getRowsWithElements();
		List<JRDesignFrame> rowBackgroundFrames = new ArrayList<JRDesignFrame>();
		for (int i = 0; i < rowsWithElements.size(); i++) {
			ReportSectionRow sectionRow = rowsWithElements.get(i);
			int paddingLeft = 0;
			int rowPaddingTop = 0;
			
			JRDesignFrame rowBackgroundFrame = new JRDesignFrame();
			rowBackgroundFrame.setKey(BACKGROUND_FRAME_KEY+" ROW "+(elementKeySequence++));
			rowBackgroundFrame.setY(paddingTop);
			rowBackgroundFrame.setWidth(getPageInnerWidth());
			rowBackgroundFrame.setMode(ModeEnum.OPAQUE);
			rowBackgroundFrame.setStretchType(StretchTypeEnum.NO_STRETCH);//TODO GROUP ELEMENTS
			rowBackgroundFrame.setPositionType(PositionTypeEnum.FLOAT);
			JRStyle style = template.getStylesMap().get("default");
			if(sectionRow.getStyleClass() != null){
				style = template.getStylesMap().get(sectionRow.getStyleClass());
				getReportBandFor(template, section).addElement(rowBackgroundFrame);
			}
			rowBackgroundFrame.setStyle(style);
			configurer.configureItem(rowBackgroundFrame);
			rowBackgroundFrames.add(rowBackgroundFrame);
			
			for (int j = 0; j < definition.getColumns().size(); j++) {
				ReportColumn column = definition.getColumns().get(j);
				
				ReportItem item = definition.getElementFor(sectionRow, column);
				
				int width = column.getWidth();
				if(item != null){
										
					int colspan = item.getColspan();
					ReportColumn nextColumn = column;
					while(--colspan > 0){
						if(nextColumn == null){
							throw new NullPointerException("nextColumn is null. Check if there's a colspan using more columns than avaiable for the report.");
						}
						nextColumn = nextColumn.getNext();
						if(nextColumn != null){
							width += nextColumn.getWidth();
						}
					}
					
					PrintItemResult printItemResult = printItem(paddingLeft, paddingTop, width, item, section, style, configurer);
					int height = printItemResult.height;
					if(rowPaddingTop < height){
						rowPaddingTop = height;
					}
				}
				paddingLeft += column.getWidth();
			}
					
			paddingTop += rowPaddingTop;
			paddingTop = drawRowSeparatorLine(section, rowsWithElements, paddingTop, rowPaddingTop, i);
		}
		
		for (JRDesignFrame jrDesignFrame : rowBackgroundFrames) {
			JRDesignBand toReportBand = getReportBandFor(template, section);
			jrDesignFrame.setHeight(toReportBand.getHeight() - jrDesignFrame.getY());
		}
		
		if(section.getType() != ReportSectionType.GROUP_HEADER && section.getType() != ReportSectionType.GROUP_FOOTER && section.getType() != ReportSectionType.GROUP_DETAIL){
			JRDesignBand reportBand = getReportBandFor(originalTemplate, section);
			JRDesignFrame staticFrame = getStaticFrameInBand(reportBand, false);
			if(staticFrame != null && staticFrame.getWidth() <= template.getPageWidth()){
				staticFrame.setY(paddingTop);
				JRDesignBand toReportBand = getReportBandFor(template, section);
				configurer.configureItem(staticFrame);
				toReportBand.addElement(staticFrame);
				toReportBand.setHeight(staticFrame.getHeight() + toReportBand.getHeight());
			}
		}
		
		if(backgroundFrame != null){
			backgroundFrame.setMode(ModeEnum.OPAQUE);
			backgroundFrame.setWidth(getPageInnerWidth());
			backgroundFrame.setHeight(getReportBandFor(template, section).getHeight());
		}
		Object value = section.getRenderParameters().get(JasperRenderParameters.PRINT_WHEN_EXPRESSION);
		if(value != null){
			getReportBandFor(template, section).setPrintWhenExpression(new JRDesignExpression((String) value));
		}
	}

	private int drawRowSeparatorLine(ReportSection section, List<ReportSectionRow> list, int paddingTop, int rowHeight, int i) {
		if(paddingTop > 0 && rowHeight > 0 && i + 1 < list.size()){
			JRDesignBand band = getReportBandFor(template, section);
			JRDesignLine jrDesignLine = createSeparatorLine(paddingTop);
			band.addElement(jrDesignLine);
			paddingTop++;
		}
		return paddingTop;
	}
	private JRDesignLine createSeparatorLine(int topPosition) {
		JRDesignLine jrDesignLine = new JRDesignLine();
		jrDesignLine.setForecolor(LINE_BREAK);
		jrDesignLine.setHeight(1);
		jrDesignLine.getLinePen().setLineWidth(0f);
		jrDesignLine.setPositionType(PositionTypeEnum.FLOAT);
		jrDesignLine.setWidth(getPageInnerWidth());
		jrDesignLine.setY(topPosition);
		jrDesignLine.getPropertiesMap().setProperty("ROW_SEPARATOR", "ROW_SEPARATOR");
		return jrDesignLine;
	}

	private int getPageInnerWidth() {
		return template.getPageWidth() - template.getLeftMargin() - template.getRightMargin();
	}

	protected JRDesignFrame getStaticFrameInBand(JRDesignBand reportBand, boolean top) {
		List<JRChild> children = reportBand.getChildren();
		for (JRChild jrChild : children) {
			if(jrChild instanceof JRDesignFrame){
				String property = ((JRDesignFrame) jrChild).getPropertiesMap().getProperty("static");
				if(property != null){
					if(property.equals("top") && top){
						JRDesignFrame result = (JRDesignFrame) jrChild.clone();
						result.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
						return result;
					} else if(!property.equals("top") && !top){
						JRDesignFrame result = (JRDesignFrame) jrChild.clone();
						result.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_BOTTOM);
						return result;
					}
				}
			}
		}
		return null;
	}

	class PrintItemResult {
		int height;
		
	}
	
	private PrintItemResult printItem(int paddingLeft, int paddingTop, int width, ReportItem item, ReportSection section, JRStyle style, ItemConfigurer configurer) throws JRException {
		JRDesignBand band = getReportBandFor(template, section);
		JRDesignElement element = componentMapper.getElementFor(item, width, section, style);
		if(element == null){
			return new PrintItemResult();
		}
		if(element.getStyle() == null){
			element.setStyle(style);
		}
		if(configurer != null){
			configurer.configureItem(element);
		}
		if(section.getStyle().getForegroundColor() != null){
			element.setForecolor(section.getStyle().getForegroundColor());
		}
		element.setY(paddingTop);
		element.setX(paddingLeft);
//		int elementPadding = item.getStyle().getPaddingBottom() + item.getStyle().getPaddingTop();
		if(band.getHeight() < paddingTop + element.getHeight()){
			band.setHeight(paddingTop + element.getHeight());
		}
		
		band.addElement(element);
		
		PrintItemResult itemResult = new PrintItemResult();
		itemResult.height = element.getHeight();
		return itemResult;
	}

	void setKeys(ReportItem item, JRDesignElement result) {
		String key;
		ReportSectionRow row = item.getRow();
		ReportColumn column = item.getColumn();
		if(row != null && column != null){
			key = item.getDescriptionName()+" "+(elementKeySequence++)+" C["+column.getIndex()+",span="+item.getColspan()+"] R["+row.getSection().getType()+","+row.getRowNumber()+"] W["+item.getWidth()+"]";
		} else {
			key = item.getDescriptionName()+" "+(elementKeySequence++)+" C[] R[] W["+item.getWidth()+"]";
		}
		result.setKey(key);
		mappedKeys.put(key, item);
		mappedKeysJRElements.put(key, result);
	}

	void configureRenderParameters(JRDesignElement result, Map<String, Object> renderParameters) {
		Set<String> parameters = renderParameters.keySet();
		for (String parameter : parameters) {
			if(parameter.startsWith("jasper-")){
				Method setterMethod = null;
				Object value = renderParameters.get(parameter);
				if(value == null){
					continue;
				}
				String methodName = convertToMethodSetter(parameter);
				Method[] declaredMethods = result.getClass().getMethods();
				for (Method method : declaredMethods) {
					if(method.getName().equals(methodName)){
						setterMethod = method;
						break;
					}
				}
				if(setterMethod != null){
					boolean classType = setterMethod.getParameterTypes()[0] instanceof Class<?>;
					if(classType && (getFirstParameterAsClass(setterMethod).isAssignableFrom(value.getClass())
							|| getFirstParameterAsClass(setterMethod).isPrimitive())){
						try {
							setterMethod.invoke(result, value);
						} catch (Exception e) {
							throw new RuntimeException("Error setting render parameter "+parameter+" with value "+value, e);
						}
					} else {
						throw new RuntimeException("The render parameter "+parameter+" should be of type "+setterMethod.getParameterTypes()[0]+" found "+value.getClass());
					}
				}
			}
		}
	}

	private Class<?> getFirstParameterAsClass(Method setterMethod) {
		return ((Class<?>)setterMethod.getParameterTypes()[0]);
	}

	private String convertToMethodSetter(String parameter) {
		char[] charArray = parameter.substring("jasper".length()).toCharArray();
		StringBuilder setter = new StringBuilder("set");
		for (int i = 0; i < charArray.length; i++) {
			char c = charArray[i];
			if(c == '-'){
				c = charArray[++i];
				setter.append(Character.toUpperCase(c));
			} else {
				setter.append(c);
			}
		}
		return setter.toString();
	}

	void copyBorderToPen(Border border, JRBoxPen pen) {
		pen.setLineColor(border.getColor());
		pen.setLineStyle(LineStyleEnum.SOLID);
		pen.setLineWidth(border.getWidth() / 4.0f);
	}



	JRDesignStaticText getInnerStaticText(JRDesignElement el) {
		if(el instanceof JRDesignStaticText){
			return (JRDesignStaticText) el;
		} else if(el instanceof JRDesignFrame){
			List<JRChild> children = ((JRDesignFrame) el).getChildren();
			for (JRChild jrChild : children) {
				JRDesignStaticText innerStaticText = getInnerStaticText((JRDesignElement) jrChild);
				if(innerStaticText != null){
					return innerStaticText;
				}
			}
		}
		return null;
	}

	JRDesignTextField getInnerTextField(JRDesignElement el) {
		if(el instanceof JRDesignTextField){
			return (JRDesignTextField) el;
		} else if(el instanceof JRDesignFrame){
			List<JRChild> children = ((JRDesignFrame) el).getChildren();
			for (JRChild jrChild : children) {
				JRDesignTextField innerTextField = getInnerTextField((JRDesignElement) jrChild);
				if(innerTextField != null){
					return innerTextField;
				}
			}
		}
		return null;
	}
	JRDesignElement findTextFieldInList(ReportTextField reportTextField, List<JRChild> searchItens) {
		String reportElementType = reportTextField.getType();
		String reportElementDesign = reportTextField.getStyle().getDesign();
		
		JRDesignElement element = findElementTypeAndDesignInsideSection(searchItens, reportElementType, reportElementDesign, false);
		if(element != null){
			return element;
		}
		return new JRDesignTextField();
	}

	JRDesignElement findStaticTextInList(ReportLabel reportLabel, List<JRChild> searchItens) {
		String reportElementType = reportLabel.getType();
		String reportElementDesign = reportLabel.getStyle().getDesign();
		
		JRDesignElement element = findElementTypeAndDesignInsideSection(searchItens, reportElementType, reportElementDesign, false);
		if(element != null){
			return element;
		}
		return new JRDesignStaticText();
	}

	private JRDesignElement findElementTypeAndDesignInsideSection(List<JRChild> searchItens, String reportElementType, String reportElementDesign, boolean throwExceptionIfNotFound) {
		if(reportElementDesign == null || reportElementDesign.equals("")){
			reportElementDesign = "default";
		}
		Class<? extends JRDesignElement> elementClass = getReportElementClass(reportElementType);
		for (JRChild jrChild : searchItens) {
			JRPropertiesMap propertiesMap = ((JRDesignElement) jrChild).getPropertiesMap();
			String propertyDesign = propertiesMap.getProperty("design");
			if(propertyDesign == null || propertyDesign.equals("")){
				propertyDesign = "default";
			}
			if(jrChild instanceof JRDesignFrame){
				String propertyElement = propertiesMap.getProperty("element");
				if(propertyElement != null && propertyElement.equals(reportElementType)
						&& propertyDesign != null && propertyDesign.equals(reportElementDesign)){
					return (JRDesignElement) jrChild;
				}
			}
			if(elementClass != null && elementClass.isAssignableFrom(jrChild.getClass())){
				if(propertyDesign != null && propertyDesign.equals(reportElementDesign)){
					return (JRDesignElement) jrChild;
				}
			} else if(jrChild instanceof JRDesignTextField){
				if(((JRDesignTextField) jrChild).getExpression().getText().equalsIgnoreCase("\""+reportElementType+"\"")){
					return (JRDesignElement) jrChild;
				}
			} else if(jrChild instanceof JRDesignStaticText){
				if(((JRDesignStaticText) jrChild).getText().equalsIgnoreCase(reportElementType)){
					return (JRDesignElement) jrChild;
				}
			}
		}
		if(throwExceptionIfNotFound){
			throw new RuntimeException("element '"+reportElementType+"' design '"+reportElementDesign+"' not found");
		} else {
			return null;
		}
	}
	
	private Class<? extends JRDesignElement> getReportElementClass(String reportElementType) {
		if(reportElementType.equals("label")){
			return JRDesignStaticText.class;
		} else if(reportElementType.equals("field")){
			return JRDesignTextField.class;
		}
		return null;
	}

	List<JRChild> compileItemsFromOriginalTemplate(ReportSection section) {
		List<JRChild> elements = new ArrayList<JRChild>();
		if(section instanceof ReportGroupSection){
			ReportGroup group = ((ReportGroupSection) section).getGroup();
			List<ReportGroup> groups = getDefinition().getGroups();
			int groupIndex = 0;
			for (; groupIndex < groups.size(); groupIndex++) {
				if(groups.get(groupIndex).getExpression().equals(group.getExpression())){
					break;
				}
			}
			
			JRGroup[] templateGroups = originalTemplate.getGroups();
			int groupTemplateIndex = Math.min(groupIndex, templateGroups.length-1);
			JRGroup jrGroup;
			if(groupTemplateIndex == -1){
				jrGroup = new JRDesignGroup(); 
				((JRDesignSection)jrGroup.getGroupHeaderSection()).addBand(new JRDesignBand());
				((JRDesignSection)jrGroup.getGroupFooterSection()).addBand(new JRDesignBand());
			} else {
				jrGroup = templateGroups[groupTemplateIndex];
			}
			
			elements.addAll(((JRDesignGroup)jrGroup).getGroupHeaderSection().getBands()[0].getChildren());
			elements.addAll(((JRDesignGroup)jrGroup).getGroupFooterSection().getBands()[0].getChildren());
		} else {
			elements.addAll(getReportBandFor(originalTemplate, section).getChildren());
		}
		elements.addAll(((JRDesignBand)originalTemplate.getPageHeader()).getChildren());
		elements.addAll(((JRDesignBand)originalTemplate.getColumnHeader()).getChildren());
		elements.addAll(((JRDesignSection)originalTemplate.getDetailSection()).getBands()[0].getChildren());
		elements.addAll(((JRDesignBand)originalTemplate.getColumnFooter()).getChildren());
		elements.addAll(((JRDesignBand)originalTemplate.getPageFooter()).getChildren());
		elements.addAll(((JRDesignBand)originalTemplate.getSummary()).getChildren());
		return elements;
	}

	String getPdfFontName(Boolean bold, Boolean italic, JRStyle jrStyle) {
		if(bold == null && italic == null){
			return null;
		}
		String pdfFontName = "Helvetica";
		String extraInfo = "";
		if(bold != null && (bold || jrStyle != null && jrStyle.getFontName() != null && jrStyle.getFontName().contains("Bold"))){
			extraInfo += "Bold";
		}
		if(italic != null && (italic || jrStyle != null && jrStyle.getFontName() != null && jrStyle.getFontName().contains("Oblique"))){
			extraInfo += "Oblique";
		}
		pdfFontName = pdfFontName + (extraInfo.length()>0?"-"+extraInfo:"");
		return pdfFontName;
	}

	JRDesignElement getFrameForItens(ReportItem item, List<ReportItem> items, int frameWidth, int colspan, ReportSection section, JRStyle jrStyle) throws JRException {
		
		JRDesignFrame frame; 
		if(item instanceof ReportBlock){
			frame = (JRDesignFrame) createFrameForBlock((ReportBlock)item, section, colspan > 1, frameWidth);
			List<JRChild> list = getAllChildrenFlat(frame.getChildren());
			for (ReportItem child : items) {
				if(child instanceof ReportLabel){
					JRDesignStaticText staticText = getInnerStaticText(findStaticTextInList((ReportLabel) child, list));
					staticText.setText(((ReportLabel) child).getText());
				} else if(child instanceof ReportTextField) {
					ReportTextField tf = (ReportTextField) child;
					JRDesignTextField textField = getInnerTextField(findTextFieldInList(tf, list));
					textField.setExpression(createFieldOrParameterExpression(tf.getExpression(), null, tf.isCallToString()));
				} else {
					throw new RuntimeException("element not supported in blocks "+child);
				}
			}
			if(colspan > 1){ //if using colspan respect colspan width
				frame.setWidth(frameWidth);
			}
		} else if(item instanceof ReportComposite){
			frame = new JRDesignFrame();
			//TODO PEGAR DO TEMPLATE
			frame.setWidth(frameWidth);
			frame.setStretchType(StretchTypeEnum.NO_STRETCH);
			
			int[] columnWidths = calculateWidthsForReportComposite(item, items, frameWidth, frame);
			
			List<JRDesignElement> elements = createJrElementsFromItems(items, frameWidth, columnWidths, section, jrStyle);
			
			if(item instanceof ReportOverlapComposite){
				adjustPaddingsAndAddElementsForOverlap(frame, elements);
			} else {
				adjustPaddingsAndAddElements(frame, elements);
			}
		} else {
			throw new RuntimeException("report element unknown "+item);
		}
		frame.getPropertiesMap().setProperty("element", item instanceof ReportBlock? ((ReportBlock)item).getElement() : "composite");
		frame.getPropertiesMap().setProperty("colspan", String.valueOf(colspan));
		frame.setForecolor(item.getStyle().getForegroundColor());
		frame.setBackcolor(item.getStyle().getBackgroundColor());
		if(item.getStyle().getBackgroundColor() != null){
			frame.setMode(ModeEnum.OPAQUE);
		}
		return frame;
	}
	public int[] calculateWidthsForReportComposite(ReportItem item, List<ReportItem> items, int frameWidth, JRDesignFrame frame) {
		int[] columnWidths;
		if(item instanceof ReportOverlapComposite) {
			ReportOverlapComposite overlapComposite = new ReportOverlapComposite();
			if(overlapComposite.isWidthPercent()){
				throw new IllegalArgumentException("OverlapComposite does not support percentual width");
			}
			if(overlapComposite.isWidthAuto()){
				columnWidths = new int[]{frameWidth};
			} else {
				frame.setWidth(overlapComposite.getWidth());
				columnWidths = new int[]{overlapComposite.getWidth()};
			}
		} else if(item instanceof ReportGrid){
			//recalculate elements widths
			int width = frameWidth;
			ReportGrid reportGrid = (ReportGrid) item;
			columnWidths = copy(reportGrid.getColumnWidths());
			if(!reportGrid.isWidthAuto() && !reportGrid.isWidthPercent()){
				frame.setWidth(reportGrid.getWidth());
				width = reportGrid.getWidth();
			}
			recalculateWidths(width, columnWidths);
		} else {
			int avaiableWidth = frameWidth;
			int autoWidthCount = 0;
			columnWidths = new int[items.size()];
			int begin = 0;
			for (int i = 0; i < items.size(); i++) {
				ReportItem reportItem = items.get(i);
				if(reportItem.isWidthAuto()){
					autoWidthCount++;
				} else if (reportItem.isWidthPercent()){
					columnWidths[i] = calculatePercentSize(frameWidth, reportItem);
				} else {
					columnWidths[i] = reportItem.getWidth();
				}
				avaiableWidth -= columnWidths[i];
				boolean lastItem = i == items.size() -1;
				if(avaiableWidth <= autoWidthCount || lastItem){
					//there is not enought space for the last element
					if(autoWidthCount > 0){
						if(!reportItem.isWidthAuto() && avaiableWidth <= autoWidthCount){
							avaiableWidth += columnWidths[i];
						}
						int autoWidth = avaiableWidth / autoWidthCount;
						
						for (int j = begin; j < (reportItem.isWidthAuto()?i+1:i); j++) {
							if(items.get(j).isWidthAuto()){
								columnWidths[j] = autoWidth;
							}
						}
					}
					autoWidthCount = 0;
					avaiableWidth = frameWidth;
					if(!reportItem.isWidthAuto()){
						avaiableWidth -= columnWidths[i];
					}
					begin = i;
				}
			}
		}
		return columnWidths;
	}
	public void adjustPaddingsAndAddElementsForOverlap(JRDesignFrame frame, List<JRDesignElement> elements) {
		int paddingLeft = 0;
		int paddingTop = 0;
		for (JRDesignElement jrDesignElement : elements) {
//			if(frame.getWidth() < paddingLeft + jrDesignElement.getWidth()){
//				paddingLeft = 0;
//				paddingTop = frame.getHeight();
//			}
			jrDesignElement.setX(paddingLeft);
			jrDesignElement.setY(paddingTop);
//			
			if(frame.getHeight() < jrDesignElement.getHeight() + jrDesignElement.getY()){
				frame.setHeight(jrDesignElement.getHeight() + jrDesignElement.getY());
			}
//			paddingLeft += jrDesignElement.getWidth();
			frame.addElement(jrDesignElement);
		}
	}
	public void adjustPaddingsAndAddElements(JRDesignFrame frame, List<JRDesignElement> elements) {
		int paddingLeft = 0;
		int paddingTop = 0;
		for (JRDesignElement jrDesignElement : elements) {
			if(frame.getWidth() < paddingLeft + jrDesignElement.getWidth()){
				paddingLeft = 0;
				paddingTop = frame.getHeight();
			}
			jrDesignElement.setX(paddingLeft);
			jrDesignElement.setY(paddingTop);
			
			if(frame.getHeight() < jrDesignElement.getHeight() + jrDesignElement.getY()){
				frame.setHeight(jrDesignElement.getHeight() + jrDesignElement.getY());
			}
			paddingLeft += jrDesignElement.getWidth();
			frame.addElement(jrDesignElement);
		}
	}
	
	public List<JRDesignElement> createJrElementsFromItems(List<ReportItem> items, int frameWidth, int[] columnWidths, ReportSection section, JRStyle jrStyle) throws JRException {
		//if(itemCalculatedWidth < 0){
		//	throw new RuntimeException("Report Design not valid. The itens use more width than avaiable (avaiable: "+frameWidth+"). Itens: "+items);
		//}
		int index = 0;
		List<JRDesignElement> elements = new ArrayList<JRDesignElement>();
		for (ReportItem reportItem : items) {
			int suggestedSize = columnWidths[index];
			index ++;
			if(index == columnWidths.length){
				index = 0;
			}
			JRDesignElement elementFor = componentMapper.getElementFor(reportItem, suggestedSize, section, jrStyle);
			if(elementFor.getStyle() == null){
				elementFor.setStyle(jrStyle);
			}
			elements.add(elementFor);
		}
		return elements;
	}
	public int calculatePercentSize(int frameWidth, ReportItem reportItem) {
		return (int) ((reportItem.getWidth() - ReportConstants.PERCENT_WIDTH) * 0.01 * frameWidth);
	}

	private void recalculateWidths(int width, int[] columnWidths) {
		int usedWidth = 0;
		int autoColumns = 0;
		for (int i = 0; i < columnWidths.length; i++) {
			if((columnWidths[i] & ReportConstants.AUTO_WIDTH) == ReportConstants.AUTO_WIDTH){
				autoColumns++;
				continue;
			} else if((columnWidths[i] & ReportConstants.PERCENT_WIDTH) == ReportConstants.PERCENT_WIDTH){
				columnWidths[i] = (int)((columnWidths[i] - ReportConstants.PERCENT_WIDTH) * 0.01 * width);
			}
			usedWidth += columnWidths[i];
		}
		if(autoColumns > 0){
			int autowidth = (width - usedWidth) / autoColumns;
			for (int i = 0; i < columnWidths.length; i++) {
				if((columnWidths[i] & ReportConstants.AUTO_WIDTH) == ReportConstants.AUTO_WIDTH){
					columnWidths[i] = autowidth;
				}
			}
		}
	}
	
	private int[] copy(int[] columnWidths) {
		int[] copy = new int[columnWidths.length];
		System.arraycopy(columnWidths, 0, copy, 0, columnWidths.length);
		return copy;
	}
	private JRDesignFrame createFrameForBlock(ReportBlock item, ReportSection section, boolean resizeTo, int frameWidth) {
		List<JRChild> searchItens = compileItemsFromOriginalTemplate(section);
		String element = item.getElement();
		for (JRChild jrChild : searchItens) {
			if(jrChild instanceof JRDesignFrame){
				JRPropertiesMap propertiesMap = ((JRDesignFrame) jrChild).getPropertiesMap();
				String propertyElement = propertiesMap.getProperty("element");
				if(propertyElement != null && propertyElement.equals(element)){
					JRDesignFrame clone = (JRDesignFrame) jrChild.clone();
					if(resizeTo){
						List<JRChild> allChildrenFlat = getAllChildrenFlat(clone.getChildren());
						for (JRChild jrChild2 : allChildrenFlat) {
							if(((JRDesignElement)jrChild2).getWidth() == clone.getWidth()){
								//resize child that is full width
								((JRDesignElement)jrChild2).setWidth(frameWidth);
							}
						}
					}
					return clone;
				}
			}
		}
		return new JRDesignFrame();
	}

	Map<JasperDesign, Map<ReportSection, JRDesignBand>> virtualBands = new HashMap<JasperDesign, Map<ReportSection, JRDesignBand>>();

	private JRDesignBand getReportBandFor(JasperDesign template, ReportSection section) {
		if(!virtualBands.containsKey(template)){
			virtualBands.put(template, new HashMap<ReportSection, JRDesignBand>());
		}
		Map<ReportSection, JRDesignBand> map = virtualBands.get(template);
		switch (section.getType()) {
			case TITLE:			return (JRDesignBand) template.getTitle();
			case PAGE_HEADER:	return (JRDesignBand) template.getPageHeader();
			case PAGE_FOOTER:	return (JRDesignBand) template.getPageFooter();
			case COLUMN_HEADER:	return (JRDesignBand) template.getColumnHeader();
			case COLUMN_FOOTER:	return (JRDesignBand) template.getColumnFooter();
			case LAST_PAGE_FOOTER:
				if(template.getLastPageFooter() == null){
					//last page footer will be created dynamically by demand
					template.setLastPageFooter(new JRDesignBand());
				}
				return (JRDesignBand) template.getLastPageFooter();
			case SUMARY:		return (JRDesignBand) template.getSummary();
			case DETAIL_HEADER: return (JRDesignBand) template.getDetailSection().getBands()[0];
			case DETAIL:		return (JRDesignBand) (template.getDetailSection().getBands().length > 1? template.getDetailSection().getBands()[1]: template.getDetailSection().getBands()[0]);
			case GROUP_HEADER:  return (JRDesignBand) getSectionForGroup(template, ((ReportGroupSection)section).getGroup()).getGroupHeaderSection().getBands()[0];
			case GROUP_FOOTER:  return (JRDesignBand) getSectionForGroup(template, ((ReportGroupSection)section).getGroup()).getGroupFooterSection().getBands()[0];
			case GROUP_DETAIL:  return (JRDesignBand) getVirtualSectionForGroup(template, ((ReportGroupSection)section).getGroup(), ReportSectionType.GROUP_DETAIL);
			case FIRST_PAGE_HEADER :
			case SUMARY_DATA_HEADER:
			case SUMARY_DATA_DETAIL:
				if(map.get(section) == null){
					map.put(section, new JRDesignBand());
				}
				return map.get(section);
			default:
				throw new RuntimeException("Cannot determine JRBand for section "+section);
		}
	}
	
	Map<JasperDesign, Map<JRGroup, Map<ReportSectionType, JRDesignBand>>> virtualGroupBands = new HashMap<JasperDesign, Map<JRGroup,Map<ReportSectionType,JRDesignBand>>>();

	private JRDesignBand getVirtualSectionForGroup(JasperDesign template, ReportGroup group, ReportSectionType sectionType) {
		if(virtualGroupBands.get(template) == null){
			virtualGroupBands.put(template, new HashMap<JRGroup, Map<ReportSectionType,JRDesignBand>>());
		}
		Map<JRGroup, Map<ReportSectionType, JRDesignBand>> map = virtualGroupBands.get(template);
		String groupName = createGroupName(group.getExpression());
		JRGroup[] groups = template.getGroups();
		for (JRGroup jrGroup : groups) {
			if(jrGroup.getName().equals(groupName)){
				if(map.get(jrGroup) == null){
					map.put(jrGroup, new HashMap<ReportSectionType, JRDesignBand>());
				}
				if(map.get(jrGroup) == null){
					map.put(jrGroup, new HashMap<ReportSectionType, JRDesignBand>());
				}
				Map<ReportSectionType, JRDesignBand> map2 = map.get(jrGroup);
				if(map2.get(sectionType) == null){
					map2.put(sectionType, new JRDesignBand());
				}
				return map2.get(sectionType);
			}
		}
		throw new RuntimeException("Group "+group+" not found in report");
	}
	private JRDesignGroup getSectionForGroup(JasperDesign template, ReportGroup group) {
		String groupName = createGroupName(group.getExpression());
		JRGroup[] groups = template.getGroups();
		for (JRGroup jrGroup : groups) {
			if(jrGroup.getName().equals(groupName)){
				return (JRDesignGroup) jrGroup;
			}
		}
		throw new RuntimeException("Group "+group+" not found in report");
	}

	private void configureTitleBand() throws JRException {
		if(!definition.getSectionTitle().isRender()){
			return;
		}
		JRDesignBand titleBand = (JRDesignBand)template.getTitle();
		if(org.springframework.util.StringUtils.hasText(definition.getTitle()) || org.springframework.util.StringUtils.hasText(definition.getSubtitle()) || definition.getReportTitleItems().size() > 0){
			String design = getDefinition().getStyle().getDesign();
			List<JRChild> titleElements = originalTemplate.getTitle().getChildren();
			List<JRChild> removed = removeNotInDesign(titleElements, design);
			for (JRChild jrChild : titleElements) {
				titleBand.addElement((JRDesignElement) jrChild.clone());
			}
			titleBand.setHeight(originalTemplate.getTitle().getHeight());
			for (JRChild jrChild : removed) {
				titleBand.setHeight(titleBand.getHeight() - ((JRDesignElement)jrChild).getHeight());
			}
			if(titleBand.getChildren().size() == 1){
				JRDesignElement firstChild = (JRDesignElement)titleBand.getChildren().get(0);
				firstChild.setY(0);
				titleBand.setHeight(firstChild.getHeight() + firstChild.getY());
			}
			
			List<JRChild> allChildren = getAllChildrenFlat(titleBand.getChildren());
			replaceTextElement(allChildren, "TITLE", getDefinition().getTitle());
			replaceTextElement(allChildren, "SUBTITLE", getDefinition().getSubtitle());
		}
		
		if(definition.getReportTitleItems().size() > 0){
			//put title elements in report
			JRDesignFrame titleBodyFrame = null;
			List<JRChild> allChildren = getAllChildrenFlat(titleBand.getChildren());
			for (JRChild jrChild : allChildren) {
				if(jrChild instanceof JRDesignFrame){
					if(((JRDesignFrame) jrChild).getPropertiesMap().containsProperty("titlebody")){
						titleBodyFrame = (JRDesignFrame) jrChild.clone(); 
					}
				}
			}
			if(titleBodyFrame == null){
				throw new RuntimeException("Report definition has title itens but there is no frame with property titlebody in template.");
				//return;
			}
			JRDesignElement frameForItens = getFrameForItens(new ReportComposite(),	definition.getReportTitleItems(), titleBodyFrame.getWidth(), 1, definition.getSectionTitle(), template.getDefaultStyle());
			titleBodyFrame.addElement(frameForItens);
			
			if(frameForItens.getHeight() > titleBodyFrame.getHeight()){
				int difference = frameForItens.getHeight() - titleBodyFrame.getHeight();
				titleBand.setHeight(titleBand.getHeight() + difference);
				titleBodyFrame.setHeight(titleBodyFrame.getHeight() + difference);
			}
			
			titleBand.addElement(titleBodyFrame);
		}
	}

	private void replaceTextElement(List<JRChild> allChildren, String element, String value) {
		JRDesignTextElement title = findTextElement(allChildren, element);
		if(title instanceof JRDesignTextField){
			((JRDesignTextField) title).setExpression(createExpression("\""+value+"\"", null, false));
		}
		if(title instanceof JRDesignStaticText){
			((JRDesignStaticText) title).setText(value);
		}
	}

	private JRDesignTextElement findTextElement(List<JRChild> allChildren, String exp) {
		for (JRChild jrChild : allChildren) {
			String text = null;
			if(jrChild instanceof JRDesignTextField){
				text = ((JRDesignTextField) jrChild).getExpression().getText();
			}
			if(jrChild instanceof JRDesignStaticText){
				text = ((JRDesignStaticText) jrChild).getText();
			}
			if(text != null && (text.equalsIgnoreCase(exp) || text.equalsIgnoreCase("\""+exp+"\""))){
				return (JRDesignTextElement) jrChild;
			}
		}
		return null;
	}

	List<JRChild> getAllChildrenFlat(List<JRChild> children) {
		List<JRChild> result = new ArrayList<JRChild>();
		for (JRChild jrChild : children) {
			result.add(jrChild);
		}
		for (JRChild jrChild : children) {
			if(jrChild instanceof JRDesignElementGroup){
				result.addAll(((JRDesignElementGroup) jrChild).getChildren());
			}
			if(jrChild instanceof JRDesignFrame){
				result.addAll(((JRDesignFrame) jrChild).getChildren());
			}
		}
		return result;
	}

	private List<JRChild> removeNotInDesign(List<JRChild> titleElements, String design) {
		List<JRChild> notInDesign = new ArrayList<JRChild>();
		for (Iterator<?> iterator = titleElements.iterator(); iterator.hasNext();) {
			JRChild jrChild = (JRChild) iterator.next();
			if(!getDesign(jrChild).equals(design)){
				notInDesign.add(jrChild);
				iterator.remove();
			}
		}
		return notInDesign;
	}

	protected void configureTemplate() throws JRException {
		createEmptyTemplate();
	}

	private void createEmptyTemplate() throws JRException {
		this.originalTemplate = getTemplate();
		template = new JasperDesign();
		template.setName(getDefinition().getReportName());
		template.setBackground(originalTemplate.getBackground());
		template.setColumnDirection(originalTemplate.getColumnDirection());
		template.setDefaultStyle(originalTemplate.getDefaultStyle());
		template.setFilterExpression(originalTemplate.getFilterExpression());
		template.setFormatFactoryClass(originalTemplate.getFormatFactoryClass());
		template.setPageHeight(originalTemplate.getPageHeight());
		template.setPageWidth(originalTemplate.getPageWidth());
		
		template.setBottomMargin(originalTemplate.getBottomMargin());
		template.setTopMargin(originalTemplate.getTopMargin());
		template.setRightMargin(originalTemplate.getRightMargin());
		template.setLeftMargin(originalTemplate.getLeftMargin());
		
		template.setResourceBundle(originalTemplate.getResourceBundle());
		
		if(definition.getStyle().getNoMargin() != null && definition.getStyle().getNoMargin()){
			int sideMargin = template.getLeftMargin() + template.getRightMargin();
			template.setLeftMargin(0);
			template.setRightMargin(0);
			template.setTopMargin(0);
			template.setBottomMargin(0);
			
			template.setPageWidth(template.getPageWidth() - sideMargin);
		}
		if(definition.getStyle().getPageWidth() != null){
			int reduceWidth = template.getLeftMargin() + template.getRightMargin();
			template.setColumnWidth(definition.getStyle().getPageWidth() - reduceWidth);
			template.setPageWidth(definition.getStyle().getPageWidth());
		}
		if(definition.getStyle().getPageHeight() != null){
			template.setPageHeight(definition.getStyle().getPageHeight());
		}
		
		JRStyle[] styles = originalTemplate.getStyles();
		for (JRStyle jrStyle : styles) {
			template.addStyle(jrStyle);
		}
		JRStyle defaultStyle = template.getStylesMap().get("default");
		if(defaultStyle == null){
			defaultStyle = new JRDesignStyle();
			defaultStyle.setBackcolor(Color.WHITE);
			((JRDesignStyle)defaultStyle).setName("default");
			template.setDefaultStyle(defaultStyle);
		} else {
			template.setDefaultStyle(defaultStyle);
		}
				
		JRParameter[] parameters = originalTemplate.getParameters();
		for (JRParameter jrParameter : parameters) {
			if(template.getParametersMap().get(jrParameter.getName()) == null){
				template.addParameter((JRParameter) jrParameter.clone());
			}
		}
		
		if(originalTemplate.getSummary() == null){
			originalTemplate.setSummary(new JRDesignBand());
		}
		if(originalTemplate.getLastPageFooter() == null){
			originalTemplate.setLastPageFooter(new JRDesignBand());
		}
		
		template.setColumnFooter(new JRDesignBand());
		template.setPageFooter(new JRDesignBand());
		template.setSummary(new JRDesignBand());
//		template.setLastPageFooter(new JRDesignBand());
	}

	protected void overwriteDefaultSettings() {
		getTemplate().setLanguage(JRReport.LANGUAGE_JAVA);
		getTemplate().setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
	}
}
