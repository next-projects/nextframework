package org.nextframework.summary;

import org.springframework.util.StringUtils;

public class SummaryUtils {

	public static String convertCompositeGroupToMethodFormat(String scopeGroup) {
		return convertCompositeGroupToMethodFormat(scopeGroup, false);
	}

	public static String convertCompositeGroupToMethodFormat(String scopeGroup, boolean ignoreSummaryName) {
		// the group can be configured in the format foo.bar
		// however composite groups are compared with foo_Bar in the inner workings
		if (scopeGroup.indexOf('.') > 0) {
			String[] parts = scopeGroup.split("\\.");
			StringBuilder builder = new StringBuilder();
			builder.append(parts[0]);
			int beginFrom = 1; /*1!*/
			if (parts[0].equals("summary") && ignoreSummaryName) {
				builder.append(".").append(parts[1]);
				beginFrom = 2;
			}
			for (int i = beginFrom; i < parts.length; i++) {
				String part = parts[i];

				builder.append("_");
				builder.append(StringUtils.capitalize(part));
			}
			return builder.toString();
		}
		return scopeGroup;

	}

}
