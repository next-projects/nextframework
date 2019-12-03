<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="combo" uri="combo"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>

<n:bean name="${filterPanelTag.name}">

	<c:if test="${! empty filterPanelTag.sectionTitle }">
		<div class="sectionTitle">${filterPanelTag.sectionTitle}</div>
	</c:if>

	<div class="filterWindow">
		<n:doBody />
	</div>

</n:bean>