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
package org.nextframework.bean.editors;

import java.beans.PropertyEditorSupport;

import org.nextframework.types.Cep;

public class CepPropertyEditor extends PropertyEditorSupport {

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (text != null) {
			text = text.replace("-", "");
			switch (text.length()) {
				case 0:
					setValue(null);
					break;
				case 8:
					setValue(new Cep(text));
					break;
				default:
					throw new IllegalArgumentException("O CEP deve ter 8 dígitos");
			}

		} else {
			setValue(null);
		}
	}

	@Override
	public String getAsText() {
		if (getValue() == null) {
			return "";
		}
		return getValue().toString();
	}

}
