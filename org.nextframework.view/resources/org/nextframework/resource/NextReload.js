var NextReload = function(){};

NextReload.FunctionCall = function(call) {

    this.call = call;
    this.params = [];
    this.parse();
};
NextReload.FunctionCall.prototype.call = null;
NextReload.FunctionCall.prototype.beanName = null;
NextReload.FunctionCall.prototype.methodName = null;
NextReload.FunctionCall.prototype.params = null;
NextReload.FunctionCall.prototype.parse = function() {
    if (this.call == null) {
        throw exception("null call string");
    }
    var sepPoint = this.call.indexOf('.');
    var sepParenthesis = this.call.indexOf('(', sepPoint);
    var sepParenthesisClose = this.call.lastIndexOf(')');
    this.beanName = this.call.substring(0, sepPoint);
    this.methodName = this.call.substring(sepPoint + 1, sepParenthesis);
    var paramsStr = this.call.substring(sepParenthesis + 1, sepParenthesisClose);
    var params = (paramsStr).split(",");
    for (var i in params) {
        if (!(params).hasOwnProperty(i)) continue;
        var cp = new NextReload.FunctionCallParameter(params[i].trim());
        this.params.push(cp);
    }
};
NextReload.FunctionCall.$typeDescription={"params":{name:"Array", arguments:["NextReload.FunctionCallParameter"]}};

NextReload.FunctionCallParameter = function(param) {

    this.param = param;
};
NextReload.FunctionCallParameter.prototype.param = null;
NextReload.FunctionCallParameter.$typeDescription={};

NextReload.OptionsLoader = function(property, functionCall) {

    this.propertyEl = window.document.getElementsByName(property)[0];
    this.functionCall = functionCall;
    this.parameterReferences = [];
    for (var i in functionCall.params) {
        if (!(functionCall.params).hasOwnProperty(i)) continue;
        this.parameterReferences.push(new NextReload.OptionsLoader.ParameterReference(this, functionCall.params[i]));
    }
};
NextReload.OptionsLoader.prototype.parameterReferences = null;
NextReload.OptionsLoader.prototype.functionCall = null;
NextReload.OptionsLoader.prototype.propertyEl = null;
NextReload.OptionsLoader.prototype.setParameterOptional = function(parameterName, optional) {
    for (var i in this.parameterReferences) {
        if (!(this.parameterReferences).hasOwnProperty(i)) continue;
        if (this.parameterReferences[i].callParameter.param == parameterName) {
            this.parameterReferences[i].setOptional(optional);
            return this;
        }
    }
    return this;
};
NextReload.OptionsLoader.prototype.refOnChange = function(parameterReference) {
    this.checkOptions();
};
NextReload.OptionsLoader.prototype.checkOptions = function() {
    for (var i in this.parameterReferences) {
        if (!(this.parameterReferences).hasOwnProperty(i)) continue;
        if (this.parameterReferences[i].getParameterValue() == null && !this.parameterReferences[i].isOptional()) {
            this.cleanOptions(true);
            return;
        }
    }
    this.loadOptions();
};
NextReload.OptionsLoader.prototype.loadOptions = function() {
    var selectedValues = this.getSelectedValues();
    var sParam = "";
    for (var i in this.parameterReferences) {
        if (!(this.parameterReferences).hasOwnProperty(i)) continue;
        var parameterValue = this.parameterReferences[i].getParameterValue();
        if (parameterValue == null) {
            sParam += "<null>";
        } else {
            sParam += parameterValue;
        }
        sParam += "#";
    }
    var sTypes = "";
    for (var i in this.parameterReferences) {
        if (!(this.parameterReferences).hasOwnProperty(i)) continue;
        var parameterType = this.parameterReferences[i].getParameterType();
        if (parameterType == null) {
            sTypes += "java.lang.Void";
        } else {
            //represents an unknown type
            sTypes += parameterType;
        }
        sTypes += ";";
    }
    //			'listaClasses='+listaClasses
    var bigThis = this;
    var req = next.ajax.newRequest().setUrl("/ajax/combo").setParameter("loadFunction", this.functionCall.call).setParameter("parameterList", sParam).setParameter("classesList", sTypes);
    req.setOnComplete(function(p1) {
        var lista = null;
        eval(p1);
        bigThis.setOptions(lista, selectedValues);
    });
    req.send();
};
NextReload.OptionsLoader.prototype.setOptions = function(list, selectedValues) {
    this.cleanOptions(false);
    var selectCount = 0;
    for (var i in list) {
        if (!(list).hasOwnProperty(i)) continue;
        var item = list[i];
        var option = new Option(item[1], item[0]);
        this.propertyEl.add(option);
        if (selectedValues.indexOf(item[0]) >= 0) {
            selectCount++;
            option.selected = true;
        }
    }
    if (selectCount != selectedValues.length) {
        next.events.dispatchEvent(this.propertyEl, "change");
    }
};
NextReload.OptionsLoader.prototype.getSelectedValues = function() {
    var selectedValues = [];
    for (var i = 0; i < this.propertyEl.options.length; i++) {
        var op = this.propertyEl.options[i];
        if (op.selected) {
            selectedValues.push(op.value);
        }
    }
    return selectedValues;
};
NextReload.OptionsLoader.prototype.cleanOptions = function(refreshOnValueChange) {
    var refresh = false;
    if (this.getSelectedValues().length > 0) {
        refresh = true;
    }
    while (this.propertyEl.options.length > 0) {
        this.propertyEl.options.remove(this.propertyEl.options.length - 1);
    }
    var includeBlank = this.propertyEl.getAttribute("data-includeblank");
    if (includeBlank == null || (includeBlank == "true")) {
        var blankLabel = this.propertyEl.getAttribute("data-blanklabel");
        if (blankLabel == null) {
            blankLabel = "";
        }
        this.propertyEl.add(new Option(blankLabel, "<null>"));
        this.propertyEl.selectedIndex = 0;
    }
    if (refresh && refreshOnValueChange) {
        next.events.dispatchEvent(this.propertyEl, "change");
    }
};
NextReload.OptionsLoader.ParameterReference = function(loader, callParameter) {

    this.optional = false;
    this.callParameter = callParameter;
    this.loader = loader;
    this.domElementRef = this.getDomElementRef();
    if (this.domElementRef != null) {
        var bigThis = this;
        this.attachedEvent = next.events.attachEvent(this.domElementRef, "change", function(p1) {
            bigThis.refOnChange();
        });
    }
};
NextReload.OptionsLoader.ParameterReference.prototype.callParameter = null;
NextReload.OptionsLoader.ParameterReference.prototype.loader = null;
NextReload.OptionsLoader.ParameterReference.prototype.domElementRef = null;
NextReload.OptionsLoader.ParameterReference.prototype.attachedEvent = null;
NextReload.OptionsLoader.ParameterReference.prototype.optional = null;
NextReload.OptionsLoader.ParameterReference.prototype.setOptional = function(optional) {
    this.optional = optional;
};
NextReload.OptionsLoader.ParameterReference.prototype.isOptional = function() {
    return this.optional;
};
NextReload.OptionsLoader.ParameterReference.prototype.getParameterType = function() {
    if (this.domElementRef != null) {
        return this.domElementRef.getAttribute("data-rawType");
    }
    return null;
};
NextReload.OptionsLoader.ParameterReference.prototype.getParameterValue = function() {
    var value = null;
    if ((this.domElementRef.tagName.toLowerCase() == "select")) {
        var select = this.domElementRef;
        if (select.selectedIndex >= 0) {
            value = select.options[select.selectedIndex].value;
        }
    } else {
        var input = this.domElementRef;
        value = input.value;
    }
    if (value != null && ((value.trim() == "") || (value == "<null>"))) {
        value = null;
    }
    return value;
};
NextReload.OptionsLoader.ParameterReference.prototype.detach = function() {
    next.events.detachEvent(this.domElementRef, "change", this.attachedEvent);
};
NextReload.OptionsLoader.ParameterReference.prototype.refOnChange = function() {
    this.loader.refOnChange(this);
};
NextReload.OptionsLoader.ParameterReference.prototype.getDomElementRef = function() {
    //				}
    var paramName = this.callParameter.param;
    return window.document.getElementsByName(paramName)[0];
};
NextReload.OptionsLoader.ParameterReference.$typeDescription={"callParameter":"NextReload.FunctionCallParameter", "loader":"NextReload.OptionsLoader", "domElementRef":"Element", "attachedEvent":{name:"Function1", arguments:["?","?"]}};

NextReload.OptionsLoader.$typeDescription={"parameterReferences":{name:"Array", arguments:["NextReload.OptionsLoader.ParameterReference"]}, "functionCall":"NextReload.FunctionCall", "propertyEl":"Select"};

NextReload.prototype.configure = function(property, callbackServerString) {
    var propertyInput = next.dom.toElement(property);
    if (propertyInput == null || !(propertyInput.tagName.toLowerCase() == "select")) {
        throw exception("Property " + property + " not found or is not a SELECT");
    }
    var loader = new NextReload.OptionsLoader(property, new NextReload.FunctionCall(callbackServerString));
    loader.checkOptions();
    return loader;
};
NextReload.$typeDescription={};

