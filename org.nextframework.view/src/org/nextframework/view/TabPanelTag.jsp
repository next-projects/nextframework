<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="next"%>

<c:if test="${!empty blocks && (n:size(blocks) > 1 || tag.renderUniqueTab)}">
	<ul class="nav nav-tabs">
		<c:forEach items="${blocks}" var="block" varStatus="status">
			<c:set var="current" value="" scope="page"/>
			<c:if test="${status.index == selectedIndex}">
				<c:set var="current" value="id='current' class='active'" scope="page"/>
			</c:if>
			<c:set var="linkId" value="${id}_link_${status.index}" scope="page"/>
			<li ${current}><a data-toggle="tab" href="#${block.id}">${block.title}</a></li>				
		</c:forEach>
	</ul>
</c:if>