package org.nextframework.authorization;

import java.net.MalformedURLException;

import org.nextframework.controller.Controller;
import org.nextframework.controller.DefaultAction;
import org.nextframework.controller.MultiActionController;
import org.nextframework.controller.NextCompositeViewResolver;
import org.nextframework.core.standard.MessageType;
import org.nextframework.service.ServiceFactory;
import org.nextframework.util.Util;
import org.nextframework.web.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

public class LoginController extends MultiActionController implements AuthenticationController {

	static final String DEFAULT_LOGIN_PAGE = "/WEB-INF/nextframework/authorization/login.jsp";
	static final String EXPLODED_DEFAULT_LOGIN_PAGE = "/WEB-INF/classes/org/nextframework/authorization/login.jsp";

	// The programmer must create a class that implements AuthorizationDAO in order to use this controller
	@Autowired
	private AuthorizationDAO authorizationDAO;

	@Override
	public String getPath() {
		Controller controllerAnnotation = this.getClass().getAnnotation(Controller.class);
		if (controllerAnnotation == null) {
			logger.error("The class " + this.getClass() + " does not have a @Controller annotation. Cannot determine path for login.");
			return null;
		}
		return controllerAnnotation.path()[0];
	}

	/**
	 * Action que envia para a página de login
	 */
	@DefaultAction
	public String doPage(UserForm user) {
		copyLoginPageIfNecessary();
		setAttribute("user", user);
		return resolveLoginView(getServletContext(), WebUtils.getRequestModule());
	}

	/**
	 * Retained as an extension hook for applications that overrode the old copy
	 * operation. The default page is now exposed directly from the module JAR.
	 */
	@Deprecated
	protected void copyLoginPageIfNecessary() {
	}

	protected String resolveLoginView(ServletContext servletContext, String module) {
		String applicationLoginPage = "/WEB-INF/jsp/" + module + "/login.jsp";
		try {
			if (servletContext.getResource(applicationLoginPage) != null) {
				return "login";
			}
			String defaultLoginPage = DEFAULT_LOGIN_PAGE;
			if (servletContext.getResource(defaultLoginPage) == null) {
				defaultLoginPage = EXPLODED_DEFAULT_LOGIN_PAGE;
				if (servletContext.getResource(defaultLoginPage) == null) {
					throw new IllegalStateException("Default login page is not available at " + DEFAULT_LOGIN_PAGE
							+ " or " + EXPLODED_DEFAULT_LOGIN_PAGE);
				}
			}
			return NextCompositeViewResolver.WEB_RESOURCE_URL_PREFIX + defaultLoginPage;
		} catch (MalformedURLException e) {
			throw new IllegalStateException("Could not resolve the login page", e);
		}
	}

	/**
	 * Efetua o login do usuário
	 */
	public String doLogin(UserForm user) {

		String login = user.getUsername();

		//se foi passado o login na requisição, iremos verificar se o usuário existe e a senha está correta
		if (login != null) {

			//buscamos o usuário do banco pelo login
			User userByLogin = authorizationDAO.findUserByUsername(login);

			// se o usuário existe e a senha está correta
			String passwordPersisted = userByLogin != null ? userByLogin.getPassword() : null;
			String passwordProvided = user.getPassword();

			if (userByLogin != null && validPassword(passwordPersisted, passwordProvided)) {

				HttpServletRequest servletRequest = getRequest().getServletRequest();
				servletRequest.getSession();
				servletRequest.changeSessionId();

				//Setando o atributo de seção USER fazemos o login do usuário no sistema.
				getRequest().setUserAttribute("USER", userByLogin);

				//Limpamos o cache de permissões o menu.
				//O menu será refeito levando em consideração as permissões do usuário
				//TODO REBUILD MENU CACHE
				//getRequest().setUserAttribute(MenuTag.MENU_CACHE_MAP, null);
				return "redirect:" + afterLoginRedirectTo();
			}

			//Se o login e/ou a senha não estiverem corretos, avisar o usuário
			getRequest().setAttribute("invalidLogin", true);
			getRequest().addMessage(Util.objects.newMessage("next.login.invalidLogin", null, "Login e/ou senha inválidos!"), MessageType.ERROR);
		}

		//limpar o campo senha, e enviar para a tela de login já que o processo falhou
		user.setPassword(null);

		return doPage(user);
	}

	protected boolean validPassword(String passwordPersisted, String passwordProvided) {
		if (passwordPersisted == null || passwordProvided == null) {
			return false;
		}
		try {
			PasswordEncoder passwordEncoder = getPasswordEncoder();
			if (passwordEncoder == null) {
				logger.error("No PasswordEncoder service is available. Authentication was denied.");
				return false;
			}
			return passwordEncoder.matches(passwordProvided, passwordPersisted);
		} catch (RuntimeException e) {
			logger.error("Password verification failed. Authentication was denied.", e);
			return false;
		}
	}

	protected PasswordEncoder getPasswordEncoder() {
		return ServiceFactory.getService(PasswordEncoder.class);
	}

	protected String afterLoginRedirectTo() {
		return "/";
	}

	public static class UserForm implements User {

		private static final long serialVersionUID = 1L;

		private String username;
		private String password;

		public String getUsername() {
			return username;
		}

		public String getPassword() {
			return password;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public void setPassword(String password) {
			this.password = password;
		}

	}

}
