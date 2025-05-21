/**
 *  Copyright 2011 Alexandru Craciun, Eyal Kaspi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.stjs.javascript;

public class Dummy {

	@SuppressWarnings("unused")
	private static String[] clazz = { "HTMLAnchorElement", "HTMLAppletElement", "HTMLAreaElement", "HTMLBaseElement",
			"HTMLBaseFontElement", "HTMLBodyElement", "HTMLBRElement", "HTMLButtonElement", "HTMLCollection",
			"HTMLDirectoryElement", "HTMLDivElement", "HTMLDListElement", "HTMLDocument", "HTMLDOMImplementation",
			"HTMLElement", "HTMLFieldSetElement", "HTMLFontElement", "HTMLFormElement", "HTMLFrameElement",
			"HTMLFrameSetElement", "HTMLHeadElement", "HTMLHeadingElement", "HTMLHRElement", "HTMLHtmlElement",
			"HTMLIFrameElement", "HTMLImageElement", "HTMLInputElement", "HTMLIsIndexElement", "HTMLLabelElement",
			"HTMLLegendElement", "HTMLLIElement", "HTMLLinkElement", "HTMLMapElement", "HTMLMenuElement",
			"HTMLMetaElement", "HTMLModElement", "HTMLObjectElement", "HTMLOListElement", "HTMLOptGroupElement",
			"HTMLOptionElement", "HTMLParagraphElement", "HTMLParamElement", "HTMLPreElement", "HTMLQuoteElement",
			"HTMLScriptElement", "HTMLSelectElement", "HTMLStyleElement", "HTMLTableCaptionElement",
			"HTMLTableCellElement", "HTMLTableColElement", "HTMLTableElement", "HTMLTableRowElement",
			"HTMLTableSectionElement", "HTMLTextAreaElement", "HTMLTitleElement", "HTMLUListElement" };

/*
	public static void main2(String[] args) throws ClassNotFoundException, IOException {
		if (true) {
			throw new RuntimeException("xxx");
		}
		for (String c : clazz) {
			Class<?> cc = Class.forName("org.w3c.dom.html." + c);
			System.out.println(cc.getName());

			Set<String> methods = new HashSet<String>();
			PrintWriter out = new PrintWriter(new FileWriter("target/dom/" + cc.getSimpleName() + ".java"));
			out.println("package org.stjs.javascript.dom;");
			out.print("abstract public class " + cc.getSimpleName() + "");
			if (cc.getInterfaces().length > 0) {
				out.print(" extends " + cc.getInterfaces()[0].getSimpleName());
			}
			out.println("{");
			PropertyDescriptor[] descs = null;// PropertyUtils.getPropertyDescriptors(cc);
			for (PropertyDescriptor desc : descs) {
				out.println("public " + desc.getPropertyType().getSimpleName() + " " + desc.getName() + ";");
				if (desc.getReadMethod() != null) {
					methods.add(desc.getReadMethod().getName());
				}
				if (desc.getWriteMethod() != null) {
					methods.add(desc.getWriteMethod().getName());
				}
			}
			for (Method m : cc.getDeclaredMethods()) {
				if (!methods.contains(m.getName())) {
					out.print("abstract public " + m.getReturnType().getSimpleName() + " " + m.getName() + "(");
					for (int i = 0; i < m.getParameterTypes().length; ++i) {
						Class<?> paramType = m.getParameterTypes()[i];
						if (i > 0) {
							out.print(", ");
						}
						out.print(paramType.getSimpleName() + " arg" + i);
					}
					out.println(");");
				}
			}
			out.println("}");
			out.flush();
			out.close();
		}

	}

	public static void main(String[] args) {
		throw new RuntimeException();
	}*/
}
