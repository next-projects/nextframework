<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="next"%>
<%@ taglib prefix="t" uri="template"%>

<n:form>
	<n:bean name="user">
		<div class="pageBody loginPanel">
			<div class="pageTitle">
				Login	
			</div>
			<t:propertyConfig renderAs="single" showLabel="true" mode="input">
				<div class="loginForm">
					<div class="loginProperties">
						<div class="loginProperty">
							<t:property name="username"/>
						</div>
						<div class="loginProperty">
							<t:property name="password" type="password"/>
						</div>
					</div>
					<div class="loginSubmitPanel">
						<n:submit action="doLogin" panelColspan="2" panelAlign="right" type="submit">Entrar</n:submit>
					</div>
				</div>
			</t:propertyConfig>
		</div>
	</n:bean>
</n:form>

<script type="text/javascript">
	document.getElementsByName('login')[0].focus();
</script>
