package google.maps;

import org.stjs.javascript.Array;

public class DirectionsResult {

	/**
	 * An array of DirectionsRoutes, 
	 * each of which contains information about the legs and steps of which it is composed. 
	 * There will only be one route unless the DirectionsRequest was made with 
	 * provideRouteAlternatives set to true.
	 */
	public Array<DirectionsRoute> routes;

}
