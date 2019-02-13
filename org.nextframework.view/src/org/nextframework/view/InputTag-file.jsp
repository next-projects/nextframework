<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<input type="file" id="${tag.id}" name="${tag.name}" onchange = "${tag.inputComponent.fileOnChange}" value="${tag.inputComponent.fileValue}" ${tag.dynamicAttributesToString}/> 
${tag.inputComponent.fileLink}
<input type="hidden" name="${tag.name}_excludeField" id="${tag.name}_excludeField">
<c:if test="${tag.showDeleteButton}">
	<button id="${tag.name}_removerbtn" type="button" onclick="document.getElementById('${tag.name}_excludeField').value='true'; document.getElementById('${tag.name}_div').style.textDecoration = 'line-through'" ${tag.inputComponent.showRemoverBtn}>remover</button>
</c:if>
<c:catch>
<c:if test="${tag.value.cdfile < 0}">
	<input type="hidden" name="${tag.name}_tempField" id="${tag.name}_tempField" value="_tempFileObject${tag.value.cdfile}.next">
</c:if>
<c:if test="${tag.value.cdfile > 0}">
	<input type="hidden" name="${tag.name}_fileObject" id="${tag.name}_fileObject" value="${tag.valueClass.name}[cdfile=${tag.value.cdfile};name=${tag.value.name};size=0]">
</c:if>
</c:catch>