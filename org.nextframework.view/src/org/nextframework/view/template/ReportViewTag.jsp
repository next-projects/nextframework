<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="n" uri="nextframework.tags.next"%>
<%@ taglib prefix="t" uri="nextframework.tags.template"%>

<t:view title="${reportViewTag.title}" formEnctype="${reportViewTag.dynamicAttributesMap['formEnctype']}">
	<n:doBody />
</t:view>