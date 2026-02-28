<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="n" uri="nextframework.tags.next"%>
<%@ taglib prefix="t" uri="nextframework.tags.template"%>

<t:view title="${listViewTag.title}">

	<input type="hidden" name="currentPage" value="0" />
	<input type="hidden" name="notFirstTime" value="true" />

	<c:if test="${listViewTag.showNewLink || !empty listViewTag.linkArea}">
		<div class="${listViewTag.linkBarStyleClass}">
			${listViewTag.invokeLinkArea}
			<c:if test="${listViewTag.showNewLink}">
				<n:link action="create" class="${listViewTag.linkStyleClass}">${listViewTag.newLinkLabel}</n:link>
			</c:if>
		</div>
	</c:if>

	<n:doBody />

</t:view>