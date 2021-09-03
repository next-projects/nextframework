<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="combo" uri="combo"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>

<t:view title="${formViewTag.title}">

	<c:if test="${param.fromInsertOne == 'true'}">
		<input type="hidden" name="fromInsertOne" value="true" />
	</c:if>

	<c:if test="${formViewTag.showListLink || !empty formViewTag.linkArea}">
		<div class="linkBar">
			${formViewTag.invokeLinkArea}
			<c:if test="${formViewTag.showListLink}">
				<n:link action="list" class="outterTableHeaderLink">${formViewTag.listLinkLabel}</n:link>
			</c:if>
		</div>
	</c:if>

	<div>
		<n:bean name="${crudContext.beanName}">
			<c:if test="${param.ACTION=='view'}">
				<c:set var="modeView" value="output" scope="request" />
			</c:if>
			<t:propertyConfig mode="${n:default('input', modeView)}">
				<n:doBody />
			</t:propertyConfig>
		</n:bean>
	</div>

</t:view>