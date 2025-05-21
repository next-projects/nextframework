package org.nextframework.resource;

import static org.nextframework.js.NextGlobalJs.next;

import org.nextframework.js.ajax.AjaxRequest;
import org.nextframework.resource.NextSuggest.SuggestElement;
import org.stjs.javascript.Array;
import org.stjs.javascript.functions.Callback1;

public class NextSuggestAjaxProvider extends NextSuggestSuggestionProvider {
	
	private int serverId;
	private String serverUrl;

	public NextSuggestAjaxProvider(int serverId, String serverUrl){
		this.serverId = serverId;
		this.serverUrl = serverUrl;
	}

	@Override
	public void requestSuggestions(final SuggestElement suggestElement) {
		AjaxRequest request = next.ajax.newRequest();
		request.setUrl(serverUrl);
		request.setParameter("serverId", serverId);
		request.setParameter("_text", suggestElement.getQueryText());
		request.setAppendContext(false);
		request.setCallback(new Callback1<Array<SuggestItem>>() {
			public void $invoke(Array<SuggestItem> p1) {
				suggestElement.suggest(p1);
			}
		});
		request.send();
	}

}
