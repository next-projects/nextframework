<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>

<form method="${tag.method}" enctype="${tag.enctype}" name="${tag.name}" action="${tag.url}" ${tag.dynamicAttributesToString} onsubmit="return false;" >
	<input type="hidden" name="${tag.actionParameter}" value="${tag.action}"/>
	<input type="hidden" name="suppressValidation" value="false"/>
	<input type="hidden" name="suppressErrors" value="false"/>
	<script language="javascript">
		var ${tag.name} = document.forms["${tag.name}"];
		${tag.name}.validate = '${tag.validate}';
		function ${tag.submitFunction}(action) {
			var validar = ${tag.name}.validate;
			try {
				${tag.validateFunction};
			} catch (e) {
				validar = false;
			}
			try {
				clearMessages();//limpa as mensagens que vieram do servidor
			} catch(e){
			}
			if(validar == 'true') {
				var valid = ${tag.validateFunction}();
				if(valid) {
					if(action){
						${tag.name}.${tag.actionParameter}.value = action;
					}
					${tag.name}.submit();
				}
			} else {
				if(action){
					${tag.name}.${tag.actionParameter}.value = action;
				}
				${tag.name}.submit();
			}
		}
	</script>
	<n:bean name="${tag.forBean}" bypass="${empty tag.forBean}">
		<t:propertyConfig mode="${tag.propertyMode}">
			<n:doBody/>
		</t:propertyConfig>
	</n:bean>
</form>