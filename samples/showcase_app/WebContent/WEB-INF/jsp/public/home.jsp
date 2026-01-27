<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="code" uri="code"%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/landing.css">

<%--
    Next Framework Code Tags - JSP Component Pattern

    <code:class>     - Container that registers methods and executes the main entry point
    <code:method>    - Defines a named reusable block (like a function)
    <code:main>      - The default entry point, executed automatically after all methods are registered
    <code:call>      - Invokes a method, passing attributes as parameters and body as ${CALL_BODY}

    This pattern separates CONTENT (what to show) from STRUCTURE (how to show it),
    making pages easier to maintain and components reusable.
--%>

<code:class>

<%-- ==================== CONTENT SECTIONS ==================== --%>

<code:method name="heroContent">
    <h1 class="display-4 landing-title mb-3">ERP-lite</h1>
    <h2 class="h4 landing-tagline mb-4">Simple business management for small companies</h2>
    <p class="landing-description mb-5">
        Manage customers, products, inventory, orders, and payments in one place.
        Everything you need to run your business, without the complexity.
    </p>
    <n:link url="/public/login" type="BUTTON" class="btn btn-login">Login</n:link>
</code:method>

<code:method name="screenshot">
    <span class="placeholder-logo">ERP-lite</span>
</code:method>

<code:method name="featuresTitle">
    Everything you need to manage your business
</code:method>

<code:method name="featureCards">
    <code:call method="featureCard" icon="bi-people" title="Customer Management">
        Keep track of all your customers, contacts, and interactions in one organized place.
    </code:call>
    <code:call method="featureCard" icon="bi-box-seam" title="Product Catalog & Inventory">
        Manage your products, track stock levels, and never run out of inventory again.
    </code:call>
    <code:call method="featureCard" icon="bi-cart" title="Sales Orders">
        Create and manage sales orders efficiently from quote to delivery.
    </code:call>
    <code:call method="featureCard" icon="bi-credit-card" title="Payments & Invoicing">
        Generate invoices, track payments, and manage your cash flow with ease.
    </code:call>
    <code:call method="featureCard" icon="bi-bar-chart" title="Reports & Charts">
        Get insights into your business with visual reports and analytics dashboards.
    </code:call>
    <code:call method="featureCard" icon="bi-file-earmark-text" title="Document Management">
        Store and organize all your business documents in one secure location.
    </code:call>
</code:method>

<code:method name="brandName">ERP-lite</code:method>

<code:method name="footerLinks">
    <a href="#">Home</a>
    <a href="#">About</a>
    <a href="#">Pricing</a>
    <a href="#">Contact</a>
</code:method>

<code:method name="footerSocial">
    <a href="#"><i class="bi bi-twitter"></i></a>
    <a href="#"><i class="bi bi-github"></i></a>
    <a href="#"><i class="bi bi-linkedin"></i></a>
</code:method>


<%-- ==================== PAGE STRUCTURE ==================== --%>

<%-- Reusable card component: icon, title passed as attributes; description as body --%>
<code:method name="featureCard">
    <div class="col-lg-4 col-md-6">
        <div class="feature-card p-4">
            <div class="feature-icon mb-3">
                <i class="bi ${icon}"></i>
            </div>
            <h5 class="fw-bold">${title}</h5>
            <p class="text-muted mb-0">${CALL_BODY}</p>
        </div>
    </div>
</code:method>

<code:main>
<div class="landing-page">

    <div class="landing-hero">
        <div class="container h-100">
            <div class="row h-100 align-items-center justify-content-center">
                <div class="col-lg-6 hero-content">
                    <code:call method="heroContent"/>
                </div>
                <div class="col-lg-4 offset-lg-1 d-none d-lg-block">
                    <div class="app-screenshot-wrapper">
                        <div class="app-screenshot-glow"></div>
                        <div class="app-screenshot-placeholder">
                            <code:call method="screenshot"/>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="features-section py-5">
        <div class="container">
            <h2 class="text-center fw-bold mb-5"><code:call method="featuresTitle"/></h2>
            <div class="row gy-5 gx-3 justify-content-center features-grid">
                <code:call method="featureCards"/>
            </div>
        </div>
    </div>

    <footer class="landing-footer py-4">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-lg-4 col-md-6">
                    <span class="footer-brand"><code:call method="brandName"/></span>
                </div>
                <div class="col-md-4 text-center">
                    <nav class="d-flex justify-content-center gap-4">
                        <code:call method="footerLinks"/>
                    </nav>
                </div>
                <div class="col-lg-4 col-md-6">
                    <div class="footer-social d-flex justify-content-end gap-2">
                        <code:call method="footerSocial"/>
                    </div>
                </div>
            </div>
        </div>
    </footer>

</div>
</code:main>

</code:class>
