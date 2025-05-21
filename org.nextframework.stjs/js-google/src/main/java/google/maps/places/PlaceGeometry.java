package google.maps.places;

import google.maps.LatLng;
import google.maps.LatLngBounds;

public class PlaceGeometry {

	/**
	 * 	LatLng	The Place's position.
	 */
	public LatLng location;

	/**
	 * The preferred viewport when displaying this Place on a map. This property will be null if the preferred viewport for the Place is not known.
	 */
	public LatLngBounds viewport;

}
