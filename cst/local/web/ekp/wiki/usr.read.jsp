<%@ include file="/sys/jsv/template/template.inner.head.jsp" %>
<%@ include file="/ekp/wiki/config.jsp" %>
<%@page import="com.kcube.cst.local.InterfaceConfig"%>
<%!
InterfaceConfig _conf = (InterfaceConfig) com.kcube.sys.conf.ConfigService.getConfig(InterfaceConfig.class);
%>
<script type="text/javascript">
JSV.Block(function () {
	var template = '<template color="green" catalog="/ekp/wiki/catalog.xml.jsp">\
			<header label="<kfmt:message key="wiki.004"/>"/>\
			<fields class="Array" columns="100px,,,100px,100px,,100px," type="read">'
			<% if (useCtgr(moduleParam)) { %>
			 + '<field property="title.read"/>\
			 <field property="foldingBar.detailInfo"/>\
			 <field property="folder.read"/>\
			 <field property="securities.read"/>\
			 <field property="foldingBar.hidden"/>\
			 <field property="content.read"/>\
			 <field property="attachments.read" otrStyle="true"/>\
			 <field property="folder.hidden"/>\
			 <field property="author.hidden"/>'
			 <% } else {%>
			 + '<field property="title.read"/>\
			 <field property="content.read"/>\
			 <field property="attachments.read" otrStyle="true"/>\
			 <field property="author.hidden"/>'
			 <% } %>
			+ '</fields>\
			<opinions class="Array">\
				<opinion property="opinions.read"/>\
				<opinion property="opinions.write"/>\
			</opinions>'
			<% if (isDuplex(moduleParam)) { %> 
			+ '<duplex property="duplex.read"/>'
			<% } %>
		+ '</template>';
	var model = <%ctx.execute("WikiItemUser.ReadByUser");%>;
	if (JSV.getParameter('tr')) {
		JSV.setState('tr',JSV.getParameter('tr'));
	}
	
	var type = JSV.getParameter('type') || 'list';
	var ctgr = JSV.getParameter('ctgr') || '0';
	JSV.setState('type', type);
	JSV.setState('ctgr', ctgr);

	t = new ItemTemplate(document.getElementById('main${PAGE_ID}'), template);
	t.setValue(model);
	t.viewer.defaultFolded('detailInfo');
	
	t.viewer.setListBtn(function() {
		JSV.doGET('usr.list.jsp');
	});
	
	var btnArea = t.viewer.getTitleBtnArea();
	
	var isAdmin = <%=isAdmin%>;
	var isAuthor = t.getProperty('author.id') == '<%=userId%>' ? true : false;
	var isVrsnUser = t.getProperty('vrsnUser.id') == '<%=userId%>' ? true : false;
	var redirect = {'checkout':'/ekp/wiki/usr.edit.jsp?id=@{id}', 'checkin':'/ekp/wiki/usr.read.jsp?id=@{id}'};
	var msg = {'checkout':'<kfmt:message key="wiki.010"/>', 'checkin':'<kfmt:message key="wiki.011"/>'};

	var diffBtn = new KButton(btnArea, <fmt:message key="btn.pub.history"/>);
	diffBtn.onclick = function() {
		popupDiffList();
	}
	
	if ((parseInt(t.getProperty('flagCode')) & 16) == 0) {
		var editBtn = new KButton(btnArea, <fmt:message key="btn.pub.modify_round"/>);
		editBtn.onclick = function() {
			doCheck(redirect.checkout, '/jsl/WikiItemUser.StartVersionUp.jsl?id=@{id}', msg.checkout);
		}
	} else if ((parseInt(t.getProperty('flagCode')) & 16) == 16 && (isAdmin || isVrsnUser)) {
		if (isVrsnUser) {
			var editBtn = new KButton(btnArea, <fmt:message key="btn.pub.modify_round"/>);
			editBtn.onclick = function() {
				JSV.doGET('usr.edit.jsp?id=@{id}');
			}
		}
		var liftEditBtn = new KButton(btnArea, <kfmt:message key="btn.wiki.002"/>);
		liftEditBtn.onclick = function() {
			if (JSV.INNERQUERY == null) {
				doCheck(JSV.setUrlAlert(redirect.checkin, '<kfmt:message key="wiki.012"/>'), isVrsnUser ? '/jsl/WikiItemUser.CancelVersionUp.jsl?id=@{id}' : '/jsl/WikiItemAdmin.CancelVersionUp.jsl?id=@{id}', msg.checkin);
			} else {
				$.ajax({
					url : JSV.getContextPath(JSV.getModuleUrl(isVrsnUser ? '/jsl/WikiItemUser.CancelVersionUp.jsl' : '/jsl/WikiItemAdmin.CancelVersionUp.jsl')),
					type : 'POST',
					dataType : 'json',
					data : {'id' : JSV.getParameter('id')},
					success : function(data, status){
						TableTemplate.onListView('usr.read.jsp?id=@{id}', null, true);	
					},
					error : function(xhr){
					}
				});
			}
		}
	}
	
	if (isAdmin || isAuthor) {
		var liftEditBtn = new KButton(btnArea, <fmt:message key="btn.pub.delete_icon"/>);
		liftEditBtn.onclick = function() {
			doDelete(isAdmin);
		}
	}
	
	ScrapMenu.favoriteItem(btnArea, '<%=com.kcube.ekp.wiki.WikiItemHistory.ALIMI_WIKI%>', 'com.kcube.ekp.wiki.WikiItemConfig.fvrtIndexUrl', model.id, model.title);
	ShortURL.createApp(btnArea, '<%=com.kcube.ekp.wiki.WikiItemHistory.ALIMI_WIKI%>', 'com.kcube.ekp.wiki.WikiItemConfig.fvrtIndexUrl', JSV.getModuleUrl('/apps/wiki/usr/list/read/' + model.id));
	
	function popupDiffList() {
		var u = '/ekp/wiki/usr.diff.list.jsp';
		var q = 'gid=@{id}';
		var n = 'diffPopup';
		var f = 'width=700,height=600,scrollbars=yes,status=no,toolbar=no,menubar=no,location=no,resizable=yes';
		var url = JSV.getContextPath(JSV.getModuleUrl(u), q);
		window.open(url, n, f);
	}

	function doDelete(isAdmin) {
		JSV.confirm('<kfmt:message key="wiki.026"/>', function(res) {
			if (res) {
				var f = new ItemForm();
				var url = '/ekp/wiki/usr.list.jsp' ;
				var actionUrl = '/jsl/';
				f.setRedirect(url);
				if (isAdmin) {
					actionUrl += 'WikiItemAdmin.DeleteByAdmin.jsl?id=@{id}';
				} else {
					actionUrl += 'WikiItemOwner.DeleteByOwner.jsl?id=@{id}';
				}
				f.submit(actionUrl);
			}
		});
	}

	function doCheck(url, action, msg) {
		JSV.confirm(msg, function(res) {
			if (res) {
				var f = new ItemForm();
				f.setRedirect(url);
				f.submit(action);
			}
		});
	}
	function doPartEdit(section) {
		JSV.alert(section);
	}
});

//오리지널위키에서는 커스텀요소 적용안되게 수정.
if(<%=_conf.getOriginMbs()%> != JSV.getAppId()){
	var t;
	RecommendEditor.prototype.onclick = function(){
		if (this.exeFunc) {
			try {
				var func = eval(this.exeFunc);
				func();
			} catch (e){}
		} else if (this.action != '') {
			$.ajax({'url':JSV.getContextPath(this.action),
				'dataType':'json',
				'data':this.ctx,
				'context':this,
				'success':function(data, status) {
					if (data && data.error) {
						this.onFail();
					} else {
						this.onSuccess(data);
					}
				},
				'error':function(xhr) { this.onFail(); }
			});
		}
	}
	RecommendEditor.prototype.onSuccess = function(value){
		if(value){	
			this.setValue(value[this.attribute]);
		}
	}
	RecommendEditor.prototype.showMessage = function() {
		if (this.errorMsgDiv == null) {
			this.errorMsgDiv = $('<div>\
				    <div class="selectWrapper">\
					    <div id="RecmndEdrTextWrpaer' + this.seq + '" class="textWrpaer"></div>\
					</div>\
				</div>').addClass('RecommendEditor_errorMsg').hide().appendTo(this.widget);
		}
		$('#RecmndEdrTextWrpaer' + this.seq).html(JSV.getLang('CstmRecommendEditor','modifyErrorMsg'));
		this.errorMsgDiv.show();
		$(document).one('click.errorMsgDiv', this, function(e){
			e.data.errorMsgDiv.fadeOut();
		});
	}
}
</script>
<div id="main${PAGE_ID}"></div>
<%@ include file="/sys/jsv/template/template.inner.tail.jsp" %>