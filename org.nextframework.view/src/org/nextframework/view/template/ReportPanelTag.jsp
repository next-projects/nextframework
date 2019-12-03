<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="combo" uri="combo"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>

<n:bean name="filter">

	<c:if test="${! empty reportPanelTag.sectionTitle }">
		<div class="sectionTitle">${reportPanelTag.sectionTitle}</div>
	</c:if>

	<div class="filterWindow">
		<n:doBody />
		<div class="actionBar">
			<n:submit action="${reportPanelTag.submitAction}" validate="true" confirmationScript="${reportPanelTag.submitConfirmationScript}">${reportPanelTag.submitLabel}</n:submit>
		</div>
	</div>

</n:bean>