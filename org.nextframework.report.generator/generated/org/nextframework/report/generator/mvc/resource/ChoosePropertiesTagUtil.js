var ChoosePropertiesTagUtil = function(){};

ChoosePropertiesTagUtil.OPEN = "/resource/org/nextframework/report/renderer/html/resource/mais.gif";
ChoosePropertiesTagUtil.CLOSE = "/resource/org/nextframework/report/renderer/html/resource/menos.gif";
ChoosePropertiesTagUtil.application = null;
ChoosePropertiesTagUtil.install = function(app) {
    ChoosePropertiesTagUtil.application = app;
    var table = ChoosePropertiesTagUtil.el("propertiesTable");
    var rows = table.rows;
    for (var i = 1; i < rows.length; i++) {
        //row 0 is placeholder
        var row = rows[i];
        var openClose = next.dom.getInnerElementById(row, "openCloseBtn");
        openClose.style.visibility = "hidden";
        if (i + 1 < rows.length) {
            var row2 = rows[i + 1];
            var row1Property = row.getAttribute("data-forProperty");
            var row2Property = row2.getAttribute("data-forProperty");
            if (row2Property.length > row1Property.length + 1) {
                var base = row2Property.substring(0, row1Property.length + 1);
                if ((base == row1Property + ".")) {
                    openClose.style.visibility = "";
                    ChoosePropertiesTagUtil.colapseGroup(row, rows);
                    ChoosePropertiesTagUtil.installOpenCloseButton(openClose, row, rows);
                }
            }
        }
    }
};
ChoosePropertiesTagUtil.installOpenCloseButton = function(openClose, row, rows) {
    openClose.onclick = function(p1) {
        var open = openClose.getAttribute("data-open");
        if (open == null || ("false" == open)) {
            openClose.src = ChoosePropertiesTagUtil.application + ChoosePropertiesTagUtil.CLOSE;
            ChoosePropertiesTagUtil.openGroup(row, rows);
            openClose.setAttribute("data-open", "true");
        } else {
            openClose.src = ChoosePropertiesTagUtil.application + ChoosePropertiesTagUtil.OPEN;
            ChoosePropertiesTagUtil.colapseGroup(row, rows);
            openClose.setAttribute("data-open", "false");
        }
        return null;
    };
};
ChoosePropertiesTagUtil.openGroup = function(row, rows) {
    var row1Property = row.getAttribute("data-forProperty");
    for (var i = row.rowIndex + 1; i < rows.length; i++) {
        var row2 = rows[i];
        var openClose = next.dom.getInnerElementById(row2, "openCloseBtn");
        openClose.src = ChoosePropertiesTagUtil.application + ChoosePropertiesTagUtil.OPEN;
        openClose.setAttribute("data-open", "false");
        var row2Property = row2.getAttribute("data-forProperty");
        if (row2Property.length > row1Property.length + 1) {
            var base = row2Property.substring(0, row1Property.length + 1);
            if ((base == row1Property + ".")) {
                if (row2Property.substring(base.length).indexOf('.') > 0) {
                    continue;
                }
                row2.style.display = "";
            } else {
                break;
            }
        } else {
            break;
        }
    }
};
ChoosePropertiesTagUtil.colapseGroup = function(row, rows) {
    for (var i = row.rowIndex + 1; i < rows.length; i++) {
        var row2 = rows[i];
        var row1Property = row.getAttribute("data-forProperty");
        var row2Property = row2.getAttribute("data-forProperty");
        if (row2Property.length > row1Property.length + 1) {
            var base = row2Property.substring(0, row1Property.length + 1);
            if ((base == row1Property + ".")) {
                row2.style.display = "none";
            } else {
                break;
            }
        }
    }
};
ChoosePropertiesTagUtil.el = function(id) {
    return next.dom.toElement(id);
};
ChoosePropertiesTagUtil.$typeDescription={};

