<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>

<div class="${n:defaultStyleClass('org.nextframework.view.InputTag','SELECT_MANY_BOX-group')}">
	<select name="${tag.name}_from_" id="${tag.name}_from_" onclick="selectManyBoxCancelTo(this)" ondblclick="selectManyBoxAdd(this)" ${tag.dynamicAttributesToString} size="10">${tag.inputComponent.avaiableValues}</select>
	<div class="${n:defaultStyleClass('org.nextframework.view.InputTag','SELECT_MANY_BOX-buttons')}">
		<button name="${tag.name}_left_" disabled="disabled" onclick="selectManyBoxAdd(this)" class="${n:defaultStyleClass('org.nextframework.view.InputTag','SELECT_MANY_BOX-buttonAdd')}"></button>
		<button name="${tag.name}_right" disabled="disabled" onclick="selectManyBoxRemove(this)" class="${n:defaultStyleClass('org.nextframework.view.InputTag','SELECT_MANY_BOX-buttonRemove')}"></button>
	</div>
	<select name="${tag.name}_to___" id="${tag.name}_to___" onclick="selectManyBoxCancelFrom(this)" ondblclick="selectManyBoxRemove(this)" ${tag.dynamicAttributesToString} size="10">${tag.inputComponent.selectedValues}</select>
</div>
<div id="${tag.name}_value" style="display: none">${tag.inputComponent.inputValues}</div>
<input type="hidden" name="_${tag.name}" value=" " />