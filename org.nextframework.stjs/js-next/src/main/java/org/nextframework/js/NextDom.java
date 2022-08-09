package org.nextframework.js;

import org.nextframework.js.dom.Popup;
import org.nextframework.js.form.Form;
import org.stjs.javascript.Array;
import org.stjs.javascript.Map;
import org.stjs.javascript.dom.Div;
import org.stjs.javascript.dom.Element;
import org.stjs.javascript.dom.Input;
import org.stjs.javascript.dom.Select;

public abstract class NextDom {

	public abstract <X extends Element> X toElement(String id);

	public abstract <X extends Element> X toElement(Object el);

	public abstract <X extends Element> X getInnerElementById(Element parent, String id);

	public abstract <X extends Element> X getInnerElementByName(Element parent, String name);

	public abstract <X extends Element> X getInnerElementByName(Element parent, String name, String tag);

	public abstract <X extends Element> X newElement(String string, Map<String, ?> prop);

	public abstract <X extends Element> X newElement(String string);

	public abstract <X extends Div> X newDivElement(String innerHTML, Map<String, ?> prop);

	public abstract <X extends Div> X newDivElement(String innerHTML);

	public abstract <X extends Div> X newDivElement();

	public abstract <X extends Div> X newSpanElement(String innerHTML, Map<String, ?> prop);

	public abstract <X extends Div> X newSpanElement(String innerHTML);

	public abstract <X extends Div> X newSpanElement();

	public abstract <X extends Input> X newInput(String type, String name, String label, Map<String, Object> options);

	public abstract <X extends Input> X newInput(String type, String name, String label);

	public abstract <X extends Input> X newInput(String type, String name);

	public abstract <X extends Input> X newInput(String type);

	public abstract <X extends Input> X newInput();

	/**
	 * Cria um wrapper para o elemento. Se o elemento já possui um wrapper, retorna o wrapper já criado.
	 * @param el
	 * @return
	 */
	public abstract <X extends Element> X wrapper(Element el);

	public abstract String generateUniqueId();

	public abstract boolean setSelectedValue(Element el, String value);

	public abstract boolean setSelectedValue(Element el, String value, boolean dispatchEvent);

	public abstract boolean setSelectedValueWithId(Element el, Object idValue);

	public abstract boolean setSelectedValueWithId(Element el, Object idValue, boolean dispatchEvent);

	public abstract Array<String> getSelectedValues(Select el);

	public abstract void setSelectedValues(Element el, Array<String> values);

	public abstract String getSelectedValue(Element el);

	public abstract String getSelectedText(Element el);

	public abstract void removeSelectValue(Element el, String value);

	public abstract Form getForm();

	public abstract Form getForm(String name);

	public abstract Popup getNewPopupDiv();

	public abstract void autobind(Object obj, Element parent);

	public abstract void autobind(Object obj);

	public abstract Element getParentTag(Element el, String parentTagName);

}