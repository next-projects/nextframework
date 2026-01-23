<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/landing.css">

<div class="landing-page">

<div class="landing-hero">
    <div class="container h-100">
        <div class="row h-100 align-items-center justify-content-center">
            <div class="col-lg-6 hero-content">
                <h1 class="display-4 landing-title mb-3">ERP-lite</h1>
                <h2 class="h4 landing-tagline mb-4">Simple business management for small companies</h2>
                <p class="landing-description mb-5">
                    Manage customers, products, inventory, orders, and payments in one place.
                    Everything you need to run your business, without the complexity.
                </p>
                <n:link url="/next" type="BUTTON" class="btn btn-login">Login</n:link>
            </div>
            <div class="col-lg-4 offset-lg-1 d-none d-lg-block">
                <!-- TODO: Add app screenshot here -->
                <div class="app-screenshot-wrapper">
                    <div class="app-screenshot-glow"></div>
                    <div class="app-screenshot-placeholder">
                        <span class="placeholder-logo">ERP-lite</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="features-section py-5">
    <div class="container">
        <h2 class="text-center fw-bold mb-5">Everything you need to manage your business</h2>
        <div class="row gy-5 gx-3 justify-content-center features-grid">
            <div class="col-lg-4 col-md-6">
                <div class="feature-card p-4">
                    <div class="feature-icon mb-3">
                        <i class="bi bi-people"></i>
                    </div>
                    <h5 class="fw-bold">Customer Management</h5>
                    <p class="text-muted mb-0">Keep track of all your customers, contacts, and interactions in one organized place.</p>
                </div>
            </div>
            <div class="col-lg-4 col-md-6">
                <div class="feature-card p-4">
                    <div class="feature-icon mb-3">
                        <i class="bi bi-box-seam"></i>
                    </div>
                    <h5 class="fw-bold">Product Catalog & Inventory</h5>
                    <p class="text-muted mb-0">Manage your products, track stock levels, and never run out of inventory again.</p>
                </div>
            </div>
            <div class="col-lg-4 col-md-6">
                <div class="feature-card p-4">
                    <div class="feature-icon mb-3">
                        <i class="bi bi-cart"></i>
                    </div>
                    <h5 class="fw-bold">Sales Orders</h5>
                    <p class="text-muted mb-0">Create and manage sales orders efficiently from quote to delivery.</p>
                </div>
            </div>
            <div class="col-lg-4 col-md-6">
                <div class="feature-card p-4">
                    <div class="feature-icon mb-3">
                        <i class="bi bi-credit-card"></i>
                    </div>
                    <h5 class="fw-bold">Payments & Invoicing</h5>
                    <p class="text-muted mb-0">Generate invoices, track payments, and manage your cash flow with ease.</p>
                </div>
            </div>
            <div class="col-lg-4 col-md-6">
                <div class="feature-card p-4">
                    <div class="feature-icon mb-3">
                        <i class="bi bi-bar-chart"></i>
                    </div>
                    <h5 class="fw-bold">Reports & Charts</h5>
                    <p class="text-muted mb-0">Get insights into your business with visual reports and analytics dashboards.</p>
                </div>
            </div>
            <div class="col-lg-4 col-md-6">
                <div class="feature-card p-4">
                    <div class="feature-icon mb-3">
                        <i class="bi bi-file-earmark-text"></i>
                    </div>
                    <h5 class="fw-bold">Document Management</h5>
                    <p class="text-muted mb-0">Store and organize all your business documents in one secure location.</p>
                </div>
            </div>
        </div>
    </div>
</div>

<footer class="landing-footer py-4">
    <div class="container">
        <div class="row align-items-center">
            <div class="col-lg-4 col-md-6">
                <span class="footer-brand">ERP-lite</span>
            </div>
            <div class="col-md-4 text-center">
                <nav class="d-flex justify-content-center gap-4">
                    <a href="#">Home</a>
                    <a href="#">About</a>
                    <a href="#">Pricing</a>
                    <a href="#">Contact</a>
                </nav>
            </div>
            <div class="col-lg-4 col-md-6">
                <div class="footer-social d-flex justify-content-end gap-2">
                    <a href="#"><i class="bi bi-twitter"></i></a>
                    <a href="#"><i class="bi bi-github"></i></a>
                    <a href="#"><i class="bi bi-linkedin"></i></a>
                </div>
            </div>
        </div>
        <div class="text-center mt-3 pt-3 border-top border-secondary border-opacity-25">
            <small>Powered by Next Framework</small>
        </div>
    </div>
</footer>

</div>
