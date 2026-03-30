<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="n" uri="nextframework.tags.next"%>
<%@ taglib prefix="t" uri="nextframework.tags.template"%>

<t:view title="${reportViewTag.title}" enctype="${reportViewTag.dynamicAttributesMap['enctype']}">
	<n:doBody />
</t:view>