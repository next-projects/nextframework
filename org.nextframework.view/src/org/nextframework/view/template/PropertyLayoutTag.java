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

import java.util.Arrays;
import java.util.List;

import javax.servlet.jsp.JspException;

import org.nextframework.exception.NextException;
import org.nextframework.util.Util;
import org.nextframework.view.BaseTag;
import org.nextframework.view.ColumnTag;
import org.nextframework.view.GroupTag;
import org.nextframework.view.PanelGridTag;
import org.nextframework.view.PanelTag;

/**
 * @author rogelgarcia and marcusabreu
 * @since 03/02/2006
 * @version 1.1
 */
public class PropertyLayoutTag extends TemplateTag {

	public static final String SINGLE = "single";
	public static final String DOUBLE = "double";
	public static final String INVERT = "invert";
	public static final String STACKED = "stacked";
	public static final String STACKED_INVERT = "stacked_invert";
	private static final List<String> RENDERAS_OPTIONS = Arrays.asList(SINGLE, DOUBLE, INVERT, STACKED, STACKED_INVERT);

	protected String bodyId;
	protected String renderAs;
	protected Boolean invertLabel;
	protected Integer colspan;
	protected Integer labelColspan;
	protected String label;

	private String panelStyleClass;
	private String panelStyle;
	private String labelPanelStyleClass;
	private String labelPanelStyle;
	private String labelStyleClass;
	private String labelStyle;
	private String stackedPanelStyleClass;

	@Override
	protected void applyDefaultStyleClasses() throws JspException {
		//Não aplica no fluxo natural.
	}

	@Override
	protected String getSubComponentName() {
		return renderAs;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void doComponent() throws Exception {

		if (Util.strings.isEmpty(bodyId)) {
			bodyId = generateUniqueId();
		}

		PropertyConfigTag configTag = findParent(PropertyConfigTag.class);
		BaseTag findFirst = findFirst(PropertyConfigTag.class, PanelTag.class, ColumnTag.class, GroupTag.class, PanelGridTag.class);
		verifyRenderAs(configTag, findFirst);

		//Aplica estilos padrão apenas após resolver o 'renderAs'
		super.applyDefaultStyleClasses();

		verifyColspan();

		includeJspTemplate(); //se nao for possível, utilizar renderização normal

	}

	private void verifyRenderAs(PropertyConfigTag configTag, BaseTag findFirst) {
		if (Util.strings.isNotEmpty(renderAs)) {
			renderAs = renderAs.toLowerCase();
		}
		if (Util.strings.isEmpty(renderAs)) {
			if (findFirst instanceof PropertyConfigTag && Util.strings.isNotEmpty(configTag.getRenderAs())) {
				renderAs = configTag.getRenderAs();
			} else if (findFirst instanceof PanelTag) {
				PanelTag panel = (PanelTag) findFirst;
				renderAs = panel.getPropertyRenderAs();
			} else if (findFirst instanceof PanelGridTag) {
				PanelGridTag panelGrid = (PanelGridTag) findFirst;
				renderAs = panelGrid.getPropertyRenderAs();
			} else if (findFirst instanceof GroupTag) {
				GroupTag groupTag = (GroupTag) findFirst;
				renderAs = groupTag.getPropertyRenderAs();
			} else if (findFirst instanceof ColumnTag) {
				renderAs = SINGLE;
			}
		}
		if (Util.strings.isEmpty(renderAs)) {
			renderAs = SINGLE;
		}
		if (DOUBLE.equals(renderAs) && Util.booleans.isTrue(invertLabel)) {
			renderAs = INVERT;
		}
		if (STACKED.equals(renderAs) && Util.booleans.isTrue(invertLabel)) {
			renderAs = STACKED_INVERT;
		}
		if (!RENDERAS_OPTIONS.contains(renderAs)) {
			throw new NextException("Property 'renderAs' must be one of: " + RENDERAS_OPTIONS + ". Value found: " + renderAs);
		}
	}

	private void verifyColspan() {
		if (colspan == null || colspan == 0) {
			colspan = getViewConfig().getDefaultColspan(renderAs);
			if (colspan == null || colspan == 0) {
				colspan = DOUBLE.equals(renderAs) || INVERT.equals(renderAs) ? 2 : 1;
			}
		}
		if (labelColspan == null || labelColspan == 0) {
			labelColspan = 1;
		}
		if (DOUBLE.equals(renderAs) || INVERT.equals(renderAs)) {
			colspan = colspan - labelColspan;
		}
	}

	public String getBodyId() {
		return bodyId;
	}

	public void setBodyId(String bodyId) {
		this.bodyId = bodyId;
	}

	public String getRenderAs() {
		return renderAs;
	}

	public void setRenderAs(String renderAs) {
		this.renderAs = renderAs;
	}

	public Boolean getInvertLabel() {
		return invertLabel;
	}

	public void setInvertLabel(Boolean invertLabel) {
		this.invertLabel = invertLabel;
	}

	public Integer getColspan() {
		return colspan;
	}

	public Integer getLabelColspan() {
		return labelColspan;
	}

	public void setLabelColspan(Integer labelColspan) {
		this.labelColspan = labelColspan;
	}

	public void setColspan(Integer colspan) {
		this.colspan = colspan;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getPanelStyleClass() {
		return panelStyleClass;
	}

	public void setPanelStyleClass(String panelStyleClass) {
		this.panelStyleClass = panelStyleClass;
	}

	public String getPanelStyle() {
		return panelStyle;
	}

	public void setPanelStyle(String panelStyle) {
		this.panelStyle = panelStyle;
	}

	public String getLabelPanelStyle() {
		return labelPanelStyle;
	}

	public void setLabelPanelStyle(String labelPanelStyle) {
		this.labelPanelStyle = labelPanelStyle;
	}

	public String getLabelPanelStyleClass() {
		return labelPanelStyleClass;
	}

	public void setLabelPanelStyleClass(String labelPanelStyleClass) {
		this.labelPanelStyleClass = labelPanelStyleClass;
	}

	public String getLabelStyleClass() {
		return labelStyleClass;
	}

	public void setLabelStyleClass(String labelStyleClass) {
		this.labelStyleClass = labelStyleClass;
	}

	public String getLabelStyle() {
		return labelStyle;
	}

	public void setLabelStyle(String labelStyle) {
		this.labelStyle = labelStyle;
	}

	public String getStackedPanelStyleClass() {
		return stackedPanelStyleClass;
	}

	public void setStackedPanelStyleClass(String stackedPanelStyleClass) {
		this.stackedPanelStyleClass = stackedPanelStyleClass;
	}

}