package org.nextframework.resource;

import static org.nextframework.js.NextGlobalJs.next;
import static org.stjs.javascript.JSCollections.$array;
import static org.stjs.javascript.JSCollections.$map;

import org.nextframework.js.ajax.AjaxRequest;
import org.nextframework.resource.NextDialogs.MessageDialog;
import org.stjs.javascript.Array;
import org.stjs.javascript.Global;
import org.stjs.javascript.JSCollections;
import org.stjs.javascript.Map;
import org.stjs.javascript.dom.DOMEvent;
import org.stjs.javascript.dom.Div;
import org.stjs.javascript.dom.Element;
import org.stjs.javascript.dom.HTMLCollection;
import org.stjs.javascript.dom.Input;
import org.stjs.javascript.dom.Label;
import org.stjs.javascript.dom.Table;
import org.stjs.javascript.dom.TableCell;
import org.stjs.javascript.dom.TableRow;
import org.stjs.javascript.functions.Callback1;

public class NextDataGrid {

	public static class OptionalColumnsComponent {

		private String tableId;
		private String dropId;
		private Map<String, String> columnsMap;

		private Array<Column> columns;
		private Element dropEl;
		private Map<String, Object> ajaxInfo;
		private Array<String> hideColumns;

		private static class Column {

			String id;
			String label;
			boolean showColumn;
			Input check;
			int columnIndex;
			Table table;

			public Column(String id, String label) {
				this.id = id;
				this.label = label;
				this.showColumn = true;
				this.columnIndex = getColumnIndex();
				this.table = getTable();
			}

			public void appendColumn(Element p) {
				this.check = next.dom.newInput("checkbox");
				check.checked = showColumn;
				check.style.margin = "6px";
				p.appendChild(check);
				Label labelEl = next.dom.newElement("label");
				labelEl.htmlFor = check.id;
				labelEl.innerHTML = label;
				labelEl.style.verticalAlign = "2px";
				p.appendChild(labelEl);
			}

			private int getColumnIndex() {
				TableCell tableCell = getTableHeaderCell();
				return tableCell.cellIndex;
			}

			public TableCell getTableHeaderCell() {
				Element element = next.dom.toElement(id);
				while (!element.parentNode.tagName.toLowerCase().equals("td") && !element.parentNode.tagName.toLowerCase().equals("th")) {
					element = element.parentNode;
				}
				TableCell tableCell = (TableCell) element.parentNode;
				return tableCell;
			}

			private Table getTable() {
				Element element = next.dom.toElement(id);
				return (Table) next.dom.getParentTag(element, "table");
			}

			public void updateElements() {
				showColumn = check.checked;
				updateVisibility();
			}

			public void updateVisibility() {
				updateRows(table.tHead.rows);
				updateRows(table.tBodies.$get(0).rows);
			}

			public void updateRows(HTMLCollection<TableRow> rows) {
				for (int i = 0; i < rows.length; i++) {
					TableCell cell = rows.$get(i).cells.$get(columnIndex);
					cell.style.display = showColumn ? "" : "none";
				}
			}

		}

		public OptionalColumnsComponent(String tableId, String dropId, Map<String, String> columnsMap, Map<String, Object> ajaxInfo, Array<String> hideColumns) {
			this.tableId = tableId;
			this.dropEl = next.dom.toElement(dropId);
			this.columnsMap = columnsMap;
			this.ajaxInfo = ajaxInfo;
			this.columns = $array();
			this.hideColumns = hideColumns;
		}

		private void init() {
			for (String label : columnsMap) {
				Column column = new Column(columnsMap.$get(label), label);
				if (hideColumns != null && hideColumns.indexOf(column.label) >= 0) {
					column.showColumn = false;
					column.updateVisibility();
				}
				this.columns.push(column);
			}
			dropEl.style.cursor = "pointer";
			final OptionalColumnsComponent bigThis = this;
			next.events.attachEvent(dropEl, "click", new Callback1<DOMEvent>() {
				public void $invoke(DOMEvent p1) {
					bigThis.showConfigurationDialog();
				}
			});
		}

		public void showConfigurationDialog() {
			MessageDialog dialog = new MessageDialog();
			dialog.setTitle("Configurar colunas");
			for (String c : columns) {
				Column column = columns.$get(c);
				Element divOp = next.dom.newElement("div", $map("class", next.globalMap.get("NextDialogs.option", "popup_box_option")));
				column.appendColumn(divOp);
				dialog.body.appendChild(divOp);
			}
			final OptionalColumnsComponent bigThis = this;
			dialog.setCallback(new NextDialogs.DialogCallback() {
				public void onClose(String command, Object value) {
					if (command.equals("CANCEL")) {
						bigThis.cancel();
					} else {
						bigThis.saveColumns();
					}
				}
			});
			dialog.show();
		}

		protected void saveColumns() {
			for (String c : columns) {
				Column column = columns.$get(c);
				column.getTableHeaderCell().style.width = "auto";
				column.updateElements();
			}
			columns.$get(columns.$length() - 1).getTableHeaderCell().style.width = "1%";
			persist();
		}

		/**
		 * @see DataGridOptionalColumnsTag for ajaxInfo setup
		 */
		private void persist() {

			Integer ajaxId = (Integer) ajaxInfo.$get("ajaxId");
			String serverUrl = (String) ajaxInfo.$get("serverUrl");
			String cacheKey = (String) ajaxInfo.$get("cacheKey");

			Array<String> configMap = JSCollections.$array();
			for (String c : columns) {
				Column column = columns.$get(c);
				if (!column.showColumn) {
					configMap.push(column.label);
				}
			}

			AjaxRequest request = next.ajax.newRequest();
			request.setUrl(serverUrl);
			request.setParameter("serverId", ajaxId);
			request.setParameter("cacheKey", cacheKey);
			request.setParameter("hideColumns", Global.JSON.stringify(configMap));
			request.setAppendContext(false);
			request.setOnComplete(new Callback1<String>() {
				@Override
				public void $invoke(String p1) {
					Global.console.log(p1);
				}
			});

			request.send();
		}

		protected void cancel() {

		}

	}

	public void createOptionalColumns(String tableId, String dropId, Map<String, String> columnsMap, Map<String, Object> ajaxInfo, Array<String> hideColumns) {
		new OptionalColumnsComponent(tableId, dropId, columnsMap, ajaxInfo, hideColumns).init();
	}

}
