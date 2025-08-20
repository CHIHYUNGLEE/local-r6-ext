package com.kcube.sys.emp;

import com.kcube.lib.action.Action;
import com.kcube.lib.action.ActionContext;

public class CstmEmployeeAction
{
	/**
	 * 사용자의 기본 개인화 메뉴를 생성한다.
	 * <p>
	 * /jsl/CstmEmployeeAction.CreateUserMenu.jsl
	 */
	public static class CreateUserMenu implements Action
	{
		public void execute(ActionContext ctx) throws Exception
		{
			new CstmEmployeeJob().start();
		}
	}
}
