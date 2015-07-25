<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>

<%--
	Put this file on WEB-INF/jsp 
	Include <jsp:include page="${bodyPage}" /> where the body should be places
 --%>

<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE9" ><!-- Solves AJAX bug in IE10 -->
		
		<link rel="stylesheet"	href="${application}/resource/theme/fonts/open-sans.css"/>
		
		<link rel="stylesheet"	href="${application}/resource/theme/theme1.css"/>
		
		<n:head includeDefaultCss="false" includeThemeCss="false"/>
	</head>
	<body>
		<div class="applicationTitleBar">
			<div class="applicationTitle">
				<b>Next</b> Application 
			</div>
		</div>
		
		<div class="menubar">
			<div class="menubarInner">
				<n:menu menupath="/WEB-INF/menu.xml"/>
			</div>
		</div>
		 
		<div class="messageOuterDiv">
			<n:messages/> 
		</div>
		<div class="body">
			<jsp:include page="${bodyPage}" /> 
		</div>
		
	</body>
</html>