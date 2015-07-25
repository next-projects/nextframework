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

import org.nextframework.authorization.UserAuthorization;

/**
 * @author rogelgarcia
 */
public class CrudAuthorization implements UserAuthorization {
	
	protected boolean canCreate;
	protected boolean canRead;
	protected boolean canUpdate;
	protected boolean canDelete;
	
	public boolean canCreate() {
		return canCreate;
	}
	
	public boolean canDelete() {
		return canDelete;
	}
	
	public boolean canRead() {
		return canRead;
	}
	
	public boolean canUpdate() {
		return canUpdate;
	}
	
	public void setCreate(boolean canCreate) {
		this.canCreate = canCreate;
	}
	
	public void setDelete(boolean canDelete) {
		this.canDelete = canDelete;
	}
	
	public void setRead(boolean canRead) {
		this.canRead = canRead;
	}
	
	public void setUpdate(boolean canUpdate) {
		this.canUpdate = canUpdate;
	}
	
}