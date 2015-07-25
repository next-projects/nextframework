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
package org.nextframework.view.template;

import java.io.CharArrayWriter;

import javax.servlet.jsp.tagext.JspFragment;

import org.nextframework.controller.crud.CrudContext;
import org.nextframework.exception.NextException;
import org.nextframework.util.Util;

/**
 * @author rogelgarcia
 * @since 03/02/2006
 * @version 1.1
 */
public class FormViewTag extends TemplateTag {

	protected String title;
	protected boolean showListLink = true;
	protected JspFragment linkArea;

	public JspFragment getLinkArea() {
		return linkArea;
	}

	public void setLinkArea(JspFragment linkArea) {
		this.linkArea = linkArea;
	}

	public boolean isShowListLink() {
		return showListLink;
	}

	public void setShowListLink(boolean showListagemLink) {
		this.showListLink = showListagemLink;
	}
	
	@Deprecated
	public boolean isShowListagemLink() {
		return showListLink;
	}
	
	@Deprecated
	public void setShowListagemLink(boolean showListagemLink) {
		this.showListLink = showListagemLink;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String titulo) {
		this.title = titulo;
	}
	
	@Deprecated
	public String getTitulo() {
		return title;
	}
	@Deprecated
	public void setTitulo(String titulo) {
		this.title = titulo;
	}

	@Override
	protected void doComponent() throws Exception {
		if(Util.strings.isEmpty(title)){
//			titulo = (String) getPageContext().findAttribute("TEMPLATE_beanDisplayName");
			title = CrudContext.getCurrentInstance().getDisplayName();
		}
		pushAttribute("crudContext", CrudContext.getCurrentInstance());
		pushAttribute("entradaTag", this);
		includeJspTemplate();
		popAttribute("entradaTag");
		popAttribute("crudContext");
	}
	
	/**
	 * método para ser chamado do template
	 * @return
	 */
	public String getInvokeLinkArea(){
		CharArrayWriter charArrayWriter = new CharArrayWriter();
		try {
			if (linkArea != null) {
				linkArea.invoke(charArrayWriter);
			}
		} catch (Exception e) {
			throw new NextException(e);
		}
		return charArrayWriter.toString();
	}

}
