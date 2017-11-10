<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="next"%>
<%@ taglib prefix="combo" uri="combo"%>
<%@ taglib prefix="t" uri="template"%>


<n:panel title="${TtabelaEntrada.title}" colspan="${TtabelaEntrada.colspan}">
<div class="form-horizontal">
	<t:propertyConfig showLabel="${tag.propertyShowLabel}" renderAs="form-group">
		<n:doBody />
	</t:propertyConfig>
</div>
</n:panel>
