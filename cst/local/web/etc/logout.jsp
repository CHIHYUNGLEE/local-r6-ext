<%@page import="com.kcube.sys.usr.UserService"%><%@page import="com.kcube.sys.login.LoginModule"%><%@page import="com.kcube.sys.saml.sp.SamlService"%><%
	String logoutUrl = SamlService.getLogoutUrl();
	Long tenantId = UserService.getTenantId();
	String loginId = UserService.getLoginId();
	session.removeAttribute(LoginModule.SESSION);
	session.invalidate();
	String url = logoutUrl == null ? request.getParameter("url") : logoutUrl;

	if ("SAML_LOGOUT".equals(url))
	{
		SamlService.samlLogout(request, response, tenantId, loginId);
	} else {
%>
		<script>
			window.location.href = '<%=url%>';
		</script>
<%		
	}
%>