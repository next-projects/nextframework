package org.nextframework.view.js;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JavascriptMap implements JavascriptInstance {
	
	static JSBuilderUtils json = new JSBuilderUtils();

	Map<String, Object> map = new LinkedHashMap<String, Object>();
	
	public JavascriptMap(){
	}
	
	public JavascriptMap(Object...props){
		for (int i = 0; i < props.length; i+=2) {
			this.map.put(props[i].toString(), props[i+1]);
		}
	}
	
	public JavascriptMap(Map<String, Object> map){
		this.map = map;
	}
	
	public Object putProperty(String key, Object value) {
		return map.put(key, value);
	}

	public String toJsMap(){
		List<String> properties = new ArrayList<String>();
		Set<String> keySet = map.keySet();
		for (String key : keySet) {
			Object value = map.get(key);
			properties.add(property(key, value));
		}
		String result = "{"+toJsMap(properties.toArray(new String[properties.size()]))+"}";
		return result;
	}
	
	@Override
	public String toString() {
		return toJsMap();
	}
	
	protected String property(String prop, Object value) {
		value = JSBuilderUtils.convertToJavascriptValue(value);
		if(value != null){
			value = value.toString().replace("\n", "\n    ");
		}
		return prop +": "+value;
	}

	protected String toJsMap(String... properties) {
		String result = "";
		boolean inline = properties.length <= 3;
		for (int i = 0; i < properties.length; i++) {
			if(properties[i] != null){
				result += properties[i];
				if(inline){
					result += ", ";
				} else {
					result += ",\n        ";
				}
			}
		}
		if(result.length() == 0){
			return "";
		}
		
		return result.substring(0, result.length()-(inline?2:10));
	}
	
	public static void main(String[] args) {
		JavascriptMap o = new JavascriptMap();
		o.putProperty("olhaQBom", 3);
		o.putProperty("bomEhDois", true);
		o.putProperty("bagaca" , "\n");
		o.putProperty("data" , new Date());
		System.out.println(o.toString());
	}
}
