<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>

<n:panelGrid colspan="${reportTableTag.colspan}" flatMode="${reportTableTag.flatMode}"
	columns="${reportTableTag.columns}" scaleToGridSystem="${reportTableTag.scaleToGridSystem}"
	styleClass="${reportTableTag.styleClass}" style="${reportTableTag.style}"
	columnStyleClasses="${reportTableTag.columnStyleClasses}" columnStyles="${reportTableTag.columnStyles}"
	rowStyleClasses="${reportTableTag.rowStyleClasses}" rowStyles="${reportTableTag.rowStyles}"
	dynamicAttributesMap="${reportTableTag.dynamicAttributesMap}" propertyRenderAs="${reportTableTag.propertyRenderAs}" >

	<t:propertyConfig mode="input" showLabel="${reportTableTag.propertyShowLabel}">
		<n:doBody />
	</t:propertyConfig>

</n:panelGrid>