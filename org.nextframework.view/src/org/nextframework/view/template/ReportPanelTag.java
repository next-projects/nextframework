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
	
	String submitLabel = "Gerar relatório";
	String submitAction = "gerar";
	String submitConfirmationScript = "true";

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

	@Override
	protected void doComponent() throws Exception {
		pushAttribute("TJanelaRelatorio", this);
		includeJspTemplate();
		popAttribute("TJanelaRelatorio");
	}
	
	public String getUniqueId(){
		return generateUniqueId();
	}

	public String getSubmitAction() {
		return submitAction;
	}

	public void setSubmitAction(String submitAction) {
		this.submitAction = submitAction;
	}

}
