package org.nextframework.report.generator.datasource;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

class JoinManager {

	private static final char SEP = '.';
	private static final char SEP2 = '_';

	private Set<String> joins = new LinkedHashSet<String>();
	private String baseAlias;

	public JoinManager(String baseAlias) {
		this.baseAlias = baseAlias;
	}

	public void addJoin(String path) {
		int separator = path.lastIndexOf(SEP);
		if (separator > 0) {
			addJoin(path.substring(0, separator));
			joins.add(path);
		} else {
			joins.add(path);
		}
	}

	public Map<String, String> getJoinMap() {
		Map<String, String> map = new LinkedHashMap<String, String>();
		for (String join : joins) {
			map.put(createJoinPath(join), createJoinAlias(join));
		}
		return map;
	}

	private String createJoinPath(String join) {
		int separator = join.lastIndexOf(SEP);
		if (separator > 0) {
			String base = join.substring(0, separator);
			base = baseAlias + SEP2 + base.replace(SEP, SEP2);
			return base + SEP + join.substring(separator + 1);
		} else {
			return baseAlias + SEP + join;
		}
	}

	private String createJoinAlias(String join) {
		int separator = join.lastIndexOf(SEP);
		if (separator > 0) {
			String base = join.substring(0, separator);
			base = baseAlias + SEP2 + base.replace(SEP, SEP2);
			return base + SEP2 + join.substring(separator + 1);
		} else {
			return baseAlias + SEP2 + join;
		}
	}

	public String applyJoin(String selectProperty) {
		return createJoinPath(selectProperty);
	}

}