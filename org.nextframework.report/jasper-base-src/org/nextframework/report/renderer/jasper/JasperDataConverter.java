package org.nextframework.report.renderer.jasper;

import java.util.ArrayList;
import java.util.Formattable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.bean.PropertyDescriptor;
import org.nextframework.report.definition.ReportDefinition;
import org.nextframework.report.definition.ReportGroup;
import org.nextframework.report.definition.ReportParent;
import org.nextframework.report.definition.elements.ReportChart;
import org.nextframework.report.definition.elements.ReportImage;
import org.nextframework.report.definition.elements.ReportItem;
import org.nextframework.report.definition.elements.ReportItemIterator;
import org.nextframework.report.definition.elements.ReportTextField;
import org.nextframework.report.definition.elements.Subreport;
import org.nextframework.report.definition.elements.style.ReportDefinitionStyle;
import org.nextframework.report.renderer.jasper.builder.MappedJasperReport;
import org.nextframework.summary.SummaryRow;
import org.springframework.util.StringUtils;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

public class JasperDataConverter {

	public JasperDataParametersResult getParametersMap(ReportDefinition report) {
		JasperDataParametersResult result = new JasperDataParametersResult();
		Map<String, Object> parameters = new LinkedHashMap<String, Object>();
		ReportItemIterator iterator = new ReportItemIterator(report);
		while (iterator.hasNext()) {
			ReportItem reportItem = iterator.next();
			if (reportItem instanceof ReportChart) {
				if (((ReportChart) reportItem).isRendered()) {
					parameters.put("chart" + report.getChartIndex((ReportChart) reportItem), ((ReportChart) reportItem).getChart());
				}
			} else if (reportItem instanceof ReportImage) {
				if (((ReportImage) reportItem).isRendered()) {
					parameters.put("image" + report.getImageIndex((ReportImage) reportItem), ((ReportImage) reportItem).getInputStream());
				}
			} else if (reportItem instanceof Subreport) {
				Subreport subreport = (Subreport) reportItem;
				ReportDefinition subreportDefinition = subreport.getReport();
				ReportDefinitionStyle styleClone = subreportDefinition.getStyle().clone();
				styleClone.setNoMargin(true);
				styleClone.setNoTitle(true);

				boolean renderPageFooter = subreportDefinition.getSectionPageFooter().isRender();
				boolean renderTitle = subreportDefinition.getSectionTitle().isRender();

				//TODO REFACTOR THIS
				//subreportDefinition.getSectionPageFooter().setRender(false);
				subreportDefinition.getSectionTitle().setRender(false);

				subreportDefinition.setStyle(styleClone);
				//TODO SET DIFERENT REPORT NAMES
				MappedJasperReport mappedJasperReport = JasperReportsRenderer.renderAsMappedJasperReport(subreportDefinition);
				result.subreports.add(mappedJasperReport);

				subreportDefinition.getSectionPageFooter().setRender(renderPageFooter);
				subreportDefinition.getSectionTitle().setRender(renderTitle);

				parameters.put("subreport" + report.getSubreportIndex(subreport), mappedJasperReport.getJasperReport());
				JasperDataParametersResult subReportParametersResult = getParametersMap(subreportDefinition);
				result.subreports.addAll(subReportParametersResult.getSubreports());

				Map<String, Object> subreportParametersMap = subReportParametersResult.getParameters();
				subreportParametersMap.put("isSubreport", true);
				parameters.put("subreport" + report.getSubreportIndex(subreport) + "_params", subreportParametersMap);
				if (subreport.getExpression() == null) {
					parameters.put("subreport" + report.getSubreportIndex(subreport) + "_ds", getDataSource(subreportDefinition));
				} else {
					parameters.put("subreport" + report.getSubreportIndex(subreport) + "_ds_map", new SubreportExpressionMap(subreportDefinition));
				}
			}
		}
		Set<String> reportParameters = report.getParameters().keySet();
		for (String param : reportParameters) {
			Object object = report.getParameters().get(param);
			if (object != null) {
				parameters.put(param, object);
			} else {
				parameters.put(param, null);
			}
		}
		result.setParameters(parameters);
		return result;
	}

	public JRDataSource getDataSource(ReportDefinition definition) {
		List<?> data = definition.getData();
		if (data == null) {
			throw new NullPointerException("reportData is null");
		}
		return toMap(data, definition);
	}

	@SuppressWarnings("unchecked")
	public static JRMapCollectionDataSource toMap(List<?> rows, ReportDefinition report) {
		List<Map<String, ?>> lista = new ArrayList<Map<String, ?>>();
		for (Object registro : rows) {
			BeanDescriptor bd = BeanDescriptorFactory.forBean(registro);
			Map mapa = new HashMap();
			List<ReportItem> reportItens = report.getReportItens();
			if (registro instanceof Map) {
				mapa.putAll((Map) registro);
			} else {
				for (ReportItem reportItem : reportItens) {
					try {
						addItemToMap(mapa, reportItem, bd);
					} catch (Exception e) {
						Object bean = registro;
						if (bean instanceof SummaryRow) {
							bean = ((SummaryRow) bean).getRow();
						}
						throw new RuntimeException("Cannot get report item " + reportItem + " of " + bean.toString() + " of class " + bean.getClass(), e);
					}
				}
			}
			for (ReportGroup group : report.getGroups()) {
				Object groupValue = bd.getPropertyDescriptor(group.getExpression()).getValue();
				mapa.put(group.getExpression(), groupValue);
			}
//			List<ReportColumn> columns = report.getColumns();
//			for (ReportColumn reportColumn : columns) {
//				mapa.put(reportColumn.getExpression(), bd.getPropertyDescriptor(reportColumn.getExpression()).getValue().toString());
//			}
			lista.add(mapa);
		}
		JRMapCollectionDataSource ds = new JRMapCollectionDataSource(lista);
		return ds;
	}

	@SuppressWarnings("unchecked")
	private static void addItemToMap(Map mapa, ReportItem reportItem, BeanDescriptor bd) {
		if (reportItem instanceof ReportTextField) {
			ReportTextField reportTextField = (ReportTextField) reportItem;
			if (reportTextField.isFieldReference()) {
				String expression = reportTextField.getExpression();
				PropertyDescriptor propertyDescriptor = bd.getPropertyDescriptor(expression);
				Object value = propertyDescriptor.getValue();
				if (StringUtils.hasLength(reportTextField.getPattern())) {
					if (propertyDescriptor.getType().equals(boolean.class) || propertyDescriptor.getType().equals(Boolean.class)) {
						String[] values = reportTextField.getPattern().split("/");
						if (values.length == 2) {
							values = new String[] { values[0], values[1], values[1] };
						}
						if (values.length == 3) {
							if (Boolean.TRUE.equals(value)) {
								value = values[0];
							} else if (Boolean.FALSE.equals(value)) {
								value = values[1];
							} else {
								value = values[2];
							}
						}
					}
				} else {
					if (value instanceof Formattable) {
						value = String.format("%s", value);
					}
				}
				mapa.put(expression, value);
			}
		} else if (reportItem instanceof Subreport) {
			String expression = ((Subreport) reportItem).getExpression();
			if (expression != null) {
				//TODO CHECK TYPE.. MUST BE LIST
				mapa.put(expression, bd.getPropertyDescriptor(expression).getValue());
			}
		} else if (reportItem instanceof ReportImage) {
			ReportImage reportImage = (ReportImage) reportItem;
			if (!reportImage.isRendered() && reportImage.isFieldReference()) {
				Object value = bd.getPropertyDescriptor(reportImage.getReference()).getValue();
				//System.out.println(isReportImageOK((InputStream) value));
				mapa.put(reportImage.getReference(), value);
			}
		} else if (reportItem instanceof ReportChart) {
			ReportChart reportChart = (ReportChart) reportItem;
			if (!reportChart.isRendered() && reportChart.isFieldReference()) {
				Object value = bd.getPropertyDescriptor(reportChart.getReference()).getValue();
				//System.out.println(isReportImageOK((InputStream) value));
				mapa.put(reportChart.getReference(), value);
			}
		} else if (reportItem instanceof ReportParent) {
			List<ReportItem> itens = ((ReportParent) reportItem).getChildren();
			for (ReportItem reportItem2 : itens) {
				addItemToMap(mapa, reportItem2, bd);
			}
		}
	}

}
