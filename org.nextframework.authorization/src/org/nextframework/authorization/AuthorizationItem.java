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
 * Represents one item of an authorization.
 * 
 * In a CRUD operation there would be four itens:
 * Create, Read, Update, Delete
 * 
 * @author rogelgarcia
 */
public class AuthorizationItem {
	
    protected String id;
    protected String name;
    protected String[] values;
    protected String description;
    
    /**
     * @param id 
     */
    public AuthorizationItem(String id, String name, String[] values) {
        this.id = id;
        this.name = name;
        this.values = values;
    }
    
    public String getDescription() {
        return description;
    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String[] getValues() {
        return values;
    }
    public void setDescription(String descricao) {
        this.description = descricao;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setName(String nome) {
        this.name = nome;
    }
    public void setValues(String[] valores) {
        this.values = valores;
    }

}
