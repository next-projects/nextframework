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

import org.nextframework.chart.Chart;
import org.nextframework.controller.resource.Resource;
import org.nextframework.view.chart.jfree.ChartRendererJFreeChart;

public class ImgTag extends BaseTag {

	public static final String RESOURCE_SERVLET_PATH = "/resource";

	public static final String RESOURCE_SERVLET_TYPE = ResourceProvider.RESOURCE;

	private Object resource;

	public Object getResource() {
		return resource;
	}

	public void setResource(Object resource) {
		this.resource = resource;
	}

	@Override
	protected void doComponent() throws Exception {
		if (resource instanceof Chart) {
			resource = new Resource("image/png", "chart.png", ChartRendererJFreeChart.renderAsImage((Chart) resource));
		}
		if (resource instanceof Resource) {
			Integer id = ResourceUtil.save(getRequest().getSession(), (Resource) resource);
			getOut().println("<img src=\"" + getRequest().getContextPath() + RESOURCE_SERVLET_PATH + "/" + RESOURCE_SERVLET_TYPE + "?id=" + id + "\" " + getDynamicAttributesToString() + "/>");
		} else {
			throw new RuntimeException("Erro ao renderizar imagem, o tipo de resource deve ser Resource ou Chart");
		}
	}

}
