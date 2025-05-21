/*
 * Next Framework http://www.nextframework.org
 * Copyright (C) 2009 the original author or authors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * You may obtain a copy of the license at
 * 
 *     http://www.gnu.org/copyleft/lesser.html
 * 
 */
package org.nextframework.report;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

/**
 * @author rogelgarcia
 * @since 22/01/2006
 * @version 1.1
 */
public class ReportGeneratorImpl implements ReportGenerator {

	protected ReportTranslator reportTranslator;

	public ReportGeneratorImpl(ReportTranslator translator) {
		if (translator == null) {
			throw new NullPointerException();
		}
		reportTranslator = translator;
	}

	public byte[] toPdf(IReport report) throws ReportException {

		JasperReport jasperReport;
		Map<String, JasperReport> subReportParameters;
		try {
			jasperReport = reportTranslator.translate(report);
			subReportParameters = subReportParameters(report, reportTranslator);
		} catch (JRException e1) {
			throw new ReportException(e1);
		}

		Map<String, Object> allParameters = new HashMap<String, Object>();
		allParameters.putAll(subReportParameters);
		reportParameters(report, allParameters);

		Object ds = report.getDataSource();
		JRDataSource dataSource = toDataSource(ds);

		try {
			try {
				return JasperRunManager.runReportToPdf(jasperReport, allParameters, dataSource);
			} catch (JRException e) {
				throw new ReportException(e);
			}
		} finally {
			if (report.getDataSource() instanceof ResultSet) {
				try {
					ResultSet rs = (ResultSet) report.getDataSource();
					rs.getStatement().getConnection().close();
				} catch (Exception e) {
					System.out.println("Atenção! Não foi possível fechar conexão após gerar relatório!");
				}
			}
		}

	}

	protected Map<String, JasperReport> subReportParameters(IReport jasperReport, ReportTranslator reportTranslator) throws JRException {
		Map<String, IReport> subReportMap = jasperReport.getSubReportMap();
		Map<String, JasperReport> result = new HashMap<String, JasperReport>();
		for (String name : subReportMap.keySet()) {
			IReport ireport = subReportMap.get(name);
			JasperReport jreport = reportTranslator.translate(ireport);
			result.putAll(subReportParameters(ireport, reportTranslator));
			result.put(name, jreport);
		}
		return result;
	}

	private void reportParameters(IReport report, Map<String, Object> allParameters) {
		allParameters.putAll(report.getParameters());
		Map<String, IReport> subReportMap = report.getSubReportMap();
		for (String name : subReportMap.keySet()) {
			IReport ireport = subReportMap.get(name);
			reportParameters(ireport, allParameters);
		}

	}

	@SuppressWarnings("unchecked")
	private JRDataSource toDataSource(Object ds) {
		JRDataSource dataSource = null;
		if (ds instanceof JRDataSource) {
			dataSource = (JRDataSource) ds;
		} else if (ds instanceof ResultSet) {
			dataSource = new JRResultSetDataSource((ResultSet) ds);
		} else if (ds instanceof Object[]) {
			Object[] array = (Object[]) ds;
			if (array.length > 0 && array[0] instanceof Map) {
				dataSource = new JRMapArrayDataSource(array);
			} else {
				dataSource = new JRBeanArrayDataSource(array);
			}
		} else if (ds instanceof Collection<?>) {
			Iterator<?> iterator = ((Collection<?>) ds).iterator();
			Object primeiroElemento = null;
			if (iterator.hasNext()) {
				primeiroElemento = iterator.next();
			}
			if (primeiroElemento instanceof Map) {
				dataSource = new JRMapCollectionDataSource((Collection<Map<String, ?>>) ds);
			} else {
				dataSource = new JRBeanCollectionDataSource((Collection<?>) ds);
			}
		} else if (ds instanceof Iterator<?>) {
			Iterator<?> iterator = (Iterator<?>) ds;
			dataSource = new JRIteratorDataSource(iterator);
		} else if (ds == null) {
			dataSource = new JREmptyDataSource();
		} else {
			throw new ReportException("O tipo de datasource é inválido! ");
		}
		return dataSource;
	}

}
