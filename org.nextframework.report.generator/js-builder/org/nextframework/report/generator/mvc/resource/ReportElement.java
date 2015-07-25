package org.nextframework.report.generator.mvc.resource;

import static org.nextframework.js.NextGlobalJs.next;

import org.stjs.javascript.Array;
import org.stjs.javascript.JSCollections;
import org.stjs.javascript.Map;
import org.stjs.javascript.dom.Element;
import org.stjs.javascript.dom.TableCell;
import org.stjs.javascript.functions.Callback0;

public class ReportElement {

	String name;
	ReportRow row;
	ReportColumn column;
	LayoutItem layoutItem;
	private Element node;
	
	Callback0 onFocus;
	
	public ReportElement(String name){
		this.name = name;
	}
	
	public void setNode(Element node) {
		this.node = node;
	}
	
	public Element getNode() {
		return node;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public TableCell getCell(){
		return row.getTdForColumn(column);
	}
}

class LabelReportElement extends ReportElement {

	String label;
	boolean changed;

	public LabelReportElement(String name, Map<String, Object> properties) {
		super(name);
		this.label = properties.$get("displayName") != null? properties.$get("displayName").toString() : name;
	}
	
	@Override
	public String toString() {
		return label;
	}
	
}
class FieldReportElement extends ReportElement {
	
	String pattern;
	
	public FieldReportElement(String name, Map<String, Object> value) {
		super(name);
	}
	
	@Override
	public String toString() {
		return "$"+name;
	}
}

///////////////////////////LAYOUT

abstract class LayoutItem {
	
	ReportLayoutManager layoutManager;

	public LayoutItem(ReportLayoutManager layoutManager) {
		this.layoutManager = layoutManager;
	}
	
	public abstract Array<ReportElement> getElements();
	
	public abstract void clearElements();
}

class FieldDetail extends LayoutItem {
	
	String name;

	LabelReportElement label;
	FieldReportElement field;

	private Map<String, Object> options;
	
	private boolean aggregate;
	
	String aggregateType;
	
	public FieldDetail(ReportLayoutManager layoutManager, String name, LabelReportElement label, FieldReportElement field, Map<String, Object> options) {
		super(layoutManager);
		this.name = name;
		this.label = label;
		this.field = field;
		this.options = options;
	}
	
	public boolean isAggregate() {
		if(label == null || label.column == null || label.column.getIndex() == 0){
			return false;
		}
		return aggregate;
	}

	public void setAggregate(boolean aggregate) {
		this.aggregate = aggregate;
	}

	public boolean isDate(){
		return ReportPropertyConfigUtils.isDate(options);
	}
	
	public boolean isNumber(){
		return ReportPropertyConfigUtils.isNumber(options);
	}

	public boolean isAggregatable(){
		return ReportPropertyConfigUtils.isAggregatable(options);
	}

	@Override
	public String toString() {
		String result = "<fieldDetail name='"+name+"'";
		if(field != null && field.pattern != null && !field.pattern.equals("") ){
			String pattern = field.pattern;
			//pattern = next.util.escapeSingleQuotes(pattern);
			result += " pattern=\""+pattern+"\"";
		}
		if(label != null && label.name != null && !label.name.equals("") && label.changed){
			result += " label='"+next.util.escapeSingleQuotes(label.label)+"'";
		}
		if(isAggregate()){
			result += " aggregate='true'";
			if(aggregateType != null && aggregateType.length() > 0){
				result += " aggregateType='"+aggregateType+"'";
			}
		}
		result += "/>";
		return result;
	}

	@Override
	public Array<ReportElement> getElements() {
		Array<ReportElement> $array = JSCollections.$array();
		if(label != null){
			$array.push(label);
		}
		if(field != null){
			$array.push(field);
		}
		return $array;
	}

	@Override
	public void clearElements() {
		label = null;
		field = null;
	}
}