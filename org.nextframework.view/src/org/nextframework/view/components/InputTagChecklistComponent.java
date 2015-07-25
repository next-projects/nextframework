package org.nextframework.view.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.nextframework.exception.NextException;
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
			// if(!valueExplicito)
			throw new NextException(
					"O atributo value da tag input não pode ser um Collection ou Array quando o tipo for CHECKLIST. Você deve ter utilizado a tag property para montar esse input. Nesse caso deve ser explicitado qual é o valor desse checkbox ao invés de utilizar o value adquirido automaticamente pelo property. Coloque no atributo value desse input qual o valor que esse checkbox deve representar");
			// else
			// throw new
			// NextException("O atributo value da tag input não pode ser um Collection ou Array quando o tipo for CHECKLIST");
		}
		Object itensValue = null;
		if (inputTag.getItens() != null && inputTag.getItens() instanceof String) {
			String expression = (String) inputTag.getItens();
			itensValue = inputTag.getOgnlValue(expression);
		} else if (inputTag.getItens() != null) {
			itensValue = inputTag.getItens();
		}
		List<String> lista = new ArrayList<String>();
		if (itensValue instanceof Collection<?>) {
			for (Object object : (Collection<?>) itensValue) {
				lista.add(TagUtils.getObjectValueToString(object));
			}
		} else if (itensValue != null && itensValue.getClass().isArray()) {
			Object[] array = (Object[]) itensValue;
			for (Object object : array) {
				lista.add(TagUtils.getObjectValueToString(object));
			}
		}
		String valueToString = TagUtils.getObjectValueToString(inputTag.getValue());
		boolean toCheck = false;
		for (String string : lista) {
			if (string.equals(valueToString)) {
				toCheck = true;
				break;
			}
		}
		inputTag.setChecked(toCheck);
		inputTag.setCheckboxValue(valueToString);
		
		inputTag.setSelectedType(InputTagType.CHECKBOX);// utilizar o template do checkbox
	}

}
