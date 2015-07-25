<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!-- Valor selecionado ${tag.valueToString} -->
<span style="white-space: nowrap; width: ${tag.dynamicAttributesMap['spanwidth']};">
<table border="0" style="width: ${tag.dynamicAttributesMap['spanwidth']}; display:inline" cellpadding="0" cellspacing="0">
<tr>
<td id="selectoneinsert_td_${tag.name}">
<select name="${tag.name}" id="${tag.id}" onchange="${tag.reloadOnChangeString}" ${tag.dynamicAttributesToString}>${tag.selectoneblankoption}${tag.inputComponent.selectItensString}</select>
</td>
<td style="width: 48px" style="padding-left: 2px">
<button id="${tag.name}_btn" name="${tag.name}_btn" type="button" onclick="${tag.inputComponent.selectOneInsertOnClick} " style='width: 100%;'>Novo</button>
</td>
</tr>
</table>
</span>
