var ReportPropertyConfigUtils = function(){};

ReportPropertyConfigUtils.isAggregatable = function(properties) {
    return properties["numberType"];
};
ReportPropertyConfigUtils.isTransient = function(properties) {
    return properties["transient"];
};
ReportPropertyConfigUtils.isExtended = function(properties) {
    return properties["extended"];
};
ReportPropertyConfigUtils.isFilterable = function(properties) {
    return properties["filterable"];
};
ReportPropertyConfigUtils.isFilterRequired = function(properties) {
    return properties["requiredFilter"];
};
ReportPropertyConfigUtils.isEntity = function(properties) {
    return properties["entity"];
};
ReportPropertyConfigUtils.setFilterSelectMultiple = function(properties, checked) {
    properties["filterSelectMultiple"] = checked;
};
ReportPropertyConfigUtils.setFilterRequired = function(properties, checked) {
    properties["requiredFilter"] = checked;
};
ReportPropertyConfigUtils.isFilterSelectMultiple = function(properties) {
    return properties["filterSelectMultiple"];
};
ReportPropertyConfigUtils.setFilterPreSelectDate = function(properties, filterPreSelectDate) {
    if (("<null>" == filterPreSelectDate)) {
        filterPreSelectDate = null;
    }
    properties["preSelectDate"] = filterPreSelectDate;
};
ReportPropertyConfigUtils.setFilterPreSelectEntity = function(properties, filterPreSelectEntity) {
    if (("<null>" == filterPreSelectEntity)) {
        filterPreSelectEntity = null;
    }
    properties["preSelectEntity"] = filterPreSelectEntity;
};
ReportPropertyConfigUtils.getFilterPreSelectDate = function(properties) {
    return properties["preSelectDate"];
};
ReportPropertyConfigUtils.getFilterPreSelectEntity = function(properties) {
    return properties["preSelectEntity"];
};
ReportPropertyConfigUtils.setFilterDisplayName = function(properties, value) {
    properties["filterDisplayName"] = value;
};
ReportPropertyConfigUtils.getFilterDisplayName = function(properties) {
    var fdn = properties["filterDisplayName"];
    if (fdn == null) {
        fdn = ReportPropertyConfigUtils.getDisplayName(properties);
    }
    return fdn;
};
ReportPropertyConfigUtils.getDisplayName = function(properties) {
    return properties["displayName"];
};
ReportPropertyConfigUtils.getProcessors = function(properties) {
    var processors = properties["processors"];
    if (processors == null) {
        return [];
    }
    return (processors).split(",");
};
ReportPropertyConfigUtils.setProcessors = function(properties, processors) {
    var sProcessors = next.util.join(processors, ",");
    properties["processors"] = sProcessors;
};
ReportPropertyConfigUtils.isDate = function(options) {
    var type = ReportPropertyConfigUtils.getType(options);
    return (type == "java.util.Calendar") || (type == "java.util.Date") || (type == "java.sql.Date");
};
ReportPropertyConfigUtils.getType = function(options) {
    return options["type"];
};
ReportPropertyConfigUtils.isNumber = function(properties) {
    return properties["numberType"] == true;
};
ReportPropertyConfigUtils.isGroupable = function(properties) {
    if (properties["comparable"] != true && properties["entity"] != true) {
        return false;
    }
    return !ReportPropertyConfigUtils.isNumber(properties);
};
ReportPropertyConfigUtils.configureInputToLabel = function(labelElement, labelInput) {
    ReportPropertyConfigUtils.showElement(labelInput);
    labelInput.value = labelElement.label;
    labelInput.onkeyup = function(p1) {
        labelElement.label = labelInput.value;
        labelElement.changed = true;
        labelElement.getNode().innerHTML = labelElement.label;
        return true;
    };
    labelInput.onchange = function(p1) {
        ReportDesigner.getInstance().writeXml();
        return true;
    };
};
ReportPropertyConfigUtils.configurePatternInputToField = function(field, patternInput) {
    ReportPropertyConfigUtils.showElement(patternInput);
    if (field.pattern != null && field.pattern != "") {
        patternInput.value = field.pattern;
    } else {
        patternInput.selectedIndex = 0;
    }
    patternInput.onchange = function(p1) {
        var fieldReportElement = field;
        fieldReportElement.pattern = patternInput.value;
        ReportDesigner.getInstance().writeXml();
        return true;
    };
};
ReportPropertyConfigUtils.configureFieldToAggregateInputs = function(fieldDetail, aggregateInput, aggregateTypeInput) {
    if (fieldDetail.label.column.getIndex() == 0) {
        aggregateInput.disabled = true;
        aggregateTypeInput.disabled = true;
    } else {
        aggregateInput.disabled = false;
        aggregateTypeInput.disabled = false;
    }
    ReportPropertyConfigUtils.showElement(aggregateInput);
    ReportPropertyConfigUtils.showElement(aggregateTypeInput);
    aggregateInput.checked = fieldDetail.isAggregate();
    aggregateInput.onclick = function(p1) {
        if (fieldDetail.label.column.getIndex() == 0) {
            alert("O item na primeira coluna nao pode ser agregado.\n" + "O espaco da primeira coluna e reservado para grupos.\n" + "Mova o campo para outra coluna para poder agregar.");
            return false;
        }
        fieldDetail.setAggregate(aggregateInput.checked);
        ReportDesigner.getInstance().writeXml();
        return true;
    };
    if (fieldDetail.aggregateType == null || fieldDetail.aggregateType.length == 0) {
        aggregateTypeInput.selectedIndex = 0;
    } else {
        aggregateTypeInput.value = fieldDetail.aggregateType;
    }
    aggregateTypeInput.onchange = function(p1) {
        fieldDetail.aggregateType = aggregateTypeInput.value;
        ReportDesigner.getInstance().writeXml();
        return true;
    };
};
ReportPropertyConfigUtils.hideElement = function(node) {
    (node.parentNode.parentNode).style.display = "none";
};
ReportPropertyConfigUtils.showElement = function(node) {
    (node.parentNode.parentNode).style.display = "";
};
ReportPropertyConfigUtils.ANY = 1;
ReportPropertyConfigUtils.IN_VAR = 2;
ReportPropertyConfigUtils.IN_SIGNAL = 3;
ReportPropertyConfigUtils.OPEN_PARENTHESIS = 1;
ReportPropertyConfigUtils.CLOSE_PARENTHESIS = 2;
ReportPropertyConfigUtils.SIGNAL = 3;
ReportPropertyConfigUtils.VAR = 4;
ReportPropertyConfigUtils.parseExpression = function(expression) {
    var parts = [];
    var token = "";
    var status = ReportPropertyConfigUtils.ANY;
    for (var i = 0; i < expression.length; i++) {
        var c = expression.charAt(i);
        switch(status) {
            case ReportPropertyConfigUtils.ANY:
                if (ReportPropertyConfigUtils.isLetter(c) || ReportPropertyConfigUtils.isDigit(c)) {
                    token += c;
                    status = ReportPropertyConfigUtils.IN_VAR;
                } else {
                    if (ReportPropertyConfigUtils.isNotEmpty(token)) {
                        parts.push(token);
                    }
                    token = "" + c;
                    status = ReportPropertyConfigUtils.IN_SIGNAL;
                }
                break;
            case ReportPropertyConfigUtils.IN_VAR:
                if (ReportPropertyConfigUtils.isLetter(c) || ReportPropertyConfigUtils.isDigit(c) || c == '.') {
                    token += c;
                } else {
                    if (ReportPropertyConfigUtils.isNotEmpty(token)) {
                        parts.push(token);
                    }
                    token = "" + c;
                    status = ReportPropertyConfigUtils.IN_SIGNAL;
                }
                break;
            case ReportPropertyConfigUtils.IN_SIGNAL:
                if (ReportPropertyConfigUtils.isLetter(c) || ReportPropertyConfigUtils.isDigit(c)) {
                    if (ReportPropertyConfigUtils.isNotEmpty(token)) {
                        parts.push(token);
                    }
                    token = "" + c;
                    status = ReportPropertyConfigUtils.IN_VAR;
                } else {
                    if (ReportPropertyConfigUtils.isNotEmpty(token)) {
                        parts.push(token);
                    }
                    token = "" + c;
                }
                break;
        }
    }
    if (ReportPropertyConfigUtils.isNotEmpty(token)) {
        parts.push(token);
    }
    return parts;
};
ReportPropertyConfigUtils.isLetter = function(c) {
    return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
};
ReportPropertyConfigUtils.isDigit = function(c) {
    return (c >= '0' && c <= '9');
};
ReportPropertyConfigUtils.isNotEmpty = function(token) {
    return token != null && token.trim().length > 0;
};
ReportPropertyConfigUtils.isEmpty = function(token) {
    return token == null || token.trim().length == 0;
};
ReportPropertyConfigUtils.validateExpression = function(expression) {
    var tokens = ReportPropertyConfigUtils.parseExpression(expression);
    var status = ReportPropertyConfigUtils.ANY;
    var parentesesStack = 0;
    for (var i = 0; i < tokens.length; i++) {
        var token = tokens[i];
        if (token == ")") {
            parentesesStack--;
            if (parentesesStack < 0) {
                return "Existem mais ')' do que '('. Verifique a expressão.";
            }
        }
        if (token == "(") {
            parentesesStack++;
        }
        var tokenType = ReportPropertyConfigUtils.getTokenType(token);
        switch(status) {
            case ReportPropertyConfigUtils.ANY:
                if (tokenType == ReportPropertyConfigUtils.SIGNAL) {
                    status = ReportPropertyConfigUtils.IN_SIGNAL;
                }
                if (tokenType == ReportPropertyConfigUtils.VAR) {
                    status = ReportPropertyConfigUtils.IN_VAR;
                }
                if (tokenType == ReportPropertyConfigUtils.CLOSE_PARENTHESIS) {
                    return "Fecha parênteses inesperado [item " + (i + 1) + "]";
                }
                break;
            case ReportPropertyConfigUtils.IN_VAR:
                if (tokenType == ReportPropertyConfigUtils.VAR) {
                    return "Variável inesperada '" + token + "'";
                }
                if (tokenType == ReportPropertyConfigUtils.OPEN_PARENTHESIS) {
                    return "Operador esperado '(' [item " + (i + 1) + "]";
                }
                if (tokenType == ReportPropertyConfigUtils.CLOSE_PARENTHESIS) {
                    status = ReportPropertyConfigUtils.IN_VAR;
                }
                if (tokenType == ReportPropertyConfigUtils.SIGNAL) {
                    status = ReportPropertyConfigUtils.IN_SIGNAL;
                }
                break;
            case ReportPropertyConfigUtils.IN_SIGNAL:
                if (tokenType == ReportPropertyConfigUtils.SIGNAL) {
                    return "Operador inesperado '" + token + "'";
                }
                if (tokenType == ReportPropertyConfigUtils.CLOSE_PARENTHESIS) {
                    return "Operador inesperado ')' [item " + (i + 1) + "]";
                }
                if (tokenType == ReportPropertyConfigUtils.OPEN_PARENTHESIS) {
                    status = ReportPropertyConfigUtils.ANY;
                }
                if (tokenType == ReportPropertyConfigUtils.VAR) {
                    status = ReportPropertyConfigUtils.IN_VAR;
                }
                break;
        }
    }
    if (parentesesStack != 0) {
        return "O número de '(' e ')' são diferentes. Verifique a expressão.";
    }
    return null;
};
ReportPropertyConfigUtils.getTokenType = function(token) {
    if (token.length == 1) {
        if (token == "(") {
            return ReportPropertyConfigUtils.OPEN_PARENTHESIS;
        } else if (token == ")") {
            return ReportPropertyConfigUtils.CLOSE_PARENTHESIS;
        } else {
            if (!ReportPropertyConfigUtils.isDigit(token.charAt(0))) {
                return ReportPropertyConfigUtils.SIGNAL;
            }
        }
    }
    return ReportPropertyConfigUtils.VAR;
};
ReportPropertyConfigUtils.$typeDescription={};

