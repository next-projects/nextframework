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
package org.nextframework.controller.crud;

import org.nextframework.controller.Action;
import org.nextframework.controller.Command;
import org.nextframework.controller.DefaultAction;
import org.nextframework.controller.Input;
import org.nextframework.controller.MultiActionController;
import org.nextframework.controller.OnErrors;
import org.nextframework.core.web.WebRequestContext;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author rogelgarcia
 * @since 01/02/2006
 * @version 1.1
 */
public abstract class AbstractCrudController<FILTER extends ListViewFilter, FORMBEAN> extends MultiActionController {

	public static final String LIST = "list";

	public static final String FORM = "form";

	public static final String CREATE = "create";

	public static final String UPDATE = "update";

	public static final String VIEW = "view";

	public static final String SAVE = "save";

	public static final String DELETE = "delete";

	@DefaultAction
	@Action(LIST)
	@Input(LIST)
	@Command(session = true, validate = true)
	public abstract ModelAndView doList(WebRequestContext request, FILTER filter) throws CrudException;

	@Action(FORM)
	@Input(FORM)
	public abstract ModelAndView doForm(WebRequestContext request, FORMBEAN form) throws CrudException;

	@Action(CREATE)
	@OnErrors(LIST)
	public abstract ModelAndView doCreate(WebRequestContext request, FORMBEAN form) throws CrudException;

	@Action(VIEW)
	public ModelAndView doView(WebRequestContext request, FORMBEAN form) throws CrudException {
		request.setAttribute(VIEW, true);
		return doUpdate(request, form);
	}

	@Action(UPDATE)
	@OnErrors(LIST)
	public abstract ModelAndView doUpdate(WebRequestContext request, FORMBEAN form) throws CrudException;

	@Action(SAVE)
	@Command(validate = true)
	@Input(FORM)
	public abstract ModelAndView doSave(WebRequestContext request, FORMBEAN form) throws CrudException;

	@Action(DELETE)
	@OnErrors(LIST)
	public abstract ModelAndView doDelete(WebRequestContext request, FORMBEAN form) throws CrudException;

}
