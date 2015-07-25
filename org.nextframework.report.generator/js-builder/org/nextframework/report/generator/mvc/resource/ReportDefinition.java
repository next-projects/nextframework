package org.nextframework.report.generator.mvc.resource;

import static org.nextframework.js.NextGlobalJs.next;
import static org.stjs.javascript.JSCollections.$array;

import org.nextframework.js.NextGlobalJs;
import org.stjs.javascript.Array;
import org.stjs.javascript.Global;
import org.stjs.javascript.dom.DOMEvent;
import org.stjs.javascript.dom.Div;
import org.stjs.javascript.dom.Table;
import org.stjs.javascript.dom.TableCell;
import org.stjs.javascript.dom.TableRow;
import org.stjs.javascript.functions.Function1;

public class ReportDefinition implements Selectable {

	Table designTable;
	ReportSection sectionTitle;
	ReportSection sectionDetailHeader;
	ReportSection sectionDetail;
	
	Array<ReportColumn> columns;
	Array<ReportGroup> groups;
	
	Array<ReportElement> elements;
	ReportDesigner designer;
	
	ReportElement selectedElement;
	
	public ReportDefinition(ReportDesigner designer, Table table){
		this.designTable = table;
		this.elements = $array();
		this.columns = $array();
		this.groups = $array();
		this.designer = designer;
		
		this.sectionTitle = new ReportSection(SectionType.TITLE, this, null);
		this.sectionTitle.rows.$get(0).row.insertCell(0);
		this.sectionDetailHeader = new ReportSection(SectionType.DETAIL_HEADER, this, null);
		this.sectionDetail = new ReportSection(SectionType.DETAIL, this, null);
	}
	
	interface RowIterator {
		void titleRow(ReportRow reportRow);
		void labelRow(TableRow reportRow);
		void row(ReportRow reportRow);
	}
	
	public Array<ReportSection> getAllSections(){
		Array<ReportSection> array = $array(
				sectionDetailHeader,
				sectionDetail
		);
		for (int i = 0; i < groups.$length(); i++) {
			array.push(groups.$get(i).groupHeader);
		}
		return array;
	}
	
	public ReportGroup removeGroup(String groupName) {
		ReportGroup group = getReportGroup(groupName);
		ReportSection section = group.groupHeader;
		section.clear();
		for (int i = 0; i < groups.$length(); i++) {
			group = groups.$get(i);
			if(group.groupName.equals(groupName)){
				groups.splice(i, 1);
				break;
			}
		}
		return group;
	}

	public ReportGroup getReportGroup(String groupName) {
		ReportGroup group = null;
		for (int i = 0; i < groups.$length(); i++) {
			group = groups.$get(i);
			if(group.groupName.equals(groupName)){
				break;
			}
		}
		return group;
	}
	public ReportGroup addGroup(String groupName) {
		ReportSection groupHeader = new ReportSection(SectionType.GROUP_HEADER, this, groupName);
		ReportGroup reportGroup = new ReportGroup(groupHeader, groupName);
		
		groups.push(reportGroup);
		
		return reportGroup;
	}
	
	public void removeColumn(int index){
		final ReportColumn column = columns.$get(index);
		
		if(column == null){
			Global.alert("Coluna nao encontrada");
			return;
		}
		
		removeColumnElements(column);

		iteratorOverRows(new RowIterator() {
			
			@Override
			public void row(ReportRow reportRow) {
				TableCell tdForColumn = reportRow.getTdForColumn(column);
				reportRow.row.removeChild(tdForColumn);
			}
			
			@Override
			public void titleRow(ReportRow reportRow) {
				readjustColspan(reportRow.row);
			}
			@Override
			public void labelRow(TableRow reportRow) {
				readjustColspan(reportRow);
			}

			private void readjustColspan(TableRow reportRow) {
				if(reportRow.cells.length > 0){
					if(reportRow.cells.$get(0).colSpan > 1){
						reportRow.cells.$get(0).colSpan--;
					}
				}
			}
		});
		
		next.util.removeItem(columns, column);
		
		designer.writeXml();
	}


	private void removeColumnElements(final ReportColumn column) {
		final ReportDefinition bigThis = this;
		iteratorOverRows(new RowIterator() {
			public void titleRow(ReportRow reportRow) {}
			public void labelRow(TableRow reportRow) {}
			
			public void row(ReportRow reportRow) {
				ReportElement element = bigThis.getElementForRowAndColumn(reportRow, column);
				if(element != null){
					if(element.layoutItem != null){
						bigThis.designer.layoutManager.remove(element.layoutItem);
					} else {
						bigThis.remove(element);
					}
				}
			}
		});
	}
	
	public void remove(ReportElement element) {
		TableCell cell = getCellForElement(element);
		cell.removeChild(element.getNode());
		next.util.removeItem(elements, element);
	}

	private TableCell getCellForElement(ReportElement element) {
		ReportRow row = element.row;
		ReportColumn column = element.column;
		return getCellForRowAndColumn(row, column);
	}

	public TableCell getCellForRowAndColumn(ReportRow row, ReportColumn column) {
		return row.row.cells.$get(column.getIndex());
	}

	public void addColumn(){
		final ReportColumn reportColumn = new ReportColumn(this);
		columns.push(reportColumn);
		
		iteratorOverRows(new RowIterator() {
			@Override
			public void titleRow(ReportRow reportRow) {
				if(reportRow.row.cells.length > 0){
					reportRow.row.cells.$get(0).colSpan = reportColumn.getIndex() + 1;
				}
			}
			@Override
			public void row(ReportRow reportRow) {
				reportRow.row.insertCell(reportColumn.getIndex());
			}
			@Override
			public void labelRow(TableRow reportRow) {
				reportRow.cells.$get(0).colSpan = reportColumn.getIndex() + 1;
			}
		});
	}

	private void iteratorOverRows(RowIterator rowIterator) {
		Array<ReportSection> allSections = getAllSections();
		rowIterator.titleRow(sectionTitle.rows.$get(0));
		rowIterator.labelRow(sectionTitle.labelRow);
		for (String key : allSections) {
			ReportSection section = allSections.$get(key);
			TableRow labelRow = section.labelRow;
			rowIterator.labelRow(labelRow);
			Array<ReportRow> rows = section.rows;
			for (String rowkey : rows) {
				ReportRow row = rows.$get(rowkey);
				rowIterator.row(row);
			}
		}
	}
	
	public void addElement(ReportElement element, ReportSection section, int columnIndex){
		ReportRow row = section.getLastRow();
		ReportColumn column = getColumnByIndex(columnIndex);
		addElementToRowAndColumn(element, row, column);
	}
	
	public ReportElement getElementForRowAndColumn(ReportRow row, ReportColumn column){
		for (String key : elements) {
			ReportElement element = elements.$get(key);
			if(element.row.equals(row) && element.column.equals(column)){
				return element;
			}
		}
		return null;
	}

	public void addElementToRowAndColumn(final ReportElement element, final ReportRow row, ReportColumn column) {
		element.row = row;
		element.column = column;
		
		elements.push(element);
		
		
		TableCell td = row.getTdForColumn(column);
		
		final Div div = (Div) Global.window.document.createElement("DIV");
		div.innerHTML = element.toString();
		div.style.padding = "1px";
		
		td.appendChild(div);
		
		element.setNode(div);
		
		final ReportDefinition bigThis = this;
		
		div.onclick = new Function1<DOMEvent, Boolean>() {
			@Override
			public Boolean $invoke(DOMEvent p1) {
				bigThis.selectItem(element, true);
				return true;
			}
		};
	}

	protected void selectItem(ReportElement el, boolean blur) {
//		designer.fieldArea.blur();
//		designer.groupArea.blur();
//		designer.filterArea.blur();
		if(blur){
			designer.blurAllBut(this);
		}
		
		if(el.layoutItem != null){
			el = el.layoutItem.getElements().$get(0);
		}
		if(selectedElement != null){
			unselectSelectedItem();
			if(selectedElement.equals(el)){
				selectedElement = null;
				return;
			}
		}
		if(el.layoutItem != null){
			for (int i = 0; i < el.layoutItem.getElements().$length(); i++) {
				markSelectItem(el.layoutItem.getElements().$get(i));
			}
		} else {
			markSelectItem(el);
		}
		this.selectedElement = el;
		if(el.onFocus != null){
			el.onFocus.$invoke();
		}
		
		
//		if(el instanceof FieldReportElement){
//			designer.patternInput.value = ((FieldReportElement)el).pattern;
//			final ReportDefinition bigThis = this;
//			ReportElement bigEl = el;
//			designer.patternInput.onkeyup = new Function1<DOMEvent, Boolean>() {
//				public Boolean $invoke(DOMEvent p1) {
//					FieldReportElement fieldReportElement = ((FieldReportElement)bigThis.selectedElement);
//					fieldReportElement.pattern = bigThis.designer.patternInput.value;
//					bigThis.designer.writeXml();
//					return true;
//				}
//			};
//		}
	}

	private void unselectSelectedItem() {
		if(selectedElement != null){ 
			if(selectedElement.layoutItem != null){
				for (int i = 0; i < selectedElement.layoutItem.getElements().$length(); i++) {
					unselectItem(selectedElement.layoutItem.getElements().$get(i));
				}
			} else {
				unselectItem(selectedElement);
			}
		}
		designer.hideInputLabel();
		designer.hideInputDatePattern();
		designer.hideInputNumberPattern();
		designer.hideInputPatternGroup();
		designer.hideAggregate();
		designer.labelInput.value = "";
		designer.patternDateInput.value = "";
	}
	
	public void blur(){
		unselectSelectedItem();
		selectedElement = null;
	}

	private void markSelectItem(ReportElement el) {
		el.getNode().style.border = "1px dotted gray";
		el.getNode().style.backgroundColor = "#FFD";
		el.getNode().style.padding = "0px";
	}

	private void unselectItem(ReportElement reportElement) {
		reportElement.getNode().style.border = "";
		reportElement.getNode().style.backgroundColor = "";
		reportElement.getNode().style.padding = "1px";
	}

	public ReportColumn getColumnByIndex(int column) {
		while(columns.$length() <= column){
			addColumn();
		}
		return columns.$get(column);
	}

}

class ReportGroup {
	
	ReportSection groupHeader;
	
	String groupName;

	public ReportGroup(ReportSection groupHeader, String groupName) {
		super();
		this.groupHeader = groupHeader;
		this.groupName = groupName;
	}
	
}

enum SectionType {
	TITLE, 
	GROUP_HEADER, 
	DETAIL_HEADER, 
	DETAIL
}

class ReportSection {
	
	Array<ReportRow> rows;
	
	String group = null;

	ReportDefinition definition;

	TableRow labelRow;

	SectionType sectionType;
	
	public ReportSection(SectionType sectionType, ReportDefinition definition, String group){
		this.sectionType = sectionType;
		this.definition = definition;
		this.group = group;
		rows = $array();
		
		int insertAt = definition.designTable.rows.length;
		if(group != null){
			insertAt -= 4;
		}
		this.labelRow = (TableRow) definition.designTable.insertRow(insertAt);
		labelRow.id = "section"+sectionType+(group!= null? group:"")+"-L";
		next.style.addClass(labelRow, "sectiondecorator");
		
		TableCell labelRowTd = (TableCell) labelRow.insertCell(0);
		labelRowTd.innerHTML = sectionType.toString() + (group != null? " ["+group+"]": "");
		if(definition.columns.$length() > 0){
			labelRowTd.colSpan = definition.columns.$length(); 
		}
			
		next.style.addClass(labelRowTd, "sectionLabel");
		
		rows.push(createRow());
	}
	
	public void clear() {
		labelRow.parentNode.removeChild(labelRow);
		for (int i = 0; i < rows.$length(); i++) {
			ReportRow row = rows.$get(i);
			row.row.parentNode.removeChild(row.row);
		}
	}

	public ReportRow getLastRow() {
		if(rows.$length() == 0){
			Global.alert("error: report section "+this.sectionType+" does not have rows");
			return null;
		}
		return rows.$get(rows.$length()-1);
	}

	public ReportRow getRow(int index){
		while(rows.$length() <= index){
			rows.push(createRow());
		}
		return rows.$get(index);
	}

	private ReportRow createRow() {
		int labelRowIndex = labelRow.rowIndex;
		int insertInIndex = labelRowIndex + rows.$length() + 1;
		TableRow row = (TableRow) definition.designTable.insertRow(insertInIndex);
		//next.style.addClass(row, sectionType.toString());
		for(int i = 0; i < definition.columns.$length(); i++){
			row.insertCell(0);
		}
		return new ReportRow(this, row, rows.$length());
	}
	
}

class ReportRow {
	
	TableRow row;
	int index;
	ReportSection section;
	
	public ReportRow(ReportSection section, TableRow row, int index) {
		this.section = section;
		this.row = row;
		this.index = index;
	}

	public TableCell getTdForColumn(ReportColumn column) {
		if(column.getIndex() >= row.cells.length){
			Global.alert("Error: There is no column "+column.getIndex()+" for row "+row.rowIndex);
		}
		return row.cells.$get(column.getIndex());
	}
}

class ReportColumn {

	private ReportDefinition reportDefinition;

	public ReportColumn(ReportDefinition reportDefinition) {
		this.reportDefinition = reportDefinition;
	}
	
	public int getIndex(){
		//return reportDefinition.columns.indexOf(this);
		return NextGlobalJs.next.util.indexOf(reportDefinition.columns, this);
	}
}