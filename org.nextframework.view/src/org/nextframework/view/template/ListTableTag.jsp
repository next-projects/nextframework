<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="combo" uri="combo"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>

<n:dataGrid itens="${listTableTag.itens}" var="${listTableTag.name}" cellspacing="0" groupProperty="${listTableTag.groupProperty}" >
	<n:bean name="${listTableTag.name}" valueType="${listTableTag.valueType}">
		<n:getContent tagName="actionPanelTag" vars="acoes">
			<t:propertyConfig mode="output" renderAs="column">
				<n:doBody />
			</t:propertyConfig>
			<c:if test="${ !empty acoes || listTableTag.showViewLink || listTableTag.showEditLink || listTableTag.showDeleteLink }">
				<n:column header="${n:messageDefault('actionColumnName', null, 'Ação')}" style="width: 1%; white-space: nowrap; padding-right: 3px;">

					${acoes}

					<script language="javascript">
						<c:catch var="exSelecionar">
							imprimirSelecionar(new Array(${n:hierarchy(listTableTag.valueType)}), "<a href=\"javascript:selecionar('${n:escape(n:valueToString(n:reevaluate(listTableTag.name, pageContext)))}','${n:escape(n:descriptionToString(n:reevaluate(listTableTag.name, pageContext)))}')\">${listTableTag.selectLinkLabel}</a>&nbsp;");
						</c:catch>
					</script>
					<c:if test="${!empty exSelecionar}">
						${n:printStackTrace(exSelecionar)}
						<span style="font-size: 10px; color: red; white-space: pre; display:block;">Erro ao imprimir botão selecionar: ${exSelecionar.message} <c:catch>${exSelecionar.rootCause.message}</c:catch></span>
					</c:if>

					<c:if test="${listTableTag.showViewLink}">
						<n:link action="view" parameters="${n:idProperty(n:reevaluate(listTableTag.name,pageContext))}=${n:id(n:reevaluate(listTableTag.name,pageContext))}">${listTableTag.viewLinkLabel}</n:link>
					</c:if>						
					<c:if test="${listTableTag.showEditLink}">
						<n:link action="update" parameters="${n:idProperty(n:reevaluate(listTableTag.name,pageContext))}=${n:id(n:reevaluate(listTableTag.name,pageContext))}">${listTableTag.updateLinkLabel}</n:link>
					</c:if>
					<c:if test="${listTableTag.showDeleteLink}">
						<n:link action="delete" parameters="${n:idProperty(n:reevaluate(listTableTag.name,pageContext))}=${n:id(n:reevaluate(listTableTag.name,pageContext))}" confirmationMessage="${n:messageDefault('deleteLinkConfirmation', null, 'Deseja realmente excluir esse registro?')}" >${listTableTag.deleteLinkLabel}</n:link>
					</c:if>

				</n:column>
			</c:if>
		</n:getContent>
	</n:bean>
</n:dataGrid>

<div class="pagging" >
	${n:messageDefault('paginaLabel', null, 'Página')} <n:pagging currentPage="${listTableTag.currentPage}" totalNumberOfPages="${listTableTag.numberOfPages}" selectedClass="pageSelected" unselectedClass="pageUnselected" />
</div>