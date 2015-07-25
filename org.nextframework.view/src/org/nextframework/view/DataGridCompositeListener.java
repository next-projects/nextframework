package org.nextframework.view;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

class DataGridCompositeListener implements DataGridListener {
	
	private List<DataGridListener> listeners;

	DataGridCompositeListener(List<DataGridListener> listeners){
		this.listeners = listeners;
	}
	@Override
	public Iterator<String> replaceBodyStyleIterator(Iterator<String> bodyStyleIterator) {
		for (DataGridListener l : listeners) {
			bodyStyleIterator = l.replaceBodyStyleIterator(bodyStyleIterator);
		}
		return bodyStyleIterator;
	}

	@Override
	public Iterator<String> replaceBodyStyleClassesIterator(Iterator<String> bodyStyleClassIterator) {
		for (DataGridListener l : listeners) {
			bodyStyleClassIterator = l.replaceBodyStyleIterator(bodyStyleClassIterator);
		}
		return bodyStyleClassIterator;
	}

	@Override
	public String updateRowAttribute(String attr, String attrValue) {
		for (DataGridListener l : listeners) {
			attrValue = l.updateRowAttribute(attr, attrValue);
		}
		return attrValue;
	}

	@Override
	public void setDataGrid(DataGridTag dataGridTag) {
		for (DataGridListener l : listeners) {
			l.setDataGrid(dataGridTag);
		}
	}

	@Override
	public void beforeStartTableTag() throws IOException {
		for (DataGridListener l : listeners) {
			l.beforeStartTableTag();
		}
	}
	
	@Override
	public void afterEndTableTag() throws IOException {
		for (DataGridListener l : listeners) {
			l.afterEndTableTag();
		}
	}

	@Override
	public String replaceTableTagBegin(String tableTagBegin) {
		for (DataGridListener l : listeners) {
			tableTagBegin = l.replaceTableTagBegin(tableTagBegin);
		}
		return tableTagBegin;
	}
	@Override
	public void onRenderColumnHeader(String label) {
		for (DataGridListener l : listeners) {
			l.onRenderColumnHeader(label);
		}
	}
	@Override
	public void onRenderColumnHeaderBody() throws IOException {
		for (DataGridListener l : listeners) {
			l.onRenderColumnHeaderBody();
		}
	}
	@Override
	public void beforeTableTagContainer() throws IOException {
		for (DataGridListener l : listeners) {
			l.beforeTableTagContainer();
		}
	}
}