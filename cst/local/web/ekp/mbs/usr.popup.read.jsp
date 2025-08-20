<%@ include file="/sys/jsv/template/template.head.jsp" %>
<%@page import="com.kcube.ekp.mbs.inapp.MbInApp"%>
<script src="<c:url value="/ekp/mbs/js/readFunction.js"/>"></script>
<link type="text/css" href="<c:url value="/ekp/mbs/js/readPage.css"/>" media="screen,print" rel="stylesheet">
<script type="text/javascript">
var t;
var imageMbs;
var model;
JSV.Block(function(){
	model = <%ctx.execute("MbItemUser.ReadByUser");%>;
	var inAppInfo = JSV.loadJSON(JSV.getModuleUrl('/jsl/MbInAppUser.ReadByUser.json?inAppId=' + model.inAppId));
	var appType = inAppInfo.appType;
	imageMbs = appType == '<%=MbInApp.APP_ALBUM%>';
	var isWikiMbs = appType == '<%=MbInApp.APP_WIKI%>';
	
	var template = '<template view="popup" catalog="/ekp/mbs/catalog.xml.jsp">\
			<fields class="Array" columns="100px,,,100px,100px,,100px," type="read">\
				<field property="title.popupRead"/>\
				<field property="foldingBar.detailInfo"/>\
				<field property="folder.read"/>';
				if (!isWikiMbs) {
					template += '<field property="exprMonth.read"/>';
				}
				if (inAppInfo.scrt) {
					template += '<field property="securities.read"/>';
				}
				template += '<field property="foldingBar.hidden"/>\
					<field property="content.readPopup"/>';
				if (!isWikiMbs) {
					template += '<field property="references.read" otrStyle="true"/>';
				}
				template += '<field property="attachments.read" otrStyle="true"/>\
			</fields>';
			if (inAppInfo.opinion) {
				template += '<opinions class="Array">';
				if (inAppInfo.sympathy) {
					template += '<opinion property="opinions.read_pop"/>';
				} else {
					template += '<opinion property="opinions.noSympathyReadPop"/>';
				}
				template += '<opinion property="opinions.write_pop"/>\
					</opinions>';
			}
		template += '</template>';

	t = new ItemTemplate(document.getElementById('main'), template);
	t.setValue(model);
	t.viewer.defaultFolded('detailInfo');
	
	if (inAppInfo.opinion) {
		t.getChild('opnWriter').setEditable(inAppInfo.anonyOpn);
	}
	if (JSV.getParameter('userId')) {
		t.viewer.setListBtn(function() {
			JSV.doGET('usr.popup.list.jsp');
		});
	}
});

//공감아이콘 변경
SympathyViewer.Sympatheis = {'1':JSV.getLang('SympathyViewer', 'symp01'),'2':JSV.getLang('SympathyViewer', 'symp02'),'3':JSV.getLang('SympathyViewer', 'symp03')
		,'4':JSV.getLang('SympathyViewer', 'symp04'),'5':JSV.getLang('SympathyViewer', 'symp05'),'6':JSV.getLang('SympathyViewer', 'symp06')}; 
		
		
//댓글좋아요 		
OpnViewer.prototype.setValue = function(elements, comp) {
	this.component = comp;
	if (!this.itemIdVal) {
		if (comp) {
			this.itemIdVal = comp.getProperty(this.itemId);
		}
		if (!this.itemIdVal) {
			this.itemIdVal = JSV.getParameter(this.itemId);
		}
	}
	if (elements) {
		this.opnList = elements;
	}
	if (comp && comp.layout.form) {
		var layout = comp.layout.form;
	}
	if (this.useEditable && elements[1] && (typeof(eval(elements[1])) == 'boolean')) {
		this.writer.setEditable(eval(elements[1]));
		elements = elements[0];
	}
	this.total = elements.total || elements.totalRows || 0;
	if (this.total == 0) {
		$('#opnVwFilter' + this.seq).hide();
	}
	if (this.style.noTitle) {
		$('#opnVwCntMain' + this.seq).hide();
	} else {
		if (!this.appendRight) {
			var fold = !this.isFold;
			$('#opnVwCntA' + this.seq).on('click', this, function(e) {
				if (fold) {
					$('#opnVwContent' + e.data.seq).slideUp('fast');
					if(layout) $(layout).find('table.opnWrtTable').slideUp('fast');
					$('#opnVwCntArrow' + e.data.seq).removeClass('selected');
					fold = false;
				} else {
					$('#opnVwContent' + e.data.seq).slideDown('fast');
					if(layout) $(layout).find('table.opnWrtTable').slideDown('fast');
					$('#opnVwCntArrow' + e.data.seq).addClass('selected');
					fold = true;
				}
			});
			if (!fold) {
				$('#opnVwContent' + this.seq).hide();
				if(layout) $(layout).find('table.opnWrtTable').hide();
				$('#opnVwCntArrow' + this.seq).removeClass('selected');
			}
		}
		$('#opnVwCnt' + this.seq).text(this.total);
	}
	this.more = ((elements.rest || !isNaN(elements.rest)) && elements.rest > 0) ? true : false;
	if (this.more) {
		if (this.style.opnView) {
			this.createAreaMore();
		}
	}
	
	if (this.style.sympathy) {
		this.style.sympathy.style.seq = this.seq;
		this.style.sympathy.style.appendRight = this.appendRight;
		var symComp = this.createArea($('#opnVwContent' + this.seq).get(0), this.style.sympathy);
		JSV.register(symComp, this, 'updateSympathyCount');
	}
	
	this.array = elements.array;
	this.length = elements.array.length;
	this.createOpinionCall(this.array);
	
	if (OpnViewer.RIGHTFRAME) {	
		setTimeout(function() {
			OpnViewer.resetScroll();
		}, 500);
	}
}		
		
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
</script>
<%@ include file="/sys/jsv/template/template.body.jsp" %>
<div id="main"></div>
<%@ include file="/sys/jsv/template/template.tail.jsp" %>