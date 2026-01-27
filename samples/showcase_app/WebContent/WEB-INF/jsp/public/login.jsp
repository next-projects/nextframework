<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/landing.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">

<div class="login-page">
    <div class="login-card">
        <div class="login-brand">ERP-lite</div>
        <h4 class="login-title">Sign In</h4>
        <n:messages/>
        <n:form>
            <n:bean name="user">
                <n:panelGrid columns="2" propertyRenderAs="double">
                    <t:property name="username"/>
                    <t:property name="password" type="password"/>
                </n:panelGrid>
                <div class="d-grid mt-3">
                    <n:submit action="doLogin" type="submit" class="btn btn-primary btn-lg">Login</n:submit>
                </div>
            </n:bean>
        </n:form>
    </div>
</div>
