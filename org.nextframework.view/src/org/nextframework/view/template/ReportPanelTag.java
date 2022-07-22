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
public class ReportPanelTag extends TemplateTag {

	private String sectionTitle;

	private String submitAction = "generate";
	private String submitConfirmationScript = "true";
	private String submitLabel;

	private String sectionTitleStyleClass;
	private String panelStyleClass;
	private String actionBarStyleClass;

	@Override
	protected void doComponent() throws Exception {

		if (sectionTitle == null) {
			sectionTitle = getDefaultViewLabel("sectionTitle", null);
		}

		if (submitLabel == null) {
			submitLabel = getDefaultViewLabel("submitLabel", "Gerar relatório");
		}

		pushAttribute("TJanelaRelatorio", this);
		includeJspTemplate();
		popAttribute("TJanelaRelatorio");

	}

	public String getUniqueId() {
		return generateUniqueId();
	}

	public String getSectionTitle() {
		return sectionTitle;
	}

	public void setSectionTitle(String sectionTitle) {
		this.sectionTitle = sectionTitle;
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

	public String getSectionTitleStyleClass() {
		return sectionTitleStyleClass;
	}

	public void setSectionTitleStyleClass(String sectionTitleStyleClass) {
		this.sectionTitleStyleClass = sectionTitleStyleClass;
	}

	public String getPanelStyleClass() {
		return panelStyleClass;
	}

	public void setPanelStyleClass(String panelStyleClass) {
		this.panelStyleClass = panelStyleClass;
	}

	public String getActionBarStyleClass() {
		return actionBarStyleClass;
	}

	public void setActionBarStyleClass(String actionBarStyleClass) {
		this.actionBarStyleClass = actionBarStyleClass;
	}

}