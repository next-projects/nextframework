<%@page import="org.nextframework.view.template.PropertyTag"%>
<%@page import="org.nextframework.core.config.ViewConfig"%>
<%@page import="org.nextframework.service.ServiceFactory"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>
<%@ taglib prefix="code" uri="code"%>

<t:view title="${reportElement.reportTitle}" useBean="model">

	<n:content id="context">
		<n:link url="${crudPath}">Voltar para Relat�rios</n:link>
		<n:link action="editDesignForId" parameters="id=${model.id}">Editar Relat�rio</n:link>
	</n:content>

	<t:simplePanel>
		<t:formTable columns="8">
			<c:forEach items="${filters}" var="filter">
				<c:if test="${empty filterMetadataMap[filter].fixedCriteria}">
					<t:propertyLayout label="${filterMetadataMap[filter].displayName}" colspan="4">
						<c:choose>
							<c:when test="${filterMetadataMap[filter].dateType}">
								<c:set var="begin" value="${filter}_begin" />
								<c:set var="end" value="${filter}_end" />
								<div class="${useBootstrap ? 'd-flex' : ''}">
									<n:input name="${filter}_begin" value="${filterValuesMap[begin]}" type="${filterMetadataMap[filter].type}" id="filter_${filter}_b" label="${filterMetadataMap[filter].displayName}" required="${filterMetadataMap[filter].requiredFilter}" />
									<span class="${useBootstrap ? 'm-1' : ''}">at�</span>
									<n:input name="${filter}_end" value="${filterValuesMap[end]}" type="${filterMetadataMap[filter].type}" id="filter_${filter}_e" label="${filterMetadataMap[filter].displayName} (at�)" required="${filterMetadataMap[filter].requiredFilter}" />
								</div>
							</c:when>
							<c:when test="${filterMetadataMap[filter].filterSelectMultiple}">
								<n:input name="${filter}" value="${filterValuesMap[filter]}" useType="${filterMetadataMap[filter].type}" type="select-many-popup" id="filter_${filter}" label="${filterMetadataMap[filter].displayName}" required="${filterMetadataMap[filter].requiredFilter}" />
							</c:when>
							<c:otherwise>
								<n:input name="${filter}" value="${filterValuesMap[filter]}" type="${filterMetadataMap[filter].type}" id="filter_${filter}" label="${filterMetadataMap[filter].displayName}" required="${filterMetadataMap[filter].requiredFilter}" />
							</c:otherwise>
						</c:choose>
					</t:propertyLayout>
				</c:if>
			</c:forEach>
		</t:formTable>
		<t:actionPanel>
			<c:forEach items="${filterActions}" var="item">
				<n:submit action="${item.key}">${item.value}</n:submit>
			</c:forEach>
			<n:submit action="showResults">Pesquisar</n:submit>
			<t:property name="selectedType" normalCase="true" type="hidden" />
			<t:property name="reportXml" normalCase="true" type="hidden" />
		</t:actionPanel>
	</t:simplePanel>

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
				form.ACTION.value = 'showFilterView';
				submitForm();
			}
		</script>
	</c:if>

	<t:listPanel>
		${html}
	</t:listPanel>

	<t:property name="id" type="hidden" renderAs="single" />

</t:view>