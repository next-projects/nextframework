<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="combo" uri="combo"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>

<t:view title="${formViewTag.title}" useBean="${crudContext.beanName}" propertyMode="${param.ACTION=='view' ? 'output' : 'input'}">

	<c:if test="${param.fromInsertOne == 'true'}">
		<input type="hidden" name="fromInsertOne" value="true" />
	</c:if>

	<c:if test="${formViewTag.showListLink || !empty formViewTag.linkArea}">
		<div class="${formViewTag.linkBarStyleClass}">
			${formViewTag.invokeLinkArea}
			<c:if test="${formViewTag.showListLink}">
				<n:link action="list" class="${formViewTag.linkStyleClass}">${formViewTag.listLinkLabel}</n:link>
			</c:if>
		</div>
	</c:if>

	<n:doBody />

</t:view>