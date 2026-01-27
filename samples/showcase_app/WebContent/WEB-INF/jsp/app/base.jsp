<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/favicon.svg">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css">
    <n:head title="ERP-lite"/>
</head>
<body>
    <nav class="app-navbar navbar navbar-expand-lg">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/app/home">ERP-lite</a>
            <div class="d-flex align-items-center">
                <span class="user-info me-3">
                    <c:if test="${not empty USER}">${USER.name != null ? USER.name : USER.username}</c:if>
                </span>
                <n:link url="/public/login" action="logout" class="btn btn-outline-light btn-sm">Logout</n:link>
            </div>
        </div>
    </nav>

    <div class="container mt-4">
        <n:messages/>
        <jsp:include page="${bodyPage}"/>
    </div>

    <div class="powered-badge" id="poweredBadge">
        <span class="powered-text">Powered by</span>
        <img src="${pageContext.request.contextPath}/logo.svg" alt="Next" class="powered-logo">
        <span class="powered-brand">Next Framework</span>
        <span class="powered-separator">|</span>
        <button type="button" class="powered-close" onclick="document.getElementById('poweredBadge').style.display='none'">&times;</button>
    </div>
</body>
</html>
