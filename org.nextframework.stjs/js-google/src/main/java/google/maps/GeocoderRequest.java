package google.maps;

import org.stjs.javascript.annotation.SyntheticType;

@SyntheticType
public class GeocoderRequest {

	/**
	 * Address. Optional.
	 */
	public String address;

	/**
	 * LatLngBounds within which to search. Optional.
	 */
	public LatLngBounds bounds;

	/**
	 * LatLng about which to search. Optional.
	 */
	public LatLng location;//	|LatLngLiteral

	/**
	 * Country code used to bias the search, specified as a Unicode region subtag / CLDR identifier. Optional.
	 */
	public String region;

}
