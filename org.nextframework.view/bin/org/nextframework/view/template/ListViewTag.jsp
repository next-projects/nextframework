<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="next"%>
<%@ taglib prefix="combo" uri="combo"%>
<%@ taglib prefix="t" uri="template"%>

<t:tela titulo="${listagemTag.titulo}" validateForm="true">
		<input type="hidden" name="currentPage" value="0"/>
		<input type="hidden" name="notFirstTime" value="true"/>
		<c:if test="${listagemTag.showNewLink || !empty listagemTag.linkArea}">
			<div class="linkBar">
				${listagemTag.invokeLinkArea}
				<c:if test="${listagemTag.showNewLink}">						
					<n:link action="create">${n:default('Novo', listagemTag.dynamicAttributesMap['novolabel'])}</n:link>
				</c:if>						
			</div>
		</c:if>	
	
		<div>
			<n:doBody />
		</div>

</t:tela>

