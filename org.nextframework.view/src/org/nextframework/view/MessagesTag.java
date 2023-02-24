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
import java.util.Locale;
import java.util.Set;

import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.bean.PropertyDescriptor;
import org.nextframework.controller.MultiActionController;
import org.nextframework.controller.crud.CrudException;
import org.nextframework.core.standard.Message;
import org.nextframework.core.standard.MessageType;
import org.nextframework.core.standard.Next;
import org.nextframework.core.web.NextWeb;
import org.nextframework.core.web.WebRequestContext;
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

	private Boolean renderAsHtml;

	protected String containerClass;

	protected String bindBlockClass;

	protected String messageBlockClass;

	protected String titleClass;

	protected String globalErrorclass;

	protected String fieldNameClass;

	protected String bindErrorClass;

	protected String validationErrorClass;

	protected String debugClass;

	protected String traceClass;

	protected String infoClass;

	protected String warnClass;

	protected String errorClass;

	protected String eventClass;

	protected String toastClass;

	protected String exceptionClass;

	protected String exceptionCauseClass;

	private String title;
	private String invalidValueLabel;

	@Override
	protected void doComponent() throws Exception {

		WebRequestContext requestContext = NextWeb.getRequestContext();
		Locale locale = requestContext.getLocale();
		boolean renderAsHtml = Util.booleans.isTrue(this.renderAsHtml);

		String sc = containerClass != null ? " class='" + containerClass + "'" : "";
		getOut().println("<div id='messagesContainer'" + sc + ">");

		if (!renderAsHtml) {
			getOut().println("</div>"); //Fecha o container de uma vez, pois seu conteúdo será inserido via js.
			getOut().println("<script type='text/javascript'>next.events.onLoad(function(){");
			declareStyleClasses();
		}

		BindException errors = requestContext.getBindException();
		printBindException(errors, renderAsHtml, locale);

		Message[] messages = requestContext.getMessages();
		printBindException(messages, renderAsHtml, locale);
		printMessages(messages, renderAsHtml, locale);

		if (!renderAsHtml) {
			getOut().println("}, true);</script>");
			getOut().println("<script language='javascript'>function clearMessages(){" +
					"if(next.util.isDefined(document.getElementById('messagesContainer')))document.getElementById('messagesContainer').style.display = 'none';" +
					"}</script>");
		}

		requestContext.clearMessages();

	}

	private void printBindException(BindException errors, boolean renderAsHtml, Locale locale) throws IOException {

		boolean suppressErrors = "true".equalsIgnoreCase(getRequest().getParameter(MultiActionController.SUPPRESS_ERRORS));
		if (errors.hasErrors() && !suppressErrors) {

			title = getDefaultViewLabel("messagePanelTitle", "Valores incorretos encontrados em");
			invalidValueLabel = getDefaultViewLabel("invalidValueLabel", "Valor inválido");

			if (renderAsHtml) {
				String sc1 = bindBlockClass != null ? " class='" + bindBlockClass + "'" : "";
				getOut().println("<div id='bindBlock'" + sc1 + ">");
				String sc2 = titleClass != null ? " class='" + titleClass + "'" : "";
				getOut().println("<div id=\"bindTitle\"" + sc2 + ">" + title + " '" + errors.getObjectName() + "'</div>");
			} else {
				getOut().println(String.format("next.messages.setBindTitle(\"%s\");", title + " '" + errors.getObjectName() + "'"));
			}

			if (errors.getGlobalErrorCount() > 0) {

				if (renderAsHtml) {
					getOut().println("<ul>");
				}

				List<ObjectError> globalErrors = errors.getGlobalErrors();
				for (ObjectError objectError : globalErrors) {

					String msg = resolveMessage(objectError, locale);

					if (renderAsHtml) {
						String sc1 = globalErrorclass != null ? " class='" + globalErrorclass + "'" : "";
						getOut().println("<li" + sc1 + ">" + msg + "</li>");
					} else {
						getOut().println(String.format("next.messages.addBindMessage(\"%s\", 'globalError');", msg));
					}

				}

				if (renderAsHtml) {
					getOut().println("</ul>");
				}

			}

			List<ObjectError> allErrors = errors.getAllErrors();
			if (allErrors.size() > 0) {

				if (renderAsHtml) {
					getOut().println("<ul>");
				}

				for (ObjectError object : allErrors) {
					if (object instanceof FieldError) {

						FieldError fieldError = (FieldError) object;
						String field = Util.beans.getDisplayName(errors.getTarget().getClass(), fieldError.getField(), locale);
						String msg = resolveMessage(fieldError, locale);

						if (fieldError.isBindingFailure()) {
							if (renderAsHtml) {
								String sc1 = bindErrorClass != null ? " class='" + bindErrorClass + "'" : "";
								String sc2 = fieldNameClass != null ? " class='" + fieldNameClass + "'" : "";
								getOut().println("<li" + sc1 + "> <span" + sc2 + ">" + field + ": </span> " + invalidValueLabel + ": " + fieldError.getRejectedValue() + " -> " + msg + "</li>");
							} else {
								String sc2 = fieldNameClass != null ? " class='" + fieldNameClass + "'" : "";
								String msg2 = escapeText("<span" + sc2 + ">" + field + "</span> " + invalidValueLabel + ": " + fieldError.getRejectedValue() + " -> " + msg);
								getOut().println(String.format("next.messages.addBindMessage(\"%s\", 'bindError');", msg2));
							}
						} else {
							if (renderAsHtml) {
								String sc1 = validationErrorClass != null ? " class='" + validationErrorClass + "'" : "";
								String sc2 = fieldNameClass != null ? " class='" + fieldNameClass + "'" : "";
								getOut().println("<li" + sc1 + "> <span" + sc2 + ">" + field + "</span> " + msg + "</li>");
							} else {
								String sc2 = fieldNameClass != null ? " class='" + fieldNameClass + "'" : "";
								String msg2 = escapeText("<span" + sc2 + ">" + field + "</span> " + msg);
								getOut().println(String.format("next.messages.addBindMessage(\"%s\", 'validationError');", msg2));
							}
						}

					}
				}

				if (renderAsHtml) {
					getOut().println("</ul>");
				}

			}

			if (renderAsHtml) {
				getOut().println("</div>");
			}

		}

	}

	private void printBindException(Message[] messages, boolean renderAsHtml, Locale locale) throws IOException {
		if (messages.length > 0) {
			for (int i = 0; i < messages.length; i++) {
				Message message = messages[i];
				if (message.getSource() instanceof BindException) {
					printBindException((BindException) message.getSource(), renderAsHtml, locale);
					messages[i] = null;
				}
			}
		}
	}

	private void printMessages(Message[] messages, boolean renderAsHtml, Locale locale) throws IOException {

		if (messages.length > 0) {

			if (renderAsHtml) {
				String sc1 = messageBlockClass != null ? " class='" + messageBlockClass + "'" : "";
				getOut().println("<div id='messageBlock'" + sc1 + ">");
				getOut().println("<ul>");
			}

			for (Message message : messages) {
				if (message != null && message.getSource() != null) {
					String convertToMessage = convertToMessage(message.getSource(), locale);
					if (Util.strings.isNotEmpty(convertToMessage)) {
						convertToMessage = escapeText(convertToMessage);
						if (renderAsHtml) {
							String clazz = getMessageStyleClass(message);
							String sc1 = clazz != null ? " class='" + clazz + "'" : "";
							getOut().println("<li" + sc1 + ">" + convertToMessage + "</li>");
						} else {
							if (message.getType() == MessageType.TOAST) {
								getOut().println(String.format("next.messages.toast(\"%s\");", convertToMessage));
							} else {
								getOut().println(String.format("next.messages.addMessage(\"%s\", \"%s\");", convertToMessage, message.getType().name().toLowerCase()));
							}
						}
					}
				}
			}

			if (renderAsHtml) {
				getOut().println("</ul>");
				getOut().println("</div>");
			}

		}

	}

	private void declareStyleClasses() throws IOException {
		BeanDescriptor bd = BeanDescriptorFactory.forBean(this);
		for (PropertyDescriptor pd : bd.getPropertyDescriptors()) {
			if (pd.getName().endsWith("Class") && pd.getValue() != null) {
				getOut().println("next.messages.styleClasses['" + pd.getName() + "'] = '" + pd.getValue() + "';");
			}
		}
	}

	private String resolveMessage(MessageSourceResolvable msr, Locale locale) {
		try {
			return Next.getMessageSource().getMessage(msr, locale);
		} catch (NoSuchMessageException e) {
			return Util.exceptions.getExceptionDescription(e, locale);
		}
	}

	private String resolveException(Throwable exception, Locale locale) {
		try {
			return Util.exceptions.getExceptionDescription(exception, false, locale);
		} catch (NoSuchMessageException e) {
			return Util.exceptions.getExceptionDescription(e, locale);
		}
	}

	private String escapeText(String string) {
		return string.replace("\\", "\\\\").replace("\"", "\\\"").replace('\r', ' ').replace('\n', ' ');
	}

	protected String convertToMessage(Object source, Locale locale) {

		if (source instanceof Throwable) {

			StringBuilder builder = new StringBuilder();
			Throwable exception = (Throwable) source;
			if (exception instanceof CrudException) {
				exception = ((CrudException) exception).getCause();
			}

			String sc1 = exceptionClass != null ? " class='" + exceptionClass + "'" : "";
			String exceptionDesc = resolveException(exception, locale);
			builder.append("<span" + sc1 + ">" + exceptionDesc + "</span>");

			Set<Throwable> allCauses = new HashSet<Throwable>();
			allCauses.add(exception);
			Throwable cause = exception.getCause();
			while (cause != null && !allCauses.contains(cause)) {

				String sc2 = exceptionCauseClass != null ? " class='" + exceptionCauseClass + "'" : "";
				exceptionDesc = resolveException(cause, locale);
				builder.append("<ul><li" + sc2 + ">" + exceptionDesc + "</li></ul>");

				allCauses.add(cause);
				cause = cause.getCause();

			}

			return builder.toString();

		} else if (source instanceof MessageSourceResolvable) {

			String msg = resolveMessage((MessageSourceResolvable) source, locale);
			return msg;

		}

		return Util.strings.toStringDescription(source, locale);
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
			case EVENT:
				return eventClass;
			case TOAST:
				return toastClass;
			default:
				return message.getType().name().toLowerCase();
		}
	}

	public Boolean getRenderAsHtml() {
		return renderAsHtml;
	}

	public void setRenderAsHtml(Boolean renderAsHtml) {
		this.renderAsHtml = renderAsHtml;
	}

	public String getContainerClass() {
		return containerClass;
	}

	public void setContainerClass(String containerClass) {
		this.containerClass = containerClass;
	}

	public String getBindBlockClass() {
		return bindBlockClass;
	}

	public void setBindBlockClass(String bindBlockClass) {
		this.bindBlockClass = bindBlockClass;
	}

	public String getMessageBlockClass() {
		return messageBlockClass;
	}

	public void setMessageBlockClass(String messageBlockClass) {
		this.messageBlockClass = messageBlockClass;
	}

	public String getTitleClass() {
		return titleClass;
	}

	public void setTitleClass(String titleClass) {
		this.titleClass = titleClass;
	}

	public String getGlobalErrorclass() {
		return globalErrorclass;
	}

	public void setGlobalErrorclass(String globalErrorclass) {
		this.globalErrorclass = globalErrorclass;
	}

	public String getFieldNameClass() {
		return fieldNameClass;
	}

	public void setFieldNameClass(String fieldNameClass) {
		this.fieldNameClass = fieldNameClass;
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

	public String getEventClass() {
		return eventClass;
	}

	public void setEventClass(String eventClass) {
		this.eventClass = eventClass;
	}

	public String getToastClass() {
		return toastClass;
	}

	public void setToastClass(String toastClass) {
		this.toastClass = toastClass;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getInvalidValueLabel() {
		return invalidValueLabel;
	}

	public void setInvalidValueLabel(String invalidValueLabel) {
		this.invalidValueLabel = invalidValueLabel;
	}

}