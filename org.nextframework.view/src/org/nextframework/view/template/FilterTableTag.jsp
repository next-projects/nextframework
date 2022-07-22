<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>

<n:panelGrid colspan="${filterTableTag.colspan}" columns="${filterTableTag.columns}" cellpadding="1" cellspacing="0"
	styleClass="${filterTableTag.styleClass}" style="${filterTableTag.style}"
	columnStyleClasses="${filterTableTag.columnStyleClasses}" columnStyles="${filterTableTag.columnStyles}"
	rowStyleClasses="${filterTableTag.rowStyleClasses}" rowStyles="${filterTableTag.rowStyles}"
	dynamicAttributesMap="${filterTableTag.dynamicAttributesMap}" propertyRenderAs="${filterTableTag.propertyRenderAs}" >

	<t:propertyConfig mode="input" showLabel="${filterTableTag.propertyShowLabel}">
		<n:doBody />
	</t:propertyConfig>

</n:panelGrid>