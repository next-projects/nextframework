package org.nextframework.view;

import java.util.HashMap;
import java.util.Map;

import org.nextframework.view.components.InputTagCheckboxComponent;
import org.nextframework.view.components.InputTagChecklistComponent;
import org.nextframework.view.components.InputTagComponent;
import org.nextframework.view.components.InputTagDateTimeComponent;
import org.nextframework.view.components.InputTagFileComponent;
import org.nextframework.view.components.InputTagHiddenComponent;
import org.nextframework.view.components.InputTagNumberComponent;
import org.nextframework.view.components.InputTagSelectManyBoxComponent;
import org.nextframework.view.components.InputTagSelectManyComponent;
import org.nextframework.view.components.InputTagSelectManyPopupComponent;
import org.nextframework.view.components.InputTagSelectOneButtonComponent;
import org.nextframework.view.components.InputTagSelectOneComponent;
import org.nextframework.view.components.InputTagSelectOneInsertComponent;
import org.nextframework.view.components.InputTagSelectOneRadioComponent;
import org.nextframework.view.components.InputTagSuggestComponent;
import org.springframework.beans.BeanUtils;

public class InputTagComponentManager {

	private static InputTagComponentManager instance = new InputTagComponentManager();

	public static InputTagComponentManager getInstance() {
		return instance;
	}

	public static void setInstance(InputTagComponentManager instance) {
		InputTagComponentManager.instance = instance;
	}

	Map<InputTagType, Class<? extends InputTagComponent>> componentClasses = new HashMap<InputTagType, Class<? extends InputTagComponent>>();

	public Class<? extends InputTagComponent> registerComponent(InputTagType key, Class<? extends InputTagComponent> value) {
		return componentClasses.put(key, value);
	}

	private InputTagComponentManager() {
		init();
	}

	protected void init() {

		registerComponent(InputTagType.CHECKLIST, InputTagChecklistComponent.class);

		registerComponent(InputTagType.CHECKBOX, InputTagCheckboxComponent.class);

		registerComponent(InputTagType.FILE, InputTagFileComponent.class);

		registerComponent(InputTagType.DATE, InputTagDateTimeComponent.class);
		registerComponent(InputTagType.TIME, InputTagDateTimeComponent.class);

		registerComponent(InputTagType.HIDDEN, InputTagHiddenComponent.class);

		registerComponent(InputTagType.SUGGEST, InputTagSuggestComponent.class);

		registerComponent(InputTagType.INTEGER, InputTagNumberComponent.class);
		registerComponent(InputTagType.FLOAT, InputTagNumberComponent.class);
		registerComponent(InputTagType.MONEY, InputTagNumberComponent.class);

		registerComponent(InputTagType.SELECT_MANY, InputTagSelectManyComponent.class);
		registerComponent(InputTagType.SELECT_MANY_BOX, InputTagSelectManyBoxComponent.class);
		registerComponent(InputTagType.SELECT_MANY_POPUP, InputTagSelectManyPopupComponent.class);
		registerComponent(InputTagType.SELECT_ONE_BUTTON, InputTagSelectOneButtonComponent.class);
		registerComponent(InputTagType.SELECT_ONE, InputTagSelectOneComponent.class);
		registerComponent(InputTagType.SELECT_ONE_INSERT, InputTagSelectOneInsertComponent.class);
		registerComponent(InputTagType.SELECT_ONE_RADIO, InputTagSelectOneRadioComponent.class);

	}

	public InputTagComponent getInputComponent(InputTagType type) {
		Class<? extends InputTagComponent> clazz = componentClasses.get(type);
		if (clazz == null) {
			clazz = InputTagComponent.class;
		}
		InputTagComponent instance = BeanUtils.instantiate(clazz);
		instance.setSelectedType(type);
		return instance;
	}

}
