package org.nextframework.view.menu;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Locale;

import org.nextframework.authorization.Authorization;
import org.nextframework.authorization.User;
import org.nextframework.core.web.NextWeb;
import org.nextframework.util.Util;

public class MenuResolver {

	public static Menu carregaMenu(String caminho, User user, Locale locale) throws Exception {
		MenuParser menuParser = new MenuParser(locale);
		InputStream resourceStream = NextWeb.getRequestContext().getSession().getServletContext().getResourceAsStream(caminho);
		Menu menu = menuParser.parse(resourceStream);
		verificaMenu(user, menu);
		return menu;
	}

	private static void verificaMenu(User user, Menu menu) {
		String app = NextWeb.getRequestContext().getContextPath();
		verificarAutorizacao(app, menu, user);
		removeEmptyMenus(menu);
	}

	private static void verificarAutorizacao(String app, Menu menu, User user) {

		for (Iterator<Menu> iter = menu.getSubmenus().iterator(); iter.hasNext();) {
			Menu submenu = iter.next();

			String url = submenu.getUrl();
			if (org.springframework.util.StringUtils.hasText(url)) {
				if (url.contains("?")) {
					url = url.substring(0, url.indexOf('?'));
				}
				if (url.startsWith(app)) {
					int contextPathLength = app.length();
					url = url.substring(contextPathLength);
				}
				if (!Authorization.getAuthorizationManager().isAuthorized(url, null, user)) {
					iter.remove();
					continue;
				}
			}

			verificarAutorizacao(app, submenu, user);
		}

	}

	private static void removeEmptyMenus(Menu menu) {

		if (menu.getSubmenus().size() > 0) {
			for (int i = 0; i < menu.getSubmenus().size(); i++) {
				Menu submenu = menu.getSubmenus().get(i);
				removeEmptyMenus(submenu);
				if (!menu.getSubmenus().contains(submenu)) {
					i--;
				}
			}

		}

		//Remove separacoes seguidas
		Menu ultimo = null;
		for (Iterator<Menu> it = menu.getSubmenus().iterator(); it.hasNext();) {
			Menu atual = it.next();
			boolean separacao = atual.getTitle().startsWith("---");
			if (separacao) {
				if (ultimo != null) {
					it.remove();
				} else {
					ultimo = atual;
				}
			} else {
				ultimo = null;
			}
		}
		if (ultimo != null) {
			menu.getSubmenus().remove(ultimo);
		}

		//Remove menu sem filhos e sem links
		if (menu.getSubmenus().size() == 0 && Util.strings.isEmpty(menu.getUrl()) && (menu.getTitle() != null && !menu.getTitle().matches("--(-)+"))) {
			Menu parent2 = menu.getParent();
			if (parent2 != null) {
				parent2.getSubmenus().remove(menu);
			}
		}

	}

}