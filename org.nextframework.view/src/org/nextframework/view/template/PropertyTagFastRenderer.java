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
package org.nextframework.view.template;

/**
 * Possibilita a renderiza��o da tag t:property via c�digo java.
 * N�o utilizando o template, a renderiza��o ter uma performance bem superior.
 * 
 * Se existir uma classe na aplica��o que implemente essa interface, ela ser� autodetectada, e a do framework ser� ignorada.
 * @author rogel
 *
 */
public interface PropertyTagFastRenderer {
	
	/**
	 * Renderiza a tag, caso n�o seja poss�vel fazer a renderiza��o o retorno deve ser falso.
	 * Se for poss�vel fazer a renderiza��o o retorno deve ser true.
	 * Se o retorno for false, ser� utilizado o template da tag property (PropertyTag.jsp)
	 * @param tag
	 * @return
	 * @throws Exception 
	 */
	boolean render(PropertyTag tag) throws Exception;

}
