var SelectManyPopup = function(element, style, styleClasses){
	element.selectManyPopup = this;
	this.input = element;
	this.styleObject = style;
	this.styleClasses = styleClasses;
	if(!next.util.isDefined(element)){
		alert('SelectManyPopup was created with \'undefined\' value');
		return;
	}
	this.configure();
};

SelectManyPopup.prototype.configure = function(){
	
	this.button = next.dom.getInnerElementById(this.input.parentNode, this.input.id + '_trigger');
	this.labels = next.dom.getInnerElementById(this.input.parentNode, this.input.id + '_labels');
	this.setLabels();
	
	if (this.button != null) {
		var bigThis = this;
		next.events.attachEvent(this.button, 'click', function(){
			
			var popupDiv = next.dom.getNewPopupDiv();
			popupDiv.style.cssText = bigThis.styleObject+';'+popupDiv.style.cssText;
			
			var options = bigThis.input.options;
			var checkList = new Array();
			
			var controlArea = next.dom.newElement('DIV', {className: next.globalMap.get('SelectManyPopup.header', 'popup_box_header')});
			var controlArea_buttons = next.dom.newElement('DIV', {className: next.globalMap.get('SelectManyPopup.headerButtons'), style: {cssFloat: 'left'}});
			var controlArea_filter = next.dom.newElement('DIV', {className: next.globalMap.get('SelectManyPopup.headerFilter'), style: {clear:'right', cssFloat: 'right'}});
			
			var markAll = next.dom.newElement('BUTTON', {innerHTML: 'Marcar Todos', className: next.globalMap.get('SelectManyPopup.button', 'button')});
			var unmarkAll = next.dom.newElement('BUTTON', {innerHTML: 'Desmarcar Todos', className: next.globalMap.get('SelectManyPopup.button', 'button')});
			
			controlArea_buttons.appendChild(markAll);
			controlArea_buttons.appendChild(unmarkAll);
			
			var filter = next.dom.newInput('text', '', 'Filtrar ', {id: next.dom.generateUniqueId(), className: next.globalMap.get('SelectManyPopup.filter')});
			controlArea_filter.appendChild(filter);
			
			popupDiv.appendChild(controlArea);
			controlArea.appendChild(controlArea_buttons);
			controlArea.appendChild(controlArea_filter);
			
			var divOptionsBlock = next.dom.newElement('DIV', {className: next.globalMap.get('SelectManyPopup.body', 'popup_box_body'), style: {clear: 'both', maxHeight: '600px', overflow: 'auto'}});
			popupDiv.appendChild(divOptionsBlock);
			
			//var text = '';
			for(var i = 0; i < options.length; i++){
				var op = options[i];
				var divOp = next.dom.newElement('DIV', {className: next.globalMap.get('SelectManyPopup.option', 'popup_box_option')});
				var check = next.dom.newInput('checkbox', '', op.text,
					{
						id: next.dom.generateUniqueId(),
						className: next.globalMap.get('SelectManyPopup.optionInput'),
						labelOptions: {className: next.globalMap.get('SelectManyPopup.optionLabel')},
						containerOptions: {className: next.globalMap.get('SelectManyPopup.optionContainer')}
					});
				check.childNodes[0].value = op.value;
				check.childNodes[0].checked = op.selected;
				divOp.appendChild(check);
				checkList.push(check.childNodes[0]);
				divOptionsBlock.appendChild(divOp);
			}
			
			var buttonArea = next.dom.newElement('DIV', {className: next.globalMap.get('SelectManyPopup.footer', 'popup_box_footer')});
			var cancel = next.dom.newElement('BUTTON', {innerHTML: 'Cancelar', className: next.globalMap.get('SelectManyPopup.button', 'button')});
			var ok = next.dom.newElement('BUTTON', {innerHTML: 'OK', className: next.globalMap.get('SelectManyPopup.button', 'button')});
			
			buttonArea.appendChild(cancel);
			buttonArea.appendChild(ok);
			
			next.events.attachEvent(cancel, 'click', function(){
				popupDiv.close();
			});
			next.events.attachEvent(ok, 'click', function(){
				bigThis.checkItems(checkList, options);
				popupDiv.close();
			});
			next.events.attachEvent(markAll, 'click', function(){
				bigThis.markAll(checkList, options);
			});
			next.events.attachEvent(unmarkAll, 'click', function(){
				bigThis.unmarkAll(checkList, options);
			});
			next.events.attachEvent(filter.childNodes[1], 'keyup', function(e){
				var filter = this.value; 
				bigThis.filter(filter, checkList, options);
				//this will force IE to re-render the block
				if(popupDiv.style.marginLeft == '0px'){
					popupDiv.style.marginLeft = '';
					popupDiv.style.marginRight = '0px';
				} else {
					popupDiv.style.marginLeft = '0px';
					popupDiv.style.marginRight = '';
				}
			});
			
			popupDiv.appendChild(buttonArea);
			
			next.style.centralizeHorizontal(popupDiv);
			popupDiv.style.top = '160px';
			
			filter.childNodes[1].focus();
			
		});
	}
	
}

SelectManyPopup.prototype.filter = function(filter, checkList, options){
	filter = next.util.removeAccents(filter.toLowerCase());
	for(var i = 0; i < checkList.length; i++){
		for(var j = 0; j < options.length; j++){
			if(checkList[i].value == options[j].value){
				if(next.util.removeAccents(options[j].text.toLowerCase()).indexOf(filter) < 0){
					checkList[i].parentNode.parentNode.style.display = 'none';
				} else {
					checkList[i].parentNode.parentNode.style.display = 'block';
				}
			}
		}
	}
}

SelectManyPopup.prototype.markAll = function(checkList, options){
	for(var i = 0; i < checkList.length; i++){
		checkList[i].checked = true;
	}
}

SelectManyPopup.prototype.unmarkAll = function(checkList, options){
	for(var i = 0; i < checkList.length; i++){
		checkList[i].checked = false;
	}
}

SelectManyPopup.prototype.checkItems = function(checkList, options){
	for(var i = 0; i < checkList.length; i++){
		if(checkList[i].parentNode.parentNode.style.display == 'none'){
			checkList[i].checked = false;
		}
	}	
	for(var i = 0; i < checkList.length; i++){
		for(var j = 0; j < options.length; j++){
			if(checkList[i].value == options[j].value){
				options[j].selected = checkList[i].checked;
			}
		}
	}
	this.setLabels();
	if(this.input.onchange){
		this.input.onchange();
	}
}

SelectManyPopup.prototype.setLabels = function(){
	var options = this.input.options;
	var labelsText = '';
	for(var j = 0; j < options.length; j++){
		if(options[j].selected){
			labelsText += ', ' + options[j].text; 
		}
	}
	labelsText += '  ';
	labelsText = labelsText.substring(2, labelsText.length);
	this.labels.value = labelsText;
}

SelectManyPopup.install = function(element, style, styleClasses){
	new SelectManyPopup(next.dom.toElement(element), style, styleClasses);
};
