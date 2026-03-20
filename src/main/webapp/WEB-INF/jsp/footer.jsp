<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<footer class="bottombar">
    <div class="footer-main">
        <div>
            <a class="footer-brand-logo" href="${pageContext.request.contextPath}/home">
                <img src="${pageContext.request.contextPath}/images/logo.png" alt="SneakerZone">
            </a>
            <p class="footer-tagline">Le migliori sneaker,<br>consegnate a casa tua.</p>
        </div>
        <div>
            <h4 class="footer-heading">Esplora</h4>
            <ul class="footer-links">
                <li><a href="${pageContext.request.contextPath}/catalogo?genere=Uomo">Uomo</a></li>
                <li><a href="${pageContext.request.contextPath}/catalogo?genere=Donna">Donna</a></li>
                <li><a href="${pageContext.request.contextPath}/catalogo?genere=Unisex">Unisex</a></li>
            </ul>
        </div>
        <div>
            <h4 class="footer-heading">Account</h4>
            <ul class="footer-links">
                <c:choose>
                    <c:when test="${not empty sessionScope.utenteConnesso}">
                        <li><a href="${pageContext.request.contextPath}/myAccount">Profilo</a></li>
                        <li><a href="${pageContext.request.contextPath}/wishlist">Wishlist</a></li>
                        <li><a href="${pageContext.request.contextPath}/ordini">I miei ordini</a></li>
                    </c:when>
                    <c:otherwise>
                        <li><a href="${pageContext.request.contextPath}/login">Accedi</a></li>
                        <li><a href="${pageContext.request.contextPath}/registrazione">Registrati</a></li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
        <div>
            <h4 class="footer-heading">Supporto</h4>
            <address class="footer-address">
                <p><i class="ti ti-mail" aria-hidden="true"></i> supporto@sneakerzone.it</p>
            </address>
        </div>
    </div>
    <div class="footer-bottom">
        <p class="footer-copy">
            &copy; 2025 SneakerZone &middot; Tutti i diritti riservati
        </p>
    </div>
</footer>
<script src="${pageContext.request.contextPath}/js/common.js"></script>
<script src="${pageContext.request.contextPath}/js/wishlist.js"></script>
