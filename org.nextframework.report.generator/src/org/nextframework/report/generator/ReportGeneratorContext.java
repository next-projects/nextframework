package org.nextframework.report.generator;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.nextframework.compilation.JavaSourceCompiler;
import org.nextframework.exception.NextException;
import org.nextframework.report.definition.builder.IReportBuilder;


class ReportGeneratorContext {

	static Map<ReportElement, WeakReference<Class<IReportBuilder>>> cache = Collections.synchronizedMap(new HashMap<ReportElement, WeakReference<Class<IReportBuilder>>>()); 


	public static Class<IReportBuilder> getClassFor(ReportGenerator generator) {
		WeakReference<Class<IReportBuilder>> ref = cache.get(generator.reportElement);
		Class<IReportBuilder> class1;
		if(ref == null || ((class1 = ref.get()) == null)){
			class1 = createReportClass(generator);
			cache.put(generator.reportElement, new WeakReference<Class<IReportBuilder>>(class1));
		}
		return class1;
	}

	protected static Class<IReportBuilder> createReportClass(ReportGenerator generator) {
		return createReportClass(new URLClassLoader(new URL[0], ReportGeneratorContext.class.getClassLoader()), generator);
		//return createReportClass(ReportGeneratorContext.class.getClassLoader(), generator);
	}

	@SuppressWarnings("unchecked")
	protected static Class<IReportBuilder> createReportClass(ClassLoader classLoader, ReportGenerator generator) {
		try {
			String source = generator.getSourceCode();
			return (Class<IReportBuilder>) JavaSourceCompiler.compileClass(classLoader, generator.getReportQualifiedClassName(), source.getBytes());
		} catch (ClassNotFoundException e) {
			throw new NextException("cannot load ReportBuilder generated class", e);
		} catch (Exception e) {
			throw new NextException("cannot create ReportBuilder generated class", e);
		}
	}
	
	
}
