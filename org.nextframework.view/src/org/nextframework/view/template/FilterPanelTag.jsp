<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="next"%>
<%@ taglib prefix="combo" uri="combo"%>
<%@ taglib prefix="t" uri="template"%>

<n:bean name="${tag.name}">
	<div class="sectionTitle">
		Pesquisar
	</div>

	<div class="filterWindow">
		<n:doBody />
	</div>
</n:bean>