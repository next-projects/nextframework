package org.nextframework.report.generator.chart;

import java.util.ArrayList;
import java.util.List;

public class ChartElement {

	public static final String SHOW_ALL = "showall";
	public static final String LIMIT = "limit";
	public static final String GROUP = "group";

	String type;
	String groupProperty;
	String groupLevel;
	String seriesProperty;
	String valueProperty;
	String valueAggregate;
	String title;
	String groupTitle;
	String seriesTitle;
	String propertiesAsSeries;
	String seriesLimitType;
	boolean ignoreEmptySeriesAndGroups;

	List<ChartSerieElement> series = new ArrayList<ChartSerieElement>();

	public List<ChartSerieElement> getSeries() {
		return series;
	}

	public boolean isIgnoreEmptySeriesAndGroups() {
		return ignoreEmptySeriesAndGroups;
	}

	public void setIgnoreEmptySeriesAndGroups(boolean ignoreEmptySeriesAndGroups) {
		this.ignoreEmptySeriesAndGroups = ignoreEmptySeriesAndGroups;
	}

	public String getSeriesLimitType() {
		return seriesLimitType;
	}

	public void setSeriesLimitType(String seriesLimitType) {
		this.seriesLimitType = seriesLimitType;
	}

	public String getSeriesProperty() {
		return seriesProperty;
	}

	public String getPropertiesAsSeries() {
		return propertiesAsSeries;
	}

	public void setSeriesProperty(String seriesProperty) {
		this.seriesProperty = seriesProperty;
	}

	public void setPropertiesAsSeries(String propertiesAsSeries) {
		this.propertiesAsSeries = propertiesAsSeries;
	}

	public String getType() {
		return type;
	}

	public String getGroupProperty() {
		return groupProperty;
	}

	public String getValueProperty() {
		return valueProperty;
	}

	public String getTitle() {
		return title;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setGroupProperty(String groupProperty) {
		this.groupProperty = groupProperty;
	}

	public void setValueProperty(String valueProperty) {
		this.valueProperty = valueProperty;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getGroupLevel() {
		return groupLevel;
	}

	public void setGroupLevel(String groupLevel) {
		this.groupLevel = groupLevel;
	}

	public String getValueAggregate() {
		return valueAggregate;
	}

	public void setValueAggregate(String valueAggregate) {
		this.valueAggregate = valueAggregate;
	}

	public String getGroupTitle() {
		return groupTitle;
	}

	public String getSeriesTitle() {
		return seriesTitle;
	}

	public void setGroupTitle(String groupTitle) {
		this.groupTitle = groupTitle;
	}

	public void setSeriesTitle(String seriesTitle) {
		this.seriesTitle = seriesTitle;
	}

	@Override
	public String toString() {
		String format = String
				.format(
						"%n\t\t%s [groupProperty=%s, seriesProperty=%s, valueProperty=%s, valueAggregate=%s, title=%s]",
						type.toUpperCase(), groupProperty + (groupLevel != null ? "(" + groupLevel + ")" : ""), seriesProperty, valueProperty, valueAggregate, title);
		if (series == null || series.size() == 0) {
			return format;
		} else {
			return format + "\n\t\t\tSeries=" + series;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((groupLevel == null) ? 0 : groupLevel.hashCode());
		result = prime * result + ((groupProperty == null) ? 0 : groupProperty.hashCode());
		result = prime * result + ((groupTitle == null) ? 0 : groupTitle.hashCode());
		result = prime * result + (ignoreEmptySeriesAndGroups ? 1231 : 1237);
		result = prime * result + ((propertiesAsSeries == null) ? 0 : propertiesAsSeries.hashCode());
		result = prime * result + ((series == null) ? 0 : series.hashCode());
		result = prime * result + ((seriesLimitType == null) ? 0 : seriesLimitType.hashCode());
		result = prime * result + ((seriesProperty == null) ? 0 : seriesProperty.hashCode());
		result = prime * result + ((seriesTitle == null) ? 0 : seriesTitle.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((valueAggregate == null) ? 0 : valueAggregate.hashCode());
		result = prime * result + ((valueProperty == null) ? 0 : valueProperty.hashCode());
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
		ChartElement other = (ChartElement) obj;
		if (groupLevel == null) {
			if (other.groupLevel != null)
				return false;
		} else if (!groupLevel.equals(other.groupLevel))
			return false;
		if (groupProperty == null) {
			if (other.groupProperty != null)
				return false;
		} else if (!groupProperty.equals(other.groupProperty))
			return false;
		if (groupTitle == null) {
			if (other.groupTitle != null)
				return false;
		} else if (!groupTitle.equals(other.groupTitle))
			return false;
		if (ignoreEmptySeriesAndGroups != other.ignoreEmptySeriesAndGroups)
			return false;
		if (propertiesAsSeries == null) {
			if (other.propertiesAsSeries != null)
				return false;
		} else if (!propertiesAsSeries.equals(other.propertiesAsSeries))
			return false;
		if (series == null) {
			if (other.series != null)
				return false;
		} else if (!series.equals(other.series))
			return false;
		if (seriesLimitType == null) {
			if (other.seriesLimitType != null)
				return false;
		} else if (!seriesLimitType.equals(other.seriesLimitType))
			return false;
		if (seriesProperty == null) {
			if (other.seriesProperty != null)
				return false;
		} else if (!seriesProperty.equals(other.seriesProperty))
			return false;
		if (seriesTitle == null) {
			if (other.seriesTitle != null)
				return false;
		} else if (!seriesTitle.equals(other.seriesTitle))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (valueAggregate == null) {
			if (other.valueAggregate != null)
				return false;
		} else if (!valueAggregate.equals(other.valueAggregate))
			return false;
		if (valueProperty == null) {
			if (other.valueProperty != null)
				return false;
		} else if (!valueProperty.equals(other.valueProperty))
			return false;
		return true;
	}

}
