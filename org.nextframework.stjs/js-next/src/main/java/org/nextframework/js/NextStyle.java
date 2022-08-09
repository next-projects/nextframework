package org.nextframework.js;

import org.stjs.javascript.Array;
import org.stjs.javascript.dom.Element;

public abstract class NextStyle {

	public abstract boolean hasClass(Element element, String className);

	public abstract boolean addClass(Element element, String className);

	public abstract boolean removeClass(Element element, String className);

	/**
	 * Retorna o valor de uma propriedade CSS computada pelo browser. Deve ser utilizado o padrão CSS de propriedades exemplo:
	 * next.style.getStyleProperty(obj, 'padding-left').
	 * <BR>
	 * @param obj Elemento do qual deseja o valor
	 * @param property Propriedade que se deseja saber o valor
	 * @return O valor computado pelo browser
	 */
	public abstract Object getStyleProperty(Element el, String property);

	public abstract Integer getTop(Element el);

	public abstract Integer getLeft(Element el);

	public abstract Integer getHeight(Element el);

	public abstract Integer getFullHeight(Element el);

	public abstract Integer getWidth(Element el);

	public abstract Integer getFullWidth(Element el);

	public abstract void setRadius(Element el, String radius);

	public abstract void setShadow(Element el, String shadow);

	public abstract void setOpacity(Element element, int i);

	public abstract Array<Integer> getWindowSize();

	public abstract Array<Integer> getBodySize();

	public abstract void centralizeHorizontal(Element el);

	public abstract void centralizeVertical(Element el);

	public abstract void centralizeVerticalMiddleLine(Element el);

	public abstract void centralizeMiddleLine(Element el);

	public abstract void centralize(Element el);

}
