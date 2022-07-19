<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<input type="text" id="${tag.id}" name="${tag.name}" value="${tag.valueToString}" maxlength="${tag.dynamicAttributesMap['maxlength']}" size="${tag.dynamicAttributesMap['size']}" onKeyUp="mascara_data(this, event, '${tag.pattern}');" onKeyPress="return valida_tecla_data(this, event, '${tag.pattern}')" onchange="${tag.reloadOnChangeString}" ${tag.dynamicAttributesToString}/>
<c:if test="${empty tag.dynamicAttributesMap['disabled'] || tag.dynamicAttributesMap['disabled'] == 'false' }">
	<input type="hidden" name="${tag.name}_datePattern" value="${tag.pattern}"/>
<c:if test="${tag.inputComponent.showCalendar}">	
	<button id="${tag.id}_trigger" class="calendarbutton">...</button>
	<script type="text/javascript">
	  Calendar.setup(
	    {
	      inputField  : "${tag.id}", // ID of the input field
	      ifFormat    : "${tag.inputComponent.calendarPattern}", // the date format
	      button      : "${tag.id}_trigger", // ID of the button
	      showsTime   : ${tag.inputComponent.showCalendarTime},
	      showsSeconds: ${tag.inputComponent.showCalendarSeconds},
	      weekNumbers : false
	    }
	  );
	</script>
</c:if>
</c:if>