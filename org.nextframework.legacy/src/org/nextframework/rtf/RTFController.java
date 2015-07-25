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
		
	protected Class<FILTRO> filtroClass;
	
	@SuppressWarnings("unchecked")
	public RTFController(){
		Class[] genericTypes = GenericTypeResolver.resolveTypeArguments(this.getClass(), RTFController.class);
//		if(genericTypes.length < 1){
//			boolean invalido = false;
//			//tentar a outra forma de Generics
//			{
//				try {
//					genericTypes = Util.generics.getGenericTypes2(this.getClass());
//					if(genericTypes.length != 1){
//						invalido = true;
//					}
//				} catch (Exception e) {
//					genericTypes = new Class[]{Object.class};
//				}
//				
//			}
//			if(invalido){
//				throw new RuntimeException("A classe "+this.getClass().getName()+" deve declarar um tipo genérico que indique o command que será usado");
//			}
//		}
		Class<?> clazz = genericTypes[0];
		filtroClass = (Class<FILTRO>) clazz;
	}
	
	@Override
	public Resource generateResource(WebRequestContext request, FILTRO filtro) throws Exception {
		RTF rtf = createRTF(request, filtro);
		RTFGenerator generator = LegacyRftUtils.getRTFGenerator();
		byte[] generate = generator.generate(rtf);
		Resource resource = new Resource("application/rtf", getRTFName(rtf), generate);
		return resource;
	}

	private String getRTFName(RTF rtf) {
		String name = rtf.getFileName();
        if(name == null){
        	name = rtf.getName();
        	if (name.indexOf('/') != -1) {
        		name = name.substring(rtf.getName().lastIndexOf('/') + 1);
			}
        }
        if(!name.endsWith(".rtf")){
        	name+=".rtf";
        }
		return name;
	}

	@Override
	public ModelAndView doFiltro(WebRequestContext request, FILTRO filtro) throws Exception {
		try {
			request.setAttribute("filtro", filtro);
			filtro(request, filtro);
		} catch (Exception e) {
			throw new ResourceGenerationException(FILTRO, e);
		}
		return getFiltroModelAndView(request, filtro);
	}

	protected void filtro(WebRequestContext request, FILTRO filtro) {
		
	}
	
	protected ModelAndView getFiltroModelAndView(WebRequestContext request, FILTRO filtro) {
		if (name == null) {
			if(!this.getClass().getSimpleName().endsWith("RTF")){
				throw new NextException("Um controller de rtf deve ter o sufixo RTF ou então setar a variável name");
			}
			String className = org.springframework.util.StringUtils.uncapitalize(this.getClass()
					.getSimpleName());
			name = className.substring(0, className.length()- "RTF".length());
		}
		return new ModelAndView("rtf/"+name,"filtro", filtro);
	}
	
	public abstract RTF createRTF(WebRequestContext request, FILTRO filtro) throws Exception;

}
