<%@page import="com.kcube.sys.usr.UserService"%>
<%@page import="com.kcube.sys.conf.module.ModuleConfigService,com.kcube.sys.module.ModuleParam,com.kcube.ekp.mbs.inapp.MbInApp"%>
<%!
private static com.kcube.doc.file.AttachmentConfig _attachmentConfig =
	(com.kcube.doc.file.AttachmentConfig)
		com.kcube.sys.conf.ConfigService.getConfig(
			com.kcube.doc.file.AttachmentConfig.class);

private static final String MBS_FILE_SIZE = "com.kcube.ekp.mbs.MbItemConfig.mbsFileSize";
private static final String MBS_TOTAL_SIZE = "com.kcube.ekp.mbs.MbItemConfig.mbsTotalSize";

private int getFileSize(ModuleParam mParam)
{
	return mParam == null ? -1 : Integer.parseInt(ModuleConfigService.getProperty(mParam, MBS_FILE_SIZE));
}

private int getTotalSize(ModuleParam mParam)
{
	return mParam == null ? -1 : Integer.parseInt(ModuleConfigService.getProperty(mParam, MBS_TOTAL_SIZE));
}

private String getNotSupportedExt()
{
	return _attachmentConfig.getNotSupportedExt();
}

private Long getDefaultHome(ModuleParam mParam)
{
	String val = ModuleConfigService.getProperty(mParam, MbInApp.MBS_DEFAULT_KM);
	return (mParam == null) ? null : (val == null) ? null : Long.parseLong(val);
}
private boolean useReadScrt(ModuleParam mParam)
{
	return ModuleConfigService.useCtgrReadScrt(UserService.getTenantId(), mParam.getModuleId(), mParam.getAppId());	
}
private boolean useWriteScrt(ModuleParam mParam)
{
	return ModuleConfigService.useCtgrWriteScrt(UserService.getTenantId(), mParam.getModuleId(), mParam.getAppId());	
}
private boolean useRplyScrt(ModuleParam mParam)
{
	return ModuleConfigService.useCtgrRplyScrt(UserService.getTenantId(), mParam.getModuleId(), mParam.getAppId());	
}
%>