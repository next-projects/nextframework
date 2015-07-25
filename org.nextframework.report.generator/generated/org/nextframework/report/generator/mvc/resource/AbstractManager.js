var AbstractManager = function(designer, attachedView) {

    this.designer = designer;
    this.attachedView = attachedView;
    this.objects = [];
    var bigThis = this;
    if (attachedView.constructor ==  ReportGeneratorSelectManyBoxView) {
        var selectManyAttachedView = attachedView;
        (selectManyAttachedView.getFrom())["onAdd"] = function(p1) {
            var properties = (p1)["properties"];
            if (bigThis.getMaximumItems() > 0 && selectManyAttachedView.getTo().options.length > bigThis.getMaximumItems()) {
                alert("Numero maximo de itens atingido");
                selectManyAttachedView.unselect(p1.value);
            } else {
                bigThis.addElement(p1.value, properties);
            }
        };
        (selectManyAttachedView.getFrom())["onRemove"] = function(p1) {
            var properties = (p1)["properties"];
            bigThis.remove(p1.value);
        };
    }
};
AbstractManager.prototype.designer = null;
AbstractManager.prototype.objects = null;
AbstractManager.prototype.attachedView = null;
AbstractManager.prototype.selectElement = function(name, pattern) {
    (this.attachedView).select(name, null);
};
//to select in the selectmanybox the properties are not necessary
AbstractManager.prototype.contains = function(name) {
    for (var i = 0; i < this.objects.length; i++) {
        if ((this.objects[i].name == name)) {
            return true;
        }
    }
    return false;
};
AbstractManager.prototype.getByName = function(name) {
    for (var key in this.objects) {
        if (!(this.objects).hasOwnProperty(key)) continue;
        var el = this.objects[key];
        if ((el.name == name)) {
            return el;
        }
    }
    return null;
};
AbstractManager.prototype.remove = function(name) {
    this.attachedView.unselect(name);
    try {
        var i = 0;
        for (var key in this.objects) {
            if (!(this.objects).hasOwnProperty(key)) continue;
            var el = this.objects[key];
            if ((el.name == name)) {
                this.objects.splice(i, 1);
                this.onRemove(name);
                return;
            }
            i++;
        }
    } finally {
        this.designer.writeXml();
    }
};


AbstractManager.prototype.addElement = function(name, properties) {
    if (!this.accept(name, properties)) {
        return;
    }
    this.objects.push(new SimpleNamedObject(name, properties));
    this.attachedView.select(name, properties);
    this.onAdd(name, properties);
    this.designer.writeXml();
};


AbstractManager.$typeDescription={"designer":"ReportDesigner", "objects":{name:"Array", arguments:["SimpleNamedObject"]}, "attachedView":"SelectView"};


var ReportGroupManager = function(designer, attachedView) {
    AbstractManager.call(this, designer, attachedView);
    var bigThis = this;
    attachedView.onselectto = function(p1) {
        if (p1 != null) {
            bigThis.selectElementByName(p1.value, true);
        }
    };
};
stjs.extend(ReportGroupManager, AbstractManager);

ReportGroupManager.prototype.selectElement = function(name, pattern) {
    AbstractManager.prototype.selectElement.call(this, name, pattern);
    this.getElement(this.getElementByName(name)).pattern = pattern;
};
ReportGroupManager.prototype.getElement = function(namedObject) {
    return namedObject.value["element"];
};
ReportGroupManager.prototype.getElementByName = function(name) {
    for (var key in this.objects) {
        if (!(this.objects).hasOwnProperty(key)) continue;
        var sno = this.objects[key];
        if ((sno.name == name)) {
            return sno;
        }
    }
    return null;
};
ReportGroupManager.prototype.selectElementByName = function(groupName, cascadeToDefinition) {
    this.select(this.getElementByName(groupName), cascadeToDefinition);
};
ReportGroupManager.prototype.select = function(sno, cascadeToDefinition) {
    if (sno == null) {
        return;
    }
    if (cascadeToDefinition) {
        this.designer.definition.selectItem(this.getElement(sno), false);
    }
    eval("showdesignerTab('designerTab_2', 2, 'designerTab_link_2'); ");
    (this.attachedView).markSelected(sno.name);
    if (ReportPropertyConfigUtils.isDate(sno.value)) {
        ReportPropertyConfigUtils.configurePatternInputToField(this.getElement(sno), this.designer.patternDateInputGroup);
    }
};
ReportGroupManager.prototype.onRemove = function(name) {
    this.designer.definition.removeGroup(name);
};
ReportGroupManager.prototype.onAdd = function(groupName, properties) {
    var reportGroup = this.designer.definition.addGroup(groupName);
    if (this.designer.definition.columns.length == 0) {
        this.designer.definition.addColumn();
    }
    var element = new FieldReportElement(groupName, properties);
    properties["element"] = element;
    var bigThis = this;
    element.onFocus = function() {
        bigThis.selectElementByName(groupName, false);
    };
    this.designer.definition.addElement(element, reportGroup.groupHeader, 0);
};
ReportGroupManager.prototype.toString = function() {
    var value = "        <groups>\n";
    for (var key in this.objects) {
        if (!(this.objects).hasOwnProperty(key)) continue;
        var el = this.objects[key];
        var pattern = this.getElement(el).pattern;
        if (pattern == null || (pattern == "")) {
            value += "            <group name='" + el.name + "'/>\n";
        } else {
            value += "            <group name='" + el.name + "' pattern=\"" + pattern + "\"/>\n";
        }
    }
    value += "        </groups>\n";
    return value;
};
ReportGroupManager.prototype.accept = function(name, properties) {
    return ReportPropertyConfigUtils.isGroupable(properties);
};
ReportGroupManager.prototype.getMaximumItems = function() {
    return 4;
};
ReportGroupManager.$typeDescription=stjs.copyProps(AbstractManager.$typeDescription, {});


var ReportFilterManager = function(designer, attachedView) {
    AbstractManager.call(this, designer, attachedView);
    var bigThis = this;
    attachedView.onselectto = function(p1) {
        if (p1 != null) {
            bigThis.onSelectElement(p1.value);
        }
    };
};
stjs.extend(ReportFilterManager, AbstractManager);

ReportFilterManager.prototype.onSelectElement = function(value) {
    var bigThis = this;
    var properties = this.getByName(value).value;
    var filterLabel = ReportPropertyConfigUtils.getFilterDisplayName(properties);
    this.designer.showFilterLabel();
    this.designer.filterLabel.value = filterLabel;
    this.designer.filterLabel.onchange = function(p1) {
        ReportPropertyConfigUtils.setFilterDisplayName(bigThis.getByName(value).value, bigThis.designer.filterLabel.value);
        bigThis.designer.writeXml();
        return true;
    };
    if (ReportPropertyConfigUtils.isDate(properties)) {
        this.designer.showFilterPreSelectDate();
        this.configureFilterPreSelectDateCombo(properties);
    } else {
        this.designer.hideFilterPreSelectDate();
    }
    if (ReportPropertyConfigUtils.isEntity(properties)) {
        this.designer.showFilterPreSelectEntity();
        this.configureFilterPreSelectEntityCombo(properties);
    } else {
        this.designer.hideFilterPreSelectEntity();
    }
    if (ReportPropertyConfigUtils.isEntity(properties)) {
        this.designer.showFilterSelectMultiple();
    } else {
        this.designer.hideFilterSelectMultiple();
    }
    if (!ReportPropertyConfigUtils.isFilterRequired(properties)) {
    } else {
        this.designer.hideFilterRequired();
    }
    this.designer.showFilterRequired();
    this.designer.filterRequired.checked = ReportPropertyConfigUtils.isFilterRequired(properties);
    this.designer.filterRequired.onchange = function(p1) {
        ReportPropertyConfigUtils.setFilterRequired(bigThis.getByName(value).value, bigThis.designer.filterRequired.checked);
        bigThis.designer.writeXml();
        return true;
    };
    this.designer.filterSelectMultiple.checked = ReportPropertyConfigUtils.isFilterSelectMultiple(properties);
    this.designer.filterSelectMultiple.onchange = function(p1) {
        ReportPropertyConfigUtils.setFilterSelectMultiple(bigThis.getByName(value).value, bigThis.designer.filterSelectMultiple.checked);
        bigThis.designer.writeXml();
        return true;
    };
};
ReportFilterManager.prototype.configureFilterPreSelectDateCombo = function(properties) {
    var bigThis = this;
    this.designer.filterPreSelectDate.selectedIndex = 0;
    next.dom.setSelectedValue(this.designer.filterPreSelectDate, ReportPropertyConfigUtils.getFilterPreSelectDate(properties));
    this.designer.filterPreSelectDate.onchange = function(p1) {
        var selectedValue = next.dom.getSelectedValue(bigThis.designer.filterPreSelectDate);
        ReportPropertyConfigUtils.setFilterPreSelectDate(properties, selectedValue);
        bigThis.designer.writeXml();
        return true;
    };
};
ReportFilterManager.prototype.configureFilterPreSelectEntityCombo = function(properties) {
    var bigThis = this;
    //do ajax
    var type = ReportPropertyConfigUtils.getType(properties);
    var entityValue = ReportPropertyConfigUtils.getFilterPreSelectEntity(properties);
    //boolean multiple = ReportPropertyConfigUtils.isFilterSelectMultiple(properties);
    var path = this.designer.controllerPath;
    this.designer.filterPreSelectEntity.options.length = 0;
    this.designer.filterPreSelectEntity.add(new Option("", "<null>"));
    next.ajax.newRequest().setUrl(path).setAction("getFilterList").setParameter("type", type).setCallback(function(p1) {
        for (var k in p1) {
            if (!(p1).hasOwnProperty(k)) continue;
            var item = p1[k];
            var id = item[0];
            var value = item[1];
            bigThis.designer.filterPreSelectEntity.add(new Option(value, id));
        }
        next.dom.setSelectedValue(bigThis.designer.filterPreSelectEntity, entityValue);
        if (entityValue == null) {
            ReportPropertyConfigUtils.setFilterPreSelectEntity(properties, null);
        }
    }).send();
    this.designer.filterPreSelectEntity.onchange = function(p1) {
        var selectedValue = next.dom.getSelectedValue(bigThis.designer.filterPreSelectEntity);
        ReportPropertyConfigUtils.setFilterPreSelectEntity(properties, selectedValue);
        bigThis.designer.writeXml();
        return true;
    };
};
ReportFilterManager.prototype.accept = function(name, properties) {
    return (!ReportPropertyConfigUtils.isTransient(properties) || ReportPropertyConfigUtils.isFilterable(properties)) && !ReportPropertyConfigUtils.isExtended(properties) && !ReportPropertyConfigUtils.isNumber(properties);
};
ReportFilterManager.prototype.onAdd = function(name, properties) {
};
ReportFilterManager.prototype.onRemove = function(name) {
};
ReportFilterManager.prototype.toString = function() {
    var value = "        <filters>\n";
    for (var key in this.objects) {
        if (!(this.objects).hasOwnProperty(key)) continue;
        var el = this.objects[key];
        var fdn = ReportPropertyConfigUtils.getFilterDisplayName(el.value);
        var fpd = ReportPropertyConfigUtils.getFilterPreSelectDate(el.value);
        var fpe = ReportPropertyConfigUtils.getFilterPreSelectEntity(el.value);
        var dn = ReportPropertyConfigUtils.getDisplayName(el.value);
        value += "            <filter name='" + el.name + "'";
        if (fdn != dn) {
            value += " filterDisplayName='" + fdn + "'";
        }
        if (fpd != null) {
            value += " preSelectDate='" + fpd + "'";
        }
        if (fpe != null) {
            value += " preSelectEntity='" + fpe + "'";
        }
        if (ReportPropertyConfigUtils.isFilterSelectMultiple(el.value)) {
            value += " filterSelectMultiple='true'";
        }
        if (ReportPropertyConfigUtils.isFilterRequired(el.value)) {
            value += " requiredFilter='true'";
        }
        value += "/>\n";
    }
    value += "        </filters>\n";
    return value;
};
ReportFilterManager.prototype.getMaximumItems = function() {
    return 0;
};
ReportFilterManager.$typeDescription=stjs.copyProps(AbstractManager.$typeDescription, {});


var ReportCalculatedFieldsManager = function(designer, view) {

    this.designer = designer;
    this.view = view;
    this.objects = [];
};
ReportCalculatedFieldsManager.prototype.designer = null;
ReportCalculatedFieldsManager.prototype.objects = null;
ReportCalculatedFieldsManager.prototype.view = null;
ReportCalculatedFieldsManager.prototype.toString = function() {
    var value = "        <calculatedFields>\n";
    for (var key in this.objects) {
        if (!(this.objects).hasOwnProperty(key)) continue;
        var el = this.objects[key];
        var exp = el.value["expression"];
        var display = el.value["displayName"];
        var formatAs = el.value["formatAs"];
        var formatTimeDetail = el.value["formatTimeDetail"];
        var formatWithDecimal = el.value["formatWithDecimal"];
        var processors = el.value["processors"];
        value += "            <calculatedField " + "name='" + el.name + "' " + "expression='" + exp + "' " + "displayName='" + display + "' " + "formatAs='" + formatAs + "' " + "formatTimeDetail='" + formatTimeDetail + "' " + "formatWithDecimal='" + formatWithDecimal + "' " + "processors='" + processors + "' " + "/>\n";
    }
    value += "        </calculatedFields>\n";
    return value;
};
ReportCalculatedFieldsManager.prototype.add = function(name, calculationProperties) {
    var editing = this.getByName(name) != null;
    if (editing) {
        this.remove(name);
    }
    this.objects.push(new SimpleNamedObject(name, calculationProperties));
    if (!editing) {
        this.view.add(new Option(calculationProperties["displayName"], name));
    }
    this.designer.writeXml();
};
ReportCalculatedFieldsManager.prototype.remove = function(name) {
    var i = 0;
    for (var key in this.objects) {
        if (!(this.objects).hasOwnProperty(key)) continue;
        var el = this.objects[key];
        if ((el.name == name)) {
            this.objects.splice(i, 1);
            return;
        }
        i++;
    }
};
ReportCalculatedFieldsManager.prototype.getByName = function(name) {
    var i = 0;
    for (var key in this.objects) {
        if (!(this.objects).hasOwnProperty(key)) continue;
        var el = this.objects[key];
        if ((el.name == name)) {
            return el;
        }
        i++;
    }
    return null;
};
ReportCalculatedFieldsManager.$typeDescription={"designer":"ReportDesigner", "objects":{name:"Array", arguments:["SimpleNamedObject"]}, "view":"Select"};

