<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>

<c:if test="${!empty blocks && (n:size(blocks) > 1 || tag.renderUniqueTab)}">
	<div class="${tabPanelTag.navPanelClass}">
		<ul class="${tabPanelTag.navClass}" >
			<c:forEach items="${blocks}" var="block" varStatus="status">
				<c:set var="current" value="class='${tabPanelTag.navLinkClass}'" scope="page"/>
				<c:if test="${status.index == selectedIndex}">
					<c:set var="current" value="class='${tabPanelTag.navLinkClass} active'" scope="page"/>
				</c:if>
				<c:set var="linkId" value="${id}_link_${status.index}" scope="page"/>
				<li class="${tabPanelTag.navItemClass}"><a id="${linkId}" ${current} href="javascript:show${id}('${block.id}', ${status.index}, '${linkId}'); ${block.onSelectTab}">${block.title}</a></li>
			</c:forEach>
		</ul>
	</div>
</c:if>