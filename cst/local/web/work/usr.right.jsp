<%@ include file="/jspf/head.portlet.jsp" %>
<%@ include file="/jspf/head.jqueryui.jsp" %>
<%@ include file="/work/config.jsp" %>
<%@ include file="/work/classLoader.jsp" %>
<%@ include file="/work/jsv-work-ext.jsp" %>
<%@page import="com.kcube.work.Work"%>
<style type="text/css">
.TemplateLayoutRight{background:#FFF;}
.rightSection_wrap${PORTLET_ID}{height:100%;background-color:#FFF;overflow:hidden;padding: 0 20px;}
.rightSection_wrap${PORTLET_ID} #tabList${PORTLET_ID}{margin:15px 0 0 0;}
.rightSection_wrap${PORTLET_ID} .FlexTabList .flxTabUl_lineBlue{border:none;}
.rightSection_wrap${PORTLET_ID} .FlexTabList .flxTabUl .flxTabLi_lineBlue{position:relative;height:31px;padding:0 15px;width:50%;border:none;background-color:#f5f5f5;-moz-box-sizing:border-box;box-sizing:border-box;overflow:hidden;z-index:1;}
.rightSection_wrap${PORTLET_ID} .FlexTabList .flxTabUl .flxTabLi_lineBlue:first-child{}
.rightSection_wrap${PORTLET_ID} .FlexTabList .flxTabUl .flxTabLi_lineBlue .textDiv{margin:9px 0 0 0;height:15px;font-size:13px;font-family:nanumgothic,'Gulim';color:#9ea3a6;font-weight:600;text-align:center;}
.rightSection_wrap${PORTLET_ID} .FlexTabList .flxTabUl .flxTabLi_lineBlue_selected{height:31px;padding:0 15px;background-color:#b2b5b8;border:none;border-bottom:0;z-index:2;}
.rightSection_wrap${PORTLET_ID} .FlexTabList .flxTabUl .flxTabLi_lineBlue_selected .flxSpan{}
.rightSection_wrap${PORTLET_ID} .FlexTabList .flxTabUl .flxTabLi_lineBlue_selected .textDiv{color:#FFF;font-weight:600;}
.rightSection_wrap${PORTLET_ID} .FlexTabList .flxTabUl .flxTabLi_lineBlue_selected .textDiv .chatRefresh{display:inline-block;width:15px;height:13px;background:url(<c:url value="/work/img/btn_refresh_wh.png"/>)  0 0 no-repeat;padding-right: 5px;float: right;background-position:0 -13px;}
.rightSection_wrap${PORTLET_ID} .FlexTabList .flxTabUl .flxTabLi_lineBlue_selected .textDiv .actRefresh{display:inline-block;width:15px;height:13px;background:url(<c:url value="/work/img/btn_refresh_wh.png"/>)  0 0 no-repeat;padding-right: 5px;float: right;background-position:0 -13px;}
.rightSection_wrap${PORTLET_ID} .rightSection{min-width:248px;height:100%;}
.rightSection_wrap${PORTLET_ID} .rightSection .moreBtn{display:block;text-align:center;border-bottom:1px solid #f3f3f3;padding:9px 0 8px 0;}
.rightSection_wrap${PORTLET_ID} .rightSection .moreBtn .txt{color:#b2b5b8;font-size:12px;font-family:Gulim;background:url(<c:url value="/work/img/i_moreBtn.png"/>) right center no-repeat;padding:0 14px 0 0;}
.rightSection_wrap${PORTLET_ID} .rightSection .moreBtn:hover .txt{color:#666;background:url(<c:url value="/work/img/i_moreBtn_hover.png"/>) right center no-repeat;}
.WorkChatWriter .mainInputArea .inputLocation .mainInputText{border-top: 1px solid #ebebeb;border-right: 1px solid #dadada;border-bottom: 1px solid #d1d1d1;border-left: 1px solid #ebebeb;resize: none;font-size: 12px;font-family: 'Gulim';margin: 0;width: 225px;height: 50px;ime-mode: active;padding: 6px 8px 4px 8px;line-height: 18px;background: none;border-radius:3px;outline: 0; -moz-box-sizing: border-box;box-sizing: border-box;-webkit-appearance: none;overflow-y: auto;color: #20232c;overflow:auto;min-height:60px;}
</style>
<script type="text/javascript">
var writerOpen = false;
var listView;
var tabs;
var chatId;
function afterWrite(value) {
	value.userId = value.user.id;
	value.userName = value.user.name;
	value.userDisp = value.user.displayName;
	return value;
}
function afterFileWrite(value){
	value.filename = value.attachments[0].filename;
	value.filesize = value.attachments[0].size;
	value.height = value.attachments[0].height;
	value.fileId = value.attachments[0].id;
	value.path = value.attachments[0].path;
	value.type = value.attachments[0].type;
	value.width = value.attachments[0].width;
	if(value.attachments[0].thumb){
		value.thumbCode = value.attachments[0].thumb.type;
		value.thumbPath = value.attachments[0].thumb.path;
	}
	return value;
}
function feedResize(){
	var feedArea = $('#feedArea');
	var feedAreaHeight = feedArea.parents('div.TemplateLayoutRight').height() - 60;
	feedArea.css('height', feedAreaHeight);
}
function chatResize(){
	var chatArea = $('#chatArea');
	var chatAreaHeight = chatArea.parents('div.TemplateLayoutRight').height() - 60;
	chatArea.css('height', chatAreaHeight);
	
	var listDiv = $('#listDiv${PORTLET_ID}');
	var minusHeight = 175;
	if(!writerOpen){
		minusHeight = minusHeight - $('#writeDiv${PORTLET_ID}').height() + 10;
	}
	var listHeight = listDiv.parents('div.TemplateLayoutRight').height() - minusHeight;
	listDiv.slimScroll({
		height: listHeight,
		width: '99%',
    	alwaysVisible: false,
    	size:'8px',
    	color:'#C9C9C9',
    	start: 'bottom'
	 }).css('height', listHeight).parent().css('height', listHeight);
	if(listView && listView.scrollTo){
		listView.scrollTo();
	}
}
function refreshActHistory(){
	var value = {'id':1};
	tabs.setValue(value);
	tabs.onchange(value);
}
function refreshChat(){
	listView.clearList();
	var url = '/jsl/WorkChatItemUser.ListByUser.json';
	listView.setItemValues(url,null,null,null, '1');
	var chat = JSV.loadJSON(JSV.getModuleUrl('/jsl/WorkChatUser.FindChatByUser.json?workId='+1));
	if(chat){
		chatId = chat.chatId;
		//Chat 등록 
		var $chatWriteDiv = $('#writeDiv${PORTLET_ID}');
		if(chat.status === 3000){
			writerOpen = true;
			$chatWriteDiv.show();
		}else{
			writerOpen = false;
			$chatWriteDiv.hide();
		}
	}
	chatResize();
	listView.scrollTo();
}
function GetMByte${PORTLET_ID}(chatByte) {
	return (chatByte) ? chatByte / 1024 / 1024 : 0;
}
function WorkChatListener(msg){
	if (msg.key == 'chatRecive') {
		if(msg.item) {
	    	var data = afterWrite(msg.item);
	    	listView.nodeCreatItem(data);
			listView.scrollTo();
		}
	} else if (msg.key == 'chatFileSend') {
		if(msg.item) {
	    	var data = afterWrite(msg.item);
    		data = afterFileWrite(data);
    		listView.nodeCreatItem(data);
			listView.scrollTo();
			setFileSize(data.filesize);
		}
	} else if (msg.key == 'chatOpen') {
		writerOpen = true;
		chatWriteControl();
	} else if (msg.key == 'chatClose') {
		writerOpen = false;
		chatWriteControl();
	}  else if (msg.key == 'openSuccess') {
		if(msg.item){
			if(eval(msg.item.isOpen)){
				writerOpen = true;
			}
		}else{
			var chat = JSV.loadJSON(JSV.getModuleUrl('/jsl/WorkChatUser.FindChatByUser.json?workId='+JSV.getParameter("id")));
			if(chat.status === 3000){
				writerOpen = true;
			}
		}
		chatWriteControl();
	}
}
function chatWriteControl(){
	var $chatWriteDiv = $('#writeDiv${PORTLET_ID}');
	if(writerOpen){
		$chatWriteDiv.show();
	}else{
		$chatWriteDiv.hide();
	}
	chatResize();
}
JSV.Block(function () {
	var itemId = JSV.getParameter('itemId', '${PORTLET_ID}');
	var menu =  JSV.getParameter('menu', '${PORTLET_ID}') || 1;
	//FlexTabList
	var tabMenu = '<kfmt:message key="work.049"/>';
	//var role = WorkItem.getRole(JSV.getParameter('id'));
	//var chatEnable = role.isActor || role.isHelper;
	var chatEnable = true;
	if(chatEnable){
		tabMenu = '<kfmt:message key="work.049"/>';	
	}else{
		menu = 2;
	}
	tabs = new FlexTabList(document.getElementById('tabList${PORTLET_ID}'), {'skin':'lineBlue'});
	$(tabMenu.split(',')).each(function(n){
		var option = this.split(':');
		var obj = new Object();
		obj.id = option[0];
		obj.name = option[1];
		tabs.add(option[1], obj);
		if(menu == null && n == 0){
			tabs.setValue(obj);
		} else if(menu == obj.id){			
			tabs.setValue(obj);
		}
	});
		
	if(chatEnable){
		var chat = JSV.loadJSON(JSV.getModuleUrl('/jsl/WorkChatUser.FindChatByUser.json?workId='+JSV.getParameter("id")));
		if(chat){
			chatId = chat.chatId;
			var chatCompStyle = {sessionKey:'${sessionKey}', fileSize:<%=getWorkChatFileSize(moduleParam)%> 
			, totalSize:GetMByte${PORTLET_ID}(FileFieldEditor.getByte('2G')), notSupport:'<%=getNotSupportedExt()%>', chatId:chatId};
			
			var writer = new WorkChatWriter(document.getElementById('writeDiv${PORTLET_ID}'), chatCompStyle);
			<%if(false){%>
			writer.onAfterWrite = function(value){
				value = afterWrite(value);
				listView.nodeCreatItem(value);
				listView.scrollTo();
				JSV.notify(value, this);
			}
			<%}%>
			$('#writeDiv${PORTLET_ID}').hide();
			//Chat 등록 
			if(chat.status === 3000){
				writerOpen = true;
				var $chatWriteDiv = $('#writeDiv${PORTLET_ID}');
				$chatWriteDiv.show();
			}
			//Chat 대화 목록
			var chatArea = document.getElementById('listDiv${PORTLET_ID}');
			listView = new WorkChatListViewer(chatArea,{
				'userId':<%=com.kcube.sys.usr.UserService.getUserId()%>,
				'morePlace':document.getElementById('morePlace${PORTLET_ID}')
			});
			listView.afterWrite = function(value) {
				return afterWrite(value);
			}
		}
	}
	$('#chatRefresh').bind('click', function(e) {
		e.stopPropagation();
		e.preventDefault();
		refreshChat();
		return
	});
	
	//tab onchange
	tabs.onchange = function(value){
		var menu = value.id;
		$('.rightSection').find('.FeedPortlet').remove();
		$('.rightSection').find('.non_contents').remove();
		if(menu == 2){
			$('#chatArea').hide();
			$('#feedArea').show();
			new FeedPortlet(document.getElementById('feedArea'), {'historyType' : 'document', 'itemId' : itemId});
			feedResize();
		}else if(menu == 1 && chatEnable){
			$('#feedArea').hide();
			$('#chatArea').show();
			refreshChat();
		}
	}
	tabs.onchange({'id' : menu});
		
	$(document).unbind('resize.rightSection').bind('resize.rightSection', this, function(e) {
		if (JSV.browser.msie6) {
			if(	chatResize)window.clearTimeout(chatListResize); 
			chatListResize = window.setTimeout(function(){
				if(chatEnable){
					chatResize();
				}
				feedResize();
			}, 200);
		} else {
			if(chatEnable){
				chatResize();
			}
			feedResize();
		}
	});
}, '${PORTLET_ID}');
</script>
<div class="rightSection_wrap${PORTLET_ID}" id="rightSection${PORTLET_ID}">
	<div id ="tabList${PORTLET_ID}"></div>
   	<div class="rightSection" id="chatArea">
		<div id="morePlace${PORTLET_ID}"></div>
   		<div id="listDiv${PORTLET_ID}"></div>
		<div id="writeDiv${PORTLET_ID}" class="inputPlace" style="clear:both;"></div>
   	</div>
   	<div class="rightSection" id="feedArea"></div>
</div>
<%@ include file="/jspf/tail.portlet.jsp" %>