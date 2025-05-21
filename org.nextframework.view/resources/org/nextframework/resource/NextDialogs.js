var NextDialogs = function(){};

NextDialogs.SIZE_SMALL = "SM";
NextDialogs.SIZE_LARGE = "LG";
NextDialogs.SIZE_EXTRALARGE = "XL";
NextDialogs.CANCEL = "CANCEL";
NextDialogs.OK = "OK";
NextDialogs.DialogCallback = function(){};


NextDialogs.DialogCallback.$typeDescription={};

NextDialogs.MessageDialog = function() {

    this.titleDiv = next.dom.newElement("div", {"class": next.globalMap.get("NextDialogs.header", "popup_box_header")});
    this.bodyDiv = next.dom.newElement("div", {"class": next.globalMap.get("NextDialogs.body", "popup_box_body")});
    this.buttonsDiv = next.dom.newElement("div", {"class": next.globalMap.get("NextDialogs.footer", "popup_box_footer")});
    this.commandsMap = {"OK": "Ok", 
        "CANCEL": "Cancelar"};
    //cannot use the constants .. causes bugs
    this.dialogCallback = (function(){
    var _InlineType = function(){NextDialogs.DialogCallback.call(this);};

    stjs.extend(_InlineType, NextDialogs.DialogCallback);

    _InlineType.prototype.onClick = function(command, value, button) {
        console.log("Command " + command);
        console.log("Value " + value);
        return true;
    };
    _InlineType.$typeDescription=stjs.copyProps(NextDialogs.DialogCallback.$typeDescription, {});
    
    return new _InlineType();
    })();
};
NextDialogs.MessageDialog.prototype.size = null;
NextDialogs.MessageDialog.prototype.titleDiv = null;
NextDialogs.MessageDialog.prototype.bodyDiv = null;
NextDialogs.MessageDialog.prototype.buttonsDiv = null;
NextDialogs.MessageDialog.prototype.commandsMap = null;
NextDialogs.MessageDialog.prototype.dialogCallback = null;
NextDialogs.MessageDialog.prototype.popup = null;
NextDialogs.MessageDialog.prototype.setSize = function(size) {
    this.size = size;
};
NextDialogs.MessageDialog.prototype.setTitle = function(title) {
    this.titleDiv.innerHTML = title;
};
NextDialogs.MessageDialog.prototype.appendToTitle = function(disposableElement) {
    this.titleDiv.appendChild(disposableElement);
};
NextDialogs.MessageDialog.prototype.appendToBody = function(disposableElement) {
    this.bodyDiv.appendChild(disposableElement);
};
NextDialogs.MessageDialog.prototype.setCommandsMap = function(commandsMap) {
    this.commandsMap = commandsMap;
};
NextDialogs.MessageDialog.prototype.setCallback = function(dialogCallback) {
    this.dialogCallback = dialogCallback;
};
NextDialogs.MessageDialog.prototype.show = function() {
    this.popup = next.dom.getNewPopupDiv();
    if (this.size != null) {
        this.popup.setSize(this.size);
    }
    if (this.titleDiv.textContent.trim().length > 0) {
        this.popup.appendChild(this.titleDiv);
    }
    this.popup.appendChild(this.bodyDiv);
    if (this.commandsMap != null) {
        this.buttonsDiv.textContent = "";
        for (var key in this.commandsMap) {
            var button = this.createButton(this.popup, key);
            this.buttonsDiv.appendChild(button);
        }
    }
    if (this.buttonsDiv.textContent.trim().length > 0) {
        this.popup.appendChild(this.buttonsDiv);
    }
};
NextDialogs.MessageDialog.prototype.createButton = function(popup, key) {
    var bigThis = this;
    var button = next.dom.newElement("button");
    button.innerHTML = this.commandsMap[key];
    button.id = "dialog_btn_" + key;
    button.className = next.globalMap.get("NextDialogs.button", "button");
    button.onclick = function(p1) {
        var close = true;
        if (bigThis.dialogCallback != null) {
            close = bigThis.dialogCallback.onClick(key, bigThis.getValue(), button);
        }
        if (close) {
            popup.close();
        }
        return true;
    };
    return button;
};
NextDialogs.MessageDialog.prototype.getValue = function() {
    return null;
};
NextDialogs.MessageDialog.prototype.close = function() {
    if (this.popup != null) {
        this.popup.close();
    }
};
NextDialogs.MessageDialog.$typeDescription={"titleDiv":"Element", "bodyDiv":"Element", "buttonsDiv":"Element", "commandsMap":{name:"Map", arguments:[null,null]}, "dialogCallback":"NextDialogs.DialogCallback", "popup":"Popup"};

NextDialogs.prototype.showInputNumberDialog = function(title, mensagem) {
    var input = next.dom.newInput("text");
    input.className = next.globalMap.get("NextDialogs.inputText", null);
    input.onkeydown = function(event) {
        return eval("mascara_float(this, event)");
    };
    var dialog = (function(){
    var _InlineType = function(){NextDialogs.MessageDialog.call(this);};

    stjs.extend(_InlineType, NextDialogs.MessageDialog);

    _InlineType.prototype.getValue = function() {
        if (input.value != null && input.value != "") {
            var stringValue = input.value;
            stringValue = stringValue.replace(".", "").replace(",", ".");
            return parseFloat(stringValue);
        }
        return null;
    };
    _InlineType.$typeDescription=stjs.copyProps(NextDialogs.MessageDialog.$typeDescription, {});
    
    return new _InlineType();
    })();
    dialog.setTitle(title);
    dialog.appendToBody(next.dom.newElement("div", {"innerHTML": mensagem}));
    dialog.appendToBody(input);
    dialog.show();
    input.focus();
    return dialog;
};
NextDialogs.prototype.showDialog = function(title, disposableBody) {
    var dialog = new NextDialogs.MessageDialog();
    dialog.setTitle(title);
    dialog.appendToBody(disposableBody);
    dialog.setCommandsMap(null);
    dialog.show();
    return dialog;
};
NextDialogs.$typeDescription={};

