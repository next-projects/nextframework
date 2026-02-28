<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="n" uri="nextframework.tags.next"%>
<div class="${n:defaultStyleClass('org.nextframework.view.InputTag','SELECT_MANY_POPUP-group')}">
	<input id="${tag.id}_labels" type="text" name="${tag.name}_labels" value="" class="select_many_popup_labels ${tag.dynamicAttributesMap['class']}" readonly="readonly" ${tag.dynamicAttributesToString} />
	<c:if test="${(empty tag.dynamicAttributesMap['disabled'] || tag.dynamicAttributesMap['disabled'] == 'false') && (empty tag.dynamicAttributesMap['readonly'] || tag.dynamicAttributesMap['readonly'] == 'false')}">
		<button id="${tag.id}_trigger" class="${n:defaultStyleClass('org.nextframework.view.InputTag','SELECT_MANY_POPUP-button')}"></button>
	</c:if>
</div>
<select id="${tag.id}" name="${tag.name}" multiple="multiple" ${n:attributeNotEmpty('onchange', tag.reloadOnChangeString)} ${n:attributeNotEmpty('onrenderitems', tag.inputComponent.onRenderItemsString)} style="display:none">${tag.inputComponent.selectItensString}</select>
<input type="hidden" name="_${tag.name}" value=" "/>
<script type="text/javascript">
	SelectManyPopup.install('${tag.name}');
</script>