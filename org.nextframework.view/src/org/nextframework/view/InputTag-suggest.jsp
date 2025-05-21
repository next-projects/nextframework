<div style="display: inline-block;">
	<input type="text" name="${tag.name}_text" id="${tag.id}_text" onfocus="next.suggest.install(this, '${tag.dynamicAttributesMap['provider']}')" ${tag.dynamicAttributesToString} autocomplete="off" value="${tag.inputComponent.valueToStringDescription}"/>
	<span style="width: 20px; height: 16px; ">
		<img id="sg_ok" src="${application}/resource/img/suggest_ok.gif" style="position:absolute; display:none">
		<img id="sg_notok" src="${application}/resource/img/suggest_notok.gif"  style="position:absolute; display:none" title="O valor não foi selecionado!">
		&nbsp;&nbsp;&nbsp;
	</span>
	<input type="hidden" name="${tag.name}" id="${tag.id}" value="${tag.valueToString}" ${tag.dynamicAttributesToString}/>
</div>
