var SelectManyPopup = function(element, style){
	element.selectManyPopup = this;
	this.input = element;
	this.styleObject = style;
	if(!next.util.isDefined(element)){
		alert('SelectManyPopup was created with \'undefined\' value');
		return;
	}
	this.configure();
};

SelectManyPopup.prototype.configure = function(){
	this.container = next.dom.getParentTagByClass(this.input, 'select_many_popup_container');
	this.button = next.dom.getInnerElementByClass(this.container, 'select_many_popup_button');
	this.labels = next.dom.getInnerElementByClass(this.container, 'select_many_popup_labels');
	this.setLabels();
	var bigThis = this;
	next.events.attachEvent(this.button, 'click', function(){
		var popupDiv = next.dom.getNewPopupDiv();
		popupDiv.style.cssText = bigThis.styleObject+';'+popupDiv.style.cssText;
//		for(k in bigThis.styleObject){
//			alert(k);
//			popupDiv.style[k] = bigThis.styleObject[k];
//		}
		var options = bigThis.input.options;
		var checkList = new Array();
		
		var controlArea = next.dom.newElement('DIV', {className: 'select_many_popup_box_controls'});
		var controlArea_buttons = next.dom.newElement('DIV', {className: 'select_many_popup_box_controls_buttons', style: {cssFloat: 'left'}});
		var controlArea_filter = next.dom.newElement('DIV', {className: 'select_many_popup_box_controls_filter', style: {clear:'right', cssFloat: 'right'}});
		
		var markAll = next.dom.newElement('BUTTON', {innerHTML: 'Marcar Todos'});
		var unmarkAll = next.dom.newElement('BUTTON', {innerHTML: 'Desmarcar Todos'});
		unmarkAll.style.marginLeft = '6px';
		
		controlArea_buttons.appendChild(markAll);
		controlArea_buttons.appendChild(unmarkAll);
		
		var filter = next.dom.newInput('text', '', 'Filtrar ', {title:'Apresenta apenas os itens com o filtro digitado.', id: next.dom.generateUniqueId()});
		controlArea_filter.appendChild(filter);
		
		popupDiv.appendChild(controlArea);
		controlArea.appendChild(controlArea_buttons);
		controlArea.appendChild(controlArea_filter);
		
		var divOptionsBlock = next.dom.newElement('DIV', {style: {clear: 'both', maxHeight: '600px', overflow: 'auto'}});
		popupDiv.appendChild(divOptionsBlock);
		
//		var text = '';
		for(var i = 0; i < options.length; i++){
			var op = options[i];
			var divOp = next.dom.newElement('DIV', {className: 'select_many_popup_box_opdiv'});
			var check = next.dom.newInput('checkbox', '', op.text, {id: next.dom.generateUniqueId()});
			check.childNodes[0].value = op.value;
			check.childNodes[0].checked = op.selected;
			
			divOp.appendChild(check);
			
			checkList.push(check.childNodes[0]);
			
//			if(i == 0){
//				divOp.style.clear = 'both';
//			}
//				text += '<div style="float:left; width: 200px"> <input type="checkbox" checked="checked">'+op.text + '</div>';
//			} else {
//				text += '<div style="float:left; width: 200px"> <input type="checkbox"> '+op.text + '</div>';
//			}
			divOptionsBlock.appendChild(divOp);
		}
//		popupDiv.innerHTML = text;
		
		var buttonArea = next.dom.newElement('DIV', {className: 'select_many_popup_box_buttons', style: {clear:'both', textAlign: 'right'}});
		var cancel = next.dom.newElement('BUTTON', {innerHTML: 'Cancelar', title: 'Cancela a operação.'});
		var ok = next.dom.newElement('BUTTON', {innerHTML: 'Ok', title: 'Seleciona os itens visíveis marcados.'});
		ok.style.marginLeft = '6px';
		
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
//			popupDiv.style.width = '650px';
		});
		
		popupDiv.appendChild(buttonArea);
		
		next.style.centralizeHorizontal(popupDiv);
		popupDiv.style.top = '160px';
		
		filter.childNodes[1].focus();
	});
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

SelectManyPopup.install = function(element, style){
	new SelectManyPopup(next.dom.toElement(element), style);
};