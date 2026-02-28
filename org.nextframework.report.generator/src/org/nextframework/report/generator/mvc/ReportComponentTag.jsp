<%@page import="org.nextframework.view.template.PropertyTag"%>
<%@page import="org.nextframework.core.config.ViewConfig"%>
<%@page import="org.nextframework.service.ServiceFactory"%>

<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="n" uri="nextframework.tags.next"%>
<%@ taglib prefix="t" uri="nextframework.tags.template"%>
<%@ taglib prefix="code" uri="nextframework.tags.code"%>
<%@ taglib prefix="rg" uri="nextframework.tags.reportgenerator"%>

<style>
	.xmlinputstyle {
		display: none;
	}
</style>

<c:set var="groupStyleClass" value="${n:defaultStyleClass('org.nextframework.view.GroupTag','fieldsetStyleClass')}" />
<c:set var="inputTagClass" value="${n:defaultStyleClass('org.nextframework.view.InputTag','class')}" />
<c:set var="inputCheckTagClass" value="${n:defaultStyleClass('org.nextframework.view.InputTag','CHECKBOX-class')}" />
<c:set var="inputSelectTagClass" value="${n:defaultStyleClass('org.nextframework.view.InputTag','SELECT_ONE-class')}" />
<c:set var="btnStyleClass" value="${n:defaultStyleClass('org.nextframework.view.SubmitTag','BUTTON-class')}" />
<c:set var="btnStyleClassNl" value="${n:defaultStyleClass('org.nextframework.view.template.DetailTag','newLineButtonStyleClass')}" />

<div id="designerArea" style="width: 100%">

	<div id="reportDesign" class="${groupStyleClass}">
		<table id="designTable">
		</table>
	</div>
	<div>
		<div style="text-align: right;">
			<button id="moveLeft" onclick="ReportDesigner.getInstance().moveItem(-1);" class="${btnStyleClassNl}">&lt;-</button>
			<button id="moveRight" onclick="ReportDesigner.getInstance().moveItem(1);" class="${btnStyleClassNl}">-&gt;</button>
			<button id="removeDefinitionItem" onclick="ReportDesigner.getInstance().removeSelectedDefinitionItem()" class="${btnStyleClassNl}">Remover</button>
		</div>
	</div>

	<n:tabPanel id="designerTab">
		<n:panel title="Configuração">
			<t:formTable columns="10">

				<t:propertyLayout label="Título do Relatório" bodyId="reportTitle" colspan="6">
					<input id="reportTitle" class="${inputTagClass}" />
				</t:propertyLayout>
				<t:propertyLayout label="Tornar Público" bodyId="reportPublic" invertLabel="true" colspan="4" labelColspan="3">
					<n:input type="checkbox" name="reportPublic" id="reportPublic" value="${model.reportPublic}" class="${inputCheckTagClass}" />
				</t:propertyLayout>

				<jsp:include page="${customPageUrl}" />

			</t:formTable>
		</n:panel>
		<n:panel title="Campos">
			<t:formTable columns="10">

				<n:panel colspan="10">
					<n:input name="fields" type="select-many-box" itens="${emptyList}" useType="java.lang.String" />
					<button onclick="ReportDesigner.getInstance().showConfigureProperties();" class="${btnStyleClassNl}">Configurar campos</button>
				</n:panel>

				<t:propertyLayout label="Legenda" bodyId="label" colspan="8">
					<input id="label" class="${inputTagClass}" />
				</t:propertyLayout>
				<n:panel colspan="2" />

				<t:propertyLayout label="Formato" bodyId="patternDate" colspan="8">
					<select id="patternDate" class="${inputSelectTagClass}" style="text-transform: none;">
						<option selected="selected" value="">Dia/Mês/Ano (padrão)</option>
						<option value="dd/MM/yyyy HH:mm">Dia/Mês/Ano Hora:Minuto</option>
						<option value="dd/MM">Dia/Mês</option>
						<option value="MM/yyyy">Mês/Ano</option>
						<option value="MMM yyyy">Mês (abreviado) Ano</option>
						<option value="MMMMM 'de' yyyy">Mês Ano Extenso</option>
						<option value="yyyy">Ano</option>
						<option value="dd 'de' MMMMM">Dia/Mês Extenso</option>
						<option value="dd 'de' MMMMM 'de' yyyy">Dia/Mês/Ano Extenso</option>
					</select>
				</t:propertyLayout>
				<n:panel colspan="2" />

				<t:propertyLayout label="Formato" bodyId="patternNumber" colspan="8">
					<select id="patternNumber" class="${inputSelectTagClass}" style="text-transform: none;">
						<option selected="selected" value="">Padrão</option>
						<option value="0">Sem casas decimais</option>
						<option value="#,##0">Sem casas decimais + separador milhar</option>
						<option value="0.00">2 casas decimais</option>
						<option value="0.000">3 casas decimais</option>
						<option value="#,##0.00">2 casas decimais + separador milhar</option>
						<option value="#,##0.000">3 casas decimais + separador milhar</option>
						<c:forEach items="${reportFieldsFormatters}" var="kv">
							<option value="c${kv.key}">${kv.value}</option>
						</c:forEach>
					</select>
				</t:propertyLayout>
				<n:panel colspan="2" />

				<t:propertyLayout label="Agregar Valores" bodyId="aggregate" invertLabel="true" colspan="4" labelColspan="3">
					<n:input id="aggregate" type="checkbox" class="${inputCheckTagClass}"/>
				</t:propertyLayout>
				<t:propertyLayout bodyId="aggregateType" label="" renderAs="single" colspan="4">
					<select id="aggregateType" class="${inputSelectTagClass}">
						<option value="SUM">Soma</option>
						<option value="AVERAGE">Média</option>
						<option value="AVERAGENN">Média (não vazios)</option>
						<option value="MAX">Máximo</option>
						<option value="MIN">Mínimo</option>
					</select>
				</t:propertyLayout>

			</t:formTable>
		</n:panel>
		<n:panel title="Grupos">
			<t:formTable columns="10">

				<n:panel colspan="10">
					<n:input name="groups" type="select-many-box" itens="${emptyList}" useType="java.lang.String" />
				</n:panel>

				<t:propertyLayout label="Formato" bodyId="patternDateGroup" colspan="8">
					<select id="patternDateGroup" class="${inputSelectTagClass}" style="text-transform: none;">
						<option selected="selected" value="">Mês/Ano (padrão)</option>
						<option value="MMMMM 'de' yyyy">Mês/Ano Extenso</option>
						<option value="MMMMM">Mês</option>
						<option value="yyyy">Ano</option>
						<option value="ddddd">Dia da Semana</option>
						<option value="dd/MM/yyyy">Dia/Mês/Ano</option>
						<option value="dd 'de' MMMMM 'de' yyyy">Dia/Mês/Ano Extenso</option>
						<option value="HH">Hora</option>
					</select>
				</t:propertyLayout>

			</t:formTable>
		</n:panel>
		<n:panel title="Filtros">
			<t:formTable columns="12">

				<n:panel colspan="10">
					<n:input name="filters" type="select-many-box" itens="${emptyList}" useType="java.lang.String" />
				</n:panel>
				<n:panel colspan="2" />

				<t:propertyLayout label="Legenda" bodyId="filterLabel" colspan="8">
					<input id="filterLabel" class="${inputTagClass}" />
				</t:propertyLayout>
				<n:panel colspan="4" />

				<t:propertyLayout label="Valor padrão" bodyId="filterPreSelectDate" colspan="4">
					<n:input id="filterPreSelectDate" itens="${reportFilterDateAutoFilterList}" type="${ReportFilterDateAutoFilterType}" />
				</t:propertyLayout>
				<t:propertyLayout label="Valor padrão" bodyId="filterPreSelectEntity" colspan="4">
					<select id="filterPreSelectEntity" class="${inputSelectTagClass}"></select>
				</t:propertyLayout>
				<t:propertyLayout label="Critério fixo" bodyId="filterFixedCriteria" colspan="4">
					<select id="filterFixedCriteria" class="${inputSelectTagClass}"></select>
				</t:propertyLayout>

				<t:propertyLayout label="Múltiplos valores" bodyId="filterSelectMultiple" invertLabel="true" colspan="4" labelColspan="3">
					<n:input id="filterSelectMultiple" type="checkbox" class="${inputCheckTagClass}"/>
				</t:propertyLayout>
				<t:propertyLayout label="Obrigatório" bodyId="filterRequired" invertLabel="true" colspan="4" labelColspan="3">
					<n:input id="filterRequired" type="checkbox" class="${inputCheckTagClass}"/>
				</t:propertyLayout>

			</t:formTable>
		</n:panel>
		<n:panel title="Gráficos">
			<t:formTable columns="10">

				<n:panel colspan="6">
					<select id="charts" size="10" class="${inputSelectTagClass}">
					</select>
				</n:panel>
				<n:panel class="botoesVerticais" colspan="2">
					<button onclick="cwizz.show();" class="${btnStyleClass}">Criar Gráfico</button>
					<button onclick="cwizz.edit(ReportDesigner.getInstance().getSelectedChartConfiguration())" class="${btnStyleClass}">Editar Gráfico</button>
					<button onclick="ReportDesigner.getInstance().removeSelectedChart()" class="${btnStyleClass}">Remover Gráfico</button>
				</n:panel>

			</t:formTable>
		</n:panel>
		<n:panel title="Cálculos">
			<t:formTable columns="10">

				<n:panel colspan="6">
					<n:input id="calculatedFields" name="calculatedFields" type="select-many" itens="${emptyList}" useType="java.lang.String" size="10" />
				</n:panel>
				<n:panel class="botoesVerticais" colspan="2">
					<button title="" onclick="ReportDesigner.getInstance().showAddCalculatedProperty();" class="${btnStyleClass}">Adicionar Cálculo</button>
					<button title="" onclick="ReportDesigner.getInstance().editCalculatedProperty();" class="${btnStyleClass}">Editar Cálculo</button>
					<button title="" onclick="ReportDesigner.getInstance().removeSelectedCalculatedProperty();" class="${btnStyleClass}">Remover Cálculo</button>
				</n:panel>

			</t:formTable>
		</n:panel>
	</n:tabPanel>
	<textarea readonly="readonly" rows="20" cols="120" id="xml" style="text-transform: none;" class="xmlinputstyle" name="reportXml"></textarea>
</div>

<div style="display: none">

	<div id="propertiesWizzard">
		<t:formTable columns="12">
			<n:panel colspan="12">
				Escolha as propriedades que deseja usar no relatório:<br>
				<n:group columns="12">
					<n:panel style="height: 40em; overflow: scroll;" colspan="12">
						<rg:chooseProperties />
					</n:panel>
				</n:group>
			</n:panel>
		</t:formTable>
	</div>

	<div id="chartWizzard">
		<div id="chartWizzard_page_1">
			<t:formTable columns="12">
				<n:panel colspan="12">
					Escolha o tipo do gráfico:
				</n:panel>
				<n:panel style="display: flex; justify-content: center; gap: 4em;" colspan="12">
					<div>
						<input type="radio" name="chartType" value="pie" id="chartTypePie" checked="checked" class="${inputCheckTagClass}" />
						<label for="chartTypePie">
							<img src="${app}/resource/org/nextframework/report/generator/mvc/resource/pieChart.png" style="width: 64px; height: 64px;" align="middle">
							Pizza
						</label>
					</div>
					<div>
						<input type="radio" name="chartType" value="column" id="chartTypeColumn" class="${inputCheckTagClass}" />
						<label for="chartTypeColumn">
							<img src="${app}/resource/org/nextframework/report/generator/mvc/resource/columnChart.png" style="width: 64px; height: 64px;" align="middle">
							Colunas
						</label>
					</div>
					<div>
						<input type="radio" name="chartType" value="line" id="chartTypeLine" class="${inputCheckTagClass}" />
						<label for="chartTypeLine">
							<img src="${app}/resource/org/nextframework/report/generator/mvc/resource/lineChart.png" style="width: 64px; height: 64px;" align="middle">
							Linhas
						</label>
					</div>
				</n:panel>
			</t:formTable>
		</div>
		<div id="chartWizzard_page_2" style="display: none">
			<t:formTable columns="12">
				
				<t:propertyLayout label="Título do gráfico" bodyId="chartTitle" colspan="12">
					<input type="text" id="chartTitle" class="${inputTagClass}" />
				</t:propertyLayout>

				<t:propertyLayout label="Rítulo dos grupos (eixo horizontal)" bodyId="chartGroupTitle" colspan="4">
					<input type="text" id="chartGroupTitle" class="${inputTagClass}" />
				</t:propertyLayout>
				<t:propertyLayout label="Rítulo das series (eixo vertical)" bodyId="chartSeriesTitle" colspan="4">
					<input type="text" id="chartSeriesTitle" class="${inputTagClass}" />
				</t:propertyLayout>
				<t:propertyLayout label="Remover séries ou grupos em branco" bodyId="chartIgnoreEmptySeriesAndGroups" invertLabel="true" colspan="4" labelColspan="3">
					<input type="checkbox" id="chartIgnoreEmptySeriesAndGroups" class="${inputCheckTagClass}"/>
				</t:propertyLayout>

				<n:panel colspan="12">
					<h5>Grupos</h5>
					Escolha um campo para formar o agrupamento do gráfico.
					Cada elemento do agrupamento será representado por uma fatia no gráfico de pizza, ou grupo no eixo horizontal de outros gráficos.
				</n:panel>

				<t:propertyLayout label="Agrupar por" bodyId="chartGroupBy" colspan="4">
					<select id="chartGroupBy" class="${inputSelectTagClass}"></select>
				</t:propertyLayout>
				<t:propertyLayout label="Nível" bodyId="chartGroupByLevel" colspan="4">
					<select id="chartGroupByLevel" class="${inputSelectTagClass}">
						<%-- values from Calendar class --%>
						<option value="1">Ano</option>
						<option value="2">Mês</option>
						<option value="5">Dia</option>
						<option value="11">Hora</option>
						<option value="12">Minuto</option>
					</select>
				</t:propertyLayout>

			</t:formTable>
			<t:formTable id="chartSeriesSectionType" columns="12">

				<n:panel colspan="12">
					<h5>Tipo de dados da série</h5>
				</n:panel>

				<t:propertyLayout label="Usar uma propriedade como série e outra como valor" bodyId="propertyTypeDefault" invertLabel="true" colspan="4" labelColspan="3">
					<input type="radio" name="seriesType" value="propertiesDefault" id="propertyTypeDefault" checked="checked" class="${inputCheckTagClass}"/>
				</t:propertyLayout>
				<t:propertyLayout label="Usar propriedades como série e valor" bodyId="propertyTypeAsSeries" invertLabel="true" colspan="4" labelColspan="3">
					<input type="radio" name="seriesType" value="propertiesAsSeries" id="propertyTypeAsSeries" class="${inputCheckTagClass}"/>
				</t:propertyLayout>

			</t:formTable>
			<div id="propertiesDefaultBlock">
				<t:formTable id="chartSeriesSection" columns="12">

					<n:panel colspan="12">
						<h5>Series</h5>
						As séries formam um segundo agrupamento, e são representadas pelas linhas, colunas ou barras do gráfico.
					</n:panel>

					<t:propertyLayout label="Séries" bodyId="chartSeries" colspan="4">
						<select id="chartSeries" class="${inputSelectTagClass}"></select>
					</t:propertyLayout>
					<n:panel stype="padding: 0px;" colspan="8">
						<t:formTable columns="12">
							<t:propertyLayout label="Mostrar todos os valores de série" bodyId="chartConfigLimitSeriesShowAll" invertLabel="true" colspan="12" labelColspan="11">
								<input name="chartConfigLimitSeries" id="chartConfigLimitSeriesShowAll" type="radio" value="showall" checked="checked" class="${inputCheckTagClass}"/>
							</t:propertyLayout>
							<t:propertyLayout label="Limitar o número de séries (mostrar os valores top)" bodyId="chartConfigLimitSeriesLimit" invertLabel="true" colspan="12" labelColspan="11">
								<input name="chartConfigLimitSeries" id="chartConfigLimitSeriesLimit" type="radio" value="limit" class="${inputCheckTagClass}"/>
							</t:propertyLayout>
							<t:propertyLayout label="Agrupar as séries excedentes (somar as séries menores em 'outros')" bodyId="chartConfigLimitSeriesGroup" invertLabel="true" colspan="12" labelColspan="11">
								<input name="chartConfigLimitSeries" id="chartConfigLimitSeriesGroup" type="radio" value="group" class="${inputCheckTagClass}"/>
							</t:propertyLayout>
						</t:formTable>
					</n:panel>

				</t:formTable>
				<t:formTable columns="12">

					<n:panel colspan="12">
						<h5>Valores</h5>
						Escolha um campo para formar os valores do gráfico.
						O valor será usado para definir o tamanho da fatia do grupo num gráfico de pizza, ou a altura de uma barra ou altura linha em outros gráficos.
					</n:panel>

					<t:propertyLayout label="Mostrar número de itens como valor" bodyId="chartCountTrue" invertLabel="true" colspan="4" labelColspan="3">
						<input type="radio" id="chartCountTrue" name="pieCount" value="true" checked="checked" class="${inputCheckTagClass}"/>
					</t:propertyLayout>
					<n:panel colspan="8" />

					<t:propertyLayout label="Valor" bodyId="chartCountFalse" invertLabel="true" colspan="4" labelColspan="3">
						<input type="radio" id="chartCountFalse" name="pieCount" value="false" class="${inputCheckTagClass}"/>
					</t:propertyLayout>
					<n:panel colspan="8" />

					<t:propertyLayout label="" bodyId="chartValue" renderAs="single" colspan="4">
						<select id="chartValue" class="${inputSelectTagClass}"></select>
					</t:propertyLayout>
					<t:propertyLayout label="" bodyId="chartAggregateType" renderAs="single" colspan="4">
						<select id="chartAggregateType" class="${inputSelectTagClass}">
							<option value="SUM">Soma</option>
							<option value="AVERAGE">Média</option>
							<option value="AVERAGENN">Média (não vazios)</option>
							<option value="MAX">Máximo</option>
							<option value="MIN">Mínimo</option>
						</select>
					</t:propertyLayout>

				</t:formTable>
			</div>
			<t:formTable id="propertiesAsSeriesBlock" style="display: none" columns="12">

				<n:panel colspan="12">
					<h5>Series</h5>
					Escolha as propriedades que formarão as séries do gráfico. Cada série será representada por uma barra ou linha.
				</n:panel>

				<n:panel colspan="12">
					<n:input name="chartPropertiesAsSeries" type="select-many-box" itens="${emptyList}" useType="java.lang.String" />
				</n:panel>

				<t:propertyLayout label="Função de Agregação" bodyId="chartAggregateTypeSerie" colspan="4">
					<select id="chartAggregateTypeSerie" class="${inputSelectTagClass}">
						<option value="SUM">Soma</option>
						<option value="AVERAGE">Média</option>
						<option value="AVERAGENN">Média (não vazios)</option>
						<option value="MAX">Máximo</option>
						<option value="MIN">Mínimo</option>
					</select>
				</t:propertyLayout>
				<t:propertyLayout label="Legenda" bodyId="chartLabelSerie" colspan="4">
					<input id="chartLabelSerie" class="${inputTagClass}" />
				</t:propertyLayout>

			</t:formTable>
		</div>
		<div id="chartWizzard_page_3" style="display: none">
			<t:formTable columns="12">

				<t:propertyLayout label="Título do gráfico" bodyId="chartTitleSpan">
					<span id="chartTitleSpan" class="${inputTagClass}"></span>
				</t:propertyLayout>

				<t:propertyLayout label="Grupos" bodyId="chartGroupSpan">
					<span id="chartGroupSpan" class="${inputTagClass}"></span>
				</t:propertyLayout>

				<t:propertyLayout label="Valores" bodyId="chartValueSpan">
					<span id="chartValueSpan" class="${inputTagClass}"></span>
				</t:propertyLayout>

			</t:formTable>
		</div>
	</div>

	<div id="calculatedPropertiesWizzard">
		<t:formTable columns="12">

			<t:propertyLayout label="Nome" bodyId="calculationDisplayName" colspan="12">
				<input type="text" name="calculationDisplayName" id="calculationDisplayName" value="" class="${inputTagClass}"/>
				<input type="hidden" name="calculationName" value="" readonly="readonly" />
			</t:propertyLayout>

			<t:propertyLayout label="Expressão" bodyId="calculationExpression" colspan="12">
				<input type="text" name="calculationExpression" id="calculationExpression" value="" readonly="readonly" class="${inputTagClass}"/>
			</t:propertyLayout>

			<t:propertyLayout label="Formatar como número" bodyId="calculationFormatAsNumber" invertLabel="true" colspan="6" labelColspan="5">
				<input type="radio" name="calculationFormatAs" id="calculationFormatAsNumber" value="number" checked="checked" class="${inputCheckTagClass}"/>
			</t:propertyLayout>
			<t:propertyLayout label="Formatar como tempo" bodyId="calculationFormatAsTime" invertLabel="true" colspan="6" labelColspan="5">
				<input type="radio" name="calculationFormatAs" id="calculationFormatAsTime" value="time" class="${inputCheckTagClass}"/>
			</t:propertyLayout>

			<n:panel colspan="6" />
			<t:propertyLayout label="" bodyId="c_fatd" renderAs="single" colspan="6">
				<select name="calculationFormatAsTimeDetail" id="c_fatd" class="${inputSelectTagClass}">
						<option value="minutes">Minutos</option>
						<option value="hours" selected="selected">Horas</option>
						<option value="days">Dias</option>
						<option value="months">Meses</option>
				</select>
			</t:propertyLayout>

			<t:propertyLayout label="Processadores" bodyId="calculationProcessor" colspan="12">
				<n:input name="calculationProcessor" id="calculationProcessor" type="select-many" itens="${reportCalculatedFieldsProcessor}" />
			</t:propertyLayout>

			<n:panel id="validationExpressionError" style="color: red;" colspan="12">
			</n:panel>

			<n:panel id="varDiv" class="botoesHorizontais" colspan="6">
				<button class="${btnStyleClass}">EXAMPLE</button>
			</n:panel>
			<n:panel class="botoesHorizontais" colspan="6">
				<button id="op_nmb" onclick="ReportDesigner.getInstance().appendNumberToExpression()" class="${btnStyleClass}" title="Insere um número digitado pelo usuário na expressão">Inserir Número</button>
				<button id="op_now" onclick="ReportDesigner.getInstance().appendToExpression('$N')" class="${btnStyleClass}" title="Insere a data atual na expressão">Inserir data atual</button>
				<div style="flex-basis: 100%;" />
				<button id="op_add" onclick="ReportDesigner.getInstance().appendToExpression('+')" class="${btnStyleClass}" title="Adição">&nbsp;&nbsp;+&nbsp;&nbsp;</button>
				<button id="op_sub" onclick="ReportDesigner.getInstance().appendToExpression('-')" class="${btnStyleClass}" title="Subtração">&nbsp;&nbsp;-&nbsp;&nbsp;</button>
				<button id="op_mlt" onclick="ReportDesigner.getInstance().appendToExpression('*')" class="${btnStyleClass}" title="Multiplicação">&nbsp;&nbsp;*&nbsp;&nbsp;</button>
				<button id="op_dvd" onclick="ReportDesigner.getInstance().appendToExpression('/')" class="${btnStyleClass}" title="Divisão">&nbsp;&nbsp;/&nbsp;&nbsp;</button>
				<button id="op_pop" onclick="ReportDesigner.getInstance().appendToExpression('(')" class="${btnStyleClass}" title="Abrir parênteses">&nbsp;&nbsp;(&nbsp;&nbsp;</button>
				<button id="op_pcl" onclick="ReportDesigner.getInstance().appendToExpression(')')" class="${btnStyleClass}" title="Fechar parênteses">&nbsp;&nbsp;)&nbsp;&nbsp;</button>
				<button id="op_bck" onclick="ReportDesigner.getInstance().appendToExpression('$B')" class="${btnStyleClass}" title="Apagar último item">&nbsp;&#8592;&nbsp;</button>
				<button id="op_cln" onclick="ReportDesigner.getInstance().appendToExpression('$C')" class="${btnStyleClass}" title="Limpar expressão">&nbsp;&nbsp;C&nbsp;&nbsp;</button>
			</n:panel>

		</t:formTable>
	</div>

</div>