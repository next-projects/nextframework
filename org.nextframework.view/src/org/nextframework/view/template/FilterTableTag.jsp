<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="combo" uri="combo"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>

<n:panelGrid columns="${n:default(2, filterTableTag.columns)}" colspan="${filterTableTag.colspan}" width="${filterTableTag.width}" cellpadding="1"
	styleClass="${n:default('inputTable', filterTableTag.styleClass)}" style="${filterTableTag.style}"
	columnStyleClasses="${n:default('labelColumn, propertyColumn', filterTableTag.columnStyleClasses)}" columnStyles="${filterTableTag.columnStyles}"
	rowStyleClasses="${filterTableTag.rowStyleClasses}" rowStyles="${filterTableTag.rowStyles}"
	dynamicAttributesMap="${filterTableTag.dynamicAttributesMap}" propertyRenderAsDouble="${filterTableTag.propertyRenderAsDouble}" >

	<t:propertyConfig mode="input" showLabel="${filterTableTag.propertyShowLabel}" renderAs="double">
		<n:doBody />
	</t:propertyConfig>

</n:panelGrid>

<c:if test="${filterTableTag.showSubmit}">
	<div class="actionBar">
		<n:submit type="submit" url="${filterTableTag.submitUrl}" action="${filterTableTag.submitAction}" validate="${filterTableTag.validateForm}" >${filterTableTag.submitLabel}</n:submit>
	</div>
</c:if>