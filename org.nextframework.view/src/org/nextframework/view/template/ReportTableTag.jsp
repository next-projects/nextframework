<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="combo" uri="combo"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>

<n:panelGrid columns="${n:default(2, reportTableTag.columns)}" colspan="${reportTableTag.colspan}"
	styleClass="${n:default('inputTable', reportTableTag.styleClass)}" style="${reportTableTag.style}"
	columnStyleClasses="${n:default('labelColumn, propertyColumn', reportTableTag.columnStyleClasses)}" columnStyles="${reportTableTag.columnStyles}"
	rowStyleClasses="${reportTableTag.rowStyleClasses}" rowStyles="${reportTableTag.rowStyles}"
	dynamicAttributesMap="${reportTableTag.dynamicAttributesMap}" propertyRenderAsDouble="${reportTableTag.propertyRenderAsDouble}" >

	<t:propertyConfig mode="input" renderAs="double">
		<n:doBody />
	</t:propertyConfig>

</n:panelGrid>