package org.nextframework.report.renderer.html.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SerialMap {

	List<Integer> values = new ArrayList<Integer>();
	
	Map<Object, Integer> levels = new HashMap<Object, Integer>();
	
	public SerialMap(){
		
	}
	
	public void setLevel(Object o, int i){
		levels.put(o, i);
		while(values.size() < i+1){
			values.add(-1);
		}
		values.set(i, 0);
	}
	
	Integer currentLevel = 0;
	
	public void setCurrentObjectLevel(Object o){
		Integer newLevel = levels.get(o);
		if(newLevel < currentLevel){
			incrementLevel(o);
		}
		currentLevel = newLevel;
	}
	
	public List<Integer> getLevelFor(Object o){
		Integer level = levels.get(o);
		if(level == null){
			throw new NullPointerException("Level not defined for "+o);
		}
		return values.subList(0, level+1);
	}
	
	public void incrementLevel(Object o){
		values.set(levels.get(o), values.get(levels.get(o)) + 1);
	}
	
	public List<Integer> getValues() {
		return values;
	}
	
	@Override
	public String toString() {
		return values.toString();
	}
	
	public static void main(String[] args) {
		SerialMap map = new SerialMap();
		map.setLevel("report", 0);
		map.setLevel("groupA", 0);
		map.setLevel("groupB", 1);
		map.setLevel("groupC", 2);
		map.setLevel("detailHeader", 3);
		map.setLevel("detail", 3);
		
		print(map);
		
		map.incrementLevel("detail");
		
		print(map);
		
		map.incrementLevel("groupC");
		
		print(map);
		
		map.incrementLevel("groupA");
		
		print(map);
	}

	private static void print(SerialMap map) {
		System.out.println("------------------------------");
		System.out.println("report "+map.getLevelFor("report"));
		System.out.println("groupA "+map.getLevelFor("groupA"));
		System.out.println("groupB "+map.getLevelFor("groupB"));
		System.out.println("groupC "+map.getLevelFor("groupC"));
		System.out.println("det..H "+map.getLevelFor("detailHeader"));
		System.out.println("detail "+map.getLevelFor("detail"));
		System.out.println("------------------------------");
	}
}

