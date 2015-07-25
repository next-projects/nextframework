package google.maps;

import org.stjs.javascript.dom.Element;

public class Map /*extends MVCObject*/{

	public Map(Element mapDiv){
	}
	public Map(Element mapDiv, MapOptions opts){
	}
	
	private LatLng center;
	private Integer zoom;
	
	public LatLng getCenter() {
		return center;
	}
	public Integer getZoom() {
		return zoom;
	}
	public void setCenter(LatLng center) {
		this.center = center;
	}
	public void setZoom(Integer zoom) {
		this.zoom = zoom;
	}
	
	public void fitBounds(LatLngBounds bounds){
	}
	
	public LatLngBounds getBounds(){
		return null;
	}
	
}
