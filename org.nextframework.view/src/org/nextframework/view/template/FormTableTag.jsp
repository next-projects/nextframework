<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="combo" uri="combo"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>


<n:panel title="${formTableTag.title}" colspan="${formTableTag.colspan}">

	<n:panelGrid columns="${formTableTag.columns}" cellpadding="1" cellspacing="0"
		styleClass="${n:default('inputTable', formTableTag.styleClass)}" style="${formTableTag.style}"
		columnStyleClasses="${n:default('labelColumn, propertyColumn', formTableTag.columnStyleClasses)}" columnStyles="${formTableTag.columnStyles}"
		rowStyleClasses="${formTableTag.rowStyleClasses}" rowStyles="${formTableTag.rowStyles}"
		dynamicAttributesMap="${formTableTag.dynamicAttributesMap}" propertyRenderAsDouble="${formTableTag.propertyRenderAsDouble}" >

		<t:propertyConfig showLabel="${formTableTag.propertyShowLabel}">
			<n:doBody />
		</t:propertyConfig>

	</n:panelGrid>

</n:panel>