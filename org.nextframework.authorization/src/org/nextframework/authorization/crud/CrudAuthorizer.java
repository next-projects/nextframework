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

import org.nextframework.authorization.Authorizer;
import org.nextframework.authorization.UserAuthorization;

/**
 * @author rogelgarcia
 */
public class CrudAuthorizer implements Authorizer {

	public static final String ACTION_LIST = "list";
	public static final String ACTION_VIEW = "view";
	public static final String ACTION_CREATE = "create";
	public static final String ACTION_UPDATE = "update";
	public static final String ACTION_SAVE = "save";
	public static final String ACTION_DELETE = "delete";
	public static final String ACTION_FORM = "form";

	public static final String METHOD_LIST = "doList";
	public static final String METHOD_VIEW = "doView";
	public static final String METHOD_CREATE = "doCreate";
	public static final String METHOD_UPDATE = "doUpdate";
	public static final String METHOD_SAVE = "doSave";
	public static final String METHOD_DELETE = "doDelete";
	public static final String METHOD_FORM = "doForm";

	private CrudAuthorizer() {
	}

	private static CrudAuthorizer instance;

	public synchronized static CrudAuthorizer getInstance() {
		if (instance == null) {
			instance = new CrudAuthorizer();
		}
		return instance;
	}

	public boolean isAuthorized(String action, UserAuthorization authorization) {
		if (!(authorization instanceof CrudAuthorization)) {
			throw new IllegalArgumentException(
					"The authorization used by " + CrudAuthorizer.class + " must be of type " + CrudAuthorization.class);
		}
		CrudAuthorization autorizacaoCrud = (CrudAuthorization) authorization;
		if (action == null || action.equals("") || action.equals(ACTION_LIST) || action.equals(METHOD_LIST) || action.equals(ACTION_VIEW) || action.equals(METHOD_VIEW)) {
			return autorizacaoCrud.canRead();
		} else {
			if (action.equals(ACTION_CREATE) || action.equals(METHOD_CREATE)) {
				return autorizacaoCrud.canCreate();
			} else if (action.equals(ACTION_UPDATE) || action.equals(METHOD_UPDATE)) {
				return autorizacaoCrud.canUpdate();
			} else if (action.equals(ACTION_SAVE) || action.equals(ACTION_FORM) || action.equals(METHOD_SAVE) || action.equals(METHOD_FORM)) {
				return autorizacaoCrud.canCreate() || autorizacaoCrud.canUpdate();
			} else if (action.equals(ACTION_DELETE) || action.equals(METHOD_DELETE)) {
				return autorizacaoCrud.canDelete();
			}
		}

		return true;
	}

}