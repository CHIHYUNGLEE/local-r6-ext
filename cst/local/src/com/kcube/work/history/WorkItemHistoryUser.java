package com.kcube.work.history;

import com.kcube.lib.action.ActionContext;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.UserPermission;

/**
 * 업무방 이력 사용자 Action 정의 Classs
 * @author WJ
 */
public class WorkItemHistoryUser
{
	/**
	 * 문서(문서에 속한 파일이력 포함)에 대한 로그를 출력한다.
	 */
	public static class DocListByUser extends WorkItemHistoryAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long minId = ctx.getLong("minId", null);
			Long itemId = ctx.getLong("itemId", null);
			int count = ctx.getInt("count", 10);
			String ts = "1." + count + "..";

			SqlSelect stmt = WorkItemHistorySql.getDocList(itemId, minId);
			WorkItemHistorySql.writeJson(ctx.getWriter(), stmt, ts);
		}
	}

	/**
	 * 나와 관련 활동에(협업자 및 담당자로 속해있는 활동) 대한 로그를 출력한다.
	 */
	public static class DocRecentListByUser extends WorkItemHistoryAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long minId = ctx.getLong("minId", null);
			int count = ctx.getInt("count", 10);
			String ts = "1." + count + "..";

			SqlSelect stmt = WorkItemHistorySql.getDocRecentList(minId, mp);
			WorkItemHistorySql.writeJson(ctx.getWriter(), stmt, ts);
		}
	}
}