<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:if test="${useBootstrap}">
	<div class="input-group flex-nowrap">
</c:if>
<c:if test="${!useBootstrap}">
	<div class="select_many_popup_container">
</c:if>
	<input id="${tag.id}_labels" type="text" name="${tag.name}_labels" value="" class="select_many_popup_labels ${tag.dynamicAttributesMap['class']}" readonly="readonly" ${tag.dynamicAttributesToString} />
	<select id="${tag.id}" name="${tag.name}" onchange="${tag.reloadOnChangeString}" multiple="multiple" style="display:none">${tag.inputComponent.selectItensString}</select>
	<input type="hidden" name="_${tag.name}" value=" "/>
	<c:set var="aberto" value="${(empty tag.dynamicAttributesMap['disabled'] || tag.dynamicAttributesMap['disabled'] == 'false') && (empty tag.dynamicAttributesMap['readonly'] || tag.dynamicAttributesMap['readonly'] == 'false')}" />
	<c:if test="${aberto}">
		<c:if test="${useBootstrap}">
			<i id="${tag.id}_trigger" class="bi bi-three-dots input-group-text" style="cursor:pointer;"></i>
		</c:if>
		<c:if test="${!useBootstrap}">
			<button id="${tag.id}_trigger" class="select_many_popup_button" >...</button>
		</c:if>
	</c:if>
</div>
<script type="text/javascript">
	SelectManyPopup.install('${tag.name}', '${tag.inputComponent.styleString}');
</script>