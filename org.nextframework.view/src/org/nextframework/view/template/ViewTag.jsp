<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="combo" uri="combo"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>

<c:if test="${empty Tproperty.dynamicAttributesMap['enctype']}">
	<c:set var="_form_enctype" scope="page" value="multipart/form-data" />
</c:if>
<c:if test="${!empty Tproperty.dynamicAttributesMap['enctype']}">
	<c:set var="_form_enctype" scope="page" value="${Tproperty.dynamicAttributesMap['enctype']}" />
</c:if>

<n:form name="${viewTag.formName}" enctype="${_form_enctype}" validate="${viewTag.validateForm}" method="${viewTag.formMethod}" action="${viewTag.formAction}" validateFunction="validarFormulario" bypass="${!viewTag.includeForm}">

	<n:validation functionName="validateForm" bypass="${!viewTag.includeForm || !viewTag.validateForm}">
		<script language="javascript">
			// caso seja alterada a fun��o validation ela ser� chamada ap�s a validacao do formulario
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
		
		<c:if test="${! empty viewTag.title }">
			<div class="pageTitleBar">
				<div class="pageTitle">
					${viewTag.title}
				</div>
			</div>
		</c:if>

		<div class="pageBody">
			<n:bean name="${viewTag.useBean}" valueType="${viewTag.beanType}" bypass="${empty viewTag.useBean}">
				<t:propertyConfig mode="${viewTag.propertyMode}" bypass="${(empty viewTag.useBean || empty viewTag.propertyMode) && empty viewTag.includeForm}">
					<n:doBody />
				</t:propertyConfig>
			</n:bean>
		</div>

	</n:validation>
</n:form>
