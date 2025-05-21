var ReportElement = function(name) {

    this.name = name;
};
ReportElement.prototype.name = null;
ReportElement.prototype.row = null;
ReportElement.prototype.column = null;
ReportElement.prototype.layoutItem = null;
ReportElement.prototype.node = null;
ReportElement.prototype.onFocus = null;
ReportElement.prototype.setNode = function(node) {
    this.node = node;
};
ReportElement.prototype.getNode = function() {
    return this.node;
};
ReportElement.prototype.toString = function() {
    return this.name;
};
ReportElement.prototype.getCell = function() {
    return this.row.getTdForColumn(this.column);
};
ReportElement.$typeDescription={"row":"ReportRow", "column":"ReportColumn", "layoutItem":"LayoutItem", "node":"Element", "onFocus":"Callback0"};


var LabelReportElement = function(name, properties) {
    ReportElement.call(this, name);
    this.label = properties["displayName"] != null ? properties["displayName"].toString() : name;
};
stjs.extend(LabelReportElement, ReportElement);

LabelReportElement.prototype.label = null;
LabelReportElement.prototype.changed = null;
LabelReportElement.prototype.toString = function() {
    return this.label;
};
LabelReportElement.$typeDescription=stjs.copyProps(ReportElement.$typeDescription, {});


var FieldReportElement = function(name, value) {
    ReportElement.call(this, name);
};
stjs.extend(FieldReportElement, ReportElement);

FieldReportElement.prototype.pattern = null;
FieldReportElement.prototype.toString = function() {
    return "$" + this.name;
};
FieldReportElement.$typeDescription=stjs.copyProps(ReportElement.$typeDescription, {});


///////////////////////////LAYOUT
var LayoutItem = function(layoutManager) {

    this.layoutManager = layoutManager;
};
LayoutItem.prototype.layoutManager = null;


LayoutItem.$typeDescription={"layoutManager":"ReportLayoutManager"};


var FieldDetail = function(layoutManager, name, label, field, options) {
    LayoutItem.call(this, layoutManager);
    this.name = name;
    this.label = label;
    this.field = field;
    this.options = options;
};
stjs.extend(FieldDetail, LayoutItem);

FieldDetail.prototype.name = null;
FieldDetail.prototype.label = null;
FieldDetail.prototype.field = null;
FieldDetail.prototype.options = null;
FieldDetail.prototype.aggregate = null;
FieldDetail.prototype.aggregateType = null;
FieldDetail.prototype.isAggregate = function() {
    if (this.label == null || this.label.column == null || this.label.column.getIndex() == 0) {
        return false;
    }
    return this.aggregate;
};
FieldDetail.prototype.setAggregate = function(aggregate) {
    this.aggregate = aggregate;
};
FieldDetail.prototype.isDate = function() {
    return ReportPropertyConfigUtils.isDate(this.options);
};
FieldDetail.prototype.isNumber = function() {
    return ReportPropertyConfigUtils.isNumber(this.options);
};
FieldDetail.prototype.isAggregatable = function() {
    return ReportPropertyConfigUtils.isAggregatable(this.options);
};
FieldDetail.prototype.toString = function() {
    var result = "<fieldDetail name='" + this.name + "'";
    if (this.field != null && this.field.pattern != null && !(this.field.pattern == "")) {
        var pattern = this.field.pattern;
        result += " pattern=\"" + pattern + "\"";
    }
    if (this.label != null && this.label.name != null && !(this.label.name == "") && this.label.changed) {
        result += " label='" + next.util.escapeSingleQuotes(this.label.label) + "'";
    }
    if (this.isAggregate()) {
        result += " aggregate='true'";
        if (this.aggregateType != null && this.aggregateType.length > 0) {
            result += " aggregateType='" + this.aggregateType + "'";
        }
    }
    result += "/>";
    return result;
};
FieldDetail.prototype.getElements = function() {
    var $array = [];
    if (this.label != null) {
        $array.push(this.label);
    }
    if (this.field != null) {
        $array.push(this.field);
    }
    return $array;
};
FieldDetail.prototype.clearElements = function() {
    this.label = null;
    this.field = null;
};
FieldDetail.$typeDescription=stjs.copyProps(LayoutItem.$typeDescription, {"label":"LabelReportElement", "field":"FieldReportElement", "options":{name:"Map", arguments:[null,"Object"]}});

