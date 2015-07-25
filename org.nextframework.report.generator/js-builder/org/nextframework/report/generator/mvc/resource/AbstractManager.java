package org.nextframework.report.generator.mvc.resource;

import static org.nextframework.js.NextGlobalJs.next;
import static org.stjs.javascript.JSCollections.$array;

import org.stjs.javascript.Array;
import org.stjs.javascript.Global;
import org.stjs.javascript.Map;
import org.stjs.javascript.dom.DOMEvent;
import org.stjs.javascript.dom.Option;
import org.stjs.javascript.dom.Select;
import org.stjs.javascript.functions.Callback0;
import org.stjs.javascript.functions.Callback1;
import org.stjs.javascript.functions.Function1;

public abstract class AbstractManager {

	ReportDesigner designer;
	
	Array<SimpleNamedObject> objects;

	SelectView attachedView;
	
	@SuppressWarnings("unchecked")
	public AbstractManager(ReportDesigner designer, SelectView attachedView) {
		this.designer = designer;
		this.attachedView = attachedView;
		
		objects = $array();
		
		final AbstractManager bigThis = this;
		
		if(attachedView instanceof ReportGeneratorSelectManyBoxView){
			final ReportGeneratorSelectManyBoxView selectManyAttachedView = (ReportGeneratorSelectManyBoxView) attachedView;
			
			((Map<String, Object>)selectManyAttachedView.getFrom()).$put("onAdd", new Callback1<Option>() {
				@Override
				public void $invoke(Option p1) {
					Map<String, Object> properties = (Map<String, Object>) ((Map<String, Object>)p1).$get("properties");
					if(bigThis.getMaximumItems() > 0 && selectManyAttachedView.getTo().options.length > bigThis.getMaximumItems()){
						Global.alert("Numero maximo de itens atingido");
						selectManyAttachedView.unselect(p1.value);
					} else {
						bigThis.addElement(p1.value, properties);
					}
				}
			});
			
			((Map<String, Object>)selectManyAttachedView.getFrom()).$put("onRemove", new Callback1<Option>() {
				@Override
				public void $invoke(Option p1) {
					@SuppressWarnings("unused")
					Map<String, Object> properties = (Map<String, Object>) ((Map<String, Object>)p1).$get("properties");
					bigThis.remove(p1.value);
				}
			});
		}			
	}
	
	public void selectElement(String name, String pattern){
		((ReportGeneratorSelectManyBoxView)this.attachedView).select(name, null);//to select in the selectmanybox the properties are not necessary
	}	
	
	public boolean contains(String name) {
		for (int i = 0; i < objects.$length(); i++) {
			if(objects.$get(i).name.equals(name)){
				return true;
			}
		}
		return false;
	}
	
	public SimpleNamedObject getByName(String name) {
		for(String key: objects){
			SimpleNamedObject el = objects.$get(key);
			if(el.name.equals(name)){
				return el;
			}
		}
		return null;
	}
	
	public void remove(String name) {
		attachedView.unselect(name);
		try {
			int i = 0;
			for(String key: objects){
				SimpleNamedObject el = objects.$get(key);
				if(el.name.equals(name)){
					objects.splice(i, 1);
					onRemove(name);
					return;
				}
				i++;
			}
		} finally {
			designer.writeXml();
		}
	}

	protected abstract void onRemove(String name);
	
	protected abstract int getMaximumItems();
	
	
	public void addElement(String name, Map<String, Object> properties){
		if(!accept(name, properties)){
			return;
		}
		objects.push(new SimpleNamedObject(name, properties));
		
		attachedView.select(name, properties);
		
		onAdd(name, properties);
		
		designer.writeXml();
	}

	protected abstract void onAdd(String name, Map<String, Object> properties);

	protected abstract boolean accept(String name, Map<String, Object> properties);
}


class ReportGroupManager extends AbstractManager {

	public ReportGroupManager(ReportDesigner designer, ReportGeneratorSelectManyBoxView attachedView) {
		super(designer, attachedView);
		
		final ReportGroupManager bigThis = this;
		
		attachedView.onselectto = new Callback1<Option>() {
			@Override
			public void $invoke(Option p1) {
				if(p1 != null){
					bigThis.selectElementByName(p1.value, true);
				}
			}
		};
	}
	
	@Override
	public void selectElement(String name, String pattern) {
		super.selectElement(name, pattern);
		
		getElement(getElementByName(name)).pattern = pattern;
	}
	
	private FieldReportElement getElement(SimpleNamedObject namedObject){
		return (FieldReportElement) namedObject.value.$get("element");
	}
	
	private SimpleNamedObject getElementByName(String name){
		for(String key: objects){
			SimpleNamedObject sno = objects.$get(key);
			if(sno.name.equals(name)){
				return sno;
			}
		}
		return null;
	}

	public void selectElementByName(String groupName, boolean cascadeToDefinition) {
		select(getElementByName(groupName), cascadeToDefinition);
	}
	
	public void select(final SimpleNamedObject sno, boolean cascadeToDefinition) {
		if(sno == null){
			return;
		}
		
		if(cascadeToDefinition){
			designer.definition.selectItem(getElement(sno), false);
		}
		
		Global.eval("showdesignerTab('designerTab_2', 2, 'designerTab_link_2'); ");
		
		((ReportGeneratorSelectManyBoxView)attachedView).markSelected(sno.name);
		
		if(ReportPropertyConfigUtils.isDate(sno.value)){
			ReportPropertyConfigUtils.configurePatternInputToField((FieldReportElement) getElement(sno), designer.patternDateInputGroup);
		}
	}

	protected void onRemove(String name) {
		designer.definition.removeGroup(name);
	}
	
	protected void onAdd(final String groupName, Map<String, Object> properties) {
		ReportGroup reportGroup = designer.definition.addGroup(groupName);
		
		if(designer.definition.columns.$length() == 0){
			designer.definition.addColumn();
		}
		
		FieldReportElement element = new FieldReportElement(groupName, properties);
		properties.$put("element", element);
		final ReportGroupManager bigThis = this;
		element.onFocus = new Callback0() {
			@Override
			public void $invoke() {
				bigThis.selectElementByName(groupName, false);
			}
		};
		designer.definition.addElement(element, reportGroup.groupHeader, 0);
	}
	
	@Override
	public String toString() {
		String value = "        <groups>\n";
		for (String key : objects) {
			SimpleNamedObject el = objects.$get(key);
			String pattern = getElement(el).pattern;
			if(pattern == null || pattern.equals("")){
				value += "            <group name='"+el.name+"'/>\n";
			} else {
				value += "            <group name='"+el.name+"' pattern=\""+pattern+"\"/>\n";
			}
		}
		value += "        </groups>\n";
		return value;
	}
	
	protected boolean accept(String name, Map<String, Object> properties) {
		return ReportPropertyConfigUtils.isGroupable(properties);
	}

	@Override
	protected int getMaximumItems() {
		return 4;
	}
}
class ReportFilterManager extends AbstractManager {
	
	public ReportFilterManager(ReportDesigner designer, ReportGeneratorSelectManyBoxView attachedView) {
		super(designer, attachedView);
		
		final ReportFilterManager bigThis = this;
		
		attachedView.onselectto = new Callback1<Option>() {
			@Override
			public void $invoke(Option p1) {
				if(p1 != null){
					bigThis.onSelectElement(p1.value);
				}
			}
		};
	}

	protected void onSelectElement(final String value) {
		final ReportFilterManager bigThis = this;
		Map<String, Object> properties = getByName(value).value;
		String filterLabel = ReportPropertyConfigUtils.getFilterDisplayName(properties);
		designer.showFilterLabel();
		designer.filterLabel.value = filterLabel;
		designer.filterLabel.onchange = new Function1<DOMEvent, Boolean>() {
			public Boolean $invoke(DOMEvent p1) {
				ReportPropertyConfigUtils.setFilterDisplayName(
						bigThis.getByName(value).value, 
						bigThis.designer.filterLabel.value);
				
				bigThis.designer.writeXml();
				return true;
			}
		};
		if(ReportPropertyConfigUtils.isDate(properties)) {
			designer.showFilterPreSelectDate();
			configureFilterPreSelectDateCombo(properties);
		} else {
			designer.hideFilterPreSelectDate();
		}
		if(ReportPropertyConfigUtils.isEntity(properties)) {
			designer.showFilterPreSelectEntity();
			configureFilterPreSelectEntityCombo(properties);
		} else {
			designer.hideFilterPreSelectEntity();
		}
		
		if(ReportPropertyConfigUtils.isEntity(properties)) {
			designer.showFilterSelectMultiple();
 		} else {
 			designer.hideFilterSelectMultiple();
 		}
		if(!ReportPropertyConfigUtils.isFilterRequired(properties)){
		} else {
			designer.hideFilterRequired();
		}
		designer.showFilterRequired();

		designer.filterRequired.checked = ReportPropertyConfigUtils.isFilterRequired(properties);
		designer.filterRequired.onchange = new Function1<DOMEvent, Boolean>() {
			public Boolean $invoke(DOMEvent p1) {
				ReportPropertyConfigUtils.setFilterRequired(
						bigThis.getByName(value).value, 
						bigThis.designer.filterRequired.checked);
				
				bigThis.designer.writeXml();
				return true;
			}
		};
		
		designer.filterSelectMultiple.checked = ReportPropertyConfigUtils.isFilterSelectMultiple(properties);
		designer.filterSelectMultiple.onchange = new Function1<DOMEvent, Boolean>() {
			public Boolean $invoke(DOMEvent p1) {
				ReportPropertyConfigUtils.setFilterSelectMultiple(
						bigThis.getByName(value).value, 
						bigThis.designer.filterSelectMultiple.checked);
				
				bigThis.designer.writeXml();
				return true;
			}
		};
		
	}

	public void configureFilterPreSelectDateCombo(final Map<String, Object> properties) {
		final ReportFilterManager bigThis = this;
		designer.filterPreSelectDate.selectedIndex = 0;
		next.dom.setSelectedValue(designer.filterPreSelectDate, ReportPropertyConfigUtils.getFilterPreSelectDate(properties));
		designer.filterPreSelectDate.onchange = new Function1<DOMEvent, Boolean>() {
			public Boolean $invoke(DOMEvent p1) {
				String selectedValue = next.dom.getSelectedValue(bigThis.designer.filterPreSelectDate);
				ReportPropertyConfigUtils.setFilterPreSelectDate(
						properties, 
						selectedValue);
				
				bigThis.designer.writeXml();
				return true;
			}
		};		
	}

	private void configureFilterPreSelectEntityCombo(final Map<String, Object> properties) {
		final ReportFilterManager bigThis = this;
//		this.designer.filterPreSelectEntity
		//do ajax
		String type = ReportPropertyConfigUtils.getType(properties);
		final String entityValue = ReportPropertyConfigUtils.getFilterPreSelectEntity(properties);
		//boolean multiple = ReportPropertyConfigUtils.isFilterSelectMultiple(properties);
		String path = this.designer.controllerPath;
		designer.filterPreSelectEntity.options.length = 0;
		designer.filterPreSelectEntity.add(new Option("", "<null>"));
		
		next.ajax.newRequest()
			.setUrl(path)
			.setAction("getFilterList")
			.setParameter("type", type)
			.setCallback(new Callback1<Array<Array<String>>>() {
				public void $invoke(Array<Array<String>> p1) {
					for (String k : p1) {
						Array<String> item = p1.$get(k);
						String id = item.$get(0);
						String value = item.$get(1);
						bigThis.designer.filterPreSelectEntity.add(new Option(value, id));
					}
					next.dom.setSelectedValue(bigThis.designer.filterPreSelectEntity, entityValue);
					if(entityValue == null){
						ReportPropertyConfigUtils.setFilterPreSelectEntity(properties, null);
					}
				}
			}).send();
		
		designer.filterPreSelectEntity.onchange = new Function1<DOMEvent, Boolean>() {
			public Boolean $invoke(DOMEvent p1) {
				String selectedValue = next.dom.getSelectedValue(bigThis.designer.filterPreSelectEntity);
				ReportPropertyConfigUtils.setFilterPreSelectEntity(
						properties, 
						selectedValue);
				
				bigThis.designer.writeXml();
				return true;
			}
		};
	}

	@Override
	protected boolean accept(String name, Map<String, Object> properties) {
		return (!ReportPropertyConfigUtils.isTransient(properties) ||
				ReportPropertyConfigUtils.isFilterable(properties)) &&
				!ReportPropertyConfigUtils.isExtended(properties) &&
				!ReportPropertyConfigUtils.isNumber(properties);
	}

	@Override
	protected void onAdd(String name, Map<String, Object> properties) {
	}

	@Override
	protected void onRemove(String name) {
	}	
	
	@Override
	public String toString() {
		String value = "        <filters>\n";
		for (String key : objects) {
			SimpleNamedObject el = objects.$get(key);
			String fdn = ReportPropertyConfigUtils.getFilterDisplayName(el.value);
			String fpd = ReportPropertyConfigUtils.getFilterPreSelectDate(el.value);
			String fpe = ReportPropertyConfigUtils.getFilterPreSelectEntity(el.value);
			String dn = ReportPropertyConfigUtils.getDisplayName(el.value);
			value += "            <filter name='"+el.name+"'";
			if(fdn != dn){
				value += " filterDisplayName='"+fdn+"'";
			}
			if(fpd != null){
				value += " preSelectDate='"+fpd+"'";
			}
			if(fpe != null){
				value += " preSelectEntity='"+fpe+"'";
			}
			if(ReportPropertyConfigUtils.isFilterSelectMultiple(el.value)){
				value += " filterSelectMultiple='true'";
			}
			if(ReportPropertyConfigUtils.isFilterRequired(el.value)){
				value += " requiredFilter='true'";
			}
			value += "/>\n";
		}
		value += "        </filters>\n";
		return value;
	}

	@Override
	protected int getMaximumItems() {
		return 0;
	}
}

class ReportCalculatedFieldsManager {
	
	private ReportDesigner designer;
	
	Array<SimpleNamedObject> objects;

	private Select view;
	
	public ReportCalculatedFieldsManager(ReportDesigner designer, Select view){
		this.designer = designer;
		this.view = view;
		
		objects = $array();
	}
	
	@Override
	public String toString() {
		String value = "        <calculatedFields>\n";
		for (String key : objects) {
			SimpleNamedObject el = objects.$get(key);
			Object exp = el.value.$get("expression");
			Object display = el.value.$get("displayName");
			Object formatAs = el.value.$get("formatAs");
			Object formatTimeDetail = el.value.$get("formatTimeDetail");
			Object formatWithDecimal = el.value.$get("formatWithDecimal");
			Object processors = el.value.$get("processors");
			
			value += "            <calculatedField " +
					"name='"+el.name+"' " +
					"expression='"+exp+"' " +
					"displayName='"+display+"' " +
					"formatAs='"+formatAs+"' " +
					"formatTimeDetail='"+formatTimeDetail+"' " +
					"formatWithDecimal='"+formatWithDecimal+"' " +
					"processors='"+processors+"' " +
					"/>\n";
		}
		value += "        </calculatedFields>\n";
		return value;
	}

	public void add(String name, Map<String, Object> calculationProperties) {
		boolean editing = getByName(name) != null;
		if(editing){
			remove(name);
		}
		this.objects.push(new SimpleNamedObject(name, calculationProperties));

		if(!editing){
			this.view.add(new Option((String)calculationProperties.$get("displayName"), name));
		}
		
		designer.writeXml();
	}
	
	public void remove(String name){
		int i = 0;
		for(String key: objects){
			SimpleNamedObject el = objects.$get(key);
			if(el.name.equals(name)){
				objects.splice(i, 1);
				return;
			}
			i++;
		}
	}

	public SimpleNamedObject getByName(String name) {
		for(String key: objects){
			SimpleNamedObject el = objects.$get(key);
			if(el.name.equals(name)){
				return el;
			}
		}
		return null;
	}
}
