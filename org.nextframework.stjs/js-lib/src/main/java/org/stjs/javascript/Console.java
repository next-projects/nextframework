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

/**
 * This is the bridge to the browser console object
 * 
 * @author acraciun
 * 
 */
abstract public class Console {
	public abstract void log(Object msg);
	
	public abstract void log(Object msg, Object... otherParams);

	public abstract void warn(Object msg, Object... otherParams);

	public abstract void error(Object msg, Object... otherParams);

	public abstract void debug(Object msg, Object... otherParams);

	public abstract void trace(Object msg, Object... otherParams);

	public abstract void info(Object msg, Object... otherParams);
	
	public abstract void info(Object msg);

	public abstract void dir(Object msg);

	public abstract void time(Object msg);

	public abstract void timeEnd(Object msg);

	public abstract void profile(Object msg);

	public abstract void profileEnd(Object msg);

}
