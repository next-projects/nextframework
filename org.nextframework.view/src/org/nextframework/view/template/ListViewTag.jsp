<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>

<t:view title="${listViewTag.title}">

	<input type="hidden" name="currentPage" value="0" />
	<input type="hidden" name="notFirstTime" value="true" />

	<c:if test="${listViewTag.showNewLink || !empty listViewTag.linkArea}">
		<div class="${listViewTag.linkBarStyleClass}">
			${listViewTag.invokeLinkArea}
			<c:if test="${listViewTag.showNewLink}">
				<n:link action="create">${listViewTag.newLinkLabel}</n:link>
			</c:if>
		</div>
	</c:if>

	<n:doBody />

</t:view>