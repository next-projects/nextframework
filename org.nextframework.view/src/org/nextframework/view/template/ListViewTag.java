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

/**
 * @author rogelgarcia
 * @since 03/02/2006
 * @version 1.1
 */
public class ListViewTag extends TemplateTag {

	protected String title;
	protected boolean showNewLink = true;
	protected String newLinkLabel = null;
	protected JspFragment linkArea;

	protected String linkBarStyleClass = null;
	protected String linkStyleClass = null;

	@Override
	protected void doComponent() throws Exception {

		CrudContext crudContext = CrudContext.getCurrentInstance();

		if (title == null && crudContext != null && crudContext.hasCustomDisplayName()) {
			title = crudContext.getDisplayName();
		}
		if (title == null) {
			title = getDefaultViewLabel("title", null);
		}
		if (title == null && crudContext != null) {
			title = crudContext.getDisplayName();
		}

		if (newLinkLabel == null) {
			newLinkLabel = getDefaultViewLabel("newLinkLabel", "Novo");
		}

		if (crudContext != null) {
			pushAttribute("crudContext", crudContext);
		}
		pushAttribute("listagemTag", this); //Legacy
		includeJspTemplate();
		popAttribute("listagemTag");
		if (crudContext != null) {
			popAttribute("crudContext");
		}

	}

	/**
	 * método para ser chamado do template
	 */
	public String getInvokeLinkArea() {
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Deprecated
	public String getTitulo() {
		return title;
	}

	@Deprecated
	public void setTitulo(String titulo) {
		this.title = titulo;
	}

	public boolean isShowNewLink() {
		return showNewLink;
	}

	public void setShowNewLink(boolean showNewLink) {
		this.showNewLink = showNewLink;
	}

	public String getNewLinkLabel() {
		return newLinkLabel;
	}

	public void setNewLinkLabel(String newLinkLabel) {
		this.newLinkLabel = newLinkLabel;
	}

	public JspFragment getLinkArea() {
		return linkArea;
	}

	public void setLinkArea(JspFragment linkArea) {
		this.linkArea = linkArea;
	}

	public String getLinkBarStyleClass() {
		return linkBarStyleClass;
	}

	public void setLinkBarStyleClass(String linkBarStyleClass) {
		this.linkBarStyleClass = linkBarStyleClass;
	}

	public String getLinkStyleClass() {
		return linkStyleClass;
	}

	public void setLinkStyleClass(String linkStyleClass) {
		this.linkStyleClass = linkStyleClass;
	}

}