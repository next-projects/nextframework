newLine${tag.id} = newLine${tag.idCapitalized} = function(){
    var table = document.getElementById('${tag.id}');
    return newRow(table);
}

deleteLine${tag.id} = deleteLine${tag.idCapitalized} = function(obj){
    var table = document.getElementById('${tag.id}');
    return deleteRow(obj, table); 
}