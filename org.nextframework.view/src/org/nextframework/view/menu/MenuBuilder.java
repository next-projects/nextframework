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
package org.nextframework.view.menu;

import java.util.Iterator;

import org.nextframework.util.Util;

public class MenuBuilder {
	
	public static final String ICONE_PADRAO = "&nbsp;&nbsp;&nbsp;&nbsp;";
	
	protected int identation;
	protected String urlPrefix;

	public String getUrlPrefix() {
		return urlPrefix;
	}

	public void setUrlPrefix(String urlPrefix) {
		this.urlPrefix = urlPrefix;
	}
	
	public String build(Menu menu){
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append('[').append('\n');
		identation = 1;
		for (Iterator<Menu> iter = menu.getSubmenus().iterator(); iter.hasNext();) {
			Menu submenu = iter.next();
			build(submenu, stringBuilder,iter.hasNext(), false);
		}
		stringBuilder.append(']');
		return stringBuilder.toString();
	}

	private void build(Menu menu, StringBuilder stringBuilder, boolean hasNext, boolean iconePadrao) {
		ident(stringBuilder);
		if(menu.getTitle()!= null && menu.getTitle().startsWith("---")){
			stringBuilder.append("_cmSplit");
			if(hasNext){
				stringBuilder.append(',');
			}
			return;
		}
		openMenu(stringBuilder);
		
		String icon = menu.getIcon();
		if(iconePadrao && Util.strings.isEmpty(icon)){
			icon = ICONE_PADRAO;
		} else if (Util.strings.isNotEmpty(icon)){
			icon = "<img src=\""+icon+"\" align=\"absmiddle\">";
		}
		printItem(icon, stringBuilder);
		stringBuilder.append(',');
		
		printItem(menu.getTitle(), stringBuilder);		
		stringBuilder.append(',');
		
		
		/*
		 * Esse código foi inserido por causa do internet explorer
		 * Quando se tentava fazer download de algum arquivo (relatorio por exemplo)
		 * aparecia a janela de download e então se fazia o download do arquivo
		 * Quando se pedia através do menu a tela do mesmo relatório aparecia a janela de download 
		 * e não a tela. O código abaixo contorna esse problema.. O parametro força o IE a pedir a url novamente e por 
		 * consequencia o JSP é mostrado e não a janela de download
		 */
		String url = menu.getUrl();
//		if(url.contains("?")){
//			url += "&NEXT_forceReload=";
//		} else {
//			url += "?NEXT_forceReload=";
//		}
		
		if(urlPrefix != null && Util.strings.isNotEmpty(url) && !url.startsWith("javascript:")) {
			url = urlPrefix + url;
		}
		//Verifica URL Sufix
		//url = BaseTag.applyUrlSufix(Next.getRequestContext(), url, null);
		printItem(url, stringBuilder);
		stringBuilder.append(',');
		
		printItem(menu.getTarget(), stringBuilder);
		stringBuilder.append(',');
		printItem(menu.getDescription(), stringBuilder);
		if (menu.containSubMenus()) {
			stringBuilder.append(',').append('\n');
			identation++;
			for (Iterator<Menu> iter = menu.getSubmenus().iterator(); iter.hasNext();) {
				Menu submenu = iter.next();
				build(submenu, stringBuilder,iter.hasNext(), true);
			}
			identation--;
			ident(stringBuilder);
			closeMenu(stringBuilder, hasNext);
		} else {
			closeMenu(stringBuilder, hasNext);
		}
	}

	private void closeMenu(StringBuilder stringBuilder, boolean hasNext) {
		stringBuilder.append(']');
		if(hasNext){
			stringBuilder.append(',');
		}
		stringBuilder.append('\n');
	}

	private void printItem(String texto, StringBuilder stringBuilder) {
		stringBuilder.append('\'');
		stringBuilder.append(texto);
		stringBuilder.append('\'');
	}

	private void openMenu(StringBuilder stringBuilder) {
		stringBuilder.append('[');
	}

	private void ident(StringBuilder stringBuilder) {
		for (int i = 0; i < identation; i++) {
			stringBuilder.append("    ");
		}
		
	}
}
