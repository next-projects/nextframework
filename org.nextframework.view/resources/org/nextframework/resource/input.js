//------------------------------------------------------------

function getTecla(event){
	if(navigator.appName.indexOf("Netscape")!= -1) {
		return event.which;
	}
	else {
		return event.keyCode;
	}
}

function getChar(event){
	if(navigator.appName.indexOf("Netscape")!= -1) {
		return String.fromCharCode(event.which);
	}
	else {
		return String.fromCharCode(event.keyCode);
	}
}

function valida_tecla(campo, event, acceptEnter) {
	var BACKSPACE = 8;
	var key;
	var tecla;
	CheckTAB=true;
	if(navigator.appName.indexOf("Netscape")!= -1) {
		tecla = event.which;
	}
	else {
		tecla = event.keyCode;
	}
	key = String.fromCharCode(tecla);
	if (tecla == 13) {
		if(acceptEnter){
			return true;
		} else {
			return false;
		}
	}
	if (tecla == BACKSPACE) {
		return true;
	}
	return (isNum(key));
}

function valida_tecla_data(campo, event, pattern) {
	var BACKSPACE = 8;
	var key;
	var tecla;
	CheckTAB=true;

	if(navigator.appName.indexOf("Netscape")!= -1) {
		tecla = event.which;
	}
	else {
		tecla = event.keyCode;
	}

	//ignorar se for TAB .. no firefox o tab é acusado 0
	if(tecla == 0){
		return true;
	}
	//ignorar se for SHIFT
	if(tecla == 16){
		return false;
	}
	
	key = String.fromCharCode(tecla);
	
	
	if(tecla != 8){
		var l = campo.value.length;
		
		var charBefore;
		if(l > 0){
			charBefore = pattern.charAt(l - 1);
		}
		var currChar = pattern.charAt(l);
		if(	(currChar == 'm' ||
			currChar == 's') && charBefore != currChar){ //primeira casa dos minutos ou segundos
			return isTime(key);
		}
		if( (currChar == 'h' || currChar == 'H') && charBefore != currChar){ //primeira casa da hora
			return key == '0' || key == '1' || key == '2';
		} else if( (currChar == 'h' || currChar == 'H') && charBefore == currChar){ //segunda casa da hora
			if(campo.value.charAt(l - 1) == '2'){
				return key == '0' || key == '1' || key == '2'|| key == '3' || key == '4';
			}
		}
		
		if( (currChar == 'M' ) && charBefore != currChar){ //primeira casa do mes
			return key == '0' || key == '1';
		} else if( (currChar == 'M' ) && charBefore == currChar){ //segunda casa do mes
			if(campo.value.charAt(l - 1) == '1'){
				return key == '0' || key == '1' || key == '2';
			} else if(key == '0'){
				return false;
			}
		}
		if( (currChar == 'd' ) && charBefore != currChar){ //primeira casa do dia
			return key == '0' || key == '1' || key == '2' || key == '3';
		} else if( (currChar == 'd' ) && charBefore == currChar){ //segunda casa do dia
			if(campo.value.charAt(l - 1) == '3'){
				return key == '0' || key == '1';			
			} else if(campo.value.charAt(l - 1) == '0' && key == '0'){
				return false;
			}
		}
	}

	if (tecla == 13) {
		return false;
	}
	if (tecla == BACKSPACE) {
		return true;
	}
	return (isNum(key));
}

function isTime( caractere ) { 
	var strValidos = '012345'; 
	if (strValidos.indexOf(caractere) == -1) {
		return false; 
	}
	return true; 
}

function isNum( caractere ) { 
	var strValidos = '0123456789'; 
	if (strValidos.indexOf(caractere) == -1) {
		return false; 
	}
	return true; 
}

//------------------------------------------------------------

function mascara_hora(el) {
	var myhour = '';
	myhour = myhour + el.value; 
	if (myhour.length == 2) {
		myhour = myhour + ':'; 
		el.value = myhour; 
	}
}

function verifica_hora(hour) {
	situacao = 1;
	hora = (hour.substring(0,2)); 
	minutos = (hour.substring(3,5)); 
	ponto = (hour.substring(2,3));
	if(hora>24) {
		situacao = 0;
		if(minutos>60) {
			situacao = 0;
			if(ponto != ':') {
				situacao = 0;
				if(situacao==0) {
					alert('Hora inválida! Exemplo de hora válida: 09:30');
					hour.value='';
					return false;
				}
				return true;
			}
		}
	}
}

//------------------------------------------------------------

function mascara_data(el, event, pattern) {
	var tecla = getTecla(event);
	if(tecla != 8){
		var mydata = el.value;
		var l = mydata.length;
		
		var currChar = pattern.charAt(l);
		if( currChar != 'd' && 
			currChar != 'M' &&
			currChar != 'y' &&
			currChar != 'h' &&
			currChar != 'm' &&
			currChar != 's' &&
			currChar != 'H'){
			mydata = mydata + currChar;
			el.value = mydata;
		}
		
		var beforeChar = pattern.charAt(l - 1);
		if( beforeChar != 'd' && 
			beforeChar != 'M' &&
			beforeChar != 'y' &&
			beforeChar != 'h' &&
			beforeChar != 'm' &&
			beforeChar != 's' &&
			beforeChar != 'H' &&
			beforeChar != mydata.charAt(l - 1)){
			mydata = mydata.substring(0, l-1) + beforeChar + mydata.substring(l-1);
			el.value = mydata;
		}
	}
}

function verifica_data(data) {
	var situacao = '';

	if (data.length == 0) {
		return true;
	}

	if (data.length != 10) {
		situacao = 'falsa';
	}
	else {
		mes = (data.substring(3,5));
	
		// verifica se o mes e valido
		if (mes < 1 || mes > 12 ) {
			situacao = 'falsa';
		}
		else {
			dia = (data.substring(0,2));

			// Verifica se o dia é válido para cada mês, exceto fevereiro.
			if (dia < 1 || dia > 31 || (dia > 30 && (mes == 4 || mes == 6 || mes == 9 || mes == 11))) {
				situacao = 'falsa';
			}
		
			// Verifica se o dia é válido para o mês de fevereiro.
			if (mes == 2 && (dia < 1 || dia > 29 || (dia > 28 && (parseInt(ano/4) != ano/4)))) {
				situacao = 'falsa';
			}
		}
	}

	if (situacao == 'falsa') {
		alert('Data inválida!');
		return false;
	}

	return true;
}

//------------------------------------------------------------

function mascara_cpf(el) {
	var mydata = '';
	mydata = mydata + el.value;
	if (mydata.length == 3) {
		mydata = mydata + '.';
		el.value = mydata;
	}
	if (mydata.length == 7) {
		mydata = mydata + '.';
		el.value = mydata;
	}
	if (mydata.length == 11) {
		mydata = mydata + '-';
		el.value = mydata;
	}
}

//------------------------------------------------------------

function mascara_cep(el) {
	var mydata = '';
	mydata = mydata + el.value;
	if (mydata.length == 5) {
		mydata = mydata + '-';
		el.value = mydata;
	}
}

//------------------------------------------------------------

function mascara_cnpj(el) {
	var mydata = '';
	mydata = mydata + el.value;
	if (mydata.length == 2) {
		mydata = mydata + '.';
		el.value = mydata;
	}
	if (mydata.length == 6) {
		mydata = mydata + '.';
		el.value = mydata;
	}
	if (mydata.length == 10) {
		mydata = mydata + '/';
		el.value = mydata;
	}
	if (mydata.length == 15) {
		mydata = mydata + '-';
		el.value = mydata;
	}
}

function formata_cnpj (numCICEl) {
	numCIC = String(ApenasNum(numCICEl.value));
	switch (numCIC.length) {
		case 15 :
			numCICEl.value = numCIC.substring(0,2) + "." + numCIC.substring(2,5) + "." + numCIC.substring(5,8) + "/" + numCIC.substring(8,12) + "-" + numCIC.substring(12,14);
			return;
		case 0:
			return;
		default : 
			alert("Tamanho incorreto do CNPJ. O CNPJ deve conter 15 dígitos");
			numCICEl.focus();
			return;
	}
}

//------------------------------------------------------------

function mascara_float(campo, teclapres) {
	var tecla = 0;
    if(navigator.appName.indexOf("Netscape")!= -1) {
   		tecla= teclapres.which;
   	}
    else {
		tecla = teclapres.keyCode;
	}
	/*
	if(tecla == 190 || tecla == 110) {
		//alert(tecla);
		campo.value = campo.value + ',';
		return false;
	}
	*/

	// Falta o caractere ',' (vírgula) do teclado numérico.
	if (  (tecla == 109 && campo.value == '') || (tecla == 189 && campo.value == '') // tecla '-' (só é possivel se for a primeira posicao)
			|| (tecla >= 48 && tecla <= 57) || (tecla >= 96 && tecla <= 105)
	 		|| tecla == 110 || tecla == 188 || tecla == 190
	  		|| tecla == 8 || tecla == 9 || tecla == 13
	    	|| tecla == 37 || tecla == 39
	    	|| tecla == 45 || tecla == 46 || tecla == 35 || tecla == 36
		    || (tecla == 67 || tecla == 86 && teclapres.ctrlKey)) {
		if(tecla == 188){// não pode inserir duas virgulas (TODO colocar a condicao da virgula do teclado numerico)
			return campo.value.indexOf(',') < 0;
		}
		return true;
	}

	return false; 
}

//------------------------------------------------------------

function mascara_inscricaoestadual(el) {
	var mydata = '';
	mydata = mydata + el.value;
	if (mydata.length == 13) {
		mydata = mydata + '-';
		el.value = mydata;
	}
}
//------------------------------------------------------------

function mascara_integer(campo, teclapres) {
	var tecla = 0;
    if(navigator.appName.indexOf("Netscape")!= -1) {
   		tecla= teclapres.which;
   	}
    else {
		tecla = teclapres.keyCode;
	}

	// Falta o caractere ',' (vírgula) do teclado numérico.
	if ( (tecla == 109 && campo.value == '') || (tecla == 189 && campo.value == '') // tecla '-' (só é possivel se for a primeira posicao)
		  ||(tecla >= 48 && tecla <= 57) || (tecla >= 96 && tecla <= 105)
	      || tecla == 8 || tecla == 9 || tecla == 13
	      || tecla == 37 || tecla == 39
	      || tecla == 45 || tecla == 46 || tecla == 35 || tecla == 36
	      || ((tecla == 67 || tecla == 86) && teclapres.ctrlKey)) {
		return true; 
	}

	return false; 
}
	
//------------------------------------------------------------

function formata_money(campo, teclapres) {
	if(navigator.appName.indexOf("Netscape")!= -1) {
		var tecla= teclapres.which;
	}
    else {
		var tecla = teclapres.keyCode;
	}

	tammax = 13;
	vr = campo.value;
	vr = vr.replace( "/", "" );
	vr = vr.replace( "/", "" );
	vr = vr.replace( ",", "" );
	vr = vr.replace( ".", "" );
	vr = vr.replace( ".", "" );
	vr = vr.replace( ".", "" );
	vr = vr.replace( ".", "" );
	tam = vr.length;

	if (tecla == 188 || tecla == 190 || tecla == 110 || tecla == 108) {
		return false;
	}

	if (tam < tammax && tecla != 8) {
		tam = vr.length + 1 ;
	}

	if (tecla == 8 ) {
		tam = tam - 1 ;
	}
	if (tecla == 8 || tecla >= 48 && tecla <= 57 || tecla >= 96 && tecla <= 105) {
		if (tam <= 2) { 
			campo.value = vr ;
		}
		if  (tam > 2) {
			campo.value = vr.substr(0, tam - 2) + ',' + vr.substr(tam - 2, tam) ;
		}
		if ( tam >= 6 && tam <= 8) {
			campo.value = vr.substr(0, tam - 5) + '.' + vr.substr(tam - 5, 3) + ',' + vr.substr(tam - 2, tam);
		}
		if ( tam >= 9 && tam <= 11) {
			campo.value = vr.substr(0, tam - 8) + '.' + vr.substr(tam - 8, 3) + '.' + vr.substr(tam - 5, 3) + ',' + vr.substr(tam - 2, tam) ;
		}
		if ( tam >= 12 && tam <= 14) {
			campo.value = vr.substr(0, tam - 11) + '.' + vr.substr(tam - 11, 3) + '.' + vr.substr(tam - 8, 3) + '.' + vr.substr(tam - 5, 3) + ',' + vr.substr(tam - 2, tam);
		}
		if ( (tam >= 15) && (tam <= 17) ) {
			campo.value = vr.substr(0, tam - 14) + '.' + vr.substr(tam - 14, 3) + '.' + vr.substr(tam - 11, 3) + '.' + vr.substr(tam - 8, 3) + '.' + vr.substr( tam - 5, 3) + ',' + vr.substr(tam - 2, tam) ;
		}
	}
	
	
	//verifica a tecla
	if ( (tecla == 109 && campo.value == '') || (tecla == 189 && campo.value == '') // tecla '-' (só é possivel se for a primeira posicao)
		  ||(tecla >= 48 && tecla <= 57) || (tecla >= 96 && tecla <= 105)
	      || tecla == 8 || tecla == 9 || tecla == 13
	      || tecla == 37 || tecla == 39
	      || tecla == 45 || tecla == 46 || tecla == 35 || tecla == 36
	      || ((tecla == 67 || tecla == 86) && teclapres.ctrlKey)) {
		return true; 
	}

	return false; 
}

//------------------------------------------------------------

function mascara_telefone(el) {
	var mydata = '';
	mydata = mydata + el.value;
	if (mydata.length == 1) {
		mydata = '(' + mydata;
		el.value = mydata;
	}
	if (mydata.length == 3) {
		mydata = mydata+') ';
		el.value = mydata;
	}
	if (mydata.length == 9) {
		mydata =  mydata + '-';
		el.value = mydata;
	}
	if(mydata.length == 15){
		mydata = mydata.replace('-', '');
		mydata = mydata.substring(0, 10) + '-' + mydata.substring(10);
		el.value = mydata;
	}
}

//------------------------------------------------------------

//selectManyAddOptionMessage = false;
function selectManyAddOption(from, optionName, value, properties){
	var name = from.name.substring(0, from.name.length - 6);
	from  = next.dom.toElement(from);
	
//	var option = new Option(optionName, value);
	var option = null;
	
	from.options.add(option = new Option(optionName, value));
	
	option.innerHTML = optionName;
	option.value = value;
	option.properties = properties;
//	try {
//		from.add(option);
//	} catch(e){
//		if(!selectManyAddOptionMessage){
//			alert("Seu navegador precisar ser atualizado para suportar essa página.");
//			selectManyAddOptionMessage = true;
//		}
//	}
}

function selectManyBoxCancelTo(from){
	var name = from.name.substring(0, from.name.length - 6);
	from  = next.dom.toElement(from)
	to    = next.dom.toElement(name + '_to___');
	left  = next.dom.toElement(name + '_left_');
	right = next.dom.toElement(name + '_right');
	
	selectManyBoxCancel(to, right, left);
}
function selectManyBoxCancelFrom(to){
	var name = to.name.substring(0, to.name.length - 6);
	to    = next.dom.toElement(to);
	from  = next.dom.toElement(name + '_from_');
	left  = next.dom.toElement(name + '_left_');
	right = next.dom.toElement(name + '_right');

	selectManyBoxCancel(from, left, right);
}

function selectManyBoxCancel(a, b, c){
	a.selectedIndex = -1;
	b.disabled = true;
	c.disabled = false;
}

function selectManyBoxAdd(btn){
	var name = btn.name.substring(0, btn.name.length - 6);
	from  = next.dom.toElement(name + '_from_');
	to    = next.dom.toElement(name + '_to___');
	left  = next.dom.toElement(name + '_left_');
	right = next.dom.toElement(name + '_right');	
	
	selectManyBoxMove(from, to, true);
	selectManyBoxCancel(from, left, right);
	
	selectManyBoxUpdateData(to);
}
function selectManyBoxRemove(btn){
	var name = btn.name.substring(0, btn.name.length - 6);
	from  = next.dom.toElement(name + '_from_');
	to    = next.dom.toElement(name + '_to___');
	left  = next.dom.toElement(name + '_left_');
	right = next.dom.toElement(name + '_right');	
	
	selectManyBoxMove(to, from, false);
	selectManyBoxCancel(to, right, left);
	
	selectManyBoxUpdateData(to);
}

function selectManyBoxMove(a, b, adding){
	if(a.selectedIndex < 0){
		return;
	}
	
	var el = a.options.item(a.selectedIndex);
	var newOp = new Option(el.innerHTML, el.value);
	newOp.properties = el.properties;
	
	b.options.add(newOp);
	if(a.options.remove){
		a.options.remove(a.selectedIndex);	
	} else {
		a.remove(el);
	}
	
	var name = a.name.substring(0, a.name.length - 6);
	from  = next.dom.toElement(name + '_from_');
	if(adding){
		var func = from.onAdd;
		if(func){
			func.call(a, newOp);
		}
	} else {
		var func = from.onRemove;
		if(func){
			func.call(a, newOp);
		}
	}

}

function selectManyBoxUpdateData(to){
	var name = to.name.substring(0, to.name.length - 6);
	value    = next.dom.toElement(name + '_value');
	value.innerHTML = '';

	for(var i = 0; i < to.options.length; i++){
		var input = next.dom.newInput('hidden', name, null, {'value': to.options[i].value, 'name': name+'['+i+']'});
		value.appendChild(input);
	}
}


