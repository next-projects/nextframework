package org.nextframework.report.generator.layout;

import java.util.ArrayList;
import java.util.List;

public class LayoutElement {

	List<LayoutItem> items = new ArrayList<LayoutItem>();
	
	public List<LayoutItem> getItems() {
		return items;
	}

	@Override
	public String toString() {
		return String.format("%s", items);
	}


	public LayoutItem getItemWithName(String name) {
		for (LayoutItem item : items) {
			if(item instanceof FieldDetailElement){
				String name2 = ((FieldDetailElement) item).getName();
				if(name2.equals(name)){
					return item;
				}
			}
		}
		return null;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((items == null) ? 0 : items.hashCode());
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
		LayoutElement other = (LayoutElement) obj;
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		return true;
	}

	
}
