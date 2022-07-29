<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:if test="${useBootstrap}">
	<div class="input-group">
</c:if>
<input type="text" id="${tag.id}" name="${tag.name}" value="${tag.valueToString}" maxlength="${tag.dynamicAttributesMap['maxlength']}" size="${tag.dynamicAttributesMap['size']}" onKeyUp="mascara_data(this, event, '${tag.pattern}');" onKeyPress="return valida_tecla_data(this, event, '${tag.pattern}')" onchange="${tag.reloadOnChangeString}" ${tag.dynamicAttributesToString}/>
<c:if test="${empty tag.dynamicAttributesMap['disabled'] || tag.dynamicAttributesMap['disabled'] == 'false' }">
	<c:if test="${tag.inputComponent.showCalendar}">
		<c:if test="${useBootstrap}">
			<i id="${tag.id}_trigger" class="bi bi-calendar-date input-group-text"></i>
		</c:if>
		<c:if test="${!useBootstrap}">
			<button id="${tag.id}_trigger" class="calendarbutton">...</button>
		</c:if>
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
	<input type="hidden" name="${tag.name}_datePattern" value="${tag.pattern}"/>
</c:if>
<c:if test="${useBootstrap}">
	</div>
</c:if>