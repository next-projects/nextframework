<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="combo" uri="combo"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>

<n:bean name="filter">

	<n:getContent tagName="actionPanelTag" vars="acoes">

		<div class="${reportPanelTag.panelStyleClass}">

			<c:if test="${! empty reportPanelTag.sectionTitle }">
				<div class="${reportPanelTag.sectionTitleStyleClass}">${reportPanelTag.sectionTitle}</div>
			</c:if>

			<n:doBody />

			<div class="${reportPanelTag.actionBarStyleClass}">
				${acoes}
				<n:submit action="${reportPanelTag.submitAction}" validate="true" confirmationScript="${reportPanelTag.submitConfirmationScript}">${reportPanelTag.submitLabel}</n:submit>
			</div>

		</div>

	</n:getContent>


</n:bean>