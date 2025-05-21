package google.maps;

import org.stjs.javascript.Array;

import google.maps.places.GeocoderAddressComponent;

public class GeocoderResult {

	/**
	 * 	An array of GeocoderAddressComponents
	 */
	public Array<GeocoderAddressComponent> address_components;

	/**
	 * A string containing the human-readable address of this location.
	 */
	public String formatted_address;

	/**
	 * 	A GeocoderGeometry object
	 */
	public GeocoderGeometry geometry;

	/**
	 * Whether the geocoder did not return an exact match for the original request, though it was able to match part of the requested address.
	 */
	public boolean partial_match;

	/**
	 * An array of strings denoting all the localities contained in a postal code. This is only present when the result is a postal code that contains multiple localities.
	 */
	public Array<String> postcode_localities;

	/**
	 * An array of strings denoting the type of the returned geocoded element. For a list of possible strings, refer to the Address Component Types section of the Developer's Guide.
	 */
	public Array<String> types;

}
