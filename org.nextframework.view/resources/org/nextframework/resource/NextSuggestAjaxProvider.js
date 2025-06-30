var NextSuggestAjaxProvider = function(serverId, serverUrl) {
    NextSuggestSuggestionProvider.call(this);
    this.serverId = serverId;
    this.serverUrl = serverUrl;
};
stjs.extend(NextSuggestAjaxProvider, NextSuggestSuggestionProvider);

NextSuggestAjaxProvider.prototype.serverId = null;
NextSuggestAjaxProvider.prototype.serverUrl = null;
NextSuggestAjaxProvider.prototype.requestSuggestions = function(suggestElement) {
    var request = next.ajax.newRequest();
    request.setUrl(this.serverUrl);
    request.setParameter("serverId", this.serverId);
    request.setParameter("_text", suggestElement.getQueryText());
    request.setAppendContext(false);
    request.setCallback(function(p1) {
        suggestElement.suggest(p1);
    });
    request.send();
};
NextSuggestAjaxProvider.$typeDescription=stjs.copyProps(NextSuggestSuggestionProvider.$typeDescription, {});

