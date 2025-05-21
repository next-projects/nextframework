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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import org.nextframework.util.Util;

/**
 * @author rogelgarcia
 * @since 31/01/2006
 * @version 1.1
 */
public class TabPanelTag extends BaseTag implements AcceptPanelRenderedBlock {

	private static final String TABPANEL_LAST_ID = "TABPANEL_LAST_ID";

	protected List<PanelRenderedBlock> blocks = new ArrayList<PanelRenderedBlock>();

	protected Boolean renderUniqueTab = false;
	protected String navPanelClass;
	protected String navClass;
	protected String navItemClass;
	protected String navLinkClass;
	protected String contentClass;
	protected String selectedClass;
	protected String unselectedClass;

	@Override
	protected void doComponent() throws Exception {

		if (this.id == null) {
			Integer lastId = (Integer) getRequest().getAttribute(TABPANEL_LAST_ID);
			lastId = lastId != null ? lastId + 1 : 1;
			getRequest().setAttribute(TABPANEL_LAST_ID, lastId);
			this.id = "Tabs" + lastId;
		}

		String body = null;
		if (getJspBody() != null) {
			body = getBody();
		}

		int currentPanel = 0;
		List<TabPanelBlock> tabBlocks = new ArrayList<TabPanelBlock>();
		for (PanelRenderedBlock block : blocks) {
			tabBlocks.add(new TabPanelBlock(block, this.id, currentPanel++));
		}

		//verificar se já existia algum pré-selecionado
		int selectedIndex = 0;
		String index = getRequest().getParameter("TABPANEL_" + this.id);
		if (index != null) {
			selectedIndex = Integer.parseInt(index);
		}

		renderScript(tabBlocks, selectedIndex);
		renderSelectArea(tabBlocks, selectedIndex);
		renderPanels(tabBlocks, selectedIndex);
		renderSelectedPanelScript(tabBlocks, selectedIndex);

		if (body != null) {
			getOut().println(body);
		}

	}

	private void renderScript(List<TabPanelBlock> tabBlocks, int selectedIndex) throws IOException {

		if (tabBlocks.size() <= 1 && !renderUniqueTab) {
			return;//se o número de tabs nao for maior que 1, nao é necessário criar um script
		}

		if (this.id != null) {
			getOut().println("<input type=\"hidden\" name=\"TABPANEL_" + this.id + "\" value=\"" + selectedIndex + "\"/>");
		}

		FormTag form = findParent(FormTag.class);
		getOut().println("<script language=\"javascript\">");
		getOut().println("    function show" + this.id + "(panel, index, linkid) {");
		for (int i = 0; i < tabBlocks.size(); i++) {
			TabPanelBlock block = tabBlocks.get(i);
			String linkId = createLinkId(this.id, i);
			getOut().println("        hide" + this.id + "('" + block.getId() + "');");
			getOut().println("        unselect" + this.id + "('" + block.getId() + "', '" + linkId + "');");
		}
		getOut().println("        document.getElementById(panel).style.display = 'unset';");
		getOut().println("        select" + this.id + "(panel, index, linkid);");
		getOut().println("    }");
		getOut().println("    function hide" + this.id + "(panel) {");
		getOut().println("        document.getElementById(panel).style.display = 'none';");
		getOut().println("    }");

		getOut().println("    function select" + this.id + "(panel, index, linkid) {");
		if (form != null && id != null) {
			getOut().println("        document.forms[\"" + form.getName() + "\"].TABPANEL_" + this.id + ".value = index;");
		}
		getOut().println("        next.style.addClass(document.getElementById(linkid), 'active');");
		getOut().println("        next.style.addClass(document.getElementById(linkid).parentNode, 'active');");
		getOut().println("    }");
		getOut().println("    function unselect" + this.id + "(panel, linkid) {");
		getOut().println("        next.style.removeClass(document.getElementById(linkid), 'active');");
		getOut().println("        next.style.removeClass(document.getElementById(linkid).parentNode, 'active');");
		getOut().println("    }");
		getOut().println("</script>");

	}

	private String createLinkId(String idTabPanel, int i) {
		return (idTabPanel + "_link_" + i);
	}

	private void renderSelectArea(List<TabPanelBlock> tabBlocks, int selectedIndex) throws IOException {

		if (tabBlocks.size() == 1 && !renderUniqueTab) {
			return;//manter compatibilidade com o estilo de rernderizacao antigo, onde, quando houver apenas uma aba.. não mostrar o tabpanel
		}

		pushAttribute("id", this.id);
		pushAttribute("blocks", tabBlocks);
		pushAttribute("selectedIndex", selectedIndex);

		try {
			includeJspTemplate();
		} catch (ServletException e) {
			throw new RuntimeException(e);
		}

		popAttribute("selectedIndex");
		popAttribute("blocks");
		popAttribute("id");

	}

	private void renderPanels(List<TabPanelBlock> tabBlocks, int selectedIndex) throws IOException {

		if (tabBlocks.size() == 1 && !renderUniqueTab) {
			getOut().println(tabBlocks.get(0).getBody());
		} else {

			int index = 0;
			String css = contentClass != null ? " class=\"" + contentClass + "\"" : "";
			getOut().println("<div" + css + ">");

			for (TabPanelBlock block : tabBlocks) {

				String columnStyleClass = index == selectedIndex ? selectedClass : unselectedClass;
				String blockClass = block.getStyleClass();
				if (blockClass != null) {
					columnStyleClass = (Util.strings.isNotEmpty(columnStyleClass) ? columnStyleClass + " " : "") + blockClass;
				}
				String classString = Util.strings.isNotEmpty(columnStyleClass) ? " class=\"" + columnStyleClass + "\"" : "";

				String blockStyle = block.getStyle();
				String styleString = Util.strings.isNotEmpty(blockStyle) ? " style=\"" + blockStyle + "\"" : "";

				getOut().print("<div" + classString + styleString + " id=\"" + block.getId() + "\">");
				getOut().print(block.getBody());
				getOut().print("</div>");

				index++;

			}

			getOut().print("</div>");

		}

	}

	protected void renderSelectedPanelScript(List<TabPanelBlock> tabBlocks, int selectedIndex) throws IOException {
		if (tabBlocks.size() > 1) {
			TabPanelBlock block = tabBlocks.get(selectedIndex);
			getOut().println("<script language=\"javascript\">");
			getOut().print("show" + this.id + "('" + block.getId() + "', " + selectedIndex + ", '" + this.id + "_link_" + selectedIndex + "');");
			getOut().print("</script>");
		}
	}

	public boolean addBlock(PanelRenderedBlock o) {
		return blocks.add(o);
	}

	public Boolean getRenderUniqueTab() {
		return renderUniqueTab;
	}

	public void setRenderUniqueTab(Boolean renderUniqueTab) {
		this.renderUniqueTab = renderUniqueTab;
	}

	public String getNavPanelClass() {
		return navPanelClass;
	}

	public void setNavPanelClass(String navPanelClass) {
		this.navPanelClass = navPanelClass;
	}

	public String getNavClass() {
		return navClass;
	}

	public void setNavClass(String navClass) {
		this.navClass = navClass;
	}

	public String getNavItemClass() {
		return navItemClass;
	}

	public void setNavItemClass(String navItemClass) {
		this.navItemClass = navItemClass;
	}

	public String getNavLinkClass() {
		return navLinkClass;
	}

	public void setNavLinkClass(String navLinkClass) {
		this.navLinkClass = navLinkClass;
	}

	public String getContentClass() {
		return contentClass;
	}

	public void setContentClass(String contentClass) {
		this.contentClass = contentClass;
	}

	public String getSelectedClass() {
		return selectedClass;
	}

	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}

	public String getUnselectedClass() {
		return unselectedClass;
	}

	public void setUnselectedClass(String unselectedClass) {
		this.unselectedClass = unselectedClass;
	}

}
