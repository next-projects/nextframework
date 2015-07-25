package org.nextframework.view.components;


public class InputTagSelectOneRadioComponent extends InputTagSelectComboComponent {

	protected String createOption(String value, String label, String selected) {
		selected = selected != null && selected.length() > 0 ? "checked=\"checked\"" : "";
		Object itemStyle = inputTag.getDAAtribute("itemStyle", false);
		return "<span style="+itemStyle+"><input type='radio' class='radioClass' value='" + (value != null ? value : "<null>") + "' name='" + inputTag.getName() + "' " + selected + " "+inputTag.getDynamicAttributesToString()+"/>" + label + "</span>";
	}
}
