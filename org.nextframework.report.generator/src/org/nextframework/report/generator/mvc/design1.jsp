<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>

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