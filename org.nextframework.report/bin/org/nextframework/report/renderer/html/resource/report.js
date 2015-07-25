
TableVisitor = function(table){
	this.table = table;
};
TableVisitor.prototype.visitRows = function(f){
	var rows = this.table.rows;
	for (var i = 0; i < rows.length; i++){
		var row = rows[i];
		f(row, i, i > 0? rows[i-1]:null);
	}
};

function enableReport(id){
	var reportDiv = document.getElementById(id);

	var table = reportDiv.getElementsByTagName('TABLE')[0];

	var visitor = new TableVisitor(table);

	visitor.visitRows(configurePaddings);

	visitor.visitRows(installPlusSign);

	configureTree(table);
	
	
	visitor.visitRows(function(row){
		if(row.reportChildren){
			closeRow(row);
		}
	});
}

function openCloseRow(){
	if(this.opened){
		closeRow(this);
	} else {
		openRow(this);
	}
}
function openRow(row){
	row.opened = true;
	for(var i = 0; i < row.reportChildren.length; i++){
		var child = row.reportChildren[i];
		child.style.display = '';
		if(child.showChildren){
			child.showChildren();
		}
	}
}

function closeRow(row){
	row.opened = false;
	for(var i = 0; i < row.reportChildren.length; i++){
		var child = row.reportChildren[i];
		child.style.display = 'none';
		if(child.hideChildren){
			child.hideChildren();
		}
	}
}

function installTreeTop(row){
	if(!row.reportChildren){
		row.reportChildren = new Array();
		next.events.attachEvent(row, 'click', openCloseRow);

		row.hideChildren = function(){
			if(row.opened)
			for(var i = 0; i < row.reportChildren.length; i++){
				var child = row.reportChildren[i];
				child.style.display = 'none';

				if(child.hideChildren && child.opened){
					child.hideChildren();
				}
			}
		}

		row.showChildren = function(){
			if(row.opened)
			for(var i = 0; i < row.reportChildren.length; i++){
				var child = row.reportChildren[i];
				child.style.display = '';

				if(child.showChildren && child.opened){
					child.showChildren();
				}
			}
		}
	}
}

function configureTree(table){
	var rows = table.rows;
	for (var i = 0; i < rows.length; i++){
		var row = rows[i];
		if(i > 0){
			var inner = true;
			for(var j = i-1; j >=0 ; j--){
				var previousRow = rows[j];
				if(getDepth(previousRow) < getDepth(row)){
					installTreeTop(previousRow);
					previousRow.reportChildren.push(row);
					break;
				}
			}

		}
	}
}

function installPlusSign(row, i, previousRow){
	if(row.className.indexOf('DETAIL') >= 0 && row.className.indexOf('DETAIL_HEADER') < 0  &&
			row.className.indexOf('SUMARY') < 0){
		next.events.attachEvent(row, 'mouseover', function(){
			this.style.backgroundColor = '#FFFFDD';
		});
		next.events.attachEvent(row, 'mouseout', function(){
			this.style.backgroundColor = '';
		});
	}
	if(previousRow != null && getDepth(previousRow) < getDepth(row)){
		var plusSign = document.createElement('div');
		plusSign.style.cssFloat = 'left';
		plusSign.style.float = 'left';
		plusSign.style.position = 'relative';
		//plusSign.style.paddingTop = '2px';
		plusSign.innerHTML = '<img src="'+next.http.getApplicationContext() + '/resource/report/mais.gif'+'" style="padding-left: 5px; padding-right: 5px;">';
		plusSign.id = 'treesign';
		
		next.events.attachEvent(previousRow, 'click', alternateSign);

		//previousRow.firstChild.firstChild.style.cssFloat = 'left';
		//previousRow.firstChild.firstChild.style.float = 'left';
		
		previousRow.firstChild.insertBefore(plusSign, previousRow.firstChild.firstChild);

		previousRow.opened = true;

	}
}

function configurePaddings(row){
	var depth = getDepth(row);
	row.firstChild.style.paddingLeft = ((depth-1)*10) + 'px';
}

function getDepth(row){
	if(!row.getAttribute('hierarchydepth')){
		return 1;
	}
	return parseInt(row.getAttribute('hierarchydepth'));
}

function getHierarchyString(row){
	return row.getAttribute('hierarchy');
}

function alternateSign(){
	var row = this;
	var img = this.firstChild.firstChild.firstChild;
	if(img.src.indexOf('mais') > 0){
		img.src = next.http.getApplicationContext() + '/resource/report/menos.gif';
	} else {
		img.src = next.http.getApplicationContext() + '/resource/report/mais.gif';
	}
}