<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="next"%>
<%@ taglib prefix="t" uri="template"%>

<script type="text/javascript" src="${app}/resource/org/nextframework/report/generator/mvc/resource/ChoosePropertiesTagUtil.js"></script>

Escolha as propriedades que deseja usar no relatório:<BR/>
<div style="border: 1px inset #CCCCCC">
	<table cellspacing="0" cellpadding="2" id="propertiesTable">
		<tr data-forProperty="placeholder">
			<td style="min-width: 480px"></td>
			<td></td>
		</tr>
	</table>
</div>

<script type="text/javascript">

	Util = ChoosePropertiesTagUtil;
	next.events.onLoad(function(){
		Util.install('${application}');
	});
	
	var avaiableProperties = {};
	<c:forEach items="${avaiableProperties}" var="property">
	avaiableProperties['${property}'] = ${propertyMetadata[property]['json']};</c:forEach>
	
	var propertiesTable = document.getElementById("propertiesTable");
	for (var key in avaiableProperties) {
		var options = avaiableProperties[key];
		
		var row = propertiesTable.insertRow(-1);
		var td1 = row.insertCell(0);
		var td2 = row.insertCell(1);
		
		row.setAttribute('data-forProperty', key);
		td1.style="padding-left:" + (options.propertyDepth*16) + "px";
		td2.style="color: #777";
		
		var img = document.createElement('img');
		img.id = "openCloseBtn";
		img.style = "width: 16px; height: 16px; visibility: hidden";
		img.src = next.http.getApplicationContext() + "/resource/org/nextframework/report/renderer/html/resource/mais.gif";
		td1.appendChild(img);
		
		var inputCheckbox = document.createElement("input");
		inputCheckbox.id = "selProp_" + key;
		inputCheckbox.type = "checkbox";
		inputCheckbox.name = "properties";
		inputCheckbox.value = key;
		inputCheckbox.propertyMetadata = options;
		td1.appendChild(inputCheckbox);
		
		var spanDesc = document.createElement("span");
		spanDesc.title = options.displayName;
		spanDesc.innerHTML = options.displayNameSimple;
		td1.appendChild(spanDesc);
		
		if (options.dateType) {
			td2.innerHTML = "Data";
		}else if (options.money) {
			td2.innerHTML = "Dinheiro";
		}else if (options.numberType) {
			td2.innerHTML = "Número";
		}else if (options.entity) {
			td2.innerHTML = "Entidade";
		}else if (options.enumType) {
			td2.innerHTML = "Enumeração: " + options.enumExample;
		}else if (options.type == 'java.lang.String') {
			td2.innerHTML = "Texto";
		}
		
	}
	
</script>