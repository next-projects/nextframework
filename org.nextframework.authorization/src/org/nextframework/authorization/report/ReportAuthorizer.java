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

import org.nextframework.authorization.UserAuthorization;

public class ReportAuthorizer {
	
	private ReportAuthorizer(){
		
	}

	private static ReportAuthorizer instance;
	public static ReportAuthorizer getInstance(){
		if(instance == null){
			instance = new ReportAuthorizer();
		}
		return instance;
	}
	

	public boolean isAuthorized(String acao, UserAuthorization authorization){
        if(!(authorization instanceof ReportAuthorization)){
            throw new IllegalArgumentException("The type of Authorization used by "+this.getClass().getName()+" must be "+ReportAuthorization.class.getName());
        }
		return ((ReportAuthorization)authorization).canGenerate();
	}
}
