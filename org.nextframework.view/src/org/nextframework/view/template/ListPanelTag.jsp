<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>

<div class="${listPanelTag.panelStyleClass}">

	<c:if test="${! empty listPanelTag.sectionTitle }">
		<div class="${listPanelTag.sectionTitleStyleClass}">${listPanelTag.sectionTitle}</div>
	</c:if>

	<div class="${listPanelTag.bodyStyleClass}">
		<n:doBody />
	</div>

	<c:if test="${!empty acoes}">
		<div class="${listPanelTag.actionBarStyleClass}">
			${acoes}
		</div>
	</c:if>

</div>