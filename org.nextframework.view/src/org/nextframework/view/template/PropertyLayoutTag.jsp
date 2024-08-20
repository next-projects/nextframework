<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>

<c:choose>
	<%-- double --%>
	<c:when test="${propertyLayoutTag.renderAs == 'double'}">
		<n:panel id="l_${propertyLayoutTag.bodyId}" class="${propertyLayoutTag.labelPanelStyleClass}" style="${propertyLayoutTag.labelPanelStyle}" colspan="${propertyLayoutTag.labelColspan}">
			<c:if test="${! empty propertyLayoutTag.label}">
				<label for="${propertyLayoutTag.bodyId}" class="${propertyLayoutTag.labelStyleClass}" style="${propertyLayoutTag.labelStyle}"><n:output value="${propertyLayoutTag.label}" /></label>
			</c:if>
		</n:panel>
		<n:panel id="p_${propertyLayoutTag.bodyId}" class="${propertyLayoutTag.panelStyleClass}" style="${propertyLayoutTag.panelStyle}" colspan="${propertyLayoutTag.colspan}">
			<n:doBody />
		</n:panel>
	</c:when>
	<%-- invert --%>
	<c:when test="${propertyLayoutTag.renderAs == 'invert'}">
		<n:panel id="p_${propertyLayoutTag.bodyId}" class="${propertyLayoutTag.panelStyleClass}" style="${propertyLayoutTag.panelStyle}" colspan="${propertyLayoutTag.colspan}">
			<n:doBody />
		</n:panel>
		<n:panel id="l_${propertyLayoutTag.bodyId}" class="${propertyLayoutTag.labelPanelStyleClass}" style="${propertyLayoutTag.labelPanelStyle}" colspan="${propertyLayoutTag.labelColspan}">
			<c:if test="${! empty propertyLayoutTag.label}">
				<label for="${propertyLayoutTag.bodyId}" class="${propertyLayoutTag.labelStyleClass}" style="${propertyLayoutTag.labelStyle}"><n:output value="${propertyLayoutTag.label}" /></label>
			</c:if>
		</n:panel>
	</c:when>
	<%-- stacked --%>
	<c:when test="${propertyLayoutTag.renderAs == 'stacked'}">
		<n:panel id="p_${propertyLayoutTag.bodyId}" class="${propertyLayoutTag.panelStyleClass}" style="${propertyLayoutTag.panelStyle}" colspan="${propertyLayoutTag.colspan}">
			<c:if test="${! empty propertyLayoutTag.label}">
				<label for="${propertyLayoutTag.bodyId}" class="${propertyLayoutTag.labelStyleClass}" style="${propertyLayoutTag.labelStyle}"><n:output value="${propertyLayoutTag.label}" /></label>
			</c:if>
			<div class="${propertyLayoutTag.stackedPanelStyleClass}">
				<n:doBody />
			</div>
		</n:panel>
	</c:when>
	<%-- stacked invert --%>
	<c:when test="${propertyLayoutTag.renderAs == 'stacked_invert'}">
		<n:panel id="p_${propertyLayoutTag.bodyId}" class="${propertyLayoutTag.panelStyleClass}" style="${propertyLayoutTag.panelStyle}" colspan="${propertyLayoutTag.colspan}">
			<div class="${propertyLayoutTag.stackedPanelStyleClass}">
				<n:doBody />
				<c:if test="${! empty propertyLayoutTag.label}">
					<label for="${propertyLayoutTag.bodyId}" class="${propertyLayoutTag.labelStyleClass}" style="${propertyLayoutTag.labelStyle}"><n:output value="${propertyLayoutTag.label}" /></label>
				</c:if>
			</div>
		</n:panel>
	</c:when>
	<%-- single --%>
	<c:otherwise>
		<n:panel id="p_${propertyLayoutTag.bodyId}" class="${propertyLayoutTag.panelStyleClass}" style="${propertyLayoutTag.panelStyle}" colspan="${propertyLayoutTag.colspan}">
			<c:if test="${! empty propertyLayoutTag.label && propertyLayoutTag.showLabel}">
				<label for="${propertyLayoutTag.bodyId}" class="${propertyLayoutTag.labelStyleClass}" style="${propertyLayoutTag.labelStyle}"><n:output value="${propertyLayoutTag.label}" /></label>
			</c:if>
			<n:doBody />
		</n:panel>
	</c:otherwise>
</c:choose>