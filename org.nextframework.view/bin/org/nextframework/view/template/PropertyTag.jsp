<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="next"%>
<%@ taglib prefix="combo" uri="combo"%>
<%@ taglib prefix="t" uri="template"%>

<%-- Ao utilizar PropertyTagFastRenderer, o renderizador pode renderizar a tag apenas em código java e o template não ser chamado nesse caso.
     As modificações nesse arquivo devem ser refletidas no fastRenderer para manter a coerencia na renderização --%>

<n:property name="${Tproperty.name}">
	${Tproperty.idConfig} <%-- Le os atributos da propriedade para saber se é ID --%>
	<c:choose>
		<c:when test="${Tproperty.renderAs == 'column'}">
			<%-- column --%>		
			<n:column>
				<c:if test="${Tproperty.entityId}">
					<n:header style="width: 1%; padding-right: 3px;${Tproperty.headerStyle}" class="${Tproperty.headerStyleClass}">
						${Tproperty.header}
					</n:header>
				</c:if>
				<c:if test="${!Tproperty.entityId}">
					<n:header style="${Tproperty.headerStyle}" class="${Tproperty.headerStyleClass}">
						${n:default(Tproperty.header, label)}
					</n:header>
				</c:if>				
				<n:body align="${Tproperty.columnAlign}" style="${Tproperty.bodyStyle}" class="${Tproperty.bodyStyleClass}">
					<c:if test="${Tproperty.mode == 'input'}">
						
						<n:input pattern="${Tproperty.pattern}" selectOneWindowSize="${Tproperty.selectOneWindowSize}" selectOnePathParameters="${Tproperty.selectOnePathParameters}" insertPath="${Tproperty.insertPath}" holdValue="${Tproperty.holdValue}" showRemoverButton="${Tproperty.showRemoverButton}" optionalParams="${Tproperty.optionalParams}" transientFile="${Tproperty.transientFile}" autoSugestUniqueItem="${Tproperty.autoSugestUniqueItem}" onLoadItens="${Tproperty.onLoadItens}" useAjax="${Tproperty.useAjax}" showLabel="${Tproperty.showLabel}"  type="${Tproperty.type}" reloadOnChange="${Tproperty.reloadOnChange}" trueFalseNullLabels="${Tproperty.trueFalseNullLabels}" selectOnePath="${Tproperty.selectOnePath}" itens="${Tproperty.itens}"
							label="${n:default(label, Tproperty.label)}" selectLabelProperty="${Tproperty.selectLabelProperty}" includeBlank="${Tproperty.includeBlank}" blankLabel="${Tproperty.blankLabel}" cols="${Tproperty.cols}" rows="${Tproperty.rows}" write="${Tproperty.write}" dynamicAttributesMap="${Tproperty.dynamicAttributesMap}"
							id="${compId}">
							<n:doBody />
						</n:input>
					</c:if>
					<c:if test="${Tproperty.mode == 'output'}">
						<n:output pattern="${Tproperty.pattern}" style="${Tproperty.dynamicAttributesMap['style']}" styleClass="${Tproperty.dynamicAttributesMap['styleclass']}" trueFalseNullLabels="${Tproperty.trueFalseNullLabels}" escapeHTML="true"/>
					</c:if>
				</n:body>
			</n:column>
		</c:when>
		<c:when test="${Tproperty.entityId && empty value}">
			<%-- id --%>
			<%-- Se a propriedade for ID e for nula, não escrever nada... Se for ID mas tiver valor, cai nas outras opções
			<c:if test="${Tproperty.mode == 'input'}">
				<n:input pattern="${Tproperty.pattern}" selectOnePathParameters="${Tproperty.selectOnePathParameters}" insertPath="${Tproperty.insertPath}"  holdValue="${Tproperty.holdValue}"  showRemoverButton="${Tproperty.showRemoverButton}" optionalParams="${Tproperty.optionalParams}"  transientFile="${Tproperty.transientFile}" autoSugestUniqueItem="${Tproperty.autoSugestUniqueItem}" onLoadItens="${Tproperty.onLoadItens}" useAjax="${Tproperty.useAjax}" showLabel="false" type="${Tproperty.type}" reloadOnChange="${Tproperty.reloadOnChange}" trueFalseNullLabels="${Tproperty.trueFalseNullLabels}" selectOnePath="${Tproperty.selectOnePath}" itens="${Tproperty.itens}"
					includeBlank="${Tproperty.includeBlank}" cols="${Tproperty.cols}" rows="${Tproperty.rows}" write="${Tproperty.write}" dynamicAttributesMap="${Tproperty.dynamicAttributesMap}">
					<n:doBody />
				</n:input>
			</c:if>
			<c:if test="${Tproperty.mode == 'output'}">
				<n:output trueFalseNullLabels="${Tproperty.trueFalseNullLabels}"/>
			</c:if>
			--%>
		</c:when>
		<c:when test="${Tproperty.renderAs == 'double'}">
			<%-- double --%>		
			<n:panel style="${Tproperty.labelStyle}" class="${Tproperty.labelStyleClass}" >
					<label for="${compId}"><n:output pattern="${Tproperty.pattern}" trueFalseNullLabels="${Tproperty.trueFalseNullLabels}" value="${n:default(label, Tproperty.label)}" /></label> 
			</n:panel>
			<n:panel colspan="${Tproperty.colspan}" style="${Tproperty.panelStyle}" class="${Tproperty.panelStyleClass}">
				<c:if test="${Tproperty.mode == 'input'}">
					<n:input pattern="${Tproperty.pattern}"  selectOneWindowSize="${Tproperty.selectOneWindowSize}"  selectOnePathParameters="${Tproperty.selectOnePathParameters}" insertPath="${Tproperty.insertPath}"  holdValue="${Tproperty.holdValue}" showRemoverButton="${Tproperty.showRemoverButton}" optionalParams="${Tproperty.optionalParams}"  transientFile="${Tproperty.transientFile}" autoSugestUniqueItem="${Tproperty.autoSugestUniqueItem}" onLoadItens="${Tproperty.onLoadItens}" useAjax="${Tproperty.useAjax}" showLabel="${Tproperty.showLabel}" type="${Tproperty.type}" reloadOnChange="${Tproperty.reloadOnChange}" trueFalseNullLabels="${Tproperty.trueFalseNullLabels}" selectOnePath="${Tproperty.selectOnePath}" itens="${Tproperty.itens}"
						label="${n:default(Tproperty.label, label)}" id="${compId}" selectLabelProperty="${Tproperty.selectLabelProperty}" includeBlank="${Tproperty.includeBlank}" blankLabel="${Tproperty.blankLabel}" cols="${Tproperty.cols}" rows="${Tproperty.rows}" write="${Tproperty.write}" dynamicAttributesMap="${Tproperty.dynamicAttributesMap}">
						<n:doBody />
					</n:input>
				</c:if>
				<c:if test="${Tproperty.mode == 'output'}">
					<n:output  pattern="${Tproperty.pattern}" styleClass="${Tproperty.dynamicAttributesMap['styleclass']}" style="${Tproperty.dynamicAttributesMap['style']}" trueFalseNullLabels="${Tproperty.trueFalseNullLabels}" escapeHTML="${Tproperty.type=='html'? false: true}"/>
				</c:if>
			</n:panel>
		</c:when>
		<c:otherwise>
			<%-- single --%>
			<n:panel colspan="${Tproperty.colspan}" style="${Tproperty.panelStyle}" class="${Tproperty.panelStyleClass}">
				<c:if test="${Tproperty.mode == 'input'}">
					<n:input pattern="${Tproperty.pattern}" selectOneWindowSize="${Tproperty.selectOneWindowSize}"  selectOnePathParameters="${Tproperty.selectOnePathParameters}" insertPath="${Tproperty.insertPath}"  holdValue="${Tproperty.holdValue}" showRemoverButton="${Tproperty.showRemoverButton}" optionalParams="${Tproperty.optionalParams}"  transientFile="${Tproperty.transientFile}" autoSugestUniqueItem="${Tproperty.autoSugestUniqueItem}" onLoadItens="${Tproperty.onLoadItens}" useAjax="${Tproperty.useAjax}" labelStyle="${Tproperty.labelStyle}" labelStyleClass="${Tproperty.labelStyleClass}" showLabel="${Tproperty.showLabel}" type="${Tproperty.type}" reloadOnChange="${Tproperty.reloadOnChange}" trueFalseNullLabels="${Tproperty.trueFalseNullLabels}" selectOnePath="${Tproperty.selectOnePath}" itens="${Tproperty.itens}"
						label="${n:default(label, Tproperty.label)}&nbsp;${labelseparator}" selectLabelProperty="${Tproperty.selectLabelProperty}" includeBlank="${Tproperty.includeBlank}" blankLabel="${Tproperty.blankLabel}" cols="${Tproperty.cols}" rows="${Tproperty.rows}" write="${Tproperty.write}" id="${compId}" dynamicAttributesMap="${Tproperty.dynamicAttributesMap}">
						<n:doBody />
		 			</n:input>
				</c:if>
				<c:if test="${Tproperty.mode == 'output' && (empty Tproperty.write || Tproperty.write)}">
					<c:if test="${Tproperty.showLabel}">
						<n:panel>
						<n:output pattern="${Tproperty.pattern}" value="${n:default(label, Tproperty.label)} ${labelseparator}" style="${Tproperty.labelStyle}" styleClass="${Tproperty.labelStyleClass}" trueFalseNullLabels="${Tproperty.trueFalseNullLabels}" escapeHTML="false"/>															
						<n:output pattern="${Tproperty.pattern}" styleClass="${Tproperty.dynamicAttributesMap['styleclass']}" style="${Tproperty.dynamicAttributesMap['style']}" trueFalseNullLabels="${Tproperty.trueFalseNullLabels}" escapeHTML="true"/>										
						</n:panel>
					</c:if>
					<c:if test="${!Tproperty.showLabel}">
						<n:output pattern="${Tproperty.pattern}" styleClass="${Tproperty.dynamicAttributesMap['styleclass']}" style="${Tproperty.dynamicAttributesMap['style']}" trueFalseNullLabels="${Tproperty.trueFalseNullLabels}" escapeHTML="${Tproperty.type=='html'? false: true}"/>					
					</c:if>					
				</c:if>
			</n:panel>
		</c:otherwise>
	</c:choose>
</n:property>
