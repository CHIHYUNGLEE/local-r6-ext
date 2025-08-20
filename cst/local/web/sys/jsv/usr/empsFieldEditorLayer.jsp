<%@ include file="/jspf/head.portlet.jsp" %>
<%@ include file="/ekp/emp/config.jsp" %>
<%
	Long dprt = com.kcube.sys.emp.EmployeeService.getEmployee(com.kcube.sys.usr.UserService.getUserId()).getDprtId();
%>
<style type="text/css">
.ui-dialog { z-index: 9999 !important ;}
.ui-widget-overlay { z-index: 9998 !important ;}
/*공통*/
#empsLayer${PORTLET_ID} *{font-family:'Malgun Gothic','맑은 고딕';}
#empsLayer${PORTLET_ID} {position:relative;height:100%;overflow:hidden;box-sizing:border-box;-moz-box-sizing:border-box;}
#empsLayer${PORTLET_ID}.has-bottomBtn{padding-bottom:94px;}
#empsLayer${PORTLET_ID} .contents{height:100%;overflow:auto;padding:0;}
#empsLayer${PORTLET_ID} .bottom_btns{position:absolute;bottom:0;left:0;right:0;}
#empsLayer${PORTLET_ID} .bottom_btns .tiButtons{padding:30px;font-size:0;text-align:center;}
#empsLayer${PORTLET_ID} .bottom_btns .tiButtons .KButton{margin:0 5px;}
#empsLayer${PORTLET_ID} .groupDiv{white-space:nowrap;}
#empsLayer${PORTLET_ID} .groupDiv > .leftArea, .commonComponent-layer .groupDiv .middleArea, .commonComponent-layer .groupDiv .rightArea{vertical-align:top;display:inline-block;*zoom:1;*display:inline;height:100%;box-sizing:border-box;-moz-box-sizing:border-box;}
#empsLayer${PORTLET_ID} .groupDiv .middleArea{text-align:center;}
#empsLayer${PORTLET_ID} .tree_scroll_area{padding:12px 15px;box-sizing:border-box;-moz-box-sizing:border-box;}
#empsLayer${PORTLET_ID} .TreeMenuSearchResult{margin:0;}
#empsLayer${PORTLET_ID} .TreeMenuSearchResult .resultCt .box{padding:0;}
/*사용자다중선택*/
#empsLayer${PORTLET_ID} .sort{padding:30px 30px 0 30px;}
#empsLayer${PORTLET_ID} .sort .groupDiv{height:464px;}
#empsLayer${PORTLET_ID} .sort .groupDiv > .leftArea{width:390px;}
#empsLayer${PORTLET_ID} .sort .groupDiv .middleArea{width:68px;}
#empsLayer${PORTLET_ID} .sort .groupDiv .rightArea{width:320px;}
#empsLayer${PORTLET_ID} .sort .leftInner{position:relative;border:1px solid #e7e9ee;border-radius:4px;overflow:hidden;}
#empsLayer${PORTLET_ID} .sort .axis_tab{background:#f7f7f7;overflow:hidden;}
#empsLayer${PORTLET_ID} .sort .axis_tab .FlexTabList_rx-axis{height:32px;padding:0 16px 0 8px;margin:13px 0 0 0;border-color:#ebebeb;}
#empsLayer${PORTLET_ID} .sort .axis_tab .FlexTabList_rx-axis li{margin-left:8px;}
#empsLayer${PORTLET_ID} .sort .axis_tab .FlexTabList_rx-axis li .textDiv{height:31px;color:#333;border-color:#ebebeb;}
#empsLayer${PORTLET_ID} .sort .axis_tab .FlexTabList_rx-axis li:hover .textDiv{color:#000;border-color:#9e9e9e;}
#empsLayer${PORTLET_ID} .sort .axis_tab .FlexTabList_rx-axis li.flxTabLi_rx-axis_selected .textDiv{color:#407aff;border-color:#407aff;}
#empsLayer${PORTLET_ID} .sort .header_search{height:46px;border-bottom:1px solid #eaeaea;}
#empsLayer${PORTLET_ID} .sort .header_search .TableSearch{height:100%;margin-top:0;}
#empsLayer${PORTLET_ID} .sort .header_search .TableSearch .selectPrt{display:none;}
#empsLayer${PORTLET_ID} .sort .header_search .TableSearch .period{display:none;}
#empsLayer${PORTLET_ID} .sort .header_search .TableSearch .iptBar{width:100%;background:url(<c:url value="/img/ico/ico_schList.png"/>) top 15px left 15px no-repeat}
#empsLayer${PORTLET_ID} .sort .header_search .TableSearch .iptBar:after{left:50%;background:#518cfc;bottom:0;}
#empsLayer${PORTLET_ID} .sort .header_search .TableSearch .iptBar.on:after{width:50%;}
#empsLayer${PORTLET_ID} .sort .header_search .TableSearch .iptBar:before{content:'';position:absolute;top:inherit;bottom:0;left:55%;display:block;width:0;height:2px;background:#518cfc;transition:all .15s ease-out;-moz-transition:all .15s ease-out;-webkit-transition:all .15s ease-out;-ms-transition:all .15s ease-out;-o-transition:all .15s ease-out;}
#empsLayer${PORTLET_ID} .sort .header_search .TableSearch .iptBar.on:before{left:0;width:50%;}
#empsLayer${PORTLET_ID} .sort .header_search .TableSearch .iptBar .ipt{padding:0 35px 0 38px;font-family:'Malgun Gothic','맑은 고딕';}
#empsLayer${PORTLET_ID} .sort .header_search .TableSearch .iptBar .clear{top:11px;right:10px;box-sizing:content-box;-moz-box-sizing:content-box;-o-box-sizing:content-box;-ms-box-sizing:content-box;}
#empsLayer${PORTLET_ID} .sort .tree_area .TreeViewer .treeTxtDiv .btn_view{float:right;display:block;padding:5px 3px;opacity:0.8;}
#empsLayer${PORTLET_ID} .sort .tree_area .TreeViewer .treeTxtDiv .btn_view > img{display:block;}
#empsLayer${PORTLET_ID} .sort .tree_area .checkbox_list{padding:0;}
#empsLayer${PORTLET_ID} .sort .tree_area .checkbox{padding-top:11px;}
#empsLayer${PORTLET_ID} .sort .tree_area .checkbox input[type="checkbox"]{position:absolute;display:none;}
#empsLayer${PORTLET_ID} .sort .tree_area .checkbox input[type="checkbox"]+ label{position:relative;display:inline-block;padding-left:24px;}
#empsLayer${PORTLET_ID} .sort .tree_area .checkbox input[type="checkbox"]+ label .text{color:#111;font-size:13px;letter-spacing:-0.25px;white-space:normal;}
#empsLayer${PORTLET_ID} .sort .tree_area .checkbox input[type="checkbox"]+ label .text .titleHighlight{color:#ef4036;font-weight:normal;}
#empsLayer${PORTLET_ID} .sort .tree_area .checkbox input[type="checkbox"]+ label .user{color:#111;font-size:13px;letter-spacing:-0.25px;white-space:normal;display:inline-block;width:140px;}
#empsLayer${PORTLET_ID} .sort .tree_area .checkbox input[type="checkbox"]+ label .user .titleHighlight{color:#ef4036;font-weight:normal;}
#empsLayer${PORTLET_ID} .sort .tree_area .checkbox input[type="checkbox"]+ label:before{content:'';position:absolute;left:0;top:0;width:15px;height:15px;border:1px solid #d9d9d9;border-radius:3px;background:#FFF;}
#empsLayer${PORTLET_ID} .sort .tree_area .checkbox input[type="checkbox"]+ label:hover:before{border:1px solid #ababab;}
#empsLayer${PORTLET_ID} .sort .tree_area .checkbox input[type="checkbox"]:checked+ label:after{content:'';position:absolute;top:5px;left:3px;display:block;width:11px;height:8px;background:url(<c:url value="/img/layout/chkbox_checked.png"/>) 0 0 no-repeat;}
#empsLayer${PORTLET_ID} .sort .tree_area .checkbox input[type="checkbox"]:checked+ label .text{color:#407aff;font-weight:bold;}
#empsLayer${PORTLET_ID} .sort .tree_area .checkbox input[type="checkbox"]:checked+ label .text .titleHighlight{color:#407aff;font-weight:bold;}
#empsLayer${PORTLET_ID} .sort .tree_area .checkbox input[type="checkbox"]:checked+ label .user{color:#407aff;font-weight:bold;}
#empsLayer${PORTLET_ID} .sort .tree_area .checkbox input[type="checkbox"]:checked+ label .user .titleHighlight{color:#407aff;font-weight:bold;}
#empsLayer${PORTLET_ID} .sort .tree_area .tab_content_space{height: 100%;overflow-y: auto;}
#empsLayer${PORTLET_ID} .sort .tree_area .tab_content_space .space_list .member_item{position: relative;margin: 0 0 10px 0;padding: 0 0 0 19px;height: 13px;}
#empsLayer${PORTLET_ID} .sort .tree_area .tab_content_space .space_list .member_item:after{display: block;content: "";clear: both;}
#empsLayer${PORTLET_ID} .sort .tree_area .tab_content_space .space_list .member_item .bu_boxArr{position: absolute;left: 0;top: 1px;width: 11px;height: 11px;background: url(<c:url value="/sys/jsv/xid/bu_boxArr_b.gif"/>) 0 0 no-repeat;}
#empsLayer${PORTLET_ID} .sort .tree_area .tab_content_space .space_list .member_item .member_txt{display: inline-block;max-width:100%;height: 13px;line-height: 15px;text-overflow: ellipsis;overflow: hidden;white-space: nowrap;font-weight: bold;}
#empsLayer${PORTLET_ID} .sort .member_area{position:relative;padding:4px 0;height:171px;}
#empsLayer${PORTLET_ID} .sort .member_area.list {border-top:0px;background:#ffffff;}
#empsLayer${PORTLET_ID} .sort .member_area .list_viewer .ListViewer{background:#f7f7f7;border-radius:0;border-width:0;border-top:1px solid #eaeaea;}
#empsLayer${PORTLET_ID} .sort .middle_icon{position:relative;display:inline-block;margin-top:244px;font-size:0;}
#empsLayer${PORTLET_ID} .sort .middle_icon .ico_area{display:inline-block;padding:8px;}
#empsLayer${PORTLET_ID} .sort .middle_icon .ico_area img{position:relative;z-index:1;display:block;width:18px;height:18px;opacity:0.5;}
#empsLayer${PORTLET_ID} .sort .middle_icon .ico_area:before{content:'';position:absolute;top:0;bottom:0;left:0;right:0;border-radius:50%;background:rgba(0,0,0,0.05);}
#empsLayer${PORTLET_ID} .sort .CommonListFieldEditor .leftArea{border:1px solid #e7e9ee;border-radius:4px;background:#f7f7f7;}
#empsLayer${PORTLET_ID} .CommonListFieldEditor .leftArea .btnArea .KButton .tooltip{left:0}
</style>
<script type="text/javascript">
JSV.Block(function () {
	var isGroup = JSV.getParameter('isGroup', '${PORTLET_ID}') && JSV.getParameter('isGroup', '${PORTLET_ID}') == 'true'; 
	var teamRootId = JSV.getParameter('teamRootId', '${PORTLET_ID}');
	var role = JSV.getParameter('role', '${PORTLET_ID}');
	var id = JSV.getParameter('id', '${PORTLET_ID}') || '';
	
	<%-- User model--%>
	var model;
	var openerComponent = 'EmpsFieldEditor' + id;
	if (window[openerComponent].getData) {
		model = JSV.clone(window[openerComponent].getData());
	}
	if(!model)
		model = [];
	var addModel = [];
	
	var isMaster = '';
	if (role)
		isMaster = '&isMaster=true';
 	
 	var dprtId = <%=getDprtId()%>;
 	var selDprtId = JSV.getParameter('dprtId', '${PORTLET_ID}') || '<%= dprt %>';
 	
 	console.log(JSV.getParameter('dprtId', '${PORTLET_ID}'));//null
 	console.log('<%= dprt %>');//null
 	console.log(dprtId);//7
	console.log(selDprtId);//null
	
	if (selDprtId == 'null')
		selDprtId = dprtId;
	
	if (teamRootId) {
		dprtId = teamRootId;
		var teamUserTree = new LazyXMLTreeContentProvider(
			'/jsl/FolderSelector.ChildrenList.xml?includeUser=true&isNative=true' + isMaster,
			'/jsl/FolderSelector.AncestorsOrSelf.xml?includeUser=true&isNative=true' + isMaster
		);
		teamUserTree.inputChanged = function(view, oldInput, newInput) {
			this.roots = [];
			this.nodes = {};
			this.children = {};
			var children = $(JSV.loadXml(newInput)).children();
			for (var i = 0; i < children.length; i++) {
				var child = children[i];
				var id = this.getId(child);
				if (id == teamRootId) {
					$(child).removeAttr('pid');
				}
				
				var pid = this.getParentId(child);
				this.nodes[id] = child;
				if (pid) {
					if (this.children[pid] == null)
					{
						this.children[pid] = [];
					}
					this.children[pid].push(child);
				} else {
					this.roots[this.roots.length] = child;
				}
			}
		}
	}
	
	<%-- CommonListFieldEditor --%>
	var style = {
		multiple: true,
		isDel: true,
		isDynamicDelete: true,
		size: 12,
		titleTxt: '<fmt:message key="folder.117"/>',
		scrollDistance: '5px'
	};
	var selectList = new CommonListFieldEditor($('#rightAreaScroll${PORTLET_ID}'), style);
	selectList.onDelClick = function(dataObj, delBtn, clickId, succDelFunc) {
		succDelFunc();
		selectListDel('u' + clickId);
		addModel.remove(dataObj, equals);
	}
	selectList.dynamicDelete.beforeFunc = function() {
		var selection = selectList.clv.getSelection();
		var len = selection.length;
		if (len > 0) {
			for (var i = 0; i < len; i++) {
				selectListDel('u' + selection[i].id);
				addModel.remove(selection[i], equals);
			}
		} else {
			treeTd.find('.treeTxt').removeClass('TreeViewer_Selected');
			treeTd.find('input').prop('checked', false);
			addModel = [];
		}
	}
	selectList.setLabelProvider(new ListViewerLabelProvider('title'));
	function selectListDel(objId) {
		if (tree && tree.nodes[objId]) {
			tree.checkNode(objId, false);
		}
	}
	
	//전사, 회원
	var empsAxis = {'id':0, 'rootId':1000, 'name':'<fmt:message key="hrm.033"/>'};
	var mbrsAxis = {'id':1, 'rootId':1000, 'name':'<fmt:message key="space.198"/>'};	
	<%-- 전사 Tree --%>
	var treeTd = $('#treeTd${PORTLET_ID}');
	var searchDiv = $('#searchDiv${PORTLET_ID}');
	var searchResultTd = $('#searchResultTd${PORTLET_ID}');
	var isLoaded = false;
	var searchDprtId = dprtId;
	var dprtName;
	var placeholder = '<fmt:message key="hrm.150"/>';
	var tree;
	function loadTree() {
		tree = new TreeViewer(treeTd, {useCheckBox:true, useCheckBoxTxtClk:true, excludeCheckAttr:'isUser', excludeCheckAttrValue:'false'});
		tree.setContentProvider(teamRootId ? teamUserTree : new LazyXMLTreeContentProvider(
			 '/jsl/FolderSelector.ChildrenList.xml?includeUser=true&isNative=true' + isMaster,
			 '/jsl/FolderSelector.AncestorsOrSelf.xml?includeUser=true&isNative=true' + isMaster
		));
		tree.setInput('/jsl/FolderSelector.SelfOrChildren.xml?includeUser=true&isNative=true&id=' + dprtId + isMaster, selDprtId);
		tree.onclick = function(obj) {
			if ($(obj).attr('isUser') == 'false') {
				dprtName = $(obj).attr('id') == dprtId ? '<fmt:message key="doc.188"/>' : JSV.getLocaleStr($(obj).text());
				placeholder = '<fmt:message key="hrm.150"/>';
				placeholder = placeholder.replace('$dprtName', dprtName);
				searchDiv.find('.ipt').attr('placeholder', placeholder);
				searchDprtId = $(obj).attr('id');
			}
		}
		tree.onchkclick = function(obj, val) {
			var user = {};
			var userid = $(obj).attr('userId');
			var objId = $(obj).attr('id');
			if (userid) {
				user.id = userid;
				user.name = $(obj).attr('userName');
				user.title = $(obj).attr('displayName');
				user.displayName = $(obj).attr('displayName');
			} else {
				var ob = this.getObject(objId);
				user.id = $(ob).attr('id');
				user.name = $(ob).attr('userName');
				user.title = $(ob).attr('displayName');
				user.displayName = $(ob).attr('displayName');
			}
			if (val) {
				if (addModel.indexOf(user, equals) == -1) {
					addModel.push(user);
				}
			} else {
				addModel.remove(user, equals);
			}
			selectList.setValue(addModel);
		}
		isLoaded = true;
		placeholder = placeholder.replace('$dprtName', '<fmt:message key="doc.188"/>');
		var search = new TableSearch(searchDiv, {'placeholderName':placeholder});
		var searchResult = new TreeMenuSearchResult(searchResultTd, tree, {'axisList':{'array':[empsAxis]}, 'useCheckBox':true, 'includeUser':true, 'isMaster':role ? true : false, 'dprtId':dprtId});
		JSV.register(search, {
			notify:function(value) {
				treeTd.hide();
				searchResultTd.show();
				var arr = value.search.split('_');
				if (arr[1].trim() != '') {
					searchResult.search(searchDprtId, arr[1], '/jsl/FolderSelector.SelfOrChildren.xml?id=', dprtName);
				}
			}
		});
		search.clear = search.$widget.find('span.clear').click(this, function(e) {
			$(this).hide();
			searchResult.hide();
			treeTd.show();
		});
	}
	
	<%-- 회원 ListViewer --%>
	var listViewerDiv = $('#listViewerDiv${PORTLET_ID}');
	var listViewer = new ListViewer(listViewerDiv, {'name':'user', 'height':171});
	listViewer.setLabelProvider(new ListViewerLabelProvider('displayName'));
	listViewer.onclick = function(obj) {
		var ob = {};
		ob.id = obj.userId;
		ob.name = JSV.getLocaleStr(obj.userName);
		ob.title = JSV.getLocaleStr(obj.displayName);
		ob.displayName = JSV.getLocaleStr(obj.displayName);
		if (addModel.indexOf(ob, equals) == -1) {
			addModel.push(ob);
		}
		selectList.setValue(addModel);
	}
	
	var tab = new FlexTabList($('#axisDiv${PORTLET_ID}'), {'skin':'rx-axis','isPaging':true});
	tab.onchange = function(axis) {
		searchDiv.hide();
		searchResultTd.hide();
		treeTd.hide();
		$('.tab_content_space').hide();
		if (axis.id == 0) {
			searchDiv.show();
			treeTd.show();
			if (!isLoaded) {
				loadTree();
			}
		} else {
			$('.tab_content_space').show();
		}
		doRight(axis.id);
	}
	tab.add(empsAxis.name, empsAxis);
	
	var defaultHeight = 417;
	if (isGroup) {
		defaultHeight = 371;
		tab.add(mbrsAxis.name, mbrsAxis);
		tab.setValue(mbrsAxis);
		tab.onchange(mbrsAxis);
	} else {
		$('#axisDiv${PORTLET_ID}').hide();
		tab.setValue(empsAxis);
		tab.onchange(empsAxis);
	}
	
	<%-- Buttons --%>
	var buttonDiv = $('#buttonDiv${PORTLET_ID}');
	var ok = new KButton(buttonDiv, <fmt:message key="btn.pub.ok.icon"/>);
	ok.onclick = function() {
		for (var i = 0; i < addModel.length; i++) {
			if (model.indexOf(addModel[i], equals) == -1) {
				model.push(addModel[i]);
			}
		}
		if (window[openerComponent].validate) {
			var validator = window[openerComponent].validate(model);
			if(validator.result == false){
				JSV.alert(validator.msg);
				return;
			}
		}
		JSV.changeDialog(function () {
			if(window.setEmpData) {
				window.setEmpData(model);
			}
			if (window[openerComponent].onok) {
				window[openerComponent].onok(model);
			}
		});
		JSV.layerDialogClose();
	}
	var cancel = new KButton(buttonDiv, <fmt:message key="btn.pub.cancel_bg"/>);
	cancel.onclick = function() {
		JSV.layerDialogClose();
	}
	
	function equals(obj1, obj2) {
		return (obj1.id == obj2.id);
	}
	function doRight(axisId) {
		var height;
		var memberDiv = $('#memberDiv${PORTLET_ID}');
		memberDiv.hide();
		listViewerDiv.hide();
		if (axisId == 0) {
			height = defaultHeight;
		} else {
			height = defaultHeight - 135;
			memberDiv.show();
			listViewerDiv.show();
			listViewer.setValue(JSV.loadJSON('/jsl/WorkSpaceMember.WorkingList.json?' + JSV.MODNAMES.SPACEID + '=' + JSV.getSpaceId() + '&isAll=true'));
		}
		$('#treeScroll${PORTLET_ID}').slimscroll({
			height: height + 'px',
			opacity: '0.1',
			color: '#000',
			distance: '5px',
			borderRadius: '8px',
			size: '8px',
			alwaysVisible : true
		});
	}
}, '${PORTLET_ID}');
</script>
<div class="commonComponent-layer has-bottomBtn" id="empsLayer${PORTLET_ID}">
	<div class="contents">
		<div class="sort">
			<div class="groupDiv">
				<div class="leftArea">
					<div class="leftInner">
						<div class="axis_tab">
							<div align="left" class="TreeMenu_axis" id="axisDiv${PORTLET_ID}" style="display: block;"></div>
						</div>
						<div class="header_search" id="searchDiv${PORTLET_ID}"></div>
						<div class="tree_area">
							<div id="treeScroll${PORTLET_ID}" class="tree_scroll_area">
								<table cellspacing="0px" cellpadding="0px" class="TreeMenu_treeTable" style="">
									<tbody>
										<tr>
											<td class="mapTd" id="searchResultTd${PORTLET_ID}" style="display: block;"></td>
											<td class="mapTd" id="treeTd${PORTLET_ID}" style="display: block;"></td>
										</tr>
										<div class="tab_content_space">
											<ul class="space_list">
												<li class="member_item">
													<i class="bu_boxArr">&nbsp;</i>
													<a id="member_btn" class="member_txt"><fmt:message key="space.308"/></a>
												</li>
											</ul>
										</div>
									</tbody>
								</table>
							</div>
						</div>
						<div class="member_area" id="memberDiv${PORTLET_ID}" style="display:bolck;">
							<div id="listViewerDiv${PORTLET_ID}" class="list_viewer"></div>
						</div>
					</div>
				</div>
				<div class="middleArea">
					<div class="middle_icon"><span class="ico_area"><img src="<c:url value="/img/ico/ico_move_right.png"/>" class="ico"></span></div>
				</div>
				<div class="rightArea">
					<div id="rightAreaScroll${PORTLET_ID}" class="slimScrollArea"></div>
				</div>
			</div>
		</div>
	</div>
	<div class="bottom_btns"><div class="tiButtons" id="buttonDiv${PORTLET_ID}"></div></div>
</div>
<%@ include file="/jspf/tail.portlet.jsp" %>