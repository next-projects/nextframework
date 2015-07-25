package org.nextframework.view.components;

import org.nextframework.controller.MultiActionController;
import org.nextframework.controller.crud.AbstractCrudController;
import org.nextframework.exception.NextException;
import org.nextframework.view.FormTag;
import org.nextframework.view.SelecionarCadastrarServlet;

public class InputTagSelectOneInsertComponent extends InputTagSelectComboComponent {

	public String getSelectOneInsertOnClick() {
		if (inputTag.getInsertPath() == null) {
			throw new NextException("Quando o tipo do input for select-one-insert o atributo insertPath é obrigatório");
		}
		String contextPath = inputTag.getRequest().getContextPath();
		String fullPath = contextPath + SelecionarCadastrarServlet.SELECIONAR_CADASTRAR_PATH + inputTag.getInsertPath();
		if (fullPath.contains("?")) {
			fullPath += "&";
		} else {
			fullPath += "?";
		}
		fullPath += MultiActionController.ACTION_PARAMETER + "=" + AbstractCrudController.CREATE + "&fromInsertOne=true";
		FormTag form = inputTag.findParent(FormTag.class, true);
		String typeString = "java.lang.Object";
		if (inputTag.getType() instanceof Class<?>) {
			typeString = ((Class<?>) inputTag.getType()).getName();
		}
		String onclick = "var c = new selecionarCallbackObject(" + form.getName() + "['" + inputTag.getName() + "'], " + "null, '" + typeString + "', " + "null, null, function(label,valor){" + form.getName() + "['" + inputTag.getName() + "'].options.add(new Option(label, valor, false, true)); "
				+ "}); var win = open('" + fullPath + "','filha" + ((int) (Math.random() * 100000)) + "','width=' + 780 + ', height=' + 580 + ', top=50, left=115, resizable, scrollbars, status=yes'); win.selecionarCallback = c;";
		return onclick;
	}
}
