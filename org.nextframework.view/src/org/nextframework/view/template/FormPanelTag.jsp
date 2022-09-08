<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>

<div class="${formPanelTag.panelStyleClass}">

	<c:if test="${! empty formPanelTag.sectionTitle }">
		<div class="${formPanelTag.sectionTitleStyleClass}">${formPanelTag.sectionTitle}</div>
	</c:if>

	<n:getContent tagName="actionPanelTag" vars="acoes">

		<div class="${formPanelTag.bodyStyleClass}">
			<n:doBody />
		</div>

		<c:if test="${formPanelTag.showSubmit || !empty acoes}">
			<div class="${formPanelTag.actionBarStyleClass}">
				${acoes}
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