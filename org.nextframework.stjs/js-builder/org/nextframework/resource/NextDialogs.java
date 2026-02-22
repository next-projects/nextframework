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

	public static final String SIZE_SMALL = "SM";
	public static final String SIZE_LARGE = "LG";
	public static final String SIZE_EXTRALARGE = "XL";

	public static final String CANCEL = "CANCEL";
	public static final String OK = "OK";

	public abstract static class DialogCallback {

		public abstract boolean onClick(String command, Object value, Element button);

	}

	public static class MessageDialog {

		private String size;

		private Element titleDiv;
		private Element bodyDiv;
		private Element buttonsDiv;

		private Element borrowedElement;
		private Element borrowedElementParent;

		private Map<String, String> commandsMap;
		private DialogCallback dialogCallback;

		private Popup popup;

		public MessageDialog() {

			titleDiv = next.dom.newElement("div", $map("class", next.globalMap.get("NextDialogs.header", "popup_box_header")));

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

		public void setSize(String size) {
			this.size = size;
		}

		public void setTitle(String title) {
			this.titleDiv.innerHTML = title;
		}

		public void appendToTitle(Element disposableElement) {
			this.titleDiv.appendChild(disposableElement);
		}

		public void appendToBody(Element disposableElement) {
			this.bodyDiv.appendChild(disposableElement);
		}

		public void appendToBodyBorrowedElement(Element borrowedElement) {
			this.borrowedElement = borrowedElement;
			this.borrowedElementParent = this.borrowedElement.parentNode;
			appendToBody(this.borrowedElement);
		}

		public void setCommandsMap(Map<String, String> commandsMap) {
			this.commandsMap = commandsMap;
		}

		public void setCallback(DialogCallback dialogCallback) {
			this.dialogCallback = dialogCallback;
		}

		public void show() {

			popup = next.dom.getNewPopupDiv();

			if (size != null) {
				popup.setSize(size);
			}

			if (titleDiv.textContent.trim().length() > 0) {
				popup.appendChild(titleDiv);
			}

			popup.appendChild(bodyDiv);

			if (commandsMap != null) {
				buttonsDiv.textContent = "";
				for (String key : commandsMap) {
					Element button = createButton(popup, key);
					buttonsDiv.appendChild(button);
				}
			}

			if (buttonsDiv.textContent.trim().length() > 0) {
				popup.appendChild(buttonsDiv);
			}

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
					if (bigThis.borrowedElementParent != null) {
						//É importante voltar o borrowedElement antes de invocar o dialogCallback,
						//pois a lógica do callback poderá invocar um submitForm() e os inputs de borrowedElement deverão estar no form original.
						bigThis.borrowedElementParent.appendChild(bigThis.borrowedElement);
					}
					if (bigThis.dialogCallback != null) {
						close = bigThis.dialogCallback.onClick(key, bigThis.getValue(), button);
					}
					if (close) {
						popup.close();
					} else {
						//Se o dialogCallback retornou false (para não fechar o modal, os inputs de borrowedElement deverão voltar para o modal.
						bigThis.appendToBody(bigThis.borrowedElement);
					}
					return true;
				}

			};

			return button;
		}

		public Object getValue() {
			return null;
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
