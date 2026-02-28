<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="n" uri="nextframework.tags.next"%>
<%@ taglib prefix="combo" uri="nextframework.tags.combo"%>
<%@ taglib prefix="t" uri="nextframework.tags.template"%>

<t:view title="${formViewTag.title}" useBean="${crudContext.beanName}" propertyMode="${param.ACTION=='view' ? 'output' : 'input'}">

	<c:if test="${param.fromInsertOne == 'true'}">
		<input type="hidden" name="fromInsertOne" value="true" />
	</c:if>

	<c:if test="${!empty formViewTag.linkArea || formViewTag.showNewLink || formViewTag.showListLink}">
		<div class="${formViewTag.linkBarStyleClass}">
			${formViewTag.invokeLinkArea}
			<c:if test="${formViewTag.showNewLink}">
				<n:link action="create" class="${formViewTag.linkStyleClass}">${formViewTag.newLinkLabel}</n:link>
			</c:if>
			<c:if test="${formViewTag.showListLink}">
				<n:link action="list" class="${formViewTag.linkStyleClass}">${formViewTag.listLinkLabel}</n:link>
			</c:if>
		</div>
	</c:if>

	<n:doBody />

</t:view>