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

/**
 * @author rogelgarcia
 * @since 03/02/2006
 * @version 1.1
 */
public class FormPanelTag extends TemplateTag {

	protected boolean showSubmit = true;
	protected String updateLinkLabel = null;
	protected String submitAction = "save";
	protected String submitConfirmationScript;
	protected String submitLabel = null;

	private String panelStyleClass;
	private String bodyStyleClass;
	private String actionBarStyleClass;
	private String buttonStyleClass;

	@Override
	protected void doComponent() throws Exception {

		if (updateLinkLabel == null) {
			updateLinkLabel = getDefaultViewLabel("updateLinkLabel", "Editar");
		}

		if (submitLabel == null) {
			submitLabel = getDefaultViewLabel("submitLabel", "Salvar");
		}

		pushAttribute("janelaEntradaTag", this); //Legacy
		includeJspTemplate();
		popAttribute("janelaEntradaTag");

	}

	public String getUniqueId() {
		return generateUniqueId();
	}

	public boolean isShowSubmit() {
		return showSubmit;
	}

	public void setShowSubmit(boolean showSubmit) {
		this.showSubmit = showSubmit;
	}

	public String getUpdateLinkLabel() {
		return updateLinkLabel;
	}

	public void setUpdateLinkLabel(String updateLinkLabel) {
		this.updateLinkLabel = updateLinkLabel;
	}

	public String getSubmitAction() {
		return submitAction;
	}

	public void setSubmitAction(String submitAction) {
		this.submitAction = submitAction;
	}

	public String getSubmitConfirmationScript() {
		return submitConfirmationScript;
	}

	public void setSubmitConfirmationScript(String submitConfirmationScript) {
		this.submitConfirmationScript = submitConfirmationScript;
	}

	public String getSubmitLabel() {
		return submitLabel;
	}

	public void setSubmitLabel(String submitLabel) {
		this.submitLabel = submitLabel;
	}

	public String getPanelStyleClass() {
		return panelStyleClass;
	}

	public void setPanelStyleClass(String panelStyleClass) {
		this.panelStyleClass = panelStyleClass;
	}

	public String getBodyStyleClass() {
		return bodyStyleClass;
	}

	public void setBodyStyleClass(String bodyStyleClass) {
		this.bodyStyleClass = bodyStyleClass;
	}

	public String getActionBarStyleClass() {
		return actionBarStyleClass;
	}

	public void setActionBarStyleClass(String actionBarStyleClass) {
		this.actionBarStyleClass = actionBarStyleClass;
	}

	public String getButtonStyleClass() {
		return buttonStyleClass;
	}

	public void setButtonStyleClass(String buttonStyleClass) {
		this.buttonStyleClass = buttonStyleClass;
	}

}