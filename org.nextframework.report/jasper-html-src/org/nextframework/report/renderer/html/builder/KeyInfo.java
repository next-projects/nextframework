package org.nextframework.report.renderer.html.builder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nextframework.report.definition.ReportSectionType;
import org.nextframework.report.renderer.jasper.builder.JasperDesignBuilder;

public class KeyInfo {

	Pattern pattern1 = Pattern.compile("(\\w*) (\\d*) C\\[(\\d*)\\,span=(\\d*)] R\\[(\\w*),(\\d*)\\] W\\[([-0-9]*)\\]");
	Pattern pattern2 = Pattern.compile("(\\w*) (\\d*) C\\[\\] R\\[\\] W\\[([-0-9]*)\\]");

	String className;
	int index;
	int column;
	int colspan;
	ReportSectionType reportSectionType;
	int rowIndex;
	int width;

	public KeyInfo(String key) {
		if (key != null && key.startsWith(JasperDesignBuilder.BACKGROUND_FRAME_KEY)) {
			column = -1;
			colspan = -1;
			rowIndex = -1;
			width = -1;
			return;
		}
		Matcher matcher = pattern1.matcher(key);
		if (matcher.matches()) {
			className = matcher.group(1);
			index = Integer.parseInt(matcher.group(2));
			column = Integer.parseInt(matcher.group(3));
			colspan = Integer.parseInt(matcher.group(4));
			reportSectionType = ReportSectionType.valueOf(matcher.group(5));
			rowIndex = Integer.parseInt(matcher.group(6));
			width = Integer.parseInt(matcher.group(7));
		} else {
			matcher = pattern2.matcher(key);
			matcher.matches();
			className = matcher.group(1);
			index = Integer.parseInt(matcher.group(2));
			width = Integer.parseInt(matcher.group(3));
		}
	}

	public static void main(String[] args) {
		String k1 = "ReportLabel 7 C[] R[] W[-1073741824]";
		String k2 = "ReportComposite 9 C[0] R[DETAIL,3] W[-1073741824]";

		System.out.println(new KeyInfo(k1).getWidth());
		System.out.println(new KeyInfo(k2).getWidth());
	}

	public int getColspan() {
		return colspan;
	}

	public String getClassName() {
		return className;
	}

	public int getIndex() {
		return index;
	}

	public int getColumn() {
		return column;
	}

	public ReportSectionType getReportSectionType() {
		return reportSectionType;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public int getWidth() {
		return width;
	}

}
