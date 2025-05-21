package org.nextframework.report.generator.data;

import java.util.Calendar;
import java.util.Date;

import org.nextframework.bean.annotation.DescriptionProperty;

public enum ReportFilterDateAutoFilter {

	MES_ANTERIOR,
	ULTIMOS_30_DIAS,
	SEMANA_ANTERIOR,
	ULTIMOS_7_DIAS,
	DIA_ANTERIOR,
	DIA_ATUAL;

	@DescriptionProperty
	public String getDescription() {
		switch (this) {
			case MES_ANTERIOR:
				return "Mês Anterior";
			case ULTIMOS_30_DIAS:
				return "Últimos 30 Dias";
			case SEMANA_ANTERIOR:
				return "Semana Anterior";
			case ULTIMOS_7_DIAS:
				return "Últimos 7 dias";
			case DIA_ANTERIOR:
				return "Dia Anterior";
			case DIA_ATUAL:
				return "Dia Atual";
			default:
				return "desconhecido";
		}
	}

	public Date[] getDateInterval() {
		Calendar c1 = zeroTime();
		Calendar c2 = zeroTime();
		switch (this) {
			case MES_ANTERIOR:
				c1.add(Calendar.MONTH, -1);
				c1.set(Calendar.DATE, 1);
				c2.set(Calendar.DATE, 1);
				break;
			case ULTIMOS_30_DIAS:
				c1.add(Calendar.DATE, -30);
				break;
			case SEMANA_ANTERIOR:
				int dow = c1.get(Calendar.DAY_OF_WEEK);
				c1.add(Calendar.DATE, -(dow - 1));
				c2.add(Calendar.DATE, -(dow - 1 + 7));
				break;
			case ULTIMOS_7_DIAS:
				c1.add(Calendar.DATE, -7);
				break;
			case DIA_ANTERIOR:
				c1.add(Calendar.MONTH, -1);
				break;
			case DIA_ATUAL:
				c2.add(Calendar.MONTH, 1);
				break;

			default:
				throw new RuntimeException("ReportFilterDateAutoFilter: not implemented");
		}
		return new Date[] { c1.getTime(), c2.getTime() };
	}

	private Calendar zeroTime() {
		Calendar instance = Calendar.getInstance();
		instance.set(Calendar.HOUR_OF_DAY, 0);
		instance.set(Calendar.MINUTE, 0);
		instance.set(Calendar.SECOND, 0);
		instance.set(Calendar.MILLISECOND, 0);
		return instance;
	}

	public Date getBegin() {
		return getDateInterval()[0];
	}

	public Date getEnd() {
		return getDateInterval()[1];
	};

}
