<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.kcube.sys.emp.EmployeeService"%>
<%@page import="com.kcube.sys.usr.UserService,com.kcube.sys.conf.module.ModuleConfigService,com.kcube.sys.module.ModuleParam,com.kcube.work.Work"%>
<%!
	private static final String CHAT = "com.kcube.work.WorkConfig.workChatUse";
	private static final String OPINION = "com.kcube.work.WorkConfig.opinion";
	private static final String SYMPATHY = "com.kcube.work.WorkConfig.sympathy";
	private static final String SYMPATHY_ICONS = "com.kcube.work.WorkConfig.sympathyIcons";
	private static final String WORK_FILE_SIZE = "com.kcube.work.WorkConfig.workFileSize";
	private static final String WORK_TOTAL_SIZE = "com.kcube.work.WorkConfig.workTotalSize";
	private static final String WORK_REPORT_NOT_SUPPORTED_EXT = "com.kcube.work.WorkConfig.workReportNotSupportedExt";
	private static final String WORK_READ_CNT_POPUP = "com.kcube.work.WorkConfig.readCntPopup";
	private static final String WORK_CHAT_FILE_SIZE = "com.kcube.work.WorkConfig.workChatFileSize";
	private static final String READ_MAP_SECURE = "com.kcube.work.WorkConfig.readMapSecure";
	private static final String WORK_PORTLET_KMID = "com.kcube.work.WorkConfig.workPortletKmId";
	private static final String GANTTCHART = "com.kcube.work.WorkConfig.ganttchart";

	private static com.kcube.doc.ItemConfig _itemConfig = (com.kcube.doc.ItemConfig) com.kcube.sys.conf.ConfigService
		.getConfig(com.kcube.doc.ItemConfig.class);

	private static com.kcube.doc.file.AttachmentConfig _attachmentConfig = (com.kcube.doc.file.AttachmentConfig) com.kcube.sys.conf.ConfigService
		.getConfig(com.kcube.doc.file.AttachmentConfig.class);

	private static com.kcube.sys.emp.EmployeeConfig _conf = (com.kcube.sys.emp.EmployeeConfig) com.kcube.sys.conf.ConfigService
		.getConfig(com.kcube.sys.emp.EmployeeConfig.class);

	private static Long getDprtId()
	{
		return _conf.getDprtId();
	}

	private String isRequiredTag()
	{
		return (_itemConfig.isRequiredTag()) ? "required='true'" : "";
	}

	private void checkAdmin(ModuleParam mParam) throws Exception
	{
		com.kcube.sys.usr.UserPermission.checkAppAdmin(mParam);
	}
	
	private boolean isChat(ModuleParam mParam)
	{
		return mParam == null ? false : ModuleConfigService.getBooleanProperty(mParam, CHAT);
	}
	
	private boolean isOpinion(ModuleParam mParam)
	{
		return mParam == null ? false : ModuleConfigService.getBooleanProperty(mParam, OPINION);
	}
	
	private boolean isSympathy(ModuleParam mParam)
	{
		return mParam == null ? false : ModuleConfigService.getBooleanProperty(mParam, SYMPATHY);
	}

	private String getSympathyIcons(ModuleParam mParam)
	{
		return mParam == null ? "" : ModuleConfigService.getProperty(mParam, SYMPATHY_ICONS);
	}

	private static String getNotSupportedExt()
	{
		return _attachmentConfig.getNotSupportedExt();
	}
	
	private static boolean isReadMapSecure(ModuleParam mParam)
	{
		return mParam == null ? false : ModuleConfigService.getBooleanProperty(mParam, READ_MAP_SECURE) && ModuleConfigService.useCtgrReadScrt(UserService.getTenantId(), mParam.getModuleId());
	}
	
	private boolean useCtgr(ModuleParam mParam)
	{
		return mParam == null ? false : ModuleConfigService.useCtgr(UserService.getTenantId(), mParam.getModuleId(), mParam.getAppId());
	}

	private String getWorkReportTemplateNotSupportedExt(ModuleParam mParam)
	{
		if (mParam != null)
		{
			String ext = ModuleConfigService.getProperty(mParam, WORK_REPORT_NOT_SUPPORTED_EXT);
			if (StringUtils.isNotEmpty(ext))
			{
				return ext;
			}
		}
		return getNotSupportedExt();
	}

	private int getFileSize(ModuleParam mParam)
	{
		if (mParam != null)
		{
			String size = ModuleConfigService.getProperty(mParam, WORK_FILE_SIZE);
			if (size != null)
				return Integer.parseInt(size);
			else
				return -1;
		}
		else
			return -1;
	}

	private int getTotalSize(ModuleParam mParam)
	{
		if (mParam != null)
		{
			String size = ModuleConfigService.getProperty(mParam, WORK_TOTAL_SIZE);
			if (size != null)
				return Integer.parseInt(size);
			else
				return -1;
		}
		else
			return -1;
	}

	private int getWorkChatFileSize(ModuleParam mParam)
	{
		if (mParam != null)
		{
			String size = ModuleConfigService.getProperty(mParam, WORK_CHAT_FILE_SIZE);
			if (size != null)
				return Integer.parseInt(size);
			else
				return -1;
		}
		else
			return -1;
	}
	
	private static boolean isReadCntPopup(ModuleParam mParam)
	{
		return mParam == null ? false : ModuleConfigService.getBooleanProperty(mParam, WORK_READ_CNT_POPUP);
	}

	private static Long getLevel2() throws Exception
	{
		Long level2 = null;
		try
		{
			level2 = EmployeeService.getEmployee(UserService.getUserId()).getDprt().getLevel2();
			if (level2 == null)
			{
				level2 = getDprtId();
			}
		}
		catch (NullPointerException e)
		{
			level2 = getDprtId();
		}
		return level2;
	}
	
	private int getPortletKmId(ModuleParam mParam)
	{
		return mParam == null ? -1 : Integer.parseInt(ModuleConfigService.getProperty(mParam, WORK_PORTLET_KMID));
	}
	
	private boolean isGanttChart(ModuleParam mParam)
	{
		return mParam == null ? false : ModuleConfigService.getBooleanProperty(mParam, GANTTCHART);
	}
%>