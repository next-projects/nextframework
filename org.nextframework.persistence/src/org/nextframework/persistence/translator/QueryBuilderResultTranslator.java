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
package org.nextframework.persistence.translator;

import java.util.List;

import org.hibernate.SessionFactory;
import org.nextframework.persistence.QueryBuilder;

public interface QueryBuilderResultTranslator {

	public void init(SessionFactory sessionFactory, QueryBuilder<?> queryBuilder);

	public void init(SessionFactory sessionFactory, String[] selectedProperties, AliasMap[] aliasMaps);

	/**
	 * N�o Thread-Safe a chamada deve ser synchronizada
	 * Recomenda��o: utilizar translate(List) que � Thread Safe
	 * @param values
	 * @return
	 */
	public Object translate(Object[] values);

	/**
	 * Thread-Safe
	 * @param values
	 * @return
	 */
	public List<?> translate(List<?> values);

	public String[] getExtraFields();

}
