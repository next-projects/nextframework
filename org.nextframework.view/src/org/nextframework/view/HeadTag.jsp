<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	response.setHeader("P3P","CP='IDC DSP COR ADM DEVi TAIi PSA PSD IVAi IVDi CONi HIS OUR IND CNT'");

	response.setHeader("pragma", "no-cache");
	response.setHeader("cache-control", "no-store");
	response.addHeader("cache-control", "no-cache");
	response.setHeader("expires", "-1");
%>
<meta http-equiv="content-type" content="text/html; charset=${tag.charset}">
<meta http-equiv="pragma" content="no-cache" />
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="expires" content="-1" />

<% request.setAttribute("app", request.getContextPath()); %>

<c:if test="${tag.includeNormalizeCss}">
<link rel="stylesheet"	href="${app}/resource/theme/normalize.css"/>	
</c:if>
<c:if test="${tag.includeSystemCss}">
<link rel="stylesheet"	href="${app}/resource/theme/system.css"/>	
</c:if>

<script language="JavaScript" src="${app}/resource/js/stjs.js"></script>
<script language="JavaScript" src="${app}/resource/js/stjs-ext.js"></script>

<c:if test="${tag.includeNextJs}">
	<script language="JavaScript" src="${app}/resource/js/NextDataGrid.js"></script>
	<script language="JavaScript" src="${app}/resource/js/NextSuggestSuggestionProvider.js"></script>
	<script language="JavaScript" src="${app}/resource/js/NextSuggestAjaxProvider.js"></script>
	<script language="JavaScript" src="${app}/resource/js/NextSuggestStaticListProvider.js"></script>
	<script language="JavaScript" src="${app}/resource/js/NextSuggest.js"></script>
	<script language="JavaScript" src="${app}/resource/js/NextDialogs.js"></script>
	<script language="JavaScript" src="${app}/resource/js/NextReload.js"></script>
	<script language="JavaScript" src="${app}/resource/js/next-modules.js"></script>
	<script language="JavaScript" src="${app}/resource/js/next.js"></script>
</c:if>
<c:if test="${tag.includeNextDirectJs}">
	<script language="JavaScript" src="${app}/resource/js/next-direct.js"></script>
</c:if>

<script language="JavaScript" src="${app}/resource/report/report.js"></script>

<script language="JavaScript" src="${app}/resource/js/ajax.js"></script>
<script language="JavaScript" src="${app}/resource/js/input.js"></script>
<script language="JavaScript" src="${app}/resource/js/input-selectmanypopup.js"></script>
<script language="JavaScript" src="${app}/resource/js/validate.js"></script>

<c:if test="${tag.includeUtilJs}">
	<script language="JavaScript" src="${app}/resource/js/util.js"></script>
</c:if>

<script language="JavaScript" src="${app}/resource/js/dynatable.js"></script>
<script language="JavaScript" src="${app}/resource/js/dynatable-listeners.js"></script>
<script language="JavaScript" src="${app}/resource/js/JSCookMenu.js"></script>

<script language="JavaScript" src="${app}/resource/js/treetable.js"></script>


<link rel="StyleSheet"        href="${app}/resource/css/suggest.css" type="text/css">

<link rel="StyleSheet"        href="${app}/resource/css/system.css" type="text/css">

<link rel="StyleSheet"        href="${app}/resource/css/select_many_popup.css" type="text/css">

<%-- CALENDAR --%>
<script language="JavaScript" src="${app}/resource/calendar/calendar.js"></script>
<script language="JavaScript" src="${app}/resource/calendar/calendar-ptBR.js"></script>
<script language="JavaScript" src="${app}/resource/calendar/calendar-setup.js"></script>

<link rel="StyleSheet"        href="${app}/resource/calendar/calendar-system.css" type="text/css">	

<%-- DATAGRID --%>
<script language="JavaScript" src="${app}/resource/js/datagrid.js"></script>

<%-- PROGRESS --%>
<script language="JavaScript" src="${app}/resource/js/progress.js"></script>

<%-- INICIALIZA��O DO MENU --%>
<script language="JavaScript">
	//menu
	var cmThemeOfficeBase = '${app}/resource/menu/';
</script>

<%-- MENU --%>
<c:if test="${tag.includeThemeCss}">
<link rel="StyleSheet" href="${app}/resource/menu/theme.css" type="text/css">
</c:if>
<script language="JavaScript" src="${app}/resource/menu/theme.js"></script>

<%-- INICIALIZA��O DO HTMLAREA --%>
<script language="JavaScript">
	//htmlarea
	try {
		preparaHtmlArea('${app}/resource/htmlarea/');	
	} catch(e){}// se n�o conseguiu achar o javascript n�o dar exce��o
</script>


<%-- DEFAULT CSS --%>
<c:if test="${tag.includeDefaultCss}">
<link rel="StyleSheet"        href="${app}/resource/css/default.css" type="text/css">	
</c:if>

<%-- CSS JS DA APLICA��O --%>
<c:if test="${searchCssDir == true}">
	<c:forEach items="${csss}" var="css">
	<link rel="StyleSheet"        href="${app}${css}" type="text/css">	
	</c:forEach>
	<c:forEach items="${csssModule}" var="css">
	<link rel="StyleSheet"        href="${app}${css}" type="text/css">	
	</c:forEach>
</c:if>


<c:if test="${searchJsDir == true}">
	<c:forEach items="${jss}" var="js">
	<script language="JavaScript" src="${app}${js}"></script>	
	</c:forEach>
	<c:forEach items="${jssModule}" var="js">
	<script language="JavaScript" src="${app}${js}"></script>	
	</c:forEach>
</c:if>
