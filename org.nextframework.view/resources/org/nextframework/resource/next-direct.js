/*Instala os módulos do next para uma utilização simplificada*/
$ = next.dom.toElement;

//$V = function(el){
//	return $(el).value;
//}

//$id = function(el, newValue){
//	el = $(el);
//	var value = el.id;
//	if(next.util.isDefined(newValue)){
//		el.id = newValue;
//	}
//	return value;
//}
	
util = next.util;
log = next.log;
events = next.events;
http = next.http;
dom = next.dom;
ajax = next.ajax;
effects = next.effects;
messages = next.messages;
dialogs = next.dialogs;
suggest = next.suggest;

Request = ajax.Request;

Form = dom.Form;


