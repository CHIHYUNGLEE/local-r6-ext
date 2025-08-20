package com.kcube.sys.emp;

import com.kcube.lib.action.ActionService;
import com.kcube.sys.AppBoot;

/**
 * 사용자 정보 Boot
 */
public class CstmEmployeeBoot implements AppBoot
{
	public void init() throws Exception
	{
		ActionService.addAction(new CstmEmployeeAction());
	}

	public void destroy()
	{
	}
}
