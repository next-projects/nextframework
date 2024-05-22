package org.nextframework.view.chart.jfree;

import java.awt.Color;
import java.awt.Paint;

import org.jfree.chart.plot.DefaultDrawingSupplier;

public class JFreeDrawingSupplier extends DefaultDrawingSupplier {

	private static final long serialVersionUID = 1L;

	public static final Paint[] DEFAULT_PAINT_SEQUENCE = createDefaultPaintArray();

	public static final Paint[] DEFAULT_OUTLINE_PAINT_SEQUENCE = new Paint[] { Color.white };

	public JFreeDrawingSupplier(Color[] paintSequence) {
		super(paintSequence,
				DEFAULT_FILL_PAINT_SEQUENCE,
				DEFAULT_OUTLINE_PAINT_SEQUENCE,
				DEFAULT_STROKE_SEQUENCE,
				DEFAULT_OUTLINE_STROKE_SEQUENCE,
				DEFAULT_SHAPE_SEQUENCE);
	}

	public JFreeDrawingSupplier() {
		super(DEFAULT_PAINT_SEQUENCE,
				DEFAULT_FILL_PAINT_SEQUENCE,
				DEFAULT_OUTLINE_PAINT_SEQUENCE,
				DEFAULT_STROKE_SEQUENCE,
				DEFAULT_OUTLINE_STROKE_SEQUENCE,
				DEFAULT_SHAPE_SEQUENCE);
	}

	private static Paint[] createDefaultPaintArray() {
		return new Paint[] {
				new Color(51, 102, 204),
				new Color(220, 57, 18),
				new Color(255, 153, 0),
				new Color(16, 150, 24),
				new Color(153, 0, 153),
				new Color(0, 153, 198),
				new Color(221, 68, 119),
				new Color(102, 170, 0),
				new Color(184, 46, 46),
				new Color(49, 99, 149),
				new Color(153, 68, 153),
				new Color(34, 170, 153),
				new Color(170, 170, 17)
		};
	}

}
