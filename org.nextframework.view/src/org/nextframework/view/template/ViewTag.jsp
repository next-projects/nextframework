<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="next"%>
<%@ taglib prefix="combo" uri="combo"%>
<%@ taglib prefix="t" uri="template"%>

<n:form name="${viewTag.formName}" validate="${viewTag.validateForm}" method="${viewTag.formMethod}" action="${viewTag.formAction}" validateFunction="validarFormulario" bypass="${!viewTag.includeForm}">

	<n:validation functionName="validateForm" bypass="${!viewTag.includeForm || !viewTag.validateForm}">
		<script language="javascript">
			// caso seja alterada a função validation ela será chamada após a validacao do formulario
			var validation;
			function validarFormulario(){
				<c:if test="${!viewTag.validateForm}">
					return true;
				</c:if>
				var valido = validateForm();
				if(validation){
					valido = validation(valido);
				}
				return valido;
			}
		</script>
		
		<div class="pageTitleBar">
			<div class="pageTitle">
				${viewTag.titulo}
			</div>
		</div>

		<div class="pageBody">
			<n:bean name="${viewTag.useBean}" valueType="${viewTag.beanType}" bypass="${empty viewTag.useBean}">
				<t:propertyConfig mode="${viewTag.propertyMode}" bypass="${(empty viewTag.useBean || empty viewTag.propertyMode) && empty viewTag.includeForm}">
					<n:doBody />
				</t:propertyConfig>
			</n:bean>
		</div>

	</n:validation>
</n:form>
