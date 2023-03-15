package org.nextframework.resource;

import static org.nextframework.js.NextGlobalJs.next;
import static org.stjs.javascript.Global.console;
import static org.stjs.javascript.JSCollections.$map;

import org.nextframework.js.dom.Popup;
import org.stjs.javascript.Global;
import org.stjs.javascript.JSCollections;
import org.stjs.javascript.Map;
import org.stjs.javascript.dom.DOMEvent;
import org.stjs.javascript.dom.Element;
import org.stjs.javascript.dom.Input;
import org.stjs.javascript.functions.Function1;

public class NextDialogs {

	public static final String CANCEL = "CANCEL";
	public static final String OK = "OK";

	public abstract static class DialogCallback {

		public abstract boolean onClick(String command, Object value, Element button);

	}

	public static class MessageDialog {

		private String title;
		private Map<String, String> commandsMap;
		private DialogCallback dialogCallback;

		private Element titleDiv;
		private Element bodyDiv;
		private Element buttonsDiv;
		private Popup popup;

		public MessageDialog() {

			titleDiv = next.dom.newElement("div", $map("class", next.globalMap.get("NextDialogs.header", "separator")));

			bodyDiv = next.dom.newElement("div", $map("class", next.globalMap.get("NextDialogs.body", "popup_box_body")));

			buttonsDiv = next.dom.newElement("div", $map("class", next.globalMap.get("NextDialogs.footer", "popup_box_footer")));

			commandsMap = $map(
					"OK", "Ok",
					"CANCEL", "Cancelar"); //cannot use the constants .. causes bugs

			dialogCallback = new DialogCallback() {

				public boolean onClick(String command, Object value, Element button) {
					console.log("Command " + command);
					console.log("Value " + value);
					return true;
				}

			};

		}

		public void setTitle(String title) {
			this.title = title;
		}

		public void appendToBody(Element disposableElement) {
			this.bodyDiv.appendChild(disposableElement);
		}

		public void setCommandsMap(Map<String, String> commandsMap) {
			this.commandsMap = commandsMap;
		}

		public void setCallback(DialogCallback dialogCallback) {
			this.dialogCallback = dialogCallback;
		}

		public void show() {

			popup = next.dom.getNewPopupDiv();

			if (title != null) {
				titleDiv.innerHTML = title;
				popup.appendChild(titleDiv);
			}

			popup.appendChild(bodyDiv);

			if (commandsMap != null) {
				for (String key : commandsMap) {
					Element button = createButton(popup, key);
					buttonsDiv.appendChild(button);
				}
				popup.appendChild(buttonsDiv);
			}

			updatePopup(popup);
			centralize();

		}

		public Element createButton(final Popup popup, final String key) {

			final MessageDialog bigThis = this;

			final Element button = next.dom.newElement("button");
			button.innerHTML = commandsMap.$get(key);
			button.id = "dialog_btn_" + key;
			button.className = next.globalMap.get("NextDialogs.button", "button");
			button.onclick = new Function1<DOMEvent, Boolean>() {

				public Boolean $invoke(DOMEvent p1) {
					boolean close = true;
					if (bigThis.dialogCallback != null) {
						close = bigThis.dialogCallback.onClick(key, bigThis.getValue(), button);
					}
					if (close) {
						popup.close();
					}
					return true;
				}

			};

			return button;
		}

		public Object getValue() {
			return null;
		}

		public void updatePopup(Popup popup) {
			
		}

		public void centralize() {
			if (popup != null) {
				next.style.centralize(popup);
			}
		}

		public void close() {
			if (popup != null) {
				popup.close();
			}
		}

	}

	public MessageDialog showInputNumberDialog(String title, String mensagem) {

		final Input input = next.dom.newInput("text");
		input.className = next.globalMap.get("NextDialogs.inputText", null);
		input.onkeydown = new Function1<DOMEvent, Boolean>() {

			public Boolean $invoke(DOMEvent event) {
				return Global.eval("mascara_float(this, event)");
			}

		};

		MessageDialog dialog = new MessageDialog() {

			@Override
			public Object getValue() {
				if (input.value != null && input.value != "") {
					String stringValue = (String) input.value;
					stringValue = stringValue.replace(".", "").replace(",", ".");
					return Global.parseFloat(stringValue);
				}
				return null;
			}

		};

		dialog.setTitle(title);

		dialog.appendToBody(next.dom.newElement("div", JSCollections.$map("innerHTML", mensagem)));
		dialog.appendToBody(input);

		dialog.show();

		input.focus();

		return dialog;
	}

	public MessageDialog showDialog(String title, Element disposableBody) {
		MessageDialog dialog = new MessageDialog();
		dialog.setTitle(title);
		dialog.appendToBody(disposableBody);
		dialog.setCommandsMap(null);
		dialog.show();
		return dialog;
	}

}
