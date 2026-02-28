<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="n" uri="nextframework.tags.next"%>
<div class="${n:defaultStyleClass('org.nextframework.view.InputTag','DATE-group')}">
	<input type="text" id="${tag.id}" name="${tag.name}" value="${tag.valueToString}" maxlength="${tag.dynamicAttributesMap['maxlength']}" size="${tag.dynamicAttributesMap['size']}" onKeyUp="mascara_data(this, event, '${tag.pattern}');" onKeyPress="return valida_tecla_data(this, event, '${tag.pattern}')" ${n:attributeNotEmpty('onchange', tag.reloadOnChangeString)} ${tag.dynamicAttributesToString} />
	<c:if test="${empty tag.dynamicAttributesMap['disabled'] || tag.dynamicAttributesMap['disabled'] == 'false' }">
		<c:if test="${tag.inputComponent.showCalendar}">
			<button id="${tag.id}_trigger" class="${n:defaultStyleClass('org.nextframework.view.InputTag','DATE-button')}"></button>
		</c:if>
	</c:if>
</div>
<c:if test="${empty tag.dynamicAttributesMap['disabled'] || tag.dynamicAttributesMap['disabled'] == 'false' }">
	<input type="hidden" name="${tag.name}_datePattern" value="${tag.pattern}" />
	<c:if test="${tag.inputComponent.showCalendar}">
		<script type="text/javascript">
			Calendar.setup(
				{
					inputField  : "${tag.id}",
					ifFormat    : "${tag.inputComponent.calendarPattern}",
					button      : "${tag.id}_trigger",
					showsTime   : ${tag.inputComponent.showCalendarTime},
					showsSeconds: ${tag.inputComponent.showCalendarSeconds},
					weekNumbers : false
				}
			);
		</script>
	</c:if>
</c:if>