package google.maps;

import org.stjs.javascript.annotation.SyntheticType;

@SyntheticType
public class InfoWindowOptions {

	/**
	 * string|Node	Content to display in the InfoWindow. This can be an HTML element, a plain-text string, or a string containing HTML. The InfoWindow will be sized according to the content. To set an explicit size for the content, set content to be a HTML element with that size.
	 */
	public Object content;

	/**
	 * Disable auto-pan on open. By default, the info window will pan the map so that it is fully visible when it opens.
	 */
	public boolean disableAutoPan;

	/**
	 * Maximum width of the infowindow, regardless of content's width. This value is only considered if it is set before a call to open. To change the maximum width when changing content, call close, setOptions, and then open.
	 */
	public Integer maxWidth;

	/**
	 * The LatLng at which to display this InfoWindow. If the InfoWindow is opened with an anchor, the anchor's position will be used instead.
	 */
	public LatLng position;

	public Integer zIndex;

}
