package org.nextframework.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	private static final SimpleDateFormat sdfDMY = new SimpleDateFormat("dd/MM/yyyy");
	private static final SimpleDateFormat sdfDMYHM = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	private static final SimpleDateFormat sdfDMYHMS = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	private static final SimpleDateFormat sdfYMD_HMS = new SimpleDateFormat("yy/MM/dd,HH:mm:ss");

	/**
	 * Retornar true se as duas datas são a mesma data. Ignorando as horas minutos segundos e milisegundos
	 * @param a
	 * @param b
	 * @return
	 */
	public boolean isSameDate(Date a, Date b) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(a);
		Calendar cb = Calendar.getInstance();
		cb.setTime(b);
		return isSameDate(ca, cb);
	}

	/**
	 * Retornar true se as duas datas são a mesma data. Ignorando as horas minutos segundos e milisegundos
	 * @param a
	 * @param b
	 * @return
	 */
	public boolean isSameDate(Calendar a, Calendar b) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(a.getTime());
		Calendar cb = Calendar.getInstance();
		cb.setTime(b.getTime());
		return isSameProperties(a, b, Calendar.ERA, Calendar.YEAR, Calendar.MONTH, Calendar.DATE);
	}

	public boolean isSameProperties(Calendar a, Calendar b, int... properties) {
		for (int i : properties) {
			if (a.get(i) != b.get(i)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Configura como 0 as horas minutos segundos e milisegundos.
	 * Retorna a nova data configurada.
	 * @param date
	 * @return
	 */
	public Date resetTime(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		resetTime(calendar);
		return calendar.getTime();
	}

	/**
	 * Configura como 0 as horas minutos segundos e milisegundos.
	 * Retorna o calendar alterado (mesma instancia do que foi passado como parâmetro).
	 * @param date
	 * @return
	 */
	public Calendar resetTime(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}

	public Date toDate(String date, String pattern) {
		try {
			return new SimpleDateFormat(pattern).parse(date);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public Calendar toCalendar(String date, String pattern) {
		Calendar c = Calendar.getInstance();
		c.setTime(toDate(date, pattern));
		return c;
	}

	public Calendar toCalendar(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	/**
	 * Retorna o Date correspondente a string no formato dd/MM/yyyy HH:mm
	 * @param date
	 * @return
	 */
	public Date dmyhm(String date) {
		try {
			return sdfDMYHM.parse(date);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Retorna o Date correspondente a string no formato yy/MM/dd,HH:mm:ss
	 * @param date
	 * @return
	 */
	public Date ymd_hms(String date) {
		try {
			return sdfYMD_HMS.parse(date);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Retorna o Date correspondente a string no formato dd/MM/yyyy
	 * @param date
	 * @return
	 */
	public Date dmy(String date) {
		try {
			return sdfDMY.parse(date);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Retorna o Calendar correspondente a string no formato dd/MM/yyyy HH:mm
	 * @param date
	 * @return
	 */
	public Calendar dmyhmCalendar(String date) {
		try {
			Calendar c = Calendar.getInstance();
			c.setTime(sdfDMYHM.parse(date));
			return c;
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Retorna o Calendar correspondente a string no formato dd/MM/yyyy
	 * @param date
	 * @return
	 */
	public Calendar dmyCalendar(String date) {
		try {
			Calendar c = Calendar.getInstance();
			c.setTime(sdfDMY.parse(date));
			return c;
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Retorna o String dd/MM/yyyy HH:mm correspondente ao Date ou Calendar passado como parâmetro
	 * @param date
	 * @return
	 */
	public String toDmyhm(Object data) {
		if (data instanceof Calendar) {
			data = ((Calendar) data).getTime();
		}
		return sdfDMYHM.format(data);
	}

	/**
	 * Retorna o String dd/MM/yyyy HH:mm:ss correspondente ao Date ou Calendar passado como parâmetro
	 * @param date
	 * @return
	 */
	public String toDmyhms(Object data) {
		if (data instanceof Calendar) {
			data = ((Calendar) data).getTime();
		}
		return sdfDMYHMS.format(data);
	}

	/**
	 * Retorna o String dd/MM/yyyy correspondente ao Date ou Calendar passado como parâmetro
	 * @param date
	 * @return
	 */
	public String toDmy(Object data) {
		if (data instanceof Calendar) {
			data = ((Calendar) data).getTime();
		}
		return sdfDMY.format(data);
	}

	/**
	 * Retorna o String no formato informado correspondente ao Date ou Calendar passado como parâmetro
	 * @param data
	 * @param pattern
	 * @return
	 */
	public String toString(Object data, String pattern) {
		if (data instanceof Calendar) {
			data = ((Calendar) data).getTime();
		}
		return new SimpleDateFormat(pattern).format(data);
	}

}
