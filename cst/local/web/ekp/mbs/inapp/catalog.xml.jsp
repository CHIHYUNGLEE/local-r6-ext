<%@ include file="/jspf/head.xml.jsp" %>
<%@ include file="/ekp/mbs/inapp/config.jsp" %>
<registry>
	<properties>
		<id>
			<hidden component="HiddenFieldEditor" parent="null" id="id"/>
		</id>
		<name>
			<write component="MultiLanguageEditor" id="name">
				<header label="<kfmt:message key="mbs.inapp.095"/>" required="true"/>
				<style maxLength="85"/>
			</write>
		</name>
		<appType>
			<hidden component="HiddenFieldEditor" parent="null"/>
			<read component="ComboViewer" id="appType">
				<header label="<kfmt:message key="mbs.inapp.018"/>"/>
				<style options="<kfmt:message key="mbs.options.001"/>"/>
			</read>
			<write component="RadioGroupFieldEditor" id="appType">
				<header label="<kfmt:message key="mbs.inapp.018"/>"/>
				<style options="<kfmt:message key="mbs.options.002"/>"/>
			</write>
		</appType>
		<scrt>
			<hidden component="HiddenFieldEditor" parent="null"/>
			<read component="ComboViewer" id="scrt">
				<header label="<kfmt:message key="mbs.inapp.087"/>"/>
				<style options="<kfmt:message key="mbs.options.007"/>"/>
			</read>
			<write component="RadioGroupFieldEditor" id="scrt">
				<header label="<kfmt:message key="mbs.inapp.087"/>"/>
				<style options="<kfmt:message key="mbs.options.007"/>"/>
			</write>
		</scrt>
		<visible>
			<write component="RadioGroupFieldEditor">
				<header label="<kfmt:message key="mbs.inapp.009"/>"/>
				<style options="<kfmt:message key="mbs.options.004"/>"/>
			</write>
		</visible>
		<reserve>
			<write component="BooleanFieldEditor" id="reserve">
				<header label="<kfmt:message key="mbs.inapp.023"/>"/>
				<style message="<kfmt:message key="mbs.inapp.029"/>"/>
			</write>
		</reserve>
		<process>
			<write component="SecurityRadioFieldEditor" name="prcsType,prcsRoles" id="process">
				<header label="<kfmt:message key="mbs.inapp.054"/>"/>
				<style options="<kfmt:message key="mbs.options.008"/>" dialogOption="opnerComponent=SecurityRadioFieldEditor" flagCode="2000" nofocus="true"/>
			</write>
		</process>
		<notify>
			<write component="SecurityRadioFieldEditor" name="notiType,notiRoles" id="notify">
				<header label="<kfmt:message key="mbs.inapp.055"/>"/>
				<style options="<kfmt:message key="mbs.options.008"/>" dialogOption="opnerComponent=SecurityRadioFieldEditor" flagCode="2000" nofocus="true"/>
			</write>
		</notify>
		<unRead>
			<write component="BooleanFieldEditor">
				<header label="<kfmt:message key="mbs.inapp.025"/>"/>
				<style message="<kfmt:message key="mbs.inapp.029"/>"/>
			</write>
		</unRead>
		<fileLimit>
			<write component="BooleanTextFieldEditor" name="limitSize,maxFileSize,maxDocSize" id="fileLimit">
				<header label="<kfmt:message key="mbs.inapp.031"/>"/>
				<style textMarginLeft="10px" useHide="true">
					<boolStyle type="toggle" message="<kfmt:message key="mbs.inapp.082"/>" toggleMessage="<kfmt:message key="mbs.inapp.035"/>"/>
					<textStyle class="Array">
						<style width="150px" header="<kfmt:message key="mbs.inapp.032"/>" headerWidth="150" unit="<kfmt:message key="mbs.inapp.034"/>" maxLength="8"/>
						<style width="150px" header="<kfmt:message key="mbs.inapp.033"/>" headerWidth="150" unit="<kfmt:message key="mbs.inapp.034"/>" maxLength="8"/>
					</textStyle>
				</style>
			</write>
		</fileLimit>
		<limitExt>
			<write component="BooleanTextFieldEditor" name="limitExt,invalidExt" id="limitExt">
				<header label="<kfmt:message key="mbs.inapp.036"/>"/>
				<style inline="true">
					<boolStyle type="toggle" message="<kfmt:message key="mbs.inapp.082"/>" toggleMessage="<kfmt:message key="mbs.inapp.035"/>"/>
					<textStyle class="Array">
						<style width="200px" example="<kfmt:message key="mbs.inapp.037"/>" comment="<kfmt:message key="mbs.inapp.038"/>" tooltip="true"/>
					</textStyle>
				</style>
			</write>
		</limitExt>
		<vote>
			<write component="BooleanFieldEditor">
				<header label="<kfmt:message key="mbs.inapp.026"/>"/>
				<style message="<kfmt:message key="mbs.inapp.029"/>"/>
			</write>
		</vote>
		<introduce>
			<write component="BooleanFieldEditor" id="introduce">
				<header label="<kfmt:message key="mbs.inapp.027"/>"/>
				<style message="<kfmt:message key="mbs.inapp.029"/>"/>
			</write>
		</introduce>
		<showManager>
			<write component="RadioGroupFieldEditor" id="showManager">
				<header label="<kfmt:message key="mbs.inapp.030"/>"/>
				<style options="<kfmt:message key="mbs.options.006"/>"/>
			</write>
		</showManager>
		<intro>
			<write component="TextAreaEditor" id="intro">
				<style rows="5" maxLength="1000" description="<kfmt:message key="mbs.inapp.049"/>"/>
			</write>
		</intro>
		<references>
			<write component="RfrnFieldEditor" id="references">
				<header label="<fmt:message key="doc.062"/>"/>
				<style viewer="ListViewer" isScroll="false" isDel="true" delBtnToolTip="<fmt:message key="pub.015"/>" extendClass="rfrnList" itemId="id" appId="appId"/>
			</write>
		</references>
		<attachments>
			<write component="FileFieldEditor" id="attachments">
				<style fileSize="<%=getFileSize(moduleParam)%>" totalSize="<%=getTotalSize(moduleParam)%>" notSupport="<%=getNotSupportedExt()%>" sessionKey="${sessionKey}"/>
			</write>
		</attachments>
		<template>
			<write component="BooleanFieldEditor" id="template">
				<header label="<kfmt:message key="mbs.inapp.028"/>"/>
				<style message="<kfmt:message key="mbs.inapp.029"/>"/>
			</write>
		</template>
		<content>
			<write component="WebEditor" hidden="content" id="content">
				<style height="350" name="content" editor="kcube" attachUrl="ImageAction.Download" sessionKey="${sessionKey}"/>
			</write>
		</content>
		<caution>
			<write component="BooleanFieldEditor" id="caution">
				<header label="<fmt:message key="cst.mbs.inapp.001"/>"/>
				<style message="<fmt:message key="cst.mbs.inapp.002"/>"/>
			</write>
		</caution>
		<statement>
			<write component="TextAreaEditor" id="statement">
				<style rows="1" maxLength="100" description="<fmt:message key="cst.mbs.inapp.003"/>"/>
			</write>
		</statement>
		<exprPeriod>
			<write component="RadioGroupFieldEditor" id="exprPeriod">
				<header label="<kfmt:message key="mbs.inapp.024"/>"/>
				<style options="<kfmt:message key="mbs.options.005"/>"/>
			</write>
		</exprPeriod>
		<feedOpt>
			<write component="OptionSelectEditor" name="opinion,sympathy,reply" id="feedOpt">
				<header label="<kfmt:message key="mbs.inapp.042"/>"/>
				<style textMarginLeft="10px">
					<opts class="Array">
						<n label="<kfmt:message key="mbs.inapp.043"/>" options="<kfmt:message key="mbs.options.007"/>" />
						<n label="<kfmt:message key="mbs.inapp.044"/>" options="<kfmt:message key="mbs.options.007"/>" />
						<n label="<kfmt:message key="mbs.inapp.045"/>" options="<kfmt:message key="mbs.options.007"/>" />
					</opts>
				</style>
			</write>
		</feedOpt>
		<anonyOpt>
			<write component="OptionSelectEditor" name="anony,anonyOpn" id="anonyOpt">
				<header label="<kfmt:message key="mbs.inapp.046"/>"/>
				<style textMarginLeft="10px">
					<opts class="Array">
						<n label="<kfmt:message key="mbs.inapp.047"/>" options="<kfmt:message key="mbs.options.007"/>" />
						<n label="<kfmt:message key="mbs.inapp.048"/>" options="<kfmt:message key="mbs.options.007"/>" />
					</opts>
				</style>
			</write>
		</anonyOpt>
		<url>
			<write component="URLFieldEditor">
				<header label="<kfmt:message key="mbs.inapp.019"/>" required="true"/>
			</write>
		</url>
		<popUp>
			<write component="RadioGroupFieldEditor" id="popUp">
				<header label="<kfmt:message key="mbs.inapp.020"/>"/>
				<style options="<kfmt:message key="mbs.options.003"/>"/>
			</write>
		</popUp>
		<popUpStyle>
			<write component="TextFieldEditor" id="popUpStyle">
				<header label="<kfmt:message key="mbs.inapp.021"/>" desc="<fmt:message key="app.ptl.166"/>" isButton="false" required="true"/>
				<style maxLength="255"/>
			</write>
		</popUpStyle>
	</properties>
	<validators>
		<name validator="required"
				message="[<kfmt:message key="mbs.inapp.095"/>] <fmt:message key="doc.016"/>"/>
		<process ref="process"
				message="[<kfmt:message key="mbs.inapp.054"/>] <fmt:message key="doc.016"/>"/>
		<notify ref="notify"
				message="[<kfmt:message key="mbs.inapp.055"/>] <fmt:message key="doc.016"/>"/>
		<url validator="required"
				message="[<kfmt:message key="mbs.inapp.019"/>] <fmt:message key="doc.016"/>"/>
		<popUpStyle validator="required"
				message="[<kfmt:message key="mbs.inapp.021"/>] <fmt:message key="doc.016"/>"/>
		<content ref="content"
				message="[<fmt:message key="doc.002"/>] <fmt:message key="doc.016"/>"/>
		<attachments validator="required"
				message="[<fmt:message key="doc.017"/>] <fmt:message key="doc.016"/>"/>
	</validators>
</registry>
<%@ include file="/jspf/tail.xml.jsp" %>