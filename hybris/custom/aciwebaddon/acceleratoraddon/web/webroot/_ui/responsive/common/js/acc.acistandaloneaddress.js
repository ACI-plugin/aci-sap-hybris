ACC.acistandalone = {

	spinner: $("<img src='" + ACC.config.commonResourcePath + "/images/spinner.gif' />"),

	bindUseDeliveryAddress: function ()
	{
		$('#useDeliveryAddress').on('change', function ()
		{
			if ($('#useDeliveryAddress').is(":checked"))
			{
				var options = {'countryIsoCode': $('#useDeliveryAddressData').data('countryisocode'), 'useDeliveryAddress': true};
				ACC.acistandalone.enableAddressForm();
				ACC.acistandalone.displayCreditCardAddressForm(options, ACC.acistandalone.useDeliveryAddressSelected);
				ACC.acistandalone.disableAddressForm();
			}
			else
			{
				ACC.acistandalone.clearAddressForm();
				ACC.acistandalone.enableAddressForm();
			}
		});

		if ($('#useDeliveryAddress').is(":checked"))
		{
			var options = {'countryIsoCode': $('#useDeliveryAddressData').data('countryisocode'), 'useDeliveryAddress': true};
			ACC.acistandalone.enableAddressForm();
			ACC.acistandalone.displayCreditCardAddressForm(options, ACC.acistandalone.useDeliveryAddressSelected);
			ACC.acistandalone.disableAddressForm();
		}
	},

	bindStandalonePaymentAddressForm: function ()
	{
		$('.submit_standalonePaymentAddressForm').click(function ()
		{
			var paymentOption = $('input[type=radio][name=paymentMethod]:checked').val();
			ACC.common.blockFormAndShowProcessingMessage($(this));
			$('.billingAddressForm').filter(":hidden").remove();
			ACC.acistandalone.enableAddressForm();
			$("#aciPaymentAddressForm input[id=paymentId]").val(paymentOption);
			$('#aciPaymentAddressForm').submit();
		});
	},

	bindCycleFocusEvent: function ()
	{
		$('#lastInTheForm').blur(function ()
		{
			$('#aciPaymentAddressForm[tabindex$="10"]').focus();
		})
	},

	isEmpty: function (obj)
	{
		if (typeof obj == 'undefined' || obj === null || obj === '') return true;
		return false;
	},

	disableAddressForm: function ()
	{
		$('input[id^="address\\."]').prop('disabled', true);
		$('select[id^="address\\."]').prop('disabled', true);
	},

	enableAddressForm: function ()
	{
		$('input[id^="address\\."]').prop('disabled', false);
		$('select[id^="address\\."]').prop('disabled', false);
	},

	clearAddressForm: function ()
	{
		$('input[id^="address\\."]').val("");
		$('select[id^="address\\."]').val("");
	},

	useDeliveryAddressSelected: function ()
	{
		if ($('#useDeliveryAddress').is(":checked"))
		{
			$('#address\\.country').val($('#useDeliveryAddressData').data('countryisocode'));
			ACC.acistandalone.disableAddressForm();
		}
		else
		{
			ACC.acistandalone.clearAddressForm();
			ACC.acistandalone.enableAddressForm();
		}
	},
	
	

	bindCreditCardAddressForm: function ()
	{
		$('#billingCountrySelector :input').on("change", function ()
		{
			var countrySelection = $(this).val();
			var options = {
				'countryIsoCode': countrySelection,
				'useDeliveryAddress': false
			};
			ACC.acistandalone.displayCreditCardAddressForm(options);
		})
	},

	displayCreditCardAddressForm: function (options, callback)
	{
		$.ajax({ 
			url: ACC.config.encodedContextPath + '/checkout/multi/sop/billingaddressform',
			async: true,
			data: options,
			dataType: 'html',
			beforeSend: function ()
			{
				$('#billingAddressForm').html(ACC.acistandalone.spinner);
			}
		}).done(function (data)
				{
					$("#billingAddressForm").html(data);
					if (typeof callback == 'function')
					{
						callback.call();
					}
				});
	}
}

$(document).ready(function ()
{
	with (ACC.acistandalone)
	{
		//bindUseDeliveryAddress()
		bindStandalonePaymentAddressForm();
		//bindCreditCardAddressForm();
	}

	// check the checkbox
	//$("#useDeliveryAddress").click();
});
