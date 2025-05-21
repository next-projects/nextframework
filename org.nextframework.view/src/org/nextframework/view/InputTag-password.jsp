<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<!-- Por segurança o valor do password não é enviado (TODO: FAZER ESSE COMPORTAMENTO)-->
<input type="password" id="${tag.id}" name="${tag.name}" value="${tag.value}" ${n:attributeNotEmpty('onchange', tag.reloadOnChangeString)} ${tag.dynamicAttributesToString}/>