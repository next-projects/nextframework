<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>

<%-- Ao utilizar PropertyTagFastRenderer, o renderizador pode renderizar a tag apenas em c�digo java e o template n�o ser chamado nesse caso.
     As modifica��es nesse arquivo devem ser refletidas no fastRenderer para manter a coerencia na renderiza��o --%>

<n:property name="${propertyTag.name}">
	${propertyTag.idConfig} <%-- Le os atributos da propriedade para saber se � ID --%>
	<c:choose>

		<%-- column --%>
		<c:when test="${propertyTag.renderAs == 'column'}">
			<n:column>
				<c:if test="${propertyTag.entityId}">
					<n:header style="width: 1%; padding-right: 3px;${propertyTag.headerStyle}" class="${propertyTag.headerStyleClass}">
						${propertyTag.header}
					</n:header>
				</c:if>
				<c:if test="${!propertyTag.entityId}">
					<n:header style="${propertyTag.headerStyle}" class="${propertyTag.headerStyleClass}">
						${n:default(propertyTag.header, label)}
					</n:header>
				</c:if>
				<n:body align="${propertyTag.columnAlign}" style="${propertyTag.bodyStyle}" class="${propertyTag.bodyStyleClass}">
					<c:if test="${propertyTag.mode == 'input'}">
						<n:input class="form-control" pattern="${propertyTag.pattern}" selectOneWindowSize="${propertyTag.selectOneWindowSize}" selectOnePathParameters="${propertyTag.selectOnePathParameters}" insertPath="${propertyTag.insertPath}" holdValue="${propertyTag.holdValue}" showDeleteButton="${propertyTag.showDeleteButton}" optionalParams="${propertyTag.optionalParams}" transientFile="${propertyTag.transientFile}" autoSugestUniqueItem="${propertyTag.autoSugestUniqueItem}" onLoadItens="${propertyTag.onLoadItens}" useAjax="${propertyTag.useAjax}" showLabel="${propertyTag.showLabel}"  type="${propertyTag.type}" reloadOnChange="${propertyTag.reloadOnChange}" trueFalseNullLabels="${propertyTag.trueFalseNullLabels}" selectOnePath="${propertyTag.selectOnePath}" itens="${propertyTag.itens}"
							label="${n:default(label, propertyTag.label)}" selectLabelProperty="${propertyTag.selectLabelProperty}" includeBlank="${propertyTag.includeBlank}" blankLabel="${propertyTag.blankLabel}" cols="${propertyTag.cols}" rows="${propertyTag.rows}" write="${propertyTag.write}" dynamicAttributesMap="${propertyTag.dynamicAttributesMap}"
							id="${compId}">
							<n:doBody />
						</n:input>
					</c:if>
					<c:if test="${propertyTag.mode == 'output'}">
						<n:output pattern="${propertyTag.pattern}" style="${propertyTag.dynamicAttributesMap['style']}" styleClass="${propertyTag.dynamicAttributesMap['styleclass']}" trueFalseNullLabels="${propertyTag.trueFalseNullLabels}" replaceMessagesCodes="${propertyTag.replaceMessagesCodes}" escapeHTML="true"/>
					</c:if>
				</n:body>
			</n:column>
		</c:when>

		<%-- id --%>
		<c:when test="${propertyTag.entityId && empty value}">
			<%-- Se a propriedade for ID e for nula, n�o escrever nada... Se for ID mas tiver valor, cai nas outras op��es
			<c:if test="${propertyTag.mode == 'input'}">
				<n:input pattern="${propertyTag.pattern}" selectOnePathParameters="${propertyTag.selectOnePathParameters}" insertPath="${propertyTag.insertPath}"  holdValue="${propertyTag.holdValue}"  showDeleteButton="${propertyTag.showDeleteButton}" optionalParams="${propertyTag.optionalParams}"  transientFile="${propertyTag.transientFile}" autoSugestUniqueItem="${propertyTag.autoSugestUniqueItem}" onLoadItens="${propertyTag.onLoadItens}" useAjax="${propertyTag.useAjax}" showLabel="false" type="${propertyTag.type}" reloadOnChange="${propertyTag.reloadOnChange}" trueFalseNullLabels="${propertyTag.trueFalseNullLabels}" selectOnePath="${propertyTag.selectOnePath}" itens="${propertyTag.itens}"
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
			<n:panel style="${propertyTag.labelStyle}" class="${propertyTag.labelStyleClass}" >
					<label for="${compId}"><n:output pattern="${propertyTag.pattern}" trueFalseNullLabels="${propertyTag.trueFalseNullLabels}" value="${n:default(label, propertyTag.label)}" /></label>
			</n:panel>
			<n:panel colspan="${propertyTag.colspan}" style="${propertyTag.panelStyle}" class="${propertyTag.panelStyleClass}">
				<c:if test="${propertyTag.mode == 'input'}">
					<n:input pattern="${propertyTag.pattern}" selectOneWindowSize="${propertyTag.selectOneWindowSize}" selectOnePathParameters="${propertyTag.selectOnePathParameters}" insertPath="${propertyTag.insertPath}"  holdValue="${propertyTag.holdValue}" showDeleteButton="${propertyTag.showDeleteButton}" optionalParams="${propertyTag.optionalParams}"  transientFile="${propertyTag.transientFile}" autoSugestUniqueItem="${propertyTag.autoSugestUniqueItem}" onLoadItens="${propertyTag.onLoadItens}" useAjax="${propertyTag.useAjax}" showLabel="${propertyTag.showLabel}" type="${propertyTag.type}" reloadOnChange="${propertyTag.reloadOnChange}" trueFalseNullLabels="${propertyTag.trueFalseNullLabels}" selectOnePath="${propertyTag.selectOnePath}" itens="${propertyTag.itens}"
						label="${n:default(label, propertyTag.label)}" id="${compId}" selectLabelProperty="${propertyTag.selectLabelProperty}" includeBlank="${propertyTag.includeBlank}" blankLabel="${propertyTag.blankLabel}" cols="${propertyTag.cols}" rows="${propertyTag.rows}" write="${propertyTag.write}" dynamicAttributesMap="${propertyTag.dynamicAttributesMap}">
						<n:doBody />
					</n:input>
				</c:if>
				<c:if test="${propertyTag.mode == 'output'}">
					<n:output pattern="${propertyTag.pattern}" styleClass="${propertyTag.dynamicAttributesMap['styleclass']}" style="${propertyTag.dynamicAttributesMap['style']}" trueFalseNullLabels="${propertyTag.trueFalseNullLabels}" replaceMessagesCodes="${propertyTag.replaceMessagesCodes}" escapeHTML="${propertyTag.type=='html'? false: true}"/>
				</c:if>
			</n:panel>
		</c:when>

		<%-- form-group --%>
		<c:when test="${propertyTag.renderAs == 'form-group'}">
			<%-- form-group --%>
			<div class="form-group">
				<label class="control-label col-sm-2 ${propertyTag.labelStyleClass}" style="${propertyTag.labelStyle}" for="${compId}"><n:output pattern="${propertyTag.pattern}" trueFalseNullLabels="${propertyTag.trueFalseNullLabels}" value="${n:default(label, propertyTag.label)}" /></label> 
				<div class="col-sm-10 ${propertyTag.panelStyleClass}" style="${propertyTag.panelStyle}">
					<c:if test="${propertyTag.mode == 'input'}">
						<n:input class="form-control" pattern="${propertyTag.pattern}"  selectOneWindowSize="${propertyTag.selectOneWindowSize}"  selectOnePathParameters="${propertyTag.selectOnePathParameters}" insertPath="${propertyTag.insertPath}"  holdValue="${propertyTag.holdValue}" showRemoverButton="${propertyTag.showRemoverButton}" optionalParams="${propertyTag.optionalParams}"  transientFile="${propertyTag.transientFile}" autoSugestUniqueItem="${propertyTag.autoSugestUniqueItem}" onLoadItens="${propertyTag.onLoadItens}" useAjax="${propertyTag.useAjax}" showLabel="${propertyTag.showLabel}" type="${propertyTag.type}" reloadOnChange="${propertyTag.reloadOnChange}" trueFalseNullLabels="${propertyTag.trueFalseNullLabels}" selectOnePath="${propertyTag.selectOnePath}" itens="${propertyTag.itens}"
							label="${n:default(propertyTag.label, label)}" id="${compId}" selectLabelProperty="${propertyTag.selectLabelProperty}" includeBlank="${propertyTag.includeBlank}" blankLabel="${propertyTag.blankLabel}" cols="${propertyTag.cols}" rows="${propertyTag.rows}" write="${propertyTag.write}" dynamicAttributesMap="${propertyTag.dynamicAttributesMap}">
							<n:doBody />
						</n:input>
					</c:if>
					<c:if test="${propertyTag.mode == 'output'}">
						<n:output  pattern="${propertyTag.pattern}" styleClass="${propertyTag.dynamicAttributesMap['styleclass']}" style="${propertyTag.dynamicAttributesMap['style']}" trueFalseNullLabels="${propertyTag.trueFalseNullLabels}" escapeHTML="${propertyTag.type=='html'? false: true}"/>
					</c:if>
				</div>
			</div>		
		</c:when>

		<%-- single --%>
		<c:otherwise>
			<n:panel colspan="${propertyTag.colspan}" style="${propertyTag.panelStyle}" class="${propertyTag.panelStyleClass}">
				<c:if test="${propertyTag.mode == 'input'}">
					<n:input pattern="${propertyTag.pattern}" selectOneWindowSize="${propertyTag.selectOneWindowSize}"  selectOnePathParameters="${propertyTag.selectOnePathParameters}" insertPath="${propertyTag.insertPath}"  holdValue="${propertyTag.holdValue}" showDeleteButton="${propertyTag.showDeleteButton}" optionalParams="${propertyTag.optionalParams}"  transientFile="${propertyTag.transientFile}" autoSugestUniqueItem="${propertyTag.autoSugestUniqueItem}" onLoadItens="${propertyTag.onLoadItens}" useAjax="${propertyTag.useAjax}" labelStyle="${propertyTag.labelStyle}" labelStyleClass="${propertyTag.labelStyleClass}" showLabel="${propertyTag.showLabel}" type="${propertyTag.type}" reloadOnChange="${propertyTag.reloadOnChange}" trueFalseNullLabels="${propertyTag.trueFalseNullLabels}" selectOnePath="${propertyTag.selectOnePath}" itens="${propertyTag.itens}"
						label="${n:default(label, propertyTag.label)}&nbsp;${labelseparator}" selectLabelProperty="${propertyTag.selectLabelProperty}" includeBlank="${propertyTag.includeBlank}" blankLabel="${propertyTag.blankLabel}" cols="${propertyTag.cols}" rows="${propertyTag.rows}" write="${propertyTag.write}" id="${compId}" dynamicAttributesMap="${propertyTag.dynamicAttributesMap}">
						<n:doBody />
		 			</n:input>
				</c:if>
				<c:if test="${propertyTag.mode == 'output' && (empty propertyTag.write || propertyTag.write)}">
					<c:if test="${propertyTag.showLabel}">
						<n:panel>
							<n:output pattern="${propertyTag.pattern}" value="${n:default(label, propertyTag.label)} ${labelseparator}" style="${propertyTag.labelStyle}" styleClass="${propertyTag.labelStyleClass}" trueFalseNullLabels="${propertyTag.trueFalseNullLabels}" escapeHTML="false"/>
							<n:output pattern="${propertyTag.pattern}" styleClass="${propertyTag.dynamicAttributesMap['styleclass']}" style="${propertyTag.dynamicAttributesMap['style']}" trueFalseNullLabels="${propertyTag.trueFalseNullLabels}" replaceMessagesCodes="${propertyTag.replaceMessagesCodes}" escapeHTML="true"/>
						</n:panel>
					</c:if>
					<c:if test="${!propertyTag.showLabel}">
						<n:output pattern="${propertyTag.pattern}" styleClass="${propertyTag.dynamicAttributesMap['styleclass']}" style="${propertyTag.dynamicAttributesMap['style']}" trueFalseNullLabels="${propertyTag.trueFalseNullLabels}" replaceMessagesCodes="${propertyTag.replaceMessagesCodes}" escapeHTML="${propertyTag.type=='html'? false: true}"/>
					</c:if>
				</c:if>
			</n:panel>
		</c:otherwise>

	</c:choose>
</n:property>