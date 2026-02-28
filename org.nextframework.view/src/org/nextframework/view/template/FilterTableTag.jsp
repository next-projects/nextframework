<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="n" uri="nextframework.tags.next"%>
<%@ taglib prefix="t" uri="nextframework.tags.template"%>

<n:panelGrid colspan="${filterTableTag.colspan}" columns="${filterTableTag.columns}" flatMode="${filterTableTag.flatMode}"
	styleClass="${filterTableTag.styleClass}" style="${filterTableTag.style}"
	columnStyleClasses="${filterTableTag.columnStyleClasses}" columnStyles="${filterTableTag.columnStyles}"
	rowStyleClasses="${filterTableTag.rowStyleClasses}" rowStyles="${filterTableTag.rowStyles}"
	dynamicAttributesMap="${filterTableTag.dynamicAttributesMap}" propertyRenderAs="${filterTableTag.propertyRenderAs}" >

	<t:propertyConfig mode="input" showLabel="${filterTableTag.propertyShowLabel}">
		<n:doBody />
	</t:propertyConfig>

</n:panelGrid>