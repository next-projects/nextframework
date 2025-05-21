package org.nextframework.summary.compilation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.summary.Summary;
import org.nextframework.summary.SummaryRow;
import org.nextframework.summary.SummaryUtils;
import org.nextframework.summary.definition.SummaryDefinition;
import org.nextframework.summary.definition.SummaryGroupDefinition;

public class SummaryResult<ROW, SUMMARY extends Summary<ROW>> extends AbstractSumarizedData<SUMMARY, SummaryRow<ROW, SUMMARY>, SummaryResult<ROW, SUMMARY>> {

	List<SummaryRow<ROW, SUMMARY>> result;
	private Class<SUMMARY> summaryClass;
	private SummaryDefinition<SUMMARY> summaryDefinition;
	private List<ROW> rows;

	public SummaryResult(List<SummaryRow<ROW, SUMMARY>> result, Class<SUMMARY> summaryClass) {
		this.result = result;
		this.summaryClass = summaryClass;
	}

	public Class<SUMMARY> getSummaryClass() {
		return summaryClass;
	}

	public SummaryRow<ROW, SUMMARY> getFirstItem() {
		if (result == null || result.size() == 0) {
			return null;
		}
		return result.get(0);
	}

	public SUMMARY getFirstSummary() {
		SummaryRow<ROW, SUMMARY> firstItem = getFirstItem();
		if (firstItem != null) {
			return firstItem.getSummary();
		} else {
			return null;
		}
	}

	@Override
	public List<SummaryRow<ROW, SUMMARY>> getItems() {
		return result;
	}

	public List<ROW> getRowItens() {
		if (rows == null) {
			rows = new ArrayList<ROW>();
			for (SummaryRow<ROW, SUMMARY> summaryRow : this.result) {
				rows.add(summaryRow.getRow());
			}
		}
		return rows;
	}

	@Override
	protected SummaryResult<ROW, SUMMARY> createNew(List<SummaryRow<ROW, SUMMARY>> result) {
		return new SummaryResult<ROW, SUMMARY>(result, this.summaryClass);
	}

	@Override
	protected Summary<?> getSummaryFor(SummaryRow<ROW, SUMMARY> e) {
		return e.getSummary();
	}

	@Override
	protected Object getRowFor(SummaryRow<ROW, SUMMARY> e) {
		return e.getRow();
	}

	protected String[] getAvaiableGroups() {
		Set<SummaryGroupDefinition> groups = getSummaryDefinition().getGroups();
		String[] avaiableGroups = new String[groups.size() + 1];
		int i = 0;
		for (SummaryGroupDefinition group : groups) {
			avaiableGroups[i++] = group.getName();
		}
		avaiableGroups[avaiableGroups.length - 1] = "ROW";
		return avaiableGroups;
	}

	public SummaryResult<ROW, SUMMARY> getSumariesForGroup(String groupName) {
		groupName = SummaryUtils.convertCompositeGroupToMethodFormat(groupName);
		List<SummaryRow<ROW, SUMMARY>> result = new ArrayList<SummaryRow<ROW, SUMMARY>>();
		for (SummaryRow<ROW, SUMMARY> row : this.result) {
			if (row.isGroupChanged(groupName)) {
				result.add(row);
			}
		}
		return createNew(result);
	}

	public static <SUMMARY extends Summary<ROW>, ROW> SummaryResult<ROW, SUMMARY> createFrom(List<ROW> rows, Class<SUMMARY> summaryClass) {
		return SummaryBuilder.compileSummary(summaryClass).createSummaryResult(rows);
	}

	@Override
	protected SummaryDefinition<SUMMARY> getSummaryDefinition() {
		if (this.summaryDefinition == null) {
			this.summaryDefinition = new SummaryDefinition<SUMMARY>(summaryClass);
		}
		return this.summaryDefinition;
	}

	//**************************************// ORDER

	@SuppressWarnings("all")
	private int compareRows(OrderItem orderItem, SummaryRow<ROW, SUMMARY> o1, SummaryRow<ROW, SUMMARY> o2) {
		Object value1 = BeanDescriptorFactory.forBean(o1).getPropertyDescriptor(orderItem.property).getValue();
		Object value2 = BeanDescriptorFactory.forBean(o2).getPropertyDescriptor(orderItem.property).getValue();
		if (!(value1 instanceof Comparable && value2 instanceof Comparable)) {
			throw new IllegalArgumentException("cannot order by " + orderItem + ", the returned type does not implements Comparable");
		}
		return ((Comparable) value1).compareTo(value2);
	}

	@Override
	protected void reorderGroup(String group, final List<OrderItem> orderItems) {
		Comparator<SummaryRow<ROW, SUMMARY>> comparator = new Comparator<SummaryRow<ROW, SUMMARY>>() {

			public int compare(SummaryRow<ROW, SUMMARY> o1, SummaryRow<ROW, SUMMARY> o2) {
				if (orderItems != null) { //2011/11/30
					//nothing to order here
					for (OrderItem orderItem : orderItems) {
						int difference = compareRows(orderItem, o1, o2);
						if (difference != 0) {
							return orderItem.asc ? difference : -difference;
						}
					}
				}
				return o1.getRowIndex() - o2.getRowIndex(); //keep original order?
			}

		};
		reorderGroup(group, comparator);
	}

	public SummaryResult<ROW, SUMMARY> reorderGroup(String group, Comparator<SummaryRow<ROW, SUMMARY>> comparator) {
		String[] avaiableGroups = getAvaiableGroups();

		int beginIndex = 0;
		int endIndex = 0;
		List<SummaryRow<ROW, SUMMARY>> items = getItems();
		for (int i = 0; i < items.size(); i++) {
			SummaryRow<ROW, SUMMARY> summaryRow = items.get(i);
			endIndex = i;
			if (outterGroupChanged(summaryRow.getChangedGroups(), avaiableGroups, group)) { //if group changed.. reorder sublist
				if (endIndex - beginIndex > 1) {
					//only order if there's more than one item
					reorderSubList(group, avaiableGroups, getItems().subList(beginIndex, endIndex), comparator);
				}
				beginIndex = i;
			}
		}
		if (getItems().size() - beginIndex > 1) {
			reorderSubList(group, avaiableGroups, getItems().subList(beginIndex, getItems().size()), comparator);
		}
		return this;
	}

	private String[] keepUnderGroups(String[] changedGroups, String[] avaiableGroups, String currentGroup) {
		if (currentGroup.equals("ROW")) {
			return new String[0];
		}
		int index = indexOf(avaiableGroups, currentGroup);
		List<String> result = new ArrayList<String>();
		for (int i = index; i < avaiableGroups.length; i++) {
			String avaiableGroup = avaiableGroups[i];
			if (indexOf(changedGroups, avaiableGroup) >= 0) {
				result.add(avaiableGroup);
			}
		}
		return result.toArray(new String[result.size()]);
	}

	private boolean outterGroupChanged(String[] changedGroups, String[] avaiableGroups, String currentGroup) {
		int index = indexOf(avaiableGroups, currentGroup);
		if (index < 0) {
			throw new RuntimeException("unexpected result, the group " + currentGroup + " was not found in the avaiableGroups " + Arrays.deepToString(avaiableGroups));
		}
		for (int i = 0; i < index; i++) {
			if (indexOf(changedGroups, avaiableGroups[i]) >= 0) {
				return true;
			}
		}
		return false;
	}

	private int indexOf(String[] array, String value) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(value)) {
				return i;
			}
		}
		return -1;
	}

	private void reorderSubList(String currentGroup, String[] avaiableGroups, List<SummaryRow<ROW, SUMMARY>> subList, final Comparator<SummaryRow<ROW, SUMMARY>> comparator) {
		SummaryRow<ROW, SUMMARY> model = subList.get(0);
		Collections.sort(subList, comparator);
		subList.set(0, new SummaryRow<ROW, SUMMARY>(subList.get(0).getRowIndex(), subList.get(0).getRow(), subList.get(0).getSummary(), model.isFirst(), model.getChangedGroups()));
		for (int i = 1; i < subList.size(); i++) {
			subList.set(i, new SummaryRow<ROW, SUMMARY>(subList.get(i).getRowIndex(), subList.get(i).getRow(), subList.get(i).getSummary(), false, keepUnderGroups(subList.get(i).getChangedGroups(), avaiableGroups, currentGroup)));
		}
	}

}
