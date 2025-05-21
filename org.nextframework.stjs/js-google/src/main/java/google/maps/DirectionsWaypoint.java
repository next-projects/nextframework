package google.maps;

import org.stjs.javascript.annotation.SyntheticType;

@SyntheticType
public class DirectionsWaypoint {

	/**
	 * Waypoint location. Can be an address string or LatLng. Optional.
	 */
	public LatLng location;

	/**
	 * If true, indicates that this waypoint is a stop between the origin and destination. 
	 * This has the effect of splitting the route into two. This value is true by default. Optional.
	 */
	public boolean stopover;

}
