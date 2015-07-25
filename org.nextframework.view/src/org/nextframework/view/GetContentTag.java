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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GetContentTag extends BaseTag implements LogicalTag{

	protected String tagName;
	protected String vars;
	protected String bodyVar;
	protected List<String> tags = new ArrayList<String>() {

		private static final long serialVersionUID = 1L;

		@Override
		public String toString() {
			StringBuffer buf = new StringBuffer();

			Iterator<String> i = iterator();
			boolean hasNext = i.hasNext();
			while (hasNext) {
				String o = i.next();
				buf.append(String.valueOf(o));
				hasNext = i.hasNext();
				if (hasNext)
					buf.append(" ");
			}

			return buf.toString();
		}

	};
	
	public void register(String body){
		tags.add(body);
	}
	
	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public boolean getTag(BaseTag baseTag){
		String tag = tagName;
		String id = null;
		if(tagName.contains("#")){
			tag = tagName.substring(0, tagName.indexOf("#"));
			id = tagName.substring(tagName.indexOf("#")+1);
		}
		return baseTag.getClass().getSimpleName().equalsIgnoreCase(tag) && (id == null || id.equals(baseTag.getId()));
	}

	@Override
	protected void doComponent() throws Exception {

		if(vars == null){
			vars = tagName+"s";
		}
		pushAttribute(vars, tags);
		if(bodyVar != null){
			getPageContext().setAttribute(vars, tags);
			String body = getBody();
			getPageContext().setAttribute(bodyVar, body);
			
		} else {
			doBody();
		}
		popAttribute(vars);
	}

	public String getVars() {
		return vars;
	}

	public void setVars(String vars) {
		this.vars = vars;
	}

	public String getBodyVar() {
		return bodyVar;
	}

	public void setBodyVar(String bodyVar) {
		this.bodyVar = bodyVar;
	}
	
	
}
