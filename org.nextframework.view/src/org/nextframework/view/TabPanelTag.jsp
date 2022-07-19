<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>

<c:if test="${!empty blocks && (n:size(blocks) > 1 || tag.renderUniqueTab)}">
	<c:if test="${useBootstrap}">
		<ul class="${tabPanelTag.navClass}">
			<c:forEach items="${blocks}" var="block" varStatus="status">
				<c:set var="current" value="" scope="page"/>
				<c:if test="${status.index == selectedIndex}">
					<c:set var="current" value="class='active'" scope="page"/>
				</c:if>
				<li ${current}><a data-toggle="tab" href="#${block.id}">${block.title}</a></li>
			</c:forEach>
		</ul>
	</c:if>
	<c:if test="${!useBootstrap}">
		<div class="${tabPanelTag.navClass}" >
			<ul>
				<c:forEach items="${blocks}" var="block" varStatus="status">
					<c:set var="current" value="" scope="page"/>
					<c:if test="${status.index == selectedIndex}">
						<c:set var="current" value="class='active'" scope="page"/>
					</c:if>
					<c:set var="linkId" value="${id}_link_${status.index}" scope="page"/>
					<li ${current}><a href="javascript:show${id}('${block.id}', ${status.index}, '${linkId}'); ${block.onSelectTab}" id="${linkId}">${block.title}</a></li>
				</c:forEach>
			</ul>
			<div class='clear'>&nbsp;</div>
		</div>
	</c:if>
</c:if>