package org.nextframework.report.generator.mvc;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.classmanager.ClassManagerFactory;
import org.nextframework.report.generator.data.FieldFormatter;
import org.nextframework.report.generator.data.FieldProcessor;
import org.nextframework.report.generator.data.ReportFilterDateAutoFilter;
import org.nextframework.util.Util;
import org.nextframework.view.BaseTag;

public class ReportComponentTag extends BaseTag {

	@Override
	protected void doComponent() throws Exception {

		Class<?> customBeanClass = (Class<?>) getRequest().getAttribute("customBeanClass");
		if (customBeanClass != null) {
			String typeName = Util.strings.uncaptalize(ReportComponentTag.class.getSimpleName());
			String customPageUrl = "/WEB-INF/tags/" + typeName + "_" + customBeanClass.getSimpleName() + ".jsp";
			pushAttribute("customPageUrl", customPageUrl);
		}

		pushAttribute("reportCalculatedFieldsProcessor", getProcessorsMap());
		pushAttribute("reportFieldsFormatters", getFormattersMap());
		pushAttribute("reportFilterDateAutoFilterList", ReportFilterDateAutoFilter.values());
		pushAttribute("ReportFilterDateAutoFilterType", ReportFilterDateAutoFilter.class);

		includeJspTemplate();

	}

	private Object getProcessorsMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("", "Nenhum");
		Class<FieldProcessor>[] fieldProcessors = ClassManagerFactory.getClassManager().getAllClassesOfType(FieldProcessor.class);
		for (Class<FieldProcessor> class1 : fieldProcessors) {
			if (!Modifier.isAbstract(class1.getModifiers())) {
				map.put(class1.getSimpleName(), BeanDescriptorFactory.forClass(class1).getDisplayName());
			}
		}
		return map;
	}

	private Map<String, String> getFormattersMap() {
		Map<String, String> map = new LinkedHashMap<String, String>();
		Class<FieldFormatter>[] fieldProcessors = ClassManagerFactory.getClassManager().getAllClassesOfType(FieldFormatter.class);
		Arrays.sort(fieldProcessors, new Comparator<Class<FieldFormatter>>() {
			public int compare(Class<FieldFormatter> o1, Class<FieldFormatter> o2) {
				return o1.getSimpleName().compareTo(o2.getSimpleName());
			};
		});
		for (Class<FieldFormatter> class1 : fieldProcessors) {
			if (!Modifier.isAbstract(class1.getModifiers())) {
				map.put(class1.getSimpleName(), BeanDescriptorFactory.forClass(class1).getDisplayName());
			}
		}
		return map;
	}

}
