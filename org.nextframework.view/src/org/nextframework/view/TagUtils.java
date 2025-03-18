package org.nextframework.view;

import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import org.springframework.context.MessageSourceResolvable;

@SuppressWarnings("deprecation")
public class TagUtils {

	/**
	 * Faz o escape de aspas duplas
	 * @param opValue
	 * @return
	 */
	public static String escape(String opValue) {
		if (opValue == null)
			return null;
		return opValue.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"");
	}

	public static String escapeSingleQuotes(String opValue) {
		if (opValue == null)
			return null;
		return opValue.replaceAll("\\\\", "\\\\\\\\").replaceAll("\'", "\\\\'");
	}

	public static Object evaluate(String expression, JspContext pageContext) throws ELException {
		return ViewUtils.evaluate(expression, (PageContext) pageContext, Object.class);
	}

	public static boolean hasId(Class<? extends Object> class1) {
		class1 = Util.objects.getRealClass(class1);
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
		class1 = Util.objects.getRealClass(class1);
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
		if (baseTagPropertyEditorsManager == null) {
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String getObjectValueToString(Object value, boolean includeDescription, String pattern) {

		if (value == null)
			return "";

		try {

			boolean usePattern = Util.strings.isNotEmpty(pattern) && (value instanceof Number || value instanceof Date || value instanceof Calendar);
			if (usePattern || value instanceof MessageSourceResolvable) {
				return Util.strings.toStringDescription(value, pattern, pattern, pattern, NextWeb.getRequestContext().getLocale());
			}

			PropertyEditor propertyEditor = TagUtils.getPropertyEditorsFromRequest().get(value.getClass());
			if (propertyEditor != null) {
				propertyEditor.setValue(value);
				return propertyEditor.getAsText();
			}

			if (hasId(value.getClass())) {
				return Util.strings.toStringIdStyled(value, includeDescription);
			} else if (value instanceof Enum) {
				return ((Enum) value).name();
			} else if (value instanceof Class) {
				return ((Class) value).getName();
			}

			return value.toString();

		} catch (LazyInitializationException e) {
			String id = "";
			try {
				id = Util.strings.toStringIdStyled(value, false);
			} catch (LazyInitializationException e2) {
				//Nada...
			}
			return value.getClass().getSimpleName() + " [Não foi possível fazer toString LazyInicializationException] " + id;
		} catch (InvalidPropertyException e1) {
			if (ServiceFactory.getService(ViewConfig.class).isAutoLoadOnView()) {
				//UMA COISA MUITO FORÇADA.. TENTANDO LOADAR O OBJETO NA FORÇA
				try {
					BaseTag.log.warn("Perda de performance! Carregando objeto sob demanda " + value.getClass());
					if (value != null && value.getClass().getName().contains("$$") && e1.getCause().getCause() instanceof LazyInitializationException) {
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
			} else {
				throw e1;
			}
		}

	}

	public static String getObjectDescriptionToString(Object value) {
		return getObjectDescriptionToString(value, null, null, null);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String getObjectDescriptionToString(Object value, String formatDate, String formatNumber, String formatString) {

		if (value == null)
			return "";

		try {

			if (HibernateUtils.isLazy(value)) {
				value = HibernateUtils.getLazyValue(value);
			}

			boolean usePattern = (value instanceof Number && Util.strings.isNotEmpty(formatNumber)) ||
					((value instanceof Date || value instanceof Calendar) && Util.strings.isNotEmpty(formatDate));
			if (usePattern || value instanceof MessageSourceResolvable) {
				return Util.strings.toStringDescription(value, formatDate, formatNumber, formatString, NextWeb.getRequestContext().getLocale());
			}

			PropertyEditor propertyEditor = getPropertyEditorsFromRequest().get(value.getClass());
			if (propertyEditor != null) {
				propertyEditor.setValue(value);
				return propertyEditor.getAsText();
			}

			return Util.strings.toStringDescription(value, formatDate, formatNumber, formatString, NextWeb.getRequestContext().getLocale());

		} catch (LazyInitializationException e) {

			return value.getClass().getSimpleName() + " [Não foi possível fazer toString LazyInicializationException]";

		} catch (InvalidPropertyException e1) {

			if (ServiceFactory.getService(ViewConfig.class).isAutoLoadOnView()) {
				//TENTANDO CARREGAR O OBJETO LAZY
				BaseTag.log.warn("Perda de performance! Carregando objeto sob demanda " + value.getClass());
				try {
					if (value.getClass().getName().contains("$$") && e1.getCause().getCause() instanceof LazyInitializationException) {
						GenericDAO daoForClass = DAOUtils.getDAOForClass(value.getClass().getSuperclass());
						value = daoForClass.load(value);
						//value = new QueryBuilder(Next.getObject(HibernateTemplate.class)).from(value.getClass().getSuperclass()).entity(value).unique();
						return getObjectDescriptionToString(value, formatDate, formatNumber, formatString);
					} else {
						throw e1;
					}
				} catch (NullPointerException e) {
					throw e1;
				}
			} else {
				if (e1.getCause() instanceof InvocationTargetException
						&& ((InvocationTargetException) e1.getCause()).getTargetException() instanceof LazyInitializationException) {
					throw new RuntimeException("Não foi possível fazer ObjectDescriptionToString de " + value.getClass().getSimpleName() + ". (Talvez seja necessário um fetch)", e1);
				} else {
					throw e1;
				}
			}

		}

	}

	/*
	 * JSP FRAGMENT BEGIN
	 */

	public static JspFragment popJspFragment(HttpServletRequest request) {
		List<JspFragment> jspFragmentStack = TagUtils.getJspFragmentStack(request);
		return jspFragmentStack.size() > 0 ? jspFragmentStack.remove(jspFragmentStack.size() - 1) : null;
	}

	public static void pushJspFragment(HttpServletRequest request, JspFragment jspFragment) {
		List<JspFragment> jspFragmentStack = TagUtils.getJspFragmentStack(request);
		jspFragmentStack.add(jspFragment);
	}

	@SuppressWarnings("unchecked")
	public static List<JspFragment> getJspFragmentStack(HttpServletRequest request) {
		List<JspFragment> stack = (List<JspFragment>) request.getAttribute("JSPFRAGMENT");
		if (stack == null) {
			stack = new ArrayList<JspFragment>();
			request.setAttribute("JSPFRAGMENT", stack);
		}
		return stack;
	}

	/*
	 * JSP FRAGMENT END
	 */

}
