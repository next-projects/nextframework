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
	
	String id = "0";
	
	Map<Class<?>, List<ExtensionInfo>> extensions = new HashMap<Class<?>, List<ExtensionInfo>>();
	
	Map<Class<?>, Object> servicesMap = new HashMap<Class<?>, Object>();
	
	Map<Class<?>, Class<?>> extendedClasses = new HashMap<Class<?>, Class<?>>();
	
	public BeanExtender(List<?> services){
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
				if(isExtensionElegible(method)){
					Class<?> classToExtend = method.getParameterTypes()[0];
					
					ExtensionInfo extensionInfo = new ExtensionInfo();
					extensionInfo.setClassToExtend(classToExtend);
					extensionInfo.setClassExtended(method.getReturnType());
					extensionInfo.setMethod(method);
					extensionInfo.setService(service);
					
					List<ExtensionInfo> list = extensions.get(classToExtend);
					if(list == null){
						list = new ArrayList<ExtensionInfo>();
						extensions.put(classToExtend, list);
					}
					list.add(extensionInfo);
				}
			}
		}
	}

	private boolean isExtensionElegible(Method method) {
		if(method.getParameterTypes().length == 1 &&
					method.getReturnType() != Void.class &&
					method.getAnnotation(ExtendBean.class) != null){
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("rawtypes")
	public Class createExtendedClassForBeanClass(Class x) {
		List<ExtensionInfo> extensionInfoList = getExtensionsFor(x);
		
		if(extensionInfoList == null || extensionInfoList.size() == 0){
			return x;
		}
		
		SourceCodeBuilder code = new SourceCodeBuilder();
		code.setSuperclass(x);
		code.setClassName(x.getSimpleName()+"$$ExtendedByBeanExtender_"+id);
		
		code.declareAttribute(x, "delegate$$bean");
		
		Method[] methods = x.getMethods();
		for (Method method : methods) {
			if(method.getDeclaringClass().equals(Object.class) || Modifier.isFinal(method.getModifiers())){
				continue;
			}
			if(method.isSynthetic()){ //Duplicated methods when the method is from interface
				continue;
			}
			//if(method.getName().equals("compareTo") && method.getParameterTypes()[0].equals(Object.class)){//workaround.. check why there are 2 compareTo methods
			//	continue;
			//}
			Class<?>[] parameterTypes = method.getParameterTypes();
			String[] parameterNames = new String[parameterTypes.length];
			Object[] parameterArgs = new Object[parameterTypes.length];
			for (int i = 0; i < parameterTypes.length; i++) {
				parameterNames[i] = "param"+i;
				parameterArgs[i] = new StringBuilder("param"+i); 
			}
			SourceCodeBlock overritenMethod = code.declareMethod(method.getReturnType(),
					method.getName(),
					parameterTypes,
					parameterNames);
			
			if(method.getReturnType() != Void.class && method.getReturnType() != void.class){
				overritenMethod.call("return delegate$$bean."+method.getName(), parameterArgs);
			} else {
				overritenMethod.call("delegate$$bean."+method.getName(), parameterArgs);
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
			String serviceFieldName = "service$$"+extensionInfo.getService().getClass().getSimpleName();
			if(serviceClasses.add(extensionInfo.getService().getClass())){
				code.declareAttribute(extensionInfo.getService().getClass(), 
						serviceFieldName);
			}
			Method serviceMethod = extensionInfo.getMethod();
			SourceCodeBlock methodBlock = code.declareMethod(serviceMethod.getReturnType(), 
					serviceMethod.getName(),
					new Class[0],
					new String[0]);
			methodBlock.call("return "+serviceFieldName+"."+serviceMethod.getName(), new StringBuilder("this"));
			
			Annotation[] annotations = serviceMethod.getAnnotations();
			for (Annotation annotation : annotations) {
//				if(!annotation.annotationType().equals(ExtendBean.class)){
				code.addImport(annotation.annotationType());
				methodBlock.declareAnnotation(annotation, true);
//				}
			}
		}
//		System.out.println(code);
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
	
	public <X> X extendBean(X x) {
		X newBean = getExtendedBeanFrom(x);
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
			if(field.getName().startsWith("service$$")){
				Object service = servicesMap.get(field.getType());
				field.setAccessible(true);
				try {
					field.set(o, service);
				} catch (Exception e) {
					throw new RuntimeException("cannot inject service "+field.getType());
				}
			}
		}
	}

	protected <X> X getExtendedBeanFrom(X x) {
		Class<X> extendedClass = getExtendedClassForBean(x);
		if(extendedClass.equals(x.getClass())){
			return x;
		}
		//BeanCopier copier = BeanCopier.create(x.getClass(), x.getClass(), false);//TODO CACHE
		X newBean;
		try {
			newBean = extendedClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("cannot instantiate extended bean", e);
		}
		//copier.copy(x, newBean, null);
		return newBean;
	}

	@SuppressWarnings("unchecked")
	public <X> Class<X> getExtendedClassForBean(X x) {
		Class<X> result = (Class<X>) extendedClasses.get(x.getClass());
		if(result == null){
			result = createExtendedClassForBeanClass(x.getClass());
			extendedClasses.put(x.getClass(), result);
		}
		return result;
	}

	
//	public static void main(String[] args) throws Exception {
//		run();
//	}
//
//	public static void run() throws Exception {
//		Bean x = new Bean();
//		x.setA("atext");
//		x.setB("btext");
////		BeanExtender beanExtender = new BeanExtender(Arrays.asList(new MyService(), new MyService2()));
////		Class extendedClass = beanExtender.createExtendedClassForBeanClass(SecondBean.class);
////		//System.out.println(extendedClass);
////		Field[] declaredFields = extendedClass.getDeclaredFields();
////		for (Field field : declaredFields) {
////			//System.out.println(field.getType().getSimpleName()+" "+field.getName());
////		}
////		Method[] declaredMethods = extendedClass.getDeclaredMethods();
////		for (Method method : declaredMethods) {
////			//System.out.println(method.getReturnType()+" "+method.getName()+"(");
////		}
//		BeanExtender extender = new BeanExtender(Arrays.asList(new MyService(), new MyService2()));
//		Bean b = extender.extendBean(x);
//		long a = System.currentTimeMillis();
//		List<Bean> list = new ArrayList<Bean>();
//		for (int i = 0; i < 100000; i++) {
//			Bean bx = extender.extendBean(x);
//			list.add(bx);
//		}
//		System.out.println(System.currentTimeMillis() - a);
//		System.out.println(list.size());
//		System.out.println(b.getClass());
//		System.out.println(b.getA());
//		System.out.println(b.getB());
//		MyBeanExtension ext = (MyBeanExtension) b.getClass().getMethod("getExtension").invoke(b);
//		System.out.println(ext.getC());
////		b.getClass().getMethod("setC", String.class).invoke(b, "textCCC");
////		System.out.println(b.getClass().getMethod("getC").invoke(b));
//	}
}
