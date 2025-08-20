<%@ include file="/jspf/head.xml.jsp" %>
<%@ include file="/ekp/mbs/config.jsp" %>
<registry>
	<properties>
		<id>
			<list component="<%= getIdComponent(moduleParam) %>" width="70px">
				<header label="<fmt:message key="doc.006"/>" sort="id" search="id"/>
				<style attribute="id"/>
			</list>
			<search>
				<header label="<fmt:message key="doc.098"/>" search="id"/>
			</search>
			<hidden component="HiddenFieldEditor" id="id" parent="null"/>
		</id>
		<title>
			<read component="MainTitleViewer" id="readTitle" name="title" viewer="true">
				<style detailId="detailInfo">
					<title component="TitleViewer" reference="title">
						<style className="mainTitle_title"/>
					</title>
					<bottom>
						<author component="EmpHtmlViewer" reference="author" styleType="authorInfo">
							<style useMainTitle="true" vrtl="<fmt:message key="sys.vrtl.userId"/>"/>
						</author>
						<sep1/>
						<rgstDate component="DateViewer" reference="rgstDate" styleType="dateInfo">
							<style format="<fmt:message key="date.longTohour"/>"/>
						</rgstDate>
						<sep2/>
						<readCnt component="ReadCountViewer" reference="id,readCnt" styleType="viewInfo">
							<style isReadCntPopup="<%= isReadCntPopup(moduleParam) %>" className="mainTitle_readCnt" count="MbItemViewer.ListCount" detail="MbItemViewer.DetailListCount" />
						</readCnt>
					</bottom>
				</style>
			</read>
			<write component="TextFieldEditor" focus="true" name="title" id="title">
				<header label="<fmt:message key="doc.001"/>" required="true"/>
				<style maxLength="85"/>
			</write>
			<list component="TitleColumn">
				<header label="<fmt:message key="doc.001"/>" sort="title" search="title"/>
				<style href="usr.read.jsp?id=@{id}" attribute="title" position="pos" rplyIcon="true" newIcon="rgstDate"  
				isSympathy="<%=isSympathy(moduleParam)%>" opnCnt="vrtlOpnCnt" opnView="MbItemOpinion.ViewOpinion"/>
			</list>
			<ownList component="TitleColumn">
				<header label="<fmt:message key="doc.001"/>" sort="title" search="title"/>
				<style href="own.read.jsp?id=@{id}" attribute="title" newIcon="rgstDate"  opnCnt="vrtlOpnCnt" opnView="MbItemOpinion.ViewOpinion"/>
			</ownList>
			<admList component="TitleColumn">
				<header label="<fmt:message key="doc.001"/>" sort="title" search="title"/>
				<style href="javascript:popup('@{id}');" attribute="title" position="pos" newIcon="rgstDate"/>
			</admList>
			<difflist component="TitleColumn">
				<header label="<fmt:message key="doc.001"/>"/>
				<style href="usr.diff.read.jsp?id=@{id}" attribute="title"/>
			</difflist>
			<popupRead component="MainTitleViewer" id="readTitle" name="title">
				<style>
					<title component="TitleViewer" reference="title">
						<style className="mainTitle_title"/>
					</title>					
					<bottom>
						<author component="EmpHtmlViewer" reference="author" styleType="authorInfo">
							<style useMainTitle="true" vrtl="<fmt:message key="sys.vrtl.userId"/>"/>
						</author>
						<sep1/>
						<rgstDate component="DateViewer" reference="rgstDate" styleType="dateInfo">
							<style format="<fmt:message key="date.longTohour"/>"/>
						</rgstDate>
						<sep2/>
						<readCnt component="ReadCountViewer" reference="id,readCnt" styleType="viewInfo">
							<style isReadCntPopup="<%= isReadCntPopup(moduleParam) %>" className="mainTitle_readCnt" count="MbItemViewer.ListCount" detail="MbItemViewer.DetailListCount" />
						</readCnt>
					</bottom>
				</style>
			</popupRead>
		</title>
		<name>
			<ownList component="TextColumn" width="200px">
				<header label="<fmt:message key="doc.210"/>"/>
				<style attribute="name"/>
			</ownList>
		</name>
		<delBtn>
			<list component="ButtonColumn" width="70px">
				<header label="<fmt:message key="doc.031"/>"/>
				<style attribute="status">
					<options class="Array">
						<option value="0" img="<fmt:message key="btn.pub.delete_small"/>" execute="doDelete('/jsl/MbItemOwner.DoRemoveByOwner.jsl?id=@{id}', element)"/>
					</options>
				</style>
			</list>
		</delBtn>
		<thumbnail>
			<list component="ImageThumbColumn" width="130px">
				<header label="<kfmt:message key="mbs.002"/>"/>
				<style inline="/jsl/inline/ImageAction.Download/?type=@{thumbCode}&amp;path=@{thumbPath}&amp;size=L&amp;square=true"
						href="usr.read.jsp?id=@{id}"
						type="thumbCode" path="thumbPath" thumbPreview="true"/>
			</list>
			<listWebzine component="ImageThumbColumn" width="130px">
				<header label="<kfmt:message key="mbs.002"/>"/>
				<style align="center" inline="/jsl/inline/ImageAction.Download/?type=@{thumbCode}&amp;path=@{thumbPath}&amp;size=L&amp;square=true"
						href="usr.read.jsp?id=@{id}"
						type="thumbCode" path="thumbPath"/>
			</listWebzine>
			<listOwnWebzine component="ImageThumbColumn" width="130px">
				<header label="<kfmt:message key="mbs.002"/>"/>
				<style align="center" inline="/jsl/inline/ImageAction.Download/?type=@{thumbCode}&amp;path=@{thumbPath}&amp;size=L&amp;square=true"
						href="own.read.jsp?id=@{id}"
						type="thumbCode" path="thumbPath"/>
			</listOwnWebzine>
			<etc component="ImageThumbInfoColumn">
				<style noReadCnt="true" userId="userId" userName="userName" userDisp="userName" itemid="id" />
			</etc>
		</thumbnail>
		<readCnt>
			<read component="TextViewer">
				<header label="<fmt:message key="doc.068"/>"/>
			</read>
			<list component="<%= getReadCntComponent(moduleParam) %>" width="45px">
				<header label="<fmt:message key="doc.068"/>" sort="readCnt"/>
				<style attribute="readCnt" count="MbItemViewer.ListCount" detail="MbItemViewer.DetailListCount"  itemid="id"/>
			</list>
		</readCnt>
		<author>
			<read component="EmpHtmlViewer">
				<header label="<fmt:message key="doc.003"/>"/>
				<style vrtl="<fmt:message key="sys.vrtl.userId"/>"/>
			</read>
			<write component="EmpTextFieldEditor" id="auth" observe="folder">
				<header label="<fmt:message key="doc.003"/>" required="true"/>
				<style vrtl="<fmt:message key="sys.vrtl.userId"/>"/>
			</write>
			<hidden name="author.id" component="HiddenFieldEditor" parent="null"/>
			<list component="EmpColumn" sort="userName" width="127px">
				<header label="<fmt:message key="doc.003"/>" sort="userName" search="userName"/>
				<style id="userId" name="userName" vrtl="<fmt:message key="sys.vrtl.userId"/>"/>
			</list>
			<difflist component="EmpColumn" sort="userName" width="127px">
				<header label="<fmt:message key="doc.003"/>"/>
				<style id="userId" name="userName"/>
			</difflist>
			<hidden component="HiddenFieldEditor" id="author" parent="null"/>
		</author>
		<rgstDate>
			<read component="DateViewer">
				<header label="<fmt:message key="doc.005"/>"/>
				<style format="<fmt:message key="date.long"/>"/>
			</read>
			<list component="DateColumn" width="70px">
				<header label="<fmt:message key="doc.022"/>" sort="rgstDate_lastUpdt_rsrvDate"/>
				<style format="<fmt:message key="date.medium"/>" attribute="rgstDate"/>
			</list>
			<difflist component="DateColumn" width="105px">
				<header label="<fmt:message key="doc.022"/>"/>
				<style format="<fmt:message key="date.long"/>" attribute="rgstDate"/>
			</difflist>
		</rgstDate>
		<diff>
			<list component="RadioDiffColumn" width="60px">
				<header label="<fmt:message key="diff.001"/>"/>
				<style oldName="oldrds" newName="newrds"/>
			</list>
		</diff>
		<vrsn>
			<list component="DescendingOrderColumn" width="70px">
				<header label="<fmt:message key="diff.004"/>"/>
			</list>
		</vrsn>
		<delbtn>
			<list component="ButtonColumn" width="80px">
				<header label="<fmt:message key="pub.015"/>"/>
				<style attribute="status">
					<options class="Array">
						<option value="show" img="<fmt:message key="btn.pub.delete_small"/>" execute="doDelete('/jsl/MbItemAdmin.DeleteByAdmin.jsl?id=@{id}',element)"/>
					</options>
				</style>
			</list>
		</delbtn>
		<attachments>
			<list component="FileColumn" width="50px" id="attachments">
				<header label="<fmt:message key="doc.038"/>"/>
				<style select="/jsl/MbItemUser.AttachmentList.json?id=@{id}"
						inline="/jsl/inline/MbItemUser.DownloadByUser?id=@{id}"
						attach="/jsl/attach/MbItemUser.DownloadByUser?id=@{id}"/>
			</list>
			<write component="FileFieldEditor" id="attachments">
				<style fileSize="<%=getFileSize(moduleParam)%>" totalSize="<%=getTotalSize(moduleParam)%>" notSupport="<%=getNotSupportedExt()%>" sessionKey="${sessionKey}"/>
			</write>
			<imageType component="FileFieldEditor" id="attachments">
				<style sessionKey="${sessionKey}"/>
			</imageType>
			<read component="FileViewer" observe="scrtRead">
				<header label="<fmt:message key="doc.017"/>"/>
				<style inline="/jsl/inline/MbItemUser.DownloadByUser?id=@{id}"
						attach="/jsl/attach/MbItemUser.DownloadByUser?id=@{id}"
						attachZip="/jsl/attachZip/MbItemUser.DownloadZipByUser?id="
						preview="/ext/docviewer/preview.index.jsp?module=MBS&amp;id=@{id}"
						optional="true"/>
			</read>
			<owner component="FileViewer">
				<header label="<fmt:message key="doc.017"/>"/>
				<style inline="/jsl/inline/MbItemOwner.DownloadByOwner?id=@{id}"
						attach="/jsl/attach/MbItemOwner.DownloadByOwner?id=@{id}"
						attachZip="/jsl/attachZip/MbItemOwner.DownloadZipByOwner?id="
						preview="/ext/docviewer/preview.index.jsp?module=MBS&amp;id=@{id}"
						optional="true"/>
			</owner>
			<master component="FileViewer">
				<header label="<fmt:message key="doc.017"/>"/>
				<style inline="/jsl/inline/MbItemMaster.DownloadByMaster?id=@{id}"
						attach="/jsl/attach/MbItemMaster.DownloadByMaster?id=@{id}"
						preview="/ext/docviewer/preview.index.jsp?module=MBS&amp;id=@{id}"
						optional="true"/>
			</master>
			<admin component="FileViewer">
				<header label="<fmt:message key="doc.017"/>"/>
				<style inline="/jsl/inline/MbItemAdmin.DownloadByAdmin?id=@{id}"
						attach="/jsl/attach/MbItemAdmin.DownloadByAdmin?id=@{id}"
						preview="/ext/docviewer/preview.index.jsp?module=MBS&amp;id=@{id}"
						optional="true"/>
			</admin>
			<hidden component="HiddenFieldEditor" id="attachments" parent="null"/>
		</attachments>
		<images>
			<write component="FileFieldEditor" id="images">
				<header label="<kfmt:message key="mbs.002"/>" required="true"/>
				<style onlyImage="true" fileSize="<%=getFileSize(moduleParam)%>" totalSize="<%=getTotalSize(moduleParam)%>" sessionKey="${sessionKey}"/>
			</write>
			<noHeaderWrite component="FileFieldEditor" id="images">
				<style onlyImage="true" fileSize="<%=getFileSize(moduleParam)%>" totalSize="<%=getTotalSize(moduleParam)%>" sessionKey="${sessionKey}"/>
			</noHeaderWrite>
			<read component="FileViewer" observe="scrtRead">
				<header label="<fmt:message key="doc.017"/>"/>
				<style inline="/jsl/inline/MbItemUser.DownloadByUser?id=@{id}"
						attach="/jsl/attach/MbItemUser.DownloadByUser?id=@{id}"
						attachZip="/jsl/attachZip/MbItemUser.DownloadZipByUser?id="
						preview="/ext/docviewer/preview.index.jsp?module=MBS&amp;id=@{id}"
						optional="true"/>
			</read>
			<hidden component="HiddenFieldEditor" id="attachments" parent="null"/>
		</images>
		<rcmdCnt>
			<read component="TextViewer">
				<header label="<fmt:message key="doc.034"/>"/>
			</read>
			<list component="TextColumn" width="70px">
				<header label="<fmt:message key="doc.034"/>" sort="rcmdCnt"/>
				<style attribute="rcmdCnt"/>
			</list>
		</rcmdCnt>
		<tags>
			<write component="TagFieldEditor">
				<style autoComplete="true" <%=isRequiredTag()%>/>
			</write>
			<read component="TagViewer" id="tags">
				<header label="<fmt:message key="doc.146"/>"/>
				<style optional="true"/>
			</read>
			<search>
				<header label="<fmt:message key="doc.146"/>" search="tag"/>
			</search>
		</tags>
		<content>
			<write component="WebEditor" hidden="content" id="content">
				<style height="350" name="content" editor="kcube" attachUrl="ImageAction.Download" sessionKey="${sessionKey}"/>
			</write>
			<wikiWrite component="WebEditor" hidden="content" id="content">
				<style isNotReplace="true" height="350" name="content" editor="kcube" attachUrl="ImageAction.Download"
					itemId="id" isWiki="true"
					docPopupUrl="/ekp/mbs/usr.popup.read.jsp?csId=@{csId}%26mdId=@{mdId}%26appId=@{appId}%26id="
					docSelectUrl="/ekp/mbs/usr.select.list.jsp"
					attachSelect="/jsl/MbItemUser.AttachmentList.json?id="
					attachInline="/jsl/inline/MbItemUser.DownloadByUser?id="
					sessionKey="${sessionKey}"/>
			</wikiWrite>
			<wikiEdit component="WebEditor" hidden="content" id="content">
				<style height="350" name="content" editor="kcube" attachUrl="ImageAction.Download"
					itemId="id" isWiki="true"
					docPopupUrl="/ekp/mbs/usr.popup.read.jsp?csId=@{csId}%26mdId=@{mdId}%26appId=@{appId}%26id="
					docSelectUrl="/ekp/mbs/usr.select.list.jsp"
					attachSelect="/jsl/MbItemUser.AttachmentList.json?id="
					attachInline="/jsl/inline/MbItemUser.DownloadByUser?id="
					sessionKey="${sessionKey}"/>
			</wikiEdit>
			<noImageWrite component="WebEditor" hidden="content" id="content">
				<style isImage="false" height="350" name="content" editor="kcube" attachUrl="ImageAction.Download" sessionKey="${sessionKey}"/>
			</noImageWrite>
			<reply component="WebEditor" hidden="content" id="content">
				<style height="350" name="content" editor="kcube" isReadOnly="true" attachUrl="ImageAction.Download" prefix="<fmt:message key="doc.reply.content.prefix"/>" sessionKey="${sessionKey}"/>
			</reply>
			<read component="ContentViewer">
				<style layoutClass="bottomBorder">
					<tag component="TagViewer" reference="tags"/>
				</style>
			</read>
			<readOwner component="ContentViewer">
				<style layoutClass="bottomBorder">
					<tag component="TagViewer" reference="tags"/>
				</style>
			</readOwner>
			<readPopup component="ContentViewer">
				<style layoutClass="bottomBorder">
					<tag component="TagViewer" reference="tags"/>
				</style>
			</readPopup>
			<admin component="ContentViewer">
				<style layoutClass="bottomBorder">
					<tag component="TagViewer" reference="tags"/>
				</style>
			</admin>
			<list>
				<header label="<fmt:message key="cst.doc.002"/>" search="content"/>
			</list>
		</content>
		<foldingBar>
			<detailInfo component="FoldingBar" foldable="true" closeMsg="<fmt:message key="doc.171"/>" openMsg="<fmt:message key="doc.172"/>" id="detailInfo">
				<style type="hidden"/>
			</detailInfo>
			<writeDetail component="FoldingBar" foldable="true" foldClass="boldAnchor" closeMsg="<fmt:message key="doc.244"/>" openMsg="<fmt:message key="doc.244"/>" id="writeDetail">
				<style color="white"/>
			</writeDetail>
			<hidden component="FoldingBar" foldable="true" id="detailInfoHidden">
				<style type="hidden"/>
			</hidden>
		</foldingBar>
		<exprMonth>
			<hidden component="HiddenFieldEditor" id="exprMonth" parent="null"/>
			<read component="ComboViewer" id="exprMonth">
				<header label="<fmt:message key="doc.019"/>"/>
				<style options="<kfmt:message key="mbs.007"/>"/>
			</read>
			<write component="ComboFieldEditor" id="exprMonth">
				<header label="<fmt:message key="doc.019"/>" desc="<kfmt:message key="mbs.023"/>" descWidth="360px"/>
				<style options="<kfmt:message key="mbs.007"/>" isStuff="true"/>
			</write>
		</exprMonth>
		<references>
			<hidden component="HiddenFieldEditor" parent="null"/>
			<write component="RfrnFieldEditor">
				<header label="<fmt:message key="doc.062"/>"/>
				<style viewer="ListViewer" isScroll="false" isDel="true" delBtnToolTip="<fmt:message key="pub.015"/>" extendClass="rfrnList" itemId="id" appId="appId"/>
			</write>
			<read component="RfrnHtmlViewer" id="references">
				<header label="<fmt:message key="doc.062"/>"/>
				<style itemId="id" userId="<%=com.kcube.sys.usr.UserService.getUserId()%>"
						rfrnHeader="<fmt:message key="RelViewer.Header"/>"
						reloadUrl="/ekp/mbs/usr.read.jsp" actionUrl="/jsl/MbItemReference.DeleteReference.jsl"
						optional="true"/>
			</read>
		</references>
		<securities>
			<read component="SecurityGradeViewer" name="id,scrtLevel" id="scrtRead">
				<header label="<fmt:message key="doc.075"/>"/>
				<style options="<fmt:message key="doc.064"/>" actionUrl="MbItemUser.ViewSecurities" permUrl="MbItemUser.AttachmentPermission"/>
			</read>
			<write component="SecurityRadioFieldEditor" name="scrtLevel,securities" id="scrt">
				<header label="<fmt:message key="doc.021"/>"/>
				<style options="<fmt:message key="doc.064"/>" dialogOption="opnerComponent=SecurityRadioFieldEditor" />
			</write>
			<hidden component="HiddenFieldsEditor"><style name="security"/></hidden>
		</securities>
		<gid>
			<hidden component="HiddenFieldEditor" parent="null"/>
		</gid>
		<pid>
			<hidden component="HiddenFieldEditor" parent="null"/>
		</pid>
		<rplyCnt>
			<read component="TextViewer">
				<header label="<fmt:message key="doc.033"/>"/>
			</read>
			<list component="TextColumn" width="50px">
				<header label="<fmt:message key="doc.033"/>"/>
				<style attribute="rplyCnt"/>
			</list>
			<hidden component="HiddenFieldEditor" parent="null"/>
		</rplyCnt>
		<status>
			<list component="ComboColumn" width="50px">
				<header label="<fmt:message key="doc.027"/>"/>
				<style attribute="status" options="<kfmt:message key="mbs.024"/>"/>
			</list>
			<hidden component="HiddenFieldEditor" parent="null"/>
		</status>
		<currentOwner>
			<hidden component="HiddenFieldEditor" parent="null"/>
		</currentOwner>
		<folder>
			<hidden component="HiddenFieldEditor" parent="null"/>
			<write component="FolderCompositeEditor" required="true" id="folder" name="folder">
				<header label="<fmt:message key="doc.210"/>" required="true"/>
				<style viewer="FolderTextViewer" editor="MultiBoardFieldEditor"/>
			</write>
			<masterWrite component="FolderCompositeEditor" required="true" id="folder" name="folder">
				<header label="<fmt:message key="doc.210"/>" required="true"/>
				<style viewer="FolderTextViewer" editor="MultiBoardFieldEditor" isMaster="true"/>
			</masterWrite>
			<noWiki component="FolderCompositeEditor" required="true" id="folder" name="folder">
				<header label="<fmt:message key="doc.210"/>" required="true"/>
				<style appTypes="0,1000,3000" viewer="FolderTextViewer" editor="MultiBoardFieldEditor"/>
			</noWiki>
			<read component="FolderViewer" id="folder">
				<header label="<fmt:message key="doc.210"/>"/>
				<style code="1000"/>
			</read>
			<reply component="FolderViewer" id="folder">
				<header label="<fmt:message key="doc.210"/>"/>
				<style/>
			</reply>
		</folder>
		<duplex>
			<read component="DuplexViewer" id="duplex">
				<style actionUrl="/jsl/MbItemUser.DuplexByUser.json" title="title" param="id=id,ts=com.kcube.doc.list,inAppId=inAppId,type=type,mdId=mdId,appId=appId"/>
			</read>
		</duplex>
		<opinions>
			<write component="OpnWriter" id="opnWriter">
				<style itemId="id" reloadUrl="/ekp/mbs/usr.read.jsp"  actionUrl="/jsl/MbItemOpinion.AddOpinion.json" vrtl="<fmt:message key="sys.vrtl.userId"/>"/>
			</write>
			<read component="OpnViewer" id="opnViewer" observe="opnWriter">
				<style userId="<%=com.kcube.sys.usr.UserService.getUserId()%>"
				isCenter="<%=isAdmin(moduleParam)%>"
				reloadUrl="/ekp/mbs/usr.read.jsp?id=@{id}" actionUrl="/jsl/MbItemOpinion.DeleteOpinion.jsl" vrtl="<fmt:message key="sys.vrtl.userId"/>"
				actionAddUrl="/jsl/MbItemOpinion.AddOpinion.json"
				opnView="MbItemOpinion.ViewOpinion" opnDelete="MbItemOpinion.DeleteOpinion" opnUpdate="MbItemOpinion.UpdateOpinion">
					<%if (isSympathy(moduleParam)) {%>
					<sympathy component="SympathyViewer" reference="sympathies">
						<style userId="<%=com.kcube.sys.usr.UserService.getUserId()%>"
						options="<%=getSympathyIcons(moduleParam)%>"
						actionAddUrl="/jsl/MbItemUser.SympathyByUser.json"
						actionDelUrl="/jsl/MbItemUser.DelSympathyByUser.jsl"
						actionViewUrl="/jsl/MbItemUser.ViewSympathyByUser.json"/>
					</sympathy>
					<%}%>
				</style>
			</read>
			<noSympathyRead component="OpnViewer" id="opnViewer" observe="folder,opnWriter">
				<style userId="<%=com.kcube.sys.usr.UserService.getUserId()%>"
				isCenter="<%=isAdmin(moduleParam)%>"
				reloadUrl="/ekp/mbs/usr.read.jsp" actionUrl="/jsl/MbItemOpinion.DeleteOpinion.jsl" vrtl="<fmt:message key="sys.vrtl.userId"/>"
				actionAddUrl="/jsl/MbItemOpinion.AddOpinion.json"
				opnView="MbItemOpinion.ViewOpinion" opnDelete="MbItemOpinion.DeleteOpinion" opnUpdate="MbItemOpinion.UpdateOpinion">
				</style>
			</noSympathyRead>
			<write_pop component="OpnWriter" id="opnWriter" observe="folder">
				<style itemId="id" reloadUrl="/ekp/mbs/usr.popup.read.jsp"  actionUrl="/jsl/MbItemOpinion.AddOpinion.json" vrtl="<fmt:message key="sys.vrtl.userId"/>"/>
			</write_pop>
			<read_pop component="OpnViewer" id="opnViewer" observe="folder,opnWriter">
				<style userId="<%=com.kcube.sys.usr.UserService.getUserId()%>"
				isCenter="<%=isAdmin(moduleParam)%>"
				reloadUrl="/ekp/mbs/usr.popup.read.jsp" actionUrl="/jsl/MbItemOpinion.DeleteOpinion.jsl"
				actionAddUrl="/jsl/MbItemOpinion.AddOpinion.json"
				vrtl="<fmt:message key="sys.vrtl.userId"/>"
				opnView="MbItemOpinion.ViewOpinion" opnDelete="MbItemOpinion.DeleteOpinion" opnUpdate="MbItemOpinion.UpdateOpinion">
					<%if (isSympathy(moduleParam)) {%>
					<sympathy component="SympathyViewer" reference="sympathies">
						<style userId="<%=com.kcube.sys.usr.UserService.getUserId()%>"
						options="<%=getSympathyIcons(moduleParam)%>"	
						actionAddUrl="/jsl/MbItemUser.SympathyByUser.json" 
						actionDelUrl="/jsl/MbItemUser.DelSympathyByUser.jsl"
						actionViewUrl="/jsl/MbItemUser.ViewSympathyByUser.json"/>
					</sympathy>
					<%}%>
				</style>
			</read_pop>
			<noSympathyReadPop component="OpnViewer" id="opnViewer" observe="folder,opnWriter">
				<style userId="<%=com.kcube.sys.usr.UserService.getUserId()%>"
				isCenter="<%=isAdmin(moduleParam)%>"
				reloadUrl="/ekp/mbs/usr.popup.read.jsp" actionUrl="/jsl/MbItemOpinion.DeleteOpinion.jsl"
				actionAddUrl="/jsl/MbItemOpinion.AddOpinion.json"
				vrtl="<fmt:message key="sys.vrtl.userId"/>"
				opnView="MbItemOpinion.ViewOpinion" opnDelete="MbItemOpinion.DeleteOpinion" opnUpdate="MbItemOpinion.UpdateOpinion">
				</style>
			</noSympathyReadPop>
		</opinions>
		<exprDate>
			<list component="DateColumn" width="70px">
				<header label="<fmt:message key="doc.019"/>"/>
				<style format="<fmt:message key="date.medium"/>" attribute="exprDate"/>
			</list>
			<hidden component="HiddenFieldEditor" parent="null"/>
		</exprDate>
		<rsrvDate>
			<write component="ReserveDateEditor" width="70px" id="rsrvDate">
				<header label="<kfmt:message key="mbs.inapp.023"/>"/>
				<style isPop="false" minDate="0" format="yy/mm/dd" attribute="rsrvDate"/>
			</write>
			<read component="DateViewer">
				<header label="<kfmt:message key="mbs.inapp.023"/>"/>
				<style format="<fmt:message key="date.medium"/>"/>
			</read>
			<hidden component="HiddenFieldEditor" parent="null"/>
		</rsrvDate>
		<trnsSrc>
			<hidden component="HiddenFieldEditor" parent="null"/>
		</trnsSrc>
		<trnsKey>
			<hidden component="HiddenFieldEditor" parent="null"/>
		</trnsKey>
		<titleContent>
			<search>
				<header label="<fmt:message key="cst.doc.001"/>" search="title,content"/>
			</search>
		</titleContent>
	</properties>
	<validators>
		<title validator="required"
				message="[<fmt:message key="doc.001"/>] <fmt:message key="doc.016"/>"/>
		<author validator="required"
				message="[<fmt:message key="doc.003"/>] <fmt:message key="doc.016"/>"/>
		<content ref="content"
				message="[<fmt:message key="doc.002"/>] <fmt:message key="doc.016"/>"/>
		<folder validator="required"
				message="[<fmt:message key="doc.210"/>] <fmt:message key="doc.016"/>"/>
	</validators>
</registry>
<%@ include file="/jspf/tail.xml.jsp" %>