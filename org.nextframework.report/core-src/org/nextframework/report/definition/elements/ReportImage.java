package org.nextframework.report.definition.elements;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ReportImage extends ReportItem {

	private ByteArrayInputStream image;
	private int height;
	private String reference;

	public ReportImage(byte[] image, int width, int height) {
		this(new ByteArrayInputStream(image), width, height);
	}

	public ReportImage(ByteArrayInputStream image, int width, int height) {
		this.image = image;
		this.width = width;
		this.height = height;
	}

	public ReportImage(String fieldReference, int width, int height) {
		this.reference = fieldReference;
		this.width = width;
		this.height = height;
	}

	/**
	 * Gets the reference. Only avaiable if the image is not rendered
	 * @return
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * Returns if the reference is a field reference. Otherwise it is a parameter reference.
	 * The reference is a parameter reference when the reference starts with "param."
	 * @return
	 */
	public boolean isFieldReference() {
		return reference != null && !reference.startsWith("param.");
	}

	/**
	 * Get the rendered image input stream. Only available if the image is rendered
	 * @return
	 */
	public InputStream getInputStream() {
		return image;
	}

	/**
	 * Gets the image height
	 * @return
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Gets the image width
	 */
	public int getWidth() {
		return super.getWidth();
	}

	/**
	 * Determines if this ReportImage already have the bytes set.
	 * If the bytes are not set this image should be rendered reading a field or variable expression.
	 * @return
	 */
	public boolean isRendered() {
		return image != null;
	}

	@Override
	public String getDescriptionName() {
		return "Image";
	}

}
