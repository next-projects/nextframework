package org.nextframework.chart;

public class ChartTimeData extends ChartData {

	private static final long serialVersionUID = -1950564415039497533L;

	public static enum TimeInterval {
		DAY,
		MONTH,
		YEAR
	}

	private TimeInterval timeInterval = TimeInterval.DAY;

	public TimeInterval getTimeInterval() {
		return timeInterval;
	}

	public void setTimeInterval(TimeInterval timeInterval) {
		this.timeInterval = timeInterval;
	}

}
