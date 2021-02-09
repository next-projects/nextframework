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

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.controller.MultiActionController;
import org.nextframework.controller.crud.CrudException;
import org.nextframework.core.standard.Message;
import org.nextframework.core.standard.MessageType;
import org.nextframework.core.web.NextWeb;
import org.nextframework.core.web.WebRequestContext;
import org.nextframework.persistence.exception.ForeignKeyException;
import org.nextframework.util.Util;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
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

	protected Set<String> printedErrors = new HashSet<String>();

	protected String titleClass = "messagetitle";

	protected String itemClass = "messageitem";

	protected String exceptionClass = "exceptionitem";

	protected String exceptionCauseClass = "causeitem";

	protected String fieldName = "fieldname";

	protected String bindErrorClass = "binderror";

	protected String validationErrorClass = "validationerror";

	protected String globalErrorclass = "globalerror";

	protected String debugClass = "debug";

	protected String traceClass = "trace";

	protected String infoClass = "info";

	protected String warnClass = "warn";

	protected String errorClass = "error";

	@SuppressWarnings("all")
	@Override
	protected void doComponent() throws Exception {

		WebRequestContext requestContext = NextWeb.getRequestContext();
		Message[] messages = requestContext.getMessages();
		BindException errors = requestContext.getBindException();
		boolean renderAsHtml = Util.booleans.isTrue(this.renderAsHtml);

		if (!renderAsHtml) {
			getOut().println("<div id='messagesContainer' class='messagesContainer'></div><script type='text/javascript'>next.events.onLoad(function(){");
		}

		if (errors.hasErrors() && !"true".equalsIgnoreCase(getRequest().getParameter(MultiActionController.SUPPRESS_ERRORS))) {

			if (renderAsHtml) {
				getOut().println("<div class='bindblock' id='bindBlock'>");
				getOut().println("<span id=\"bindTitle\" class=\"" + titleClass + "\">Valores incorretos encontrados em '" + errors.getObjectName() + "'</span>");
			} else {
				getOut().println(String.format("next.messages.setBindTitle(\"%s\");", "Valores incorretos encontrados em '" + errors.getObjectName() + "'"));
			}

			if (errors.getGlobalErrorCount() > 0) {

				if (renderAsHtml) {
					getOut().println("<ul>");
				}

				List globalErrors = errors.getGlobalErrors();
				for (Object object : globalErrors) {
					if (renderAsHtml) {
						getOut().println("<li class=\"" + globalErrorclass + "\">" + ((ObjectError) object).getDefaultMessage() + "</li>");
					} else {
						getOut().println(String.format("next.messages.addBindMessage(\"%s\", '%s');", ((ObjectError) object).getDefaultMessage(), globalErrorclass));
					}
				}

				if (renderAsHtml) {
					getOut().println("</ul>");
				}

			}

			List allErrors = errors.getAllErrors();
			if (allErrors.size() > 0) {

				if (renderAsHtml) {
					getOut().println("<ul>");
				}

				for (Object object : allErrors) {
					if (object instanceof FieldError) {

						// TODO MELHORAR A MENSAGEM
						FieldError fieldError = (FieldError) object;
						BeanDescriptor beanDescriptor = BeanDescriptorFactory.forBean(errors.getTarget());
						String field = fieldError.getField();
						field = beanDescriptor.getPropertyDescriptor(field).getDisplayName();

						if (fieldError.isBindingFailure()) {

							if (renderAsHtml) {
								getOut().println("<li class=\"" + bindErrorClass + "\"> <span class=\"" + fieldName + "\" title=\"" + removeQuotes(fieldError.getDefaultMessage()) + "\">" + field + ": </span> Valor inválido: " + fieldError.getRejectedValue() + "</li>");
							} else {
								//TODO REMOVER PARENTESES
								getOut().println(String.format("next.messages.addBindMessage(\"%s\", '%s');",
										escapeText("<span class=\"" + fieldName + "\" title=\"" + removeQuotes(fieldError.getDefaultMessage()) + "\">" + field + ": </span> Valor inválido: " + fieldError.getRejectedValue()), bindErrorClass));
							}

						} else {

							if (renderAsHtml) {
								getOut().println("<li class=\"" + validationErrorClass + "\"> <span class=\"" + fieldName + "\">" + field + "</span> " + fieldError.getDefaultMessage() + "</li>");
							} else {
								getOut().println(String.format("next.messages.addBindMessage(\"%s\", '%s');",
										escapeText("<span class=\"" + fieldName + "\">" + field + "</span> " + fieldError.getDefaultMessage()), validationErrorClass));
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

		if (messages.length > 0) {

			if (renderAsHtml) {
				getOut().println("<div class='messageblock' id='messageBlock'>");
				getOut().println("<ul>");
			}

			for (Message message : messages) {

				String clazz = "";
				switch (message.getType()) {
					case DEBUG:
						clazz = debugClass;
						break;
					case TRACE:
						clazz = traceClass;
						break;
					case INFO:
						clazz = infoClass;
						break;
					case WARN:
						clazz = warnClass;
						break;
					case ERROR:
						clazz = errorClass;
						break;
					default:
						clazz = message.getType().name().toLowerCase();
				}

				if (message.getSource() != null) {
					String convertToMessage = convertToMessage(message.getSource());
					if (Util.strings.isNotEmpty(convertToMessage)) {
						convertToMessage = escapeText(convertToMessage);

						if (renderAsHtml) {
							getOut().println("<li class=\"" + clazz + "\">" + convertToMessage + "</li>");
						} else {
							if (MessageType.TOAST.name().equalsIgnoreCase(clazz)) {
								getOut().println(String.format("next.messages.toast(\"%s\");", convertToMessage));
							} else {
								getOut().println(String.format("next.messages.addMessage(\"%s\", \"%s\");", convertToMessage, clazz));
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

		if (!renderAsHtml) {
			getOut().println("}, true);</script>");
			getOut().println("<script language='javascript'>function clearMessages(){" +
					"if(next.util.isDefined(document.getElementById('messagesContainer')))document.getElementById('messagesContainer').style.display = 'none';" +
					"}</script>");
		}

		requestContext.clearMessages();

	}

	private String escapeText(String string) {
		return string.replace("\\", "\\\\").replace("\"", "\\\"").replace('\r', ' ').replace('\n', ' ');
	}

	private String removeQuotes(String str) {
		if (str != null) {
			return str.replace('"', ' ');
		}
		return null;
	}

	protected String convertToMessage(Object source) {
		if (source instanceof String) {
			return source.toString();
		} else if (source instanceof Throwable) {
			//TODO FAZER ARVORE DE EXCECOES

			Throwable exception = (Throwable) source;
			StringBuilder builder = new StringBuilder();
			//getResumedStack(exception, true);
			if (exception instanceof CrudException) {
				exception = ((CrudException) exception).getCause();
			}

			if (exception instanceof DataAccessException) {
				if (exception instanceof ForeignKeyException) {
					ForeignKeyException fkException = (ForeignKeyException) exception;
					builder.append("<span class=\"" + exceptionClass + "\">" + fkException.getOriginalMessage() + "</span>");
					Throwable mostSpecificCause = fkException.getMostSpecificCause();
					if (mostSpecificCause != null) {
						String message = mostSpecificCause.getMessage();
						printedErrors.add(message);
						builder.append("<ul><li class=\"" + exceptionCauseClass + "\">" + message + "</li></ul>");
					}
				} else if (exception instanceof DataIntegrityViolationException) {
					builder.append("<span class=\"" + exceptionClass + "\">Integridade de dados violada</span>");
					String message = exception.getMessage();
					printedErrors.add(message);
					builder.append("<ul><li><span class=\"" + exceptionCauseClass + "\">" + message + "</span></li></ul>");
				} else if (exception instanceof DataRetrievalFailureException) {
					builder.append("<span class=\"" + exceptionClass + "\">Erro ao ler dados</span>");
					String message = exception.getMessage();
					printedErrors.add(message);
					builder.append("<ul><li><span class=\"" + exceptionCauseClass + "\">" + message + "</span></li></ul>");
				} else if (exception instanceof ConcurrencyFailureException) {
					builder.append("<span class=\"" + exceptionClass + "\">Problema com uso concorrente de dados</span>");
					String message = exception.getMessage();
					printedErrors.add(message);
					//builder.append("<ul><li><span class=\""+exceptionCauseClass+"\">"+message+"</span></li></ul>");
				} else {
					String message = exception.getMessage();
					printedErrors.add(message);
					builder.append("<span class=\"" + exceptionClass + "\">" + message + "</span>");
				}
			} else if (exception.getClass().getName().startsWith("java.lang")) {
				String message = exception.getMessage();
				printedErrors.add(message);
				builder.append("<span class=\"" + exceptionClass + "\"> " + exception.getClass().getSimpleName() + ": " + message + "</span>");
			} else {
				String message = exception.getMessage();
				printedErrors.add(message);
				builder.append("<span class=\"" + exceptionClass + "\">" + message + "</span>");
			}

			Throwable cause = exception;
			boolean first = true;
			while ((cause = cause.getCause()) != null) {
				if (first) {
					//getResumedStack(cause, true);
					first = false;
				}
				if (cause instanceof DataAccessException) {
					if (cause instanceof DataIntegrityViolationException) {
						builder.append("<ul><li class=\"" + exceptionCauseClass + "\">Integridade de dados violada</li></ul>");
					}
					if (cause instanceof DataRetrievalFailureException) {
						builder.append("<ul><li class=\"" + exceptionCauseClass + "\">Erro ao ler dados</li></ul>");
					}
					if (cause instanceof ConcurrencyFailureException) {
						builder.append("<ul><li class=\"" + exceptionCauseClass + "\">Problema com uso concorrente de dados</li></ul>");
					}
				}

				if (cause instanceof SQLException) {
					SQLException exception2 = (SQLException) cause;
					if (exception2.getNextException() != null) {
						String message = cause.getMessage();
						String message2 = exception2.getNextException().getMessage();
						if (!printedErrors.contains(message)) {
							printedErrors.add(message);
							builder.append("<ul><li class=\"" + exceptionCauseClass + "\">" + message + "</li></ul>");
						}
						if (!printedErrors.contains(message2)) {
							printedErrors.add(message2);
							builder.append("<ul><li class=\"" + exceptionCauseClass + "\">" + message2 + "</li></ul>");
						}
					} else if (cause.getCause() == null) {
						String message = cause.getMessage();
						if (!printedErrors.contains(message)) {
							printedErrors.add(message);
							builder.append("<ul><li class=\"" + exceptionCauseClass + "\">" + message + "</li></ul>");
						}
					} else {
						String message = cause.getMessage();
						if (!printedErrors.contains(message)) {
							printedErrors.add(message);
							builder.append("<ul><li class=\"" + exceptionCauseClass + "\">" + message + "</li></ul>");
						}
					}
				} else if (cause.getClass().getName().startsWith("java.lang")) {
					String message = cause.getMessage();
					if (!printedErrors.contains(message) || message == null) {
						printedErrors.add(message);
						builder.append("<ul><li class=\"" + exceptionCauseClass + "\">" + cause.getClass().getSimpleName() + ": " + message + "</li></ul>");
						//printApplicationStack(builder, cause);
					}
				} else {
					String message = cause.getMessage();
					if (!printedErrors.contains(message)) {
						printedErrors.add(message);
						builder.append("<ul><li class=\"" + exceptionCauseClass + "\">" + message + "</li></ul>");
					}

				}
			}
			return builder.toString();
		}
		return source.toString();
	}

	/*
	private void printApplicationStack(StringBuilder builder, Throwable cause) {
		List<StackTraceElement> elementsToPrint = getResumedStack(cause, false);
		
		builder.append("<ul> ");
	
		for (StackTraceElement element : elementsToPrint) {
			builder.append("<ul><li class=\"" + exceptionCauseClass + "\">"+element+"</li></ul>");
		}
		builder.append("</ul>");
	}
	*/

	/*
	private List<StackTraceElement> getResumedStack(Throwable cause, boolean printResume) {
		List<StackTraceElement> elementsToPrint = new ArrayList<StackTraceElement>();
		StackTraceElement[] stackTrace = cause.getStackTrace();
		List<String> fromClasses = new ArrayList<String>();
		for (int i = stackTrace.length-1; i >= 0; i--) {
			StackTraceElement element = stackTrace[i];
			if(!( //tentar colocar o stackTrace somente da aplicação
					element.getClassName().startsWith("org.nextframework") ||
					element.getClassName().startsWith("org.apache") ||
					element.getClassName().startsWith("org.jboss") ||
					element.getClassName().startsWith("java") ||
					element.getClassName().startsWith("org.springframework") ||
					element.getClassName().startsWith("sun") ||
					element.getClassName().startsWith("org.hibernate") ||
					element.getClassName().startsWith("net.sf") 
				)){
				if (fromClasses.contains(element.getClassName())) {
					int indexOf = fromClasses.indexOf(element.getClassName());
					fromClasses.remove(indexOf);
					elementsToPrint.remove(indexOf);
				}
				elementsToPrint.add(element);
				fromClasses.add(element.getClassName());
			}
		}
	
		if (printResume) {
			log.error(cause.getMessage(), cause);
			StackTraceElement[] last = cause.getStackTrace();
			Throwable exception = cause;
			StackTraceElement[] toArray = elementsToPrint.toArray(new StackTraceElement[elementsToPrint.size()]);
			exception.setStackTrace(toArray);
			//log.error("\n", exception);
			//log.error("\n"+cause.getClass().getName()+": "+cause.getMessage());
	//			for (StackTraceElement element : elementsToPrint) {
	//				log.error("Stack Resumido:\t"+element);
	//			}
			exception.setStackTrace(last);
		}
		return elementsToPrint;
	}
	*/

	public Boolean getRenderAsHtml() {
		return renderAsHtml;
	}

	public String getBindErrorClass() {
		return bindErrorClass;
	}

	public String getDebugClass() {
		return debugClass;
	}

	public String getErrorClass() {
		return errorClass;
	}

	public String getGlobalErrorclass() {
		return globalErrorclass;
	}

	public String getInfoClass() {
		return infoClass;
	}

	public String getTraceClass() {
		return traceClass;
	}

	public String getValidationErrorClass() {
		return validationErrorClass;
	}

	public String getWarnClass() {
		return warnClass;
	}

	public void setRenderAsHtml(Boolean renderAsHtml) {
		this.renderAsHtml = renderAsHtml;
	}

	public void setBindErrorClass(String bindErrorClass) {
		this.bindErrorClass = bindErrorClass;
	}

	public void setDebugClass(String debugClass) {
		this.debugClass = debugClass;
	}

	public void setErrorClass(String errorClass) {
		this.errorClass = errorClass;
	}

	public void setGlobalErrorclass(String globalErrorclass) {
		this.globalErrorclass = globalErrorclass;
	}

	public void setInfoClass(String infoClass) {
		this.infoClass = infoClass;
	}

	public void setTraceClass(String traceClass) {
		this.traceClass = traceClass;
	}

	public void setValidationErrorClass(String validationErrorClass) {
		this.validationErrorClass = validationErrorClass;
	}

	public void setWarnClass(String warnClass) {
		this.warnClass = warnClass;
	}

}
