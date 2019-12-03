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

import org.nextframework.exception.NextException;
import org.nextframework.util.Util;
import org.nextframework.view.BaseTag;
import org.nextframework.view.DataGridTag;
import org.nextframework.view.PanelGridTag;

/**
 * @author rogelgarcia
 * @since 07/02/2006
 * @version 1.1
 */
public class PropertyConfigTag extends TemplateTag {

	public static final String INPUT = "input";

	public static final String OUTPUT = "output";

	public static final String COLUMN = "column";

	public static final String SINGLE = "single";

	public static final String DOUBLE = "double";

	public static final String DOUBLELINE = "doubleline";

	protected String mode = null;
	protected String renderAs = null;
	protected Boolean showLabel = null;
	protected Boolean disabled = null;

	@SuppressWarnings("unchecked")
	@Override
	protected void doComponent() throws Exception {

		PropertyConfigTag parent = findParent(PropertyConfigTag.class);

		if (Util.strings.isNotEmpty(mode)) {
			mode = mode.toLowerCase();
			if (!INPUT.equals(mode) && !OUTPUT.equals(mode)) {
				throw new NextException("A tag propertyConfig só aceita no atributo 'mode' os seguintes valores: input ou output. Valor encontrado: " + mode);
			}
		} else {
			if (parent != null) {
				this.mode = parent.getMode();
			}
		}

		if (Util.strings.isNotEmpty(renderAs)) {
			renderAs = renderAs.toLowerCase();
			if (!COLUMN.equals(renderAs) && !SINGLE.equals(renderAs) && !DOUBLE.equals(renderAs) && !DOUBLELINE.equals(renderAs)) {
				throw new NextException("A tag propertyConfig só aceita no atributo 'renderAs' os seguintes valores: column, single, double ou doubleline. Valor encontrado: " + renderAs);
			}
		} else {
			BaseTag findFirst = findFirst(PropertyConfigTag.class, PanelGridTag.class, DataGridTag.class);
			if (findFirst != null) {
				if (findFirst instanceof PropertyConfigTag) {
					this.renderAs = ((PropertyConfigTag) findFirst).getRenderAs();
				} else if (findFirst instanceof PanelGridTag) {
					Boolean propertyRenderAsDouble = ((PanelGridTag) findFirst).getPropertyRenderAsDouble();
					this.renderAs = propertyRenderAsDouble != null ? (propertyRenderAsDouble ? DOUBLE : SINGLE) : null;
				} else if (findFirst instanceof DataGridTag) {
					this.renderAs = COLUMN;
				}
			}
		}

		if (showLabel == null && DOUBLE.equals(renderAs)) {
			showLabel = false;
		}
		if (showLabel == null && parent != null) {
			showLabel = parent.getShowLabel();
		}

		if (disabled == null && parent != null) {
			disabled = parent.getDisabled();
		}

		getRequest().setAttribute("propertyConfigDisabled", disabled);
		doBody();

	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getRenderAs() {
		return renderAs;
	}

	public void setRenderAs(String renderAs) {
		this.renderAs = renderAs;
	}

	public Boolean getShowLabel() {
		return showLabel;
	}

	public void setShowLabel(Boolean showLabel) {
		this.showLabel = showLabel;
	}

	public Boolean getDisabled() {
		return disabled;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

}