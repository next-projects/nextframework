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

import org.nextframework.chart.Chart;
import org.nextframework.chart.ChartData;
import org.nextframework.chart.ChartDataBuilder;
import org.nextframework.chart.ChartRow;
import org.nextframework.chart.ChartStyle.LegendPosition;
import org.nextframework.report.definition.ReportDefinition;
import org.nextframework.report.definition.ReportSection;
import org.nextframework.report.definition.ReportSectionType;
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
import org.nextframework.util.Util;

import net.sf.jasperreports.engine.type.VerticalAlignEnum;

public class DynamicReportLayoutBuilder extends RepositoryReportLayoutBuilder {

	public enum ReportCycle {
		BASE,
		BASE_CONFIG,
		CHARTS,
		RESUME,
		DETAIL
	}

	private ReportCycle cycle = ReportCycle.BASE;

	@Override
	public ReportDefinition getDefinition() {
		if (cycle == ReportCycle.BASE) {
			rendering = true;
			cycle = ReportCycle.BASE_CONFIG;
			definition = new DynamicBaseReportDefinition();
			configureDefinition();
			return definition;
		}
		return super.getDefinition();
	}

	@Override
	protected boolean isSetupGroups() {
		if (cycle == ReportCycle.BASE || cycle == ReportCycle.BASE_CONFIG || cycle == ReportCycle.CHARTS) {
			return false;
		}
		return super.isSetupGroups();
	}

	@Override
	protected boolean isSetupColumnWidths() {
		if (cycle == ReportCycle.BASE || cycle == ReportCycle.BASE_CONFIG) {
			return false;
		}
		return super.isSetupColumnWidths();
	}

	@SuppressWarnings("all")
	@Override
	protected void layoutReport() {

		getDefinition().getParameters().put("renderPDF", Boolean.FALSE);
		getDefinition().getParameters().put(ReportCycle.class.getSimpleName(), cycle);

		switch (cycle) {

			case BASE_CONFIG:

				getDefinition().setReportName(getReportName() + "Base");
				getDefinition().setData(Arrays.asList(new Object()));
				((DynamicBaseReportDefinition) getDefinition()).setSummarizedData((SummaryResult<?, ? extends Summary<?>>) summaryResult);
				getDefinition().setTitle(getTitle());

				Subreport subCharts = new Subreport(createSubReportDefinition(ReportCycle.CHARTS));
				if (Util.collections.isNotEmpty(subCharts.getReport().getChildren())) {
					getDefinition().addItem(subCharts, getDefinition().getSectionDetail(), 0);
					getDefinition().getSectionDetail().breakLine();
				}

				Subreport subResume = new Subreport(createSubReportDefinition(ReportCycle.RESUME));
				if (Util.collections.isNotEmpty(subResume.getReport().getChildren())) {
					subResume.setRenderParameter(JasperRenderParameters.PRINT_WHEN_EXPRESSION, "$P{renderPDF}");
					getDefinition().addItem(subResume, getDefinition().getSectionDetail(), 0);
					getDefinition().getSectionDetail().breakLine();
				}

				Subreport subDetails = new Subreport(createSubReportDefinition(ReportCycle.DETAIL));
				if (Util.collections.isNotEmpty(subDetails.getReport().getChildren())) {
					getDefinition().addItem(subDetails, getDefinition().getSectionDetail(), 0);
				}

				getDefinition().getParameters().put("no-detail-separator", true);
				getDefinition().getParameters().put(HtmlReportBuilder.ENALBE_SUBREPORT, getReportName() + "Detail");

				break;

			case CHARTS:

				getDefinition().setReportName(getDefinition().getReportName() + "Charts");
				getDefinition().setData(Arrays.asList(new Object()));
				getDefinition().getSectionDetailHeader().setRender(false);
				createCharts();
				break;

			case RESUME:

				getDefinition().setReportName(getDefinition().getReportName() + "Resume");
				getDefinition().getSectionDetailHeader().setRender(false);
				getDefinition().getSectionDetail().setRender(false);
				for (int i = 1; i < getDefinition().getGroups().size(); i++) {
					getDefinition().getGroups().get(i).getSectionHeader().setRender(false);
					getDefinition().getGroups().get(i).getSectionDetail().setRender(false);
					getDefinition().getGroups().get(i).getSectionFooter().setRender(false);
				}
				layoutReportFields();
				separator("", getColumnQuantity(), getDefinition().getSectionLastPageFooter());
				break;

			case DETAIL:

				getDefinition().setReportName(getDefinition().getReportName() + "Detail");
				layoutReportFields();
				break;

		}

	}

	protected String getTitle() {
		return "-";
	}

	@SuppressWarnings("all")
	private ReportDefinition createSubReportDefinition(ReportCycle cycle) {
		DynamicReportLayoutBuilder r = newInstance();
		r.setLocale(locale);
		r.setData((SummaryResult) this.summaryResult);
		r.cycle = cycle;
		return r.getDefinition();
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

	protected void createCharts() {

	}

	protected void layoutReportFields() {

	}

	protected SubreportTable getChartTable(final Chart chart0) {
		final ReportDefinition definitionSuper = getDefinition();
		return new SubreportTable(new TableInformationAdaptor() {

			DecimalFormat df = new DecimalFormat("#,##0.00");

			@Override
			public void configureDefinition(ReportDefinition definition) {
				definition.getParameters().putAll(definitionSuper.getParameters());
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

			@Override
			public Collection<?> getColumnHeaderDataSet() {
				ChartData data2 = chart0.getData();
				return Arrays.asList(data2.getSeries());
			}

			@Override
			public ReportItem getComponentFor(Object header, int i) {
				ReportTextField component = (ReportTextField) super.getComponentFor(header, i);
				component.getStyle().setFontSize(7);
				component.getStyle().setAlignment(ReportAlignment.RIGHT);
				return component;
			}

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
				if (formatter != null) {
					formatter.setValue(row.getGroup());
					value = formatter.getAsText();
				}
				return value;
			}

			@Override
			public Object getValue(Object _row, Object header, int columnIndex) {
				ChartRow row = (ChartRow) _row;
				Number value = row.getValues()[columnIndex - 1];
				if (value == null) {
					return "";
				}
				return df.format(value);
			}

		});
	}

	public void onNewChart(Chart chart) {
		ChartData chartData = chart.getData();
		if (chartData.getData().size() >= 1) {
			ChartRow row = chartData.getData().get(0);
			if (row.getValues().length == 1) { //only one serie
				if (chartData.getSeries()[0].equals(ChartDataBuilder.UNIQUE_SERIES)) {
					chart.getStyle().setLegendPosition(LegendPosition.NONE);
					chartData.getSeries()[0] = "Qtd";
				}
				reorderGroupsByValue(chartData.getData()); //reorder when only one serie
			}
		}
	}

	private void reorderGroupsByValue(List<ChartRow> data) {

		if (data.size() == 0) {
			return;
		}

		if (!(data.get(0).getGroup() instanceof Date) && !(data.get(0).getGroup() instanceof Calendar) && !(data.get(0).getGroup() instanceof Number)) {

			Collections.sort(data, new Comparator<ChartRow>() {

				public int compare(ChartRow o1, ChartRow o2) {
					Object g1 = o1.getGroup();
					Object g2 = o2.getGroup();
					if ((g1 != null && g2 != null && g1.equals(g2)) || (g1 == null && g2 == null)) {
						Number n1 = o1.getValues()[0];
						Number n2 = o2.getValues()[0];
						if (n1 == null && n2 == null) {
							return 0;
						}
						if (n1 == null) {
							return -1;
						}
						if (n2 == null) {
							return 1;
						}
						return (int) (n2.doubleValue() - n1.doubleValue());
					}
					if (g1 == null) {
						return -1;
					}
					if (g2 == null) {
						return 1;
					}
					return g1.toString().compareTo(g2.toString());
				}

			});

		}

	}

	@Override
	protected ReportItem addItemToDefinition(int column, ReportSection section, ReportItem reportItem) {
		if (section.getType() == ReportSectionType.DETAIL_HEADER || section.getType() == ReportSectionType.SUMARY_DATA_HEADER) {
			((ReportLabel) reportItem).setHeight(22);
			((ReportLabel) reportItem).setRenderParameter("jasper-vertical-alignment", VerticalAlignEnum.TOP);
		}
		return super.addItemToDefinition(column, section, reportItem);
	}

}