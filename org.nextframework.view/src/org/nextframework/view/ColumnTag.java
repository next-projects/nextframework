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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.nextframework.core.web.NextWeb;
import org.nextframework.util.Util;
import org.nextframework.view.DataGridTag.Status;

/**
 * @author rogelgarcia
 * @since 27/01/2006
 * @version 1.1
 */
public class ColumnTag extends BaseTag {

	public static final String COLUMN_RESIZE_CODE_BEGIN = "<table class=\"datagridresizeblocktable\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\"><tr><th class=\"datagridoriginalcontents\" style=\"background-color: transparent; background-image: none;\">";
	public static final String COLUMN_RESIZE_CODE_END = "</th><th class=\"datagridresizeblock\" onmousedown=\"datagridStartResizeColumn(event, this, '{id}')\">&nbsp;</th></tr></table>";
	
	public static final String REGISTERING_DATAGRID = "REGISTERING_DATAGRID";

	protected String header;
	
	protected String order;
	
	protected String footer;

	protected DataGridTag dataGrid;
	
	protected boolean hasBodyTag = false;

	public void setHasBodyTag(boolean hasBodyTag) {
		this.hasBodyTag = hasBodyTag;
	}

	@Override
	protected void doComponent() throws Exception {
		if (dataGrid == null) {
			dataGrid = findParent2(DataGridTag.class, true);
		}
		// pega o corpo atual da tag (dependendo o status do datagrid apenas uma
		// parte HEADER, BODY OU FOOTER
		// será renderizado)
		Map<String, Object> mapHeader = new HashMap<String, Object>();
		Map<String, Object> mapBody = new HashMap<String, Object>();
		Set<String> keySet = getDynamicAttributesMap().keySet();
		for (String string : keySet) {
			if(string.startsWith("body")){
				mapBody.put(string.substring("body".length()), getDynamicAttributesMap().get(string));
			} else if(string.startsWith("header")) {
				mapHeader.put(string.substring("header".length()).toLowerCase(), getDynamicAttributesMap().get(string));
			} else {
				mapBody.put(string, getDynamicAttributesMap().get(string));
				mapHeader.put(string, getDynamicAttributesMap().get(string));
			}
		}
		if(dataGrid.getCurrentStatus() == Status.REGISTER){
			pushAttribute(REGISTERING_DATAGRID, true);
			//temos que falar que estamos registrando um dataGrid para determinados códigos do corpo do column não ser executados. Por exemplo validação
		}
		String tagBody = null;
		if(!(dataGrid.getCurrentStatus() == Status.HEADER && header != null)){// SE ESTIVER NA ETAPA DE HEADER.. MAS JÁ TIVER HEADER.. NAO INVOCAR O CORPO
			tagBody = getBody();
		}
		
		
		if(dataGrid.getCurrentStatus() == Status.REGISTER){
			popAttribute(REGISTERING_DATAGRID);
		}
		switch (dataGrid.getCurrentStatus()) {
		case HEADER:
			if(dataGrid.isRenderResizeColumns()){
				//se for reziseColumns não poderá haver colunas com width em percentual
				Object styleO = mapHeader.get("style");
				if(styleO != null){
					String style = styleO.toString();
					mapHeader.put("style", checkStyleForHeaderNoPercent(style));
				}
			}
			if (header == null && Util.strings.isEmpty(tagBody)) {
				header = "";
			} else if (header == null && !Util.strings.isEmpty(tagBody) && tagBody.trim().startsWith("<!--HEADER-->")) {
				getOut().print(tagBody);
				//o tagBody já conterá o TD então nao vamos continuar 
				break;
			}
			if(header == null){
				header = "";
			}
			dataGrid.onRenderColumnHeader(header);
			if(order == null){
				String contents = doResizeColumnContents(header, dataGrid);
				getOut().print("<th "+getDynamicAttributesToString(mapHeader)+">");
				dataGrid.onRenderColumnHeaderBody();
				getOut().print(contents + "</th>"); 
			} else{
				String orderLink = getRequest().getContextPath()+NextWeb.getRequestContext().getRequestQuery()+"?orderBy="+order;
					
				//Verifica URL Sufix
				orderLink = WebUtils.rewriteUrl(orderLink);
				String contents = "<a class=\"order\" href=\""+orderLink+"\">"+header+"</a>";
				getOut().print("<th " + getDynamicAttributesToString(mapHeader) + ">"); 
				dataGrid.onRenderColumnHeaderBody();
				getOut().print(doResizeColumnContents(contents, dataGrid) + "</th>");
			}
			break;
		case BODY:
			if(dataGrid.isRenderResizeColumns()){
				//se for reziseColumns não poderá haver colunas com width em percentual
				Object styleO = mapBody.get("style");
				if(styleO != null){
					String style = styleO.toString();
					mapBody.put("style", checkStyleForHeaderNoPercent(style));
				}
			}
			if (!hasBodyTag) {
				getOut().print("<td"+getDynamicAttributesToString(mapBody)+">");
				getOut().print(tagBody == null || tagBody.trim().equals("")? "&nbsp;" : tagBody.trim());
				getOut().print("</td>");
			} else {
				//Adicionado, porque o tagBody pode vir com <td ...> ... </td> e precisa validar se o conteúdo entre as tags é em branco.
				//modificado por pedrogoncalves em 17/04/2007
				
				//Código removido, o uso de expressão regular estava deixando o datagrid lento, foi alterado para fazer semelhante no arquivo BodyTag.java
				//modificado por pedrogoncalves em 20/04/2007
				
//				Pattern pattern = Pattern.compile("<td (.*?)>(.*)</td>",Pattern.DOTALL);
//				Matcher matcher = pattern.matcher(tagBody.trim());
//				if (matcher.find()) {
//					String tdBody = matcher.group(2);
//					getOut().print(tdBody == null || tdBody.trim().equals("")? "<td "+matcher.group(1)+" >&nbsp;</td>" : tagBody);
//				} else {
//					getOut().print(tagBody);
//				}
				getOut().print(tagBody);
			}
			break;
		case DYNALINE:
			PanelRenderedBlock block = new PanelRenderedBlock();
			block.setBody(tagBody);				
			dataGrid.add(block);
			break;
		case FOOTER:
			if (Util.strings.isEmpty(footer)) {
				getOut().print("<td"+getDynamicAttributesToString()+"> </td>");
			} else {
				getOut().print(footer);
			}
			break;
		case REGISTER:
			// registrar
			// if (getJspBody()!=null) {
			// PrintWriter writer = new PrintWriter(new
			// ByteArrayOutputStream());
			// getJspBody().invoke(writer);
			// }
			if (Util.strings.isNotEmpty(header)) {
				dataGrid.setRenderHeader(true);
			}
			dataGrid.setHasColumns(true);
			//adicionado para dar informacoes ao datagrid sobre as colunas
			dataGrid.registerColumn(this);
			break;
		}
	}

	public static String checkStyleForHeaderNoPercent(String style) {
		//o Javascript irá remover os %
//		if(style == null){
//			return style;
//		}
//		int indexOfWidth = style.toLowerCase().indexOf("width");
//		if(indexOfWidth < 0){
//			return style;
//		}
//		int indexOfEndWidth = style.indexOf(';', indexOfWidth);
//		if(indexOfEndWidth < 0){
//			indexOfEndWidth = style.length();
//		}
//		String widthAttribute = style.substring(indexOfWidth, indexOfEndWidth);
//		if(widthAttribute.contains("%")){
//			String newStyle = style.substring(0, indexOfWidth) + "/* Nao pode utilizar width em % com colunas dinamicas */" + style.substring(indexOfEndWidth);
//			return newStyle;
//			//throw new NextException("Quando o datagrid estiver usando colunas dinâmicas, não pode haver estilos que atribuam width com percentuais nas colunas. "+style+" // "+newStyle);
//		}
		return style;
	}

	public static String doResizeColumnContents(String contents, DataGridTag dataGrid) {
		String begin = "";
		String end = "";
		if(dataGrid.isRenderResizeColumns()){
			end = COLUMN_RESIZE_CODE_END.replace("{id}", dataGrid.getId());
			begin = COLUMN_RESIZE_CODE_BEGIN;
		}
		contents = begin + contents + end;
		return contents;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}
	
	public void setFooter(String footer) {
		this.footer = footer;
	}
	
	public String getFooter() {
		return footer;
	}

}
