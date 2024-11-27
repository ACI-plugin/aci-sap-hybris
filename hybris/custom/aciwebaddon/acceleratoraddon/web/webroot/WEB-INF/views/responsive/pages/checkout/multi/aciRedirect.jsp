<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<script type="text/javascript" src="${commonResourcePath}/js/jquery-3.7.0.min.js"></script>

<form  action="${aci_summary_action}" method="POST"> 
<div style="display:none">
	<input class="summaryForm" type="submit" value="Pay now" /> 
</div>
</form>
<script>
$(document).ready(function (){
	$('.summaryForm').trigger('click');
});
</script>
