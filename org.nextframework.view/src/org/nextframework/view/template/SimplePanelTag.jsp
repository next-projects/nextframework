<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="n" uri="nextframework.tags.next"%>
<%@ taglib prefix="t" uri="nextframework.tags.template"%>

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