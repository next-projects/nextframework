package org.nextframework.report.generator.datasource;

import java.util.List;
import java.util.Map;

import org.nextframework.report.generator.ReportElement;

public class StaticDataProvider<TYPE> implements DataSourceProvider<TYPE> {

	Class<TYPE> type;
	String method;
	
	public StaticDataProvider(Class<TYPE> type, String method) {
		this.type = type;
		this.method = method;
	}

	public Class<TYPE> getType() {
		return type;
	}
	
	public String getMethod() {
		return method;
	}

	@Override
	public Class<TYPE> getMainType() {
		return type;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TYPE> getResult(ReportElement element, Map<String, Object> filterMap, int limitResults) {
		try {
			return (List<TYPE>) type.getMethod(method).invoke(null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StaticDataProvider<?> other = (StaticDataProvider<?>) obj;
		if (method == null) {
			if (other.method != null)
				return false;
		} else if (!method.equals(other.method))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	
	
}
