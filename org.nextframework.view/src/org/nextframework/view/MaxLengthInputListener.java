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

import java.util.Map;

import org.nextframework.validation.annotation.MaxLength;

/**
 * @author rogelgarcia
 * @since 03/02/2006
 * @version 1.1
 */
public class MaxLengthInputListener implements InputListener<MaxLength> {
	
	
	public void onRender(InputTag input, MaxLength maxLength) {
		Map<String, Object> attributes = input.getDynamicAttributesMap();
		Object maxlengthinput = attributes.get("maxlength");
		Object sizeinput = attributes.get("size");
		if(maxlengthinput == null) {
			attributes.put("maxlength", maxLength.value());
		}
		if(sizeinput == null) {
			attributes.put("size", Math.min(maxLength.value(),50));
		}
	}

	public Class<MaxLength> getAnnotationType() {
		return MaxLength.class;
	}

}
