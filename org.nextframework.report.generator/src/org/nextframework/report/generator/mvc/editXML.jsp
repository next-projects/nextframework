<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>

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