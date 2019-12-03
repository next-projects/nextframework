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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
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

public class OracleSQLErrorCodeSQLExceptionTranslator extends SQLErrorCodeSQLExceptionTranslator {

	static String errorRegex = "ORA-(?:\\d*?): restrição de integridade \\((.*)?\\) violada - registro filho localizado\n";
	static String errorRegexIngles = "ORA-(?:\\d*?): integrity constraint \\((.*)?\\) violated - child record found\n";
	//static String errorRegex = "(.*) statement conflicted with COLUMN REFERENCE constraint '(.*?)'. The conflict occurred in database '(?:.*?)', table '(.*?)', column '(.*?)'.";
	static Pattern pattern = Pattern.compile(errorRegex);
	static Pattern patternIngles = Pattern.compile(errorRegexIngles);

	static final Log log = LogFactory.getLog(OracleSQLErrorCodeSQLExceptionTranslator.class);

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
		if (table != null) {
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

		//TODO ARRUMAR (FAZER HIGH COHESION.. LOW COUPLING)
		if (sqlEx.getNextException() != null) {
			sqlEx = sqlEx.getNextException();
		}
		String errorMessage = sqlEx.getMessage();

		Matcher matcher = pattern.matcher(errorMessage);
		Matcher matcherIngles = patternIngles.matcher(errorMessage);
		if (!matcher.find()) {
			matcher = matcherIngles;
		} else {
			matcher.reset();
		}

		if (matcher.find()) {

			String fk_name = matcher.group(1);
			if (fk_name.contains(".")) {
				fk_name = fk_name.substring(fk_name.indexOf('.') + 1, fk_name.length());
			}

			String fk_table_name = matcher.group(1).toUpperCase();
			String pk_table_name = matcher.group(1).toUpperCase();
			String fkTableDisplayName = null;
			String pkTableDisplayName = null;
			Connection connection = null;

			try {
				connection = dataSource.getConnection();
				DatabaseMetaData metaData = connection.getMetaData();
				ResultSet importedKeys = metaData.getImportedKeys(null, null, null);
				while (importedKeys.next()) {
					System.out.println(importedKeys.getString("FK_NAME"));
					if (importedKeys.getString("FK_NAME").equals(fk_name)) {
						pk_table_name = importedKeys.getString("PKTABLE_NAME");
						if (pk_table_name != null) {
							pk_table_name = pk_table_name.toUpperCase();
						}

						fk_table_name = importedKeys.getString("FKTABLE_NAME");
						if (fk_table_name != null) {
							fk_table_name = fk_table_name.toUpperCase();
						}
					}
				}
			} catch (SQLException e) {
				log.warn("Não foi possível conseguir o metadata do banco para ler informacoes de FK.");
				return null;
			} finally {
				if (connection != null) {
					try {
						connection.close();
					} catch (SQLException e) {

					}
				}
			}

			Class<?>[] entities = ClassManagerFactory.getClassManager().getClassesWithAnnotation(Entity.class);
			pkTableDisplayName = pk_table_name;
			fkTableDisplayName = fk_table_name;
			for (Class<?> entityClass : entities) {
				String tableName = getTableName(entityClass);
				if (tableName.equals(pk_table_name)) {
					pkTableDisplayName = BeanDescriptorFactory.forClass(entityClass).getDisplayName();
				}
				if (tableName.equals(fk_table_name)) {
					fkTableDisplayName = BeanDescriptorFactory.forClass(entityClass).getDisplayName();
				}
			}

			String defaultMensagem;
			if (sql.toLowerCase().trim().startsWith("delete")) {
				defaultMensagem = "Não foi possível remover " + pkTableDisplayName + ". Existe(m) registro(s) vinculado(s) em " + fkTableDisplayName + ".";
			} else if (sql.toLowerCase().trim().startsWith("update")) {
				defaultMensagem = "Não foi possível atualizar " + fkTableDisplayName + ". A referência para " + pkTableDisplayName + " é inválida.";
			} else if (sql.toLowerCase().trim().startsWith("insert")) {
				defaultMensagem = "Não foi possível inserir " + fkTableDisplayName + ". A referência para " + pkTableDisplayName + " é inválida.";
			} else {
				defaultMensagem = "Não foi possível efetuar operação em " + pkTableDisplayName + ". Existe(m) registro(s) vinculado(s) em " + fkTableDisplayName + ".";
			}

			return new ForeignKeyException(defaultMensagem);

		} else {

			int indexOf = errorMessage.indexOf("APP");
			if (indexOf > 0) {
				errorMessage = errorMessage.substring(indexOf + 3);
				return new ApplicationDatabaseException(errorMessage);
			}

		}

		return null;
	}

}