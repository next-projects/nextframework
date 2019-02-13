/**
 * Fun??o utilizada como callback de janelas que foram criadas com o objectivo
 * de selecionar algum bean.
 * Possui as informa??es necess?rias para a janela saber como deve preencher o
 * formu?rio que a chamou
 */
function selecionarCallbackObject(valueInput, labelInput, valueType, button, buttonUnselect, callback) {
    this.valueInput = valueInput;
	this.labelInput = labelInput;
	this.valueType = valueType;
	this.button = button;
	this.buttonUnselect = buttonUnselect;
	this.callback = callback;
}

function imprimirVoltar() {
	if(top.cadastrar) {
		document.write("<input type='button' value='Voltar' onclick='refreshPai()'>");
	}
}

function refreshPai() {
	//top.opener.document.forms[0].ACTION.value = top.cadastrar;
	//alert(top.opener.document.forms[0].ACTION.value);
	top.opener.document.forms[0].submit();
	top.close();
}

/**
 * Imprime o bot?o selecionar onde for necess?rio
 * O bot?o selecionar s? ? impresso onde a classe for da hierarquia da classe que pediu para selecionar
 */
function imprimirSelecionar(classesList, texto){
	//alert('valor '+valor+'  label '+label+'    classesList '+classesList);
	//alert(top.selecionarCallback);
	if(top.selecionarCallback){
		
		//document.write("dd");
		var ok = false;
		for(i in classesList){
			var clazz = classesList[i];
			if(clazz == top.selecionarCallback.valueType){
				ok = true;
			}
		}
		if(ok){
			//document.write("<a href=\"javascript:alert('Info: valor="+valor+" label="+label+"')\">info</a>&nbsp;");
			document.write(texto);	
		}
		//DEBUG ----- c?digo abaixo ? debug descomente se nao aparecer o botao selecionar
		//else {
		//	alert('A classe \n'+classesList[0]+' \nnao eh a mesma ou uma subclasse de \n'+top.selecionarCallback.valueType);
		//}
	}
}

/**
 *
 */
function selecionar(valor, label, forcombo){
	var isNN = navigator.appName.indexOf("Netscape")!= -1;
	if(top.selecionarCallback){
		if(forcombo){
		
			var callback = top.selecionarCallback;
			var callbackcallback = callback.callback;
			var onchangeFunction = callback.valueInput.onchange;
			//callback.valueInput.value = valor;
			//alert(callback.valueInput.name);
			
			var combo = callback.valueInput;
			var options = combo.options;
			var op = new Option(label, valor, false, true);
			var isNN = navigator.appName.indexOf("Netscape")!= -1;
			if(isNN){
				options.add(op);
			} else {
				callbackcallback(label, valor);
			}
				
			//options[options.length++].text = label;
			
			
			//combo.value = valor;
			
			if(onchangeFunction){
				onchangeFunction();
			}

			if(isNN){
				setTimeout('top.close()', 500);
			} else {
				top.close();
			}
		} else {

			var callback = top.selecionarCallback;
			var onchangeFunction = callback.labelInput.onchange;
			callback.valueInput.value = valor;
			callback.labelInput.value = label;

//			alert(onchangeFunction);
			if(onchangeFunction){
				onchangeFunction();
			}
			callback.button.style.display = 'none';
			callback.buttonUnselect.style.display = '';
			
			if(isNN){
				setTimeout('top.close()', 500);
			} else {
				top.close();
			}

		}

	}

}

function preparaHtmlArea(editorurl){
	_editor_url = editorurl;
	var win_ie_ver = parseFloat(navigator.appVersion.split("MSIE")[1]);
	if (navigator.userAgent.indexOf('Mac')        >= 0) { win_ie_ver = 0; }
	if (navigator.userAgent.indexOf('Windows CE') >= 0) { win_ie_ver = 0; }
	if (navigator.userAgent.indexOf('Opera')      >= 0) { win_ie_ver = 0; }
	if (win_ie_ver >= 5.5) {
	  document.write('<scr' + 'ipt src="' +editorurl+ 'editor.js"');
	  document.write(' language="Javascript1.2"></scr' + 'ipt>');  
	}
	else { 
		document.write('<scr'+'ipt>function editor_generate() { return false; }</scr'+'ipt>'); 
	}
	//alert(_editor_url);
}

function limparCombo(combo, includeblank, currentValue,blankLabel){
	pararEm = 0;
	var remove = 1;
	
	if(combo.type == 'select-multiple'){
		currentValue = false;
	}

	while(combo.options.length > pararEm + remove - 1){
		//alert((combo.options.length -1));
		if(currentValue){
			if(remove == 1 && currentValue != '<null>' && combo.options[combo.options.length - 1].value == currentValue){
				remove = 2;
			}
		}
		if((combo.options.length - remove) >= 0){
			combo.remove((combo.options.length - remove));
		}
		
	}		
	
	if(blankLabel == null) blankLabel = " ";
	
	var op = new Option();
	op.text = blankLabel;
	op.value = '<null>';
	combo.options.add(op);//forçar o redimensionamento do form
	combo.remove(pararEm + remove - 1);
	
	if(includeblank){
		op = new Option();
		op.text = blankLabel;
		op.value = '<null>';
		combo.options.add(op);
	}
	
	//Se o componente for 'SelectManyPopup', a label deve ser redefinida
	if (combo.selectManyPopup) {
		combo.selectManyPopup.setLabels();
	}
	
}

function enableProperties(form){
	var elements = form.elements;
	for(var i = 0; i < elements.length; i++){
		var element = elements[i];
		var disabled = element.getAttribute("originaldisabled");
		if(disabled == null){
			element.disabled = false;
		}
	}
}

function aplicaHTML(html, elemento){
	try {
		var comp = elemento;
		if( typeof(elemento) == 'string' ){
			comp = document.getElementById(elemento);
		}
		if (comp != null) {
			
			comp.innerHTML = html;
			
			//Procura por novos blocos de script e roda
			var scripts = comp.getElementsByTagName("script");
			for(var i = 0; i < scripts.length ; i++){
				var script = scripts[i];
				try{
					//alert(script.text);
					globalEval(script.text);
				}catch(e){
					alert(e);
				}
			}
		}else{
			alert("Elemento não encontrado: " + elemento);
		}
	} catch(e1){
		alert('Não foi possível adicionar o javascript dinâmico. '+ e);
	}
}

function globalEval(src) {
    if (window.execScript) {
        window.execScript(src);
        return;
    }
    var fn = function() {
        window.eval.call(window,src);
    };
    fn();
};