<%@ include file="/jspf/head.html.jsp"%>
<%@ page import="com.kcube.sys.login.LoginModule,com.kcube.sys.license.License,com.kcube.sys.tenant.TenantCache,com.kcube.sys.tenant.TenantService,com.kcube.sys.tenant.TenantPermission"%><%
	boolean isMobile = request.getParameter(LoginModule.MPARAM) != null && request.getParameter(LoginModule.MPARAM).equals("true"); 
	if (isMobile) {
		out.print("{\"mobileNotAuth\":true}");
		return;
	}
	License license = License.getLicense();
	String domain = request.getServerName();
	TenantPermission.checkValidTenant(domain);
	
	boolean useFindAccount = false;
//  인증센터 사용시..
	Long tenantId = (Long) request.getAttribute(LoginModule.TENANT);
// 	if(tenantId != null){
// 		int[] authInfo = com.kcube.sys.saml.sp.SamlService.getAuthInfo(tenantId);
// 		useFindAccount = (authInfo != null && authInfo[0] != com.kcube.sys.saml.sp.Saml.OTHER_LOGIN);
// 	}else{
// 		useFindAccount = true;
// 	}
%>
<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-type" content="text/html;charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=Edge"/>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title><fmt:message key="kcube.head.title"/> Login</title>
<link type="text/css" href="<%= request.getContextPath() %>/style.css?nocache=<%=com.kcube.sys.AppServlet.getNoCacheStr() %>" media="screen,print" rel="stylesheet">
<link href="<%= request.getContextPath() %>/pub/login/login.css" rel="stylesheet">
<script type="text/javascript" src="<%= request.getContextPath() %>/lib/com/kcube/jsv/jsv-utils_min.js"></script>
<script type="text/javascript">
	JSV.CONTEXT_PATH = "<%= request.getContextPath() %>";
	JSV.setLocale('<c:out value="${locale.language}"/>', '<c:out value="${defaultLocale.language}"/>');
</script>
<script type="text/javascript" src="<%= request.getContextPath() %>/script.language.ver.js?nocache=<%=com.kcube.sys.AppServlet.getNoCacheStr() %>"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/pub/login/login.js"></script>
<%@ include file="/ekp/emp/passwordconfig.jsp"%>
<script type="text/javascript">
var _parent = window;
var selfCall = _parent == window.top;
try {
	do {
		_parent = _parent.parent;
		if (!selfCall && _parent == window.top) {
			_parent.location.href = '<%= request.getContextPath() %>';
		}
	}
	while (!selfCall && _parent != window.top);
} catch(e) {
}
Date.prototype.getWeek = function (dowOffset) {
  /*getWeek() was developed by Nick Baicoianu at MeanFreePath: http://www.meanfreepath.com */
	dowOffset = typeof(dowOffset) == 'number' ? dowOffset : 0; //default dowOffset to zero
  	var newYear = new Date(this.getFullYear(),0,1);
	var day = newYear.getDay() - dowOffset; //the day of week the year begins on
	day = (day >= 0 ? day : day + 7);
	var daynum = Math.floor((this.getTime() - newYear.getTime() -
	(this.getTimezoneOffset()-newYear.getTimezoneOffset())*60000)/86400000) + 1;
	var weeknum;
	  //if the year starts before the middle of a week
	if(day < 4) {
		weeknum = Math.floor((daynum+day-1)/7) + 1;
	    if(weeknum > 52) {
	    	var nYear = new Date(this.getFullYear() + 1,0,1);
	    	var nday = nYear.getDay() - dowOffset;
	    	nday = nday >= 0 ? nday : nday + 7;
	      	/*if the next year starts before the middle of
	        	the week, it is week #1 of that year*/
	    	weeknum = nday < 4 ? 1 : 53;
	    }
	} else {
		weeknum = Math.floor((daynum+day-1)/7);
	}
	return weeknum;
};
JSV.Block(function() {
	var bgArr = JSV.loadJSON('/pub/login/login.json');
	var bgLeng = bgArr.length;
	var weekBg = new Date().getWeek(1) % bgLeng;
	var todayArr = bgArr[weekBg];
	var cType = todayArr.type;
	$('#loginArea').addClass('type' + cType);
	$('#bgType' + cType).show();
	$('#bgImg' + cType).css('background-image', 'url(<%= request.getContextPath() %>/pub/login/img/' + todayArr.img + ')');
	$('#typeAuthor' + cType).text(todayArr.author);
	var textArr = todayArr.text;
	var contents = $('#type' + todayArr.type + 'Contents');
	if (cType == 1) {
		for (var i = 0; i < textArr.length; i++) {
			$('<p>').addClass('text').append($('<span>').addClass('fade-up').text(textArr[i])).appendTo(contents);
		}
	} else {
		$('#imgTitle').html(todayArr.title + '<span id="imgYear" class="year">' + todayArr.year + '</span>');
		$('#imgYear').text(todayArr.year);
		$('#imgInfo').text(todayArr.info);
		
		for (var i = 0; i < textArr.length; i++) {
			if (i > 0)
				contents.append($('<br>'));
			contents.append(textArr[i]);
		}
	}
	
	f = $('form:first').get(0);
	doLoad();
	if (JSV.browser.chrome || JSV.browser.firefox || ISWIN10IE11) {
		window.onbeforeunload = doUnLoad();
	} else {
		$(window).on('unload', function(event) {
			doUnLoad();
		});
	}
	
	
	$('#loginpw').on({
		'mousedown':function(event)
		{
			doLoginpwcapslock(event)
		}
		,'keyup':function(event)
		{
			doLoginpwcapslock(event)
		}
		,'keydown': function(event)
		{
			doLoginpw(event)
		}
	});
	
	$('#submitImg').on('click', function(event) {
		doSubmit();
		return false;
	});
});
var ISWIN10IE11 = (JSV.userAgent.indexOf('trident') > 0 && JSV.appVersion.indexOf('rv:11') > 0);
var passwordLimitCnt = <%=getPasswordLimitCnt()%>;	
var cookieName = 'KCUBE_LOGINID_SAVED';
var tmpCookieName = 'KCUBE_LOGINID_IE11';
var login_error_key = 'com.kcube.login.error';
var login_invalid_error = 'com.kcube.login.error.invalid';
var login_password_error = 'com.kcube.login.error.password';
var login_password_wrong = 'com.kcube.login.error.passwordWrong';
var login_password_locked = 'com.kcube.login.error.locked';
var login_password_cnt = 'com.kcube.login.lockCnt';
var license_error_date = 'com.kcube.license.exprDateErr';
var license_error_user = 'com.kcube.license.maxUserErr';
var f;
var tenant;
function doUnLoad() {
	var loginCheckMsg = getCookie(login_error_key);
	if (ISWIN10IE11) {
		var tmpMsg = getCookie(tmpCookieName);
		if (loginCheckMsg) {
			setCookie(tmpCookieName, loginCheckMsg);
			removeCookie(login_error_key);
			location.href = location.href;
		} else if (tmpMsg) {
			checkLogin(tmpMsg, tmpCookieName);
		}
	} else {
		checkLogin(loginCheckMsg, login_error_key);
	}
}
function checkLogin(loginCheckMsg, cName) {
	if (loginCheckMsg == login_invalid_error) {
		JSV.alert('<fmt:message key="login.005"/>');
	} else if (loginCheckMsg == login_password_error) {
		JSV.alert('<fmt:message key="login.006"/>');
	} else if (loginCheckMsg == login_password_wrong) {
		JSV.alert('<fmt:message key="login.020"/>' + getCookie(login_password_cnt) + '<fmt:message key="login.021"/>' + passwordLimitCnt + '<fmt:message key="login.022"/>');
	} else if (loginCheckMsg == login_password_locked) {
		var txt = JSV.getLocale() == 'en' || JSV.getLocale() == 'in' ? '<fmt:message key="login.023"/>' : '<fmt:message key="login.020"/>' + passwordLimitCnt + '<fmt:message key="login.023"/>';
		JSV.alert(txt);
	} else if (loginCheckMsg == license_error_date) {
		JSV.alert('<fmt:message key="login.063"/>');
	} else if (loginCheckMsg == license_error_user) {
		JSV.alert('<fmt:message key="login.064"/>');
	}
	removeCookie(cName);
}
function doLoad() {
	var loginidCookie = getCookie(cookieName);
	if (loginidCookie != null && loginidCookie != '') {
		focusId();
		f.loginid.value = loginidCookie;
		f.idcookie.checked = 1;
		$('#selTenant').val($.trim(getCookie('TENANT')));
		focusPw();
	} else {
		focusId();
		f.loginid.value = '';
		f.idcookie.checked = 0;
	}
}

function doLoginpwcapslock(event){
 	if (event.originalEvent.getModifierState('CapsLock'))
	{
 		$('.ntcCapsLock').text('capslock on!!!').css({'color':'red','font-size':'15px'});
	}else{
		$('.ntcCapsLock').text('');
	} 
}



function doLoginpw(event) {
	if (event.keyCode == 13) {
		doSubmit();
		event.preventDefault();
		event.stopPropagation();
	}
}
function focusId() {
	document.getElementById('loginid').focus();
}
function focusPw() {
	document.getElementById('loginpw').focus();
}
function doSubmit() {
	var lid = document.getElementById('loginid').value;
	var lpw = document.getElementById('loginpw').value;
	if (lid == null || lid == '') {
		JSV.alert('<fmt:message key="login.007"/>', function() {
			focusId();
		});
	} else if (lpw == null || lpw == '') {
		JSV.alert('<fmt:message key="login.008"/>', function() {
			focusPw();
		});
	} else {
		var url = '<%= request.getContextPath() %>/pub/login/usr.findtenant.jsp';
		$.ajax({'url':url,
				'type':'POST',
				'dataType':'json',
				'async':false,
				'xhrFields':{withCredentials: true},
				'data': {'loginid' : lid, 'loginpw' : lpw},
				'success':function(data, status) {
					if (data && data.array) {
						if (data.array.length > 1) {
							f.action = '<%= request.getContextPath() %>/pub/login/usr.selectTenant.jsp';
							f.submit();
						} else {
							if (data.array[0]) {
								$('#selTenant').val(data.array[0].tenant);
								setLoginidCookie();
								f.submit();
							} else {
								JSV.alert('<fmt:message key="login.005"/>');
							}
						}
					} else {
						JSV.alert('<fmt:message key="login.005"/>');
					}
				},
				'error':function(xhr) {
					if (JSV.browser.msie) {
						window.status = 'json error: ' + url;
					} else {
						JSV.consoleLog('JSON load Error :'+url);
					}
				}
		});
	}
}
function popUpfindAccount() {
	var sfeature = 'width=550,height=650,scrollbars=no,status=no,toolbar=no,menubar=no,location=no,resizable=no';
	var url = '<%= request.getContextPath() %>/pub/authentication/findAccount/usr.find.account.jsp';
	<%if(tenantId != null){%>
		url = JSV.suffix(url, '?tenantId=<%=tenantId%>');
	<%}%>
	window.open(url, 'popUpfindAccount', sfeature);
}
</script>
<link type="image/x-icon" rel="shortcut icon" href="<%=request.getContextPath()%>/kcube.ico">
<link type="image/x-icon" rel="icon" href="<%=request.getContextPath()%>/kcube.ico">
</head>
<body>
	<div id="loginArea" class="login">
		<div class="login_area">
			<span class="pd_top"></span>
			<h1 class="logo"></h1>
			<span class="pd_bottom"></span>
			<form name="f" method="post" action="" class="form">
				<fieldset>
					<legend>Login</legend>
					<input type="hidden" name="tenant" id="selTenant"/>
					<div class="id_loc">
						<div class="input_area">
							<label for="loginid">ID</label>
							<input type="text" id="loginid" name="loginid" placeholder="Enter your ID">
							<span class="fade-up"></span>
						</div>
					</div>
					<div class="pw_loc">
						<div class="input_area">
							<label for="loginpw">Password</label>
							<input type="password" id="loginpw" name="loginpw" placeholder="Enter your password" >
							<span class="fade-up"></span>
						</div>
						<span class="ntcCapsLock"></span>
					</div>
					<div class="remember_loc">
						<div class="check">
							<input type="checkbox" name="idcookie" id="idcookie" class="check_input">
							<label for="idcookie" class="check_label">
								<span class="check_icon">
									<div class="circle"><span class="color"></span></div>
									<div class="chk">
										<svg xmlns="http://www.w3.org/2000/svg" width="19" height="19" viewBox="0 0 19 19">
										    <g id="done" transform="translate(-45 -388)">
										        <path d="M9 16.2L6.8 14l-1.4 1.4L9 19l7-7-1.4-1.4z" transform="translate(44 383)"></path>
										    </g>
										</svg>
									</div>
								</span>
								<p class="text">Remember me</p>
							</label>
						</div>
						<%if (useFindAccount) {%>
							<div class="pw_find">
								<a hidefocus="true" class="txt" onclick="javascript:popUpfindAccount();"><fmt:message key="login.042"/></a>
							</div>
						<%}%>
					</div>
					<a href="javascript:void(0);" id="submitImg" class="btn_login"><span class="text">Login</span></a>
				</fieldset>
			</form>
			<div class="footer">
				<p class="copyright"><fmt:message key="kcube.copyright"/></p>
			</div>
		</div>
		<div id="bgType1" class="login_visual" style="display:none">
			<div class="visual_img">
				<span class="filter"></span>
				<span class="bg" id="bgImg1"></span>
			</div>
			<div class="visual_contents">
				<div class="content_table_area">
					<div class="content_table">
						<div class="row">
							<div class="cell visual_header fade-in">
								<div class="inner">
									<p class="text">The one sentence<br>that can change your life</p>
									<img src="<%= request.getContextPath() %>/pub/login/img/deco_img.png" alt="">
								</div>
							</div>
						</div>
						<div class="row">
							<div class="cell visual_text">
								<div class="inner">
									<div class="type" style="display:block;">
										<div class="text_tbl">
											<div class="cell text_cell">
												<div id="type1Contents"></div>
												<span class="line fade-in"></span>
											</div>
											<div class="cell by_cell">
												<p id="typeAuthor1" class="by fade-in"></p>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="row">
							<div class="cell visual_footer fade-in">
								<p class="text"><fmt:message key="kcube.loginPage.Text"/></p>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div id="bgType2" class="login_visual" style="display:none">
			<div class="visual_img">
				<span class="filter"></span>
				<span id="bgImg2" class="bg scale-up-down"></span>
			</div>
			<div class="visual_contents">
				<div class="type">
					<div class="mp_info">
						<p class="title"><span id="imgTitle" class="fade-up"></span></p>
						<p class="info"><span id="imgInfo" class="fade-up"></span></p>
						<p id="typeAuthor2" class="by fade-in fade-up"></p>
					</div>
					<div class="message fade-in">
						<p id="type2Contents" class="text"></p>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>