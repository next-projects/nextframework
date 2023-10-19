package google.maps.places;

public class PlaceResult {

	/**
	 * The collection of address components for this Place's location.
	 */
	public org.stjs.javascript.Array<GeocoderAddressComponent> address_components;

	/**
	 * The rated aspects of this Place, based on Google and Zagat user reviews. The ratings are on a scale of 0 to 30.
	 */
	public org.stjs.javascript.Array<PlaceAspectRating> aspects;

	/**
	 * The Place's full address.
	 */
	public String formatted_address;

	/**
	 * The Place's phone number, formatted according to the number's regional convention.
	 */
	public String formatted_phone_number;

	/**
	 * The Place's geometry-related information.
	 */
	public PlaceGeometry geometry;

	/**
	 * Attribution text to be displayed for this Place result.
	 */
	public org.stjs.javascript.Array<String> html_attributions;

	/**
	 * URL to an image resource that can be used to represent this Place's category.
	 */
	public String icon;

	/**
	 * A unique identifier denoting this Place. This identifier may not be used to retrieve information about this Place, and to verify the identity of a Place across separate searches. As ids can occasionally change, it is recommended that the stored id for a Place be compared with the id returned in later Details requests for the same Place, and updated if necessary.
	 */
	public String id;

	/**
	 * The Place's phone number in international format. International format includes the country code, and is prefixed with the plus (+) sign.
	 */
	public String international_phone_number;

	/**
	 * The Place's name. Note: In the case of user entered Places, this is the raw text, as typed by the user. Please exercise caution when using this data, as malicious users may try to use it as a vector for code injection attacks (See http://en.wikipedia.org/wiki/Code_injection).
	 */
	public String name;

	/**
	 * A flag indicating whether the Place is permanently closed. If the place is not permanently closed, the flag is not present in search or details responses.
	 */
	public boolean permanently_closed;

	/**
	 * Photos of this Place. The collection will contain up to ten PlacePhoto objects.
	 */
	public org.stjs.javascript.Array<PlacePhoto> photos;

	/**
	 * The price level of the Place, on a scale of 0 to 4. Price levels are interpreted as follows:
	 */
	public Integer price_level;

	/**
	 * A rating, between 1.0 to 5.0, based on user reviews of this Place.
	 */
	public Double rating;

	/**
	 * An opaque string that may be used to retrieve up-to-date information about this Place (via PlacesService.getDetails()). reference contains a unique token that you can use to retrieve additional information about this Place in a Place Details request. You can store this token and use it at any time in future to refresh cached data about this Place, but the same token is not guaranteed to be returned for any given Place across different searches.
	 */
	public String reference;

	/**
	 * The editorial review summary. Only visible in details responses, for customers of Maps API for Business and when extensions: 'review_summary' is specified in the details request. The review_summary field is experimental, and subject to change.
	 */
	public String review_summary;

	/**
	 * A list of reviews of this Place.
	 */
	public org.stjs.javascript.Array<PlaceReview> reviews;

	/**
	 * An array of types for this Place (e.g., ["political",  "locality"] or ["restaurant", "establishment"]).
	 */
	public org.stjs.javascript.Array<String> types;

	/**
	 * URL of the associated Google Place Page.
	 */
	public String url;

	/**
	 * A fragment of the Place's address for disambiguation (usually street name and locality).
	 */
	public String vicinity;

	/**
	 * The authoritative website for this Place, such as a business' homepage.
	 */
	public String website;

}
