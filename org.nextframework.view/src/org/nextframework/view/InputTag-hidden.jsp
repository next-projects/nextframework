<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<input type="hidden" id="${tag.id}" name="${tag.name}" value="${tag.valueToString}" ${tag.dynamicAttributesToString} /><c:if test="${tag.write}"><div class="form-control-plaintext"><span id="${tag.name}_value" ${tag.dynamicAttributesToString}>${tag.inputComponent.booleanDescriptionToString}</span></div></c:if>
<c:if test="${tag.inputComponent.dateOrTime && !empty tag.pattern && (empty tag.dynamicAttributesMap['disabled'] || tag.dynamicAttributesMap['disabled'] == 'false') }">
	<input type="hidden" name="${tag.name}_datePattern" value="${tag.pattern}"/>
</c:if>