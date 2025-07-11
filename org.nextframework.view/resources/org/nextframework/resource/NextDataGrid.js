﻿var NextDataGrid = function(){};

NextDataGrid.OptionalColumnsComponent = function(tableId, dropId, columnsMap, ajaxInfo, hideColumns) {

    this.tableId = tableId;
    this.dropEl = next.dom.toElement(dropId);
    this.columnsMap = columnsMap;
    this.ajaxInfo = ajaxInfo;
    this.columns = [];
    this.hideColumns = hideColumns;
};
NextDataGrid.OptionalColumnsComponent.prototype.tableId = null;
NextDataGrid.OptionalColumnsComponent.prototype.dropId = null;
NextDataGrid.OptionalColumnsComponent.prototype.columnsMap = null;
NextDataGrid.OptionalColumnsComponent.prototype.columns = null;
NextDataGrid.OptionalColumnsComponent.prototype.dropEl = null;
NextDataGrid.OptionalColumnsComponent.prototype.ajaxInfo = null;
NextDataGrid.OptionalColumnsComponent.prototype.hideColumns = null;
NextDataGrid.OptionalColumnsComponent.Column = function(id, label) {

    this.id = id;
    this.label = label;
    this.showColumn = true;
    this.columnIndex = this.getColumnIndex();
    this.table = this.getTable();
};
NextDataGrid.OptionalColumnsComponent.Column.prototype.id = null;
NextDataGrid.OptionalColumnsComponent.Column.prototype.label = null;
NextDataGrid.OptionalColumnsComponent.Column.prototype.showColumn = null;
NextDataGrid.OptionalColumnsComponent.Column.prototype.check = null;
NextDataGrid.OptionalColumnsComponent.Column.prototype.columnIndex = null;
NextDataGrid.OptionalColumnsComponent.Column.prototype.table = null;
NextDataGrid.OptionalColumnsComponent.Column.prototype.appendColumn = function(p) {
    var optionsMap = {};
    optionsMap["id"] = next.dom.generateUniqueId();
    optionsMap["className"] = next.globalMap.get("NextDataGrid.optionInput", null);
    optionsMap["labelOptions"] = {"className": next.globalMap.get("NextDataGrid.optionLabel", null)};
    optionsMap["containerOptions"] = {"className": next.globalMap.get("NextDataGrid.optionContainer", null)};
    var panelSpan = next.dom.newInput("checkbox", null, this.label, optionsMap);
    this.check = panelSpan.getElementsByTagName("input")[0];
    this.check.checked = this.showColumn;
    p.appendChild(panelSpan);
};
NextDataGrid.OptionalColumnsComponent.Column.prototype.getColumnIndex = function() {
    var tableCell = this.getTableHeaderCell();
    return tableCell.cellIndex;
};
NextDataGrid.OptionalColumnsComponent.Column.prototype.getTableHeaderCell = function() {
    var element = next.dom.toElement(this.id);
    while (!(element.parentNode.tagName.toLowerCase() == "td") && !(element.parentNode.tagName.toLowerCase() == "th")) {
        element = element.parentNode;
    }
    var tableCell = element.parentNode;
    return tableCell;
};
NextDataGrid.OptionalColumnsComponent.Column.prototype.getTable = function() {
    var element = next.dom.toElement(this.id);
    return next.dom.getParentTag(element, "table");
};
NextDataGrid.OptionalColumnsComponent.Column.prototype.updateElements = function() {
    this.showColumn = this.check.checked;
    this.updateVisibility();
};
NextDataGrid.OptionalColumnsComponent.Column.prototype.updateVisibility = function() {
    this.updateRows(this.table.tHead.rows);
    this.updateRows(this.table.tBodies[0].rows);
};
NextDataGrid.OptionalColumnsComponent.Column.prototype.updateRows = function(rows) {
    for (var i = 0; i < rows.length; i++) {
        var cell = rows[i].cells[this.columnIndex];
        cell.style.display = this.showColumn ? "" : "none";
    }
};
NextDataGrid.OptionalColumnsComponent.Column.$typeDescription={"check":"Input", "table":"Table"};

NextDataGrid.OptionalColumnsComponent.prototype.init = function() {
    for (var label in this.columnsMap) {
        var column = new NextDataGrid.OptionalColumnsComponent.Column(this.columnsMap[label], label);
        if (this.hideColumns != null && this.hideColumns.indexOf(column.label) >= 0) {
            column.showColumn = false;
            column.updateVisibility();
        }
        this.columns.push(column);
    }
    this.dropEl.style.cursor = "pointer";
    var bigThis = this;
    next.events.attachEvent(this.dropEl, "click", function(p1) {
        bigThis.showConfigurationDialog();
    });
};
NextDataGrid.OptionalColumnsComponent.prototype.showConfigurationDialog = function() {
    var dialog = new NextDialogs.MessageDialog();
    dialog.setTitle("Configurar colunas");
    var panelDiv = next.dom.newElement("div", {"class": next.globalMap.get("NextDataGrid.panel", "")});
    for (var c in this.columns) {
        if (!(this.columns).hasOwnProperty(c)) continue;
        var column = this.columns[c];
        var divOp = next.dom.newElement("div", {"class": next.globalMap.get("NextDataGrid.option", "popup_box_option")});
        column.appendColumn(divOp);
        panelDiv.appendChild(divOp);
    }
    dialog.appendToBody(panelDiv);
    var bigThis = this;
    dialog.setCallback((function(){
    var _InlineType = function(){NextDialogs.DialogCallback.call(this);};

    stjs.extend(_InlineType, NextDialogs.DialogCallback);

    _InlineType.prototype.onClick = function(command, value, button) {
        if ((command == "CANCEL")) {
            bigThis.cancel();
        } else {
            bigThis.saveColumns();
        }
        return true;
    };
    _InlineType.$typeDescription=stjs.copyProps(NextDialogs.DialogCallback.$typeDescription, {});
    
    return new _InlineType();
    })());
    dialog.show();
};
NextDataGrid.OptionalColumnsComponent.prototype.saveColumns = function() {
    for (var c in this.columns) {
        if (!(this.columns).hasOwnProperty(c)) continue;
        var column = this.columns[c];
        column.getTableHeaderCell().style.width = "auto";
        column.updateElements();
    }
    this.columns[this.columns.length - 1].getTableHeaderCell().style.width = "1%";
    this.persist();
};
/**
		 * @see DataGridOptionalColumnsTag for ajaxInfo setup
		 */
NextDataGrid.OptionalColumnsComponent.prototype.persist = function() {
    var ajaxId = this.ajaxInfo["ajaxId"];
    var serverUrl = this.ajaxInfo["serverUrl"];
    var cacheKey = this.ajaxInfo["cacheKey"];
    var configMap = [];
    for (var c in this.columns) {
        if (!(this.columns).hasOwnProperty(c)) continue;
        var column = this.columns[c];
        if (!column.showColumn) {
            configMap.push(column.label);
        }
    }
    var request = next.ajax.newRequest();
    request.setUrl(serverUrl);
    request.setParameter("serverId", ajaxId);
    request.setParameter("cacheKey", cacheKey);
    request.setParameter("hideColumns", JSON.stringify(configMap));
    request.setAppendContext(false);
    request.setOnComplete(function(p1) {
        console.log(p1);
    });
    request.send();
};
NextDataGrid.OptionalColumnsComponent.prototype.cancel = function() {
};
NextDataGrid.OptionalColumnsComponent.$typeDescription={"columnsMap":{name:"Map", arguments:[null,null]}, "columns":{name:"Array", arguments:["NextDataGrid.OptionalColumnsComponent.Column"]}, "dropEl":"Element", "ajaxInfo":{name:"Map", arguments:[null,"Object"]}, "hideColumns":{name:"Array", arguments:[null]}};

NextDataGrid.prototype.createOptionalColumns = function(tableId, dropId, columnsMap, ajaxInfo, hideColumns) {
    new NextDataGrid.OptionalColumnsComponent(tableId, dropId, columnsMap, ajaxInfo, hideColumns).init();
};
NextDataGrid.$typeDescription={};

