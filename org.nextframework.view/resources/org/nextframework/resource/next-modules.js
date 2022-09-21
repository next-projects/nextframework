NEXT_MODULES_DEFINED = true;

/**************************************************************************************  UTIL  **/
NextUtil = function(){};

NextUtil.defaultDiacriticsRemovalMap = [
   {'base':'A', 'letters':/[\u0041\u24B6\uFF21\u00C0\u00C1\u00C2\u1EA6\u1EA4\u1EAA\u1EA8\u00C3\u0100\u0102\u1EB0\u1EAE\u1EB4\u1EB2\u0226\u01E0\u00C4\u01DE\u1EA2\u00C5\u01FA\u01CD\u0200\u0202\u1EA0\u1EAC\u1EB6\u1E00\u0104\u023A\u2C6F]/g},
   {'base':'AA','letters':/[\uA732]/g},
   {'base':'AE','letters':/[\u00C6\u01FC\u01E2]/g},
   {'base':'AO','letters':/[\uA734]/g},
   {'base':'AU','letters':/[\uA736]/g},
   {'base':'AV','letters':/[\uA738\uA73A]/g},
   {'base':'AY','letters':/[\uA73C]/g},
   {'base':'B', 'letters':/[\u0042\u24B7\uFF22\u1E02\u1E04\u1E06\u0243\u0182\u0181]/g},
   {'base':'C', 'letters':/[\u0043\u24B8\uFF23\u0106\u0108\u010A\u010C\u00C7\u1E08\u0187\u023B\uA73E]/g},
   {'base':'D', 'letters':/[\u0044\u24B9\uFF24\u1E0A\u010E\u1E0C\u1E10\u1E12\u1E0E\u0110\u018B\u018A\u0189\uA779]/g},
   {'base':'DZ','letters':/[\u01F1\u01C4]/g},
   {'base':'Dz','letters':/[\u01F2\u01C5]/g},
   {'base':'E', 'letters':/[\u0045\u24BA\uFF25\u00C8\u00C9\u00CA\u1EC0\u1EBE\u1EC4\u1EC2\u1EBC\u0112\u1E14\u1E16\u0114\u0116\u00CB\u1EBA\u011A\u0204\u0206\u1EB8\u1EC6\u0228\u1E1C\u0118\u1E18\u1E1A\u0190\u018E]/g},
   {'base':'F', 'letters':/[\u0046\u24BB\uFF26\u1E1E\u0191\uA77B]/g},
   {'base':'G', 'letters':/[\u0047\u24BC\uFF27\u01F4\u011C\u1E20\u011E\u0120\u01E6\u0122\u01E4\u0193\uA7A0\uA77D\uA77E]/g},
   {'base':'H', 'letters':/[\u0048\u24BD\uFF28\u0124\u1E22\u1E26\u021E\u1E24\u1E28\u1E2A\u0126\u2C67\u2C75\uA78D]/g},
   {'base':'I', 'letters':/[\u0049\u24BE\uFF29\u00CC\u00CD\u00CE\u0128\u012A\u012C\u0130\u00CF\u1E2E\u1EC8\u01CF\u0208\u020A\u1ECA\u012E\u1E2C\u0197]/g},
   {'base':'J', 'letters':/[\u004A\u24BF\uFF2A\u0134\u0248]/g},
   {'base':'K', 'letters':/[\u004B\u24C0\uFF2B\u1E30\u01E8\u1E32\u0136\u1E34\u0198\u2C69\uA740\uA742\uA744\uA7A2]/g},
   {'base':'L', 'letters':/[\u004C\u24C1\uFF2C\u013F\u0139\u013D\u1E36\u1E38\u013B\u1E3C\u1E3A\u0141\u023D\u2C62\u2C60\uA748\uA746\uA780]/g},
   {'base':'LJ','letters':/[\u01C7]/g},
   {'base':'Lj','letters':/[\u01C8]/g},
   {'base':'M', 'letters':/[\u004D\u24C2\uFF2D\u1E3E\u1E40\u1E42\u2C6E\u019C]/g},
   {'base':'N', 'letters':/[\u004E\u24C3\uFF2E\u01F8\u0143\u00D1\u1E44\u0147\u1E46\u0145\u1E4A\u1E48\u0220\u019D\uA790\uA7A4]/g},
   {'base':'NJ','letters':/[\u01CA]/g},
   {'base':'Nj','letters':/[\u01CB]/g},
   {'base':'O', 'letters':/[\u004F\u24C4\uFF2F\u00D2\u00D3\u00D4\u1ED2\u1ED0\u1ED6\u1ED4\u00D5\u1E4C\u022C\u1E4E\u014C\u1E50\u1E52\u014E\u022E\u0230\u00D6\u022A\u1ECE\u0150\u01D1\u020C\u020E\u01A0\u1EDC\u1EDA\u1EE0\u1EDE\u1EE2\u1ECC\u1ED8\u01EA\u01EC\u00D8\u01FE\u0186\u019F\uA74A\uA74C]/g},
   {'base':'OI','letters':/[\u01A2]/g},
   {'base':'OO','letters':/[\uA74E]/g},
   {'base':'OU','letters':/[\u0222]/g},
   {'base':'P', 'letters':/[\u0050\u24C5\uFF30\u1E54\u1E56\u01A4\u2C63\uA750\uA752\uA754]/g},
   {'base':'Q', 'letters':/[\u0051\u24C6\uFF31\uA756\uA758\u024A]/g},
   {'base':'R', 'letters':/[\u0052\u24C7\uFF32\u0154\u1E58\u0158\u0210\u0212\u1E5A\u1E5C\u0156\u1E5E\u024C\u2C64\uA75A\uA7A6\uA782]/g},
   {'base':'S', 'letters':/[\u0053\u24C8\uFF33\u1E9E\u015A\u1E64\u015C\u1E60\u0160\u1E66\u1E62\u1E68\u0218\u015E\u2C7E\uA7A8\uA784]/g},
   {'base':'T', 'letters':/[\u0054\u24C9\uFF34\u1E6A\u0164\u1E6C\u021A\u0162\u1E70\u1E6E\u0166\u01AC\u01AE\u023E\uA786]/g},
   {'base':'TZ','letters':/[\uA728]/g},
   {'base':'U', 'letters':/[\u0055\u24CA\uFF35\u00D9\u00DA\u00DB\u0168\u1E78\u016A\u1E7A\u016C\u00DC\u01DB\u01D7\u01D5\u01D9\u1EE6\u016E\u0170\u01D3\u0214\u0216\u01AF\u1EEA\u1EE8\u1EEE\u1EEC\u1EF0\u1EE4\u1E72\u0172\u1E76\u1E74\u0244]/g},
   {'base':'V', 'letters':/[\u0056\u24CB\uFF36\u1E7C\u1E7E\u01B2\uA75E\u0245]/g},
   {'base':'VY','letters':/[\uA760]/g},
   {'base':'W', 'letters':/[\u0057\u24CC\uFF37\u1E80\u1E82\u0174\u1E86\u1E84\u1E88\u2C72]/g},
   {'base':'X', 'letters':/[\u0058\u24CD\uFF38\u1E8A\u1E8C]/g},
   {'base':'Y', 'letters':/[\u0059\u24CE\uFF39\u1EF2\u00DD\u0176\u1EF8\u0232\u1E8E\u0178\u1EF6\u1EF4\u01B3\u024E\u1EFE]/g},
   {'base':'Z', 'letters':/[\u005A\u24CF\uFF3A\u0179\u1E90\u017B\u017D\u1E92\u1E94\u01B5\u0224\u2C7F\u2C6B\uA762]/g},
   {'base':'a', 'letters':/[\u0061\u24D0\uFF41\u1E9A\u00E0\u00E1\u00E2\u1EA7\u1EA5\u1EAB\u1EA9\u00E3\u0101\u0103\u1EB1\u1EAF\u1EB5\u1EB3\u0227\u01E1\u00E4\u01DF\u1EA3\u00E5\u01FB\u01CE\u0201\u0203\u1EA1\u1EAD\u1EB7\u1E01\u0105\u2C65\u0250]/g},
   {'base':'aa','letters':/[\uA733]/g},
   {'base':'ae','letters':/[\u00E6\u01FD\u01E3]/g},
   {'base':'ao','letters':/[\uA735]/g},
   {'base':'au','letters':/[\uA737]/g},
   {'base':'av','letters':/[\uA739\uA73B]/g},
   {'base':'ay','letters':/[\uA73D]/g},
   {'base':'b', 'letters':/[\u0062\u24D1\uFF42\u1E03\u1E05\u1E07\u0180\u0183\u0253]/g},
   {'base':'c', 'letters':/[\u0063\u24D2\uFF43\u0107\u0109\u010B\u010D\u00E7\u1E09\u0188\u023C\uA73F\u2184]/g},
   {'base':'d', 'letters':/[\u0064\u24D3\uFF44\u1E0B\u010F\u1E0D\u1E11\u1E13\u1E0F\u0111\u018C\u0256\u0257\uA77A]/g},
   {'base':'dz','letters':/[\u01F3\u01C6]/g},
   {'base':'e', 'letters':/[\u0065\u24D4\uFF45\u00E8\u00E9\u00EA\u1EC1\u1EBF\u1EC5\u1EC3\u1EBD\u0113\u1E15\u1E17\u0115\u0117\u00EB\u1EBB\u011B\u0205\u0207\u1EB9\u1EC7\u0229\u1E1D\u0119\u1E19\u1E1B\u0247\u025B\u01DD]/g},
   {'base':'f', 'letters':/[\u0066\u24D5\uFF46\u1E1F\u0192\uA77C]/g},
   {'base':'g', 'letters':/[\u0067\u24D6\uFF47\u01F5\u011D\u1E21\u011F\u0121\u01E7\u0123\u01E5\u0260\uA7A1\u1D79\uA77F]/g},
   {'base':'h', 'letters':/[\u0068\u24D7\uFF48\u0125\u1E23\u1E27\u021F\u1E25\u1E29\u1E2B\u1E96\u0127\u2C68\u2C76\u0265]/g},
   {'base':'hv','letters':/[\u0195]/g},
   {'base':'i', 'letters':/[\u0069\u24D8\uFF49\u00EC\u00ED\u00EE\u0129\u012B\u012D\u00EF\u1E2F\u1EC9\u01D0\u0209\u020B\u1ECB\u012F\u1E2D\u0268\u0131]/g},
   {'base':'j', 'letters':/[\u006A\u24D9\uFF4A\u0135\u01F0\u0249]/g},
   {'base':'k', 'letters':/[\u006B\u24DA\uFF4B\u1E31\u01E9\u1E33\u0137\u1E35\u0199\u2C6A\uA741\uA743\uA745\uA7A3]/g},
   {'base':'l', 'letters':/[\u006C\u24DB\uFF4C\u0140\u013A\u013E\u1E37\u1E39\u013C\u1E3D\u1E3B\u017F\u0142\u019A\u026B\u2C61\uA749\uA781\uA747]/g},
   {'base':'lj','letters':/[\u01C9]/g},
   {'base':'m', 'letters':/[\u006D\u24DC\uFF4D\u1E3F\u1E41\u1E43\u0271\u026F]/g},
   {'base':'n', 'letters':/[\u006E\u24DD\uFF4E\u01F9\u0144\u00F1\u1E45\u0148\u1E47\u0146\u1E4B\u1E49\u019E\u0272\u0149\uA791\uA7A5]/g},
   {'base':'nj','letters':/[\u01CC]/g},
   {'base':'o', 'letters':/[\u006F\u24DE\uFF4F\u00F2\u00F3\u00F4\u1ED3\u1ED1\u1ED7\u1ED5\u00F5\u1E4D\u022D\u1E4F\u014D\u1E51\u1E53\u014F\u022F\u0231\u00F6\u022B\u1ECF\u0151\u01D2\u020D\u020F\u01A1\u1EDD\u1EDB\u1EE1\u1EDF\u1EE3\u1ECD\u1ED9\u01EB\u01ED\u00F8\u01FF\u0254\uA74B\uA74D\u0275]/g},
   {'base':'oi','letters':/[\u01A3]/g},
   {'base':'ou','letters':/[\u0223]/g},
   {'base':'oo','letters':/[\uA74F]/g},
   {'base':'p','letters':/[\u0070\u24DF\uFF50\u1E55\u1E57\u01A5\u1D7D\uA751\uA753\uA755]/g},
   {'base':'q','letters':/[\u0071\u24E0\uFF51\u024B\uA757\uA759]/g},
   {'base':'r','letters':/[\u0072\u24E1\uFF52\u0155\u1E59\u0159\u0211\u0213\u1E5B\u1E5D\u0157\u1E5F\u024D\u027D\uA75B\uA7A7\uA783]/g},
   {'base':'s','letters':/[\u0073\u24E2\uFF53\u00DF\u015B\u1E65\u015D\u1E61\u0161\u1E67\u1E63\u1E69\u0219\u015F\u023F\uA7A9\uA785\u1E9B]/g},
   {'base':'t','letters':/[\u0074\u24E3\uFF54\u1E6B\u1E97\u0165\u1E6D\u021B\u0163\u1E71\u1E6F\u0167\u01AD\u0288\u2C66\uA787]/g},
   {'base':'tz','letters':/[\uA729]/g},
   {'base':'u','letters':/[\u0075\u24E4\uFF55\u00F9\u00FA\u00FB\u0169\u1E79\u016B\u1E7B\u016D\u00FC\u01DC\u01D8\u01D6\u01DA\u1EE7\u016F\u0171\u01D4\u0215\u0217\u01B0\u1EEB\u1EE9\u1EEF\u1EED\u1EF1\u1EE5\u1E73\u0173\u1E77\u1E75\u0289]/g},
   {'base':'v','letters':/[\u0076\u24E5\uFF56\u1E7D\u1E7F\u028B\uA75F\u028C]/g},
   {'base':'vy','letters':/[\uA761]/g},
   {'base':'w','letters':/[\u0077\u24E6\uFF57\u1E81\u1E83\u0175\u1E87\u1E85\u1E98\u1E89\u2C73]/g},
   {'base':'x','letters':/[\u0078\u24E7\uFF58\u1E8B\u1E8D]/g},
   {'base':'y','letters':/[\u0079\u24E8\uFF59\u1EF3\u00FD\u0177\u1EF9\u0233\u1E8F\u00FF\u1EF7\u1E99\u1EF5\u01B4\u024F\u1EFF]/g},
   {'base':'z','letters':/[\u007A\u24E9\uFF5A\u017A\u1E91\u017C\u017E\u1E93\u1E95\u01B6\u0225\u0240\u2C6C\uA763]/g}
   ];
 
NextUtil.prototype.removeAccents = function(str) {
	var changes = NextUtil.defaultDiacriticsRemovalMap;
	for(var i=0; i<changes.length; i++) {
		str = str.replace(changes[i].letters, changes[i].base);
	}
	return str;
}

NextUtil.prototype.isDefined = function(el){
	return typeof(el) != "undefined" && el != null;
};
NextUtil.prototype.evalScripts = function(contents){
	var scripts = '';
	var text = contents.replace(/<script[^>]*>([\s\S]*?)<\/script>/gi, function(all, code){
		scripts += code + ';\n';
		return '';
	});
	eval(scripts);
}
NextUtil.prototype.escapeSingleQuotes = function(string){
	var result = '';
	for(var i = 0; i < string.length; i++){
		var charAt =string.charAt(i);
		if(charAt == '\\'){
			result += '\\\\';
		} else if(charAt == '\''){
			result += '\\\'';
		} else {
			result += charAt;
		}
	}
	return result;
}
NextUtil.prototype.removeSingleQuotes = function(string){
	var result = '';
	for(var i = 0; i < string.length; i++){
		var charAt =string.charAt(i);
		if(charAt != '\''){
			result += charAt;
		}
	}
	return result;
}

/**
 * Cria uma função de parametros default.
 * 
 * Exemplo:
 * 
 * var def = next.util.defaultParam(params);
 * 
 * def("title", "Sem Titulo"); // se o parametro title nao foi definido em params, ele será definido como Sem Titulo
 * 
 * @param params
 * @return
 */
NextUtil.prototype.defaultParam = function(params, object){
	var df = function(pname, def) { 
		if (typeof params[pname] == "undefined") { params[pname] = def; }
		if(object) object[pname] = params[pname];
	};
	return df;
}
NextUtil.prototype.typeOf = function(item){	
	if (item == null) return 'null';
	
	if (item.nodeName){
		if (item.nodeType == 1) return 'element';
		if (item.nodeType == 3) return (/\S/).test(item.nodeValue) ? 'textnode' : 'whitespace';
	} else if (typeof item.length == 'number'){
		if (item.callee) return 'arguments';
		if (item.push) return 'array';
	}
	
	return typeof item;
}

NextUtil.prototype.isArray = function(item){
	return this.typeOf(item) == 'array';
}
NextUtil.prototype.isFunction = function(item){
	return this.typeOf(item) == 'function';
}
NextUtil.prototype.isObject = function(item){
	return this.typeOf(item) == 'object';
}

/**
 * Iterage sobre um array ou mapa de objetos
 * @param obj Array ou mapa a ser iteragido
 * @param func Função de callback que será chamada para cada item
 * @return
 */
NextUtil.prototype.each = function(obj, func){
	if(this.typeOf(obj) == 'array'){
		for(var i = 0; i < obj.length; i++){
			func.call(obj, obj[i], i);
		}
	} else {
		for(var x in obj){
			func.call(obj, x, obj[x]);
		}
	}
}
/**
 * Remove o item do array (o objeto passado tem que ser a mesma referencia do objeto no array)
 * @param arr
 * @param item
 * @return
 */
NextUtil.prototype.removeItem = function(arr, item){
	var i = 0;
	for(; i < arr.length; i++){
		if(arr[i] === item){
			this.removeIndex(arr, i);
			return;
		}
	}
}

NextUtil.prototype.indexOf = function(arr, item){
	var i = 0;
	for(; i < arr.length; i++){
		if(arr[i] === item || (item != null && item.equals && item.equals(arr[i]))){
			return i;
		}
	}
	return -1;
}

/**
 * Remove um item em um indice de um array
 * @param arr
 * @param index
 * @return
 */
NextUtil.prototype.removeIndex = function(arr, index){
	arr.splice(index, 1);
}
/**
 * Concatena os elementos do array utilizando o token como separador
 * @param arr
 * @param token
 * @return
 */
NextUtil.prototype.join = function(arr, token){
	var result = '';
	for(var i = 0; i < arr.length; i++){
		result += arr[i];
		if(i + 1 < arr.length){
			result += token;
		}
	}
	return result;
}
/**
 * Transforma a string passada como parametro em uma outra utilizando CamelCase no lugar do separador.<BR>
 * Exemplo: next.util.fromSeparatorToCamel('padding-left', '-') == 'paddingLeft' 
 * @param name
 * @param separator
 * @return
 */
NextUtil.prototype.fromSeparatorToCamel = function(name, separator){
	var sep;
	while((sep = name.indexOf(separator)) >= 0){
		name = name.substring(0, sep) + name.charAt(sep+1).toUpperCase() + name.substring(sep+2);
	}
	return name;
}

NextUtil.prototype.getYearFromDate = function(date){
	if(date.getFullYear){
		return date.getFullYear();
	} else {
		return date.getYear() + 1900; 
	}
}

/**
 * Transforma o elemento passado como parâmetro em um array.
 * @param el
 * @return
 */
NextUtil.prototype.toArray = function(el){
	var r = new Array();
	for(var i = 0; i < el.length; i++){
		r.push(el[i]);
	}
	return r;
}

/**************************************************************************************  GLOBAL MAP  **/

NextGlobalMap = function(){
	this.map = {};
};

NextGlobalMap.prototype.put = function(key, value){
	if (key != null) {
		this.map[key] = value;
	}
}

NextGlobalMap.prototype.get = function(key, defaultValue){
	if (key != null && key != 'null') {
		var value = this.map[key];
		if (value != null) {
			return value;
		}
	}
	return defaultValue;
}

new NextGlobalMap();

/**************************************************************************************  NUMBERS  **/

NextNumbers = function(){};

/**
 * Formata um número
 * @param n Número original
 * @param c Casas decimais
 * @param d Dígito separador de decimal
 * @param t Dígito separador de milhar
 */
NextNumbers.prototype.formatDecimal = function(n, c, d, t){
	var c = isNaN(c = Math.abs(c)) ? 2 : c, d = d == undefined ? "," : d, t = t == undefined ? "." : t, s = n < 0 ? "-" : "", i = parseInt(n = Math.abs(+n || 0).toFixed(c)) + "", j = (j = i.length) > 3 ? j % 3 : 0;
	return s + (j ? i.substr(0, j) + t : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + t) + (c ? d + Math.abs(n - i).toFixed(c).slice(2) : "");
}

NextNumbers.prototype.formataDecimal = function(n, c, d, t){
	return formatDecimal(n, c, d, t);
}

/**************************************************************************************  LOG  **/

NextLog = function(){};
NextLog.enabled = false;

/**
 * Exibe um warning no log do next
 * @param text Texto do log
 * @param family Módulo que é feito o log
 */
NextLog.prototype.warn = function(text, family){
	var log = next.dom.newElement('DIV', {style:{backgroundColor: '#FFFEF0'}});
	log.innerHTML = text;
	next.dom.insertFirstChild(this.getLogDiv(), log);
}

/**
 * Exibe um info no log do next
 * @param text Texto do log
 * @param family Módulo que é feito o log
 */
NextLog.prototype.info = function(text, family){
	var log = next.dom.newElement('DIV', {});
	log.innerHTML = text;
	next.dom.insertFirstChild(this.getLogDiv(), log);
}

NextLog.prototype.getLogDiv = function(){
	var logDiv = document.getElementById('NEXT_LOG_DIV');
	if(!next.util.isDefined(logDiv)){
		logDiv = next.dom.newElement('DIV', {
			id: 'NEXT_LOG_DIV',
			style : {
				width: '200px',
				height: '100px',
				position: 'absolute',
				bottom: '0px',
				right: '0px',
				backgroundColor: '#FFEEDD',
				border: '1px solid black',
				overflow: 'auto'
			}
		});
		document.body.appendChild(logDiv);
	}
	return logDiv;
}

new NextLog();

/**************************************************************************************  EVENTS  **/

NextEvents = function(){
	this.loaded = false;
};
NextEvents.prototype.toString = function (){
	return 'Next Events Object';
};

/**
 * Adiciona uma função para ser executada quando o browser terminar a carga da página.
 * <BR>
 * Exemplo: next.events.onLoad(function(){alert('página carregada');});
 * @param func
 * @return
 */
NextEvents.prototype.onLoad = function(func, forceExecutionWhenLoaded){
	if(this.loaded && forceExecutionWhenLoaded){
		func();
	} else {
		next.events.attachEvent(window, 'load', func);
	}
};

NextEvents.prototype.dispatchEvent = function(el, eventType){
	try {
		// Create the event.
		var event = document.createEvent('Event');
		
		// Define that the event name is 'build'.
		event.initEvent(eventType, true, true);
		
		el.dispatchEvent(event);	
		console.debug('dispatching event '+event+ ' to '+el.name);
	} catch(e) {
		try {
			console.log(e);
		} catch(x){}
	}
}

/**
 * Anexa um evento a um elemento. (Na função informada, o this será o elemento passado como parâmetro)<BR>
 * Exemplo: next.events.attachEvent(div, 'click', function(){alert('Evento click executado!');});
 * @param el
 * @param event
 * @param func
 * @return
 */
NextEvents.prototype.attachEvent = function(el, event, func){
	if(!next.util.isDefined(el)){
		next.log.warn("events", "Tentando atribuir um evento "+event+" a um objeto nulo.");
		return null;
	}
	if(el.addEventListener){
		el.addEventListener(event, func, false);
		return func;
	} else if(el.attachEvent){
		//criar outra função para redirecionar o this e fazer com que o IE funcione igual a outros browsers
		//o this na função irá referenciar o elemento
		var newFunc = function(e){
			var ret = func.call(el, e);
			if (ret === false) {
				window.event.returnValue = false;
				window.event.cancelBubble = true;
			}			
		};
		el.attachEvent('on'+event, newFunc);
		return newFunc;
	}
}

NextEvents.prototype.detachEvent = function(el, event, func){
	if(!next.util.isDefined(el)){
		next.log.warn("events", "Tentando remover um evento "+event+" a um objeto nulo.");
		return null;
	}
	if(el.addEventListener){
		el.removeEventListener(event, func, false);
	} else if(el.attachEvent){
		el.detachEvent('on'+event, func);
	}
}

NextEvents.prototype.getKey = function(event){
	if(navigator.appName.indexOf("Netscape")!= -1) {
		return event.which;
	}
	else {
		return event.keyCode;
	}
}

NextEvents.prototype.getChar = function(event){
	if(navigator.appName.indexOf("Netscape")!= -1) {
		return String.fromCharCode(event.which);
	}
	else {
		return String.fromCharCode(event.keyCode);
	}
}

/**
 * Cancela a propagação de eventos<BR>
 */
NextEvents.prototype.cancelEvent = function(event){
	var evt = event? event: window.event;
	if (evt.preventDefault) evt.preventDefault();
	if (evt.stopPropagation) evt.stopPropagation();
	if (evt.cancelBubble != null) evt.cancelBubble = true;
}

new NextEvents();

/**************************************************************************************  HTTP  **/

NextHttp = function(){};

/**
 * Configura um cookie na página
 * @param cookieName Nome do cookie
 * @param value Valor do cookie
 * @param exdays Número de dias para expirar o cookie, passar null caso não expira
 * @return
 */
NextHttp.prototype.setCookie = function(cookieName, value, exdays) {
	var exdate = new Date();
	exdate.setDate(exdate.getDate() + exdays);
	var cookieValue = escape(value)
			+ ((exdays == null) ? "" : "; expires=" + exdate.toUTCString());
	document.cookie = cookieName + "=" + cookieValue;
}

/**
 * Retorna o valor de um determinado cookie
 * @param cookieName
 * @return
 */
NextHttp.prototype.getCookie = function(cookieName) {
	var i, x, y, arrayCookies = document.cookie.split(";");
	for (i = 0; i < arrayCookies.length; i++) {
		x = arrayCookies[i].substr(0, arrayCookies[i].indexOf("="));
		y = arrayCookies[i].substr(arrayCookies[i].indexOf("=") + 1);
		x = x.replace(/^\s+|\s+$/g, "");
		if (x == cookieName) {
			return unescape(y);
		}
	}
}

NextHttp.prototype.getServletPath = function(){
	return window.location.pathname;
}
/**
 * Retorna o applicationContext, equivalente ao request.getContextPath() do JEE<BR>
 * Lê a URL atual do browser para conseguir o nome da aplicação.
 * @return
 */
NextHttp.prototype.getApplicationContext = function(){
	var appCtx = window.location.pathname;
	if(appCtx.indexOf('/') == 0){
		appCtx = appCtx.substring(1, appCtx.length);
	}
	var index = appCtx.indexOf('/');
	if(index > 0){
		appCtx = appCtx.substring(0, index);
	}
	return '/'+appCtx;
}

new NextHttp();

/**************************************************************************************  DOM  **/

NextDom = function(){};
NextDom.sequenceGenerator = 1;
NextDom.prototype.id = function(id){
	return document.getElementById(id);
}

/**
 * Gera um id único
 * @return
 */
NextDom.prototype.generateUniqueId = function(){
	return "__uid"+ (NextDom.sequenceGenerator++);
}

/**
 * Cria um elemento DOM com os atributos. Podem ser informados eventos e estilo.<BR>
 * Exemplo:<BR> 
 * <PRE>
 * next.dom.newElement('DIV', {
 * 		id:'meudiv', 
 * 		style:{
 * 			color: 'red'
 * 		}, 
 * 		events:{
 * 			click: function(){alert('click')}
 * 		}
 * })
 * </PRE>
 * @param tag
 * @param options
 * @return
 */
NextDom.prototype.newElement = function(tag, options){
	if(tag == "image"){
		// o IE aceita a tag image, mas outros browsers não.. para forçar a compatibilidade alertar
		alert("Use tag 'img' ao invés de 'image' no método newElement");
	}

	var element = document.createElement(tag);
	this.attachAttributes(element, options);
	return element;
}

NextDom.prototype.newSpanElement = function(text, options){
	var div = this.newElement("span", options);
	if(text){
		div.innerHTML = text;
	}
	return div;
}

NextDom.prototype.newDivElement = function(text, options){
	var div = this.newElement("div", options);
	if(text){
		div.innerHTML = text;
	}
	return div;
}

/**
 * Cria um novo input
 * @param type
 * @param name
 * @param label
 * @param options
 * @return
 */
NextDom.prototype.newInput = function(type, name, label, options){
	if(!next.util.isDefined(options)){
		options = {};
	}
	var element = document.createElement("input");
	if(type){
		options.type = type;
	} else {
		options.type = "text";
	}
	if(name){
		options.name = name;
	}
	this.attachAttributes(element, options);
	
	if(next.util.isDefined(label)){
		var labelTag = document.createElement("label");
		labelTag.setAttribute("for", options.id);
		labelTag.innerHTML = label;
		this.attachAttributes(labelTag, options.labelOptions);
		
		var container = document.createElement("span");
		this.attachAttributes(container, options.containerOptions);
		if(type == "checkbox"){
			container.appendChild(element);
			container.appendChild(labelTag);
		} else {
			container.appendChild(labelTag);
			container.appendChild(element);
		}
		
		element = container;
	}
	return element;
}

/**
 * Anexa um conjunto de atributos ao elemento.<BR>
 * Exemplo:
 * <PRE> 
 * next.dom.attachAttributes(td, {
 * 		colspan: 2, 
 * 		style: {color: '#EFEFEF'},
 * 		events: {mouseover: function(){alert('mouse over')} }
 * });
 * </PRE>
 * @param element
 * @param options
 * @return
 */
NextDom.prototype.attachAttributes = function(element, options){
	if(!next.util.isDefined(options)){
		return;
	}
	if(!next.util.isDefined(options.id)){
		options.id = this.generateUniqueId();
	}
	if(next.util.isDefined(options.className)){
		element.className = options.className;
		delete options['className'];
	}
	element.setAttribute("id", options.id);
	var events = options.events;
	if(!next.util.isDefined(options.events)){
		events = {};
	}
	for(ev in events){
		next.events.attachEvent(element, ev, events[ev]);
	}
	
	var style = options.style;
	if(!next.util.isDefined(options.style)){
		style = {};
	}
	for(st in style){
		var value = style[st];
		st = next.util.fromSeparatorToCamel(st, '-');
		element.style[st] = value;
	}
	for(att in options){
		if(att == "events"){continue;}
		if(att == "style"){continue;}
		if(att == "labelOptions"){continue;}
		if(att == "containerOptions"){continue;}
		element.setAttribute(att, options[att]);
		element[att] = options[att];
	}
}

NextDom.prototype.removeSelectValue = function(el, opValue){
	el = next.dom.toElement(el);
	var ops = el.options;
	for(var i = ops.length-1; i >= 0 ; i--){
		if(ops[i].value == opValue){
			el.remove(i);
		}
	}
}

NextDom.prototype.setSelectedValues = function(el, values){
	el = next.dom.toElement(el);
	var ops = el.options;
	for(var i = 0; i < ops.length; i++){
		for(var j = 0; j < values.length;j++){
			if(ops[i].value == values[j]){
				ops[i].selected = true;
			}
		}
	}
}

NextDom.prototype.setSelectedValueWithId = function(el, id, dispatchEvent){
	el = next.dom.toElement(el);
	var ops = el.options;
	var selectedIndex = el.selectedIndex;
	for(var i = 0; i < ops.length; i++){
		var opv = ops[i].value;
		opv = opv.substring(opv.indexOf('['), opv.length);
		if(opv.indexOf(id) > 0){//make better checking
			ops[i].selected = true;
			if(ops[i].index != selectedIndex){
				if(dispatchEvent){
					next.events.dispatchEvent(el, "change");
				}
				return true;
			}
		}
	}
	return false;
}

NextDom.prototype.setSelectedValue = function(el, value, dispatchEvent){
	el = next.dom.toElement(el);
	var ops = el.options;
	var selectedIndex = el.selectedIndex;
	for(var i = 0; i < ops.length; i++){
		if(ops[i].value == value){
			ops[i].selected = true;
			if(ops[i].index != selectedIndex){
				if(dispatchEvent){
					next.events.dispatchEvent(el, "change");
				}
				return true;
		}
	}
}
	return false;
}

NextDom.prototype.getInputValue = function(el){
	el = next.dom.toElement(el);
	if(el.tagName.toLowerCase() == 'select'){
		return this.getSelectedValue(el);
	} else {
		if( (el.type == 'radio' || el.type == 'checkbox') && !el.checked ){
			return null;
		} else {
			return el.value;
		}
	}
}

NextDom.prototype.getSelectedValues = function(el){
	el = next.dom.toElement(el);
	return next.dom.getSelectedValue(el);
}

/**
 * Retorna o valor selecionado de um combobox.<BR>
 * Exemplo: next.dom.getSelectedValue(combo);
 * @param el
 * @return
 */
NextDom.prototype.getSelectedValue = function(el){
	el = next.dom.toElement(el);
	if(el.multiple){
		var result = new Array();
		var ops = el.options;
		for(var i = 0; i < ops.length; i++){
			if(ops[i].selected){
				result.push(ops[i].value);
			}
		}
		return result;
	}
	if(el.selectedIndex < 0){
		return null;
	}
	return el.options[el.selectedIndex].value;
}

/**
 * Retorna o TEXTO do valor selecionado de um combobox.<BR>
 * Exemplo: next.dom.getSelectedText(combo);
 * @param el
 * @return
 */
NextDom.prototype.getSelectedText = function(el){
	el = next.dom.toElement(el);
	if(el.multiple){
		var result = new Array();
		var ops = el.options;
		for(var i = 0; i < ops.length; i++){
			if(ops[i].selected){
				result.push(ops[i].innerHTML);
			}
		}
		return result;
	}
	if(el.selectedIndex < 0){
		return null;
	}
	return el.options[el.selectedIndex].innerHTML;
}

NextDom.prototype.newCheckbox = function(name, label, options){
	return this.newInput("checkbox", name, label, options);
}

/**
 * Se o elemento passado como parâmetro for um elemento, retorna o próprio elemento.<BR>
 * Se for uma string, será pesquisada na página um elemento com ID ou NAME igual a string.<BR>
 * Retorna null se não encontrar.
 * @param el
 * @return
 */
NextDom.prototype.toElement = function(el){
	var type = next.util.typeOf(el);
	if(type == 'string'){
		var byId = document.getElementById(el);
		if(next.util.isDefined(byId)){
			el = byId;
		} else {
			var byName = document.getElementsByName(el);
			if(byName.length > 0){
				el = byName[0];
			}
		}
	}
	if(next.util.typeOf(el) == 'element'){
		return el;
	}
	return null;
}

/**
 * Insere um elemento como o primeiro filho de um elemento.<BR>
 * next.dom.inserFirstChild(parent, child);
 * @param parent
 * @param child
 * @return
 */
NextDom.prototype.insertFirstChild = function(parent, child){
	if(parent.hasChildNodes()){
		var firstChild = parent.childNodes[0]
		parent.insertBefore(child, firstChild);
	} else {
		parent.appendChild(child);
	}
}

NextDom.prototype.getParentTag = function(el, parentTagName){
	el = el.parentNode;
	while(next.util.isDefined(el) && el.tagName.toLowerCase() != parentTagName.toLowerCase()){
		el = el.parentNode;
	}
	return el;
}

NextDom.prototype.getParentTagById = function(el, parentTagId){
	el = el.parentNode;
	while(next.util.isDefined(el) && el.id != parentTagId) {
		el = el.parentNode;
	}
	return el;
}

NextDom.prototype.getParentTagByClass = function(el, className){
	el = el.parentNode;
	while(next.util.isDefined(el) && !next.style.hasClass(el, className)) {
		el = el.parentNode;
	}
	return el;
}

/**
 * Cria um wrapper para o elemento. Se o elemento já possui um wrapper, retorna o wrapper já criado.
 * @param el
 * @return
 */
NextDom.prototype.wrapper = function(el){
	var parent = el.parentNode;
	if(!next.util.isDefined(parent.getAttribute('data-wrapper'))){
		var wrapper = next.dom.newElement('DIV', {
								'data-wrapper': true, 
								style: {padding: '0px', margin: '0px', border: '0px solid'}
						});
		parent.insertBefore(wrapper, el);
		parent.removeChild(el);
		wrapper.appendChild(el);
		return wrapper;
	}
	return parent;
}

NextDom.prototype.getInnerElementById = function(parent, innerId, innerTagName){
	if(parent && parent.childNodes){
		for(var i = 0; i < parent.childNodes.length; i++){
			if(innerTagName){
				if(parent.childNodes[i].tagName != innerTagName){
					continue;
				}
			}
			if(parent.childNodes[i].id == innerId){
				return parent.childNodes[i];
			}
		}
		for(var i = 0; i < parent.childNodes.length; i++){
			var child=next.dom.getInnerElementById(parent.childNodes[i], innerId, innerTagName);
			if(child){
				return child;
			}
		}
	}
	return null;
}

NextDom.prototype.getInnerElementByClass = function(parent, className, innerTagName){
	if(parent && parent.childNodes){
		for(var i = 0; i < parent.childNodes.length; i++){
			if(innerTagName){
				if(parent.childNodes[i].tagName != innerTagName){
					continue;
				}
			}
			if(next.style.hasClass(parent.childNodes[i], className)){
				return parent.childNodes[i];
			}
		}
		for(var i = 0; i < parent.childNodes.length; i++){
			var child=next.dom.getInnerElementByClass(parent.childNodes[i], className, innerTagName);
			if(child){
				return child;
			}
		}
	}
	return null;
}

NextDom.prototype.getInnerElementByName = function(parent, innerName, innerTagName){
	if(parent && parent.childNodes){
		for(var i = 0; i < parent.childNodes.length; i++){
			if(innerTagName){
				if(parent.childNodes[i].tagName != innerTagName){
					continue;
				}
			}
			if(parent.childNodes[i].name == innerName){
				return parent.childNodes[i];
			}
		}
		for(var i = 0; i < parent.childNodes.length; i++){
			var child=next.dom.getInnerElementByName(parent.childNodes[i], innerName, innerTagName);
			if(child){
				return child;
			}
		}
	}
	return null;
}

NextDom.prototype.getForm = function(name, elements){
	return new NextDomForm(name, elements);
}

NextDom.zIndexCount = 10000;

/**
 * Creates and return a new div popup, the screen will be blocked.
 * Call the div's close() method on finish.
 */
NextDom.prototype.getNewPopupDiv = function (){
	next.effects.blockScreen();
	var popupdiv = next.dom.newElement('DIV',
			{
				className: next.globalMap.get('PopupDiv.box', 'popup_box'),
				style : {
					zIndex: NextDom.zIndexCount++,
					position: 'absolute'
				}
			}
		);
	next.dom.insertFirstChild(document.body, popupdiv);
	popupdiv.close = function(){
		popupdiv.parentNode.removeChild(popupdiv);
		next.effects.unblockScreen();
	}
	return popupdiv;
}

/**
 * Classe que representa um formulário.
 * @param name
 * @param elements
 */
NextDomForm = function(name, elements){
	if(next.util.isDefined(name)){
		this.formElement = next.dom.toElement(name);
		if(this.formElement == null){
			if(document.forms.length > 0){
				throw ("Form '"+name+"' not found. Should it be '"+document.forms[0].name+"'?");
			} else {
				throw ("Form '"+name+"' not found. The page does not have forms");
			}
		}
	} else {
		if(document.forms.length == 0){
			throw ("The page does not have forms");
		}
		this.formElement = document.forms[0];
	}
	this.elements = elements;
}

NextDomForm.prototype.getElements = function(){
	if(next.util.isDefined(this.elements)){
		return this.elements;
	}
	this.elements = this.formElement.elements;
	
	return this.elements;
}

/**
 * Retorna um novo formulário com apenas os elementos determinados.
 * <BR>
 * Exemplo: new next.dom.Form('meuForm').subForm('detalhe'); //retornará um form com as propriedades iniciadas com 'detalhe'
 * @param property Inicio do padrão de nome da propriedade
 * @return
 */
NextDomForm.prototype.subForm = function(){
	var a = new Array();
	if(next.util.isArray(arguments[0])){
		a = arguments[0];
	} else {
		for(var i = 0; i < arguments.length; i++){
			a.push(arguments[i]);
		}
	}
	
	var elements = new Array();
	var formElements = this.getElements();
	for(var i = 0; i < formElements.length; i++){
		for(var j = 0; j < a.length; j++){
			if(formElements[i].name.indexOf(a[j]) == 0){
				elements.push(formElements[i]);
			}
		}
	}
	return new NextDomForm(this.formElement, elements);
}

/**
 * Remove desse objeto Form o campo em questao
 * 
 */
NextDomForm.prototype.removeField = function(field){
	var newList = new Array();
	var oldList = this.getElements();
	for(var i = 0; i < oldList.length; i++){
		if(oldList[i].name != field){
			newList.push(oldList[i]);
		}
	}
	this.elements = newList;
}

NextDomForm.prototype.setProperty = function(name, value){
	this.removeField(name);
	var input = next.dom.newInput("text", name, null, {'value': value});
	if(!next.util.isDefined(this.customElements)){
		this.customElements = new Array();
	}
	this.customElements.push(input);
	return this;
}

/**
 * Remove os campos ACAO, suppressValidation, supperssErrors
 */
NextDomForm.prototype.removeSystemFields = function(){
	this.removeField('ACAO');
	this.removeField('ACTION');
	this.removeField('suppressValidation');
	this.removeField('suppressErrors');
}

/**
 * Cria a query com os elementos do formulário
 * @return
 */
NextDomForm.prototype.toQueryString = function(){
	var elements = new Array(); 
	elements = elements.concat(next.util.toArray(this.getElements()));
	if(next.util.isDefined(this.customElements)){
		elements = elements.concat(this.customElements);
	}
	var queryString = [];
	next.util.each(elements, function(el){
		var type = el.type == null? 'undefined' : el.type.toLowerCase();
		if (!el.name || el.disabled || type == 'submit' || type == 'reset' || type == 'file' || type == 'image') return;

		if(el.tagName.toLowerCase() == 'select'){
			if(el.multiple){
				var ops = next.dom.getSelectedValue(el);
				next.util.each(ops, function(op){queryString.push(encodeURIComponent(el.name)+'='+encodeURIComponent(op))});
			} else {
				queryString.push(encodeURIComponent(el.name)+'='+encodeURIComponent(next.dom.getSelectedValue(el)));
			}
		} else if(!((type == 'radio' || type == 'checkbox') && !el.checked)){
			queryString.push(encodeURIComponent(el.name)+'='+encodeURIComponent(el.value));
		}
	});
	return next.util.join(queryString, '&');
}

NextDomForm.prototype.newForm = function(){
	return new NextDomForm();
}

/**
 * Associa os elementos do HTML aos atributos do objeto.
 * O elemento deve ter como id um '#' seguido do nome do atributo.
 * Se deseja associar um botao, a um elemento do objeto, e o atributo no objeto for foo,
 * o id do botão deve ser #foo. 
 * Também associa eventos. Se existir um elemento #foo, e no objeto existir uma função fooOnClick, 
 * a função será associada ao evento onclick do elemento.
 * Se o parametro el for informado, só irá fazer o bind, dos elementos que forem filhos de el.
 * Os atributos precisam ter sido definidos no protoype da classe. (Objetos STJS atendem esse requisito) 
 */
NextDom.prototype.autobind = function (obj, parentEl){
	if(!next.util.isDefined(parentEl)){
		parentEl = window.document.body;
	}
	for(p in obj){
		if(!next.util.isFunction(obj[p])){
			var el = next.dom.getInnerElementById(parentEl, "#"+p);
			if(next.util.isDefined(el)){
				console.debug("binding "+el+" to "+p);
				obj[p] = el;
			}
		} else {
			var eventIndexStart = p.indexOf('On'); 
			if(eventIndexStart > 0){
				var id = "#"+p.substring(0, eventIndexStart);
				var el = next.dom.getInnerElementById(parentEl, id);
				if(next.util.isDefined(el)){
					console.debug("binding event on"+p.substring(eventIndexStart+2)+" of "+el.tagName.toLowerCase()+id+" to "+p);
					next.dom.bindEventToObject(el, p.substring(eventIndexStart+2).toLowerCase(), obj, p);
				}
			}
		}
	}
}

NextDom.prototype.bindEventToObject = function(el, event, obj, func){
	next.events.attachEvent(el, event, 
			function(){
				return obj[func].call(obj, arguments);
			});
}

NextDom.prototype.Form = NextDomForm;

new NextDom();

/**************************************************************************************  STYLE  **/

/*
 * 
 * Para o correto funcionamento das funções de estilo o browser deve estar funcionando em modo Standard
 * 
 * Coloque o seguinte conteúdo no topo do HTML
 *  <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
 *  
 */

NextStyle = function(){}

/**
 * Verifica se um determinado elemento possui uma classe CSS
 * @param ele
 * @param cls
 * @return
 */
NextStyle.prototype.hasClass = function(ele, cls) {
	if(ele.className && cls){
		return ele.className.match(new RegExp('(\\s|^)' + cls + '(\\s|$)'));
	} else {
		return false;
	}
}
/**
 * Adiciona uma classe CSS ao elemento
 * @param ele
 * @param cls
 * @return
 */
NextStyle.prototype.addClass = function(ele, cls) {
	if (!this.hasClass(ele, cls) && cls != null && cls.length > 0){
		ele.className += " " + cls;
		ele.className = ele.className.trim();
	}
}
/**
 * Remove uma classe CSS do elemento
 * @param ele
 * @param cls
 * @return
 */
NextStyle.prototype.removeClass = function(ele, cls) {
	if (this.hasClass(ele, cls)) {
		var reg = new RegExp('(\\s|^)' + cls + '(\\s|$)');
		ele.className = ele.className.replace(reg, ' ');
		ele.className = ele.className.trim();
	}
}
/**
 * Transforma um valor em pixels em um inteiro (faz as conversões necessárias)
 * <BR>
 * Exemplo: Se o valor informado for '20px' retorna 20, se for '20' retorna 20
 * 
 * @param value
 * @return
 */
NextStyle.prototype.fromPxToInt = function(value){
	if(!next.util.isDefined(value)){
		return 0;
	}
	if(next.util.typeOf(value) != 'number'){
		if(value.indexOf('px') > 0){
			value = parseInt(value.substring(0, value.indexOf('px')));
		}
	}
	return parseInt(value);
}

/**
 * Retorna o valor de uma propriedade CSS computada pelo browser. Deve ser utilizado o padrão CSS de propriedades exemplo:
 * next.style.getStyleProperty(obj, 'padding-left').
 * <BR>
 * @param obj Elemento do qual deseja o valor
 * @param property Propriedade que se deseja saber o valor
 * @return O valor computado pelo browser
 */
NextStyle.prototype.getStyleProperty = function(obj, property){
	return this.getStylePropertyGeneric(obj, next.util.fromSeparatorToCamel(property, '-'), property);
}

NextStyle.prototype.getStylePropertyGeneric = function(obj, IEStyleProp, CSSStyleProp)
{
	if (obj.currentStyle) // IE
		return obj.currentStyle[IEStyleProp];
	else if (window.getComputedStyle) // W3C
		return window.getComputedStyle(obj,"").getPropertyValue(CSSStyleProp);
	return null;
}

NextStyle.prototype.getOffset = function ( el ) {
	var _x = 0;
	var _y = 0;
	while( el && !isNaN( el.offsetLeft ) && !isNaN( el.offsetTop ) ) {
		_x += el.offsetLeft - el.scrollLeft;
		_y += el.offsetTop - el.scrollTop;
		el = el.offsetParent;
	}
	return { top: _y, left: _x };
}

NextStyle.prototype.getTop = function ( el ) {
	return this.getOffset(el).top;
}
NextStyle.prototype.getLeft = function ( el ) {
	return this.getOffset(el).top;
}

/**
 * Retorna a altura do elemento. Nao considera padding, margin e nem border. Altura interna.
 * @param el
 * @return Um inteiro representando a altura interna em pixels
 */
NextStyle.prototype.getHeight = function(el){
	el = next.dom.toElement(el);
	var offsetHeight = el.offsetHeight;
	if(offsetHeight == 0){
		return 0; //se o offsetHeight é zero, o height também é
	}
	//pegar o valor calculado
	var styleHeight = this.getStyleProperty(el, 'height');
	if(styleHeight == 'auto'){
		function m(p){return next.style.fromPxToInt(next.style.getStyleProperty(el, p));}
		var spacing = m('padding-top')+m('padding-bottom')+m('border-top-width')+m('border-bottom-width');
		styleHeight = offsetHeight - spacing;
	} else {
		styleHeight = next.style.fromPxToInt(styleHeight);
	}
	return styleHeight;
}
/**
 * Retorna a largura do elemento. Nao considera padding, margin e nem border. Largura interna.
 * @param el
 * @return Um inteiro representando a largura interna em pixels
 */
NextStyle.prototype.getWidth = function(el){
	el = next.dom.toElement(el);
	var offsetWidth = el.offsetWidth;
	if(offsetWidth == 0){
		return 0; //se o offsetWidth é zero, o width também é
	}
	//pegar o valor calculado
	var styleWidth = this.getStyleProperty(el, 'width');
	if(styleWidth == 'auto'){
		function m(p){return next.style.fromPxToInt(next.style.getStyleProperty(el, p));}
		var spacing = m('padding-left')+m('padding-right')+m('border-left-width')+m('border-right-width');
		styleWidth = offsetWidth - spacing;
	} else {
		styleWidth = next.style.fromPxToInt(styleWidth);
	}
	return styleWidth;
}

/**
 * Retorna a altura completa do elemento. Incluindo padding, margin e border.
 * Se a margem for auto, será considerado o valor 0.
 * @param el
 * @return
 */
NextStyle.prototype.getFullHeight = function(el){
	el = next.dom.toElement(el);
	var offsetHeight = el.offsetHeight;
	var fullHeight = offsetHeight;
	//se o display for none.. a margem também é
	var display = this.getStyleProperty(el, 'display');
	if(display == 'none'){
		fullHeight = 0;
	} else {
		function m(p){mg = next.style.getStyleProperty(el, p); if(mg == 'auto'){mg = 0;}return next.style.fromPxToInt(mg);}
		fullHeight += m('margin-top')+m('margin-bottom');
	}
	return fullHeight;
}
/**
 * Retorna a largura completa do elemento. Incluindo padding, margin e border.
 * Se a margem for auto, será considerado o valor 0.
 * @param el
 * @return
 */
NextStyle.prototype.getFullWidth = function(el){
	el = next.dom.toElement(el);
	var offsetWidth = el.offsetWidth;
	var fullWidth = offsetWidth;
	//se o display for none.. a margem também é
	var display = this.getStyleProperty(el, 'display');
	if(display == 'none'){
		fullWidth = 0;
	} else {
		function m(p){mg = next.style.getStyleProperty(el, p); if(mg == 'auto'){mg = 0;}return next.style.fromPxToInt(mg);}
		fullWidth += m('margin-left')+m('margin-right');
	}
	return fullWidth;
}

NextStyle.prototype.getBodySize = function(){
	return [next.style.getFullWidth(document.body), next.style.getFullHeight(document.body)];
}

/**
 * Returns the size of the window avaiable for the document
 */
NextStyle.prototype.getWindowSize = function(){
	var winW = 630, winH = 460;
	if (document.body && document.body.offsetWidth) {
		winW = document.body.offsetWidth;
		winH = document.body.offsetHeight;
	}
	if (document.compatMode == 'CSS1Compat' && document.documentElement
			&& document.documentElement.offsetWidth) {
		winW = document.documentElement.offsetWidth;
		winH = document.documentElement.offsetHeight;
	}
	if (window.innerWidth && window.innerHeight) {
		winW = window.innerWidth;
		winH = window.innerHeight;
	}
	
	var documentHeight = next.style.getFullHeight(document.body);
	if(documentHeight > winH){
		winH = documentHeight;
	}
	
	return [winW, winH];
}

NextStyle.prototype.setRadius = function(el, radius){
	el.style.mozBorderRadius = radius;
	el.style.borderRadius = radius;
}

//-webkit-box-shadow: 6px 8px 16px 0px #AAA;
//-moz-box-shadow: 6px 8px 16px 0px #AAA;
//box-shadow: 6px 8px 16px 0px #AAA;
NextStyle.prototype.setShadow = function(el, shadow){
	el.style.webkitBoxShadow = shadow;
	el.style.mozBoxShadow = shadow;
	el.style.boxShadow = shadow;
}

//	filter: alpha(opacity=1);	/* internet explorer */
//	-khtml-opacity: 0.01;		/* khtml, old safari */
//	-moz-opacity: 0.01;	 		/* mozilla, netscape */
//	opacity: 0.01;				/* fx, safari, opera */
NextStyle.prototype.setOpacity = function(el, opacity){
	el.style.filter = "alpha(opacity="+opacity+")";
	if(opacity < 100){
		el.style.khtmlOpacity = "0."+opacity+"";
		el.style.mozOpacity   = "0."+opacity+"";
		el.style.opacity      = "0."+opacity+"";
	} else {
		el.style.khtmlOpacity = "1";
		el.style.mozOpacity   = "1";
		el.style.opacity      = "1";
	}
}

NextStyle.prototype.centralizeHorizontal = function(element){
	var width = next.style.getFullWidth(element);
	var windowWidth = next.style.getWindowSize()[0];
	var left = (windowWidth /2) - (width/2);
	element.style.left = left + 'px';
}

NextStyle.prototype.centralizeVerticalMiddleLine = function(element){
	var height = next.style.getFullHeight(element);
	var windowHeight = next.style.getWindowSize()[1];
	var top = (windowHeight /2) - (height);
	element.style.top = top + 'px';
}

NextStyle.prototype.centralizeVertical = function(element){
	var height = next.style.getFullHeight(element);
	var windowHeight = next.style.getWindowSize()[1];
	var top = (windowHeight /2) - (height/2);
	element.style.top = top + 'px';
}

NextStyle.prototype.centralize = function(element){
	next.style.centralizeHorizontal(element);
	next.style.centralizeVertical(element);
}

NextStyle.prototype.centralizeMiddleLine = function(element){
	next.style.centralizeHorizontal(element);
	next.style.centralizeVerticalMiddleLine(element);
}

new NextStyle();

/**************************************************************************************  AJAX  **/

/**** CALLBACKS *****/
NextAjaxCallBacks = function(){};
NextAjaxCallBacks.prototype.eval = function(){
	return function(data){eval(data)};
};
NextAjaxCallBacks.prototype.alert = function(){
	return function(data){alert(data)};
};
NextAjaxCallBacks.prototype.evalScripts = function(){
	return function(contents){
		next.util.evalScripts(contents);
	}
}
NextAjaxCallBacks.prototype.innerHTML = function(el, evalScripts){
	if(!next.util.isDefined(evalScripts)){
		evalScripts = true;
	}
	el = next.dom.toElement(el);
	return function(data){el.innerHTML = data; if(evalScripts){ next.ajax.callbacks.evalScripts()(data);}};
};
/**** CALLBACKS *****/

NextAjax = function(){
	this.callbacks = new NextAjaxCallBacks();
};

NextAjax.appendContext = true;
NextAjax.noCache = false;

NextAjax.READY_STATE_UNINITIALIZED=0;
NextAjax.READY_STATE_LOADING=1;
NextAjax.READY_STATE_LOADED=2;
NextAjax.READY_STATE_INTERACTIVE=3;
NextAjax.READY_STATE_COMPLETE=4;

/**
 * Efetua uma chamada ajax. <BR>
 * Exemplo:
 * <PRE>
 * 	next.ajax.send({
 *		url: '/module/controller', 
 *		appendContext: true,
 *		params: 'ACTION=filtrar&id=5', 
 *		evalScripts: true,
 *		onComplete: function(data){
 *						document.getElementById('container').innerHTML = data;
 *					}
 *	});
 * </PRE>
 * @param options
 * @return
 */
NextAjax.prototype.send = function(options){
	var p = next.util.defaultParam(options);
	p("url", window.location.pathname);
	p("appendContext", options.url == window.location.pathname? false:NextAjax.appendContext);
	p("params", "");
	p("async", true);
	p("charset", "UTF-8");
	p("method", "POST");
	p("callbackParameters", {});
	p("onComplete", function(data){});
	p("afterComplete", function(data){});
	p("evalResponse", false);
	p("evalScripts", false);
	p("noCache", NextAjax.noCache);
	p("onError", function(data, status, cp, req){
			var exmessage = req.getResponseHeader("EX-MESSAGE");
			if(exmessage){
				alert(exmessage);
				console.error("Erro "+status+": Erro no servidor ao efetuar AJAX\nURL: "+options.url+"?"+options.params);
			} else {
				var exerror = req.getResponseHeader("EX-ERROR-MESSAGE");
				alert("Erro "+status+": Erro no servidor ao efetuar AJAX\nURL: "+options.url+"?"+options.params+"\n\n"+exerror); 
			}
		});
	p("afterError", function(data){});	
	p("on404", function(data, status){alert("Erro 404 - Não encontrado: "+options.url);});

	if(options.appendContext){
		options.url = next.http.getApplicationContext()+options.url;
	}
	//console.log(options.url);
	if(next.util.typeOf(options.onComplete) != 'array'){
		var f = options.onComplete;
		options.onComplete = new Array();
		options.onComplete.push(f);
	}
	if(options.evalResponse){
		options.onComplete.push(next.ajax.callbacks.eval());
	}
	if(options.evalScripts){
		options.onComplete.push(next.ajax.callbacks.evalScripts());
	}
	if(next.util.typeOf(options.onComplete) == 'array'){
		var ops = options.onComplete;
		options.onComplete = function(data, callbackParameters){
			for(var i = 0; i < ops.length; i++){
				ops[i].call(window, data, callbackParameters);
			}
		};
	}
	
	var request = next.ajax.getXMLHTTPRequest();
	
	request.onreadystatechange = function () {
		if(request.readyState == NextAjax.READY_STATE_COMPLETE) {
			if(request.status && request.status == 200){
				options.onComplete(request.responseText, options.callbackParameters, request);				
				options.afterComplete(request.responseText, options.callbackParameters, request);				
			} else if(request.status && request.status == 404){
				options.on404(request.responseText, request.status, options.callbackParameters, request);
			} else {
				options.onError(request.responseText, request.status, options.callbackParameters, request);
				options.afterError(request.responseText, request.status, options.callbackParameters, request);
			}
		}
	}
	request.open(options.method, options.url, options.async);
	request.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset="+options.charset);
	if(options.noCache){
		var nocacheparam = 'noCache'+next.dom.generateUniqueId()+'='+next.dom.generateUniqueId()+Math.random();
		if(options.params != ''){
			options.params += '&';
		}
		options.params += nocacheparam;
	}
	request.send(options.params);
}

NextAjax.prototype.getXMLHTTPRequest = function(){
	if (window.XMLHttpRequest) {
		return new XMLHttpRequest();
	} else if (typeof ActiveXObject != "undefined"){
		return new ActiveXObject("Microsoft.XMLHTTP");
	} else {
		throw "Nao foi possível criar um objeto XMLHTTPRequest";
	}
}

NextAjax.prototype.newFormRequest = function(name, elements){
	var r = new NextAjaxRequest({}, new NextDomForm(name, elements));
	return r;
}
NextAjax.prototype.newRequest = function(){
	var r = new NextAjaxRequest({});
	return r;
}

new NextAjax();

NextAjaxRequest = function(options, form){
	if(!next.util.isDefined(options)){
		options = {};
	}
	this.options = options;
	this.form = form;
	this.customParams = {};
	if(next.util.isDefined(this.form)){
		this.setUrl(this.form.formElement.action);
		this.setAppendContext(false);
	}
};
NextAjaxRequest.prototype.send = function(data){
	if(next.util.isDefined(data)){
		this.options.params = data;
	}
	if(!next.util.isDefined(this.options.params)){
		this.options.params = '';
	}
	for(p in this.customParams){
		this.options.params += '&'+ encodeURIComponent(p) + '=' + encodeURIComponent(this.customParams[p]);
	}
	if(next.util.isDefined(this.form)){
		this.form.removeSystemFields();
		this.options.params += '&' + this.form.toQueryString();
	}
	next.ajax.send(this.options);
};

NextAjaxRequest.prototype.setFormPropertyPrefix = function(){
	var a = new Array();
	if(next.util.isArray(arguments[0])){
		a = arguments[0];
	} else {
		for(var i = 0; i < arguments.length; i++){
			a.push(arguments[i]);
		}
	}
	if(next.util.isDefined(this.form)){
		this.form = this.form.subForm(a);
	}
	return this;
};
NextAjaxRequest.prototype.setAction = function(action){
	this.customParams['ACTION'] = action;
	return this;
};

NextAjaxRequest.prototype.setParameterFromElement = function(el, name){
	el = next.dom.toElement(el);
	var value = next.dom.getInputValue(el);
	if(value == null){
		this.customParams[name] = '';
		return;
	}
	if(!next.util.isDefined(name)){
		name = el.name;
}
	return this.setParameter(name, value);
}
NextAjaxRequest.prototype.setParameter = function(name, value){
	if(value == null){
		return this.setParameterFromElement(name);
	}
	this.customParams[name] = value;
	return this;
};

NextAjaxRequest.prototype.setUrl = function(url){
	this.options['url'] = url;
	return this;
};
NextAjaxRequest.prototype.setAppendContext = function(appendContext){
	this.options['appendContext'] = appendContext;
	return this;
};
NextAjaxRequest.prototype.setOnComplete = function(onComplete){
	this.options['onComplete'] = onComplete;
	return this;
};
NextAjaxRequest.prototype.setOnError = function(onError){
	this.options['onError'] = onError;
	return this;
};
NextAjaxRequest.prototype.setAfterError = function(onError){
	this.options['afterError'] = onError;
	return this;
};
NextAjaxRequest.prototype.setCallback = function(callback, methodName){
	this.options['onComplete'] = function(data) {
		var objectResponse = null;
		try {
			objectResponse = eval("(" + data + ")");
		} catch(e){
			alert('Ajax error evaluating response '+e+'\n'+data);
			throw e;
		}
		if(methodName){
			callback[methodName].call(callback, objectResponse);
		} else {
		callback(objectResponse);
		}
	};
	return this;
};

NextAjaxRequest.prototype.newAjaxRequest = function(){
	return new NextAjaxRequest();
};

NextAjax.prototype.Request = NextAjaxRequest;

/**************************************************************************************  EFFECTS  **/
NextEffects = function(){};

NextEffects.prototype.blockScreen = function(){
	var blockScreenId = '__block_screen';
	var blockScreen = document.getElementById(blockScreenId);
	var innerElement;
	if(!next.util.isDefined(blockScreen)){
		blockScreen = next.dom.newElement('DIV', {'id':blockScreenId});
		next.dom.insertFirstChild(document.body, blockScreen);
		innerElement = next.dom.newElement('DIV',
				{
					className: 'blockScreenTransparent',
					style: {
						position: 'fixed',
						top:'0px',
						left:'0px',
						zIndex: 1050,
						width: '100vw',
						height: '100vh',
						display: 'none'
					}
				});
		next.dom.insertFirstChild(blockScreen, innerElement);
	} else {
		innerElement = 	blockScreen.childNodes[0];	
	}
	innerElement.style.display = 'block';
}

NextEffects.prototype.unblockScreen = function(){
	var blockScreenId = '__block_screen';
	var blockScreen = document.getElementById(blockScreenId);
	if(next.util.isDefined(blockScreen)){
		var innerElement = 	blockScreen.childNodes[0];
		innerElement.style.display = 'none';
	}
}

NextEffects.prototype.show = function(el){
	el = next.dom.toElement(el);
	el.style.display = '';  
}

NextEffects.prototype.hide = function(el){
	el = next.dom.toElement(el);
	el.style.display = 'none';
}

NextEffects.prototype.showProperty = function(el){
	next.effects.showHideProperty(el, true);
}

NextEffects.prototype.hideProperty = function(el){
	next.effects.showHideProperty(el, false);
}

NextEffects.prototype.showHideProperty = function(el, show){
	
	var el2 = el;
	if (typeof(el) == 'string') {
		el2 = next.dom.toElement(el);
	}
	
	if (el2 == null) {
		alert("Elemento '" + el + "' não encontrado!");
	}
	
	var panel = next.dom.id('p_' + el2.id);
	if (panel != null) {
		panel.style.display = show ? '' : 'none';
	}
	
	var label = next.dom.id('l_' + el2.id);
	if (label != null) {
		label.style.display = show ? '' : 'none';
	}
	
}

NextEffects.prototype.highlightOnOver = function(el, overColor, outColor){
	next.events.attachEvent(el, 'mouseover', function(){
		this.style.backgroundColor = overColor;
	});
	next.events.attachEvent(el, 'mouseout', function(){
		this.style.backgroundColor = outColor;
	});
}

/**
 * Pisca a borda de um elemento, o elemento tem que possuir uma borda configurada
 * @param el
 * @return
 */
NextEffects.prototype.blink = function(el){
	var styleProperty=next.style.getStyleProperty(el, "border-color");
	if(styleProperty){
		styleProperty = styleProperty.toString();
	} else {
		styleProperty = "#FFFFFF";
	}
	this.blinkColors(el, styleProperty, "#BBBB55");
}

NextEffects.prototype.blinkColors = function(el, colorA, colorB){
	var interval = 80;

	el.style.borderColor = colorB;
	this.runSteps(function(step){
		if(step % 2 == 0){
			el.style.borderColor = colorA;
		} else {
			el.style.borderColor = colorB;
		}
		return true;
	}, interval, 6);
}

/**
 * Cria um efeito de slide in ou slide out
 * @param el Elemento onde deve ser aplicado o efeito
 * @param mode 'in' ou 'out' 
 * @param options
 * @param onEndFunction
 * @return
 */
NextEffects.prototype.slide = function(el, mode, options, onEndFunction){
	el = next.dom.toElement(el);
	if(!next.util.isDefined(options)){
		options = {};
	}
	var wrapper = next.dom.wrapper(el);
	wrapper.style.overflow = 'hidden';
	var d = next.util.defaultParam(options);
	d("increment", 6);
	d("fps", 50);
	d("minimumHeight", 0);
	d("maximumHeight", next.style.getFullHeight(el));
	d("time", 1000);
	
	var frameInterval = options.time / parseInt(options.fps);
	var numberOfFrames = options.time / frameInterval;
	var currentHeight = next.style.getHeight(wrapper);
	
	var step;
	if(mode == 'out'){
		var delta = currentHeight - options.minimumHeight;
		if(delta < 0){
			return;
		}
		step = -delta / numberOfFrames;
	} else {
		var delta = options.maximumHeight - currentHeight;
		if(delta < 0){
			return;
		}
		step = delta / numberOfFrames;
	}
	var stepFunction = function(){
		currentHeight += step;
		if(next.util.typeOf(currentHeight) == 'number' && currentHeight < 0){
			currentHeight = 0;
		}
		wrapper.style.height = parseInt(currentHeight) + 'px';
	};
	
	this.runSteps(stepFunction, frameInterval, numberOfFrames, options.time + 10000, function(){
		//para compensar um possivel erro na precisao do calculo
		if(mode == 'out'){
			wrapper.style.height = options.minimumHeight;
		} else {
			wrapper.style.height = options.maximumHeight;
		}
		if(onEndFunction){
			onEndFunction();
		}
	});
	
}


/**
 * Cria um efeito de fade in ou fade out
 * @param el Elemento onde deve ser aplicado o efeito
 * @param fromOpacity 
 * @param toOpacity
 * @param options
 * @param onEndFunction
 * @return
 */
NextEffects.prototype.fade = function(el, fromOpacity, toOpacity, options, onEndFunction){
	el = next.dom.toElement(el);
	if(!next.util.isDefined(options)){
		options = {};
	}
	var d = next.util.defaultParam(options);
	d("time", 1000);
	d("fps", 50);
	
	
	var frameInterval = options.time / parseInt(options.fps);
	var numberOfFrames = options.time / frameInterval;
	
	var delta = toOpacity - fromOpacity;
	var step = delta / numberOfFrames;
	
	el.style.opacity = fromOpacity;
	
	
	var currentOpacity = fromOpacity; 
	
	var stepFunction = function(){
		currentOpacity += step;
		el.style.opacity = currentOpacity;
	}
	
	var onEnd = function(){
		el.style.opacity = toOpacity;
		if(onEndFunction){
			onEndFunction(el);
		}
	}
	
	this.runSteps(stepFunction, frameInterval, numberOfFrames, options.time+10000, onEnd);
	
}

/**
 * Executa a função definida por <i>func</i> a cada intervalo de tempo.
 * @param func Função a ser executada (pode receber um parâmetro com passo atual
 * @param frameInterval Intervalo entre cada chamada
 * @param ammount Número de chamadas
 * @param timeout Timeout (default: 60 segundos)
 * @param onEndFunction Função a ser executada quando terminar a execução
 * @return
 */
NextEffects.prototype.runSteps = function(func, frameInterval, ammount, timeout, onEndFunction){
	if(!next.util.isDefined(timeout)){timeout = 60000;}
	var endOnFalse = false;
	if(ammount == 0){
		endOnFalse = true;
	}
	var clear = function(){
		clearInterval(interval);
		if(onEndFunction){
			onEndFunction();
		}
	}
	var currentStep = 0;
	var interval = window.setInterval(function(){
		//next.log.info('currentStep '+currentStep);
		if(endOnFalse){
			if(!func(currentStep)){
				clear();
			}
		} else {
			if(ammount-- >= 0){
				func(currentStep);
			} else {
				clear();
			}
		}
		currentStep++;
	}, frameInterval);
	
	
	setTimeout(function(){clearInterval(interval);}, timeout);
}

new NextEffects();


/**************************************************************************************  MENSAGEM  **/

NextMessageTypes = function(){
	this.DEBUG = 'debug';
	this.TRACE = 'trace';
	this.INFO = 'info';
	this.WARN = 'warn';
	this.ERROR = 'error';
	this.REDIRECT = 'redirect';
	this.FORBIDDEN = 'forbidden';
	this.QUESTION = 'question';
	this.EXCLAMATION = 'exclamation';
	this.EVENT = 'event';
	this.TASK = 'task';
	this.MESSAGE = 'message';
};

NextMessages = function(){
	this.types = new NextMessageTypes();
	this.styleClasses = {};
	this.onAddMessageEvents = new Array();
	this.onRemoveMessageEvents = new Array();
};

NextMessagesMessage = function(message, type, li, div, block){
	this.message = message;
	this.type = type;
	this.li = li;
	this.div = div;
	this.block = block;
}

NextMessages.prototype.getStyleClass = function(type){
	return type != null && type != 'null' ? this.styleClasses[type + 'Class'] : null;
}

NextMessagesMessage.prototype.remove = function(){
	var bigThis = this;
	next.util.each(next.messages.onRemoveMessageEvents, function(e){
		e(bigThis.message, bigThis.block, bigThis.div, bigThis.li);
	});
	var ul = this.li.parentNode; 
	ul.removeChild(this.li);
	if(!ul.hasChildNodes()){
		ul.parentNode.parentNode.removeChild(ul.parentNode);
		this.div.messageBlocks[this.block] = null;
	}
	//next.util.removeItem(next.messages.messages, this);
}

NextMessages.prototype.Message = NextMessagesMessage;

NextMessages.prototype.toast = function(msg){
	var toastDiv = next.dom.newElement('div', { className:this.getStyleClass('toast'), innerHTML: "<div>"+msg+"</div>" });
	next.dom.insertFirstChild(document.body, toastDiv);
	setTimeout(function(){
		next.effects.fade(toastDiv, 0.9, 0, {}, function(){
			document.body.removeChild(toastDiv);
		})}, 2000 + msg.length * 100);
}

NextMessages.prototype.initialize = function(div, blockId){
	if(next.util.isDefined(div.messageBlocks) && next.util.isDefined(div.messageBlocks[blockId])){
		return;
	}
	var bigThis = this;
	var originalDiv = div;
	if(div.id != blockId){
		var childNodes = div.childNodes;
		var found = false;
		for(var i = 0; i < childNodes.length; i++){
			if(childNodes[i].id == blockId){
				found = true;
				if(!next.util.isDefined(div.messageBlocks)){
					div.messageBlocks = {};
				}
				div.messageBlocks[blockId] = childNodes[i];
				break;
			}
		}
		if(!found){
			var messageBlockDiv = document.createElement('div');
			messageBlockDiv.id = blockId;
			div.appendChild(messageBlockDiv);
			if(!next.util.isDefined(div.messageBlocks)){
				div.messageBlocks = {};
			}
			div.messageBlocks[blockId] = messageBlockDiv;
		}
	} else {
		if(!next.util.isDefined(div.messageBlocks)){
			div.messageBlocks = {};
		}
		div.messageBlocks[blockId] = div;
	}
	div.messageBlocks[blockId].getUL = function(){
		var el = div.messageBlocks[blockId];
		var children = el.childNodes;
		var ul = null;
		for(var i = 0; i < children.length; i++){
			if(children[i].tagName == 'UL'){
				ul = children[i];
				break;
			}
		}
		if(ul == null){
			ul = document.createElement('ul');
			div.messageBlocks[blockId].appendChild(ul);
		}
		ul.addMessage = function(message, type){
			var li = document.createElement('li');
			var sc = bigThis.getStyleClass(type);
			if (sc != null) {
				li.className = sc;
			}
			li.innerHTML = message;
			var closeIcon = document.createElement('div');
			closeIcon.className = 'messageCloseIcon';
			closeIcon.innerHTML = 'x';
			li.appendChild(closeIcon);
			next.events.attachEvent(closeIcon, 'click', function(){
				var li = this.parentNode;
				var ul = li.parentNode; 
				li.parentNode.removeChild(li);
				next.util.each(next.messages.onRemoveMessageEvents, function(e){
					e(message, blockId, originalDiv, li);
				});
				ul.removeMessage(null);
			});
			ul.appendChild(li);
			return li;
		}
		ul.removeMessage = function(message){
			var lis = ul.childNodes;
			for(var i = 0; i < lis.length; i++){
				if(lis[i].innerHTML == message){
					next.util.each(next.messages.onRemoveMessageEvents, function(e){
						e(message, blockId, originalDiv, lis[i]);
					});
					ul.removeChild(lis[i]);
					break;
				}
			}
			var count = 0;
			lis = ul.childNodes;
			for(var i = 0; i < lis.length; i++){
				if(lis[i].tagName == 'LI'){
					count++;
				}
			}
			if(count == 0){
				try {
					ul.parentNode.parentNode.removeChild(ul.parentNode);
					div.messageBlocks[blockId] = null;
				} catch (e) {}
			}
		}
		return ul;
	}
	next.style.addClass(div.messageBlocks[blockId], this.getStyleClass(blockId));
}

/**
 * Adiciona uma mensagem na tela
 * @param message
 * @param type
 * @param div
 * @return
 */
NextMessages.prototype.addMessage = function(message, type, div){
	return this.addMessageToBlock(message, type, div, 'messageBlock');
}
/**
 * Remove uma mensagem da tela
 * @param message
 * @param div
 * @return
 */
NextMessages.prototype.removeMessage = function(message, div){
	return this.removeMessageFromBlock(message, div, 'messageBlock');
}
/**
 * Adiciona uma mensagem de bind na tela
 * @param message
 * @param type
 * @param div
 * @return
 */
NextMessages.prototype.addBindMessage = function(message, type, div){
	return this.addMessageToBlock(message, type, div, 'bindBlock');
}
/**
 * Remove uma mensagem de bind na tela
 * @param message
 * @param div
 * @return
 */
NextMessages.prototype.removeBindMessage = function(message, div){
	return this.removeMessageFromBlock(message, div, 'bindBlock');
}
/**
 * Configura o título da mensagem de bind
 * @param title
 * @param div
 * @return
 */
NextMessages.prototype.setBindTitle = function(title, div){
	if(next.util.isDefined(div)){
		div = next.dom.toElement(div);
	} else {
		div = document.getElementById('messagesContainer');
	}
	this.initialize(div, 'bindBlock');
	if(title == ''){
		var t = next.dom.id('bindTitle');
		if(next.util.isDefined(t)){
			t.parentNode.removeChild(t);
		}
	}
	var t = next.dom.id('bindTitle');
	if(!next.util.isDefined(t)){
		t = document.createElement('div');
		t.id = 'bindTitle';
		var sc = this.getStyleClass('title');
		if (sc != null) {
			t.className = sc;
		}
		var bb = document.getElementById('bindBlock');
		if(bb.hasChildNodes()){
			bb.insertBefore(t, bb.childNodes[0]);
		} else {
			bb.appendChild(t);
		}
	}
	t.innerHTML = title;
}


NextMessages.prototype.addMessageToBlock = function(message, type, div, block){
	if(next.util.isDefined(div)){
		div = next.dom.toElement(div);
	} else {
		div = document.getElementById('messagesContainer');
	}
	this.initialize(div, block);
	var li = div.messageBlocks[block].getUL().addMessage(message, type);
	var messageObj = new NextMessagesMessage(message, type, li, div, block);
	next.util.each(this.onAddMessageEvents, function(e){
		e(message, type, block, div, li);
	});
	return messageObj;
}

NextMessages.prototype.removeMessageFromBlock = function(message, div, block){
	if(next.util.isDefined(div)){
		div = next.dom.toElement(div);
	} else {
		div = document.getElementById('messagesContainer');
	}
	this.initialize(div, block);
	div.messageBlocks[block].getUL().removeMessage(message);
}

/**
 * Adicionar um listener de eventos ao adicionar mensagens
 * @param func
 * @return
 */
NextMessages.prototype.onAdd = function(func){
	this.onAddMessageEvents.push(func);
}

/**
 * Adicionar um listener de eventos ao remover mensagens
 * @param func
 * @return
 */
NextMessages.prototype.onRemove = function(func){
	this.onRemoveMessageEvents.push(func);
}

new NextMessages();

/****************************************************************************************** BROWSER */

NextBrowser = function(){
}

NextBrowser.browser = {
		init: function () {
			this.name = this.searchString(this.dataBrowser) || "An unknown browser";
			this.version = this.searchVersion(navigator.userAgent)
				|| this.searchVersion(navigator.appVersion)
				|| "an unknown version";
			this.OS = this.searchString(this.dataOS) || "an unknown OS";
		},
		searchString: function (data) {
			for (var i=0;i<data.length;i++)	{
				var dataString = data[i].string;
				var dataProp = data[i].prop;
				this.versionSearchString = data[i].versionSearch || data[i].identity;
				if (dataString) {
					if (dataString.indexOf(data[i].subString) != -1)
						return data[i].identity;
				}
				else if (dataProp)
					return data[i].identity;
			}
		},
		searchVersion: function (dataString) {
			var index = dataString.indexOf(this.versionSearchString);
			if (index == -1) return;
			return parseFloat(dataString.substring(index+this.versionSearchString.length+1));
		},
		dataBrowser: [
			{
				string: navigator.userAgent,
				subString: "Chrome",
				identity: "Chrome"
			},
			{ 	string: navigator.userAgent,
				subString: "OmniWeb",
				versionSearch: "OmniWeb/",
				identity: "OmniWeb"
			},
			{
				string: navigator.vendor,
				subString: "Apple",
				identity: "Safari",
				versionSearch: "Version"
			},
			{
				prop: window.opera,
				identity: "Opera",
				versionSearch: "Version"
			},
			{
				string: navigator.vendor,
				subString: "iCab",
				identity: "iCab"
			},
			{
				string: navigator.vendor,
				subString: "KDE",
				identity: "Konqueror"
			},
			{
				string: navigator.userAgent,
				subString: "Firefox",
				identity: "Firefox"
			},
			{
				string: navigator.vendor,
				subString: "Camino",
				identity: "Camino"
			},
			{		// for newer Netscapes (6+)
				string: navigator.userAgent,
				subString: "Netscape",
				identity: "Netscape"
			},
			{
				string: navigator.userAgent,
				subString: "MSIE",
				identity: "Explorer",
				versionSearch: "MSIE"
			},
			{
				string: navigator.userAgent,
				subString: "Gecko",
				identity: "Mozilla",
				versionSearch: "rv"
			},
			{ 		// for older Netscapes (4-)
				string: navigator.userAgent,
				subString: "Mozilla",
				identity: "Netscape",
				versionSearch: "Mozilla"
			}
		],
		dataOS : [
			{
				string: navigator.platform,
				subString: "Win",
				identity: "Windows"
			},
			{
				string: navigator.platform,
				subString: "Mac",
				identity: "Mac"
			},
			{
				   string: navigator.userAgent,
				   subString: "iPhone",
				   identity: "iPhone/iPod"
			},
			{
				string: navigator.platform,
				subString: "Linux",
				identity: "Linux"
			}
		]

	};

//Browser name: next.browser.name
//Browser version: next.browser.version
//OS name: next.browser.OS
NextBrowser.browser.init();

