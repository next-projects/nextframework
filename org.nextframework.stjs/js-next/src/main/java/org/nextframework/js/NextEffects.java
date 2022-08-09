package org.nextframework.js;

import org.stjs.javascript.Map;
import org.stjs.javascript.dom.Element;

public abstract class NextEffects {

	public static String blockScreenColor;

	public abstract void hide(Object el);

	public abstract void show(Object el);

	public abstract void hideProperty(Element el);

	public abstract void showProperty(Element el);

	public abstract void blinkColors(Element el, String colorA, String colorB);

	public abstract void blink(Element el);

	public abstract void blockScreen();

	public abstract void unblockScreen();

	public abstract void fade(Element el, int fromOpacity, int toOpacity, Map<String, Object> options);

	public abstract void fade(Element el, double fromOpacity, double toOpacity);

}