package org.nextframework.view;

import java.io.IOException;
import java.util.Iterator;

public interface DataGridListener {

	void setDataGrid(DataGridTag dataGridTag);

	Iterator<String> replaceBodyStyleIterator(Iterator<String> bodyStyleIterator);

	Iterator<String> replaceBodyStyleClassesIterator(Iterator<String> bodyStyleClassIterator);

	void beforeTableTagContainer() throws IOException;

	void beforeStartTableTag() throws IOException;

	String replaceTableTagBegin(String tableTagBegin);

	void onRenderColumnHeader(String label);

	void onRenderColumnHeaderBody() throws IOException;

	String updateRowAttribute(String attr, String attrValue);

	void afterEndTableTag() throws IOException;

	void afterTableTagContainer() throws IOException;

}