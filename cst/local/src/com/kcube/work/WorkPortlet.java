package com.kcube.work;

import com.kcube.lib.action.ActionContext;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.UserPermission;

/**
 * 업무방 포틀릿 Action
 */
public class WorkPortlet
{
	/**
	 * 나의 업무목록 (담당자 또는 협업자에 속한)
	 */
	public static class MyWorkList extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			int status = ctx.getInt("status", -1);
			int folderId = ctx.getInt("folderId", -1);
			String year = ctx.getParameter("year");

			WorkSql sql = new WorkSql(mp, null, ctx.getParameter("ts"), ctx.getBoolean("isCountDisplay"));
			SqlSelect select = sql.getMyWorkList(status, folderId, year);
			SqlSelect count = sql.getMyWorkCount(status, folderId, year);

			sql.writeMyWorkListJson(ctx.getWriter(), select, count, folderId);
		}
	}

	/**
	 * 팀업무 목록
	 * <p>
	 * 본인 소속의 부서에 대한 업무 목록을 돌려준다.(부서원이 담당자 이거나 협업자 일 경우)
	 */
	public static class ListByTeamWork extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			String year = ctx.getParameter("year");
			Long userId = ctx.getLong("userId", null);
			int status = ctx.getInt("status", -1);

			WorkSql sql = new WorkSql(mp, ctx.getParameter("ts"));
			SqlSelect select = sql.getTeamVisibleSelect(year, userId, status);
			sql.writeJson(ctx.getWriter(), select);
		}
	}

	/**
	 * 전부서업무 문서 목록
	 */
	public static class ListByCompanyWork extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long folderId = new Long(ctx.getParameter("folderId"));
			String year = ctx.getParameter("year");
			boolean checkSecret = ctx.getBoolean("checkSecret");

			WorkSql sql = new WorkSql(
				mp, folderId, ctx.getParameter("ts"), ctx.getBoolean("isCountDisplay"), checkSecret);
			SqlSelect select = sql.getCompanyVisibleSelect(year);

			sql.writeJson(ctx.getWriter(), select);
		}
	}
}
