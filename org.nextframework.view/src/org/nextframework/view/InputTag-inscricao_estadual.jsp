<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<input type="text" id="${tag.id}" name="${tag.name}" value="${tag.valueToString}" onKeyUp="mascara_inscricaoestadual(this)" onKeyPress="return valida_tecla(this, event)" ${n:attributeNotEmpty('onchange', tag.reloadOnChangeString)} ${tag.dynamicAttributesToString}/>