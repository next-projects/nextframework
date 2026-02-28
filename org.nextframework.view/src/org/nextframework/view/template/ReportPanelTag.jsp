<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="n" uri="nextframework.tags.next"%>
<%@ taglib prefix="combo" uri="nextframework.tags.combo"%>
<%@ taglib prefix="t" uri="nextframework.tags.template"%>

<div class="${reportPanelTag.panelStyleClass}" style="${reportPanelTag.panelStyle}">

	<c:if test="${! empty reportPanelTag.sectionTitle }">
		<div class="${reportPanelTag.sectionTitleStyleClass}" style="${reportPanelTag.sectionTitleStyle}">${reportPanelTag.sectionTitle}</div>
	</c:if>

	<n:bean name="filter">

		<n:getContent tagName="actionPanelTag" vars="actionPanels">

			<div class="${reportPanelTag.bodyStyleClass}" style="${reportPanelTag.bodyStyle}">
				<n:doBody />
			</div>

			<div class="${reportPanelTag.actionBarStyleClass}" style="${reportPanelTag.actionBarStyle}">
				<c:forEach items="${actionPanels}" var="actionPanel">
					<div class="${filterPanelTag.actionBarItemStyleClass}">${actionPanel}</div>
				</c:forEach>
				<n:submit class="${reportPanelTag.buttonStyleClass}" action="${reportPanelTag.submitAction}" validate="true" confirmationScript="${reportPanelTag.submitConfirmationScript}">${reportPanelTag.submitLabel}</n:submit>
			</div>

		</n:getContent>

	</n:bean>

</div>