package org.nextframework.view.components;

public class InputTagSelectManyPopupComponent extends InputTagSelectComboComponent {

	@Override
	public boolean isIncludeBlank() {
		return false; //select-many-popup never has include blank
	}

}