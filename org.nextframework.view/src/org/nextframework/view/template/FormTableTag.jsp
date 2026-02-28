<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="n" uri="nextframework.tags.next"%>
<%@ taglib prefix="t" uri="nextframework.tags.template"%>

<n:panelGrid colspan="${formTableTag.colspan}" columns="${formTableTag.columns}" flatMode="${formTableTag.flatMode}"
	styleClass="${formTableTag.styleClass}" style="${formTableTag.style}"
	columnStyleClasses="${formTableTag.columnStyleClasses}" columnStyles="${formTableTag.columnStyles}"
	rowStyleClasses="${formTableTag.rowStyleClasses}" rowStyles="${formTableTag.rowStyles}"
	dynamicAttributesMap="${formTableTag.dynamicAttributesMap}" propertyRenderAs="${formTableTag.propertyRenderAs}" >

	<t:propertyConfig mode="input" showLabel="${formTableTag.propertyShowLabel}">
		<n:doBody />
	</t:propertyConfig>

</n:panelGrid>