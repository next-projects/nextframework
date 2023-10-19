package google.maps;

import org.stjs.javascript.annotation.SyntheticType;
import org.stjs.javascript.dom.Node;

@SyntheticType
public class DirectionsRendererOptions {

	/**
	 * The directions to display on the map and/or in a <div> panel, retrieved as a DirectionsResult object from DirectionsService.
	 */
	public DirectionsResult directions;

	/**
	 * If true, allows the user to drag and modify the paths of routes rendered by this DirectionsRenderer.
	 */
	public Boolean draggable;

	/**
	 * This property indicates whether the renderer should provide UI to select amongst alternative routes. By default, this flag is false and a user-selectable list of routes will be shown in the directions' associated panel. To hide that list, set hideRouteList to true.
	 */
	public Boolean hideRouteList;

	/**
	 * The InfoWindow in which to render text information when a marker is clicked. Existing info window content will be overwritten and its position moved. If no info window is specified, the DirectionsRenderer will create and use its own info window. This property will be ignored if suppressInfoWindows is set to true.
	 */
	public InfoWindow infoWindow;

	/**
	 * Map on which to display the directions.
	 */
	public Map map;

	/**
	 * Options for the markers. All markers rendered by the DirectionsRenderer will use these options.
	 */
	public MarkerOptions markerOptions;

	/**
	 * The <div> in which to display the directions steps.
	 */
	public Node panel;

	/**
	 * Options for the polylines. All polylines rendered by the DirectionsRenderer will use these options.
	 */
	public PolylineOptions polylineOptions;

	/**
	 * By default, the input map is centered and zoomed to the bounding box of this set of directions. If this option is set to true, the viewport is left unchanged, unless the map's center and zoom were never set.
	 */
	public Boolean preserveViewport;

	/**
	 * The index of the route within the DirectionsResult object. The default value is 0.
	 */
	public Number routeIndex;

	/**
	 * Suppress the rendering of the BicyclingLayer when bicycling directions are requested.
	 */
	public Boolean suppressBicyclingLayer;

	/**
	 * Suppress the rendering of info windows.
	 */
	public Boolean suppressInfoWindows;

	/**
	 * Suppress the rendering of markers.
	 */
	public Boolean suppressMarkers;

	/**
	 * Suppress the rendering of polylines.
	 */
	public Boolean suppressPolylines;

}
