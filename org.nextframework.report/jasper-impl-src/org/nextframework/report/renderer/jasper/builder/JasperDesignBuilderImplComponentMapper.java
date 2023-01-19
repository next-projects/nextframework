package org.nextframework.report.renderer.jasper.builder;

import java.awt.Color;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nextframework.chart.Chart;
import org.nextframework.report.definition.ReportParent;
import org.nextframework.report.definition.ReportSection;
import org.nextframework.report.definition.ReportSectionType;
import org.nextframework.report.definition.elements.ReportChart;
import org.nextframework.report.definition.elements.ReportConstants;
import org.nextframework.report.definition.elements.ReportImage;
import org.nextframework.report.definition.elements.ReportItem;
import org.nextframework.report.definition.elements.ReportLabel;
import org.nextframework.report.definition.elements.ReportTextElement;
import org.nextframework.report.definition.elements.ReportTextField;
import org.nextframework.report.definition.elements.Subreport;
import org.nextframework.report.definition.elements.style.ReportAlignment;
import org.nextframework.report.definition.elements.style.ReportBasicStyle;

import net.sf.jasperreports.engine.JRBoxContainer;
import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.base.JRBaseLineBox;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignFrame;
import net.sf.jasperreports.engine.design.JRDesignImage;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignSubreport;
import net.sf.jasperreports.engine.design.JRDesignTextElement;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;
import net.sf.jasperreports.engine.type.HorizontalAlignEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.OnErrorTypeEnum;
import net.sf.jasperreports.engine.type.PositionTypeEnum;

public class JasperDesignBuilderImplComponentMapper {
	
	public static int MAX_REPORT_CHART_AUTO_HEIGHT = 100;
	
	JasperDesignBuilderImpl jasperDesignBuilderImpl;

	public JasperDesignBuilderImplComponentMapper(JasperDesignBuilderImpl jasperDesignBuilderImpl) {
		this.jasperDesignBuilderImpl = jasperDesignBuilderImpl;
	}

	JRDesignElement getElementFor(ReportItem item, int suggestedWidth, ReportSection section, JRStyle jrStyle) throws JRException {
		JRDesignElement result;
		int computeWidth = suggestedWidth;
		if(item instanceof ReportLabel){
			ReportLabel reportLabel = (ReportLabel) item;
			JRDesignElement returnElement = section.getType() != ReportSectionType.TITLE? (JRDesignElement) jasperDesignBuilderImpl.findStaticTextInList(reportLabel, jasperDesignBuilderImpl.compileItemsFromOriginalTemplate(section)).clone(): new JRDesignStaticText();
			JRDesignStaticText jrDesignStaticText = jasperDesignBuilderImpl.getInnerStaticText(returnElement);
			jrDesignStaticText.setText(reportLabel.getText());
			
			configureFrameWidthForTextElement(returnElement, computeWidth);
			configureTextElement(computeWidth, reportLabel, jrDesignStaticText, section, jrStyle);
			result = returnElement;
		} else if(item instanceof ReportTextField){
			ReportTextField reportTextField = (ReportTextField) item;
			String expression = reportTextField.getExpression();
			if(expression == null){
				return null;
			}
			JRDesignElement returnElement = section.getType() != ReportSectionType.TITLE? (JRDesignElement) jasperDesignBuilderImpl.findTextFieldInList(reportTextField, jasperDesignBuilderImpl.compileItemsFromOriginalTemplate(section)).clone():  new JRDesignTextField();
			JRDesignTextField jrDesignTextField = jasperDesignBuilderImpl.getInnerTextField(returnElement);
			JRDesignExpression jrExpr = jasperDesignBuilderImpl.createFieldOrParameterExpression(expression, null, true);
			jrDesignTextField.setExpression(jrExpr);
			if(reportTextField.getPatternExpression() != null){
				String originalExpressionText = jrExpr.getText();
				jrDesignTextField.setExpression(new JRDesignExpression(reportTextField.getPatternExpression().replace("${0}", originalExpressionText)));
			}
			jrDesignTextField.setBlankWhenNull(true);
			jrDesignTextField.setEvaluationTime(EvaluationTimeEnum.AUTO);
			jrDesignTextField.setStretchWithOverflow(true);
			jrDesignTextField.setPositionType(PositionTypeEnum.FLOAT);
			jrDesignTextField.setPattern(reportTextField.getPattern());
			configureFrameWidthForTextElement(returnElement, computeWidth);
			configureTextElement(computeWidth, reportTextField, jrDesignTextField, section, jrStyle);
			result = returnElement;
		} else if(item instanceof ReportParent) {
			List<ReportItem> items = ((ReportParent) item).getChildren();
			result = jasperDesignBuilderImpl.getFrameForItens(item, items, computeWidth, item.getColspan(), section, jrStyle);
//		} else if(item instanceof ReportBlock) {
//			List<ReportItem> items = ((ReportBlock) item).getItems();
//			result = jasperDesignBuilderImpl.getFrameForItens(item, items, computeWidth, item.getColspan(), section, jrStyle);
		} else if(item instanceof ReportImage) {
			ReportImage reportImage = (ReportImage)item;
			JRDesignImage image = new JRDesignImage(null);
			image.setWidth(computeWidth);
			image.setHeight(reportImage.getHeight());
			image.setEvaluationTime(EvaluationTimeEnum.AUTO);
			image.setOnErrorType(OnErrorTypeEnum.BLANK);
			if(reportImage.isRendered()){
				image.setExpression(jasperDesignBuilderImpl.createFieldOrParameterExpression("param.image"+jasperDesignBuilderImpl.definition.getImageIndex(reportImage), InputStream.class, false));
			} else {
				JRDesignExpression fieldExpression = jasperDesignBuilderImpl.createFieldOrParameterExpression(reportImage.getReference(), InputStream.class, false);
				image.setExpression(fieldExpression);
				image.setUsingCache(false);
			}
			result = image;
		} else if(item instanceof ReportChart) {
			ReportChart reportChart = (ReportChart) item;
			JRDesignImage image = new JRDesignImage(null);
			image.setWidth(computeWidth);
			if(reportChart.getHeight() <= 0){
				if(computeWidth > MAX_REPORT_CHART_AUTO_HEIGHT){
					image.setHeight(MAX_REPORT_CHART_AUTO_HEIGHT);
				} else {
					image.setHeight(computeWidth);
				}
			} else {
				image.setHeight(reportChart.getHeight());
			}
			image.setEvaluationTime(EvaluationTimeEnum.AUTO);
			image.setOnErrorType(OnErrorTypeEnum.ERROR);
			int chartHeight = image.getHeight();
			if(reportChart.isRendered()){
				String parameterReference = "$P{"+"chart"+jasperDesignBuilderImpl.definition.getChartIndex(reportChart)+"}";
				String chartRendererExpression = "new "+ChartDrawRenderer.class.getName()+"("+parameterReference+", "+computeWidth+", "+chartHeight+")";
				JRDesignExpression fieldExpression = new JRDesignExpression(chartRendererExpression);
				image.setExpression(fieldExpression);
			} else {
				String fieldReference = "$F{"+reportChart.getReference()+"}";
				String chartRendererExpression = "new "+ChartDrawRenderer.class.getName()+"("+fieldReference+", "+computeWidth+", "+chartHeight+")";
				JRDesignExpression fieldExpression = new JRDesignExpression(chartRendererExpression);
				jasperDesignBuilderImpl.createFieldOrParameterExpression(reportChart.getReference(), Chart.class, false);
				image.setExpression(fieldExpression);
				image.setUsingCache(false);
			}
			result = image;
		} else if(item instanceof Subreport){
			Subreport subreport = (Subreport) item;
			JRDesignSubreport jrSubreport = new JRDesignSubreport(jasperDesignBuilderImpl.template);
			jrSubreport.setWidth(computeWidth);
			jrSubreport.setHeight(10);
			if(subreport.getHeight() != ReportConstants.AUTO_HEIGHT){
				jrSubreport.setHeight(subreport.getHeight());
			}
			String subreportIndex = jasperDesignBuilderImpl.definition.getSubreportIndex(subreport);
			jrSubreport.setExpression(jasperDesignBuilderImpl.createFieldOrParameterExpression("param.subreport"+subreportIndex, JasperReport.class, false));
			jrSubreport.setParametersMapExpression(jasperDesignBuilderImpl.createExpression("param.subreport"+subreportIndex+"_params", Map.class, false));
			if(subreport.getExpression() == null){
				jrSubreport.setDataSourceExpression(jasperDesignBuilderImpl.createExpression("param.subreport"+subreportIndex+"_ds", JRDataSource.class, false));
			} else {
				jasperDesignBuilderImpl.createFieldOrParameterExpression(subreport.getExpression(), List.class, false);
				jrSubreport.setDataSourceExpression(jasperDesignBuilderImpl.createExpression("$P{subreport"+subreportIndex+"_ds_map}.get($F{"+subreport.getExpression()+"})", JRDataSource.class, false));
			}
			result = jrSubreport;
		} else {
			JRDesignStaticText jrDesignStaticText = new JRDesignStaticText();
			jrDesignStaticText.setWidth(computeWidth);//TODO PEGAR DO TEMPLATE
			jrDesignStaticText.setHeight(14);
			jrDesignStaticText.setText("["+item.getDescriptionName()+"]");
			jrDesignStaticText.setForecolor(Color.ORANGE);
			result = jrDesignStaticText;	
		}
		Map<String, Object> renderParameters = item.getRenderParameters();
		jasperDesignBuilderImpl.configureRenderParameters(result, renderParameters);
		
		if(renderParameters.containsKey(JasperRenderParameters.PRINT_WHEN_EXPRESSION)){
			String printExpression = (String) renderParameters.get(JasperRenderParameters.PRINT_WHEN_EXPRESSION);
			result.setPrintWhenExpression(new JRDesignExpression(printExpression));
			if (printExpression.contains("$F{")) {
				Matcher m = Pattern.compile("\\$F\\{(.*)\\}").matcher(printExpression);
				while (m.find()) {
					jasperDesignBuilderImpl.createFieldOrParameterExpression(m.group(1), null, false);
				}
			}
		}
		
		if(result instanceof JRBoxContainer){
			ReportBasicStyle style = item.getStyle();
			
			JRBoxContainer container = (JRBoxContainer) result;
			JRLineBox lineBox = container.getLineBox();
			
			lineBox.setBottomPadding(style.getPaddingBottom());
			lineBox.setTopPadding(style.getPaddingTop());
			lineBox.setLeftPadding(style.getPaddingLeft());
			lineBox.setRightPadding(style.getPaddingRight());
			
			if(lineBox instanceof JRBaseLineBox){
				JRBaseLineBox baseLineBox = (JRBaseLineBox) lineBox;
				if(style.getBorderBottom() != null){
					jasperDesignBuilderImpl.copyBorderToPen(style.getBorderBottom(), baseLineBox.getBottomPen());
				}
				if(style.getBorderLeft() != null){
					jasperDesignBuilderImpl.copyBorderToPen(style.getBorderLeft(), baseLineBox.getLeftPen());
				}
				if(style.getBorderRight() != null){
					jasperDesignBuilderImpl.copyBorderToPen(style.getBorderRight(), baseLineBox.getRightPen());
				}
				if(style.getBorderTop() != null){
					jasperDesignBuilderImpl.copyBorderToPen(style.getBorderTop(), baseLineBox.getTopPen());
				}
			}
		}
		result.setPositionType(PositionTypeEnum.FLOAT);
		
		jasperDesignBuilderImpl.setKeys(item, result);
		return result;
	}
	

	void configureTextElement(int width, ReportTextElement reportTextField, JRDesignTextElement jrDesignTextElement, ReportSection section, JRStyle jrStyle) {
		jrDesignTextElement.setHeight(14);
		Integer fontSize = reportTextField.getStyle().getFontSize();
		if(fontSize != null){
			jrDesignTextElement.setFontSize(fontSize);
		} else {
			fontSize = jrStyle != null? jrStyle.getFontSize() : null;
		}
		if(fontSize != null){
			jrDesignTextElement.setHeight((int)(Math.round(fontSize * 1.3 +0.1)));
		}
		 //TODO PEGAR DO TEMPLATE
		jrDesignTextElement.setWidth(width);
		jrDesignTextElement.setBold(reportTextField.getStyle().getBold());
		jrDesignTextElement.setItalic(reportTextField.getStyle().getItalic());
		jrDesignTextElement.setPdfFontName(jasperDesignBuilderImpl.getPdfFontName(reportTextField.getStyle().getBold(), reportTextField.getStyle().getItalic(), jrStyle));
		
		if(!reportTextField.isHeightAuto()){
			jrDesignTextElement.setHeight(reportTextField.getHeight());
		}
		
		HorizontalAlignEnum horizontalAlign = HorizontalAlignEnum.LEFT;
		ReportAlignment alignment = reportTextField.getStyle().getAlignment();
		if(alignment == null){
			alignment = ReportAlignment.LEFT;
		}
		switch (alignment) {
			case LEFT:
				horizontalAlign = HorizontalAlignEnum.LEFT;
				break;
			case RIGHT:
				horizontalAlign = HorizontalAlignEnum.RIGHT;
				break;
			case CENTER:
				horizontalAlign = HorizontalAlignEnum.CENTER;
				break;
			case JUSTIFIED:
				horizontalAlign = HorizontalAlignEnum.JUSTIFIED;
				break;
		}
		jrDesignTextElement.setHorizontalAlignment(horizontalAlign);
		
		if(reportTextField.getStyle().getForegroundColor() != null){
			jrDesignTextElement.setForecolor(reportTextField.getStyle().getForegroundColor());
		}
		if(reportTextField.getStyle().getBackgroundColor() != null){
			jrDesignTextElement.setBackcolor(reportTextField.getStyle().getBackgroundColor());
			jrDesignTextElement.setMode(ModeEnum.OPAQUE);
		}
	}
	
	private void configureFrameWidthForTextElement(JRDesignElement returnElement, int computeWidth) {
		if(returnElement instanceof JRDesignFrame){
			int originalWidth = returnElement.getWidth();
			returnElement.setWidth(computeWidth);
			List<JRChild> allChildrenFlat = jasperDesignBuilderImpl.getAllChildrenFlat(((JRDesignFrame) returnElement).getChildren());
			for (JRChild jrChild : allChildrenFlat) {
				JRDesignElement jrDesignElement = (JRDesignElement)jrChild;
				if(jrDesignElement.getWidth() == originalWidth){
					jrDesignElement.setWidth(computeWidth);
				}
				int elementEnd = jrDesignElement.getX() + jrDesignElement.getWidth();
				if(elementEnd > computeWidth){
					jrDesignElement.setX(jrDesignElement.getX() - (elementEnd - computeWidth));
				} else {
					if(elementEnd == originalWidth){
						jrDesignElement.setX(jrDesignElement.getX() + (computeWidth - originalWidth));
					}
				}
			}
		}
	}
	

	

}
