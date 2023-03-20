<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>

<div id="${modalTag.id}" class="${modalTag.overlayStyleClass}">
	<div class="${modalTag.panelStyleClass}">
		<div class="${modalTag.contentStyleClass}">
			<n:doBody/>
		</div>
	</div>
</div>

<script type="text/javascript">
	var blockScreenId_${modalTag.id} = 0;
	function showModal_${modalTag.id}(){
		blockScreenId_${modalTag.id} = next.effects.blockScreen();
		var div = document.getElementById("${modalTag.id}");
		div.style.display = "block";
		div.style.zIndex = next.dom.getNextZIndex();
		<c:if test="${useBootstrap}">next.style.addClass(div, 'show');</c:if>
	}
	function hideModal_${modalTag.id}(){
		var div = document.getElementById("${modalTag.id}");
		div.style.display = "none";
		<c:if test="${useBootstrap}">next.style.removeClass(div, 'show');</c:if>
		next.effects.unblockScreen(blockScreenId_${modalTag.id});
	}
	<c:if test="${modalTag.visible}">
		showModal_${modalTag.id}();
	</c:if>
	<c:if test="${!modalTag.visible}">
		hideModal_${modalTag.id}();
	</c:if>
</script>