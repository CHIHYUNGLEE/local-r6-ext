<%@ include file="/jspf/head.xml.jsp" %><%
	request.setAttribute("gcopEnabled", com.kcube.sys.PlusAppService.checkLicense(com.kcube.sys.PlusAppBoot.SYSTEM_GCOP));
%><%!
	private static com.kcube.space.SpaceConfig _spaceConfig = (com.kcube.space.SpaceConfig) com.kcube.sys.conf.ConfigService.getConfig(com.kcube.space.SpaceConfig.class);
	
	private static boolean isUseWorkPortal()
	{
		return _spaceConfig.isUseWorkPortal();
	}
	
	private static boolean isUseTeamPortal()
	{
		return _spaceConfig.isUseTeamPortal();
	}
%>
<%@ page import="com.kcube.sys.usr.UserPermission" %>
<list class="Array">
	<n id="1" url="/sys/conf/adm/configInit.jsp"><fmt:message key="conf.001"/></n>
	<% if(UserPermission.isAdmin() && UserPermission.isLocationAllowed()){ %>
	
	<n id="100" pid="1" url="/sys/conf/adm/MailConfig.xml.jsp"><fmt:message key="conf.002"/></n>
	
	<fmt:message key="blog.Enabled" var="blogEnabled"/>
	<c:if test="${blogEnabled}">
	<n id="7000" pid="100"><fmt:message key="blog.001"/></n>
	<n id="7001" pid="7000" url="/sys/conf/adm/MailConfigDetail.xml.jsp?confid=com.kcube.blog.BlogNotify.apprOwner"><fmt:message key="conf.blog.002"/></n>
	<n id="7002" pid="7000" url="/sys/conf/adm/MailConfigDetail.xml.jsp?confid=com.kcube.blog.BlogNotify.rjctOwner"><fmt:message key="conf.blog.003"/></n>
	<n id="7003" pid="7000" url="/sys/conf/adm/MailConfigDetail.xml.jsp?confid=com.kcube.blog.BlogNotify.pauseOwner"><fmt:message key="conf.blog.004"/></n>
	<n id="7004" pid="7000" url="/sys/conf/adm/MailConfigDetail.xml.jsp?confid=com.kcube.blog.BlogNotify.createOwner"><fmt:message key="conf.blog.005"/></n>
	<n id="7005" pid="7000" url="/sys/conf/adm/MailConfigDetail.xml.jsp?confid=com.kcube.blog.BlogNotify.applyAdmin"><fmt:message key="conf.blog.006"/></n>
	<n id="7006" pid="7000" url="/sys/conf/adm/MailConfigDetail.xml.jsp?confid=com.kcube.blog.qna.QnaItemNotify.question"><fmt:message key="conf.blog.007"/></n>
	<n id="7007" pid="7000" url="/sys/conf/adm/MailConfigDetail.xml.jsp?confid=com.kcube.blog.qna.QnaItemNotify.answer"><fmt:message key="conf.blog.008"/></n>
	</c:if>
	
	<c:if test="${blogEnabled}">
	<n id="13900" pid="1900">Blog</n>
	<n id="13901" pid="13900" url="/sys/conf/adm/AlimiConfigDetail.xml.jsp?confid=com.kcube.blog.BlogNotify.appr"><fmt:message key="conf.blog.009"/></n>
	<n id="13902" pid="13900" url="/sys/conf/adm/AlimiConfigDetail.xml.jsp?confid=com.kcube.blog.BlogNotify.rjct"><fmt:message key="conf.blog.010"/></n>
	<n id="13903" pid="13900" url="/sys/conf/adm/AlimiConfigDetail.xml.jsp?confid=com.kcube.blog.BlogNotify.pause"><fmt:message key="conf.blog.011"/></n>
	<n id="13904" pid="13900" url="/sys/conf/adm/AlimiConfigDetail.xml.jsp?confid=com.kcube.blog.BlogNotify.create"><fmt:message key="conf.blog.012"/></n>
	<n id="13905" pid="13900" url="/sys/conf/adm/AlimiConfigDetail.xml.jsp?confid=com.kcube.blog.BlogNotify.aplyAdm"><fmt:message key="conf.blog.013"/></n>
	<n id="13906" pid="13900" url="/sys/conf/adm/AlimiConfigDetail.xml.jsp?confid=com.kcube.blog.BlogNotify.sympathy"><fmt:message key="conf.blog.014"/></n>
	<n id="13908" pid="13900" url="/sys/conf/adm/AlimiConfigDetail.xml.jsp?confid=com.kcube.blog.qna.QnaItemNotify.qstn"><fmt:message key="conf.blog.015"/></n>
	<n id="13909" pid="13900" url="/sys/conf/adm/AlimiConfigDetail.xml.jsp?confid=com.kcube.blog.qna.QnaItemNotify.answer"><fmt:message key="conf.blog.016"/></n>
	<n id="13910" pid="13900" url="/sys/conf/adm/AlimiConfigDetail.xml.jsp?confid=com.kcube.blog.qna.QnaItemNotify.sympathyQna"><fmt:message key="conf.blog.017"/></n>
	</c:if>
	
	<n id="101" pid="1" url="/sys/conf/adm/system.config.xml.jsp"><fmt:message key="conf.040"/></n>
	<n id="1011" pid="101" url="/sys/conf/adm/stat.config.xml.jsp"><fmt:message key="conf.451"/></n>
	<n id="102" pid="1" url="/sys/conf/adm/custom.config.xml.jsp"><fmt:message key="conf.056"/></n>
	<n id="103" pid="1" url="/sys/conf/adm/document.config.xml.jsp"><fmt:message key="conf.075"/></n>
	<n id="104" pid="1" url="/ekp/emp/adm.config.xml.jsp"><fmt:message key="conf.013"/></n>
	<% if(UserPermission.isSystem()){ %>
		<n id="108" pid="1" url="/ekp/emp/password.config.xml.jsp"><fmt:message key="login.024"/></n>
	<% } %>
	<n id="105" pid="1" url="/sys/map/adm/adm.config.xml.jsp"><fmt:message key="conf.081"/></n>
	<n id="106" pid="1" url="/sys/conf/adm/attachment.config.xml.jsp"><fmt:message key="conf.025"/></n>
	<n id="107" pid="1"><fmt:message key="conf.464"/></n>
	<n id="9107" pid="107" url="/sys/conf/adm/image.config.xml.jsp"><fmt:message key="conf.041"/></n>
	<n id="9108" pid="107" url="/sys/conf/adm/thumb.config.xml.jsp"><fmt:message key="conf.457"/></n>
	<n id="109" pid="1" url="/sys/conf/adm/notify.config.xml.jsp"><fmt:message key="conf.261"/></n>
	
	<n id="113" pid="1" url="/ekp/ntc/adm.config.xml.jsp"><fmt:message key="conf.080"/></n>
	<n id="114" pid="1" url="/ekp/vitae/adm.config.xml.jsp"><fmt:message key="conf.394"/></n>
	<n id="EXPERT" pid="114" url="/ekp/vitae/adm.axisConfig.jsp"><fmt:message key="axis.064"/></n>
	<n id="115" pid="1" url="/sys/module/adm/adm.config.xml.jsp"><fmt:message key="conf.419"/></n>
	
	<n id="1000" pid="1" url="/space/portal/adm/adm.config.xml.jsp"><fmt:message key="conf.263"/></n>
	<n id="1001" pid="1000" url="/space/manage/conf.adminMenu.jsp?mode=libraryAdmin"><fmt:message key="conf.291"/></n>
	<% if (isUseWorkPortal()) {%>
	<n id="1002" pid="1000" url="/space/manage/conf.adminMenu.jsp?mode=workAdmin"><fmt:message key="conf.264"/></n>
	<n id="1003" pid="1000" url="/space/manage/conf.adminMenu.jsp?mode=workTemplateAdmin"><fmt:message key="conf.265"/></n>
	<%} %>
	<% if (isUseTeamPortal()) {%>
	<n id="1004" pid="1000" url="/space/manage/conf.adminMenu.jsp?mode=teamAdmin"><fmt:message key="conf.266"/></n>
	<n id="1005" pid="1000" url="/space/manage/conf.adminMenu.jsp?mode=teamTemplateAdmin"><fmt:message key="conf.267"/></n>
	<n id="1006" pid="1000" url="/sys/conf/adm/TeamConfig.xml.jsp"><fmt:message key="conf.448"/></n>
	<%} %>
	
	<c:if test="${blogEnabled}">
	<n id="2000" pid="1"><fmt:message key="blog.menu.030"/></n>
	<n id="2001" pid="2000" url="/sys/conf/adm/blog.config.xml.jsp"><fmt:message key="blog.conf.001"/></n>
	<n id="2002" pid="2000" url="/sys/conf/adm/blog.home.config.xml.jsp"><fmt:message key="blog.conf.003"/></n>
	<n id="2003" pid="2000" url="/blog/manage/menu/conf.defaultMenu.jsp"><fmt:message key="blog.conf.004"/></n>
	</c:if>
	
	<% if (com.kcube.sys.license.LicenseService.isAuthorized(com.kcube.sys.PlusAppBoot.SYSTEM_ECM)) { %>
		<n id="5000" pid="1" url="/ecm/conf/conf.doc.xml.jsp"><fmt:message key="ecm.admin.021"/></n> 
	<% } %>
	<n id="8101" pid="1" url="/sys/conf/adm/sync.config.xml.jsp"><fmt:message key="conf.017"/></n>
	<%-- Interface Config --%>
	<n id="INTERFACE" pid="1" url="/cst/adm.config.xml.jsp">Interface Config</n>
<% if (com.kcube.ext.conf.CustomService.hasAdditionConfigurationMenuURL()){ %>
<c:catch>
<jsp:include page="<%=com.kcube.ext.conf.CustomService.getAdditionConfigurationMenuURL() %>" flush="false"/>
</c:catch>
<% if (com.kcube.sys.license.License.getLicense().isManage() && UserPermission.isSystem()) { %>
	<n id="9999" pid="1" url="/sys/conf/system/configSetting.xml.jsp"><fmt:message key="conf.512"/></n>
<% } %>
<% } %>
<% } %>
</list>
<%@ include file="/jspf/tail.xml.jsp" %>
