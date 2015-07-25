<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="next"%>
<%@ taglib prefix="combo" uri="combo"%>
<%@ taglib prefix="t" uri="template"%>


<n:bean name="filtro">
	<div class="sectionTitle">
		Relatório
	</div>

	<div class="filterWindow">
		<n:doBody />
		<div class="actionBar">
			<n:submit action="${TJanelaRelatorio.submitAction}" validate="true" confirmationScript="${TJanelaRelatorio.submitConfirmationScript}">${TJanelaRelatorio.submitLabel}</n:submit>
		</div>
	</div>
	

</n:bean>



