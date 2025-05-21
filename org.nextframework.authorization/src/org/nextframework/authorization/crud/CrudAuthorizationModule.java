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
 *	 http://www.gnu.org/copyleft/lesser.html
 * 
 */
/*
 * Criado em 18/03/2005
 *
 */
package org.nextframework.authorization.crud;

import java.io.Serializable;

import org.nextframework.authorization.UserAuthorization;
import org.nextframework.authorization.AuthorizationItem;
import org.nextframework.authorization.AuthorizationModuleSupport;
import org.nextframework.authorization.Permission;

/**
 * @author rogelgarcia
 */
public class CrudAuthorizationModule extends AuthorizationModuleSupport implements Serializable {

	private static final long serialVersionUID = -7934456371062584642L;

	//public static final String ID_MODULO = "Crud";
	public static final String LIST_FORM = "Listagens / Entrada de dados";

	public static final String READ = "read";
	public static final String CREATE = "create";
	public static final String UPDATE = "update";
	public static final String DELETE = "delete";

	public CrudAuthorization createAuthorization(Permission[] permissoes) {

		boolean canCreate = false;
		boolean canRead = false;
		boolean canUpdate = false;
		boolean canDelete = false;

		for (int i = 0; i < permissoes.length; i++) {
			Permission permissao = permissoes[i];
			try {
				if (permissao == null) {
					throw new NullPointerException("permissao nula encontrada");
				}
				if (!canCreate) {
					String permissionvalue = permissao.getPermissionValue(CREATE);
					if (permissionvalue == null) {
						throw new IllegalArgumentException("Sem parâmetro: " + CREATE);
					}
					canCreate = permissionvalue.equals("true");
				}
				if (!canRead) {
					String permissionvalue = permissao.getPermissionValue(READ);
					if (permissionvalue == null) {
						throw new IllegalArgumentException("Sem parâmetro: " + READ);
					}
					canRead = permissionvalue.equals("true");
				}
				if (!canUpdate) {
					String permissionvalue = permissao.getPermissionValue(UPDATE);
					if (permissionvalue == null) {
						throw new IllegalArgumentException("Sem parâmetro: " + UPDATE);
					}
					canUpdate = permissionvalue.equals("true");
				}
				if (!canDelete) {
					String permissionvalue = permissao.getPermissionValue(DELETE);
					if (permissionvalue == null) {
						throw new IllegalArgumentException("Sem parâmetro: " + DELETE);
					}
					canDelete = permissionvalue.equals("true");
				}
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Inconsistencia no objeto Permission! " +
						"Faltando algum dos parametros (create, read, update ou delete): " + permissao + "..." + e.getMessage());
			} catch (NullPointerException e) {
				throw new IllegalArgumentException("NullPointerException inespereado no CRUDAuthorizationModule " + e.getMessage());
			}
		}

		CrudAuthorization autorizacao = new CrudAuthorization();
		autorizacao.setCreate(canCreate);
		autorizacao.setRead(canRead);
		autorizacao.setUpdate(canUpdate);
		autorizacao.setDelete(canDelete);
		return autorizacao;
	}

	public boolean isAuthorized(String acao, UserAuthorization autorizacao) {
		return CrudAuthorizer.getInstance().isAuthorized(acao, autorizacao);
	}

	public AuthorizationItem[] getAuthorizationItens() {
		return new AuthorizationItem[] {
				new AuthorizationItem(READ, "ler", new String[] { "true", "false" }),
				new AuthorizationItem(CREATE, "criar", new String[] { "true", "false" }),
				new AuthorizationItem(UPDATE, "editar", new String[] { "true", "false" }),
				new AuthorizationItem(DELETE, "excluir", new String[] { "true", "false" })
		};
	}

	public String getAuthorizationGroupName() {
		return LIST_FORM;
	}

}
