package org.nextframework.summary;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.nextframework.summary.annotations.Scope;
import org.nextframework.summary.compilation.SummaryResult;
import org.nextframework.summary.definition.SummaryDefinition;
import org.nextframework.summary.definition.SummaryGroupDefinition;
import org.nextframework.summary.definition.SummaryItemDefinition;
import org.nextframework.summary.definition.SummaryVariableDefinition;

public class TextReportPrinter {

	private PrintWriter out;

	public TextReportPrinter() {
		out = new PrintWriter(System.out);
	}

	public TextReportPrinter(PrintWriter out) {
		this.out = out;
	}

	public static <ROW, SUMMARY extends Summary<ROW>, X extends SummaryRow<ROW, SUMMARY>> void print(PrintWriter out, List<X> rows) {
		new TextReportPrinter(out).printReport(rows);
	}

	public static <ROW, SUMMARY extends Summary<ROW>> void print(SummaryResult<ROW, SUMMARY> summaryResult) {
		print(summaryResult.getItems());
	}

	public static <ROW, SUMMARY extends Summary<ROW>, X extends SummaryRow<ROW, SUMMARY>> void print(List<X> rows) {
		new TextReportPrinter().printReport(rows);
	}

	public <ROW, SUMMARY extends Summary<ROW>, X extends SummaryRow<ROW, SUMMARY>> void printReport(List<X> rows) {
		for (SummaryRow<ROW, SUMMARY> row : rows) {
			if (row.isFirst()) {
				printFirstRow(row);
			}
			String[] changedGroups = row.getChangedGroups();
			if (changedGroups.length > 0 && !row.isFirst()) {
				out.println("   =======================================================================================");
			}
			for (String string : changedGroups) {
				printRowForGroup(row, string);
			}
			List<SummaryItemDefinition> list = filterItens(row, SummaryVariableDefinition.class, Scope.ROW);
			for (SummaryItemDefinition summaryItemDefinition : list) {
				printValue(row, summaryItemDefinition, "      ");
			}
			out.println("      " + row.getRow());
		}
		out.flush();
	}

	<ROW, SUMARY extends Summary<ROW>> void printRowForGroup(SummaryRow<ROW, SUMARY> row, String group) {
		List<SummaryItemDefinition> list = filterItens(row, SummaryGroupDefinition.class, Scope.GROUP);
		for (SummaryItemDefinition summaryItemDefinition : list) {
			if (group.equals(summaryItemDefinition.getScopeGroup())) {
				printValue(row, summaryItemDefinition, "   ");
			}
		}
		list = filterItens(row, SummaryVariableDefinition.class, Scope.GROUP);
		for (SummaryItemDefinition summaryItemDefinition : list) {
			if (group.equals(summaryItemDefinition.getScopeGroup())) {
				printValue(row, summaryItemDefinition, "   ");
			}
		}
		out.println("   ---------------------------------------------------------------------------------------");
	}

	<ROW, SUMARY extends Summary<ROW>> void printFirstRow(SummaryRow<ROW, SUMARY> row) {
		List<SummaryItemDefinition> list = filterItens(row, SummaryVariableDefinition.class, Scope.REPORT);
		for (SummaryItemDefinition summaryItemDefinition : list) {
			printValue(row, summaryItemDefinition, "");
		}
		out.println("------------------------------------------------------------------------------------------");
	}

	<ROW, SUMARY extends Summary<ROW>> void printValue(SummaryRow<ROW, SUMARY> row, SummaryItemDefinition summaryItemDefinition, String padding) {
		out.print(padding);
		out.print(summaryItemDefinition.getName() + "=");
		Object value = summaryItemDefinition.getValue(row.getSummary());
		out.println(value);
	}

	<ROW, SUMARY extends Summary<ROW>> List<SummaryItemDefinition> filterItens(SummaryRow<ROW, SUMARY> row, Class<? extends SummaryItemDefinition> classe, Scope scope) {
		//a classe passada deve ser do compiledsummary, pegar a classe superior
		Class<?> summaryClass = row.getSummary().getClass().getSuperclass();
		@SuppressWarnings("all")
		SummaryDefinition<? extends Summary<ROW>> summaryDefinition = new SummaryDefinition(summaryClass);
		List<SummaryItemDefinition> list = new ArrayList<SummaryItemDefinition>();
		List<SummaryItemDefinition> itens = summaryDefinition.getItens();
		for (SummaryItemDefinition summaryItemDefinition : itens) {
			if (classe.isAssignableFrom(summaryItemDefinition.getClass()) && summaryItemDefinition.getScope() == scope) {
				list.add(summaryItemDefinition);
			}
		}
		return list;
	}

}
