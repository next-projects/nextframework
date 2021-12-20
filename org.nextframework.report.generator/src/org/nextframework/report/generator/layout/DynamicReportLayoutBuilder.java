package org.nextframework.report.generator.layout;

import java.beans.PropertyEditor;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import net.sf.jasperreports.engine.type.VerticalAlignEnum;

import org.nextframework.chart.Chart;
import org.nextframework.chart.ChartData;
import org.nextframework.chart.ChartDataBuilder;
import org.nextframework.chart.ChartRow;
import org.nextframework.chart.ChartStyle.LegendPosition;
import org.nextframework.report.definition.ReportDefinition;
import org.nextframework.report.definition.ReportSection;
import org.nextframework.report.definition.ReportSectionType;
import org.nextframework.report.definition.builder.LayoutReportBuilder;
import org.nextframework.report.definition.elements.ReportItem;
import org.nextframework.report.definition.elements.ReportLabel;
import org.nextframework.report.definition.elements.ReportTextField;
import org.nextframework.report.definition.elements.Subreport;
import org.nextframework.report.definition.elements.SubreportTable;
import org.nextframework.report.definition.elements.TableInformationAdaptor;
import org.nextframework.report.definition.elements.style.ReportAlignment;
import org.nextframework.report.renderer.html.builder.HtmlReportBuilder;
import org.nextframework.report.renderer.jasper.builder.JasperRenderParameters;
import org.nextframework.summary.Summary;
import org.nextframework.summary.compilation.SummaryResult;

public class DynamicReportLayoutBuilder extends LayoutReportBuilder {
	
	enum ReportCycle {
		BASE,
		BASE_CONFIG,
		CHARTS,
		RESUME,
		DETAIL
	}
	
	ReportCycle cycle = ReportCycle.BASE;
	
	@Override
	public ReportDefinition getDefinition() {
		switch (cycle) {
			case CHARTS:
				setupGroups = false;
				break;
		}
		if(cycle == ReportCycle.BASE){
			cycle = ReportCycle.BASE_CONFIG;
			definition = new DynamicBaseReportDefinition();
			((DynamicBaseReportDefinition)definition).setSummarizedData((SummaryResult<?, ? extends Summary<?>>) summaryResult);
			definition.setReportName(getReportName()+"BASE");
			definition.setData(Arrays.asList(new Object()));
			setupColumnWidths = false;
			setupGroups = false;
			configureDefinition();
			definition.setTitle(getTitle());
			definition.setData(Arrays.asList(new Object()));
			
						
			definition.addItem(new Subreport(
					createSubReportPart(ReportCycle.CHARTS).getDefinition()), 
					definition.getSectionDetail(), 0);
			definition.getSectionDetail().breakLine();
			
			Subreport subResume = new Subreport(
							createSubReportPart(ReportCycle.RESUME).getDefinition());
			subResume.getRenderParameters().put(JasperRenderParameters.PRINT_WHEN_EXPRESSION, "$P{renderPDF}");
			definition.addItem(subResume, 
					definition.getSectionDetail(), 0);
			definition.getSectionDetail().breakLine();
			
			definition.addItem(new Subreport(
					createSubReportPart(ReportCycle.DETAIL).getDefinition()), 
					definition.getSectionDetail(), 0);

			definition.getParameters().put("no-detail-separator", true);
			definition.getParameters().put(HtmlReportBuilder.ENALBE_SUBREPORT, getReportName() + "Detail");
			
			return definition;
		} else {
			return super.getDefinition();
		}
	}
	
	@Override
	public int getChartDefaultHeight() {
		return 200;
	}
	
	public void onNewChart(Chart chart){
		ChartData chartData = chart.getData();
		if(chartData.getData().size() >= 1){
			ChartRow row = chartData.getData().get(0);
			if(row.getValues().length == 1){ //only one serie
				if(chartData.getSeries()[0].equals(ChartDataBuilder.UNIQUE_SERIES)){
					chart.getStyle().setLegendPosition(LegendPosition.NONE);
					chartData.getSeries()[0] = "Qtd";
				}
				reorderGroupsByValue(chartData.getData()); //reorder when only one serie
			}
		}
	}

	private void reorderGroupsByValue(List<ChartRow> data) {
		if(data.size() == 0){
			return;
		}
		if(!(data.get(0).getGroup() instanceof Date) && !(data.get(0).getGroup() instanceof Calendar)
				&& !(data.get(0).getGroup() instanceof Number)){
			Collections.sort(data, new Comparator<ChartRow>() {
				public int compare(ChartRow o1, ChartRow o2) {
					Object g1 = o1.getGroup();
					Object g2 = o2.getGroup();
					if((g1 != null && g2 != null && g1.equals(g2)) || (g1 == null && g2 == null)){
						Number n1 = o1.getValues()[0];
						Number n2 = o2.getValues()[0];
						if(n1 == null && n2 == null){
							return 0;
						}
						if(n1 == null){
							return -1;
						}
						if(n2 == null){
							return 1;
						}
						return (int) (n2.doubleValue() - n1.doubleValue());
					}
					if(g1 == null){
						return -1;
					}
					if(g2 == null){
						return 1;
					}
					return g1.toString().compareTo(g2.toString());
					/*
					Number n1 = o1.getValues()[0];
					Number n2 = o2.getValues()[0];
					if((n1 != null && n2 != null && n1.equals(n2)) || (n1 == null && n2 == null)){
						Object g1 = o1.getGroup();
						Object g2 = o2.getGroup();
						if(g1 == null && g2 == null){
							return 0;
						}
						if(g1 == null){
							return -1;
						}
						if(g2 == null){
							return 1;
						}
						return g1.toString().compareTo(g2.toString());
					}
					if(n1 == null){
						return -1;
					} 
					if(n2 == null){
						return 1;
					}
					return (int) (n2.doubleValue() - n1.doubleValue());
					*/
				}
			});
		}
	}

	public DynamicReportLayoutBuilder createSubReportPart(ReportCycle cycle) {
		DynamicReportLayoutBuilder r = newInstance();
		r.setData((SummaryResult) this.summaryResult);
		r.setLocale(locale);
		r.cycle = cycle;
		return r;
	}
	
	protected String getTitle() {
		return "-";
	}

	@SuppressWarnings("all")
	@Override
	protected void layoutReport() {
		getDefinition().getParameters().put("renderPDF", Boolean.FALSE);
		switch (cycle) {
			case RESUME:
				getDefinition().setReportName(getDefinition().getReportName() + "Resume");
				getDefinition().getSectionFirstPageHeader().breakLine();
				getDefinition().getSectionDetailHeader().setRender(false);
				getDefinition().getSectionDetail().setRender(false);
				for (int i = 1; i < getDefinition().getGroups().size(); i++) {
					getDefinition().getGroups().get(i).getSectionHeader().setRender(false);
					getDefinition().getGroups().get(i).getSectionDetail().setRender(false);
					getDefinition().getGroups().get(i).getSectionFooter().setRender(false);
				}
				layoutReportFields();
				break;
			case CHARTS:
				getDefinition().setReportName(getDefinition().getReportName() + "Charts");
				getDefinition().getSectionDetailHeader().setRender(false);
				getDefinition().setData(Arrays.asList(new Object()));
				createCharts();
				break;
			case DETAIL:
				getDefinition().setReportName(getDefinition().getReportName() + "Detail");
				layoutReportFields();
				break;
		}
	}

	public DynamicReportLayoutBuilder newInstance() {
		try {
			return this.getClass().newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	protected ReportItem addItemToDefinition(int column, ReportSection section, ReportItem reportItem) {
		if(section.getType() == ReportSectionType.DETAIL_HEADER
				|| section.getType() == ReportSectionType.SUMARY_DATA_HEADER){
			((ReportLabel)reportItem).setHeight(22);
			((ReportLabel)reportItem).setRenderParameter("jasper-vertical-alignment", VerticalAlignEnum.TOP);
		}
		return super.addItemToDefinition(column, section, reportItem);
	}

	protected void createCharts() {

	}

	protected void layoutReportFields() {
	
	}
	
	protected SubreportTable getChartTable(final Chart chart0) {
		return new SubreportTable(new TableInformationAdaptor() {
			
        	DecimalFormat df = new DecimalFormat("#,##0.00");
        	
			@Override
			public Collection<?> getRowGroupDataSet() {
				List<ChartRow> data = chart0.getData().getData();
				return data;
			}
			
			@Override
			public String formatRowGroup(Object _row) {
				ChartRow row = (ChartRow) _row;
				PropertyEditor formatter = chart0.getStyle().getGroupFormatter();
				String value = null;
				if(formatter != null){
					formatter.setValue(row.getGroup());
					value = formatter.getAsText();
				}
				return value;
			}
			

			@Override
			public Collection<?> getColumnHeaderDataSet() {
				ChartData data2 = chart0.getData();
				return Arrays.asList(data2.getSeries());
			}
			
			@Override
			public Object getValue(Object _row, Object header, int columnIndex) {
				ChartRow row = (ChartRow) _row;
				Number value = row.getValues()[columnIndex-1];
				if(value == null){
					return "";
				}
				return df.format(value);
			}
			
			@Override
			public ReportItem getComponentFor(Object header, int i) {
				ReportTextField component = (ReportTextField) super.getComponentFor(header, i);
				component.getStyle().setFontSize(7);
				component.getStyle().setAlignment(ReportAlignment.RIGHT);
				return component;
			}
			
			@Override
			public void configureGroupField(ReportTextField groupField) {
				super.configureGroupField(groupField);
				groupField.getStyle().setFontSize(7);
			}
			
			@Override
			public void configureHeaderField(ReportLabel headerField, int columnIndex) {
				super.configureHeaderField(headerField, columnIndex);
				headerField.getStyle().setFontSize(7);
			}
		});
	}	
}
