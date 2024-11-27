ACC.ACIwidget = {
	config:{},
	onSaveTransactionData: function(data) {
	        console.log(data);
	    	location.href = ACC.ACIwidget.config.processacipaymenturl;
	    	
	     },
		
     onError: function (error) {
    	 if (error.name == 'InvalidCheckoutIdError') {
    		 window.location.href = ACC.ACIwidget.config.errorurl;
    	 }
    	 
     },
     
     onReady: function() { 
    	 if ( $('#aciconfig').data('currentpage') == 'checkout') {
	    	 if (ACC.ACIwidget.config.isRegistered =='yes' ){
	    		 var createRegistrationHtml = '<div id="allowRegistration"><div class="customLabel">Store payment details?</div><div class="customInput"><input type="hidden" name="customParameters[SHOPPER_savedCard]" value="false"/><input id="create-registration" type="checkbox" name="createRegistration" value="true" /></div></div>';
	             $('form.wpwl-form-card').find('.wpwl-button').before(createRegistrationHtml);
	             $('#create-registration').click(function () {
	                 $("input[name='customParameters[SHOPPER_savedCard]']").val($("#create-registration").prop('checked'));
	             });
	    	 }	 
	    	 
	    	 $('.wpwl-control').change(function() {
				   if (ACC.ACIwidget.config.aciregallowedcards.includes($('.wpwl-control-brand').val())) {
							 $('#allowRegistration').show();
					}
					else {
					 $('#allowRegistration').hide();
					}
				});
	    	 if (ACC.ACIwidget.config.aciregallowedcards.includes($('.wpwl-control-brand').val())) {
					 $('#allowRegistration').show();
				}
				else {
				 $('#allowRegistration').hide();
				}
					
	 		if (ACC.ACIwidget.config.async) {
	 			$('.wpwl-button-brand').trigger('click');
	 		}
    	 }
  	},
  	
  	populateConfig: function() {
  		var aciconfig = {};
  		if ( $('#aciconfig').data('currentpage') == 'checkout') {
	  		aciconfig.processacipaymenturl = $('#aciconfig').data('processacipaymenturl');
	   		var str = $('#aciconfig').data('aciregallowedcards');
	   		var aciregallowedcards = str.split(" ");
	   		aciconfig.aciregallowedcards=aciregallowedcards;
	   		aciconfig.isRegistered = $('#aciconfig').data('isregistered');
	   		aciconfig.useSummary = $('#aciconfig').data('usesummary');
  		}
  		aciconfig.errorurl = $('#aciconfig').data('errorurl');
  		ACC.ACIwidget.config = aciconfig;
  	},
  	
  	initWidgetOptions: function () {
  		
  		ACC.ACIwidget.populateConfig();
        var options = {};
        if ( $('#aciconfig').data('currentpage') == 'checkout') {
			options.googlePay= {
        		gatewayMerchantId: "8a8294175d602369015d73bf009f1808"
			};
            options.useSummaryPage = ACC.ACIwidget.config.useSummary;
        	options.registrations = {
                requireCvv: true,
                hideInitialPaymentForms: false
            };
           
            options.onSaveTransactionData = ACC.ACIwidget.onSaveTransactionData;
        }
        options.onReady = ACC.ACIwidget.onReady;
        options.onError = ACC.ACIwidget.onError;
        //return Object.assign({}, options, ACC.ACIwidgetOverride.customconfig);
        return $.extend({}, options, ACC.ACIwidgetOverride.customconfig);
       
    }
     
}

