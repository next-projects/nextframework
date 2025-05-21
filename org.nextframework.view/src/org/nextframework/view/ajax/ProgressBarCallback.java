package org.nextframework.view.ajax;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nextframework.authorization.Authorization;
import org.nextframework.core.standard.Next;
import org.nextframework.core.standard.RequestContext;
import org.nextframework.core.web.NextWeb;
import org.nextframework.core.web.WebRequestContext;
import org.nextframework.exception.BusinessException;
import org.nextframework.util.Util;
import org.nextframework.view.progress.ProgressMonitor;

public class ProgressBarCallback implements AjaxCallbackController {

	private static final String PROGRESS_BARS = "progressBars";

	public void doAjax(HttpServletRequest request, HttpServletResponse response) throws Exception {
		WebRequestContext requestContext = NextWeb.getRequestContext();
		Locale locale = requestContext.getLocale();
		PrintWriter out = response.getWriter();
		try {
			String serverId = request.getParameter("serverId");
			ProgressMonitor progressMonitor = getMonitors(Next.getRequestContext()).get(serverId);
			if (progressMonitor == null) {
				progressMonitor = new ProgressMonitor();
				progressMonitor.subTask(Util.objects.newMessage("org.nextframework.view.ajax.ProgressBarCallback.noSubTask", null, "..."));
				progressMonitor.setError(new BusinessException("org.nextframework.view.ajax.ProgressBarCallback.noSubTaskError", null, "Atualização de progresso não disponível!"));
			}
			String progressBarId = request.getParameter("progressbarid");
			int percentDone = progressMonitor.getPercentDone();
			String subtask = toString(progressMonitor.getSubtask(), locale);
			out.printf("var progressBar = ProgressBar.getById('%s');", progressBarId);
			if (progressMonitor.getError() == null) {
				boolean done = ProgressMonitor.DONE_SUCCESS.equals(progressMonitor.getDone());
				out.printf("progressBar.setInformation(%d, '%s', %b, %s);", percentDone, subtask, done, convertToJsArray(progressMonitor.getTasks(), locale));
			} else {
				List<Object> tasks = new ArrayList<Object>(progressMonitor.getTasks());
				String error = toString(progressMonitor.getError(), locale);
				tasks.add("<span class='progressInfoError'>" + Util.strings.escape(error) + "</span>");
				out.printf("progressBar.setError(%d, '%s', %b, %s);", percentDone, subtask, false, convertToJsArray(tasks, locale));
			}
			if (progressMonitor.getDone() == null) {
				out.println("progressBar.startSynchronization();");
			} else {
				getMonitors(Next.getRequestContext()).remove(serverId);
			}
		} catch (Exception e) {
			String errorStr = toString(e, locale);
			out.println("alert(\"" + errorStr + "\")");
		}
	}

	private String convertToJsArray(List<Object> tasks, Locale locale) {
		StringBuilder builder = new StringBuilder("[");
		for (int i = 0; i < tasks.size(); i++) {
			Object task = tasks.get(i);
			if (task == null) {
				task = "";
			}
			builder.append("\"");
			builder.append(toString(task, locale));
			builder.append("\"");
			if (i + 1 < tasks.size()) {
				builder.append(",");
			}
		}
		builder.append("]");
		return builder.toString();
	}

	public static String registerProgressMonitor(ProgressMonitor progressMonitor) {
		RequestContext requestContext = Next.getRequestContext();
		Object syncobj = Authorization.getUserLocator().getUser();
		if (syncobj == null) {
			syncobj = Next.getApplicationContext();
		}
		synchronized (syncobj) {
			Map<String, ProgressMonitor> monitorsMap = getMonitors(requestContext);
			String serverId = generateServerId();
			monitorsMap.put(serverId, progressMonitor);
			return serverId;
		}
	}

	private static String generateServerId() {
		long time = Calendar.getInstance().getTimeInMillis();
		int rnd = ThreadLocalRandom.current().nextInt(1, 999);
		return String.valueOf(time) + String.valueOf(rnd);
	}

	@SuppressWarnings("unchecked")
	private static Map<String, ProgressMonitor> getMonitors(RequestContext requestContext) {
		Map<String, ProgressMonitor> monitorsMap = (Map<String, ProgressMonitor>) requestContext.getUserAttribute(PROGRESS_BARS);
		if (monitorsMap == null) {
			monitorsMap = new HashMap<String, ProgressMonitor>();
			requestContext.setUserAttribute(PROGRESS_BARS, monitorsMap);
		}
		return monitorsMap;
	}

	private String toString(Object obj, Locale locale) {
		String objStr = Util.strings.toStringDescription(obj, locale);
		objStr = Util.strings.escapeQuotes(objStr.replace('\n', ' ').replace('\r', ' '));
		return objStr;
	}

}
