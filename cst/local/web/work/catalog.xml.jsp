<%@ include file="/jspf/head.xml.jsp" %>
<%@ include file="/work/config.jsp" %>
<%@ include file="/work/classLoader.jsp" %><%
	com.kcube.sys.emp.Employee emp = com.kcube.sys.emp.EmployeeService.getEmployee(com.kcube.sys.usr.UserService.getUserId());
	Long dprtId = emp.getDprtId();
	String dprtName = com.kcube.lib.secure.SecureUtils.XSSFilter(com.kcube.sys.i18n.I18NService.getLocalLanguage(emp.getDprtName()));
%>
<registry>
	<properties>
		<id>
			<select component="CheckboxColumn" width="40px">
				<header label="<fmt:message key="doc.014"/>" toggle="id"/>
				<style name="id" attribute="id"/>
			</select>
			<hidden component="HiddenFieldEditor" parent="null" id="id"/>
		</id>
		<title>
			<write component="TextFieldEditor" focus="true">
				<header label="<fmt:message key="doc.001"/>" required="true"/>
				<style maxLength="55"/>
			</write>
			<list component="TitleColumn">
				<header label="<fmt:message key="doc.001"/>" sort="title" search="title"/>
				<style href="usr.read.jsp?id=@{id}" attribute="title" newIcon="rgstDate"
				isSympathy="<%=isSympathy(moduleParam)%>"
				opnCnt="opnCnt" opnView="WorkOpinion.ViewOpinion"/>
			</list>
			<reqList component="TitleColumn">
				<header label="<fmt:message key="doc.001"/>" sort="title" search="title"/>\
				<style attribute="title" href="" newIcon="reqDate" isNowrap="true"/>\
			</reqList>
			<admList component="TitleColumn" >
				<header label="<fmt:message key="doc.001"/>" sort="title" search="title"/>
				<style href="javascript:popup('@{id}');" attribute="title" newIcon="rgstDate" />
			</admList>
			<read component="MainTitleViewer" id="readTitle" name="title" viewer="true">
				<style detailId="detailInfo">
					<title component="TitleViewer" reference="title">
						<style className="mainTitle_title" fvrtAction="doFvrt"/>
					</title>
					<right>
						<statusEditor id="WorkStatus" observe="apprReviewSetting" component="WorkStatusFieldEditor" reference="status">
							<style attribute="id" completeDate="completeDate" editable="true" className="TaskStatusFieldEditor mainTitle_rcmdCnt" action="/jsl/WorkActor.UpdateStatus.json"/>
						</statusEditor>
					</right>
					<bottom>
						<scrtLevel component="WorkSecurityGradeViewer" reference="id,scrtLevel,isHelper,isShare">
							<style options="<kfmt:message key="work.options.scrt"/>" actionUrl="WorkUser.ViewSecurities"/>
						</scrtLevel>
						<sep1/>
						<author component="EmpHtmlViewer" reference="author" styleType="authorInfo">
							<style useMainTitle="true"/>
						</author>
						<sep2/>
						<rgstDate component="DateViewer" reference="lastUpdt" styleType="dateInfo">
							<style format="<fmt:message key="date.longTohour"/>"/>
						</rgstDate>
						<sep3/>
						<readCnt component="ReadCountViewer" reference="id,readCnt" styleType="viewInfo">
							<style isReadCntPopup="<%= isReadCntPopup(moduleParam) %>" className="mainTitle_readCnt" 
								count="WorkViewer.ListCount" detail="WorkViewer.DetailListCount" />
						</readCnt>
					</bottom>
				</style>
			</read>
			<readAdmin component="TitleViewer" id="title">
				<header label="<fmt:message key="doc.001"/>"/>
			</readAdmin>
			<reportList component="TitleColumn">
				<header label="<fmt:message key="doc.001"/>" sort="title" search="title"/>
				<style href='/work/prcs.read.jsp?prcsId=@{id}&amp;id=@{itemId}' attribute="title" newIcon="reqDate"/>
			</reportList>
		</title>
		<folder>
			<hidden component="HiddenFieldEditor" parent="null"/>
			<write component="FolderCompositeEditor" required="true" id="folder">
				<header label="<kfmt:message key="work.006"/>" required="true"/>
				<style scrtCode="2000" viewer="FolderTextViewer" editor="FolderFieldEditor" idx="0"/>
			</write>
			<read component="FolderViewer" id="folder">
				<header label="<kfmt:message key="work.006"/>"/>
			</read>
		</folder>
		<actor>
			<write component="EmpFieldEditor" id="actor">
				<header label="<kfmt:message key="work.002"/>"  required="true"/>
			</write>
			<read component="EmpHtmlViewer" id="actor">
				<header label="<kfmt:message key="work.002"/>"/>
				<style vrtl="<fmt:message key="sys.vrtl.userId"/>"/>
			</read>
			<list component="EmpColumn" sort="actorName" width="127px">
				<header label="<kfmt:message key="work.002"/>" sort="actorName" search="actorName"/>
				<style id="actorUserId" name="actorName" vrtl="<fmt:message key="sys.vrtl.userId"/>"/>
			</list>
		</actor>
		<charge>
			<write component="DprtCompositeEditor"   name="chargeDprtId,chargeDprtName" id="charge">
				<header label="<kfmt:message key="work.003"/>" required="true" />
				<style  idx="-1" viewer="FolderTextViewer" editor="FolderFieldEditor" code="1000" typeOrg="true" img="<kfmt:message key="work.btn.001"/>"
				dprtId="<%= dprtId %>" 
				dprtName="<%= dprtName %>"
				/>
			</write>
			<read component="TextViewer" name="chargeDprtName">
				<header label="<kfmt:message key="work.003"/>" />
			</read>
		</charge>
		<author>
			<hidden name="author" component="HiddenFieldEditor" parent="null"/>
		</author>
		<reqUser>
			<list component="EmpColumn" sort="reqUserName" width="127px">
				<header label="<kfmt:message key="work.055"/>" sort="reqUserName" search="reqUserName"/>
				<style id="reqUserId" name="reqUserName" vrtl="<fmt:message key="sys.vrtl.userId"/>"/>
			</list>
			<reqList component="EmpColumn" sort="reqUserName" width="127px">
				<header label="<kfmt:message key="work.055"/>" sort="reqUserName" />
				<style id="reqUserId" name="reqUserName" vrtl="<fmt:message key="sys.vrtl.userId"/>"/>
			</reqList>
			<takeList component="EmpColumn" sort="reqUserName" width="127px">
				<header label="<kfmt:message key="work.114"/>" sort="reqUserName" />
				<style id="reqUserId" name="reqUserName" vrtl="<fmt:message key="sys.vrtl.userId"/>"/>
			</takeList>
		</reqUser>
		<resUser>
			<list component="EmpColumn" sort="resUserName" width="127px">
				<header label="<kfmt:message key="work.002"/>" sort="resUserName" search="resUserName"/>
				<style id="resUserId" name="resUserName" vrtl="<fmt:message key="sys.vrtl.userId"/>"/>
			</list>
			<takeList component="EmpColumn" sort="resUserName" width="127px">
				<header label="<kfmt:message key="work.115"/>" sort="resUserName" search="resUserName"/>
				<style id="resUserId" name="resUserName" vrtl="<fmt:message key="sys.vrtl.userId"/>"/>
			</takeList>
		</resUser>
		<securities>
			<list component="ComboColumn" width="90px" name="scrtLevel">
				<header label="<fmt:message key="doc.021"/>" sort="scrtLevel" />
				<style options="<kfmt:message key="work.078"/>" attribute="scrtLevel"/>
			</list>
			<read component="ComboViewer" name="scrtLevel">
				<header label="<fmt:message key="doc.021"/>"/>
				<style options="<fmt:message key="doc.064"/>"/>
			</read>
			<write component="WorkSecurityFieldEditor" name="scrtLevel,securities" id="scrt">
				<header label="<fmt:message key="doc.021"/>"/>
				<style options="<kfmt:message key="work.150"/>" checkBox="true" dialogOption="opnerComponent=SecurityRadioFieldEditor" />
			</write>
			<hidden component="HiddenFieldsEditor"><style name="security"/></hidden>
		</securities>
		<helpers>
			<write component="WorkOrgSelectFieldEditor" id="helpers" name="helpers" observe="scrt,actor">
				<header label="<kfmt:message key="work.004"/>" />
				<style autoComplete="true" type="4000" showPopupComponent="EmpsFieldEditor" label="<kfmt:message key="work.004"/>">
					<autoCompleteStyle searchUrl="/jsl/EmployeeSelector.SelectUserByNameQuery.json" isId="true"/>
				</style>
			</write>
			<list component="WorkHelperColumn" width="127px">
				<header label="<kfmt:message key="work.002"/>"/>
			</list>
		</helpers>
		<exprDate>
			<list component="DateColumn" width="80px">
				<header label="<fmt:message key="doc.086"/>" sort="exprDate"/>
				<style format="<fmt:message key="date.medium"/>" attribute="exprDate"/>
			</list>
		</exprDate>
		<exprMonth>
			<hidden component="HiddenFieldEditor" parent="null"/>
			<read component="ComboViewer">
				<header label="<fmt:message key="doc.019"/>"/>
				<style options="<kfmt:message key="work.005"/>"/>
			</read>
			<write component="WorkExpireDateEditor" name="exprMonth,exprDate">
				<header label="<fmt:message key="doc.019"/>"/>
				<style options="<kfmt:message key="work.005"/>"/>
			</write>
		</exprMonth>
		<tags>
			<write component="TagFieldEditor">
				<header label="<fmt:message key="doc.146"/>" <%=isRequiredTag()%>/>
				<style autoComplete="true" <%=isRequiredTag()%>/>
			</write>
			<search>
				<header label="<fmt:message key="doc.146"/>" search="tag"/>
			</search>
		</tags>
		<content>
			<write component="WebEditor" hidden="content" id="content">
				<style height="350" name="content" editor="kcube" attachUrl="ImageAction.Download" sessionKey="${sessionKey}"/>
			</write>
			<read component="ContentViewer" id="content">
				<style layoutClass="bottomBorder">
					<tag component="TagViewer" reference="tags"/>
				</style>
			</read>
		</content>
		<attachments>
			<write component="DocFileViewer" id="attachments">
				<style  label="<kfmt:message key="work.title.name"/>"
						add="<kfmt:message key="work.atch.008"/>" 
						del="<kfmt:message key="work.atch.002"/>" 
						edit="<kfmt:message key="work.atch.009"/>" 
						hist="<kfmt:message key="work.atch.015"/>" 
						cancelChk="<kfmt:message key="work.atch.007"/>" 
						cancelChkErr="<kfmt:message key="work.atch.006"/>" 
						userId="<%=com.kcube.sys.usr.UserService.getUserId()%>" 
						notSupport="<%=getNotSupportedExt()%>"
						fileSize="<%=getFileSize(moduleParam)%>"
						totalSize="<%=getTotalSize(moduleParam)%>"
						writeMode ="true"/>
			</write>
			<read component="DocFileViewer" id="attachments">
				<style  label="<kfmt:message key="work.title.name"/>"
						inline="/jsl/inline/WorkAttachmentVersioning.DownloadByUser?id=@{id}"
						attach="/jsl/attach/WorkAttachmentVersioning.DownloadByUser?id=@{id}"
						add="<kfmt:message key="work.atch.008"/>" del="<kfmt:message key="work.atch.002"/>" 
						edit="<kfmt:message key="work.atch.009"/>" hist="<kfmt:message key="work.atch.015"/>" 
						cancelChk="<kfmt:message key="work.atch.007"/>" 
						cancelChkErr="<kfmt:message key="work.atch.006"/>" 
						userId="<%=com.kcube.sys.usr.UserService.getUserId()%>"
						notSupport="<%=getNotSupportedExt()%>"
						fileSize="<%=getFileSize(moduleParam)%>"
						totalSize="<%=getTotalSize(moduleParam)%>"/>
			</read>
		</attachments>
		<reportFiles>
			<write component="DocFileViewer" id="reportFiles">
				<style label="<kfmt:message key="work.title.reportFile"/>"
						template="true"
						add="<kfmt:message key="work.atch.008"/>" 
						del="<kfmt:message key="work.atch.002"/>" 
						edit="<kfmt:message key="work.atch.009"/>" 
						hist="<kfmt:message key="work.atch.015"/>" 
						cancelChk="<kfmt:message key="work.atch.007"/>" 
						cancelChkErr="<kfmt:message key="work.atch.006"/>" 
						userId="<%=com.kcube.sys.usr.UserService.getUserId()%>" 
						notSupport="<%=getNotSupportedExt()%>"
						fileSize="<%=getFileSize(moduleParam)%>"
						totalSize="<%=getTotalSize(moduleParam)%>"
						writeMode ="true"/>
			</write>
			<read component="DocFileViewer" id="reportFiles">
				<style  label="<kfmt:message key="work.title.reportFile"/>"
						davKey="WORKREPORT"
						template="true"
						inline="/jsl/inline/WorkReportFileVersioning.DownloadByUser?id=@{id}"
						attach="/jsl/attach/WorkReportFileVersioning.DownloadByUser?id=@{id}"
						add="<kfmt:message key="work.atch.008"/>" 
						del="<kfmt:message key="work.atch.002"/>"
						edit="<kfmt:message key="work.atch.009"/>" 
						hist="<kfmt:message key="work.atch.015"/>"
						cancelChk="<kfmt:message key="work.atch.007"/>" 
						cancelChkErr="<kfmt:message key="work.atch.006"/>" 
						userId="<%=com.kcube.sys.usr.UserService.getUserId()%>" 
						notSupport="<%=getNotSupportedExt()%>"
						fileSize="<%=getFileSize(moduleParam)%>"
						totalSize="<%=getTotalSize(moduleParam)%>"/>
			</read>
			<admin component="DocFileViewer" id="reportFiles">
			     <style label="<kfmt:message key="work.title.reportFile"/>"
			            template="true"
			            inline="/jsl/inline/WorkReportFileVersioning.DownloadByAdmin?id=@{id}"
			            attach="/jsl/attach/WorkReportFileVersioning.DownloadByAdmin?id=@{id}"
			            add="<kfmt:message key="work.atch.008"/>" 
			            del="<kfmt:message key="work.atch.002"/>"
			            edit="<kfmt:message key="work.atch.009"/>" 
			            hist="<kfmt:message key="work.atch.015"/>"
			            cancelChk="<kfmt:message key="work.atch.007"/>" 
			            cancelChkErr="<kfmt:message key="work.atch.006"/>" 
			            userId="<%=com.kcube.sys.usr.UserService.getUserId()%>" 
			            notSupport="<%=getNotSupportedExt()%>"
			            fileSize="<%=getFileSize(moduleParam)%>"
			            totalSize="<%=getTotalSize(moduleParam)%>"/>
			</admin>
		</reportFiles>
		<chatFiles>
			<read component="FileViewer" id="chatFiles">
				<header label="<fmt:message key="doc.017"/>"/>
				<style inline="/jsl/inline/WorkChatItemUser.DownloadByUser?id=@{id}"
						attach="/jsl/attach/WorkChatItemUser.DownloadByUser?id=@{id}"
						optional="true"/>
			</read>
		</chatFiles>
		<editorFiles>
			<hidden id="editorFiles" name="editorFiles" component="HiddenFieldEditor" parent="null"/>
		</editorFiles>
		<rgstDate>
			<list component="DateColumn" width="100px">
				<header label="<fmt:message key="doc.022"/>" sort="rgstDate"/>
				<style format="<fmt:message key="date.medium"/>" attribute="rgstDate"/>
			</list>
		</rgstDate>
		<lastUpdt>
			<list component="DateColumn" width="100px">
				<header label="<fmt:message key="doc.088"/>" sort="lastUpdt"/>
				<style format="<fmt:message key="date.medium"/>" attribute="lastUpdt"/>
			</list>
		</lastUpdt>
		<reqShareDate>
			<list component="DateColumn" width="100px">
				<header label="<kfmt:message key="work.052"/>" sort="reqDate"/>
				<style format="<fmt:message key="date.medium"/>" attribute="reqDate"/>
			</list>
		</reqShareDate>
		<isHelper>
			<list component="ComboColumn" width="60px">
				<header label="<kfmt:message key="work.011"/>"/>
				<style attribute="isHelper" options="<kfmt:message key="work.012"/>"/>
			</list>
		</isHelper>
		<isTransfer>
			<list component="ComboColumn" width="120px">
				<header label="<kfmt:message key="work.133"/>" sort="isTransfer"/>
				<style attribute="isTransfer" options="<kfmt:message key="work.134"/>"/>
			</list>
		</isTransfer>
		<workReferences>
			<write component="WorkReferenceComponent" id="workReferences" name="workReferences">
				<style label="<kfmt:message key="work.rfrn.title"/>" 
				write="true"
				read="false" 
				notSupport="<%=getNotSupportedExt()%>"
				fileSize="<%=getFileSize(moduleParam)%>"
				totalSize="<%=getTotalSize(moduleParam)%>" />
			</write>
			<read component="WorkReferenceComponent"  id="workReferences" name="workReferences">
				<style label="<kfmt:message key="work.rfrn.title"/>" 
				read="true" write="false"
				inline="/jsl/inline/WorkReferenceAction.DownloadByUser?id=@{id}"
				attach="/jsl/attach/WorkReferenceAction.DownloadByUser?id=@{id}"
				notSupport="<%=getNotSupportedExt()%>"
				fileSize="<%=getFileSize(moduleParam)%>"
				totalSize="<%=getTotalSize(moduleParam)%>" />
			</read>
			<admin component="WorkReferenceComponent"  id="workReferences" name="workReferences">
			    <style label="<kfmt:message key="work.rfrn.title"/>" 
			    read="true" write="false"
			    inline="/jsl/inline/WorkReferenceAction.DownloadByAdmin?id=@{id}"
			    attach="/jsl/attach/WorkReferenceAction.DownloadByAdmin?id=@{id}"
			    notSupport="<%=getNotSupportedExt()%>"
			    fileSize="<%=getFileSize(moduleParam)%>"
			    totalSize="<%=getTotalSize(moduleParam)%>" />
			</admin>
		</workReferences>
		<opinions>
			<write component="OpnWriter" id="opnWriter">
				<style itemId="id" reloadUrl="/work/usr.read.jsp" actionUrl="/jsl/WorkOpinion.AddOpinion.json"/>
			</write>
			<read component="OpnViewer"  observe="folder,opnWriter">
				<style userId="<%=com.kcube.sys.usr.UserService.getUserId()%>"
		        isCenter="<%=com.kcube.sys.usr.UserPermission.isAdmin()%>"
		        reloadUrl="/work/usr.read.jsp" actionUrl="/jsl/WorkOpinion.DeleteOpinion.jsl"
		        actionAddUrl="/jsl/WorkOpinion.AddOpinion.json"
		        opnView="WorkOpinion.ViewOpinion" opnDelete="WorkOpinion.DeleteOpinion" opnUpdate="WorkOpinion.UpdateOpinion">
		        <%if(isSympathy(moduleParam)){%>
					<sympathy component="SympathyViewer" reference="sympathies">
						<style userId="<%=com.kcube.sys.usr.UserService.getUserId()%>"
						options="<%=getSympathyIcons(moduleParam)%>"	
						actionAddUrl="/jsl/WorkUser.SympathyByUser.json" 
						actionDelUrl="/jsl/WorkUser.DelSympathyByUser.json"
						actionViewUrl="/jsl/WorkUser.ViewSympathyByUser.json" />
					</sympathy>
				<%}%>
				</style>
			</read>
		</opinions>
		<status>
			<list component="StatusBoxColumn" width="70px">
				<header label="" sort=""/>
				<style attribute="status" 
					options="<kfmt:message key="work.options.statusText"/>" styleOptions="<kfmt:message key="work.options.statusText.style"/>"/>
			</list>
		</status>
		<reqShareStatus>
			<list component="StatusBoxColumn" width="72px">
				<header label="" sort="status"/>
				<style name="status" attribute="status" options="<kfmt:message key="work.053"/>" styleOptions="<kfmt:message key="work.050"/>"/>
			</list>
		</reqShareStatus>
		<fvrt>
			<list component="FavoriteColumn" width="28px">
				<header type="favorite"/>
				<style action="doFvrt" fvrtIdName="docGid" fvrtScdIdName="id"/>
			</list>
		</fvrt>
		<itemsVisible>
			<hidden id="itemsVisible" name="itemsVisible" component="HiddenFieldEditor" parent="null"/>
		</itemsVisible>
		<recentDate>
			<list component="DateColumn" width="100px">
				<header label="<kfmt:message key="work.089"/>" sort="recentDate"/>
				<style format="<fmt:message key="date.medium"/>" attribute="recentDate"/>
			</list>
		</recentDate>
		<totalFileSize>
			<list component="FileSizeColumn" width="100px">
				<header label="<kfmt:message key="work.atch.019"/>" sort="totalFileSize"/>
				<style attribute="totalFileSize" />
			</list>
		</totalFileSize>
		<quota>
			<read component="TextViewer" id="quota">
				<header label="<kfmt:message key="work.chat.003"/>"/>
			</read>
		</quota>
		<fileSize>
			<read component="TextViewer" id="fileSize">
				<header label="<kfmt:message key="work.chat.004"/>"/>
			</read>
		</fileSize>
		<foldingBar>
			<detailInfo component="FoldingBar" foldable="true" closeMsg="<fmt:message key="doc.171"/>" openMsg="<fmt:message key="doc.172"/>" id="detailInfo">
				<style type="hidden"/>
			</detailInfo>
			<writeDetail component="FoldingBar" foldable="true" foldClass="boldAnchor" closeMsg="<fmt:message key="doc.173"/>" openMsg="<fmt:message key="doc.174"/>" id="writeDetail">
				<style color="white"/>
			</writeDetail>
			<hidden component="FoldingBar" foldable="true" id="detailInfoHidden">
				<style type="hidden"/>
			</hidden>
		</foldingBar>
		<rgstDate>
			<list component="DateColumn" width="100px">
				<header label="<fmt:message key="doc.022"/>" sort="rgstDate"/>
				<style format="<fmt:message key="date.medium"/>" attribute="rgstDate"/>
			</list>
		</rgstDate>
		<reqDate>
			<list component="DateColumn" width="100px">
				<header label="<kfmt:message key="work.105"/>" sort="reqDate"/>
				<style format="<fmt:message key="date.medium"/>" attribute="reqDate"/>
			</list>
		</reqDate>
		<cmpltDate>
			<list component="DateColumn" width="100px">
				<header label="<kfmt:message key="work.106"/>" sort="cmpltDate"/>
				<style format="<fmt:message key="date.medium"/>" attribute="cmpltDate"/>
			</list>
		</cmpltDate>
		<apprReviewSetting>
			<write id="apprReviewSetting" component="WorkProcessEditor" width="100px">
				<style empsUserCount="-1" empUserCount="1" autoComplete="true" empsPopupComponent="EmpsFieldEditor" empPopupComponent="EmpFieldEditor">
					<autoCompleteStyle searchUrl="/jsl/EmployeeSelector.SelectUserByNameQuery.json" isId="true"/>
				</style>
			</write>
			<read id="apprReviewSetting" component="ProcessStatusViewer" observe="WorkStatus">
				<style listUrl = '/jsl/WorkProcessAction.ProcessHistory.json?itemId=@{id}'/>
			</read>
			<hidden component="HiddenFieldsEditor" id="apprReviewSetting" parent="null"/>
		</apprReviewSetting>
		<processStatus>
			<list component="ProcessStatusBoxColumn" width="70px">
				<header label="" sort=""/>
				<style attribute="status" options="<kfmt:message key="work.options.processStatusText"/>" styleOptions="<kfmt:message key="work.options.processStatusText.style"/>"/>
			</list>
		</processStatus>
		<ganttChart>
			<read component="GanttChartViewer" id="chart"  name="chart">
				<style depth="pos" action="/jsl/WorkPlanUser.ListByUser.json?workId=@{id}"/>
			</read>
		</ganttChart>
	</properties>
	<validators>
		<title validator="required"
				message="[<fmt:message key="doc.001"/>] <fmt:message key="doc.016"/>"/>
		<actor validator="required"
				message="[<kfmt:message key="work.002"/>] <fmt:message key="doc.016"/>"/>
		<charge ref="charge"
				message="[<kfmt:message key="work.003"/>] <fmt:message key="doc.016"/>"/>
		<% if(useCtgr(moduleParam)) { %>
		<folder validator="required"
				message="[<kfmt:message key="work.006"/>] <fmt:message key="doc.016"/>"/>
		<%} %>				
	</validators>
</registry>
<%@ include file="/jspf/tail.xml.jsp" %>