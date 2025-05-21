package org.nextframework.report.generator.mvc.resource;

import org.nextframework.js.NextGlobalJs;
import org.stjs.javascript.dom.DOMEvent;
import org.stjs.javascript.dom.Element;
import org.stjs.javascript.dom.HTMLCollection;
import org.stjs.javascript.dom.Image;
import org.stjs.javascript.dom.Table;
import org.stjs.javascript.dom.TableRow;
import org.stjs.javascript.functions.Function1;

public class ChoosePropertiesTagUtil {

	static String OPEN = "/resource/org/nextframework/report/renderer/html/resource/mais.gif";
	static String CLOSE = "/resource/org/nextframework/report/renderer/html/resource/menos.gif";

	static String application = null;

	public static void install(String app) {
		ChoosePropertiesTagUtil.application = app;
		Table table = el("propertiesTable");
		HTMLCollection<TableRow> rows = table.rows;
		for (int i = 1; i < rows.length; i++) {//row 0 is placeholder
			TableRow row = rows.$get(i);
			Image openClose = NextGlobalJs.next.dom.getInnerElementById(row, "openCloseBtn");
			openClose.style.visibility = "hidden";
			if (i + 1 < rows.length) {
				TableRow row2 = rows.$get(i + 1);
				String row1Property = row.getAttribute("data-forProperty");
				String row2Property = row2.getAttribute("data-forProperty");
				if (row2Property.length() > row1Property.length() + 1) {
					String base = row2Property.substring(0, row1Property.length() + 1);
					if (base.equals(row1Property + ".")) {
						openClose.style.visibility = "";
						colapseGroup(row, rows);
						installOpenCloseButton(openClose, row, rows);
					}
				}
			}
		}
	}

	private static void installOpenCloseButton(final Image openClose, final TableRow row, final HTMLCollection<TableRow> rows) {
		openClose.onclick = new Function1<DOMEvent, Boolean>() {

			public Boolean $invoke(DOMEvent p1) {
				String open = openClose.getAttribute("data-open");
				if (open == null || "false".equals(open)) {
					openClose.src = application + CLOSE;
					openGroup(row, rows);
					openClose.setAttribute("data-open", "true");
				} else {
					openClose.src = application + OPEN;
					colapseGroup(row, rows);
					openClose.setAttribute("data-open", "false");
				}
				return true;
			}

		};
	}

	private static void openGroup(TableRow row, HTMLCollection<TableRow> rows) {
		String row1Property = row.getAttribute("data-forProperty");
		for (int i = row.rowIndex + 1; i < rows.length; i++) {
			TableRow row2 = rows.$get(i);
			Image openClose = NextGlobalJs.next.dom.getInnerElementById(row2, "openCloseBtn");
			openClose.src = application + OPEN;
			openClose.setAttribute("data-open", "false");
			String row2Property = row2.getAttribute("data-forProperty");
			if (row2Property.length() > row1Property.length() + 1) {
				String base = row2Property.substring(0, row1Property.length() + 1);
				if (base.equals(row1Property + ".")) {
					if (row2Property.substring(base.length()).indexOf('.') > 0) {
						continue;
					}
					row2.style.display = "";
				} else {
					break;
				}
			} else {
				break;
			}
		}
	}

	private static void colapseGroup(TableRow row, HTMLCollection<TableRow> rows) {
		for (int i = row.rowIndex + 1; i < rows.length; i++) {
			TableRow row2 = rows.$get(i);
			String row1Property = row.getAttribute("data-forProperty");
			String row2Property = row2.getAttribute("data-forProperty");
			if (row2Property.length() > row1Property.length() + 1) {
				String base = row2Property.substring(0, row1Property.length() + 1);
				if (base.equals(row1Property + ".")) {
					row2.style.display = "none";
				} else {
					break;
				}
			}
		}
	}

	private static <X extends Element> X el(String id) {
		return NextGlobalJs.next.dom.toElement(id);
	}

}
