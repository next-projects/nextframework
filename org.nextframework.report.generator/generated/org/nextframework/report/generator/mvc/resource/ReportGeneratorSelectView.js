var ReportGeneratorSelectView = function(viewDiv) {

    this.items = [];
    this.viewDiv = viewDiv;
};
stjs.extend(ReportGeneratorSelectView, SelectView);

ReportGeneratorSelectView.prototype.viewDiv = null;
ReportGeneratorSelectView.prototype.items = null;
ReportGeneratorSelectView.prototype.selectedItem = null;
ReportGeneratorSelectView.prototype.onDblClick = null;
ReportGeneratorSelectView.prototype.onselect = null;
ReportGeneratorSelectView.prototype.usePropertyAsLabel = null;
ReportGeneratorSelectView.prototype.setUsePropertyAsLabel = function(usePropertyAsLabel) {
    this.usePropertyAsLabel = usePropertyAsLabel;
};
ReportGeneratorSelectView.prototype.getUsePropertyAsLabel = function() {
    return this.usePropertyAsLabel;
};
ReportGeneratorSelectView.prototype.selectItem = function(item) {
    if (this.onselect != null) {
        this.onselect(this.selectedItem);
    }
    if (!(item.selectView == this)) {
        alert("Erro: o item não é do tipo do select view");
        return;
    }
    this.unselectSelectedItem();
    if ((item == this.selectedItem)) {
        this.selectedItem = null;
        return;
    }
    this.selectedItem = item;
    this.selectedItem.selectItem();
};
ReportGeneratorSelectView.prototype.unselectSelectedItem = function() {
    if (this.selectedItem != null) {
        this.selectedItem.unselect();
    }
};
ReportGeneratorSelectView.prototype.blur = function() {
    this.unselectSelectedItem();
    this.selectedItem = null;
};
ReportGeneratorSelectView.prototype.select = function(name, value) {
    var div = window.document.createElement("DIV");
    div.style.padding = "1px";
    div.innerHTML = name;
    if (this.usePropertyAsLabel != null) {
        div.innerHTML = value[this.usePropertyAsLabel] != null ? value[this.usePropertyAsLabel].toString() : name;
    }
    this.viewDiv.appendChild(div);
    this.items.push(new ReportGeneratorSelectViewItem(this, new SimpleNamedObject(name, value), div));
};
ReportGeneratorSelectView.prototype.unselect = function(name) {
    var i = 0;
    for (var key in this.items) {
        if (!(this.items).hasOwnProperty(key)) continue;
        var el = this.items[key];
        if ((el.name == name)) {
            if ((el == this.selectedItem)) {
                this.selectedItem = null;
            }
            var div = el.div;
            div.parentNode.removeChild(div);
            this.items.splice(i, 1);
            break;
        }
        i++;
    }
};
ReportGeneratorSelectView.$typeDescription={"viewDiv":"Div", "items":{name:"Array", arguments:["ReportGeneratorSelectViewItem"]}, "selectedItem":"ReportGeneratorSelectViewItem", "onDblClick":"ReportGeneratorSelectViewEvent", "onselect":{name:"Callback1", arguments:["ReportGeneratorSelectViewItem"]}};


var ReportGeneratorSelectViewEvent = function(){};

ReportGeneratorSelectViewEvent.prototype.invoke = function(item){};
ReportGeneratorSelectViewEvent.$typeDescription={};


var ReportGeneratorSelectViewItem = function(selectView, obj, div) {

    this.selectView = selectView;
    this.name = obj.name;
    this.value = obj.value;
    this.div = div;
    var bigThis = this;
    this.div.onclick = function(p1) {
        bigThis.selectView.selectItem(bigThis);
        return true;
    };
    this.div.ondblclick = function(p1) {
        bigThis.selectView.selectItem(bigThis);
        if (bigThis.selectView.onDblClick != null) {
            bigThis.selectView.onDblClick.invoke(bigThis);
        }
        return true;
    };
};
ReportGeneratorSelectViewItem.prototype.selectView = null;
ReportGeneratorSelectViewItem.prototype.name = null;
ReportGeneratorSelectViewItem.prototype.div = null;
ReportGeneratorSelectViewItem.prototype.value = null;
ReportGeneratorSelectViewItem.prototype.selectItem = function() {
    next.style.addClass(this.div, "selected");
    this.div.style.padding = "0px";
};
ReportGeneratorSelectViewItem.prototype.unselect = function() {
    next.style.removeClass(this.div, "selected");
    this.div.style.padding = "1px";
};
ReportGeneratorSelectViewItem.$typeDescription={"selectView":"ReportGeneratorSelectView", "div":"Div", "value":{name:"Map", arguments:[null,"Object"]}};


var SimpleNamedObject = function(name, value) {

    this.name = name;
    this.value = value;
};
SimpleNamedObject.prototype.name = null;
SimpleNamedObject.prototype.value = null;
SimpleNamedObject.prototype.toString = function() {
    return this.name;
};
SimpleNamedObject.$typeDescription={"value":{name:"Map", arguments:[null,"Object"]}};

