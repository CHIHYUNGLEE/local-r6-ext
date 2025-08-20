<%@ include file="/jspf/head.portlet.jsp" %>
<style>
<!--

-->
</style>
<script>
JSV.Block(function (){
	var gamePopup  = new KButton(btnArea${PORTLET_ID}, <fmt:message key="cstm.rsp.032"/>);
	
	gamePopup.onclick = function() {
		window.open(JSV.getContextPath('/cst/local/ptl/ptl.gamePopup.jsp'), 'Game', 'height=750px,width=1300px,toolbar=no,status=no');
	}
}, '${PORTLET_ID}');

</script>
<div id="btnArea${PORTLET_ID}" class="btnArea${PORTLET_ID}"></div>
<%@ include file="/jspf/tail.portlet.jsp" %>