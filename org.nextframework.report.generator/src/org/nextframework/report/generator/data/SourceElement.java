package org.nextframework.report.generator.data;


public class SourceElement {

	Class<?> type;
	
	String query;
	
	public SourceElement(Class<?> type){
		this.type = type;
	}
	public SourceElement(){
	}
	public SourceElement(String query){
		this.query = query;
	}

	public SourceElement(Class<?> type, String query) {
		super();
		this.type = type;
		this.query = query;
	}
	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
	public Class<?> getType() {
		return type;
	}
	public void setType(Class<?> type) {
		this.type = type;
	}
}
