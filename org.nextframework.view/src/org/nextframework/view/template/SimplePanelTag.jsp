<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>

<div class="${simplePanelTag.panelStyleClass}">

	<c:if test="${! empty simplePanelTag.sectionTitle }">
		<div class="${simplePanelTag.sectionTitleStyleClass}">${simplePanelTag.sectionTitle}</div>
	</c:if>

	<n:getContent tagName="actionPanelTag" vars="acoes">

		<div class="${simplePanelTag.bodyStyleClass}">
			<n:doBody />
		</div>

		<c:if test="${!empty acoes}">
			<div class="${simplePanelTag.actionBarStyleClass}">
				${acoes}
			</div>
		</c:if>

	</n:getContent>

</div>