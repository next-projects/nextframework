<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="next"%>
<%@ taglib prefix="combo" uri="combo"%>
<%@ taglib prefix="t" uri="template"%>

<div class="inputWindow">
	<n:tabPanel id="janelaEntrada">
		<n:doBody />
	</n:tabPanel>
	
	<c:if test="${janelaEntradaTag.showSubmit}">
		<div class="actionBar">
			<c:if test="${param.ACTION == 'view'}">
				<n:submit id="do_editar_submit" action="update" validate="false">Editar</n:submit>
			</c:if>
			<c:if test="${param.ACTION != 'view'}">
				<n:submit id="do_${janelaEntradaTag.submitAction}_submit" action="${janelaEntradaTag.submitAction}" validate="true" confirmationScript="${janelaEntradaTag.submitConfirmationScript}">${janelaEntradaTag.submitLabel}</n:submit>
			</c:if>							
		</div>
	</c:if>
</div>

