package google.maps;

import org.stjs.javascript.dom.Node;

public class DirectionsRenderer {

	public DirectionsRenderer(DirectionsRendererOptions directionsRendererOptions) {
	}

	public DirectionsRenderer() {
	}

	public DirectionsRenderer(org.stjs.javascript.Map<String, Object> params) {
	}

	/**
	 * Returns the renderer's current set of directions.
	 */
	public DirectionsResult getDirections() {
		return null;
	}

	/**
	 * Returns the map on which the DirectionsResult is rendered.
	 * @return
	 */
	public Map getMap() {
		return null;
	}

	/**
	 * 		Returns the panel <div> in which the DirectionsResult is rendered.
	 * @return
	 */
	public Node getPanel() {
		return null;
	}

	/**
	 * None	Set the renderer to use the result from the DirectionsService. Setting a valid set of directions in this manner will display the directions on the renderer's designated map and panel.
	 * @param directions
	 */
	public void setDirections(DirectionsResult directions) {
	}

	/**
	 * This method specifies the map on which directions will be rendered. Pass null to remove the directions from the map.
	 */
	public void setMap(Map map) {
	}

	/**
	 * Change the options settings of this DirectionsRenderer after initialization. 
	 */
	public void setOptions(DirectionsRendererOptions options) {
	}
//	getRouteIndex()	number	Returns the current (zero-based) route index in use by this DirectionsRenderer object.
//	setPanel(panel:Node)	None	This method renders the directions in a <div>. Pass null to remove the content from the panel.
//	setRouteIndex(routeIndex:number)	None	Set the (zero-based) index of the route in the DirectionsResult object to render. By default, the first route in the array will be rendered.

}
