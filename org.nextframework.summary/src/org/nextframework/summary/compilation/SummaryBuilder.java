package org.nextframework.summary.compilation;

import org.nextframework.compilation.JavaSourceCompiler;
import org.nextframework.summary.Summary;

public class SummaryBuilder {

	static SummaryBuilder instance = new SummaryBuilder();

	public static <Y extends Summary<E>, E> CompiledSummary<Y, E> compileSummary(Class<Y> summary) {
		return SummaryBuilder.instance.compile(summary);
	}

	@SuppressWarnings("unchecked")
	public <Y extends Summary<E>, E> CompiledSummary<Y, E> compile(Class<Y> summary) {

		String compiledSummaryClassName = summary.getName() + "CompiledSummary__$$";
		try {
			return (CompiledSummary<Y, E>) summary.getClassLoader().loadClass(compiledSummaryClassName).getDeclaredConstructor().newInstance();
		} catch (Exception e1) {
			//se nao conseguir carregar a classe é porque ela nao foi gerada ainda
		}

		SummaryJavaBuilder javaBuilder = new SummaryJavaBuilder();
		byte[] source = javaBuilder.generateSourceForSummary(summary, compiledSummaryClassName);
		try {
			//System.out.println(new String(source));
			return (CompiledSummary<Y, E>) JavaSourceCompiler.compileClass(summary.getClassLoader(), compiledSummaryClassName, source).getDeclaredConstructor().newInstance();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Could not load summary for " + summary, e);
		} catch (Exception e) {
			throw new RuntimeException("Could not compile summary for " + summary, e);
		}

	}

}
