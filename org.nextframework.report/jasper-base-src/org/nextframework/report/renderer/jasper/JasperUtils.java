package org.nextframework.report.renderer.jasper;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.report.definition.ReportDefinition;
import org.nextframework.report.definition.elements.ReportItem;
import org.nextframework.report.definition.elements.ReportItemIterator;
import org.nextframework.report.definition.elements.ReportTextField;
import org.nextframework.report.renderer.ValueConverter;

public class JasperUtils {

	@SuppressWarnings("rawtypes")
	public static String generateDataCSV(ReportDefinition definition) {

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintWriter dataout = new PrintWriter(os);

		ReportItemIterator iterator = new ReportItemIterator(definition);
		for (; iterator.hasNext();) {
			ReportItem next = iterator.next();
			if (next instanceof ReportTextField) {
				String expression = ((ReportTextField) next).getExpression();
				dataout.print(expression);
				dataout.print(";");
			}
		}
		dataout.println();

		List<?> data = definition.getData();
		if (data != null) {
			for (Object summaryRow : data) {
				iterator = new ReportItemIterator(definition);
				for (; iterator.hasNext();) {
					ReportItem next = iterator.next();
					if (next instanceof ReportTextField) {
						String expression = ((ReportTextField) next).getExpression();
						if (expression == null || expression.startsWith("\"") || expression.startsWith("\'")) {
							continue;
						}
						BeanDescriptor beanDescriptor = BeanDescriptorFactory.forBean(summaryRow);
						Object value;
						if (summaryRow instanceof Map<?, ?>) {
							value = ((Map) summaryRow).get(expression);
						} else {
							//REFACTOR REBUILD THIS TO STRING DESCRIPTION!!! Util.strings.toStringDescription(beanDescriptor.getPropertyDescriptor(expression).getValue());
							if (!expression.startsWith("param")) {
								Object rawValue = toString(beanDescriptor.getPropertyDescriptor(expression).getValue());
								value = rawValue != null ? rawValue.toString() : rawValue;
							} else {
								value = null;
							}
						}
						if (value != null)
							dataout.print(value.toString().replace(';', ' ').replace('\n', ' '));
						else
							dataout.print("");
						dataout.print(";");
					}
				}
				dataout.println();
			}
		}

		dataout.flush();
		dataout.close();

		return os.toString();
	}

	//REFACTOR DO IT IN THE RIGHT PLACE
	@Deprecated
	private static Object toString(Object value) {
		String formatDate = "dd/MM/yyyy";
		String formatNumber = "#,##0.##";
		if (value instanceof Calendar) {
			value = ((Calendar) value).getTime();
		}
		BeanDescriptor beanDescriptor = null;
		{
			if (value == null) {
				return "";
			}
			Class<?> horaClass = safeClass("org.nextframework.types.SimpleTime");
			if (value != null && horaClass != null && horaClass.isAssignableFrom(value.getClass())) {
				return value.toString();
			} else if (value instanceof Date) { //FIXME
				DateFormat dateFormat = new SimpleDateFormat(formatDate);
				return dateFormat.format(value);
			} else if (value instanceof Number) {
				NumberFormat numberFormat = new DecimalFormat(formatNumber);
				return numberFormat.format(value);
			}
			beanDescriptor = BeanDescriptorFactory.forBean(value);
			Object description = beanDescriptor.getDescription();
			if (description == null) {
				//C�DIGO ALTERADO EM 16 DE NOVEMBRO DE 2006
				//description = value.toString();
				// C�DIGO ALTERADO EM 05 DE DEZEMBRO DE 2006
				// MOTIVO: O C�DIGO ANTERIOR IMPRIMIA:   br....Aluno@93CD21
				if (beanDescriptor.getDescriptionPropertyName() == null) {
					description = value.toString();
				} else {
					description = "";
				}
			}
			return description.toString();
		}
	}

	@Deprecated
	private static Class<?> safeClass(String string) {
		try {
			return Class.forName(string);
		} catch (Exception e) {
			return null;
		}
	}

	public static String applyConverter(ValueConverter valueConverter, Object content) {
		Object converted = valueConverter != null ? valueConverter.apply(content) : content;
		return converted != null ? converted.toString() : null;
	}

}