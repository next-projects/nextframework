package org.nextframework.report.generator.mvc.resource;

import static org.nextframework.js.NextGlobalJs.next;

import org.nextframework.js.NextGlobalJs;
import org.stjs.javascript.Map;
import org.stjs.javascript.dom.DOMEvent;
import org.stjs.javascript.dom.Option;
import org.stjs.javascript.dom.Select;
import org.stjs.javascript.functions.Callback1;

public class ReportGeneratorSelectManyBoxView implements SelectView {
	
	private Select from;
	private Select to;

	String usePropertyAsLabel;
	
	Callback1<Option> onselect;
	
	Callback1<Option> onselectto;
	
	public ReportGeneratorSelectManyBoxView(Select inputFrom){
		String name = inputFrom.name.substring(0, inputFrom.name.length() - 6);
		this.from = inputFrom;
		this.to    = (Select) next.dom.toElement(name + "_to___");
		
		final ReportGeneratorSelectManyBoxView bigThis = this;
		
		next.events.attachEvent(this.from, "click", new Callback1<DOMEvent>() {
			@Override
			public void $invoke(DOMEvent p2) {
				if(bigThis.onselect != null && bigThis.from.selectedIndex >= 0){
					bigThis.onselect.$invoke(bigThis.from.options.item(bigThis.from.selectedIndex));
				}
			}
		});
		next.events.attachEvent(this.to, "click", new Callback1<DOMEvent>() {
			@Override
			public void $invoke(DOMEvent p2) {
				if(bigThis.onselect != null && bigThis.to.selectedIndex >= 0){
					bigThis.onselect.$invoke(bigThis.to.options.item(bigThis.to.selectedIndex));
				}
				if(bigThis.onselectto != null && bigThis.to.selectedIndex >= 0){
					bigThis.onselectto.$invoke(bigThis.to.options.item(bigThis.to.selectedIndex));
				}
			}
		});
		
	}
	
	public void setUsePropertyAsLabel(String usePropertyAsLabel) {
		this.usePropertyAsLabel = usePropertyAsLabel;
	}
	
	@Override
	public void select(String name, Map<String, Object> properties) {
		for(int i = 0; i < from.options.length; i++){
			if(from.options.item(i).value.equals(name)){
				from.selectedIndex = i;
				NextGlobalJs.selectManyBoxAdd(from);
				break;
			}
		}
	}
	
	public void markSelected(String name) {
		for(int i = 0; i < to.options.length; i++){
			if(to.options.$get(i).value.equals(name)){
				to.selectedIndex = i;
				break;
			}
		}
	}

	public void add(String name, Map<String, Object> properties) {
		String label = name;
		if(usePropertyAsLabel != null){
			label = properties.$get(usePropertyAsLabel) != null? properties.$get(usePropertyAsLabel).toString() : name;
		}
		NextGlobalJs.selectManyAddOption(from, label, name, properties);
	}

	@Override
	public void unselect(String name) {
		for(int i = 0; i < to.options.length; i++){
			if(to.options.$get(i).value.equals(name)){
				to.selectedIndex = i;
				break;
			}
		}
		NextGlobalJs.selectManyBoxRemove(from);
	}
	
	public Select getFrom() {
		return from;
	}
	
	public Select getTo() {
		return to;
	}

	@Override
	public void blur() {
		from.selectedIndex = -1;
		to.selectedIndex = -1;
	}

	public void clearAll() {
		clearFrom();
		clearTo();
	}

	public void clearTo() {
		clear(to);
	}

	public void clearFrom() {
		clear(from);
	}

	private void clear(Select select) {
		while(select.options.length > 0){
			select.options.remove(0);
		}
	}

}
