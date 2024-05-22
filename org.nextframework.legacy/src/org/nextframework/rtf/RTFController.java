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
package org.nextframework.rtf;

import org.nextframework.controller.resource.Resource;
import org.nextframework.controller.resource.ResourceGenerationException;
import org.nextframework.controller.resource.ResourceSenderController;
import org.nextframework.core.web.WebRequestContext;
import org.nextframework.exception.NextException;
import org.springframework.core.GenericTypeResolver;
import org.springframework.web.servlet.ModelAndView;

@Deprecated
public abstract class RTFController<FILTRO> extends ResourceSenderController<FILTRO> {

	protected String name;

	public void setName(String name) {
		this.name = name;
	}

	protected Class<FILTRO> filterClass;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public RTFController() {
		Class[] genericTypes = GenericTypeResolver.resolveTypeArguments(this.getClass(), RTFController.class);
		Class<?> clazz = genericTypes[0];
		filterClass = (Class<FILTRO>) clazz;
	}

	@Override
	public Resource generateResource(WebRequestContext request, FILTRO filter) throws Exception {
		RTF rtf = createRTF(request, filter);
		RTFGenerator generator = LegacyRftUtils.getRTFGenerator();
		byte[] generate = generator.generate(rtf);
		Resource resource = new Resource("application/rtf", getRTFName(rtf), generate);
		return resource;
	}

	private String getRTFName(RTF rtf) {
		String name = rtf.getFileName();
		if (name == null) {
			name = rtf.getName();
			if (name.indexOf('/') != -1) {
				name = name.substring(rtf.getName().lastIndexOf('/') + 1);
			}
		}
		if (!name.endsWith(".rtf")) {
			name += ".rtf";
		}
		return name;
	}

	@Override
	public ModelAndView doFilter(WebRequestContext request, FILTRO filter) throws Exception {
		try {
			request.setAttribute("filter", filter);
			filter(request, filter);
		} catch (Exception e) {
			throw new ResourceGenerationException(FILTER, e);
		}
		return getFilterModelAndView(request, filter);
	}

	protected void filter(WebRequestContext request, FILTRO filter) {

	}

	protected ModelAndView getFilterModelAndView(WebRequestContext request, FILTRO filter) {
		if (name == null) {
			if (!this.getClass().getSimpleName().endsWith("RTF")) {
				throw new NextException("Um controller de rtf deve ter o sufixo RTF ou então setar a variável name");
			}
			String className = org.springframework.util.StringUtils.uncapitalize(this.getClass()
					.getSimpleName());
			name = className.substring(0, className.length() - "RTF".length());
		}
		return new ModelAndView("rtf/" + name, "filtro", filter);
	}

	public abstract RTF createRTF(WebRequestContext request, FILTRO filtro) throws Exception;

}
