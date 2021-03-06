package org.nextframework.view;

import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.tagext.JspFragment;

import org.hibernate.LazyInitializationException;
import org.nextframework.bean.annotation.DescriptionProperty;
import org.nextframework.core.config.ViewConfig;
import org.nextframework.core.web.NextWeb;
import org.nextframework.core.web.WebRequestContext;
import org.nextframework.persistence.DAOUtils;
import org.nextframework.persistence.GenericDAO;
import org.nextframework.persistence.HibernateUtils;
import org.nextframework.service.ServiceFactory;
import org.nextframework.util.ReflectionCache;
import org.nextframework.util.ReflectionCacheFactory;
import org.nextframework.util.Util;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.InvalidPropertyException;

@SuppressWarnings("deprecation")
public class TagUtils {

	/**
	 * Faz o escape de aspas duplas
	 * @param opValue
	 * @return
	 */
	public static String escape(String opValue) {
		if(opValue==null) return null;
		
		return opValue.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"");
	}

	public static String escapeSingleQuotes(String opValue) {
		if(opValue==null) return null;
		return opValue.replaceAll("\\\\", "\\\\\\\\").replaceAll("\'", "\\\\'");
	}
	

	public static Object evaluate(String expression, JspContext pageContext) throws ELException {
		return ViewUtils.evaluate(expression, (PageContext) pageContext, Object.class);
	}

	public static boolean hasId(Class<? extends Object> class1) {
		if(class1.getName().contains("$$")){
			//a classe enhanceada pelo Hibernate d� pau ao fazer class.getMethods .. temos que pegar a classe superior nao enhanceada
			class1 = class1.getSuperclass();
		}
		ReflectionCache reflectionCache = ReflectionCacheFactory.getReflectionCache();
		while (class1 != null && !class1.equals(Object.class)) {
			Method[] methods = reflectionCache.getMethods(class1);
			for (Method method : methods) {
				if (reflectionCache.isAnnotationPresent(method, Id.class)) {
					return true;
				}
			}
			class1 = class1.getSuperclass();
		}
		return false;
	}

	public static boolean hasDescriptionProperty(Class<? extends Object> class1) {
		if(class1.getName().contains("$$")){
			//a classe enhanceada pelo Hibernate d� pau ao fazer class.getMethods .. temos que pegar a classe superior nao enhanceada
			class1 = class1.getSuperclass();
		}
		ReflectionCache reflectionCache = ReflectionCacheFactory.getReflectionCache();
		while (!class1.equals(Object.class)) {
			Method[] methods = reflectionCache.getMethods(class1);
			for (Method method : methods) {
				if (reflectionCache.isAnnotationPresent(method, DescriptionProperty.class)) {
					return true;
				}
			}
			class1 = class1.getSuperclass();
		}
		return false;
	}
	

	public static Map<Class<?>, PropertyEditor> getPropertyEditorsFromRequest() {
		return getPropertyEditorsManager(NextWeb.getRequestContext()).getPropertyEditors();
	}

	public static BaseTagPropertyEditorsManager getPropertyEditorsManager() {
		return getPropertyEditorsManager(NextWeb.getRequestContext());
	}
	
	public static BaseTagPropertyEditorsManager getPropertyEditorsManager(WebRequestContext requestContext) {
		String attributeName = BaseTagPropertyEditorsManager.class.getName();
		BaseTagPropertyEditorsManager baseTagPropertyEditorsManager = (BaseTagPropertyEditorsManager) requestContext.getAttribute(attributeName);
		if(baseTagPropertyEditorsManager == null){
			baseTagPropertyEditorsManager = BeanUtils.instantiate(BaseTag.propertyEditorManagerClass);
			NextWeb.getRequestContext().setAttribute(attributeName, baseTagPropertyEditorsManager);
		}
		
		return baseTagPropertyEditorsManager;
	}

	

	public static String getObjectValueToString(Object value) {
		return getObjectValueToString(value, false, null);
	}
	
	public static String getObjectValueToString(Object value, boolean includeDescription) {
		return getObjectValueToString(value, includeDescription, null);
	}
	/**
	 * Transforma o objeto em uma String correta para ser colocada em algum value
	 * Faz o IdStringStyle 
	 * @param value
	 * @param pattern TODO
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String getObjectValueToString(Object value, boolean includeDescription, String pattern) {
		if(value == null) return "";
		PropertyEditor propertyEditor = TagUtils.getPropertyEditorsFromRequest().get(value.getClass());
		try {
			if(Util.strings.isNotEmpty(pattern)) { //TODO REVER ESSA L�GICA DE FORMATA��O, EST� EM V�RIOS LUGARES DO FRAMEWORK, UNIFICAR
				if(value instanceof Calendar){
					value = ((Calendar)value).getTime();
				}
				if(value instanceof Date){ //FIXME
					SimpleDateFormat sdf = new SimpleDateFormat(pattern);
					return sdf.format(value);
				} else if(value instanceof Number){
					DecimalFormat df = new DecimalFormat(pattern);
					return df.format(value);
				}
			}
			if(propertyEditor != null) {
				propertyEditor.setValue(value);
				return propertyEditor.getAsText();
			} else if(hasId(value.getClass())) {
				return Util.strings.toStringIdStyled(value, includeDescription);
			} else if(value instanceof Enum) {
				return ((Enum)value).name();
			} else if(value instanceof Class) {
				return ((Class)value).getName();
			}
			
			return value.toString();
		} catch (LazyInitializationException e) {
			String id = "";
			try{
				id = Util.strings.toStringIdStyled(value, false);
			}catch(LazyInitializationException e2){
				
			}
			return value.getClass().getSimpleName()+" [N�o foi poss�vel fazer toString LazyInicializationException] "+id;
		} catch(InvalidPropertyException e1){
			if(ServiceFactory.getService(ViewConfig.class).isAutoLoadOnView()){
				//UMA COISA MUITO FOR�ADA.. TENTANDO LOADAR O OBJETO NA FOR�A
				try {
					BaseTag.log.warn("Perda de performance! Carregando objeto sob demanda "+value.getClass());
					if(value != null && value.getClass().getName().contains("$$") && e1.getCause().getCause() instanceof LazyInitializationException){
						GenericDAO daoForClass = DAOUtils.getDAOForClass(value.getClass().getSuperclass());
						value = daoForClass.load(value);
						//value = new QueryBuilder(Next.getObject(HibernateTemplate.class)).from(value.getClass().getSuperclass()).entity(value).unique();
						return getObjectValueToString(value, includeDescription, pattern);
					} else {
						throw e1;
					}
				} catch (NullPointerException e) {
					throw e1;
				}	
			}
			else {
				throw e1;
			}
		}
		
	}

	public static String getObjectDescriptionToString(Object value) {
		if(value == null) return "";
		PropertyEditor propertyEditor = getPropertyEditorsFromRequest().get(value.getClass());
		return getObjectDescriptionToString(value, propertyEditor);
	}

	@SuppressWarnings("unchecked")
	public static String getObjectDescriptionToString(Object value, PropertyEditor propertyEditor) {
		if(value == null) return "";
		try {
			if(HibernateUtils.isLazy(value)){
				value = HibernateUtils.getLazyValue(value);
			}
			if(propertyEditor != null){
				propertyEditor.setValue(value);
				return propertyEditor.getAsText();
			} else if(hasDescriptionProperty(value.getClass())){
				return Util.strings.toStringDescription(value);
			}
			return value.toString();
		} catch (LazyInitializationException e) {
			return value.getClass().getSimpleName()+" [N�o foi poss�vel fazer toString LazyInicializationException]";
		} catch(InvalidPropertyException e1){
			
			if(ServiceFactory.getService(ViewConfig.class).isAutoLoadOnView()){
				//TENTANDO CARREGAR O OBJETO LAZY
				BaseTag.log.warn("Perda de performance! Carregando objeto sob demanda "+value.getClass());
				try {
					if(value != null && value.getClass().getName().contains("$$") && e1.getCause().getCause() instanceof LazyInitializationException){
						GenericDAO daoForClass = DAOUtils.getDAOForClass(value.getClass().getSuperclass());
						value = daoForClass.load(value);
						//value = new QueryBuilder(Next.getObject(HibernateTemplate.class)).from(value.getClass().getSuperclass()).entity(value).unique();
						return getObjectDescriptionToString(value);
					} else {
						throw e1;
					}
				} catch (NullPointerException e) {
					throw e1;
				}
			}
			else {
				if(e1.getCause() instanceof InvocationTargetException 
						&& ((InvocationTargetException)e1.getCause()).getTargetException() instanceof LazyInitializationException){
					throw new RuntimeException("N�o foi poss�vel fazer ObjectDescriptionToString de "+value.getClass().getSimpleName()+". (Talvez seja necess�rio um fetch)", e1);
				} else {
					throw e1;	
				}
				
			}
		}
	}
	
	/*
	 * JSP FRAGMENT BEGIN
	 */

	public static JspFragment popJspFragment(HttpServletRequest request){
		List<JspFragment> jspFragmentStack = TagUtils.getJspFragmentStack(request);
		return jspFragmentStack.remove(jspFragmentStack.size() - 1);
	}

	public static void pushJspFragment(HttpServletRequest request, JspFragment jspFragment){
		List<JspFragment> jspFragmentStack = TagUtils.getJspFragmentStack(request);
		jspFragmentStack.add(jspFragment);
	}

	@SuppressWarnings("unchecked")
	public static List<JspFragment> getJspFragmentStack(HttpServletRequest request) {
		List<JspFragment> stack = (List<JspFragment>) request.getAttribute("JSPFRAGMENT");
		if(stack == null){
			stack = new ArrayList<JspFragment>();
			request.setAttribute("JSPFRAGMENT", stack);
		}
		return stack;
	}
	
	/*
	 * JSP FRAGMENT END
	 */

}
