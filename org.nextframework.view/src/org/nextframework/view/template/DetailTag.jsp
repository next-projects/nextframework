<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="next"%>
<%@ taglib prefix="combo" uri="combo"%>
<%@ taglib prefix="t" uri="template"%>

<n:panel title="${Tdetalhe.detailDysplayName}">
	<div class="detailBlock">
	<n:dataGrid itens="${Tdetalhe.itens}" cellspacing="0" dynaLine="true" id="${Tdetalhe.tableId}"  var="${Tdetalhe.detailVar}" 
			headerStyle="${Tdetalhe.dynamicAttributesMap['headerstyle']}" 
			bodyStyles="${Tdetalhe.dynamicAttributesMap['bodystyles']}" 
			style="${Tdetalhe.dynamicAttributesMap['style']}"
			indexProperty="${Tdetalhe.indexProperty}"
			>
		<n:bean name="${Tdetalhe.detailVar}" valueType="${Tdetalhe.detailClass}" propertyPrefix="${Tdetalhe.fullNestedName}" propertyIndex="${index}">
			<n:getContent tagName="actionPanelTag" vars="acoes">
				<t:propertyConfig renderAs="column">
					<n:doBody />
				</t:propertyConfig>
				<c:if test="${Tdetalhe.showColunaAcao && !view}">
				<n:column header="${Tdetalhe.nomeColunaAcao}" style="width: 1%; white-space: nowrap; padding-right: 3px;">
					${acoes}
					<c:if test="${Tdetalhe.showBotaoRemover}">
						<c:if test="${!propertyConfigDisabled || dataGridDynaline}">
							<button type="button"                     onclick="_detail_deleteLine_${Tdetalhe.tableId}(extrairIndiceDeNome(this.id)-1)" id="button.excluir[table_id=${Tdetalhe.tableId}, indice=${rowIndex}]">
								remover
							</button>
						</c:if>
						<c:if test="${propertyConfigDisabled && !dataGridDynaline}">	
							<button type="button" disabled="disabled" onclick="_detail_deleteLine_${Tdetalhe.tableId}(extrairIndiceDeNome(this.id)-1)" id="button.excluir[table_id=${Tdetalhe.tableId}, indice=${rowIndex}]">
								remover
							</button>						
						</c:if>					
					</c:if>
				</n:column>
				</c:if>
			</n:getContent>
		</n:bean>
	</n:dataGrid>
	<c:if test="${Tdetalhe.showBotaoNovaLinha && !view}">
		<c:if test="${empty Tdetalhe.dynamicAttributesMap['labelnovalinha']}">
			<c:set value="Adicionar Registro" scope="page" var="labelnovalinha"/>
		</c:if>
		<c:if test="${!empty Tdetalhe.dynamicAttributesMap['labelnovalinha']}">
			<c:set value="${Tdetalhe.dynamicAttributesMap['labelnovalinha']}" scope="page" var="labelnovalinha"/>
		</c:if>
		
		<c:if test="${!propertyConfigDisabled}">
			<button type="button" onclick="_detail_newLine_${Tdetalhe.tableId}()">
				${labelnovalinha}
			</button>
		</c:if>
		<c:if test="${propertyConfigDisabled}">
			<button type="button" disabled="disabled" onclick="_detail_newLine_${Tdetalhe.tableId}()">
				${labelnovalinha}
			</button>
		</c:if>
	</c:if>
	</div>
</n:panel>

<script>
	/**
	 * index (int) - indice da propriedade (o detalhe tem que obrigatoriamente possuir um header)
	 * quiet (boolean)
	 */
	function _detail_deleteLine_${Tdetalhe.tableId}(index, quiet){
		var id = "button.excluir[table_id=${Tdetalhe.tableId}, indice="+(index+1)+"]"; //passar de indice da propriedade para indice da linha (considerando header)
		var isToRemove = function(button){${Tdetalhe.onRemove}}(this);//onRemove function should return true or false
		var removed = false;
		if(isToRemove) {
			try {
				removed = excluirLinhaPorNome(id, quiet);
				if(removed){
					reindexFormPorNome(id, document.forms[0], '${Tdetalhe.fullNestedName}', true);
				}
			} catch(e){
				alert('Erro ao excluir detalhe: \n'+e);
			}
		}
		return removed;		
	}

	function _detail_newLine_${Tdetalhe.tableId}(){
		${Tdetalhe.beforeNewLine}
		var indiceLinha = newLine${Tdetalhe.tableId}();
		
		${Tdetalhe.onNewLine};
		return indiceLinha -1;//passar para indice da propriedade (considerando header)
	}
</script>