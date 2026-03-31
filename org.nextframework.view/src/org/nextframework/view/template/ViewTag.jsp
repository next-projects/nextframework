<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="n" uri="nextframework.tags.next"%>
<%@ taglib prefix="t" uri="nextframework.tags.template"%>

<n:form name="${viewTag.formName}" method="${viewTag.formMethod}" action="${viewTag.formAction}" enctype="${viewTag.formEnctype}" validate="${viewTag.validateForm}" validateFunction="validate"  bypass="${!viewTag.includeForm}">

	<n:validation functionName="validateForm" bypass="${!viewTag.includeForm || !viewTag.validateForm}">

		<c:if test="${viewTag.includeForm && viewTag.validateForm}">
			<script language="javascript">
				// caso seja alterada a função validateExtra ela será chamada após a validação do formulário
				var validateExtra;
				function validate(){
					<c:if test="${!viewTag.validateForm}">
						return true;
					</c:if>
					<c:if test="${viewTag.validateForm}">
						var valido = validateForm();
						if(validateExtra){
							valido = validateExtra(valido);
						}
						return valido;
					</c:if>
				}
			</script>
		</c:if>

		<div class="${viewTag.pageStyleClass}">
			<c:if test="${! empty viewTag.title }">
				<div class="${viewTag.titleStyleClass}">${viewTag.title}</div>
			</c:if>
			<div class="${viewTag.bodyStyleClass}">
				<n:bean name="${viewTag.useBean}" valueType="${viewTag.beanType}" bypass="${empty viewTag.useBean}">
					<t:propertyConfig mode="${viewTag.propertyMode}" bypass="${(empty viewTag.useBean || empty viewTag.propertyMode) && empty viewTag.includeForm}">
						<n:doBody />
					</t:propertyConfig>
				</n:bean>
			</div>
		</div>

	</n:validation>

</n:form>