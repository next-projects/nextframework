<!-- Valor selecionado ${tag.valueToString} -->
<div>
	<div>
		<div style="float:left">
			<select name="${tag.name}_from_" id="${tag.name}_from_"
						style="width: ${tag.inputComponent.inputWidth}px"
						onclick="selectManyBoxCancelTo(this)" 
						ondblclick="selectManyBoxAdd(this)"
						size="10">${tag.inputComponent.avaiableValues}</select>
		</div>
		<div  style="float:left">
			<button name="${tag.name}_left_" disabled="disabled" onclick="selectManyBoxAdd(this)" >-&gt;</button>
			<BR/>
			<button name="${tag.name}_right" disabled="disabled" onclick="selectManyBoxRemove(this)">&lt;-</button>
		</div>
		<div>
			<select name="${tag.name}_to___" id="${tag.name}_to___" 
							style="width: ${tag.inputComponent.inputWidth}px"
							onclick="selectManyBoxCancelFrom(this)" 
							ondblclick="selectManyBoxRemove(this)"
							size="10">${tag.inputComponent.selectedValues}</select>
		</div>
	</div>
	<div id="${tag.name}_value" style="display:none">
		${tag.inputComponent.inputValues}
	</div>
	<input type="hidden" name="_${tag.name}" value=" "/>
</div>
