var ChartWizzard = function(element, designer) {

    this.mainDiv = element;
    this.nextButton = next.dom.getInnerElementById(this.mainDiv, "nextButton");
    this.pages = [];
    this.designer = designer;
    this.chartGroupBy = next.dom.getInnerElementById(this.mainDiv, "chartGroupBy");
    this.chartGroupByLevel = next.dom.getInnerElementById(this.mainDiv, "chartGroupByLevel");
    this.chartSeries = next.dom.getInnerElementById(this.mainDiv, "chartSeries");
    this.chartValue = next.dom.getInnerElementById(this.mainDiv, "chartValue");
    this.chartAggregateType = next.dom.getInnerElementById(this.mainDiv, "chartAggregateType");
    this.chartTitle = next.dom.getInnerElementById(this.mainDiv, "chartTitle");
    this.chartGroupTitle = next.dom.getInnerElementById(this.mainDiv, "chartGroupTitle");
    this.chartSeriesTitle = next.dom.getInnerElementById(this.mainDiv, "chartSeriesTitle");
    this.chartIgnoreEmptySeriesAndGroups = next.dom.getInnerElementById(this.mainDiv, "chartIgnoreEmptySeriesAndGroups");
    this.chartCountTrue = next.dom.getInnerElementById(this.mainDiv, "chartCountTrue");
    this.chartCountFalse = next.dom.getInnerElementById(this.mainDiv, "chartCountFalse");
    this.propertyTypeAsSeries = next.dom.getInnerElementById(this.mainDiv, "propertyTypeAsSeries");
    this.propertyTypeDefault = next.dom.getInnerElementById(this.mainDiv, "propertyTypeDefault");
    this.chartConfigLimitSeriesShowAll = next.dom.getInnerElementById(this.mainDiv, "chartConfigLimitSeriesShowAll");
    this.chartConfigLimitSeriesLimit = next.dom.getInnerElementById(this.mainDiv, "chartConfigLimitSeriesLimit");
    this.chartConfigLimitSeriesGroup = next.dom.getInnerElementById(this.mainDiv, "chartConfigLimitSeriesGroup");
    this.chartSeriesSection = next.dom.getInnerElementById(this.mainDiv, "chartSeriesSection");
    this.propertiesAsSeries = new ReportGeneratorSelectManyBoxView(next.dom.toElement("chartPropertiesAsSeries_from_"));
    var bigThis = this;
    this.chartLabelSerie = next.dom.getInnerElementById(this.mainDiv, "chartLabelSerie");
    this.chartLabelSerie.onchange = function(p1) {
        bigThis.configureSerieLabel();
        return true;
    };
    this.chartAggregateTypeSerie = next.dom.getInnerElementById(this.mainDiv, "chartAggregateTypeSerie");
    this.chartAggregateTypeSerie.onchange = function(p1) {
        bigThis.configureSerieAggregateType();
        return true;
    };
    this.propertiesAsSeries.getTo().onchange = function(p1) {
        bigThis.resetSerieLabel();
        bigThis.resetAggregateType();
        return true;
    };
    this.propertyTypeAsSeries.onclick = this.propertyTypeDefault.onclick = function(p1) {
        bigThis.configurePropertyType();
        return true;
    };
    this.addPage("chartWizzard_page_1");
    this.addPage("chartWizzard_page_2");
    this.addPage("chartWizzard_page_3");
};
ChartWizzard.AGGREGATE_FUNCTION = "aggregateFunction";
ChartWizzard.SERIE_LABEL = "label";
ChartWizzard.ATTR_ISDATE = "data-isdate";
ChartWizzard.prototype.mainDiv = null;
ChartWizzard.prototype.nextButton = null;
ChartWizzard.prototype.currentPage = 110;
ChartWizzard.prototype.pages = null;
ChartWizzard.prototype.designer = null;
ChartWizzard.prototype.chartGroupBy = null;
ChartWizzard.prototype.chartGroupByLevel = null;
ChartWizzard.prototype.chartSeries = null;
ChartWizzard.prototype.chartValue = null;
ChartWizzard.prototype.chartAggregateType = null;
ChartWizzard.prototype.chartTitle = null;
ChartWizzard.prototype.chartGroupTitle = null;
ChartWizzard.prototype.chartSeriesTitle = null;
ChartWizzard.prototype.chartIgnoreEmptySeriesAndGroups = null;
ChartWizzard.prototype.chartCountTrue = null;
ChartWizzard.prototype.chartCountFalse = null;
ChartWizzard.prototype.propertyTypeDefault = null;
ChartWizzard.prototype.propertyTypeAsSeries = null;
ChartWizzard.prototype.chartConfigLimitSeriesShowAll = null;
ChartWizzard.prototype.chartConfigLimitSeriesLimit = null;
ChartWizzard.prototype.chartConfigLimitSeriesGroup = null;
ChartWizzard.prototype.chartSeriesSection = null;
ChartWizzard.prototype.editing = null;
ChartWizzard.prototype.propertiesAsSeries = null;
ChartWizzard.prototype.chartAggregateTypeSerie = null;
ChartWizzard.prototype.chartLabelSerie = null;
ChartWizzard.createInstance = function() {
    return new ChartWizzard(window.document.getElementById("chartWizzard"), ReportDesigner.getInstance());
};
ChartWizzard.setup = function(id, designer) {
    var chartWizzard = new ChartWizzard(window.document.getElementById(id), designer);
    return chartWizzard;
};
ChartWizzard.prototype.resetSerieLabel = function() {
    if (this.propertiesAsSeries.getTo().selectedIndex >= 0) {
        var optionSelected = this.propertiesAsSeries.getTo().options[this.propertiesAsSeries.getTo().selectedIndex];
        var properties = this.getPropertiesFromOption(optionSelected);
        var label = properties[ChartWizzard.SERIE_LABEL];
        if (label != null) {
            this.chartLabelSerie.value = label;
        } else {
            this.chartLabelSerie.value = ReportPropertyConfigUtils.getDisplayName(properties);
        }
    } else {
        this.chartLabelSerie.value = "";
    }
};
ChartWizzard.prototype.resetAggregateType = function() {
    if (this.propertiesAsSeries.getTo().selectedIndex >= 0) {
        var optionSelected = this.propertiesAsSeries.getTo().options[this.propertiesAsSeries.getTo().selectedIndex];
        var properties = this.getPropertiesFromOption(optionSelected);
        var aggregateFunction = properties[ChartWizzard.AGGREGATE_FUNCTION];
        if (aggregateFunction == null) {
            this.chartAggregateTypeSerie.selectedIndex = 0;
        } else {
            next.dom.setSelectedValue(this.chartAggregateTypeSerie, aggregateFunction);
        }
    } else {
        this.chartAggregateTypeSerie.selectedIndex = 0;
    }
};
ChartWizzard.prototype.configureSerieLabel = function() {
    if (this.propertiesAsSeries.getTo().selectedIndex >= 0) {
        var optionSelected = this.propertiesAsSeries.getTo().options[this.propertiesAsSeries.getTo().selectedIndex];
        var properties = this.getPropertiesFromOption(optionSelected);
        properties[ChartWizzard.SERIE_LABEL] = this.chartLabelSerie.value;
    }
};
//			Global.alert("changing "+selectedOption+ " to "+aggregateFunction);
ChartWizzard.prototype.configureSerieAggregateType = function() {
    if (this.propertiesAsSeries.getTo().selectedIndex >= 0) {
        var aggregateFunction = next.dom.getSelectedValue(this.chartAggregateTypeSerie);
        var selectedOption = next.dom.getSelectedText(this.propertiesAsSeries.getTo());
        var optionSelected = this.propertiesAsSeries.getTo().options[this.propertiesAsSeries.getTo().selectedIndex];
        var properties = this.getPropertiesFromOption(optionSelected);
        properties[ChartWizzard.AGGREGATE_FUNCTION] = aggregateFunction;
    }
};
//			Global.alert("changing "+selectedOption+ " to "+aggregateFunction);
ChartWizzard.prototype.getPropertiesFromOption = function(optionSelected) {
    var optionObject = optionSelected;
    var properties = optionObject["properties"];
    if (properties == null) {
        properties = {};
        (optionSelected)["properties"] = properties;
    }
    return properties;
};
ChartWizzard.prototype.configurePropertyType = function() {
    if (this.propertyTypeDefault.checked) {
        window.document.getElementById("propertiesDefaultBlock").style.display = "";
        window.document.getElementById("propertiesAsSeriesBlock").style.display = "none";
    } else {
        window.document.getElementById("propertiesDefaultBlock").style.display = "none";
        window.document.getElementById("propertiesAsSeriesBlock").style.display = "";
    }
};
ChartWizzard.prototype.configureValues = function() {
    this.chartCountFalse.disabled = true;
    this.chartValue.innerHTML = "";
    for (var key in this.designer.fields) {
        var properties = this.designer.fields[key];
        if (ReportPropertyConfigUtils.isAggregatable(properties)) {
            var option = window.document.createElement("option");
            option.value = key;
            option.innerHTML = properties["displayName"];
            this.chartValue.appendChild(option);
            this.chartCountFalse.disabled = false;
        }
    }
};
ChartWizzard.prototype.configureSeries = function() {
    this.chartSeries.innerHTML = "";
    for (var key in this.designer.fields) {
        var properties = this.designer.fields[key];
        if (ReportPropertyConfigUtils.isGroupable(properties) && !ReportPropertyConfigUtils.isNumber(properties) && !ReportPropertyConfigUtils.isDate(properties)) {
            var option = window.document.createElement("option");
            option.value = key;
            option.innerHTML = properties["displayName"];
            this.chartSeries.appendChild(option);
        }
    }
};
ChartWizzard.prototype.configureGroupItens = function() {
    this.propertiesAsSeries.clearAll();
    this.propertiesAsSeries.setUsePropertyAsLabel("displayName");
    this.chartGroupBy.innerHTML = "";
    this.chartGroupByLevel.selectedIndex = 1;
    ReportPropertyConfigUtils.hideElement(this.chartGroupByLevel);
    for (var key in this.designer.fields) {
        var properties = this.designer.fields[key];
        if (ReportPropertyConfigUtils.isNumber(properties)) {
            this.propertiesAsSeries.add(key, properties);
        }
        if (ReportPropertyConfigUtils.isGroupable(properties) && !ReportPropertyConfigUtils.isNumber(properties)) {
            var option = window.document.createElement("option");
            option.value = key;
            option.innerHTML = properties["displayName"];
            if (ReportPropertyConfigUtils.isDate(properties)) {
                option.setAttribute(ChartWizzard.ATTR_ISDATE, "true");
            }
            this.chartGroupBy.appendChild(option);
        }
    }
    var bigThis = this;
    this.chartGroupBy.onchange = function(p1) {
        bigThis.configureGroupDateLevel();
        return true;
    };
    this.configureGroupDateLevel();
};
ChartWizzard.prototype.configureGroupDateLevel = function() {
    var isdate = this.chartGroupBy.options.item(this.chartGroupBy.selectedIndex).getAttribute(ChartWizzard.ATTR_ISDATE);
    if (isdate != null && (isdate == "true")) {
        ReportPropertyConfigUtils.showElement(this.chartGroupByLevel);
    } else {
        ReportPropertyConfigUtils.hideElement(this.chartGroupByLevel);
    }
};
ChartWizzard.prototype.addPage = function(pageName) {
    var page = next.dom.getInnerElementById(this.mainDiv, pageName);
    this.pages.push(page);
    return page;
};
ChartWizzard.prototype.getSelectedChartTypeCapitalized = function() {
    var selectedChartType = this.getSelectedChartType();
    if (selectedChartType != null) {
        selectedChartType = selectedChartType.substring(0, 1).toUpperCase() + selectedChartType.substring(1);
    }
    return selectedChartType;
};
ChartWizzard.prototype.getSelectedChartType = function() {
    var chartTypeRadios = window.document.getElementsByName("chartType");
    for (var i = 0; i < chartTypeRadios.length; i++) {
        var radio = chartTypeRadios[i];
        if (radio.checked) {
            return radio.value;
        }
    }
    alert("Erro: Nenhum tipo de gráfico selecionado");
    return null;
};
ChartWizzard.prototype.next = function() {
    switch(this.currentPage) {
        case 110:
            this.gotoChartConfig();
            break;
        case 121:
            this.gotoConfirmationView();
            break;
    }
};
ChartWizzard.prototype.gotoConfirmationView = function() {
    this.hideAllPages();
    this.showPage("chartWizzard_page_3");
    this.currentPage = 131;
    var groupByProperty = this.chartGroupBy.options.item(this.chartGroupBy.selectedIndex);
    var groupByLevelProperty = this.chartGroupByLevel.options.item(this.chartGroupByLevel.selectedIndex);
    var useCount = this.chartCountTrue.checked;
    var valueProperty = this.chartValue.options[this.chartValue.selectedIndex];
    var aggregate = this.chartAggregateType.options[this.chartAggregateType.selectedIndex];
    var title = this.chartTitle.value;
    var groupTitle = this.chartGroupTitle.value;
    var seriesTitle = this.chartSeriesTitle.value;
    var seriesProperty = "";
    if (this.chartSeries.selectedIndex >= 0) {
        seriesProperty = this.chartSeries.options.item(this.chartSeries.selectedIndex).value;
    }
    var configuration = new ChartConfiguration(this.getSelectedChartType(), groupByProperty.value, groupByLevelProperty.value, (this.getSelectedChartType() == "pie") ? "" : (this.propertyTypeDefault.checked ? seriesProperty : ""), useCount ? "count" : valueProperty.value, aggregate.value, title, groupTitle, seriesTitle, this.propertyTypeAsSeries.checked ? "true" : "false", this.getSeriesLimitType(), this.getChartIgnoreEmptySeriesAndGroups());
    var options = this.propertiesAsSeries.getTo().options;
    for (var i = 0; i < options.length; i++) {
        var option = options[i];
        var properties = this.getPropertiesFromOption(option);
        var aggregateFunction = properties[ChartWizzard.AGGREGATE_FUNCTION];
        var label = properties[ChartWizzard.SERIE_LABEL];
        configuration.addSerie(option.value, aggregateFunction, label);
    }
    this.setSpanText("chartTitleSpan", configuration.title);
    this.setSpanText("chartGroupSpan", configuration.groupProperty);
    this.setSpanText("chartValueSpan", (configuration.valueProperty == "count") ? "Contagem dos itens" : configuration.valueProperty + " (" + configuration.valueAggregate + ")");
    this.nextButton.innerHTML = "Concluido";
    var bigThis = this;
    this.nextButton.onclick = function(p1) {
        bigThis.saveConfiguration(configuration);
        bigThis.dismiss();
        return true;
    };
};
ChartWizzard.prototype.getChartIgnoreEmptySeriesAndGroups = function() {
    return this.chartIgnoreEmptySeriesAndGroups.checked;
};
ChartWizzard.prototype.getSeriesLimitType = function() {
    var chartTypeRadios = window.document.getElementsByName("chartConfigLimitSeries");
    for (var i = 0; i < chartTypeRadios.length; i++) {
        var radio = chartTypeRadios[i];
        if (radio.checked) {
            return radio.value;
        }
    }
    //		Global.alert("Erro: Nenhum tipo de limitador de séries selecionado");
    return "showall";
};
ChartWizzard.prototype.saveConfiguration = function(configuration) {
    if (this.editing == null) {
        this.designer.addChart(configuration);
    } else {
        this.designer.updateChart(this.editing.option, configuration);
    }
};
ChartWizzard.prototype.setSpanText = function(id, text) {
    var span = next.dom.getInnerElementById(this.mainDiv, id);
    span.innerHTML = text;
};
ChartWizzard.prototype.configureChartSeriesSectionType = function() {
    if ((this.getSelectedChartType() == "pie")) {
        window.document.getElementById("chartSeriesSectionType").style.display = "none";
    } else {
        window.document.getElementById("chartSeriesSectionType").style.display = "";
    }
};
ChartWizzard.prototype.gotoChartConfig = function() {
    this.hideAllPages();
    next.effects.hide("chartLabelTypePie");
    next.effects.hide("chartLabelTypeColumn");
    next.effects.hide("chartLabelTypeLine");
    this.propertyTypeDefault.checked = true;
    this.configurePropertyType();
    this.configureChartSeriesSectionType();
    next.effects.show("chartLabelType" + this.getSelectedChartTypeCapitalized());
    if ((this.getSelectedChartType() == "pie")) {
        this.chartSeriesSection.style.display = "none";
    } else {
        this.chartSeriesSection.style.display = "";
    }
    this.showPage("chartWizzard_page_2");
    this.currentPage = 121;
    this.chartTitle.value = "Grafico " + (this.designer.charts.options.length + 1);
    this.chartAggregateTypeSerie.selectedIndex = 0;
    this.chartLabelSerie.value = "";
};
ChartWizzard.prototype.hideAllPages = function() {
    for (var key in this.pages) {
        if (!(this.pages).hasOwnProperty(key)) continue;
        this.pages[key].style.display = "none";
    }
};
ChartWizzard.prototype.showPage = function(string) {
    for (var key in this.pages) {
        if (!(this.pages).hasOwnProperty(key)) continue;
        var page = this.pages[key];
        if ((page.id == string)) {
            page.style.display = "";
        }
    }
};
ChartWizzard.prototype.show = function() {
    this.editing = null;
    this.nextButton.innerHTML = "Proximo";
    var bigThis = this;
    this.nextButton.onclick = function(p1) {
        bigThis.next();
        return true;
    };
    this.configureGroupItens();
    this.configureSeries();
    this.configureValues();
    this.hideAllPages();
    this.showPage("chartWizzard_page_1");
    this.currentPage = 110;
    this.mainDiv.style.visibility = "";
    (window.document.getElementById("chartTypePie")).checked = true;
};
ChartWizzard.prototype.edit = function(chartConfiguration) {
    if (chartConfiguration != null) {
        this.show();
        this.checkChartType(chartConfiguration);
        this.checkLimitSeriesType(chartConfiguration);
        this.next();
        if (("true" == chartConfiguration.propertiesAsSeries)) {
            this.propertyTypeAsSeries.checked = true;
            var series = chartConfiguration.series;
            for (var key in series) {
                if (!(series).hasOwnProperty(key)) continue;
                var serie = series[key];
                this.propertiesAsSeries.select(serie.property, null);
                var options = this.propertiesAsSeries.getTo().options;
                var displayName = ReportPropertyConfigUtils.getDisplayName(this.getPropertiesFromOption(options[options.length - 1]));
                var label = "";
                this.getPropertiesFromOption(options[options.length - 1])[ChartWizzard.AGGREGATE_FUNCTION] = serie.aggregateFunction;
                if (serie.label != null && !(serie.label == "")) {
                    label = serie.label;
                } else {
                    label = displayName;
                }
                this.getPropertiesFromOption(options[options.length - 1])[ChartWizzard.SERIE_LABEL] = label;
            }
        } else {
            this.propertyTypeAsSeries.checked = false;
        }
        this.configurePropertyType();
        var chartGroup = this.setValue(this.chartGroupBy, chartConfiguration.groupProperty);
        if (chartGroup != null) {
            var isdate = chartGroup.getAttribute(ChartWizzard.ATTR_ISDATE);
            if (isdate != null && (isdate == "true")) {
                ReportPropertyConfigUtils.showElement(this.chartGroupByLevel);
                this.setValue(this.chartGroupByLevel, chartConfiguration.groupLevel);
            } else {
                ReportPropertyConfigUtils.hideElement(this.chartGroupByLevel);
            }
        }
        this.setValue(this.chartSeries, chartConfiguration.seriesProperty);
        this.setValue(this.chartValue, chartConfiguration.valueProperty);
        this.setValue(this.chartAggregateType, chartConfiguration.valueAggregate);
        this.chartTitle.value = chartConfiguration.title;
        this.chartGroupTitle.value = chartConfiguration.groupTitle;
        this.chartSeriesTitle.value = chartConfiguration.seriesTitle;
        if ((chartConfiguration.valueProperty == "count")) {
            this.chartCountTrue.checked = true;
        } else {
            this.chartCountFalse.checked = true;
        }
        this.chartIgnoreEmptySeriesAndGroups.checked = chartConfiguration.ignoreEmptySeriesAndGroups;
        this.editing = chartConfiguration;
    }
};
ChartWizzard.prototype.checkLimitSeriesType = function(chartConfiguration) {
    this.chartConfigLimitSeriesShowAll.checked = true;
    var seriesLimitType = chartConfiguration.seriesLimitType;
    var chartTypeRadios = window.document.getElementsByName("chartConfigLimitSeries");
    for (var i = 0; i < chartTypeRadios.length; i++) {
        var radio = chartTypeRadios[i];
        if (radio.value == seriesLimitType) {
            radio.checked = true;
        }
    }
};
ChartWizzard.prototype.setValue = function(select, value) {
    var options = select.options;
    for (var i = 0; i < options.length; i++) {
        var op = options[i];
        if ((op.value == value)) {
            op.selected = true;
            return op;
        }
    }
    return null;
};
ChartWizzard.prototype.checkChartType = function(chartConfiguration) {
    var type = chartConfiguration.type;
    var chartTypeRadios = window.document.getElementsByName("chartType");
    for (var i = 0; i < chartTypeRadios.length; i++) {
        var radio = chartTypeRadios[i];
        if ((radio.value == type)) {
            radio.checked = true;
        }
    }
};
ChartWizzard.prototype.dismiss = function() {
    this.mainDiv.style.visibility = "hidden";
};
ChartWizzard.$typeDescription={"mainDiv":"Div", "nextButton":"Button", "pages":{name:"Array", arguments:["Div"]}, "designer":"ReportDesigner", "chartGroupBy":"Select", "chartGroupByLevel":"Select", "chartSeries":"Select", "chartValue":"Select", "chartAggregateType":"Select", "chartTitle":"Input", "chartGroupTitle":"Input", "chartSeriesTitle":"Input", "chartIgnoreEmptySeriesAndGroups":"Input", "chartCountTrue":"Input", "chartCountFalse":"Input", "propertyTypeDefault":"Input", "propertyTypeAsSeries":"Input", "chartConfigLimitSeriesShowAll":"Input", "chartConfigLimitSeriesLimit":"Input", "chartConfigLimitSeriesGroup":"Input", "chartSeriesSection":"Div", "editing":"ChartConfiguration", "propertiesAsSeries":"ReportGeneratorSelectManyBoxView", "chartAggregateTypeSerie":"Select", "chartLabelSerie":"Input"};


var ChartConfiguration = function(type, groupProperty, groupLevel, seriesProperty, valueProperty, valueAggregate, title, groupTitle, seriesTitle, propertiesAsSeries, seriesLimitType, ignoreEmptySeriesAndGroups) {

    this.series = [];
    this.type = type;
    this.groupProperty = groupProperty;
    this.groupLevel = groupLevel;
    this.seriesProperty = seriesProperty;
    this.valueProperty = valueProperty;
    this.valueAggregate = valueAggregate;
    this.title = title;
    this.groupTitle = groupTitle;
    this.seriesTitle = seriesTitle;
    this.propertiesAsSeries = propertiesAsSeries;
    this.seriesLimitType = seriesLimitType;
    this.ignoreEmptySeriesAndGroups = ignoreEmptySeriesAndGroups;
};
ChartConfiguration.prototype.type = null;
ChartConfiguration.prototype.groupProperty = null;
ChartConfiguration.prototype.groupLevel = "0";
ChartConfiguration.prototype.seriesProperty = null;
ChartConfiguration.prototype.valueProperty = null;
ChartConfiguration.prototype.valueAggregate = null;
ChartConfiguration.prototype.title = null;
ChartConfiguration.prototype.groupTitle = null;
ChartConfiguration.prototype.seriesTitle = null;
ChartConfiguration.prototype.propertiesAsSeries = null;
ChartConfiguration.prototype.seriesLimitType = null;
ChartConfiguration.prototype.ignoreEmptySeriesAndGroups = null;
ChartConfiguration.prototype.series = null;
ChartConfiguration.prototype.option = null;
ChartConfiguration.prototype.addSerie = function(property, aggregateFunction, label) {
    this.series.push(new SerieConfiguration(property, aggregateFunction, label));
};
ChartConfiguration.prototype.toXmlString = function() {
    var tag = "<chart type=\"" + this.type + "\" " + "groupProperty=\"" + this.groupProperty + "\" groupLevel=\"" + this.groupLevel + "\" " + (("true" == this.propertiesAsSeries) ? "seriesProperty=\"\" " : "seriesProperty=\"" + this.seriesProperty + "\" ") + "valueProperty=\"" + this.valueProperty + "\" valueAggregate=\"" + this.valueAggregate + "\" " + "title=\"" + this.title + "\"" + " groupTitle=\"" + this.groupTitle + "\"" + " seriesTitle=\"" + this.seriesTitle + "\"";
    if (("true" == this.propertiesAsSeries)) {
        tag += " propertiesAsSeries=\"true\"";
    }
    if (this.seriesLimitType != null) {
        tag += " seriesLimitType=\"" + this.seriesLimitType + "\"";
    }
    if (this.ignoreEmptySeriesAndGroups) {
        tag += " ignoreEmptySeriesAndGroups=\"" + true + "\"";
    }
    if (this.series.length == 0) {
        tag += "/>";
    } else {
        tag += ">\n";
        for (var key in this.series) {
            if (!(this.series).hasOwnProperty(key)) continue;
            tag += "            " + this.series[key].toXmlString() + "\n";
        }
        tag += "        </chart>";
    }
    return tag;
};
ChartConfiguration.$typeDescription={"series":{name:"Array", arguments:["SerieConfiguration"]}, "option":"Option"};


var SerieConfiguration = function(property, aggregateFunction, label) {

    this.property = property;
    this.aggregateFunction = aggregateFunction;
    this.label = label;
};
SerieConfiguration.prototype.property = null;
SerieConfiguration.prototype.aggregateFunction = null;
SerieConfiguration.prototype.label = null;
SerieConfiguration.prototype.toXmlString = function() {
    var xml = "<serie property=\"" + this.property + "\"";
    if (this.aggregateFunction != null && !(this.aggregateFunction == "SUM")) {
        xml += " aggregateFunction=\"" + this.aggregateFunction + "\"";
    }
    if (this.label != null && !(this.label == "")) {
        xml += " label=\"" + this.label + "\"";
    }
    xml += "/>";
    return xml;
};
SerieConfiguration.$typeDescription={};

