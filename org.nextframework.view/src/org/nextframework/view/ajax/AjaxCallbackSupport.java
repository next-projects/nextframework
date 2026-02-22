package org.nextframework.view.ajax;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.nextframework.authorization.Authorization;
import org.nextframework.core.standard.Next;
import org.nextframework.core.standard.RequestContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AjaxCallbackSupport implements AjaxCallbackController {

	private static final String CALLBACKS = AjaxCallbackSupport.class.getName();

	static ThreadLocal<Integer> tokens = new ThreadLocal<Integer>();

	public void doAjax(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String parameter = request.getParameter("serverId");
		if (parameter == null) {
			throw new RuntimeException("parameter serverId was not sent");
		}
		int serverId = Integer.parseInt(parameter);
		RequestContext requestContext = Next.getRequestContext();
		Object syncobj = Authorization.getUserLocator().getUser();
		if (syncobj == null) {
			syncobj = Next.getApplicationContext();
		}
		Callback ajaxCallback;
		synchronized (syncobj) {
			ajaxCallback = getCallbacks(requestContext).get(serverId);
		}
		if (ajaxCallback != null) {
			tokens.set(serverId);
			try {
				response.getWriter().print(ajaxCallback.doAjax());
				response.getWriter().flush();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	static void unregister() {
		unregisterAjax(tokens.get());
	}

	static void unregisterAjax(int index) {
		RequestContext requestContext = Next.getRequestContext();
		Object syncobj = Authorization.getUserLocator().getUser();
		if (syncobj == null) {
			syncobj = Next.getApplicationContext();
		}
		synchronized (syncobj) {
			getCallbacks(requestContext).set(index, null);
		}
	}

	static int registerSingletonAjax(Callback ajax) {
		RequestContext requestContext = Next.getRequestContext();
		Object syncobj = Authorization.getUserLocator().getUser();
		if (syncobj == null) {
			syncobj = Next.getApplicationContext();
		}
		synchronized (syncobj) {
			List<Callback> callbacks = getCallbacks(requestContext);
			for (int i = 0; i < callbacks.size(); i++) {
				if (callbacks.get(i) != null && callbacks.get(i).equals(ajax)) {
					//the objects must equals each other
					return i;
				}
			}
			for (int i = 0; i < callbacks.size(); i++) {
				if (callbacks.get(i) == null) {
					//achou uma posição vazia
					callbacks.set(i, ajax);
					return i;
				}
			}
			callbacks.add(ajax);
			return callbacks.size() - 1;
		}
	}

	static int registerAjax(Callback ajax) {
		RequestContext requestContext = Next.getRequestContext();
		Object syncobj = Authorization.getUserLocator().getUser();
		if (syncobj == null) {
			syncobj = Next.getApplicationContext();
		}
		synchronized (syncobj) {
			List<Callback> callbacks = getCallbacks(requestContext);
			for (int i = 0; i < callbacks.size(); i++) {
				if (callbacks.get(i) == null) {
					//achou uma posição vazia
					callbacks.set(i, ajax);
					return i;
				}
			}
			callbacks.add(ajax);
			return callbacks.size() - 1;
		}
	}

	@SuppressWarnings("unchecked")
	static List<Callback> getCallbacks(RequestContext requestContext) {
		List<Callback> callbacks = (List<Callback>) requestContext.getUserAttribute(CALLBACKS);
		if (callbacks == null) {
			callbacks = new ArrayList<Callback>();
			requestContext.setUserAttribute(CALLBACKS, callbacks);
		}
		return callbacks;
	}

}
