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

import org.nextframework.bean.PropertyDescriptor;
import org.nextframework.exception.NextException;
import org.nextframework.persistence.HibernateUtils;
import org.nextframework.types.File;
import org.nextframework.util.Util;

/**
 * @author rogelgarcia | marcusabreu
 * @since 27/01/2006
 * @version 1.1
 */
public class OutputTag extends BaseTag {
	
	protected Object value;
	//quando value for number ou date
	protected String pattern;
	protected String itemSeparator;
	protected String styleClass;
	protected String style;
	protected String trueFalseNullLabels = "Sim,Não,";
	protected boolean printMarkerWhenEmpty = true;
	protected boolean searchValueWhenNull = true; 
	protected String forProperty;
	
	protected boolean escapeHTML = false;
	private PropertyDescriptor propertyDescriptor;
	
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

	public boolean isEscapeHTML() {
		return escapeHTML;
	}

	public void setEscapeHTML(boolean escapeHTML) {
		this.escapeHTML = escapeHTML;
	}

	public String getTrueFalseNullLabels() {
		return trueFalseNullLabels;
	}

	public void setTrueFalseNullLabels(String trueFalseNullLabels) {
		this.trueFalseNullLabels = trueFalseNullLabels;
	}

	@Override
	public void doComponent() throws Exception {
		if(value == null && searchValueWhenNull){
			value = getPageContext().findAttribute("value");
		}
		if(propertyDescriptor == null){
			propertyDescriptor = (PropertyDescriptor)getPageContext().findAttribute("propertyDescriptor");
		}

		String bodyToPrint = getStringBody();
		
		if(Util.strings.isNotEmpty(style) || Util.strings.isNotEmpty(styleClass) || Util.strings.isNotEmpty(forProperty)){
			getOut().print("<span");
			if(style != null){
				getOut().print(" style='"+style+"'");
			}
			if(styleClass != null){
				getOut().print(" class='"+styleClass+"'");
			}
			if(Util.strings.isNotEmpty(forProperty)){
				getOut().print(" forproperty='"+forProperty+"'");
			}
			getOut().print(">");
		}
		if(Util.strings.isEmpty(bodyToPrint) && printMarkerWhenEmpty){
			getOut().print("&nbsp;");
		} else {
			getOut().print(bodyToPrint);
		}
		
		if(Util.strings.isNotEmpty(style) || Util.strings.isNotEmpty(styleClass) || Util.strings.isNotEmpty(forProperty)){
			getOut().print("</span>");
		}
	}

	public String getStringBody() {
		String bodyToPrint = null;
		try {
			if ((value instanceof Boolean || value == null) && Util.strings.isNotEmpty(trueFalseNullLabels)) {
				escapeHTML = false;
				String[] split = trueFalseNullLabels.split(",");
				String trueString = split[0];
				String falseString = split[1];
				String nullString = "";
				if (split.length == 3) {
					nullString = split[2];
				}
				if (value == null) {
					value = nullString;
				} else if (value instanceof Boolean) {
					if (((Boolean) value)) {
						value = trueString;
					} else {
						value = falseString;
					}
				}
			} else if(value instanceof Boolean) {
				if (((Boolean) value)) {
					value = "Sim";
				} else {
					value = "Não";
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new NextException("trueFalseNullLabels inválido "+trueFalseNullLabels+". Esse atributo deve ser uma string separada por vírgula indicando o valor de TRUE FALSE e NULL. ex.: sim,não,vazio");
		}
		if(value instanceof Formattable){
			Formatter fmt = new Formatter();
			((Formattable)value).formatTo(fmt, 0, -1, -1);
			value = fmt.out().toString();
		}
		if((value instanceof Date || value instanceof java.sql.Date || value instanceof Timestamp || value instanceof Calendar) && Util.strings.isEmpty(pattern)){
			if(value instanceof Time){//FIXME
				pattern = "HH:mm"; //BUG: 000047
			} else {
				pattern = "dd/MM/yyyy";
			}
		}
		if(value instanceof Number && Util.strings.isEmpty(pattern)){
			if(value instanceof Double || value instanceof Float || value instanceof BigDecimal){
				pattern = "#,##0.00";
			} else {
				pattern = "#,##0.##";
			}
		}
		if(pattern != null && (value instanceof Date || value instanceof java.sql.Date || value instanceof Timestamp || value instanceof Calendar || value instanceof Number)){
			//FIXME
			if(value instanceof Date || value instanceof java.sql.Date || value instanceof Timestamp){
				String valueToString = new SimpleDateFormat(pattern).format(value);
				bodyToPrint = valueToString;
			} else if(value instanceof Calendar){
				Calendar data = (Calendar) value;
				SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
				bodyToPrint = dateFormat.format(data.getTime());	
			} else {// o valor obrigatoriamente deve ser do tipo Number
				Number number = (Number) value;
				String valueToString = new DecimalFormat(pattern).format(number);
				if (valueToString.startsWith(",")) {
					valueToString = "0" + valueToString;
				}
				bodyToPrint = valueToString;
			}
		} else if(value instanceof File){
				File file = (File) HibernateUtils.getLazyValue(value);
				DownloadFileServlet.addCdfile(getRequest().getSession(), file.getCdfile());
				String link = getRequest().getContextPath()+DownloadFileServlet.DOWNLOAD_FILE_PATH+"/"+file.getCdfile();
				
				//Verifica URL Sufix
				link = WebUtils.rewriteUrl(link);
				
				bodyToPrint = "<a href=\"" + link + "\" class=\"filelink\">" + file.getName() + "</a>";
		} else {
//			if(HibernateUtils.isLazy(value) && WebUtils.isCrudRequest() && propertyDescriptor != null){
//				Class<?> classType = WebUtils.getCrudClass();
//				GenericDAO<?> daoForClass = DAOUtils.getDAOForClass(classType);
//				//se for um CRUD vamos auxiliar o DAO para carregar esse objeto da proxima vez
////				daoForClass.suggestLoadForListagem(propertyDescriptor.getName());
//			}
			String objectDescriptionToString = TagUtils.getObjectDescriptionToString(value);
			if(objectDescriptionToString!=null){
				if(escapeHTML){
					bodyToPrint = objectDescriptionToString.replace("<", "&lt;").replace("\n","<BR>");	
				} else {
					bodyToPrint = objectDescriptionToString;
				}
			}
				
		}
		return bodyToPrint;
	}
	
	public String getItemSeparator() {
		return itemSeparator;
	}

	public String getPattern() {
		return pattern;
	}

	public String getStyle() {
		return style;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public Object getValue() {
		return value;
	}

	public void setItemSeparator(String itemSeparator) {
		this.itemSeparator = itemSeparator;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public boolean isPrintMarkerWhenEmpty() {
		return printMarkerWhenEmpty;
	}

	public void setPrintMarkerWhenEmpty(boolean printMarkerWhenEmpty) {
		this.printMarkerWhenEmpty = printMarkerWhenEmpty;
	}

}
