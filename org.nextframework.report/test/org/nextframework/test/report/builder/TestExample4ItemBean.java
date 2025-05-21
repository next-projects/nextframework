package org.nextframework.test.report.builder;

public class TestExample4ItemBean {

	TestExample4PhaseBean phase;

	String info;

	public TestExample4ItemBean(TestExample4PhaseBean phase, String info) {
		this.phase = phase;
		this.info = info;
	}

	public TestExample4PhaseBean getPhase() {
		return phase;
	}

	public String getInfo() {
		return info;
	}

	public void setPhase(TestExample4PhaseBean phase) {
		this.phase = phase;
	}

	public void setInfo(String info) {
		this.info = info;
	}

}
