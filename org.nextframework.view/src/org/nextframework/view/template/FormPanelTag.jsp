<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="n" uri="nextframework.tags.next"%>
<%@ taglib prefix="t" uri="nextframework.tags.template"%>

<div class="${formPanelTag.panelStyleClass}" style="${formPanelTag.panelStyle}">

	<c:if test="${! empty formPanelTag.sectionTitle }">
		<div class="${formPanelTag.sectionTitleStyleClass}" style="${formPanelTag.sectionTitleStyle}">${formPanelTag.sectionTitle}</div>
	</c:if>

	<n:getContent tagName="actionPanelTag" vars="actionPanels">

		<div class="${formPanelTag.bodyStyleClass}" style="${formPanelTag.bodyStyle}">
			<n:doBody />
		</div>

		<c:if test="${formPanelTag.showSubmit || !empty actionPanels}">
			<div class="${formPanelTag.actionBarStyleClass}" style="${formPanelTag.actionBarStyle}">
				<c:forEach items="${actionPanels}" var="actionPanel">
					<div class="${filterPanelTag.actionBarItemStyleClass}">${actionPanel}</div>
				</c:forEach>
				<c:if test="${formPanelTag.showSubmit}">
					<c:if test="${param.ACTION == 'view'}">
						<n:submit class="${formPanelTag.buttonStyleClass}" id="do_editar_submit" action="update" validate="false">${formPanelTag.updateLinkLabel}</n:submit>
					</c:if>
					<c:if test="${param.ACTION != 'view'}">
						<n:submit class="${formPanelTag.buttonStyleClass}" id="do_${formPanelTag.submitAction}_submit" action="${formPanelTag.submitAction}" validate="true" confirmationScript="${formPanelTag.submitConfirmationScript}">${formPanelTag.submitLabel}</n:submit>
					</c:if>
				</c:if>
			</div>
		</c:if>

	</n:getContent>

</div>