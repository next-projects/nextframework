package google.maps;

public class InfoWindow {

	public InfoWindow(InfoWindowOptions opts) {

	}

	/**
	 * Closes this InfoWindow by removing it from the DOM structure.
	 */
	public void close() {
	}

	/**
	 * Opens this InfoWindow on the given map. Optionally, an InfoWindow can be associated with an anchor. In the core API, the only anchor is the Marker class. However, an anchor can be any MVCObject that exposes a LatLng position property and optionally a Point anchorPoint property for calculating the pixelOffset (see InfoWindowOptions). The anchorPoint is the offset from the anchor's position to the tip of the InfoWindow.
	 */
	public void open(Map map, Object anchor) {

	}

	/**
	 * Opens this InfoWindow on the given map. Optionally, an InfoWindow can be associated with an anchor. In the core API, the only anchor is the Marker class. However, an anchor can be any MVCObject that exposes a LatLng position property and optionally a Point anchorPoint property for calculating the pixelOffset (see InfoWindowOptions). The anchorPoint is the offset from the anchor's position to the tip of the InfoWindow.
	 */
	public void open(Map map, Marker anchor) {

	}

	/**
	 * Opens this InfoWindow on the given map. Optionally, an InfoWindow can be associated with an anchor. In the core API, the only anchor is the Marker class. However, an anchor can be any MVCObject that exposes a LatLng position property and optionally a Point anchorPoint property for calculating the pixelOffset (see InfoWindowOptions). The anchorPoint is the offset from the anchor's position to the tip of the InfoWindow.
	 */
	public void open(Map map) {

	}

//	setContent(content:string|Node)	None	
//	setOptions(options:InfoWindowOptions)	None	
//	setPosition(position:LatLng)	None	
//	setZIndex(zIndex:number)	None

}
