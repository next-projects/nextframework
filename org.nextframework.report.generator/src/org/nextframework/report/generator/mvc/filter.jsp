<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>

<t:view title="${reportElement.reportTitle}" useBean="model">

	<t:property name="id" type="hidden" write="false" />

	<n:content id="context">
		<n:link url="${crudPath}">Voltar para Relatórios</n:link>
		<n:link action="editDesignForId" parameters="id=${model.id}">Editar Relatório</n:link>
	</n:content>

	<div class="filterWindow">
		<n:panelGrid columns="4">
			<c:forEach items="${filters}" var="filter">
				<c:if test="${empty filterMetadata[filter].fixedCriteria}">
					<n:panel style="padding-right: 6px;">
						${filterMetadata[filter].displayName}
					</n:panel>
					<n:panel style="width: 300px">
						<c:choose>
							<c:when test="${filterMetadata[filter].dateType}">
								<c:set var="begin" value="${filter}_begin" />
								<c:set var="end" value="${filter}_end" />
								<n:input name="${filter}_begin" value="${filterValuesMap[begin]}" type="${filterMetadata[filter].type}" id="filter_${filter}_b" label="${filterMetadata[filter].displayName}" required="${filterMetadata[filter].requiredFilter}" />
								até
								<n:input name="${filter}_end" value="${filterValuesMap[end]}" type="${filterMetadata[filter].type}" id="filter_${filter}_e" label="${filterMetadata[filter].displayName} (até)" required="${filterMetadata[filter].requiredFilter}" />
							</c:when>
							<c:when test="${filterMetadata[filter].filterSelectMultiple}">
								<n:input name="${filter}" value="${filterValuesMap[filter]}" useType="${filterMetadata[filter].type}" type="select-many-popup" id="filter_${filter}" label="${filterMetadata[filter].displayName}" required="${filterMetadata[filter].requiredFilter}" />
							</c:when>
							<c:otherwise>
								<n:input name="${filter}" value="${filterValuesMap[filter]}" type="${filterMetadata[filter].type}" id="filter_${filter}" label="${filterMetadata[filter].displayName}" required="${filterMetadata[filter].requiredFilter}" />
							</c:otherwise>
						</c:choose>
					</n:panel>
				</c:if>
			</c:forEach>
		</n:panelGrid>
		<div class="actionBar">
			<c:forEach items="${filterActions}" var="item">
				<n:submit action="${item.key}">${item.value}</n:submit>
			</c:forEach>
			<n:submit action="showResults">Pesquisar</n:submit>
			<t:property name="selectedType" normalCase="true" type="hidden" />
			<t:property name="reportXml" normalCase="true" type="hidden" />
		</div>
	</div>

	<c:if test="${!empty progressMonitor}">
		<n:modal id="overlay_BARRA">
			<n:progressBar progressMonitor="${progressMonitor}" onComplete="onFinish(element);" onError="onFinish(element);" />
		</n:modal>
		<script type="text/javascript">
			function onFinish(element) {
				if (!element.done) {
					alert('Erro ao executar a tarefa.');
				}
				hideModal_overlay_BARRA();
				model.ACTION.value = 'showFilterView';
				submitModel();
			}
		</script>
	</c:if>

	<div class="resultWindow">${html}</div>

</t:view>
