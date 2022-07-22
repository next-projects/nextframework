<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>

<n:bean name="${filterPanelTag.name}">

	<c:if test="${! empty filterPanelTag.sectionTitle }">
		<div class="${filterPanelTag.sectionTitleStyleClass}">${filterPanelTag.sectionTitle}</div>
	</c:if>

	<n:getContent tagName="actionPanelTag" vars="acoes">

		<div class="${filterPanelTag.panelStyleClass}">

			<n:doBody />

			<c:if test="${filterPanelTag.showSubmit || !empty acoes}">
				<div class="${filterPanelTag.actionBarStyleClass}">
					${acoes}
					<c:if test="${filterPanelTag.showSubmit}">
						<n:submit class="${filterPanelTag.buttonStyleClass}" url="${filterPanelTag.submitUrl}" action="${filterPanelTag.submitAction}" validate="${filterPanelTag.validateForm}">${filterPanelTag.submitLabel}</n:submit>
					</c:if>
				</div>
			</c:if>

		</div>

	</n:getContent>

</n:bean>