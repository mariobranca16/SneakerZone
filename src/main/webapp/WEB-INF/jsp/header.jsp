<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<header class="topbar">
    <div class="topbar-inner">
        <a class="topbar-logo" href="${pageContext.request.contextPath}/home">
            <img src="${pageContext.request.contextPath}/images/logo.png" alt="SneakerZone">
        </a>
        <nav>
            <ul class="topbar-nav">
                <li><a href="${pageContext.request.contextPath}/catalogo">
                    <i class="ti ti-building-store topbar-nav-icon"></i><span class="topbar-nav-label"> Catalogo</span>
                </a></li>
                <c:if test="${not empty sessionScope.utenteConnesso}">
                    <li>
                        <a class="topbar-icon-btn" href="${pageContext.request.contextPath}/wishlist"
                           aria-label="Wishlist">
                            <i class="ti ti-heart"></i>
                            <c:if test="${sessionScope.wishlistCount > 0}">
                                <span class="cart-badge" id="wishlist-badge">${sessionScope.wishlistCount}</span>
                            </c:if>
                        </a>
                    </li>
                </c:if>
                <c:choose>
                    <c:when test="${not empty sessionScope.utenteConnesso}">
                        <li class="dropdown">
                            <button class="topbar-icon-btn user-menu-toggle" type="button"
                                    aria-haspopup="true" aria-expanded="false" aria-label="Menu account">
                                <i class="ti ti-user"></i>
                            </button>
                            <ul class="dropdown-menu">
                                <li><a href="${pageContext.request.contextPath}/myAccount">Profilo</a></li>
                                <li><a href="${pageContext.request.contextPath}/ordini">I miei ordini</a></li>
                                <c:if test="${sessionScope.utenteConnesso.admin}">
                                    <li><a href="${pageContext.request.contextPath}/admin/dashboard">Pannello Admin</a>
                                    </li>
                                </c:if>
                                <li><a href="${pageContext.request.contextPath}/logout">Logout</a></li>
                            </ul>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li><a href="${pageContext.request.contextPath}/login">
                            <i class="ti ti-login topbar-nav-icon"></i><span
                                class="topbar-nav-label"> Login</span>
                        </a></li>
                    </c:otherwise>
                </c:choose>
                <li>
                    <a class="topbar-icon-btn" href="${pageContext.request.contextPath}/carrello" aria-label="Carrello">
                        <i class="ti ti-shopping-cart"></i>
                        <c:if test="${sessionScope.carrello != null and sessionScope.carrello.totaleArticoli > 0}">
                            <span class="cart-badge">${sessionScope.carrello.totaleArticoli}</span>
                        </c:if>
                    </a>
                </li>
            </ul>
        </nav>
    </div>
</header>
