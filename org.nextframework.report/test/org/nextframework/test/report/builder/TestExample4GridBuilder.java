package org.nextframework.test.report.builder;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.nextframework.report.definition.builder.LayoutReportBuilder;
import org.nextframework.report.definition.elements.SubreportTable;
import org.nextframework.report.definition.elements.TableInformationAdaptor;

public class TestExample4GridBuilder extends LayoutReportBuilder {

	Set<TestExample4PhaseBean> phases;
	List<TestExample4MainBean> gridData;

	@Override
	protected void layoutReport() {
		setTitle("Test Example 4 Grid");

		column()
				.detail(new SubreportTable(new TableInformationAdaptor() {

					@Override
					public Collection<?> getRowGroupDataSet() {
						return gridData;
					}

					@Override
					public Collection<?> getColumnHeaderDataSet() {
						return phases;
					}

					@Override
					public Object getValue(Object row, Object header, int columnIndex) {
						TestExample4MainBean main = (TestExample4MainBean) row;
						TestExample4PhaseBean phase = (TestExample4PhaseBean) header;
						List<TestExample4ItemBean> items = main.getItems();
						for (TestExample4ItemBean testExample4ItemBean : items) {
							if (phase.equals(testExample4ItemBean.getPhase())) {
								return testExample4ItemBean.getInfo();
							}
						}
						return null;
					}

				}));
	}

	public void setPhases(Set<TestExample4PhaseBean> phases) {
		this.phases = phases;
	}

	public void setGridData(List<TestExample4MainBean> data) {
		this.gridData = data;

	}

}
