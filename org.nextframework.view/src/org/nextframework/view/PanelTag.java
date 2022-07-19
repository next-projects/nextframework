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

import org.nextframework.util.Util;
import org.nextframework.view.template.PropertyTag;

/**
 * @author rogelgarcia
 * @since 30/01/2006
 * @version 1.1
 */
public class PanelTag extends BaseTag {

	protected Integer colspan;
	protected String title;
	protected String propertyRenderAs;
	protected String onSelectTab;

	@Override
	protected void doComponent() throws Exception {

		BaseTag findFirst2 = findFirst2(AcceptPanelRenderedBlock.class, PanelTag.class, ColumnTag.class);
		if (findFirst2 != null && findFirst2 instanceof AcceptPanelRenderedBlock) {

			String body;
			if (getJspBody() != null) {
				body = getBody();
			} else {
				body = "";
			}

			Map<String, Object> attrs = new HashMap<String, Object>();
			attrs.putAll(getDynamicAttributesMap());
			if ("".equals(attrs.get("style"))) {
				attrs.remove("style");
			}
			if ("".equals(attrs.get("class"))) {
				attrs.remove("class");
			}
			if (colspan != null) {
				attrs.put("colspan", colspan);
			}
			if (title != null) {
				attrs.put("title", title);
			}
			if (onSelectTab != null) {
				attrs.put("onselecttab", onSelectTab);
			}
			if (id != null) {
				attrs.put("id", id);
			}

			PanelRenderedBlock renderedBlock = new PanelRenderedBlock();
			renderedBlock.setBody(body);
			renderedBlock.setProperties(attrs);

			AcceptPanelRenderedBlock acceptPanel = findParent2(AcceptPanelRenderedBlock.class, true);
			acceptPanel.addBlock(renderedBlock);

		} else {

			Object style = getDynamicAttributesMap().get("style");
			Object clazz = getDynamicAttributesMap().get("class");
			if (Util.objects.isNotEmpty(style) || Util.objects.isNotEmpty(clazz)) {
				getOut().print("<span");
				if (Util.objects.isNotEmpty(style)) {
					getOut().print(" style=\"" + style + "\"");
				}
				if (Util.objects.isNotEmpty(clazz)) {
					getOut().print(" class=\"" + clazz + "\"");
				}
				getOut().print(">");
			}

			doBody();

			if (Util.objects.isNotEmpty(style) || Util.objects.isNotEmpty(clazz)) {
				getOut().print("</span>");
			}

		}

	}

	public Integer getColspan() {
		return colspan;
	}

	public void setColspan(Integer colspan) {
		this.colspan = colspan;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPropertyRenderAs() {
		return propertyRenderAs;
	}

	public void setPropertyRenderAs(String propertyRenderAs) {
		this.propertyRenderAs = propertyRenderAs;
	}

	@Deprecated
	public Boolean getPropertyRenderAsDouble() {
		return PropertyTag.DOUBLE.equalsIgnoreCase(propertyRenderAs);
	}

	@Deprecated
	public void setPropertyRenderAsDouble(Boolean propertyRenderAsDouble) {
		if (Util.booleans.isTrue(propertyRenderAsDouble)) {
			this.propertyRenderAs = PropertyTag.DOUBLE;
		} else {
			this.propertyRenderAs = PropertyTag.SINGLE;
		}
	}

	public String getOnSelectTab() {
		return onSelectTab;
	}

	public void setOnSelectTab(String onSelectTab) {
		this.onSelectTab = onSelectTab;
	}

}
