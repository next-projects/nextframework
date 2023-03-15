var NextSuggestStaticListProvider = function(items) {
    NextSuggestSuggestionProvider.call(this);
    this.items = items;
};
stjs.extend(NextSuggestStaticListProvider, NextSuggestSuggestionProvider);

NextSuggestStaticListProvider.prototype.items = null;
NextSuggestStaticListProvider.prototype.requestSuggestions = function(suggestElement) {
    var queryText = suggestElement.getQueryText();
    var suggestions = [];
    for (var i in this.items) {
        if (!(this.items).hasOwnProperty(i)) continue;
        var item = this.items[i];
        if (suggestElement.itemMatcher.match(queryText, item)) {
            suggestions.push(item);
        }
    }
    suggestElement.suggest(suggestions);
};
NextSuggestStaticListProvider.$typeDescription=stjs.copyProps(NextSuggestSuggestionProvider.$typeDescription, {"items":{name:"Array", arguments:["NextSuggestSuggestionProvider.SuggestItem"]}});

