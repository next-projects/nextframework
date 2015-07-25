package org.nextframework.view;

public interface OgnlEvaluator {

	<E> E evaluate(String expression,Class<E> expectedType, BaseTag baseTag);
}
