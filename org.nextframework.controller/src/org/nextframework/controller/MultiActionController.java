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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextframework.authorization.Authorization;
import org.nextframework.authorization.User;
import org.nextframework.context.DeprecatedLogger;
import org.nextframework.controller.json.JsonTranslator;
import org.nextframework.controller.mvt.ModelAndViewTranslator;
import org.nextframework.core.web.DefaultWebRequestContext;
import org.nextframework.core.web.NextWeb;
import org.nextframework.core.web.WebRequestContext;
import org.nextframework.exception.NextException;
import org.nextframework.service.ServiceFactory;
import org.nextframework.types.TypedCollectionImpl;
import org.nextframework.util.ReflectionCache;
import org.nextframework.util.ReflectionCacheFactory;
import org.nextframework.util.Util;
import org.nextframework.validation.ObjectAnnotationValidator;
import org.nextframework.validation.ValidatorRegistry;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContextException;
import org.springframework.core.GenericTypeResolver;
import org.springframework.validation.BindException;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.mvc.multiaction.InternalPathMethodNameResolver;
import org.springframework.web.servlet.mvc.multiaction.MethodNameResolver;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;
import org.springframework.web.servlet.mvc.multiaction.ParameterMethodNameResolver;
import org.springframework.web.servlet.mvc.multiaction.PropertiesMethodNameResolver;

/**
 * Copy of Spring's MultiActionController
 * 
 * Controller implementation that allows multiple request types to be handled by
 * the same class. Subclasses of this class can handle several different types
 * of request with methods of the form
 * 
 * <pre>
 * ModelAndView actionName(HttpServletRequest request, HttpServletResponse response);
 * </pre>
 * 
 * May take a third parameter HttpSession in which an existing session will be
 * required, or a third parameter of an arbitrary class that gets treated as
 * command (i.e. an instance of the class gets created, and request parameters
 * get bound to it)
 * 
 * <p>
 * These methods can throw any kind of exception, but should only let propagate
 * those that they consider fatal, or which their class or superclass is
 * prepared to catch by implementing an exception handler.
 * 
 * <p>
 * This model allows for rapid coding, but loses the advantage of compile-time
 * checking. It is similar to a Struts 1.1 DispatchAction, but more
 * sophisticated. Also supports delegation to another object.
 * 
 * <p>
 * An implementation of the MethodNameResolver interface defined in this package
 * should return a method name for a given request, based on any aspect of the
 * request, such as its URL or an "action" parameter. The actual strategy can be
 * configured via the "methodNameResolver" bean property, for each
 * MultiActionController.
 * 
 * <p>
 * The default MethodNameResolver is InternalPathMethodNameResolver; further
 * included strategies are PropertiesMethodNameResolver and
 * ParameterMethodNameResolver.
 * 
 * <p>
 * Subclasses can implement custom exception handler methods with names such as:
 * 
 * <pre>
 * ModelAndView anyMeaningfulName(HttpServletRequest request, HttpServletResponse response, ExceptionClass exception);
 * </pre>
 * 
 * The third parameter can be any subclass or Exception or RuntimeException.
 * 
 * <p>
 * There can also be an optional lastModified method for handlers, of signature:
 * 
 * <pre>
 * 
 *  
 *   long anyMeaningfulNameLastModified(HttpServletRequest request)
 *  
 * </pre>
 * 
 * If such a method is present, it will be invoked. Default return from
 * getLastModified is -1, meaning that the content must always be regenerated.
 * 
 * <p>
 * Note that method overloading isn't allowed.
 * 
 * <p>
 * See also description of workflow performed by superclasses <a
 * href="AbstractController.html#workflow">here</a>.
 * 
 * <p>
 * <b>Note:</b> For maximum data binding flexibility, consider direct usage of
 * a ServletRequestDataBinder in your controller method, instead of relying on a
 * declared command argument. This allows for full control over the entire
 * binder setup and usage, including the invocation of Validators and the
 * subsequent evaluation of binding/validation errors.
 * 
 * @author Rod Johnson, alterado por rogelgarcia
 * @author Juergen Hoeller
 * @author Colin Sampaleanu
 * @see MethodNameResolver
 * @see InternalPathMethodNameResolver
 * @see PropertiesMethodNameResolver
 * @see ParameterMethodNameResolver
 * @see org.springframework.web.servlet.mvc.LastModified#getLastModified
 * @see org.springframework.web.bind.ServletRequestDataBinder
 * 
 * @since 25/01/2006
 * @version 1.1
 */
public class MultiActionController extends AbstractController {

	/**
	 * Parametro que indica que é para limpar o filtro asntes de setar as propriedades, mesmo se já existir um command na sessão<BR><BR>
	 * Tem que passar o valor "true".
	 * Força a criação de um novo command
	 */
	public static final String CLEAR_FILTER = "clearFilter";

	public static final String SUPPRESS_ERRORS = "suppressErrors";

	private static final String SUPPRESS_VALIDATION = "suppressValidation";

	public static final String ACTION_PARAMETER = "ACTION";

	@Deprecated
	private static final String ACTION_PARAMETER_DEPRECATED = "ACAO";

	/** Suffix for last-modified methods */
	public static final String LAST_MODIFIED_METHOD_SUFFIX = "LastModified";

	/** Default command name used for binding command objects: "command" */
	public static final String DEFAULT_COMMAND_NAME = "command";

	/** Log category to use when no mapped handler is found for a request */
	public static final String PAGE_NOT_FOUND_LOG_CATEGORY = "org.springframework.web.servlet.PageNotFound";

	/** Additional logger to use when no mapped handler is found for a request */
	protected static final Log pageNotFoundLogger = LogFactory.getLog(PAGE_NOT_FOUND_LOG_CATEGORY);

	/**
	 * Helper object that knows how to return method names from incoming
	 * requests. Can be overridden via the methodNameResolver bean property
	 */
	private MethodNameResolverImpl methodNameResolver;

	/** List of Validators to apply to commands */
	private Validator[] validators;

	/** Object we'll invoke methods on. Defaults to this. */
	private Object delegate;

	/** Methods, keyed by name */
	private Map<String, Method> handlerMethodMap;

	/**
	 * LastModified methods, keyed by handler method name (without
	 * LAST_MODIFIED_SUFFIX)
	 */
	//private Map<String, Method> lastModifiedMethodMap;

	/** Methods, keyed by exception class */
	private Map<Class<Throwable>, Method> exceptionHandlerMap;

	List<BinderConfigurer> binderConfigurers;

	public static String getRequestAction(HttpServletRequest request) {
		String parameter = request.getParameter(MultiActionController.ACTION_PARAMETER);
		if (parameter == null) {
			//check deprecated parameter
			parameter = request.getParameter(MultiActionController.ACTION_PARAMETER_DEPRECATED);
			if (parameter != null) {
				DeprecatedLogger.warn(MultiActionController.ACTION_PARAMETER_DEPRECATED + " is deprecated. Use " + MultiActionController.ACTION_PARAMETER + ". Defined in constant MultiActionController.ACTION_PARAMETER");
			}
		}
		return parameter;
	}

	/**
	 * O Spring irá injetar todos os binderConfigurers para essa aplicação
	 * @param binderConfigurers
	 */
	public void setBinderConfigurers(List<BinderConfigurer> binderConfigurers) {
		this.binderConfigurers = binderConfigurers;
	}

	/**
	 * Constructor for MultiActionController that looks for handler methods in
	 * the present subclass.Caches methods for quick invocation later. This
	 * class's use of reflection will impose little overhead at runtime.
	 * 
	 * @throws ApplicationContextException
	 *             if the class doesn't contain any action handler methods (and
	 *             so could never handle any requests).
	 */
	public MultiActionController() throws ApplicationContextException {
		setDelegate(this);
	}

	/**
	 * Constructor for MultiActionController that looks for handler methods in
	 * delegate, rather than a subclass of this class. Caches methods.
	 * 
	 * @param delegate
	 *            handler class. This doesn't need to implement any particular
	 *            interface, as everything is done using reflection.
	 * @throws ApplicationContextException
	 *             if the class doesn't contain any handler methods
	 */
	public MultiActionController(Object delegate) throws ApplicationContextException {
		setDelegate(delegate);
	}

	/**
	 * Set the Validators for this controller. The Validator must support the
	 * specified command class.
	 */
	public final void setValidators(Validator[] validators) {
		this.validators = validators;
	}

	/**
	 * Return the Validators for this controller.
	 */
	public final Validator[] getValidators() {
		return validators;
	}

	/**
	 * Set the delegate used by this class. The default is <code>this</code>,
	 * assuming that handler methods have been added by a subclass. This method
	 * is rarely invoked once the class is configured.
	 * 
	 * @param delegate
	 *            class containing methods, which may be the present class, the
	 *            handler methods being in a subclass
	 * @throws ApplicationContextException
	 *             if there aren't any valid request handling methods in the
	 *             subclass.
	 */
	public final void setDelegate(Object delegate) throws ApplicationContextException {
//		if (delegate == null) {
//			// throw new IllegalArgumentException("delegate cannot be
//			// <code>null</code> in MultiActionController");
//			return;
//		}
//		this.delegate = delegate;
//		this.handlerMethodMap = new HashMap<String, Method>();
//		this.lastModifiedMethodMap = new HashMap<String, Method>();
//
//		// Look at all methods in the subclass, trying to find
//		// methods that are validators according to our criteria
//		ReflectionCache reflectionCache = ReflectionCacheFactory.getReflectionCache();
//		Method[] methods = reflectionCache.getMethods(delegate.getClass());
//		for (int i = 0; i < methods.length; i++) {
//			// We're looking for methods with given parameters.
//			if (methods[i].getReturnType().equals(ModelAndView.class) || methods[i].getReturnType().equals(void.class)) {
//				// We have a potential handler method, with the correct return
//				// type.
//				Class[] params = methods[i].getParameterTypes();
//
//				// Check that the number and types of methods is correct.
//				// We don't care about the declared exceptions.
//				if (params.length >= 1 && params[0].equals(WebRequestContext.class)) {
//					// We're in business.
//					if (logger.isDebugEnabled()) {
//						logger.debug("Found action method [" + methods[i] + "]");
//					}
//					this.handlerMethodMap.put(methods[i].getName(), methods[i]);
//
//					// Look for corresponding LastModified method.
//					try {
//						Method lastModifiedMethod = reflectionCache.getMethod(delegate.getClass(), methods[i].getName() + LAST_MODIFIED_METHOD_SUFFIX, new Class[] { WebRequestContext.class });
//						// put in cache, keyed by handler method name
//						this.lastModifiedMethodMap.put(methods[i].getName(), lastModifiedMethod);
//						if (logger.isDebugEnabled()) {
//							logger.debug("Found last modified method for action method [" + methods[i] + "]");
//						}
//					} catch (NoSuchMethodException ex) {
//						// No last modified method. That's ok.
//					}
//				}
//			}
//		}
//
//		// configurar o methodNameResolver com os métodos encontrados
//		if(this.getClass().getSimpleName().startsWith("Empresacliente")){
//			System.out.println(this.getClass());
//			Set<String> keySet = this.handlerMethodMap.keySet();
//			for (String string : keySet) {
//				System.out.println("   "+string + "  >  "+this.handlerMethodMap.get(string));
//			}
//			System.out.println("--------");
//		}
//		
//		methodNameResolver = new MethodNameResolverImpl(this);
//
//		// There must be SOME handler methods.
//		// WHAT IF SETTING DELEGATE LATER!?
//		if (this.handlerMethodMap.isEmpty()) {
//			throw new ApplicationContextException("No handler methods in class [" + getClass().getName() + "]");
//		}
//
//		// Now look for exception handlers.
//		this.exceptionHandlerMap = new HashMap<Class<Throwable>, Method>();
//		for (int i = 0; i < methods.length; i++) {
//			if (methods[i].getReturnType().equals(ModelAndView.class) && methods[i].getParameterTypes().length == 2) {
//				Class[] params = methods[i].getParameterTypes();
//				if (params[0].equals(WebRequestContext.class) && Throwable.class.isAssignableFrom(params[1])) {
//					// Have an exception handler
//					this.exceptionHandlerMap.put((Class<Throwable>) params[1], methods[i]);
//					if (logger.isDebugEnabled()) {
//						logger.debug("Found exception handler method [" + methods[i] + "]");
//					}
//				}
//			}
//		}
		if (delegate == null) {
			// throw new IllegalArgumentException("delegate cannot be
			// <code>null</code> in MultiActionController");
			return;
		}
		this.delegate = delegate;
		methodNameResolver = new MethodNameResolverImpl(this);
		this.exceptionHandlerMap = new HashMap<Class<Throwable>, Method>();
	}

	// ---------------------------------------------------------------------
	// Implementation of LastModified
	// ---------------------------------------------------------------------

	// ---------------------------------------------------------------------
	// Implementation of Controller
	// ---------------------------------------------------------------------

	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			WebRequestContext requestContext = NextWeb.getRequestContext(request, response);
			Method method = this.methodNameResolver.getHandlerMethod(request);
			request.setAttribute("firstAction", requestContext.getLastAction());
			ModelAndView result = invokeNamedMethod(method, requestContext, null);

			while (result != null && result.getViewName() != null && result.getViewName().startsWith("action:")) {
				String actionName = result.getViewName().substring("action:".length(), result.getViewName().length());

				method = this.methodNameResolver.getHandlerMethod(actionName);
				result = invokeNamedMethod(method, requestContext, null);
			}
			request.setAttribute("lastAction", requestContext.getLastAction());
			return result;
		} catch (NoSuchRequestHandlingMethodException ex) {
			return noSuchMethodHandler(request, response, ex);
		} catch (NoActionHandlerException e) {
			return noActionHandler(request, response, e);
		}
	}

	protected ModelAndView noActionHandler(HttpServletRequest request, HttpServletResponse response, NoActionHandlerException e) throws NoActionHandlerException {
		throw e;
	}

	protected ModelAndView noSuchMethodHandler(HttpServletRequest request, HttpServletResponse response, NoSuchRequestHandlingMethodException ex)
			throws IOException {
		String parameter = request.getParameter(ACTION_PARAMETER);
		pageNotFoundLogger.warn(ex.getMessage() + ", ACTION=" + parameter + ". " +
				"Checar se o método possui uma assinatura no seguinte padrão: public <nome do método>(WebRequestContext request, <Classe do Command> <nome do command>). " +
				"O método pode opcionalmente lançar exceções. A classe do command pode ser qualquer uma.");
		if (parameter == null) {
			pageNotFoundLogger.warn("Verifique se algum método do controller possui a anotação @DefaultAction");
		}
		response.sendError(HttpServletResponse.SC_NOT_FOUND, ex.getMessage() + ".\n Verifique o log para mais informações");
		return null;
	}

	/**
	 * Vai para uma determinada action. Será feito o bind dos parametros para o 
	 * command novamente. É possível utilizar o goToAction pra continuar em um outro action 
	 * onde a classe do command seje diferente. É o mesmo que utilizar um forward
	 * @param action
	 * @return
	 */
	protected ModelAndView goToAction(String action) {
		((DefaultWebRequestContext) NextWeb.getRequestContext()).setLastAction(action);
		return new ModelAndView("action:" + action);
	}

	/**
	 * Continua o processamento em outra action, utilizando o mesmo command. 
	 * @param action
	 * @param command
	 * @return
	 */
	protected ModelAndView continueOnAction(String action, Object command) {
		WebRequestContext request = NextWeb.getRequestContext();
		((DefaultWebRequestContext) request).setLastAction(action);

		HttpServletResponse servletResponse = request.getServletResponse();
		try {
			Method method = this.methodNameResolver.getHandlerMethod(action);

			ModelAndView result = invokeNamedMethod(method, NextWeb.getRequestContext(request.getServletRequest(), servletResponse), command);
			request.setAttribute("firstAction", request.getLastAction());
			while (result != null && result.getViewName() != null && result.getViewName().startsWith("action:")) {
				String actionName = result.getViewName().substring("action:".length(), result.getViewName().length());

				method = this.methodNameResolver.getHandlerMethod(actionName);
				result = invokeNamedMethod(method, request, command);
			}
			request.setAttribute("lastAction", request.getLastAction());
			return result;
		} catch (NoSuchRequestHandlingMethodException ex) {
			pageNotFoundLogger.warn(ex.getMessage());
			try {
				servletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Efetua sendRedirect para determinada action.
	 */
	protected ModelAndView sendRedirectToAction(String action) {
		WebRequestContext requestContext = NextWeb.getRequestContext();
		String requestQuery = requestContext.getRequestQuery();
		String query = "?" + ACTION_PARAMETER + "=" + action;
		return new ModelAndView("redirect:" + requestQuery + (action == null ? "" : query));
	}

	/**
	 * Efetua sendRedirect para determinada action.
	 */
	protected String redirectToAction(String action) {
		WebRequestContext requestContext = NextWeb.getRequestContext();
		String requestQuery = requestContext.getRequestQuery();
		String query = "?" + ACTION_PARAMETER + "=" + action;
		return "redirect:" + requestQuery + (action == null ? "" : query);
	}

	/**
	 * Efetua sendRedirect para determinada action.
	 */
	protected String redirectToActionWithParams(String action, String params) {
		WebRequestContext requestContext = NextWeb.getRequestContext();
		String requestQuery = requestContext.getRequestQuery();
		String query = "?" + ACTION_PARAMETER + "=" + action + "&" + params;
		return "redirect:" + requestQuery + (action == null ? "" : query);
	}

	/**
	 * Invokes the named method.
	 * <p>
	 * Uses a custom exception handler if possible; otherwise, throw an
	 * unchecked exception; wrap a checked exception or Throwable.
	 * @param useCommand 
	 */
	protected final ModelAndView invokeNamedMethod(Method method, WebRequestContext request, Object useCommand) throws Exception {
		//TODO TRATAMENTO DE LOOP ETERNO (REFERENCIA CIRCULAR)

		do {
			Input input = null;
			boolean fromErrors = false;
			try {
				List<Object> params = new ArrayList<Object>(2);
				boolean hasRequestParameter = method.getParameterTypes().length > 0 &&
						method.getParameterTypes()[0].isAssignableFrom(WebRequestContext.class) &&
						!method.getParameterTypes()[0].equals(Object.class);
				if (hasRequestParameter) {
					params.add(request);
				}

				if (useCommand == null) {
					input = getAnnotation(method, Input.class);//modificado em 22/10/2010, esse código ficava dentro do if.. e só funcionava caso existissem commands
					if ((hasRequestParameter && method.getParameterTypes().length == 2) || (!hasRequestParameter && method.getParameterTypes().length == 1)) {
						Class<?> commandClass = getCommandClass(method, hasRequestParameter ? 1 : 0);
						CommandInfo commandInfo = getCommandInfo(method);

						Object command;
						ServletRequestDataBinder binder;

						if (!fromErrors) {
							command = getCommandObject(request, commandClass, commandInfo);
							binder = bind(request, command, commandInfo.validate);
						} else {
							command = getCommandObject(request, commandClass, commandInfo);
							//se veio de erros nao fazer o bind novamente
							binder = new ServletRequestDataBinder(command, getCommandName(command));
						}

						params.add(command);

						if (binder.getBindingResult().hasErrors()) {
							String inputAction = null;

							if (input != null) {
								inputAction = input.value();
							} else {
								logger.warn("No @Input specified for method " + method.getDeclaringClass().getName() + "." + method.getName() + ". Bind errors.");
								new BindException(binder.getBindingResult()).printStackTrace();
								if (commandInfo.session) {
									//should reset the command
									command = instantiateNewSessionCommand(request, commandClass, getSessionCommandName(commandClass, commandInfo));
									inputAction = method.getName();
								}
							}
							if (inputAction != null) {
								((DefaultWebRequestContext) request).setLastAction(inputAction);
								Method handlerMethod = this.methodNameResolver.getHandlerMethod(inputAction);
								((DefaultWebRequestContext) request).setBindException(new BindException(binder.getBindingResult()));
								if (!handlerMethod.getName().equals(method.getName())) {
									//o input deve ter o mesmo command do método que declarou o input .. então deixaremos o método de input.. fazer o handling como o mesmo command
									((DefaultWebRequestContext) request).setBindException(new BindException(binder.getBindingResult()));
									method = handlerMethod;
								}
							} else {
								binder.close();
							}
						}
					}
				} else {
					params.add(useCommand);
				}
				Object result = method.invoke(this.delegate, params.toArray(new Object[params.size()]));
				return convertActionResultToModelAndView(method, result);
			} catch (NoSuchRequestHandlingMethodException e) {
				throw e;
			} catch (NextException e) {
				throw e;
			} catch (InvocationTargetException ex) {
				// the invoked method threw exception
				if (input == null) {
					OnErrors onErrors = getAnnotation(method, OnErrors.class);
					if (onErrors != null) {
						fromErrors = true;
						((DefaultWebRequestContext) request).setLastAction(onErrors.value());
						Method methodErrors = this.methodNameResolver.getHandlerMethod(onErrors.value());
						request.addError(ex.getTargetException());
						logger.error("Erro ao invocar método " + method.getName() + " da classe " + this.getClass().getName() + ". Redirecionando para onErrors: " + onErrors.value(), ex.getTargetException());
						method = methodErrors;
						continue;
					} else {
						// nao tem input e não tem onerrors.. deixar a exceção vazar para algum handler se for o caso
					}
				} else {
					//se tem input.. redirecionar para input
					boolean sameMethod = false;
					String inputName = input.value();

					Method handlerMethod = this.methodNameResolver.getHandlerMethod(inputName);
					sameMethod = handlerMethod.getName().equals(method.getName());

					//	se for o mesmo método.. deixar a excecao vazar (se mandar para o método denovo vai dar loop eterno porque a excecao vai ocorrer novamente
					if (!sameMethod) {
						// se nao for o mesmo método.. redirecionar
						// poderiamos mandar um flag já que o método a ser invocado tem o mesmo command .. nesse caso economizariamos o bind
						// mas vamos deixar fazer o bind novamente porque já pode ter ocorrido algum processamento que alterou os valores do command 
						method = handlerMethod;
						request.addError(ex.getTargetException());
						((DefaultWebRequestContext) request).setLastAction(inputName);
						logger.error("Erro ao invocar método " + method.getName() + " da classe " + this.getClass().getName() + ". Redirecionando para input: " + inputName, ex.getTargetException());
						continue;
					}
				}
				return handleException(request, ex.getTargetException());
			} catch (IllegalArgumentException ex) {
				throw new NextException("Não foi possível invocar o método. Se estiver utilizando o método continueToAction verifique se o método que pede o redirecionamento e o método de destino possuem a mesma classe de command", ex);
			} catch (Exception ex) {
				// The binding process threw an exception.
				return handleException(request, ex);
			}
		} while (true);
	}

	@SuppressWarnings("unchecked")
	public ModelAndView convertActionResultToModelAndView(Method method, Object result) {
		if (result == null) {
			return null;
		}
		if (result instanceof ModelAndView) {
			return (ModelAndView) result;
		}
		ModelAndViewTranslator<Object>[] translators = ServiceFactory.loadServices(ModelAndViewTranslator.class);
		for (ModelAndViewTranslator<Object> modelAndViewTranslator : translators) {
			if (translatorIsSuitable(modelAndViewTranslator, result)) {
				return modelAndViewTranslator.translateActionResultToModelAndView(result, method);
			}
		}
		throw new RuntimeException("No ModelAndViewTranslator found for " + result);
	}

	private boolean translatorIsSuitable(ModelAndViewTranslator<?> modelAndViewTranslator, Object result) {
		Class<?> type = GenericTypeResolver.resolveTypeArgument(modelAndViewTranslator.getClass(), ModelAndViewTranslator.class);
		return type.isAssignableFrom(result.getClass());
	}

	private CommandInfo getCommandInfo(Method method) {
		CommandInfo commandInfo = new CommandInfo();
		Command commandInfoAnnotation = getAnnotation(method, Command.class);
		if (commandInfoAnnotation == null) {
			return commandInfo;
		}
		commandInfo.name = commandInfoAnnotation.name();
		commandInfo.session = commandInfoAnnotation.session();
		commandInfo.validate = commandInfoAnnotation.validate();
		return commandInfo;
	}

	@SuppressWarnings("unused")
	private Method firstMethod(Class<?> ofClass, Method expectedMethod) {
		if (ofClass.equals(expectedMethod.getDeclaringClass())) {
			return expectedMethod;
		}
		Method[] methods = ofClass.getDeclaredMethods();
		for (Method method : methods) {
			if (method.getName().equals(expectedMethod.getName()) && Arrays.deepEquals(method.getParameterTypes(), expectedMethod.getParameterTypes())) {
				return method;
			}
		}
		return firstMethod(ofClass.getSuperclass(), expectedMethod);
	}

	private <A extends Annotation> A getAnnotation(Method method, Class<A> annotation) {
		ReflectionCache reflectionCache = ReflectionCacheFactory.getReflectionCache();
		A result = null;
		if (reflectionCache.isAnnotationPresent(method, annotation)) {
			result = method.getAnnotation(annotation);
		} else {
			Method superMethod = getSuperClassMethod(method);
			if (superMethod != null) {
				result = getAnnotation(superMethod, annotation);
			}
		}
		return result;
	}

	private Method getSuperClassMethod(Method method) {
		Class<?> superclass = method.getDeclaringClass().getSuperclass();
		Method superMethod = null;
		if (!MultiActionController.class.equals(superclass)) {
			ReflectionCache reflectionCache = ReflectionCacheFactory.getReflectionCache();
			Method[] methods = reflectionCache.getMethods(superclass);
			for (Method method2 : methods) {
				if (method2.getName().equals(method.getName())) {
					superMethod = method2;
					break;
				}
			}
		}
		return superMethod;
	}

	protected Class<?> getCommandClass(Method method, int commandIndex) {
		//TODO TENTAR DESCOBRIR O COMMAND MESMO QUANDO UTILIZAR GENERICS
		Class<?> commandClass = null;
		Method metodoOriginal = method;
		do {
			Type[] genericParameterTypes = method.getGenericParameterTypes();
			Type type = genericParameterTypes[commandIndex];
			if (type instanceof TypeVariable<?>) {
				TypeVariable<?> typeVariable = (TypeVariable<?>) type;
				String typeVariableName = typeVariable.getName();

				TypeVariable<?>[] typeParameters = this.getClass().getTypeParameters();
				if (typeParameters.length != 0) {
					throw new NextException("Implementar achar tipo de command por genericTypeParameters");
				}
				Type genericSuperclass = this.getClass().getGenericSuperclass();
				if (genericSuperclass instanceof ParameterizedType) {
					TypeVariable<?>[] typeParametersMethodClass = method.getDeclaringClass().getTypeParameters();
					int i = 0;
					for (TypeVariable<?> variable : typeParametersMethodClass) {
						if (variable.getName().equals(typeVariableName)) {
							commandClass = (Class<?>) ((ParameterizedType) genericSuperclass).getActualTypeArguments()[i];
						}
						i++;
					}
				}
				break;
			}
			if (type instanceof Object) {
				method = getSuperClassMethod(method);
			}
		} while (commandClass == null && method != null);
		if (commandClass == null) {
			commandClass = metodoOriginal.getParameterTypes()[metodoOriginal.getParameterTypes().length - 1];
		}
//		if(commandClass.equals(Object.class)){
//			logger.warn("Utilizando classe java.lang.Object como command");
//		}
		return commandClass;
	}

	/**
	 * Create a new command object of the given class.
	 * <p>
	 * This implementation uses <code>BeanUtils.instantiateClass</code>, so
	 * commands need to have public no-arg constructors. Subclasses can override
	 * this implementation if desired.
	 * 
	 * @throws Exception
	 *             if the command object could not be instantiated
	 * @see org.springframework.beans.BeanUtils#instantiateClass(Class)
	 */
	protected <E> E getCommandObject(WebRequestContext request, Class<E> clazz, CommandInfo commandInfo) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Must create new command of class [" + clazz.getName() + "]");
		}
		// Command commandAnnotation = getCommandAnnotation(annotations);

		// boolean session = false;
		// String name = "COMMAND_"+clazz.getSimpleName();
		boolean session = commandInfo.session;
		String name = getSessionCommandName(clazz, commandInfo);

		E command;
		if (session) {
			E sessionCommand = getSessionCommand(request, clazz, name);
			command = sessionCommand;
		} else {
			E command2 = getCommand(request, clazz, name);
			command = command2;
		}

		return command;
	}

	public <E> String getSessionCommandName(Class<E> clazz, CommandInfo commandInfo) {
		String name = commandInfo.name;
		if (Util.strings.isEmpty(name)) {
			name = getDefaultSessionCommandName(clazz);
		}
		return name;
	}

	public <E> String getDefaultSessionCommandName(Class<E> clazz) {
		return this.getClass().getSimpleName() + "CONTROLLER" + clazz.getName();
	}

	private <E> E getCommand(WebRequestContext request, Class<E> clazz, String name) throws Exception {
		E command = (E) BeanUtils.instantiateClass(clazz);
		onInstantiateNewCommand(command, null, false);
		return command;
	}

	@SuppressWarnings("unchecked")
	protected <E> E getSessionCommand(WebRequestContext request, Class<E> clazz, String name) {
		E sessionCommand = (E) request.getSession().getAttribute(name);
		if (sessionCommand == null
				|| "true".equalsIgnoreCase(request.getParameter(CLEAR_FILTER))
				|| "true".equals(request.getAttribute(CLEAR_FILTER))
				|| Boolean.TRUE.equals(request.getAttribute(CLEAR_FILTER))) {
			sessionCommand = instantiateNewSessionCommand(request, clazz, name);
		}
		return sessionCommand;
	}

	public <E> E instantiateNewSessionCommand(WebRequestContext request, Class<E> clazz, String name) {
		E sessionCommand;
		sessionCommand = (E) BeanUtils.instantiateClass(clazz);
		request.getSession().setAttribute(name, sessionCommand);
		onInstantiateNewCommand(sessionCommand, name, true);
		return sessionCommand;
	}

	protected void onInstantiateNewCommand(Object command, String name, boolean session) {
		CommandEventListener[] commandListeners = getCommandListeners();
		for (CommandEventListener commandListener : commandListeners) {
			commandListener.onInstantiateNewCommand(this, command, name, session);
		}
	}

	protected void onCreateBinderForCommand(ServletRequestDataBinder binder, Object command) {
		CommandEventListener[] commandListeners = getCommandListeners();
		for (CommandEventListener commandListener : commandListeners) {
			commandListener.onCreateBinderForCommand(this, command, binder);
		}
	}

	protected void onCommandBind(ServletRequestDataBinder binder, Object command) {
		CommandEventListener[] commandListeners = getCommandListeners();
		for (CommandEventListener commandListener : commandListeners) {
			commandListener.onCommandBind(this, command, binder);
		}
	}

	protected void onCommandValidation(ServletRequestDataBinder binder, Object command) {
		CommandEventListener[] commandListeners = getCommandListeners();
		for (CommandEventListener commandListener : commandListeners) {
			commandListener.onCommandValidation(this, command, binder);
		}
	}

	protected CommandEventListener[] getCommandListeners() {
		return ServiceFactory.loadServices(CommandEventListener.class);
	}

	/**
	 * Bind request parameters onto the given command bean
	 * 
	 * @param request
	 *            request from which parameters will be bound
	 * @param command
	 *            command object, that must be a JavaBean
	 * @throws Exception
	 *             in case of invalid state or arguments
	 */
	protected ServletRequestDataBinder bind(WebRequestContext request, Object command, boolean validate) throws Exception {
		logger.debug("Binding request parameters onto MultiActionController command");

		ServletRequestDataBinder binder = createBinder(request.getServletRequest(), command, getCommandName(command));
		onCreateBinderForCommand(binder, command);
		if (command.getClass().equals(Object.class)) {
			return binder;
		}
		binder.bind(request.getServletRequest());
		onCommandBind(binder, command);

		if (validate) {
			validate(request, command, binder);
		}
		String acao = request.getParameter(ACTION_PARAMETER);
		customValidation(request, command, new BindException(binder.getBindingResult()), acao);
		onCommandValidation(binder, command);

		return binder;
	}

	/**
	 * Método de validação que é chamado independentemente do @Command
	 * @param request
	 * @param command
	 * @param binder
	 * @param acao
	 */
	protected void customValidation(WebRequestContext request, Object command, BindException errors, String acao) {

	}

	protected void validate(WebRequestContext request, Object command, ServletRequestDataBinder binder) {

		if (!suppressValidation(request, command)) {
			BindException errors = new BindException(binder.getBindingResult());
			if (request.getAttribute(NextCommonsMultipartResolver.MAXUPLOADEXCEEDED) != null) {
				errors.reject("", "O tamanho máximo de upload de arquivos (10M) foi excedido");
			}
			ObjectAnnotationValidator objectAnnotationValidator = new ObjectAnnotationValidator(ServiceFactory.getService(ValidatorRegistry.class), request.getServletRequest());
			objectAnnotationValidator.validate(command, errors);
			String acao = request.getParameter(ACTION_PARAMETER);
			validate(command, errors, acao);
			if (this.validators != null) {
				for (int i = 0; i < this.validators.length; i++) {
					if (this.validators[i].supports(command.getClass())) {
						ValidationUtils.invokeValidator(this.validators[i], command, errors);
					}
				}
			}
		}
	}

	/**
	 * Sobrescreva esse método para implementar a validação
	 * @param command
	 * @param errors
	 */
	protected void validate(Object obj, BindException errors, String action) {

	}

	protected boolean suppressValidation(WebRequestContext request, Object command) {
		String suppress = request.getParameter(SUPPRESS_VALIDATION);
		if ("true".equalsIgnoreCase(suppress)) {
			return true;
		}
		return false;
	}

	/**
	 * Create a new binder instance for the given command and request.
	 * <p>
	 * Called by <code>bind</code>. Can be overridden to plug in custom
	 * ServletRequestDataBinder subclasses.
	 * <p>
	 * Default implementation creates a standard ServletRequestDataBinder, sets
	 * the specified MessageCodesResolver (if any), and invokes initBinder. Note
	 * that <code>initBinder</code> will not be invoked if you override this
	 * method!
	 * 
	 * @param request
	 *            current HTTP request
	 * @param command
	 *            the command to bind onto
	 * @return the new binder instance
	 * @throws Exception
	 *             in case of invalid state or arguments
	 * @see #bind
	 * @see #initBinder
	 */
	protected ServletRequestDataBinder createBinder(ServletRequest request, Object command, String commandDisplayName) throws Exception {
		ServletRequestDataBinder binder = new ServletRequestDataBinderNext(command, commandDisplayName);
		initBinder(request, binder);
		if (binderConfigurers != null) {
			for (BinderConfigurer binderConfigurer : binderConfigurers) {
				binderConfigurer.configureBinder(binder, request, command);
			}
		}
		return binder;
	}

	/**
	 * Return the command name to use for the given command object. Default is
	 * "command".
	 * 
	 * @param command
	 *            the command object
	 * @return the command name to use
	 * @see #DEFAULT_COMMAND_NAME
	 */
	protected String getCommandName(Object command) {
		return Util.beans.getDisplayName(NextWeb.getRequestContext().getMessageResolver(), command.getClass());
	}

	/**
	 * Initialize the given binder instance, for example with custom editors.
	 * Called by <code>createBinder</code>.
	 * <p>
	 * This method allows you to register custom editors for certain fields of
	 * your command class. For instance, you will be able to transform Date
	 * objects into a String pattern and back, in order to allow your JavaBeans
	 * to have Date properties and still be able to set and display them in an
	 * HTML interface.
	 * <p>
	 * Default implementation is empty.
	 * <p>
	 * Note: the command object is not directly passed to this method, but it's
	 * available via
	 * {@link org.springframework.validation.DataBinder#getTarget()}
	 * 
	 * @param request
	 *            current HTTP request
	 * @param binder
	 *            new binder instance
	 * @throws Exception
	 *             in case of invalid state or arguments
	 * @see #createBinder
	 * @see org.springframework.validation.DataBinder#registerCustomEditor
	 * @see org.springframework.beans.propertyeditors.CustomDateEditor
	 */
	protected void initBinder(ServletRequest request, ServletRequestDataBinder binder) throws Exception {
	}

	/**
	 * Determine the exception handler method for the given exception. Can
	 * return null if not found.
	 * 
	 * @return a handler for the given exception type, or <code>null</code>
	 * @param exception
	 *            the exception to handle
	 */
	protected Method getExceptionHandler(Throwable exception) {
		Class<?> exceptionClass = exception.getClass();
		if (logger.isDebugEnabled()) {
			logger.debug("Trying to find handler for exception class [" + exceptionClass.getName() + "]");
		}
		Method handler = (Method) this.exceptionHandlerMap.get(exceptionClass);
		while (handler == null && !exceptionClass.equals(Throwable.class)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to find handler for exception superclass [" + exceptionClass.getName() + "]");
			}
			exceptionClass = exceptionClass.getSuperclass();
			handler = (Method) this.exceptionHandlerMap.get(exceptionClass);
		}
		return handler;
	}

	/**
	 * We've encountered an exception which may be recoverable
	 * (InvocationTargetException or SessionRequiredException). Allow the
	 * subclass a chance to handle it.
	 * 
	 * @param request
	 *            current HTTP request
	 * @param response
	 *            current HTTP response
	 * @param ex
	 *            the exception that got thrown
	 * @return a ModelAndView to render the response
	 */
	private ModelAndView handleException(WebRequestContext request, Throwable ex) throws Exception {

		Method handler = getExceptionHandler(ex);
		if (handler != null) {
			return invokeExceptionHandler(handler, request, ex);
		}
		// If we get here, there was no custom handler
		if (ex instanceof Exception) {
			request.getServletResponse().addHeader("EX-MESSAGE", ex.getMessage());
			throw (Exception) ex;
		}
		if (ex instanceof Error) {
			request.getServletResponse().addHeader("EX-ERROR-MESSAGE", ex.getClass().getSimpleName() + ": " + ex.getMessage());
			throw (Error) ex;
		}
		// Should never happen!
		throw new ServletException("Unknown Throwable type encountered: " + ex);
	}

	/**
	 * Invoke the selected exception handler.
	 * 
	 * @param handler
	 *            handler method to invoke
	 */
	private ModelAndView invokeExceptionHandler(Method handler, WebRequestContext request, Throwable ex) throws Exception {

		if (handler == null) {
			throw new ServletException("No handler for exception", ex);
		}

		// If we get here, we have a handler.
		if (logger.isDebugEnabled()) {
			logger.debug("Invoking exception handler [" + handler + "] for exception [" + ex + "]");
		}
		try {
			Object result = handler.invoke(this.delegate, new Object[] { request, ex });
			ModelAndView mv = convertActionResultToModelAndView(handler, result);
			while (mv != null && mv.getViewName() != null && mv.getViewName().startsWith("action:")) {
				String actionName = mv.getViewName().substring("action:".length(), mv.getViewName().length());
				Method method = this.methodNameResolver.getHandlerMethod(actionName);
				mv = invokeNamedMethod(method, request, null);
			}
			return mv;
		} catch (InvocationTargetException ex2) {
			Throwable targetEx = ex2.getTargetException();
			if (targetEx instanceof Exception) {
				throw (Exception) targetEx;
			}
			if (targetEx instanceof Error) {
				throw (Error) targetEx;
			}
			// shouldn't happen
			throw new ServletException("Unknown Throwable type encountered", targetEx);
		}
	}

	public static class CommandInfo {
		protected String name = "";

		protected boolean validate = false;

		protected boolean session = false;

		@Override
		public String toString() {
			return "name: " + name + ", validate: " + validate + ", session: " + session;
		}
	}

	public Map<String, Method> getHandlerMethodMap() {
		return handlerMethodMap;
	}

	/* Métodos utilitários */

	/**
	 * Retorna o request atual
	 * @return
	 */
	public WebRequestContext getRequest() {
		return NextWeb.getRequestContext();
	}

	public User getUser() {
		return Authorization.getUserLocator().getUser();
	}

	public void setAttribute(String name, Object value) {
		getRequest().setAttribute(name, value);
	}

	/**
	 * Transforms a collection attribute in a TypedCollection.<BR>
	 * The TypedCollection is recognized by datagrids, so it is possible to set only one attribute to configure a datagrid.
	 * @param name
	 * @param value
	 * @param type
	 */
	@SuppressWarnings("all")
	public void setAttributeTyped(String name, Object value, Class type) {
		if (!(value instanceof Collection)) {
			throw new IllegalArgumentException("Only collections can be typed");
		}
		TypedCollectionImpl<?> typedCollection = new TypedCollectionImpl((Collection) value, type);
		getRequest().setAttribute(name, typedCollection);
	}

	public Object getAttribute(String name) {
		return getRequest().getAttribute(name);
	}

	public void setUserAttribute(String name, Object value) {
		getRequest().setUserAttribute(name, value);
	}

	public Object getUserAttribute(String name) {
		return getRequest().getUserAttribute(name);
	}

	public String getParameter(String name) {
		return getRequest().getParameter(name);
	}

	/**
	 * Transforms the object to JSON notation
	 * @param object
	 * @return
	 */
	public String toJson(Object object) {
		return ServiceFactory.getService(JsonTranslator.class).toJson(object);
	}
}
