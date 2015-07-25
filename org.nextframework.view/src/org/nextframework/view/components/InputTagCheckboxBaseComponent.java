package org.nextframework.view.components;

import org.nextframework.util.Util;
import org.nextframework.view.ComboReloadGroupTag;
import org.nextframework.view.FormTag;

public class InputTagCheckboxBaseComponent extends InputTagComponent {

	public String getReloadOnClickString() {
		String onchangestring = "";
		if (Util.booleans.isTrue(inputTag.getReloadOnChange())) {
			FormTag form = inputTag.findParent(FormTag.class, true);
			onchangestring = form.getName() + ".validate = 'false'; " + form.getName() + ".suppressErrors.value = 'true';" + form.getName() + ".suppressValidation.value = 'true';" + form.getSubmitFunction() + "()";
		} else {
			ComboReloadGroupTag comboReloadGroupTag = inputTag.findParent(ComboReloadGroupTag.class);
			if (comboReloadGroupTag != null) {
				onchangestring = comboReloadGroupTag.getFunctionName() + "('" + inputTag.getName() + "');";
			}
		}
		String daOnClick = (String) inputTag.getDAAtribute("onClick", true);
		if (daOnClick != null) {
			onchangestring = daOnClick + ";" + onchangestring;
		}
		return onchangestring;
	}
}
