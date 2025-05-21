package google.maps;

import org.stjs.javascript.Array;
import org.stjs.javascript.annotation.SyntheticType;

@SyntheticType
public class DirectionsRequest {

	/**
	 * 	If true, instructs the Directions service to avoid highways where possible. Optional.
	 */
	public Boolean avoidHighways;

	/**
	 * If true, instructs the Directions service to avoid toll roads where possible. Optional.
	 */
	public Boolean avoidTolls;

	/**
	 * Location of destination. This can be specified as either a string to be geocoded or a LatLng. Required.
	 */
	public LatLng destination;

	/**
	 * Whether or not we should provide trip duration based on current traffic conditions. Only available to Maps API for Business customers.
	 */
	public Boolean durationInTraffic;

	/**
	 * If set to true, the DirectionService will attempt to re-order the supplied intermediate waypoints to minimize overall cost of the route. 
	 * If waypoints are optimized, inspect DirectionsRoute.waypoint_order in the response to determine the new ordering.
	 */
	public Boolean optimizeWaypoints;

	/**
	 * Location of origin. This can be specified as either a string to be geocoded or a LatLng. Required.
	 */
	public LatLng origin;

	/**
	 * Whether or not route alternatives should be provided. Optional.
	 */
	public Boolean provideRouteAlternatives;

	/**
	 * Region code used as a bias for geocoding requests. Optional.
	 */
	public String region;

	/**
	 * Settings that apply only to requests where travelMode is TRANSIT. This object will have no effect for other travel modes.
	 */
	public TransitOptions transitOptions;

	/**
	 * Type of routing requested. Required.
	 */
	public TravelMode travelMode;

	/**
	 * Preferred unit system to use when displaying distance. Defaults to the unit system used in the country of origin.
	 */
	public UnitSystem unitSystem;

	/**
	 * Array of intermediate waypoints. Directions will be calculated from the origin to the destination by way of each waypoint in this array. The maximum allowed waypoints is 8, plus the origin, and destination. Maps API for Business customers are allowed 23 waypoints, plus the origin, and destination. Waypoints are not supported for transit directions. Optional.
	 */
	public Array<DirectionsWaypoint> waypoints;

}
