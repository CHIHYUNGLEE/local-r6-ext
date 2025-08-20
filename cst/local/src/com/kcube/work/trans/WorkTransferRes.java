package com.kcube.work.trans;

import com.kcube.lib.action.ActionContext;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.UserPermission;

/**
 * @author 신운재
 *         <p>
 *         업무방 인수인계 응답자 Action
 */
public class WorkTransferRes
{
	/**
	 * 요청리스트 내림.<br>
	 * 인수자가 본인
	 * @author 신운재
	 */
	public static class ReqListByResUser extends WorkTransferAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);
			String ts = ctx.getParameter("ts");
			int status = ctx.getInt("status", 0);

			SqlSelect select = _manager.getResListByUser(mp, status);
			_manager.writeJson(ctx.getWriter(), select, select, ts);
		}
	}

	/**
	 * 요청을 승인함.<br>
	 * 인수인계가 되어 담당자가 본인이 됨.
	 * @author 신운재
	 */
	public static class ApproveReqByResponser extends WorkTransferAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long trnsId = ctx.getLong("transId");
			WorkTransfer server = (WorkTransfer) _storage.loadWithLock(trnsId);
			WorkTransferPermisson.checkRespose(server);

			_manager.approve(server);
		}
	}

	/**
	 * 요청을 반려함.<br>
	 * 반려의견을 작성함.
	 * @author 신운재
	 */
	public static class DoReject extends WorkTransferAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long trnsId = ctx.getLong("transId");

			WorkTransfer client = unmarshal(ctx);
			WorkTransfer server = (WorkTransfer) _storage.loadWithLock(trnsId);

			WorkTransferPermisson.checkRespose(server);
			_manager.reject(client, server);
		}
	}
}