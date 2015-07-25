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
package org.nextframework.persistence;

import java.util.List;

import org.nextframework.persistence.QueryBuilder.GroupBy;


/**
 * Cria um resultSet para ser usado na listagem
 * Atualiza o listingFilter com o número de paginas e a página atual
 */
public class ResultListImpl<E> implements ResultList<E> {
	
	protected List<E> lista = null;
	
	public ResultListImpl(QueryBuilder<E> queryBuilder, PageAndOrder pageAndOrder){
		init(queryBuilder, pageAndOrder);
	}

	protected void init(QueryBuilder<E> queryBuilder, PageAndOrder pageAndOrder) {		
		int numeroResultados = getCount(queryBuilder);
		pageAndOrder.setNumberOfResults(numeroResultados);
		
		int i = numeroResultados / pageAndOrder.getPageSize();
		if(numeroResultados % pageAndOrder.getPageSize() != 0){
			i++;
		}
		pageAndOrder.setNumberOfPages(i);
		
		int page = pageAndOrder.getCurrentPage();
		if(pageAndOrder.resetPage()){
			//se estiver filtrando novamente voltar para a primeira página
			page = 0;
		}else{
			//se a pagina atual do filtro tiver maior que o total de resultados, puxa pra tras
			page = pageAndOrder.getCurrentPage() > i-1 ? i-1 : pageAndOrder.getCurrentPage();
			page = page < 0 ? 0 : page;
		}
		pageAndOrder.setCurrentPage(page);
		
		queryBuilder.setPageNumberAndSize(page, pageAndOrder.getPageSize());
		if (pageAndOrder.getOrderBy() != null && pageAndOrder.getOrderBy().trim().length() > 0) {
			queryBuilder.orderBy(pageAndOrder.getOrderBy()+" "+(pageAndOrder.isAsc()?"ASC":"DESC"));
		}
		
		lista = queryBuilder.list();
	}

	private int getCount(QueryBuilder<E> queryBuilder) {
		QueryBuilder<Number> countQueryBuilder = queryBuilder.createNew(Number.class);

		String select = queryBuilder.getSelect().getValue().trim();
		if (!select.toLowerCase().startsWith("distinct")) {
			countQueryBuilder.select("count(*)");
		} else {
			countQueryBuilder.select("count(" + select + ")");
		}
		QueryBuilder.From from = queryBuilder.getFrom();
		countQueryBuilder.from(from);

		List<QueryBuilder.Join> joins = queryBuilder.getJoins();
		for (QueryBuilder.Join join : joins) {
			// quando estiver contando nao precisa de fazer fetch do join
			countQueryBuilder.join(join.getJoinMode(), false, join.getPath());
		}
		QueryBuilder.Where where = queryBuilder.getWhere();
		countQueryBuilder.where(where);
		
		//Se houver group by, deve retornar a quantidade de grupos
		GroupBy groupBy = queryBuilder.getGroupBy();
		if (groupBy != null) {
			
			//Os grupos devem ser concatenados no select para o Hibernate nao retirar os itens iguais
			String selectGroupBy = countQueryBuilder.getSelect().getValue() + ", " + groupBy.getValue();
			countQueryBuilder.select(selectGroupBy);
			countQueryBuilder.setUseTranslator(false);
			
			countQueryBuilder.setGroupBy(groupBy);
			List<Number> numerosResultados = countQueryBuilder.list();
			return numerosResultados.size();
		}
		
		int numeroResultados = countQueryBuilder.unique().intValue();
		return numeroResultados;
	}
	
	public List<E> list(){
		return lista;
	}
}
