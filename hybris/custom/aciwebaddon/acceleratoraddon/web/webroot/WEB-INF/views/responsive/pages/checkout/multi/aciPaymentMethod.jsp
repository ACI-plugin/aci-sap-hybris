<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="multiCheckout" tagdir="/WEB-INF/tags/responsive/checkout/multi"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="address" tagdir="/WEB-INF/tags/responsive/address" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="aci" tagdir="/WEB-INF/tags/addons/aciwebaddon/responsive/" %>

<c:url value="${currentStepUrl}" var="choosePaymentMethodUrl" />
<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">
<div class="row">
    <div class="col-sm-6">
        <div class="checkout-headline">
            <span class="glyphicon glyphicon-lock"></span>
            <spring:theme code="checkout.multi.secure.checkout"/>
        </div>
		<multiCheckout:checkoutSteps checkoutSteps="${checkoutSteps}" progressBarId="${progressBarId}">
			<jsp:body>
             
                    <div class="checkout-paymentmethod">
                        <div class="checkout-indent">

                                <ycommerce:testId code="paymentDetailsForm">
							
								<spring:url value="/checkout/multi/aci/billing/process-address" var="aciPaymentAddressProcess"/>
							
								<form:form id="aciPaymentAddressForm" name="aciPaymentAddressForm" modelAttribute="aciPaymentDetailsForm" action="${aciPaymentAddressProcess}" method="POST">
									<hr/>
                                    <div class="headline">
                                        <spring:theme code="checkout.multi.paymentMethod.header"/>
                                    </div>

                                    <c:if test="${cartData.deliveryItemsQuantity > 0}">

                                        <div id="useDeliveryAddressData"
                                            data-titlecode="${deliveryAddress.titleCode}"
                                            data-firstname="${deliveryAddress.firstName}"
                                            data-lastname="${deliveryAddress.lastName}"
                                            data-line1="${deliveryAddress.line1}"
                                            data-line2="${deliveryAddress.line2}"
                                            data-town="${deliveryAddress.town}"
                                            data-postalcode="${deliveryAddress.postalCode}"
                                            data-countryisocode="${deliveryAddress.country.isocode}"
                                            data-regionisocode="${deliveryAddress.region.isocodeShort}"
                                            data-address-id="${deliveryAddress.id}"
                                        ></div>
                                        <formElement:formCheckbox
                                            path="useDeliveryAddress"
                                            idKey="useDeliveryAddress"
                                            labelKey="checkout.multi.sop.useMyDeliveryAddress"
                                            tabindex="11"/>
                                    </c:if>
				  
                                    <input type="hidden" value="${silentOrderPageData.parameters['billTo_email']}" class="text" name="billTo_email" id="billTo_email">
                                    <address:billAddressFormSelector supportedCountries="${countries}" regions="${regions}" tabindex="12"/>
				
									<p class="help-block"><spring:theme code="checkout.multi.paymentMethod.seeOrderSummaryForMoreInformation"/></p>							
									<form:hidden path="paymentId" class="create_update_payment_id"/>
									</form:form>
							</ycommerce:testId>
                         </div>
                    </div>

                    <button type="button" class="btn btn-primary btn-block submit_standalonePaymentAddressForm checkout-next"><spring:theme code="checkout.multi.paymentMethod.continue"/></button>
 
		   </jsp:body>
		</multiCheckout:checkoutSteps>
	</div>

	<div class="col-sm-6 hidden-xs">
		<multiCheckout:checkoutOrderDetails cartData="${cartData}" showDeliveryAddress="true" showPaymentInfo="false" showTaxEstimate="false" showTax="true" />
    </div>

    <div class="col-sm-12 col-lg-12">
        <cms:pageSlot position="SideContent" var="feature" element="div" class="checkout-help">
            <cms:component component="${feature}"/>
        </cms:pageSlot>
    </div>
</div>

</template:page>
<script>
	var wpwlOptions=ACC.ACIwidget.initWidgetOptions();

</script>
<script src="${aci_script_url}"></script>