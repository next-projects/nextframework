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
import java.sql.Timestamp;
import java.util.Date;

public class TimestampPropertyEditor extends PropertyEditorSupport{

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		throw new RuntimeException("Conversor ainda não completamente implementado");
		/*
		if(text != null){
			if(text.matches("(dd:dd:dddd)?(.*)(dd:dd(:dd)?)")){
				int ano = 0;
				int mes = 0;
				int dia = 0;
				int hora = 0;
				int minuto = 0;
				int segundo = 0;
				
			} else {
				throw new IllegalArgumentException("Não é possível converter: "+text);
			}
		} else {
			setValue(null);
		}
		*/
	}
	
	public Object getValue() {
		Object object = super.getValue();
		if(object instanceof Date){
			Timestamp timestamp = new Timestamp(((Date)object).getTime());
			return timestamp;
		}
		return object;
	}

}
