<%@ include file="/jspf/head.jsp" %>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="/sys/jsv/diff/attachmentParseHtml.jsp" %>
<%
	request.setAttribute("oldTitle", "dd");
	request.setAttribute("newTitle", "ddd");
	request.setAttribute("oldContent", "aaa");
	request.setAttribute("newContent", "aaaa");
%>
<style type="text/css">
.compare {padding:0px !important;}
</style>
<link href="<%=request.getContextPath()%>/sys/jsv/diff/diff.css" rel="stylesheet" type="text/css">
<div class="compare">
	<c:set var="oldVer">
		<fmt:message key="doc.001"/> : <c:out value="${oldTitle}"/>
		<c:out value="${oldContent}" escapeXml="false"/>
	</c:set>
	<c:set var="newVer">
		<fmt:message key="doc.001"/> : <c:out value="${newTitle}"/>
		<c:out value="${newContent}" escapeXml="false"/>
	</c:set>
	<k:diff origin="${oldVer}" revise="${newVer}"/>
</div>