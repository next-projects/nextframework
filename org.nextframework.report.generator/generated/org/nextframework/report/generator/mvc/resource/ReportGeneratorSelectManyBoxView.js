var ReportGeneratorSelectManyBoxView = function(inputFrom) {

    var name = inputFrom.name.substring(0, inputFrom.name.length - 6);
    this.from = inputFrom;
    this.to = next.dom.toElement(name + "_to___");
    var bigThis = this;
    next.events.attachEvent(this.from, "click", function(p2) {
        if (bigThis.onselect != null && bigThis.from.selectedIndex >= 0) {
            bigThis.onselect(bigThis.from.options.item(bigThis.from.selectedIndex));
        }
    });
    next.events.attachEvent(this.to, "click", function(p2) {
        if (bigThis.onselect != null && bigThis.to.selectedIndex >= 0) {
            bigThis.onselect(bigThis.to.options.item(bigThis.to.selectedIndex));
        }
        if (bigThis.onselectto != null && bigThis.to.selectedIndex >= 0) {
            bigThis.onselectto(bigThis.to.options.item(bigThis.to.selectedIndex));
        }
    });
};
stjs.extend(ReportGeneratorSelectManyBoxView, SelectView);

ReportGeneratorSelectManyBoxView.prototype.from = null;
ReportGeneratorSelectManyBoxView.prototype.to = null;
ReportGeneratorSelectManyBoxView.prototype.usePropertyAsLabel = null;
ReportGeneratorSelectManyBoxView.prototype.onselect = null;
ReportGeneratorSelectManyBoxView.prototype.onselectto = null;
ReportGeneratorSelectManyBoxView.prototype.setUsePropertyAsLabel = function(usePropertyAsLabel) {
    this.usePropertyAsLabel = usePropertyAsLabel;
};
ReportGeneratorSelectManyBoxView.prototype.select = function(name, properties) {
    for (var i = 0; i < this.from.options.length; i++) {
        if ((this.from.options.item(i).value == name)) {
            this.from.selectedIndex = i;
            selectManyBoxAdd(this.from);
            break;
        }
    }
};
ReportGeneratorSelectManyBoxView.prototype.markSelected = function(name) {
    for (var i = 0; i < this.to.options.length; i++) {
        if ((this.to.options[i].value == name)) {
            this.to.selectedIndex = i;
            break;
        }
    }
};
ReportGeneratorSelectManyBoxView.prototype.add = function(name, properties) {
    var label = name;
    if (this.usePropertyAsLabel != null) {
        label = properties[this.usePropertyAsLabel] != null ? properties[this.usePropertyAsLabel].toString() : name;
    }
    selectManyAddOption(this.from, label, name, properties);
};
ReportGeneratorSelectManyBoxView.prototype.unselect = function(name) {
    for (var i = 0; i < this.to.options.length; i++) {
        if ((this.to.options[i].value == name)) {
            this.to.selectedIndex = i;
            break;
        }
    }
    selectManyBoxRemove(this.from);
};
ReportGeneratorSelectManyBoxView.prototype.getFrom = function() {
    return this.from;
};
ReportGeneratorSelectManyBoxView.prototype.getTo = function() {
    return this.to;
};
ReportGeneratorSelectManyBoxView.prototype.blur = function() {
    this.from.selectedIndex = -1;
    this.to.selectedIndex = -1;
};
ReportGeneratorSelectManyBoxView.prototype.clearAll = function() {
    this.clearFrom();
    this.clearTo();
};
ReportGeneratorSelectManyBoxView.prototype.clearTo = function() {
    this.clear(this.to);
};
ReportGeneratorSelectManyBoxView.prototype.clearFrom = function() {
    this.clear(this.from);
};
ReportGeneratorSelectManyBoxView.prototype.clear = function(select) {
    while (select.options.length > 0) {
        select.options.remove(0);
    }
};
ReportGeneratorSelectManyBoxView.$typeDescription={"from":"Select", "to":"Select", "onselect":{name:"Callback1", arguments:["Option"]}, "onselectto":{name:"Callback1", arguments:["Option"]}};

