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
package org.nextframework.controller;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.nextframework.util.Util;

/**
 * @author rogelgarcia
 * @since 25/01/2006
 * @version 1.1
 */
public class MethodNameResolverImpl {
	
	private Class<?> clazz;
	//cache dos actions
	
	private Map<String/*ACTION*/, Method/*MÉTODO*/> mapAction = new HashMap<String, Method>();

	public MethodNameResolverImpl(Object delegate) {
		this.clazz = delegate.getClass();
	}
	
	public MethodNameResolverImpl(Class<? extends MultiActionController> delegateClass) {
		this.clazz = delegateClass;
	}

	public Method getHandlerMethod(HttpServletRequest request) throws ServletException, NoActionHandlerException {
		String actionParameter = MultiActionController.getRequestAction(request);
		return getHandlerMethod(actionParameter);
	}

	public Method getHandlerMethod(String action) throws NoActionHandlerException {
		Method method = null;
		if((method = mapAction.get(action)) != null){
			return method;
		}
		//se o método nao está no cache ... procurar
		Class<?>[] hierarquia = getHierarquia();
		
		if(Util.strings.isEmpty(action)){
			method = findDefaultAction(hierarquia);	
		}
		if(method == null){
			method = findAction(hierarquia, action);
		}
		if(method == null){
			method = findMethodName(hierarquia, action);
		}
		if(method != null){
			mapAction.put(action, method);
			return method;
		}
		if(Util.strings.isEmpty(action)){
			throw new NoActionHandlerException("No method with annotation @DefaultAction found in controller [" + this.clazz.getName() + "] that may receive requests.");
		} else {
			throw new NoActionHandlerException("Nenhum método com nome "+action+" ou anotado com @Action(\""+action+"\") encontrado no controller [" + this.clazz.getName() + "] que possa receber requisições.");
		}
	}
	
	private Method findMethodName(Class<?>[] hierarquia, String action) {
		for (Class<?> class1 : hierarquia) {
			Method[] methods = class1.getMethods();
			for (Method method : methods) {
				//if (method.getReturnType().equals(ModelAndView.class) || method.getReturnType().equals(void.class) || method.getReturnType().equals(String.class)) {
					if(method.getName().equals(action)){
						return method;						
					}
				//}
			}
		}
		return null;
	}
	
	private Method findAction(Class<?>[] hierarquia, String action) {
		for (Class<?> class1 : hierarquia) {
			Method[] methods = class1.getMethods();
			for (Method method : methods) {
				//any return type will be valid
//				if (method.getReturnType().equals(ModelAndView.class) || method.getReturnType().equals(void.class) || method.getReturnType().equals(String.class)) {
					Action annotation = method.getAnnotation(Action.class);
					if(annotation != null){
						String value = annotation.value();
						if(value.equals(action)){
							//TODO TESTAR MODIFICADO EM 31/08/2010
							//QUANDO ENCONTRAR O MÉTODO QUE TEM A ANOTACAO.. PROCURAR POR UM MÉTODO COM NOME IGUAL, PORÉM EM UMA CLASSE MAIS ESPECÍFICA
							//SE NAO ENCONTRAR UM MAIS ESPECÍFICO, CERTAMENTE ENCONTRARÁ O PRÓPRIO MÉTODO ENCONTRADO AQUI
							return findMethodName(hierarquia, method.getName());
						}
						
					}
//				}
			}
		}
		return null;
	}

	private Method findDefaultAction(Class<?>[] hierarquia) {
		Method result = null;
		for (Class<?> class1 : hierarquia) {
			Method[] methods = class1.getMethods();
			for (Method method : methods) {
				//any return type will be valid
//				if (method.getReturnType().equals(ModelAndView.class) || method.getReturnType().equals(void.class) || method.getReturnType().equals(String.class)) {
					if(method.getAnnotation(DefaultAction.class) != null){
						//Se já encontrou, mas o nome é diferente, não considera.
						//Assim, é possível mudar o defaultAction herdado.
						//É necessário subir a hierarquia para pegar o primeiro método de todos, cao sobrescrito.
						if (result != null && !result.getName().equals(method.getName())) {
							continue;
						}
						result = method;
					}
//				}
			}
		}
		return result;
	}

	private Class<?>[] getHierarquia() {
		List<Class<?>> hierarquia = new ArrayList<Class<?>>();
		Class<?> clazz = this.clazz;
		while(!MultiActionController.class.equals(clazz)){
			//hierarquia.add(0, clazz);//TODO TESTAR .. TROCADO EM 31/08/2010
			hierarquia.add(clazz);//colocar as classes mais específicas primeiro
			clazz = clazz.getSuperclass();
		}
		return hierarquia.toArray(new Class[hierarquia.size()]);
	}

	//public String getHandlerMethodName(HttpServletRequest request) throws NoSuchRequestHandlingMethodException {
	//	String actionParameter = request.getParameter(MultiActionController.ACTION_PARAMETER);
	//	return getHandlerMethodName(actionParameter);
	//}

//	public String getHandlerMethodName(String actionParameter) throws NoSuchRequestHandlingMethodException {
//		Collection<Method> methods = this.methods;
//		String methodName = getActionMethodName(actionParameter, methods);
//		if(methodName == null){
//			methodName = findMethodInAncessor(clazz.getSuperclass(), actionParameter);
//		}
//		if(methodName != null){
//			return methodName;
//		}
//		throw new NoSuchRequestHandlingMethodException("(ação "+actionParameter+")", this.clazz);
//	}

//	private String findMethodInAncessor(Class superclass, String actionParameter) {
//		if(MultiActionController.class.equals(superclass)){
//			return null;
//		}
//		ReflectionCache reflectionCache = ReflectionCacheFactory.getReflectionCache();
//		Method[] methods = reflectionCache.getMethods(superclass);
//		String name = getActionMethodName(actionParameter, Arrays.asList(methods));
//		if(name == null){
//			return findMethodInAncessor(superclass.getSuperclass(), actionParameter);
//		}
//		return name;
//	}

//	private String getActionMethodName(String actionParameter, Collection<Method> methods) {
//		String methodName = null;
//		if(Util.strings.isEmpty(actionParameter)){
//			//procurar o método com anotacao @DefaultAction
//			//TODO JOGAR EXECAO SE TIVER MAIS DE UM MÉTODO DEFAULT
//			//TODO JOGAR INICIALIZACAO PARA O CONSTRUTOR
//			for (Method method : methods) {
//				if(method.getAnnotation(DefaultAction.class)!=null){
//					methodName = method.getName();
//				}
//			}
//		} else {
//			//procurar métodos com @Action ou @Actions
//			for (Method method : methods) {
//				Action annotation = method.getAnnotation(Action.class);
//				if(annotation!=null && annotation.value().equals(actionParameter)){
//					methodName = method.getName();
//				}
//				Actions actions = method.getAnnotation(Actions.class);
//				if(actions != null){
//					for (Action action : actions.value()) {
//						if(action.value().equals(actionParameter)){
//							methodName = method.getName();
//						}
//					}
//				}
//			}
//			
//			//procurar métodos com nome igual ao parametro
//			for (Method method : methods) {
//				if(method.getName().equals(actionParameter)){
//					methodName = method.getName();
//				}
//			}
//		}
//		return methodName;
//	}



}
