<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
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