/**
 * 
 */
package org.nextframework.util;

import java.util.Iterator;

public class CyclicIterator<E> implements Iterator<E> {

	private E[] elements;
	int i = 0;

	public CyclicIterator() {
		this(null);
	}

	public CyclicIterator(E[] elements) {
		this.elements = elements;
	}

	public boolean hasNext() {
		return true;
	}

	public E next() {
		if (elements == null || elements.length == 0)
			return null;
		if (i >= elements.length) {
			i = 0;
		}
		return elements[i++];
	}

	public E[] getElements() {
		return elements;
	}

	public void remove() {
	}

	public void reset() {
		i = 0;
	}

}
