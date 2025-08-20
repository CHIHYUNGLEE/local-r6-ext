<%@ page buffer="none" errorPage="/etc/err/error.jsp"
%><%@ page import="com.kcube.sys.i18n.I18NService,com.kcube.lib.http.JslService,com.kcube.sys.module.ModuleParam,com.kcube.sys.deploy.DeployService"
%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"
%><%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"
%><%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"
%><%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"
%><%@ taglib uri="/WEB-INF/p.tld" prefix="p"
%><%@ taglib uri="/WEB-INF/k.tld" prefix="k"
%><%@ taglib uri="/WEB-INF/kfmt.tld" prefix="kfmt"
%><%
	I18NService.jstlLocale(request);
	JslService.setSessionKey(request);
	
	ModuleParam moduleParam = null;
	String rModuleId = request.getParameter(ModuleParam.MODULEID);
	if (rModuleId != null)
	{
		moduleParam = new ModuleParam(request);
		request.setAttribute("moduleParam", moduleParam);
	}
	String className = null;
	String rClassId = request.getParameter(ModuleParam.CLASSID);
	if (rClassId != null && !"0".equals(rClassId))
	{
		try {
			className = DeployService.getConfig(Long.valueOf(rClassId)).getClassName();
			request.setAttribute("className", className);
		} catch (Exception e) {}
	}
%><fmt:requestEncoding value="utf-8"
/><fmt:setLocale value="${locale}"
/><fmt:setBundle basename="Resource"
/><c:if test="${className != null}"><kfmt:base locale="${locale}"  prefix="${className}"
/></c:if>