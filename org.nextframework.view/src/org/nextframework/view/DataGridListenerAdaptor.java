package org.nextframework.view;

import java.io.IOException;
import java.util.Iterator;

public class DataGridListenerAdaptor implements DataGridListener {

	private DataGridTag dataGrid;

	@Override
	public void setDataGrid(DataGridTag dataGridTag) {
		this.dataGrid = dataGridTag;
	}

	public DataGridTag getDataGrid() {
		return dataGrid;
	}

	@Override
	public Iterator<String> replaceBodyStyleIterator(Iterator<String> bodyStyleIterator) {
		return bodyStyleIterator;
	}

	@Override
	public Iterator<String> replaceBodyStyleClassesIterator(Iterator<String> bodyStyleClassIterator) {
		return bodyStyleClassIterator;
	}

	@Override
	public void beforeTableTagContainer() throws IOException {

	}

	@Override
	public void beforeStartTableTag() throws IOException {

	}

	@Override
	public String replaceTableTagBegin(String tableTagBegin) {
		return tableTagBegin;
	}

	@Override
	public void onRenderColumnHeader(String label) {

	}

	@Override
	public void onRenderColumnHeaderBody() throws IOException {

	}

	@Override
	public String updateRowAttribute(String attr, String attrValue) {
		return attrValue;
	}

	@Override
	public void afterEndTableTag() throws IOException {

	}

	@Override
	public void afterTableTagContainer() throws IOException {

	}

}
