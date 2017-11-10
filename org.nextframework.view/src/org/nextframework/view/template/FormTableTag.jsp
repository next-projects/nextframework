<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>

<n:panel title="${formTableTag.title}" colspan="${formTableTag.colspan}">
	<div class="form-horizontal">
		<t:propertyConfig showLabel="${formTableTag.propertyShowLabel}" renderAs="form-group">
			<n:doBody />
		</t:propertyConfig>
	</div>
</n:panel>