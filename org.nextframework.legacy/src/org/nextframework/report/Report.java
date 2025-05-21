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

/**
 * @author rogelgarcia
 * @since 22/01/2006
 * @version 1.1
 */
public class Report implements IReport {

	protected String reportFileName;
	protected String reportName;
	protected Object reportData;
	protected Map<String, Object> parameterMap = new HashMap<String, Object>();
	protected Map<String, IReport> subReports = new HashMap<String, IReport>();

	public Report(String reportName) {
		this(reportName, (Object) null);
	}

	public Report(String reportName, Iterator<?> dataSource) {
		this(reportName, (Object) dataSource);
	}

	public Report(String reportName, Collection<?> dataSource) {
		this(reportName, (Object) dataSource);
	}

	public Report(String reportName, Object[] dataSource) {
		this(reportName, (Object) dataSource);
	}

	public Report(String reportName, ResultSet dataSource) {
		this(reportName, (Object) dataSource);
	}

	public Report(String reportName, JRDataSource dataSource) {
		this(reportName, (Object) dataSource);
	}

	protected Report(String reportName, Object data) {
		this(reportName, null, data);
	}

	public Report(String reportName, Map<String, Object> parameterMap, Iterator<?> dataSource) {
		this(reportName, parameterMap, (Object) dataSource);
	}

	public Report(String reportName, Map<String, Object> parameterMap, Collection<?> dataSource) {
		this(reportName, parameterMap, (Object) dataSource);
	}

	public Report(String reportName, Map<String, Object> parameterMap, Object[] dataSource) {
		this(reportName, parameterMap, (Object) dataSource);
	}

	public Report(String reportName, Map<String, Object> parameterMap, ResultSet dataSource) {
		this(reportName, parameterMap, (Object) dataSource);
	}

	public Report(String reportName, Map<String, Object> parameterMap, JRDataSource dataSource) {
		this(reportName, parameterMap, (Object) dataSource);
	}

	protected Report(String reportName, Map<String, Object> parameterMap, Object dataSource) {
		this.reportData = dataSource;
		this.reportName = reportName;
		if (parameterMap != null) {
			this.parameterMap = parameterMap;
		}
	}

	public String getName() {
		return reportName;
	}

	public void setName(String name) {
		this.reportName = name;
	}

	public Map<String, Object> getParameters() {
		return parameterMap;
	}

	public void setParameters(Map<String, Object> parameterMap) {
		this.parameterMap = parameterMap;
	}

	public Object addParameter(String key, Object value) {
		return parameterMap.put(key, value);
	}

	public Object getDataSource() {
		return reportData;
	}

	public void setDataSource(Iterator<?> dataSource) {
		this.reportData = dataSource;
	}

	public void setDataSource(Collection<?> dataSource) {
		this.reportData = dataSource;
	}

	public void setDataSource(Object[] dataSource) {
		this.reportData = dataSource;
	}

	public void setDataSource(ResultSet dataSource) {
		this.reportData = dataSource;
	}

	public void setDataSource(JRDataSource dataSource) {
		this.reportData = dataSource;
	}

	public Map<String, IReport> getSubReportMap() {
		return subReports;
	}

	public void setSubReportMap(Map<String, IReport> subReportMap) {
		this.subReports = subReportMap;
	}

	public IReport addSubReport(String name, IReport report) {
		return subReports.put(name, report);
	}

	public String getFileName() {
		return reportFileName;
	}

	public void setFileName(String fileName) {
		reportFileName = fileName;
	}

}
