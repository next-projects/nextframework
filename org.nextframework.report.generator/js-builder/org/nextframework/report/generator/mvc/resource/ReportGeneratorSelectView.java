package org.nextframework.report.generator.mvc.resource;

import static org.nextframework.js.NextGlobalJs.next;
import static org.stjs.javascript.Global.alert;
import static org.stjs.javascript.Global.window;
import static org.stjs.javascript.JSCollections.$array;

import org.stjs.javascript.Array;
import org.stjs.javascript.Map;
import org.stjs.javascript.dom.DOMEvent;
import org.stjs.javascript.dom.Div;
import org.stjs.javascript.functions.Callback1;
import org.stjs.javascript.functions.Function1;

@Deprecated
public class ReportGeneratorSelectView implements SelectView {
	
	Div viewDiv;
	Array<ReportGeneratorSelectViewItem> items;
	ReportGeneratorSelectViewItem selectedItem;
	
	ReportGeneratorSelectViewEvent onDblClick;
	
	Callback1<ReportGeneratorSelectViewItem> onselect;
	
	String usePropertyAsLabel;
	
	public ReportGeneratorSelectView(Div viewDiv){
		items = $array();
		this.viewDiv = viewDiv;
	}
	
	public void setUsePropertyAsLabel(String usePropertyAsLabel) {
		this.usePropertyAsLabel = usePropertyAsLabel;
	}
	
	public String getUsePropertyAsLabel() {
		return usePropertyAsLabel;
	}
	
	public void selectItem(ReportGeneratorSelectViewItem item){
		if(onselect != null){
			onselect.$invoke(selectedItem);
		}
		if(!item.selectView.equals(this)){
			alert("Erro: o item não é do tipo do select view");
			return;
		}
		unselectSelectedItem();
		if(item.equals(selectedItem)){
			selectedItem = null;
			return;
		}
		selectedItem = item;
		selectedItem.selectItem();
	}

	private void unselectSelectedItem() {
		if(selectedItem != null){
			selectedItem.unselect();
		}
	}
	
	public void blur(){
		unselectSelectedItem();
		selectedItem = null;
	}
	
	public void select(String name, Map<String, Object> value){
		Div div = (Div) window.document.createElement("DIV");
		div.style.padding = "1px";
		div.innerHTML = name;
		if(usePropertyAsLabel != null){
			div.innerHTML = value.$get(usePropertyAsLabel) != null? value.$get(usePropertyAsLabel).toString() : name;
		}
		
		viewDiv.appendChild(div);

		items.push(new ReportGeneratorSelectViewItem(this, new SimpleNamedObject(name, value), div));
	}

	public void unselect(String name) {
		int i = 0;
		for(String key: items){
			ReportGeneratorSelectViewItem el = items.$get(key);
			if(el.name.equals(name)){
				if(el.equals(selectedItem)){
					selectedItem = null;
				}
				Div div = el.div;
				div.parentNode.removeChild(div);
				items.splice(i, 1);
				break;
			}
			i++;
		}
	}

}

@Deprecated
interface ReportGeneratorSelectViewEvent {
	
	void invoke(ReportGeneratorSelectViewItem item);
}

@Deprecated
class ReportGeneratorSelectViewItem {

	ReportGeneratorSelectView selectView;
	String name;
	Div div;
	Map<String, Object> value;
	
	public ReportGeneratorSelectViewItem(ReportGeneratorSelectView selectView, SimpleNamedObject obj, Div div) {
		this.selectView = selectView;
		this.name = obj.name;
		this.value = obj.value;
		this.div = div;
		final ReportGeneratorSelectViewItem bigThis = this;
		this.div.onclick = new Function1<DOMEvent, Boolean>() {
			
			@Override
			public Boolean $invoke(DOMEvent p1) {
				bigThis.selectView.selectItem(bigThis);
				return true;
			}
		};
		this.div.ondblclick = new Function1<DOMEvent, Boolean>() {

			@Override
			public Boolean $invoke(DOMEvent p1) {
				bigThis.selectView.selectItem(bigThis);
				if(bigThis.selectView.onDblClick != null){
					bigThis.selectView.onDblClick.invoke(bigThis);
				}
				return true;
			}
		};
	}

	public void selectItem(){
		next.style.addClass(div, "selected");
		div.style.padding = "0px";
	}

	public void unselect() {
		next.style.removeClass(div, "selected");
		div.style.padding = "1px";
	}
}

class SimpleNamedObject {

	String name;
	Map<String, Object> value;
	
	public SimpleNamedObject(String name, Map<String, Object>  value) {
		this.name = name;
		this.value = value;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
