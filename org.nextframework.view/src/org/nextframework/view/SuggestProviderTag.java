package org.nextframework.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.nextframework.controller.json.JsonTranslator;
import org.nextframework.core.standard.Next;
import org.nextframework.service.ServiceFactory;
import org.nextframework.util.Util;
import org.nextframework.view.ajax.Callback;
import org.nextframework.view.ajax.SuggestCallback;

public class SuggestProviderTag extends BaseTag {

	protected String name;
	protected Object dataSource;

	@Override
	@SuppressWarnings("rawtypes")
	protected void doComponent() throws Exception {
		getOut().println("<script type=\"text/javascript\">");
		getOut().println("next.suggest.providers['" + name + "'] = ");
		if (dataSource != null) {
			if (dataSource instanceof Collection) {
				JsonTranslator jsonTranslator = ServiceFactory.getService(JsonTranslator.class);
				List<Map<String, String>> dataSourceMap = prepareDatasourceListForJson((Collection) dataSource);
				String json = jsonTranslator.toJson(dataSourceMap);
				getOut().println("new NextSuggestStaticListProvider(");
				getOut().println(json);
				getOut().println(")");
			} else if (dataSource instanceof SuggestCallback) {
				final SuggestCallback suggestCallback = (SuggestCallback) dataSource;
				Callback callback = new SuggestTagCallback(suggestCallback);
				int serverId = callback.register();
				getOut().println("new NextSuggestAjaxProvider(" + serverId + ", \"" + callback.getServerUrl() + "\")");
			}
		} else {
			throw new IllegalArgumentException("dataSource must be set");
		}
		getOut().println(";");
		getOut().println("</script>");
	}

	public static List<Map<String, String>> prepareDatasourceListForJson(Collection<?> collection) {
		List<Map<String, String>> dataSourceMap = new ArrayList<Map<String, String>>();
		for (Object o : collection) {
			String text = Util.strings.toStringDescription(o);
			String value = Util.strings.toStringIdStyled(o);
			Map<String, String> map = new LinkedHashMap<String, String>();
			map.put("_t", text);
			map.put("_v", value);
			dataSourceMap.add(map);
		}
		return dataSourceMap;
	}

	public String getName() {
		return name;
	}

	public Object getDataSource() {
		return dataSource;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDataSource(Object dataSourceList) {
		this.dataSource = dataSourceList;
	}

	private static final class SuggestTagCallback extends Callback implements Serializable {

		private final SuggestCallback suggestCallback;
		private static final long serialVersionUID = 1L;

		private SuggestTagCallback(SuggestCallback suggestCallback) {
			this.suggestCallback = suggestCallback;
		}

		public String doAjax() throws Exception {
			String text = Next.getRequestContext().getParameter("_text");
			List<?> result = suggestCallback.suggest(null, text);
			JsonTranslator jsonTranslator = ServiceFactory.getService(JsonTranslator.class);
			List<Map<String, String>> dsMap = prepareDatasourceListForJson(result);
			String json = jsonTranslator.toJson(dsMap);
			return json;
		}

	}

}
