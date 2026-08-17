<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<c:if test="${useBootstrap}">
	<div class="input-group flex-nowrap">
		<c:if test="${empty tag.dynamicAttributesMap['showlink'] || tag.dynamicAttributesMap['showlink'] == 'true' }">
			<div class="input-group-text w-50 text-nowrap text-truncate" title="${tag.inputComponent.fileName}">${tag.inputComponent.fileLink}</div>
		</c:if>
		<c:if test="${tag.showDeleteButton}">
			<div id="${tag.name}_removerbtn" onclick="document.getElementById('${tag.inputComponent.excludeFieldFieldId}').value='true'; try{document.getElementById('${tag.name}_div').style.textDecoration = 'line-through'}catch(e){this.style.display='none'}; try{document.getElementById('${tag.inputComponent.tempFileTokenFieldId}').value='';}catch(e){}; try{document.getElementById('${tag.inputComponent.fileObjectFieldId}').value='';}catch(e){}; try{clearAjaxUploadField(document.getElementById('${tag.id}'));}catch(e){};" ${tag.inputComponent.showRemoverBtn} class="input-group-text" style="cursor:pointer;">${tag.inputComponent.removerLabel}</div>
		</c:if>
		<div class="input-group-text p-0"></div>
		<input type="file" id="${tag.id}" name="${tag.name}" onchange="try{${tag.inputComponent.fileOnChange}}catch(e){}" data-ajax-upload="${tag.ajaxUpload}" data-temp-file-token-field="${tag.inputComponent.tempFileTokenFieldName}" ${tag.dynamicAttributesToString} />
	</div>
</c:if>
<c:if test="${!useBootstrap}">
	<input type="file" id="${tag.id}" name="${tag.name}" onchange="try{${tag.inputComponent.fileOnChange}}catch(e){}" data-ajax-upload="${tag.ajaxUpload}" data-temp-file-token-field="${tag.inputComponent.tempFileTokenFieldName}" ${tag.dynamicAttributesToString} />
	<c:if test="${empty tag.dynamicAttributesMap['showlink'] || tag.dynamicAttributesMap['showlink'] == 'true' }">
		${tag.inputComponent.fileLink}
	</c:if>
	<c:if test="${tag.showDeleteButton}">
		<button id="${tag.name}_removerbtn" type="button" onclick="document.getElementById('${tag.inputComponent.excludeFieldFieldId}').value='true'; try{document.getElementById('${tag.name}_div').style.textDecoration = 'line-through'}catch(e){this.style.display='none'}; try{document.getElementById('${tag.inputComponent.tempFileTokenFieldId}').value='';}catch(e){}; try{document.getElementById('${tag.inputComponent.fileObjectFieldId}').value='';}catch(e){}; try{clearAjaxUploadField(document.getElementById('${tag.id}'));}catch(e){};" ${tag.inputComponent.showRemoverBtn}>${tag.inputComponent.removerLabel}</button>
	</c:if>
</c:if>
<input type="hidden" name="${tag.inputComponent.excludeFieldFieldName}" id="${tag.inputComponent.excludeFieldFieldId}">
<input type="hidden" name="${tag.inputComponent.tempFileTokenFieldName}" id="${tag.inputComponent.tempFileTokenFieldId}" value="${tag.inputComponent.tempFileTokenValue}">
<c:catch>
	<c:if test="${tag.value.cdfile > 0}">
		<input type="hidden" name="${tag.inputComponent.fileObjectFieldName}" id="${tag.inputComponent.fileObjectFieldId}" value="${tag.value.getClass().name}[cdfile=${tag.value.cdfile};name=${tag.value.name};size=0]">
	</c:if>
</c:catch>
<c:if test="${tag.ajaxUpload}">
	<input type="hidden" name="${tag.inputComponent.fileClassNameFieldName}" id="${tag.inputComponent.fileClassNameFieldId}" value="${tag.inputComponent.fileClassNameValue}" disabled="disabled">
</c:if>
