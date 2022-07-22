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

/**
 * @author rogelgarcia
 * @since 31/01/2006
 * @version 1.1
 */
public class TabPanelTag extends BaseTag implements AcceptPanelRenderedBlock {

	private static final String TABPANEL_LAST_ID = "TABPANEL_LAST_ID";

	protected List<PanelRenderedBlock> blocks = new ArrayList<PanelRenderedBlock>();

	protected Boolean renderUniqueTab = false;
	protected String navClass;
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
		if (this.id != null) {
			String index = getRequest().getParameter("TABPANEL_" + this.id);
			if (index != null) {
				selectedIndex = Integer.parseInt(index);
			}
		}

		if (!getViewConfig().isUseBootstrap()) {
			renderScript(tabBlocks, selectedIndex);
		}
		renderSelectArea(tabBlocks, selectedIndex);
		renderPanels(tabBlocks, selectedIndex);
		if (!getViewConfig().isUseBootstrap()) {
			renderSelectedPanelScript(tabBlocks, selectedIndex);
		}

		if (body != null) {
			getOut().println(body);
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

	private void renderPanels(List<TabPanelBlock> tabBlocks, int selectedIndex) throws IOException {
		int index = 0;
		if (tabBlocks.size() == 1 && !renderUniqueTab) {
			getOut().println(tabBlocks.get(0).getBody());
		} else {
			String css = contentClass != null ? " class=\"" + contentClass + "\"" : "";
			getOut().println("<div" + css + ">");
			for (TabPanelBlock block : tabBlocks) {
				String cssb = index == selectedIndex ? selectedClass != null ? " class=\"" + selectedClass + "\"" : "" : unselectedClass != null ? " class=\"" + unselectedClass + "\"" : "";
				getOut().print("<div" + cssb + " id=\"" + block.getId() + "\">");
				getOut().print(block.getBody());
				getOut().print("</div>");
				index++;
			}
			getOut().print("</div>");
		}
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

	private String createLinkId(String idTabPanel, int i) {
		return (idTabPanel + "_link_" + i);
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
		getOut().println("        document.getElementById(linkid).parentNode.className = 'active';");
		getOut().println("    }");
		getOut().println("    function unselect" + this.id + "(panel, linkid) {");
		getOut().println("        document.getElementById(linkid).parentNode.className = '';");
		getOut().println("    }");
		getOut().println("</script>");
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

	public String getNavClass() {
		return navClass;
	}

	public void setNavClass(String navClass) {
		this.navClass = navClass;
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