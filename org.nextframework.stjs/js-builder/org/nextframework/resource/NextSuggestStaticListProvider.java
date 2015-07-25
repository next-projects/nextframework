package org.nextframework.resource;

import org.nextframework.resource.NextSuggest.SuggestElement;
import org.stjs.javascript.Array;
import org.stjs.javascript.JSCollections;


public class NextSuggestStaticListProvider extends NextSuggestSuggestionProvider {
	
	private Array<SuggestItem> items;

	public NextSuggestStaticListProvider(Array<SuggestItem> items){
		this.items = items;
	}

	@Override
	public void requestSuggestions(SuggestElement suggestElement) {
		String queryText = suggestElement.getQueryText();
		Array<SuggestItem> suggestions = JSCollections.$array();
		for (String i : items) {
			SuggestItem item = items.$get(i);
			if(suggestElement.itemMatcher.match(queryText, item)){
				suggestions.push(item);
			}
		}
		suggestElement.suggest(suggestions);
	}

}
