<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="n" uri="nextframework.tags.next"%>
<%@ taglib prefix="t" uri="nextframework.tags.template"%>

<t:view title="Design de Relatórios - Passo 1: Escolher dados">

	<n:content id="context">
		<n:link url="${crudPath}">Voltar para Relatórios</n:link>
	</n:content>

	<n:dataGrid itens="${entities}" itemType="<%=Class.class%>">
		<n:column header="Tabela">
			<img src="${app}/resource/org/nextframework/report/generator/mvc/resource/table-icon.png"/>
			<n:link action="selectProperties" parameters="selectedType=${row.name}">${displayNames[row]}</n:link>
		</n:column>
	</n:dataGrid>

</t:view>