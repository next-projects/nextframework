/**
 * 
 */
package org.nextframework.report.renderer.html.design;

import java.util.LinkedHashMap;
import java.util.Set;

@SuppressWarnings("serial")
public class HtmlStyle extends LinkedHashMap<String, Object> {

	@Override
	public String toString() {
		if (size() > 0) {
			String result = " style=\"";
			Set<String> keySet = this.keySet();
			for (String key : keySet) {
				result += key + ":" + this.get(key) + ";";
			}
			result += "\"";
			return result;
		}
		return "";
	}

}