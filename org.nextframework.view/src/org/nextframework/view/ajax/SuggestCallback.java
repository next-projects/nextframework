package org.nextframework.view.ajax;

import java.util.List;

public interface SuggestCallback {

	List<?> suggest(SuggestContext context, String text);
}
