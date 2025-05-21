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
package org.nextframework.view;

import org.nextframework.authorization.Authorization;
import org.nextframework.exception.NextException;
import org.nextframework.web.WebUtils;

/**
 * Verifica se o usuário atual possui permissão para acessar a url especificada
 * 
 * @author Pedro Gonçalves
 * @since 31/07/2007
 * @version 1.0
 */
public class HasAuthorizationTag extends BaseTag {

	protected String url;
	protected String action = "";

	@Override
	protected void doComponent() throws Exception {
		if (action != null && action.contains(",")) {
			String[] split = action.split(",");
			boolean hasAuthorization = false;
			for (String string : split) {
				if (hasAuthorization(string))
					hasAuthorization = true;
			}
			if (hasAuthorization)
				doBody();

		} else if (hasAuthorization(action)) {
			doBody();
		}
	}

	private boolean hasAuthorization(String action) {
		try {
			String partialURL = getPartialURL();
			if (partialURL.contains("?")) {
				partialURL = partialURL.substring(0, partialURL.indexOf('?'));
			}
			return Authorization.getAuthorizationManager().isAuthorized(partialURL, action, Authorization.getUserLocator().getUser());
		} catch (Exception e) {
			throw new NextException("Problema ao verificar autorização", e);
		}
	}

	private String getPartialURL() {
		if (url != null && url.startsWith(getRequest().getContextPath())) {
			return url.substring(getRequest().getContextPath().length());
		}
		String fullUrl = url == null ? WebUtils.getFirstUrl() : (url.startsWith("/") ? url : url);
		return fullUrl;
	}

	public String getUrl() {
		return url;
	}

	public String getAction() {
		return action;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setAction(String action) {
		this.action = action;
	}

}
