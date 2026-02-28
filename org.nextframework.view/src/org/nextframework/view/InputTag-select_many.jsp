<%@ taglib prefix="n" uri="nextframework.tags.next"%>
<select name="${tag.name}" id="${tag.id}" multiple="true" ${n:attributeNotEmpty('onchange', tag.reloadOnChangeString)} ${tag.dynamicAttributesToString}>${tag.inputComponent.selectItensString}</select>
<input type="hidden" name="_${tag.name}" value=" "/>
