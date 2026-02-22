package org.nextframework.resource;

import org.nextframework.resource.NextSuggest.SuggestElement;
import org.stjs.javascript.annotation.SyntheticType;

public abstract class NextSuggestSuggestionProvider {

	@SyntheticType
	class SuggestItem {

		String _t;
		String _v;

	}

	public abstract void requestSuggestions(SuggestElement suggestElement);

}
