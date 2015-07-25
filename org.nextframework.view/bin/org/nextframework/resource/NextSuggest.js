var NextSuggest = function() {

    this.providers = {};
};
NextSuggest.DEFAULT_KEY_TIMEOUT = 500;
NextSuggest.DEFAULT_MINIMUM_CHARS = 2;
NextSuggest.prototype.providers = null;
NextSuggest.prototype.install = function(el, providerName) {
    var elName = el.name.substring(0, el.name.length - 5);
    el = window.document.getElementsByName(elName)[0];
    if (el.getAttribute("data-suggestinstalled") != null) {
        return;
    }
    el.setAttribute("data-suggestinstalled", "true");
    new NextSuggest.SuggestElement(el, this.providers[providerName]);
};
NextSuggest.SuggestElement = function(el, provider) {

    this.valueInput = next.dom.toElement(el);
    if (this.valueInput == null) {
        throw exception("null input in suggest");
    }
    var textInputName = this.valueInput.name + "_text";
    var elementsByName = window.document.getElementsByName(textInputName);
    if (elementsByName.length > 0) {
        this.textInput = elementsByName[0];
    }
    if (this.textInput == null) {
        throw exception("null textinput in suggest, no element with name " + textInputName + " found");
    }
    this.imgOk = next.dom.getInnerElementById(this.textInput.parentNode, "sg_ok");
    this.imgNotOk = next.dom.getInnerElementById(this.textInput.parentNode, "sg_notok");
    this.timeoutManager = new NextSuggest.TimeoutManager(this);
    this.suggestView = new NextSuggest.SuggestView(this);
    this.itemMatcher = new NextSuggest.ItemMatcher();
    this.configureValidationEvents();
    this.suggestionProvider = provider;
};
NextSuggest.SuggestElement.prototype.valueInput = null;
NextSuggest.SuggestElement.prototype.textInput = null;
NextSuggest.SuggestElement.prototype.timeoutManager = null;
NextSuggest.SuggestElement.prototype.suggestView = null;
NextSuggest.SuggestElement.prototype.itemMatcher = null;
NextSuggest.SuggestElement.prototype.suggestionProvider = null;
NextSuggest.SuggestElement.prototype.imgOk = null;
NextSuggest.SuggestElement.prototype.imgNotOk = null;
NextSuggest.SuggestElement.prototype.configureValidationEvents = function() {
    var bigThis = this;
    next.events.attachEvent(this.textInput, "blur", function(p1) {
        setTimeout(function() {
            bigThis.onBlur();
        }, 200);
    });
};
NextSuggest.SuggestElement.prototype.onBlur = function() {
    this.suggestView.hide();
    if ((this.valueInput.value == "")) {
        if (this.textInput.value.length > 0) {
            this.imgNotOk.style.display = "";
        } else {
            this.imgNotOk.style.display = "none";
        }
    }
};
NextSuggest.SuggestElement.prototype.toString = function() {
    return "suggest '" + this.valueInput.name + "'";
};
NextSuggest.SuggestElement.prototype.getQueryText = function() {
    if (this.textInput.value.length >= NextSuggest.DEFAULT_MINIMUM_CHARS) {
        return this.textInput.value;
    } else {
        return null;
    }
};
NextSuggest.SuggestElement.prototype.triggerSuggestion = function() {
    if (this.textInput.value.length == 0) {
        this.suggestView.hide();
        return;
    }
    if (this.suggestionProvider == null) {
        throw exception("suggestionProvider is not configured. Set suggestionProvider in the SuggestElement element before using the component");
    }
    var text = this.getQueryText();
    if (text == null) {
        this.suggestView.hide();
    } else {
        console.info("requestSuggestions");
        this.suggestView.show();
        this.suggestionProvider.requestSuggestions(this);
    }
};
NextSuggest.SuggestElement.prototype.suggest = function(suggestions) {
    console.info("suggesting");
    this.suggestView.setSuggestions(suggestions);
};
NextSuggest.SuggestElement.prototype.handleUpArrow = function() {
    this.suggestView.selectUp();
};
NextSuggest.SuggestElement.prototype.handleDownArrow = function() {
    this.suggestView.selectDown();
    this.suggestView.show();
};
NextSuggest.SuggestElement.prototype.handleEnter = function() {
    this.suggestView.selectItem();
};
NextSuggest.SuggestElement.prototype.handleBackspace = function() {
    this.valueInput.value = "";
};
NextSuggest.SuggestElement.prototype.onItemSelected = function(suggestItem) {
    var bigThis = this;
    this.imgNotOk.style.display = "none";
    this.imgOk.style.display = "";
    this.imgOk.style.opacity = 1;
    setTimeout(function() {
        next.effects.fade(bigThis.imgOk, 1.0, 0);
    }, 1000);
};
NextSuggest.SuggestElement.$typeDescription={"valueInput":"Input", "textInput":"Input", "timeoutManager":"NextSuggest.TimeoutManager", "suggestView":"NextSuggest.SuggestView", "itemMatcher":"NextSuggest.ItemMatcher", "suggestionProvider":"NextSuggestSuggestionProvider", "imgOk":"Element", "imgNotOk":"Element"};

NextSuggest.ItemMatcher = function(){};

NextSuggest.ItemMatcher.prototype.match = function(text, item) {
    var a = next.util.removeAccents(item._t.toLowerCase());
    var b = next.util.removeAccents(text.toLowerCase());
    return a.indexOf(b) == 0;
};
NextSuggest.ItemMatcher.$typeDescription={};

NextSuggest.SuggestView = function(suggestElement) {

    this.suggestElement = suggestElement;
    var textInput = suggestElement.textInput;
    this.suggestViewDiv = next.dom.newElement("div");
    var height = next.style.getFullHeight(textInput);
    var width = next.style.getFullWidth(textInput);
    this.suggestViewDiv.className = "nssv";
    this.suggestViewDiv.style.marginTop = height + "px";
    this.suggestViewDiv.style.minWidth = (width + 50) + "px";
    this.suggestViewDiv.style.fontSize = next.style.getStyleProperty(textInput, "font-size");
    this.suggestViewDiv.style.fontFamily = next.style.getStyleProperty(textInput, "font-family");
    this.hide();
    textInput.parentNode.insertBefore(this.suggestViewDiv, textInput);
};
NextSuggest.SuggestView.SELECTED_INDEX_STYLE_CLASS = "nssi";
NextSuggest.SuggestView.prototype.suggestViewDiv = null;
NextSuggest.SuggestView.prototype.selectedIndex = null;
NextSuggest.SuggestView.prototype.selectedLi = null;
NextSuggest.SuggestView.prototype.ul = null;
NextSuggest.SuggestView.prototype.suggestElement = null;
NextSuggest.SuggestView.prototype.suggestions = null;
NextSuggest.SuggestView.prototype.selectItem = function() {
    var suggestItem = this.getSelectedItem();
    this.setItem(suggestItem);
};
NextSuggest.SuggestView.prototype.setItem = function(suggestItem) {
    if (suggestItem != null) {
        this.suggestElement.valueInput.value = suggestItem._v;
        this.suggestElement.textInput.value = suggestItem._t;
        this.hide();
        this.suggestElement.onItemSelected(suggestItem);
    }
};
NextSuggest.SuggestView.prototype.getSelectedItem = function() {
    if (this.selectedLi != null) {
        var si = (this.selectedLi)["data-suggestitem"];
        return si;
    }
    return null;
};
NextSuggest.SuggestView.prototype.selectDown = function() {
    if (this.suggestions == null || this.selectedIndex == this.suggestions.length - 1) {
        return;
    }
    this.unselect();
    if (this.ul.childNodes.length > this.selectedIndex + 1) {
        this.select(this.selectedIndex + 1);
    }
    this.checkScroll(true);
};
NextSuggest.SuggestView.prototype.selectUp = function() {
    if (this.selectedIndex == 0) {
        return;
    }
    this.unselect();
    if (this.selectedIndex - 1 >= 0) {
        this.select(this.selectedIndex - 1);
    }
    this.checkScroll(false);
};
NextSuggest.SuggestView.prototype.checkScroll = function(down) {
    var totalHeight = next.style.getFullHeight(this.ul);
    if (this.selectedLi != null) {
        var liHeight = next.style.getFullHeight(this.selectedLi);
        var top = ((this.selectedIndex) * liHeight);
        top -= liHeight * 5;
        if (top < 0) {
            top = 0;
        }
        this.ul.scrollTop = top;
    }
};
NextSuggest.SuggestView.prototype.unselect = function() {
    if (this.selectedLi != null) {
        next.style.removeClass(this.selectedLi, NextSuggest.SuggestView.SELECTED_INDEX_STYLE_CLASS);
    }
};
NextSuggest.SuggestView.prototype.select = function(i) {
    this.selectedLi = this.ul.childNodes[i];
    this.selectedIndex = i;
    next.style.addClass(this.selectedLi, NextSuggest.SuggestView.SELECTED_INDEX_STYLE_CLASS);
};
NextSuggest.SuggestView.prototype.setSuggestions = function(suggestions) {
    this.suggestions = suggestions;
    this.selectedIndex = -1;
    this.suggestViewDiv.innerHTML = "";
    this.ul = next.dom.newElement("ul");
    for (var i in suggestions) {
        if (!(suggestions).hasOwnProperty(i)) continue;
        var item = suggestions[i];
        var li = this.createLiFromItem(item);
        this.ul.appendChild(li);
    }
    this.suggestViewDiv.appendChild(this.ul);
};
NextSuggest.SuggestView.prototype.createLiFromItem = function(item) {
    var bigThis = this;
    var li = next.dom.newElement("li");
    var textInput = this.suggestElement.textInput;
    li.innerHTML = item._t;
    //			li.style.lineHeight = next.style.getStyleProperty(textInput, "line-height");
    (li)["data-suggestitem"] = item;
    next.events.attachEvent(li, "mouseover", function(p1) {
        bigThis.unselect();
        next.style.addClass(li, NextSuggest.SuggestView.SELECTED_INDEX_STYLE_CLASS);
    });
    next.events.attachEvent(li, "mouseout", function(p1) {
        bigThis.unselect();
        next.style.removeClass(li, NextSuggest.SuggestView.SELECTED_INDEX_STYLE_CLASS);
    });
    next.events.attachEvent(li, "click", function(p1) {
        console.log("seting item " + item);
        bigThis.unselect();
        bigThis.setItem(item);
    });
    return li;
};
NextSuggest.SuggestView.prototype.hide = function() {
    this.suggestViewDiv.style.display = "none";
};
NextSuggest.SuggestView.prototype.show = function() {
    this.suggestViewDiv.style.display = "";
};
NextSuggest.SuggestView.$typeDescription={"suggestViewDiv":"Div", "selectedLi":"LI", "ul":"UList", "suggestElement":"NextSuggest.SuggestElement", "suggestions":{name:"Array", arguments:["NextSuggestSuggestionProvider.SuggestItem"]}};

NextSuggest.TimeoutManager = function(suggestElement) {

    this.suggestElement = suggestElement;
    this.keyStrokeTimeout = NextSuggest.DEFAULT_KEY_TIMEOUT;
    this.installEvents(suggestElement.textInput);
};
NextSuggest.TimeoutManager.prototype.timeoutHandler = null;
NextSuggest.TimeoutManager.prototype.keyStrokeTimeout = null;
NextSuggest.TimeoutManager.prototype.suggestElement = null;
NextSuggest.TimeoutManager.prototype.installEvents = function(textInput) {
    var bigThis = this;
    next.events.attachEvent(textInput, "keyup", function(p1) {
        bigThis.handleKeyUp(p1);
    });
    next.events.attachEvent(textInput, "keydown", function(p1) {
        bigThis.handleKeyDown(p1);
    });
};
NextSuggest.TimeoutManager.prototype.handleKeyDown = function(p1) {
    var keyCode = p1.keyCode;
    if (keyCode == 38) {
        //UP ARROW
        next.events.cancelEvent(p1);
    } else if (keyCode == 40) {
        //DOWN ARROW
        next.events.cancelEvent(p1);
    }
};
NextSuggest.TimeoutManager.prototype.handleKeyUp = function(p1) {
    var bigThis = this;
    var keyCode = p1.keyCode;
    if (keyCode == 38) {
        //UP ARROW
        this.suggestElement.handleUpArrow();
    } else if (keyCode == 40) {
        //DOWN ARROW
        this.suggestElement.handleDownArrow();
    } else if (keyCode == 13) {
        //ENTER
        this.suggestElement.handleEnter();
    } else {
        if (keyCode == 8) {
            //BACKSPACE
            this.suggestElement.handleBackspace();
        }
        if (this.timeoutHandler != null) {
            clearTimeout(this.timeoutHandler);
        }
        this.timeoutHandler = setTimeout(function() {
            bigThis.onKeyTimeout(p1);
        }, this.keyStrokeTimeout);
    }
};
NextSuggest.TimeoutManager.prototype.onKeyTimeout = function(p1) {
    this.suggestElement.triggerSuggestion();
};
NextSuggest.TimeoutManager.$typeDescription={"timeoutHandler":"TimeoutHandler", "suggestElement":"NextSuggest.SuggestElement"};

NextSuggest.$typeDescription={"providers":{name:"Map", arguments:[null,"NextSuggestStaticListProvider"]}};

