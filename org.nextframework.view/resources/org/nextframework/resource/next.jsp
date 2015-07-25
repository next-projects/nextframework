<%@ page contentType="text/html; charset=ISO-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<HTML>
	<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
	</head>
	<BODY>
		<c:if test="${empty error}">
			<h1>Parabéns, sua aplicação está ligada e funcionando corretamente.</h1>
		</c:if>
		<c:if test="${!empty error}">
			<h1 style="color: #AA0000">Houve um problema ao inicializar a aplicação! Verifique o log para mais informações</h1>
			<div>${error}</div>
			<c:if test="${!empty error}">
				<div style="padding-left: 20px">-> ${error.cause}</div>
			</c:if>
			
		</c:if>
		<p style="font-style: italic">powered by NEXT FRAMEWORK</p>
		
	</BODY>
</HTML>