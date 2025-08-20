<%@page contentType="text/html;charset=utf-8"%><%@ include file="/jspf/head.xml.jsp" %>
<%------------------------------------------------------------

	로컬 Interface 설정
 
------------------------------------------------------------%>
<configuration class="Array" title="Interface Config Settings">
	<config confid="com.kcube.cst.local.InterfaceConfig.superPassword" component="TextFieldEditor" text="Super Password"/>
	<config confid="com.kcube.cst.local.InterfaceConfig.configPrpt" component="TextFieldEditor" text="configPrpt"/>
	<config confid="com.kcube.cst.local.InterfaceConfig.configPrpt2" component="TextFieldEditor" text="configPrpt"/>
	<config confid="com.kcube.cst.local.InterfaceConfig.originMbs" component="AppSelectEditor" text="<fmt:message key="cst.config.001"/>">
		<style baseName="com.kcube.ekp.mbs.MbItem"/>
	</config>
	<config confid="com.kcube.cst.local.InterfaceConfig.originBbs" component="AppSelectEditor" text="<fmt:message key="cst.config.002"/>">
		<style baseName="com.kcube.ekp.bbs.BdItem"/>
	</config>
	<config confid="com.kcube.cst.local.InterfaceConfig.originWiki" component="AppSelectEditor" text="<fmt:message key="cst.config.003"/>">
		<style baseName="com.kcube.ekp.wiki.WikiItem"/>
	</config>
</configuration>
<%@ include file="/jspf/tail.xml.jsp" %>
