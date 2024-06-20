<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>

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