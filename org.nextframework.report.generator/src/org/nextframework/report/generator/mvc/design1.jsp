<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="next"%>
<%@ taglib prefix="t" uri="template"%>

<link rel="StyleSheet" href="${app}/resource/org/nextframework/report/generator/mvc/resource/report-generator.css" type="text/css">

<t:tela titulo="Design de Relatórios - Passo 1 Escolher dados">

	<n:content id="context">
		<n:link url="${crudPath}">Voltar para Relatórios</n:link>
	</n:content>

	<n:dataGrid itens="${entities}" itemType="<%=Class.class%>" 
		rowonmouseover="this.style.backgroundColor = '#ffc'" 
		rowonmouseout="this.style.backgroundColor = ''" 
		style="cursor: default"
		bodyStyleClasses="dataGridBody1">
		<n:column header="Tabela" style="padding: 5px;">
			<img src="${app}/resource/org/nextframework/report/generator/mvc/resource/table-icon.png" style="padding-left: 3px; padding-right: 3px;"/>
			<n:link action="selectProperties" parameters="selectedType=${row.name}" class="reportTableLink">${displayNames[row]}</n:link>
		</n:column>
	</n:dataGrid>
</t:tela>