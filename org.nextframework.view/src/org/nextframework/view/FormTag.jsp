<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="n" uri="nextframework.tags.next"%>
<%@ taglib prefix="t" uri="nextframework.tags.template"%>

<form method="${tag.method}" enctype="${tag.enctype}" name="${tag.name}" action="${tag.url}" ${tag.dynamicAttributesToString} onsubmit="return false;" >
	<input type="hidden" name="${tag.actionParameter}" value="${tag.action}"/>
	<input type="hidden" name="suppressValidation" value="false"/>
	<input type="hidden" name="suppressErrors" value="false"/>
	<script language="javascript">

		var ${tag.name} = document.forms["${tag.name}"];
		${tag.name}.validate = '${tag.validate}';

		function ${tag.submitFunction}(action) {

			try {
				clearMessages();//limpa as mensagens que vieram do servidor
			} catch(e){
			}

			var validar = ${tag.name}.validate;
			try {
				${tag.validateFunction};
			} catch (e) {
				validar = false;
			}

			if(validar == 'true') {
				var valid = ${tag.validateFunction}();
				if(!valid) {
					return false;
				}
			}

			if(action){
				${tag.name}.${tag.actionParameter}.value = action;
			}

			${tag.submitFunction}_checkMultipart(${tag.name});
			${tag.name}.submit();

		}

		function ${tag.submitFunction}_checkMultipart(form) {
			if (form.enctype !== 'multipart/form-data') {
				const hasFileInput = form.querySelector('input[type="file"]');
				if (hasFileInput && form.method.toUpperCase() === 'POST') {
					console.info('Ajustando enctype para multipart/form-data...');
					 form.enctype = 'multipart/form-data';
				}
			}
		}

		document.addEventListener('submit', function(event) {
			${tag.submitFunction}_checkMultipart(event.target);
		});

	</script>
	<n:bean name="${tag.forBean}" bypass="${empty tag.forBean}">
		<t:propertyConfig mode="${tag.propertyMode}">
			<n:doBody/>
		</t:propertyConfig>
	</n:bean>
</form>