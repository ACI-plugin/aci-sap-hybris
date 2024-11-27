<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="aci" tagdir="/WEB-INF/tags/addons/aciwebaddon/responsive/" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="noBorder" value=""/>
<c:if test="${not empty paymentInfoData}">
    <c:set var="noBorder" value="no-border"/>
</c:if>
<c:choose>
    <c:when test="${addOrEdit =='add'}">
	<div class="account-section-header no-border">
	       <spring:theme code="text.account.acipaymentDetails.add" />
	</div>
	
	<c:if test="${is_aci_active}">
		<spring:url value="/my-account/aci-payment-details" var="paymentDetailsUrl"/>
		
		<div>
			<a href="${paymentDetailsUrl}" class="add-edit-payment--link">
				<spring:theme code="text.account.acipaymentDetails.paymentdetails" />
			</a>
		</div>
		
		
		<div id="aciconfig"
			data-currentpage="account"
		    data-errorurl="${paymentDetailsUrl}"
		    
		></div>
		<script src="${aci_script_url}"></script>
		<div class="aciaccount-payment">
			<aci:acipaymentbrands/>
		</div>
		
	</c:if>
</c:when>
<c:otherwise>
	<c:choose>
	    <c:when test="${not empty paymentInfoData}">
	    	<div class="account-section-header ${noBorder}">
    			<spring:theme code="text.account.paymentDetails" />
			</div>
			
			<div>
	            <spring:url value="/my-account/aci-payment-details/add-widget" var="addPaymentDetailsUrl"/>
	
					<a href="${addPaymentDetailsUrl}" class="add-edit-payment--link">
						<spring:theme code="text.account.acipaymentDetails.add" />
					</a>
			</div>
	        <div class="account-paymentdetails account-list">
	            <div class="account-cards card-select">
	            
	                <div class="row">
	                	<c:forEach items="${paymentInfoData}" var="paymentInfo"> 
	                	
	                	<c:set var="isExpired" value="N"/>
	                	<c:choose>
	                	<c:when test="${paymentInfo.aciCardExpiryYear lt current_year }">
	                	<c:set var="isExpired" value="Y"/>
	                	</c:when>
	                	<c:when test="${paymentInfo.aciCardExpiryYear == current_year }">
							<c:set var="payMonth" value="${paymentInfo.aciCardExpiryMonth}"/>
							<c:if test="${fn:startsWith(payMonth, '0')}">
								<c:set var="payMonth" value="${fn:substring(payMonth,1,1)}"/>
							</c:if>
	                		<c:if test="${payMonth lt current_month }">
	                			<c:set var="isExpired" value="Y"/>	
	                		</c:if>
	                	</c:when>

	                	</c:choose>              
	                        <div class="col-xs-12 col-sm-6 col-md-4 card">
	                            <ul class="pull-left">
	                                
	                                <li>${fn:escapeXml(paymentInfo.aciCardHolder)}</li>
	                                <li>${fn:escapeXml(paymentInfo.aciPaymentBrand)}</li>
	                                <li>
	                                    <ycommerce:testId code="paymentDetails_item_cardNumber_text" >${fn:escapeXml(paymentInfo.aciCardNumber)}</ycommerce:testId>
	                                </li>
	                                <li>
	                                        ${fn:escapeXml(paymentInfo.aciCardExpiryMonth)}&nbsp;/&nbsp;${fn:escapeXml(paymentInfo.aciCardExpiryYear)}
	                                </li>
	                                <c:if test="${isExpired == 'Y' }">
	                                <p class="expiry-error">
											<spring:theme code="text.account.acipayment.expired" />
									</p>
	                                </c:if>
	                            </ul>
	                            <div class="account-cards-actions pull-left">
	                                <ycommerce:testId code="paymentDetails_deletePayment_button" >
	                                    <a class="action-links removePaymentDetailsButton" href="#" data-payment-id="${paymentInfo.id}" data-popup-title="<spring:theme code="text.account.paymentDetails.delete.popup.title"/>">
	                                        <span class="glyphicon glyphicon-remove"></span>
	                                    </a>
	                                </ycommerce:testId>
	                            </div>
	                            
	                        </div>
	                    
		                    <div class="display-none">
		                        <div id="popup_confirm_payment_removal_${paymentInfo.id}" class="account-address-removal-popup">
		                            <spring:theme code="text.account.paymentDetails.delete.following"/>
		                            <div class="address">
		                                <strong>
		                                ${fn:escapeXml(paymentInfo.aciCardHolder)}
		                                </strong>
		                                <br>${fn:escapeXml(paymentInfo.aciPaymentBrand)}
		                                <br>${fn:escapeXml(paymentInfo.aciCardNumber)}
		                                <br>
		                                
		                                ${fn:escapeXml(paymentInfo.aciCardExpiryMonth)}&nbsp;/&nbsp;${fn:escapeXml(paymentInfo.aciCardExpiryYear)}
		                               
		                            </div>
		                            <c:url value="/my-account/aci-payment-details/remove-payment-method" var="removePaymentActionUrl"/>
		                            <form:form id="removePaymentDetails${paymentInfo.id}" action="${removePaymentActionUrl}" method="post">
		                                <input type="hidden" name="paymentInfoId" value="${paymentInfo.id}"/>
		                                <br />
		                                <div class="modal-actions">
		                                    <div class="row">
		                                        <ycommerce:testId code="paymentDetailsDelete_delete_button" >
		                                            <div class="col-xs-12 col-sm-6 col-sm-push-6">
		                                                <button type="submit" class="btn btn-default btn-primary btn-block paymentsDeleteBtn">
		                                                    <spring:theme code="text.account.paymentDetails.delete"/>
		                                                </button>
		                                            </div>
		                                        </ycommerce:testId>
		                                        <div class="col-xs-12 col-sm-6 col-sm-pull-6">
		                                            <a class="btn btn-default closeColorBox paymentsDeleteBtn btn-block" data-payment-id="${paymentInfo.id}">
		                                                <spring:theme code="text.button.cancel" />
		                                            </a>
		                                        </div>
		                                    </div>
		                                </div>
		                            </form:form>
		                        </div>
		                    </div>
	                	</c:forEach>
	            	</div> 
	            </div>
	        </div>
	    </c:when>
	    <c:otherwise>
	    	
	    	<div>
	            <spring:url value="/my-account/aci-payment-details/add-widget" var="addPaymentDetailsUrl"/>
					<a href="${addPaymentDetailsUrl}" class="add-edit-payment--link">
						<spring:theme code="text.account.acipaymentDetails.add" />
					</a>
			</div>
	        <div class="account-section-content content-empty">
	            <spring:theme code="text.account.paymentDetails.noPaymentInformation" />
	        </div>
	    </c:otherwise>
	</c:choose>
    </c:otherwise>
</c:choose>