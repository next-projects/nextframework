/**
 * 
 */
package org.nextframework.util;

import java.util.Iterator;

public class CyclicIterator implements Iterator<String> {
	
	private String[] strings;
	int i = 0;
	
	public CyclicIterator(){
		this(new String[0]);
	}

	public CyclicIterator(String[] strings){
		this.strings = strings;
	}

	public boolean hasNext() {
		return true;
	}

	public String next() {
		if(strings == null || strings.length == 0) return null;
		if(i >= strings.length){
			i = 0;
		}
		return strings[i++];
	}

	public String[] getStrings() {
		return strings;
	}

	public void remove() {
	}

	public void reset(){
		i = 0;
	}

}