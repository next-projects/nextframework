<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="combo" uri="combo"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>

<div class="inputWindow">

	<c:set value="${tabNum != null ? tabNum + 1 : 0}" var="tabNum" scope="request" />
	<n:tabPanel id="janelaEntrada_${tabNum}">
		<n:doBody />
	</n:tabPanel>

	<c:if test="${formPanelTag.showSubmit}">
		<div class="actionBar">
			<c:if test="${param.ACTION == 'view'}">
				<n:submit id="do_editar_submit" action="update" validate="false">${formPanelTag.updateLinkLabel}</n:submit>
			</c:if>
			<c:if test="${param.ACTION != 'view'}">
				<n:submit id="do_${formPanelTag.submitAction}_submit" action="${formPanelTag.submitAction}" validate="true" confirmationScript="${formPanelTag.submitConfirmationScript}">${formPanelTag.submitLabel}</n:submit>
			</c:if>
		</div>
	</c:if>

</div>