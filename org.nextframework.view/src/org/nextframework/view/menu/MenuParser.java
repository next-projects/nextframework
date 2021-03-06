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

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class MenuParser {
	
	protected static final String DTD_LOCATION = "org/nextframework/view/menu/menu.dtd";
	protected ClassLoader classLoader = this.getClass().getClassLoader();
	
	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	
	public Menu parse(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException{
		if(inputStream == null){
			throw new NullPointerException("O arquivo de menu n�o foi encontrado");
		}
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		documentBuilder.setEntityResolver(new MenuEntityResolver(classLoader));
		
		Document document = documentBuilder.parse(inputStream);
		
		Menu menu = new Menu();
		createMenu(document.getDocumentElement(), menu);
		
		return menu;
	}

	private void createMenu(Node basenode, Menu basemenu) {
		NodeList childNodes = basenode.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE){
				Menu menu = new Menu();
				basemenu.addMenu(menu);
				NamedNodeMap map = node.getAttributes();
				fillMenuAttributes(menu, map);
				createMenu(node, menu);
			}
		}
	}

	private void fillMenuAttributes(Menu menu, NamedNodeMap map) {
		String icon = map.getNamedItem("icon").getNodeValue();
		String title = map.getNamedItem("title").getNodeValue();
		
		//I18N
//		String bundleKey = Util.locale.getBundleKey("menu."+title);
//		if (bundleKey != null){
//			title = bundleKey;
//		}

		String url = map.getNamedItem("url").getNodeValue();
		String target = map.getNamedItem("target").getNodeValue();
		String description = map.getNamedItem("description").getNodeValue();

		menu.setIcon(icon);
		menu.setTitle(title);
		menu.setUrl(url);
		menu.setTarget(target);
		menu.setDescription(description);
	}
}


class MenuEntityResolver implements EntityResolver{
	
	protected ClassLoader classLoader;

	public MenuEntityResolver(ClassLoader loader) {
		super();
		classLoader = loader;
	}

	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		return new InputSource(classLoader.getResourceAsStream(MenuParser.DTD_LOCATION));
	}
	
}