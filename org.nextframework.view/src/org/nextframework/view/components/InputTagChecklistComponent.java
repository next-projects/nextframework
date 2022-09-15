package org.nextframework.view.components;

import java.util.Collection;

import org.nextframework.exception.NextException;
import org.nextframework.util.Util;
import org.nextframework.view.InputTagType;
import org.nextframework.view.TagUtils;

public class InputTagChecklistComponent extends InputTagCheckboxBaseComponent {

	@Override
	public void validateTag() {
	}

	@Override
	public void prepare() {

		super.prepare();

		if (inputTag.getValue() instanceof Collection<?> || inputTag.getValue() != null && inputTag.getValue().getClass().isArray()) {
			throw new NextException("O atributo value da tag input não pode ser um Collection ou Array quando o tipo for CHECKLIST. Você deve utilizar a tag property para montar esse input. Nesse caso deve ser explicitado qual é o valor desse checkbox ao invés de utilizar o value adquirido automaticamente pelo property. Coloque no atributo value desse input qual o valor que esse checkbox deve representar");
		}

		Object itensValue = null;
		if (inputTag.getItens() != null && inputTag.getItens() instanceof String) {
			String expression = (String) inputTag.getItens();
			itensValue = inputTag.getOgnlValue(expression);
		} else if (inputTag.getItens() != null) {
			itensValue = inputTag.getItens();
		}

		boolean toCheck = false;
		if (itensValue instanceof Collection<?>) {
			for (Object object : (Collection<?>) itensValue) {
				if (Util.objects.equals(object, inputTag.getValue())) {
					toCheck = true;
					break;
				}
			}
		} else if (itensValue != null && itensValue.getClass().isArray()) {
			for (Object object : (Object[]) itensValue) {
				if (Util.objects.equals(object, inputTag.getValue())) {
					toCheck = true;
					break;
				}
			}
		}

		inputTag.setChecked(toCheck);
		inputTag.setCheckboxValue(TagUtils.getObjectValueToString(inputTag.getValue()));
		inputTag.setSelectedType(InputTagType.CHECKBOX);// utilizar o template do checkbox

	}

}
