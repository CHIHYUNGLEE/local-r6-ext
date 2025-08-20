<%@ include file="/sys/jsv/template/template.inner.head.jsp" %>
<%@ include file="/ojt/config.jsp" %>
<script type="text/javascript">
JSV.Block(function () {
	var template = '<item color="green" catalog="/ojt/catalog.xml.jsp">\
			<header label="<kfmt:message key="ojt.002"/>"/>\
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
		var groupList = '<template color="blue" catalog="/ojt/catalog.xml.jsp">\
			<header label="<fmt:message key="doc.168"/>" type="inner"/>\
			<columns class="Array">\
				<column component="TextColumn" width="60px">\
					<header label="<fmt:message key="doc.006"/>"/>\
					<style attribute="id"/>\
				</column>\
				<column component="TitleColumn">\
					<header label="<fmt:message key="doc.001"/>"/>\
					<style href="usr.read.jsp?id=@{id}&amp;isAnoun=@{announced}" isInner="true" attribute="title" position="pos" rplyIcon="true" newIcon="rgstDate" opnCnt="opnCnt" opnView="OjtItemOpinion.ViewOpinion" isBold="<%=com.kcube.lib.secure.SecureUtils.XSSFilter(request.getParameter("id"))%>"/> \
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
					<style select="/jsl/OjtItemUser.AttachmentList.json?id=@{id}"\
							inline="/jsl/inline/OjtItemUser.DownloadByUser?id=@{id}"\
							attach="/jsl/attach/OjtItemUser.DownloadByUser?id=@{id}" />\
				</column>\
			</columns>\
			<tail/>\
		</template>';
		
	var model = <%ctx.execute("OjtItemUser.ReadByUser");%>;
	var groupModel = <%
		ctx.setParameter("id",ctx.getParameter("id"));
		ctx.execute("OjtItemUser.GroupListByUser");
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
	if (t.getProperty('currentOwner') && t.getProperty('rplyCnt') == 0 && t.getProperty('asStatus') != 3600) {
		var editBtn = new KButton(tds, <fmt:message key="btn.pub.modify_round"/>);
		editBtn.onclick = function() {
			JSV.doGET('usr.edit.jsp?id=@{id}');
		}
		
		var delBtn = new KButton(tds, <fmt:message key="btn.pub.delete_icon"/>);
		delBtn.onclick = function() {
			JSV.confirm('<fmt:message key="pub.009"/>', function(res) {
				if (res) {
					url='/jsl/OjtItemOwner.DoRemoveByOwner.jsl?id=@{id}';
					if (t.getProperty('status') == 3000)
						url='/jsl/OjtItemOwner.DoDeleteByOwner.jsl?id=@{id}';
					t.action.setRedirect(JSV.setUrlAlert('/ojt/usr.list.jsp'));
					t.submit(url);	
				}
			});
		}
	}
	
	// favorite
	ScrapMenu.favoriteItem(tds, '<%=com.kcube.ojt.OjtItemHistory.ALIMI_OJT%>', 'com.kcube.ojt.OjtItemConfig.fvrtIndexUrl', model.id, model.title);

	// announced 공지설정
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

			
	//지정된 교육생이면서 과제이고(제출된 과제 x) 진행대기,진행완료 상태가 아니면서 한번도 제출하지 않은 경우에만 과제제출 버튼 만들기
	for(var i = 0 ;  i<t.getProperty('students').length ; i++){
		if(JSON.stringify(t.getProperty('students')[i].id) == userId && t.getProperty('pid') == null && t.getProperty('students') != null && t.getProperty('asStatus') != 3300 && t.getProperty('asStatus') != 3500 && already() == 0){
			rightBtnArr.push({text:<kfmt:message key="ojt.039"/>.text,
				   onclick:function() {
					   JSV.doGET('usr.reply.jsp?id=@{id}');
			   }})
		}
	}

	
 	//제출한 과제 이면서 교육 담당자이고 제출한 과제의 상태가 확인이 아닐경우 확인 버튼 생성 
 	//확인 버튼 클릭 시 상태 업데이트 및 조건 만족 시 과제 진행완료 상태 변경
	if(t.getProperty('pid') != null && t.getProperty('cstmField1') == userId && t.getProperty('asStatus2') == 0){
		var checkBtn = new KButton(tds, <kfmt:message key="ojt.037"/>);
		checkBtn.onclick = function() {
			alert('<kfmt:message key="ojt.038"/>');
			url='/jsl/OjtItemUser.UpdateAsStatusByUser.jsl?id=@{id}';
			t.action.setRedirect(JSV.setUrlAlert('/ojt/usr.read.jsp?id=@{id}'));
			t.submit(url);	
			
		}
	}   
	
	t.viewer.setMoreBtns(rightBtnArr);
	t.viewer.setListBtn(function() {
		JSV.doGET('usr.list.jsp');
	});
	var g = new TableTemplate(document.getElementById('groupMain${PAGE_ID}'), groupList);
	g.setValue(groupModel);

	

});
var userId = <%=com.kcube.sys.usr.UserService.getUserId()%>;
var refContent = function() {
	RfrnFieldEditor.showPopup('', t.getProperty('id'), t.getProperty('appId'));
	return false;
} 

function already(){
	var alr;
	$.ajax({
		url : JSV.getContextPath('/jsl/OjtItemUser.AlreadySubmission.json'),
		type : 'POST',
		async: false, 
		dataType : 'json',
		data : {'id' : t.getProperty('id')},
		success : function(data){
			alr = data.id;
		},
		error : function(xhr){
		}
	});
	return alr;
}
function onresult() {
	JSV.INREAD(function() {
		TableTemplate.onListView('usr.read.jsp?id=@{id}&isAnoun=@{isAnoun}', null, true);
	}, function() {
		JSV.doGET('/ojt/usr.read.jsp?id=@{id}');
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
				url : JSV.getContextPath(JSV.getModuleUrl('/jsl/OjtItemReference.InsertReference.jsl')),
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
ComboViewer.prototype.setValue = function(value) {
	if (value == null && this.style && this.style.defaultValue) {
		this.widget.text(this.style.defaultValue);
	}
	if (value != null) {
		this.value = value;
		this.widget.text(JSV.getLocaleStr(this.optionList[value]));
	}
	if (this.widget.text().trim() == '') 
		this.widget.html('&nbsp;');
	//과제상태가 '지연'인 경우 색 변화
	if(value == 3700){
		$(".DateTermViewer").attr('style','color : red;font-weight : 600');	
	}
}
/* DateTermViewer.prototype.setValue = function(value) {
	if (value == null || value.length == 0 ) {
		return;
	}
	if ((value && value[0]) || (value && value[1])) {
		this.startDate = value[0];
		this.endDate = value[1];
		var sStr = '';
		var eStr = '';
		if (value[0] != null || value[0] != undefined) {
			if (typeof(this.startDate) != 'number') this.startDate = parseInt(this.startDate);
			sStr = DateFormat.format(new Date(this.startDate), this.format);
		}
		if (value[1] != null || value[1] != undefined) {
			if (typeof(this.endDate) != 'number') this.endDate = parseInt(this.endDate);
			eStr = DateFormat.format(new Date(this.endDate), this.format);
		}
		var dateTermText = sStr + ' ~ ' + eStr;
		if (this.oneDate && sStr == eStr) {
			dateTermText = sStr;
		}
		this.widget.text(dateTermText);
		
		//과제종료일시보다 현재시간이 큰 경우 색 변화
		var formatEndTime = DateFormat.format(new Date(this.endDate), this.style.format)
		var formatNow = DateFormat.format(new Date(), this.style.format)
		
		if(formatEndTime < formatNow) {
			$(".DateTermViewer").attr('style','color : red');
		}  


	} else {
		this.widget.text('-');
	}
} */
</script>
<div id="main${PAGE_ID}"></div>
<div id="groupMain${PAGE_ID}"></div>
<%@ include file="/sys/jsv/template/template.inner.tail.jsp" %>