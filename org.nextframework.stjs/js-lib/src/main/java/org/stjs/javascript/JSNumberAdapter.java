/**
 *  Copyright 2011 Alexandru Craciun, Eyal Kaspi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
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
package org.stjs.javascript;

import org.stjs.javascript.annotation.Adapter;

/**
 * here are the methods existent in Javascript for number objects and inexistent in the Java counterpart. The generator
 * should generate the correct code
 * 
 * @author acraciun
 */
@Adapter
public class JSNumberAdapter {

	public static String toLocaleString(Number n) {
		throw new UnsupportedOperationException();
	}

	public static String toLocaleString(Number n, String locale) {
		throw new UnsupportedOperationException();
	}

	public static String toLocaleString(Number n, String locale, Map<String, String> options) {
		throw new UnsupportedOperationException();
	}

	public static String toFixed(Number n, int positions) {
		throw new UnsupportedOperationException();
	}

	public static String toFixed(String n, int positions) {
		throw new UnsupportedOperationException();
	}

	public static String toExponential(Number n, int positions) {
		throw new UnsupportedOperationException();
	}

	public static String toPrecision(Number n, int positions) {
		throw new UnsupportedOperationException();
	}

}
