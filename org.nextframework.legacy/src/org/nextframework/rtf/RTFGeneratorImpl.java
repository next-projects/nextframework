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
package org.nextframework.rtf;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

public class RTFGeneratorImpl implements RTFGenerator {

	private RTFNameResolver nameResolver;

	public RTFGeneratorImpl(RTFNameResolver nameResolver){
		this.nameResolver = nameResolver;
	}
	
	public byte[] generate(RTF rtf) {
		ByteArrayOutputStream out;
		try {
			Map<String, String> parameterMap = rtf.getParameterMap();
			if(parameterMap == null){
				throw new NullPointerException("RTF não possui parameterMap");
			}
			InputStream in = nameResolver.resolveName(rtf.getName());
			out = new ByteArrayOutputStream();
			int i = 0;
			boolean inTag = false;
			StringBuilder currentTag = new StringBuilder();
			while ((i = in.read()) != -1) {
				if(i == '<'){
					inTag = true;
					continue;
				}
				if(i == '>'){
					inTag = false;
					String param = currentTag.toString();
					//System.out.println(param);
					String value = parameterMap.get(param);
					if(value != null){
						byte[] bytes = value.getBytes();
						out.write(bytes);	
					}
					
					currentTag = new StringBuilder();
					continue;
				}
				if(inTag){
					//System.out.println((char)i);
					currentTag.append((char)i);
				} else {
					out.write(i);	
				}
				
			}
		} catch (Exception e) {
			throw new RTFException(e);
		}		
		return out.toByteArray();
	}

}
