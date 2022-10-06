<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:if test="${useBootstrap}">
	<div class="input-group flex-nowrap">
		<c:if test="${empty tag.dynamicAttributesMap['showlink'] || tag.dynamicAttributesMap['showlink'] == 'true' }">
			<div class="input-group-text w-50 text-nowrap text-truncate p-0" title="${tag.inputComponent.fileName}">${tag.inputComponent.fileLink}</div>
		</c:if>
		<c:if test="${tag.showDeleteButton}">
			<div id="${tag.name}_removerbtn" onclick="document.getElementById('${tag.name}_excludeField').value='true'; try{document.getElementById('${tag.name}_div').style.textDecoration = 'line-through'}catch(e){this.style.display='none'}" ${tag.inputComponent.showRemoverBtn} class="input-group-text" style="cursor:pointer;">${tag.inputComponent.removerLabel}</div>
		</c:if>
		<input type="file" id="${tag.id}" name="${tag.name}" onchange="try{${tag.inputComponent.fileOnChange}}catch(e){}" value="${tag.inputComponent.fileValue}" ${tag.dynamicAttributesToString} />
	</div>
</c:if>
<c:if test="${!useBootstrap}">
	<input type="file" id="${tag.id}" name="${tag.name}" onchange="try{${tag.inputComponent.fileOnChange}}catch(e){}" value="${tag.inputComponent.fileValue}" ${tag.dynamicAttributesToString} />
	<c:if test="${empty tag.dynamicAttributesMap['showlink'] || tag.dynamicAttributesMap['showlink'] == 'true' }">
		${tag.inputComponent.fileLink}
	</c:if>
	<c:if test="${tag.showDeleteButton}">
		<button id="${tag.name}_removerbtn" type="button" onclick="document.getElementById('${tag.name}_excludeField').value='true'; try{document.getElementById('${tag.name}_div').style.textDecoration = 'line-through'}catch(e){this.style.display='none'}" ${tag.inputComponent.showRemoverBtn}>${tag.inputComponent.removerLabel}</button>
	</c:if>
</c:if>

<input type="hidden" name="${tag.name}_excludeField" id="${tag.name}_excludeField">
<c:catch>
	<c:if test="${tag.value.cdfile < 0}">
		<input type="hidden" name="${tag.name}_tempField" id="${tag.name}_tempField" value="_tempFileObject${tag.value.cdfile}.next">
	</c:if>
	<c:if test="${tag.value.cdfile > 0}">
		<input type="hidden" name="${tag.name}_fileObject" id="${tag.name}_fileObject" value="${tag.value.class.name}[cdfile=${tag.value.cdfile};name=${tag.value.name};size=0]">
	</c:if>
</c:catch>