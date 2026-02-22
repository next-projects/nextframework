package org.nextframework.report.renderer.jasper;

import java.util.AbstractMap;
import java.util.List;
import java.util.Set;

import org.nextframework.report.definition.ReportDefinition;

import net.sf.jasperreports.engine.JRDataSource;

public class SubreportExpressionMap extends AbstractMap<List<?>, JRDataSource> {

	private ReportDefinition definition;

	public SubreportExpressionMap(ReportDefinition definition) {
		this.definition = definition;
	}

	@Override
	public JRDataSource get(Object key) {
		return JasperDataConverter.toMap((List<?>) key, definition);
	}

	@Override
	public Set<java.util.Map.Entry<List<?>, JRDataSource>> entrySet() {
		throw new RuntimeException("not implemented");
	}

}
