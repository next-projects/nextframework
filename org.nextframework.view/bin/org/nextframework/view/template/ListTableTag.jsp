<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="next"%>
<%@ taglib prefix="combo" uri="combo"%>
<%@ taglib prefix="t" uri="template"%>

<n:dataGrid itens="${TtabelaResultados.itens}" var="${TtabelaResultados.name}" cellspacing="0" groupProperty="${TtabelaResultados.dynamicAttributesMap['groupproperty']}" >
	<n:bean name="${TtabelaResultados.name}" valueType="${TtabelaResultados.valueType}">
		<n:getContent tagName="acaoTag" vars="acoes">
			<t:propertyConfig mode="output" renderAs="column">
				<n:doBody />
			</t:propertyConfig>
			<c:if test="${(!empty acoes) || (TtabelaResultados.showEditarLink) || (TtabelaResultados.showExcluirLink) || (TtabelaResultados.showConsultarLink)}">
				<n:column header="Ação" style="width: 1px; white-space: nowrap; padding-right: 3px;"> <%-- width: 1%;  --%>
					${acoes}
						<script language="javascript">
						<c:catch var="exSelecionar">
							imprimirSelecionar(new Array(${n:hierarchy(TtabelaResultados.valueType)}), 
									"<a href=\"javascript:selecionar('${n:escape(n:valueToString(n:reevaluate(TtabelaResultados.name, pageContext)))}','${n:escape(n:descriptionToString(n:reevaluate(TtabelaResultados.name, pageContext)))}')\">selecionar</a>&nbsp;");
						</c:catch>
						</script>
						<c:if test="${!empty exSelecionar}">
							${n:printStackTrace(exSelecionar)}
							<span style="font-size: 10px; color: red; white-space: pre; display:block;">Erro ao imprimir botão selecionar: ${exSelecionar.message} <c:catch>${exSelecionar.rootCause.message}</c:catch></span>
						</c:if>
					<c:if test="${TtabelaResultados.showConsultarLink}">
						<n:link action="view" parameters="${n:idProperty(n:reevaluate(TtabelaResultados.name,pageContext))}=${n:id(n:reevaluate(TtabelaResultados.name,pageContext))}">consultar</n:link>
					</c:if>						
					<c:if test="${TtabelaResultados.showEditarLink}">
						<n:link action="update" parameters="${n:idProperty(n:reevaluate(TtabelaResultados.name,pageContext))}=${n:id(n:reevaluate(TtabelaResultados.name,pageContext))}">editar</n:link>
					</c:if>
					<c:if test="${TtabelaResultados.showExcluirLink}">				
						<n:link confirmationMessage="Deseja realmente excluir esse registro?" action="delete" parameters="${n:idProperty(n:reevaluate(TtabelaResultados.name,pageContext))}=${n:id(n:reevaluate(TtabelaResultados.name,pageContext))}">excluir</n:link>
					</c:if>		
				</n:column>
			</c:if>
		</n:getContent>
	</n:bean>
</n:dataGrid>

<div class="pagging" >
	P&aacute;gina <n:pagging currentPage="${tag.currentPage}" totalNumberOfPages="${tag.numberOfPages}" selectedClass="pageSelected" unselectedClass="pageUnselected" />
</div>
