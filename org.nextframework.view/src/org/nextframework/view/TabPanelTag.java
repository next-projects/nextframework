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

	protected List<PanelRenderedBlock> blocks = new ArrayList<PanelRenderedBlock>();

	protected Boolean renderUniqueTab = false;
	
	
	public Boolean getRenderUniqueTab() {
		return renderUniqueTab;
	}

	public void setRenderUniqueTab(Boolean renderUniqueTab) {
		this.renderUniqueTab = renderUniqueTab;
	}

	@Override
	protected void doComponent() throws Exception {
		doBody();
		String idTabPanel = id != null? id: generateUniqueId();
		int currentPanel = 0;
		List<TabPanelBlock> tabBlocks = new ArrayList<TabPanelBlock>();
		for (PanelRenderedBlock block : blocks) {
			tabBlocks.add(new TabPanelBlock(block, idTabPanel, currentPanel++));
		}
		int selectedIndex = 0;
		//verificar se já existia algum pré-selecionado
		if(id != null){
			String index = getRequest().getParameter("TABPANEL_"+id);
			if(index != null){
				selectedIndex = Integer.parseInt(index);
			}
		}
		
		renderScript(idTabPanel, tabBlocks, selectedIndex);
		renderSelectArea(idTabPanel, tabBlocks, selectedIndex);
		renderPanels(idTabPanel, tabBlocks, selectedIndex);
	}

	private void renderPanels(String idTabPanel, List<TabPanelBlock> tabBlocks, int selectedIndex) throws IOException {
		int index = 0;
		if(tabBlocks.size() == 1 && !renderUniqueTab){
			getOut().println(tabBlocks.get(0).getBody());
		} else {
			for (TabPanelBlock block : tabBlocks) {
				if(index == selectedIndex){
					getOut().print("<div style=\"width: 100%; clear: both; \" id=\""+block.getId()+"\">");	
				} else {
					getOut().print("<div style=\"width: 100%; clear: both; display: none;\" id=\""+block.getId()+"\">");
				}
				getOut().println(block.getBody());
				getOut().print("</div>");
				index ++;
			}	
		}
		
	}

	private void renderSelectArea(String idTabPanel, List<TabPanelBlock> tabBlocks, int selectedIndex) throws IOException {
		if(tabBlocks.size() == 1 && !renderUniqueTab){
			return;//manter compatibilidade com o estilo de rernderizacao antigo, onde, quando houver apenas uma aba.. não mostrar o tabpanel
		}
		pushAttribute("id", idTabPanel);
		pushAttribute("blocks", tabBlocks);
		pushAttribute("selectedIndex", selectedIndex);
		try {
			includeJspTemplate();
		} catch (ServletException e) {
			throw new RuntimeException(e);
		}
		popAttribute("id");
		popAttribute("blocks");
		popAttribute("selectedIndex");
		
		/*
		int index = 0;
		if(tabBlocks.size() > 1){
			getOut().println("<div id=\"tabPanel\">");
			getOut().println("<ul>");
			for (int i = 0; i < tabBlocks.size(); i++) {
				TabPanelBlock block = tabBlocks.get(i);
				String current = i==selectedIndex? "id=\"current\"" : "";
				String linkId = createLinkId(idTabPanel, i);
				getOut().println("<li "+current+"><a href=\"javascript:show"+idTabPanel+"('"+block.getId()+"', "+index+", '"+linkId+"'); "+escape(block.getOnSelectTab())+"\"  id=\""+linkId+"\">"+block.getTitle()+"</a></li>");
			
				index++;
			}
			getOut().println("</ul>");
			getOut().println("<div id=\"tabPanelClear\">&nbsp;</div>");
			getOut().println("</div>");
		}
		*/
	}

	private String createLinkId(String idTabPanel, int i) {
		return (idTabPanel+"_link_"+i);
	}

	private void renderScript(String idTabPanel, List<TabPanelBlock> tabBlocks, int selectedIndex) throws IOException {
		if(tabBlocks.size() <= 1 && !renderUniqueTab) return;//se o número de tabs nao for maior que 1, nao é necessário criar um script
		if(id != null){
			getOut().println("<input type=\"hidden\" name=\"TABPANEL_"+idTabPanel+"\" value=\""+selectedIndex+"\"/>");
		}
		FormTag form = findParent(FormTag.class);
		getOut().println("<script language=\"javascript\">");
		getOut().println("    function show" + idTabPanel + "(panel, index, linkid) {");
		for (int i = 0; i < tabBlocks.size(); i++) {
			TabPanelBlock block = tabBlocks.get(i);
			String linkId = createLinkId(idTabPanel, i);
			getOut().println("        hide" + idTabPanel + "('" + block.getId() + "')");
			getOut().println("        unselect" + idTabPanel + "('" + block.getId() + "', '"+linkId+"')");
		}
		getOut().println("        document.getElementById(panel).style.display = '';");
		getOut().println("        select" + idTabPanel + "(panel, index, linkid);");
		getOut().println("    }");
		getOut().println("    function hide" + idTabPanel + "(panel) {");
		getOut().println("        document.getElementById(panel).style.display = 'none';");
		getOut().println("    }");

		getOut().println("    function select" + idTabPanel + "(panel, index, linkid) {");
		if(form != null && id != null){
			getOut().println("        document.forms[\""+form.getName()+"\"].TABPANEL_"+idTabPanel+".value = index;");
		}
		getOut().println("        document.getElementById(linkid).parentNode.id = 'current';");
		getOut().println("    }");

		getOut().println("    function unselect" + idTabPanel + "(panel, linkid) {");
		getOut().println("        document.getElementById(linkid).parentNode.id = '';");
		getOut().println("    }");
		getOut().println("</script>");
	}

	public boolean addBlock(PanelRenderedBlock o) {
		return blocks.add(o);
	}

}

