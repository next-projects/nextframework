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
package org.nextframework.authorization.report;

import java.io.Serializable;

import org.nextframework.authorization.UserAuthorization;
import org.nextframework.authorization.AuthorizationItem;
import org.nextframework.authorization.AuthorizationModuleSupport;
import org.nextframework.authorization.Permission;


public class ReportAuthorizationModule extends AuthorizationModuleSupport implements Serializable{

	private static final long serialVersionUID = 2485969955102950222L;

	public static final String RELATORIOS = "Relat�rios";
	
	protected static final String GENERATE = "generate";
	
	public ReportAuthorization createAuthorization(Permission[] permissoes) {
		boolean canGenerate = false;
		for (Permission permission : permissoes) {
			try {
				if (!canGenerate) {
					canGenerate = permission.getPermissionValue(GENERATE).equals("true");
				} else {
					break;
				}
			} catch (Exception e) {
				throw new IllegalArgumentException("Inconsistencia no objeto Permission! " +
                		"Faltando parametro ("+GENERATE+"): "+permission);
			}
		}
		ReportAuthorization authorization = new ReportAuthorization();
		authorization.setCanGenerate(canGenerate);
		return authorization;
	}

	public boolean isAuthorized(String acao, UserAuthorization autorizacao) {
		return ReportAuthorizer.getInstance().isAuthorized(acao, autorizacao);
	}

	public AuthorizationItem[] getAuthorizationItens() {
		return new AuthorizationItem[]{
				new AuthorizationItem(GENERATE,"gerar", new String[]{"true","false"})
		};
	}

	public String getAuthorizationGroupName() {
		return RELATORIOS;
	}

}
