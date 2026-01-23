<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<!DOCTYPE html>
<html>
<head>
    <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/favicon.svg">
    <n:head title="ERP-lite"/>
</head>
<body>
    <jsp:include page="${bodyPage}"/>

    <div class="powered-badge" id="poweredBadge">
        <span class="powered-text">Powered by</span>
        <img src="${pageContext.request.contextPath}/logo.svg" alt="Next" class="powered-logo">
        <span class="powered-brand">Next Framework</span>
        <span class="powered-separator">|</span>
        <button type="button" class="powered-close" onclick="document.getElementById('poweredBadge').style.display='none'">&times;</button>
    </div>
</body>
</html>
