<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="next"%>
<%@ taglib prefix="t" uri="template"%>
<%@ taglib prefix="report-generator" uri="report-generator"%>

		
		<script type="text/javascript" src="${app}/resource/org/nextframework/report/generator/mvc/resource/Selectable.js"></script>
		<script type="text/javascript" src="${app}/resource/org/nextframework/report/generator/mvc/resource/SelectView.js"></script>
		<script type="text/javascript" src="${app}/resource/org/nextframework/report/generator/mvc/resource/AbstractManager.js"></script>
		<script type="text/javascript" src="${app}/resource/org/nextframework/report/generator/mvc/resource/ReportGeneratorSelectView.js"></script>
		<script type="text/javascript" src="${app}/resource/org/nextframework/report/generator/mvc/resource/ReportGeneratorSelectManyBoxView.js"></script>
		<script type="text/javascript" src="${app}/resource/org/nextframework/report/generator/mvc/resource/ReportDesigner.js"></script>
		<script type="text/javascript" src="${app}/resource/org/nextframework/report/generator/mvc/resource/ReportDefinition.js"></script>
		<script type="text/javascript" src="${app}/resource/org/nextframework/report/generator/mvc/resource/ReportElement.js"></script>
		<script type="text/javascript" src="${app}/resource/org/nextframework/report/generator/mvc/resource/ChartWizzard.js"></script>
		<script type="text/javascript" src="${app}/resource/org/nextframework/report/generator/mvc/resource/ReportPropertyConfigUtils.js"></script>
		
		<link rel="StyleSheet" href="${app}/resource/org/nextframework/report/generator/mvc/resource/report-generator.css" type="text/css">

		<script type="text/javascript">
			function aplicaInputCaixaAltaAutomaticaHabilitado(){
				return false;
			}
		</script>
		<style>
			INPUT[type=text], option {
				text-transform: none;
			}
			
			.calculationButton {
				min-width: 36px; 
				min-height: 36px; 
				margin: 4px 8px 4px 0px;
				background-image: none;
				background-color: white;
				border: 1px solid gray;
				color: black;
				font-size: 14px;
			}
			.calculationPropertyButton {
				min-width: 190px;
			}
		</style>		

<t:tela useBean="model">
	
	<n:content id="context">
		<n:link url="${crudPath}">Voltar para Relatórios</n:link>
	</n:content>

	<div style="border-bottom: 1px solid gray; margin-bottom: 6px; padding-bottom: 3px; float:left">
		<div style="padding: 6px;">
			<img src="${app}/resource/org/nextframework/report/generator/mvc/resource/table-icon.png" style="padding-left: 3px; padding-right: 3px;"/>
			${reportTypeDisplayName}
		</div>
		<report-generator:designer/>
	</div>
	<div style="clear:left">
 		<n:submit action="saveReport">Salvar Relatório</n:submit>
	</div>
	<%--
	<n:submit action="showFilterView">Show Filter View</n:submit>
	 --%>
	<t:property name="selectedType" type="hidden"/>
	
	<t:property name="id" type="hidden"/>
</t:tela>

<script type="text/javascript">
	designer = ReportDesigner.getInstance();
	designer.controllerPath = "${controllerPath}";
	<c:forEach items="${avaiableProperties}" var="property">
		designer.addAvaiableProperty('${property}');
	</c:forEach>
	<c:forEach items="${model.properties}" var="property">
		designer.addField('${property}', ${propertyMetadata[property]['json']});
		<c:if test="${!propertyMetadata[property]['calculated']}">
			document.getElementById('selProp_${property}').disabled = true;
			document.getElementById('selProp_${property}').title = 'Essa propriedade não pode ser removida.';
		</c:if>
		<c:if test="${propertyMetadata[property]['calculated']}">
			designer.addCalculation('${property}', ${propertyMetadata[property]['json']});
			designer.addAvaiableProperty('${property}');
		</c:if>
	</c:forEach>
	<c:forEach items="${items}" var="item">
		<%--//designer.layoutManager.addFieldDetailWithConfig('${item.name}', ${propertyMetadata[item.name]['json']}, '${item.label}', "${item.pattern}", ${item.aggregateField}, '${item.aggregateType}');--%>
		designer.layoutManager.selectElement('${item.name}', ${propertyMetadata[item.name]['json']}, '${item.label}', "${item.pattern}", ${item.aggregateField}, '${item.aggregateType}');
	</c:forEach>	
	<c:forEach items="${groups}" var="group">
		<%--//designer.groupManager.addElement('${group.name}', ${propertyMetadata[group.name]['json']});--%>
		designer.groupManager.selectElement('${group.name}', "${group.pattern}");
	</c:forEach>
	<c:forEach items="${filters}" var="filter">
		<%--//designer.filterManager.addElement('${filter.name}', ${propertyMetadata[filter.name]['json']});--%>
		designer.filterManager.selectElement('${filter.name}');
	</c:forEach>

	designer.setDataSourceHibernate('${model.selectedType.name}');

	designer.setReportTitle('${model.reportTitle}');

	cwizz = ChartWizzard.createInstance(); 
		//new ChartWizzard(document.getElementById("chartWizzard"), designer); 
		//ChartWizzard.setup("chartWizzard", designer);

	var chartConfig;
	<c:forEach items="${charts}" var="chart">
		chartConfig = new ChartConfiguration(
					"${chart.type}", 
					"${chart.groupProperty}", 
					"${chart.groupLevel}",
					"${chart.seriesProperty}", 
					"${chart.valueProperty}", 
					"${chart.valueAggregate}", 
					"${chart.title}",
					"${chart.groupTitle}",
					"${chart.seriesTitle}",
					"${chart.propertiesAsSeries}",
					"${chart.seriesLimitType}",
					${chart.ignoreEmptySeriesAndGroups}
					);
		<c:forEach items="${chart.series}" var="serie">
			chartConfig.addSerie("${serie.property}", "${serie.aggregateFunction}", "${serie.label}");
		</c:forEach>
		designer.addChart(chartConfig);
	</c:forEach>

</script>
<%--
<pre>
<n:output value="${model.reportXml}" escapeHTML="true"/>
</pre>
 --%>