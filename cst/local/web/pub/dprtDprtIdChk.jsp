<%@ include file="/jspf/head.jsp" %>
<%@page import="com.kcube.lib.sql.SqlUpdate"%>
<%
	SqlUpdate upt = new SqlUpdate("sync_user");
	upt.setInt("sync_flag", 0);
	upt.where("syncid = 271846739");
	upt.execute();
%>
<%@ include file="/jspf/body.jsp" %>
<%@ include file="/jspf/tail.jsp" %>