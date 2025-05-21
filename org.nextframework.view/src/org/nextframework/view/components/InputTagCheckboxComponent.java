package org.nextframework.view.components;

import org.nextframework.util.Util;
import org.nextframework.view.InputTagComponentManager;
import org.nextframework.view.InputTagType;

public class InputTagCheckboxComponent extends InputTagCheckboxBaseComponent {

	@Override
	public void prepare() {
		super.prepare();
		if (Util.strings.isNotEmpty(inputTag.getTrueFalseNullLabels())) {
			InputTagComponent delegate = InputTagComponentManager.getInstance().getInputComponent(InputTagType.SELECT_ONE);
			inputTag.setSelectedType(InputTagType.SELECT_ONE);
			inputTag.setInputComponent(delegate);
			delegate.setHelper(helper);
			delegate.setTag(inputTag);
			delegate.prepare();
		}
	}

	@Override
	protected void configureValidation(boolean disabled) {
		//checkbox não possui validação
	}

}
