package org.nextframework.view.menu;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.nextframework.authorization.Authorization;
import org.nextframework.authorization.Role;
import org.nextframework.authorization.User;

public class MenuCacheResolver {

	private static final String MENU_CACHE_MAP = MenuTag.class.getName() + "_cache";

	public static void setMenu(HttpServletRequest request, Object menu, String menupath, User user, Locale locale) {
		MenuCache menuCache = new MenuCache(menu, user, locale, System.currentTimeMillis());
		cacheMap(request).put(menupath, menuCache);
	}

	public static Object getMenu(HttpServletRequest request, String menupath, User user, Locale locale) {
		MenuCache menuCache = cacheMap(request).get(menupath);
		if (menuCache != null && !resetMenu(menuCache, user, locale)) {
			return menuCache.getMenu();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private static Map<String, MenuCache> cacheMap(HttpServletRequest request) {
		Map<String, MenuCache> menuCacheMap = (Map<String, MenuCache>) request.getSession().getAttribute(MENU_CACHE_MAP);
		if (menuCacheMap == null) {
			menuCacheMap = new HashMap<String, MenuCache>();
			request.getSession().setAttribute(MENU_CACHE_MAP, menuCacheMap);
		}
		return menuCacheMap;
	}

	private static boolean resetMenu(MenuCache menuCache, User user, Locale locale) {
		if ((menuCache.getUser() == null && user != null) || (menuCache.getUser() != null && user == null) || (menuCache.getUser() != null && user != null && !menuCache.getUser().equals(user))) {
			return true;
		}
		if ((menuCache.getLocale() == null && locale != null) || (menuCache.getLocale() != null && locale == null) || (menuCache.getLocale() != null && locale != null && !menuCache.getLocale().equals(locale))) {
			return true;
		}
		if (Authorization.getAuthorizationDAO().getLastUpdateTime() > menuCache.getTime()) {
			Role[] roles = Authorization.getAuthorizationDAO().findUserRoles(user);
			for (Role role : roles) {
				if (Authorization.getAuthorizationDAO().getLastUpdateTime(role) > menuCache.getTime()) {
					return true;
				}
			}
		}
		return false;
	}

	private static class MenuCache {

		private Object menu;
		private User user;
		private Locale locale;
		private long time;

		public MenuCache(Object menu, User user, Locale locale, long time) {
			this.menu = menu;
			this.user = user;
			this.locale = locale;
			this.time = time;
		}

		public Object getMenu() {
			return menu;
		}

		public User getUser() {
			return user;
		}

		public Locale getLocale() {
			return locale;
		}

		public long getTime() {
			return time;
		}

	}

}