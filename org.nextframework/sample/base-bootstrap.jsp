<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="n" uri="nextframework.tags.next"%>
<%@ taglib prefix="t" uri="nextframework.tags.template"%>

<%--
	Put this file on WEB-INF/jsp 
	Include <jsp:include page="${bodyPage}" /> where the body should be places
 --%>

<!DOCTYPE html>
<html class="h-100">
	<head>
		<n:head includeNextDirectJs="true" />
	</head>
	<body class="d-flex flex-column h-100">

		<header>
			<nav class="navbar navbar-expand-md navbar-dark bg-dark">
				<div class="container-fluid">
					<a class="navbar-brand" href="${app}">Next Application</a>
					<button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarCollapse" aria-controls="navbarCollapse" aria-expanded="false" aria-label="Abrir navegação">
						<span class="navbar-toggler-icon"></span>
					</button>
					<div class="collapse navbar-collapse justify-content-md-end" id="navbarCollapse">
						<n:menu menupath="/WEB-INF/menu.xml" orientation="right" subPanelStyleClass="dropdown-menu-dark" />
					</div>
				</div>
			</nav>
		</header>

		<main class="flex-shrink-0 pt-4">
			<n:messages /> 
			<jsp:include page="${bodyPage}" /> 
		</main>

		<footer class="footer mt-auto py-2 bg-light">
			<div class="container-fluid text-muted">
				Version 1.0
			</div>
		</footer>

	</body>
</html>