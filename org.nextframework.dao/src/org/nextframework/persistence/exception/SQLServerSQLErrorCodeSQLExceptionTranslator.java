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

public class SQLServerSQLErrorCodeSQLExceptionTranslator extends SQLErrorCodeSQLExceptionTranslator {

	static String errorRegex = "(.*) statement conflicted with COLUMN (?:SAME TABLE )?REFERENCE constraint '(.*?)'. The conflict occurred in database '(?:.*?)', table '(.*?)', column '(.*?)'.";
	static String errorRegex2 = "The (.*) statement conflicted with the REFERENCE constraint \"(.*)\". The conflict occurred in database \"(?:.*)\", table \"(.*)\", column '(.*)'.";
	static Pattern pattern = Pattern.compile(errorRegex);
	static Pattern pattern2 = Pattern.compile(errorRegex2);

	static final Log log = LogFactory.getLog(SQLServerSQLErrorCodeSQLExceptionTranslator.class);

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

	@Override
	protected DataAccessException customTranslate(String task, String sql, SQLException sqlEx) {
		//TODO ARRUMAR ESSA DESORDEM (FAZER HIGH COHESION.. LOW COUPLING)
		if (sqlEx.getErrorCode() == 547) {

			Matcher matcher = pattern.matcher(sqlEx.getMessage());
			if (!matcher.find()) {
				matcher = pattern2.matcher(sqlEx.getMessage());
				if (!matcher.find()) {
					return null;
				}
			}

			String fk_name = matcher.group(2);
			String fk_table_name = matcher.group(3).toUpperCase();
			if (fk_table_name.lastIndexOf('.') > 0) {
				fk_table_name = fk_table_name.substring(fk_table_name.lastIndexOf('.') + 1);
			}
			String pk_table_name = null;
			String fkTableDisplayName = null;
			String pkTableDisplayName = null;

			try {
				DatabaseMetaData metaData = dataSource.getConnection().getMetaData();
				ResultSet importedKeys = metaData.getImportedKeys(null, null, fk_table_name);
				while (importedKeys.next()) {
					if (importedKeys.getString("FK_NAME").equals(fk_name)) {
						pk_table_name = importedKeys.getString("PKTABLE_NAME");
						if (pk_table_name != null) {
							pk_table_name = pk_table_name.toUpperCase();
						}
					}
				}
			} catch (SQLException e) {
				log.warn("N�o foi poss�vel conseguir o metadata do banco para ler informacoes de FK.");
				return null;
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
			sql = matcher.group(1);
			if (sql.toLowerCase().trim().startsWith("delete")) {
				defaultMensagem = "N�o foi poss�vel remover " + pkTableDisplayName + ". Existe(m) registro(s) vinculado(s) em " + fkTableDisplayName + ".";
			} else if (sql.toLowerCase().trim().startsWith("update")) {
				defaultMensagem = "N�o foi poss�vel atualizar " + fkTableDisplayName + ". A refer�ncia para " + pkTableDisplayName + " � inv�lida.";
			} else if (sql.toLowerCase().trim().startsWith("insert")) {
				defaultMensagem = "N�o foi poss�vel inserir " + fkTableDisplayName + ". A refer�ncia para " + pkTableDisplayName + " � inv�lida.";
			} else {
				defaultMensagem = "N�o foi poss�vel efetuar opera��o em " + pkTableDisplayName + ". Existe(m) registro(s) vinculado(s) em " + fkTableDisplayName + ".";
			}

			return new ForeignKeyException(defaultMensagem, new RuntimeException(task));

		}

		return null;
	}

}