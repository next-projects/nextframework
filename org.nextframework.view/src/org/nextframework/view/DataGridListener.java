package org.nextframework.view;

import java.io.IOException;
import java.util.Iterator;

public interface DataGridListener {

	Iterator<String> replaceBodyStyleIterator(Iterator<String> bodyStyleIterator);

	Iterator<String> replaceBodyStyleClassesIterator(Iterator<String> bodyStyleClassIterator);

	String updateRowAttribute(String attr, String attrValue);

	void setDataGrid(DataGridTag dataGridTag);

	String replaceTableTagBegin(String tableTagBegin);

	void beforeStartTableTag() throws IOException;
	void afterEndTableTag() throws IOException;

	void onRenderColumnHeader(String label);

	void onRenderColumnHeaderBody() throws IOException;

	void beforeTableTagContainer() throws IOException;

}
