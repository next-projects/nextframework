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
package org.nextframework.persistence.exception;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.classmanager.ClassManagerFactory;
import org.nextframework.util.ReflectionCache;
import org.nextframework.util.ReflectionCacheFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

public class PostgreSQLErrorCodeSQLExceptionTranslator extends SQLErrorCodeSQLExceptionTranslator {
	
	static String errorRegexInglesNull = "ERROR: null value in column \"(.*)\" violates not-null constraint";
	static Pattern patternInglesNull = Pattern.compile(errorRegexInglesNull);
	static String errorRegexNull = "ERRO: valor nulo na coluna \"(.*)\" viola a restri��o n�o-nula";
	static Pattern patternNull = Pattern.compile(errorRegexNull);
	
	
	static String errorRegex = "ERRO: atualiza��o ou exclus�o em \"(.*)\" viola restri��o de chave estrangeira \"(.*)\" em \"(.*)\".*?";
	static String errorRegexIngles = "ERROR: update or delete on (?:table)? \"(.*)\" violates foreign key constraint \"(.*)\" on (?:table)? \"(.*)\".*?";
//	static String errorRegex = "(.*) statement conflicted with COLUMN REFERENCE constraint '(.*?)'. The conflict occurred in database '(?:.*?)', table '(.*?)', column '(.*?)'.";
	static Pattern pattern = Pattern.compile(errorRegex);
	static Pattern patternIngles = Pattern.compile(errorRegexIngles);
	
	static final Log log = LogFactory.getLog(PostgreSQLErrorCodeSQLExceptionTranslator.class);
	
	private DataSource dataSource;
	
	public DataSource getDataSource() {
		return dataSource;
	}

	@Override
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
		this.dataSource = dataSource;
	}
	
	private String getTableName(Class<?> entityClass) {
		ReflectionCache reflectionCache = ReflectionCacheFactory.getReflectionCache();
		Table table = reflectionCache.getAnnotation(entityClass, Table.class);
		if(table != null){
			return table.name().toUpperCase();
		}
		return entityClass.getSimpleName().toUpperCase();
	}
	
	public static class ApplicationDatabaseException extends DataAccessException {

		private static final long serialVersionUID = 1L;

		public ApplicationDatabaseException(String msg) {
			super(msg);
		}
		
	}

	@Override
	protected DataAccessException customTranslate(String task, String sql, SQLException sqlEx) {
		//TODO ARRUMAR ESSA ALGORITMO (FAZER HIGH COHESION.. LOW COUPLING)
		//System.out.println(task+" - "+sql);
		if(sqlEx.getNextException() != null){
			sqlEx = sqlEx.getNextException();//tentar buscar a excecao mais especifica
		}
		String errorMessage = sqlEx.getMessage();
		Matcher matcher = pattern.matcher(errorMessage);
		Matcher matcherIngles = patternIngles.matcher(errorMessage);
		Matcher matcherNullIngles = patternInglesNull.matcher(errorMessage);
		Matcher matcherNull = patternNull.matcher(errorMessage);
		System.out.println(">>> "+errorMessage);
		if(!matcher.find()){
			matcher = matcherIngles;
		} else {
			matcher.reset();
		}
		if(matcher.find()){
			//exce��o de FK
			//String fk_name = matcher.group(2);
			String fk_table_name = matcher.group(3).toUpperCase();
			String pk_table_name = matcher.group(1).toUpperCase();
			String fkTableDisplayName = null;
			String pkTableDisplayName = null;
//			try {
//				DatabaseMetaData metaData = dataSource.getConnection().getMetaData();
//				ResultSet importedKeys = metaData.getImportedKeys(null,null, fk_table_name);
//				
//				while(importedKeys.next()){
//					if(importedKeys.getString("FK_NAME").equals(fk_name)){
//						pk_table_name = importedKeys.getString("PKTABLE_NAME");
//						if(pk_table_name != null){
//							pk_table_name = pk_table_name.toUpperCase();
//						}
//					}
//				}
//			} catch (SQLException e) {
//				//se nao conseguir o metadata .. vazar
//				log.warn("N�o foi poss�vel conseguir o metadata do banco para ler informacoes de FK.");
//				return null;
//			}
			
			Class<?>[] entities = ClassManagerFactory.getClassManager().getClassesWithAnnotation(Entity.class);
			pkTableDisplayName = pk_table_name;
			fkTableDisplayName = fk_table_name;
			for (Class<?> entityClass : entities) {
				String tableName = getTableName(entityClass);
				if(tableName.equals(pk_table_name)){
					pkTableDisplayName = BeanDescriptorFactory.forClass(entityClass).getDisplayName();
				}
				if(tableName.equals(fk_table_name)){
					fkTableDisplayName = BeanDescriptorFactory.forClass(entityClass).getDisplayName();
				}
			}
			
			String mensagem = null;
			if(sql.toLowerCase().trim().startsWith("delete")){
				mensagem = "N�o foi poss�vel remover "+pkTableDisplayName+". Existe(m) registro(s) vinculado(s) em "+fkTableDisplayName+".";
			} else if(sql.toLowerCase().trim().startsWith("update")){
				mensagem = "N�o foi poss�vel atualizar "+fkTableDisplayName+". A refer�ncia para "+pkTableDisplayName+" � inv�lida.";
			} else if(sql.toLowerCase().trim().startsWith("insert")){
				mensagem = "N�o foi poss�vel inserir "+fkTableDisplayName+". A refer�ncia para "+pkTableDisplayName+" � inv�lida.";
			}
			return new ForeignKeyException(mensagem);
		} else if(matcherNullIngles.find()){
			return new ApplicationDatabaseException(errorMessage);
		} else if(matcherNull.find()){
			return new ApplicationDatabaseException(errorMessage);
		} else {
			int indexOf = errorMessage.indexOf("APP");
			if(indexOf > 0){
				errorMessage = errorMessage.substring(indexOf+3);
				return new ApplicationDatabaseException(errorMessage);
			}
		}
		return null;
	}
	
	public static void main(String[] args) {
		String msg = "ERROR: update or delete on \"campus\" violates foreign key constraint \"fk_curso_campus\" on \"curso\"\n  Detalhe: Key (cdcampus)=(1) is still referenced from table \"curso\".";
		Matcher matcher = patternIngles.matcher(msg);
		if(matcher.find()){
			//exce��o de FK
			String fk_name = matcher.group(2);
			System.out.println(fk_name);
			String fk_table_name = matcher.group(3).toUpperCase();
			System.out.println(fk_table_name);
			String pk_table_name = matcher.group(1).toUpperCase();
			System.out.println(pk_table_name);
		} else {
			System.out.println("false");
		}
	}
}
