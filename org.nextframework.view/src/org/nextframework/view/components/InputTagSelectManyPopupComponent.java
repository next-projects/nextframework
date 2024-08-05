package org.nextframework.view.components;

import org.nextframework.util.Util;

public class InputTagSelectManyPopupComponent extends InputTagSelectComboComponent {

	private String onRenderItemString;

	@Override
	public void prepare() {

		super.prepare();

		String onRenderItem = (String) inputTag.getDAAtribute("onrenderitem", true);
		if (Util.strings.isNotEmpty(onRenderItem)) {
			this.onRenderItemString = onRenderItem;
		}

	}

	@Override
	public boolean isIncludeBlank() {
		return false; //select-many-popup never has include blank
	}

	public String getOnRenderItemString() {
		return onRenderItemString;
	}

}