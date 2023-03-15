package org.nextframework.js;

import org.stjs.javascript.Window;
import org.stjs.javascript.dom.DOMEvent;
import org.stjs.javascript.dom.Document;
import org.stjs.javascript.dom.Element;
import org.stjs.javascript.functions.Callback1;
import org.stjs.javascript.functions.Function1;

public abstract class NextEvents {

	public abstract void onLoad(Callback1<DOMEvent> callback);

	public abstract Function1<?, ?> attachEvent(Element el, String event, Callback1<DOMEvent> callback);

	public abstract Function1<?, ?> attachEvent(Element el, String event, Function1<DOMEvent, Boolean> function);

	public abstract Function1<?, ?> attachEvent(Document doc, String event, Callback1<DOMEvent> callback);

	public abstract Function1<?, ?> attachEvent(Document doc, String event, Function1<DOMEvent, Boolean> function);

	public abstract Function1<?, ?> attachEvent(Window window, String event, Callback1<DOMEvent> callback);

	public abstract Function1<?, ?> attachEvent(Window window, String event, Function1<DOMEvent, Boolean> function);

	public abstract void detachEvent(Element el, String event, Callback1<?> callback);

	public abstract void detachEvent(Element el, String event, Function1<?, ?> function);

	public abstract void detachEvent(Document doc, String event, Callback1<?> callback);

	public abstract void detachEvent(Document doc, String event, Function1<?, ?> function);

	public abstract void detachEvent(Window window, String event, Callback1<?> callback);

	public abstract void detachEvent(Window window, String event, Function1<?, ?> function);

	public abstract void dispatchEvent(Element el, String event);

	public abstract void cancelEvent(DOMEvent e);

	public abstract char getChar(DOMEvent e);

	public abstract int getKey(DOMEvent e);

}