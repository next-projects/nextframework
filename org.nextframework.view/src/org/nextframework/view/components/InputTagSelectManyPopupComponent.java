package org.nextframework.view.components;




public class InputTagSelectManyPopupComponent  extends InputTagSelectComboComponent {
	
	public String getStyleString(){
		String daAtribute = (String) inputTag.getDAAtribute("popupstyle", true);
		daAtribute = "width: 920px; "+daAtribute ;
		return daAtribute;
	}
	
	@Override
	public boolean isIncludeBlank() {
		return false; //select-many-popup never has include blank
	}

//	private String avaiableValues;
//	private String selectedValues;
//	private String inputValues;
//	
//	public String getInputValues() {
//		return inputValues;
//	}
//	
//	public String getAvaiableValues() {
//		return avaiableValues;
//	}
//	
//	public String getSelectedValues() {
//		return selectedValues;
//	}
//
//	@Override
//	public void prepare() {
//		super.prepare();
//		
//		inputTag.setIncludeBlank(false);
//	}
	
//	@Override
//	protected void prepareItems(InputTag lastInput) {
//		Object itemsValue = getItemsValue(lastInput);
//		
//		if(!(itemsValue instanceof List<?>)){
//			throw new NextException("For the type select-many-popup the itens must be a List");
//		}
//		
//		List<?> items = (List<?>) itemsValue;
//		
//		List<Object> values = new ArrayList<Object>();
//		
//		for (Iterator<?> iterator = items.iterator(); iterator.hasNext();) {
//			Object object = (Object) iterator.next();
//			if(isValueSelected(object)){
//				values.add(object);
//				iterator.remove();
//			}
//		}
//		
//		inputValues = "";
//		int i = 0;
//		for (Object object : values) {
//			inputValues += "<input type='text' name='"+inputTag.getName()+"["+i+"]' value=\""+getSelectVaue(object)+"\"/>";
//			i++;
//		}
//		
//		this.avaiableValues = toString(organizeItens(inputTag, null, items));
//		
//		this.selectedValues = toString(organizeItens(inputTag, null, values));
//	}
	
//	@Override
//	protected String createOption(String value, String label, String selected) {
//		return super.createOption(value, label, "");
//	}
}
