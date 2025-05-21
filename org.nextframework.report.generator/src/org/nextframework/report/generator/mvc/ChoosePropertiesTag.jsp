<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>

<script type="text/javascript" src="${app}/resource/org/nextframework/report/generator/mvc/resource/ChoosePropertiesTagUtil.js"></script>

<table id="propertiesTable">
	<tr data-forProperty="placeholder" style="display: none;" >
		<td></td>
		<td></td>
	</tr>
</table>

<script type="text/javascript">

	Util = ChoosePropertiesTagUtil;
	next.events.onLoad(function() {
		Util.install('${application}');
	});

	var avaiableProperties = {};
	<c:forEach items="${avaiableProperties}" var="property">
	avaiableProperties['${property}'] = ${propertyMetadata[property]['json']};
	</c:forEach>

	var propertiesTable = document.getElementById("propertiesTable");
	for ( var key in avaiableProperties) {

		var options = avaiableProperties[key];

		var row = propertiesTable.insertRow(-1);
		var td1 = row.insertCell(0);
		var td2 = row.insertCell(1);

		row.style.display = '';

		row.setAttribute('data-forProperty', key);
		td1.style = "padding-left:" + (options.propertyDepth * 1.5) + "em";
		td2.style = "padding-left: 5em";

		var img = document.createElement('img');
		img.id = "openCloseBtn";
		img.style = "width: 16px; height: 16px;  visibility: hidden";
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
		spanDesc.style = "margin-left: 0.5em;";
		td1.appendChild(spanDesc);

		if (options.dateType) {
			td2.innerHTML = "Data";
		} else if (options.money) {
			td2.innerHTML = "Dinheiro";
		} else if (options.numberType) {
			td2.innerHTML = "Número";
		} else if (options.entity) {
			td2.innerHTML = "Entidade";
		} else if (options.enumType) {
			td2.innerHTML = "Enumeração: " + options.enumExample;
		} else if (options.type == 'java.lang.String') {
			td2.innerHTML = "Texto";
		}

		if (options.filterable && !options.columnable) {
			td2.innerHTML += " [apenas filtragem]";
		}
		if (!options.filterable) {
			td2.innerHTML += " [não filtrável]";
		}

	}

</script>