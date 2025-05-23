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

abstract public class Location {

	public String hash;
	public String host;
	public String hostname;
	public String href;
	public String pathname;
	public int port;
	public String protocol;
	public String search;

	abstract public void assign(String url);

	abstract public void reload();

	abstract public void reload(boolean fromServer);

	abstract public void replace(String url);

}
