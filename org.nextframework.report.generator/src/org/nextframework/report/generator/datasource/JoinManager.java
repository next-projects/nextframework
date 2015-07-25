package org.nextframework.report.generator.datasource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class JoinManager {

	private Set<String> joins = new LinkedHashSet<String>();
	private String baseAlias;
	
	public JoinManager(String baseAlias) {
		this.baseAlias = baseAlias;
	}

	public void addJoin(String path){
		int separator = path.lastIndexOf('.');
		if(separator > 0){
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
		int separator = join.lastIndexOf('.');
		if(separator > 0){
			String base = join.substring(0, separator);
			base = baseAlias + "_" + base.replace('.', '_');
			return base + "." + join.substring(separator+1);
		} else {
			return baseAlias + "." + join;
		}
	}

	private String createJoinAlias(String join) {
		int separator = join.lastIndexOf('.');
		if(separator > 0){
			String base = join.substring(0, separator);
			base = baseAlias + "_" + base.replace('.', '_');
			return base + "_" + join.substring(separator+1);
		} else {
			return baseAlias + "_" + join;
		}
	}

	public static void main(String[] args) {
		JoinManager joinManager = new JoinManager("base");
		joinManager.addJoin("x.y.z");
		joinManager.addJoin("x.y.w");
		joinManager.addJoin("x.k");
		joinManager.addJoin("x.y.u");
		System.out.println(joinManager.joins);
		System.out.println(joinManager.getJoinMap());
	}
}
