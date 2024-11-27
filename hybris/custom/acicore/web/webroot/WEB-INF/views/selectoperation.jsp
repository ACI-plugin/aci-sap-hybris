<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE html>
<html>
<head>
    <title>Backoffice Stimulator</title>
    <link rel="stylesheet" href="<c:url value="/static/acicore-webapp.css"/>" type="text/css"
          media="screen, projection"/>
      
</head>
<body>
		<div class="container">
		
		
		    <img src="<c:url value="${logoUrl}" />" alt="Hybris platform logo"/>
		
		    <h2>Welcome to ACI </h2>
		
		    <h3><b>Getting started</b></h3>
		</div>
    <form action="<c:url value="/backoffice/ops/simulator"/>" method="POST">
        <div id="logincontrols" class="logincontrols">
            <h3><b>${status}</b></h3>
            <fieldset class="login-form">
                <p>
                  Enter Order No :   <input type="text" name="orderNo"/>
                </p>
                <p>
                 Enter amount :    <input type="text" name="amount"/>
                </p>
                <p>
                    <select name ="operation" id="operation" class="doFlowSelectedChange form-control">
                    	<option value="PARTIAL_CAPTURE"/>PARTIAL_CAPTURE</option>
                    	<option value="REFUND"/>REFUND</option>
                    	<option value="REVERSAL"/>REVERSAL</option>
               		</select>
                </p>
                
                <p>
                    <button type="submit" class="button" autofocus>process</button>
                  
                </p>
                
            </fieldset>
    </form>
</div>
</body>
</html>