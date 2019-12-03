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
package org.nextframework.view;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.nextframework.controller.MultiActionController;
import org.nextframework.controller.crud.CrudException;
import org.nextframework.core.standard.Message;
import org.nextframework.core.standard.MessageType;
import org.nextframework.core.web.NextWeb;
import org.nextframework.core.web.WebRequestContext;
import org.nextframework.message.MessageResolver;
import org.nextframework.util.Util;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

/**
 * @author rogelgarcia
 * @since 02/02/2006
 * @version 1.1
 */
public class MessagesTag extends BaseTag {

	protected String containerClass = "messagesContainer";

	protected String globalErrorclass = "globalerror";

	protected String fieldName = "fieldname";

	protected String bindErrorClass = "binderror";

	protected String validationErrorClass = "validationerror";

	protected String debugClass = "debug";

	protected String traceClass = "trace";

	protected String infoClass = "info";

	protected String warnClass = "warn";

	protected String errorClass = "error";

	protected String exceptionClass = "exceptionitem";

	protected String exceptionCauseClass = "causeitem";

	private String title;
	private String invalidValueLabel;
	private String errorLabel;

	@SuppressWarnings("all")
	@Override
	protected void doComponent() throws Exception {

		WebRequestContext requestContext = NextWeb.getRequestContext();
		MessageResolver messageResolver = requestContext.getMessageResolver();

		getOut().println("<div id='messagesContainer' class='" + containerClass + "'></div><script type='text/javascript'>next.events.onLoad(function(){");

		BindException errors = requestContext.getBindException();
		if (errors.hasErrors() && !"true".equalsIgnoreCase(getRequest().getParameter(MultiActionController.SUPPRESS_ERRORS))) {

			title = getDefaultViewLabel("messagePanelTitle", "Valores incorretos encontrados em");
			getOut().println(String.format("next.messages.setBindTitle(\"%s\");", title + " '" + errors.getObjectName() + "'"));

			if (errors.getGlobalErrorCount() > 0) {
				List globalErrors = errors.getGlobalErrors();
				for (Object object : globalErrors) {
					renderGlobalError(messageResolver, (ObjectError) object);
				}
			}

			List allErrors = errors.getAllErrors();
			for (Object object : allErrors) {
				if (object instanceof FieldError) {
					renderError(messageResolver, errors, (FieldError) object);
				}
			}

		}

		Message[] messages = requestContext.getMessages();
		if (messages.length > 0) {
			for (Message message : messages) {
				if (message.getSource() != null) {
					renderMessage(messageResolver, message);
				}
			}
		}

		getOut().println("}, true);</script>");
		getOut().println("<script language='javascript'>function clearMessages(){" +
				"if(next.util.isDefined(document.getElementById('messagesContainer')))document.getElementById('messagesContainer').style.display = 'none';" +
				"}</script>");

		requestContext.clearMessages();
	}

	private void renderGlobalError(MessageResolver messageResolver, ObjectError object) throws IOException {
		String msg = resolveMessage(messageResolver, object);
		getOut().println(String.format("next.messages.addBindMessage(\"%s\", '%s');", msg, globalErrorclass));
	}

	private String resolveMessage(MessageResolver messageResolver, MessageSourceResolvable msr) {
		try {
			return messageResolver.message(msr);
		} catch (NoSuchMessageException e) {
			return Util.objects.getExceptionDescription(messageResolver, e, true, true);
		}
	}

	private void renderError(MessageResolver messageResolver, BindException errors, FieldError fieldError) throws IOException {
		String field = Util.beans.getDisplayName(messageResolver, errors.getTarget().getClass(), fieldError.getField());
		String msg = resolveMessage(messageResolver, fieldError);
		if (invalidValueLabel == null) {
			invalidValueLabel = getDefaultViewLabel("invalidValueLabel", "Valor inválido");
		}
		if (errorLabel == null) {
			errorLabel = getDefaultViewLabel("errorLabel", "Erro");
		}
		if (fieldError.isBindingFailure()) {
			String msg2 = Util.strings.escapeText("<span class=\"" + fieldName + "\">" + field + "</span> " + invalidValueLabel + ": " + fieldError.getRejectedValue() + " " + errorLabel + ": " + msg);
			getOut().println(String.format("next.messages.addBindMessage(\"%s\", '%s');", msg2, bindErrorClass));
		} else {
			String msg2 = Util.strings.escapeText("<span class=\"" + fieldName + "\">" + field + "</span> " + errorLabel + ": " + msg);
			getOut().println(String.format("next.messages.addBindMessage(\"%s\", '%s');", msg2, validationErrorClass));
		}
	}

	private void renderMessage(MessageResolver messageResolver, Message message) throws IOException {
		String convertToMessage = convertToMessage(messageResolver, message.getSource());
		if (Util.strings.isNotEmpty(convertToMessage)) {
			convertToMessage = Util.strings.escapeText(convertToMessage);
			if (MessageType.TOAST == message.getType()) {
				getOut().println(String.format("next.messages.toast(\"%s\");", convertToMessage));
			} else {
				String clazz = getMessageStyleClass(message);
				getOut().println(String.format("next.messages.addMessage(\"%s\", \"%s\");", convertToMessage, clazz));
			}
		}
	}

	protected String convertToMessage(MessageResolver messageResolver, Object source) {

		if (source instanceof MessageSourceResolvable) {

			String msg = resolveMessage(messageResolver, (MessageSourceResolvable) source);
			return msg;

		} else if (source instanceof Throwable) {

			StringBuilder builder = new StringBuilder();
			Throwable exception = (Throwable) source;
			if (exception instanceof CrudException) {
				exception = ((CrudException) exception).getCause();
			}

			String exceptionName = Util.objects.getExceptionDescription(messageResolver, exception, true, false);
			builder.append("<span class=\"" + exceptionClass + "\">" + exceptionName + "</span>");

			Set<Throwable> allCauses = new HashSet<Throwable>();
			allCauses.add(exception);
			Throwable cause = exception.getCause();
			while (cause != null && !allCauses.contains(cause)) {

				exceptionName = Util.objects.getExceptionDescription(messageResolver, cause, true, false);
				builder.append("<ul><li class=\"" + exceptionCauseClass + "\">" + exceptionName + "</li></ul>");

				allCauses.add(cause);
				cause = cause.getCause();
			}

			return builder.toString();

		}

		return Util.strings.toString(source);
	}

	private String getMessageStyleClass(Message message) {
		switch (message.getType()) {
			case DEBUG:
				return debugClass;
			case TRACE:
				return traceClass;
			case INFO:
				return infoClass;
			case WARN:
				return warnClass;
			case ERROR:
				return errorClass;
			default:
				return message.getType().name().toLowerCase();
		}
	}

	public String getContainerClass() {
		return containerClass;
	}

	public void setContainerClass(String containerClass) {
		this.containerClass = containerClass;
	}

	public String getGlobalErrorclass() {
		return globalErrorclass;
	}

	public void setGlobalErrorclass(String globalErrorclass) {
		this.globalErrorclass = globalErrorclass;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getBindErrorClass() {
		return bindErrorClass;
	}

	public void setBindErrorClass(String bindErrorClass) {
		this.bindErrorClass = bindErrorClass;
	}

	public String getValidationErrorClass() {
		return validationErrorClass;
	}

	public void setValidationErrorClass(String validationErrorClass) {
		this.validationErrorClass = validationErrorClass;
	}

	public String getDebugClass() {
		return debugClass;
	}

	public void setDebugClass(String debugClass) {
		this.debugClass = debugClass;
	}

	public String getTraceClass() {
		return traceClass;
	}

	public void setTraceClass(String traceClass) {
		this.traceClass = traceClass;
	}

	public String getInfoClass() {
		return infoClass;
	}

	public void setInfoClass(String infoClass) {
		this.infoClass = infoClass;
	}

	public String getWarnClass() {
		return warnClass;
	}

	public void setWarnClass(String warnClass) {
		this.warnClass = warnClass;
	}

	public String getErrorClass() {
		return errorClass;
	}

	public void setErrorClass(String errorClass) {
		this.errorClass = errorClass;
	}

	public String getExceptionClass() {
		return exceptionClass;
	}

	public void setExceptionClass(String exceptionClass) {
		this.exceptionClass = exceptionClass;
	}

	public String getExceptionCauseClass() {
		return exceptionCauseClass;
	}

	public void setExceptionCauseClass(String exceptionCauseClass) {
		this.exceptionCauseClass = exceptionCauseClass;
	}

}