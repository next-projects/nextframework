package org.nextframework.js;

import org.stjs.javascript.Array;
import org.stjs.javascript.Date;

public abstract class NextUtil {

	public abstract <E> int indexOf(Array<E> array, E e);

	public abstract <E> void removeItem(Array<E> array, E e);

	public abstract String escapeSingleQuotes(String pattern);

	public abstract String removeSingleQuotes(String pattern);

	public abstract boolean isDefined(Object o);

	public abstract void evalScripts(String contents);

	public abstract int getYearFromDate(Date date);

	public abstract String removeAccents(String text);

	public abstract String join(Array<?> array, String token);

}
