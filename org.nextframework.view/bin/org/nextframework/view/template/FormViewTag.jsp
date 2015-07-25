<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="next"%>
<%@ taglib prefix="combo" uri="combo"%>
<%@ taglib prefix="t" uri="template"%>

<t:tela titulo="${entradaTag.titulo}">
		<c:if test="${param.fromInsertOne == 'true'}">
			<input type="hidden" name="fromInsertOne" value="true"/>
		</c:if>

		<c:if test="${entradaTag.showListagemLink || !empty entradaTag.linkArea}">
			<div class="linkBar">
					${entradaTag.invokeLinkArea}			
					<c:if test="${entradaTag.showListagemLink}">
						<n:link action="list" class="outterTableHeaderLink">Listagem</n:link>
					</c:if>				
			</div>
		</c:if>	

		<div>
			<n:bean name="${crudContext.beanName}">
				<c:if test="${param.ACTION=='view'}">
					<c:set var="modeView" value="output" scope="request"/>
					<t:property name="${crudContext.idPropertyName}" mode="input" write="false"/>
				</c:if>
				<t:propertyConfig mode="${n:default('input', modeView)}">
					<n:doBody />
				</t:propertyConfig>
			</n:bean>
		</div>
</t:tela>