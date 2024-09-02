var ReportDesigner = function(divId, textAreaId) {

    var bigThis = this;
    this.selectables = [];
    this.avaiableProperties = [];
    this.mainDiv = next.dom.toElement(divId);
    this.outputXml = next.dom.toElement(textAreaId);
    this.fields = {};
    this.definition = new ReportDefinition(this, next.dom.getInnerElementById(this.mainDiv, "designTable"));
    this.selectables.push(this.definition);
    this.reportTitleInput = next.dom.getInnerElementById(this.mainDiv, "reportTitle");
    this.reportTitleInput.onblur = function(p1) {
        bigThis.updateTitle();
        return true;
    };
    this.fieldSelect = this.addSelectManyComponent("fields");
    this.groupSelect = this.addSelectManyComponent("groups");
    this.filterSelect = this.addSelectManyComponent("filters");
    this.labelInput = next.dom.getInnerElementById(this.mainDiv, "label");
    this.patternDateInput = next.dom.getInnerElementById(this.mainDiv, "patternDate");
    this.patternNumberInput = next.dom.getInnerElementById(this.mainDiv, "patternNumber");
    this.aggregateInput = next.dom.getInnerElementById(this.mainDiv, "aggregate");
    this.aggregateTypeInput = next.dom.getInnerElementById(this.mainDiv, "aggregateType");
    this.patternDateInputGroup = next.dom.getInnerElementById(this.mainDiv, "patternDateGroup");
    this.filterLabel = next.dom.getInnerElementById(this.mainDiv, "filterLabel");
    this.filterFixedCriteria = next.dom.getInnerElementById(this.mainDiv, "filterFixedCriteria");
    this.filterPreSelectDate = next.dom.getInnerElementById(this.mainDiv, "filterPreSelectDate");
    this.filterPreSelectEntity = next.dom.getInnerElementById(this.mainDiv, "filterPreSelectEntity");
    this.filterSelectMultiple = next.dom.getInnerElementById(this.mainDiv, "filterSelectMultiple");
    this.filterRequired = next.dom.getInnerElementById(this.mainDiv, "filterRequired");
    this.calculatedFieldsSelect = next.dom.toElement("calculatedFields");
    this.charts = next.dom.getInnerElementById(this.mainDiv, "charts");
    this.groupManager = new ReportGroupManager(this, this.groupSelect);
    this.filterManager = new ReportFilterManager(this, this.filterSelect);
    this.layoutManager = new ReportLayoutManager(this, this.fieldSelect);
    this.calculatedFieldsManager = new ReportCalculatedFieldsManager(this, this.calculatedFieldsSelect);
    this.reportData = new ReportData(this);
    this.hideAll();
};
ReportDesigner.instance = null;
ReportDesigner.getInstance = function() {
    if (ReportDesigner.instance == null) {
        ReportDesigner.instance = new ReportDesigner("designerArea", "xml");
    }
    return ReportDesigner.instance;
};
ReportDesigner.prototype.controllerPath = null;
ReportDesigner.prototype.reportTitle = null;
ReportDesigner.prototype.reportPublic = null;
ReportDesigner.prototype.mainDiv = null;
ReportDesigner.prototype.outputXml = null;
ReportDesigner.prototype.fields = null;
ReportDesigner.prototype.avaiableProperties = null;
ReportDesigner.prototype.fieldSelect = null;
ReportDesigner.prototype.groupSelect = null;
ReportDesigner.prototype.filterSelect = null;
ReportDesigner.prototype.calculatedFieldsSelect = null;
ReportDesigner.prototype.definition = null;
ReportDesigner.prototype.calculatedFieldsManager = null;
ReportDesigner.prototype.layoutManager = null;
ReportDesigner.prototype.groupManager = null;
ReportDesigner.prototype.filterManager = null;
ReportDesigner.prototype.reportTitleInput = null;
ReportDesigner.prototype.reportData = null;
ReportDesigner.prototype.labelInput = null;
ReportDesigner.prototype.patternDateInput = null;
ReportDesigner.prototype.patternNumberInput = null;
ReportDesigner.prototype.aggregateInput = null;
ReportDesigner.prototype.aggregateTypeInput = null;
ReportDesigner.prototype.patternDateInputGroup = null;
ReportDesigner.prototype.filterLabel = null;
ReportDesigner.prototype.filterFixedCriteria = null;
ReportDesigner.prototype.filterPreSelectDate = null;
ReportDesigner.prototype.filterPreSelectEntity = null;
ReportDesigner.prototype.filterSelectMultiple = null;
ReportDesigner.prototype.filterRequired = null;
ReportDesigner.prototype.charts = null;
ReportDesigner.prototype.selectables = null;
ReportDesigner.prototype.addSelectManyComponent = function(name) {
    var result = new ReportGeneratorSelectManyBoxView(next.dom.toElement(name + "_from_"));
    result.setUsePropertyAsLabel("displayName");
    var bigThis = this;
    result.onselect = function(p1) {
        bigThis.blurAllBut(result);
    };
    this.selectables.push(result);
    return result;
};
ReportDesigner.prototype.blurAllBut = function(select) {
    for (var key in this.selectables) {
        if (!(this.selectables).hasOwnProperty(key)) continue;
        var value = this.selectables[key];
        if (value != select) {
            value.blur();
        }
    }
};
ReportDesigner.prototype.hideAll = function() {
    this.hideInputLabel();
    this.hideInputDatePattern();
    this.hideInputNumberPattern();
    this.hideAggregate();
    this.hideInputPatternGroup();
    this.hideFilterLabel();
    this.hideFilterFixedCriteria();
    this.hideFilterPreSelectDate();
    this.hideFilterPreSelectEntity();
    this.hideFilterSelectMultiple();
    this.hideFilterRequired();
};
ReportDesigner.prototype.removeSelectedCalculatedProperty = function() {
    var selectedIndex = this.calculatedFieldsSelect.selectedIndex;
    if (selectedIndex >= 0) {
        var fieldName = this.calculatedFieldsSelect.options[selectedIndex].value;
        this.calculatedFieldsManager.remove(fieldName);
        this.fieldSelect.unselect(fieldName);
        next.dom.removeSelectValue(this.fieldSelect.getFrom(), fieldName);
        this.calculatedFieldsSelect.removeChild(this.calculatedFieldsSelect.options[selectedIndex]);
    }
    this.writeXml();
    alert("O campo calculado foi removido. É necessário remover também (se houver) todas as referências para esse campo no relatório.");
};
ReportDesigner.prototype.editCalculatedProperty = function() {
    var calculatedFields = next.dom.toElement("calculatedFields");
    var calculatedFieldSelected = next.dom.getSelectedValue(calculatedFields);
    if (calculatedFieldSelected == null) {
        return;
    }
    this.showAddCalculatedProperty();
    var calculationExpression = next.dom.toElement("calculationExpression");
    var calculationDisplayName = next.dom.toElement("calculationDisplayName");
    var calculationName = next.dom.toElement("calculationName");
    var calculationProcessor = next.dom.toElement("calculationProcessor");
    var calculationFormatAsNumber = next.dom.toElement("calculationFormatAsNumber");
    var calculationFormatAsTime = next.dom.toElement("calculationFormatAsTime");
    var calculationFormatAsTimeDetail = next.dom.toElement("calculationFormatAsTimeDetail");
    var obj = this.calculatedFieldsManager.getByName(calculatedFieldSelected);
    var exp = obj.value["expression"];
    var display = obj.value["displayName"];
    var formatAs = obj.value["formatAs"];
    var formatTimeDetail = obj.value["formatTimeDetail"];
    var processors = ReportPropertyConfigUtils.getProcessors(obj.value);
    calculationName.value = calculatedFieldSelected;
    calculationDisplayName.disabled = true;
    calculationDisplayName.readOnly = true;
    next.style.addClass(calculationDisplayName, "readOnly");
    calculationExpression.value = exp;
    calculationDisplayName.value = display;
    calculationFormatAsNumber.checked = !(formatAs == "time");
    calculationFormatAsTime.checked = (formatAs == "time");
    next.dom.setSelectedValue(calculationFormatAsTimeDetail, formatTimeDetail);
    next.dom.setSelectedValues(calculationProcessor, processors);
};
ReportDesigner.prototype.showAddCalculatedProperty = function() {
    var bigThis = this;
    var calculationExpression = next.dom.toElement("calculationExpression");
    var calculationDisplayName = next.dom.toElement("calculationDisplayName");
    var calculationName = next.dom.toElement("calculationName");
    var calculationProcessor = next.dom.toElement("calculationProcessor");
    var calculationFormatAsNumber = next.dom.toElement("calculationFormatAsNumber");
    var calculationFormatAsTimeDetail = next.dom.toElement("calculationFormatAsTimeDetail");
    var varDiv = next.dom.toElement("varDiv");
    varDiv.innerHTML = "";
    calculationDisplayName.disabled = false;
    calculationDisplayName.readOnly = false;
    next.style.removeClass(calculationDisplayName, "readOnly");
    calculationExpression.value = "";
    calculationDisplayName.value = "";
    calculationName.value = "";
    calculationProcessor.selectedIndex = 0;
    calculationFormatAsNumber.checked = true;
    calculationFormatAsTimeDetail.selectedIndex = 1;
    for (var key in this.avaiableProperties) {
        if (!(this.avaiableProperties).hasOwnProperty(key)) continue;
        var property = this.avaiableProperties[key];
        var options = this.fields[property];
        //Global.console.info(property + " " + options);
        if (options != null && (ReportPropertyConfigUtils.isNumber(options) || ReportPropertyConfigUtils.isDate(options))) {
            var b = this.createPropertyButton(property, options["displayName"]);
            varDiv.appendChild(b);
        }
    }
    if (calculationDisplayName.getAttribute("data-configured") == null) {
        next.events.attachEvent(calculationDisplayName, "change", function(p1) {
            bigThis.onChangeCalculationVarName(calculationDisplayName, calculationName);
        });
    }
    var panelDiv = window.document.getElementById("calculatedPropertiesWizzard");
    var panelDivParent = panelDiv.parentNode;
    var dialog = new NextDialogs.MessageDialog();
    dialog.setSize(NextDialogs.SIZE_LARGE);
    dialog.setTitle("Configurar campo calculado");
    dialog.appendToBody(panelDiv);
    dialog.setCallback((function(){
    var _InlineType = function(){NextDialogs.DialogCallback.call(this);};

    stjs.extend(_InlineType, NextDialogs.DialogCallback);

    _InlineType.prototype.onClick = function(command, value, button) {
        if ((command == "OK")) {
            bigThis.saveCalculatedProperty();
        }
        panelDivParent.appendChild(panelDiv);
        return true;
    };
    _InlineType.$typeDescription=stjs.copyProps(NextDialogs.DialogCallback.$typeDescription, {});
    
    return new _InlineType();
    })());
    dialog.show();
};
ReportDesigner.prototype.createPropertyButton = function(property, displayName) {
    var bigThis = this;
    var b = next.dom.newElement("button");
    b.className = next.globalMap.get("NextDialogs.button", "button");
    b.innerHTML = displayName;
    b.onclick = function(p1) {
        bigThis.appendToExpression(property);
        return true;
    };
    return b;
};
ReportDesigner.prototype.appendNumberToExpression = function() {
    var bigThis = this;
    var dialog = next.dialogs.showInputNumberDialog("Inserir Número", "Digite o número que deseja inserir na fórmula:");
    dialog.setCallback((function(){
    var _InlineType = function(){NextDialogs.DialogCallback.call(this);};

    stjs.extend(_InlineType, NextDialogs.DialogCallback);

    _InlineType.prototype.onClick = function(command, value, button) {
        if (command == NextDialogs.OK) {
            bigThis.appendToExpression("" + value);
        }
        return true;
    };
    _InlineType.$typeDescription=stjs.copyProps(NextDialogs.DialogCallback.$typeDescription, {});
    
    return new _InlineType();
    })());
};
ReportDesigner.prototype.appendToExpression = function(varText) {
    var calculationExpression = next.dom.toElement("calculationExpression");
    if (varText.charAt(0) == '$') {
        var c = varText.charAt(1);
        if (c == 'B') {
            var result = calculationExpression.value;
            result = result.substring(0, result.length - 1);
            var space = result.lastIndexOf(' ');
            if (space <= 0) {
                result = "";
            } else {
                result = result.substring(0, space) + " ";
            }
            calculationExpression.value = result;
        } else if (c == 'C') {
            calculationExpression.value = "";
        } else if (c == 'N') {
            calculationExpression.value += "$now" + " ";
        }
    } else {
        calculationExpression.value += varText + " ";
    }
    var errorMessage = ReportPropertyConfigUtils.validateExpression(calculationExpression.value);
    if (errorMessage != null) {
        next.dom.toElement("validationExpressionError").innerHTML = errorMessage;
    } else {
        next.dom.toElement("validationExpressionError").innerHTML = "";
    }
};
ReportDesigner.prototype.onChangeCalculationVarName = function(calculationDisplayName, calculationName) {
    var value = calculationDisplayName.value;
    var result = "";
    for (var i = 0; i < value.length; i++) {
        var c = value.charAt(i);
        if (result == "" && ReportPropertyConfigUtils.isDigit(c)) {
            result += "_";
        } else if (ReportPropertyConfigUtils.isLetter(c) || ReportPropertyConfigUtils.isDigit(c)) {
            result += c;
        } else {
            result += "_";
        }
    }
    calculationName.value = result;
};
ReportDesigner.prototype.showConfigureProperties = function() {
    for (var key in this.avaiableProperties) {
        if (!(this.avaiableProperties).hasOwnProperty(key)) continue;
        var field = this.avaiableProperties[key];
        var checkbox = window.document.getElementById("selProp_" + field);
        if (checkbox != null && !checkbox.disabled) {
            checkbox.checked = false;
        }
    }
    var dialog = new NextDialogs.MessageDialog();
    dialog.setSize(NextDialogs.SIZE_LARGE);
    dialog.setTitle("Configurar campos");
    var panelDiv = window.document.getElementById("propertiesWizzard");
    var panelDivParent = panelDiv.parentNode;
    dialog.appendToBody(panelDiv);
    var bigThis = this;
    dialog.setCallback((function(){
    var _InlineType = function(){NextDialogs.DialogCallback.call(this);};

    stjs.extend(_InlineType, NextDialogs.DialogCallback);

    _InlineType.prototype.onClick = function(command, value, button) {
        panelDivParent.appendChild(panelDiv);
        if ((command == "OK")) {
            bigThis.saveConfigureProperties();
        }
        return true;
    };
    _InlineType.$typeDescription=stjs.copyProps(NextDialogs.DialogCallback.$typeDescription, {});
    
    return new _InlineType();
    })());
    dialog.show();
};
ReportDesigner.prototype.saveConfigureProperties = function() {
    for (var key in this.avaiableProperties) {
        if (!(this.avaiableProperties).hasOwnProperty(key)) continue;
        var field = this.avaiableProperties[key];
        var checkbox = window.document.getElementById("selProp_" + field);
        if (!checkbox.disabled && checkbox.checked) {
            this.addField(checkbox.value, (checkbox)["propertyMetadata"]);
            checkbox.disabled = true;
        }
    }
};
ReportDesigner.prototype.saveCalculatedProperty = function() {
    var calculationExpression = next.dom.toElement("calculationExpression");
    var calculationDisplayName = next.dom.toElement("calculationDisplayName");
    var calculationName = next.dom.toElement("calculationName");
    var calculationProcessor = next.dom.toElement("calculationProcessor");
    var calculationFormatAsNumber = next.dom.toElement("calculationFormatAsNumber");
    var calculationFormatAsTimeDetail = next.dom.toElement("calculationFormatAsTimeDetail");
    var disabled = calculationDisplayName.disabled;
    var expressionMessage = this.getValidationErrorMessage(calculationExpression);
    if (expressionMessage != null) {
        alert(expressionMessage);
        return;
    }
    if (ReportPropertyConfigUtils.isEmpty(calculationName.value)) {
        alert("É necessário dar um nome para a variável");
        next.effects.blink(calculationDisplayName);
        calculationDisplayName.focus();
        return;
    }
    if (!disabled) {
        this.addAvaiableProperty(calculationName.value);
    }
    var formatAs = calculationFormatAsNumber.checked ? "number" : "time";
    var formatTimeDetail = next.dom.getSelectedValue(calculationFormatAsTimeDetail);
    var calculationProperties = {"displayName": calculationDisplayName.value, 
        "expression": calculationExpression.value, 
        "filterable": false, 
        "numberType": true, 
        "calculated": true, 
        "formatAs": formatAs, 
        "formatTimeDetail": formatTimeDetail, 
        "processors": next.util.join(next.dom.getSelectedValues(calculationProcessor), ",")};
    if (!disabled) {
        this.addField(calculationName.value, calculationProperties);
    }
    this.addCalculation(calculationName.value, calculationProperties);
};
ReportDesigner.prototype.getValidationErrorMessage = function(calculationExpression) {
    var exp = calculationExpression.value;
    return ReportPropertyConfigUtils.validateExpression(exp);
};
ReportDesigner.prototype.addCalculation = function(value, calculationProperties) {
    this.calculatedFieldsManager.add(value, calculationProperties);
};
ReportDesigner.prototype.addAvaiableProperty = function(prop) {
    this.avaiableProperties.push(prop);
};
ReportDesigner.prototype.moveItem = function(i) {
    var selectedElement = this.definition.selectedElement;
    this.moveElement(i, selectedElement);
};
ReportDesigner.prototype.moveElement = function(i, selectedElement) {
    if (selectedElement != null) {
        if (selectedElement.layoutItem != null) {
            if (selectedElement.layoutItem.constructor ==  FieldDetail) {
                this.layoutManager.moveFieldDetail(selectedElement.layoutItem, i);
            }
        }
    }
};
ReportDesigner.prototype.removeSelectedDefinitionItem = function() {
    if (this.definition.selectedElement != null) {
        if (this.definition.selectedElement.layoutItem != null) {
            this.layoutManager.selectAndRemove(this.definition.selectedElement.layoutItem);
        } else {
            if (this.definition.selectedElement != null) {
                var isGroupHeader = this.definition.selectedElement.row.section.sectionType == SectionType.GROUP_HEADER;
                if (isGroupHeader) {
                    if (confirm("Deseja excluir o grupo?")) {
                        this.groupManager.remove(this.definition.selectedElement.row.section.group);
                    }
                } else {
                    this.definition.remove(this.definition.selectedElement);
                }
            }
        }
    }
};
ReportDesigner.prototype.removeFilter = function() {
    this.writeXml();
};
ReportDesigner.prototype.removeGroup = function() {
    this.writeXml();
};
ReportDesigner.prototype.filterCurrentSelectedField = function() {
    this.writeXml();
};
ReportDesigner.prototype.groupCurrentSelectedField = function() {
    this.writeXml();
};
ReportDesigner.prototype.setReportTitle = function(reportTitle) {
    this.reportTitle = reportTitle;
    this.reportTitleInput.value = reportTitle;
    this.updateTitle();
};
ReportDesigner.prototype.updateTitle = function() {
    this.reportTitle = this.reportTitleInput.value;
    this.definition.sectionTitle.getRow(0).row.cells[0].innerHTML = this.reportTitle;
    this.definition.sectionTitle.getRow(0).row.cells[0].onclick = function(p1) {
        eval("showdesignerTab('designerTab_0', 0, 'designerTab_link_0'); ");
        return true;
    };
    this.writeXml();
};
ReportDesigner.prototype.addField = function(name, properties) {
    this.fields[name] = properties;
    this.fieldSelect.add(name, properties);
    if (this.groupManager.accept(name, properties)) {
        this.groupSelect.add(name, properties);
    }
    if (this.filterManager.accept(name, properties)) {
        this.filterSelect.add(name, properties);
    }
};
ReportDesigner.prototype.setDataSourceHibernate = function(from) {
    this.reportData.dataSourceProvider = new HibernateDataSourceProvider(from);
};
ReportDesigner.prototype.writeXml = function() {
    var value = "<report name='" + this.reportTitle + "'>\n";
    value += this.reportData.toString();
    value += this.layoutManager.toString();
    value += this.getChartsXmlString();
    value += "</report>";
    this.outputXml.value = value;
};
ReportDesigner.prototype.getChartsXmlString = function() {
    var chartXml = "    <charts>\n";
    var options = this.charts.options;
    for (var i = 0; i < options.length; i++) {
        chartXml += "        " + options[i].value + "\n";
    }
    chartXml += "    </charts>\n";
    return chartXml;
};
ReportDesigner.prototype.showInputLabel = function() {
    next.effects.showProperty(this.labelInput);
};
ReportDesigner.prototype.hideInputLabel = function() {
    next.effects.hideProperty(this.labelInput);
};
ReportDesigner.prototype.showInputDatePattern = function() {
    next.effects.showProperty(this.patternDateInput);
};
ReportDesigner.prototype.hideInputDatePattern = function() {
    next.effects.hideProperty(this.patternDateInput);
};
ReportDesigner.prototype.showInputNumberPattern = function() {
    next.effects.showProperty(this.patternNumberInput);
};
ReportDesigner.prototype.hideInputNumberPattern = function() {
    next.effects.hideProperty(this.patternNumberInput);
};
ReportDesigner.prototype.showAggregate = function() {
    next.effects.showProperty(this.aggregateInput);
    next.effects.showProperty(this.aggregateTypeInput);
};
ReportDesigner.prototype.hideAggregate = function() {
    next.effects.hideProperty(this.aggregateInput);
    next.effects.hideProperty(this.aggregateTypeInput);
};
ReportDesigner.prototype.hideInputPatternGroup = function() {
    next.effects.hideProperty(this.patternDateInputGroup);
};
ReportDesigner.prototype.showFilterLabel = function() {
    next.effects.showProperty(this.filterLabel);
};
ReportDesigner.prototype.hideFilterLabel = function() {
    next.effects.hideProperty(this.filterLabel);
};
ReportDesigner.prototype.showFilterFixedCriteria = function() {
    next.effects.showProperty(this.filterFixedCriteria);
};
ReportDesigner.prototype.hideFilterFixedCriteria = function() {
    next.effects.hideProperty(this.filterFixedCriteria);
};
ReportDesigner.prototype.showFilterPreSelectDate = function() {
    next.effects.showProperty(this.filterPreSelectDate);
};
ReportDesigner.prototype.hideFilterPreSelectDate = function() {
    next.effects.hideProperty(this.filterPreSelectDate);
};
ReportDesigner.prototype.showFilterPreSelectEntity = function() {
    next.effects.showProperty(this.filterPreSelectEntity);
};
ReportDesigner.prototype.hideFilterPreSelectEntity = function() {
    next.effects.hideProperty(this.filterPreSelectEntity);
};
ReportDesigner.prototype.showFilterSelectMultiple = function() {
    next.effects.showProperty(this.filterSelectMultiple);
};
ReportDesigner.prototype.hideFilterSelectMultiple = function() {
    next.effects.hideProperty(this.filterSelectMultiple);
};
ReportDesigner.prototype.showFilterRequired = function() {
    next.effects.showProperty(this.filterRequired);
};
ReportDesigner.prototype.hideFilterRequired = function() {
    next.effects.hideProperty(this.filterRequired);
};
ReportDesigner.prototype.addChart = function(configuration) {
    var op = new Option(configuration.title, configuration.toXmlString());
    op.innerHTML = configuration.title;
    this.updateOptionWithConfiguration(configuration, op);
    this.charts.appendChild(op);
    this.writeXml();
};
ReportDesigner.prototype.updateChart = function(option, configuration) {
    this.updateOptionWithConfiguration(configuration, option);
    option.value = configuration.toXmlString();
    option.innerHTML = configuration.title;
    this.writeXml();
};
ReportDesigner.prototype.updateOptionWithConfiguration = function(configuration, op) {
    var mapOp = op;
    mapOp["configuration"] = configuration;
    configuration.option = op;
};
ReportDesigner.prototype.getSelectedChartConfiguration = function() {
    var selectedIndex = this.charts.selectedIndex;
    if (selectedIndex >= 0) {
        var op = this.charts.options[selectedIndex];
        var mapOp = op;
        var configuration = mapOp["configuration"];
        return configuration;
    }
    return null;
};
ReportDesigner.prototype.removeSelectedChart = function() {
    var selectedIndex = this.charts.selectedIndex;
    if (selectedIndex >= 0) {
        this.charts.removeChild(this.charts.options[selectedIndex]);
    }
    this.writeXml();
};
ReportDesigner.$typeDescription={"instance":"ReportDesigner", "mainDiv":"Div", "outputXml":"TextArea", "fields":{name:"Map", arguments:[null,{name:"Map", arguments:[null,"Object"]}]}, "avaiableProperties":{name:"Array", arguments:[null]}, "fieldSelect":"ReportGeneratorSelectManyBoxView", "groupSelect":"ReportGeneratorSelectManyBoxView", "filterSelect":"ReportGeneratorSelectManyBoxView", "calculatedFieldsSelect":"Select", "definition":"ReportDefinition", "calculatedFieldsManager":"ReportCalculatedFieldsManager", "layoutManager":"ReportLayoutManager", "groupManager":"ReportGroupManager", "filterManager":"ReportFilterManager", "reportTitleInput":"Input", "reportData":"ReportData", "labelInput":"Input", "patternDateInput":"Select", "patternNumberInput":"Select", "aggregateInput":"Input", "aggregateTypeInput":"Select", "patternDateInputGroup":"Select", "filterLabel":"Input", "filterFixedCriteria":"Select", "filterPreSelectDate":"Select", "filterPreSelectEntity":"Select", "filterSelectMultiple":"Input", "filterRequired":"Input", "charts":"Select", "selectables":{name:"Array", arguments:["Selectable"]}};


var ReportData = function(designer) {

    this.designer = designer;
};
ReportData.prototype.designer = null;
ReportData.prototype.dataSourceProvider = null;
ReportData.prototype.toString = function() {
    var value = "    <data>\n";
    if (this.dataSourceProvider != null) {
        value += "        " + this.dataSourceProvider.toString() + "\n";
    }
    value += this.designer.groupManager.toString();
    value += this.designer.filterManager.toString();
    value += this.designer.calculatedFieldsManager.toString();
    value += "    </data>\n";
    return value;
};
ReportData.$typeDescription={"designer":"ReportDesigner", "dataSourceProvider":"ReportDataSourceProvider"};


var ReportDataSourceProvider = function(){};

ReportDataSourceProvider.prototype.toString = function(){};
ReportDataSourceProvider.$typeDescription={};


var HibernateDataSourceProvider = function(fromClass) {

    this.fromClass = fromClass;
};
stjs.extend(HibernateDataSourceProvider, ReportDataSourceProvider);

HibernateDataSourceProvider.prototype.fromClass = null;
HibernateDataSourceProvider.prototype.toString = function() {
    return "<dataSourceProvider type='hibernateDataProvider' fromClass='" + this.fromClass + "'/>";
};
HibernateDataSourceProvider.$typeDescription={};


var ReportLayoutManager = function(designer, fieldSelect) {

    this.items = [];
    this.designer = designer;
    this.fieldSelect = fieldSelect;
    var bigThis = this;
    (fieldSelect.getFrom())["onAdd"] = function(p1) {
        var properties = (p1)["properties"];
        bigThis.addFieldDetail(p1.value, properties);
    };
    (fieldSelect.getFrom())["onRemove"] = function(p1) {
        var properties = (p1)["properties"];
        bigThis.removeByName(p1.value);
    };
    fieldSelect.onselectto = function(p1) {
        if (p1 != null) {
            bigThis.selectElementByName(p1.value, true);
        }
    };
};
ReportLayoutManager.prototype.designer = null;
ReportLayoutManager.prototype.items = null;
ReportLayoutManager.prototype.fieldSelect = null;
ReportLayoutManager.prototype.selectElementByName = function(name, cascade) {
    this.select(this.getElementByName(name), cascade);
};
ReportLayoutManager.prototype.getElementByName = function(name) {
    for (var key in this.items) {
        if (!(this.items).hasOwnProperty(key)) continue;
        var li = this.items[key];
        if (li.constructor ==  FieldDetail) {
            var fd = li;
            if ((fd.name == name)) {
                return fd;
            }
        }
    }
    return null;
};
ReportLayoutManager.prototype.select = function(fd, cascadeToDefinition) {
    if (fd == null) {
        return;
    }
    this.fieldSelect.markSelected(fd.name);
    if (cascadeToDefinition) {
        this.designer.definition.selectItem(fd.label, false);
    }
    eval("showdesignerTab('designerTab_1', 1, 'designerTab_link_1');");
    //passar para a aba de fields
    next.effects.showProperty(this.designer.labelInput);
    ReportPropertyConfigUtils.configureInputToLabel(fd.label, this.designer.labelInput);
    if (fd.isDate()) {
        next.effects.showProperty(this.designer.patternDateInput);
        ReportPropertyConfigUtils.configurePatternInputToField(fd.field, this.designer.patternDateInput);
    }
    if (fd.isNumber()) {
        next.effects.showProperty(this.designer.patternNumberInput);
        ReportPropertyConfigUtils.configurePatternInputToField(fd.field, this.designer.patternNumberInput);
    }
    if (fd.isAggregatable()) {
        next.effects.showProperty(this.designer.aggregateInput);
        next.effects.showProperty(this.designer.aggregateTypeInput);
        ReportPropertyConfigUtils.configureFieldToAggregateInputs(fd, this.designer.aggregateInput, this.designer.aggregateTypeInput);
    }
};
ReportLayoutManager.prototype.selectAndRemove = function(layoutItem) {
    if (layoutItem.constructor ==  FieldDetail) {
        this.fieldSelect.unselect((layoutItem).name);
    }
};
ReportLayoutManager.prototype.selectElement = function(name, value, label, pattern, aggregate, aggregateType) {
    this.fieldSelect.select(name, null);
    //to select in the selectmanybox the properties are not necessary
    for (var key in this.items) {
        if (!(this.items).hasOwnProperty(key)) continue;
        var li = this.items[key];
        if (li.constructor ==  FieldDetail) {
            var fd = li;
            if ((fd.name == name)) {
                if (label.length > 0) {
                    fd.label.label = label;
                    fd.label.changed = true;
                    fd.label.getNode().innerHTML = label;
                }
                fd.aggregateType = aggregateType;
                fd.setAggregate(aggregate);
                fd.field.pattern = pattern;
                break;
            }
        }
    }
};
ReportLayoutManager.prototype.removeByName = function(value) {
    for (var key in this.items) {
        if (!(this.items).hasOwnProperty(key)) continue;
        var li = this.items[key];
        if (li.constructor ==  FieldDetail) {
            var fd = li;
            if ((fd.name == value)) {
                this.remove(li);
                break;
            }
        }
    }
};
ReportLayoutManager.prototype.moveFieldDetail = function(layoutItem, i) {
    if (i != 1 && i != -1) {
        alert("It is not possible to move fieldDetail more than one column");
        return;
    }
    var oldColumn = layoutItem.label.column;
    var newColumnIndex = layoutItem.label.column.getIndex() + i;
    if (newColumnIndex < 0 || newColumnIndex >= this.designer.definition.columns.length) {
        return;
    }
    var newColumn = this.designer.definition.getColumnByIndex(newColumnIndex);
    var headerItem = this.designer.definition.getElementForRowAndColumn(this.designer.definition.sectionDetailHeader.getLastRow(), newColumn);
    var detailItem = this.designer.definition.getElementForRowAndColumn(this.designer.definition.sectionDetail.getLastRow(), newColumn);
    var headerCell = this.designer.definition.sectionDetailHeader.getLastRow().getTdForColumn(newColumn);
    var detailCell = this.designer.definition.sectionDetail.getLastRow().getTdForColumn(newColumn);
    var oldHeaderCell = layoutItem.label.getCell();
    var oldDetailCell = layoutItem.field.getCell();
    if (headerItem != null) {
        headerCell.removeChild(headerItem.getNode());
    }
    if (detailItem != null) {
        detailCell.removeChild(detailItem.getNode());
    }
    oldHeaderCell.removeChild(layoutItem.label.getNode());
    oldDetailCell.removeChild(layoutItem.field.getNode());
    headerCell.appendChild(layoutItem.label.getNode());
    detailCell.appendChild(layoutItem.field.getNode());
    layoutItem.label.column = newColumn;
    layoutItem.field.column = newColumn;
    if (headerItem != null) {
        oldHeaderCell.appendChild(headerItem.getNode());
        headerItem.column = oldColumn;
    }
    if (detailItem != null) {
        oldDetailCell.appendChild(detailItem.getNode());
        detailItem.column = oldColumn;
    }
    if (headerItem != null && headerItem.layoutItem != null) {
        //int index = items.indexOf(layoutItem);
        var index = next.util.indexOf(this.items, layoutItem);
        this.items.splice(index, 1);
        this.items.splice(index + i, 0, layoutItem);
    }
    this.writeXML();
};
ReportLayoutManager.prototype.addFieldDetail = function(fieldName, value) {
    var definition = this.designer.definition;
    var atColumn = definition.columns.length;
    while (atColumn > 0) {
        if (definition.getElementForRowAndColumn(definition.sectionDetailHeader.getLastRow(), definition.getColumnByIndex(atColumn - 1)) == null && definition.getElementForRowAndColumn(definition.sectionDetail.getLastRow(), definition.getColumnByIndex(atColumn - 1)) == null) {
            atColumn--;
        } else {
            break;
        }
    }
    var label = new LabelReportElement(fieldName, value);
    var field = new FieldReportElement(fieldName, value);
    var fd = new FieldDetail(this, fieldName, label, field, value);
    label.layoutItem = fd;
    field.layoutItem = fd;
    var bigThis = this;
    label.onFocus = function() {
        bigThis.select(fd, false);
    };
    this.items.push(fd);
    definition.addElement(label, definition.sectionDetailHeader, atColumn);
    definition.addElement(field, definition.sectionDetail, atColumn);
    this.writeXML();
    return fd;
};
ReportLayoutManager.prototype.writeXML = function() {
    this.designer.writeXml();
};
ReportLayoutManager.prototype.toString = function() {
    var result = "    <layout>\n";
    for (var key in this.items) {
        if (!(this.items).hasOwnProperty(key)) continue;
        var item = this.items[key];
        result += "        " + item.toString();
        result += "\n";
    }
    result += "    </layout>\n";
    return result;
};
ReportLayoutManager.prototype.remove = function(layoutItem) {
    var columnIndex = -1;
    if (layoutItem.constructor ==  FieldDetail) {
        columnIndex = (layoutItem).label.column.getIndex() + 1;
    }
    var elements = layoutItem.getElements();
    layoutItem.clearElements();
    for (var key in elements) {
        if (!(elements).hasOwnProperty(key)) continue;
        this.designer.definition.remove(elements[key]);
    }
    if (layoutItem.constructor ==  FieldDetail) {
        for (var i = columnIndex; i < this.designer.definition.columns.length; i++) {
            var allSections = this.designer.definition.getAllSections();
            for (var j = 0; j < allSections.length; j++) {
                var section = allSections[j];
                var elementForRowAndColumn = this.designer.definition.getElementForRowAndColumn(section.getLastRow(), this.designer.definition.getColumnByIndex(i));
                this.designer.moveElement(-1, elementForRowAndColumn);
            }
        }
        var columnToRemove = this.designer.definition.columns.length - 1;
        if (columnToRemove != 0 || this.designer.groupManager.objects.length == 0) {
            this.designer.definition.removeColumn(columnToRemove);
        }
    }
    next.util.removeItem(this.items, layoutItem);
    this.writeXML();
};
ReportLayoutManager.$typeDescription={"designer":"ReportDesigner", "items":{name:"Array", arguments:["LayoutItem"]}, "fieldSelect":"ReportGeneratorSelectManyBoxView"};

