<%@ include file="/jspf/head.html.jsp"
%><%@ include file="/jspf/default-head-ext.jsp"
%><%@ include file="/space/skin/config.jsp"
%><%@ page import="java.util.Map"%><%@
page import="com.kcube.sys.usr.UserSession"%><%@
page import="com.kcube.space.Space"%><%@
page import="com.kcube.sys.usr.UserService"%><%@
page import="com.kcube.lib.sql.SqlSelect"%><%@
page import="com.kcube.sys.module.ModuleParam"%><%@
page import="com.kcube.space.SpaceService"%><%@
page import="com.kcube.space.SpacePermission"%><%@
page import="com.kcube.space.menu.SpaceMenuSql"%><%@
page import="com.kcube.space.library.LibrarySpaceService"%><%@
page import="com.kcube.sys.tenant.TenantService"%><%@
page import="com.kcube.lib.http.JslService"%><%@
page import="com.kcube.sys.usr.UserPermission"%><%@
page import="com.kcube.space.menu.SpaceMenu"%><%@
page import="com.kcube.space.menu.SpaceMenuSitemap"%><%@
page import="com.kcube.ekp.pn.PnItem"%><%@
page import="com.kcube.sys.tenant.Tenant"%><%@
page import="org.apache.commons.lang.StringEscapeUtils"%><%
	if (UserPermission.isSystem()) {
		response.sendRedirect(request.getContextPath() + "/menu/admin/adm.index.jsp?menu=system_kcube.xml.jsp");
	}
	ModuleParam mp = new ModuleParam(request);
	Long spId = mp.getSpaceId();
	Long defaultSpId = null;
	if (spId == null) {
		defaultSpId = LibrarySpaceService.getDefaultSpaceId();
		if (defaultSpId != null) {
			spId = defaultSpId;
			mp.setSpaceId(spId);
		} else {
			response.sendRedirect(request.getContextPath() + "/menu/admin/adm.index.jsp?menu=center_kcube.xml.jsp");
		}
	}
	if (mp.getSpaceId() != null) {
		com.kcube.space.SpacePermission.checkAllowedSpace(mp);
		
		Map<String, String> info = SpaceService.getSpaceInfo(spId);
		
		String skinKey = info.get("skin_key") != null ? info.get("skin_key") : getDefaultSkin();
		boolean isTeamSpace = info.get("sp_code").equals(Integer.toString(Space.TYPE_TEAMSPACE));
		if (isTeamSpace && !isMainPortal()) {
			skinKey = skinKey.contains(getDefaultGnbSkin()) ? getDefaultSkin() : skinKey;
		}
		
		com.kcube.sys.module.resource.ModuleResourceBundle bundle = com.kcube.sys.module.resource.ModuleResourceBundle.getModuleResourceBundle();
		bundle.setSearchParent(true);
		bundle.setLocale(com.kcube.sys.i18n.I18NService.getResourceLocale());
		String isGuidePage = bundle.getString("default.guidePage.Enabled");
		
		if (UserService.isAttribute("isFirstLogin") && isGuidePage == "true") {
			response.sendRedirect(request.getContextPath() + "/space/menu/usr.sitemap.jsp?spId=" + mp.getSpaceId() + "&skinKey=" + skinKey);
		}
		
		String logoType = info.get("logoType");
		boolean usePic = logoType.equals(Integer.toString(Space.LOGO_IMAGE));

		boolean noLogo = logoType.equals(Integer.toString(Space.LOGO_NONE));
		request.setAttribute("noLogo", noLogo);
		request.setAttribute("spaceName", info.get("name"));
		request.setAttribute("status", info.get("status"));
		request.setAttribute("spaceCode", info.get("sp_code"));
		request.setAttribute("leaderUserId", info.get("leader_userid"));
		request.setAttribute("usePic", noLogo ? false : usePic);
		request.setAttribute("picType", info.get("picType"));
		request.setAttribute("picPath", info.get("picPath"));
		request.setAttribute("workSpace", Space.TYPE_WORKSPACE);
		request.setAttribute("teamSpace", Space.TYPE_TEAMSPACE);
		request.setAttribute("mySpace", Space.TYPE_MYSPACE);
		request.setAttribute("libSpace", Space.TYPE_LIBRARYSPACE);
		request.setAttribute("textLogo", Space.LOGO_TEXT);
		
		boolean gnbSkin = skinKey.contains(getDefaultGnbSkin());
		request.setAttribute("isGnbSkin", gnbSkin);
		request.setAttribute("skin", skinKey);
		request.setAttribute("spaceId", ModuleParam.SPACEID);
		request.setAttribute("currUserId", UserService.getUserId());
		request.setAttribute("currSpId", spId);
		request.setAttribute("bldrIndexUrl", SpaceMenuSql.getBuilderIndexUrl());
		request.setAttribute("isDefault", "true".equals(info.get("isdefault")));
		request.setAttribute("isMdi", isMdi() && "true".equals(info.get("ismdi")));
		request.setAttribute("isBottomLogo", gnbSkin && !noLogo ? "true".equals(info.get("isLogoloc")) : false);
		request.setAttribute("isGnbToTalSearch", isGnbToTalSearch());
		
		String fixedMenuStyle = UserService.getUserPref(SpaceUserMenu.FIXED_MENU_STYLE);
		if (gnbSkin) {
			if (fixedMenuStyle == null) {
				fixedMenuStyle = SpaceUserMenu.ICON_TYPE;
			}
		} else {
			String skinType = skinKey.contains("A") ? "A" : "B";
			request.setAttribute("skinType", skinType);
			fixedMenuStyle = SpaceUserMenu.TEXT_TYPE;
		}
		request.setAttribute("fixedMenuStyle", fixedMenuStyle);
		request.setAttribute("useHoverMenu", !"false".equals(UserService.getUserPref(SpaceUserMenu.USE_HOVER_MENU)));
		request.setAttribute("logoType", logoType);
		request.setAttribute("logoText", info.get("logoText"));
		request.setAttribute("mdiCount", getMdiCount());
		request.setAttribute("isTeamFirst", defaultSpId != null && isTeamSpace);
		request.setAttribute("logo", "logo.png");
	}
	Boolean pswdChange = (Boolean) session.getAttribute("pswdChange");
%>
<fmt:message key="srch.unity.url" var="srchUrl"/>
<fmt:message key="webmail.url" var="webmailUrl"/>
<fmt:message key="kcube.portal.Enabled" var="portalEnabled"/>
<c:set var="portalUrl" value="/jsr_desktop"/>
<!DOCTYPE HTML>
<html style="overflow:hidden;">
<head>
<meta http-equiv="Content-type" content="text/html;charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=Edge"/>
<title><fmt:message key="kcube.head.title"/></title>
<script type="text/javascript" src="<%= request.getContextPath() %>/lib/com/kcube/jsv/JQueryUI.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/lib/com/kcube/jsv/jsv-utils_min.js?nocache=<%=com.kcube.sys.AppServlet.getNoCacheStr() %>"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/lib/com/kcube/jsv/viewers/ComboViewer.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/lib/com/kcube/jsv/columns/TitleColumn.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/sys/jsv/util/JslAction.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/sys/jsv/space/SpaceVisit.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/sys/jsv/space/SpaceWindowPopup.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/sys/jsv/doc/EmpImageViewer.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/sys/jsv/util/KButton.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/sys/jsv/usr/AnchorEmp.js"></script>
<script src="./cst/js/snowfall.jquery.js"></script>
<%@ include file="/jspf/head.jqueryui.jsp" %>
<script type="text/javascript">
	<%
		String loginId = "loginid";
		String loginPw = "loginpw";
		net.sf.json.JSONObject param = new net.sf.json.JSONObject();
		for (java.util.Enumeration<?> names = request.getParameterNames(); names.hasMoreElements(); )
		{
			String name = (String) names.nextElement();
			if (!name.equals(loginId) && !name.equals(loginPw))
				param.put(com.kcube.lib.secure.SecureUtils.XSSFilter(name), com.kcube.lib.secure.SecureUtils.XSSFilter(request.getParameter(name)));
		}
		if (defaultSpId != null)
		{
			param.put(ModuleParam.SPACEID, defaultSpId);
		}
		
		net.sf.json.JSONObject wSocketObj = null;
		if (com.kcube.sys.websocket.SocketServerService.isStart()) {
			wSocketObj = new net.sf.json.JSONObject();
			wSocketObj.put("sessionId", session.getId());
			wSocketObj.put("serverName", com.kcube.sys.login.LoginSessionManager.getServerName());
		}
		
		net.sf.json.JSONObject webdavObj = null;
		String webdavUrl = com.kcube.sys.webdav.WebDavSpring.getUrl();
		if (webdavUrl != null) {
			if (!webdavUrl.endsWith("/")) {
				webdavUrl = webdavUrl + "/";
			}

			webdavObj = new net.sf.json.JSONObject();
			webdavObj.put("url", webdavUrl);
			webdavObj.put("ext", com.kcube.sys.webdav.WebDavSpring.getExt());			
		}
	%>
	JSV.init('<%= request.getContextPath() %>', '/lib', <%=param%>, '${locale.language}', '${defaultLocale.language}', <%=wSocketObj%>, <%=webdavObj%>);
</script>
<script type="text/javascript" src="<%= request.getContextPath() %>/script.svg.ver.js?nocache=<%=com.kcube.sys.AppServlet.getNoCacheStr() %>"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/script.language.ver.js?nocache=<%=com.kcube.sys.AppServlet.getNoCacheStr() %>"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/script.svg.ver.ext.js?nocache=<%=com.kcube.sys.AppServlet.getNoCacheStr() %>"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/script.language.ver.ext.js?nocache=<%=com.kcube.sys.AppServlet.getNoCacheStr() %>"></script>
<%@ include file="/jspf/global-ext.jsp" %>
<script type="text/javascript" src="<%= request.getContextPath() %>/ekp/update/update.jsp"></script>
<link rel="stylesheet" href="<%= request.getContextPath() %>/style.css?nocache=<%=com.kcube.sys.AppServlet.getNoCacheStr() %>" type="text/css" media="screen,print">
<link rel="stylesheet" href="<%= request.getContextPath() %>/sys/jsv/util/KButton.css?nocache=<%=com.kcube.sys.AppServlet.getNoCacheStr() %>" type="text/css" media="screen,print">
<c:if test="${!isGnbSkin}">
	<link rel="stylesheet" href="<%= request.getContextPath() %>/space/menu/skin/commonSkin.css?nocache=<%=com.kcube.sys.AppServlet.getNoCacheStr() %>" type="text/css" media="screen">
</c:if>
<link id="skinLink" rel="stylesheet" href="<%= request.getContextPath() %>/space/menu/skin/${skin}/${skin}.css?nocache=<%=com.kcube.sys.AppServlet.getNoCacheStr() %>" type="text/css" media="screen">
<link rel="stylesheet" href="<%= request.getContextPath() %>/space/menu/skin/defaultSkin.css?nocache=<%=com.kcube.sys.AppServlet.getNoCacheStr() %>" type="text/css" media="screen,print">
<link rel="stylesheet" href="<%= request.getContextPath() %>/ekp/pn/skin/fixedLayerSkin.css?nocache=<%=com.kcube.sys.AppServlet.getNoCacheStr() %>" type="text/css" media="screen,print">
<link type="image/x-icon" rel="icon" href="<%= request.getContextPath() %>/kcube.ico">
<link type="image/x-icon" rel="shortcut icon" href="<%= request.getContextPath() %>/kcube.ico">
<style type="text/css">
#BottomLayer {
	overflow: hidden;
	background-color: hsla(0,0%,0%,1); 
	background: linear-gradient( to bottom, pink 30% , skyblue 80%, blue);
	margin: 0px;
	overflow: hidden;
}
#TopLayer {
	display: none;
}
</style>
<script type="text/javascript">
var login_error_key = 'com.kcube.login.error';
var license_error_date = 'com.kcube.license.exprDateErr';
var license_error_user = 'com.kcube.license.maxUserErr';
var license_warn_date = 'com.kcube.license.exprDateWarn';
var license_warn_user = 'com.kcube.license.maxUserWarn';
JSV.Block(function () {
	var topLayer = document.getElementById('TopLayer');
	var bottomLayer = document.getElementById('BottomLayer');
	
	var menuXml = JSV.getParameter('path') || '/space/menu/user.menu.xml.jsp';
	menuXml = menuXml + '?' + JSV.MODNAMES.SPACEID + '=${currSpId}&gnbSkin=${isGnbSkin}';
	mViewer = new MdiViewer(document.getElementById('MdiLayer'), {'maxCount':${mdiCount}, 'isUse': '${isMdi}'});
	topLayer.style.display = 'block';
	viewer = new MenuViewer();
	viewer.setInput(menuXml);
	
	//공지사항 팝업
	PortalNoticeOpen();
	
	<c:if test="${isDefault || isTeamFirst}">
	viewer.addMenu({'id':'AnchorEmp_showDetail', 'text':'<fmt:message key="app.ptl.211"/>', 'mdiText':'<fmt:message key="app.ptl.211"/>', 'width':0, 'type':MenuViewer.SettingMenu, 'href':'Profile', 'svg':'menu_profile', 'target':'bottom'});
	viewer.addMenu({'id':-3500, 'text':'<fmt:message key="update.018"/>', 'width':0, 'type':MenuViewer.SettingMenu, 'href':'/space/menu/usr.index.jsp?menu=setting&spId=' + JSV.getSpaceId() + '&isGnbSkin=' + isGnbSkin, 'svg':'menu_manage', 'target':'bottom'});
	</c:if>
	<% if (SpacePermission.isAdminOrLeader(spId)) { %>
	<c:choose>
		<c:when test="${isDefault}">
			viewer.addMenu({'id':mViewer.getId(), 'text':'<fmt:message key="space.028"/>', 'width':0, 'type':MenuViewer.SettingMenu, 'href':'/space/manage/adm.index.jsp?mode=homeLibraryAdmin', 'svg':'menu_portal_manage', 'target':'bottom', 'spId':JSV.getSpaceId()});
		</c:when>
		<c:when test="${spaceCode == libSpace}">
			viewer.addMenu({'id':mViewer.getId(), 'text':'<fmt:message key="space.028"/>', 'width':0, 'type':MenuViewer.SettingMenu, 'href':'/space/manage/adm.index.jsp?mode=libraryAdmin', 'svg':'menu_portal_manage', 'target':'bottom', 'spId':JSV.getSpaceId()});
		</c:when>
		<c:when test="${spaceCode == workSpace}">
			viewer.addMenu({'id':mViewer.getId(), 'text':'<fmt:message key="space.028"/>', 'width':0, 'type':MenuViewer.SettingMenu, 'href':'/space/manage/adm.index.jsp?mode=workAdmin', 'svg':'menu_portal_manage', 'target':'bottom', 'spId':JSV.getSpaceId()});
		</c:when>
		<c:when test="${spaceCode == teamSpace}">
 			viewer.addMenu({'id':mViewer.getId(), 'text':'<fmt:message key="space.028"/>', 'width':0, 'type':MenuViewer.SettingMenu, 'href':'/space/manage/adm.index.jsp?mode=teamAdmin', 'svg':'menu_portal_manage', 'target':'bottom', 'spId':JSV.getSpaceId()});
		</c:when>
	</c:choose>
	<% } else { %>
	<c:choose>
		<c:when test="${spaceCode == workSpace}">
			viewer.addMenu({'id':-4000, 'text':'<fmt:message key="space.029"/>', 'width':0, 'type':MenuViewer.SettingMenu, 'href':'/space/work/usr.info.jsp', 'target':'bottom', 'spId':JSV.getSpaceId()});
		</c:when>
	</c:choose>
	<% } %>
	<c:if test="${spaceCode == mySpace && leaderUserId == currUserId}">
		viewer.addMenu({'id':mViewer.getId(), 'text':'<fmt:message key="space.028"/>', 'width':0, 'type':MenuViewer.SettingMenu, 'href':JSV.getModuleUrl('/space/manage/adm.index.jsp?mode=myAdmin'), 'svg':'menu_portal_manage', 'target':'bottom'});
	</c:if>
	<c:if test="${spaceCode == libSpace || isTeamFirst}">
		<% if (com.kcube.sys.usr.UserPermission.isAdmin() && com.kcube.sys.usr.UserPermission.isLocationAllowed()) { %>
			viewer.addMenu({'id':mViewer.getId(), 'text':'<fmt:message key="menu.057"/>', 'width':0, 'type':MenuViewer.SettingMenu, 'href':'/menu/admin/adm.index.jsp?menu=center_kcube.xml.jsp', 'svg':'menu_manage', 'target':'popup', 'spId':JSV.getSpaceId(), 'popupStyle':'scrollbars=yes,resizable=yes,location=yes,menubar=yes,status=yes,toolbar=yes'});
		<% } %>
	</c:if>
	var mainMenu = viewer.getMenuByIndex(0);
	var targetUrl = JSV.getParameter('targetUrl');
	if(targetUrl && JSV.getParameter('isEncode')) {
		targetUrl = decodeURIComponent(targetUrl);
	}
	if (targetUrl) {
		clonePopup = JSV.getParameter('clonePopup') && JSV.getParameter('clonePopup') == 'true' ? true : false;
		clonePopup = clonePopup ? clonePopup : JSV.getParameter('openPopupPortal') && JSV.getParameter('openPopupPortal') == 'true' ? true : false;
		if (mainMenu) {
			if (mainMenu.doSysMenuClick && JSV.getParameter('sysId')) {
				mainMenu.doSysMenuClick(JSV.getParameter('sysId'));
			} else if (mainMenu.doAppMenuClick && JSV.getParameter('appId')) {
				mainMenu.doAppMenuClick(JSV.getParameter('appId'));
			} else {
				mainMenu.doClick(false, true);
			}
		}
		var menuName = JSV.getLocaleStr(JSV.decodeXSS(JSV.getParameter('targetMenuName')));
		if (mViewer) {
			var menuId = JSV.getParameter('menuId') || null;
			if(JSV.getParameter('isSysId')){
				mViewer.doMDIBOTTOM(JSV.getModuleUrl(targetUrl.replace(/&amp;/g, '&')), JSV.getParameter('sysId'), menuName, true);
			}else{
				mViewer.doMDIBOTTOM(JSV.getModuleUrl(targetUrl.replace(/&amp;/g, '&')), menuId, menuName);
			}
		}
		clonePopup = false;
	} else if (mainMenu) {
		mainMenu.doClick(true);
	}
	
	var visit = new SpaceVisit(${currUserId});
	visit.visit();
	
	if (JSV.browser.msie7) {
		setTimeout(function() {
			MenuResize();
		}, 100);
	} else {
		MenuResize();
	}
	$(window).resize(function() {
		MenuResize();
	});
	
	var loginCheckMsg = JSV.getCookie(login_error_key);
	if (loginCheckMsg) {
		if (loginCheckMsg == license_warn_date) {
			JSV.alert('<fmt:message key="login.065"/>');
		} else if (loginCheckMsg == license_error_date) {
			JSV.alert('<fmt:message key="login.066"/>');
		} else if (loginCheckMsg == license_error_user || loginCheckMsg == license_warn_user){
			JSV.alert('<fmt:message key="login.067"/>');
		}
		JSV.removeCookie(login_error_key);
	}
	
	if (viewer.settingmenus.length == 0) {
		viewer.$profileLayer.find('.user_photo').css('cursor', 'text');
	}
	
	// 비밀번호 변경일이 넘으면 팝업호출
	<% if (pswdChange) { %>
		var url = JSV.getContextPath('/ekp/emp/usr.changepw.info.jsp');
		var fObj = {'width': 600, 'height':450, 'title':'<fmt:message key="login.035"/>', 'resizable': false};
		JSV.showLayerModalDialog(url, null, fObj); 
	<% } %>
	var shortInfo = window.sessionStorage.getItem('shortInfo');
	if (shortInfo) {
		var oShort = JSV.toJsonObj(shortInfo);
		var sMenuInfo = getMenuInfo(oShort);
		window.sessionStorage.removeItem('shortInfo');
		setTimeout(function() {
			UseRightFrameMoveUrl(oShort.url, sMenuInfo.appName, sMenuInfo.sysId);	
		}, 500);
	}
	$('#BottomLayer').ready(function(){
		$('body').snowfall({deviceorientation : true, round : true, minSize: 1, maxSize:8,  flakeCount : 250});
	});
});
var usePic = ${usePic};
var isGnbSkin = ${isGnbSkin};
var scroll = false;
var viewer;
var mViewer;
var clonePopup = false;
var isMdiExcute = true;
var allSearch = false;
var fixedMenuStyleType = '${fixedMenuStyle}';
var useHoverMenu = ${useHoverMenu};
var isIconType = fixedMenuStyleType == '<%=SpaceUserMenu.ICON_TYPE%>';
/******************************
*	PortalNotice 공지사항 팝업
*******************************/
function PortalNoticeOpen() {
	var ntc = JSV.loadJSON(JSV.getModuleUrl('/jsl/PnItemUser.PortalNoticeListByUser.json')).array;
	for (var i = 0 ; i < ntc.length; i++) {
		if (JSV.getCookie('ntc' + ntc[i].id + '_${currUserId}') != 'done') {
			if (ntc[i].type == <%=PnItem.POPUP_TYPE%>) {
				var url = ntc[i].skinType ? 'skinHtmlLoad.jsp' : ntc[i].skinKey + '.jsp';
				var ntcUrl = JSV.getContextPath('/ekp/pn/skin/' + url + '?id=' + ntc[i].id);
				var width = 350;
				var height = 500;
				var top = 10;
				var left = 10;
				if (ntc[i].sizeWidth != null && ntc[i].sizeHight != "") {
					height = ntc[i].sizeHight;
					width = ntc[i].sizeWidth;
				}
				if (ntc[i].pstnTop != null && ntc[i].pstnLeft != "") {
					top = ntc[i].pstnTop;
					left = ntc[i].pstnLeft;
				}
				var f = 'top='+top+',left='+left+',width='+width+',height='+height+',scrollbars=yes,status=no,toolbar=no,menubar=no,location=no,resizable=no';
				window.open(ntcUrl, 'ntc' + ntc[i].id, f);
			} else {
				var ntcId = ntc[i].id; 
				var ntcJson = JSV.loadJSON(JSV.getModuleUrl('/jsl/PnItemUser.ReadByUser.json?id=' + ntcId));
				var typeDiv = $('<div>').addClass('type').css('background', ntcJson.bgColor).appendTo(viewer.$noticeFixedDiv);
				var noticeCodes = ['<fmt:message key="pn.047"/>', '<fmt:message key="pn.048"/>', '<fmt:message key="pn.049"/>'];
				var exitType = ntcJson.exitType;
				var oImg = ntcJson.picture;
				var url = ntcJson.url;
				var isUrl = url != null && url != '';
				var linkTag;
				var noticeCode = 0;
				if (oImg == null || oImg.type < 0) { //text
					noticeCode = ntcJson.noticeCode;
					typeDiv.addClass('text_type');
					var viewText = isUrl ? '<a class="link">' + ntcJson.text + '</a>' : ntcJson.text;
					var noticeCodeText = ntcJson.tenantId == <%=Tenant.SYSTEM_TENANTID%> ? '<fmt:message key="platform.title.prefix"/>' + noticeCodes[noticeCode] : noticeCodes[noticeCode];
					$('<p>').addClass('notice').html('<b class="sorting">' + noticeCodeText + '</b>' + viewText).appendTo(typeDiv);
					
					if (isUrl) {
						linkTag = typeDiv.find('.link');
					}
				} else { //img
					typeDiv.addClass('img_type');
					if (oImg) {
						var src = JSV.getContextPath('/jsl/inline/ImageAction.Download/?');
						if (oImg.key) {
							src += 'key=' + oImg.key;
						} else {
							src += 'type=' + oImg.type + '&path=' + oImg.path;
						}
						
						linkTag = $('<img>').attr({
							'src' : src
						}).one('error', this, function(event) {
							if (event.data.onerror) {
								event.data.onerror(this);
							}
							$(this).parent('div.banner').height('60px');
							$(window).trigger('resize');
						}).on('load', this, function(e){
							if ($(this).height() >= 150) {
								$(this).closest('div.img_type').addClass('wideImage');
							}
							$(window).trigger('resize');
						}).appendTo($('<div>').addClass('banner').appendTo(typeDiv));
					}
				}
				
				if (isUrl) {
					var target = ntcJson.popupStyle;
					linkTag.on('click', {'url':url, 'ntcId':ntcId, 'target':target}, function(e) {
						if (e.data.target == 'bottom') {
							mViewer.doMDIBOTTOM(e.data.url, 'ntc' + e.data.ntcId, '<fmt:message key="pn.003"/>');
						} else if (e.data.target == 'popup') {
							var f = 'width=1024px,height=768px,scrollbars=yes,status=no,toolbar=no,menubar=no,location=no,resizable=yes';
							window.open(e.data.url, 'ntc' + e.data.ntcId, f);
						} else { //새창
							window.open(e.data.url, 'ntc' + e.data.ntcId, 'scrollbars=yes,resizable=yes,location=yes,menubar=yes,status=yes,toolbar=yes');
						}
					});
				}
				
				var exitText = noticeCode == '2' ? '' : exitType ? '<fmt:message key="pn.055"/>' : '<fmt:message key="pn.056"/>';
				$('<a>').addClass('btn').html('<span class="icon"></span><span class="text">' + exitText + '</span>')
					.on('click', {'ntcId':ntcId, 'noticeCode':noticeCode, 'exitType':exitType}, function(e) {
						if (e.data.noticeCode != '2') {
							var cookieName = 'ntc' + e.data.ntcId;
							var todayDate = new Date();
							todayDate.setDate(todayDate.getDate() + (e.data.exitType ? 365 : 1)); 
							document.cookie = 'ntc' + e.data.ntcId + '_${currUserId}' + '=' + escape('done') + '; path=/; expires=' + todayDate.toGMTString() + ';';
						}
						$(this).parent().remove();
						setSize();
					}).appendTo(typeDiv);
				
				$(window).trigger('resize');
			}
		}
	}
}
function changeSkinType() {
	location.reload();
}
function changeSkin(skin) {
	if (!isGnbSkin) {
		var skinType = skin.indexOf('A') > 0 ? 'A' : 'B';
		MenuViewer.ImageDIR = JSV.getContextPath('/space/menu/skin/' + skin + '/');
		$('#TopLayer').removeClass().addClass(skinType).addClass(skin);
		$('#skinLink').attr('href', JSV.getContextPath('/space/menu/skin/' + skin + '/' + skin + '.css'));
		
		if (!usePic) {
			$('#logoImg').attr('src', JSV.getContextPath('/space/menu/skin/' + skin + '/${logo}')).on('error', function() {
				this.src = JSV.getContextPath('/space/menu/skin/' + skin + '/logo.png');
			});
		}
	}
}
function MenuResize() {
	viewer.resize();
	mViewer.resize();
}
function goToAllPage() {
	mViewer.doMDIBOTTOM('/space/menu/sitemap.jsp?' + JSV.MODNAMES.SPACEID + '=' + JSV.getSpaceId(), -9999, '<fmt:message key="space.655"/>');
}
function MenuViewer() {
	this.selectedmain = null;
	this.$topLayer = $('#TopLayer');
	this.$bottomLayer = $('#BottomLayer');
	this.$mainMenuLayer = $('#MainMenuLayer');
	this.mainMenuLayer = this.$mainMenuLayer.get(0);
	this.settingMenuLayer = $('#SettingMenuLayer').get(0);
	this.$profileLayer = $('#ProfileLayer');
	this.searchLayer = $('#SearchLayer').get(0);
	this.updateLayer = $('#UpdateLayer').get(0);
	this.bookmarkLayer = $('#BookmarkLayer').get(0);
	this.$noticeFixedDiv = $('#NoticeFixedDiv');
	this.width = 0;
	
	this.search = new this.SearchLayer(this);
	this.profile = new this.Profile(this);
	this.update = new this.UpdateLayer(this);
	this.bookmark = new this.BookmarkLayer(this);
	this.setting = new this.SettingMenu(this);
	
	//button
	$('#SearchBtnLayer .btn_search').on('click', this, function(e){
		AllLayerHide();
		$(e.data.searchLayer).addClass('on');
		e.data.search.$bWidget.find('.ipt').focus();
	});
	
	//logout icon
	$('#LogoutLayer .btn_logout').on('click', this, function(e){
		JSV.doGET('/etc/logout.jsp?url=' + JSV.getContextPath('/default.jsp'));
	});
	
	this.init();
}
MenuViewer.MIN_WIDTH = 1000;
MenuViewer.MainMenu = 1000;
MenuViewer.SubMenu = 2000;
MenuViewer.SystemMenu = 3000;
MenuViewer.SystemSubMenu = 4000;
MenuViewer.SettingMenu = 6000;
MenuViewer.SearchMenu = 7000;
MenuViewer.ImageDIR = '<%= request.getContextPath() %>/space/menu/skin/${skin}/';
MenuViewer.prototype.mainmenus = [];
MenuViewer.prototype.submenus = [];
MenuViewer.prototype.systemmenus = [];
MenuViewer.prototype.settingmenus = [];
MenuViewer.prototype.updateClickOn = false;
MenuViewer.prototype.bookmarkClickOn = false;
MenuViewer.prototype.setInput = function(newInput) {
	this.roots = [];
	this.children = [];
	var model = JSV.parseXml(newInput);
	for (var i = 0, len = model.length; i < len; i++) {
		var obj = model[i];
		if (!obj.pid) {
			this.roots[this.roots.length] = obj;
		} else {
			if (!this.children[obj.pid]) this.children[obj.pid] = [];
			this.children[obj.pid][this.children[obj.pid].length] = obj;
		}
	}
	this.setInputAfterInit();
}
MenuViewer.prototype.setInputAfterInit = function() {
	var mainMenuLeng = 0;
	var length = this.roots.length;
	for (var i = 0; i < length; i++) {
		var obj = this.roots[i];
		if (obj.type == MenuViewer.MainMenu) {
			mainMenuLeng++;
		}
	}
	var mainIdx = 0;
	for (var i = 0; i < length; i++) {
		var obj = this.roots[i];
		if (obj.type == MenuViewer.MainMenu) {
			mainIdx++;
		}
		this.addMenu(obj, (obj.type == MenuViewer.MainMenu && mainMenuLeng == mainIdx));
	}
}
MenuViewer.prototype.addMenu = function(obj, isLast) {
	if (obj.type == MenuViewer.MainMenu) {
		var oMainMenu = new this.MainMenu(this, obj, isLast);
		var oSubMenu = new this.SubMenu(this, obj);
		this.mainmenus.push(oMainMenu);
		this.submenus.push(oSubMenu);
	} else if (obj.type == MenuViewer.SettingMenu) {
		var oSettingMenu = new this.setting.addMenu(this.setting, obj);
		this.settingmenus.push(oSettingMenu);
	} else if (obj.type == MenuViewer.SystemMenu || obj.type == MenuViewer.SystemSubMenu) {
		var oSystemMenu = new this.SystemMenu(this, obj);
		this.systemmenus.push(oSystemMenu);
	}
}
MenuViewer.prototype.getMenu = function(id) {
	for (var i = 0; i < this.mainmenus.length; i++) {
		if (id == this.mainmenus[i].obj.id) {
			return this.mainmenus[i];
		}
	}
	return null;
}
MenuViewer.prototype.getMenuName = function(cid, isApp) {
 	for (var i = 0; i < this.mainmenus.length; i++) {
 		if (cid == (isApp ? this.mainmenus[i].obj.appId : this.mainmenus[i].obj.sysId)) {
 			return this.mainmenus[i].obj.text;
 		}
 		if (this.submenus[i] && this.submenus[i].children) {
 			var children = this.submenus[i].children;
 			for (var j = 0; j < children.length; j++) {
 				if (cid == (isApp ? children[j].appId : children[j].sysId)) {
 					return children[j].text;
 				}
 			}
 		}
 	}
 	
 	//systemmenus
	for (var i = 0; i < this.systemmenus.length; i++) {
		if (cid == (isApp ? this.systemmenus[i].obj.appId : this.systemmenus[i].obj.sysId)) {
			return this.systemmenus[i].obj.title;
		}
	}
	return 'Noname';
}
MenuViewer.prototype.getMenuByIndex = function(index) {
	return this.mainmenus[index];
}
MenuViewer.prototype.SettingMenu = function(viewer) {
	var _this = this;
	this.viewer = viewer;

	this.$parent = $(this.viewer.settingMenuLayer).addClass('layer_gnb setting').attr('title', '<fmt:message key="space.012"/>')
		.on('mouseenter', this, function(e) {
			if (e.data.viewer.settingClose != null) {
				clearTimeout(e.data.viewer.settingClose);
				e.data.viewer.settingClose = null;
			}
		}).on('mouseleave', function() {
			_this.viewer.settingClose = setTimeout(function() {
				_this.$parent.hide();
			}, 2000);
		});
	this.$widget = $('<ul>').addClass('menu_list').appendTo(this.$parent);
}
MenuViewer.prototype.SettingMenu.prototype.addMenu = function(parent, obj) {
	this.parent = parent;
	this.obj = (obj) ? obj : {};
	this.li = $('<li>').on('click', this, this.onclick).appendTo(this.parent.$widget);	
	if (this.obj.className) {
		this.li.addClass(this.obj.className);
	}
	this.link = $('<a>').addClass('link').attr('title', JSV.getLocaleStr(obj.text)).appendTo(this.li);
	if (obj.svg) {
		JSV.getSVG(obj.svg).addClass('icon').appendTo(this.link);
	}
	
	$('<span>').addClass('text').text(JSV.getLocaleStr(obj.text)).appendTo(this.link);
}
MenuViewer.prototype.SettingMenu.prototype.addMenu.prototype.onclick = function(event) {
	event.data.parent.$parent.hide();
	mViewer.doMenuClick(event.data.obj, 'SettingMenu_');
	return false;
}
MenuViewer.prototype.Profile = function(viewer) {
	this.viewer = viewer;
	var userName = '<%=StringEscapeUtils.escapeJavaScript(UserService.getUser().getName())%>';
	this.viewer.$profileLayer.attr({
		'mTitle':userName,
		'title':JSV.getLocaleStr('<%=StringEscapeUtils.escapeJavaScript(UserService.getUser().getDisplayName())%>')
	}).on('click', this, function(e) {
		e.stopPropagation();
		var layer = $(e.data.viewer.settingMenuLayer);
		if (layer.find('li').length > 1) {
			if (layer.css('display') == 'none') {
				AllLayerHide(layer);
				
				var p = $(this).position();
				var left = p.left - layer.width() + 105;
				if (layer.hasClass('subPortal')) {
					left -= layer.width() + 105;
				}
				layer.css({'left': left, 'top':p.top + 35}).show();
				e.data.viewer.settingClose = setTimeout(function() {
					layer.hide();
				}, 5000);
			} else {
				clearTimeout(e.data.viewer.settingClose);
				e.data.viewer.settingClose = null;
				layer.hide();
			}
			$(document).one('click', this, function(e){
				layer.hide();
			});
		} else {
			layer.find('li').eq(0).click();
		}
	});
	
	var userPhoto = $('<a>').addClass('user_photo').appendTo(this.viewer.$profileLayer);
	$('<span>').addClass('text').text(JSV.getLocaleStr(userName).substring(0,1)).appendTo(userPhoto);
	$('<IMG>', {'src':JSV.getContextPath('/jsl/inline/ImageAction.Download?cacheOpt=DAY&path=${currUserId}&type=' + JSV.EMPTHUMB_TYPE)}).one('error', function() {
		$(this).hide();
		$(this).siblings().show();
	}).one('load', function() {
		$(this).siblings().remove();
	}).appendTo(userPhoto);
}
MenuViewer.prototype.UpdateLayer = function(viewer) {
	this.viewer = viewer;
	var listWrap = $('<span>').css({'position':'relative', 'z-index':100, 'margin-top' : '16px'}).insertAfter(this.viewer.updateLayer);
	this.$widget = $(this.viewer.updateLayer).find('.btn_update');
	listWrap.css({'display': 'inline-block'});
	var _this = this;
	var viewTimeout = null;
	this.listView = $('<div>').addClass('listView').css({'position':'absolute', 'text-indent':'0', 'display':'none', 'top':'25px', 'left': '-210px'}).appendTo(listWrap)
	.on('mouseleave', function() {
		if (viewTimeout != null) {
			clearTimeout(viewTimeout);
		}
		updateOn = false;
		viewTimeout = setTimeout(function() {
			if (!updateOn && _this.viewer.updateClickOn && !updateDialogOn) {
				$(_this.viewer.updateLayer).click();
			}
		}, 2000);
	}).on('mouseover', function() {
		updateOn = true;
	});
	$(this.viewer.updateLayer).on('click', this, function(e) {
		if (e.data.viewer.updateClickOn) {
			e.data.viewer.updateClickOn = false;
			e.data.listView.hide();
			$('#AnchorEmp_moveDiv').hide();
		} else {
			if (e.data.$widget.hasClass('new')) {
				e.data.$widget.removeClass('new');
			}
			updateOn = true;
			e.data.listView.load(JSV.getContextPath('/ekp/update/layer.own.list.jsp'), {'PORTLET_ID':'NOTIVIEW'}, function() {
				AllLayerHide($(this));
				var _this = e.data;
				_this.viewer.updateClickOn = true;
				_this.listView.show();
				
				$(document).one('click', this, function(e){
					_this.viewer.updateClickOn = false;
					_this.listView.hide();
				});
			});
		}
	});
	this.refresh();
	var $this = this;
	var min = 1000 * 100; 
	function alimiPolling () {
		setTimeout(function() {
			if (!JSV.socketLoaded) {
				$this.refresh();
			}
			alimiPolling();
		}, min);
	}
	alimiPolling();
}
MenuViewer.prototype.UpdateLayer.prototype.refresh = function() {
	if (this.$widget.hasClass('new')) {
		this.$widget.removeClass('new');
	}
	$.ajax({
		url : JSV.getContextPath('/jsl/AlimiAction.PortletLogCountByUser.json'),
		async : true,
		context : this,
		dataType : 'json',
		success : function(data) {
			if (data && data.count > 0) {
				this.$widget.addClass('new');
			}
		}
	});
}
MenuViewer.prototype.BookmarkLayer = function(viewer) {
	this.viewer = viewer;
	var listWrap = $('<span>').css({'position':'relative', 'z-index':100, 'margin-top' : '16px'}).insertAfter(this.viewer.bookmarkLayer);
	this.$widget = $(this.viewer.bookmarkLayer).find('.btn_bookmark');
	listWrap.css({'display': 'inline-block'});
	var _this = this;
	var viewTimeout = null;
	this.listView = $('<div>').addClass('listView').css({'position':'absolute', 'text-indent':'0', 'display':'none', 'top':'25px', 'left': '-210px'}).appendTo(listWrap)
	.on('mouseleave', function() {
		if (viewTimeout != null) {
			clearTimeout(viewTimeout);
		}
		bookmarkOn = false;
		viewTimeout = setTimeout(function() {
			if (!bookmarkOn && _this.viewer.bookmarkClickOn && !bookmarkDialogOn) {
				$(_this.viewer.bookmarkLayer).click();
			}
		}, 2000);
	}).on('mouseover', function() {
		bookmarkOn = true;
	});
	$(this.viewer.bookmarkLayer).on('click', this, function(e) {
		bookmarkDialogOn = false;
		if (e.data.viewer.bookmarkClickOn) {
			e.data.viewer.bookmarkClickOn = false;
			e.data.listView.hide();
		} else {
			bookmarkOn = true;
			e.data.listView.load(JSV.getContextPath('/ekp/fvrt/layer.fvrt.list.jsp'), {'PORTLET_ID':'BOOKVIEW'}, function() {
				AllLayerHide($(this));
				var _this = e.data;
				_this.viewer.bookmarkClickOn = true;
				_this.listView.show();
				
				$(document).one('click', this, function(e){
					_this.viewer.bookmarkClickOn = false;
					bookmarkDialogOn = false;
					_this.listView.hide();
				});
			});
		}
	});
}
MenuViewer.prototype.SearchLayer = function(viewer) {
	this.viewer = viewer;
	this.$bWidget = $('<div>').addClass('inner').html('<div class="close_layer">\
			<a href="javascript:void(0);" class="btn"></a></div>\
			<div class="search_area">\
				<div class="search_bar">\
					<div class="bar_right"></div>\
					<div class="ipt_bar"><input type="text" class="ipt" id="QR" name="QR" placeholder="Search"></div>\
				</div></div>').appendTo(this.viewer.searchLayer);
	JSV.getSVG('search_icon').addClass('search_icon').insertBefore(this.$bWidget.find('.close_layer'));
	JSV.getSVG('close_icon').addClass('icon').on('click', this, function(e) {
		$(e.data.viewer.searchLayer).removeClass('on');
	}).appendTo(this.$bWidget.find('.close_layer .btn'));

	var barRight = this.$bWidget.find('.bar_right');
	JSV.getSVG('clear_icon').addClass('clear').appendTo(barRight).hide();
	var searchBtn = $('<a>').addClass('btn_search').appendTo(barRight);
	
	JSV.getSVG('btn_search').addClass('icon').appendTo(searchBtn);
	
	JSV.getSVG('btn_arrow').addClass('icon').on('click', this, function(event) {
 		doSearch();
 	}).appendTo(searchBtn);
		
	//allSearch
	if (allSearch == true) {
		barRight.find('.btn_search').remove()
		this.$bWidget.find('.search_bar').addClass('is_option');
		var searchOption = $('<div>').addClass('search_option').html('<div class="opt all">\
				<a href="javascript:void(0);" class="type">All</a>\
				<span class="type_tooltip"><fmt:message key="menu.036"/></span></div>\
				<div class="opt user"></div>\
				<div class="opt attachment"></div>').appendTo(barRight);
		searchOption.find('.all').on('click', function(event) {
	 		doSearch();
	 	});
		var userSeachDiv = searchOption.find('.user');
		JSV.getSVG('user_search').addClass('type').appendTo(userSeachDiv).on('click', this, function(event) {
			doSearchUser();
	 	});
		$('<span>').addClass('type_tooltip').text('<fmt:message key="space.641"/>').appendTo(userSeachDiv);
// 		var fileSeachDiv = searchOption.find('.attachment');
// 		JSV.getSVG('file_search').addClass('type').appendTo(fileSeachDiv);
// 		$('<span>').addClass('type_tooltip').text('<fmt:message key="space.642"/>').appendTo(fileSeachDiv);
	}
		
	this.$bWidget.find('.ipt').keyup(function() {
		$(this).parents('.search_bar').addClass('on');
    	$(this).parents('.search_bar').find('.clear').toggle(Boolean($(this).val()));
    	if (!this.value) {
    		$(this).parents('.search_bar').removeClass('on');
    	}
    }).on('keydown', this, function(event) {
		if (event.keyCode == 13) {
			doSearch();
		}
	});
	    
	this.$bWidget.find('.clear').click(function() {
    	$(this).hide();
    	$(this).parents('.search_bar').removeClass('on');
    	$(this).parents('.search_bar').find('.ipt').val('').focus();
    });
}
function alimiPushListener(data) {
	if (data && data.count > 0) {
		viewer.update.$widget.addClass('new');
	}
}
function MdiViewer(parent, style) {
	this.parent = parent;
	this.firstClick = true;
	this.style = style;
	this.bottomLayer = $('#BottomLayer').get(0);
	this.$logoLayer = $('#LogoLayer');
	
	if ($(this.parent).length == 0) {
		this.parent = $('<div>').addClass('${skin}Mdi').attr('id', 'MdiLayer').insertBefore(this.bottomLayer);
	}
	this.style = style;
	this.$ul = $('<UL>').addClass('mdi_list').appendTo(this.parent);
	$('<DIV>').addClass('bottom').appendTo(this.parent);
	this.maxCount = this.style.maxCount || 15;
	this.isFlag = false;
	this.length = 0;
	this.width = 0;
	this.txtWidth = 60;
	this.sort = 0;
	this.fixedmenus = [];
	this.isUse = (this.style.isUse && this.style.isUse == 'true') ? true : false;
	if (this.isUse) {
		$(this.parent).show();
	} else {
		$('<iframe id="bottom" name="bottom" width="100%" src="" scrolling="<%= com.kcube.lib.http.JslService.isMSIE6(request) ? "yes" : "auto"%>" frameborder="0" noresize></iframe>').appendTo(this.bottomLayer);
	}

	if (!isGnbSkin) {
		this.$bottomMoveLeft = $('#BottomMoveLeft');
	 	this.$bottomMoveRight = $('#BottomMoveRight');
	 	this.$rollingArea = $('#RollingArea');
	 	
		this.$bottomMoveLeft.on('mouseover', this, function(e) {
			e.data.$bottomMoveRight.removeClass('disabled');

			e.data.interval = window.setInterval(function() {
				var left = parseInt(e.data.$rollingArea.css('left'));
				if (left >= 0) {
					window.clearInterval(e.data.interval);
	 				e.data.$rollingArea.css('left', 0);
					e.data.$bottomMoveLeft.addClass('disabled');
					return false;
				}
	 			e.data.$rollingArea.css('left', left + 6);
			}, 20);
		}).on('mouseout', this, function(e) {
			if (e.data.interval) {
				window.clearInterval(e.data.interval);
			}
		});
		this.$bottomMoveRight.on('mouseover', this, function(e) {
			e.data.$bottomMoveLeft.removeClass('disabled');
			var maxMove = e.data.$menuBottomDiv.width() - e.data.$rollingArea.width() - e.data.$menuBottomDiv.find('.rolling_nav').outerWidth() - e.data.mdiHistoryAreaWidth;
			if (e.data.$menuBottomDiv.find('.home_menu').length > 0) {
				maxMove -= e.data.$menuBottomDiv.find('.home_menu').outerWidth();
			}
			e.data.interval = window.setInterval(function() {
				var left = parseInt(e.data.$rollingArea.css('left'));
				if (maxMove >= left) {
					window.clearInterval(e.data.interval);
					e.data.$bottomMoveRight.addClass('disabled');
					return false;
				}
				e.data.$rollingArea.css('left', left - 6);
			}, 20);
		}).on('mouseout', this, function(e) {
			if (e.data.interval) {
				window.clearInterval(e.data.interval);
			}
		});
	}
	
	$(window).on('popstate', function(e) {
		var data = e.originalEvent.state;
		if (data) {
			if (data.move) {
				location.href = location.href; 
			}
		}
	});
	
	this.layer();
	this.sortable();
}
MdiViewer.prototype.goRightFinish = function() {
	if (!isGnbSkin && this.$menuBottomLayer.hasClass('is-rolling')) {
		this.$bottomMoveLeft.removeClass('disabled');
		var maxMove = this.$menuBottomDiv.width() - this.$rollingArea.width() - this.$menuBottomDiv.find('.rolling_nav').outerWidth() - this.mdiHistoryAreaWidth;

		if (this.$menuBottomDiv.find('.home_menu').length > 0) {
			maxMove -= this.$menuBottomDiv.find('.home_menu').outerWidth();
		}
		
		this.$bottomMoveRight.addClass('disabled');
		this.$rollingArea.css('left', maxMove);
	}
}
MdiViewer.prototype.layer = function() {
	//LayerDiv
	var _this = this;
	this.$mdiMoreLayer = $('<div>').addClass('layer_gnb mdi_set').attr('id', 'MdiMoreLayer')
		.html('<ul class="menu_list">\
				<li><a href="javascript:void(0);" class="link copy"><span class="text"><fmt:message key="space.636"/></span></a></li>\
				<li><a href="javascript:void(0);" class="link popup"><span class="text"><fmt:message key="space.638"/></span></a></li>\
				<li><a href="javascript:void(0);" class="link fixedMenu"><span class="text"><fmt:message key="space.682"/></span></a></li>\
				<li><a href="javascript:void(0);" class="link btn_close"><span class="text"><fmt:message key="space.108"/></span></a></li>\
			</ul>').appendTo('body').on('mouseover', this, function(e) {
			clearTimeout(e.data.$mdiMoreLayer.close);
		}).on('mouseleave', function() {
			_this.$mdiMoreLayer.close = setTimeout(function() {
				_this.$mdiMoreLayer.hide();
			}, 2000);
		});
	this.$fixedMenuLayer = $('<div>').addClass('layer_gnb more_menu').attr('id', 'FixedMenuLayer')
		.html('<ul class="menu_list">\
				<li><a href="javascript:void(0);" class="link copy"><span class="text"><fmt:message key="space.636"/></span></a></li>\
				<li><a href="javascript:void(0);" class="link popup"><span class="text"><fmt:message key="space.638"/></span></a></li>\
				<li><a href="javascript:void(0);" class="link unfixed"><span class="text"><fmt:message key="space.683"/></span></a></li>\
			</ul>').appendTo('body').on('mouseover', this, function(e) {
			clearTimeout(e.data.$fixedMenuLayer.close);
		}).on('mouseleave', function() {
			_this.$fixedMenuLayer.close = setTimeout(function() {
				_this.$fixedMenuLayer.hide();
			}, 2000);
		});

	this.$mdiHistoryLayer = $('<div>').addClass('layer_gnb mdi_histoty').attr('id', 'MdiHistoryLayer').appendTo('body').on('mouseover', this, function(e) {
		clearTimeout(e.data.$mdiHistoryLayer.close);
	}).on('mouseleave', function() {
		_this.$mdiHistoryLayer.close = setTimeout(function() {
			_this.$mdiHistoryLayer.hide();
		}, 2000);
	});
	
	this.$mdiLayerWrap = $('#MdiLayerWrap');
	this.$mdiLayerArea = $('#MdiLayerArea');
	this.mdiLayerPadding = parseInt(this.$mdiLayerArea.css('padding-right'));

	this.$gnbMainMenuDiv = $('#GnbMainMenuDiv');
	this.$logoFixedMenuDiv = $('#LogoFixedMenuDiv');
	this.$fixedMenuList = $('#FixedMenuListArea');
	this.$fixedMenuBtnArea = $('#FixedMenuBtnArea');
	
	if (!isGnbSkin) {
		this.$menuBottomLayer = $('#MenuBottomLayer');
		this.$menuBottomDiv = $('#MenuBottomDiv');
		this.$menuInnerArea = $('#MenuInnerArea');
	}
	
	this.fixedMenuMaxCount = '<%=getMaxCount()%>';
	this.fixedUserMenu = new this.FixedUserMenu(this, this.fixedMenuMaxCount);
	this.MdiHistory();
}
MdiViewer.prototype.MdiHistory = function() {
	var $mdiLayer = $('#MdiLayer');
	JSV.getSVG('menu_history' + (isGnbSkin ? '' : '_common')).addClass('btn_mdi_history').appendTo(isGnbSkin ? this.$mdiLayerArea : '#MdiHistoryArea').on('click', this, function(e){
		var layer = e.data.$mdiHistoryLayer;
		if (layer.css('display') == 'none') {
			var param = {spId : JSV.getSpaceId()};
			var appIds = [];
			var sysIds = [];
			var menuIds = [];
			$mdiLayer.find('li').each(function() {
				var element = $(this);
				if (element.data('appId')) {
					appIds.push(element.data('appId'));
				} else if (element.data('sysId')) {
					sysIds.push(element.data('sysId'));
				} else if (element.data('menuId')) {
					menuIds.push(element.data('menuId'));
				}
			});
			param.appIds = appIds.join(',');
			param.sysIds = sysIds.join(',');
			param.menuIds = menuIds.join(',');
			var $this = $(this);
			$.ajax({
				url : JSV.getContextPath('/jsl/SpaceMdiHistoryUser.ListByUser.json'),
				data : param,
				dataType : 'json',
				success : function(data) {
					if (data && data.array) {
						var json = data.array;
						if (json.length > 0) {
							var historyUl = layer.find('ul').empty();
							if (layer.find('ul').length == 0) {
								historyUl = $('<ul>').addClass('menu_list').appendTo(layer);
							}
							for (var i = 0; i < json.length; i++) {
								var title = JSV.getLocaleStr(json[i].name);
								$('<a>').addClass('link').attr({'mTitle':json[i].name,'title':title}).html('<span class="text">' + title + '</span>').on('click', json[i], function(e){
									e.preventDefault();
									AllLayerHide();
									mViewer.doMenuClickParam(e.data, 'MdiHistoryMenu_');
								}).appendTo($('<li>').appendTo(historyUl));
							}
							
							AllLayerHide(layer);
							var p = $this.offset();
							layer.css({'left': p.left < 35 ? 35 : p.left - 95, 'top':p.top + 30}).show();
							layer.close = setTimeout(function() {
								layer.hide();
							}, 5000);
							
							$(document).one('click', this, function(e){
								layer.hide();
							});
						} else {
							AllLayerHide();
							JSV.alert('<fmt:message key="space.671"/>');
						}
					}
				},
				error : function(e) {}
			});
		} else {
			clearTimeout(layer.close);
			layer.hide();
		}
	});
}
MdiViewer.prototype.changeParam = function(value) {
	var data = JSV.clone(value);
	if (JSV.isEmpty(data['code']) || data['code'] == 0) {
		if (!JSV.isEmpty(data['sysId']) && data['sysId'] > 0) {
			data.code = <%=SpaceMenu.LEGACY_MENU_CODE%>;
			data.href = '/space/portal/legacy/gateway.jsp?sysId=' + data['sysId'];
		} else if (!JSV.isEmpty(data[JSV.MODMETHODNAMES.APPID]) && data[JSV.MODMETHODNAMES.APPID] > 0) {
			data.code = <%=SpaceMenu.APP_MENU_CODE%>;
		}
		if (!data['target']) {
			data['target'] = 'bottom';
		}
	} else {
		if (data.code == <%=SpaceMenu.PDF_MENU_CODE%>) {
			if (JSV.browser.msie10) {
				data['target'] = 'popup';
			}
		} else if (data.code == <%=SpaceMenu.ATTACH_MENU_CODE%>) {
			data['target'] = 'popup';
		}
	}
	for (var key in data) {
		if (!JSV.isEmpty(data[key])) {
			if (JSV.MODMETHODNAMES.MENUID == key) {
				if (data[key] > 0) {
					data.id = data[key];
				} else {
					data.id = null;
				}
			}
			if (JSV.MODMETHODNAMES.SPACEID == key && data[key] > 0) {
				data.spId = data[key];
			}
			if (JSV.MODMETHODNAMES.APPID == key && data[key] > 0) {
				data.appId = data[key];
			}
			if (JSV.MODMETHODNAMES.MODULEID == key) {
				data.mdId = data[key];
			}
			if ('menuCode' == key) {
				data.code = data[key];
			}
			if ('name' == key || 'title' == key) {
				data.text = data[key];
			}
			if ('linkSpaceId' == key) {
				data.linkSpaceId = data[key];
			}
			var emptyUrl = JSV.isEmpty(data['url']);
			if (!emptyUrl && JSV.isEmpty(data['href'])) {
				data.href = data['url'];
			}
			if ('serviceType' == key) {
				if (data[key] == <%=SpaceMenuSitemap.SERVICE_TYPE_LINK%>) {
					if (emptyUrl) {
						data.code = <%=SpaceMenu.PORTAL_MENU_CODE%>;
					}
				}
				if (JSV.isEmpty(data['code']) || data['code'] == 0) {
					data.code = data[key];
				}
			}
		}
	}
	if (data.spId != JSV.getSpaceId() && (data.code == <%=SpaceMenu.LEGACY_MENU_CODE%> || data.code == <%=SpaceMenu.APP_MENU_CODE%>)) {
		var portals = JSV.loadJSON('/jsl/SpaceSelector.JoinedPortalList.json').array;
		if (portals.length > 0) {
			for (var i = 0; i < portals.length; i++) {
				if (data.spId == portals[i].id) {
					data.target = 'openPopupPortal';
				}
			}
		}
	}
	
	return data;
}
MdiViewer.prototype.doMenuClickParam = function(value, popupName) {
	var data = this.changeParam(value);
	this.doMenuClick(data, popupName, true);
}
MdiViewer.prototype.doMenuClickMdi = function(value, popupName) {
	var data = this.changeParam(value);
	this.doMenuClick(data, popupName);
}
MdiViewer.prototype.doMenuClick = function(model, popupName, isFixedMenu) {
	if (this.firstClick) {
		this.firstClick = false;
		history.replaceState({move: true}, null, location.href);
		history.pushState(null, null, location.href);
	}
		
	var action;
	var obj = JSV.clone(model);
	isMdiExcute = true;
	var isValid = new RegExp(/^[0-9]+$/).test(obj.id);
	// menu log
	if (isValid && obj && obj.code && !clonePopup) {
		$.ajax({
			url : JSV.getContextPath('/jsl/SpaceMenuLogAction.DoMenuLog.json'),
			data : {spId : obj.spId, menuId : obj.id},
			dataType : 'json',
			success : function(e) {},
			error : function(e) {}
		});
	}

	if (obj.code == <%=SpaceMenu.APP_MENU_CODE%>) {
		action = 'doWorkModuleSubmit(obj, popupName)';
	} else if (obj.code == <%=SpaceMenu.SPACE_MENU_CODE%>) {
		action = SpaceWindowPopup.showSpace(JSV.getContextPath('/default.jsp?' + JSV.MODNAMES.SPACEID + '=' + obj.linkSpaceId), popupName);
		obj.target = 'popup';
	} else if (obj.code == <%=SpaceMenu.PORTAL_MENU_CODE%>) {
		action = 'doPortalSubmit(obj, popupName)';
		if (obj.target == null) obj.target = 'bottom';
	} else if (obj.code == <%=SpaceMenu.PDF_MENU_CODE%>) {
		if (JSV.browser.msie10) {
			action = 'doDownloadAttach(obj)';
			obj.target = 'popup';
		} else {
			action = 'doDownloadPDF(obj, popupName)';
			if (obj.target == null) obj.target = 'bottom';
		}
	} else if (obj.code == <%=SpaceMenu.ATTACH_MENU_CODE%>) {
		action = 'doDownloadAttach(obj, popupName)';
		obj.target = 'popup';
	} else if (obj.code == <%=SpaceMenu.EDITOR_MENU_CODE%>) {
		action = 'doEditorMenu(obj, popupName)';
		if (obj.target == null) obj.target = 'bottom';
	} else if (obj.href != null) {
		if (obj.href == 'Profile') {
			action = 'doProfile()';
		} else if (obj.href == 'ECM') {
			action = 'doECM(obj, popupName)';
		} else if (obj.href == 'MYSPACE') {
			action = 'doMYSPACE()';
			obj.target = 'popup';
		} else if (obj.href == 'TEAMSPACE') {
			action = 'doTEAMSPACE()';
			obj.target = 'popup';
		} else if (obj.target == 'clonePopup') {
			var url = JSV.suffix(obj.href, (obj.spId ? JSV.MODNAMES.SPACEID + '=' + obj.spId : null));
			action = 'doClonePopup(obj, url, popupName + obj.id)';
		} else if (obj.target == 'openPopupPortal') {
			var url = JSV.suffix(obj.href, (obj.spId ? JSV.MODNAMES.SPACEID + '=' + obj.spId : null));
			action = 'doOpenPopupPortal(obj, url, popupName + obj.id)';
		} else if (obj.target != 'popup') {
			action = 'doFrmSubmit(obj)';
		} else {
			if (obj.popupStyle == 'blank') {
				action = 'window.open(JSV.getContextPath(obj.href, (obj.spId ? JSV.MODNAMES.SPACEID + "=" + obj.spId : null)))';
			} else {
				action = 'window.open(JSV.getContextPath(obj.href, (obj.spId ? JSV.MODNAMES.SPACEID + "=" + obj.spId : null)), popupName + obj.id, obj.popupStyle).focus()';
			}
			obj.target = 'popup';
		}
	}

	var mdiId = model.menuId ? model.menuId : model.appId ? 'App' + model.appId : model.sysId ? 'sysid' + model.sysId : model.id;
	if (this.isUse && obj.target == 'bottom') {
		var li = null;
		var isHome = false;
		if (model.id == <%= SpaceMenu.HOME_MENUID%>) { //HomeMenu Icon으로 출력
			if (isGnbSkin) {
				if (this.$fixedMenuList.find('li:first').attr('fixedId') != <%= SpaceMenu.HOME_MENUID%>) {
					this.fixedUserMenu.addMenuLi(model, 0, true);
				}
				li = this.$fixedMenuList.find('li:first');
			} else {
				if (this.$menuInnerArea.find('.home_menu').length == 0) {
					this.fixedUserMenu.addMenuLi(model, 0, true);
				}
				li = this.$menuInnerArea.find('.home_menu');
			}
			isFixedMenu = true;
			isHome = true;
		} else {
			li = model.openMdi ? $('#MdiLayer li#MdiTab' + mdiId) : $('#FixedMenuListArea li#MdiTab' + mdiId);
			isFixedMenu = model.openMdi ? false : li.length > 0;
		}
		
		if (!isHome) {
			if (model.refreshTarget) {
				li = $('#MdiLayer li[target=' + model.refreshTarget + ']');
			} else {
				if (li != null && li.length == 0 && !model.openMdi && !model.sysId && !model.appId) {
					li = $('#FixedMenuListArea li#MdiTab' + mdiId);
					if (li.length == 0) {
						li = $('#MdiLayer li#MdiTab' + mdiId);
					} else {
						isFixedMenu = true;
					}
				}
				if (li.length == 0 && model.sysId) {
					li = $('#FixedMenuListArea li[mditabsysid=' + model.sysId + ']');
					if (li.length == 0) {
						li = $('#MdiLayer li[mditabsysid=' + model.sysId + ']');
					} else {
						isFixedMenu = true;
					}
				}
				if (li.length == 0 && model.appId) {
					li = $('#FixedMenuListArea li[appid=' + model.appId + ']');
					if (li.length == 0) {
						li = $('#MdiLayer li[appid=' + model.appId + ']');
					} else {
						isFixedMenu = true;
					}
				}
			}
		}

		if (isFixedMenu) {
  			isMdiExcute = clonePopup ? false : this.fixedMenuClick(li);
 			if (model.isNew) {
 				isMdiExcute = model.isNew;
 			}
 			if (isHome) {
 				isMdiExcute = true;
 			}
		} else {
			if (li.length == 0) {
				if (this.validate()) {
					return;
				}
				li = new this.MdiTAB(this, model).$li;
				//Mdi History
				if ((isValid || !model.id) && !clonePopup) {
					var param = {callSpId : JSV.getSpaceId(), name:model.mdiText || model.text};
					if (model.id  && model.id > 0) {
						param['menuId'] = model.id;
						li.data('menuId', model.id);
					}
					if (model.spId) {
						param[JSV.MODNAMES.SPACEID] = model.spId;
					}
					if (model.appId) {
						param[JSV.MODNAMES.APPID] = model.appId;
						li.data('appId', model.appId);
					}
					if (model.sysId) {
						param['sysId'] = model.sysId;
						li.data('sysId', model.sysId);
					}
					$.ajax({
						url : JSV.getContextPath('/jsl/SpaceMdiHistoryUser.Log.jsl'),
						data : param,
						dataType : 'json',
						success : function(e) {},
						error : function(e) {}
					});
				}
			}
			
			if (!model.refreshTarget) {
				var sort = 0;
				this.$ul.find('li[' + (model.sysId ? 'mditabsysid=' + model.sysId : 'id=MdiTab' + mdiId) + ']').each(function() {
					if (parseInt($(this).attr('sort')) >= parseInt(sort)) {
						sort = parseInt($(this).attr('sort'));
						li = $(this);
					}
				});
			}
		}

		if (!clonePopup) {
			obj.target = model.refreshTarget ? model.refreshTarget : li.attr('target');
		}

		if (clonePopup && li.length > 0) {
			if (li.data('model').id < 0) {
				isMdiExcute = false;
			} else {
				clonePopup = false;
				if (isGnbSkin && li.find('.selmenu').length > 0) {
					isMdiExcute = false;
					li.find('.selmenu').click();
				} else {
					isMdiExcute = true;
					obj.target = li.attr('target');
					li.click();
					eval(action);
				}
				return;
			}
		}
		
		if (isMdiExcute) {
			if (!isHome) {
				li.click();
			}
			if (!isGnbSkin) {
				this.goRightFinish();
			}
		}
	} else if (model.target == 'popup') {
		var li = $('#FixedMenuListArea li#MdiTab' + mdiId);
		if (model.sysId) {
			li = $('#FixedMenuListArea li[mditabsysid=' + model.sysId + ']');
		}
	}
	
	if (li && li.length > 0) {
		if (obj.target != 'popup') {
			this.menuClickSelect(li);
		}
	}
	
	if (isMdiExcute) {
 		eval(action);
	}
}
MdiViewer.prototype.fixedMenuClick = function(li) {
	var name = li.attr('target');
	var iframe = $('#' + name);
	var isNew = false;
	if (iframe.length == 0) { //iframe 있는 경우
		iframe = $('<iframe\
				id="' + name + '" \
				name="' + name + '" \
				width="100%" \
				src="" \
				scrolling="<%= com.kcube.lib.http.JslService.isMSIE6(request) ? "yes" : "auto"%>" \
				frameborder="0" noresize>\
			</iframe>').hide().appendTo(this.bottomLayer);
			setSize();
		isNew = true;
	}
	
// 	this.menuClickSelect(li);
	this.zSort();
	
	$(this.bottomLayer).find('iframe').hide();
	iframe.show();
	
	if (JSV.browser.chrome) {
		var height = iframe.height();
		var iframe = iframe.height(height + 1);
		setTimeout(function() {
			iframe.height(height);
		}, 100);
	}
	return isNew;
}
MdiViewer.prototype.menuClickSelect = function(li) {
	this.$ul.find('li').removeClass('select');
	this.$fixedMenuList.find('li').removeClass('select');
	
	if (!isGnbSkin && this.$menuInnerArea.find('.home_menu').length > 0) {
		this.$menuInnerArea.find('.home_menu').removeClass('select');
	}
	li.addClass('select');
}
MdiViewer.prototype.init = function() {
	this.isFlag = this.isFlag && this.length > 0;
	if (!this.isFlag) {
		if (this.length > 0) {
			this.isFlag = true;
			var liFirst = this.$ul.find('li:first');
			liFirst.addClass('first');
			this.liMargin = parseInt(liFirst.css('margin-right'));
			this.liMinWidth = parseInt(liFirst.css('min-width'));
			if (isGnbSkin) {
				this.liMaxWidth = parseInt(liFirst.outerWidth(true));
			} else {
				this.liMaxWidth = parseInt(liFirst.css('max-width'));
			}
		} else {
			this.liMinWidth = 0;
			this.liMargin = 0;
			this.liMaxWidth = 0;
		}
	}
	if (isGnbSkin) {
		this.mdiFixArea = this.mdiLayerPadding + (this.liMargin * this.length);
		this.liMinAllWidth = (this.liMinWidth + this.liMargin) * this.length + this.mdiLayerPadding;
	} else {
		this.mdiHistoryAreaWidth = $('#MdiHistoryArea').outerWidth(true);
	}
}
MdiViewer.prototype.doMDIBOTTOM = function(action, menuId, menuName, isSysId, isOrigin) {
	isSysId = isSysId ? eval(isSysId) : false;
	var model = {};
	if (isOrigin)
		model.origin = true;
	var i = action.indexOf('?');
	if (i > 0) {
		var map = JSV.toMap(action.substring(i + 1), '=', '&');
		for (var key in map) {
			if (JSV.MODNAMES.MENUID == key) {
				model.id = map[key];
				model.menuId = map[key];
			}
			if (JSV.MODNAMES.SPACEID == key) {
				model.spId = map[key];
			}
			if (JSV.MODNAMES.APPID == key) {
				model.appId = map[key];
				if (!clonePopup) {
					model.code = <%=SpaceMenu.APP_MENU_CODE%>;
				}
			}
			if (JSV.MODNAMES.CLASSID == key) {
				model.classId = map[key];
			}
			if (JSV.MODNAMES.MODULEID == key) {
				model.moduleId = map[key];
			}

			if ('sysId' == key) {
				isSysId = true;
				menuId = map[key];
			}
		}
	}

	if (model.id) {
		var menu = JSV.loadJSON('/jsl/SpaceMenuCommon.MenuName.json?'+JSV.MODNAMES.MENUID + '=' + model.id);
		model.text = (menu && menu.name) ? menu.name : 'Noname';
	} else {
// 		model.id = isSysId ? 'S' + menuId : menuId != null ? menuId : this.getId();
		model.id = isSysId ? 'S' + menuId : (menuId != null && menuId != 0) ? menuId : this.getId();
		if (menuName) {
			model.text = menuName;
		} else {
			if (model.appId) {
				model.text = getMenuName(model.appId, true);
				if (model.text == 'Noname') {
					model.text = JSV.loadJSON('/jsl/ModuleAppUser.AppName.json?'+JSV.MODNAMES.APPID + '=' + model.appId).name;
				}
			} else {
				model.text = 'Noname';
			}
		}
	}
	if (isSysId) {
		model.sysId = menuId;
		model.text = getMenuName(menuId);
		if (model.text == 'Noname' && menuName) {
			model.text = menuName;
		}
		if (!clonePopup) {
			model.code = <%=SpaceMenu.LEGACY_MENU_CODE%>;
		}
	}
	if (!model.spId) {
		model.spId = JSV.getSpaceId();
	}
	model.href = action;
	model.target = 'bottom';
	model.isNew = true;
 	this.doMenuClick(model);
}
MdiViewer.prototype.getId = function() {
	return 'menu_' + JSV.SEQUENCE++;
}
MdiViewer.prototype.reload = function() {
	$(this.$ul).find('.txt').each(function() {
		var element = $(this);
		element.attr('title', JSV.getLocaleStr(element.attr('mTitle'))).text(JSV.getLocaleStr(element.attr('mTitle')));
	});
}
MdiViewer.prototype.validate = function() {
	var maxCnt = parseInt(this.maxCount);
	var viewLiCnt = this.$ul.find('li').length;
	if (this.fixedMenuMaxCount) {
		viewLiCnt += this.fixedmenus.length;
		maxCnt += parseInt(this.fixedMenuMaxCount);
	}
	if (viewLiCnt >= maxCnt) {
		JSV.alert('<fmt:message key="space.333"/>');
		return true;
	}
	return false;
}
MdiViewer.prototype.zSort = function() {
	var length = this.length + 1;
	this.$ul.find('li').each(function(n) {
		if ($(this).hasClass('select')) {
			$(this).css('z-index', length);
		} else {
			$(this).css('z-index', length - n);
		}
	});
}
MdiViewer.prototype.sortable = function() {
// 	this.layer();
	
	_this = this;
	this.$ul.sortable({
		scroll : false,
		axis: 'x',
		items: '> li',
		tolerance: 'pointer',
		containment: '#MdiLayerWrap',
		start: function(event, ui){
			_layer = $('<div>').css({
				'position':'absolute',
				'z-index':'999',
				'width':'100%',
				'height':'100%'
			}).prependTo(_this.$mdiLayerArea);
		},
		stop: function(event, ui){
			_layer.remove();
			_this.zSort();
		},
		zIndex: 9999
	}).css('position','relative');
}
MdiViewer.prototype.MdiTAB = function(parent, model, append) {
	this.parent = parent;
	this.parent.sort += 1;
	var mdiId = model.menuId ? model.menuId : model.appId ? 'App' + model.appId : model.sysId ? 'sysid' + model.sysId : model.id;
	
// 	var mdiId = model.appId ? 'App' + model.appId : model.id;
	var mdiText = model.mdiText || model.text;
	this.name = 'KCUBE_MDI_BOTTOM' + mdiId + '_' + this.parent.sort;
	this.$li = $('<li class="mdi_item">').attr({'id': 'MdiTab' + mdiId, 'sort':this.parent.sort, 'target':this.name}).data('model', model);
	
	this.$li.html('<div class="mdi_text"><p class="text txt"></p></div><div class="mdi_btn"><div>');
	var mdiBtn = this.$li.find('.mdi_btn');
	var _this = this;
	
	JSV.getSVG('btn_more').addClass('btn_more').on('click', this, function(e) {
		e.stopPropagation();
		if (_this.parent.$mdiMoreLayer.css('display') == 'none') {
			AllLayerHide(_this.parent.$mdiMoreLayer);
						
			_this.parent.$mdiMoreLayer.close = setTimeout(function() {
				_this.parent.$mdiMoreLayer.hide();
			}, 5000);

			var p = $(this).offset();
			_this.parent.$mdiMoreLayer.css({'left': p.left - 60, 'top': p.top + 28}).show();
			
			_this.parent.$mdiMoreLayer.find('.copy').off('click').one('click', function(event) {
				AllLayerHide();
				_this.copy(e);
			});

			var closeBtn = _this.parent.$mdiMoreLayer.find('.btn_close');
			var fixedMenuBtn = _this.parent.$mdiMoreLayer.find('.fixedMenu');
			if (closeBtn.closest('li').css('display') == 'none') {
				closeBtn.closest('li').show();
			}
			closeBtn.off('click').one('click', function(event) {
				AllLayerHide();
				_this.del(e);
			});
			var id = e.data.$li.data('model').id;
			var isValid = new RegExp(/^[0-9]+$/).test(id);

			if ((isValid && e.data.$li.data('model').id > 0) || e.data.$li.data('model').appId || e.data.$li.data('model').sysId) {
				if (fixedMenuBtn.closest('li').css('display') == 'none') {
					fixedMenuBtn.closest('li').show();
				}
				fixedMenuBtn.off('click').one('click', function(event) {
					AllLayerHide();
					_this.fixedMenu(e);
				});
			} else {
				fixedMenuBtn.closest('li').hide();
			}
			_this.parent.$mdiMoreLayer.find('.popup').off('click').one('click', function(event) {
				AllLayerHide();
				_this.popup(e);
			});
			$(document).one('click', this, function(e){
				_this.parent.$mdiMoreLayer.hide();
			});
		} else {
			clearTimeout(_this.parent.$mdiMoreLayer.close);
			_this.parent.$mdiMoreLayer.hide();
		}
	}).appendTo(mdiBtn);
	
	JSV.getSVG('btn_del' + (isGnbSkin ? '' : '_common')).addClass('btn_del btn_close').appendTo(mdiBtn).on('click', this, this.del);
	
	if (model.sysId) {
		this.$li.attr('MdiTabSysId', model.sysId);
	}
	if (model.appId) {
		this.$li.attr('appId', model.appId);
	}
	
	if (append) {
		this.$li.insertAfter(append);
	} else {
		this.$li.appendTo(this.parent.$ul);
	}
	
	this.$li.find('.txt').attr({'mTitle': mdiText, 'title':JSV.getLocaleStr(mdiText).trim()}).text(JSV.getLocaleStr(mdiText).trim());

	this.iframe = $('#' + this.name);
	if (this.iframe.length == 0) {
		this.iframe = $('<iframe\
			id="' + this.name + '" \
			name="' + this.name + '" \
			width="100%" \
			src="" \
			scrolling="<%= com.kcube.lib.http.JslService.isMSIE6(request) ? "yes" : "auto"%>" \
			frameborder="0" noresize>\
		</iframe>').hide().appendTo(this.parent.bottomLayer);
		setSize();
	}
	this.$li.on('click', this, this.onclick);
	++this.parent.length;
	this.parent.resize();
}
MdiViewer.prototype.MdiTAB.prototype.onclick = function(event) {
	event.stopPropagation();
	var model = $(this).data('model');
	if (event.data.parent.$fixedMenuList) {
		event.data.parent.$fixedMenuList.find('li').removeClass('select');
	}
	if (!isGnbSkin && event.data.parent.$menuInnerArea.find('.home_menu').length > 0) {
		event.data.parent.$menuInnerArea.find('.home_menu').removeClass('select');
	}
	event.data.parent.$ul.find('li').removeClass('select');
	$(this).addClass('select');
	event.data.parent.zSort();
	
	$(event.data.parent.bottomLayer).find('iframe').hide();
	event.data.iframe.show();
// 	if (event.data.$li.data('default') && event.data.parent.$ul.find('li').index() == 0) {
// 		event.data.$li.removeData('default');
// 		viewer.getMenuByIndex(0).doClick(true);
// 	}
	
	if (JSV.browser.chrome) {
		var height = event.data.iframe.height();
		var iframe = event.data.iframe.height(height + 1);
		setTimeout(function() {
			iframe.height(height);
		}, 100);
	}
	if (event.data.onload) event.data.onload();
}
MdiViewer.prototype.MdiTAB.prototype.popup = function(event) {
	event.stopPropagation();
// 	if (event.data.parent.validate())return;
	var data = JSV.clone(event.data.$li.data('model'));
	data.target = 'clonePopup';
	data.popupStyle = 'width=1024px,height=768px,scrollbars=yes,status=no,toolbar=no,menubar=no,location=no,resizable=yes';
	event.data.parent.doMenuClick(data, 'clonePopup');
}
MdiViewer.prototype.MdiTAB.prototype.copy = function(event) {
	event.stopPropagation();
	if (event.data.parent.validate())return;
	var data = event.data.$li.data('model');
	data.openMdi = true;
// 	new event.data.parent.MdiTAB(event.data.parent, data, event.data.$li);
	new event.data.parent.MdiTAB(event.data.parent, data);
	event.data.parent.doMenuClick(data);
}
MdiViewer.prototype.MdiTAB.prototype.fixedMenu = function(event) {
	var _parent = this.parent;
	if (_parent.fixedmenus.length >= _parent.fixedMenuMaxCount) {
		JSV.alert('<fmt:message key="space.662"/>');
	} else {
		var target = event.data.$li.attr('target');
		var isAdd = _parent.fixedUserMenu.addMenu(event.data.$li);
		_parent.resize();

		if (isAdd) {
			event.data.$li.remove();
			_parent.fixedUserMenu.$fixedMenuList.find('li:last').attr('target', target).find('.selmenu').click();
		}
	}
}
MdiViewer.prototype.MdiTAB.prototype.del = function(event) {
	var model = event.data.$li.data('model');
	$('#' + event.data.name).remove();
	var select = event.data.$li.hasClass('select');
	event.data.$li.remove();
	--event.data.parent.length;
	event.data.parent.resize();
	if (select) {
		if (event.data.parent.$ul.find('li').length > 0) {
			event.data.parent.$ul.find('li:last').click();
			if (event.data.onload) event.data.onload();
		} else {
			doDefault();
		}
	}
}
MdiViewer.prototype.FixedUserMenu = function(parent, maxCnt) {
	this.maxCnt = maxCnt ? maxCnt : 10;
	this.parent = parent;
	
	var _this = this;
	this.$fixedMenuList = this.parent.$fixedMenuList;
	this.$fixedMenuLayer = this.parent.$fixedMenuLayer;
	
	this.fixedMenuAdd = $('<li>').addClass('menu_add').html(JSV.getSVG('menu_add' + (isGnbSkin ? '' : '_common')).addClass('btn')).on('click', this, function(e){
		_this.showMenu();
	}).appendTo(this.parent.$fixedMenuBtnArea);

	this.fixedMenuDel = $('<li>').addClass('menu_del').html(JSV.getSVG('menu_del' + (isGnbSkin ? '' : '_common')).addClass('btn')).on('click', this, function(e){
		_this.showMenu();
	}).appendTo(this.parent.$fixedMenuBtnArea).hide();
	
	this.setValue();
	
	this.$fixedMenuList.sortable({
		scroll : false,
		axis: 'x',
		items: '> li.change',
		tolerance: 'pointer',
		containment: '#FixedMenuListArea',
		start: function(event, ui) {
			AllLayerHide();
			_layer = $('<div>').css({
				'position':'absolute',
				'z-index':'999',
				'width':'100%',
				'height':'100%'
			}).prependTo(_this.$fixedMenuList);
		},
		stop: function(event, ui){
			_layer.remove();
			_this.zSort($(ui.item).attr('fixedid'));
		},
		zIndex: 9999
	}).css('position','relative');
}
MdiViewer.prototype.FixedUserMenu.prototype.setValue = function() {
	if (isIconType) {
		this.tooltip = $('<span>').addClass('tooltip');
		this.parent.$gnbMainMenuDiv.after(this.tooltip);
	}
	
	var json = JSV.loadJSON('/jsl/SpaceUserMenuUser.ListByUser.json?spId=' + JSV.getSpaceId()).array;
	var count = json.length > this.maxCnt ? this.maxCnt : json.length;
	for (var i = 0; i < count; i++) {
		this.addMenuLi(json[i]);
	}
}
MdiViewer.prototype.FixedUserMenu.prototype.zSort = function(menuId) {
	if (menuId != <%=SpaceMenu.HOME_MENUID%>) {
		var obj = [];
		this.$fixedMenuList.find('li[class^=change]').each(function(n) {
			var fixedId = $(this).attr('fixedid');
			obj.push(fixedId);
		});
		
		var ctx = {};
		var menu = {'id':menuId};
		ctx.menu = JSV.toJSON(menu);
		ctx.ids = obj.join(',');
		var _this = this;
		$.ajax({
			url : JSV.getContextPath(JSV.getModuleUrl('/jsl/SpaceUserMenuUser.Sort.jsl')),
			data : ctx,
			dataType : 'json',
			success : function(res) {
			},
			error : function() {
			}
		});
	}
}
MdiViewer.prototype.FixedUserMenu.prototype.showMenu = function() {
	AllLayerHide();
	
	var url = JSV.getContextPath(JSV.getModuleUrl('/space/menu/UserFixedMenuLayer.jsp'));
	var fObj = {'title':'<fmt:message key="space.658"/>', 'width':1000, height:725, resizable: false, 'dialogClass':'LayerModalDialog whiteTitleBar'};
	var _this = this;
	this.saveArr = [];
	this.$fixedMenuList.find('li[class^=change]').each(function(n) {
		_this.saveArr.push($(this).data('model'));
	});
	
	JSV.showLayerModalDialog(url, _this.saveArr, fObj, function(value) {
		if (value.length < _this.saveArr.length) {
			for (var i = _this.saveArr.length; i > value.length; i--) {
				_this.removeMenuLi(i - 1);
			}
		}
		
		var json = JSV.loadJSON('/jsl/SpaceUserMenuUser.ListByUser.json?spId=' + JSV.getSpaceId()).array;
		var count = json.length > this.maxCnt ? this.maxCnt : json.length;
		
		for (var i = 0; i < count; i++) {
			var fixedId = json[i].id;
			if (_this.saveArr.length > 0) {
				if (_this.saveArr[i] && _this.saveArr[i].id) {
					if (fixedId != _this.saveArr[i].id) {
						_this.addMenuLi(json[i], (i + 1));
						_this.removeMenuLi(i);
					}
				} else {
					_this.addMenuLi(json[i], i);
				}
			} else {
				_this.addMenuLi(json[i]);
			}
		}
		if (_this.parent.setMegaMenu) {
			_this.parent.setMegaMenu();
		}
	});
}
MdiViewer.prototype.FixedUserMenu.prototype.resize = function() {
	this.parent.resize(true);
}
MdiViewer.prototype.FixedUserMenu.prototype.btnControl = function(isAllView) {
	if (this.parent.fixedmenus.length < this.maxCnt) {
		this.fixedMenuAdd.show();
		this.fixedMenuDel.hide();
	} else {
		this.fixedMenuAdd.hide();
		this.fixedMenuDel.show();
	}
}
MdiViewer.prototype.FixedUserMenu.prototype.getSaveIds = function(menuId) {
	var obj = [];
	for (var i = 0; i < this.parent.fixedmenus.length; i++) {
		if (menuId && menuId == this.parent.fixedmenus[i].id) {
			return i;
		}
		obj.push(this.parent.fixedmenus[i].id);
	}
	return obj;
}
MdiViewer.prototype.FixedUserMenu.prototype.removeMenu = function(li) {
	var $li = $(li);
// 	var menuId = $li.attr('menuid');
	var delIndex = this.getSaveIds($li.attr('fixedid'));
	if (delIndex > -1) {
		var saveIds = this.getSaveIds();
		var delId = saveIds[delIndex];
		var _this = this;
		$.ajax({'url':JSV.getContextPath(JSV.getModuleUrl('/jsl/SpaceUserMenuUser.Delete.jsl')),
			'dataType':'json',
			'data' : {'callSpaceId': JSV.getSpaceId(), 'id':delId, 'saveIds':saveIds.join(',')},
			'async' : false,
			'success':function(data) {
				$li.find('.btn_minus').hide();
				_this.removeMenuLi(delIndex);
			},
			'error':function(xhr){
			}
		});
	}
}
MdiViewer.prototype.FixedUserMenu.prototype.removeMenuLi = function(index) {
	this.parent.fixedmenus.splice(index, 1);
	var $li = this.$fixedMenuList.find('li[class^=change]').eq(index);
	var menuId = $li.attr('menuid');
	$li.remove();
	if (menuId && viewer.$megaMenu) {
		var selectMenuDiv = viewer.$megaMenu.find('li[menuid=' + menuId + ']');
		if (selectMenuDiv.length == 0) {
			selectMenuDiv = viewer.$megaMenu.find('p[menuid=' + menuId + ']');
		}
		selectMenuDiv.find('.btn_minus').hide();
	}
	this.resize();
}
MdiViewer.prototype.FixedUserMenu.prototype.addMenu = function(li) {
	var $li = $(li);
	var obj = $li.data('model');
	//serviceType : Main Menu인경우는 serviceType값 나눠서 저장
	if (obj.type) {
		if (obj.type == <%=SpaceMenu.MAINMENU_TYPE%> || obj.type == <%=SpaceMenu.SUBMENU_TYPE%>) {
			obj.serviceType = <%=SpaceUserMenu.SERVICE_TYPE_APP%>;
			if (obj.code == <%=SpaceMenu.URL_MENU_CODE%> || obj.code == <%=SpaceMenu.PORTAL_MENU_CODE%>) {
				obj.serviceType = <%=SpaceUserMenu.SERVICE_TYPE_LINK%>;
			} else if (obj.code == <%=SpaceMenu.SPACE_MENU_CODE%>) {
				obj.serviceType = <%=SpaceUserMenu.SERVICE_TYPE_PORTAL%>;
			}
		} else if (obj.type == <%=SpaceMenu.SYSTEMMENU_TYPE%> || obj.type == <%=SpaceMenu.SYSTEMSUBMENU_TYPE%>) {
			obj.serviceType = <%=SpaceUserMenu.SERVICE_TYPE_WORKSYSTEM%>;
		}
	}
	var param = {'callSpaceId': JSV.getSpaceId(), 'serviceType' : obj.serviceType, 'name' : obj.text.replace(/\n/g, '')};
	var isValid = new RegExp(/^[0-9]+$/).test(obj.id)
	if (isValid)
		param['menuId'] = obj.id;
	if (obj.spId)
		param[JSV.MODNAMES.SPACEID] = obj.spId;
	if (obj.appId) {
		param[JSV.MODNAMES.APPID] = obj.appId;
		param['serviceType'] = <%=SpaceUserMenu.SERVICE_TYPE_APP%>;
	}
	if (obj.sysId) {
		param['sysId'] = obj.sysId;
		param['serviceType'] = <%=SpaceUserMenu.SERVICE_TYPE_APP%>;
	}
	if (obj.linkSpaceId) {
		param[JSV.MODNAMES.SPACEID] = obj.linkSpaceId;
		param['serviceType'] = <%=SpaceUserMenu.SERVICE_TYPE_PORTAL%>;
	}
	param['saveIds'] = this.getSaveIds().join(',');
	var _this = this;
	var returnVal = false;
	$.ajax({'url':JSV.getContextPath('/jsl/SpaceUserMenuUser.Save.json'),
		'dataType':'json',
		'data' : param,
		'async' : false,
		'success':function(data, status) {
			if (data) {
				if (data.error) {
					if (data.error == '<fmt:message key="space.usermenu.error.001"/>') {
						JSV.alert('<fmt:message key="space.662"/>');
					} else if (data.error == '<fmt:message key="space.usermenu.error.002"/>') {
						JSV.alert('<fmt:message key="space.663"/>');
					} else {
						JSV.alert(JSV.getLang('JslAction', 'ERROR'));
					}
				} else {
					_this.addMenuLi(data);
					if (_this.parent.setMegaMenu) {
						_this.parent.setMegaMenu();
					}
					returnVal = true;
				}
			}
		},
		'error':function(xhr){
		}
	});
	return returnVal;
}
MdiViewer.prototype.FixedUserMenu.prototype.addMenuLi = function(obj, index, isHome) {
	var title = isHome ? JSV.getLocaleStr(obj.text) : JSV.getLocaleStr(obj.name);
	var mdiId = isHome ? obj.id : obj.menuId ? obj.menuId : obj.appId ? 'App' + obj.appId : obj.id;
	this.parent.sort += 1;
	var name = 'KCUBE_MDI_BOTTOM' + mdiId + '_' + this.parent.sort;
	var $tag = !isGnbSkin && isHome ? $('<div>') : $('<li>');
	var $li = $tag.data('model', obj).attr({'id': 'MdiTab' + mdiId, 'target':name, 'mTitle':isHome ? obj.text : obj.name, 'fixedid':obj.id, 'menuid':obj.menuId});
	if (obj.sysId) {
		$li.attr('MdiTabSysId', obj.sysId);
	}
	
	if (obj.appId) {
		$li.attr('appId', obj.appId);
	}

	if (index) {
		this.$fixedMenuList.find('li:nth-child(' + index + ')').after($li);
	} else {
		if (isHome) {
			if (isGnbSkin) {
				$li.prependTo(this.$fixedMenuList);
			} else {
				$li.prependTo(this.parent.$menuInnerArea);
			}
		} else {
			$li.appendTo(this.$fixedMenuList);
		}
	}
	var _parent = this.parent;
	var _this = this;
	var svgIcon = null;
	if (isHome) {
		$li.addClass('home');
		
		if (isGnbSkin) {
			svgIcon = $('<div class="imageWrap" style="display: table; float: left; font-size: 0px; width: 32px; height: 32px;">\
					<div class="svgArea" style="position: relative; display: table-cell; text-align: center; vertical-align: middle; width: 32px; height: 32px;">\
						<span class="svgBg" style="background: rgb(147, 165, 195); position: absolute; top: 0px; left: 0px; display: block; height: 32px; width: 32px; border-radius: 50%;"></span>\
						<svg xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 0 24 24" width="24" style="margin-top: 1px; fill: rgb(255, 255, 255); position: relative; z-index: 1; display: inline-block; width: 18px; height: 18px;"><path d="M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z"></path><path d="M0 0h24v24H0z" fill="none"></path></svg>\
					</div>\
				</div>');
			//area
			svgIcon.addClass('menu selmenu').on('click', this, function(e) {
				AllLayerHide();
				mViewer.menuClickSelect($li);
				_this.onclick($(this).closest($tag));
			}).appendTo($li);
		} else {
			$li.addClass('home_menu').html('<svg xmlns="http://www.w3.org/2000/svg" width="13" height="13" viewBox="0 0 13 13">\
		    	<path fill-rule="evenodd" d="M8.75 13v-2.688a1 1 0 0 0-1-1H5.219a1 1 0 0 0-1 1V13H0V6l6.4-6L13 6v7z"/>\
				</svg>').on('click', this, function(e) {
					AllLayerHide();
					_this.onclick($(this).closest($tag));
			});
		}
	} else {
		_parent.fixedmenus.push(obj);
		$li.addClass('change');
		var isPopup = obj.target == 'popup' || obj.serviceType == <%=SpaceMenuSitemap.SERVICE_TYPE_PORTAL%>;
		$li.data('popup', isPopup);
		
		if (isIconType) {
			var iconCode = obj.iconCode;
			if (obj.iconType == JSV.DEFAULT_ICON_TYPE || obj.iconType == JSV.TEMPLATE_ICON_TYPE || obj.iconType == JSV.UPLOAD_ICON_TYPE) {
				var fileData = null;
				if (obj.iconSavePath && obj.iconSaveCode > 0) {
					var oImg = {};
					oImg.type = obj.iconSaveCode;
					oImg.path = obj.iconSavePath;
					fileData = oImg;
				}
				svgIcon = JSV.getIcon([obj.iconType, obj.iconCode, fileData], 32);
				if (obj.iconType == JSV.UPLOAD_ICON_TYPE) {
					$('<span>').addClass('hoverBg').appendTo(svgIcon);
				}
			}
			if (svgIcon == null) {
				var colorNum = Math.floor(Math.random() * 9) + 1;
				svgIcon = JSV.getColorIcon(title, (colorNum %10), 32);
				svgIcon.css('background', '#93a5c3');
			}
			
			//area
			svgIcon.addClass('menu selmenu').on('click', this, function(e) {
				e.stopPropagation();
				var _this = $(this).closest($tag);
				if (_this.hasClass('select')) {
					e.data.layerShow(_this, e);
				} else {
					AllLayerHide();
// 					mViewer.menuClickSelect($li);
					e.data.onclick(_this);
				}
			}).appendTo($li);
			
			$li.on('mouseenter', this, function(e) {
				var _this = $(this);
				if (_parent.$fixedMenuLayer.close != null) {
					clearTimeout(_parent.$fixedMenuLayer.close);
					_parent.$fixedMenuLayer.close = null;
				} 
				e.data.tooltipEvent($(this));
			}).on('mouseleave', this, function(e) {
				var _this = $(this);
				if (!_this.hasClass('select') && e.data.tooltipShow != null) {
					clearTimeout(e.data.tooltipShow);
					e.data.tooltipShow = null;
				}
				e.data.tooltip.removeClass('hover').css('opacity','0');
				_parent.$fixedMenuLayer.close = setTimeout(function() {
					_parent.$fixedMenuLayer.hide();
				}, 5000);
			}).on('mouseover', this, function(e) {
				if ($(this).hasClass('select')) {
					e.data.tooltip.addClass('hover').css('opacity','1');
				}
			});
		} else {
			//textType draw
			var liDiv = $('<div>').addClass('textWrap selmenu').appendTo($li).on('click', this, function(e) {
				e.stopPropagation();
				AllLayerHide();
				_this.onclick($(this).closest($tag));
 			});
			
			if (isGnbSkin && isPopup) {
				JSV.getSVG('new_window').addClass('new_window').appendTo(liDiv);
			}
			$('<div>').addClass('fixed_menu_text').html($('<p>').attr('title', title).addClass('text txt').text(title)).appendTo(liDiv);
			var fixedMenuBtn = $('<div>').addClass('fixed_menu_btn').appendTo(liDiv).on('click', this, function(e) {
				e.stopPropagation();
				var _this = $(this).closest('li');
				e.data.layerShow(_this, e);
			}).on('mouseleave', this, function(e) {
				_parent.$fixedMenuLayer.close = setTimeout(function() {
					_parent.$fixedMenuLayer.hide();
				}, 5000);
			});
			JSV.getSVG('btn_more').addClass('btn_more').appendTo(fixedMenuBtn);
		}
	}
}
MdiViewer.prototype.FixedUserMenu.prototype.onclick = function(_this) {
 	this.parent.doMenuClickParam(_this.data('model'), 'FixedMenu_');
}
MdiViewer.prototype.FixedUserMenu.prototype.layerShow = function(_this, e) {
	AllLayerHide(this.parent.$fixedMenuLayer);
	if (this.parent.$fixedMenuLayer.css('display') == 'none') {
		var p = _this.offset();
		var pLeft = p.left - 10;
		if (isIconType) {
			pLeft -= 30;
		}
		this.parent.$fixedMenuLayer.css({'left': pLeft, 'top': p.top + 35}).show();
		
		var thisPopup = _this.data('popup');
		if (thisPopup) {
			this.parent.$fixedMenuLayer.find('.copy').hide();
			this.parent.$fixedMenuLayer.find('.popup').hide();
		} else {
			this.parent.$fixedMenuLayer.find('.copy').show().off('click').one('click', function(event) {
				AllLayerHide();
				e.data.copy(e, _this);
			});
			
			this.parent.$fixedMenuLayer.find('.popup').show().off('click').one('click', function(event) {
				AllLayerHide();
				e.data.popup(e, _this);
			});
		}
		
		var unFixedBtn = this.parent.$fixedMenuLayer.find('.unfixed');
		if (_this.attr('fixedId') == <%=SpaceMenu.HOME_MENUID%>) {
			unFixedBtn.closest('li').hide();
		} else {
			unFixedBtn.closest('li').show();
			unFixedBtn.off('click').one('click', function(event) {
				AllLayerHide();
				e.data.unfixed(e, _this);
			});
		}
		$(document).one('click', this, function(e){
			e.data.parent.$fixedMenuLayer.hide();
		});
	} else {
		clearTimeout(this.parent.$fixedMenuLayer.close);
		this.parent.$fixedMenuLayer.hide();
	}
}
MdiViewer.prototype.FixedUserMenu.prototype.tooltipEvent = function(li) {
	var _this = this;
	
	if (this.tooltipShow != null) {
		clearTimeout(this.tooltipShow);
		this.tooltipShow = null;
	}
	AllLayerHide();
	this.tooltip.hide();
	this.tooltip.text(JSV.getLocaleStr(li.attr('mTitle')));
	var top = li.outerHeight() + li.offset().top + 5;
	if (viewer.$noticeFixedDiv.length > 0) {
		top -= viewer.$noticeFixedDiv.height();
	}
	this.tooltip.css('top', top);
	var width = this.tooltip.outerWidth();
	var left = li.offset().left + ((li.width() - width) / 2);
	this.tooltip.css('left', left);
	this.tooltip.show();
	this.tooltipShow = setTimeout(function() {
		_this.tooltip.addClass('hover').css('opacity', '1');
	}, 100);
}
MdiViewer.prototype.FixedUserMenu.prototype.copy = function(event, _this) {
	event.stopPropagation();
	if (event.data.parent.validate()) return;
	var data = event.data.parent.changeParam(_this.data('model'));
	data.openMdi = true;
	if (data.target != 'openPopupPortal' && data.target != 'popup') {
		new event.data.parent.MdiTAB(event.data.parent, data);
	}
	event.data.parent.doMenuClick(data, '_copyMdi');
}
MdiViewer.prototype.FixedUserMenu.prototype.popup = function(event, _this) {
	event.stopPropagation();
// 	if (event.data.parent.validate()) return;
	var data = JSV.clone(_this.data('model'));
	data.target = 'clonePopup';
	data.popupStyle = 'width=1024px,height=768px,scrollbars=yes,status=no,toolbar=no,menubar=no,location=no,resizable=yes';
	event.data.parent.doMenuClickMdi(data, 'clonePopup');
}
MdiViewer.prototype.FixedUserMenu.prototype.unfixed = function(event, _this) {
	event.stopPropagation();
	if (event.data.parent.validate()) return;
	var isPopup = _this.data('popup');
	var data = event.data.parent.changeParam(_this.data('model'));
	data.openMdi = true;
	event.data.removeMenu(_this);
	if (!isPopup && data.target != 'openPopupPortal') {
		new event.data.parent.MdiTAB(event.data.parent, data);
		event.data.parent.doMenuClick(data, '_copyMdi');
	}
}
function AbbreviateString(str, size) {
	var fSize = 12;
	var l = 0;
	var newStr = '';
	for (var i=0; i<str.length; i++) {
		if (str.charCodeAt(i) > 126) l += fSize;
		else if (str.charCodeAt(i) <= 122 && str.charCodeAt(i) > 96) l += (fSize - 5);
		else if (str.charCodeAt(i) <= 90 && str.charCodeAt(i) > 64) l += (fSize - 3);
		else if (str.charCodeAt(i) <= 32) l += (fSize / 2);
		else l += (fSize + charCodeFontSize[str.charCodeAt(i)]);
		
		if (l < size - 24) newStr += str.charAt(i);
	}
	if (l > size - 24) newStr = newStr + '...';

	return newStr;
}
function setSize() {
	var size = document.body.clientHeight - $('#TopLayer').outerHeight(true);
	$('#BottomLayer > iframe').height(size);
}
function doWorkModuleSubmit(obj, popupName) {
	if (obj.moduleId == <%=com.kcube.sys.ecm.EcmParam.getModuleIdByTenant(UserService.getTenantId()) %>) {
		doECM(obj, popupName);
	} else {
		var url = null;
		if (obj.appId == null || obj.appId == '' || obj.appId == 0) {
			url = '/space/portal/menu.nonLink.jsp';
		} else {
			var isDev = obj.classId && obj.classId != '' && obj.classId != 0;
			url = isDev ? '/space/menu/module.proxy.jsp' : (obj.origin ? obj.href : '${bldrIndexUrl}');
			var param = [];
			if (isDev) {
				param.push(JSV.MODNAMES.CLASSID + '=' + obj.classId);
			}
			param.push(JSV.MODNAMES.MODULEID + '=' + obj.moduleId);
			param.push(JSV.MODNAMES.APPID + '=' + obj.appId)
			param.push(JSV.MODNAMES.SPACEID + '=' + JSV.getSpaceId());
			
			if (isDev && obj.href) {
				var urlIdx = obj.href.indexOf('?');
				if (urlIdx > -1) {
					var params = obj.href.substr(urlIdx + 1).split('&');
				    for (var i = 0; i < params.length; i++) {
				        var temp = params[i].split('=');
				        var tempVal = temp[0] + '=' + temp[1];
				        if (param.indexOf(tempVal) < 0)
				        	param.push(tempVal);
				    }
				}
			}
			
			url = JSV.suffix(url, param.join('&'));
		}
		if (obj.target == 'popup') {
			window.open(JSV.getContextPath(url), popupName + obj.appId, obj.popupStyle);
		} else if (obj.target == 'clonePopup') {
			doClonePopup(obj, url, popupName + obj.appId);
		}  else if (obj.target == 'openPopupPortal') {
			doOpenPopupPortal(obj, url, popupName + obj.appId);
		} else {
			var mFrm = $('#mFrm');
			mFrm.find('input').remove();
			if (obj.origin) {
				$('<input>').attr({'type':'hidden', 'name':'originUrl'}).val(obj.href).appendTo(mFrm);
			}
			
			mFrm.attr({'method':'POST', 'action':JSV.getContextPath(url), 'target':obj.target}).submit();
		}
	}
}
function doSpaceSubmit(obj) {
	$('#mFrm').attr({'method':'POST', 'action':JSV.getContextPath('/default.jsp', JSV.MODNAMES.SPACEID + '=' + obj.linkSpaceId), 'target':'_self'}).submit();
}
function doPortalSubmit(obj, popupName) {
	var param = [];
	param.push(JSV.MODNAMES.MENUID + '=' + obj.id);
	param.push(JSV.MODNAMES.SPACEID + '=' + JSV.getSpaceId());
	var url = JSV.suffix('${portalUrl}?reload=true', param.join('&'));
	if (obj.target == 'clonePopup') {
		doClonePopup(obj, url, popupName + obj.id);
	} else {
		$('#mFrm').attr({'method':'POST', 'action':JSV.getContextPath(url), 'target':obj.target}).submit();
	}
}
function doFrmSubmit(obj) {
	$('#mFrm').attr({'method':'POST', 'action':JSV.getContextPath(obj.href, JSV.MODNAMES.SPACEID + '=' + obj.spId), 'target':obj.target}).submit();
}
function doMYSPACE() {
	mViewer.doMDIBOTTOM('/space/menu/usr.index.jsp', -3000, JSV.getLocaleStr('<%=StringEscapeUtils.escapeJavaScript(UserService.getUser().getName())%>'));
}
function doTEAMSPACE() {
 	var data = JSV.loadJSON('/jsl/TeamSpaceMember.SpaceList.json');
 	var arr = data.array;
 	if (arr.length > 0) {
 		SpaceWindowPopup.showSpace(JSV.getContextPath('/default.jsp?' + JSV.MODNAMES.SPACEID + '=' + arr[0].id), 'TeamSpaceWindow');
 	} else {
 		JSV.alert('<fmt:message key="space.team.025"/>');
 	}
 	return false;
}
function doDownloadAttach(obj) {
	var param = [];
	param.push('id=' + obj.id);
	param.push(JSV.MODNAMES.SPACEID + '=' + JSV.getSpaceId());
	var url = JSV.suffix('/jsl/attach/SpaceMenuUser.DownloadAttachByUser', param.join('&'));
	
	if (JSV.browser.msie) {
		var DocFileViewerFrame = $('#DocFileViewerFrame');
		if (DocFileViewerFrame.length > 0) {
			DocFileViewerFrame.attr('src', url);
		} else {
			$('<iframe>').attr({id:'DocFileViewerFrame', width:0, height:0, frameborder:0, src:JSV.getLocationPath(url)}).appendTo('body');
		}
	} else {
		$('#mFrm').attr({'method':'POST', 'action':JSV.getLocationPath(url), 'target':'_self'}).submit();
	}
}
function doDownloadPDF(obj, popupName) {
	var param = [];
	param.push('id=' + obj.id);
	param.push(JSV.MODNAMES.SPACEID + '=' + JSV.getSpaceId());
	
	var url = JSV.suffix('/ext/pdfviewer/html/pdfViewer.jsp', param.join('&'));

	if (obj.target == 'popup') {
		window.open(JSV.getLocationPath(url), popupName + obj.id, obj.popupStyle);
	} else if (obj.target == 'clonePopup') {
		doClonePopup(obj, url, popupName + obj.id);
	} else {
		$('#mFrm').attr({'method':'POST', 'action':JSV.getLocationPath(url), 'target':obj.target}).submit();
	}
}
function doEditorMenu(obj, popupName) {
	var param = [];
	param.push('id=' + obj.id);
	param.push(JSV.MODNAMES.SPACEID + '=' + JSV.getSpaceId());
	
	var url = JSV.suffix('/space/menu/editorViewer.jsp', param.join('&'));
	
	if (obj.target == 'popup') {
		var u = JSV.getContextPath('/sys/jsv/doc/popup.jsp?titleText=<fmt:message key="doc.010"/>&targetUrl=' + url);
		window.open(u, 'PopUpViewer' , obj.popupStyle);
	} else if (obj.target == 'clonePopup') {
		doClonePopup(obj, url, popupName + obj.id);
	} else {
		$('#mFrm').attr({'method':'POST', 'action':JSV.getLocationPath(url), 'target':obj.target}).submit();
	}
}
function doClonePopup(obj, targetUrl, popupName) {
	var param = [];
	if (obj.menuId) {
		param.push('menuId=' + obj.menuId);
	}
	if (targetUrl != null) {
		param.push('targetMenuName=' + JSV.encode(obj.text));
		param.push('targetUrl=' + targetUrl);
	} else {
		param.push('spId=' + JSV.getSpaceId());
	}
	var url = JSV.suffix('/default.jsp?clonePopup=true', param.join('&'));
	window.open(JSV.getContextPath(url), popupName, obj.popupStyle);
}
function doOpenPopupPortal(obj, targetUrl, popupName) {
	var param = [];
	if (targetUrl != null) {
		param.push('targetMenuName=' + JSV.encode(obj.text));
		param.push('targetUrl=' + targetUrl);
	} else {
		param.push('spId=' + JSV.getSpaceId());
	}
	var url = JSV.suffix('/default.jsp?spId=' + obj.spId + '&openPopupPortal=true', param.join('&'));
	window.open(JSV.getContextPath(url), popupName, obj.popupStyle);
}
function doSearch() {
	var url = '<fmt:message key="srch.unity.url"/>';
	var qrArea = $('#QR');
	var keyword = qrArea.val();
	if (keyword.trim().length == 0) {
		JSV.alert('<fmt:message key="pub.029"/>');
		qrArea.val('').focus();
		return false;
	}
	url = JSV.suffix(url, 'QR='+JSV.encode(keyword));
	mViewer.doMenuClick({'id':'search', 'text':'<fmt:message key="menu.036"/>', 'type':MenuViewer.SearchMenu, 'href':url, 'target':'bottom', 'spId':JSV.getSpaceId()}, 'SearchMenu_');
	$(viewer.searchLayer).removeClass('on');
	return false;
}
function doSearchUser() {
	var qrArea = $('#QR');
	var keyword = qrArea.val();
	if (keyword.trim().length == 0) {
		JSV.alert('<fmt:message key="pub.029"/>', function() {
			qrArea.val('').focus();
		});
		return false;
	}
	
	var url = '/ekp/emp/usr.index.jsp?QR=' + JSV.encode(keyword);
	window.open(JSV.getContextPath(url), 'empSearch','height=700,width=1100,top=50,left=50,scrollbars=yes,location=no,resizable=yes');
	return false;
}
// Call ECM Menu
function doECM(obj, popupName) {
	var url = '/ecm/usr.main.jsp';
	var param = [];
	param.push(JSV.MODNAMES.SPACEID + '=' + JSV.getSpaceId());
	param.push(JSV.MODNAMES.MODULEID + '=' + obj.moduleId);
	param.push(JSV.MODNAMES.APPID + '=' + obj.appId)
	
	if (obj.target == 'popup') {
		window.open(JSV.getContextPath(url), popupName + obj.id, obj.popupStyle);
	} else if (obj.target == 'clonePopup') {
		doClonePopup(obj, JSV.suffix(url, param.join('&')), popupName + obj.id);
	} else {
		var mFrm = $('#mFrm');
		if (obj.origin) {
			url = obj.href;
		} else {
			url = JSV.suffix(url, param.join('&'));
		}
		mFrm.attr({'method':'POST', 'action':JSV.getContextPath(url), 'target':obj.target}).submit();
	}
}
//Call Profile Menu
function doProfile() {
	AnchorEmp.showDetail('<%=UserService.getUserId()%>');
}
function getMenuName(cid, isApp) {
	return viewer.getMenuName(cid, isApp);
}
function doDefault() {
	viewer.getMenuByIndex(0).doClick(true);
}
function doPortalInfo(width, height, url, name, feature, openWindow) {
	/** ************* 작은글꼴 기준 ************** */
	/** 보정 높이 */
	var correctHeight = 26;
	/** MenuBar의 높이 */
	var menubarHeight = 24;
	/** ToolBar의 높이 */
	var toolbarHeight = 26;
	/** 주소표시줄 높이 */
	var locationHeight = 29;
	/** ScrollBar의 폭 */
	var scrollbarWidth = 12;
	/** ************* 작은글꼴 기준 ************** */
	if (height > 700) {
		height = 700;
	}
	centerX = (screen.availWidth / 2) - (width + scrollbarWidth) / 2;
	centerY = (screen.availHeight / 2) - (height / 2);
	rightX = screen.availWidth - width;
	bottomY = screen.availHeight - (height + toolbarHeight + correctHeight);

	var left = centerX;
	var top = centerY;

	var name = (name) ? name : 'kcube_kms';
	var feature = (feature ? feature : 'menubar=no,toolbar=no,location=no,scrollbars=yes,resizable=yes') + ',width=' + width
			+ 'px,height=' + height + 'px,left=' + left + 'px,top=' + top + 'px';
	(openWindow) ? openWindow.open(url, name, feature) : self.open(url, name, feature);
}
function refreshCurrMdiPage() {
	if (mViewer.isUse) {
		var refresh = false;
		if (!isGnbSkin) {
			var homeMenu = mViewer.$menuInnerArea.find('.home_menu');
			if (homeMenu.hasClass('select')) {
				var data = JSV.clone(homeMenu.data('model'));
				data.isNew = true;
				mViewer.doMenuClickParam(data);
				refresh = true;
			}
		}
		
		if (!refresh) {
			if (mViewer.$fixedMenuList.find('li.select').length > 0) {
				var data = JSV.clone(mViewer.$fixedMenuList.find('li.select').data('model'));
				data.isNew = true;
				mViewer.doMenuClickParam(data);
			} else {
				var li = mViewer.$ul.find('li.select');
				var data = JSV.clone(li.data('model'));
				data.refreshTarget = li.attr('target');
				mViewer.doMenuClick(data);
			}
		}
	}
}
function changeFixedMenuType(type) {
	if (isGnbSkin) {
		var isChanged = fixedMenuStyleType != type;
		if (isChanged) {
			viewer.$portalGnb.removeClass(fixedMenuStyleType).addClass(type);
			isIconType = type == '<%=SpaceUserMenu.ICON_TYPE%>';
			fixedMenuStyleType = type;
			mViewer.$fixedMenuList.find('li:not(:first-child)').remove();
			mViewer.fixedmenus = [];
			mViewer.fixedUserMenu.setValue();
			mViewer.resize();
		}
	}
}
function changeMenuActiveType(isHover) {
	if (isGnbSkin) {
		useHoverMenu = isHover;
		viewer.setMegaMenuActiveHover();
	}
}
//for Alimi
var viewOn = true;
var updateList = null;
var configMap = getConfigMap();
var ECMConfigMap = getECMConfigMap();
var BuilderConfigMap = getBuilderConfigMap();
var updateOn = true;
var updateDialogOn = false;
var bookmarkOn = true;
var bookmarkDialogOn = false;
// for abbreviateString - (key: 아스키코드값 value: 조정값 *특수문자에 해당하는 값에 대해 fontSize 값을 조정해준다)
var charCodeFontSize = {
	37:0, 38:0, 64:0, 92:0,
	126:-1,
	35:-3, 60:-3, 62:-3, 94:-3,
	42:-4, 43:-4, 45:-4, 48:-4, 49:-4, 50:-4, 51:-4, 52:-4, 53:-4, 54:-4, 55:-4, 56:-4, 57:-4, 61:-4, 63:-4,
	36:-5, 91:-5, 93:-5, 95:-5, 123:-5, 124:-5, 125:-5,
	34:-6, 40:-6, 41:-6, 44:-6, 46:-6, 47:-6,
	33:-7, 39:-7, 58:-7, 59:-7, 96:-7
};
$('body').on('ready',function(){
	$('body').snowfall({deviceorientation : true, round : true, minSize: 1, maxSize:8,  flakeCount : 250});
});
</script>
</head>
<body style="overflow:hidden;">
	<div id="TopLayer" class="GNB ${isGnbSkin ? "" : skinType} ${skin}">
		<div id="NoticeFixedDiv" class="top_notice"></div>
		<div id="SearchLayer"></div>
		<c:choose>
			<c:when test="${isGnbSkin}">
			<%@ include file="/space/menu/skin/gnbHeaderMenu.jsp" %>
			</c:when>
			<c:otherwise>
			<%@ include file="/space/menu/skin/headerMenu.jsp" %>
			</c:otherwise>
		</c:choose>
		<form id="mFrm" style="margin:0px;padding:0px;display:none;"></form>
	</div>
	
	<div id="BottomLayer" class="${skin}Bottom"></div>
<%@ include file="/jspf/default-ext.jsp" %>
<%@ include file="/jspf/tail.jsp" %>