<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<textarea id="${tag.id}" name="${tag.name}" cols="${tag.cols}" rows="${tag.rows}" ${n:attributeNotEmpty('onchange', tag.reloadOnChangeString)} ${tag.dynamicAttributesToString}>${tag.valueToString}</textarea>
<script language="javascript1.2">editor_generate('${tag.name}');</script>