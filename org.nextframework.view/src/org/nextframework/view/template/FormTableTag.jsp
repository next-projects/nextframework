<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>

<n:panelGrid colspan="${formTableTag.colspan}" columns="${formTableTag.columns}" flatMode="${formTableTag.flatMode}"
	styleClass="${formTableTag.styleClass}" style="${formTableTag.style}"
	columnStyleClasses="${formTableTag.columnStyleClasses}" columnStyles="${formTableTag.columnStyles}"
	rowStyleClasses="${formTableTag.rowStyleClasses}" rowStyles="${formTableTag.rowStyles}"
	dynamicAttributesMap="${formTableTag.dynamicAttributesMap}" propertyRenderAs="${formTableTag.propertyRenderAs}" >

	<t:propertyConfig mode="input" showLabel="${formTableTag.propertyShowLabel}">
		<n:doBody />
	</t:propertyConfig>

</n:panelGrid>