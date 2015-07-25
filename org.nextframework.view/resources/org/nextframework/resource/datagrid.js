/* Log begin */
function log(s){
	var log = getLogConsole();
	if(log){
		log.innerHTML = s + "<BR>" + log.innerHTML;
	}
}

function logError(s){
	var log = getLogConsole();
	if(log){
		log.innerHTML = "<font color='red'>"+s+"</font>" + "<BR>" + log.innerHTML;
	}
}

function getLogConsole() {
	return null;//console desligado
	var log = document.getElementById("log");
	if(!log){
		log = document.createElement("div");
		log.innerHTML = "LOG";
		log.id = "log";
		log.style.position= "absolute";
		log.style.right = "1px";
		log.style.bottom ="1px";
		log.style.border ="1px solid gray";
		log.style.backgroundColor = "white";
		log.style.filter = "alpha(opacity=85)";
		log.style.opacity ="0.85";
		log.style.padding = 3;
		log.style.overflow = "auto";
		log.style.height = 300;
		document.body.appendChild(log);
	}
	return log;
}
/* Log end */

/* Datagrid resize */

function DatagridData(){
	this.count = 0;
}

var datagridData = new DatagridData();
var datagridList = new Array();

next.events.onLoad(function(){
	next.events.attachEvent(document.body, 'mouseup', datagridEndResize);
	for(var i = 0; i < datagridList.length; i++){
		reloadDatagridConfig(datagridList[0]);
	}
	//log("Sistema inicializado...");
});

function datagridStartResizeColumn(event, element, tableId){
	//log('Start RESIZE');
	document.body.style.cursor = "e-resize";
	
	var th = datagridUtilGetParentTag(element, "TABLE").parentNode;
	//element.getAttribute("data-columnresizer");
	var resizingColumn = th.cellIndex;
	log("Column "+resizingColumn+" Resize Start.. at "+event.clientX);
	var table = datagridGetTableFromId(tableId);
	
	if(table.getAttribute("data-delaycontainerfixsize")){
		fixDatagridContainerSize(tableId);
	}
	
	
	var originalColumns = table.getAttribute("data-originalcolumns");
	if(!originalColumns){
		originalColumns = new Array();
		for(var i = 0; i < table.rows[0].cells.length; i++){
			originalColumns[i] = table.rows[0].cells[i].offsetWidth;
		}
		table.setAttribute("data-originalcolumns", originalColumns);
	}
	table.setAttribute("data-resizingcolumn", resizingColumn);
	table.setAttribute("data-resizestartat", event.clientX);
	document.body.setAttribute("data-currentlyResizing", tableId);
}

function datagridEndResize(event){
	//log('End RESIZE'+event);
	if(!event){
		logError('datagridEndResize: Evento nulo');
		throw 'datagridEndResize: Evento nulo';
	}
	var currentResizing = document.body.getAttribute("data-currentlyResizing");
	if(currentResizing && currentResizing != 'null'){//testar a string por causa do IE
		datagridEndResizeColumn(event, currentResizing);
		document.body.setAttribute("data-currentlyResizing", null);
	}
	return false;
}

function datagridEndResizeColumn(event, tableId){
	//log('End RESIZE COLUMN'+event);
	document.body.style.cursor = "";
	var table = datagridGetTableFromId(tableId);
	var blockDiv = document.getElementById(tableId+'_block');
	var containerDiv = blockDiv.parentNode;
	var resizingColumn = table.getAttribute("data-resizingcolumn");
	var column = table.rows[0].cells[resizingColumn];
	var resizeStartAt = table.getAttribute("data-resizestartat");
	var delta = event.clientX - parseInt(resizeStartAt);
	var newWidth = getWidthForStyling(column) + delta;
	if(parseInt(newWidth)<1){
		newWidth = 1;
	}
	//log(table.offsetWidth);
	
	resizeTableColumn(table, blockDiv, resizingColumn, delta);
	
	var lastColumn = readjustLastColumn(table, blockDiv, containerDiv);
	if(resizingColumn == table.rows[0].cells.length -1){
		//redimensionou a última coluna
		lastColumn.setAttribute("data-originalwidth", newWidth);
	}
	
	//salvar a configuracao da tabela
	saveTableConfiguration(table);
	
}
function readjustLastColumnById(tableId){
	var table = datagridGetTableFromId(tableId);
	var blockDiv = document.getElementById(tableId+'_block');
	var containerDiv = blockDiv.parentNode;
	readjustLastColumn(table, blockDiv, containerDiv);
}
function readjustLastColumn(table, blockDiv, containerDiv) {
	//verificar se a tabela ficou menor que o container
	var containerWidth = getWidthForStyling(containerDiv);
	var tableWidth = getWidthForStyling(table);
	var lastColumnIndex = table.rows[0].cells.length -1;
	var lastColumn = table.rows[0].cells[lastColumnIndex];
	if(tableWidth < containerWidth){
		if(!lastColumn.getAttribute("data-originalwidth")){
			lastColumn.setAttribute("data-originalwidth", getWidthForStyling(lastColumn));
		}
		resizeTableColumn(table, blockDiv, lastColumnIndex, containerWidth - tableWidth);
	}
	else {
		//ultrapassou os limites.. reduzir a última coluna se possível
		var originalWidth = parseInt(lastColumn.getAttribute("data-originalwidth"));
		if(originalWidth){
			var overflow = tableWidth - containerWidth;
			var lastColumnWidth = getWidthForStyling(lastColumn);
			var extra = lastColumnWidth - overflow;
			var newWidth = 0;
			if(extra > 0){
				var suggestedWidth = lastColumnWidth - overflow;
				if(suggestedWidth > originalWidth){
					newWidth = suggestedWidth;
				} else {
					newWidth = originalWidth;
				}
			} else {
				newWidth = originalWidth;
			}
			//lastColumn.style.width = newWidth + "px";
			resizeTableColumn(table, blockDiv, lastColumnIndex, newWidth - lastColumnWidth);
		}
	}
	return lastColumn;
}

function saveTableConfiguration(table){
	//var id = table.id;
	var columnsWidths = getColumnsWidths(table);
	columnsWidths[columnsWidths.length - 1] = table.rows[0].cells[columnsWidths.length - 1].getAttribute("data-originalwidth");
	
	var columnCount = getColumnCount(table);
	next.http.setCookie("DG_"+table.getAttribute("data-datagridindex")+"_"+columnCount+"_"+next.http.getServletPath(), columnsWidths.toString(), 1);
}

function getColumnCount(table){
	return table.rows[0].cells.length;
}

function fixDatagridContainerSize(tableId){
	//var table = datagridGetTableFromId(tableId);
	var blockDiv = document.getElementById(tableId+'_block');
	var containerDiv = blockDiv.parentNode;
	
	var containerDivWidth=getWidthForStyling(containerDiv);
	if(containerDivWidth <= 0){
		//nao é possível definir o tamanho
		return;
	}
	containerDiv.style.width = containerDivWidth + "px";
}

function reloadDatagridConfig(tableId){
	var table = datagridGetTableFromId(tableId);
	var blockDiv = document.getElementById(tableId+'_block');
	var containerDiv = blockDiv.parentNode;
	
	var containerDivWidth=getWidthForStyling(containerDiv);
	if(containerDivWidth <= 0){
		//se o tamanho do container não está definido, quer dizer que ele está escondido, então não é suportado o recarregamento dos widths via cookie
		table.setAttribute("data-delaycontainerfixsize", true);
		return;
	}
	containerDiv.style.width = containerDivWidth + "px";
	clearTablePercentWidths(table);
	var columnCount = getColumnCount(table);
	var tableIndex = datagridData.count;
	datagridData.count = datagridData.count +1;
	table.setAttribute("data-datagridindex", tableIndex);
	var config = next.http.getCookie("DG_"+table.getAttribute("data-datagridindex")+"_"+columnCount+"_"+next.http.getServletPath());
	if(config){
		containerDiv.style.overflow = "hidden";
		var widths = config.split(",");
		for(var i = 0; i < widths.length; i++){
			var delta = parseInt(widths[i]) - getWidthForStyling(table.rows[0].cells[i]);
			if(isNaN(delta)){
				delta = 0;
			}
			resizeTableColumn(table, blockDiv, i, delta);
		}
		readjustLastColumn(table, blockDiv, containerDiv);
		setTimeout("readjustLastColumnById('"+tableId+"');document.getElementById('"+tableId+"_block').parentNode.style.overflow = 'auto';", 10);
//		var totalWidth = 0;
//		for(var i =0; i < widths.length; i++){
//			var width = widths[i];
//			totalWidth += parseInt(width);
//		}
//		var blockDivWidth = blockDiv.offsetWidth;
//
//		resizeTableColumn(table, blockDiv, 0, 0);//ativar o sistema
//		if(blockDivWidth < totalWidth){
//			//alert("tw"+totalWidth);
//			blockDiv.style.width = totalWidth+"px";
//		}
//		for(var i =0; i < widths.length; i++){
//			//alert(widths[i]);
//			table.rows[0].cells[i].style.width = widths[i]+ "px";
//		}
//		blockDiv.style.width = getWidthForStyling(table) + "px";
//		//readjustLastColumn(table, blockDiv, containerDiv);
//		resizeTableColumn(table, blockDiv, 0, 0);
		//readjustLastColumn(table, blockDiv, containerDiv);
	}
//	table.style.visibility = "";
}

function clearTablePercentWidths(table){
	var rows = table.rows;
	for(var i = 0; i < rows.length; i++){
		var cells = rows[i].cells;
		for(var j = 0; j < cells.length; j++){
			var widthStyle = getStyleProperty(cells[j], "width", "width");
			if(widthStyle.indexOf("%")>0){
				cells[j].style.width = getWidthForStyling(cells[j]) +"px";
			}
		}
	}
}

function resizeTableColumn(table, blockDiv, resizingColumn, delta){
	var column = table.rows[0].cells[resizingColumn];
	var columnsWidths = getColumnsWidths(table);
	
	table.style.width = "auto";

	for(var i = 0; i < table.rows[0].cells.length; i++){
		var cell = table.rows[0].cells[i];
		cell.style.width = columnsWidths[i]+"px";
	}
	if(delta >= 0){
		//se a coluna cresceu tem que redimenssionar o block
		blockDiv.style.width = (blockDiv.offsetWidth + delta) + "px";
		column.style.width = (columnsWidths[resizingColumn] + delta) + "px";
	} else {
		column.style.width = (columnsWidths[resizingColumn] + delta) + "px";
//		var totalWidth = 0;
//		for(var i = 0; i < columnsWidths.length ; i++){
//			totalWidth += columnsWidths[i];
//		}
		blockDiv.style.width = getWidthForStyling(table) + "px";
	}
}

function getColumnsWidths(table) {
	var columnsWidths = new Array();
	for(var i = 0; i < table.rows[0].cells.length; i++){
		var cell = table.rows[0].cells[i];
		var width = getWidthForStyling(cell);
		columnsWidths[i] = width;
		//log("c"+i+" ofw "+width+" sw " +cell.style.width);
	}
	return columnsWidths;
}
function getWidthForStyling(el) {
	var width = el.offsetWidth;
	
	var paddingHorizontal = parseInt(getStyleProperty(el, "paddingLeft", "padding-left")) + parseInt(getStyleProperty(el, "paddingRight", "padding-right"));
	if(window.isNaN(paddingHorizontal)){
		paddingHorizontal = 0;
	}
	if(width <= 0){
		width = paddingHorizontal;
	}
	//log(el+" "+(el.offsetWidth)+"  "+paddingHorizontal);
	//TODO Fazer o marginHorizontal
	return width - paddingHorizontal;
}

function datagridGetTableFromId(tableId){
	var table = document.getElementById(tableId);
	if(!table || table.tagName != 'TABLE'){
		logError('O tableId "'+tableId+'" informado não existe ou não é uma tabela.');
		throw 'O tableId "'+tableId+'" informado não existe ou não é uma tabela.';
	}
	return table;
}

function datagridUtilGetParentTag(element, targetTag){
	while(element.nodeName != targetTag){
		element = element.parentNode;
		if(!element){
			return null;
		}
	}
	return element;
}

function getStyleProperty(obj, IEStyleProp, CSSStyleProp)
{
	if (obj.currentStyle) // IE
		return obj.currentStyle[IEStyleProp];
	else if (window.getComputedStyle) // W3C
		return window.getComputedStyle(obj,"").getPropertyValue(CSSStyleProp);
	return null;
}
/* Fim Datagrid resize*/

