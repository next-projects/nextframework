package org.nextframework.view.components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.nextframework.exception.NextException;
import org.nextframework.view.InputTag;

public class InputTagSelectManyBoxComponent extends InputTagSelectComboComponent {

	private String avaiableValues;
	private String selectedValues;
	private String inputValues;
	private Integer inputWidth = 160;
	
	public String getInputValues() {
		return inputValues;
	}
	
	public String getAvaiableValues() {
		return avaiableValues;
	}
	
	public String getSelectedValues() {
		return selectedValues;
	}

	public Integer getInputWidth() {
		return inputWidth;
	}
	
	@Override
	public void prepare() {
		super.prepare();
		
		inputTag.setIncludeBlank(false);
		
		Object widthParam = (String) inputTag.getDAAtribute("inputwidth", true);
		try {
			this.inputWidth = new Integer(widthParam.toString());
		} catch (Exception e){
			//if the property is wrong.. don't do anything
		}
	}
	
	@Override
	protected void prepareItems(InputTag lastInput) {
		Object itemsValue = getItemsValue(lastInput);
		
		if(!(itemsValue instanceof List<?>) && itemsValue != null){
			throw new NextException("For the type select-many-box the itens must be a List");
		}
		
		List<?> items = (List<?>) itemsValue;
		
		List<Object> values = new ArrayList<Object>();
		
		for (Iterator<?> iterator = items.iterator(); iterator.hasNext();) {
			Object object = (Object) iterator.next();
			if(isValueSelected(object)){
				values.add(object);
				iterator.remove();
			}
		}
		
		inputValues = "";
		int i = 0;
		for (Object object : values) {
			inputValues += "<input type='hidden' name='"+inputTag.getName()+"["+i+"]' value=\""+getSelectVaue(object)+"\"/>";
			i++;
		}
		
		this.avaiableValues = toString(organizeItens(inputTag, null, items));
		
		this.selectedValues = toString(organizeItens(inputTag, null, values));
	}
	
	@Override
	protected String createOption(String value, String label, String selected) {
		return super.createOption(value, label, "");
	}
}
