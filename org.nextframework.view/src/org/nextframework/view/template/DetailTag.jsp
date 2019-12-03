<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="combo" uri="combo"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>

<n:panel title="${detailTag.detailDysplayName}" colspan="${detailTag.colspan}">
	<div class="detailBlock">
		<n:dataGrid id="${detailTag.tableId}" itens="${detailTag.itens}" var="${detailTag.detailVar}" indexProperty="${detailTag.indexProperty}" cellspacing="0" dynaLine="true"
				headerStyle="${detailTag.dynamicAttributesMap['headerstyle']}"
				bodyStyles="${detailTag.dynamicAttributesMap['bodystyles']}"
				style="${detailTag.dynamicAttributesMap['style']}" >
			<n:bean name="${detailTag.detailVar}" valueType="${detailTag.detailClass}" propertyPrefix="${detailTag.fullNestedName}" propertyIndex="${index}">
				<n:getContent tagName="actionPanelTag" vars="acoes">
					<t:propertyConfig renderAs="column">
						<n:doBody />
					</t:propertyConfig>
					<c:if test="${(!empty acoes || detailTag.showDeleteButton) && detailTag.showActionColumn && !view}">
						<n:column header="${detailTag.actionColumnName}" style="width: 1%; white-space: nowrap; padding-right: 3px;">
							${acoes}
							<c:if test="${detailTag.showDeleteButton}">
								<c:if test="${!propertyConfigDisabled || dataGridDynaline}">
									<button type="button" onclick="_detail_deleteLine_${detailTag.tableId}(extrairIndiceDeNome(this.id)-1)" id="button.excluir[table_id=${detailTag.tableId}, indice=${rowIndex}]">${detailTag.deleteLinkLabel}</button>
								</c:if>
								<c:if test="${propertyConfigDisabled && !dataGridDynaline}">
									<button type="button" disabled="disabled" onclick="_detail_deleteLine_${detailTag.tableId}(extrairIndiceDeNome(this.id)-1)" id="button.excluir[table_id=${detailTag.tableId}, indice=${rowIndex}]">${detailTag.deleteLinkLabel}</button>
								</c:if>
							</c:if>
						</n:column>
					</c:if>
				</n:getContent>
			</n:bean>
		</n:dataGrid>
		<c:if test="${detailTag.showNewLineButton && !view}">
			<c:if test="${!propertyConfigDisabled}">
				<button type="button" onclick="_detail_newLine_${detailTag.tableId}()">${detailTag.newLineButtonLabel}</button>
			</c:if>
			<c:if test="${propertyConfigDisabled}">
				<button type="button" disabled="disabled" onclick="_detail_newLine_${detailTag.tableId}()">${detailTag.newLineButtonLabel}</button>
			</c:if>
		</c:if>
	</div>
</n:panel>

<script>

	/**
	 * index (int) - indice da propriedade (o detalhe tem que obrigatoriamente possuir um header)
	 * quiet (boolean)
	 */
	function _detail_deleteLine_${detailTag.tableId}(index, quiet){
		var id = "button.excluir[table_id=${detailTag.tableId}, indice="+(index+1)+"]"; //passar de indice da propriedade para indice da linha (considerando header)
		var isToRemove = function(button){${detailTag.onDelete}}(this);//onDelete function should return true or false
		var removed = false;
		if(isToRemove) {
			try {
				removed = excluirLinhaPorNome(id, quiet);
				if(removed){
					reindexFormPorNome(id, document.forms[0], '${detailTag.fullNestedName}', true);
				}
			} catch(e){
				alert('Erro ao excluir detalhe: \n'+e);
			}
		}
		return removed;
	}

	function _detail_newLine_${detailTag.tableId}(){
		${detailTag.beforeNewLine}
		var row = newLine${detailTag.tableId}();
		${detailTag.onNewLine};
		return row.rowIndex -1;//passar para indice da propriedade (considerando header)
	}

</script>