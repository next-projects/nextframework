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
package org.nextframework.controller.resource;

import org.nextframework.controller.Action;
import org.nextframework.controller.Command;
import org.nextframework.controller.DefaultAction;
import org.nextframework.controller.Input;
import org.nextframework.controller.MultiActionController;
import org.nextframework.core.web.WebRequestContext;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author rogelgarcia
 * @since 02/02/2006
 * @version 1.1
 */
public abstract class AbstractResourceSenderController<FILTRO> extends MultiActionController {

	public static final String FILTRO = "filtro";
	public static final String GERAR = "gerar";
	
	@DefaultAction
	@Action(FILTRO)
	@Command(session = true, validate = false)
	@Input(FILTRO)
	public abstract ModelAndView doFiltro(WebRequestContext request, FILTRO filtro) throws Exception;
	
	@Action(GERAR)
	@Command(session = true, validate = true)
	@Input(FILTRO)
	public abstract ModelAndView doGerar(WebRequestContext request, FILTRO filtro) throws Exception;

	/*
	public ModelAndView handleGenerationException(WebRequestContext request, ResourceGenerationException e) throws Throwable {
		// TODO MELHORAR O SISTEMA DE MENSAGENS
		String action = e.getAction();
		if (action.equals(FILTRO)) {
			throw new Exception("Não foi possível exibir a tela de listagem. "+e.getCause().getMessage(), e.getCause());
		} else if (action.equals(GERAR)) {
			request.addError(e.getCause());
			return goToAction(FILTRO);
		} else {
			throw new RuntimeException("Ação não suportada: "+action);
		}
	}
	
	@SuppressWarnings("unchecked")
	public ModelAndView handleServletRequestBidingException(WebRequestContext request, ServletRequestBindingException e) throws Exception {
		String action = request.getParameter(MultiActionController.ACTION_PARAMETER);
		BindException errors = (BindException) e.getRootCause();
		
		((DefaultWebRequestContext) request).setBindException(errors);
		// TODO VERIFICAR SE ESTÁ DE ACORDO
		if (action.equals(FILTRO)) {
			return doFiltro(request, (FILTRO) errors.getTarget());
		} else if (action.equals(GERAR)) {
			return doFiltro(request, (FILTRO) errors.getTarget());
		} else {
			throw new RuntimeException("Ação não suportada");
		}
	}
	*/
}

