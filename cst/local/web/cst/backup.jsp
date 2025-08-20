<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@page import="com.kcube.cst.local.CheckKMSDBFile"%>
<%
try{
  	CheckKMSDBFile chk = new CheckKMSDBFile("jdbc:oracle:thin:@localhost:1521:xe","localid","localpw");
	chk.backUp();
}catch(Exception e){
	e.printStackTrace();
}
%>