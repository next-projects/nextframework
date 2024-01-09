<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<select name="${tag.name}" id="${tag.id}" multiple="true" ${n:attributeNotEmpty('onchange', tag.reloadOnChangeString)} ${tag.dynamicAttributesToString}>${tag.inputComponent.selectItensString}</select>
<input type="hidden" name="_${tag.name}" value=" "/>
