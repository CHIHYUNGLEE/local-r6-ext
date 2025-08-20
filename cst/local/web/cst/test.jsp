<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@page import="com.kcube.sys.module.app.ModuleAppService"%>
<%@page import="com.kcube.sys.usr.UserXidManager"%>
<%
//ModuleAppService.getAdmins(3901L);
UserXidManager xid = new UserXidManager();
xid.resetTenant(1000L);
%>