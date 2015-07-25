var ReportDefinition = function(designer, table) {

    this.designTable = table;
    this.elements = [];
    this.columns = [];
    this.groups = [];
    this.designer = designer;
    this.sectionTitle = new ReportSection(SectionType.TITLE, this, null);
    this.sectionTitle.rows[0].row.insertCell(0);
    this.sectionDetailHeader = new ReportSection(SectionType.DETAIL_HEADER, this, null);
    this.sectionDetail = new ReportSection(SectionType.DETAIL, this, null);
};
stjs.extend(ReportDefinition, Selectable);

ReportDefinition.prototype.designTable = null;
ReportDefinition.prototype.sectionTitle = null;
ReportDefinition.prototype.sectionDetailHeader = null;
ReportDefinition.prototype.sectionDetail = null;
ReportDefinition.prototype.columns = null;
ReportDefinition.prototype.groups = null;
ReportDefinition.prototype.elements = null;
ReportDefinition.prototype.designer = null;
ReportDefinition.prototype.selectedElement = null;
ReportDefinition.RowIterator = function(){};

ReportDefinition.RowIterator.prototype.titleRow = function(reportRow){};
ReportDefinition.RowIterator.prototype.labelRow = function(reportRow){};
ReportDefinition.RowIterator.prototype.row = function(reportRow){};
ReportDefinition.RowIterator.$typeDescription={};

ReportDefinition.prototype.getAllSections = function() {
    var array = [this.sectionDetailHeader, this.sectionDetail];
    for (var i = 0; i < this.groups.length; i++) {
        array.push(this.groups[i].groupHeader);
    }
    return array;
};
ReportDefinition.prototype.removeGroup = function(groupName) {
    var group = this.getReportGroup(groupName);
    var section = group.groupHeader;
    section.clear();
    for (var i = 0; i < this.groups.length; i++) {
        group = this.groups[i];
        if ((group.groupName == groupName)) {
            this.groups.splice(i, 1);
            break;
        }
    }
    return group;
};
ReportDefinition.prototype.getReportGroup = function(groupName) {
    var group = null;
    for (var i = 0; i < this.groups.length; i++) {
        group = this.groups[i];
        if ((group.groupName == groupName)) {
            break;
        }
    }
    return group;
};
ReportDefinition.prototype.addGroup = function(groupName) {
    var groupHeader = new ReportSection(SectionType.GROUP_HEADER, this, groupName);
    var reportGroup = new ReportGroup(groupHeader, groupName);
    this.groups.push(reportGroup);
    return reportGroup;
};
ReportDefinition.prototype.removeColumn = function(index) {
    var column = this.columns[index];
    if (column == null) {
        alert("Coluna nao encontrada");
        return;
    }
    this.removeColumnElements(column);
    this.iteratorOverRows((function(){
    var _InlineType = function(){};

    stjs.extend(_InlineType, ReportDefinition.RowIterator);

    _InlineType.prototype.row = function(reportRow) {
        var tdForColumn = reportRow.getTdForColumn(column);
        reportRow.row.removeChild(tdForColumn);
    };
    _InlineType.prototype.titleRow = function(reportRow) {
        this.readjustColspan(reportRow.row);
    };
    _InlineType.prototype.labelRow = function(reportRow) {
        this.readjustColspan(reportRow);
    };
    _InlineType.prototype.readjustColspan = function(reportRow) {
        if (reportRow.cells.length > 0) {
            if (reportRow.cells[0].colSpan > 1) {
                reportRow.cells[0].colSpan--;
            }
        }
    };
    _InlineType.$typeDescription=stjs.copyProps(ReportDefinition.RowIterator.$typeDescription, {});
    
    return new _InlineType();
    })());
    next.util.removeItem(this.columns, column);
    this.designer.writeXml();
};
ReportDefinition.prototype.removeColumnElements = function(column) {
    var bigThis = this;
    this.iteratorOverRows((function(){
    var _InlineType = function(){};

    stjs.extend(_InlineType, ReportDefinition.RowIterator);

    _InlineType.prototype.titleRow = function(reportRow) {
    };
    _InlineType.prototype.labelRow = function(reportRow) {
    };
    _InlineType.prototype.row = function(reportRow) {
        var element = bigThis.getElementForRowAndColumn(reportRow, column);
        if (element != null) {
            if (element.layoutItem != null) {
                bigThis.designer.layoutManager.remove(element.layoutItem);
            } else {
                bigThis.remove(element);
            }
        }
    };
    _InlineType.$typeDescription=stjs.copyProps(ReportDefinition.RowIterator.$typeDescription, {});
    
    return new _InlineType();
    })());
};
ReportDefinition.prototype.remove = function(element) {
    var cell = this.getCellForElement(element);
    cell.removeChild(element.getNode());
    next.util.removeItem(this.elements, element);
};
ReportDefinition.prototype.getCellForElement = function(element) {
    var row = element.row;
    var column = element.column;
    return this.getCellForRowAndColumn(row, column);
};
ReportDefinition.prototype.getCellForRowAndColumn = function(row, column) {
    return row.row.cells[column.getIndex()];
};
ReportDefinition.prototype.addColumn = function() {
    var reportColumn = new ReportColumn(this);
    this.columns.push(reportColumn);
    this.iteratorOverRows((function(){
    var _InlineType = function(){};

    stjs.extend(_InlineType, ReportDefinition.RowIterator);

    _InlineType.prototype.titleRow = function(reportRow) {
        if (reportRow.row.cells.length > 0) {
            reportRow.row.cells[0].colSpan = reportColumn.getIndex() + 1;
        }
    };
    _InlineType.prototype.row = function(reportRow) {
        reportRow.row.insertCell(reportColumn.getIndex());
    };
    _InlineType.prototype.labelRow = function(reportRow) {
        reportRow.cells[0].colSpan = reportColumn.getIndex() + 1;
    };
    _InlineType.$typeDescription=stjs.copyProps(ReportDefinition.RowIterator.$typeDescription, {});
    
    return new _InlineType();
    })());
};
ReportDefinition.prototype.iteratorOverRows = function(rowIterator) {
    var allSections = this.getAllSections();
    rowIterator.titleRow(this.sectionTitle.rows[0]);
    rowIterator.labelRow(this.sectionTitle.labelRow);
    for (var key in allSections) {
        if (!(allSections).hasOwnProperty(key)) continue;
        var section = allSections[key];
        var labelRow = section.labelRow;
        rowIterator.labelRow(labelRow);
        var rows = section.rows;
        for (var rowkey in rows) {
            if (!(rows).hasOwnProperty(rowkey)) continue;
            var row = rows[rowkey];
            rowIterator.row(row);
        }
    }
};
ReportDefinition.prototype.addElement = function(element, section, columnIndex) {
    var row = section.getLastRow();
    var column = this.getColumnByIndex(columnIndex);
    this.addElementToRowAndColumn(element, row, column);
};
ReportDefinition.prototype.getElementForRowAndColumn = function(row, column) {
    for (var key in this.elements) {
        if (!(this.elements).hasOwnProperty(key)) continue;
        var element = this.elements[key];
        if ((element.row == row) && (element.column == column)) {
            return element;
        }
    }
    return null;
};
ReportDefinition.prototype.addElementToRowAndColumn = function(element, row, column) {
    element.row = row;
    element.column = column;
    this.elements.push(element);
    var td = row.getTdForColumn(column);
    var div = window.document.createElement("DIV");
    div.innerHTML = element.toString();
    div.style.padding = "1px";
    td.appendChild(div);
    element.setNode(div);
    var bigThis = this;
    div.onclick = function(p1) {
        bigThis.selectItem(element, true);
        return true;
    };
};
ReportDefinition.prototype.selectItem = function(el, blur) {
    //		designer.filterArea.blur();
    if (blur) {
        this.designer.blurAllBut(this);
    }
    if (el.layoutItem != null) {
        el = el.layoutItem.getElements()[0];
    }
    if (this.selectedElement != null) {
        this.unselectSelectedItem();
        if ((this.selectedElement == el)) {
            this.selectedElement = null;
            return;
        }
    }
    if (el.layoutItem != null) {
        for (var i = 0; i < el.layoutItem.getElements().length; i++) {
            this.markSelectItem(el.layoutItem.getElements()[i]);
        }
    } else {
        this.markSelectItem(el);
    }
    this.selectedElement = el;
    if (el.onFocus != null) {
        el.onFocus();
    }
};
//		}
ReportDefinition.prototype.unselectSelectedItem = function() {
    if (this.selectedElement != null) {
        if (this.selectedElement.layoutItem != null) {
            for (var i = 0; i < this.selectedElement.layoutItem.getElements().length; i++) {
                this.unselectItem(this.selectedElement.layoutItem.getElements()[i]);
            }
        } else {
            this.unselectItem(this.selectedElement);
        }
    }
    this.designer.hideInputLabel();
    this.designer.hideInputDatePattern();
    this.designer.hideInputNumberPattern();
    this.designer.hideInputPatternGroup();
    this.designer.hideAggregate();
    this.designer.labelInput.value = "";
    this.designer.patternDateInput.value = "";
};
ReportDefinition.prototype.blur = function() {
    this.unselectSelectedItem();
    this.selectedElement = null;
};
ReportDefinition.prototype.markSelectItem = function(el) {
    el.getNode().style.border = "1px dotted gray";
    el.getNode().style.backgroundColor = "#FFD";
    el.getNode().style.padding = "0px";
};
ReportDefinition.prototype.unselectItem = function(reportElement) {
    reportElement.getNode().style.border = "";
    reportElement.getNode().style.backgroundColor = "";
    reportElement.getNode().style.padding = "1px";
};
ReportDefinition.prototype.getColumnByIndex = function(column) {
    while (this.columns.length <= column) {
        this.addColumn();
    }
    return this.columns[column];
};
ReportDefinition.$typeDescription={"designTable":"Table", "sectionTitle":"ReportSection", "sectionDetailHeader":"ReportSection", "sectionDetail":"ReportSection", "columns":{name:"Array", arguments:["ReportColumn"]}, "groups":{name:"Array", arguments:["ReportGroup"]}, "elements":{name:"Array", arguments:["ReportElement"]}, "designer":"ReportDesigner", "selectedElement":"ReportElement"};


var ReportGroup = function(groupHeader, groupName) {

    this.groupHeader = groupHeader;
    this.groupName = groupName;
};
ReportGroup.prototype.groupHeader = null;
ReportGroup.prototype.groupName = null;
ReportGroup.$typeDescription={"groupHeader":"ReportSection"};


SectionType =  stjs.enumeration(
    "TITLE", 
    "GROUP_HEADER", 
    "DETAIL_HEADER", 
    "DETAIL"
);

var ReportSection = function(sectionType, definition, group) {

    this.sectionType = sectionType;
    this.definition = definition;
    this.group = group;
    this.rows = [];
    var insertAt = definition.designTable.rows.length;
    if (group != null) {
        insertAt -= 4;
    }
    this.labelRow = definition.designTable.insertRow(insertAt);
    this.labelRow.id = "section" + sectionType + (group != null ? group : "") + "-L";
    next.style.addClass(this.labelRow, "sectiondecorator");
    var labelRowTd = this.labelRow.insertCell(0);
    labelRowTd.innerHTML = sectionType.toString() + (group != null ? " [" + group + "]" : "");
    if (definition.columns.length > 0) {
        labelRowTd.colSpan = definition.columns.length;
    }
    next.style.addClass(labelRowTd, "sectionLabel");
    this.rows.push(this.createRow());
};
ReportSection.prototype.rows = null;
ReportSection.prototype.group = null;
ReportSection.prototype.definition = null;
ReportSection.prototype.labelRow = null;
ReportSection.prototype.sectionType = null;
ReportSection.prototype.clear = function() {
    this.labelRow.parentNode.removeChild(this.labelRow);
    for (var i = 0; i < this.rows.length; i++) {
        var row = this.rows[i];
        row.row.parentNode.removeChild(row.row);
    }
};
ReportSection.prototype.getLastRow = function() {
    if (this.rows.length == 0) {
        alert("error: report section " + this.sectionType + " does not have rows");
        return null;
    }
    return this.rows[this.rows.length - 1];
};
ReportSection.prototype.getRow = function(index) {
    while (this.rows.length <= index) {
        this.rows.push(this.createRow());
    }
    return this.rows[index];
};
ReportSection.prototype.createRow = function() {
    var labelRowIndex = this.labelRow.rowIndex;
    var insertInIndex = labelRowIndex + this.rows.length + 1;
    var row = this.definition.designTable.insertRow(insertInIndex);
    //next.style.addClass(row, sectionType.toString());
    for (var i = 0; i < this.definition.columns.length; i++) {
        row.insertCell(0);
    }
    return new ReportRow(this, row, this.rows.length);
};
ReportSection.$typeDescription={"rows":{name:"Array", arguments:["ReportRow"]}, "definition":"ReportDefinition", "labelRow":"TableRow", "sectionType":{name:"Enum", arguments:["SectionType"]}};


var ReportRow = function(section, row, index) {

    this.section = section;
    this.row = row;
    this.index = index;
};
ReportRow.prototype.row = null;
ReportRow.prototype.index = null;
ReportRow.prototype.section = null;
ReportRow.prototype.getTdForColumn = function(column) {
    if (column.getIndex() >= this.row.cells.length) {
        alert("Error: There is no column " + column.getIndex() + " for row " + this.row.rowIndex);
    }
    return this.row.cells[column.getIndex()];
};
ReportRow.$typeDescription={"row":"TableRow", "section":"ReportSection"};


var ReportColumn = function(reportDefinition) {

    this.reportDefinition = reportDefinition;
};
ReportColumn.prototype.reportDefinition = null;
ReportColumn.prototype.getIndex = function() {
    //return reportDefinition.columns.indexOf(this);
    return next.util.indexOf(this.reportDefinition.columns, this);
};
ReportColumn.$typeDescription={"reportDefinition":"ReportDefinition"};

