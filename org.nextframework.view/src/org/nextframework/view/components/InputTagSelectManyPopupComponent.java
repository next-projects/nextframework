package org.nextframework.view.components;

public class InputTagSelectManyPopupComponent extends InputTagSelectComboComponent {

	public String getStyleString() {
		return (String) inputTag.getDAAtribute("popupstyle", true);
	}

	@Override
	public boolean isIncludeBlank() {
		return false; //select-many-popup never has include blank
	}

}