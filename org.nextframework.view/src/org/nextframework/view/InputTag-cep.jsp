<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<input type="text" id="${tag.id}" maxlength="9" size="9" name="${tag.name}" value="${tag.valueToString}" onKeyUp="mascara_cep(this);${tag.onKeyUp}" onKeyPress="if(valida_tecla(this, event)){${tag.onKeyPress};} else {return false;}" ${n:attributeNotEmpty('onchange', tag.reloadOnChangeString)} ${tag.dynamicAttributesToString}/>