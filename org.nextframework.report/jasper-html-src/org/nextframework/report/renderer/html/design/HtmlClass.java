package org.nextframework.report.renderer.html.design;

import java.util.Iterator;
import java.util.LinkedHashSet;

public class HtmlClass extends LinkedHashSet<String> {

	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		if (size() > 0) {
			return String.format(" class=\"%s\"", join(iterator(), " "));
		}
		return "";
	}

	private String join(Iterator<String> iterator, String separator) {
		StringBuilder builder = new StringBuilder();
		while (iterator.hasNext()) {
			builder.append(iterator.next());
			if (iterator.hasNext()) {
				builder.append(separator);
			}
		}
		return builder.toString();
	}

}
