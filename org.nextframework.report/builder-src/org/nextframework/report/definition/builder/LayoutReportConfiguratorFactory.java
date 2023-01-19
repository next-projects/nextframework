package org.nextframework.report.definition.builder;

public class LayoutReportConfiguratorFactory {

	private static LayoutReportConfigurator configurator = new LayoutReportConfigurator();

	public static void setConfigurator(LayoutReportConfigurator configurator) {
		LayoutReportConfiguratorFactory.configurator = configurator;
	}

	public static LayoutReportConfigurator getConfigurator() {
		return configurator;
	}

}