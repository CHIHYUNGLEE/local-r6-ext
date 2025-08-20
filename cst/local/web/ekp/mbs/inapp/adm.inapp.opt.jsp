<%@ include file="/jspf/head.portlet.jsp" %>
<%@ include file="/ekp/mbs/inapp/js/incl.opt.jsp" %>
<%@ include file="/ekp/mbs/inapp/config.jsp" %>
<%@page import="com.kcube.ekp.mbs.inapp.MbInApp"%>
<style>
	#main${PORTLET_ID} .ItemLayout .itemLayoutDiv3{display:block; padding-top:30px; padding-bottom:0px;}
	#main${PORTLET_ID} .ItemLayout .folderCmnt{color: #ff4545;padding-bottom:10px;}
	#main${PORTLET_ID} .TemplateLayoutFrame .TemplateLayoutEtc{padding-bottom:10px;}
</style>
<script type="text/javascript">
JSV.Block(function () {
	var model = JSV.loadJSON(JSV.getModuleUrl('/jsl/MbInAppAdmin.PreWrite.json'), JSV.getParameter('id') ? 'id='+JSV.getParameter('id') : '');
	if(!model.id){
		model.appType = treeFrame.makeType;
	}
	var template = '<template color="green" catalog="/ekp/mbs/inapp/catalog.xml.jsp">\
		<fields columns=",," type="write" class="Array">\
			<field property="name.write"/>\
			<field property="visible.write"/>';
		if(model.appType == <%=MbInApp.APP_FOLDER%> || model.appType == <%=MbInApp.APP_URL%>){
			if(model.appType == <%=MbInApp.APP_URL%>){
				template += '<field property="appType.read"/>\
							 <field property="url.write"/>\
							 <field property="popUp.write"/>\
							 <field property="popUpStyle.write"/>';
			}else{
				template += '<field property="appType.hidden"/>';
			}
		}else{
			template += '<field property="scrt.write"/>\
						 <field property="appType.write"/>\
						 <field property="reserve.write"/>\
						 <field property="exprPeriod.write"/>\
						 <field property="feedOpt.write"/>\
						 <field property="anonyOpt.write"/>\
						 <%--<field property="process.write"/>--%>\
						 <field property="notify.write"/>\
						 <field property="fileLimit.write"/>\
						 <field property="limitExt.write"/>\
						 <field property="introduce.write"/>\
						 <field property="showManager.write"/>\
						 <field property="intro.write"/>\
						 <field property="references.write"/>\
						 <field property="attachments.write"/>\
						 <field property="template.write"/>\
						 <field property="content.write"/>\
						 <field property="caution.write"/>\
						 <field property="statement.write"/>';
		}
template +='<field property="id.hidden"/>\
		</fields>\
	 </template>';
	 
	var t = new ItemTemplate(document.getElementById('main${PORTLET_ID}'), template);
	var fieldsOpt_1,fieldsOpt_2 = [];
	if(model.appType == <%=MbInApp.APP_FOLDER%>){
		if(!model.id){
			$('<div>').addClass('folderCmnt').text('<kfmt:message key="mbs.inapp.022"/>').appendTo(t.layout.bottomLeftArea);
		}
	}
	else if(model.appType == <%=MbInApp.APP_URL%>){
		var popUp = t.getChild('popUp');
		var popUpStyle = t.getChild('popUpStyle');
		if (model.popUp == false) {
			ItemViewer.hideTR(popUpStyle.widget.get(0));
		}
		popUp.onchange = function(value) {
			var widget = popUpStyle.widget.get(0);
			if (value == 'true') {
				ItemViewer.showTR(widget);
				popUpStyle.setValue(model.popUpStyle || model.popUpStyle == '' ? '<fmt:message key="app.ptl.166"/>'.split(' ')[1] : model.popUpStyle);
			} else {
				ItemViewer.hideTR(widget);
				popUpStyle.setValue('');
			}
		}
	}
	else
	{
		var introduce = t.getChild('introduce');
		var showManager = t.getChild('showManager').widget;
		var intro = t.getChild('intro').widget.get(0);
		var references = t.getChild('references').widget;
		var attachments = t.getChild('attachments').widget;
		
		var tmpl = t.getChild('template');
		var content = t.getChild('content').widget;
		
		var caution = t.getChild('caution');
		var statement = t.getChild('statement').widget.get(0);
		
		var limitExt = t.getChild('limitExt');
		var reserve = t.getChild('reserve').widget;
		var feedOpt = t.getChild('feedOpt');
		var rplyOpt = feedOpt.selectComp[2].widget;
		var anonyOpt = t.getChild('anonyOpt').selectComp[0].widget;
		var expr = t.getChild('exprPeriod').widget;
// 		var process = t.getChild('process').widget;
		var limitSizeH = $(t.getHeader('fileLimit')).children();
		
		fieldsOpt_1 = [showManager, intro, references, attachments];
		fieldsOpt_2 = [anonyOpt, rplyOpt, tmpl, content, expr, /*process*/null, limitSizeH, reserve, limitExt.widget];
		
		toggleField_RULE2${PORTLET_ID}(model.appType, fieldsOpt_2, true);
		if (model.introduce == false) {
			toggleField_RULE1${PORTLET_ID}(false, fieldsOpt_1);
		}
		if (model.template == false) {
			ItemViewer.hideTR(content);
		}
		t.getChild('appType').onchange = function(value) {
			toggleField_RULE2${PORTLET_ID}(value, fieldsOpt_2);
		}
		if(model.opinion == false)
		{
			var symp = feedOpt.selectComp[1].select;
			$(symp).attr("disabled", "disabled");
			symp.value = false;
		}
		$(feedOpt.selectComp[0].select).on('change', this, function(e) {
			var symp = feedOpt.selectComp[1].select;
			if(this.value == 'false'){
				$(symp).attr("disabled", "disabled");
				symp.value = false;
			}
			else
			{
				$(symp).removeAttr("disabled");
			}
		});
		introduce.onclick = function() {
			toggleField_RULE1${PORTLET_ID}(this.getValue() == true, fieldsOpt_1);
		}
		tmpl.onclick = function(value) {
			if (this.getValue() == true) {
				ItemViewer.showTR(content);
				WebEditor.resetFrame(content.id);
			}else{
				ItemViewer.hideTR(content);
			}
		}
		limitExt.style.textStyle[0].tooltipTxt = function(){
			var ext = '<%=getNotSupportedExt()%>';
			var isImage = t.getChild('appType').getValue() == '<%=MbInApp.APP_ALBUM%>';
			if(!ext){
				return isImage ? '<kfmt:message key="mbs.inapp.076"/>' : '<kfmt:message key="mbs.inapp.040"/>';
			}else{
				var smplImgExt = '<kfmt:message key="mbs.inapp.075"/>';
				var allowOpt = (ext.indexOf('/') == 0 && (ext.lastIndexOf('/') == ext.length - 1));
				var sysExts = allowOpt ? ext.substring(1, ext.length - 1) : ext;
				if(isImage){
					var imgExt = [];
					$.each(sysExts.split(','), function(idx, sysExt){
						if(smplImgExt.indexOf(sysExt.toUpperCase()) > -1){
							imgExt.push(sysExt);
						}
					});
					sysExts = imgExt.join(',');
					return sysExts + (allowOpt ? ' <kfmt:message key="mbs.inapp.041"/>' : ' <kfmt:message key="mbs.inapp.039"/>');
				}
				else
				{
					return sysExts + (allowOpt ? ' <kfmt:message key="mbs.inapp.041"/>' : ' <kfmt:message key="mbs.inapp.039"/>');
				}
			}
		}
		console.log(model.caution);
		if (model.caution == false) {
			ItemViewer.hideTR(statement);
		}
		caution.onclick = function(value) {
			if (this.getValue() == true) {
				ItemViewer.showTR(statement);
			}else{
				ItemViewer.hideTR(statement);
			}
		}
	}
	t.setValue(model);
	
	var save = new KButton(t.layout.etcCenter, <fmt:message key="btn.pub.save_color"/>);
	save.onclick = function(){
		var validate = 'name';
		if (model.appType == <%=MbInApp.APP_URL%>) {
			validate += ',url';
			if(t.getChild('popUp').getValue() == 'true'){
				validate += ',popUpStyle';
			}
		}
		else if(!model.appType == <%=MbInApp.APP_FOLDER%>)
		{
			if(t.getChild('appType').getValue() != '<%=MbInApp.APP_WIKI%>')
			{
				validate += ',process';
			}
			validate += ',notify';
		}
		if (t.validate(validate)) {
			if(!model.id){
				var folder = {};
				folder.rootId = treeFrame.axis.rootId;
				folder.parentId = treeFrame.selectId;
				t.action.put('folder', JSV.toJSON(folder));
			}
			if(model.appType == <%=MbInApp.APP_BBS%> || model.appType == <%=MbInApp.APP_WIKI%> || model.appType == <%=MbInApp.APP_ALBUM%>){
				var s = t.getChild('attachments');
				s.files = FileFieldEditor.merge(t.getChild('content').getImages(), s.getValue());
				s.toJSON = function() {
					return this.files;
				}
			}
			t.submitInner('/jsl/MbInAppAdmin.SaveInApp.json', function(data, status){
				if(status=='success'){
					if(!data.error){
						treeFrame.tree.refresh(data.folderId);
					}else{
						if(data.error == '<kfmt:message key="mbs.error.001"/>'){
							JSV.alert('<kfmt:message key="mbs.error.001.msg"/>');
						}else{
							JSV.consoleLog('SaveInApp Error : ' + data.error);
							JSV.alert('<fmt:message key="err.name.DefaultException"/>\n<fmt:message key="err.desc.DefaultException"/>');
						}
					}
				}
				t.action.disabled = false;
			});
		}
	}
	if(model.appType != <%=MbInApp.APP_FOLDER%>)
	{
		var clear = new KButton(t.layout.etcCenter, <kfmt:message key="mbs.btn.003"/>);
		clear.onclick = function(){
			t.setValue(model);
			toggleField_RULE1${PORTLET_ID}(model.introduce == true, fieldsOpt_1);
			var content = t.getChild('content').widget;
			if (model.template == false) {
				ItemViewer.hideTR(content);
			}else{
				ItemViewer.showTR(content);
				WebEditor.resetFrame(content.id);
			}
		}
	}
}, '${PORTLET_ID}');
function toggleField_RULE1${PORTLET_ID}(isShow, arr){
	for(var i=0; i<arr.length; i++){
		if(isShow == true){
			ItemViewer.showTR(arr[i]);
		}else{
			ItemViewer.hideTR(arr[i]);
		}
	}
}
function toggleField_RULE2${PORTLET_ID}(value, arr, init){
	if(value == '<%=MbInApp.APP_ALBUM%>')
	{
		arr[0].show();
		arr[1].hide();
		ItemViewer.hideTR(arr[2].widget);
		ItemViewer.hideTR(arr[3]);
		ItemViewer.showTR(arr[4]);
// 		ItemViewer.showTR(arr[5]);
		$(arr[6]).text('<kfmt:message key="mbs.inapp.073"/>');
		ItemViewer.showTR(arr[7]);
		ItemViewer.hideTR(arr[8]);
	}
	else
	{
		$(arr[6]).text('<kfmt:message key="mbs.inapp.031"/>');
		ItemViewer.showTR(arr[8])
		if(value == '<%=MbInApp.APP_WIKI%>'){
			arr[0].hide();
			arr[1].hide()
			ItemViewer.hideTR(arr[4]);
// 			ItemViewer.hideTR(arr[5]);
			ItemViewer.hideTR(arr[7]);
		}else{
			arr[0].show();
			arr[1].show();
			ItemViewer.showTR(arr[4]);
// 			ItemViewer.showTR(arr[5]);
			ItemViewer.showTR(arr[7]);
		}
		if(!init){
			ItemViewer.showTR(arr[2].widget);
			if (arr[2].getValue() == true) {
				ItemViewer.showTR(arr[3]);
				WebEditor.resetFrame(arr[3].id);
			}else{
				ItemViewer.hideTR(arr[3]);
			}
		}
	}
}
</script>
<div id="main${PORTLET_ID}"></div>
<%@ include file="/jspf/tail.portlet.jsp" %>