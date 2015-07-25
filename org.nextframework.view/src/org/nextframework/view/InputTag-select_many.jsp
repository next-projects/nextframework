<!-- Valor selecionado ${tag.valueToString} -->
<select name="${tag.name}" id="${tag.id}" onchange="${tag.reloadOnChangeString}" multiple="true" ${tag.dynamicAttributesToString}>${tag.inputComponent.selectItensString}</select>
<input type="hidden" name="_${tag.name}" value=" "/>
