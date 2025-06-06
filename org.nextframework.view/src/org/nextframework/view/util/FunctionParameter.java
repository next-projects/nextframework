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

public class FunctionParameter {

	private ParameterType parameterType;
	private String parameterValue;
	@Deprecated
	private String absoluteParameterValue;

	public FunctionParameter(String parameterValue) {
		this.parameterValue = parameterValue;
		calculateType();
	}

	public FunctionParameter(String parameterValue, ParameterType parameterType) {
		this.parameterValue = parameterValue;
		this.parameterType = parameterType;
	}

	private void calculateType() {
		if (parameterValue.startsWith("\"") || parameterValue.startsWith("\'")) {
			this.parameterType = ParameterType.STRING;
		} else {
			this.parameterType = ParameterType.REFERENCE;
		}

	}

	public ParameterType getParameterType() {
		return parameterType;
	}

	public String getParameterValue() {
		return parameterValue;
	}

	@Deprecated
	public String getAbsoluteParameterValue() {
		return absoluteParameterValue;
	}

	@Deprecated
	public void setAbsoluteParameterValue(String absoluteParameterValue) {
		this.absoluteParameterValue = absoluteParameterValue;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof FunctionParameter)) {
			return false;
		}
		return parameterType.equals(((FunctionParameter) obj).getParameterType());
	}

	@Override
	public int hashCode() {
		return parameterValue.hashCode();
	}

}
