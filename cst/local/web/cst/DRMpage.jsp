<%@ include file="/sys/jsv/template/template.head.jsp" %>
<%@page import="com.kcube.cst.local.CheckKMSDBFile"%>
<%
String url = request.getParameter("url");
String userId = request.getParameter("userId");
String userPw = request.getParameter("userPw");
String table = request.getParameter("table");
String source = request.getParameter("source");
String e1 = null;
if(url != null){
	try{
		//DB와 실제파일 동기화
	  	CheckKMSDBFile chk = new CheckKMSDBFile(url,userId,userPw);
		if(table != ""){
			chk.start(table,source);
		}else{
			chk.start(source);
		} 
	}catch(Exception e){
		e.printStackTrace();
		e1 = e.getMessage();
	}
}
%>
<script type="text/javascript">
JSV.Block(function () {
    var template = '<template color="green">\
		<header label="DRM"/>"/>\
		<fields columns="120px,60p,,100px," type="write" class="Array">\
		    <field component="TextFieldEditor" id="url">\
		        <header label="url" desc="ex)jdbc:oracle:thin:@125.140.114.12:1521:orcl" descWidth="260px" required="true"/>\
		        <style attribute="url" />\
		    </field>\
			<field component="TextFieldEditor" id="userId">\
		        <header label="userId" required="true"/>\
		        <style attribute="userId" />\
		    </field>\
			<field component="TextFieldEditor" id="userPw">\
		        <header label="userPw" required="true"/>\
		        <style attribute="userPw" />\
		    </field>\
			<field component="TextFieldEditor" id="source">\
	        	<header label="source" desc="ex)C:/drm/kcube-repository" required="true"/>\
	        	<style attribute="source" />\
	    	</field>\
			<field component="TextFieldEditor" id="table">\
	        	<header label="table" />\
	        	<style attribute="table" />\
	    	</field>\
		</fields>\
	 </template>';
	 
    var t = new ItemTemplate(document.getElementById('main'), template);
    var url = t.getChild('url');
	var userId = t.getChild('userId');
	var userPw = t.getChild('userPw');
	var source = t.getChild('source');
	var table = t.getChild('table');
	
	var register = new KButton(t.layout.mainFootRight, {'text':'start'});
	register.onclick = function() {
		if(url.getValue().trim() != '' && userId.getValue().trim() != '' && userPw.getValue().trim() != '' && source.getValue().trim() != ''){
			$.ajax({
				url : 'DRMpage.jsp',
				type : 'POST',
				dataType : 'json',
				data : {'url' : url.getValue(),'userId' : userId.getValue(),'userPw' : userPw.getValue(),'table' : table.getValue(),'source' : source.getValue()},
				success : function(response, data, status){
					console.log("success!!");
				},
				error : function(xhr){
					console.log("error!!");
				}
			}); 
		}else{
			alert("Please fill in the empty space");
		}
	} 
});
</script>
<%@ include file="/sys/jsv/template/template.body.jsp" %>
<div id="main"></div>
<%@ include file="/sys/jsv/template/template.tail.jsp" %>