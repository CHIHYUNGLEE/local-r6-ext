package com.kcube.work.plan;

import com.kcube.doc.ItemPermission;
import com.kcube.lib.action.ActionContext;
import com.kcube.lib.jdbc.DbService;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.UserPermission;
import com.kcube.work.Work;

/**
 * @author 김경수
 *         <p>
 *         업무방 수행계획 사용자 Action
 */
public class WorkPlanUser
{
	/**
	 * 업무 수행계획 목록을 돌려준다.
	 */
	public static class ListByUser extends WorkPlanAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			WorkPlanSql sql = new WorkPlanSql(ctx.getLong("workId"));
			SqlSelect select = sql.getSelect();
			sql.writeJson(ctx.getWriter(), select);
		}
	}

	/**
	 * 업무 수행계획을 조회한다.
	 */
	public static class ReadByUser extends WorkPlanAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			WorkPlan server = (WorkPlan) _storage.load(ctx.getLong("id"));
			// 업무방 조회권한
			Work work = (Work) _workStorage.load(server.getWorkId());
			ItemPermission.checkUser(work, mp);

			_factory.marshal(ctx.getWriter(), server);
		}
	}

	/**
	 * 첨부 파일을 다운로드 한다.
	 */
	public static class DownloadByUser extends WorkPlanAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			WorkPlan.Attachment att = (WorkPlan.Attachment) DbService.load(
				WorkPlan.Attachment.class,
				ctx.getLong("id"));
			WorkPlan server = (WorkPlan) att.getItem();

			WorkPlanManager.setModuleParam(mp, server);
			WorkPlanPermission.checkAttachUser(server, mp);
			ctx.store(att);
		}
	}
}
