package org.nextframework.view.menu;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;

import org.nextframework.authorization.Authorization;
import org.nextframework.authorization.AuthorizationManager;
import org.nextframework.authorization.Role;
import org.nextframework.authorization.User;
import org.nextframework.util.Util;
import org.nextframework.web.WebUtils;
import org.xml.sax.SAXException;

public class MenuResolver {
	
	public static final String RESOLVER_CACHE_MAP = MenuResolver.class.getName()+"_cache";
	
	public static final String RESOLVER_CACHE_MAP_TIME = MenuResolver.class.getName()+"_cache_time";
	
	public static final String RESOLVER_CACHE_MAP_USER = MenuResolver.class.getName()+"_cache_user";
	
	@SuppressWarnings("all")
	public static Menu getMenu(HttpServletRequest request, String caminho, boolean reload ){
		
		//menu cache completo
		Map<String, Menu> menuCacheMap = (Map<String, Menu>) request.getSession().getAttribute(RESOLVER_CACHE_MAP);
		if(menuCacheMap != null){
			//check if the cache is valid
			Long time = (Long) request.getSession().getAttribute(RESOLVER_CACHE_MAP_TIME);
			User user = (User) request.getSession().getAttribute(RESOLVER_CACHE_MAP_USER);
			if(resetMenu(time, user)){
				menuCacheMap = null;
			}
		}
		if(menuCacheMap == null){
			menuCacheMap = new HashMap<String, Menu>();
			request.getSession().setAttribute(RESOLVER_CACHE_MAP, menuCacheMap);
			request.getSession().setAttribute(RESOLVER_CACHE_MAP_TIME, System.currentTimeMillis());
			request.getSession().setAttribute(RESOLVER_CACHE_MAP_USER, Authorization.getUserLocator().getUser());
		}
		
		//Verifica se a URL é reescrita para forçar releitura e adaptação do sufixo
		boolean urlSufix = !WebUtils.rewriteUrl("url").equals("url");
		
		//cada menu separadamente
		Menu menu = menuCacheMap.get(caminho);
		if (menu != null && !reload && !urlSufix ) {
			return menu;
		}
		
		try {
			
			menu = carregaMenu(request, caminho);
			menuCacheMap.put(caminho, menu);
			return menu;
			
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("Erro de parsing ao ler XML do menu. ", e);
		} catch (SAXException e) {
			throw new RuntimeException("Erro de SAX ao ler XML do menu. ", e);
		} catch (IOException e) {
			throw new RuntimeException("Erro de leitura (I/O) ao ler XML do menu. ", e);
		} catch (Exception e) {
			throw new RuntimeException("Erro ao gerar menu "+caminho, e);
		}
		
	}

	public static boolean resetMenu(Long lastUpdate, User user) {
		boolean resetMenu = false;
		if(lastUpdate != null){
			User currentUser = Authorization.getUserLocator().getUser();
			if(user == null && currentUser == null){
				return false;
			}
			if((user == null && currentUser != null) || !user.equals(currentUser)){
				return true;
			}
			if(Authorization.getAuthorizationDAO().getLastUpdateTime() > lastUpdate){
				Role[] roles = Authorization.getAuthorizationDAO().findUserRoles(currentUser);
				for (Role role : roles) {
					if(Authorization.getAuthorizationDAO().getLastUpdateTime(role) > lastUpdate){
						resetMenu = true;
						break;
					}
				}
			}
		}
		return resetMenu;
	}
	
	private static Menu carregaMenu(HttpServletRequest request, String caminho) throws ParserConfigurationException, SAXException, IOException {
		MenuParser menuParser = new MenuParser();
		InputStream resourceStream = request.getSession().getServletContext().getResourceAsStream(caminho);
		Menu menu = menuParser.parse(resourceStream);
		verificaMenu(request, menu);
		return menu;
	}

	private static void verificaMenu(HttpServletRequest request, Menu menu) {
		String app = request.getContextPath();
		AuthorizationManager authorizationManager = Authorization.getAuthorizationManager();
		User user = Authorization.getUserLocator().getUser();
		verificarAutorizacao(app, menu, authorizationManager, user);
		removeEmptyMenus(menu);
	}
	
	private static void verificarAutorizacao(String app, Menu menu, AuthorizationManager authorizationManager, User user) {
		
		for (Iterator<Menu> iter = menu.getSubmenus().iterator(); iter.hasNext();) {
			Menu submenu = iter.next();
			
			String url = submenu.getUrl();
			if (org.springframework.util.StringUtils.hasText(url)) {
				if(url.contains("?")){
					url = url.substring(0, url.indexOf('?'));
				}
				if(url.startsWith(app)){
					int contextPathLength = app.length();
					url = url.substring(contextPathLength);
				}
				if (!authorizationManager.isAuthorized(url, null,user)) {
					iter.remove();
					continue;
				}
			}
			verificarAutorizacao(app, submenu, authorizationManager, user);
		}
	}
	
	private static void removeEmptyMenus(Menu menu) {
		if(menu.getSubmenus().size() > 0){
			for (int i = 0; i < menu.getSubmenus().size(); i++) {
				Menu submenu = menu.getSubmenus().get(i);
				removeEmptyMenus(submenu);
				if(!menu.getSubmenus().contains(submenu)){
					i--;
				}
			}
			
		}
		
		//Remove separacoes seguidas
		Menu ultimo = null;
		for (Iterator<Menu> it = menu.getSubmenus().iterator() ; it.hasNext() ;) {
			Menu atual = it.next();
			boolean separacao = atual.getTitle().startsWith("---") ;
			if (separacao) {
				if (ultimo != null) {
					it.remove();
				}else{
					ultimo = atual;
				}
			}else{
				ultimo = null;
			}
		}
		if (ultimo != null) {
			menu.getSubmenus().remove(ultimo);
		}
		
		//Remove menu sem filhos e sem links
		if(menu.getSubmenus().size() == 0 && Util.strings.isEmpty(menu.getUrl()) && (menu.getTitle() != null && !menu.getTitle().matches("--(-)+") )){
			Menu parent2 = menu.getParent();
			if(parent2 != null){
				parent2.getSubmenus().remove(menu);	
			}
		}
	}
	
}