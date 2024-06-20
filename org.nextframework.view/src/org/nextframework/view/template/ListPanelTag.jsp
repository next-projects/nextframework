<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>

<div class="${listPanelTag.panelStyleClass}" style="${listPanelTag.panelStyle}">

	<c:if test="${! empty listPanelTag.sectionTitle }">
		<div class="${listPanelTag.sectionTitleStyleClass}" style="${listPanelTag.sectionTitleStyle}">${listPanelTag.sectionTitle}</div>
	</c:if>

	<n:getContent tagName="actionPanelTag" vars="actionPanels">

		<div class="${listPanelTag.bodyStyleClass}" style="${listPanelTag.bodyStyle}">
			<n:doBody />
		</div>

		<c:if test="${!empty actionPanels}">
			<div class="${listPanelTag.actionBarStyleClass}" style="${listPanelTag.actionBarStyle}">
				<c:forEach items="${actionPanels}" var="actionPanel">
					<div class="${filterPanelTag.actionBarItemStyleClass}">${actionPanel}</div>
				</c:forEach>
			</div>
		</c:if>

	</n:getContent>

</div>