package google.maps;

import org.stjs.javascript.Array;

public class DirectionsStep {

	/**
	 * The total distance covered by this leg. This property may be undefined as the distance may be unknown.
	 */
	public Distance distance;

	/**
	 * The total duration of this leg. This property may be undefined as the duration may be unknown.
	 */
	public Duration duration;

	/**
	 * The DirectionsService calculates directions between locations by using the nearest transportation option (usually a road) at the start and end locations. end_location indicates the actual geocoded destination, which may be different than the end_location of the last step if, for example, the road is not near the destination of this leg.
	 */
	public LatLng end_location;

	/**
	 * The DirectionsService calculates directions between locations by using the nearest transportation option (usually a road) at the start and end locations. start_location indicates the actual geocoded origin, which may be different than the start_location of the first step if, for example, the road is not near the origin of this leg.
	 */
	public LatLng start_location;

	public String encoded_lat_lngs;

	/**
	 * 	An array of waypoints along this leg that were not specified in the original request, either as a result of a user dragging the polyline or selecting an alternate route.
	 */
	public Array<LatLng> path;

}
