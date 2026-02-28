<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="n" uri="nextframework.tags.next"%>
<%@ taglib prefix="combo" uri="nextframework.tags.combo"%>
<%@ taglib prefix="t" uri="nextframework.tags.template"%>

<n:panelGrid colspan="${reportTableTag.colspan}" columns="${reportTableTag.columns}" flatMode="${reportTableTag.flatMode}"
	styleClass="${reportTableTag.styleClass}" style="${reportTableTag.style}"
	columnStyleClasses="${reportTableTag.columnStyleClasses}" columnStyles="${reportTableTag.columnStyles}"
	rowStyleClasses="${reportTableTag.rowStyleClasses}" rowStyles="${reportTableTag.rowStyles}"
	dynamicAttributesMap="${reportTableTag.dynamicAttributesMap}" propertyRenderAs="${reportTableTag.propertyRenderAs}" >

	<t:propertyConfig mode="input" showLabel="${reportTableTag.propertyShowLabel}">
		<n:doBody />
	</t:propertyConfig>

</n:panelGrid>