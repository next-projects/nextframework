<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="container mt-4">
    <div class="row">
        <div class="col-12">
            <h1>Welcome to ERP-lite</h1>
            <c:if test="${not empty USER}">
                <p class="lead">Hello, <strong>${USER.name != null ? USER.name : USER.username}</strong>!</p>
            </c:if>
            <hr>
            <div class="row">
                <div class="col-md-4 mb-3">
                    <div class="card">
                        <div class="card-body">
                            <h5 class="card-title">Customers</h5>
                            <p class="card-text">Manage your customer database</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-4 mb-3">
                    <div class="card">
                        <div class="card-body">
                            <h5 class="card-title">Products</h5>
                            <p class="card-text">Manage your product catalog</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-4 mb-3">
                    <div class="card">
                        <div class="card-body">
                            <h5 class="card-title">Orders</h5>
                            <p class="card-text">View and manage orders</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
