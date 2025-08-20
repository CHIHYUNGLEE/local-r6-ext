<%@ include file="/sys/jsv/template/template.inner.head.jsp" %>
<%@ include file="/ekp/mbs/config.jsp" %>
<%@page import="com.kcube.sys.usr.UserService"%>
<%@page import="com.kcube.ekp.mbs.MbItem"%>
<%@page import="com.kcube.ekp.mbs.inapp.MbInApp"%>
<%@ page import="com.kcube.sys.conf.ConfigService"%>
<%@ page import="com.kcube.cst.local.InterfaceConfig"%>
<%! 
	InterfaceConfig _conf = (InterfaceConfig) ConfigService.getConfig(InterfaceConfig.class);
%>
<script src="<c:url value="/ekp/mbs/js/readFunction.js"/>"></script>
<link type="text/css" href="<c:url value="/ekp/mbs/js/readPage.css"/>" media="screen,print" rel="stylesheet">
<script type="text/javascript">
JSV.Block(function(){
	model = <%ctx.execute("MbItemUser.ReadByUser");%>;
	var inAppInfo = JSV.loadJSON(JSV.getModuleUrl('/jsl/MbInAppUser.ReadByUser.json?inAppId=' + model.inAppId));
	appType = inAppInfo.appType;
	imageMbs = appType == '<%=MbInApp.APP_ALBUM%>';
	var isWikiMbs = appType == '<%=MbInApp.APP_WIKI%>';
	var useReply = (appType == '<%=MbInApp.APP_BBS%>' && inAppInfo.reply);
	var template = '<item color="green" catalog="/ekp/mbs/catalog.xml.jsp">\
			<header label="<kfmt:message key="mbs.009"/>"/>\
			<fields class="Array" columns="100px,,,100px,100px,,100px," type="read">\
				<field property="title.read"/>\
				<field property="foldingBar.detailInfo"/>\
				<field property="folder.read"/>';
				if (!isWikiMbs) {
					if (inAppInfo.reserve && model.status != <%=MbItem.REGISTERED_STATUS%>) {
						template += '<field property="rsrvDate.read"/>';
					}
					template += '<field property="exprMonth.read"/>';
				}
				if (inAppInfo.scrt) {
					template += '<field property="securities.read"/>';	
				}
				template += '<field property="foldingBar.hidden"/>\
					<field property="content.read"/>';
				if (!isWikiMbs) {
					template += '<field property="references.read" otrStyle="true"/>';
				}
				template += '<field property="attachments.read" otrStyle="true"/>\
			 	<field property="pid.hidden"/>\
				<field property="gid.hidden"/>\
				<field property="rplyCnt.hidden"/>\
				<field property="author.hidden"/>\
				<field property="status.hidden"/>\
				<field property="currentOwner.hidden"/>\
				<field property="references.hidden"/>\
			</fields>';
			if (inAppInfo.opinion) {
				template += '<opinions class="Array">';
				if (inAppInfo.sympathy) {
					template += '<opinion property="opinions.read"/>';
				} else {
					template += '<opinion property="opinions.noSympathyRead"/>';
				}
				template += '<opinion property="opinions.write"/>\
					</opinions>';
			}
			<% if (isDuplex(moduleParam)) { %>
			template += '<duplex property="duplex.read"/>';
			<% } %>
		template += '</item>';
		
	if (useReply) {
		var groupList = '<template color="blue" catalog="/ekp/mbs/catalog.xml.jsp">\
			<header label="<fmt:message key="doc.168"/>" type="inner"/>\
			<columns class="Array">\
				<column component="<%= getIdComponent(moduleParam) %>" width="60px">\
					<header label="<fmt:message key="doc.006"/>"/>\
					<style attribute="id"/>\
				</column>\
				<column component="TitleColumn">\
					<header label="<fmt:message key="doc.001"/>"/>\
					<style href="usr.read.jsp?id=@{id}&amp;isAnoun=@{announced}" isInner="true" attribute="title" position="pos" rplyIcon="true" newIcon="rgstDate" opnCnt="vrtlOpnCnt" opnView="MbItemOpinion.ViewOpinion" inAppId="inAppId" isBold="<%=com.kcube.lib.secure.SecureUtils.XSSFilter(request.getParameter("id"))%>"/> \
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
					<style select="/jsl/MbItemUser.AttachmentList.json?id=@{id}"\
							inline="/jsl/inline/MbItemUser.DownloadByUser?id=@{id}"\
							attach="/jsl/attach/MbItemUser.DownloadByUser?id=@{id}" />\
				</column>\
			</columns>\
			<tail/>\
		</template>';
		
		var groupModel = <%
			ctx.setParameter("id",ctx.getParameter("id"));
			ctx.execute("MbItemUser.GroupListByUser");
		%>;
		
		var g = new TableTemplate(document.getElementById('groupMain${PAGE_ID}'), groupList);
		g.setValue(groupModel);
	}

	t = new ItemTemplate(document.getElementById('main${PAGE_ID}'), template);
	t.setValue(model);
	t.viewer.defaultFolded('detailInfo');
	if (inAppInfo.opinion) {
		t.getChild('opnViewer').writer.setEditable(inAppInfo.anonyOpn);
		t.getChild('opnWriter').setEditable(inAppInfo.anonyOpn);
	}
	var tds = t.viewer.getTitleBtnArea();

	var isAdmin = <%=isAdmin(moduleParam)%>;
	
	if (inAppInfo.appType == <%=MbInApp.APP_WIKI%>) {
		var isAuthor = t.getProperty('author.id') == '<%=UserService.getUserId()%>';
		var isVrsnUser = t.getProperty('vrsnUser.id') == '<%=UserService.getUserId()%>';
		var redirect = {'checkout':'/ekp/mbs/usr.edit.jsp?id=@{id}', 'checkin':'/ekp/mbs/usr.read.jsp?id=@{id}'};
		var msg = {'checkout':'<kfmt:message key="mbs.018"/>', 'checkin':'<kfmt:message key="mbs.019"/>'};

		var diffBtn = new KButton(tds, <fmt:message key="btn.pub.history"/>);
		diffBtn.onclick = function() {
			popupDiffList();
		}
		
		
		if ((parseInt(t.getProperty('flagCode')) & 16) == 0 && inAppInfo.writeScrt) {
			var editBtn = new KButton(tds, <fmt:message key="btn.pub.modify_round"/>);
			editBtn.onclick = function() {
				doCheck(redirect.checkout, '/jsl/MbItemUser.StartVersionUp.jsl?id=@{id}', msg.checkout);
			}
		} else if ((parseInt(t.getProperty('flagCode')) & 16) == 16 && (isAdmin || isVrsnUser)) {
			if (isVrsnUser && inAppInfo.writeScrt) {
				var editBtn = new KButton(tds, <fmt:message key="btn.pub.modify_round"/>);
				editBtn.onclick = function() {
					JSV.doGET('usr.edit.jsp?id=@{id}');
				}
			}
			
			var liftEditBtn = new KButton(tds, <kfmt:message key="mbs.btn.004"/>);
			liftEditBtn.onclick = function() {
				if (JSV.INNERQUERY == null) {
					doCheck(JSV.setUrlAlert(redirect.checkin, '<kfmt:message key="mbs.020"/>'), isVrsnUser ? '/jsl/MbItemUser.CancelVersionUp.jsl?id=@{id}' : '/jsl/MbItemAdmin.CancelVersionUp.jsl?id=@{id}', msg.checkin);
				} else {
					$.ajax({
						url : JSV.getContextPath(JSV.getModuleUrl(isVrsnUser ? '/jsl/MbItemUser.CancelVersionUp.jsl' : '/jsl/MbItemAdmin.CancelVersionUp.jsl')),
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
			var liftEditBtn = new KButton(tds, <fmt:message key="btn.pub.delete_icon"/>);
			liftEditBtn.onclick = function() {
				doDelete(isAdmin);
			}
			
			function doDelete(isAdmin) {
				JSV.confirm('<kfmt:message key="mbs.022"/>', function(res) {
					if (res) {
						var f = new ItemForm();
						var url = '/ekp/mbs/usr.list.jsp?newItem=true';
						var actionUrl = '/jsl/';
						f.setRedirect(url);
						if (isAdmin) {
							actionUrl += 'MbItemAdmin.DeleteByAdmin.jsl?id=@{id}';
						} else {
							actionUrl += 'MbItemOwner.DeleteByOwner.jsl?id=@{id}';
						}
						f.submit(actionUrl);
					}
				});
			}
		}
		
		function popupDiffList() {
			var u = '/ekp/mbs/usr.diff.list.jsp';
			var q = 'gid=@{id}';
			var n = 'diffPopup';
			var f = 'width=700,height=600,scrollbars=yes,status=no,toolbar=no,menubar=no,location=no,resizable=yes';
			var url = JSV.getContextPath(JSV.getModuleUrl(u), q);
			window.open(url, n, f);
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
	} else {
		// owner
		if (t.getProperty('currentOwner') && t.getProperty('rplyCnt') == 0) {
			var editBtn = new KButton(tds, <fmt:message key="btn.pub.modify_round"/>);
			editBtn.onclick = function() {
				JSV.doGET('usr.edit.jsp?id=@{id}');
			}
			
			var delBtn = new KButton(tds, <fmt:message key="btn.pub.delete_icon"/>);
			delBtn.onclick = function() {
				JSV.confirm('<fmt:message key="pub.009"/>', function(res) {
					if (res) {
						url='/jsl/MbItemOwner.DoRemoveByOwner.jsl?id=@{id}';
						if (t.getProperty('status') == '<%=MbItem.REGISTERED_STATUS%>') {
							url='/jsl/MbItemOwner.DoDeleteByOwner.jsl?id=@{id}';
						}
						t.action.setRedirect(JSV.setUrlAlert('/ekp/mbs/usr.list.jsp'));
						t.submit(url);
					}
				});
			}
		}
		
		var rightBtnArr = [];
		if (inAppInfo.appType == <%=MbInApp.APP_BBS%>) {
			// announced
			if (isAdmin || model.master) {
				if (t.getProperty('announced')) {
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
			}
			
			if (useReply) {
				rightBtnArr.push({text:<fmt:message key="btn.doc.004"/>.text,
					   onclick:function() {
						   JSV.doGET('usr.reply.jsp?id=@{id}');
				   }});
			}
		}
		
		rightBtnArr.push({text:'<fmt:message key="doc.061"/>',
			   onclick:function() {
				   refContent();
		   }});
		
		t.viewer.setMoreBtns(rightBtnArr);
	}

	// favorite
	ScrapMenu.favoriteItem(tds, '<%=com.kcube.ekp.mbs.MbItemHistory.ALIMI_MBS%>', 'com.kcube.ekp.mbs.MbItemConfig.fvrtIndexUrl', model.id, model.title);
	ShortURL.createApp(tds, '<%=com.kcube.ekp.mbs.MbItemHistory.ALIMI_MBS%>', 'com.kcube.ekp.mbs.MbItemConfig.fvrtIndexUrl', JSV.getModuleUrl('/apps/mbs/usr/list/read/' + model.id));
	
	t.viewer.setListBtn(function() {
		JSV.doGET('usr.list.jsp');
	});
	
	//단건 테스트
	var jsonBtn = new KButton(tds, {'text':'test', 'type':'ROUND'});
	jsonBtn.onclick = function() {
		$.ajax({
			url : JSV.getContextPath(JSV.getModuleUrl('/jsl/MbItemUser.testJson.json')),
			type : 'POST',
			data : {'id' : '1'},
			success : function(data, status){
				//JsonWriter
 				var arr = data.array;
				var length = data.array.length; 
				
				//JSONArray
				/* var arr = data;
				var length = data.length; */				
				
				///////////////////////////////////////////////////////////////
				
				var userName = JSV.getLang('testjson', 'userName') + ' : ';
				var equipName = JSV.getLang('testjson', 'equipName') + ' : ';
				var accountName = JSV.getLang('testjson', 'accountName') + ' : ';
				var resMsg = JSV.getLang('testjson', 'resMsg') + ' : ';
				var alertMsg =JSV.getLang('test', 'jsonFail')+ '\n';
				
				for(var i = 0 ; i < length ; i++){	
					alertMsg += userName+arr[i]['userName'] + ', ' + equipName+arr[i]['equipsNum'] + ', ' + accountName+arr[i]['accountName'] + ', ' + resMsg+arr[i]['resMsg'] + '\n';
				}
				JSV.alert(alertMsg);
				
			},
			error : function(xhr){
			}
		});
	}
	
	//다건 테스트
	var jsonBtn2 = new KButton(tds, {'text':'test2', 'type':'ROUND'});
	jsonBtn2.onclick = function() {
		t.submitInner('/jsl/MbItemUser.testJson.json', function(data, status){
			if(status == 'success'){
				//JsonWriter
 				var arr = data.array;
				var length = data.array.length; 
				
				//JSONArray
				/* var arr = data;
				var length = data.length; */	
				
				JSV.alert(length+JSV.getLang('test', 'json'));
				window.location.reload();
			}
		});
	}
	
});
var t;
var imageMbs = false;
var model;
var userId = <%=com.kcube.sys.usr.UserService.getUserId()%>;
var refContent = function() {
	RfrnFieldEditor.showPopup('', t.getProperty('id'), t.getProperty('appId'));
	return false;
}
function onresult() {
	JSV.INREAD(function() {
		TableTemplate.onListView('usr.read.jsp?id=@{id}&isAnoun=@{isAnoun}', null, true);
	}, function() {
		JSV.doGET('/ekp/mbs/usr.read.jsp?id=@{id}');
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
				url : JSV.getContextPath(JSV.getModuleUrl('/jsl/MbItemReference.InsertReference.jsl')),
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
		t.action.setRedirect(JSV.setUrlAlert('/ekp/mbs/usr.list.jsp'),'<fmt:message key="doc.160"/>');
		t.submit('/jsl/MbItemAnnounce.UpdateAnnounce.jsl?id=@{id}&annc=' + obj.annc);
	});
}


		
//오리지널에서는 좋아요 기능 안보이게 변경, 공감아이콘도 변경했으나, 변경 아이콘은 두고 기존 아이콘만 추가.
if(<%=_conf.getOriginMbs()%> != JSV.getAppId()){
	//공감아이콘 변경
	SympathyViewer.Sympatheis = {'1':JSV.getLang('SympathyViewer', 'symp01'),'2':JSV.getLang('SympathyViewer', 'symp02'),'3':JSV.getLang('SympathyViewer', 'symp03')
			,'4':JSV.getLang('SympathyViewer', 'symp04'),'5':JSV.getLang('SympathyViewer', 'symp05'),'6':JSV.getLang('SympathyViewer', 'symp06')}; 
	
	//댓글 좋아요 추가
	OpnViewer.prototype.createOpinion = function(element) {
		var isGlobal = (this.style.isGlobal) ? this.style.isGlobal : null;
		var seq = JSV.SEQUENCE++;
		var li = $('<li>\
			<div class="inner_wrap">\
				<div class="thumb" id="opnVwThumb' + seq + '"></div>\
				<p class="cmt">\
					<span class="username" id="opnVwName' + seq + '"></span>\
					<span class="opnCon" id="opnVspan' + seq + '"></span>\
				</p>\
				<div class="cmt_bottom">\
					<div class="regtime" id="opnVwTime' + seq + '"></div>\
					<div class="like" id="opnVwLike' + seq + '"></div>\
					<div class="likecnt" id="opnVwLikeCnt' + seq + '" style="float: left";></div>\
					<div class="cmt_fn" id="opnVwRpCtr' + seq + '"></div>\
				</div>\
			</div>\
		</li>').attr({'id':element.id,'gid':element.gid,'itemId':element.itemId,'seq':seq}).addClass('opnVwOpn').data('component', this);
	
		this.lastId = element.id;
		var content = element.content;
		if (content.indexOf('|') > -1) {
			var idx = content.indexOf('|');
			var tmpEtcn = content.substring(0, idx);
			if (OpnWriter.Emotions[tmpEtcn])
				content = content.substring(idx +1);
		}
		content = OpnViewer.contentDecode(content);
	
		var vwName = li.find('p.cmt > span.username');
		if (!this.layerMode && element.gid && element.id != element.gid) {
			$('<i>').addClass('i_reply').prependTo(li.find('div.inner_wrap'));
			li.addClass('opnReply');
		}
		if (this.vrtl != null && element.user.id == this.vrtl) {	
			vwName.html('<span class="bold">' + element.user.name);
		} else {
			if (isGlobal != null && isGlobal == 'true') {
				vwName.html(AnchorMbr.generateBoldAnchor(element.user.id, element.user.name));
			} else {
				vwName.append(AnchorEmp.generateBoldAnchor(element.user.id, element.user.name));
			}
		}
		$('<img>').attr('src', JSV.getContextPath('/jsl/inline/ImageAction.Download?cacheOpt=DAY&path=' + element.user.id + '&type=' + JSV.EMPTHUMB_TYPE))
		.one('error', function() {
			$(this).attr('src', JSV.getContextPath(EmpImageViewer.DEFAULT_IMAGE));
		}).appendTo(li.find('div.thumb'));
	
		var $opnCon = li.find('span.opnCon').data('opnCon', content);
		$opnCon.append(OpnViewer.contentToMentionHtmlNodes(content, function(mentionUsers) {
			$opnCon.data('mentionUsers', mentionUsers);
		}));
	
		OpnViewer.setTime(li.find('.regtime'), element.rgstDate);
		
		//댓글 좋아요 개수
		var likeCntArea = li.find('.likecnt'); 
		likeCntArea.text(element.likeCnt);	
	
		//댓글 좋아요
		var likeArea = li.find('.like'); 
		var likeCancelTxt =JSV.getLang('opnLike', 'likeCancelBtn');
		var likeTxt = JSV.getLang('opnLike', 'likeBtn');
		
	
		if(element.currentLike){
			//좋아요 취소
			$('<a href="javascript:void(0);"></a>').addClass("fn_like" + seq).attr('title', JSV.getLang('OpnViewer', 'replyIcon'))
			.css({'float':'left', 'margin':'0 7px 0 0', 'font-size':'15px' ,'color':'red', 'width' :'20px'})
			.text(likeCancelTxt).appendTo(likeArea);
		}else{
			//좋아요 
			$('<a href="javascript:void(0);"></a>').addClass("fn_like" + seq).attr('title', JSV.getLang('OpnViewer', 'replyIcon'))
			.css({'float':'left', 'margin':'0 7px 0 0', 'font-size':'15px' ,'color':'black', 'width' :'20px'})
			.text(likeTxt).appendTo(likeArea);
		}
	
		
		$(document).on('click', '.fn_like' + seq, function(e){
			$.ajax({
				'url' : JSV.getContextPath(JSV.getModuleUrl('/jsl/MbItemOpinion.DoLikedByUser.json')),
				'dataType' : 'json',
				'data' : {'id':element.id},
				'success' : function(data, status){
					if (data && data.error) {
						this.errorMsgDiv = $('<div>\
							    <div class="selectWrapper">\
								    <div id="OpnEdrTextWrpaer' + seq + '" class="textWrpaer"></div>\
								</div>\
							</div>').addClass('OpnViewerEditor_errorMsg').hide().appendTo('.fn_like'+seq);
						
						$('#OpnEdrTextWrpaer' + seq).html(JSV.getLang('CstmOpnViewer','selfErrorMsg'));
						this.errorMsgDiv.show();
						$(document).one('click.errorMsgDiv', this, function(e){
							e.data.errorMsgDiv.fadeOut();
							$('.OpnViewerEditor_errorMsg').remove();
						}); 
					}else{
						$('#opnVwLikeCnt'+seq).text(data.likeCnt);
						data.isLike ? $('.fn_like' + seq).css('color','red').text(likeCancelTxt) : $('.fn_like' + seq).css('color','black').text(likeTxt);					
					}
				},
				'error' : function(xhr){
					console.log(xhr);			
				}
			});
		});
		//end
	
		
		
		if (!this.printMode) {
			var rplyArea = li.find('div.cmt_bottom > div.cmt_fn'); 
			if (element.id == element.gid && this.style.actionAddUrl) {
				$('<a href="javascript:void(0);"></a>').addClass('fn_reply').attr('title', JSV.getLang('OpnViewer', 'replyIcon'))
				.text(JSV.getLang('OpnViewer', 'replyIcon')).appendTo(rplyArea)
				.click(function(e) {
					e.stopPropagation();
					if (OpnViewer.isEdit) {
						OpnViewer.save();
						return false;
					}
					OpnViewer.isReply ? OpnViewer.replySave(li) : OpnViewer.reply(li);
				});
			}
			if (this.style.opnUpdate && this.isEditable(element)) {
				$('<a href="javascript:void(0);"></a>').addClass('fn_edit').attr('title', JSV.getLang('OpnViewer', 'editIcon'))
				.text(JSV.getLang('OpnViewer', 'editIcon')).appendTo(rplyArea)
				.click(function(e) {
					e.stopPropagation();
					if (OpnViewer.isReply) {
						OpnViewer.replySave();
						return false;
					}
					OpnViewer.isEdit ? OpnViewer.save(li) : OpnViewer.edit(li);
				});
			}
			if (this.style.opnDelete && this.isDeletable(element)) {
				$('<a href="javascript:void(0);"></a>').addClass('fn_del').attr('title', JSV.getLang('OpnViewer', 'deleteIcon'))
				.text(JSV.getLang('OpnViewer', 'deleteIcon')).appendTo(rplyArea)
				.click(function(e) {
					JSV.confirm(JSV.getLang('OpnViewer', 'deleteConfirm'), function(res) {
						if (res) {
							OpnViewer.del(li);	
						}
					});
				});
			}
			if (this.hideButtons) {
				rplyArea.hide();
			}
		}
		return li;
	}
}

/////////////////////////////////////////////////////////
FileViewer.prototype.setValueAfterInit = function() {
	if (FileViewer.initialized && this.hiddenValues) {
		for (var i = 0; i < this.hiddenValues.length; i++) {
			this.addFileItem(this.hiddenValues[i]);
		}
		var cnt = 0;
		for (var i = 0; i < this.values.length; i++) {
			if (!this.values[i].hidden)
				cnt++;
		}
		if (this.style.isList && this.cntArea) {
			var num = '<span class="num">' + cnt + '</span>';
			var txt = JSV.getLang('FileViewer', 'cntText') + num + JSV.getLang('FileViewer', 'cntTail');
			this.cntArea.html(txt + '&nbsp;');
		}
		if ((this.hiddenValues.length == 0 && this.style.optional) || this.fileLink.find('div.filesWrap').length == 0) {
			if (this.$parent.is('td')) {
				ItemViewer.hideTR(this.widget);
			} else {
				this.$parent.hide();
			}
		}
		if (!this.style.isList && cnt > 1) {
			var header = this.$parent.find('.topHd');
			var parent = null;
			if (header.length > 0) {
				parent = header;
			} else {
				parent = $('<div>').addClass('zipArea').prependTo(this.$widget);
			}
			$('<span>').addClass('FileViewer_fileCntSpan').html('<span>' + cnt + '</span>' + JSV.getLang('FileViewer', 'cntTail')).appendTo(parent);
			$('<span>').addClass('FileViewer_fileSizeSpan').text('(' + FileViewer.getSize(this.style.totalSize) + ')').appendTo(parent);
						
			if (this.style.attachZip && this.style.attachZip != "") {
				var ZipDownloadText = '<span class="zipDownloadSpan">' + JSV.getLang('FileViewer', 'zipDownload') + '</span>';
				//var attachZipUrl = JSV.getLocationPath(JSV.getModuleUrl(JSV.merge(this.style.attachZip, {'id':this.itemId})));
				var attachZipUrl = JSV.getLocationPath(JSV.getModuleUrl(this.style.attachZip+this.itemId));
				$('<a>').addClass('FileViewer_zipDownload').html(ZipDownloadText).attr('href', attachZipUrl).appendTo(parent);
			}
		}
	} else {
		setTimeout(this.jsref + '.setValueAfterInit();', 100);
	}
}
</script>
<div id="main${PAGE_ID}"></div>
<div id="groupMain${PAGE_ID}"></div>
<%@ include file="/sys/jsv/template/template.inner.tail.jsp" %>