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
import org.nextframework.authorization.User;
import org.nextframework.controller.MultiActionController;
import org.nextframework.exception.NextException;
import org.nextframework.util.Util;
import org.nextframework.view.template.PropertyConfigTag;
import org.nextframework.web.WebUtils;

/**
 * @author rogelgarcia
 * @since 26/01/2006
 * @version 1.1
 */
public class LinkTag extends BaseTag {

	// atributos
	protected String confirmationMessage;//mensagem de confirmacao.. (janela javascript)

	protected String url;

	protected String action;

	protected String img;

	protected String description;

	protected String type;

	protected String parameters;

	enum Type {
		BUTTON, IMAGE, LINK
	}

	// extra

	private String onclick;

	@Override
	protected String getSubComponentName() {
		return getResolvedType().toString();
	}

	@Override
	protected void doComponent() throws Exception {

		boolean hasAuthorization = hasAuthorization();
		url = buildFullUrl();

		if (!hasAuthorization) {
			getOut().println("<!-- Sem autorização para acessar: " + url + "-->");
			return;
		}

		//corpo = getBody();

		Type tipo = getResolvedType();

		if (tipo == Type.BUTTON) {
			boolean disabled = "disabled".equals(getDynamicAttributesMap().get("disabled"));
			boolean enabled = "false".equals(getDynamicAttributesMap().get("disabled"));

			if (!enabled) {
				PropertyConfigTag propertyConfig = findParent(PropertyConfigTag.class);
				DataGridTag dataGridTag = findParent(DataGridTag.class);
				if (propertyConfig != null && Boolean.TRUE.equals(propertyConfig.getDisabled())
						&& (dataGridTag == null || dataGridTag.getCurrentStatus() != DataGridTag.Status.DYNALINE)) {
					if (disabled) {
						getDynamicAttributesMap().put("originaldisabled", "disabled");
					}
					getDynamicAttributesMap().put("disabled", "disabled");
				}
			} else {
				getDynamicAttributesMap().remove("disabled");
			}
		}

		switch (tipo) {
			case IMAGE:
				includeTextTemplate("image");
				break;
			case BUTTON:
				if (url.startsWith("javascript:")) {
					url = url.substring("javascript:".length());
				} else {
					url = "window.location='" + url + "'";
				}
				if (Util.strings.isNotEmpty(confirmationMessage)) {
					url = "if(confirm('" + confirmationMessage + "')){" + url + "}";
				}
				includeTextTemplate("button");
				break;
			case LINK:
				includeTextTemplate("link");
				break;
		}

	}

	private boolean hasAuthorization() {
		try {
			String partialURL = getPartialURL();
			if (partialURL.contains("?")) {
				partialURL = partialURL.substring(0, partialURL.indexOf('?'));
			}
			User user = Authorization.getUserLocator().getUser();
			return Authorization.getAuthorizationManager().isAuthorized(partialURL, action, user);
		} catch (Exception e) {
			throw new NextException("Problema ao verificar autorização", e);
		}
	}

	private String getPartialURL() {
		if (url != null && url.startsWith(getRequest().getContextPath())) {
			return url.substring(getRequest().getContextPath().length());
		}
		String fullUrl = url == null ? WebUtils.getFirstUrl() : url;
		return fullUrl;
	}

	private Type getResolvedType() {
		Type type = Type.LINK;
		if ("button".equalsIgnoreCase(this.type)) {
			type = Type.BUTTON;
		}
		if (img != null) {
			type = Type.IMAGE;
		}
		return type;
	}

	private String buildFullUrl() {

		if (url != null && url.startsWith("javascript:")) {
			return url;
		}

		if (action != null && action.startsWith("javascript:")) {
			url = action;
			action = null;
			return url;
		}

		String fullUrl = url == null ? WebUtils.getFirstFullUrl() : (url.startsWith("/") ? WebUtils.getFullUrl(getRequest(), url) : url);
		String separator = fullUrl.contains("?") ? "&" : "?";
		if (action != null) {
			fullUrl += separator + MultiActionController.ACTION_PARAMETER + "=" + action;
			separator = "&";
		}

		// adicionar parameters na url
		if (parameters != null) {
			fullUrl += separator + parameters.replace(";", "&");
		}

		//Verifica URL Sufix
		fullUrl = WebUtils.rewriteUrl(fullUrl);

		return fullUrl;
	}

	public String getOnclick() {
		return onclick;
	}

	public String getAction() {
		return action;
	}

	public String getDescription() {
		return description;
	}

	public String getImg() {
		return img;
	}

	public String getParameters() {
		return parameters;
	}

	public String getType() {
		return type;
	}

	public String getUrl() {
		Type tipo = getResolvedType();
		if (confirmationMessage != null && (tipo == Type.LINK || tipo == Type.IMAGE)) {//TODO FAZER PARA OUTROS TIPOS
			if (url.startsWith("javascript: ")) {
				return "javascript: if(confirm('" + confirmationMessage + "')){" + url.substring("javascript:".length()) + "}";
			}
			return "javascript: if(confirm('" + confirmationMessage + "')){window.location = '" + url + "';}";
		}
		return url;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getConfirmationMessage() {
		return confirmationMessage;
	}

	public void setConfirmationMessage(String confirmationMessage) {
		this.confirmationMessage = confirmationMessage;
	}

}
