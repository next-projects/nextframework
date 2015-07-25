package org.nextframework.authorization;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.nextframework.controller.Controller;
import org.nextframework.controller.DefaultAction;
import org.nextframework.controller.MultiActionController;
import org.nextframework.core.standard.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;

public class LoginController extends MultiActionController implements AuthenticationController {

	// The programmer must create a class that implements AuthorizationDAO in
	// order to use this controller
	@Autowired AuthorizationDAO authorizationDAO;

	@Override
	public String getPath() {
		Controller controllerAnnotation = this.getClass().getAnnotation(Controller.class);
		if (controllerAnnotation == null) {
			logger.error("The class " + this.getClass() + " does not have a @Controller annotation. Cannot determine path for login.");
			return null;
		}
		return controllerAnnotation.path()[0];
	}

	boolean loginChecked = false;

	/**
	 * Action que envia para a p�gina de login
	 */
	@DefaultAction
	public String doPage(UserForm user) {
		copyLoginPageIfNecessary();
		setAttribute("user", user);
		return "login";
	}

	void copyLoginPageIfNecessary() {
		if (!loginChecked) {
			loginChecked = true;
			// consider the next default behavior for page redirecting
			String module = getRequest().getRequestModule();
			String dir = "/WEB-INF/jsp" + module;
			String pageResource = dir + "/login.jsp";
			if (getServletContext().getResourceAsStream(pageResource) == null) {
				String realPath = getServletContext().getRealPath(pageResource);
				File dirRef = new File(getServletContext().getRealPath(dir));
				dirRef.mkdirs();
				try {
					FileCopyUtils.copy(LoginController.class.getResourceAsStream("login.jsp"), new FileOutputStream(realPath));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Efetua o login do usu�rio
	 */
	public String doLogin(UserForm user){
		String login = user.getUsername();
		//se foi passado o login na requisi��o, iremos verificar se o usu�rio existe e a senha est� correta
		if(login != null){
			//buscamos o usu�rio do banco pelo login
			User userByLogin = authorizationDAO.findUserByUsername(login);
			
			// se o usu�rio existe e a senha est� correta
			String passwordPersisted = userByLogin != null? userByLogin.getPassword() : null;
			String passwordProvided = user.getPassword();
			if(userByLogin != null && validPassword(passwordPersisted, passwordProvided)){
				//Setando o atributo de se��o USER fazemos o login do usu�rio no sistema.
				getRequest().setUserAttribute("USER", userByLogin);
				
				//Limpamos o cache de permiss�es o menu.
				//O menu ser� refeito levando em considera��o as permiss�es do usu�rio
				//TODO REBUILD MENU CACHE
				//getRequest().setUserAttribute(MenuTag.MENU_CACHE_MAP, null);
				return "redirect:"+afterLoginRedirectTo();
			}
			
			//Se o login e/ou a senha n�o estiverem corretos, avisar o usu�rio
			getRequest().addMessage("Login e/ou senha inv�lidos", MessageType.ERROR);
			getRequest().setAttribute("invalidLogin", true);
		}
		
		//limpar o campo senha, e enviar para a tela de login j� que o processo falhou
		user.setPassword(null);
		return doPage(user);
	}

	protected boolean validPassword(String passwordPersisted, String passwordProvided) {
		return passwordPersisted.equals(passwordProvided);
	}

	protected String afterLoginRedirectTo() {
		return "/";
	}

	public static class UserForm implements User {
		
		private static final long serialVersionUID = 1L;
		String username;
		String password;

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