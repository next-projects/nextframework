<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>
<%@ taglib prefix="report-generator" uri="report-generator"%>

<t:view title="Design de Relat�rios - Passo 2 Escolher campos" useBean="model">

	<n:content id="context">
		<n:link url="${crudPath}">Voltar para Relat�rios</n:link>
	</n:content>

	<div>
		<img src="${app}/resource/org/nextframework/report/generator/mvc/resource/table-icon.png" style="padding-left: 3px; padding-right: 3px;" />
		Tabela: ${reportTypeDisplayName}
		<t:property name="selectedType" type="hidden" write="false" />
	</div>
	<div style="padding-top: 6px">
		<report-generator:chooseProperties />
	</div>
	<n:link type="button">Voltar</n:link>&nbsp;
	
	<%--
	<n:submit action="selectProperties" parameters="visualizarDados=true">Pr�-Visualizar dados</n:submit>&nbsp;
	 --%>
	<n:submit action="designReport">Pr�ximo passo</n:submit>

	<BR />
	<BR />
	<t:propertyConfig mode="output">
		<c:if test="${preview != null && n:size(preview)==0}">N�o existem dados para essa tabela.</c:if>
		<c:if test="${!empty preview}">
			<n:dataGrid itemType="${model.selectedType}" itens="${preview}">
				<c:forEach items="${model.properties}" var="property">
					<t:property name="${property}" />
				</c:forEach>
			</n:dataGrid>
		</c:if>
	</t:propertyConfig>

</t:view>