<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="next"%>
<%@ taglib prefix="combo" uri="combo"%>
<%@ taglib prefix="t" uri="template"%>


<n:panelGrid columns="${TtabelaRelatorio.columns}" style="${TtabelaRelatorio.style}" styleClass="${TtabelaRelatorio.styleClass}" rowStyleClasses="${TtabelaRelatorio.rowStyleClasses}" rowStyles="${TtabelaRelatorio.rowStyles}"
	columnStyleClasses="${TtabelaRelatorio.columnStyleClasses}" columnStyles="${TtabelaRelatorio.columnStyles}" colspan="${TtabelaRelatorio.colspan}" propertyRenderAsDouble="${TtabelaRelatorio.propertyRenderAsDouble}"
	dynamicAttributesMap="${TtabelaRelatorio.dynamicAttributesMap}">

	<t:propertyConfig mode="input" renderAs="double">
		<n:doBody />
	</t:propertyConfig>

</n:panelGrid>


