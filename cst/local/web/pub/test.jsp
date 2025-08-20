<%@page import="com.kcube.sys.emp.EmployeeService"%>
<%@page import="com.kcube.lib.sql.SqlUpdate"%>
<%@page import="com.kcube.lib.secure.EncryptUtils"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="com.kcube.lib.sql.SqlSelect"%>
<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%--
버전 업그레이드시 로그인아디와 솔트 이용해서 비번 한꺼번에 넣어주는 jsp
--%>
<%!

public static class Test{
	//솔트 넣어줄 사람 select
	public static SqlSelect saltSelect(){
		SqlSelect salt = new SqlSelect();
		salt.select("login_id, login_pswd, pswd_salt");
		salt.from("hr_user");
		salt.where("status=0");
		
		return salt;
	}
	
	//솔트 없는 사람 넣어주고 로그인아디로 비번도 넣어주기!!
	public static void saltInsert(){
		try {
			ResultSet rs = saltSelect().query();
			while(rs.next()){
				String generatedSalt = EncryptUtils.generateRandomSalt();

				SqlUpdate sql = new SqlUpdate("hr_user");
				sql.setString(
					"login_pswd",EmployeeService.encrypt(rs.getString(1), generatedSalt));
				sql.setString("pswd_salt", generatedSalt);
				sql.where("status=0");
				sql.where("login_id = ?", rs.getString(1));
				int result = sql.execute();
				System.out.println(result);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}

%>

<%

Test.saltInsert();%>
