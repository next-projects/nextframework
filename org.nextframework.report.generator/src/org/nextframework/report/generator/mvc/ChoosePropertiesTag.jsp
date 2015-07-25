<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="next"%>
<%@ taglib prefix="t" uri="template"%>

<script type="text/javascript" src="${app}/resource/org/nextframework/report/generator/mvc/resource/ChoosePropertiesTagUtil.js"></script>

<script type="text/javascript">
Util = ChoosePropertiesTagUtil;
next.events.onLoad(function(){
	Util.install('${application}');
});
</script>

Escolha as propriedades que deseja usar no relatório:<BR/>
<div style="border: 1px inset #CCCCCC">
	<table cellspacing="0" cellpadding="2" id="propertiesTable">
		<tr data-forProperty="placeholder">
			<td style="min-width: 480px"></td>
			<td></td>
		</tr>
	<c:forEach items="${avaiableProperties}" var="property">
		<tr data-forProperty="${property}">
		<n:property name="properties">
			<td style="padding-left: ${propertyMetadata[property]['propertyDepth']*16}px; ">
				<img id="openCloseBtn" 
					 width="16px" height="16px"
					 style="visibility: hidden;"
					 src="${application}/resource/org/nextframework/report/renderer/html/resource/mais.gif" data-forProperty="${property}"/>
				<n:input type="checklist" value="${property}" itens="${value}" id="selProp_${property}"/> <span title="${propertyMetadata[property]['displayName']}">${propertyMetadata[property]['displayNameSimple']}</span>
				<script type="text/javascript">
					document.getElementById("selProp_${property}").propertyMetadata = ${propertyMetadata[property]['json']};
				</script>
			</td>
			<td>
				<span style="color: #777">
					<c:choose>
						<c:when test="${propertyMetadata[property]['dateType']}">
						Data
						</c:when>
						<c:when test="${propertyMetadata[property]['money']}">
						Dinheiro
						</c:when>
						<c:when test="${propertyMetadata[property]['numberType']}">
						Número
						</c:when>
						<c:when test="${propertyMetadata[property]['entity']}">
						Entidade
						</c:when>
						<c:when test="${propertyMetadata[property]['type'].simpleName == 'String' }">
						Texto
						</c:when>
						<c:otherwise>
							Enumeração:
							
							<c:forEach var="field" items="${propertyMetadata[property]['type'].enumConstants}">
								<n:output value="${field}"/> /
							</c:forEach>
							
						</c:otherwise>
					</c:choose>
				</span>
			</td>
		</n:property>
		</tr>
	</c:forEach>
	</table>
</div>