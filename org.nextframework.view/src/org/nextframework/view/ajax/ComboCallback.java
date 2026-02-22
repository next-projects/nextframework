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
package org.nextframework.view.ajax;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.nextframework.authorization.Authorization;
import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.bean.PropertyDescriptor;
import org.nextframework.controller.BinderConfigurer;
import org.nextframework.controller.ServletRequestDataBinderNext;
import org.nextframework.core.standard.Next;
import org.nextframework.core.web.NextWeb;
import org.nextframework.persistence.DAO;
import org.nextframework.util.Util;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.context.support.WebApplicationContextUtils;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class ComboCallback implements AjaxCallbackController {

	public static boolean CHECK_REGISTER = true;

	public void doAjax(HttpServletRequest request, HttpServletResponse response) throws Exception {

		ComboFilter comboFilter = new ComboFilter();
		ServletRequestDataBinder binder = createBinder(request, comboFilter, "comboFilter");
		binder.bind(request);

		BindException errors = new BindException(binder.getBindingResult());
		if (errors.hasErrors()) {

			Locale locale = request.getLocale();

			List<?> allErrors = errors.getAllErrors();
			StringBuilder builder = new StringBuilder();
			for (Object object : allErrors) {
				String msg = Util.strings.toStringDescription(object, locale);
				builder.append(msg.replace((CharSequence) "'", "\\'"));
				builder.append("\\n");
			}

			response.getWriter().println("var lista = [];");
			response.getWriter().println("alert('" + builder.toString() + "');");

		} else {

			if (Util.strings.isEmpty(comboFilter.getLoadFunction())) {

				Class<?> type = comboFilter.getType();
				if (type == null) {
					throw new RuntimeException("tipo nulo");
				}

				String simpleName = type.getSimpleName();
				if (CHECK_REGISTER && !isRegistered(simpleName, "findBy", new Class[0])) {
					throw new RuntimeException("Não registrado para fazer a chamada. " + simpleName + ".findBy, tente pedir a página novamente!");
				}

				String daoName = Util.strings.uncaptalize(simpleName) + "DAO";
				DAO<?> service = (DAO<?>) WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext()).getBean(daoName);
				String[] label = Util.strings.isEmpty(comboFilter.getLabel()) ? new String[0] : new String[] { comboFilter.getLabel() };
				List<?> lista = service.findBy(comboFilter.getParentValue(), label);
				String listaAttr = convertToJavaScript(lista, comboFilter.getLabel());

				response.getWriter().println(listaAttr);

			} else {

				String loadFunction = comboFilter.getLoadFunction();
				if (loadFunction.matches("\\w*\\.\\w*(\\(.*?\\))?")) {

					String[] loadFunctionSplit = loadFunction.split("\\.");
					String beanName = loadFunctionSplit[0];
					String function = loadFunctionSplit[1];
					Class<?>[] paramClasses = comboFilter.getClasses();
					Object[] values = getValues(comboFilter, binder, paramClasses);
					if (function.contains("(")) {
						function = function.substring(0, function.indexOf('('));
					} else {
						paramClasses = new Class[] { comboFilter.getParentValue().getClass() };
						values = new Object[] { comboFilter.getParentValue() };
					}
					Object bean = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext()).getBean(beanName);

					if (bean == null) {
						throw new RuntimeException("nenhum bean encontrado com o nome " + beanName);
					}

					try {
						if (CHECK_REGISTER && !isRegistered(beanName, function, paramClasses)) {
							throw new RuntimeException("Não registrado para fazer a chamada. " + beanName + "." + function + ", tente pedir a página novamente!");
						}
						Object lista = Util.objects.findAndInvokeMethod(bean, function, values, paramClasses);
						//o método sempre deve retornar uma lista, mesmo que seja vazia
						if (!(lista instanceof List<?>)) {
							throw new RuntimeException("O retorno do método " + loadFunction + " não foi uma lista");
						}
						String listaAttr = convertToJavaScript((List<?>) lista, comboFilter.getLabel());
						response.getWriter().println(listaAttr);
					} catch (Exception e) {
						throw new RuntimeException("Erro ao executar " + function, e);
					}

				} else {
					throw new RuntimeException("Função inválida: " + loadFunction);
				}

			}

		}
	}

	private ServletRequestDataBinder createBinder(ServletRequest request, Object command, String commandDisplayName) throws Exception {
		ServletRequestDataBinder binder = new ServletRequestDataBinderNext(command, commandDisplayName);
		Map<String, BinderConfigurer> binderConfigurersMap = Next.getBeanFactory().getBeansOfType(BinderConfigurer.class);
		if (binderConfigurersMap != null) {
			for (BinderConfigurer binderConfigurer : binderConfigurersMap.values()) {
				binderConfigurer.configureBinder(binder, request, command);
			}
		}
		return binder;
	}

	private String convertToJavaScript(List<?> lista, String label) {
		StringBuilder javascript = new StringBuilder();
		javascript.append("var lista = [");
		if (lista != null) {
			for (Iterator<?> iter = lista.iterator(); iter.hasNext();) {
				Object element = iter.next();
				String description;
				if (Util.strings.isEmpty(label)) {
					description = Util.strings.toStringDescription(element);
				} else {
					PropertyDescriptor propertyDescriptor = BeanDescriptorFactory.forBean(element).getPropertyDescriptor(label);
					description = Util.strings.toStringDescription(propertyDescriptor.getValue());
				}
				description = escapeSingleQuotes(description);
				String id = Util.strings.toStringIdStyled(element);
				javascript.append("['" + id + "', '" + description + "']");
				if (iter.hasNext()) {
					javascript.append(",");
				}
			}
		}
		javascript.append("];");
		return javascript.toString();
	}

	private String escapeSingleQuotes(String message) {
		return message
				.replace((CharSequence) "'", "\\'")
				.replace((CharSequence) "\n", " ")
				.replace((CharSequence) "\r", " ");
	}

	private Object[] getValues(ComboFilter comboFilter, ServletRequestDataBinder binder, Class<?>[] classes) {
		if (classes.length == 0) {
			return new Object[0];
		}
		Object[] values = new Object[classes.length];
		String[] split = comboFilter.getParameter();
		for (int i = 0; i < split.length; i++) {
			Object value = split[i];
			if (ServletRequestDataBinderNext.isObjectValue(value)) {
				//Quando chega uma string "com.app.Bean[id=1],com.app.Bean[id=2]", quebra em um array
				if (List.class.isAssignableFrom(classes[i]) && value instanceof String && ((String) value).contains(",")) {
					value = ((String) value).split(",");
				}
				value = ServletRequestDataBinderNext.translateObjectValue("[?]", value, null);
			}
			if ("user".equals(value)) {
				value = Authorization.getUserLocator().getUser();
			}
			if (!classes[i].equals(Void.class)) {
				values[i] = binder.convertIfNecessary(value, classes[i]);
			} else {
				values[i] = value;
			}
		}
		return values;
	}

	public static boolean isRegistered(String object, String functionName, Class<?>[] paramClasses) {
		Set<CallbackFunctionRegistration> set = getCallbackRegistrationList();
		return set.contains(new CallbackFunctionRegistration(object, functionName, paramClasses));
	}

	public static void register(String object, String functionName, Class<?>[] paramClasses) {
		Set<CallbackFunctionRegistration> set = getCallbackRegistrationList();
		set.add(new CallbackFunctionRegistration(object, functionName, paramClasses));
	}

	private static Set<CallbackFunctionRegistration> getCallbackRegistrationList() {
		HttpSession session = NextWeb.getRequestContext().getSession();
		@SuppressWarnings("unchecked")
		Set<CallbackFunctionRegistration> attribute = (Set<CallbackFunctionRegistration>) session.getAttribute("CallbackFunctionRegistration");
		if (attribute == null) {
			attribute = new HashSet<CallbackFunctionRegistration>();
			session.setAttribute("CallbackFunctionRegistration", attribute);
		}
		return attribute;
	}

}

class CallbackFunctionRegistration implements Serializable {

	private static final long serialVersionUID = -2792971685517909148L;

	String object;
	String functionName;
	Class<?>[] classes;

	public CallbackFunctionRegistration(String object, String functionName, Class<?>[] classes) {
		super();
		this.object = object;
		this.functionName = functionName;
		this.classes = classes;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + Arrays.hashCode(classes);
		result = PRIME * result + ((functionName == null) ? 0 : functionName.hashCode());
		result = PRIME * result + ((object == null) ? 0 : object.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final CallbackFunctionRegistration other = (CallbackFunctionRegistration) obj;
		if (!Arrays.equals(classes, other.classes))
			return false;
		if (functionName == null) {
			if (other.functionName != null)
				return false;
		} else if (!functionName.equals(other.functionName))
			return false;
		if (object == null) {
			if (other.object != null)
				return false;
		} else if (!object.equals(other.object))
			return false;
		return true;
	}

	public Class<?>[] getClasses() {
		return classes;
	}

	public String getFunctionName() {
		return functionName;
	}

	public String getObject() {
		return object;
	}

}
