<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<!-- Por seguran�a o valor do password n�o � enviado (TODO: FAZER ESSE COMPORTAMENTO)-->
<input type="password" id="${tag.id}" name="${tag.name}" value="${tag.value}" ${n:attributeNotEmpty('onchange', tag.reloadOnChangeString)} ${tag.dynamicAttributesToString}/>