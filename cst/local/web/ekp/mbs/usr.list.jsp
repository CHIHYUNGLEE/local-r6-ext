<%@ include file="/sys/jsv/template/template.head.jsp" %>
<%@ include file="/jspf/head.jqueryui.jsp" %>
<%@ include file="/ekp/mbs/config.jsp" %>
<%@ page import="com.kcube.sys.usr.UserService"%>
<%@ page import="com.kcube.ekp.mbs.inapp.MbInApp"%>
<%@ page import="com.kcube.ekp.mbs.MbItemSql" %>
<%@ page import="com.kcube.sys.conf.ConfigService"%>
<%@ page import="com.kcube.cst.local.InterfaceConfig"%>
<%
	MbItemSql mb = new MbItemSql(moduleParam, ctx.getLong("inAppId", null), ctx.getParameter("com.kcube.doc.list"), false);
%>
<%!
	InterfaceConfig _conf = (InterfaceConfig) ConfigService.getConfig(InterfaceConfig.class);
%>
<style>
/****************************************
 * 이미지게시판
****************************************/
/*썸네일뷰*/
.TemplateLayout .TemplateLayoutTitleRight .inChild{font-size:0;}
.TemplateLayout .TemplateLayoutTitleRight .guide_text{display:inline-block;*zoom:1;*display:inline;vertical-align:middle;margin-right:19px;margin-bottom:2px;color:#444;font-size:13px;letter-spacing:-0.25px;line-height:18px;}
.board-info{position:relative;padding:22px 25px 13px 25px;background:#f7f7f7;margin-top:2px;margin-bottom:20px;}
.board-info .admin_info{*zoom:1;padding-bottom:13px;}
.board-info .admin_info:after{content:'';display:block;clear:both;}
.board-info .admin_info .icon{float:left;display:block;width:16px;height:16px;margin-top:1px;margin-right:9px;border:1px solid rgba(0,0,0,0.18);border-radius:50%;box-sizing:border-box;-moz-box-sizing:border-box;}
.board-info .admin_info .icon span{display:block;width:2px;height:8px;margin:3px auto 0 auto;background:url(<c:url value="/img/ico_infox000.png"/>) 0 0 no-repeat;opacity:0.3;font-size:0;text-indent:-9999px;}
.board-info .admin_info .text{color:#111;font-size:13px;letter-spacing:-0.25px;}
.board-info .admin_info .text span:not(.first){color:#333;font-weight:bold;}
.board-info .admin_info .text a{color:#111;}
.board-info .admin_info .text span.first{color:#111; cursor:default;}
.board-info .admin_info .extraAnchor {margin-left: 5px; cursor: pointer; position:relative;}
.board-info .admin_info .extraAnchor:hover {text-decoration: underline;}
.board-info .admin_info .extraAnchor .extraLayer {
	z-index:-1;height:auto;padding:3px 6px 4px 6px;box-shadow: rgba(0, 0, 0, 0.07) 10px 10px 14px;
	position:absolute;left:50%;opacity:0;border:1px solid rgba(74, 74, 74, 0.15);border-radius:2px;background:#ffffff;color:#222;font-size:12px;letter-spacing:-0.5px;white-space:nowrap;
	transition:all .15s ease-out;-moz-transition:all .15s ease-out;-webkit-transition:all .15s ease-out;-ms-transition:all .15s ease-out;-o-transition:all .15s ease-out;
	visibility:hidden;cursor:default; min-width: 50px; 
}
.board-info .admin_info .extraAnchor .extraLayer ul{padding: 4px 0px;}
.board-info .admin_info .extraAnchor .extraLayer li{font-size:13px;letter-spacing:-0.25px;text-align:left;}
.board-info .admin_info .extraAnchor .extraLayer li a,
.board-info .admin_info .extraAnchor .extraLayer li div{position:relative;display:block;min-width:70px;min-height:34px;padding:0 15px;color:#555;overflow:hidden; text-overflow: ellipsis; transition:all .15s ease-out;-moz-transition:all .15s ease-out;-webkit-transition:all .15s ease-out;-ms-transition:all .15s ease-out;-o-transition:all .15s ease-out; line-height: 35px; max-width: 120px;}
.board-info .admin_info .extraAnchor .extraLayer li a:hover{color:#111;background:#f2f2f3;}
.board-info .admin_info .extraAnchor .extraLayer li.selected a{color:#111;}
.board-info .admin_info .extraAnchor .extraLayer.on{z-index:100;opacity:1;visibility:visible;}

.board-info .contents{padding-bottom:6px;color:#333;font-size:13px;letter-spacing:-0.25px;line-height:23px;}
.board-info .RfrnHtmlViewer .rfrnList .rfrn .btns{display:none;}
.board-info .RfrnHtmlViewer .rfrnList .rfrn{padding-left:23px;background:url(<c:url value="/ekp/mbs/img/rfrn_icon.png"/>) top 3px left 0 no-repeat;}
.board-info .FileViewer{margin-left:-9px;}
.board-info .FileViewer .files .btns{display:none;}
.board-info .FileViewer .files .fileName:hover .nameDiv{text-decoration:none;}
.board-info .btn_edit{visibility:hidden;opacity:0;position:absolute;bottom:17px;right:17px;display:inline-block;height:27px;padding-left:10px;padding-right:12px;border-radius:12px;background:rgba(85,85,85,0.4);overflow:hidden;transition:background 150ms ease-out,opacity 150ms ease-out;}
.board-info:hover .btn_edit{visibility:visible;opacity:1;}
.board-info .btn_edit:hover{background:rgba(85,85,85,0.8);}
.board-info .btn_edit span{display:block;margin-top:5px;color:#fff;letter-spacing:-0.5px;white-space:nowrap;}

/*이미지즉시등록*/
.TemplateLayoutLeftView.tableView{position:relative;}
.image-upload{display:none;position:absolute;top:47px;left:0;right:0;bottom:0;z-index:10;border:4px dashed #d7d7d7;border-radius:5px;background:rgba(255,255,255,0.9);}
.image-upload .tbl{display:table;table-layout:fixed;width:100%;height:100%;}
.image-upload .cell{display:table-cell;vertical-align:middle;text-align:center;}
.image-upload .text{padding:30px;color:#aaa;font-size:30px;font-family:'NotoSans',sans-serif;font-weight:500;}

.dragMain{display:none;position:absolute;top:47px;left:0;right:0;bottom:0;z-index:12;padding: 8px;box-sizing:border-box;-moz-box-sizing:border-box;}
.dragMain .tbl{display:table;table-layout:fixed;width:100%;height:100%;}
.dragMain .cell{display:table-cell;vertical-align:middle;text-align:center;}

.LoadingLayer{position: fixed;top: 0;left: 0;width: 1633px;height: 1200px; background: gray; opacity: 0.75;}
.SftpText{font-family:nanumgothic,'Gulim';position: absolute; top: 45%; left: 47%; color: white; font-weight: bold; font-size: 25px}
</style>
<script type="text/javascript" src="<%= request.getContextPath() %>/ekp/mbs/spin.min.js"></script>
<script type="text/javascript">
var spinner;
JSV.Block(function () {
	var inAppId = '<%=MbInApp.ALL_ITEMS%>';
	inAppInfo['appType'] = '<%=MbInApp.ALL_ITEMS%>';
	if (JSV.getParameter('inAppId')) {
		inAppId = JSV.getParameter('inAppId');
		JSV.setState('inAppId', inAppId);
		allItems = inAppId == '<%=MbInApp.ALL_ITEMS%>';
		if (allItems) {
			inAppInfo['appType'] = '<%=MbInApp.ALL_ITEMS%>';
		} else {
			inAppInfo = JSV.loadJSON(JSV.getModuleUrl('/jsl/MbInAppUser.ReadByUser.json?inAppId=' + inAppId));
		}
	}
	
	var lType = TableViewer.getAppOption('ALBUMWEBZINE');
	
	imageMbs = inAppInfo.appType == '<%=MbInApp.APP_ALBUM%>';

	if (allItems || !imageMbs || lType == 'listview') {
		lType = 'list';
	} else if (imageMbs && !parent.imgClick) {
		TableViewer.setAppOption(null, lType);
	}
	
	if (imageMbs)
		parent.imgClick = true;
	
	var titleTemplate = getOpinionTemplate(inAppInfo, lType);
	var formxml = {list:'<column property="id.list"/>'
		 + titleTemplate
		 + '<column property="author.list"/>\
		<column property="rgstDate.list"/>\
		<column property="readCnt.list"/>\
		<column property="attachments.list"/>'};
	
	if (imageMbs) {
		formxml['album'] = '<column property="thumbnail.list"/>'
			 + titleTemplate
			+ '<column property="thumbnail.etc"/>';
		formxml['webzine'] = '<column property="id.list"/>\
			<column property="thumbnail.listWebzine"/>'
			 + titleTemplate
			 + '<column property="author.list"/>\
			<column property="rgstDate.list"/>\
			<column property="readCnt.list"/>\
			<column property="rcmdCnt.list"/>\
			<column property="attachments.list"/>';
	}
	
	var template = '<template checkReadUrl="/jsl/MbItemViewer.ViewListIds.json" color="green" name="com.kcube.doc.list" catalog="/ekp/mbs/catalog.xml.jsp" listType="' + (imageMbs ? 'ALBUMWEBZINE' : 'LIST') + '">\
		<header label="<kfmt:message key="mbs.008"/>"/>\
		<columns class="Array">\
			template += ' + formxml[lType] + '</columns>\
		<search class="Array" periodColumn="rgstDate">\
			<option property="title.list"/>\
			<option property="content.list"/>\
			<option property="author.list"/>\
			<option property="tags.search"/>\
			<option property="id.search"/>\
			<%if(!_conf.getOriginMbs().equals(ctx.getParameter("appId"))){%>\
			<option property="titleContent.search"/>\
			<%}%>\
		</search>'
<% if (mb.isCountCondition()) { %>
		 + '<footer/>'
		 + '<count/>'
		 + '<pageMover/>'
<% } else { %>
		 + '<footer count="disabled"/>'
<% } %>
		+ '<rows name="com.kcube.doc.rowsPerPage"/>\
	</template>';
	var isFvrt = eval(JSV.getParameter('isFvrt')) || false;
	JSV.setState('isFvrt', isFvrt);

	var t;
	if (lType == 'album') {
		t = new ImageTemplate(document.getElementById('main'), template);
		t.setDataUrl('/jsl/MbItemUser.ListByUser.json?type=' + lType + '&inAppId=' + inAppId, 'ts');
	} else {
		t = new TableTemplate(document.getElementById('main'), template);
		t.setDataUrl('/jsl/MbItemUser.ListByUser.json?type=' + lType + '&inAppId=' + inAppId, 'ts', '/jsl/MbItemAnnounce.AnnounceListByUser.json?inAppId=' + inAppId);
	}
	t.header.setLabel(JSV.getLocaleStr(inAppInfo.name));

	ScrapMenu.favoriteList(t.layout.titleLeft, '<%=com.kcube.ekp.mbs.MbItemHistory.ALIMI_MBS%>', 'com.kcube.ekp.mbs.MbItemConfig.fvrtIndexUrl', JSV.getParameter('inAppId'), JSV.getLocaleStr(inAppInfo.name));
	if (!allItems) {
		Subscription.kmId(t.layout.titleLeft, '<%=com.kcube.ekp.mbs.MbItemHistory.ALIMI_MBS%>', inAppInfo.kmId);
	}
	
	if (!allItems && inAppInfo.introduce) {
		var boardInfo = $('<div>').addClass('board-info');
		if (inAppInfo.showManager) {
			var admWrap = $('<div>').addClass('admin_info').html('<span class="icon"><span>!</span></span><p class="text" id="admins"><span><fmt:message key="space.289"/>&nbsp;</span></p>').appendTo(boardInfo);
			if (inAppInfo.masters && inAppInfo.masters.length > 0) {
				var admUl = admWrap.find('#admins');
				var mstr = inAppInfo.masters[0];
				var mText = mstr.id == null ? $('<span>').addClass('first').text(JSV.getLocaleStr(mstr.name)) : AnchorEmp.generateAnchor(mstr.id, mstr.name);
				$(mText).appendTo(admUl);
				if (inAppInfo.masters.length > 1) {
					var extra = $('<a>').addClass('extraAnchor').text('<kfmt:message key="mbs.025"/>' + (inAppInfo.masters.length-1)).appendTo(admUl);
					var extraUl = $('<ul>').addClass('extraLayer').appendTo(extra);
					for (var i = 1; i < inAppInfo.masters.length; i++) {
						mstr = inAppInfo.masters[i];
						mText = mstr.id == null ? $('<div>').text(JSV.getLocaleStr(mstr.name)) : AnchorEmp.generateAnchor(mstr.id, mstr.name); 
						$('<li>').html(mText).appendTo(extraUl);
					}
					$(extra).addClass('tooltip').on('click', extraUl, function(e){
						e.stopPropagation();
						if ($(e.data).hasClass('on')) {
							$(e.data).removeClass('on');
						} else {
							$(e.data).addClass('on');
							$(document).one('click', e.data, function(evd) {
								$(evd.data).removeClass('on');
							});
						}
					});
				}
			}
		}
		if (inAppInfo.intro) {
			$('<p>').addClass('contents').html(JSV.escapeHtml(inAppInfo.intro).replace(/\n/gi, '<br>')).appendTo(boardInfo);
		}
		$(t.layout.pathTable).after(boardInfo);
		
		if (inAppInfo.references && inAppInfo.references.length > 0) {
	 		var appRfrn = new RfrnHtmlViewer(boardInfo, {});
	 		appRfrn.setValue(inAppInfo.references);
		}
		
		if (inAppInfo.attachments && inAppInfo.attachments.length > 0) {
	 		var appFile = new FileViewer(boardInfo, {'inline':'/jsl/inline/MbInAppUser.DownloadByUser?id=@{id}',
				'attach':'/jsl/attach/MbInAppUser.DownloadByUser?id=@{id}'});
	 		appFile.setValue(inAppInfo.attachments);
	 		boardInfo.show();
		}
		
		var isAdmin = <%=isAdmin(moduleParam)%>;
		if (!isAdmin && inAppInfo.masters && inAppInfo.masters.length > 0) {
			for (var i = 0; i < inAppInfo.masters.length; i++) {
				isAdmin = inAppInfo.masters[i].id == <%=UserService.getUserId()%>;
			}
		}
		
		if (isAdmin) {
			$('<a>').addClass('btn_edit').html('<span><kfmt:message key="mbs.017"/></span>').on('click', this, function(e){
				JSV.doGET('/ekp/mbs/inapp/mstr.introEdit.jsp?id=' + inAppInfo.id);
			}).appendTo(boardInfo);
		}
	}

	if (!imageMbs && inAppInfo.appType != '<%=MbInApp.APP_WIKI%>') {
		var checkBox = new BooleanFieldEditor(t.layout.mainHeadRight, {'message':'<kfmt:message key="mbs.003"/>'});
		checkBox.onclick = function() {
			var listUrl = JSV.getModuleUrl('/jsl/MbItemUser.ListByUser.json?type=' + lType + '&inAppId=' + JSV.getParameter('inAppId') + '&ts=' + JSV.encode(t.viewer.getState()));
			if (this.getValue()) {
				t.setValue(listUrl);
			} else {
				t.setValue(listUrl, JSV.getModuleUrl('/jsl/MbItemAnnounce.AnnounceListByUser.json?inAppId=' + inAppId));
			}
		}
	}
	
	t.onTypeChange = function(newVal, oldVal) {
		//value 를 파라미터에 setState 할 필요 없음. 자동으로 localStorge에 저장됨. 로직을 세우고자 할때만  newValue 와 oldValue 를 사용하면 됨.
		JSV.doGET('usr.list.jsp');
	}
	
	var useHTML5 = window.File && window.FileList && window.FileReader;
	if (imageMbs && useHTML5) {
		$('<div>').addClass('guide_text').text('<kfmt:message key="mbs.015"/>').appendTo(t.layout.titleRight);

		var fileDragArea = $(t.layout.itemView).find('.TemplateLayoutMain');
		$(fileDragArea).before('<div class="image-upload" id="dragLogo"></div>');
		var dragLogo = $('#dragLogo');
		$('<div>').addClass('cell').html('<p class="text"><kfmt:message key="mbs.016"/></p>').appendTo($('<div>').addClass('tbl').appendTo(dragLogo));
		$(fileDragArea).before('<div class="dragMain" id="dragMain"><div class="tbl"><div class="cell"></div></div></div>');
		var dragMain = $('#dragMain');
		
		var fileCount = 0;
		
		function ignoreDrag(e) {
			e.preventDefault();	
		}
		
		function isDragDropFile(e) {
			var dt = e.originalEvent.dataTransfer;
			if (JSV.browser.msieEqualOrOver10) {
				return dt.types != null && ((dt.types.length && dt.types[0] === 'Files') || dt.types.contains('application/x-moz-file'));
			} else {
				return dt.types && (dt.types.indexOf ? dt.types.indexOf('Files') != -1 : dt.types.contains('Files'));
			}
		}
		
		function selectHandler(files) {
			var url = JSV.getContextPath(JSV.getModuleUrl('/ekp/mbs/usr.simple.write.jsp'));
	 		var fObj = {title:'<kfmt:message key="mbs.014"/>', width:479, height:479, resizable:false};
	 		JSV.showLayerModalDialog(url, {files:files, inAppId:inAppInfo.id}, fObj, function(value) {
				if (value) {
					var ctx = new Object();
					ctx.item = value;
		 			$.ajax({
						url : JSV.getContextPath(JSV.getModuleUrl('/jsl/MbItemUser.DoRegister.jsl')),
						context : this,
						type : 'POST',
						dataType : 'json',
						data : ctx,
						success : function(data, status){
							JSV.alert('<fmt:message key="pub.002"/>');
		 					location.reload(); // reload변경
						},
						error : function(xhr){
							location.reload();
						}
					});
				}
			});
		}
		function dragBackground(arr) {
			for (var i = 0; i < arr.length; i++) {
				arr[i].show();
			}
		}

		dragMain.on({'dragover':ignoreDrag
			, 'drop':function(e) {
// 				var items = e.originalEvent.dataTransfer.items;
				if (isDragDropFile(e)) {
// 					if (items) {
						var files = e.originalEvent.files || e.originalEvent.dataTransfer.files;
						var rFiles = null;
						if (e.originalEvent.files) {
							rFiles = files;
						} else {
							rFiles = [];
							for (var i = 0; i < files.length; i++) {
								if (!(!files[i].type && files[i].size % 4096 == 0)) {
									rFiles[rFiles.length] = files[i];
								}
							}
						}
						if (rFiles && rFiles.length > 0) {
							fileCount = rFiles.length;
							dragLogo.hide();
							dragMain.hide();
							selectHandler(rFiles);
						} else {
							dragMain.trigger('dragleave');
						}
// 					}
				}
				ignoreDrag(e);
			}
			,'dragleave':function(e) {
				if (isDragDropFile(e)) {
					dragLogo.hide();
					dragMain.hide();
				}
			}
		});

		var layoutBody = $('#main table.tableLayoutBodyArea').on({'dragover':ignoreDrag
			,'dragenter':function(e) {
				if (isDragDropFile(e)) {
					dragBackground([dragLogo, dragMain]);
				}
				ignoreDrag(e);
			}
		});
		
		function checkExt(filename, notSupported) {
			var limitedExt = [];
			if (notSupported == null)
				return true;
			var bAllowedOption = (notSupported.indexOf('/') == 0 && (notSupported.lastIndexOf('/') == notSupported.length - 1));
			if (bAllowedOption) {
				notSupported = notSupported.substring(1, notSupported.length - 1);
			}
			limitedExt = notSupported.split(',');
			if (limitedExt.length == 0)
				return true;

			var ext = (filename.lastIndexOf('.') > -1) ? filename.substring(filename.lastIndexOf('.') + 1).toLowerCase() : null;
			for (var i = 0; i < limitedExt.length; i++) {
				if (ext == limitedExt[i].toLowerCase()) {
					return bAllowedOption;
				}
			}
			return (!bAllowedOption);
		}
		
		var mainMenu = $(t.layout.mainMenuArea).show();
		var fileDiv = $('<div>').appendTo(t.layout.mainMenuLeft);
		var fileInput = $('<input>').attr({type:'file', multiple:'multiple'}).appendTo(fileDiv)
		.change(function(e) {
			fileCount = e.target.files.length;
			selectHandler(e.target.files);
		});

		if (JSV.browser.msie)
			new FileViewer(fileDiv);

		fileDiv.hide();
		mainMenu.hide();
	}
	
	if (!allItems && inAppInfo.appType != '<%=MbInApp.APP_FOLDER%>') {
		var write = new KButton(t.layout.titleRight, <fmt:message key="btn.doc.010"/>);
		write.onclick = function() {
			JSV.doGET('usr.write.jsp');
		}
	}
	
	if (JSV.getParameter('newItem') && parent && parent.left && parent.left.newMapIds) {
		parent.left.newMapIds();
	}
});
var imageMbs = false;
var inAppInfo = {};
var allItems = true;
function getOpinionTemplate(appInfo, type) {
	var styleStr = '';
	var component = 'TitleColumn';
	
	if (type == 'album') {
		styleStr = ' attribute="title" albumType="true" ts="com.kcube.doc.list" ';
	} else if (type == 'webzine') {
		component = 'WebzineColumn';
		styleStr = ' attribute="title,content,tag" contentHighlight="false" ';
	} else {
		styleStr = ' attribute="title" ';
	}
	
	var str = '<column component="' + component + '">\
		<header label="<fmt:message key="doc.001"/>" sort="title" search="title"/>\
		<style href="usr.read.jsp?id=@{id}" newIcon="rgstDate" ' + styleStr;
	if (!allItems) {
		if (inAppInfo.reply) {
			str += ' position="pos" rplyIcon="true" ';
		 }
	}
	str += ' opnCnt="vrtlOpnCnt" opnView="MbItemOpinion.ViewOpinion" inAppId="inAppId" checkRead="true" userId="<%=com.kcube.sys.usr.UserService.getUserId()%>"/></column>';
	
	return str;
}
TitleColumnLabelProvider.prototype.getText = function(obj, name) {
	var type = inAppInfo['appType'];
	if (type == <%=MbInApp.ALL_ITEMS%> || type == <%=MbInApp.APP_FOLDER%>) {
		return (obj.announced && obj.announced == 1) ? obj[name] : '[' + JSV.getLocaleStr(obj['name']) + '] ' + obj[name];
	} else {
		return obj[name];
	}
}
ImageThumbColumn.prototype.clickEvent = function(element) {
	var url = JSV.getContextPath(JSV.getModuleUrl('/ekp/mbs/ThumbPreviewLayer.jsp'));
	var fObj = {title:'', width:500, height:600, resizable:false, noneTitleBar:true, dialogClass : 'LayerModalDialog overflow'};
	JSV.showLayerModalDialog(url, {itemId:element['id'], inAppId:element['inAppId'], ts:JSV.getParameter('com.kcube.doc.list')}, fObj);
}
//클릭시 로드하는 동안 로딩스피너 생성
TitleColumn.prototype.onclick = function(element, href) {
	if(<%=_conf.getOriginMbs()%> != JSV.getAppId()){
		LoadingLayer();
	}
	JSV.doPOST(href);
}

function LoadingLayer() {
	var existLayer = document.getElementById('LoadingLayer');
	var layerDiv = existLayer ? $(existLayer) : $('<div>').addClass('LoadingLayer').attr('id', 'LoadingLayer').appendTo($('#main'));
	if(spinner){
		spinner.stop();
	}
	layerDiv.empty();
	var opts = {
			lines: 11,
	        length: 23, 
	        width: 4,
	        radius: 20, 
	        corners: 1,
	        rotate: 9, 
	        color: '#f8f8f8', 
	        speed: 1, 
	        trail: 50, 
	        shadow: true, 
	        hwaccel: false, 
	        className: 'spinner', 
	        zIndex: 2e9,
	        top: '35%',  
	        left: '50%'
		};
	spinner = new Spinner(opts).spin(layerDiv.get(0));
	layerDiv.css({'height':'100%', 'width':'100%'});
}
function CloseLoadingLayer() {
	$('#LoadingLayer').remove();
	if(spinner){
		spinner.stop();
	}
}



TableHeader.prototype.doSortableTD = function(header, ico, dsc) { 
	var hLabel = JSV.getLocaleStr(header.label);
	if(ico != null && ico != '') hLabel = '<strong>' + hLabel + '</strong>';
	var a = this.doAnchor(hLabel + ico);
	var sort = '';
	
	//
	var sorts = '';
	if(header.sort.includes('rgstDate')){
		sorts = header.sort.split('_')
	}else{
		sorts = header.sort.split(',')
	}
	//
	
	for (var i = 0; i < sorts.length; i++) {
		if (i != 0)
			sort += '_';
		if (sorts[i].indexOf('TPM') == 0) {
			sort += (dsc ? 'd' : 'a') + 'cstmField' + header.cstmField;
		} else {
			sort += (dsc ? 'd' : 'a') + sorts[i];
		}
	}
	a.data('sort', sort)
	.bind('click', this, function(e) {
		if (e.data.onclick) e.data.onclick($(this).data('sort'));
		return false;
	});
}

</script>
<%@ include file="/sys/jsv/template/template.body.jsp" %>
<div id="main"></div>
<%@ include file="/sys/jsv/template/template.tail.jsp" %>