<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>

<%-- Ao utilizar PropertyTagFastRenderer, o renderizador pode renderizar a tag apenas em código java e o template não ser chamado nesse caso.
     As modificações nesse arquivo devem ser refletidas no fastRenderer para manter a coerencia na renderização --%>

<n:property name="${propertyTag.name}">
	${propertyTag.idConfig} <%-- Lê os atributos da propriedade para saber se é ID --%>
	<c:choose>
		<%-- column --%>
		<c:when test="${propertyTag.renderAs == 'column'}">
			<n:column>
				<c:if test="${propertyTag.entityId}">
					<n:header class="${propertyTag.headerStyleClass}" style="width: 1%; padding-right: 3px;${propertyTag.headerStyle}">
						${propertyTag.header}
					</n:header>
				</c:if>
				<c:if test="${!propertyTag.entityId}">
					<n:header class="${propertyTag.headerStyleClass}" style="${propertyTag.headerStyle}">
						${n:default(label, propertyTag.header)}
					</n:header>
				</c:if>
				<n:body align="${propertyTag.columnAlign}" class="${propertyTag.bodyStyleClass}" style="${propertyTag.bodyStyle}">
					<c:if test="${propertyTag.mode == 'input'}">
						<n:input pattern="${propertyTag.pattern}" selectOneWindowSize="${propertyTag.selectOneWindowSize}" selectOnePathParameters="${propertyTag.selectOnePathParameters}" insertPath="${propertyTag.insertPath}" holdValue="${propertyTag.holdValue}" showDeleteButton="${propertyTag.showDeleteButton}" optionalParams="${propertyTag.optionalParams}" transientFile="${propertyTag.transientFile}" autoSugestUniqueItem="${propertyTag.autoSugestUniqueItem}" onLoadItens="${propertyTag.onLoadItens}" useAjax="${propertyTag.useAjax}" showLabel="${propertyTag.showLabel}" type="${propertyTag.type}" reloadOnChange="${propertyTag.reloadOnChange}" trueFalseNullLabels="${propertyTag.trueFalseNullLabels}" selectOnePath="${propertyTag.selectOnePath}" itens="${propertyTag.itens}"
							label="${n:default(label, propertyTag.label)}" selectLabelProperty="${propertyTag.selectLabelProperty}" includeBlank="${propertyTag.includeBlank}" blankLabel="${propertyTag.blankLabel}" cols="${propertyTag.cols}" rows="${propertyTag.rows}" write="${propertyTag.write}" dynamicAttributesMap="${propertyTag.dynamicAttributesMap}"
							id="${compId}">
							<n:doBody />
						</n:input>
					</c:if>
					<c:if test="${propertyTag.mode == 'output'}">
						<n:output pattern="${propertyTag.pattern}" styleClass="${propertyTag.dynamicAttributesMap['styleclass']}" style="${propertyTag.dynamicAttributesMap['style']}" trueFalseNullLabels="${propertyTag.trueFalseNullLabels}" replaceMessagesCodes="${propertyTag.replaceMessagesCodes}" escapeHTML="true"/>
					</c:if>
				</n:body>
			</n:column>
		</c:when>
		<%-- id --%>
		<c:when test="${propertyTag.entityId && empty value}">
			<%-- Se a propriedade for ID e for nula, não escrever nada... Se for ID mas tiver valor, cai nas outras opções
			<c:if test="${propertyTag.mode == 'input'}">
				<n:input pattern="${propertyTag.pattern}" selectOnePathParameters="${propertyTag.selectOnePathParameters}" insertPath="${propertyTag.insertPath}" holdValue="${propertyTag.holdValue}" showDeleteButton="${propertyTag.showDeleteButton}" optionalParams="${propertyTag.optionalParams}" transientFile="${propertyTag.transientFile}" autoSugestUniqueItem="${propertyTag.autoSugestUniqueItem}" onLoadItens="${propertyTag.onLoadItens}" useAjax="${propertyTag.useAjax}" showLabel="false" type="${propertyTag.type}" reloadOnChange="${propertyTag.reloadOnChange}" trueFalseNullLabels="${propertyTag.trueFalseNullLabels}" selectOnePath="${propertyTag.selectOnePath}" itens="${propertyTag.itens}"
					includeBlank="${propertyTag.includeBlank}" cols="${propertyTag.cols}" rows="${propertyTag.rows}" write="${propertyTag.write}" dynamicAttributesMap="${propertyTag.dynamicAttributesMap}">
					<n:doBody />
				</n:input>
			</c:if>
			<c:if test="${propertyTag.mode == 'output'}">
				<n:output trueFalseNullLabels="${propertyTag.trueFalseNullLabels}"/>
			</c:if>
			--%>
		</c:when>
		<%-- double --%>
		<c:when test="${propertyTag.renderAs == 'double'}">
			<n:panel id="l_${compId}" class="${propertyTag.labelPanelStyleClass}" style="${propertyTag.labelPanelStyle}">
				<label for="${compId}" class="${propertyTag.labelStyleClass}" style="${propertyTag.labelStyle}"><n:output trueFalseNullLabels="${propertyTag.trueFalseNullLabels}" value="${n:default(label, propertyTag.label)}" /></label>
			</n:panel>
			<n:panel id="p_${compId}" class="${propertyTag.panelStyleClass}" style="${propertyTag.panelStyle}" colspan="${propertyTag.colspan}">
				<c:if test="${propertyTag.mode == 'input'}">
					<n:input pattern="${propertyTag.pattern}" selectOneWindowSize="${propertyTag.selectOneWindowSize}" selectOnePathParameters="${propertyTag.selectOnePathParameters}" insertPath="${propertyTag.insertPath}" holdValue="${propertyTag.holdValue}" showDeleteButton="${propertyTag.showDeleteButton}" optionalParams="${propertyTag.optionalParams}" transientFile="${propertyTag.transientFile}" autoSugestUniqueItem="${propertyTag.autoSugestUniqueItem}" onLoadItens="${propertyTag.onLoadItens}" useAjax="${propertyTag.useAjax}" showLabel="${propertyTag.showLabel}" type="${propertyTag.type}" reloadOnChange="${propertyTag.reloadOnChange}" trueFalseNullLabels="${propertyTag.trueFalseNullLabels}" selectOnePath="${propertyTag.selectOnePath}" itens="${propertyTag.itens}"
						label="${n:default(label, propertyTag.label)}" id="${compId}" selectLabelProperty="${propertyTag.selectLabelProperty}" includeBlank="${propertyTag.includeBlank}" blankLabel="${propertyTag.blankLabel}" cols="${propertyTag.cols}" rows="${propertyTag.rows}" write="${propertyTag.write}" dynamicAttributesMap="${propertyTag.dynamicAttributesMap}">
						<n:doBody />
					</n:input>
				</c:if>
				<c:if test="${propertyTag.mode == 'output'}">
					<n:output pattern="${propertyTag.pattern}" styleClass="${propertyTag.dynamicAttributesMap['styleclass']}" style="${propertyTag.dynamicAttributesMap['style']}" trueFalseNullLabels="${propertyTag.trueFalseNullLabels}" replaceMessagesCodes="${propertyTag.replaceMessagesCodes}" escapeHTML="${propertyTag.type=='html'? false: true}"/>
				</c:if>
			</n:panel>
		</c:when>
		<%-- invert --%>
		<c:when test="${propertyTag.renderAs == 'invert'}">
			<n:panel id="p_${compId}" class="${propertyTag.panelStyleClass}" style="${propertyTag.panelStyle}" colspan="${propertyTag.colspan}">
				<c:if test="${propertyTag.mode == 'input'}">
					<n:input pattern="${propertyTag.pattern}" selectOnePathParameters="${propertyTag.selectOnePathParameters}" insertPath="${propertyTag.insertPath}"  holdValue="${propertyTag.holdValue}" showDeleteButton="${propertyTag.showDeleteButton}" optionalParams="${propertyTag.optionalParams}"  transientFile="${propertyTag.transientFile}" autoSugestUniqueItem="${propertyTag.autoSugestUniqueItem}" onLoadItens="${propertyTag.onLoadItens}" useAjax="${propertyTag.useAjax}" showLabel="${propertyTag.showLabel}" type="${propertyTag.type}" reloadOnChange="${propertyTag.reloadOnChange}" trueFalseNullLabels="${propertyTag.trueFalseNullLabels}" selectOnePath="${propertyTag.selectOnePath}" itens="${propertyTag.itens}"
						label="${n:default(label, propertyTag.label)}" id="${compId}" selectLabelProperty="${propertyTag.selectLabelProperty}" includeBlank="${propertyTag.includeBlank}" blankLabel="${propertyTag.blankLabel}" cols="${propertyTag.cols}" rows="${propertyTag.rows}" write="${propertyTag.write}" dynamicAttributesMap="${propertyTag.dynamicAttributesMap}">
						<n:doBody />
					</n:input>
				</c:if>
				<c:if test="${propertyTag.mode == 'output'}">
					<n:output pattern="${propertyTag.pattern}" styleClass="${propertyTag.dynamicAttributesMap['styleclass']}" style="${propertyTag.dynamicAttributesMap['style']}" trueFalseNullLabels="${propertyTag.trueFalseNullLabels}" replaceMessagesCodes="${propertyTag.replaceMessagesCodes}" escapeHTML="${propertyTag.type=='html'? false: true}"/>
				</c:if>
			</n:panel>
			<n:panel id="l_${compId}" class="${propertyTag.labelPanelStyleClass}" style="${propertyTag.labelPanelStyle}">
				<label for="${compId}" class="${propertyTag.labelStyleClass}" style="${propertyTag.labelStyle}"><n:output trueFalseNullLabels="${propertyTag.trueFalseNullLabels}" value="${n:default(label, propertyTag.label)}" /></label>
			</n:panel>
		</c:when>
		<%-- stacked --%>
		<c:when test="${propertyTag.renderAs == 'stacked'}">
			<n:panel id="p_${compId}" class="${propertyTag.panelStyleClass}" style="${propertyTag.panelStyle}" colspan="${propertyTag.colspan}">
				<label for="${compId}" class="${propertyTag.labelStyleClass}" style="${propertyTag.labelStyle}"><n:output trueFalseNullLabels="${propertyTag.trueFalseNullLabels}" value="${n:default(label, propertyTag.label)}" /></label>
				<div class="${propertyTag.stackedPanelStyleClass}">
					<c:if test="${propertyTag.mode == 'input'}">
						<n:input pattern="${propertyTag.pattern}" selectOneWindowSize="${propertyTag.selectOneWindowSize}" selectOnePathParameters="${propertyTag.selectOnePathParameters}" insertPath="${propertyTag.insertPath}" holdValue="${propertyTag.holdValue}" showRemoverButton="${propertyTag.showRemoverButton}" optionalParams="${propertyTag.optionalParams}" transientFile="${propertyTag.transientFile}" autoSugestUniqueItem="${propertyTag.autoSugestUniqueItem}" onLoadItens="${propertyTag.onLoadItens}" useAjax="${propertyTag.useAjax}" showLabel="${propertyTag.showLabel}" type="${propertyTag.type}" reloadOnChange="${propertyTag.reloadOnChange}" trueFalseNullLabels="${propertyTag.trueFalseNullLabels}" selectOnePath="${propertyTag.selectOnePath}" itens="${propertyTag.itens}"
							label="${n:default(propertyTag.label, label)}" id="${compId}" selectLabelProperty="${propertyTag.selectLabelProperty}" includeBlank="${propertyTag.includeBlank}" blankLabel="${propertyTag.blankLabel}" cols="${propertyTag.cols}" rows="${propertyTag.rows}" write="${propertyTag.write}" dynamicAttributesMap="${propertyTag.dynamicAttributesMap}">
							<n:doBody />
						</n:input>
					</c:if>
					<c:if test="${propertyTag.mode == 'output'}">
						<n:output pattern="${propertyTag.pattern}" styleClass="${propertyTag.dynamicAttributesMap['styleclass']}" style="${propertyTag.dynamicAttributesMap['style']}" trueFalseNullLabels="${propertyTag.trueFalseNullLabels}" replaceMessagesCodes="${propertyTag.replaceMessagesCodes}" escapeHTML="${propertyTag.type=='html'? false: true}"/>
					</c:if>
				</div>
			</n:panel>
		</c:when>
		<%-- stacked invert --%>
		<c:when test="${propertyTag.renderAs == 'stacked_invert'}">
			<n:panel id="p_${compId}" class="${propertyTag.panelStyleClass}" style="${propertyTag.panelStyle}" colspan="${propertyTag.colspan}">
				<div class="${propertyTag.stackedPanelStyleClass}">
					<c:if test="${propertyTag.mode == 'input'}">
						<n:input pattern="${propertyTag.pattern}" selectOneWindowSize="${propertyTag.selectOneWindowSize}" selectOnePathParameters="${propertyTag.selectOnePathParameters}" insertPath="${propertyTag.insertPath}" holdValue="${propertyTag.holdValue}" showRemoverButton="${propertyTag.showRemoverButton}" optionalParams="${propertyTag.optionalParams}" transientFile="${propertyTag.transientFile}" autoSugestUniqueItem="${propertyTag.autoSugestUniqueItem}" onLoadItens="${propertyTag.onLoadItens}" useAjax="${propertyTag.useAjax}" showLabel="${propertyTag.showLabel}" type="${propertyTag.type}" reloadOnChange="${propertyTag.reloadOnChange}" trueFalseNullLabels="${propertyTag.trueFalseNullLabels}" selectOnePath="${propertyTag.selectOnePath}" itens="${propertyTag.itens}"
							label="${n:default(propertyTag.label, label)}" id="${compId}" selectLabelProperty="${propertyTag.selectLabelProperty}" includeBlank="${propertyTag.includeBlank}" blankLabel="${propertyTag.blankLabel}" cols="${propertyTag.cols}" rows="${propertyTag.rows}" write="${propertyTag.write}" dynamicAttributesMap="${propertyTag.dynamicAttributesMap}">
							<n:doBody />
						</n:input>
					</c:if>
					<c:if test="${propertyTag.mode == 'output'}">
						<n:output pattern="${propertyTag.pattern}" styleClass="${propertyTag.dynamicAttributesMap['styleclass']}" style="${propertyTag.dynamicAttributesMap['style']}" trueFalseNullLabels="${propertyTag.trueFalseNullLabels}" replaceMessagesCodes="${propertyTag.replaceMessagesCodes}" escapeHTML="${propertyTag.type=='html'? false: true}"/>
					</c:if>
					<label for="${compId}" class="${propertyTag.labelStyleClass}" style="${propertyTag.labelStyle}"><n:output trueFalseNullLabels="${propertyTag.trueFalseNullLabels}" value="${n:default(label, propertyTag.label)}" /></label>
				</div>
			</n:panel>
		</c:when>
		<%-- single --%>
		<c:otherwise>
			<n:panel id="p_${compId}" class="${propertyTag.panelStyleClass}" style="${propertyTag.panelStyle}" colspan="${propertyTag.colspan}">
				<c:if test="${propertyTag.mode == 'input'}">
					<n:input pattern="${propertyTag.pattern}" selectOneWindowSize="${propertyTag.selectOneWindowSize}" selectOnePathParameters="${propertyTag.selectOnePathParameters}" insertPath="${propertyTag.insertPath}" holdValue="${propertyTag.holdValue}" showDeleteButton="${propertyTag.showDeleteButton}" optionalParams="${propertyTag.optionalParams}" transientFile="${propertyTag.transientFile}" autoSugestUniqueItem="${propertyTag.autoSugestUniqueItem}" onLoadItens="${propertyTag.onLoadItens}" useAjax="${propertyTag.useAjax}" labelStyle="${propertyTag.labelStyle}" labelStyleClass="${propertyTag.labelStyleClass}" showLabel="${propertyTag.showLabel}" type="${propertyTag.type}" reloadOnChange="${propertyTag.reloadOnChange}" trueFalseNullLabels="${propertyTag.trueFalseNullLabels}" selectOnePath="${propertyTag.selectOnePath}" itens="${propertyTag.itens}"
						label="${n:default(label, propertyTag.label)} ${labelseparator}" selectLabelProperty="${propertyTag.selectLabelProperty}" includeBlank="${propertyTag.includeBlank}" blankLabel="${propertyTag.blankLabel}" cols="${propertyTag.cols}" rows="${propertyTag.rows}" write="${propertyTag.write}" id="${compId}" dynamicAttributesMap="${propertyTag.dynamicAttributesMap}">
						<n:doBody />
		 			</n:input>
				</c:if>
				<c:if test="${propertyTag.mode == 'output' && (empty propertyTag.write || propertyTag.write)}">
					<c:if test="${propertyTag.showLabel}">
						<n:output value="${n:default(label, propertyTag.label)} ${labelseparator}" styleClass="${propertyTag.labelStyleClass}" style="${propertyTag.labelStyle}" trueFalseNullLabels="${propertyTag.trueFalseNullLabels}" escapeHTML="false"/>
						<n:output pattern="${propertyTag.pattern}" styleClass="${propertyTag.dynamicAttributesMap['styleclass']}" style="${propertyTag.dynamicAttributesMap['style']}" trueFalseNullLabels="${propertyTag.trueFalseNullLabels}" replaceMessagesCodes="${propertyTag.replaceMessagesCodes}" escapeHTML="true"/>
					</c:if>
					<c:if test="${!propertyTag.showLabel}">
						<n:output pattern="${propertyTag.pattern}" styleClass="${propertyTag.dynamicAttributesMap['styleclass']}" style="${propertyTag.dynamicAttributesMap['style']}" trueFalseNullLabels="${propertyTag.trueFalseNullLabels}" replaceMessagesCodes="${propertyTag.replaceMessagesCodes}" escapeHTML="${propertyTag.type=='html'? false: true}"/>
					</c:if>
				</c:if>
			</n:panel>
		</c:otherwise>
	</c:choose>
</n:property>