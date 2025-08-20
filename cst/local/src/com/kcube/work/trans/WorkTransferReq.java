package com.kcube.work.trans;

import com.kcube.lib.action.ActionContext;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.UserPermission;

/**
 * @author 신운재
 *         <p>
 *         업무방 인수인계 요청자 Action
 */
public class WorkTransferReq
{

	/**
	 * 요청리스트 내림.<br>
	 * 요청자가 본인
	 * @author 신운재
	 */
	public static class ReqListByReqUser extends WorkTransferAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);
			String ts = ctx.getParameter("ts");
			int status = ctx.getInt("status", 0);
			SqlSelect select = _manager.getReqListByUser(mp, status);
			_manager.writeJson(ctx.getWriter(), select, select, ts);
		}
	}

	/**
	 * 요청 하나의 대한 상세.
	 * @author 신운재
	 */
	public static class ReadByApprover extends WorkTransferAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);
			Long id = ctx.getLong("id", 0);
			WorkTransfer server = (WorkTransfer) _storage.load(id);
			_factory.marshal(ctx.getWriter(), server);

		}
	}

	/**
	 * 인계 요청에 포함되는 업무리스트 출력.
	 * @author 신운재
	 */
	public static class WorkListByUser extends WorkTransferAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);
			Long id = ctx.getLong("id", 0);

			SqlSelect select = _manager.getWorkListByWorkId(id);
			String ts = ctx.getParameter("ts");

			_manager.writeItemListJson(ctx.getWriter(), select, select, ts);

		}
	}

	/**
	 * 담당자 변경 버튼 클릭시 업무방 인수인계
	 * @author 신운재
	 */
	public static class TakeOver extends WorkTransferAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);
			Long[] ids = ctx.getLongValues("id");

			WorkTransferPermisson.checkTakOverDuplicate(ids);

			WorkTransfer trans = unmarshal(ctx);
			WorkTransfer server = (WorkTransfer) _storage.create();

			_manager.setItemList(trans, ids);
			_manager.update(trans, server, mp);

		}
	}

	/**
	 * 본인담당인 업무방을 지정된 사용자에게 인수 요청.
	 * @author 신운재
	 */
	public static class TakeOverReq extends WorkTransferAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);
			Long[] ids = ctx.getLongTokens("id");

			WorkTransferPermisson.checkTakOverDuplicate(ids);

			WorkTransfer trans = unmarshal(ctx);
			WorkTransfer server = (WorkTransfer) _storage.create();

			_manager.setItemList(trans, ids);
			_manager.update(trans, server, mp);

		}
	}

	/**
	 * 본인담당인 업무방을 지정된 사용자에게 인수 요청.
	 * @author 신운재
	 */
	public static class TakeOverAll extends WorkTransferAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			WorkTransferPermisson.checkTakOverDuplicate();

			WorkTransfer trans = unmarshal(ctx);
			WorkTransfer server = (WorkTransfer) _storage.create();

			_manager.setItemList(trans);
			_manager.update(trans, server, mp);
		}
	}
}