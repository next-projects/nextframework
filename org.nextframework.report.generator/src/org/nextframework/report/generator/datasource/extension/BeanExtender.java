package org.nextframework.report.generator.datasource.extension;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nextframework.compilation.SourceCodeBlock;
import org.nextframework.compilation.SourceCodeBuilder;
import org.nextframework.report.generator.annotation.ExtendBean;

public class BeanExtender {

	private static final String SUBCLASS_SUFIX = "$$ExtendedByBeanExtender";

	private String id = "0";
	private Map<Class<?>, List<ExtensionInfo>> extensions = new HashMap<Class<?>, List<ExtensionInfo>>();
	private Map<Class<?>, Object> servicesMap = new HashMap<Class<?>, Object>();
	private Map<Class<?>, Class<?>> extendedClasses = new HashMap<Class<?>, Class<?>>();

	public BeanExtender(List<?> services) {
		checkServices(services);
	}

	public void setId(String id) {
		this.id = id;
	}

	private void checkServices(List<?> services) {

		for (Object service : services) {
			servicesMap.put(service.getClass(), service);
			Method[] methods = service.getClass().getMethods();
			for (Method method : methods) {
				if (isExtensionElegible(method)) {

					Class<?> classToExtend = method.getParameterTypes()[0];

					ExtensionInfo extensionInfo = new ExtensionInfo();
					extensionInfo.setClassToExtend(classToExtend);
					extensionInfo.setClassExtended(method.getReturnType());
					extensionInfo.setMethod(method);
					extensionInfo.setService(service);

					List<ExtensionInfo> list = extensions.get(classToExtend);
					if (list == null) {
						list = new ArrayList<ExtensionInfo>();
						extensions.put(classToExtend, list);
					}
					list.add(extensionInfo);

				}
			}
		}

	}

	private boolean isExtensionElegible(Method method) {
		if (method.getParameterTypes().length == 1 &&
				method.getReturnType() != Void.class &&
				method.getAnnotation(ExtendBean.class) != null) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	public Class createExtendedClassForBeanClass(Class x) {

		List<ExtensionInfo> extensionInfoList = getExtensionsFor(x);

		if (extensionInfoList == null || extensionInfoList.size() == 0) {
			return x;
		}

		SourceCodeBuilder code = new SourceCodeBuilder();
		code.setSuperclass(x);
		code.setClassName(x.getSimpleName() + SUBCLASS_SUFIX + "_" + id);

		code.declareAttribute(x, "delegate$$bean");

		Method[] methods = x.getMethods();
		for (Method method : methods) {
			if (method.getDeclaringClass().equals(Object.class) || Modifier.isFinal(method.getModifiers())) {
				continue;
			}
			if (method.isSynthetic()) { //Duplicated methods when the method is from interface
				continue;
			}
			//if(method.getName().equals("compareTo") && method.getParameterTypes()[0].equals(Object.class)){//workaround.. check why there are 2 compareTo methods
			//	continue;
			//}
			Class<?>[] parameterTypes = method.getParameterTypes();
			String[] parameterNames = new String[parameterTypes.length];
			Object[] parameterArgs = new Object[parameterTypes.length];
			for (int i = 0; i < parameterTypes.length; i++) {
				parameterNames[i] = "param" + i;
				parameterArgs[i] = new StringBuilder("param" + i);
			}
			SourceCodeBlock overritenMethod = code.declareMethod(method.getReturnType(),
					method.getName(),
					parameterTypes,
					parameterNames);

			if (method.getReturnType() != Void.class && method.getReturnType() != void.class) {
				overritenMethod.call("return delegate$$bean." + method.getName(), parameterArgs);
			} else {
				overritenMethod.call("delegate$$bean." + method.getName(), parameterArgs);
			}
			Annotation[] annotations = method.getAnnotations();
			for (Annotation annotation : annotations) {
//				if(!annotation.annotationType().equals(ExtendBean.class)){
				code.addImport(annotation.annotationType());
				overritenMethod.declareAnnotation(annotation, true);
//				}
			}
		}

		Set<Class> serviceClasses = new HashSet<Class>();
		for (ExtensionInfo extensionInfo : extensionInfoList) {

			String serviceFieldName = "service$$" + extensionInfo.getService().getClass().getSimpleName();
			if (serviceClasses.add(extensionInfo.getService().getClass())) {
				code.declareAttribute(extensionInfo.getService().getClass(), serviceFieldName);
			}

			Method serviceMethod = extensionInfo.getMethod();

			SourceCodeBlock methodBlock = code.declareMethod(serviceMethod.getReturnType(), serviceMethod.getName(), new Class[0], new String[0]);

			Annotation[] annotations = serviceMethod.getAnnotations();
			for (Annotation annotation : annotations) {
				code.addImport(annotation.annotationType());
				methodBlock.declareAnnotation(annotation, true);
			}

			ExtendBean extendBean = serviceMethod.getAnnotation(ExtendBean.class);
			if (extendBean.cacheResult()) {

				String methodCacheAttribute = "cache$$" + extensionInfo.getService().getClass().getSimpleName() + "$$" + serviceMethod.getName();
				code.declareAttribute(serviceMethod.getReturnType(), methodCacheAttribute);

				methodBlock.append("if (" + methodCacheAttribute + " == null) " + methodCacheAttribute + " = " + serviceFieldName + "." + serviceMethod.getName() + "(this);");
				methodBlock.append("return " + methodCacheAttribute + ";");

			} else {

				methodBlock.append("return " + serviceFieldName + "." + serviceMethod.getName() + "(this);");
				//methodBlock.call("return "+serviceFieldName+"."+serviceMethod.getName(), new StringBuilder("this"));
			}

		}

		//System.out.println(code);

		try {
			return code.generateClass(x.getClassLoader());
		} catch (Exception e) {
			throw new RuntimeException("cannot extend bean class", e);
		}

	}

	@SuppressWarnings("rawtypes")
	protected List<ExtensionInfo> getExtensionsFor(Class x) {
		return extensions.get(x);
	}

	public <X> X extendBean(X x, Class<X> clazz) {
		X newBean = getExtendedBeanFrom(x, clazz);
		injectServices(newBean);
		injectDelegateBean(newBean, x);
		return newBean;
	}

	protected void injectDelegateBean(Object newBean, Object x) {
		try {
			Field field = newBean.getClass().getDeclaredField("delegate$$bean");
			field.setAccessible(true);
			field.set(newBean, x);
		} catch (Exception e) {
			throw new RuntimeException("cannot set bean delegate", e);
		}
	}

	protected void injectServices(Object o) {
		//TODO USE CACHE 
		Field[] declaredFields = o.getClass().getDeclaredFields();
		for (Field field : declaredFields) {
			if (field.getName().startsWith("service$$")) {
				Object service = servicesMap.get(field.getType());
				field.setAccessible(true);
				try {
					field.set(o, service);
				} catch (Exception e) {
					throw new RuntimeException("cannot inject service " + field.getType());
				}
			}
		}
	}

	protected <X> X getExtendedBeanFrom(X x, Class<X> clazz) {
		Class<X> extendedClass = getExtendedClass(clazz);
		if (extendedClass.equals(x.getClass())) {
			return x;
		}
		X newBean;
		try {
			newBean = extendedClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("cannot instantiate extended bean", e);
		}
		return newBean;
	}

	@SuppressWarnings("unchecked")
	protected <X> Class<X> getExtendedClass(Class<X> clazz) {
		Class<X> result = (Class<X>) extendedClasses.get(clazz);
		if (result == null) {
			result = createExtendedClassForBeanClass(clazz);
			extendedClasses.put(clazz, result);
		}
		return result;
	}

	public boolean isSubClass(Class<?> clazz) {
		return clazz.getName().contains(SUBCLASS_SUFIX);
	}

}
