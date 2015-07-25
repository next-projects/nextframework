package org.nextframework.test.report.builder;

import java.util.Set;

import org.nextframework.report.definition.builder.LayoutReportBuilder;

public class TestExample4Builder extends LayoutReportBuilder {

	Set<TestExample4PhaseBean> phases;
	
	@Override
	protected void layoutReport() {
		setTitle("Test Example 4");
		
		fieldDetail("name");
		
		for (TestExample4PhaseBean phase : phases) {
			fieldDetail("mapPhase["+phase.getName()+"]", phase.getName());
		}
	}

	@Override
	protected Class<?> getRowClass() {
		return TestExample4MainBean.class;
	}

	public void setPhases(Set<TestExample4PhaseBean> phases) {
		this.phases = phases;
	}
}
