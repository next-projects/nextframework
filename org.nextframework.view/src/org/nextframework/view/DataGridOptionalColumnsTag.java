package org.nextframework.view;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.nextframework.controller.json.JsonTranslator;
import org.nextframework.core.standard.Next;
import org.nextframework.service.ServiceFactory;
import org.nextframework.view.DataGridTag.Status;
import org.nextframework.view.ajax.Callback;

public class DataGridOptionalColumnsTag extends BaseTag implements LogicalTag {

	public static final String CACHE_KEY_PREFIX = "view.datagrid.optionalcolumns.";

	public static String getCacheString(String cacheKey) {
		return CACHE_KEY_PREFIX + cacheKey;
	}

	private String cacheKey;

	public String getCacheKey() {
		return cacheKey;
	}

	public void setCacheKey(String cacheKey) {
		this.cacheKey = cacheKey;
	}

	@Override
	protected void doComponent() throws Exception {
		DataGridTag dataGrid = findParent2(DataGridTag.class, true);
		if (dataGrid.getCurrentStatus() == Status.REGISTER) {
			dataGrid.registerListener(new Listener(cacheKey));
		}
	}

	public static class Listener extends DataGridListenerAdaptor {

		private String id;

		private Map<String, String> headers = new LinkedHashMap<String, String>();

		private String lastHeader;

		private String cacheKey;

		public Listener(String cacheKey) {
			this.cacheKey = cacheKey;
		}

		@Override
		public void beforeTableTagContainer() throws IOException {
			this.id = getDataGrid().generateUniqueId();
			getDataGrid().getOut().println("<div style='position: relative;'> <!-- optional columns -->");
			getDataGrid().getOut().println("<div style='position: absolute; top: -20px; right: 0px;'>" +
					"<img id=\"" + id + "\" width=\"10px\" src=\"" + getDataGrid().getRequest().getContextPath() + "/resource/img/dropdown.png\"/></div>");
		}

		@Override
		public void onRenderColumnHeader(String label) {
			lastHeader = label;
			headers.put(label, getDataGrid().generateUniqueId());
		}

		@Override
		public void onRenderColumnHeaderBody() throws IOException {
			String id = headers.get(lastHeader);
			getDataGrid().getOut().print("<div id='" + id + "'></div>");
		}

		@Override
		public void afterTableTagContainer() throws IOException {

			OptionalColumnsSaverCallback callback = new OptionalColumnsSaverCallback();
			int ajaxId = callback.registerSingleton();
			getDataGrid().getOut().println("</div> <!-- optional columns -->");
			String jsonHeadersMap = ServiceFactory.getService(JsonTranslator.class).toJson(headers);

			String cacheString = getCacheString(cacheKey);
			String hideColumns = Next.getRequestContext().getUserPersistentAttribute(cacheString);

			String ajaxInfo = "{ajaxId: " + ajaxId + ", serverUrl: '" + callback.getServerUrl() + "', cacheKey: '" + cacheKey + "'}";
			getDataGrid().getOut().println("<script>next.datagrid.createOptionalColumns('" + getDataGrid().getId() + "', '" + this.id + "', " + jsonHeadersMap + ", " + ajaxInfo + ", " + hideColumns + ");</script>");

		}

	}

	/**
	 * The javascript code that calls this class is on NextDataGrid.
	 */
	public static class OptionalColumnsSaverCallback extends Callback {

		private static final long serialVersionUID = 1L;

		public OptionalColumnsSaverCallback() {
		}

		@Override
		public String doAjax() throws Exception {
			String cacheKey = Next.getRequestContext().getParameter("cacheKey");
			String hideColumns = Next.getRequestContext().getParameter("hideColumns");
			String cacheString = getCacheString(cacheKey);
			Next.getRequestContext().setUserPersistentAttribute(cacheString, hideColumns);
			return "server: updated config for " + cacheString;
		}

		@Override
		public int hashCode() {
			return 1;
		}

		@Override
		public boolean equals(Object obj) {
			//all instances of this class equals each other
			//required for the registerSingleton to work
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() == obj.getClass())
				return true;
			return false;
		}

	}

}
