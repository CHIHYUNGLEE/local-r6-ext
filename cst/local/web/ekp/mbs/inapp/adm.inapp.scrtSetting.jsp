<%@ include file="/jspf/head.portlet.jsp" %>
<%@page import="com.kcube.map.Folder"%>
<script type="text/javascript">
JSV.Block(function () {
	var id = JSV.getParameter('id', '${PORTLET_ID}');
	var key = JSV.getParameter('key', '${PORTLET_ID}');
	var style = {
			'size':11,
			'multiple':true,
			'isDel': true,
			'dataKey':key,
			'searchDataKey':key,
			'isSecurityDialog' : true,
			'title': JSV.getParameter('title', '${PORTLET_ID}')
	};
	var comp = new SecurityCommonListFieldEditor(document.getElementById('groupDiv_${PORTLET_ID}'), style);
	<%-- ScrtList --%>
	comp.getListValue = function(){
		if(id == 'manager' || id == 'exclScrt')
		{
			var param = '&inAppId='+treeFrame.tree.getObject(treeFrame.selectId).getAttribute('inAppId');
			if(id == 'manager'){
				param += '&isManage=true';
			}
			var listUrl = JSV.getModuleUrl(JSV.suffix('/jsl/MbInAppAdmin.ScrtListByAdmin.xml', param));
			return JSV.parseXml(listUrl);
		}
		else if(id == 'readScrt' || id == 'writeScrt' || id == 'rplyScrt')
		{
			var param = '&scrtCode='+(id == 'readScrt' ? <%=Folder.SCRT_CODE[0]%> : (id == 'writeScrt' ? <%=Folder.SCRT_CODE[1]%> : <%=Folder.SCRT_CODE[2]%>))+'&folderId='+treeFrame.selectId;
			var listUrl = JSV.getModuleUrl(JSV.suffix('/jsl/FolderScrt.FolderScrtList.xml', param));
			
			return JSV.parseXml(listUrl);
		}
	}
	
	<%-- Scrt Submit --%>
	comp.getSaveData = function() {
		var saveData = {};
		var value = this.getValue();
		if(id == 'manager' || id == 'exclScrt')
		{
			var arr = [];
			for(var i=0; i < value.length; i++)
			{
				arr[i] = {xid : value[i].xid, title : value[i].title};
			}
			saveData.actionUrl = JSV.getContextPath(JSV.getModuleUrl('/jsl/MbInAppAdmin.UpdateSecurity.jsl'));
			saveData.data = {
					'scrtList' : JSV.toJSON(arr),
					'isManage' : id == 'manager' ? true : false,
					'inAppId' : treeFrame.tree.getObject(treeFrame.selectId).getAttribute('inAppId'),
			};
		}
		else if(id == 'readScrt' || id == 'writeScrt' || id == 'rplyScrt')
		{
			saveData.actionUrl = JSV.getContextPath(JSV.getModuleUrl('/jsl/FolderScrt.SaveFolderScrt.jsl'));
			saveData.data = {
					'folderScrtList' : JSV.toJSON({folderScrtList : value}),
					'scrtCode' : id == 'readScrt' ? <%=Folder.SCRT_CODE[0]%> : (id == 'writeScrt' ? <%=Folder.SCRT_CODE[1]%> : <%=Folder.SCRT_CODE[2]%>),
					'folderId' : treeFrame.selectId,
					'service' : true
			};
		}
		return saveData;
	}
	comp.setLabelProvider(new ListViewerLabelProvider('title'));
	comp.setValue(comp.getListValue());
	$(window).resize();
}, '${PORTLET_ID}');
</script>
<div id="groupDiv_${PORTLET_ID}" style="padding-left:5px;"></div>
<%@ include file="/jspf/tail.portlet.jsp" %>