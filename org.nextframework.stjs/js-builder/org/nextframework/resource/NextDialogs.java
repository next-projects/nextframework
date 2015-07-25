package org.nextframework.resource;

import static org.nextframework.js.NextGlobalJs.next;
import static org.stjs.javascript.Global.console;
import static org.stjs.javascript.JSCollections.$map;

import org.nextframework.js.dom.Popup;
import org.stjs.javascript.Global;
import org.stjs.javascript.JSCollections;
import org.stjs.javascript.Map;
import org.stjs.javascript.dom.DOMEvent;
import org.stjs.javascript.dom.Div;
import org.stjs.javascript.dom.Element;
import org.stjs.javascript.dom.Input;
import org.stjs.javascript.functions.Function1;

public class NextDialogs {
	
	public static final String CANCEL = "CANCEL";
	public static final String OK = "OK";
	
	public abstract static class DialogCallback {
		public abstract void onClose(String command, Object value);
	}

	@SuppressWarnings("unchecked")
	private static abstract class AbstractDialog<E extends AbstractDialog<?>> {

		protected String title;
		protected Element body;
		protected Map<String, String> commandsMap;
		protected DialogCallback dialogCallback;
		
		public AbstractDialog(){
			body = next.dom.newElement("div");
			
			commandsMap = $map(
					"OK", "Ok",  
					"CANCEL", "Cancelar"); //cannot use the constants .. causes bugs
			
			dialogCallback = new DialogCallback() {
				public void onClose(String command, Object value) {
					console.log("Command "+command);
					console.log("Value "+value);
				}
			};
		}
		
		public void setCallback(DialogCallback dialogCallback) {
			this.dialogCallback = dialogCallback;
		}
		
		public E setCommandsMap(Map<String, String> commandsMap) {
			this.commandsMap = commandsMap;
			return (E)this;
		}
		
		public E setTitle(String title) {
			this.title = title;
			return (E)this;
		}
		
		public E setBody(Element body) {
			this.body = body;
			return (E)this;
		}
		
		public abstract void show();
		public abstract Object getValue();
	}
	
	public static class MessageDialog extends AbstractDialog<MessageDialog> {

		Popup popup;
		
		public void close(){
			popup.close();
		}
		
		@Override
		public void show() {
			popup = next.dom.getNewPopupDiv();
			Element titleDiv = next.dom.newElement("h2", 
					$map("innerHTML", title, "class", "separator"));
			popup.appendChild(titleDiv);
			popup.appendChild(body);
			
			if(commandsMap != null){
				popup.appendChild(next.dom.newElement("div", 
						$map("innerHTML", "&nbsp;", "class", "separator", "font-size", "1px;")));
	
				Div buttonDiv = next.dom.newElement("div");
				buttonDiv.style.textAlign = "right";
				
				for(String key: commandsMap){
					Element button = createButton(popup, key);
					buttonDiv.appendChild(button);
				}
				popup.appendChild(buttonDiv);
			}

			next.style.centralize(popup);
			popup.style.top = "120px";
		}

		public Element createButton(final Popup popup, final String key) {
			final MessageDialog bigThis = this;
			Element button = next.dom.newElement("button");
			button.innerHTML = commandsMap.$get(key);
			button.id = "dialog_btn_"+key;
			button.style.margin = "4px";
			button.onclick = new Function1<DOMEvent, Boolean>() {
				public Boolean $invoke(DOMEvent p1) {
					popup.close();
					bigThis.dialogCallback.onClose(key, bigThis.getValue());
					return true;
				}
			};
			return button;
		}

		@Override
		public Object getValue() {
			return null;
		}
		
	}
	
	public static class InputMessageDialog extends MessageDialog {
		
		protected Input input;
		
		@Override
		public Object getValue() {
			if(input == null){
				Global.alert("The input has not been set for dialog");
			}
			return input.value;
		}
	}
	
	public static class InputNumberMessageDialog extends InputMessageDialog {
		@Override
		public Object getValue() {
			String stringValue = (String) super.getValue();
			stringValue = stringValue.replace(".", "").replace(",", ".");
			return Global.parseFloat(stringValue);
		}
	}
	
	public InputNumberMessageDialog showInputNumberDialog(String title, String mensagem){
		InputNumberMessageDialog d = new InputNumberMessageDialog();
		d.setTitle(title);
		Div body = next.dom.newElement("div");
		Div messageDiv = next.dom.newElement("div", 
									JSCollections.$map("innerHTML", mensagem));
		Div inputDiv = next.dom.newElement("div");
		
		final Input input = next.dom.newInput("text");
		input.style.marginTop = "4px";
		input.style.marginBottom = "4px";
		d.input = input;
		
		//onKeyDown="return mascara_float(this,event)"
		input.onkeydown = new Function1<DOMEvent, Boolean>() {
			public Boolean $invoke(DOMEvent event) {
				return Global.eval("mascara_float(input, event)");
			}
		};
		
		body.appendChild(messageDiv);
		body.appendChild(inputDiv);
		inputDiv.appendChild(input);
		d.setBody(body);
		d.show();
		
		input.focus();
		
		return d;
	}
	
	public void showMessageDialog(String message){
		
	}
	
	public MessageDialog showDialog(String title, Element el){
		MessageDialog dialog = new MessageDialog();
		dialog.setTitle(title);
		dialog.commandsMap = null;
		dialog.body.appendChild(el);
		dialog.show();
		return dialog;
	}
}
