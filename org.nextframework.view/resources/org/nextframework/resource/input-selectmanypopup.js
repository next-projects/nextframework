var SelectManyPopup = function(element){
	element.selectManyPopup = this;
	this.input = element;
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

			var checkList = new Array();
			var options = bigThis.input.options;

			var dialog = new NextDialogs.MessageDialog();
			dialog.setSize(NextDialogs.SIZE_LARGE);

			var titleButtonsDiv = next.dom.newElement('DIV', {className: next.globalMap.get('SelectManyPopup.headerButtons')});
			dialog.appendToTitle(titleButtonsDiv);

			var markAllBtn = next.dom.newElement('BUTTON', {innerHTML: 'Marcar Todos', className: next.globalMap.get('SelectManyPopup.button', 'button')});
			titleButtonsDiv.appendChild(markAllBtn);
			next.events.attachEvent(markAllBtn, 'click', function(){
				bigThis.markAll(checkList, options);
			});

			var unmarkAllBtn = next.dom.newElement('BUTTON', {innerHTML: 'Desmarcar Todos', className: next.globalMap.get('SelectManyPopup.button', 'button')});
			titleButtonsDiv.appendChild(unmarkAllBtn);
			next.events.attachEvent(unmarkAllBtn, 'click', function(){
				bigThis.unmarkAll(checkList, options);
			});

			var filterSpan = next.dom.newInput('text', '', 'Filtrar ',
				{
					id: next.dom.generateUniqueId(),
					className: next.globalMap.get('SelectManyPopup.filter'),
					containerOptions: {className: next.globalMap.get('SelectManyPopup.filterContainer')}
				});
			titleButtonsDiv.appendChild(filterSpan);
			next.events.attachEvent(filterSpan.childNodes[1], 'keyup', function(e){
				var filter = this.value; 
				bigThis.filter(filter, checkList, options);
			});

			var optionsDiv = next.dom.newElement('DIV', {className: next.globalMap.get('SelectManyPopup.panel')});
			dialog.appendToBody(optionsDiv);

			for(var i = 0; i < options.length; i++){

				var op = options[i];

				var optionDiv = next.dom.newElement('DIV', {className: next.globalMap.get('SelectManyPopup.option', 'popup_box_option')});
				var checkCtrl = next.dom.newInput('checkbox', '', op.text,
					{
						id: next.dom.generateUniqueId(),
						className: next.globalMap.get('SelectManyPopup.optionInput'),
						labelOptions: {className: next.globalMap.get('SelectManyPopup.optionLabel')},
						containerOptions: {className: next.globalMap.get('SelectManyPopup.optionContainer')}
					});
				checkCtrl.childNodes[0].value = op.value;
				checkCtrl.childNodes[0].checked = op.selected;

				checkList.push(checkCtrl.childNodes[0]);
				optionDiv.appendChild(checkCtrl);
				optionsDiv.appendChild(optionDiv);

			}

			dialog.setCallback({
				onClick: function(command, value, button) {
					if ((command == "OK")) {
						bigThis.checkItems(checkList, options);
					}
					return true;
				}
			});

			if(next.util.isDefined(bigThis.input.getAttribute('onrenderitems'))){
				bigThis.onRenderItems(optionsDiv, bigThis.input.getAttribute('onrenderitems'));
			}

			dialog.show();

			filterSpan.childNodes[1].focus();

		});
	}
	
}

SelectManyPopup.prototype.onRenderItems = function(optionsDiv, onrenderitems){
	try{
		eval(onrenderitems);
	}catch(e){
		alert('Erro ao executar onrenderitems!\\n'+e.name+': '+e.message);
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

SelectManyPopup.install = function(element, style){
	new SelectManyPopup(next.dom.toElement(element), style);
};