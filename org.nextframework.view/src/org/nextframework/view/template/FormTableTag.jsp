<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="next"%>
<%@ taglib prefix="combo" uri="combo"%>
<%@ taglib prefix="t" uri="template"%>


<n:panel title="${TtabelaEntrada.title}" colspan="${TtabelaEntrada.colspan}">
	<n:panelGrid columns="${TtabelaEntrada.columns}" style="${TtabelaEntrada.style}" styleClass="${TtabelaEntrada.styleClass}" rowStyleClasses="${TtabelaEntrada.rowStyleClasses}" rowStyles="${TtabelaEntrada.rowStyles}"
		columnStyleClasses="${TtabelaEntrada.columnStyleClasses}" columnStyles="${TtabelaEntrada.columnStyles}" colspan="${TtabelaEntrada.colspan}" propertyRenderAsDouble="${TtabelaEntrada.propertyRenderAsDouble}"
		dynamicAttributesMap="${TtabelaEntrada.dynamicAttributesMap}" cellpadding="1" cellspacing="0">

		<t:propertyConfig showLabel="${tag.propertyShowLabel}">
			<n:doBody />
		</t:propertyConfig>

	</n:panelGrid>
</n:panel>
