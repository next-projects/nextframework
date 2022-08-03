package org.nextframework.view.components;

public class InputTagSelectManyPopupComponent extends InputTagSelectComboComponent {

	public String getStyleString() {
		String daAtribute = (String) inputTag.getDAAtribute("popupstyle", true);
		daAtribute = "width: 920px;" + (daAtribute != null ? " " + daAtribute : "");
		return daAtribute;
	}

	@Override
	public boolean isIncludeBlank() {
		return false; //select-many-popup never has include blank
	}

}