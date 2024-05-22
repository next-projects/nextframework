package org.nextframework.view.components;

import java.io.Serializable;

import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.persistence.HibernateUtils;
import org.nextframework.util.Util;
import org.nextframework.view.TagUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

public class InputTagSuggestComponent extends InputTagComponent {

	public String getValueToStringDescription() {
		Object value = inputTag.getValue();
		if (!HibernateUtils.isLazy(value)) {
			if (!StringUtils.hasText(Util.strings.toStringDescription(value))) {
				//probably not loaded
				try {
					Class<?> userClass = ClassUtils.getUserClass(value.getClass());
					BeanDescriptor bd = BeanDescriptorFactory.forBean(value);
					value = HibernateUtils.loadValue(value, userClass, (Serializable) bd.getId());
				} catch (Exception e) {
					// if can't load.. do nothing
				}
			}
		}
		return TagUtils.getObjectDescriptionToString(value);
	}

}
