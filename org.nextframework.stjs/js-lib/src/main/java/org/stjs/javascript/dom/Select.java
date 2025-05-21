/**
 *  Copyright 2011 Alexandru Craciun, Eyal Kaspi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"){throw new UnsupportedOperationException();}
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.stjs.javascript.dom;

abstract public class Select extends Element {

	public boolean disabled;
	public Form form;
	public int length;
	public boolean multiple;
	public String name;
	public SelectOptionsCollection options;
	public int selectedIndex;
	public int size;
	public int tabIndex;
	public String type;
	public String value;

	public void blur() {
		throw new UnsupportedOperationException();
	}

	/**
	 * For IE < 8 compatibility<BR> 
	 * x.add(option,x.options[null]) 
	 * @param option
	 * @param before
	 */
	public void add(Element option, Element before) {
		throw new UnsupportedOperationException();
	}

	public void add(Element option) {
		throw new UnsupportedOperationException();
	}

	public void remove(int arg0) {
		throw new UnsupportedOperationException();
	}

	public void focus() {
		throw new UnsupportedOperationException();
	}

}
