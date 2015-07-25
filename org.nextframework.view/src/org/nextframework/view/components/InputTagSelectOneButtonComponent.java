package org.nextframework.view.components;

import java.util.ArrayList;

import org.nextframework.exception.NextException;
import org.nextframework.util.Util;
import org.nextframework.view.ComboReloadGroupTag;
import org.nextframework.view.FormTag;
import org.nextframework.view.InputTag;
import org.nextframework.view.SelecionarCadastrarServlet;

public class InputTagSelectOneButtonComponent extends InputTagSelectComponent {
	
	@Override
	protected InputTag prepareComboReload(ComboReloadGroupTag comboReload) {
		inputTag.setItens(new ArrayList<Object>()); // se for select_one_button não existe lista
		return comboReload.getLastInput(inputTag);
	}

	public String getSelectOneButtonOnClick() {
		if (inputTag.getSelectOnePath() == null) {
			throw new NextException("Quando o tipo do input for select-one-button o atributo selectOnePath é obrigatório");
		}
		String contextPath = inputTag.getRequest().getContextPath();
		String fullPath = contextPath + SelecionarCadastrarServlet.SELECIONAR_CADASTRAR_PATH + inputTag.getSelectOnePath();

		if (Util.strings.isNotEmpty(inputTag.getSelectOnePathParameters())) {
			if (!fullPath.contains("?")) {
				fullPath += "?";
			}
			if (inputTag.getSelectOnePathParameters().startsWith("javascript:")) {
				fullPath += "' +" + inputTag.getSelectOnePathParameters().substring("javascript:".length()) + " + '";
			} else {
				fullPath += inputTag.getSelectOnePathParameters();
			}
		}

		FormTag form = inputTag.findParent(FormTag.class, true);
		String typeString = "java.lang.Object";
		if (inputTag.getType() instanceof Class<?>) { //TODO possivel bug... e se o type for texto?
			typeString = ((Class<?>) inputTag.getType()).getName();
		} else {
			//throw new NextException("Cannot use select-one-button with type not set with class.");
			//log.warn("O atributo type foi informado com o valor "+type+" para um select-one-button ("+getName()+"). Isso poderá ocasionar erros. Tente não utilizar o atributo type para esse input. Use apenas selectOnePath. " );
		}
		String windowWidth = "window.document.body.clientWidth";
		String windowHeight = "window.document.body.clientHeight";
		String windowTop = "50";
		String windowLeft = "0";
		if(Util.strings.isNotEmpty(inputTag.getSelectOneWindowSize())){
			String[] split = inputTag.getSelectOneWindowSize().split(",");
			if(split.length > 0) windowWidth = split[0];
			if(split.length > 1) windowHeight = split[1];
			if(split.length > 2) windowTop = split[2];
			if(split.length > 3) windowLeft = split[3];
		}
		String onclick = "var c = new selecionarCallbackObject(" + form.getName() + "['" + inputTag.getName() + "'], " + form.getName() + "['" + inputTag.getName() + "_label'], '" + typeString + "', " + form.getName() + "['" + inputTag.getName() + "_btn'], " + form.getName() + "['" + inputTag.getName() + "_btnUnselect']"
				+ "); var win = open('" + fullPath + "','filha" + ((int) (Math.random() * 100000)) + "','width=' + ("+windowWidth+") + ', height=' + ("+windowHeight+") + ', top="+windowTop+", left="+windowLeft+", resizable, scrollbars'); win.selecionarCallback = c;";
		return onclick;
	}
	

	public String getSelectOneUnselectButtonStyle() {
		if (Util.objects.isEmpty(inputTag.getValue()) || inputTag.getValue().equals("<null>")) {
			return "display:none";
		} else {
			return "";
		}
	}

	public String getSelectOneButtonStyle() {
		if (Util.objects.isEmpty(inputTag.getValue()) || inputTag.getValue().equals("<null>")) {
			return "";
		} else {
			return "display:none";
		}
	}

}
