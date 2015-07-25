var NextDialogs = function(){};

NextDialogs.CANCEL = "CANCEL";
NextDialogs.OK = "OK";
NextDialogs.DialogCallback = function(){};


NextDialogs.DialogCallback.$typeDescription={};

NextDialogs.AbstractDialog = function() {

    this.body = next.dom.newElement("div");
    this.commandsMap = {"OK": "Ok", 
        "CANCEL": "Cancelar"};
    //cannot use the constants .. causes bugs
    this.dialogCallback = (function(){
    var _InlineType = function(){NextDialogs.DialogCallback.call(this);};

    stjs.extend(_InlineType, NextDialogs.DialogCallback);

    _InlineType.prototype.onClose = function(command, value) {
        console.log("Command " + command);
        console.log("Value " + value);
    };
    _InlineType.$typeDescription=stjs.copyProps(NextDialogs.DialogCallback.$typeDescription, {});
    
    return new _InlineType();
    })();
};
NextDialogs.AbstractDialog.prototype.title = null;
NextDialogs.AbstractDialog.prototype.body = null;
NextDialogs.AbstractDialog.prototype.commandsMap = null;
NextDialogs.AbstractDialog.prototype.dialogCallback = null;
NextDialogs.AbstractDialog.prototype.setCallback = function(dialogCallback) {
    this.dialogCallback = dialogCallback;
};
NextDialogs.AbstractDialog.prototype.setCommandsMap = function(commandsMap) {
    this.commandsMap = commandsMap;
    return this;
};
NextDialogs.AbstractDialog.prototype.setTitle = function(title) {
    this.title = title;
    return this;
};
NextDialogs.AbstractDialog.prototype.setBody = function(body) {
    this.body = body;
    return this;
};


NextDialogs.AbstractDialog.$typeDescription={"body":"Element", "commandsMap":{name:"Map", arguments:[null,null]}, "dialogCallback":"NextDialogs.DialogCallback"};

NextDialogs.MessageDialog = function(){NextDialogs.AbstractDialog.call(this);};

stjs.extend(NextDialogs.MessageDialog, NextDialogs.AbstractDialog);

NextDialogs.MessageDialog.prototype.popup = null;
NextDialogs.MessageDialog.prototype.close = function() {
    this.popup.close();
};
NextDialogs.MessageDialog.prototype.show = function() {
    this.popup = next.dom.getNewPopupDiv();
    var titleDiv = next.dom.newElement("h2", {"innerHTML": this.title, 
        "class": "separator"});
    this.popup.appendChild(titleDiv);
    this.popup.appendChild(this.body);
    if (this.commandsMap != null) {
        this.popup.appendChild(next.dom.newElement("div", {"innerHTML": "&nbsp;", 
            "class": "separator", 
            "font-size": "1px;"}));
        var buttonDiv = next.dom.newElement("div");
        buttonDiv.style.textAlign = "right";
        for (var key in this.commandsMap) {
            var button = this.createButton(this.popup, key);
            buttonDiv.appendChild(button);
        }
        this.popup.appendChild(buttonDiv);
    }
    next.style.centralize(this.popup);
    this.popup.style.top = "120px";
};
NextDialogs.MessageDialog.prototype.createButton = function(popup, key) {
    var bigThis = this;
    var button = next.dom.newElement("button");
    button.innerHTML = this.commandsMap[key];
    button.id = "dialog_btn_" + key;
    button.style.margin = "4px";
    button.onclick = function(p1) {
        popup.close();
        bigThis.dialogCallback.onClose(key, bigThis.getValue());
        return true;
    };
    return button;
};
NextDialogs.MessageDialog.prototype.getValue = function() {
    return null;
};
NextDialogs.MessageDialog.$typeDescription=stjs.copyProps(NextDialogs.AbstractDialog.$typeDescription, {"popup":"Popup"});

NextDialogs.InputMessageDialog = function(){NextDialogs.MessageDialog.call(this);};

stjs.extend(NextDialogs.InputMessageDialog, NextDialogs.MessageDialog);

NextDialogs.InputMessageDialog.prototype.input = null;
NextDialogs.InputMessageDialog.prototype.getValue = function() {
    if (this.input == null) {
        alert("The input has not been set for dialog");
    }
    return this.input.value;
};
NextDialogs.InputMessageDialog.$typeDescription=stjs.copyProps(NextDialogs.MessageDialog.$typeDescription, {"input":"Input"});

NextDialogs.InputNumberMessageDialog = function(){NextDialogs.InputMessageDialog.call(this);};

stjs.extend(NextDialogs.InputNumberMessageDialog, NextDialogs.InputMessageDialog);

NextDialogs.InputNumberMessageDialog.prototype.getValue = function() {
    var stringValue = NextDialogs.InputMessageDialog.prototype.getValue.call(this);
    stringValue = stringValue.replace(".", "").replace(",", ".");
    return parseFloat(stringValue);
};
NextDialogs.InputNumberMessageDialog.$typeDescription=stjs.copyProps(NextDialogs.InputMessageDialog.$typeDescription, {});

NextDialogs.prototype.showInputNumberDialog = function(title, mensagem) {
    var d = new NextDialogs.InputNumberMessageDialog();
    d.setTitle(title);
    var body = next.dom.newElement("div");
    var messageDiv = next.dom.newElement("div", {"innerHTML": mensagem});
    var inputDiv = next.dom.newElement("div");
    var input = next.dom.newInput("text");
    input.style.marginTop = "4px";
    input.style.marginBottom = "4px";
    d.input = input;
    //onKeyDown="return mascara_float(this,event)"
    input.onkeydown = function(event) {
        return eval("mascara_float(input, event)");
    };
    body.appendChild(messageDiv);
    body.appendChild(inputDiv);
    inputDiv.appendChild(input);
    d.setBody(body);
    d.show();
    input.focus();
    return d;
};
NextDialogs.prototype.showMessageDialog = function(message) {
};
NextDialogs.prototype.showDialog = function(title, el) {
    var dialog = new NextDialogs.MessageDialog();
    dialog.setTitle(title);
    dialog.commandsMap = null;
    dialog.body.appendChild(el);
    dialog.show();
    return dialog;
};
NextDialogs.$typeDescription={};

