<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="n" uri="nextframework.tags.next"%>
<%@ taglib prefix="t" uri="nextframework.tags.template"%>

<div class="${filterPanelTag.panelStyleClass}" style="${filterPanelTag.panelStyle}">

	<c:if test="${! empty filterPanelTag.sectionTitle }">
		<div class="${filterPanelTag.sectionTitleStyleClass}" style="${filterPanelTag.sectionTitleStyle}">${filterPanelTag.sectionTitle}</div>
	</c:if>

	<n:bean name="${filterPanelTag.name}" bypass="${empty filterPanelTag.name}">

		<n:getContent tagName="actionPanelTag" vars="actionPanels">

			<div class="${filterPanelTag.bodyStyleClass}" style="${filterPanelTag.bodyStyle}">
				<n:doBody />
			</div>

			<c:if test="${filterPanelTag.showSubmit || !empty actionPanels}">
				<div class="${filterPanelTag.actionBarStyleClass}" style="${filterPanelTag.actionBarStyle}">
					<c:forEach items="${actionPanels}" var="actionPanel">
						<div class="${filterPanelTag.actionBarItemStyleClass}">${actionPanel}</div>
					</c:forEach>
					<c:if test="${filterPanelTag.showSubmit}">
						<n:submit class="${filterPanelTag.buttonStyleClass}" url="${filterPanelTag.submitUrl}" action="${filterPanelTag.submitAction}" parameters="${filterPanelTag.submitParameters}" validate="${filterPanelTag.validateForm}">${filterPanelTag.submitLabel}</n:submit>
					</c:if>
				</div>
			</c:if>

		</n:getContent>

	</n:bean>

</div>