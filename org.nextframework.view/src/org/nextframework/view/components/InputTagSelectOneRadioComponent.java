package org.nextframework.view.components;

import org.nextframework.core.config.ViewConfig;
import org.nextframework.service.ServiceFactory;
import org.nextframework.util.Util;
import org.nextframework.view.InputTag;

public class InputTagSelectOneRadioComponent extends InputTagSelectComboComponent {

	@Override
	protected String createOption(Integer index, String label, String value, String selected) {

		String id = inputTag.getId() + "_" + index;

		String itemClass = (String) inputTag.getDAAtribute("itemClass", true);
		String itemStyle = (String) inputTag.getDAAtribute("itemStyle", true);

		ViewConfig viewConfig = ServiceFactory.getService(ViewConfig.class);
		String grpStyleClass = viewConfig.getDefaultStyleClass(InputTag.class, "SELECT_ONE_RADIO-group");
		String btnStyleClass = viewConfig.getDefaultStyleClass(InputTag.class, "SELECT_ONE_RADIO-button");
		String lblStyleClass = viewConfig.getDefaultStyleClass(InputTag.class, "SELECT_ONE_RADIO-label");

		if (Util.strings.isEmpty(itemClass)) {
			itemClass = grpStyleClass;
		} else if (itemClass.contains("+")) {
			itemClass = itemClass.replace("+", grpStyleClass);
		}

		selected = selected != null && selected.length() > 0 ? "checked=\"checked\"" : "";

		StringBuilder sb = new StringBuilder("<div");
		if (Util.strings.isNotEmpty(itemClass)) {
			sb.append(" class='").append(itemClass).append("'");
		}
		if (Util.strings.isNotEmpty(itemStyle)) {
			sb.append(" style='").append(itemStyle).append("'");
		}
		sb.append(">");

		sb.append("<input id='").append(id).append("' name='").append(inputTag.getName()).append("' type='radio'");
		if (Util.strings.isNotEmpty(btnStyleClass)) {
			sb.append(" class='").append(btnStyleClass).append("'");
		}
		sb.append(" value='").append(value != null ? value : "<null>").append("'");
		if (Util.strings.isNotEmpty(selected)) {
			sb.append(" ").append(selected);
		}
		sb.append("/>");

		sb.append("<label for='").append(id).append("'");
		if (Util.strings.isNotEmpty(lblStyleClass)) {
			sb.append(" class='").append(lblStyleClass).append("'");
		}
		sb.append(">").append(label).append("</label>");

		sb.append("</div>");

		return sb.toString();
	}

}
