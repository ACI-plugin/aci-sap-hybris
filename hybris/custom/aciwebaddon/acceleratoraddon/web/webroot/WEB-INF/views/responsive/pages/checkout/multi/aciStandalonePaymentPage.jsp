<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/responsive/checkout/multi"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="aci" tagdir="/WEB-INF/tags/addons/aciwebaddon/responsive/" %>

<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">

<div class="row">
    <div class="col-sm-6">
        <div class="checkout-headline">
            <span class="glyphicon glyphicon-lock"></span>
            <spring:theme code="checkout.multi.secure.checkout" />
        </div>
		<multi-checkout:checkoutSteps checkoutSteps="${checkoutSteps}" progressBarId="${progressBarId}">
			<jsp:body>
			<div class="checkout-paymentmethod">
                <div class="checkout-indent">
 
                    <div class="headline">
	                    <c:choose>
	                        <c:when test="${flow_type eq 'SUMMARY'}"><spring:theme code="checkout.multi.aciPayMethod"/></c:when>
		                    <c:otherwise><spring:theme code="checkout.multi.aciPaymentMethod"/></c:otherwise>
		                 </c:choose>
                    </div>

				<ycommerce:testId code="checkoutStepTwo">
				
				<aci:selectpayment/>
				</ycommerce:testId>
				</div>
				</div>
			</jsp:body>
		</multi-checkout:checkoutSteps>
    </div>

    <div class="col-sm-6 hidden-xs">
		<multi-checkout:checkoutOrderDetails cartData="${cartData}" showDeliveryAddress="true" showPaymentInfo="false" showTaxEstimate="false" showTax="true" />
    </div>

    <div class="col-sm-12 col-lg-12">
        <cms:pageSlot position="SideContent" var="feature" element="div" class="checkout-help">
            <cms:component component="${feature}"/>
        </cms:pageSlot>
    </div>
</div>

</template:page>
<script>
window.wpwlOptions = ACC.ACIwidget.initWidgetOptions();
</script>

<script asyn="false" src="${aci_script_url}"></script>