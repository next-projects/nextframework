<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="n" uri="next"%>
<%@ taglib prefix="t" uri="template"%>

<t:tela titulo="Autorização">
    <t:janelaFiltro>
        <t:tabelaFiltro showSubmit="false">
            <t:property name="role" itens="${roles}" reloadOnChange="true"/>
        </t:tabelaFiltro>
    </t:janelaFiltro>
    
    <c:forEach items="${filter.groupAuthorizationMap}" var="item">
        <t:janelaResultados>
            <n:panelGrid columns="1" rowStyleClasses="filter1" width="100%">
                <n:panel style="color: #333333">   <b><i>${item.key}</i></b></n:panel>
            </n:panelGrid>
            <n:dataGrid itens="${item.value}" width="100%" cellspacing="1" headerStyleClass="listagemHeader" bodyStyleClasses="listagemBody1, listagemBody2" footerStyleClass="listagemFooter">
                <n:bean name="row" propertyPrefix="groupAuthorizationMap[${item.key}][${index}]" valueType="${authorizationProcessItemFilterClass}">

                <n:column header="Tela">
                    <t:property name="description" mode="output"/>                
                    <t:property name="path" mode="input" type="hidden" write="false"/>                      
                </n:column>
                
                <c:forEach items="${mapaGroupModule[item.key].authorizationItens}" var="authorizationItem">
                    <n:column header="${authorizationItem.nome}" width="80px">
                        <c:if test="${fn:length(authorizationItem.valores) == 2}">
                            <%-- Possibilidade de ser true false --%>
                            <n:property name="permissionMap[${authorizationItem.id}]">
                                <n:input type="checkbox"/>                            
                            </n:property>
                        </c:if>
                        <c:if test="${fn:length(authorizationItem.valores) != 2}">
                            (Não implementado ainda)
                            <n:input itens="${authorizationItem.valores}"/>
                        </c:if>                        
                    </n:column>
                </c:forEach>                    
                </n:bean>
            </n:dataGrid>
            
        </t:janelaResultados>
    </c:forEach>
    <c:if test="${!empty filter.role}">
        <t:janelaResultados>
        <div class="filter1" width="100%"><n:submit action="salvar">Salvar</n:submit></div>
        </t:janelaResultados>
    </c:if>
</t:tela>
