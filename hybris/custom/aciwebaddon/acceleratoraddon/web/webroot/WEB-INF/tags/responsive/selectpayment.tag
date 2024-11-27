<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %> 
<%@ taglib prefix="address" tagdir="/WEB-INF/tags/responsive/address" %>
<%@ taglib prefix="aci" tagdir="/WEB-INF/tags/addons/aciwebaddon/responsive/" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="isregistered" value="no" />
<sec:authorize access="isFullyAuthenticated()">
	<c:set var="isregistered" value="yes" />
</sec:authorize>
<c:if test="${is_aci_active}">
<div id="aciconfig"
	data-currentpage="checkout"
    data-errorurl="${cartUrl}"
    data-processacipaymenturl="${process_aci_payment_url}"
    data-aciregallowedcards="${aci_reg_allowed_cards}"
   	data-isregistered="${isregistered}"
   	data-usesummary="${useSummary}"
></div>

<div id="acipayments">
<c:if test="${not empty aci_checkoutid}">
	<aci:acipaymentbrands/>
</c:if>
</div>
		


</c:if>