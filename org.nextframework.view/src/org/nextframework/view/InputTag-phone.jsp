<input type="text" id="${tag.id}" name="${tag.name}" maxlength="15" value="${tag.valueToString}" onKeyUp="mascara_telefone(this)" onKeyPress="return valida_tecla(this, event)" onchange="${tag.reloadOnChangeString}" ${tag.dynamicAttributesToString}/>