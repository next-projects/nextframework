<input type="text" id="${tag.id}" maxlength="9" size="9" name="${tag.name}" value="${tag.valueToString}" onKeyUp="mascara_cep(this);${tag.onKeyUp}" onKeyPress="if(valida_tecla(this, event)){${tag.onKeyPress};} else {return false;}" onchange="${tag.reloadOnChangeString}" ${tag.dynamicAttributesToString}/>