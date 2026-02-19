package org.nextframework.view.chart.jfree;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.nextframework.chart.Chart;
import org.nextframework.chart.ChartRenderer;
import org.nextframework.chart.ChartRendererFactory;
import org.nextframework.chart.ChartType;
import org.nextframework.chart.google.ChartRendererGoogleTools;
import org.nextframework.exception.NextException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ChartRendererJFreeChart implements ChartRenderer {

	public static String TYPE = "JFREECHART";

	static java.util.Map<ChartType, JFreeTypeRenderer> chartMapping;

	static {
		StandardBarPainter painter = new StandardBarPainter();
		BarRenderer.setDefaultBarPainter(painter);
		BarRenderer.setDefaultShadowsVisible(false);
		chartMapping = new HashMap<ChartType, JFreeTypeRenderer>();
		chartMapping.put(ChartType.PIE, new JFreePieRenderer());
		chartMapping.put(ChartType.BAR, new JFreeBarRenderer());
		chartMapping.put(ChartType.COLUMN, new JFreeBarRenderer());
		chartMapping.put(ChartType.AREA, new JFreeBarRenderer());
		chartMapping.put(ChartType.LINE, new JFreeBarRenderer());
		chartMapping.put(ChartType.SCATTER, new JFreeBarRenderer());
		chartMapping.put(ChartType.CURVED_LINE, new JFreeCurvedLineRenderer());
		chartMapping.put(ChartType.COMBO, new JFreeComboRenderer());
	}

	public static JFreeChart renderAsJFreeChart(Chart chart) {
		return (JFreeChart) ChartRendererFactory.getRendererForOutput(TYPE).renderChart(chart);
	}

	public static byte[] renderAsSVG(Chart chart) {
		try {
			int width = Integer.parseInt(chart.getStyle().getWidth());
			int height = Integer.parseInt(chart.getStyle().getHeight());
			return convertChartToSVG(renderAsJFreeChart(chart), width, height);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Format PNG
	 * @param chart
	 * @return
	 */
	public static byte[] renderAsImage(Chart chart) {
		try {
			int width = Integer.parseInt(chart.getStyle().getWidth());
			int height = Integer.parseInt(chart.getStyle().getHeight());
			return convertChartToByteArray(chart, renderAsJFreeChart(chart), width, height);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

//	public static Resource renderAsResource(Chart chart){
//		return convertChartToResourse(chart, renderAsJFreeChart(chart), chart.getStyle().getWidth(), chart.getStyle().getHeight(), "chart");
//	}
//
//	public static Resource convertChartToResourse(JFreeChart chart, int width, int height, String fileName) {
//		return convertChartToResourse(null, chart, width, height, fileName);
//	}

	/**
	 * Exports a JFreeChart to a SVG file.
	 * 
	 * @param chart JFreeChart to export
	 * @param bounds the dimensions of the viewport
	 * @param svgFile the output file.
	 * @throws IOException if writing the svgFile fails.
	 */
	public static byte[] convertChartToSVG(JFreeChart chart, int width, int height) {

		// Get a DOMImplementation and create an XML document
		DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
		Document document = domImpl.createDocument(null, "svg", null);

		// Create an instance of the SVG Generator
		SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

		// draw the chart in the SVG generator
		chart.draw(svgGenerator, new Rectangle(width, height));

		// Write svg file
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Writer out = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
			svgGenerator.stream(out, true /* use css */);
			out.flush();
			out.close();
			return baos.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

//	public static Resource convertChartToResourse(Chart chartDetails, JFreeChart chart, int width, int height, String fileName) {
//		Resource resource = null;
//		
//        try {
//        	byte[] bytes = convertChartToByteArray(chartDetails, chart, width, height);
//			//resource = new Resource("image/png", fileName+".png", baos.toByteArray());
//			resource = new Resource("image/png", fileName+".png", bytes);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return resource;
//	}

	private static byte[] convertChartToByteArray(Chart chartDetails, JFreeChart chart, int width, int height) throws IOException, IIOInvalidTreeException {

		BufferedImage buffImg = chart.createBufferedImage(width, height, BufferedImage.BITMASK | BufferedImage.SCALE_SMOOTH, null);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageWriter writer = ImageIO.getImageWritersByFormatName("png").next();

		ImageWriteParam writeParam = writer.getDefaultWriteParam();
		ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
		IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);

		if (chartDetails != null) {
			if (chartDetails.getId() == null) {
				chartDetails.setId("%ID%");
			}
			IIOMetadataNode textEntry = new IIOMetadataNode("tEXtEntry");
			textEntry.setAttribute("keyword", "chart-google-data");
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(bout);
			objectOutputStream.writeObject(chartDetails);
			objectOutputStream.flush();
			textEntry.setAttribute("value", (String) ChartRendererFactory.getRendererForOutput(ChartRendererGoogleTools.TYPE).renderChart(chartDetails));

			IIOMetadataNode text = new IIOMetadataNode("tEXt");
			text.appendChild(textEntry);

			IIOMetadataNode root = new IIOMetadataNode("javax_imageio_png_1.0");
			root.appendChild(text);

			metadata.mergeTree("javax_imageio_png_1.0", root);

		}
		//setDPI(metadata);

		final ImageOutputStream stream = ImageIO.createImageOutputStream(baos);
		try {
			writer.setOutput(stream);
			writer.write(metadata, new IIOImage(buffImg, null, metadata), writeParam);
		} finally {
			stream.close();
		}

		byte[] bytes = baos.toByteArray();//ChartUtilities.encodeAsPNG(buffImg, true, 9);
		return bytes;
	}

	@SuppressWarnings("unused")
	private static void setDPI(IIOMetadata metadata) throws IIOInvalidTreeException {

		// for PNG, it's dots per millimeter
		double dotsPerMilli = 1.0 * 600 / 10 / 2.54;

		IIOMetadataNode horiz = new IIOMetadataNode("HorizontalPixelSize");
		horiz.setAttribute("value", Double.toString(dotsPerMilli));

		IIOMetadataNode vert = new IIOMetadataNode("VerticalPixelSize");
		vert.setAttribute("value", Double.toString(dotsPerMilli));

		IIOMetadataNode dim = new IIOMetadataNode("Dimension");
		dim.appendChild(horiz);
		dim.appendChild(vert);

		IIOMetadataNode root = new IIOMetadataNode("javax_imageio_1.0");
		root.appendChild(dim);

		metadata.mergeTree("javax_imageio_1.0", root);
	}

	@SuppressWarnings("unused")
	private boolean saveJpeg(int[] byteArray, int width, int height, int dpi, String file) {

		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		WritableRaster wr = bufferedImage.getRaster();
		wr.setPixels(0, 0, width, height, byteArray);

		try {
			// Image writer 
			ImageWriter imageWriter = ImageIO.getImageWritersBySuffix("jpeg").next();
			ImageOutputStream ios = ImageIO.createImageOutputStream(new File(file));
			imageWriter.setOutput(ios);

			// Compression
			JPEGImageWriteParam jpegParams = (JPEGImageWriteParam) imageWriter.getDefaultWriteParam();
			jpegParams.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
			jpegParams.setCompressionQuality(0.85f);

			// Metadata (dpi)
			IIOMetadata data = imageWriter.getDefaultImageMetadata(new ImageTypeSpecifier(bufferedImage), jpegParams);
			Element tree = (Element) data.getAsTree("javax_imageio_jpeg_image_1.0");
			Element jfif = (Element) tree.getElementsByTagName("app0JFIF").item(0);
			jfif.setAttribute("Xdensity", Integer.toString(dpi));
			jfif.setAttribute("Ydensity", Integer.toString(dpi));
			jfif.setAttribute("resUnits", "1"); // density is dots per inch                 

			// Write and clean up
			imageWriter.write(data, new IIOImage(bufferedImage, null, null), jpegParams);
			ios.close();
			imageWriter.dispose();
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	public String getOutputType() {
		return TYPE;
	}

	public JFreeChart renderChart(Chart chart) {

		JFreeTypeRenderer jFreeTypeRenderer = chartMapping.get(chart.getStyle().getChartType());

		if (jFreeTypeRenderer == null) {
			throw new NextException("Não é possível renderizar o gráfico do tipo especificado " + chart.getChartType());
		}

		JFreeChart chartRendered = jFreeTypeRenderer.render(chart);

		//return convertChartToResourse(chartRendered, chart.getStyle().getWidth(), chart.getStyle().getHeight(), "chart.png");
		return chartRendered;
	}

}
