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

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formattable;
import java.util.Formatter;

import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.core.standard.Message;
import org.nextframework.core.web.NextWeb;
import org.nextframework.exception.NextException;
import org.nextframework.persistence.HibernateUtils;
import org.nextframework.types.File;
import org.nextframework.util.Util;
import org.nextframework.web.WebUtils;
import org.springframework.context.MessageSourceResolvable;

/**
 * @author rogelgarcia | marcusabreu
 * @since 27/01/2006
 * @version 1.1
 */
public class OutputTag extends BaseTag {

	protected Object value;
	protected String pattern;
	protected boolean escapeHTML = false;
	protected boolean replaceMessagesCodes = false;
	protected String styleClass;
	protected String style;
	protected String forProperty;
	protected boolean searchValueWhenNull = true;
	protected String trueFalseNullLabels;
	protected boolean printMarkerWhenEmpty = true;

	@Override
	public void doComponent() throws Exception {

		if (value == null && searchValueWhenNull) {
			value = getPageContext().findAttribute("value");
		}

		String bodyToPrint = getStringBody();
		if (Util.strings.isEmpty(bodyToPrint) && printMarkerWhenEmpty) {
			bodyToPrint = "&nbsp;";
		}

		boolean createSpan = Util.strings.isNotEmpty(styleClass) || Util.strings.isNotEmpty(style) || Util.strings.isNotEmpty(forProperty);
		if (createSpan) {
			getOut().print("<span");
			if (styleClass != null) {
				getOut().print(" class='" + styleClass + "'");
			}
			if (style != null) {
				getOut().print(" style='" + style + "'");
			}
			if (Util.strings.isNotEmpty(forProperty)) {
				getOut().print(" forproperty='" + forProperty + "'");
			}
			getOut().print(">");
		}

		getOut().print(bodyToPrint);

		if (createSpan) {
			getOut().print("</span>");
		}

	}

	public String getStringBody() {
		String body = getStringBody(value, pattern);
		if (body != null) {
			if (escapeHTML) {
				body = body.replace("<", "&lt;").replace("\n", "<BR>");
			}
			if (replaceMessagesCodes) {
				body = Util.strings.replaceString(NextWeb.getRequestContext().getMessageResolver(), body);
			}
		}
		return body;
	}

	public String getStringBody(Object value, String pattern) {

		if (value instanceof Message) {
			value = ((Message) value).getSource();
		}

		if (value == null) {
			String[] split = getResolvedTrueFalseNullLabels();
			value = split[2];
		} else if (value instanceof Boolean) {
			String[] split = getResolvedTrueFalseNullLabels();
			if (((Boolean) value)) {
				value = split[0];
			} else {
				value = split[1];
			}
		} else if (value instanceof Formattable) {
			Formatter fmt = new Formatter();
			((Formattable) value).formatTo(fmt, 0, -1, -1);
			value = fmt.out().toString();
		}

		if ((value instanceof Date || value instanceof java.sql.Date || value instanceof Timestamp || value instanceof Calendar) && Util.strings.isEmpty(pattern)) {
			if (value instanceof Time) {
				pattern = "HH:mm";
			} else {
				pattern = "dd/MM/yyyy";
			}
		} else if (value instanceof Number && Util.strings.isEmpty(pattern)) {
			if (value instanceof Double || value instanceof Float || value instanceof BigDecimal) {
				pattern = "#,##0.00";
			} else {
				pattern = "#,##0.##";
			}
		}

		String body = null;

		if (value instanceof String) {
			body = (String) value;
		} else if (value instanceof Number) {
			Number number = (Number) value;
			String valueToString = new DecimalFormat(pattern).format(number);
			if (valueToString.startsWith(",")) {
				valueToString = "0" + valueToString;
			}
			body = valueToString;
		} else if (value instanceof Date || value instanceof java.sql.Date || value instanceof Timestamp) {
			String valueToString = new SimpleDateFormat(pattern).format(value);
			body = valueToString;
		} else if (value instanceof Calendar) {
			Calendar data = (Calendar) value;
			SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
			body = dateFormat.format(data.getTime());
		} else if (value instanceof File) {
			File file = (File) HibernateUtils.getLazyValue(value);
			DownloadFileServlet.addCdfile(getRequest().getSession(), file.getCdfile());
			String link = getRequest().getContextPath() + DownloadFileServlet.DOWNLOAD_FILE_PATH + "/" + file.getCdfile();
			link = WebUtils.rewriteUrl(link); //URL Sufix
			body = "<a href=\"" + link + "\" class=\"filelink\">" + file.getName() + "</a>";
		} else if (value instanceof Throwable) {
			body = Util.objects.getExceptionDescription(NextWeb.getRequestContext().getMessageResolver(), (Throwable) value, true, true);
		} else if (value instanceof MessageSourceResolvable) {
			body = NextWeb.getRequestContext().getMessageResolver().message((MessageSourceResolvable) value);
		} else {
			BeanDescriptor beanDescriptor = BeanDescriptorFactory.forBean(value);
			Object description = beanDescriptor.getDescription();
			if (description != null) {
				body = getStringBody(description, pattern);
			} else {
				body = TagUtils.getObjectDescriptionToString(value);
			}
		}

		return body;
	}

	private String[] getResolvedTrueFalseNullLabels() {
		if (Util.strings.isEmpty(trueFalseNullLabels)) {
			String trueString = getDefaultViewLabel("trueLabel", "Sim");
			String falseString = getDefaultViewLabel("falseLabel", "N�o");
			String nullString = getDefaultViewLabel("nullLabel", "");
			return new String[] { trueString, falseString, nullString };
		}
		String[] split = trueFalseNullLabels.split(",");
		if (split.length != 3) {
			throw new NextException("trueFalseNullLabels inv�lido " + trueFalseNullLabels + ". Esse atributo deve ser uma string separada por v�rgula indicando o valor de TRUE FALSE e NULL. ex.: sim,n�o,vazio");
		}
		return split;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public boolean isEscapeHTML() {
		return escapeHTML;
	}

	public void setEscapeHTML(boolean escapeHTML) {
		this.escapeHTML = escapeHTML;
	}

	public boolean isReplaceMessagesCodes() {
		return replaceMessagesCodes;
	}

	public void setReplaceMessagesCodes(boolean replaceMessagesCodes) {
		this.replaceMessagesCodes = replaceMessagesCodes;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getForProperty() {
		return forProperty;
	}

	public void setForProperty(String forProperty) {
		this.forProperty = forProperty;
	}

	public boolean isSearchValueWhenNull() {
		return searchValueWhenNull;
	}

	public void setSearchValueWhenNull(boolean searchValueWhenNull) {
		this.searchValueWhenNull = searchValueWhenNull;
	}

	public String getTrueFalseNullLabels() {
		return trueFalseNullLabels;
	}

	public void setTrueFalseNullLabels(String trueFalseNullLabels) {
		this.trueFalseNullLabels = trueFalseNullLabels;
	}

	public boolean isPrintMarkerWhenEmpty() {
		return printMarkerWhenEmpty;
	}

	public void setPrintMarkerWhenEmpty(boolean printMarkerWhenEmpty) {
		this.printMarkerWhenEmpty = printMarkerWhenEmpty;
	}

}
