package org.nextframework.view.components;

import org.nextframework.util.Util;

public class InputTagSelectManyPopupComponent extends InputTagSelectComboComponent {

	private String onRenderItemsString;

	@Override
	public void prepare() {

		super.prepare();

		String onRenderItems = (String) inputTag.getDAAtribute("onrenderitems", true);
		if (Util.strings.isNotEmpty(onRenderItems)) {
			this.onRenderItemsString = onRenderItems;
		}

	}

	@Override
	public boolean isIncludeBlank() {
		return false; //select-many-popup never has include blank
	}

	public String getOnRenderItemsString() {
		return onRenderItemsString;
	}

}