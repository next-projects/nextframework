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

import java.beans.PropertyEditor;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.exception.TagNotFoundException;
import org.nextframework.util.Util;

/**
 * @author rogelgarcia
 * @since 25/01/2006
 * @version 1.1
 */
@SuppressWarnings("deprecation")
public class BaseTag extends SimpleTagSupport implements DynamicAttributes {
	
	public static Class<? extends BaseTagPropertyEditorsManager> propertyEditorManagerClass = BaseTagPropertyEditorsManager.class;
	
	public static BaseTagTemplateManager templateManager = new BaseTagTemplateManager();
	
	protected static Log log = LogFactory.getLog(BaseTag.class);
	
	/**atributo que existe em todas as tags*/
	protected String id;
	
	protected Boolean rendered;
	protected Boolean bypass;
	
	public Boolean getBypass() {
		return bypass;
	}
	public void setBypass(Boolean bypass) {
		this.bypass = bypass;
	}

	public Boolean getRendered() {
		return rendered;
	}

	public void setRendered(Boolean printWhen) {
		this.rendered = printWhen;
	}
	
	
	private String STACK_ATTRIBUTE_NAME = "TagStack";
	
	public String TAG_ATTRIBUTE = "tag";
	
	protected Map<String, Object> dynamicAttributesMap = new HashMap<String, Object>();
	
	private BaseTag parent;

//	private static ThreadLocal<Boolean> editorsInitialized = new ThreadLocal<Boolean>();
	
	public String generateUniqueId(){
		Integer idsequence = (Integer) getRequest().getAttribute("IDSEQUENCE");
		if(idsequence == null){
			idsequence = 0;
		}
		getRequest().setAttribute("IDSEQUENCE", idsequence + 1);
		return ("GENERATED_") + idsequence;
	}
	
	public BaseTag(){
    	initPropertyEditors();
	}

	private void initPropertyEditors() {
	}

	public BaseTag getParent() {
		return parent;
	}

	protected void pushAttribute(String name, Object value){
		getStack(name+"_stack").push(value);
		getRequest().setAttribute(name, value);
	}
	
	@SuppressWarnings("unchecked")
	private Stack<Object> getStack(String string) {
		Stack<Object> stack = (Stack<Object>) getRequest().getAttribute(string);
		if(stack == null){
			stack = new Stack<Object>();
			getRequest().setAttribute(string, stack);
		}
		return stack;
	}

	@SuppressWarnings("unchecked")
	protected Object popAttribute(String name){
		Stack stack = getStack(name+"_stack");
		Object pop = stack.pop();
		if(!stack.isEmpty()){
			getRequest().setAttribute(name, stack.peek());
		} else {
			getRequest().setAttribute(name, null);
		}
		return pop;
	}
	
	/**
	 * Renderiza o corpo da tag
	 * @throws JspException
	 * @throws IOException
	 */
	protected final void doBody() throws JspException, IOException {
		if (getJspBody()!=null) {
			try {
				getJspBody().invoke(getOut());
			} catch (Exception e) {
				printException(e);
			}
		}
	}

	private void printException(Exception e) throws IOException {
		String extype = "";
		if(e.getClass().getName().startsWith("java.lang")){
			extype = e.getClass().getSimpleName() + ": ";
		}
		getOut().println("<font class=\"exceptionitem\" color=\"red\">" + extype +"<b>"+ e.getMessage() +"</b>"+ "</font>");
		Throwable cause = getNextException(e);
		while(cause != null){
			getOut().println("<font class=\"exceptionitem\" color=\"red\">" + cause.getMessage() + "</font>");
			cause = getNextException(cause);
		}
		e.printStackTrace();
	}
	
	private Throwable getNextException(Throwable e) {
		if(e == null){
			return null;
		}
		if(e instanceof ServletException){
			if(e.getCause() != null && e.getCause() != e){
				return e.getCause();
			} else {
				return ((ServletException)e).getRootCause();	
			}
		}
		return e.getCause();
	}

	BaseTagPropertyEditorsManager baseTagPropertyEditorsManagerCache = null;
	
	protected Map<Class<?>, PropertyEditor> getPropertyEditors() {
		if(baseTagPropertyEditorsManagerCache == null){
			baseTagPropertyEditorsManagerCache = TagUtils.getPropertyEditorsManager();
		}
		return baseTagPropertyEditorsManagerCache.getPropertyEditors();
	}

	public String getBody() throws JspException, IOException {
		if(getJspBody() != null){
			ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
			PrintWriter writer = new PrintWriter(arrayOutputStream);
			getJspBody().invoke(writer);
			writer.flush();
			return arrayOutputStream.toString();
		} else {
			return "";
		}
	}
	
	public JspWriter getOut() {
		return getPageContext().getOut();
	}
	
	public <E> E findParent(Class<E> tagClass) {
		return findParent(tagClass, false);
	}
	
	/**
	 * Retorna a primeira tag encontrada que for de alguma das classes passadas
	 * @param classes
	 * @return
	 */
	public BaseTag findFirst(Class<? extends BaseTag>... classes){
		List<BaseTag> tags = getTagsFromTopToThis();
		boolean found = false;
		for (Iterator<?> iter = tags.iterator(); iter.hasNext();) {
			BaseTag element = (BaseTag) iter.next();
			if(element == this){
				found = true;
			}
			if(found){
				iter.remove();
			}
		}
		Collections.reverse(tags);
		List<Class<? extends BaseTag>> asList = Arrays.asList(classes);
		for (BaseTag tag : tags) {
			for (Class<? extends BaseTag> class1 : asList) {
				if(class1.isAssignableFrom(tag.getClass())){
					return tag;
				}
			}
			//if(asList.contains(tag.getClass())){
			//	return tag;
			//}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	protected BaseTag findFirst2(Class... classes){
		List<BaseTag> tags = getTagsFromThisToTop();
		tags.remove(0);
		List<Class> asList = Arrays.asList(classes);
		for (BaseTag tag : tags) {
			for (Class class1 : asList) {
				if(class1.isAssignableFrom(tag.getClass())){
					return tag;
				}
			}
		}
		return null;
	}
	
	/**
	 * Retorna a lista de tags ordenadas dessa tag(primeira) at� a ultima(topo das tags)
	 * @return
	 */
	protected List<BaseTag> getTagsFromThisToTop() {
		List<BaseTag> tags = new ArrayList<BaseTag>();
		tags.addAll(getTagStack());
		Collections.reverse(tags);
		return tags;
	}
	
	protected List<BaseTag> getTagsFromTopToThis() {
		List<BaseTag> tags = new ArrayList<BaseTag>();
		tags.addAll(getTagStack());
		return tags;
	}

	@SuppressWarnings("unchecked")
	public <E> E findParent(Class<E> tagClass, boolean throwExceptionIfNotFound) throws TagNotFoundException {
		Stack<BaseTag> tagStack = getTagStack();
		for (int i = tagStack.size()-2; i >= 0; i--) {
			BaseTag baseTag = tagStack.get(i);
			if(tagClass.equals(baseTag.getClass())){
				return (E)baseTag;
			}
		}
		if(throwExceptionIfNotFound){
			throw new TagNotFoundException("A tag "+this.getClass().getName()+" tentou procurar uma tag "+tagClass.getName()+" mas n�o encontrou. Provavelmente � obrigat�rio a tag "+this.getClass().getName()+" estar aninhada a uma tag "+tagClass.getName());
		}
		return null;
	}
	
	/**
	 * Acha uma tag que for do tipo da classe passada ou subclasse. 
	 * @param <E>
	 * @param tagClass Classe da tag a ser encontrada. Pode ser passada uma tag pai ou interface que a tag possui.
	 * @param throwExceptionIfNotFound
	 * @return
	 * @throws TagNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public <E> E findParent2(Class<E> tagClass, boolean throwExceptionIfNotFound) throws TagNotFoundException {
		Stack<BaseTag> tagStack = getTagStack();
		for (int i = tagStack.size()-2; i >= 0; i--) {
			BaseTag baseTag = tagStack.get(i);
			if(tagClass.isAssignableFrom(baseTag.getClass())){
				return (E)baseTag;
			}
		}
		if(throwExceptionIfNotFound){
			throw new TagNotFoundException("A tag "+this.getClass().getName()+" tentou procurar uma tag "+tagClass.getName()+" mas n�o encontrou.");
		}
		return null;
	}

	@Override
	public final void doTag() throws JspException, IOException {
		if(Boolean.FALSE.equals(rendered)){
			return;
		}
		if(Boolean.TRUE.equals(bypass)){
			//se for para pular a funcionalidade dessa tag.. e utilizar s� as tags filhas
			doBody();
			return;
		}
		boolean registeringDataGrid = getRequest().getAttribute(ColumnTag.REGISTERING_DATAGRID) != null;
		if(registeringDataGrid && !(this instanceof ColumnChildTag)){
			//se estiver registrando o datagrid n�o precisa renderizar nada
			return;
		}
		if (!getTagStack().isEmpty()) {
			parent = getTagStack().peek();
		}
		try{
			//coloca a tag no escopo
			pushTagOnStack();
	
			//verificar se est� dentro de um panelGrid
			//panelGrid tem um comportamento especial para poder suportar tags dentro dele sem utilizar a tag Panel
			BaseTag parent = getParent();
			while (parent != null && parent instanceof LogicalTag) {
				parent = parent.getParent();
			}
			PanelGridTag panelGrid = parent instanceof PanelGridTag ? (PanelGridTag) parent : null;
			GetContentTag getContent = findParentGetContent();
			if (getContent != null) {
				doTagInGetContent(getContent);
			} else if (panelGrid != null && !(this instanceof PanelTag) && !(this instanceof LogicalTag)) {
				doTagInPanelGrid(panelGrid);
			} else {
				try {
					doComponent();
				} catch (JspException e) {
					throw e;
				} catch (Exception e) {
					try {
						printException(e);
					} catch (IOException e1) {
						throw new JspException(e);
					}
					//throw new JspException(e);
				}
			}
			//tira a tag do escopo
		} finally {
			popTagFromStack();	
		}
		
		
	}
	
	private GetContentTag findParentGetContent() {
		//TODO MELHORAR GETCONTENT TAG PERFORMANCE
		Stack<BaseTag> tagStack = getTagStack();
		for (int i = tagStack.size()-2; i >= 0; i--) {
			BaseTag baseTag = tagStack.get(i);
			if(GetContentTag.class.equals(baseTag.getClass())){
				GetContentTag contentTag = (GetContentTag) baseTag;
				if(contentTag.getTag(this)){
					return contentTag;
				}
			}
		}
		return null;
	}
	
	protected void doTagInGetContent(GetContentTag getContent) throws JspException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintWriter printWriter = new PrintWriter(outputStream);
		getPageContext().pushBody(printWriter);
		try {
			doComponent();
		} catch (JspException e) {
			throw e;
		} catch (Exception e){
			throw new JspException(e);
		}
		getPageContext().popBody();
		printWriter.flush();
		String body = outputStream.toString();
		
		getContent.register(body);
	}

	private void doTagInPanelGrid(PanelGridTag panelGrid) throws JspException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintWriter printWriter = new PrintWriter(outputStream);
		getPageContext().pushBody(printWriter);
		try {
			doComponent();
		} catch (JspException e) {
			throw e;
		} catch (Exception e){
			throw new JspException(e);
		}
		getPageContext().popBody();
		printWriter.flush();
		String body = outputStream.toString();
		PanelRenderedBlock block = new PanelRenderedBlock();
		Map<String, Object> properties =new HashMap<String, Object>();
		addBasicPanelProperties(properties);
		addPanelProperties(properties);
		
		block.setBody(body);
		block.setProperties(properties);
		panelGrid.addBlock(block);
	}

	protected void addBasicPanelProperties(Map<String, Object> properties) {
		Set<String> keySet = dynamicAttributesMap.keySet();
		for (String string : keySet) {
			if(string.startsWith("panel")){
				properties.put(string.substring("panel".length()), dynamicAttributesMap.get(string));
			}
		}
	}

	protected void addPanelProperties(Map<String, Object> properties) {
		
	}

	protected void doComponent() throws Exception{
		
	}

	private void pushTagOnStack() {
		getTagStack().add(this);
	}

	private void popTagFromStack() {
		getTagStack().pop();
	}

	protected Stack<BaseTag> getTagStack() {
		@SuppressWarnings("unchecked")
		Stack<BaseTag> stack = (Stack<BaseTag>) getRequest().getAttribute(STACK_ATTRIBUTE_NAME);
		if(stack == null){
			stack = new Stack<BaseTag>();
			getRequest().setAttribute(STACK_ATTRIBUTE_NAME, stack);
		}
		return stack;
	}

	public HttpServletRequest getRequest() {
		return (HttpServletRequest)(getPageContext()).getRequest();
	}

	public HttpServletResponse getResponse() {
		return (HttpServletResponse)getPageContext().getResponse();
	}

	public ServletContext getServletContext() {
		return getPageContext().getServletContext();
	}

	public PageContext getPageContext() {
		return (PageContext)getJspContext();
	}
	
	protected void includeTextTemplate() throws ServletException, IOException, ELException, JspException{
		String url = "/WEB-INF/classes/"+getTemplateName()+".jsp";
		templateManager.checkTemplate(this, getTemplateName(), null);
		includeTextTemplateFile(url);
	}

	protected void includeTextTemplate(String suffix) throws ServletException, IOException, ELException, JspException{
		String url = "/WEB-INF/classes/"+getTemplateName()+"-"+suffix+".jsp";
		templateManager.checkTemplate(this, getTemplateName(), suffix);
		includeTextTemplateFile(url);
	}
	
	/**
	 * Sobrescreva caso sua tag venha de um JAR diferente do JAR do next
	 * @param resourcePath
	 * @return
	 */
	protected boolean isTagFromJar(String resourcePath) {
		return resourcePath.contains("next");
	}
	
	protected void includeJspTemplate() throws ServletException, IOException {
		String url = "/WEB-INF/classes/"+getTemplateName()+".jsp";
		templateManager.checkTemplate(this, getTemplateName(), null);
		includeJspTemplateFile(url);
	}

	protected String getTemplateName() {
		return this.getClass().getName().replaceAll("\\.", "/");
	}
	
	protected void includeJspTemplate(String suffix) throws ServletException, IOException {
		String url = "/WEB-INF/classes/"+getTemplateName()+"-"+suffix+".jsp";
		templateManager.checkTemplate(this, getTemplateName(), suffix);
		includeJspTemplateFile(url);
	}
	
	protected void includeTextTemplateFile(String template) throws ServletException, IOException, ELException, JspException{
		String[] text = templateManager.getTextFromTemplate(this, template);
		Object last = getRequest().getAttribute(TAG_ATTRIBUTE);
		getRequest().setAttribute(TAG_ATTRIBUTE, this);
		
		evaluateAndPrint(text[0]);
		if(getJspBody()!= null){
			getJspBody().invoke(null);
			//tirei o println talvez d� algum efeito colateral 31/08/2006 -> O println tava atrapalhando em determinadas tags
			//getOut().println();
		}
		if(!text[1].trim().equals("")){
			evaluateAndPrint(text[1]);
		}
		
		
		getRequest().setAttribute(TAG_ATTRIBUTE, last);
	}
	
	protected void evaluateAndPrint(String expression) throws ELException, IOException {
		Object evaluate = TagUtils.evaluate(expression, getPageContext());
		getOut().print(evaluate);
	}
	
	protected Object evaluate(String expression) throws ELException {
		PageContext pageContext = getPageContext();
		return TagUtils.evaluate(expression, pageContext);
	}

	protected void evaluateAndPrintIncludeTag(String expression) throws ELException, IOException {
		Object last = getRequest().getAttribute(TAG_ATTRIBUTE);
		getRequest().setAttribute(TAG_ATTRIBUTE, this);
		Object evaluate = TagUtils.evaluate(expression, getPageContext());
		getOut().print(evaluate);
		getRequest().setAttribute(TAG_ATTRIBUTE, last);
	}
	
	protected void includeJspTemplateFile(String template) throws ServletException, IOException{
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(arrayOutputStream);
		//Object lastBody = getRequest().getAttribute(JSPFRAGMENT);
		//getRequest().setAttribute(JSPFRAGMENT, getJspBody());
		TagUtils.pushJspFragment(getRequest(), getJspBody());
		
		Object last = getRequest().getAttribute(TAG_ATTRIBUTE);
		getRequest().setAttribute(TAG_ATTRIBUTE, this);
		
		String tagAttribute = Util.strings.uncaptalize(this.getClass().getSimpleName());
		pushAttribute(tagAttribute, this);
		
		dispatchToTemplate(template, writer);

		popAttribute(tagAttribute);
		
		writer.flush();
		getOut().write(arrayOutputStream.toString());
		//getOut().write(arrayOutputStream.toByteArray());
		
		getRequest().setAttribute(TAG_ATTRIBUTE, last);
		//getRequest().setAttribute(JSPFRAGMENT, lastBody);
		TagUtils.popJspFragment(getRequest());
	}
	
	//Faz o dispatch para determinada url, coloca a saida no writer
	private void dispatchToTemplate(String template, PrintWriter writer) throws ServletException, IOException {
		WrappedWriterResponse response = new WrappedWriterResponse(getResponse(), writer);
		RequestDispatcher requestDispatcher = null;
		requestDispatcher = getRequest().getRequestDispatcher(template);
		requestDispatcher.include(getRequest(), response);
	}
	
	public void setDynamicAttribute(String uri, String localName, Object value) throws JspException {
		if(localName != null){
			localName = localName.toLowerCase();
		}
		dynamicAttributesMap.put(localName, value);
	}

	public Map<String, Object> getDynamicAttributesMap() {
		return dynamicAttributesMap;
	}

	@SuppressWarnings("unchecked")
	protected boolean isEntity(Class c) {
		return TagUtils.hasId(c);
	}

	/**
	 * Verifica a igualdade de dois objetos (analiza id)
	 * @param value1
	 * @param value2
	 * @return
	 */
	public boolean areEqual(Object value1, Object value2) {
		boolean id = true;
		if(value1 == null){
			return false;
		} else {
			id = TagUtils.hasId(value1.getClass()) && id;
		}
		Class<? extends Object> class1 = value1.getClass();
		if(class1.getName().contains("$$")){
			class1 = class1.getSuperclass();
		}
		
		if(value2 == null){
			return false;
		} else {
			id = TagUtils.hasId(value2.getClass()) && id;
		}
		Class<? extends Object> class2 = value2.getClass();
		if(class2.getName().contains("$$")){
			class2 = class2.getSuperclass();
		}
		if(id){
			BeanDescriptor bd1 = BeanDescriptorFactory.forBean(value1);
			BeanDescriptor bd2 = BeanDescriptorFactory.forBean(value2);
			if(class1.equals(class2)){
				if(bd1.getId().getClass().getName().startsWith("java")){ //native class type (if not native, check string equality)
					return bd1.getId().equals(bd2.getId());
				}
			} else {
				boolean oneInstanceofOther = class1.isAssignableFrom(class2) || class2.isAssignableFrom(class1);
				if(oneInstanceofOther){
					//tentar verificar pelo id.. quando uma classe extender a outra
					return bd1.getId().equals(bd2.getId());
				}
			}
			return TagUtils.getObjectValueToString(value1, false, null).equals(TagUtils.getObjectValueToString(value2, false, null));
		}
		return value1.equals(value2);
	}

	public String getDynamicAttributesToString() {
		return getDynamicAttributesToString(dynamicAttributesMap);
	}
	
	public String getDynamicAttributesToString(Map<String, Object> dynamicAttributesMap) {
		StringBuilder builder = new StringBuilder(" ");
		Set<String> keySet = dynamicAttributesMap.keySet();
		for (String key : keySet) {
			boolean inPanelGrid = findParent(PanelGridTag.class) != null;
			if(inPanelGrid && key.startsWith("panel")){
				continue;//nao montar tags iniciadas com panel... provavelmente est� configurando o panel externo
			}
			builder.append(" ");
			builder.append(key);
			builder.append("=");
			builder.append("'");
			
			Object object = dynamicAttributesMap.get(key);
			if(object != null){
				if(object instanceof String && ((String)object).startsWith("ognl:")){
					object = ((String)object).substring(5);
					object = getOgnlValue((String)object);
				}
				object = object.toString();
			}
			builder.append(TagUtils.escapeSingleQuotes((String)object));
			builder.append("'");
		}
		String toString = builder.toString();
		return toString;
	}

	
	public static OgnlEvaluator evaluator = new OgnlEvaluator() {
		
		@SuppressWarnings("unchecked")
		@Override
		public <E> E evaluate(String expression, Class<E> expectedType, BaseTag baseTag) {
			WebContextMap contextMap = new WebContextMap(baseTag.getRequest());
			E value; 
			if(expectedType != null){
				value = OgnlExpressionParser.parse(expression, expectedType, contextMap);
			} else {
				value = (E) OgnlExpressionParser.parse(expression, contextMap);
			}
			return value;
		}
	};

	
	
	public Object getOgnlValue(String expression) {
		return evaluator.evaluate(expression, null, this);
	}
	
	protected <E> E getOgnlValue(String expression, Class<E> expectedType) {
		return evaluator.evaluate(expression, expectedType, this);
	}

	/**
	 * Atributo
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * Atributo
	 * @return
	 */
	public void setId(String id) {
		this.id = id;
	}

	public void setDynamicAttributesMap(Map<String, Object> dynamicAttributesMap) {
		this.dynamicAttributesMap.putAll(dynamicAttributesMap);
	}
	
}

