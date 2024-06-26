<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>

<div class="${simplePanelTag.panelStyleClass}" style="${simplePanelTag.panelStyle}">

	<c:if test="${! empty simplePanelTag.sectionTitle }">
		<div class="${simplePanelTag.sectionTitleStyleClass}" style="${simplePanelTag.sectionTitleStyle}">${simplePanelTag.sectionTitle}</div>
	</c:if>

	<n:getContent tagName="actionPanelTag" vars="actionPanels">

		<div class="${simplePanelTag.bodyStyleClass}" style="${simplePanelTag.bodyStyle}">
			<n:doBody />
		</div>

		<c:if test="${!empty actionPanels}">
			<div class="${simplePanelTag.actionBarStyleClass}" style="${simplePanelTag.actionBarStyle}">
				<c:forEach items="${actionPanels}" var="actionPanel">
					<div class="${filterPanelTag.actionBarItemStyleClass}">${actionPanel}</div>
				</c:forEach>
			</div>
		</c:if>

	</n:getContent>

</div>