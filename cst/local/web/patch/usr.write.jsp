<%@ include file="/sys/jsv/template/template.head.jsp" %>
<%@ include file="/patch/config.jsp" %>
<style type="text/css">
</style>
<script type="text/javascript">
JSV.Block(function () {
    var template = '<template color="green" catalog="/patch/catalog.xml.jsp">\
		<header label="<kfmt:message key="patch.001"/>"/>\
		<fields columns="100pxx,60p,,100px," type="write" class="Array">\
		    <field property="version.write"/>\
		    <field property="mode.write"/>\
			<field property="path.write"/>\
			<field property="id.write" />\
			<field property="pswd.write"/>\
			<field property="option.write"/>\
			<field property="date.write"/>\
			<field property="revision.write"/>\
			<field property="fullHistory.write"/>\
			<field property="history.write"/>\
		</fields>\
	 </template>';

	 var t = new ItemTemplate(document.getElementById('main'), template);
	 //t.setValue('/jsl/PatchItemUser.PreWrite.json');
	 $('.iLwriteTopTd.ContentViewer_parent').attr('rowspan',2);
 	 var version = t.getChild('version');
	 var mode = t.getChild('mode');
	 var path = t.getChild('path');
	 var id = t.getChild('id');
	 var pswd = t.getChild('pswd');
	 var option = t.getChild('option');
     var date = t.getChild('date'); 
     var revision = t.getChild('revision'); 
     var fullHistory = t.getChild('fullHistory'); 
     var history = t.getChild('history'); 
     
	 var register = new KButton(t.layout.mainHeadRight, <kfmt:message key="btn.patch.001"/>);
	 register.onclick = function() {
		 var validateStr = 'path';
		 if(option.getValue() == 1 ) {
			 validateStr= 'path,date,id,pswd';
		 }else{
			 validateStr= 'path,revision,id,pswd';
		 }
		 
		 if (t.validate(validateStr)) {
			$.ajax({
				url : JSV.getContextPath(JSV.getModuleUrl('/jsl/PatchItemUser.PatchDownload.jsl')),
				type : 'POST',
				dataType : 'json',
				data : {'revision' : revision.getValue(),'path' : path.getValue(),'id' : id.getValue(),'pswd' : pswd.getValue()},
				success : function(data, status){
				},
				error : function(xhr){
					console.log("error!!!!");
				}
			});
		 }
	 }
	 //버전에 따른 모드 활성화
	 var modeChange = function (value) {
		 if(value == 1) {
			 path.setValue('<%=getDefaultR4Url(moduleParam)%>');
			 ItemViewer.hideTR(mode.widget);
	     }else if(value == 2){
	    	 path.setValue('<%=getDefaultR5Url(moduleParam)%>');
	    	 ItemViewer.showTR(mode.widget);
		 }else{
			 path.setValue('<%=getDefaultR6Url(moduleParam)%>');
			 ItemViewer.showTR(mode.widget);
		 }
	 }
	 modeChange(1);
     
     version.onchange = function(value){
    	 modeChange(value);
     }
     
     //옵션에 따른 기준 활성화
	 var optChange = function (value) {
		 if(value == 1) {
			 ItemViewer.hideTR(revision.widget.get(0));
			 ItemViewer.showTR(date.t);
		 } else {
			 ItemViewer.hideTR(date.t);
	    	 ItemViewer.showTR(revision.widget.get(0));
		 }
	 }
	 optChange(1);
     
	 option.onchange = function(value){
    	 optChange(value);
     }
	 
	 var fullBtn = new KButton(t.layout.mainHeadRight, <kfmt:message key="btn.patch.002"/>);
	 fullBtn.onclick = function(value){
		 var validateStr = 'path,id,pswd';
		 
		 if (t.validate(validateStr)) {
			$.ajax({
				url : JSV.getContextPath('/jsl/PatchItemUser.showFullHistory.json'),
				type : 'POST',
				dataType : 'json',
				data : {'path' : path.getValue(),'id' : id.getValue(),'pswd' : pswd.getValue()},
				success : function(data, status){
					var t = "";
					console.log(data);
					if(data.error != undefined && data.error.indexOf('E170001') != -1){
						alert("WRONG SVN ID OR PASSWORD!");
					}else if(data.error != undefined && data.error.indexOf('E160013') != -1){
						alert("WRONG SVN PATH!");				
					}else{
						if(data.array.length > 0){
							for(var i = 0 ; i<data.array.length;i++ ){
								t += "<p>"+"-------------------------------------------------------------------"+"</p>";
								t += "<p>" +data.array[i].info+"</p>";
								t += "<p>" +data.array[i].comment+"</p>";
								t += "<p>" +"changed Path : "+data.array[i].changedPath+"</p>";
							}
						}  
						fullHistory.setValue(t);
					}
				},
				error : function(xhr){
					console.log("error!!!!");
				}
			});
		 }
	 } 
	 
	 var spcfBtn = new KButton(t.layout.mainHeadRight, <kfmt:message key="btn.patch.003"/>);
	 spcfBtn.onclick = function(value){
		 var validateStr = 'path';
		 if(option.getValue() == 1 ) {
			 validateStr= 'path,date,id,pswd';
		 }else{
			 validateStr= 'path,revision,id,pswd';
		 }
		 if (t.validate(validateStr)) {
			$.ajax({
				url : JSV.getContextPath('/jsl/PatchItemUser.showChoosedHistory.json'),
				type : 'POST',
				dataType : 'json',
				data : {'path' : path.getValue(), 'revision': revision.getValue(),'id' : id.getValue(),'pswd' : pswd.getValue()},
				success : function(data, status){
					var t = "";
					if(data.error != undefined && data.error.indexOf('E170001') != -1){
						alert("WRONG SVN ID OR PASSWORD!");
						t += "svn: E160013: No files found";
					}else if(data.error != undefined && data.error.indexOf('E160013') != -1){
						alert("WRONG SVN PATH!");
						t += "svn: E160013: No files found";					
					}else{
						if(data.array.length > 0){
							for(var i = 0 ; i<data.array.length;i++ ){
								t += "<p>"+"-------------------------------------------------------------------"+"</p>";
								t += "<p>" +data.array[i].info+"</p>";
								t += "<p>" +data.array[i].comment+"</p>";
								t += "<p>" +"changed Path : "+data.array[i].changedPath+"</p>";
							}
						}
					}
					history.setValue(t);
				},
				error : function(xhr){
					console.log("error!!!!");
				}
			});
		 }
	 }
});
</script>
<%@ include file="/sys/jsv/template/template.body.jsp" %>
<div id="main"></div>
<%@ include file="/sys/jsv/template/template.tail.jsp" %>