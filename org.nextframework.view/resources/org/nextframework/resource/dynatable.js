/**
 * IMPORTANTE: Se ao tentar chamar qualquer dessas fun??es,
 * voc? receber a seguinte mensagem de erro:
 *
 * Erro: Objeto Esperado
 *
 * Significa que a fun??o n?o chegou a ser chamada porque um dos parametros n?o existe
 */

/*
 * TODAS AS FUN??ES DE APPEND append*()
 * Consideram que a ?ltima linha da tabela ? de sum?rio!
 * A linha ser? inserida na posicao rows.length-1
 */

/**
 * Quando uma linha ? removida, essa fun??o reindexa as propriedades do form.
 * O atributo removedIndexedProperty representa a propriedade e o ?ndice que foi removido, ? uma 
 * string no formato propriedade[indice]
 * O form ? o form onde as propriedades se encontram
 * Exemplo se o removedIndexedProperty for listaAssociado[6]
 * Todas as propriedades do form que forem da listaAssociado ser?o reindexadas
 * listaAssociado[7].cdassociado -> listaAssociado[6].cdassociado
 * listaAssociado[7] -> listaAssociado[6] ... etc
 */
function reindex(form, removedIndexedProperty){
	if(form==null){
		alert("reindex(): form is null");
		return;
	}
	if(removedIndexedProperty==null){
		alert("reindex(): removedIndexedProperty is null");
		return;
	} else {
		if(!removedIndexedProperty.match("\\w*\\[\\d*\\]")){
			alert("reindex(): removedIndexedProperty is not valid ("+removedIndexedProperty+")\nCorrect pattern property[index]");
			return;
		}
	}
	//alert('reindexing '+removedIndexedProperty);
	var property = removedIndexedProperty.substring(0,removedIndexedProperty.lastIndexOf("["));
	var excludedNumber = extrairNumeroDeIndexedProperty(removedIndexedProperty);
	//alert('property '+property);
	//alert('excludedNumber '+excludedNumber);
	for(i = 0; i < form.elements.length; i++){
		var element = form.elements[i];
		if(element.name == null) continue;
		//alert(element.name + " "+(element.name.match(property+"\\[\\d*\\].*")));
		if(element.name.indexOf(property+"[") != 0){
			continue;
		}
		
		//from this point the element is to be updated
		//alert('reindex '+element.name);
		
		var openBracketsIndex = element.name.indexOf('[', property.length);
		var closeBracketsIndex = element.name.indexOf(']', property.length);
		
		var number = parseInt(element.name.substring(openBracketsIndex+1, closeBracketsIndex));
		//alert(number);
		
		if(number > excludedNumber){
			number--;
			var reindexedName = property+"["+number+"]"+ element.name.substring(closeBracketsIndex+1);
			//alert(element.name + " -> "+reindexedName);
			dt_onReindex(element, reindexedName);
			element.name = reindexedName;
			dt_afterReindex(element, reindexedName);
			//alert('after'+element.name);
		}
		
	}
}
/**
 * Remove uma linha de uma determinada tabela
 */
function removeRow(tableId, rowNumber){
	//alert('removing '+rowNumber);
	var table = document.getElementById(tableId);
	if(table==null){
		alert('removeRow(): Table with id ('+tableId+') does not exist   \n\n@author rogelgarcia');
		return;
	}
	if(new RegExp('\\d*').exec(rowNumber)==''){
		alert('removeRow(): rowNumber invalid ('+rowNumber+')   \n\n@author rogelgarcia');
		return;
	}
	var row = table.rows[rowNumber];
	dt_onDeleteRow(table, rowNumber);
	table.deleteRow(rowNumber);
	organizarCSS(table);
	dt_afterDeleteRow(table, rowNumber);
	return row;
}

/**
 *  Excluir uma linha de um botao que tenha id com formato button.excluir[table_id=?, indice=?]
 *  E reindexa
 */
function excluirLinhaPorNome(nome, ignoreMessage){
	//alert(nome);
	if(!ignoreMessage){
		if (!confirm('Tem certeza que deseja excluir este item?')) {
		   return false;
		}
	}
	

	var open = nome.lastIndexOf("[");
	var close = nome.lastIndexOf("]");
	var prop = nome.substring(open+1,close);
	var virgula = prop.lastIndexOf(",");
	var table_id = prop.substring(9,virgula);
	var indice = prop.substring(virgula+9, prop.length);
	removeRow(table_id, indice);
	reindexButtons(table_id, indice);
	
	return true;
}

/**
 * Extrai o indice de um id com formato button.excluir[table_id=?, indice=?]
 * @param nome
 * @return
 */
function extrairIndiceDeNome(nome){
	var open = nome.lastIndexOf("[");
	var close = nome.lastIndexOf("]");
	var prop = nome.substring(open+1,close);
	var virgula = prop.lastIndexOf(",");
	var table_id = prop.substring(9,virgula);
	var indice = prop.substring(virgula+9, prop.length);
	
	return indice;
}

/**
 *  Excluir uma linha de um botao que tenha id com formato button.excluir[table_id=?, indice=?]
 *  E reindexa
 */
function reindexFormPorNome(nome, form, indexedProperty, considerHeader){
	//alert(nome);
	var open = nome.lastIndexOf("[");
	var close = nome.lastIndexOf("]");
	var prop = nome.substring(open+1,close);
	var virgula = prop.lastIndexOf(",");
	var table_id = prop.substring(9,virgula);
	var indice = prop.substring(virgula+9, prop.length);
	if(considerHeader){
		indice--;
	}
	reindex(form, indexedProperty+'['+indice+']');
	reindex(form, '_'+indexedProperty+'['+indice+']');
	
}

/**
 *  Reindexa bot?es que tenham id com formato button.excluir[table_id=?, indice=?]
 */
function reindexButtons(tableId, excludedRowNumber){
	//alert('reindexando '+tableId+' - '+excludedRowNumber);
	var finished = false;
	while(!finished){
		var newId = 'button.excluir[table_id='+tableId+', indice='+excludedRowNumber+']';
		excludedRowNumber++;
		var atual = 'button.excluir[table_id='+tableId+', indice='+excludedRowNumber+']';
		var button = document.getElementById('button.excluir[table_id='+tableId+', indice='+excludedRowNumber+']');
		if(button == null){
			finished = true;
			return;
		}
		//alert(atual+' >>> '+newId);
		button.id = newId;
	}

}

/*
 * Remove uma linha da tabela e reindexa o formul?rio
 * Essa fun??o considera que a primeira linha ? de t?tulo
 */
function removeReindex(form, tableId, indexedProperty){
	if(form==null){
		alert("removeReindex(): O form fornecido ? null   \n\n@author rogelgarcia");
		return;
	}
	var table = document.getElementById(tableId);
	if(table==null){
		alert('removeReindex(): A tabela com id ('+tableId+') n?o existe   \n\n@author rogelgarcia');
		return;
	}
	removeRow(tableId,extrairNumeroDeIndexedProperty(indexedProperty)+1);
	reindex(form,indexedProperty);
}

/*
 * Fun??o de ajuda. Extrai o n?mero de algo tipo: propriedate[8]
 */
function extrairNumeroDeIndexedProperty(indexedPropery){
	var open = indexedPropery.lastIndexOf("[");
	var close = indexedPropery.lastIndexOf("]");
	var number = parseInt(indexedPropery.substring(open+1,close));
	return number;
}

/*
 * Adiciona uma linha ao final de uma tabela com linha de t?tulo e com propriedades indexadas
 * Quando a tabela posui linha de t?tulo o indice das linhas ? diferente do indice das propriedades indexadas
 * Essa fun??o considera que a ?ltima linha ? de rodap?
 */
function appendRowIndexedTitled(tableId){
	var table = document.getElementById(tableId);
	if(table==null){
		alert('appendRowIndexedTitled(): A tabela com id ('+tableId+') n?o existe   \n\n@author rogelgarcia');
		return;
	}
	var index = table.rows.length-2;
	appendRowIndexed(tableId, index);
	
}

/*
 * Adiciona uma linha ao final da tabela, com propriedades indexadas pelo indexedNumber fornecido
 * Essa fun??o considera que a ?ltima linha ? de rodap?
 */
function appendRowIndexed(tableId, indexedNumber){
	var table = document.getElementById(tableId);
	if(table==null){
		alert('appendRowIndexed(): A tabela com id ('+tableId+') n?o existe   \n\n@author rogelgarcia');
		return;
	}
	var position = table.rows.length-1;
	return createRowIndexed(tableId, indexedNumber, position);
}

/* 
 * Adiciona uma linha ao final de uma tabela sem propriedades indexadas
 * Essa fun??o considera que a ?ltima linha ? de rodap?
 */
function appendRow(tableId){
	var table = document.getElementById(tableId);
	if(table==null){
		alert('appendRow(): A tabela com id ('+tableId+') n?o existe   \n\n@author rogelgarcia');
		return;
	}
	createRow(tableId, table.length-1);
}

/*
 * Adiciona uma linha a uma tabela onde o conte?do inserido tenha propriedades indexadas
 */
function createRowIndexed(tableId, indexedNumber, position){
	var table = document.getElementById(tableId);
	if(table==null){
		alert('createRowIndexed(): A tabela com id ('+tableId+') n?o existe   \n\n@author rogelgarcia');
		return;
	}
	if(new RegExp('\\d*').exec(position)==''){
		alert('createRowIndexed(): indexedNumber inv?lido ('+indexedNumber+')   \n\n@author rogelgarcia');
		return;
	}
	if(new RegExp('\\d*').exec(position)==''){
		alert('createRowIndexed(): position inv?lido ('+position+')   \n\n@author rogelgarcia');
		return;
	}
	if(table.dataModel==null){
		var msg = 'createRowIndexed(): Voc? est? tentando inserir uma nova linha numa tabela sem dataModel\n'+
				  'dataModel ? uma propriedade da tabela. Um array com o conte?do de cada c?lula da linha que ser? incluida\n'+
				  'mytable.dataModel = [\'conteudo celula 1\',\'conteudo celula 2\',\'conteudo celula 3\']  \n\n@author rogelgarcia';
		alert(msg);
		return;
	} 
	if(table.tdClassModel==null){
		var msg = 'createRowIndexed(): Voc? est? tentando inserir uma nova linha numa tabela sem tdClassModel\n'+
				  'tdClassModel ? uma propriedade da tabela. Um array com o nome da classe de CSS de cada c?lula da linha que ser? incluida\n'+
				  'mytable.tdClassModel = [\'classe1\',\'classe2\',\'classe3\']  \n\n@author rogelgarcia';
		alert(msg);
		return;
	} 
	if(table.trClassModel==null){
		var msg = 'createRowIndexed(): Voc? est? tentando inserir uma nova linha numa tabela sem trClassModel\n'+
				  'trClassModel ? uma propriedade da tabela. Um array com o nome da classe de CSS de cada linha que ser? incluida\n'+
				  'mytable.trClassModel = [\'classe1\',\'classe2\',\'classe3\']  \n\n@author rogelgarcia';
		alert(msg);
		return;
	} 
	if(table.indexName==null){
		var msg = 'createRowIndexed(): Voc? est? tentando inserir uma nova linha numa tabela sem indexName\n'+
				  'indexName ? uma propriedade da tabela do tipo string.\n'+
				  'Toda ocorrencia de indexName no dataModel ser? substituida pelo ?ndice que ser? inserido.\n'+
				  'mytable.indexName = \'{index}\' \n\n@author rogelgarcia';
		alert(msg);
		return;
	}
	if(table.indexPlusName==null){
		var msg = 'createRowIndexed(): Voc? est? tentando inserir uma nova linha numa tabela sem indexPlusName\n'+
				  'indexPlusName ? uma propriedade da tabela do tipo string.\n'+
				  'Toda ocorrencia de indexPlusName no dataModel ser? substituida pelo ?ndice que ser? inserido +1.\n'+
				  'mytable.indexNamePlus = \'{indexplus}\' \n\n@author rogelgarcia';
		alert(msg);
		return;
	} 
	if(table.indexSequenceName==null){
		var msg = 'createRowIndexed(): Voc? est? tentando inserir uma nova linha numa tabela sem indexSequenceName\n'+
				  'indexSequenceName ? uma propriedade da tabela do tipo string.\n'+
				  'Toda ocorrencia de indexSequenceName no dataModel ser? substituida pelo ?ndice que ser? inserido +1.\n'+
				  'mytable.indexSequenceName = \'{indexSequence}\' \n\n@author rogelgarcia';
		alert(msg);
		return;
	} 
	
	if(table.indexSequence == null){
		table.indexSequence = 1;
	} else {
		table.indexSequence++;
	}
	var newRow;
	if(table.tBodies[0]){
		//alert(table.tBodies[0]);
		newRow = table.tBodies[0].insertRow(position-1);
	} else {
		newRow = table.insertRow(position);
	}				
	for(var i = 0; i < table.dataModel.length; i++){
		var newData = newRow.insertCell(i);
		var cont = table.dataModel[i];
		while(cont.indexOf(table.indexName)>=0){
			cont = cont.replace(table.indexName,indexedNumber);
		}
		while(cont.indexOf(table.indexPlusName)>=0){
			cont = cont.replace(table.indexPlusName,indexedNumber+1);
		}
		while(cont.indexOf(table.indexSequenceName)>=0){
			cont = cont.replace(table.indexSequenceName, "SQ_"+(table.indexSequence));
		}
		newData.className = table.tdClassModel[i];
		aplicaHTML(cont, newData);
	}
	
	organizarCSS(table);
	dt_onCreateNewRow(newRow, indexedNumber, position);
	return newRow;
}

function organizarCSS(table){
	//organizar CSS das linhas
	//i comeca de 1 para ignorar a linha de titulo
	for(var i = 1; i < table.rows.length; i++){
		var row = table.rows[i];
		//alert(row.className);
		//alert((i-1)%table.trClassModel.length);
		row.className = table.trClassModel[(i-1)%table.trClassModel.length];//currentClassName(table, currentClass);
		//alert(row.className);
	}
}

/**
 * 
 */
function moveRowUp(tableId, tableRowIndex){
	var table = document.getElementById(tableId);
	if(tableRowIndex == 0){
		throw "cannot move row 0 up";
	}
	if(tableRowIndex == table.rows.length){
		throw "table row does not exist "+index;
	}
	var rowA = table.rows[tableRowIndex - 1];
	var rowB = table.rows[tableRowIndex];
	
	rowA.parentNode.insertBefore(rowB, rowA);
	
	var classNameA = rowA.className;
	var classNameB = rowB.className;
	rowA.className = classNameB;
	rowB.className = classNameA;
	
	var getInputs = function(element){
		var result = new Array();
		for(var i = 0; i < element.childNodes.length; i++){
			if(element.childNodes[i].tagName == 'INPUT' || element.childNodes[i].tagName == 'SELECT'){
				result.push(element.childNodes[i]);
			}
			var subResult = getInputs(element.childNodes[i]);
			for(var j = 0; j < subResult.length; j++){
				result.push(subResult[j]);
			}
		}
		return result;
	};
	
	var rowAElements = getInputs(rowA);
	var rowBElements = getInputs(rowB);
	
	var reindexRowElements = function(elements, newIndex){
		var reindexName = function(name, idx){
			var openB = name.lastIndexOf("[");
			var closeB = name.lastIndexOf("]");
			name = name.substring(0, openB+1) + idx + name.substring(closeB, name.length);
			return name;
		};
		for(var i = 0; i < elements.length; i++){
			elements[i].name = reindexName(elements[i].name, newIndex);
		}
	};
	
	reindexRowElements(rowAElements, tableRowIndex-1);
	reindexRowElements(rowBElements, tableRowIndex-2);
	
	if(table.indexProperty){
		var resetIndex = function(elements, value){
			for(var i = 0; i < elements.length; i++){
				if(elements[i].name.indexOf(table.indexProperty) > 0){
					var idx = parseInt(elements[i].value);
					if(idx){
						elements[i].value = idx + value;
					}
				}
			}
		};
		
		resetIndex(rowAElements, + 1);
		resetIndex(rowBElements, - 1);
	}
}


/**
 * Cria uma linha em determinada tabela
 */
function createRow(tableId, position){
	var table = document.getElementById(tableId);
	if(table==null){
		alert('createRow(): A tabela com id ('+tableId+') nao existe   \n\n@author rogelgarcia');
		return;
	}
	if(new RegExp('\\d*').exec(position)==''){
		alert('createRow(): position invalido ('+position+')   \n\n@author rogelgarcia');
		return;
	}
	if(table.dataModel==null){
		var msg = 'createRow(): Voce esta tentando inserir uma nova linha numa tabela sem dataModel\n'+
				  'dataModel eh uma propriedade da tabela. Um array com o conteudo de cada celula da linha que sera incluida\n'+
				  'mytable.dataModel = [\'conteudo celula 1\',\'conteudo celula 2\',\'conteudo celula 3\']  \n\n@author rogelgarcia';
		alert(msg);
		return;
	} 
	if(table.tdClassModel==null){
		var msg = 'createRow(): Voce esta tentando inserir uma nova linha numa tabela sem tdClassModel\n'+
				  'tdClassModel eh uma propriedade da tabela. Um array com o nome da classe de CSS de cada celula da linha que sera incluida\n'+
				  'mytable.tdClassModel = [\'classe1\',\'classe2\',\'classe3\']  \n\n@author rogelgarcia';
		alert(msg);
		return;
	} 
	var newRow = table.insertRow(position);
	for(var i = 0; i < table.dataModel.length; i++){
		var newData = newRow.insertCell(i);
		var cont = table.dataModel[i];
		newData.className = table.tdClassModel[i];
		aplicaHTML(cont, newData);
	}
	organizarCSS(table);	
	
	dt_onCreateNewRow(newRow, null, position);
	
	return newRow;
}

function newRow(table){
	var rowCount = table.tBodies[0].rows.length + 1;
    return createRowIndexed(table.id, rowCount-1, rowCount);//check if there are headers
}

function deleteRow(el, table){
	var formEl = next.dom.getParentTag(el, 'form');
	var row = next.dom.getParentTag(el, 'tr');
	if(!next.util.isDefined(table)){
		table = next.dom.getParentTag(el, 'table');
	}
	return deleteRowByIndex(table, row.rowIndex - 1);//check if there are headers
}

function deleteRowByIndex(table, index){
	var formEl = next.dom.getParentTag(table, 'form');
	
	//excluirLinhaPorNome
	var row = removeRow(table.id, index + 1); //check if there are headers 
	reindexButtons(table.id, index + 1);
	
	//reindexFormPorNome
	reindex(formEl, table.indexedProperty+'['+index+']');
	reindex(formEl, '_'+table.indexedProperty+'['+index+']');
	
	return row;
}