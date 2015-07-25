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
/*
 * Criado em 18/03/2005
 *
 */
package org.nextframework.authorization;


/**
 * An AuthorizationModule represents a type of authorization in the system.
 * The AuthorizationModule defines which AuthorizationItems it has. And verifies if the logged user
 * can execute a given operation.
 * <p>
 * An AuthorizationModule also creates UserAuthorization that represents the authorization specific of an user.
 * 
 * @author rogelgarcia
 */
public interface AuthorizationModule {
    
    public UserAuthorization createAuthorization(Permission[] permissions);
    
    public boolean isAuthorized(String action, Permission[] permissions);
    
    public boolean isAuthorized(String action, UserAuthorization authorization);
    
    public AuthorizationItem[] getAuthorizationItens();
    
    public String getAuthorizationGroupName();
    
    public void setPath(String path);
    
    public void setControllerClass(Class<?> controllerClass);
    
}
