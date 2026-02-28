<%@ taglib prefix="n" uri="nextframework.tags.next"%>
<!-- Por segurança o valor do password não é enviado (TODO: FAZER ESSE COMPORTAMENTO)-->
<input type="password" id="${tag.id}" name="${tag.name}" value="${tag.value}" ${n:attributeNotEmpty('onchange', tag.reloadOnChangeString)} ${tag.dynamicAttributesToString}/>