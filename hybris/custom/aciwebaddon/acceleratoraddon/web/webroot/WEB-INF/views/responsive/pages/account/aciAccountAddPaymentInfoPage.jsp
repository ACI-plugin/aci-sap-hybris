<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="aci" tagdir="/WEB-INF/tags/addons/aciwebaddon/responsive/" %>

<c:set var="noBorder" value=""/>
<c:if test="${not empty paymentInfoData}">
    <c:set var="noBorder" value="no-border"/>
</c:if>

<div class="account-section-header ${noBorder}">
    <spring:theme code="text.account.acipaymentDetails.add" />
</div>
 <div class="account-paymentdetails account-list">
		
<c:if test="${is_aci_active}">
<spring:url value="/aci/my-account/payment-details" var="returnUrl"/>

<div id="aciconfig"
	data-currentpage="account"
    data-errorurl="${returnUrl}"
    
></div>
<script src="${aci_script_url}"></script>
	<div class="aciaccount-payment">
		<aci:acipaymentbrands/>
	</div>
</c:if>
</div>