package org.nextframework.report.generator.data;

public class FilterElement {

	String name;
	String displayName;
	boolean selectMultiple;
	ReportFilterDateAutoFilter preSelectDate;
	String preSelectEntity;
	String fixedCriteria;
	boolean required;

	public FilterElement(String property, String displayName, String selectMultiple, String dateAutoFilter, String preSelectEntity, String fixedCriteria, String required) {
		this.name = property;
		this.displayName = displayName;
		this.selectMultiple = "true".equals(selectMultiple);
		this.required = "true".equals(required);
		this.preSelectEntity = preSelectEntity;
		this.fixedCriteria = fixedCriteria;
		try {
			if (dateAutoFilter != null) {
				this.preSelectDate = ReportFilterDateAutoFilter.valueOf(dateAutoFilter);
			}
		} catch (Exception e) {
		}
	}

	public FilterElement() {

	}

	public String getFilterDisplayName() {
		return displayName;
	}

	public boolean isFilterSelectMultiple() {
		return selectMultiple;
	}

	public String getName() {
		return name;
	}

	public ReportFilterDateAutoFilter getPreSelectDate() {
		return preSelectDate;
	}

	public String getPreSelectEntity() {
		return preSelectEntity;
	}

	public String getFixedCriteria() {
		return fixedCriteria;
	}

	public boolean isFilterRequired() {
		return required;
	}

	@Override
	public String toString() {
		return String.format("%s", name);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((preSelectDate == null) ? 0 : preSelectDate.hashCode());
		result = prime * result + ((preSelectEntity == null) ? 0 : preSelectEntity.hashCode());
		result = prime * result + ((fixedCriteria == null) ? 0 : fixedCriteria.hashCode());
		result = prime * result + (required ? 1231 : 1237);
		result = prime * result + (selectMultiple ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		FilterElement other = (FilterElement) obj;
		if (displayName == null) {
			if (other.displayName != null) {
				return false;
			}
		} else if (!displayName.equals(other.displayName)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (preSelectDate != other.preSelectDate) {
			return false;
		}
		if (preSelectEntity == null) {
			if (other.preSelectEntity != null) {
				return false;
			}
		} else if (!preSelectEntity.equals(other.preSelectEntity)) {
			return false;
		}
		if (fixedCriteria == null) {
			if (other.fixedCriteria != null) {
				return false;
			}
		} else if (!fixedCriteria.equals(other.fixedCriteria)) {
			return false;
		}
		if (required != other.required) {
			return false;
		}
		if (selectMultiple != other.selectMultiple) {
			return false;
		}
		return true;
	}

}
