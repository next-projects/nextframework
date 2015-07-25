<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="next"%>
<%@ taglib prefix="combo" uri="combo"%>
<%@ taglib prefix="t" uri="template"%>


<c:set var="submitLabel" value="${n:default('Pesquisar', TabelaFiltroTag.submitLabel)}" />
<c:set var="panelGridColumns" value="${n:default(2, TabelaFiltroTag.columns)}" />
<c:set var="panelGridStyleClass" value="${n:default('inputTable', TabelaFiltroTag.styleClass)}" />
<c:set var="panelGridColumnStylesClasses" value="${n:default('labelColumn, propertyColumn', TabelaFiltroTag.columnStyleClasses)}" />


<n:panelGrid columns="${panelGridColumns}"
	 style="${tag.style}"
	 colspan="${tag.colspan}"
	 columnStyleClasses="${panelGridColumnStylesClasses}"
	 columnStyles="${tag.columnStyles}"
	 dynamicAttributesMap="${tag.dynamicAttributesMap}"
	 rowStyles="${tag.rowStyles}"
	 styleClass="${panelGridStyleClass}"
	 propertyRenderAsDouble="${tag.propertyRenderAsDouble}" width="${tag.width}" rowStyleClasses="${tag.rowStyleClasses}" cellpadding="1">
		
		<t:propertyConfig mode="input" showLabel="${tag.propertyShowLabel}" renderAs="double">
			<n:doBody />
		</t:propertyConfig>

</n:panelGrid>

<c:if test="${tag.showSubmit}">
<div class="actionBar">
	<n:submit type="submit" action="${TabelaFiltroTag.submitAction}" validate="${TabelaFiltroTag.validateForm}" url="${TabelaFiltroTag.submitUrl}">${submitLabel}</n:submit>
</div>
</c:if>
