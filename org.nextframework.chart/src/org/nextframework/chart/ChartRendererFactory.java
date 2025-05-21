package org.nextframework.chart;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.nextframework.service.ServiceException;
import org.nextframework.service.ServiceFactory;
import org.nextframework.view.chart.jfree.ChartRendererJFreeChart;

public class ChartRendererFactory {

	private static List<ChartRenderer> renderers = new ArrayList<ChartRenderer>();

	public static void registerRenderer(ChartRenderer renderer) {
		for (Iterator<ChartRenderer> iterator = renderers.iterator(); iterator.hasNext();) {
			ChartRenderer chartRenderer = iterator.next();
			if (chartRenderer.getOutputType().equals(renderer.getOutputType())) {
				iterator.remove();
				break;
			}
		}
		renderers.add(renderer);
	}

	public static ChartRenderer getRendererForOutput(String outputType) {
		ChartRendererListener listener = null;
		try {
			listener = ServiceFactory.getService(ChartRendererListener.class);
		} catch (ServiceException e) {
		}
		for (ChartRenderer renderer : renderers) {
			if (renderer.getOutputType().equals(outputType)) {
				if (listener != null) {
					return new ChartRendererWithListener(listener, renderer);
				}
				return renderer;
			}
		}
		return null;
	}

	static {
		registerRenderer(new org.nextframework.chart.google.ChartRendererGoogleTools());
		registerRenderer(new ChartRendererJFreeChart());
	}

}
