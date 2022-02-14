package org.nextframework.util;

import java.math.BigDecimal;

/**
 * @author marcusabreu
 */
public class NumberUtils {

	public boolean isPositive(Integer number) {
		return number != null && number > 0;
	}

	public boolean isEmpty(Number number) {
		return number == null || number.doubleValue() == 0;
	}

	public boolean isNotEmpty(Number number) {
		return !isEmpty(number);
	}

	public Double round(Double valor, int decimal) {
		if (valor == null)
			return null;
		double d = Math.pow(10.0, decimal);
		return Math.round(valor * d) / d;
	}

	public BigDecimal truncateDecimal(double x, int numberofDecimals) {
		if (x > 0) {
			return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_FLOOR);
		} else {
			return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_CEILING);
		}
	}

	private static final String NUMERO = "[0-9]*";

	public boolean isNumber(String valor) {
		if (valor == null)
			return false;
		return valor.trim().matches(NUMERO);
	}

	public boolean isBetween(Number valorVerificacao, Number valorInicial, Number valorFinal) {
		if (valorVerificacao != null && valorInicial != null && valorFinal != null && valorVerificacao.doubleValue() > valorInicial.doubleValue() && valorVerificacao.doubleValue() <= valorFinal.doubleValue()) {
			return true;
		}
		return false;
	}

	public Double percent(Number dividendo, Number divisor, int casas, boolean nullSafe) {
		if (dividendo == null || divisor == null) {
			return nullSafe ? 0.0 : null;
		}
		if (round(divisor.doubleValue(), 5).doubleValue() == 0) {
			return 0.0;
		}
		double calc = (dividendo.doubleValue() / divisor.doubleValue()) * 100;
		return round(calc, casas);
	}

	public boolean equals(Number n1, Number n2) {
		return equals(n1, n2, null);
	}

	private static final double DEFAULT_EPSILON = 0.000001d; //Precisão de 5 casas decimais. 20.12345 != 20.12346; 0.123456 == 20.123457

	public boolean equals(Number n1, Number n2, Double epsilon) {
		if (n1 == null && n2 == null) {
			return true;
		}
		if (n1 == null || n2 == null) {
			return false;
		}
		if (n1 instanceof Float && n2 instanceof Float) {
			return Math.abs(((Float) n1) - ((Float) n2)) < (epsilon != null ? epsilon : DEFAULT_EPSILON);
		}
		if (n1 instanceof Double && n2 instanceof Double) {
			return Math.abs(((Double) n1) - ((Double) n2)) < (epsilon != null ? epsilon : DEFAULT_EPSILON);
		}
		return n1.equals(n2);
	}

}