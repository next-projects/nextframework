<%@ taglib prefix="n" uri="next"%>
<%@ taglib prefix="report-generator" uri="report-generator"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<style>
.xmlinputstyle {
	display:none;
}
</style>

<div id="designerArea" style="width: 880px">
<%--
	<div id="fieldArea" style="float:left">
		<div>Campos</div>
		<div id="fieldAreaBody"></div>
		<!-- <button onclick="window.generator.addGroup(selectedField.value)">Agrupar</button> -->
		<button id="addField" style="width: 200px;" onclick="ReportDesigner.getInstance().addCurrentSelectedField();">Adicionar Campo</button>
		<BR/>
		<button id="groupField" style="width: 200px;" onclick="ReportDesigner.getInstance().groupCurrentSelectedField();">Agrupar</button>
		<BR/>
		<button id="filterField" style="width: 200px;" onclick="ReportDesigner.getInstance().filterCurrentSelectedField();">Adicionar Filtro</button>
	</div>
		 --%>
 
	<div style="min-height: 166px;">
		<div id="reportDesign" class="reportDesignArea">
			<div class="reportDesignAreaInner">
				<table id="designTable" width="100%" cellpadding="0" cellspacing="0" style="table-layout: fixed">
				</table>
			</div>
		</div>
		<div >
			<div style="text-align: right; margin-left: 4px;">
				<button id="moveLeft" onclick="ReportDesigner.getInstance().moveItem(-1);">&lt;-</button>
				<button id="moveRight" onclick="ReportDesigner.getInstance().moveItem(1);">-&gt;</button>
				<button id="removeDefinitionItem" onclick="ReportDesigner.getInstance().removeSelectedDefinitionItem()">Remover</button>
			</div>
		</div>			
	</div>
	
	<div style="min-height: 170px;">
	<n:tabPanel id="designerTab" >
		<n:panel title="Configura��o">
			<div style="float:left">
				<table>
					<tr>
						<td>T�tulo do Relat�rio </td>
						<td><input size="38" id="reportTitle"/></td>
					</tr>
					<tr>
						<td>Tornar P�blico </td>
						<td>
							<n:input id="reportPublic" type="checkbox" name="reportPublic" value="${model.reportPublic}"/>
						</td>
					</tr>
				</table>
				<c:if test="${! empty customPageUrl}">
					<jsp:include page="${customPageUrl}" />
				</c:if>
			</div>
		</n:panel>
		<n:panel title="Campos">
			<div>
				<div style="float:left; width: 630px">
					<n:input name="fields" type="select-many-box" itens="${emptyList}" useType="java.lang.String" inputWidth="300"/>
					
					<div style="padding-top: 5px;">
						<button title="Permite reconfigurar os campos dispon�veis para construir o relat�rio."
						onclick="ReportDesigner.getInstance().showConfigureProperties();">Configurar campos</button>
					</div>					
				</div>
				<div style="float:left; padding-top: 20px; padding-bottom: 20px;">
					<!-- <div style="border-bottom: 1px solid #aaa">Propriedades</div> -->
					<div>
						<table>
							<tr style="display:none">
								<td style="width: 180px;">Legenda:</td><td><input id="label" size="60"/></td>
							</tr>
							<tr style="display:none">
								<td style="width: 180px;">Formato:</td>
								<td>
									<select id="patternDate" style="text-transform: none;">
										<option selected="selected" value="">Dia/M�s/Ano (padr�o)</option>
										<option value="dd/MM/yyyy HH:mm">Dia/M�s/Ano Hora:Minuto</option>
										<option value="dd/MM">Dia/M�s</option>
										<option value="MM/yyyy">M�s/Ano</option>
										<option value="MMM yyyy">M�s (abreviado) Ano</option>
										<option value="MMMMM 'de' yyyy">M�s Ano Extenso</option>
										<option value="yyyy">Ano</option>
										<option value="dd 'de' MMMMM">Dia/M�s Extenso</option>
										<option value="dd 'de' MMMMM 'de' yyyy">Dia/M�s/Ano Extenso</option>
									</select>
								</td>
							</tr>
							<tr style="display:none">
								<td style="width: 180px;">Formato (casas decimais):</td>
								<td>
									<select id="patternNumber" style="text-transform: none;">
										<option selected="selected" value="">Padr�o</option>
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
								</td>
							</tr>
							<tr style="display:none">
								<td colspan="2"><input type="checkbox" id="aggregate"/><label for="aggregate"> Agregar Valores</label></td>
							</tr>
							<tr style="display:none">
								<td style="width: 180px;"></td>
								<td>
									<select id="aggregateType">
										<option value="SUM">Soma</option>
										<option value="AVERAGE">M�dia</option>
										<option value="AVERAGENN">M�dia (n�o vazios)</option>
										<option value="MAX">M�ximo</option>
										<option value="MIN">M�nimo</option>
									</select>
								</td>
							</tr>
						</table>
						
					</div>
				</div>
			</div>
		</n:panel>		
		<n:panel title="Grupos">
			<div style="float: left; width: 630px">
				<n:input name="groups" type="select-many-box" itens="${emptyList}" useType="java.lang.String" inputWidth="300"/>
			</div>
			<div style="float:left; width: 400px; padding-top: 20px; padding-bottom: 20px;">
				<!-- <div style="border-bottom: 1px solid #aaa">Propriedades</div> -->
				<div>
					<table>
						<tr style="display: none">
							<td style="width: 100px;">Formato:</td>
							<td>
								<select id="patternDateGroup" style="text-transform: none;">
									<option selected="selected" value="">M�s/Ano (padr�o)</option>
									<option value="MMMMM 'de' yyyy">M�s/Ano Extenso</option>
									<option value="MMMMM">M�s</option>
									<option value="yyyy">Ano</option>
									<option value="ddddd">Dia da Semana</option>
									<option value="dd/MM/yyyy">Dia/M�s/Ano</option>
									<option value="dd 'de' MMMMM 'de' yyyy">Dia/M�s/Ano Extenso</option>
									<option value="HH">Hora</option>
								</select>
							</td>
						</tr>
					</table>
				</div>
			</div>
			<%--
			<div id="groupArea">
				<div>Grupos</div>
				<div id="groupAreaBody"></div>
				<button id="removeGroup" style="width: 200px;" onclick="ReportDesigner.getInstance().removeGroup();">Remover Grupo</button>
			</div>			
			 --%>
		</n:panel>
		<n:panel title="Filtros">
			<div style="float: left; width: 630px">
				<n:input name="filters" type="select-many-box" itens="${emptyList}" useType="java.lang.String" inputWidth="300"/>
			</div>
			<div style="float:left; width: 400px; padding-top: 20px; padding-bottom: 20px;">
				<div>
					<table>
						<tr style="display:none">
							<td><div style="width: 80px;">Legenda:</div></td><td><input id="filterLabel" size="60"/></td>
						</tr>
						<tr style="display:none">
							<td>
								Crit�rio fixo:
							</td>
							<td>
								<select id="filterFixedCriteria">
								</select>
							</td>
						</tr>
						<tr style="display:none">
							<td>
								Valor padr�o:
							</td>
							<td>
								<n:input id="filterPreSelectDate" itens="${reportFilterDateAutoFilterList}" type="${ReportFilterDateAutoFilterType}"/>
							</td>
						</tr>
						<tr style="display:none">
							<td>
								Valor padr�o:
							</td>
							<td>
								<select id="filterPreSelectEntity">
								</select>
							</td>
						</tr>						
						<tr style="display:none">
							<td colspan="2">
								<input type="checkbox" id="filterSelectMultiple"> 
								<label for="filterSelectMultiple" title="Evite usar em valores que podem ter listas muito grandes">M�ltiplos valores</label>
							</td>
						</tr>
						<tr style="display:none">
							<td colspan="2">
								<input type="checkbox" id="filterRequired">
								<label for="filterRequired">Obrigat�rio</label>
							</td>
						</tr>						
					</table>
				</div>
			</div>
			<%-- 
			<div id="filterArea">
				<div>Filtros</div>
				<div id="filterAreaBody"></div>
				<button id="removeFilter" style="width: 200px;" onclick="ReportDesigner.getInstance().removeFilter();">Remover Filtro</button>
			</div>			
			--%>
		</n:panel>
		<n:panel title="Gr�ficos">
			<div style="float:left">
				<div style="float: left">
					<select size="10" style="width: 200px;" id="charts">
					
					</select>
				</div>
				<div style="float:left; padding-left: 4px;">
					<button onclick="cwizz.show();" style="width:160px; margin-bottom: 2px;">Criar Gr�fico</button>
					<BR/>
					<button onclick="cwizz.edit(ReportDesigner.getInstance().getSelectedChartConfiguration())" style="width:160px; margin-bottom: 2px;">Editar Gr�fico</button>
					<BR/>
					<button onclick="ReportDesigner.getInstance().removeSelectedChart()" style="width:160px; margin-bottom: 2px;">Remover Gr�fico</button>
				</div>
			</div>
		</n:panel>	
		<n:panel title="C�lculos">
			<div style="float:left; width: 380px">
				<div style="float:left">
					<n:input name="calculatedFields" id="calculatedFields" type="select-many" itens="${emptyList}" useType="java.lang.String"
						style="width: 200px" size="10"/>
				</div>
				<div style="float:left;  padding-left: 4px;">
					<button title=""
						onclick="ReportDesigner.getInstance().showAddCalculatedProperty();" style="width:160px; margin-bottom: 2px;">Adicionar C�lculo</button>
					<BR/>
					<button title=""
						onclick="ReportDesigner.getInstance().editCalculatedProperty();" style="width:160px; margin-bottom: 2px;">Editar C�lculo</button>
					<BR/>
					<button title=""
						onclick="ReportDesigner.getInstance().removeSelectedCalculatedProperty();" style="width:160px; margin-bottom: 2px;">Remover C�lculo</button>
				</div>	
			</div>
		</n:panel>		
		<%--
		<n:panel title="XML">
		</n:panel>
		 --%>
	</n:tabPanel>
	</div>	
	<textarea readonly="readonly" rows="20" cols="120" id="xml" style="text-transform: none;" class="xmlinputstyle" name="reportXml"></textarea>
</div>

<div id="calculatedPropertiesWizzard" class="propertiesWizzard" style="display: none;  ">
	<div style="position: absolute; left: 0px; top: 0px; width: 100%; height: 100%; background-color: white" class="blockview">
	&nbsp;
	</div>
	<div class="propertiesBox">
		<div class="propertiesBody">
			<div class="propertiesWizzardTitle">
				<table width="100%" cellpadding="0" cellspacing="0">
					<tr>
						<td>Adicionar campo calculado</td>
						<td align="right"><button onclick="ReportDesigner.getInstance().hideAddCalculatedProperty();">X</button></td>
					</tr>
				</table>
				<div style="height: 500px; overflow: auto; font-weight: normal">
					<%--
					<div>
						<p><label><input type="radio" name="calculationType" value="custom" id="c_c" onclick="ReportDesigner.getInstance().selectTypeCalculatedProperty('custom');"/> Personalizado</label></p>
						<p><label><input type="radio" name="calculationType" value="system" id="c_s" onclick="ReportDesigner.getInstance().selectTypeCalculatedProperty('system');"/> Do Sistema</label></p>
					</div>
					 --%>
					<hr>
					<div id="c_customized">
						<div style="margin-top: 4px; margin-bottom: 12px;">
							<label>
								<div style="margin-bottom: 6px;">Nome</div>
								<input type="text" name="calculationDisplayName" value="" size="80"/>
								<input type="hidden" name="calculationName" value="" size="40" readonly="readonly" title="Nome que ser� utilizado no processamento do relat�rio"/>
							</label>
						</div>
						<div style="margin-top: 4px; margin-bottom: 12px;">
							<label>
								<div style="margin-bottom: 6px;">Express�o</div>
								<input type="text" name="calculationExpression" value="" size="80" readonly="readonly"/>
							</label>
						</div>
						<div style="margin-top: 4px; margin-bottom: 12px;">
							Formatar como <br> 
							<div style="margin: 6px;">
								<label><input type="radio" name="calculationFormatAs" id="calculationFormatAsNumber" value="number" checked="checked"/> N�mero</label>
							</div>
							<div style="margin: 6px;">
								<label>
									<input type="radio" name="calculationFormatAs" id="calculationFormatAsTime" value="time"/> Tempo &nbsp;&nbsp;
									<select name="calculationFormatAsTimeDetail" id="c_fatd">
										<option value="minutes">Minutos</option>
										<option value="hours" selected="selected">Horas</option>
										<option value="days">Dias</option>
										<option value="months">Meses</option>
									</select>
								</label>
							</div>
						</div>
						<div style="margin-top: 4px; margin-bottom: 12px;">
							<div style="margin-bottom: 6px;">Processadores</div> <n:input name="calculationProcessor" type="select-many" itens="${reportCalculatedFieldsProcessor}"/>
						</div>
						<div style="margin-top: 4px; margin-bottom: 12px;">
							<div id="validationExpressionError" style="color:red; min-height: 26px;">
							</div>
						</div>
						<div>

							<div style="float:left; width: 600px" id="varDiv">
								<button class="calculationButton calculationPropertyButton">EXAMPLE</button>
							</div>
							<div style="float:left; width: 200px; padding-left: 10px;">
								<button id="op_n" onclick="ReportDesigner.getInstance().appendNumberToExpression()"	class="calculationButton" style="width: 179px;" title="Insere um n�mero digitado pelo usu�rio na express�o">Inserir N�mero</button>
								<button id="op_a" onclick="ReportDesigner.getInstance().appendToExpression('+')"  	class="calculationButton" title="Adi��o">&nbsp;&nbsp;+&nbsp;&nbsp;</button>
								<button id="op_s" onclick="ReportDesigner.getInstance().appendToExpression('-')"  	class="calculationButton" title="Subtra��o">&nbsp;&nbsp;-&nbsp;&nbsp;</button>
								<button id="op_m" onclick="ReportDesigner.getInstance().appendToExpression('*')"  	class="calculationButton" title="Multiplica��o">&nbsp;&nbsp;*&nbsp;&nbsp;</button>
								<button id="op_d" onclick="ReportDesigner.getInstance().appendToExpression('/')"  	class="calculationButton" title="Divis�o">&nbsp;&nbsp;/&nbsp;&nbsp;</button>
								<button id="op_p" onclick="ReportDesigner.getInstance().appendToExpression('(')"  	class="calculationButton" title="Abrir par�nteses">&nbsp;&nbsp;(&nbsp;&nbsp;</button>
								<button id="op_f" onclick="ReportDesigner.getInstance().appendToExpression(')')"  	class="calculationButton" title="Fechar par�nteses">&nbsp;&nbsp;)&nbsp;&nbsp;</button>
								<button id="op_b" onclick="ReportDesigner.getInstance().appendToExpression('$B')" 	class="calculationButton" title="Apagar �ltimo item">&nbsp;&#8592;&nbsp;</button>
								<button id="op_c" onclick="ReportDesigner.getInstance().appendToExpression('$C')" 	class="calculationButton" title="Limpar express�o" style="color:#C72000">&nbsp;&nbsp;C&nbsp;&nbsp;</button>
							</div>
						</div>
					</div>
					<div id="c_system"  style="display:none">
						<p>Sistema</p>
					</div>
				</div>
			</div>		
			<div style="text-align:right">
				<button onclick="ReportDesigner.getInstance().saveCalculatedProperty();">Ok</button>
			</div>
		</div>
	</div>	
</div>

<div id="propertiesWizzard" class="propertiesWizzard" style="display: none; ">
	<div style="position: absolute; left: 0px; top: 0px; width: 100%; height: 100%; background-color: white" class="blockview">
	&nbsp;
	</div>
	<div class="propertiesBox">
		<div class="propertiesBody">
			<div class="propertiesWizzardTitle">
				<table width="100%" cellpadding="0" cellspacing="0">
					<tr>
						<td>Configurar Propriedades</td>
						<td align="right"><button onclick="ReportDesigner.getInstance().hideConfigureProperties();">X</button></td>
					</tr>
				</table>
				<div style="height: 400px; overflow: auto; font-weight: normal">
					<report-generator:chooseProperties/>
				</div>
			</div>		
			<div style="text-align:right">
				<button onclick="ReportDesigner.getInstance().saveConfigureProperties();">Ok</button>
			</div>
		</div>
	</div>
</div>

<div class="chartWizzard" id="chartWizzard" style="visibility: hidden;  ">
	<div style="position: absolute; left: 0px; top: 0px; width: 100%; height: 100%; background-color: white" class="blockview">
	&nbsp;
	</div>
	<div class="chartBox">
		<div class="chartBody">
			<div class="chartWizzardTitle">
				<table width="100%" cellpadding="0" cellspacing="0">
					<tr>
						<td>Configurar Gr�fico</td>
						<td align="right"><button onclick="cwizz.dismiss()">X</button></td>
					</tr>
				</table>
			</div>
			<div id="chartWizzard_page_1">
				<div class="spacer">Escolha o tipo do gr�fico:</div>
				<div>
					<div>
						<input type="radio" style="border: 0px;" name="chartType" value="pie" id="chartTypePie" checked="checked"/> 
						<label for="chartTypePie">
							<img src="${app}/resource/org/nextframework/report/generator/mvc/resource/pieChart.png" style="width: 64px; height: 64px;" align="middle">
							Pizza
						</label>
					</div>
					
					<div id="typeColumn">
						<input type="radio" style="border: 0px;" name="chartType" value="column" id="chartTypeColumn"/> 
						<label for="chartTypeColumn">
							<img src="${app}/resource/org/nextframework/report/generator/mvc/resource/columnChart.png" style="width: 64px; height: 64px;" align="middle">
							Colunas
						</label>
					</div>
					
					<div id="typeLine">
						<input type="radio" style="border: 0px;" name="chartType" value="line" id="chartTypeLine"/> 
						<label for="chartTypeLine">
							<img src="${app}/resource/org/nextframework/report/generator/mvc/resource/lineChart.png" style="width: 64px; height: 64px;" align="middle">
							Linhas
						</label>
					</div>
					
					<%--
					<div id="typeBar">
						<input type="radio" style="border: 0px;" name="chartType" value="bar" id="chartTypeBar"/> 
						<label for="chartTypeBar">
							<img src="${app}/resource/org/nextframework/report/generator/mvc/resource/barChart.png" style="width: 64px; height: 64px;" align="middle">
							Barras
						</label>
					</div>
					 --%>
				</div>
			</div>
			<div id="chartWizzard_page_2" style="display:none">
				<div class="spacer">
					<div id="chartLabelTypePie">
						<img src="${app}/resource/org/nextframework/report/generator/mvc/resource/pieChart.png" style="width: 64px; height: 64px;" align="middle"> Gr�fico de Pizza
					</div>
					<div id="chartLabelTypeColumn">
						<img src="${app}/resource/org/nextframework/report/generator/mvc/resource/columnChart.png" style="width: 64px; height: 64px;" align="middle"> Gr�fico de Colunas
					</div>
					<div id="chartLabelTypeLine">
						<img src="${app}/resource/org/nextframework/report/generator/mvc/resource/lineChart.png" style="width: 64px; height: 64px;" align="middle"> Gr�fico de Linhas
					</div>
				</div>
				<div class="chartPageSection">
					<div class="chartPageSectionTitle">Grupos</div>
					<div>
						Escolha um campo para formar o agrupamento do gr�fico. 
						Cada elemento do agrupamento ser� representado por uma fatia no gr�fico de pizza, ou grupo no eixo horizontal de outros gr�ficos.
						<div class="spacer">
							Agrupar por: 
							<select id="chartGroupBy">
								
							</select>
							<span style="padding-left: 4px;">
								<label for="chartGroupByLevel">N�vel</label>
								<span>
									<select id="chartGroupByLevel">
										<%-- values from Calendar class --%>
										<option value="1">Ano</option>
										<option value="2">M�s</option>
										<option value="5">Dia</option>
										<option value="11">Hora</option>
										<option value="12">Minuto</option>
									</select>
								</span>
							</span>
						</div>
					</div>
				</div>
				<div class="chartPageSection" id="chartSeriesSectionType">
					<div class="chartPageSectionTitle">Tipo de dados da s�rie</div>
					<div>
						<div class="spacer">
							<div>
								<input type="radio" style="border: 0px" name="seriesType" value="propertiesDefault" id="propertyTypeDefault" checked="checked"/>
								<label for="propertyTypeDefault">
									Usar uma propriedade como s�rie e outra como valor. 
								</label>
							</div>
							<div>
								<input type="radio" style="border: 0px" name="seriesType" value="propertiesAsSeries" id="propertyTypeAsSeries"/>
								<label for="propertyTypeAsSeries">
									Usar propriedades como s�rie e valor.
								</label>
							</div>
						</div>

					</div>
				</div>
				<div id="propertiesDefaultBlock">
					<div class="chartPageSection" id="chartSeriesSection">
						<div class="chartPageSectionTitle">Series</div>
						<div>
							As s�ries formam um segundo agrupamento, e s�o representadas pelas linhas, colunas ou barras do gr�fico.
							<div class="spacer">
								S�ries: 
								<select id="chartSeries">
									
								</select>
							</div>					
						</div>
						<div class="spacer">
							<div>
								<label><input name="chartConfigLimitSeries" id="chartConfigLimitSeriesShowAll" type="radio" value="showall" checked="checked"/> Mostrar todos os valores de s�rie</label>
							</div>
							<div>
								<label><input name="chartConfigLimitSeries" id="chartConfigLimitSeriesLimit"   type="radio" value="limit"/> Limitar o n�mero de s�ries (mostrar os valores top)</label>
							</div>
							<div>
								<label><input name="chartConfigLimitSeries" id="chartConfigLimitSeriesGroup"   type="radio" value="group"/> Agrupar as s�ries excedentes (somar as s�ries com menor valor em coluna 'outros')</label>
							</div>
						</div>
					</div>
					<div class="chartPageSection">
						<div class="chartPageSectionTitle">Valores</div>
						<div>
							Escolha um campo para formar os valores do gr�fico. 
							O valor ser� usado para definir o tamanho da fatia do grupo num gr�fico de pizza, ou a altura de uma barra ou altura linha em outros gr�ficos.
							<div class="spacer">
								<div>
									<input type="radio" style="border: 0px" checked="checked" name="pieCount" value="true" id="chartCountTrue"/>
									<label for="chartCountTrue">
										Mostrar n�mero de itens como valor.
									</label>
								</div>
								<div>
									<input type="radio" style="border: 0px;" name="pieCount" value="false"  id="chartCountFalse">
									<label for="chartCountFalse">
									Valor: 
									</label>
									<select id="chartValue"></select>
									Fun��o de Agrega��o:
									<select id="chartAggregateType">
										<option value="SUM">Soma</option>
										<option value="AVERAGE">M�dia</option>
										<option value="AVERAGENN">M�dia (n�o vazios)</option>
										<option value="MAX">M�ximo</option>
										<option value="MIN">M�nimo</option>
									</select>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div id="propertiesAsSeriesBlock" style="display:none">
					<div class="chartPageSection" id="chartPropertyAsSeriesSection">
						<div class="chartPageSectionTitle">Series</div>
						<div>
							Escolha as propriedades que formar�o as s�ries do gr�fico. Cada s�rie ser� representada por uma barra ou linha.
							<table>
								<tr>
									<td width="380px;">
										<div class="spacer" >
											<n:input name="chartPropertiesAsSeries" type="select-many-box" itens="${emptyList}" useType="java.lang.String"/>
										</div>
									</td>
									<td valign="top">
										<table id="chartAggregateTypeSerieDiv">
											<tr>
												<td>Fun��o de Agrega��o:</td>
												<td>
													<select id="chartAggregateTypeSerie">
														<option value="SUM">Soma</option>
														<option value="AVERAGE">M�dia</option>
														<option value="AVERAGENN">M�dia (n�o vazios)</option>
														<option value="MAX">M�ximo</option>
														<option value="MIN">M�nimo</option>
													</select>
												</td>
											</tr>
											<tr>
												<td>Legenda:</td>
												<td><input id="chartLabelSerie"/></td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</div>
					</div>
				</div>
				
				<div class="chartPageSection">
					<div class="chartPageSectionTitle">T�tulo</div>
					<table>
						<tr>
							<td>T�tulo do gr�fico:</td><td><input type="text" id="chartTitle" size="27"/></td>
						</tr>
						<tr>
							<td>R�tulo dos grupos (eixo horizontal): </td><td><input type="text" id="chartGroupTitle" size="27"/></td>
						</tr>
						<tr>
							<td>R�tulo das series (eixo vertical):</td><td> <input type="text" id="chartSeriesTitle" size="27"/></td>
						</tr>
					</table>	
				</div>
				<div class="chartPageSection">
					<div class="chartPageSectionTitle">Configura��o</div>
					<div class="spacer">
						<label><input type="checkbox" id="chartIgnoreEmptySeriesAndGroups"/> Remover s�ries ou grupos em branco.</label>
					</div>
				</div>
			</div>
			<div id="chartWizzard_page_3" style="display:none">
				<div class="spacer">
					<img src="${app}/resource/org/nextframework/report/generator/mvc/resource/pieChart.png" style="width: 64px; height: 64px;" align="middle"> Gr�fico de Pizza
				</div>
				<div class="chartPageSection">
					<div class="spacer">
						T�tulo: <span id="chartTitleSpan"></span>
					</div>
					<div class="spacer">
						Grupo: <span id="chartGroupSpan"></span>
					</div>
					<div class="spacer">
						Valor: <span id="chartValueSpan"></span>
					</div>
				</div>				
			</div>
			<div class="chartActionBar" >
				<button id="nextButton">Pr�ximo</button>
			</div>
		</div>
	</div>
</div>
