<%@ include file="/sys/jsv/template/template.inner.head.jsp" %>
<%@ include file="/ekp/bbs/config.jsp" %>
<script type="text/javascript">
JSV.Block(function () {
	var template = '<item color="green" catalog="/ekp/bbs/catalog.xml.jsp">\
			<header label="<kfmt:message key="bbs.002"/>"/>\
			<fields class="Array" columns="100px,,,100px,100px,,100px," type="read">\
			<field property="title.read"/>'
			<% if (useCtgr(moduleParam)) { %>
			 + '<jsp:include page="usr.read.form" flush="true"/>'
			 + '<field property="folder.hidden"/>'
			 <% } else {%>
			 + '<jsp:include page="usr.read.noctgr.form" flush="true"/>'
			 <% } %>
			 + '<field property="content.read"/>\
			 	<field property="references.read" otrStyle="true"/>\
			 	<field property="attachments.read" otrStyle="true"/>\
			 	<field property="pid.hidden"/>\
				<field property="gid.hidden"/>\
				<field property="rplyCnt.hidden"/>\
				<field property="author.hidden"/>\
				<field property="status.hidden"/>\
				<field property="currentOwner.hidden"/>\
				<field property="references.hidden"/>\
			</fields>\
			<opinions class="Array">\
				<opinion property="opinions.read"/>\
				<opinion property="opinions.write"/>\
			</opinions>'
			<% if (isDuplex(moduleParam)) { %>
			+ '<duplex property="duplex.read"/>'
			<% } %>
		+ '</item>';
		var groupList = '<template color="blue" catalog="/ekp/bbs/catalog.xml.jsp">\
			<header label="<fmt:message key="doc.168"/>" type="inner"/>\
			<columns class="Array">\
				<column component="TextColumn" width="60px">\
					<header label="<fmt:message key="doc.006"/>"/>\
					<style attribute="id"/>\
				</column>\
				<column component="TitleColumn">\
					<header label="<fmt:message key="doc.001"/>"/>\
					<style href="usr.read.jsp?id=@{id}&amp;isAnoun=@{announced}" isInner="true" attribute="title" position="pos" rplyIcon="true" newIcon="rgstDate" opnCnt="opnCnt" opnView="BdItemOpinion.ViewOpinion" isBold="<%=com.kcube.lib.secure.SecureUtils.XSSFilter(request.getParameter("id"))%>"/> \
				</column>\
				<column component="EmpColumn" width="82px">\
					<header label="<fmt:message key="doc.003"/>"/>\
					<style id="userId" name="userName" vrtl="<fmt:message key="sys.vrtl.userId"/>"/>\
				</column>\
				<column component="DateColumn" width="100px">\
					<header label="<fmt:message key="doc.022"/>"/>\
					<style format="<fmt:message key="date.medium"/>" attribute="rgstDate"/>\
				</column>\
				<column name="attachments" width="50px" component="FileColumn" id="attachments">\
					<header label="<fmt:message key="doc.038"/>"/>\
					<style select="/jsl/BdItemUser.AttachmentList.json?id=@{id}"\
							inline="/jsl/inline/BdItemUser.DownloadByUser?id=@{id}"\
							attach="/jsl/attach/BdItemUser.DownloadByUser?id=@{id}" />\
				</column>\
			</columns>\
			<tail/>\
		</template>';
	var model = <%ctx.execute("BdItemUser.ReadByUser");%>;
	var groupModel = <%
		ctx.setParameter("id",ctx.getParameter("id"));
		ctx.execute("BdItemUser.GroupListByUser");
	%>;

	if (JSV.getParameter('tr')) {
		JSV.setState('tr',JSV.getParameter('tr'));
	}

	t = new ItemTemplate(document.getElementById('main${PAGE_ID}'), template);
	t.setValue(model);
	t.viewer.defaultFolded('detailInfo');
	
	var tds = t.viewer.getTitleBtnArea();

	var rightBtnArr = [];

	// owner
	if (t.getProperty('currentOwner') && t.getProperty('rplyCnt') == 0 ) {
		var editBtn = new KButton(tds, <fmt:message key="btn.pub.modify_round"/>);
		editBtn.onclick = function() {
			JSV.doGET('usr.edit.jsp?id=@{id}');
		}
		
		var delBtn = new KButton(tds, <fmt:message key="btn.pub.delete_icon"/>);
		delBtn.onclick = function() {
			JSV.confirm('<fmt:message key="pub.009"/>', function(res) {
				if (res) {
					url='/jsl/BdItemOwner.DoRemoveByOwner.jsl?id=@{id}';
					if (t.getProperty('status') == 3000)
						url='/jsl/BdItemOwner.DoDeleteByOwner.jsl?id=@{id}';
					t.action.setRedirect(JSV.setUrlAlert('/ekp/bbs/usr.list.jsp'));
					t.submit(url);	
				}
			});
		}
	}
	
	// favorite
	ScrapMenu.favoriteItem(tds, '<%=com.kcube.ekp.bbs.BdItemHistory.ALIMI_BBS%>', 'com.kcube.ekp.bbs.BdItemConfig.fvrtIndexUrl', model.id, model.title);
	ShortURL.createApp(tds, '<%=com.kcube.ekp.bbs.BdItemHistory.ALIMI_BBS%>', 'com.kcube.ekp.bbs.BdItemConfig.fvrtIndexUrl', JSV.getModuleUrl('/apps/bbs/usr/list/read/' + model.id));	
	// announced
	<% if (isAdmin(moduleParam)) {%>
		if (t.getProperty('announced') == true) {
			rightBtnArr.push({text:<fmt:message key="btn.doc.021"/>.text, onclick:function() {
				var url = '/sys/jsv/doc/AnnounceDialog.jsp';
				var query = 'isEdit=true&sDate=' + t.getProperty('anncSdate') + '&eDate=' + t.getProperty('anncEdate');
				window.open(JSV.getContextPath(url, query), 'announce', 'height=200,width=680,toolbar=no,scrollbars=no,status=no,resizable=no');
			}});
		} else {
			rightBtnArr.push({text:<fmt:message key="btn.doc.020"/>.text, onclick:function() {
				window.open(JSV.getContextPath('/sys/jsv/doc/AnnounceDialog.jsp'), 'announce', 'height=200,width=680,toolbar=no,scrollbars=no,status=no,resizable=no');
			}});
		}
	<% } %>
	rightBtnArr.push({text:<fmt:message key="btn.doc.004"/>.text,
		   onclick:function() {
			   JSV.doGET('usr.reply.jsp?id=@{id}');
	   }})
	t.viewer.setMoreBtns(rightBtnArr);

	t.viewer.setListBtn(function() {
		JSV.doGET('usr.list.jsp');
	});

	var g = new TableTemplate(document.getElementById('groupMain${PAGE_ID}'), groupList);
	g.setValue(groupModel);
});
var t;
var userId = <%=com.kcube.sys.usr.UserService.getUserId()%>;
var refContent = function() {
	RfrnFieldEditor.showPopup('', t.getProperty('id'), t.getProperty('appId'));
	return false;
}
function onresult() {
	JSV.INREAD(function() {
		TableTemplate.onListView('usr.read.jsp?id=@{id}&isAnoun=@{isAnoun}', null, true);
	}, function() {
		JSV.doGET('/ekp/bbs/usr.read.jsp?id=@{id}');
	});
}
function setData(value) {
	JSV.INREAD(function() {
		var refList = JSV.clone(value);
		var item = {};
		item.id = parseInt(JSV.getParameter('id'));
		if (refList) {
			item.references = new Array(refList.length);
			item.references.nodeName = 'reference';
			for (var i = 0; i < refList.length; i++) {
				item.references[i] = refList[i];
			}
			
			$.ajax({
				url : JSV.getContextPath(JSV.getModuleUrl('/jsl/BdItemReference.InsertReference.jsl')),
				type : 'POST',
				dataType : 'json',
				data : {'item' : JSV.toJSON(item)},
				success : function(data, status){
					onresult();
				},
				error : function(xhr){
				}
			});
		}
	});
}
function getHiddenData() {
	var att = t.getChild('references').rfrns;
	for (var i = 0; i < att.length; i++) {
		if (att[i].rgstUser.id != userId) {
			att[i].disabled = true;
		}
	}
	return att;
}
function setAnnounce(obj) {
	JSV.INREAD(function() {
		for (var key in obj) {
			if (key != 'annc') {
				t.action.put(key, obj[key]);
			}
		}
		t.action.setRedirect(JSV.setUrlAlert('/ekp/bbs/usr.list.jsp'),'<fmt:message key="doc.160"/>');
		t.submit('/jsl/BdItemAnnounce.UpdateAnnounce.jsl?id=@{id}&annc=' + obj.annc);
	});
}
</script>
<div id="main${PAGE_ID}"></div>
<div id="groupMain${PAGE_ID}"></div>
<%@ include file="/sys/jsv/template/template.inner.tail.jsp" %>