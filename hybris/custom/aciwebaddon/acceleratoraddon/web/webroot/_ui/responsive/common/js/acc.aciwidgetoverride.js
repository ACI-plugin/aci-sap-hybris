ACC.ACIwidgetOverride = {
	/*********Add merchant specific configurations here Start ***********/
	customconfig:{
		 style: "plain",
         showCVVHint: true,
		 onReady: function (){
    	   ACC.ACIwidget.onReady();
   		//Add custom logic here - Sample code given below
   		//	 $(".wpwl-group-cardHolder").after($(".wpwl-group-expiry"));
   		//	 $(".wpwl-group-cardNumber").before($(".wpwl-group-cardHolder"));

    	 //  $(".wpwl-group-cardNumber").after( $(".wpwl-group-brand"))

    	//   $(".wpwl-group-expiry").after( $(".wpwl-group-cvv"))

   		},
        //maskCvv: true
	}


}

