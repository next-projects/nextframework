<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<input type="hidden" id="${tag.id}" name="${tag.name}" value="${tag.valueToString}" ${tag.dynamicAttributesToString} /><c:if test="${tag.write}"><span id="${tag.name}_value" ${tag.dynamicAttributesToString}>${tag.inputComponent.booleanDescriptionToString}</span></c:if>
<c:if test="${!empty tag.pattern && tag.inputComponent.dateOrTime}">
<input type="hidden" name="${tag.name}_datePattern" value="${tag.pattern}"/>
</c:if>