<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="n" uri="nextframework.tags.next"%>
<input id="${tag.id}" name="${tag.name}" type="text" value="${tag.valueToString}" ${n:attributeNotEmpty('onchange', tag.reloadOnChangeString)} ${tag.dynamicAttributesToString}/>
<c:if test="${(empty tag.dynamicAttributesMap['disabled'] || tag.dynamicAttributesMap['disabled'] == 'false') && !empty tag.pattern}">
	<script type="text/javascript">
		IMask(document.getElementById("${tag.id}"),
				{
					mask: '${tag.pattern}',
					definitions: getMaskFormatter2IMaskDefinitions()
				}
		);
	</script>
</c:if>