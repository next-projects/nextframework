<!-- Valor selecionado ${tag.valueToString} -->
<input type="text" name="${tag.name}_label" id="${tag.id}" onchange="${tag.reloadOnChangeString}" readonly="true" value="${tag.descriptionToString}" ${tag.dynamicAttributesToString}/>
<input type="hidden" name="${tag.name}" value="${tag.valueWithDescriptionToString}" />
<button id="${tag.name}_btn" name="${tag.name}_btn" type="button" onclick="${tag.inputComponent.selectOneButtonOnClick} " style='${tag.inputComponent.selectOneButtonStyle}'>Selecionar</button>
<button id="${tag.name}_btnUnselect" name="${tag.name}_btnUnselect" type="button" onclick="document.getElementsByName('${tag.name}_label')[0].value = ''; document.getElementsByName('${tag.name}')[0].value = '<null>'; document.getElementById('${tag.name}_btn').style.display=''; document.getElementById('${tag.name}_btnUnselect').style.display='none'" style='${tag.inputComponent.selectOneUnselectButtonStyle}'>Limpar</button>
