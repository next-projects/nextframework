<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>

<div class="${listPanelTag.panelStyleClass}" style="${listPanelTag.panelStyle}">

	<c:if test="${! empty listPanelTag.sectionTitle }">
		<div class="${listPanelTag.sectionTitleStyleClass}" style="${listPanelTag.sectionTitleStyle}">${listPanelTag.sectionTitle}</div>
	</c:if>

	<div class="${listPanelTag.bodyStyleClass}" style="${listPanelTag.bodyStyle}">
		<n:doBody />
	</div>

	<c:if test="${!empty acoes}">
		<div class="${listPanelTag.actionBarStyleClass}" style="${listPanelTag.actionBarStyle}">
			${acoes}
		</div>
	</c:if>

</div>