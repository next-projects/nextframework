<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="combo" uri="combo"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>

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