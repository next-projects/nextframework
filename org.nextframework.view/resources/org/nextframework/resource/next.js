﻿/**
 * Acessa a API do next framework
 */
var next = {};
if(typeof(NEXT_MODULES_DEFINED) == "undefined"){
	alert('Módulos NEXT não carregados. Faça import do arquivo next-modules.js. (Antes do arquivo next.js)');
}

next.util = new NextUtil();

next.globalMap = new NextGlobalMap();

next.numbers = new NextNumbers();

next.log = new NextLog(); 

next.events = new NextEvents();
next.events.onLoad(function(){next.events.loaded = true;});

next.http = new NextHttp();

next.dom = new NextDom();

next.ajax = new NextAjax();

next.effects = new NextEffects();

next.messages = new NextMessages();

next.style = new NextStyle();

next.dialogs = new NextDialogs();

next.suggest = new NextSuggest();

next.reload = new NextReload();

next.datagrid = new NextDataGrid();

next.browser = NextBrowser.browser;
