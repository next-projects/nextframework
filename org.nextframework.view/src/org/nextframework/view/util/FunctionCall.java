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
package org.nextframework.view.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nextframework.authorization.Authorization;
import org.nextframework.authorization.User;
import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.PropertyDescriptor;
import org.nextframework.view.ComboReloadGroupTag;

public class FunctionCall {

	String object;
	String functionName;
	String parameters;
	String call;
	private DefaultFunctionCallInfo callInfo;

	public String getCall() {
		return call;
	}

	public FunctionCall(String call, BeanDescriptor beanDescriptor) {
		this.call = call.trim();
		Pattern pattern1 = Pattern.compile("(\\w*)\\s*?\\.(\\w*)\\s*?((\\(.*?\\)))");
		Matcher matcher1 = pattern1.matcher(call);
		if (matcher1.find()) {
			//System.out.println("found "+call);
			//int groupCount = matcher1.groupCount();
			//for (int i = 0; i < groupCount; i++) {
			//	System.out.println(i+ " = "+matcher1.group(i));
			//}
			this.object = matcher1.group(1);
			this.functionName = matcher1.group(2);
			this.parameters = matcher1.group(3);
		} else {
			Pattern pattern2 = Pattern.compile("(\\w*)\\s*?\\.?(\\w*)\\s*?");
			Matcher matcher2 = pattern2.matcher(call);
			if (matcher2.find()) {
				//System.out.println("found "+call);
				//int groupCount = matcher2.groupCount();
				//for (int i = 0; i < groupCount; i++) {
				//	System.out.println(i+ " = "+matcher2.group(i));
				//}
				this.object = matcher2.group(1);
				this.functionName = matcher2.group(2);
				this.parameters = "";
			} else {
				throw new RuntimeException("Função fora do padrão: " + call);
			}
		}
		this.callInfo = montarCallInfo(this, beanDescriptor);
		//System.out.println(Arrays.deepToString(getParameterArray()));
	}

	public DefaultFunctionCallInfo getCallInfo() {
		return callInfo;
	}

	private DefaultFunctionCallInfo montarCallInfo(FunctionCall call, BeanDescriptor beanDescriptor) {
		if (call.getCall().equals("all")) {
			return null;
		}
		DefaultFunctionCallInfo callInfo = new DefaultFunctionCallInfo();
		FunctionParameter[] parameterArray = call.getParameterArray();
		for (FunctionParameter param : parameterArray) {
			Class<?> clazz;
			Object value;
			switch (param.getParameterType()) {
				case REFERENCE:
					PropertyDescriptor propertyDescriptor = beanDescriptor.getPropertyDescriptor(param.getParameterValue());
					clazz = propertyDescriptor.getRawType();
					value = propertyDescriptor.getValue();
					break;
				case STRING:
					clazz = String.class;
					value = param.getParameterValue();
					break;
				case BOOLEAN:
					clazz = Boolean.class;
					value = new Boolean(param.getParameterValue());
					break;
				case USER:
					clazz = User.class;
					value = Authorization.getUserLocator().getUser();
					break;
				default:
					throw new RuntimeException("Tipo de parametro não suportado: " + param.getParameterType() + "   " + param.getParameterValue());
			}
			callInfo.addParam(param.getParameterValue(), value, clazz);
		}
		return callInfo;
	}

	public FunctionParameter[] getParameterArray() {
		List<FunctionParameter> parameters = new ArrayList<FunctionParameter>();
		if (this.parameters == null || this.parameters.trim().length() == 0) {
			return new FunctionParameter[0];
		}
		char[] parameterrCharArray = this.parameters.toCharArray();
		int step = 1;
		StringBuilder currentParam = null;
		boolean aspasDuplas = false;
		ParameterType currentType = null;
		for (int i = 0; i < parameterrCharArray.length; i++) {
			char currentChar = parameterrCharArray[i];
			try {
				if (String.valueOf(currentChar).equals(ComboReloadGroupTag.PARAMETER_SEPARATOR)) {
					throw new CharacterInvalidException();
				}
				switch (step) {
					case 1:
						if (currentChar == '(') {
							step = 2;
							continue;
						} else if (currentChar == ' ') {
							continue;
						} else {
							throw new CharacterInvalidException();
						}
					case 2:
						if (currentParam != null && currentParam.length() > 0) {
							parameters.add(new FunctionParameter(currentParam.toString().trim(), currentType));
						}
						currentParam = new StringBuilder();
						if (currentChar == ' ') {
							continue;
						} else if (currentChar == '\'') {
							aspasDuplas = false;
							step = 3;
							continue;
						} else if (currentChar == '"') {
							aspasDuplas = true;
							step = 3;
							continue;
						} else if (currentChar == 't') {
							step = 11;
						} else if (currentChar == 'f') {
							step = 12;
						} else if (currentChar == 'u') {
							step = 13;
						} else if (currentChar == ')') {
							if (currentParam.toString().trim().length() > 0) {
								parameters.add(new FunctionParameter(currentParam.toString().trim(), currentType));
							}
							step = 6;
							continue;
						} else if (String.valueOf(currentChar).matches("[0-9]")) {
							step = 8;
						} else if (String.valueOf(currentChar).matches("[A-Za-z]")) {
							step = 7;
						} else {
							throw new CharacterInvalidException();
						}
						break;
					case 3:
						currentType = ParameterType.STRING;
						if (currentChar == '"') {
							if (aspasDuplas) {
								step = 5;
								continue;
							}
						} else if (currentChar == '\'') {
							if (!aspasDuplas) {
								step = 5;
								continue;
							}
						} else if (currentChar == '\\') {
							step = 4;
							continue;
						}
						break;
					case 4:
						if (currentChar == '\'' || currentChar == '\"' || currentChar == '\\') {
							step = 3;
						} else {
							throw new CharacterSequenceEscapeException();
						}
						break;
					case 5:
						if (currentChar == ' ') {
							continue;
						} else if (currentChar == ',') {
							step = 2;
							continue;
						} else if (currentChar == ')') {
							parameters.add(new FunctionParameter(currentParam.toString().trim(), currentType));
							step = 6;
							continue;
						} else {
							throw new CharacterExcpectedException(",");
						}
					case 6:
						break;
					case 7:
						currentType = ParameterType.REFERENCE;
						if (currentChar == ' ') {
							step = 5;
							continue;
						} else if (currentChar == ',') {
							step = 2;
							continue;
						} else if (currentChar == ')') {
							parameters.add(new FunctionParameter(currentParam.toString().trim(), currentType));
							step = 6;
							continue;
						}
						break;
					case 8:
						currentType = ParameterType.NUMBER;
						if (currentChar == ' ') {
							step = 5;
							continue;
						} else if (currentChar == ',') {
							step = 2;
							continue;
						} else if (currentChar == ')') {
							parameters.add(new FunctionParameter(currentParam.toString().trim(), currentType));
							step = 6;
							continue;
						} else if (String.valueOf(currentChar).matches("[A-Za-z]")) {
							throw new CharacterInvalidException();
						}
						break;
					case 11:
						currentType = ParameterType.BOOLEAN;
						switch (currentParam.length()) {
							case 1:
								if (currentChar != 'r') {
									step = 7;
								}
								break;
							case 2:
								if (currentChar != 'u') {
									step = 7;
								}
								break;
							case 3:
								if (currentChar != 'e') {
									step = 7;
								}
								break;
							case 4:
								if (currentChar == ' ') {
									step = 5;
									continue;
								} else if (currentChar == ',') {
									step = 2;
									continue;
								} else if (currentChar == ')') {
									parameters.add(new FunctionParameter(currentParam.toString().trim(), currentType));
									step = 6;
									continue;
								} else {
									step = 7;
								}
						}
						break;
					case 12:
						currentType = ParameterType.BOOLEAN;
						switch (currentParam.length()) {
							case 1:
								if (currentChar != 'a') {
									step = 7;
								}
								break;
							case 2:
								if (currentChar != 'l') {
									step = 7;
								}
								break;
							case 3:
								if (currentChar != 's') {
									step = 7;
								}
								break;
							case 4:
								if (currentChar != 'e') {
									step = 7;
								}
								break;
							case 5:
								if (currentChar == ' ') {
									step = 5;
									continue;
								} else if (currentChar == ',') {
									step = 2;
									continue;
								} else if (currentChar == ')') {
									parameters.add(new FunctionParameter(currentParam.toString().trim(), currentType));
									step = 6;
									continue;
								} else {
									step = 7;
								}
						}
						break;
					case 13:
						currentType = ParameterType.USER;
						switch (currentParam.length()) {
							case 1:
								if (currentChar != 's') {
									step = 7;
								}
								break;
							case 2:
								if (currentChar != 'e') {
									step = 7;
								}
								break;
							case 3:
								if (currentChar != 'r') {
									step = 7;
								}
								break;
							case 4:
								if (currentChar == ' ') {
									step = 5;
									continue;
								} else if (currentChar == ',') {
									step = 2;
									continue;
								} else if (currentChar == ')') {
									parameters.add(new FunctionParameter(currentParam.toString().trim(), currentType));
									step = 6;
									continue;
								} else {
									step = 7;
								}
						}
						break;
					default:
						throw new RuntimeException("Ocorreu um erro inesperado ao fazer parsing da função " + call + " Passo inválido: " + step);
				}
				currentParam.append(currentChar);
			} catch (CharacterInvalidException e) {
				throw new RuntimeException("Invalid function: " + call + " character '" + currentChar + "' invalid at position " + i);
			} catch (CharacterSequenceEscapeException e) {
				throw new RuntimeException("Invalid function: " + call + " character '" + currentChar + "' invalid at position " + i + ". Invalid escape sequence");
			} catch (CharacterExcpectedException e) {
				throw new RuntimeException("Invalid function: " + call + " character '" + currentChar + "' invalid at position " + i + ". Character expected: " + e.getMessage());
			}
		}
		return parameters.toArray(new FunctionParameter[parameters.size()]);
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

}

class CharacterInvalidException extends Exception {

	private static final long serialVersionUID = 1L;

	public CharacterInvalidException() {
		super();
	}

}

class CharacterSequenceEscapeException extends Exception {

	private static final long serialVersionUID = 1L;

	public CharacterSequenceEscapeException() {
		super();
	}

}

class CharacterExcpectedException extends Exception {

	private static final long serialVersionUID = 1L;

	public CharacterExcpectedException(String chars) {
		super(chars);
	}

}
