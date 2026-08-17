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
			return submitNextForm(${tag.name}, {
				action: action,
				actionParameter: '${tag.actionParameter}',
				validateFunction: '${tag.validateFunction}'
			});
		}

	</script>
	<n:bean name="${tag.forBean}" bypass="${empty tag.forBean}">
		<t:propertyConfig mode="${tag.propertyMode}">
			<n:doBody/>
		</t:propertyConfig>
	</n:bean>
</form>
