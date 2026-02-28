<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="n" uri="nextframework.tags.next"%>
<%@ taglib prefix="t" uri="nextframework.tags.template"%>

<%--
	Put this file on WEB-INF/jsp 
	Include <jsp:include page="${bodyPage}" /> where the body should be places
 --%>

<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE9">
		<n:head />
	</head>
	<body>
	
		<div class="applicationTitleBar">
			<div class="applicationTitle">
				<b>Next</b> Application
			</div>
		</div>
	
		<div class="menubar">
			<div class="menubarInner">
				<n:menu menupath="/WEB-INF/menu.xml" />
			</div>
		</div>
	
		<div class="messageOuterDiv">
			<n:messages />
		</div>
		<div class="body">
			<jsp:include page="${bodyPage}" />
		</div>
	
	</body>
</html>