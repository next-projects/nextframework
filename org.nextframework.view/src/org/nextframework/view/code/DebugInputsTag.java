/*
 * Next Framework http://www.nextframework.org
 * Copyright (C) 2009 the original author or authors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * You may obtain a copy of the license at
 * 
 *     http://www.gnu.org/copyleft/lesser.html
 * 
 */
package org.nextframework.view.code;

import java.util.HashSet;
import java.util.Set;

import org.nextframework.view.BaseTag;
import org.nextframework.view.LogicalTag;

public class DebugInputsTag extends BaseTag implements LogicalTag {

	protected Class<?> commandClass;
	protected Set<String> propriedades = new HashSet<String>();
	protected Set<String> propriedadesRepetidas = new HashSet<String>();

	public boolean addProperty(String propriedade) {
		if (propriedades.contains(propriedade)) {
			propriedadesRepetidas.add(propriedade);
			return false;
		} else {
			propriedades.add(propriedade);
			return true;
		}
	}

	public Class<?> getCommandClass() {
		return commandClass;
	}

	public void setCommandClass(Class<?> commandClass) {
		this.commandClass = commandClass;
	}

	@Override
	protected void doComponent() throws Exception {
		doBody();
		for (String property : propriedadesRepetidas) {
			getOut().println("Propriedade duplicada: " + property + "<BR>");
		}
	}

}
