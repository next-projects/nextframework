var READY_STATE_UNINITIALIZED=0;
var READY_STATE_LOADING=1;
var READY_STATE_LOADED=2;
var READY_STATE_INTERACTIVE=3;
var READY_STATE_COMPLETE=4;
var req;

function getInputValue(input){
	return next.dom.getInputValue(input);
}

function ajaxcallerrorcallback(request){
	alert('Erro ao executar ajax!\n'+request.status + ' - '+request.statusText);
	document.write(request.responseText);
}

function sendRequest(url,params,HttpMethod, callbackfunction1, errorcallback, originalArguments){
	if (!HttpMethod){
		HttpMethod="POST";
	}
	if(callbackfunction1){
		var request = getXMLHTTPRequest();
		request.onreadystatechange = function innerFunctionOnReadyStateChangeWithCallback(){
			var ready = request.readyState;
			var data = null;
			if(ready == READY_STATE_COMPLETE) {
				data = request.responseText;
				if(request){
					if(request.status && request.status == 200){
						callbackfunction1(data, originalArguments);				
					} else {
						if(callbackfunction1.onerror){
							callbackfunction1.onerror(request, originalArguments);
						} else if(errorcallback){
							errorcallback(request, originalArguments);
						} else {
							callbackfunction1(data, originalArguments);
						}
					}
				}
			}
		};
		request.open(HttpMethod, url, true);
	//	request.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=ISO-8859-1");
		request.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
		request.send(params);
	} else {
		req=getXMLHTTPRequest();
		if (req){
			if((callbackfunction1 == null) == true){
				req.onreadystatechange = onReadyStateChange;			
			} else {
				req.onreadystatechange = function onReadyStateChangeWithCallback(){
					if(req){
						var ready=req.readyState;
						var data=null;
						if (ready==READY_STATE_COMPLETE){
							data=req.responseText;
							callbackfunction1(data);		
						} else {
							data="loading...["+ready+"]";
						}
					}
				};
			}
	
			req.open(HttpMethod,url,true);
			//req.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=ISO-8859-1");
			req.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
			req.send(params);
		}
	}
}

function getXMLHTTPRequest() {
	var xRequest=null;
	if (window.XMLHttpRequest) {
		xRequest=new XMLHttpRequest();
	} else if (typeof ActiveXObject != "undefined"){
		xRequest=new ActiveXObject("Microsoft.XMLHTTP");
	}
	return xRequest;
}

var callbackFunction;

function onReadyStateChange(){
	if(req){
		var ready=req.readyState;
		var data=null;
		if (ready==READY_STATE_COMPLETE){
			data=req.responseText;
			if(callbackFunction){
				callbackFunction(data);		
			}
		} else {
			data="loading...["+ready+"]";
		}
	}
}

function addItensToCombo(combo, lista, holdingValue){
	var selectedOp;
	if(holdingValue){
		selectedOp = combo.options[0];
	}
	if(combo.type == 'select-multiple'){
		holdingValue = false;
	}
	var valorMantido = false;

	for(i in lista){
		combo.options.add(new Option(lista[i][1], lista[i][0]));
		if(holdingValue && combo.options[combo.options.length - 1].value == selectedOp.value){
			combo.options[combo.options.length - 1].selected = true;
			combo.options.remove(0);
			valorMantido = true;
		}
	}
	if(holdingValue && !valorMantido && combo.options.length > 0 && combo.options[0].value != '<null>'){
		combo.options.remove(0);		
	}
	return valorMantido;
}
var nowLoadingItens = new Array();
var ajaxLoadComboLoading = new Array();

function ajaxLoadCombo(appname, combo, type, loadfunction, classesList, parameterList, label, parentValue){
	var params = 
			'parentValue='+parentValue + '&' +
			'label='+label + '&' +
			'type='+type + '&' +
			'loadFunction='+loadfunction + '&' +
			'parameterList='+parameterList + '&' +
			'classesList='+classesList
		;
	var callback = function(data){
		eval(data);
		for(var i = 0; i < ajaxLoadComboLoading.length; i++){
			if(ajaxLoadComboLoading[i] == params){
				ajaxLoadComboLoading.splice(i,1);
			}
		}
		try {
			lista;
		}catch(e){
			lista = [];
		}
		
		if (combo.setItens != null) {
			combo.setItens(lista);
		} else {
			var valorMantido = addItensToCombo(combo, lista, false);
			if(!combo.wasEmpty && !valorMantido){combo.onchange();}
		}
		
		//Se o componente for 'SelectManyPopup', a label deve ser redefinida
		if (combo.selectManyPopup) {
			combo.selectManyPopup.setLabels();
		}
		
	};

	for(var i = 0; i < ajaxLoadComboLoading.length; i++){
		if(ajaxLoadComboLoading[i] == params){
			return;
		}
	}
	ajaxLoadComboLoading.push(params);
	sendRequest(appname+'/ajax/combo', params, 'POST', callback);
}



function ItemToLoad(formname, property){
	this.formname = formname;
	this.property = property;
}

function registerLoad(formname, property){
	for(var i = 0; i < nowLoadingItens.length; i++){
		if(nowLoadingItens[i].property == property){
			return;
		}
	}
	nowLoadingItens.push(new ItemToLoad(formname, property));
	loadItensInStack();
}

function loadItensInStack(){
	while(nowLoadingItens.length > 0){
		var nowLoading = nowLoadingItens.pop();
		var formulario = eval(nowLoading.formname);
		formulario[nowLoading.property].loadItens();
	}
}

function lockAll(formname, lock) {
	var formulario = eval(formname);
	var el = formulario.elements;
	for(var i = 0; i < el.length; i+=1){
		el[i].disabled = lock;
	}
}