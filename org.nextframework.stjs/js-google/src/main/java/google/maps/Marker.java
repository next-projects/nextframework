package google.maps;

public class Marker {

	private LatLng position;

	public Marker(MarkerOptions markerParameters) {
	}

	public LatLng getPosition() {
		return position;
	}

	public void setPosition(LatLng position) {
		this.position = position;
	}

	public boolean getClickable() {
		return false;
	}

	public String getCursor() {
		return null;
	}

	public boolean getDraggable() {
		return false;
	}

	public boolean getFlat() {
		return false;
	}

	public Map getMap() { // can return StreetViewPanorama
		return null;
	} 

	public String getTitle() {
		return null;
	}

	public boolean getVisible() {
		return false;
	}

	public Integer getZIndex() {
		return null;
	}
	
	public void setMap(Map map){
		
	}

	public void setClickable(boolean flag) {
	}

	public void setCursor(String cursor) {
	}

	public void setDraggable(boolean flag) {
	}

	public void setFlat(boolean flag) {
	}

	public void setTitle(String title) {
	}

	public void setVisible(boolean visible) {
	}

	public void setZIndex(Integer zIndex) {
	}
	
	public	void	setIcon(Icon icon)	{
		
	}
	public	void	setIcon(String icon)	{
		
	}
	public	void	setIcon(org.stjs.javascript.Map<String, ? extends Object> icon)	{
		
	}
	
//	public	Animation	getAnimation()	{}
//	public	string|Icon|Symbol	getIcon()	{}
//	public	string|Icon|Symbol	getShadow()	{}
//	public	MarkerShape	getShape()	{}
//	public	void	setAnimation(animation:Animation)	{}
//	public	void	setMap(map:Map|StreetViewPanorama)	{}
//	public	void	setOptions(options:MarkerOptions)	{}
//	public	void	setShadow(shadow:string|Icon|Symbol)	{}
//	public	void	setShape(shape:MarkerShape)	{}

}
