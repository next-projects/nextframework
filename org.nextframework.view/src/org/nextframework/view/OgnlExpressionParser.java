package org.nextframework.view;

import java.util.HashMap;
import java.util.Map;

import org.nextframework.exception.ExpressionParseException;
import org.springframework.beans.BeanWrapperImpl;

import ognl.Ognl;
import ognl.OgnlException;

/**
 * @author rogelgarcia
 * @since 27/10/2005
 * @version 1.0
 */
public class OgnlExpressionParser {

	public static Map<String, Object> expressionCache = new HashMap<String, Object>();

	public static Object parse(String expression, Map<String, Object> contextMap) throws ExpressionParseException {
		Object value;
		try {
			Object tree = getExpressionTree(expression);
			value = Ognl.getValue(tree, (Object) contextMap, null);
		} catch (OgnlException e) {
			throw new ExpressionParseException("Erro ao fazer parsing de " + expression, e);
		}
		return value;
	}

	public static <X> X parse(String expression, Class<X> expectedClass, Map<String, Object> contextMap) throws ExpressionParseException {
		Object value;
		try {
			Object tree = getExpressionTree(expression);
			value = Ognl.getValue(tree, (Object) contextMap, null);
		} catch (OgnlException e) {
			if (!e.getMessage().startsWith("source is null")) {
				throw new ExpressionParseException("Erro ao fazer parsing de " + expression, e);
			} else {
				value = null;
			}

		}
		if (value != null && !expectedClass.isAssignableFrom(value.getClass())) {
			BeanWrapperImpl beanWrapperImpl = new BeanWrapperImpl();
			value = beanWrapperImpl.convertIfNecessary(value, expectedClass);
		}

		if (value != null && !expectedClass.isAssignableFrom(value.getClass())) {
			if (value != null && String.class.isAssignableFrom(expectedClass)) {
				@SuppressWarnings("unchecked")
				X toString = (X) value.toString();
				return toString;
			}
			throw new ExpressionParseException("A expressão #{" + expression + "} retornou um objeto do tipo " + value.getClass().getName() + " era esperado " + expectedClass.getName() + "! Não foi possível fazer a conversão");
		}
		@SuppressWarnings("unchecked")
		X x = (X) value;

		return x;
	}

	private static Object getExpressionTree(String expression) throws OgnlException {
		Object tree = expressionCache.get(expression);
		if (tree == null) {
			synchronized (expressionCache) {
				tree = expressionCache.get(expression);
				if (tree == null) {
					tree = Ognl.parseExpression(expression);
					expressionCache.put(expression, tree);
				}
			}
		}
		return tree;
	}

}
