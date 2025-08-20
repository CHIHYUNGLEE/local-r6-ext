<%
request.setAttribute("WSCKP", "workId=" + (String)request.getParameter("id"));
request.setAttribute("WSCK_CURR", "true");
%><%@ include file="/sys/jsv/template/template.inner.head.jsp"%>
<%@ include file="/work/classLoader.jsp"%>
<%@ include file="/work/jsv-work-ext.jsp"%>
<%@ include file="/work/config.jsp" %>
<%@page import="com.kcube.sys.usr.UserService"%>
<%@page import="com.kcube.sys.emp.EmployeeService"%>
<%@page import="com.kcube.sys.emp.Employee"%>
<%@page import="com.kcube.work.Work"%>
<style>
.TemplateLayoutLeft {min-width: 600px;}
.TaskStatusFieldEditor.mainTitle_rcmdCnt {margin:6px 5px 0 0;}

.MainTitleViewer .preIcon .fvrt {margin:10px 5px 0 0;}
.MainTitleViewer .titleSpan .mainTitle_title {color: #222; font-size: 18px; line-height: 20px; font-family: nanumgothic,'Gulim'; letter-spacing: -1px; font-weight: 600; padding: 1px 0 0 22px;}
.MainTitleViewer .contentInfo .infoWrapper {margin-top: 8px;}
.MainTitleViewer .contentInfo .infoWrapper .WorkSecurityGradeViewer {margin-left:8px; color:#333333; font-family:'Malgun Gothic','맑은 고딕','Gulim','Dotum',sans-serif;}

td.TemplateLayoutExtension {padding-bottom:20px;}
</style>
<%
	Employee emp = EmployeeService.getEmployee(UserService.getUserId());
	String deptCode = emp.getTrnsDprtKey();
	String empNo = emp.getEmpno();
%>
<script type="text/javascript">
JSV.Block(function () {
	var template = '<item color="green" rightArea="true" catalog="/work/catalog.xml.jsp">\
						<fields columns="120px,,120px," type="read" class="Array">\
							<field property="title.read"/>\
						</fields>\
					</item>';
	t = new ItemTemplate(document.getElementById('main${PAGE_ID}'), template);
	
	<%-- 오른쪽 영역 - 활동,대화 --%>
	$(t.layout.right).attr('id', 'DetailActFrm');
	loadActHistory();
});
var t;
var wsocket;
var actorId;
function loadActHistory() {
	if (typeof refreshActHistory !== 'undefined' && $.isFunction(refreshActHistory)) {
		refreshActHistory();
	} else {
		JSV.doLOAD(JSV.getModuleUrl('usr.right.jsp?itemId='+1), 'DetailActFrm', null, null, function() {
			try {
				if (wsocket.readyState === 1) {
					WorkChatListener({'key':'openSuccess'})
				}
			}catch(e){
			}
		});
	}
}
function initSocket() {
	if (window.WebSocket) {
		initWebSocket();
	} else {
		window.WEB_SOCKET_SWF_LOCATION = JSV.getContextPath('/lib/com/kcube/jsv/websocket/WebSocketMain.swf');
		window.WEB_SOCKET_DEBUG = true;
		if (!JSV.socketJsLoading) {
			JSV.socketJsLoading = true;
			JSV.cachedScript('/lib/com/kcube/jsv/websocket/swfobject.js').done(
				function(script, textStatus) {
					JSV.cachedScript('/lib/com/kcube/jsv/websocket/web_socket.js').done(
						function(script, textStatus) {
							var hostName = location.host.indexOf(':') > 0 ? location.host.substring(0,
									location.host.indexOf(':')) : location.host;
							WebSocket.loadFlashPolicyFile('xmlsocket://' + hostName + ':'
									+ JSV.socketParam.flashPort);
							initWebSocket();
							JSV.socketJsLoading = false;
						});
				});
		}
	}
}
function initWebSocket() {
	JSV.socketPreLoaded = true;
	JSV.socketTimeoutObj = setTimeout(function() {
		JSV.socketPreLoaded = false;
	}, JSV.socketTimeout);
	var hostName = location.host.indexOf(':') > 0
			? location.host.substring(0, location.host.indexOf(':'))
			: location.host;
	var url = 'ws://' + hostName + ':' + JSV.socketParam.port + '/wst?jid=' + JSV.socketParam.sessionId
			+ '&serverName=' + JSV.socketParam.serverName;
	if (JSV.socketParam.param)
		url += '&' + JSV.socketParam.param;
	wsocket = new WebSocket(url);

	JSV.socketReconnectTimeoutObj = null;

	wsocket.onopen = function(e) {
		window.clearTimeout(JSV.socketTimeoutObj);
		JSV.socketPreLoaded = false
		JSV.socketLoaded = true;
	};

	wsocket.onclose = function(e) {
		JSV.socketReconnectTimeoutObj = setTimeout(function() {
			initWebSocket();
		}, JSV.socketTimeout);
	}

	wsocket.onmessage = function(e) {
		var data = (typeof e.data == 'string' && e.data.indexOf('{') == 0) ? JSV.toJsonObj(e.data) : e.data;
		if (typeof data == 'object') {
			if (data.key != null) {
				try {
					var func = eval(data.key);
					func(data.msg);
				} catch (e) {

				}
			}
		}
	};
}
</script>
<div id="main${PAGE_ID}"></div>
<form id="form"></form>
<%@ include file="/sys/jsv/template/template.inner.tail.jsp"%>