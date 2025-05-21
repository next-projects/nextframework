<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%
	response.setHeader("P3P","CP='IDC DSP COR ADM DEVi TAIi PSA PSD IVAi IVDi CONi HIS OUR IND CNT'");
	response.setHeader("pragma", "no-cache");
	response.setHeader("cache-control", "no-store");
	response.addHeader("cache-control", "no-cache");
	response.setHeader("expires", "-1");
%>
<meta http-equiv="content-type" content="text/html; charset=${headTag.charset}">
<meta http-equiv="pragma" content="no-cache" />
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="expires" content="-1" />

<c:if test="${useBootstrap}">
	<meta http-equiv="X-UA-Compatible" content="IE=edge" >
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<link href="${app}/resource/bootstrap/css/bootstrap.min.css" rel="stylesheet">
	<script src="${app}/resource/bootstrap/js/bootstrap.bundle.min.js" language="JavaScript"></script>
	<link href="${app}/resource/bootstrap/fonts/bootstrap-icons.css" rel="stylesheet" >
</c:if>
<c:if test="${!useBootstrap && headTag.includeNormalizeCss}">
	<link rel="stylesheet" href="${app}/resource/css/normalize.css"/>
</c:if>
<c:if test="${headTag.includeSystemCss}">
	<link rel="stylesheet" href="${app}/resource/css/system.css"/>
</c:if>

<script language="JavaScript" src="${app}/resource/js/stjs.js"></script>
<script language="JavaScript" src="${app}/resource/js/stjs-ext.js"></script>

<c:if test="${headTag.includeNextJs}">
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
<c:if test="${headTag.includeNextDirectJs}">
	<script language="JavaScript" src="${app}/resource/js/next-direct.js"></script>
</c:if>

<script language="JavaScript" src="${app}/resource/report/report.js"></script>

<script language="JavaScript" src="${app}/resource/js/imask.js"></script>

<script language="JavaScript" src="${app}/resource/js/ajax.js"></script>
<script language="JavaScript" src="${app}/resource/js/input.js"></script>
<script language="JavaScript" src="${app}/resource/js/input-selectmanypopup.js"></script>
<script language="JavaScript" src="${app}/resource/js/validate.js"></script>

<c:if test="${headTag.includeUtilJs}">
	<script language="JavaScript" src="${app}/resource/js/util.js"></script>
</c:if>

<script language="JavaScript" src="${app}/resource/js/dynatable.js"></script>
<script language="JavaScript" src="${app}/resource/js/dynatable-listeners.js"></script>
<script language="JavaScript" src="${app}/resource/js/JSCookMenu.js"></script>

<%-- TREETABLE CONFLITA COM API DO GOOGLE! --%>
<%-- <script language="JavaScript" src="${app}/resource/js/treetable.js"></script> --%>


<link rel="StyleSheet" href="${app}/resource/css/suggest.css" type="text/css">

<link rel="StyleSheet" href="${app}/resource/css/input-selectmanypopup.css" type="text/css">
 
<%-- CALENDAR --%>
<script language="JavaScript" src="${app}/resource/calendar/calendar.js"></script>
<script language="JavaScript" src="${app}/resource/calendar/calendar-ptBR.js"></script>
<script language="JavaScript" src="${app}/resource/calendar/calendar-setup.js"></script>

<link rel="StyleSheet" href="${app}/resource/calendar/calendar-system.css" type="text/css">

<%-- DATAGRID --%>
<script language="JavaScript" src="${app}/resource/js/datagrid.js"></script>

<%-- PROGRESS --%>
<script language="JavaScript" src="${app}/resource/js/progress.js"></script>

<%-- MENU --%>
<script language="JavaScript">
	//menu
	var cmThemeOfficeBase = '${app}/resource/menu/';
</script>
<c:if test="${headTag.includeMenuThemeCss}">
	<link rel="StyleSheet" href="${app}/resource/menu/theme.css" type="text/css">
</c:if>
<script language="JavaScript" src="${app}/resource/menu/theme.js"></script>

<%-- INICIALIZAÇÃO DO HTMLAREA --%>
<script language="JavaScript">
	//htmlarea
	try {
		preparaHtmlArea('${app}/resource/htmlarea/');	
	} catch(e){}// se não conseguiu achar o javascript não dar exceção
</script>

<%-- GLOBAL MAP --%>
<c:if test="${useBootstrap}">
	<script src="${app}/resource/bootstrap/js/globalMap.js" language="JavaScript"></script>
</c:if>

<%-- DEFAULT CSS --%>
<c:if test="${headTag.includeDefaultCss}">
	<c:if test="${useBootstrap}">
		<link rel="StyleSheet" href="${app}/resource/layout/lightbluebs/default.css" type="text/css">
	</c:if>
	<c:if test="${!useBootstrap}">
		<link rel="StyleSheet" href="${app}/resource/layout/lightblue/default.css" type="text/css">
	</c:if>
</c:if>

<%-- APPLICATION CSS JS --%>
<c:if test="${searchCssDir == true}">
	<c:forEach items="${csss}" var="css">
	<link rel="StyleSheet" href="${app}${css}" type="text/css">
	</c:forEach>
	<c:forEach items="${csssModule}" var="css">
	<link rel="StyleSheet" href="${app}${css}" type="text/css">
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

<%-- USER LOCALE --%>
<c:set var="userLocale" value="${pageContext.response.locale}" scope="request" />
<script type="text/javascript">
	var userLocale = ["${pageContext.response.locale}", "<%= pageContext.getResponse().getLocale().toLanguageTag() %>"];
</script>