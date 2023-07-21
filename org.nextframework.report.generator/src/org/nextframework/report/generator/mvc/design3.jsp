<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>
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

<t:view useBean="model">

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
				<report-generator:designer />
			</n:panel>
		</t:formTable>
		<t:actionPanel>
			<n:submit action="saveReport">Salvar Relatório</n:submit>
		</t:actionPanel>
	</t:simplePanel>

	<t:property name="selectedType" type="hidden" />
	<t:property name="id" type="hidden" />

</t:view>

<script type="text/javascript">

	designer = ReportDesigner.getInstance();
	designer.controllerPath = "${controllerPath}";
	<c:forEach items="${avaiableProperties}" var="property">
		designer.addAvaiableProperty('${property}');
	</c:forEach>
	<c:forEach items="${model.properties}" var="property">
		designer.addField('${property}', ${propertyMetadata[property]['json']});
		<c:if test="${!propertyMetadata[property]['calculated']}">
			{
				var comp = document.getElementById('selProp_${property}');
				if (comp == null) {
					alert('O campo ${property} especificado no XML não existe mais!');
				}else{
					document.getElementById('selProp_${property}').disabled = true;
					document.getElementById('selProp_${property}').title = 'Essa propriedade não pode ser removida.';
				}
			}
		</c:if>
		<c:if test="${propertyMetadata[property]['calculated']}">
			designer.addCalculation('${property}', ${propertyMetadata[property]['json']});
			designer.addAvaiableProperty('${property}');
		</c:if>
	</c:forEach>
	<c:forEach items="${items}" var="item">
		designer.layoutManager.selectElement('${item.name}', ${propertyMetadata[item.name]['json']}, '${item.label}', "${item.pattern}", ${item.aggregateField}, '${item.aggregateType}');
	</c:forEach>	
	<c:forEach items="${groups}" var="group">
		designer.groupManager.selectElement('${group.name}', "${group.pattern}");
	</c:forEach>
	<c:forEach items="${filters}" var="filter">
		designer.filterManager.selectElement('${filter.name}');
	</c:forEach>

	designer.setDataSourceHibernate('${model.selectedType.name}');

	designer.setReportTitle('${model.reportTitle}');

	cwizz = ChartWizzard.createInstance(); 

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