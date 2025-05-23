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
package org.stjs.generator;

import java.util.regex.Pattern;

public class GeneratorConstants {

	public static final String SPECIAL_THIS = "THIS";

	/**
	 * this is the type to be used when defining an inline type
	 */
	public static final String SPECIAL_INLINE_TYPE = "_InlineType";

	public static final String SUPER = "super";

	public static final String THIS = "this";

	public static final String ARGUMENTS_PARAMETER = "arguments";

	public static final String TYPE_DESCRIPTION_PROPERTY = "$typeDescription";

	public static final Pattern NAMESPACE_PATTERN = Pattern
			.compile("([A-Za-z_][A-Za-z_0-9]*)(?:\\.([A-Za-z_][A-Za-z_0-9]*))*");

}
