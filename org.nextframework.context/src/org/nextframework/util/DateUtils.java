package org.nextframework.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	private static final String DMY_PATTERN = "dd/MM/yyyy";
	private static final String DMY_HM_PATTERN = "dd/MM/yyyy HH:mm";
	private static final String DMY_HMS_PATTERN = "dd/MM/yyyy HH:mm:ss";
	private static final String YMD_HMS_PATTERN = "yy/MM/dd,HH:mm:ss";
	private static final String YYYYMMDD_HHMMSS_PATTERN = "yyyy-MM-dd HH:mm:ss";

	/**
	 * Retornar true se as duas datas são a mesma data. Ignorando as horas minutos
	 * segundos e milisegundos
	 * 
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
	 * Retornar true se as duas datas são a mesma data. Ignorando as horas minutos
	 * segundos e milisegundos
	 * 
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
	 * Configura como 0 as horas minutos segundos e milisegundos. Retorna a nova
	 * data configurada.
	 * 
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
	 * Configura como 0 as horas minutos segundos e milisegundos. Retorna o calendar
	 * alterado (mesma instancia do que foi passado como parâmetro).
	 * 
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
		return parse(date, pattern);
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
	 * 
	 * @param date
	 * @return
	 */
	public Date dmyhm(String date) {
		return parse(date, DMY_HM_PATTERN);
	}

	/**
	 * Retorna o Date correspondente a string no formato yy/MM/dd,HH:mm:ss
	 * 
	 * @param date
	 * @return
	 */
	public Date ymd_hms(String date) {
		return parse(date, YMD_HMS_PATTERN);
	}

	/**
	 * Retorna o Date correspondente a string no formato yyyy-MM-dd HH:mm:ss
	 * 
	 * @param date
	 * @return
	 */
	public Date yyyyMMdd_HHmmss(String date) {
		return parse(date, YYYYMMDD_HHMMSS_PATTERN);
	}

	/**
	 * Retorna o Date correspondente a string no formato dd/MM/yyyy
	 * 
	 * @param date
	 * @return
	 */
	public Date dmy(String date) {
		return parse(date, DMY_PATTERN);
	}

	/**
	 * Retorna o Calendar correspondente a string no formato dd/MM/yyyy HH:mm
	 * 
	 * @param date
	 * @return
	 */
	public Calendar dmyhmCalendar(String date) {
		Calendar c = Calendar.getInstance();
		c.setTime(parse(date, DMY_HM_PATTERN));
		return c;
	}

	/**
	 * Retorna o Calendar correspondente a string no formato dd/MM/yyyy
	 * 
	 * @param date
	 * @return
	 */
	public Calendar dmyCalendar(String date) {
		Calendar c = Calendar.getInstance();
		c.setTime(parse(date, DMY_PATTERN));
		return c;
	}

	/**
	 * Retorna o String dd/MM/yyyy HH:mm correspondente ao Date ou Calendar passado
	 * como parâmetro
	 * 
	 * @param date
	 * @return
	 */
	public String toDmyhm(Object data) {
		return format(data, DMY_HM_PATTERN);
	}

	/**
	 * Retorna o String dd/MM/yyyy HH:mm:ss correspondente ao Date ou Calendar
	 * passado como parâmetro
	 * 
	 * @param date
	 * @return
	 */
	public String toDmyhms(Object data) {
		return format(data, DMY_HMS_PATTERN);
	}

	/**
	 * Retorna o String dd/MM/yyyy correspondente ao Date ou Calendar passado como
	 * parâmetro
	 * 
	 * @param date
	 * @return
	 */
	public String toDmy(Object data) {
		return format(data, DMY_PATTERN);
	}

	/**
	 * Retorna o String no formato informado correspondente ao Date ou Calendar
	 * passado como parâmetro
	 * 
	 * @param data
	 * @param pattern
	 * @return
	 */
	public String toString(Object data, String pattern) {
		return format(data, pattern);
	}

	private Date parse(String date, String pattern) {
		try {
			return new SimpleDateFormat(pattern).parse(date);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	private String format(Object data, String pattern) {
		if (data instanceof Calendar) {
			data = ((Calendar) data).getTime();
		}
		return new SimpleDateFormat(pattern).format(data);
	}

}
