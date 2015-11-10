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
import org.nextframework.controller.MultiActionController;
import org.nextframework.core.standard.Next;
import org.nextframework.exception.NextException;
import org.nextframework.util.Util;
import org.nextframework.view.template.PropertyConfigTag;

/**
 * @author rogelgarcia
 * @since 26/01/2006
 * @version 1.1
 */
public class SubmitTag extends BaseTag {
	
	// atributos
	protected String url;

	protected String action;

	protected String img;

	protected String description;

	protected String type;

	protected String parameters;
	
	protected Boolean validate;
	
	protected String confirmationScript;
	
	enum Type {
		/* Adicionado SUBMIT em 22/05/2009 para dar suporte ao ENTER e fazer submit do form */
		BUTTON, IMAGE, LINK, SUBMIT
	}
	
	//extra
	//protected String formName;
	protected String onclick;

	public String getOnclick() {
		return onclick;
	}

	@Override
	protected void doComponent() throws Exception {
		if(action == null){
			//a action deve ser explicita para qual método deve ser chamado
			//action = NextWeb.getRequestContext().getLastAction();
		}
		boolean hasAuthorization = hasAuthorization();
		url = montarUrlCompleta();
		
		if(!hasAuthorization){
			getOut().println("<!-- Sem autorização para acessar: "+url+"-->");
			return;
		}
		FormTag form = findParent(FormTag.class, true);
		String formName = form.getName();
		String submitFunction = form.getSubmitFunction();
		if(!url.startsWith("javascript:")){
			if(validate != null){
				onclick = formName+".action = '"+url+"'; "+formName+".validate = '"+validate+"'; "+submitFunction+"()";
			} else {
				onclick = formName+".action = '"+url+"'; "+submitFunction+"()";	
			}	
		} else {
			onclick = url.substring("javascript:".length());;
		}
		
		if(getDynamicAttributesMap().get("onclick") != null){
			onclick = getDynamicAttributesMap().get("onclick")+";"+onclick;
			getDynamicAttributesMap().remove("onclick");
		}
		if(confirmationScript != null && confirmationScript.trim().length() > 0) {
			if(confirmationScript.contains(";")) {
				throw new NextException("O confirmationScript não pode conter ';' Ele deve ser uma expressão (ou chamada de função) booleana. " +
						"Se a expressão retornar true o submit será executado. confirmationScriptEncontrado: "+confirmationScript);
			}
			onclick = "if ("+confirmationScript+") {"+onclick+"}";
		}
		
		if (action != null) {
			action = formName+"."+MultiActionController.ACTION_PARAMETER+".value ='"+action+"';";
		} else {
			action = formName+"."+MultiActionController.ACTION_PARAMETER+".value = '';";//se o submit foi criado com action null... devemos enviar para action null
		}
		
		Type tipo = definirTipo();
		
		if(tipo == Type.BUTTON){
			boolean disabled = "disabled".equals(getDynamicAttributesMap().get("disabled"));
			boolean enabled = "false".equals(getDynamicAttributesMap().get("disabled"));
			
			if(!enabled){
				PropertyConfigTag propertyConfig = findParent(PropertyConfigTag.class);
				DataGridTag dataGridTag = findParent(DataGridTag.class);
				if(propertyConfig != null && Boolean.TRUE.equals(propertyConfig.getDisabled())
						&& (dataGridTag == null || dataGridTag.getCurrentStatus() != DataGridTag.Status.DYNALINE)){
					if(disabled){
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
			includeTextTemplate("button");
			break;
		case LINK:
			includeTextTemplate("link");
			break;
		case SUBMIT:
			includeTextTemplate("submit");
			break;
		}
	}
	
	private boolean hasAuthorization() {
		try {
			return Authorization.getAuthorizationManager().isAuthorized(getPartialURL(), action, Authorization.getUserLocator().getUser());
		} catch (Exception e) {
			throw new NextException("Problema ao verificar autorização", e);
		}
	}
	
	
	private String getPartialURL(){
		if (url != null && url.startsWith(getRequest().getContextPath())) {
			return url.substring(getRequest().getContextPath().length());
		}
		String fullUrl = url == null ? WebUtils.getFirstUrl() : (url.startsWith("/") ?  url : url);
		return fullUrl;
	}

	private Type definirTipo() {
		Type tipo = Type.BUTTON;
		if ("link".equalsIgnoreCase(type)) {
			tipo = Type.LINK;
		}
		if ("submit".equalsIgnoreCase(type)) {
			tipo = Type.SUBMIT;
		}
		if ("image".equalsIgnoreCase(type)) {
			tipo = Type.IMAGE;
		}
		if (img != null && Util.strings.isEmpty(type)) {
			tipo = Type.IMAGE;
		}
		return tipo;
	}

	private String montarUrlCompleta() {
		if(url != null && url.startsWith("javascript:")){
			return url;
		}
		if(action != null && action.startsWith("javascript:")){
			url = action;
			action = null;
			return url;
		}
		//updated in 2012-09-05 url.length == 0
		String fullUrl = url == null || url.length() == 0 ? WebUtils.getFirstFullUrl() : (url.startsWith("/") ? WebUtils.getFullUrl(getRequest(), url) : url);
		String separator = fullUrl.contains("?") ? "&" : "?";
		// adicionar parameters na url
		//comment fix #14.. can cause undesired effects.. in this case use & to separate parameters, instead of ; 
//		if (parameters != null) {
//			fullUrl += separator + parameters.replace(";", "&");
//		}
		
		//Verifica URL Sufix
		fullUrl = WebUtils.rewriteUrl(fullUrl);
		
		return fullUrl;
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

	public Boolean getValidate() {
		return validate;
	}

	public void setValidate(Boolean validate) {
		this.validate = validate;
	}

	public String getConfirmationScript() {
		return confirmationScript;
	}

	public void setConfirmationScript(String confirmationScript) {
		this.confirmationScript = confirmationScript;
	}

}
