/*
ALGORITMO TREETABLE - DOCUMENTACAO
Os treeTable são tablesHTML extendidos. É necessário fazer a instalação do TreeTable em algum table do HTML.
O table obrigatoriamente deve ter um id (NÃO REPETIDO NA PÁGINA).
Ex.:
installTreeTable(tableId);
*/

/*
Exemplo de uso de tree table ou tree view (treeview é na verdade um treetable com uma coluna):

Cada linha do treetable contém um node.. cada node contém vários columns que representam cada coluna no treetable

Para criar um node faça o seguinte:
var node = new Node(id); //onde id pode ser um string que represente o nó na aplicacao (a api do treetable nao utiliza essa informacao)

Um node possui vários column que representam o td no treetable
Para criar um column faça o seguinte:
var column = node.newColumn();

A primeira column de um node pode utilizar um ícone.. para configurar o ícone utilize
column.icon = 'url';

O conteúdo do column é setado através da propriedade innerHTML
column.innerHTML = 'conteudo';

Atributos do column serão copiados para o td respectivo. É possível utilizar por exemplo:
column.width = '50';

Tanbém é possível setar os estilos:
column.style.backGroundColor = 'red';

Se um node tiver filhos, configure o node dessa forma (isso fará com que apareca um '+')
node.hasChild = true;

Para configurar uma table para ser um treeTable faça o seguinte (uma table html deve ser configurada para ser um treeTable)
var treeTable = installTreeTable('idDoTable', 'prefixo'); //prefixo é a url base onde ficam os arquivos do treetable

Para adicionar um node a tabela faça o seguinte:
treeTable.addNode(node);

Para adicionar um node filho de outro:
node.addChild(child);

IMPORTANTE: Ao utilizar o addChild o node pai já deve ter sido adicionado ao treeTable


 */

//CLASSES
function Node(id){
	if(id == null){
		exception('Não é possível criar um node com id nulo');
	}
	this.hasChild = false;
	this.id = id;
	this.level = 0;
	this.childSize = 0; // numero de filhos totais (incluindo filhos de filhos)
	this.columns = new Array();
	var columns = this.columns;
	this.newColumn = function(){
		var column = new Object();
		column.style = new Object();
		columns.push(column);
		return column;
	};
	var parent = this;
	this.addChild = function(node){
		nodeAddChild(parent, node);
	};
	this.children = new Array();
	this.currentIndex = 0;
}

function nodeAddChild(parent, child){
	if(child == null){
		exception('Erro ao adicionar child. child é nulo parent.id = '+parent.id);
	}
	parent.children.push(child);
	child.parent = parent;
	child.level = parent.level + 1;
	child.table = parent.table;
	var rowid = parent.row.id+'.'+ (parent.currentIndex++);

	/////
	var table = parent.table;


	var rownumber = parent.row.rowIndex + parent.childSize + 1;
	//alert(rowid+ '   '+rownumber+ '   parent.childSize '+parent.childSize);
	
	var row = table.insertRow(rownumber);
	
	var superparent = parent;
	while(superparent != null){
		superparent.childSize++;
		superparent = superparent.parent;
	}

	//row id 1 1.1...etc
	row.id = rowid;
	child.row = row;
	row.node = child;
	var cells = createCells(table, row, child);
	configCells(table, cells, child, rowid);

	table.nodeOpened[child.row.id] = false;
	row.style.display = 'none';
	table.configRow(row);
	
	table.allNodes.push(child);
	table.onaddnode(child, table);
}
//CLASSES END

function exception(ex){
	alert(ex); 
	throw (ex);
}
function installTreeTable(tableId, prefix){
	var table = document.getElementById(tableId);
	table.currentIndex = 0;
	table.nodeOpened = new Array();
	if(table == null){
		exception('Nenhuma tabela com o id \''+tableId+'\' foi encontrada. Não foi possível criar o TreeTable');
	}
	table.isTreeTable = function (){return true;};
	table.startRowIndex = table.rows.length;
	if(table.startRowIndex > 0){
		table.columnCount = table.rows[0].cells.length;
	} else {
		table.columnCount = -1;
	}
	table.addNode = function(node){
		treeTableAddNode(table, node);
	};
	table.openNode = function(node){
		treeTableOpenNode(table, node);
	};
	table.onaddnode = function(){};
	table.onremovenode = function(){};
	table.englobarConteudoTd = function(conteudo, td, column, node){return '<span style="vertical-align:3; padding-left:3">'+conteudo+'</span>'};
	if(prefix == null){
		prefix = '';
	}
	table.getNode = function(nodeid){
		return treeTableGetNode(table, nodeid);
	};
	table.configRow = function(row){
		defaultConfigRow(row);
	};
	
	table.allNodes = new Array();
	table.urlPrefix = prefix;
	return table;
}

function treeTableGetNode(table, nodeid){
	for(var i = 0; i < table.allNodes.length; i++){
		var node = table.allNodes[i];		
		if(node.id == nodeid){
			return node;
		}
	}
	return null;
}
function treeTableAddNode(table, node){
	if(node == null){
		exception('Erro ao adicionar node. node é nulo');
	}
	node.level = 0;
	node.table = table;				
	var rownumber = table.rows.length;

	var row = table.insertRow(rownumber);

	//row id 1 1.1...etc
	var rowid = 'row_'+table.id +'_'+ (table.currentIndex++);
	row.id = rowid;
	node.row = row;
	row.node = node;
	var cells = createCells(table, row, node);
	configCells(table, cells, node, rowid);
	table.configRow(row);
	table.allNodes.push(node);
	table.onaddnode(node, table);
}

function defaultConfigRow(row){
	row.onmouseover = function (){row.style.backgroundColor = '#f3f3f3';}; 
	row.onmouseout  = function (){row.style.backgroundColor = '';}; 				
}

function treeTableOpenNodeWithId(tableid, nodeid){
	var table = document.getElementById(tableid);
	var node = table.getNode(nodeid);
	treeTableOpenNode(table, node);
}

function treeTableOpenNode(table, node){
	var img = document.getElementById(node.row.id+'_sign');
	img.src = table.urlPrefix + 'menos.gif';
	table.nodeOpened[node.row.id] = true;
	show(table, node.children);
	if(node.onopennode){
		node.onopennode(node, table);
	}
	if(node.children.length == 0){
		setTimeout('treeTableOpenNodeWithId(\''+table.id+'\',\''+node.id+'\')', 800);
	}
	//showNodes(table);
}

function showNodes(table){
	var div = document.getElementById('nodes');
	div.innerHTML = '...';
	for(x in table.nodeOpened){
		div.innerHTML+='<BR>'+ x + '  =  ' + table.nodeOpened[x];
	}
}

function show(table, children){
	for(var i = 0; i < children.length; i++){
		var child = children[i];
		var row = child.row;
		row.style.display='';
		if(child.children.length > 0 && table.nodeOpened[child.row.id]){
			show(table, child.children);
		}
	}
}

function treeTableCloseNode(table, node){
	var img = document.getElementById(node.row.id+'_sign');
	img.src = table.urlPrefix + 'mais.gif';
	table.nodeOpened[node.row.id] = false;
	hide(node.children);
	if(node.onclosenode){
		node.onclosenode(node, table);
	}
	//showNodes(table);
}

function hide(children){
	for(var i = 0; i < children.length; i++){
		var child = children[i];
		var row = child.row;
		row.style.display='none';
		hide(child.children);
	}
}

function treeTableChangeState(tableid, rowid, event){
	var row = document.getElementById(rowid);
	var node = row.node;
	var table = document.getElementById(tableid);
	treeTableChangeStateByObjects(table, node, event);
}

function treeTableChangeStateByObjects(table, node, event){
	//alert(node.id);
	if(event && event.cancel){
		return;
	}
	if(table.nodeOpened[node.row.id]){
		treeTableCloseNode(table, node);					
	} else {
		treeTableOpenNode(table, node);
	}
}


///auxiliar
function configCells(table, cells, node, rowid){
	for(var i = 0; i < cells.length; i++){
		if(node.columns.length > i){
			var column = node.columns[i];
			var plussign = '';
			if(i == 0 && node.hasChild){
				plussign = '<img src="'+table.urlPrefix + 'mais.gif" id="'+rowid+'_sign" style="float:left"/>';
				cells[i].style.paddingLeft = node.level * 15 + node.level + 'px';
				var onselect = column.onselect;
				cells[i].onclick = function(event){treeTableChangeStateByObjects(table, node, event); if(onselect){onselect(node);}};
			} else {
				var onselect = column.onselect;
				if(onselect){
					cells[i].onclick = function(){onselect(node)};
				}
			}
			if(i == 0 && !node.hasChild){
				plussign = '<img src="'+table.urlPrefix + 'empty.gif" style="float:left"/>';
				cells[i].style.paddingLeft = node.level * 15 + node.level;
			}
			if(column.innerHTML){
				
				var innerHTML;
				if(i == 0){
					innerHTML = table.englobarConteudoTd(column.innerHTML, cells[i], column, node);				
				} else {
					innerHTML = column.innerHTML;
				}

				if(column.icon){
					innerHTML = '<img style="align:top" src="'+table.urlPrefix + column.icon+'"/>' + innerHTML;
				}
				innerHTML = plussign + innerHTML;
				cells[i].innerHTML = innerHTML;
			}
			for(attr in column){
				if(attr != 'icon' && attr != 'innerHTML' && attr != 'style' && attr != 'onselect'){
					cells[i][attr] = column[attr];
				} else if(attr == 'style'){
					var styles = column[attr];
					for(style in styles){
						cells[i].style[style] = column.style[style];
					}
				}
			}
		}
	}
}


function createCells(table, row, node){
	var cells = new Array();
	if(node.columns.length >= 0){
		for(var i = 0; i < node.columns.length; i++){
			var cell = row.insertCell(i);
			cells.push(cell);
			cell.innerHTML = '&nbsp;';
		}
	}
	return cells;
}
