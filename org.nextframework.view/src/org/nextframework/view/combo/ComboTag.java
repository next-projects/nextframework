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
package org.nextframework.view.combo;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.tagext.JspFragment;

import org.nextframework.exception.NextException;
import org.nextframework.util.ReflectionCache;
import org.nextframework.util.ReflectionCacheFactory;
import org.nextframework.util.Util;
import org.nextframework.view.BaseTag;
import org.nextframework.view.LogicalTag;
import org.nextframework.view.TagUtils;

/**
 * @author rogelgarcia
 * @since 01/02/2006
 * @version 1.1
 */
@SuppressWarnings("deprecation")
public class ComboTag extends BaseTag implements LogicalTag {

	protected void invoke(TagHolder holder) throws JspException, IOException {
		TagHolderFragment fragment = new TagHolderFragment(getJspContext(), Arrays.asList(holder), this);
		fragment.invoke(getOut());
	}

	public static class TagHolder {

		protected BaseTag baseTag;
		protected List<TagHolder> children = new ArrayList<TagHolder>();
		protected Map<String, String> rtexprs = new HashMap<String, String>();

		public TagHolder(BaseTag tag) {
			if (tag == null) {
				throw new NullPointerException("baseTag null");
			}
			baseTag = tag;
		}

		public TagHolder(BaseTag tag, String... exprs) {
			this(tag);
			if (exprs.length % 2 != 0) {
				throw new IllegalArgumentException("exprs invalidos " + Arrays.toString(exprs));
			}
			for (int i = 0; i < exprs.length; i += 2) {
				rtexprs.put(exprs[i], exprs[i + 1]);
			}
		}

		public boolean addChild(TagHolder o) {
			return children.add(o);
		}

		public BaseTag getBaseTag() {
			return baseTag;
		}

		public List<TagHolder> getChildren() {
			return children;
		}

		public void setBaseTag(BaseTag baseTag) {
			this.baseTag = baseTag;
		}

		public void setChildren(List<TagHolder> children) {
			this.children = children;
		}

	}

	public static class TagHolderFragment extends JspFragment {

		protected JspContext jspContext;
		protected List<TagHolder> tagHolders = new ArrayList<TagHolder>();
		protected BaseTag parent;

		public TagHolderFragment(JspContext context, List<TagHolder> holders, BaseTag parent) {
			jspContext = context;
			tagHolders = holders;
			this.parent = parent;
			for (TagHolder holder : tagHolders) {
				holder.baseTag.setParent(parent);
			}
		}

		@Override
		public void invoke(Writer out) throws JspException, IOException {
			for (TagHolder holder : tagHolders) {
				//holder.baseTag.setParent(parent);
				holder.baseTag.setJspContext(jspContext);
				Set<String> keySet = holder.rtexprs.keySet();
				for (String propertyName : keySet) {
					String expr = holder.rtexprs.get(propertyName);
					try {
						setProperty(holder.baseTag, propertyName, TagUtils.evaluate(expr, jspContext));
					} catch (ELException e) {
						throw new NextException("Cannot set property " + propertyName + " of tag " + holder.baseTag + " value " + expr + ". Parsing problem");
					}
				}
				if (holder.getChildren().size() > 0) {
					holder.baseTag.setJspBody(new TagHolderFragment(getJspContext(), holder.getChildren(), holder.baseTag));
				}
				jspContext.pushBody(out);
				holder.baseTag.doTag();
				jspContext.popBody();
			}
		}

		private void setProperty(BaseTag baseTag, String property, Object object) {

			if (object != null) {

				Method setter = null;
				ReflectionCache reflectionCache = ReflectionCacheFactory.getReflectionCache();

				try {
					setter = reflectionCache.getMethod(baseTag.getClass(), "set" + Util.strings.captalize(property), object.getClass());
				} catch (SecurityException e) {
					throw new NextException("Cannot set property " + property + " of tag " + baseTag + " value " + object);
				} catch (NoSuchMethodException e) {
					try {
						setter = reflectionCache.getMethod(baseTag.getClass(), "set" + Util.strings.captalize(property), String.class);
						object = object.toString();
					} catch (SecurityException e1) {
						throw new NextException("Cannot set property " + property + " of tag " + baseTag + " value " + object);
					} catch (NoSuchMethodException e1) {
						try {
							setter = reflectionCache.getMethod(baseTag.getClass(), "set" + Util.strings.captalize(property), Object.class);
						} catch (SecurityException e2) {
							throw new NextException("Cannot set property " + property + " of tag " + baseTag + " value " + object);
						} catch (NoSuchMethodException e2) {
							throw new NextException("Cannot set property " + property + " of tag " + baseTag + " value " + object + "  " + e.getMessage(), e);
						}
					}
				}

				try {
					//System.out.println("Setting property "+property+" value "+object);
					setter.invoke(baseTag, object);
				} catch (IllegalArgumentException e) {
					throw new NextException("Cannot set property " + property + " of tag " + baseTag + " value " + object);
				} catch (IllegalAccessException e) {
					throw new NextException("Cannot set property " + property + " of tag " + baseTag + " value " + object);
				} catch (InvocationTargetException e) {
					throw new NextException("Cannot set property " + property + " of tag " + baseTag + " value " + object);
				}

			}

		}

		@Override
		public JspContext getJspContext() {
			return jspContext;
		}

	}

}