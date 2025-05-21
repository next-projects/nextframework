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
package org.nextframework.types;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.util.Formattable;
import java.util.Formatter;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;
import org.nextframework.summary.aggregator.Incrementable;

/**
 * @@author Fabrício
 */
public class Money extends Number implements Serializable, Comparable<Object>, UserType, Incrementable<Money>, Formattable {

	// private static final Log log = LogFactory.getLog(Money.class);

	private static final long serialVersionUID = 1L;

	protected BigDecimal value = null;

	protected MathContext mathContext = new MathContext(50, RoundingMode.HALF_EVEN);

	protected boolean isNull = false;

	// -----------------------------------------------

	public Money() {
		value = BigDecimal.ZERO;
	}

	public Money(boolean nullValue) {
		this();
		isNull = nullValue;
	}

	public boolean isNull() {
		return isNull && value.intValue() == 0;
	}

	public Money(BigDecimal value, int precision, RoundingMode roundingMode) {
		this.value = value;
		this.mathContext = new MathContext(precision, roundingMode);
	}

	public Money(Money money) {
		this(money.value, money.mathContext.getPrecision(), money.mathContext.getRoundingMode());
	}

	public Money(Number value, boolean multipliedBy100) {
		this.value = new BigDecimal(value.toString());
		if (multipliedBy100 == true) {
			this.value = this.value.movePointLeft(2);
		}
	}

	public Money(long value, boolean multipliedBy100) {
		this(new Long(value), multipliedBy100);
	}

	public Money(double value) {
		this(new Double(value), false);
	}

	public Money(Double value) {
		this(value, false);
	}

	public Money(String value) {
		this.value = new BigDecimal(value);
	}

	// -----------------------------------------------

	public Money round() {
		return new Money(value.setScale(2, mathContext.getRoundingMode()), mathContext.getPrecision(), mathContext.getRoundingMode());
	}

	public long toLong() {
		return value.setScale(2, mathContext.getRoundingMode()).movePointRight(2).longValue();
	}

	public int getPrecision() {
		return mathContext.getPrecision();
	}

	public Money setPrecision(int precision) {
		Money resultado = new Money(this);
		resultado.mathContext = new MathContext(precision, mathContext.getRoundingMode());
		return resultado;
	}

	public RoundingMode getRoundingMode() {
		return mathContext.getRoundingMode();
	}

	public Money setRoundingMode(RoundingMode roundingMode) {
		Money resultado = new Money(this);
		resultado.mathContext = new MathContext(mathContext.getPrecision(), roundingMode);
		return resultado;
	}

	// -----------------------------------------------

	public String toString() {
		// return new DecimalFormat("#,##0.00").format(value);
		return value != null ? value.toString() : "";
	}

	public String getStringFormated() {
		return new DecimalFormat("#,##0.00").format(value);
	}

	/**
	 * Testa se o objeto passado é o mesmo do objeto atual. ATENÇÂO: Não use
	 * este método para comparar valores. Neste caso, use o método
	 * compareTo(Object object).
	 */
	public boolean equals(Object object) {
		return this == object;
	}

	public int hashCode() {
		return value.hashCode();
	}

	public int compareTo(Object object) {
		if (object instanceof Money) {
			return -((Money) object).value.compareTo(value);
		} else if (object instanceof BigDecimal) {
			return -((BigDecimal) object).compareTo(value);
		} else if (object instanceof Number) {
			return -new Money((Number) object, false).value.compareTo(value);
		} else {
			throw new IllegalArgumentException();
		}
	}

	// -----------------------------------------------

	public int[] sqlTypes() {
		return new int[] { Types.BIGINT };
	}

	public Class<Money> returnedClass() {
		return Money.class;
	}

	public boolean equals(Object x, Object y) throws HibernateException {
		if (x != null) {
			return ((Money) x).compareTo(y) == 0;
		} else if (x == null && y == null) {
			return true;
		}
		return false;
	}

	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
		Object obj = rs.getObject(names[0]);
		if (obj == null) {
			return null;
		}
		if (!(obj instanceof Number)) {
			String msg = "O campo de uma propriedade do tipo Money não está com o tipo long no banco.";
			// log.error(msg);
			throw new RuntimeException(msg);
		}
		Long value = ((Number) obj).longValue();
		return new Money(value, true);
	}

	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
		if (value instanceof Money) {
			if (((Money) value).isNull()) {
				st.setNull(index, Types.BIGINT);
			} else {
				st.setLong(index, ((Money) value).toLong());
			}
		} else {
			st.setNull(index, Types.BIGINT);
		}
	}

	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	public boolean isMutable() {
		return false;
	}

	public Serializable disassemble(Object value) throws HibernateException {
		return (Money) value;
	}

	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return cached;
	}

	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return original;
	}

	public BigDecimal getValue() {
		return value;
	}

	// -----------------------------------------------

	public Money divide(Money money) {
		return new Money(value.divide(money.value, mathContext), mathContext.getPrecision(), mathContext.getRoundingMode());
	}

	public Money multiply(Money money) {
		return new Money(value.multiply(money.value, mathContext), mathContext.getPrecision(), mathContext.getRoundingMode());
	}

	public Money subtract(Money money) {
		return new Money(value.subtract(money.value, mathContext), mathContext.getPrecision(), mathContext.getRoundingMode());
	}

	public Money add(Money money) {
		return new Money(value.add(money.value, mathContext), mathContext.getPrecision(), mathContext.getRoundingMode());
	}

	@Override
	public double doubleValue() {
		return getValue().doubleValue();
	}

	@Override
	public float floatValue() {
		return getValue().floatValue();
	}

	@Override
	public int intValue() {
		return getValue().intValue();
	}

	@Override
	public long longValue() {
		return getValue().longValue();
	}

	public static void main(String[] args) {
		System.out.println(new Integer(300).compareTo(new Integer(200)));
		System.out.println(new Money("300").compareTo(new Money("200")));
	}

	@Override
	public void formatTo(Formatter formatter, int flags, int width, int precision) {
		DecimalFormat defaultFormat = new DecimalFormat("#,##0.00");
		try {
			formatter.out().append(defaultFormat.format(value));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
