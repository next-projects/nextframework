<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="next"%>
<%@ taglib prefix="t" uri="template"%>

<script type="text/javascript">
	function aplicaInputCaixaAltaAutomaticaHabilitado(){
		return false;
	}
</script>

<t:tela titulo="Erro no relat�rio" useBean="model">

	<n:content id="context">
		<n:link url="${crudPath}">Voltar para Relat�rios</n:link>
	</n:content>
	
	<n:panel style="color: #AA0000">
		Ocorreu um erro ao ler as informa��es do relat�rio.
		<a onclick="document.getElementById('edittable').style.display = ''" style="cursor: pointer">Resolver problema (requer conhecimentos avan�ados)</a> 
	</n:panel>
	
	<div id="edittable" style="display:none">
		<n:panelGrid columns="2" propertyRenderAs="double">
			<t:property name="id" type="hidden" write="true"/>
			<t:property name="reportPublic" />
			<t:property name="reportXml" rows="40" cols="180" style="text-transform: none;"/>
		</n:panelGrid>
		
		<div style="clear:left">
	 		<n:submit action="saveReport">Salvar Relat�rio</n:submit>
		</div>
	</div>
	<%--
	<n:submit action="showFilterView">Show Filter View</n:submit>
	 --%>

</t:tela>