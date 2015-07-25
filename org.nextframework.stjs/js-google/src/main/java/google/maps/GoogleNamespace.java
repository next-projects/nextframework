package google.maps;

import org.stjs.javascript.Array;
import org.stjs.javascript.annotation.GlobalScope;
import org.stjs.javascript.functions.Callback0;
import org.stjs.javascript.functions.Callback1;

@GlobalScope
public class GoogleNamespace {

	public static GoogleNamespace google;
	
	public GoogleMapsNamespace maps;
	
	public abstract class GoogleMapsNamespace {
		
		public EventNamespace event;
		public GoogleMapsGeometryNamespace geometry;
		
	}
	
	public abstract class GoogleMapsGeometryNamespace {
		
		public GoogleMapsGeometrySphericalNamespace spherical;
		public GoogleMapsGeometryEncodingNamespace encoding;
		
	}
	public abstract class GoogleMapsGeometryEncodingNamespace {
		
		/**
		 * Decodes an encoded path string into a sequence of LatLngs.
		 * @return
		 */
		public abstract Array<LatLng> decodePath(String encodedPath);
		/**
		 * Encodes a sequence of LatLngs into an encoded path string.
		 */
		public abstract String encodePath(Array<LatLng> path);
//		public void encodePath(MVCArray<LatLng> path);
		
	}
	public abstract class GoogleMapsGeometrySphericalNamespace {
		
		public abstract double computeDistanceBetween(LatLng from, LatLng to, Double radius);
		public abstract double computeDistanceBetween(LatLng from, LatLng to);
		
	}
	
	public abstract class EventNamespace {
		public abstract MapsEventListener addListener(Object instance, String eventName, Callback0 callback0);
		public abstract MapsEventListener addListener(Object instance, String eventName, Callback1<?> callback0);
		public abstract void trigger(Object instance, String eventName, Array<?> args);
		public abstract void trigger(Object instance, String eventName);
//		addDomListener(instance:Object, eventName:string, handler:Function, capture?:boolean)	MapsEventListener	Cross browser event handler registration. This listener is removed by calling removeListener(handle) for the handle that is returned by this function.
//		addDomListenerOnce(instance:Object, eventName:string, handler:Function, capture?:boolean)	MapsEventListener	Wrapper around addDomListener that removes the listener after the first event.
//		addListener(instance:Object, eventName:string, handler:Function)	MapsEventListener	Adds the given listener function to the given event name for the given object instance. Returns an identifier for this listener that can be used with removeListener().
//		addListenerOnce(instance:Object, eventName:string, handler:Function)	MapsEventListener	Like addListener, but the handler removes itself after handling the first event.
//		clearInstanceListeners(instance:Object)	None	Removes all listeners for all events for the given instance.
//		clearListeners(instance:Object, eventName:string)	None	Removes all listeners for the given event for the given instance.
//		removeListener(listener:MapsEventListener)	None	Removes the given listener, which should have been returned by addListener above.
//		trigger(instance:Object, eventName:string, var_args:*)	None	Triggers the given event. All arguments after eventName are passed as arguments to the listeners.
	}
}
