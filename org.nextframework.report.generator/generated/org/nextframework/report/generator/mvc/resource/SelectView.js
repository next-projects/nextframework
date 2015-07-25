var SelectView = function(){};

stjs.extend(SelectView, Selectable);

SelectView.prototype.select = function(name, properties){};
SelectView.prototype.unselect = function(name){};
SelectView.$typeDescription=stjs.copyProps(Selectable.$typeDescription, {});

