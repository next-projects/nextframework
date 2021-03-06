<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!-- Valor selecionado ${tag.valueToString} -->
<div class="select_many_popup_container">
	<input type="text" name="${tag.name}_labels" value="" class="select_many_popup_labels" readonly="readonly" ${tag.dynamicAttributesToString}/>
	<c:if test="${empty tag.dynamicAttributesMap['disabled'] || tag.dynamicAttributesMap['disabled'] == 'false'}">
		<button class="select_many_popup_button">...</button>
	</c:if>
	<div id="${tag.name}_value" class="select_many_popup_values" style="display:none">
		<!-- Valor selecionado ${tag.valueToString} -->
		<select name="${tag.name}" id="${tag.id}" onchange="${tag.reloadOnChangeString}" multiple="multiple">${tag.inputComponent.selectItensString}</select>
		<input type="hidden" name="_${tag.name}" value=" "/>
	</div>
	<c:if test="${empty tag.dynamicAttributesMap['disabled'] || tag.dynamicAttributesMap['disabled'] == 'false'}">
		<script type="text/javascript">
			SelectManyPopup.install('${tag.name}', '${tag.inputComponent.styleString}');
		</script>
	</c:if>
</div>