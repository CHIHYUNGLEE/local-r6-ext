<%@ include file="/jspf/head.portlet.jsp" %>
<%@page import="com.kcube.ekp.mbs.inapp.MbInApp"%>
<%@ include file="/ekp/mbs/inapp/config.jsp" %>
<style>
	#main${PORTLET_ID}{padding: 30px;}
</style>
<script type="text/javascript">
JSV.Block(function () {
	var appType = treeFrame.tree.getObject(treeFrame.selectId).getAttribute('appType');
	var basicData = [
		{id:'manager', key:'xid', title:'*<kfmt:message key="mbs.inapp.050"/>'}
	];
	if(appType != <%=MbInApp.APP_URL%>){
		<%if(useReadScrt(moduleParam)){%>
			basicData.push({id:'readScrt', key:'xid', title:'*<kfmt:message key="mbs.inapp.051"/>'});
		<%}%>
		<%if(useWriteScrt(moduleParam)){%>
			basicData.push({id:'writeScrt', key:'xid', title:'*<kfmt:message key="mbs.inapp.052"/>'});
		<%}%>
		<%if(useRplyScrt(moduleParam)){%>
		basicData.push({id:'rplyScrt', key:'xid', title:'*<fmt:message key="modl.048"/>'});
		<%}%>
	}
	basicData.push({id:'exclScrt', key:'xid', title:'*<kfmt:message key="mbs.inapp.053"/>'})
	
	var scrtStyle = {
			isDel: true,
			isEdit: true,
			size : 11,
			isSortable: true,
			titleTxt : '<fmt:message key="space.734"/>',
			basicData : basicData,
	};
	var scrt = new CommonListFieldEditor(document.getElementById('main${PORTLET_ID}'), scrtStyle);
	scrt.setLabelProvider(new ListViewerLabelProvider('title'));
	scrt.onBasicListClick = function(dataObj, liArea, seq){
		dataObj['PORTLET_ID'] = seq;
		scrt.$rightAreaInner.load(JSV.getContextPath('/ekp/mbs/inapp/adm.inapp.scrtSetting.jsp'), dataObj);
	}
	scrt.setValue({});
	scrt.firstSelect();
}, '${PORTLET_ID}');
</script>
<div id="main${PORTLET_ID}"></div>
<%@ include file="/jspf/tail.portlet.jsp" %>