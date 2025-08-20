<%@ include file="/sys/jsv/template/template.head.jsp" %>
<%@ include file="/ekp/bbs/config.jsp" %>
<%@ page import="com.kcube.ekp.bbs.BdItemSql" %>
<%@page import="com.kcube.cst.local.InterfaceConfig"%>
<%
	BdItemSql bs = new BdItemSql(moduleParam, ctx.getLong("tr", null), ctx.getParameter("com.kcube.doc.list"), false);
%>
<%!
	InterfaceConfig _conf = (InterfaceConfig) com.kcube.sys.conf.ConfigService.getConfig(InterfaceConfig.class);
%>

<script type="text/javascript" src="<%= request.getContextPath() %>/ekp/bbs/spin.min.js"></script>
<script type="text/javascript">
var spinner;
JSV.Block(function () {
	var template = '<template color="green" name="com.kcube.doc.list" catalog="/ekp/bbs/catalog.xml.jsp" listType="LIST">\
		<header label="<kfmt:message key="bbs.001"/>"/>\
		<columns class="Array">\
			<column property="id.list"/>\
			<column property="title.list"/>\
			<column property="author.list"/>\
			<column property="rgstDate.list"/>\
			<column property="readCnt.list"/>\
			<column property="attachments.list"/>\
		</columns>\
		<search class="Array" periodColumn="rgstDate">\
			<option property="title.list"/>\
			<option property="author.list"/>\
			<option property="tags.search"/>\
			<option property="id.search"/>\
		</search>'
<% if (bs.isCountCondition()) { %>
		 + '<footer/>'
		 + '<count/>'
		 + '<pageMover/>'
<% } else { %>
		 + '<footer count="disabled"/>'
<% } %>
		+ '<rows name="com.kcube.doc.rowsPerPage"/>\
	</template>';
	
	if (JSV.getParameter('tr')) {
		JSV.setState('tr', JSV.getParameter('tr'));
	}
	var t = new TableTemplate(document.getElementById('main'), template);
	t.setDataUrl('/jsl/BdItemUser.ListByUser.json', 'ts', '/jsl/BdItemAnnounce.AnnounceListByUser.json');
	var folderName = FoldersInformationViewer({'folderIds':JSV.getParameter('tr'), 'defaultText':'<kfmt:message key="bbs.001"/>'});
	t.header.setLabel(folderName);

	ScrapMenu.favoriteList(t.layout.titleLeft, '<%=com.kcube.ekp.bbs.BdItemHistory.ALIMI_BBS%>', 'com.kcube.ekp.bbs.BdItemConfig.fvrtIndexUrl', JSV.getParameter('tr'), folderName);
	
	var useSub = <%=!useCtgr(moduleParam) || bs.getLevel() > 1%>;
	if (useSub)
		Subscription.kmId(t.layout.titleLeft, '<%=com.kcube.ekp.bbs.BdItemHistory.ALIMI_BBS%>', JSV.getParameter('tr'));
	 
	var write = new KButton(t.layout.titleRight, <fmt:message key="btn.doc.010"/>);
	write.onclick = function() {
		JSV.doGET('usr.write.jsp');
	};
	
/* 	var i = 0;
	let interval = setInterval(callback, 1000);

	function callback() {
		console.log(i);
		i++;
	}
	
	window.addEventListener('focus', function() {
		interval = setInterval(callback, 1000);
	});

	window.addEventListener('blur', function() {
		clearInterval(interval);
	}); */
	
});

if(<%=_conf.getOriginBbs()%> != JSV.getAppId()){
	TableRows.prototype.getInitial = function() {
		if (this.name && JSV.getCookie(this.name)) {
			return parseInt(JSV.getCookie(this.name));
		} else {
			//this.rows = '10:10,15:15,20:20,30:30,40:40,50:50'
			//한페이지 개수 바꾸고 싶을땐 this.row[?] 숫자만 바꾸면됨.
			//50개 이상으로 할땐 주석처리하고 tableviewer에서 직접변수 바꿔줌.
			var initValue = this.rows[2].split(':');
			return initValue[0];
		}
	}
}
</script>
<%@ include file="/sys/jsv/template/template.body.jsp" %>
<div id="main"></div>
<%@ include file="/sys/jsv/template/template.tail.jsp" %>
