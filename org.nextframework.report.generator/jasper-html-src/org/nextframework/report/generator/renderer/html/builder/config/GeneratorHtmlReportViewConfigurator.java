package org.nextframework.report.generator.renderer.html.builder.config;

import org.nextframework.core.config.ViewConfig;
import org.nextframework.report.definition.ReportDefinition;
import org.nextframework.report.definition.ReportSection;
import org.nextframework.report.definition.ReportSectionType;
import org.nextframework.report.generator.layout.DynamicReportLayoutBuilder.ReportCycle;
import org.nextframework.report.renderer.html.builder.PrintElement;
import org.nextframework.report.renderer.html.builder.config.DefaultHtmlReportViewConfigurator;
import org.nextframework.report.renderer.html.design.HtmlTag;
import org.nextframework.service.ServiceFactory;
import org.nextframework.view.DataGridTag;

public class GeneratorHtmlReportViewConfigurator extends DefaultHtmlReportViewConfigurator {

	private ViewConfig viewConfig = ServiceFactory.getService(ViewConfig.class);

	public void configureTable(ReportDefinition definition, HtmlTag containerTag, HtmlTag table) {

		containerTag.getStyleClass().add(viewConfig.getDefaultStyleClass(DataGridTag.class, "containerStyleClass"));

		ReportCycle cycle = (ReportCycle) definition.getParameters().get(ReportCycle.class.getSimpleName());
		if (cycle != null && cycle == ReportCycle.BASE_CONFIG || cycle == ReportCycle.CHARTS) {
			table.getStyleClass().add(viewConfig.getDefaultStyleClass(DataGridTag.class, "baseReportStyleClass"));
		} else {
			table.getStyleClass().add(viewConfig.getDefaultStyleClass(DataGridTag.class, "styleClass"));
		}

	}

	public void configureTr(ReportSection trSection, HtmlTag tr, String styleClass, boolean isFirstRowOfSection, boolean isFirstRowOfBlock) {

		if (trSection.getType() == ReportSectionType.DETAIL_HEADER) {
			tr.getStyleClass().add(viewConfig.getDefaultStyleClass(DataGridTag.class, "headerStyleClass"));
		} else if (trSection.getType() == ReportSectionType.GROUP_HEADER) {
			tr.getStyleClass().add(getGroupStyleClassLevel(trSection));
		} else if (trSection.getType() == ReportSectionType.SUMARY_DATA_HEADER) {
			tr.getStyleClass().add(viewConfig.getDefaultStyleClass(DataGridTag.class, "headerStyleClass"));
		}

		tr.getStyleClass().add("SEC_" + trSection.getType().toString());

		if (styleClass != null && !styleClass.startsWith("group") && !styleClass.startsWith("detail") && !styleClass.startsWith("summary")) {
			tr.getStyleClass().add(styleClass);
		}

	}

	private String getGroupStyleClassLevel(ReportSection trSection) {
		Integer index = (Integer) trSection.getRenderParameters().get("index");
		index = index != null ? index : 0;
		String groupStyleClasses = viewConfig.getDefaultStyleClass(DataGridTag.class, "groupStyleClasses");
		if (groupStyleClasses == null) {
			return "";
		}
		String[] split = groupStyleClasses.split("(\\s*)?,(\\s*)?");
		return split[Math.min(split.length - 1, index)];
	}

	public void configureTd(PrintElement element, HtmlTag td) {

	}

	public void configureTextTag(HtmlTag tag) {
		tag.getStyle().put("display", "unset");
	}

	public void configureFrameTag(HtmlTag tag) {

	}

	public void configureImageTag(HtmlTag tag) {

	}

}
