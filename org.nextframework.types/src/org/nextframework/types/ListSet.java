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
package org.nextframework.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * @author rogelgarcia
 */
@Deprecated
public class ListSet<E> extends ArrayList<E> implements Set<E> {

	private static final long serialVersionUID = 3258413915376202040L;
	private Class<E> clazz;

	public ListSet(Class<E> clazz) {
		this.clazz = clazz;
	}

	public ListSet(Class<E> clazz, Collection<? extends E> c) {
		super(c);
		this.clazz = clazz;
	}

	public E get(int index) {
		if (this.size() < index + 1) {
			while (this.size() < index + 1) {
				try {
					add(clazz.newInstance());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		return (E) super.get(index);
	}

}
