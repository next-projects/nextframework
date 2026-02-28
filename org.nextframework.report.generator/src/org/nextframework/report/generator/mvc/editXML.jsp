<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="n" uri="nextframework.tags.next"%>
<%@ taglib prefix="t" uri="nextframework.tags.template"%>

<t:view title="Erro no relatório" useBean="model">

	<n:content id="context">
		<n:link url="${crudPath}">Voltar para Relatórios</n:link>
	</n:content>

	<t:formPanel submitAction="saveReport">
		<t:formTable columns="12">
			<t:property name="reportXml" rows="40" normalCase="true" style="text-transform: none;" colspan="12" />
			<t:property name="reportPublic" colspan="12" />
			<t:property name="id" type="hidden" renderAs="single" />
		</t:formTable>
	</t:formPanel>

</t:view>