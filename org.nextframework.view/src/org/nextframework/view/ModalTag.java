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

import org.nextframework.view.combo.ComboTag;

public class ModalTag extends ComboTag {

	protected boolean visible = true;
	protected String overlayStyleClass;
	protected String panelStyleClass;
	protected String contentStyleClass;

	@Override
	protected void doComponent() throws Exception {

		if (getId() == null) {
			setId(generateUniqueId());
		}

		includeJspTemplate();

	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getOverlayStyleClass() {
		return overlayStyleClass;
	}

	public void setOverlayStyleClass(String overlayStyleClass) {
		this.overlayStyleClass = overlayStyleClass;
	}

	public String getPanelStyleClass() {
		return panelStyleClass;
	}

	public void setPanelStyleClass(String panelStyleClass) {
		this.panelStyleClass = panelStyleClass;
	}

	public String getContentStyleClass() {
		return contentStyleClass;
	}

	public void setContentStyleClass(String contentStyleClass) {
		this.contentStyleClass = contentStyleClass;
	}

}
