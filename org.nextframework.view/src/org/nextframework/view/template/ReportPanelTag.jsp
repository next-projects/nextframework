<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="combo" uri="combo"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>

<div class="${reportPanelTag.panelStyleClass}">

	<c:if test="${! empty reportPanelTag.sectionTitle }">
		<div class="${reportPanelTag.sectionTitleStyleClass}">${reportPanelTag.sectionTitle}</div>
	</c:if>

	<n:bean name="filter">

		<n:getContent tagName="actionPanelTag" vars="acoes">

			<div class="${reportPanelTag.bodyStyleClass}">
				<n:doBody />
			</div>

			<div class="${reportPanelTag.actionBarStyleClass}">
				${acoes}
				<n:submit class="${reportPanelTag.buttonStyleClass}" action="${reportPanelTag.submitAction}" validate="true" confirmationScript="${reportPanelTag.submitConfirmationScript}">${reportPanelTag.submitLabel}</n:submit>
			</div>

		</n:getContent>

	</n:bean>

</div>