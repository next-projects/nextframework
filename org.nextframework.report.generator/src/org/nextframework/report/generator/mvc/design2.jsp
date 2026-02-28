<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="n" uri="nextframework.tags.next"%>
<%@ taglib prefix="t" uri="nextframework.tags.template"%>
<%@ taglib prefix="rg" uri="nextframework.tags.reportgenerator"%>

<t:view title="Design de Relatórios - Passo 2: Escolher campos" useBean="model">

	<n:content id="context">
		<n:link url="${crudPath}">Voltar para Relatórios</n:link>
	</n:content>
	
	<t:simplePanel>
		<t:formTable columns="12">
			<n:panel colspan="12">
				<img src="${app}/resource/org/nextframework/report/generator/mvc/resource/table-icon.png" />
				Tabela: ${reportTypeDisplayName}
			</n:panel>
			<n:panel colspan="12">
				Escolha as propriedades que deseja usar no relatório:<br>
				<n:group columns="12">
					<n:panel colspan="12">
						<rg:chooseProperties />
					</n:panel>
				</n:group>
			</n:panel>
		</t:formTable>
		<t:actionPanel>
			<n:link type="button">Voltar</n:link>
			<n:submit action="designReport">Próximo passo</n:submit>
		</t:actionPanel>
	</t:simplePanel>

	<t:property name="selectedType" type="hidden" write="false" />

</t:view>