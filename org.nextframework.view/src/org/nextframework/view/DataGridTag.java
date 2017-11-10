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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.jstl.core.LoopTagStatus;

import org.nextframework.core.config.ViewConfig;
import org.nextframework.exception.NextException;
import org.nextframework.persistence.HibernateUtils;
import org.nextframework.service.ServiceFactory;
import org.nextframework.types.TypedValue;
import org.nextframework.util.CyclicIterator;
import org.nextframework.util.Util;
import org.nextframework.view.combo.ComboTag;
import org.nextframework.view.combo.ComboTag.TagHolder;
import org.nextframework.view.util.AbstractGroupPropertyAnaliser;
import org.springframework.beans.PropertyAccessorFactory;



/**
 * @author rogelgarcia | marcusabreu
 * @since 27/01/2006
 * @version 1.1
 */
@SuppressWarnings("deprecation")
public class DataGridTag extends BaseTag {
	
	protected String property;
	protected Object itemType;
	protected Object itens;
	protected String style;
	protected String styleClass = "table";
	protected String headerStyleClass = "dataGridHeader";
	protected String headerStyle;
	protected String bodyStyleClasses = "dataGridBody1, dataGridBody2";
	protected String bodyStyles;
	protected String footerStyleClass = "dataGridFooter";
	protected String footerStyle;
	protected String groupStyleClasses = "dataGridGroup1, dataGridGroup2, dataGridGroup3";
	protected String groupStyle = "";
	protected Boolean dynaLine = false;
	
	protected String indexProperty;
	
	protected String groupProperty;
	protected String[] groupProperties = null; /** Grupos já separados */
	
	protected String varStatus;
	protected String var = "row";
	protected String varIndex = "index";
	protected String varRowIndex = "rowIndex";
	protected String varSequence = "sequence";
	
	protected String varLastGroupProperty = "lastProperty";
	protected String varGroupPropertyIndex = "groupIndex";
	protected String varGroupItens = "groupItens";
	
	public enum Status {REGISTER, HEADER, BODY, DYNALINE, FOOTER, GROUPTOTAL}
	
	//extra
	protected Status currentStatus = Status.REGISTER;
	protected boolean renderHeader = false;
	protected boolean renderBody = false;
	protected boolean renderFooter = false;
	
	//dynaline
	protected List<PanelRenderedBlock> blocks = new ArrayList<PanelRenderedBlock>();
	
	//configuracao dos rows
	private Map<String, CyclicIterator<String>> rowAttributes = new LinkedHashMap<String, CyclicIterator<String>>();
	private String rowSeparator;
	/**
	 * Esse atributo nao é utilzado
	 */
	private String row;
	
	//informacoes do loopTagStatus
	protected Object _current;
	protected int _index;
	protected int _count;
	protected boolean _isFirst;
	protected boolean _isLast;
	protected Integer _begin = 0;
	protected Integer _end;
	protected Integer _step = 1;

	//listeners
	List<DataGridListener> listeners = new ArrayList<DataGridListener>();
	
	Iterator<String> bodyStyleIterator;
	Iterator<String> bodyStyleClassIterator;
	
	protected List<ColumnTag> columns = new ArrayList<ColumnTag>();
	
	private boolean hasColumns = false; // informa se esse dataGrid tem tags Columns no seu corpo
	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void doComponent() throws Exception {
		if(itens != null && property != null){
			throw new RuntimeException("O dataGrid não pode ter as propriedades property e itens configuradas ao mesmo tempo. ");
		}
		
		if(itens != null && itens instanceof TypedValue && itemType == null){
			itemType = ((TypedValue)itens).getCollectionItemType();
			itens = ((TypedValue)itens).getOriginalCollection();
		}
		
		if(itemType != null && property != null){
			throw new RuntimeException("O dataGrid não pode ter as propriedades property e itemType configuradas ao mesmo tempo. ");
		}
		if(property != null){
			PropertyTag propertyTag = new PropertyTag();
			propertyTag.setName(property);
			TagHolder tagHolderPropertyTag = new ComboTag.TagHolder(propertyTag);
			
			
			this.property = null;
			TagHolder tagHolderDataGrid = new ComboTag.TagHolder(this, "itens", "${value}");
			BeanTag beanTag = new BeanTag();
			beanTag.setJspBody(getJspBody());
			
			tagHolderPropertyTag.addChild(tagHolderDataGrid);
			TagHolder tagHolderBean = new ComboTag.TagHolder(beanTag, "name", var, "valueType", "${parameterizedTypes[0]}", "propertyIndex", "${"+varIndex+"}", "propertyPrefix", "${name}");
			tagHolderDataGrid.addChild(tagHolderBean);
			ComboTag.TagHolderFragment fragment = new ComboTag.TagHolderFragment(getJspContext(), Arrays.asList(tagHolderPropertyTag), this);
			fragment.invoke(getOut());
			return;
		} else if(itemType != null){
			if(!(itemType instanceof String) && !(itemType instanceof Class)){
				throw new RuntimeException("O atributo itemType deve ser do typo String ou Class");
			}
			Class itemClass;
			try {
				itemClass = (Class) (itemType instanceof Class ? itemType : Class.forName((String) itemType));
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Atributo itemType inválido: "+itemType, e);
			}
			BeanTag beanTag = new BeanTag();
			beanTag.setName(var);
			beanTag.setValueType(itemClass);
			beanTag.setJspBody(getJspBody());
			TagHolder tagHolderBeanTag = new ComboTag.TagHolder(beanTag);
			this.itemType = null;
			
			TagHolder tagHolderDataGrid = new ComboTag.TagHolder(this);
			
			tagHolderDataGrid.addChild(tagHolderBeanTag);
			
			
			ComboTag.TagHolderFragment fragment = new ComboTag.TagHolderFragment(getJspContext(), Arrays.asList(tagHolderDataGrid), getParent());
			fragment.invoke(getOut());
			
			return;
		}
		Object requestObject = getRequest().getAttribute(var);
		Object sessionObject = getRequest().getSession().getAttribute(var);
		Object contextAttribute = getRequest().getSession().getServletContext().getAttribute(var);
		
		// o var que estava no escopo deve ser retirado (ele é recolocado no final da tag)
		getRequest().setAttribute(var, null);
		getRequest().getSession().setAttribute(var, null);
		getRequest().getSession().getServletContext().setAttribute(var, null);
		
		
		if (itens != null && itens instanceof String) {
			String expression = (String) itens;
			Object value = getOgnlValue(expression, Collection.class);
			itens = value;
		}
		if(itens != null && itens.getClass().isArray()){
			if (!itens.getClass().getComponentType().isPrimitive()) {
				Object[] array = (Object[]) itens;
				itens = new ArrayList<Object>();
				for (int i = 0; i < array.length; i++) {
					((List<Object>) itens).add(array[i]);
				}
			}
		}
		
		if(itens == null){ // se os itens forem null funcionar como se fosse uma tabela vazia
			itens = new ArrayList<Object>();
		}
		if(!(itens instanceof Collection)){
			throw new IllegalArgumentException("Tipo de itens não suportado por dataGrid: "+itens);
		}
		
		if(Util.strings.isNotEmpty(indexProperty)){
			if(!(itens instanceof List)){
				throw new NextException("In a datagrid the indexProperty can only be used to reorder items of type List. Found "+itens.getClass()+". Cannot reorder a collection of this type.");
			}
			try {
				Collections.sort((List) itens, new Comparator() {

					@Override
					public int compare(Object o1, Object o2) {
						Comparable propertyValue1 = (Comparable) PropertyAccessorFactory.forBeanPropertyAccess(o1).getPropertyValue(indexProperty);
						Comparable propertyValue2 = (Comparable) PropertyAccessorFactory.forBeanPropertyAccess(o2).getPropertyValue(indexProperty);
						return propertyValue1.compareTo(propertyValue2);
					}
				});
			} catch (Exception e) {
				throw new NextException("Cannot reorder list in datagrid. ", e);
			}
		}
		
		//etapa de registro
		if (getJspBody()!=null) {
			executeRegister();
			createCompositeListener();
			compositeListener.setDataGrid(this);
		}
		
		bodyStyleIterator = getBodyStylesIterator();
		bodyStyleClassIterator = getBodyStyleClassesIterator();
		
		bodyStyleIterator = compositeListener.replaceBodyStyleIterator(bodyStyleIterator);
		bodyStyleClassIterator = compositeListener.replaceBodyStyleClassesIterator(bodyStyleClassIterator);
		
		//configurar os iterators dos rows
		Set<String> daKeys = new LinkedHashSet<String>(getDynamicAttributesMap().keySet());
		for (String daKey : daKeys) {
			if(daKey.startsWith("row")){
				CyclicIterator<String> cyclicIterator = new CyclicIterator<String>(toStringArray(getDynamicAttributesMap().get(daKey)));
				rowAttributes.put(daKey.substring(3), cyclicIterator);
				getDynamicAttributesMap().remove(daKey);
			}
		}
		
		//é necessário configurar o RenderResize antes de usar
		configureResizeColumns();
		
		configureHideDatagridWhileLoading();
		
		String styleString = style!=null?" style=\""+style+"\"":"";
		String classString = styleClass!=null?" class=\""+styleClass+"\"":"";
		if(isRenderResizeColumns() && Util.strings.isEmpty(this.id)){
			this.id = generateUniqueId();
		}
		String id = this.id == null? "" : "id=\""+this.id+"\"";
		//if(!getDynamicAttributesMap().containsKey("width")){
		//	getDynamicAttributesMap().put("width", "100%");
		//}
		//if(!getDynamicAttributesMap().containsKey("cellspacing")){
		//	getDynamicAttributesMap().put("cellspacing", "1");
		//}
		
		if(hasColumns){
			compositeListener.beforeTableTagContainer();
			String hideID = generateUniqueId();
			if(isRenderResizeColumns()){
				getOut().println("<div class=\"datagridcontainer\">");
				getOut().println("<div class=\"datagridblock\" id=\""+this.id+"_block\">");
			}
			if(isHideDatagridWhileLoading()){
				getOut().println("<div id=\""+hideID+"_wait\" style='text-align:center'>Aguarde...</div><div id=\""+hideID+"\" style=\"display:none\">");
			}
			compositeListener.beforeStartTableTag();
			String tableTagBegin = "<table"+styleString+classString+getDynamicAttributesToString()+id+">";
			tableTagBegin = compositeListener.replaceTableTagBegin(tableTagBegin);
			getOut().println(tableTagBegin);
			
			if (renderHeader) {
				currentStatus = Status.HEADER;
				renderHeader();
			}
			
			Collection collection = (Collection) itens;
			//tentar carregar a collecao se for lazy
			if(HibernateUtils.isLazy(collection)){
				collection = HibernateUtils.getLazyValue(collection);
			}
			if (Util.strings.isNotEmpty(varStatus)) {
				getRequest().setAttribute(varStatus, getLoopTagStatus());
			}
			currentStatus = Status.BODY;
			_count = collection.size();
			if (_end == null) {
				_end = _count - 1;
			}
			//modificado por pedro em 14 maio 2007
			getOut().print("<tbody>");
			
			iterate(collection);
			
			getOut().print("</tbody>");
			
			if (renderFooter) {
				currentStatus = Status.FOOTER;
				renderFooter();
			}
			
			getOut().println("</table>");
			compositeListener.afterEndTableTag();
			if(isHideDatagridWhileLoading()){
				getOut().println("</div>");
				getOut().println("<script type=\"text/javascript\">document.getElementById('"+hideID+"').style.display='';document.getElementById('"+hideID+"_wait').style.display='none';</script>");
			}
			if(isRenderResizeColumns()){
				getOut().println("</div>");
				getOut().println("</div>");
				getOut().println("<script type=\"text/javascript\">/*reloadDatagridConfig('"+this.id+"');*/ datagridList.push('"+this.id+"');</script>");
			}
			
			if(dynaLine){
				currentStatus = Status.DYNALINE;
				pushAttribute("dataGridDynaline", true);
				renderDynaLine();
				popAttribute("dataGridDynaline");
			}
		} else {
			Collection collection = (Collection) itens;
			if (Util.strings.isNotEmpty(varStatus)) {
				getRequest().setAttribute(varStatus, getLoopTagStatus());
			}
			currentStatus = Status.BODY;
			_count = collection.size();
			if (_end == null) {
				_end = _count - 1;
			}
			iterate(collection);
		}
		

		getRequest().setAttribute(var, requestObject);
		getRequest().getSession().setAttribute(var, sessionObject);
		getRequest().getSession().getServletContext().setAttribute(var, contextAttribute);
	}

	public void executeRegister() throws JspException, IOException {
		PrintWriter writer = new PrintWriter(new ByteArrayOutputStream());
		getJspBody().invoke(writer);
	}

	Boolean renderResizeColumns = null;
	
	private void configureResizeColumns(){
		Object nocolumnresize = getDynamicAttributesMap().get("nocolumnresize");
		if("true".equals(nocolumnresize)){
			renderResizeColumns = false;
			return;
		}
		DataGridTag datagrid = findParent2(DataGridTag.class, false);
		if(datagrid != null && Util.booleans.isTrue(datagrid.getDynaLine())){
			//se tiver dentro de um datagrid com dynaline.. nao redimensionar
			renderResizeColumns = false;
			return;
		}
		//TabPanelTag tabpanel = findParent2(TabPanelTag.class, false);
		//if(tabpanel != null){
		//	return false;
		//}
		renderResizeColumns = ServiceFactory.getService(ViewConfig.class).isDefaultResizeDatagridColumns();
	}
	
	Boolean hideDatagridWhileLoading = null;
	
	private void configureHideDatagridWhileLoading(){
		Object attr = getDynamicAttributesMap().get("hideDatagridWhileLoading");
		if("true".equals(attr)){
			hideDatagridWhileLoading = true;
			return;
		}
		if("false".equals(attr)){
			hideDatagridWhileLoading = false;
			return;
		}
		DataGridTag datagrid = findParent2(DataGridTag.class, false);
		if(datagrid != null && Util.booleans.isTrue(datagrid.getDynaLine())){
			//se tiver dentro de um datagrid com dynaline.. nao esconder
			hideDatagridWhileLoading = false;
			return;
		}
		//TabPanelTag tabpanel = findParent2(TabPanelTag.class, false);
		//if(tabpanel != null){
		//	return false;
		//}
		hideDatagridWhileLoading = ServiceFactory.getService(ViewConfig.class).isDefaultHideDatagridWhileLoading();
	}
	
	boolean isRenderResizeColumns() {
		return renderResizeColumns;
	}
	
	public boolean isHideDatagridWhileLoading() {
		return hideDatagridWhileLoading;
	}
	
	public String getIndexProperty() {
		return indexProperty;
	}

	private String[] toStringArray(Object object) {
		if(object == null){
			object = "";
		}
		if(rowSeparator == null){
			return new String[]{object.toString()};
		}
		return object.toString().split(rowSeparator);
	}

	private void renderDynaLine() throws ELException, IOException, JspException, ServletException {
		getRequest().setAttribute(var, null);
		getRequest().setAttribute(varIndex, "{index}");
		getRequest().setAttribute(varRowIndex, "{indexplus}");
		getRequest().setAttribute(varSequence, "{indexSequence}");
		
		doBody();
		
		
		//getOut().print("</tr>");
		getOut().println("<script language=\"javascript\">");
		List<String> tdBodys = new ArrayList<String>();
		String nestedPropertyName = null;
		for (PanelRenderedBlock block : blocks) {
			String body = block.body;
			tdBodys.add(body);
			if(nestedPropertyName == null){
				int index = body.indexOf("{index}");
				if(index >= 0){
					String a = body.substring(0, index);
					int begin = a.length();
					for (int i = a.length()-1; i >=0 ; i--) {
						if(a.charAt(i) == '\'' || a.charAt(i) == '\"' || a.charAt(i) == '='){
							begin = i + 1;
							break;
						}
					}
					nestedPropertyName = body.substring(begin, index-1); 
				}
			}
		}
		getOut().println(enhanceProperty(id, "trClassModel", Util.strings.isEmpty(bodyStyleClasses)? Arrays.asList(""):Arrays.asList(bodyStyleClasses.split(","))));
		getOut().println(enhanceProperty(id, "tdClassModel", Arrays.asList("")));
		getOut().println(enhanceProperty(id, "dataModel", tdBodys));
		getOut().println(enhanceProperty(id, "indexName", "{index}"));
		getOut().println(enhanceProperty(id, "indexedProperty", nestedPropertyName));
		getOut().println(enhanceProperty(id, "indexSequenceName", "{indexSequence}"));
		getOut().println(enhanceProperty(id, "indexPlusName", "{indexplus}"));
		getOut().println(enhanceProperty(id, "indexProperty", indexProperty));
		includeTextTemplate("newLineFunction");
		getOut().println("</script>");
	}

	private void renderHeader() throws ELException, IOException, JspException {
		String styleString = headerStyle!=null?" style=\""+headerStyle+"\"":"";
		String classString = headerStyleClass!=null?" class=\""+headerStyleClass+"\"":"";
		getOut().print("<thead>"); //modificado por pedro para suportar o plugin do jquery para dar trace na tabela
		getOut().print("<tr"+styleString+classString+"  onselectstart=\"return false;\" onmousedown=\"return false;\">");
		doBody();
		getOut().print("</tr>");
		getOut().print("</thead>");
	}
	
//	private void doColumns() throws JspException, IOException {
//		for (ColumnTag column : columns) {
//			column.doTag();
//		}
//	}
	
	protected void renderBody(Object current, String groupPropertyValue) throws ELException, IOException, JspException {
		String style = bodyStyleIterator.next();
		String styleClass = bodyStyleClassIterator.next();
		String styleString = style!=null?" style=\""+style+"\"":"";
		String classString = styleClass!=null?" class=\""+styleClass+"\"":"";
		if(hasColumns){
			String daattributes = "";
			Set<String> attributes = rowAttributes.keySet();
			for (String attr : attributes) {
				String attrValue = rowAttributes.get(attr).next();
				attrValue = compositeListener.updateRowAttribute(attr, attrValue);
				daattributes+=" "+attr+"=\""+TagUtils.escape(attrValue)+"\"";
			}
			renderRow(styleString, classString, daattributes, current, groupPropertyValue);			
		} else {
			doBody();
		}
	}

	protected void renderRow(String styleString, String classString, String daattributes, Object current, String groupPropertyValue) throws IOException, JspException {
		String gp = "";
		if(groupProperty != null){
			gp = " group=\""+groupPropertyValue+"\" ";
		}
		getOut().print("<tr"+styleString+classString+gp+daattributes+">");
		doBody();
		getOut().print("</tr>");
	}

	private void renderFooter() throws ELException, IOException, JspException {
		String styleString = footerStyle!=null?" style=\""+footerStyle+"\"":"";
		String classString = footerStyleClass!=null?" class=\""+footerStyleClass+"\"":"";
		getOut().print("<tfoot>");
		getOut().print("<tr"+styleString+classString+">");
		doBody();
		getOut().print("</tr>");
		getOut().print("</tfoot>");
	}


	@SuppressWarnings("all")
	private void iterate(Collection collection) throws ELException, IOException, JspException {
		_isFirst = true;
		Iterator iterator = collection.iterator();
		
		//Inicia o analizador de groupProperties
		GroupPropertyAnaliser groupPropertyAnaliser = new GroupPropertyAnaliser(this); 
		
		
		while(iterator.hasNext()){

			_current = iterator.next();
			
			getRequest().setAttribute(var, _current);
			getRequest().setAttribute(varIndex, _index);
			getRequest().setAttribute(varRowIndex, _index+1);
			getRequest().setAttribute(varSequence, "SQ_SRV_"+_index);
			
			//Define no analizador as propriedades atuais
			groupPropertyAnaliser.defineActualPropertiesAndTotalize(_current);
			
			currentStatus = Status.BODY;
			renderBody(_current, groupPropertyAnaliser.getGroupId() );
							
			_index++;
			
		}
		_current = null;
		
		//fecha totalizadores
		groupPropertyAnaliser.closeGroups();
		
		getRequest().setAttribute(var, null);
		getRequest().setAttribute(varIndex, null);
	}

	public void registerColumn(ColumnTag columnTag){
		this.columns.add(columnTag);
	}
	
	private LoopTagStatus getLoopTagStatus() {
		return new LoopTagStatus(){

			public Object getCurrent() {
				return DataGridTag.this._current;
			}

			public int getIndex() {
				return DataGridTag.this._index;
			}

			public int getCount() {
				return DataGridTag.this._count;
			}

			public boolean isFirst() {
				return DataGridTag.this._isFirst;
			}

			public boolean isLast() {
				return DataGridTag.this._isLast;
			}

			public Integer getBegin() {
				return DataGridTag.this._begin;
			}

			public Integer getEnd() {
				return DataGridTag.this._end;
			}

			public Integer getStep() {
				return DataGridTag.this._step;
			}
			
		};
	}
	
	public String getIdCapitalized(){
		String id = getId();
		if(id != null){
			return Util.strings.captalize(id);
		}
		return null;
	}

	private CyclicIterator<String> getBodyStyleClassesIterator() {
		if(Util.strings.isEmpty(bodyStyleClasses)) return new CyclicIterator<String>();
		return new CyclicIterator<String>(bodyStyleClasses.split(","));
	}
	
	private CyclicIterator<String> getBodyStylesIterator() {
		if(Util.strings.isEmpty(bodyStyles)) return new CyclicIterator<String>();
		return new CyclicIterator<String>(bodyStyles.split(","));
	}

	public Status getCurrentStatus() {
		return currentStatus;
	}
	public void setCurrentStatus(Status currentStatus) {
		this.currentStatus = currentStatus;
	}

	public String getFooterStyle() {
		return footerStyle;
	}

	public String getFooterStyleClass() {
		return footerStyleClass;
	}

	public String getHeaderStyle() {
		return headerStyle;
	}

	public String getHeaderStyleClass() {
		return headerStyleClass;
	}

	public Object getItens() {
		return itens;
	}

	public void setBodyStyleClasses(String bodyStyleClasses) {
		this.bodyStyleClasses = bodyStyleClasses;
	}

	public void setBodyStyles(String bodyStyles) {
		this.bodyStyles = bodyStyles;
	}

	public void setFooterStyle(String footerStyle) {
		this.footerStyle = footerStyle;
	}

	public void setFooterStyleClass(String footerStyleClass) {
		this.footerStyleClass = footerStyleClass;
	}

	public void setHeaderStyle(String headerStyle) {
		this.headerStyle = headerStyle;
	}

	public void setHeaderStyleClass(String headerStyleClass) {
		this.headerStyleClass = headerStyleClass;
	}

	public void setItens(Object itens) {
		this.itens = itens;
	}


	public String getVar() {
		return var;
	}


	public String getVarStatus() {
		return varStatus;
	}


	public void setVar(String var) {
		this.var = var;
	}


	public void setVarStatus(String varStatus) {
		this.varStatus = varStatus;
	}

	public boolean isRenderBody() {
		return renderBody;
	}

	public boolean isRenderFooter() {
		return renderFooter;
	}

	public boolean isRenderHeader() {
		return renderHeader;
	}

	public void setRenderBody(boolean renderBody) {
		this.renderBody = renderBody;
	}

	public void setRenderFooter(boolean renderFooter) {
		this.renderFooter = renderFooter;
	}

	public void setRenderHeader(boolean renderHeader) {
		this.renderHeader = renderHeader;
	}

	@Override
	public String getBody() throws JspException, IOException {
		return super.getBody();
	}

	public Boolean getDynaLine() {
		return dynaLine;
	}

	public String getVarIndex() {
		return varIndex;
	}

	public void setDynaLine(Boolean dynaLine) {
		this.dynaLine = dynaLine;
	}

	public void setVarIndex(String varIndex) {
		this.varIndex = varIndex;
	}

	/**
	 * Adiciona um bloco para ser renderizado no detalhe
	 * @param o
	 * @return
	 */
	public boolean add(PanelRenderedBlock o) {
		return blocks.add(o);
	}

	public String enhanceProperty(String id, String propertyName, String value){
		if(value == null){
			return "document.getElementById('"+id+"')."+propertyName+" = null;";
		}
		return "document.getElementById('"+id+"')."+propertyName+" = \""+value+"\";";
	}
	
	public String enhanceProperty(String id, String propertyName, List<String> value){
		return "document.getElementById('"+id+"')."+propertyName+" = [\n"+toStringEnhancedProperty(value)+"\n];";
	}

	private String toStringEnhancedProperty(List<String> value) {
		StringBuilder builder = new StringBuilder();
		int i = 0;
		int lastIndex = value.size()-1;
		for (String string : value) {
			boolean isLast = i == lastIndex;
			string = string.replaceAll("\n", " ");
			string = string.replaceAll("\t", " ");
			string = string.replaceAll("\r", " ");
			string = TagUtils.escape(string.trim());
			string = string.replaceAll("</script>", "<\\\\/script>");
			//System.out.println(string);
			string = "\""+string+"\"";
			if(!isLast){
				string += ", \n";
			}
			builder.append(string);
			i++;
		}
		return builder.toString();
	}

	public String getVarRowIndex() {
		return varRowIndex;
	}

	public void setVarRowIndex(String varRowIndex) {
		this.varRowIndex = varRowIndex;
	}
	
	public String getVarSequence() {
		return varSequence;
	}
	
	public void setVarSequence(String varSequence) {
		this.varSequence = varSequence;
	}
	
	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public void setHasColumns(boolean b) {
		this.hasColumns  = b;
		
	}

	public String getRow() {
		return row;
	}

	public String getRowSeparator() {
		return rowSeparator;
	}

	public void setRow(String row) {
		this.row = row;
	}

	public void setRowSeparator(String rowSeparator) {
		this.rowSeparator = rowSeparator;
	}

	public Object getItemType() {
		return itemType;
	}

	public void setItemType(Object itemType) {
		this.itemType = itemType;
	}
	
	
	//=====================================================================

	public String getGroupProperty() {
		return groupProperty;
	}
	public void setGroupProperty(String groupProperty) {
		this.groupProperty = groupProperty;
		
		//converte o groupProperty
		String[] split = groupProperty.split("(\\s*)?[,;](\\s*)?");
		List<String> grupos = new ArrayList<String>();
		for (String string : split) {
			if(Util.strings.isNotEmpty(string)){
				grupos.add(string);
			}
		}
		
		this.groupProperties = grupos.toArray(new String[grupos.size()]);
	}
	
	public String getBodyStyles() {
		return bodyStyles;
	}

	public String getBodyStyleClasses() {
		return bodyStyleClasses;
	}


	public String getGroupStyle() {
		return groupStyle;
	}
	public void setGroupStyle(String groupStyle) {
		this.groupStyle = groupStyle;
	}

	public String getGroupStyleClasses() {
		return groupStyleClasses;
	}
	public void setGroupStyleClasses(String groupStyleClass) {
		this.groupStyleClasses = groupStyleClass;
	}
	
	public int getNumberOfColumns() {
		return columns.size();
	}
	
	public String[] getGroupProperties() {
		return groupProperties;
	}

	public String getVarGroupItens() {
		return varGroupItens;
	}

	public String getVarGroupPropertyIndex() {
		return varGroupPropertyIndex;
	}

	public String getVarLastGroupProperty() {
		return varLastGroupProperty;
	}

	class GroupPropertyAnaliser extends AbstractGroupPropertyAnaliser{
		
		private DataGridTag dataGridTag;
		private String varLastGroupProperty;
		private String varGroupPropertyIndex;
		private String varGroupItens;
		private String groupStyleClasses;
		private String groupStyle;
		private int numberOfColumns;
		
		private Iterator<String> bodyStyleIterator;
		private Iterator<String> bodyStyleClassIterator;
		
		public GroupPropertyAnaliser(DataGridTag dataGridTag){
			super(dataGridTag.getGroupProperties());
			if(groupProperties != null){
				this.dataGridTag = dataGridTag;
				this.varLastGroupProperty = dataGridTag.getVarLastGroupProperty();
				this.varGroupPropertyIndex = dataGridTag.getVarGroupPropertyIndex();
				this.varGroupItens = dataGridTag.getVarGroupItens();
				this.groupStyleClasses = dataGridTag.getGroupStyleClasses();
				this.groupStyle = dataGridTag.getGroupStyle();
				this.numberOfColumns = dataGridTag.getNumberOfColumns();
				
				this.bodyStyleIterator = dataGridTag.bodyStyleIterator;
				this.bodyStyleClassIterator = dataGridTag.bodyStyleClassIterator;
			}
		}
		
		private String getLevel(String groupStyleClass, int i) {
			if(groupStyleClass == null){
				return "";
			}
			String[] split = groupStyleClass.split("(\\s*)?,(\\s*)?");
			return split[Math.min(split.length - 1, i)];
		}
		
		@Override
		protected void renderGroup(int uid, int i, Object property) {
			try {
				String[] gruposString = this.groupid.split("~");
				String group = "";
				for (int j = 0 ; j < i ; j++) {
					group += gruposString[j] + "~";
				}
				group = "group=\""+ group +"\"";
				int paddingLeft = 20 * i + 2;
				String groupDisplay = TagUtils.getObjectDescriptionToString(property);
				
				dataGridTag.getOut().println("<tr class=\""+getLevel(groupStyleClasses, i)+"\" style=\""+getLevel(groupStyle, i)+"\" "+group+" isgroupline=\"true\">");
				dataGridTag.getOut().println("<td colspan=\""+numberOfColumns+"\" style=\"padding-left: "+paddingLeft+"\">"+groupDisplay+"</td>");
				dataGridTag.getOut().println("</tr>");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			((CyclicIterator<String>) bodyStyleIterator).reset();
			((CyclicIterator<String>) bodyStyleClassIterator).reset();
		}
		
		@Override
		protected void renderTotalizer(int i, Object oldProperty) {
			dataGridTag.getRequest().setAttribute(varLastGroupProperty, oldProperty);				
			dataGridTag.getRequest().setAttribute(varGroupPropertyIndex, i);				
			dataGridTag.getRequest().setAttribute(varGroupItens, this.groupItens[i]);
			try {
				dataGridTag.doBody();
			} catch (Exception e) {
				e.printStackTrace();
			}
			dataGridTag.getRequest().setAttribute(varGroupItens, null);
			dataGridTag.getRequest().setAttribute(varGroupPropertyIndex, null);
			dataGridTag.getRequest().setAttribute(varLastGroupProperty, null);
		}
		
		@Override
		protected void changeStatus() {
			dataGridTag.setCurrentStatus( DataGridTag.Status.GROUPTOTAL );
		}
		
	}
	
	public List<DataGridListener> getListeners() {
		return listeners;
	}
	
	public void registerListener(DataGridListener listener){
		listeners.add(listener);
	}

	public void setIndexProperty(String indexProperty) {
		this.indexProperty = indexProperty;
	}
	
	
	DataGridListener compositeListener = null;
	public DataGridListener createCompositeListener(){
		if(compositeListener == null){
			compositeListener = new DataGridCompositeListener(listeners);
		}
		return compositeListener;
	}

	public void onRenderColumnHeader(String label) {
		compositeListener.onRenderColumnHeader(label);
	}

	public void onRenderColumnHeaderBody() throws IOException {
		compositeListener.onRenderColumnHeaderBody();
	}
	
}