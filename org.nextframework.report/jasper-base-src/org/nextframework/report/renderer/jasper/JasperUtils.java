package org.nextframework.report.renderer.jasper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.nextframework.report.definition.elements.Subreport;

public class JasperUtils {

	/**
	 * Utility to debug generated reports
	 * @param definition
	 * @param dir
	 * @param fileName
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static void writeJrxmlAndDataToDisk(ReportDefinition definition, String dir, String fileName) {
		try {
			File dirFile = new File(dir);
			dirFile.mkdirs();
			ReportItemIterator reportItemIterator = new ReportItemIterator(definition);
			int i = 0;
			while (reportItemIterator.hasNext()) {
				ReportItem next = reportItemIterator.next();
				if (next instanceof Subreport) {
					writeJrxmlAndDataToDisk(((Subreport) next).getReport(), dir, fileName + "_subreport_" + (++i));
				}
			}
			//para debug
			writeBytes(new File(dirFile, fileName + ".jrxml"), JasperReportsRenderer.renderAsJRXML(definition));
			PrintWriter dataout = new PrintWriter(new File(dirFile, fileName + ".csv"));
			ReportItemIterator iterator = new ReportItemIterator(definition);
			List<?> data = definition.getData();
			if(data == null){
				data = new ArrayList<Object>();
			}
			for (; iterator.hasNext();) {
				ReportItem next = iterator.next();
				if (next instanceof ReportTextField) {
					String expression = ((ReportTextField) next).getExpression();
					dataout.print(expression);
					dataout.print(";");
				}
			}
			dataout.println();
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
						if(summaryRow instanceof Map<?, ?>){
							value = ((Map)summaryRow).get(expression);
						} else {
							//REFACTOR REBUILD THIS TO STRING DESCRIPTION!!! Util.strings.toStringDescription(beanDescriptor.getPropertyDescriptor(expression).getValue());
							if(!expression.startsWith("param")){
								Object rawValue = toString(beanDescriptor.getPropertyDescriptor(expression).getValue());
								value = rawValue != null? rawValue.toString() : rawValue;
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
			dataout.flush();
			dataout.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	//REFACTOR DO IT IN THE RIGHT PLACE
	@Deprecated
	private static Object toString(Object value) {
		String formatDate   = "dd/MM/yyyy";
		String formatNumber = "#,##0.##";
		if(value instanceof Calendar){
			value = ((Calendar)value).getTime();
		}
		BeanDescriptor beanDescriptor = null;
		{
			if (value == null) {
				return "";
			}
			Class<?> horaClass = safeClass("org.nextframework.types.SimpleTime");
			if(value != null && horaClass != null && horaClass.isAssignableFrom(value.getClass())){
				return value.toString();
			} else if(value instanceof Date){ //FIXME
				DateFormat dateFormat = new SimpleDateFormat(formatDate);
				return dateFormat.format(value);
			} else if(value instanceof Number){
				NumberFormat numberFormat = new DecimalFormat(formatNumber);
				return numberFormat.format(value);
			}
			beanDescriptor = BeanDescriptorFactory.forBean(value);
			
			Object description = beanDescriptor.getDescription();
			if (description == null) {
				//CÓDIGO ALTERADO EM 16 DE NOVEMBRO DE 2006
				//description = value.toString();
				// CÓDIGO ALTERADO EM 05 DE DEZEMBRO DE 2006
				// MOTIVO: O CÓDIGO ANTERIOR IMPRIMIA:   br....Aluno@93CD21
				if(beanDescriptor.getDescriptionPropertyName() == null){
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

	private static void writeBytes(File file, byte[] bytes) throws FileNotFoundException, IOException {
		FileOutputStream out = new FileOutputStream(file);
		out.write(bytes);
		out.flush();
		out.close();
	}
}
