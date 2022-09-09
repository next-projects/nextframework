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
	function hideModal_${modalTag.id}(){
		var div = document.getElementById("${modalTag.id}");
		div.style.display = "none";
		<c:if test="${useBootstrap}">next.style.removeClass(div, 'show');</c:if>
		next.effects.unblockScreen();
	}
	function showModal_${modalTag.id}(){
		var div = document.getElementById("${modalTag.id}");
		div.style.display = "block";
		<c:if test="${useBootstrap}">next.style.addClass(div, 'show');</c:if>
		next.effects.blockScreen();
	}
	<c:if test="${!modalTag.visible}">
		hideModal_${modalTag.id}();
	</c:if>
	<c:if test="${modalTag.visible}">
		showModal_${modalTag.id}();
	</c:if>
</script>