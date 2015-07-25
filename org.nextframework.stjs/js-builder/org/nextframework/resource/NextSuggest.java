package org.nextframework.resource;

import static org.nextframework.js.NextGlobalJs.next;
import static org.stjs.javascript.Global.setTimeout;
import static org.stjs.javascript.Global.window;

import org.nextframework.js.NextGlobalJs;
import org.nextframework.resource.NextSuggestSuggestionProvider.SuggestItem;
import org.stjs.javascript.Array;
import org.stjs.javascript.Global;
import org.stjs.javascript.JSCollections;
import org.stjs.javascript.Map;
import org.stjs.javascript.TimeoutHandler;
import org.stjs.javascript.dom.DOMEvent;
import org.stjs.javascript.dom.Div;
import org.stjs.javascript.dom.Element;
import org.stjs.javascript.dom.HTMLList;
import org.stjs.javascript.dom.Input;
import org.stjs.javascript.dom.LI;
import org.stjs.javascript.dom.UList;
import org.stjs.javascript.functions.Callback0;
import org.stjs.javascript.functions.Callback1;

public class NextSuggest {

	
	private static final int DEFAULT_KEY_TIMEOUT = 500;
	private static final int DEFAULT_MINIMUM_CHARS = 2;
	
	public Map<String, NextSuggestStaticListProvider> providers;
	
	public NextSuggest(){
		providers = JSCollections.$map();
	}
	
	public void install(Input el, String providerName){
		String elName = el.name.substring(0, el.name.length() - 5);
		el = (Input) Global.window.document.getElementsByName(elName).$get(0);
		if(el.getAttribute("data-suggestinstalled") != null){
			return;
		}
		el.setAttribute("data-suggestinstalled", "true");
		new SuggestElement(el, providers.$get(providerName));
	}
	
	static class SuggestElement {
		
		private Input valueInput;
		private Input textInput;

		private TimeoutManager timeoutManager;
		
		protected SuggestView suggestView;
		
		protected ItemMatcher itemMatcher;
		
		protected NextSuggestSuggestionProvider suggestionProvider;
		private Element imgOk;
		private Element imgNotOk;
		
		public SuggestElement(Object el, NextSuggestSuggestionProvider provider){
			this.valueInput = NextGlobalJs.next.dom.toElement(el);
			
			if(valueInput == null){
				throw Global.exception("null input in suggest");
			}
			String textInputName = valueInput.name+"_text";
			HTMLList<Element> elementsByName = window.document.getElementsByName(textInputName);
			if(elementsByName.length > 0){
				this.textInput = (Input) elementsByName.$get(0);
			}
			
			if(textInput == null){
				throw Global.exception("null textinput in suggest, no element with name "+textInputName+" found");
			}
			
			imgOk = next.dom.getInnerElementById(textInput.parentNode, "sg_ok");
			imgNotOk = next.dom.getInnerElementById(textInput.parentNode, "sg_notok");
			
			timeoutManager = new TimeoutManager(this);
			suggestView = new SuggestView(this);
			itemMatcher = new ItemMatcher();
			
			configureValidationEvents();
			
			this.suggestionProvider = provider;
		}
		

		private void configureValidationEvents() {
			final SuggestElement bigThis = this;
			next.events.attachEvent(textInput, "blur", new Callback1<DOMEvent>() {
				public void $invoke(DOMEvent p1) {
					Global.setTimeout(new Callback0() {
						public void $invoke() {
							bigThis.onBlur();					
						}
					}, 200);
				}
			});
		}


		protected void onBlur() {
			suggestView.hide();
			if(valueInput.value.equals("")){
				if(textInput.value.length() > 0){
					imgNotOk.style.display = "";
				} else { 
					imgNotOk.style.display = "none";
				}
			}
		}


		@Override
		public String toString() {
			return "suggest '"+valueInput.name+"'";
		}

		public String getQueryText() {
			if(textInput.value.length() >= DEFAULT_MINIMUM_CHARS){
				return textInput.value;
			} else {
				return null;
			}
		}

		public void triggerSuggestion() {
			if(textInput.value.length() == 0){
				suggestView.hide();
				return;
			}
			if(suggestionProvider == null){
				throw Global.exception("suggestionProvider is not configured. Set suggestionProvider in the SuggestElement element before using the component");
			}
			
			String text = getQueryText();
			if(text == null){
				suggestView.hide();
			} else {
				Global.console.info("requestSuggestions");
				suggestView.show();
				suggestionProvider.requestSuggestions(this);
			}
		}

		public void suggest(Array<SuggestItem> suggestions) {
			Global.console.info("suggesting");
			suggestView.setSuggestions(suggestions);
		}

		public void handleUpArrow() {
			suggestView.selectUp();
		}

		public void handleDownArrow() {
			suggestView.selectDown();
			suggestView.show();
		}

		public void handleEnter() {
			suggestView.selectItem();
		}

		public void handleBackspace() {
			valueInput.value = "";
		}

		public void onItemSelected(SuggestItem suggestItem) {
			final SuggestElement bigThis = this;
			imgNotOk.style.display = "none";
			imgOk.style.display = "";
			imgOk.style.opacity = 1;
			Global.setTimeout(new Callback0() {
				@Override
				public void $invoke() {
					next.effects.fade(bigThis.imgOk, 1.0, 0);
				}
			}, 1000);
		}
	}
	
	static class ItemMatcher {
		public boolean match(String text, SuggestItem item){
			String a = next.util.removeAccents(item._t.toLowerCase());
			String b = next.util.removeAccents(text.toLowerCase());
			return a.indexOf(b) == 0;
		}
	}
	
	static class SuggestView {

		public static final String SELECTED_INDEX_STYLE_CLASS = "nssi";

		private Div suggestViewDiv;
		
		int selectedIndex;
		
		LI selectedLi;
		
		UList ul;

		private SuggestElement suggestElement;

		private Array<SuggestItem> suggestions;

		public SuggestView(SuggestElement suggestElement) {
			this.suggestElement = suggestElement;
			Input textInput = suggestElement.textInput;
			this.suggestViewDiv = next.dom.newElement("div");
			int height = next.style.getFullHeight(textInput);
			int width = next.style.getFullWidth(textInput);
			suggestViewDiv.className = "nssv";
			suggestViewDiv.style.marginTop = height+"px";
			suggestViewDiv.style.minWidth = (width+50)+"px";
			
			suggestViewDiv.style.fontSize = next.style.getStyleProperty(textInput, "font-size");
			suggestViewDiv.style.fontFamily = next.style.getStyleProperty(textInput, "font-family");
			
			
			hide();
			textInput.parentNode.insertBefore(suggestViewDiv, textInput);
		}

		public void selectItem() {
			SuggestItem suggestItem = getSelectedItem();
			setItem(suggestItem);
		}

		public void setItem(SuggestItem suggestItem) {
			if(suggestItem != null){
				suggestElement.valueInput.value = suggestItem._v;
				suggestElement.textInput.value = suggestItem._t;
				hide();
				suggestElement.onItemSelected(suggestItem);
			}
		}

		private SuggestItem getSelectedItem() {
			if(selectedLi != null){
				SuggestItem si = (SuggestItem) ((Map<String, Object>)selectedLi).$get("data-suggestitem");
				return si;
			}
			return null;
		}

		public void selectDown() {
			if(suggestions == null || selectedIndex == suggestions.$length()-1){
				return;
			}
			unselect();
			if(ul.childNodes.length > selectedIndex +1){
				select(selectedIndex+1);
			}
			checkScroll(true);
		}
		
		public void selectUp() {
			if(selectedIndex == 0){
				return;
			}
			unselect();
			if(selectedIndex -1 >= 0 ){
				select(selectedIndex-1);
			}
			checkScroll(false);
		}

		private void checkScroll(boolean down) {
			int totalHeight = next.style.getFullHeight(ul);
			if(selectedLi != null){
				int liHeight = next.style.getFullHeight(selectedLi);
				int top = ((selectedIndex) * liHeight);
				top -= liHeight * 5;
				if(top < 0){
					top = 0;
				}
				ul.scrollTop = top; 				
			}
		}

		public void unselect() {
			if(selectedLi != null){
				next.style.removeClass(selectedLi, SELECTED_INDEX_STYLE_CLASS);
			}
		}

		private void select(int i) {
			selectedLi = (LI) ul.childNodes.$get(i);
			selectedIndex = i;
			next.style.addClass(selectedLi, SELECTED_INDEX_STYLE_CLASS);
		}

		public void setSuggestions(Array<SuggestItem> suggestions) {
			this.suggestions = suggestions;
			selectedIndex = -1;
			suggestViewDiv.innerHTML = "";
			this.ul = next.dom.newElement("ul");
			for (String i : suggestions) {
				SuggestItem item = suggestions.$get(i);
				LI li = createLiFromItem(item);
				ul.appendChild(li);
			}
			suggestViewDiv.appendChild(ul);
		}

		public LI createLiFromItem(final SuggestItem item) {
			final SuggestView bigThis = this;
			final LI li = next.dom.newElement("li");
			Input textInput = suggestElement.textInput;
			li.innerHTML = item._t;
//			li.style.lineHeight = next.style.getStyleProperty(textInput, "line-height");
			
			((Map<String, Object>)li).$put("data-suggestitem", item);
			next.events.attachEvent(li, "mouseover", new Callback1<DOMEvent>() {
				public void $invoke(DOMEvent p1) {
					bigThis.unselect();
					next.style.addClass(li, SELECTED_INDEX_STYLE_CLASS);
				}
			});
			next.events.attachEvent(li, "mouseout", new Callback1<DOMEvent>() {
				public void $invoke(DOMEvent p1) {
					bigThis.unselect();
					next.style.removeClass(li, SELECTED_INDEX_STYLE_CLASS);
					
				}
			});
			next.events.attachEvent(li, "click", new Callback1<DOMEvent>() {
				public void $invoke(DOMEvent p1) {
					Global.console.log("seting item "+item);
					bigThis.unselect();
					bigThis.setItem(item);
				}
			});
			return li;
		}

		public void hide() {
			suggestViewDiv.style.display = "none";
		}
		
		public void show() {
			suggestViewDiv.style.display = "";
		}
		
	}
	
	static class TimeoutManager {

		TimeoutHandler timeoutHandler;
		
		int keyStrokeTimeout;

		SuggestElement suggestElement;
		
		public TimeoutManager(SuggestElement suggestElement){
			this.suggestElement = suggestElement;
			keyStrokeTimeout = DEFAULT_KEY_TIMEOUT;
			installEvents(suggestElement.textInput);
		}

		private void installEvents(Input textInput) {
			final TimeoutManager bigThis = this;
			next.events.attachEvent(textInput, "keyup", new Callback1<DOMEvent>(){
				public void $invoke(DOMEvent p1) {
					bigThis.handleKeyUp(p1);
				}
			});
			next.events.attachEvent(textInput, "keydown", new Callback1<DOMEvent>(){
				public void $invoke(DOMEvent p1) {
					bigThis.handleKeyDown(p1);
				}
			});
		}

		protected void handleKeyDown(DOMEvent p1) {
			int keyCode = p1.keyCode;
			if(keyCode == 38){ //UP ARROW
				next.events.cancelEvent(p1);
			} else if(keyCode == 40){ //DOWN ARROW
				next.events.cancelEvent(p1);
			}
		}

		private void handleKeyUp(final DOMEvent p1) {
			final TimeoutManager bigThis = this;
			int keyCode = p1.keyCode;
			if(keyCode == 38){ //UP ARROW
				suggestElement.handleUpArrow();
			} else if(keyCode == 40){ //DOWN ARROW
				suggestElement.handleDownArrow();
			} else if(keyCode == 13){ //ENTER
				suggestElement.handleEnter();
			} else {
				if(keyCode == 8){//BACKSPACE
					suggestElement.handleBackspace();
				}
				if(timeoutHandler != null){
					Global.clearTimeout(timeoutHandler);
				}
				timeoutHandler = setTimeout(new Callback0() {
					public void $invoke() {
						bigThis.onKeyTimeout(p1);
					}
				}, keyStrokeTimeout);
			}
		}

		protected void onKeyTimeout(DOMEvent p1) {
			suggestElement.triggerSuggestion();
		}
	}
	
}
