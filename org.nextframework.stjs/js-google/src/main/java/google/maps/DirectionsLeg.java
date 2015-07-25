package google.maps;

import org.stjs.javascript.Array;

public class DirectionsLeg {

	/**
	 * An estimated arrival time for this leg. Only applicable for TRANSIT requests.
	 */
	public Time arrival_time;	
	/**
	 * 	An estimated departure time for this leg. Only applicable for TRANSIT requests.
	 */
	public Time departure_time;
	/**
	 * The total distance covered by this leg. This property may be undefined as the distance may be unknown.
	 */
	public Distance distance;
	/**
	 * The total duration of this leg. This property may be undefined as the duration may be unknown.
	 */
	public Duration duration;
	/**
	 * The total duration of this leg, taking into account current traffic conditions. This property may be undefined as the duration may be unknown. Only available to Maps API for Business customers when durationInTraffic is set to true when making the request.
	 */
	public Duration duration_in_traffic;
	/**
	 * The address of the destination of this leg.
	 */
	public String end_address;
	/**
	 * The DirectionsService calculates directions between locations by using the nearest transportation option (usually a road) at the start and end locations. end_location indicates the actual geocoded destination, which may be different than the end_location of the last step if, for example, the road is not near the destination of this leg.
	 */
	public LatLng end_location;
	/**
	 * The address of the origin of this leg.
	 */
	public String start_address;	
	/**
	 * The DirectionsService calculates directions between locations by using the nearest transportation option (usually a road) at the start and end locations. start_location indicates the actual geocoded origin, which may be different than the start_location of the first step if, for example, the road is not near the origin of this leg.
	 */
	public LatLng start_location;
	/**
	 * 	An array of DirectionsSteps, each of which contains information about the individual steps in this leg.
	 */
	public Array<DirectionsStep> steps;
	/**
	 * 	An array of waypoints along this leg that were not specified in the original request, either as a result of a user dragging the polyline or selecting an alternate route.
	 */
	public Array<LatLng> via_waypoints;
}
