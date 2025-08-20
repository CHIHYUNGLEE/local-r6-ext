package com.kcube.work.request.share;

import java.util.ArrayList;
import java.util.List;

import com.kcube.lib.action.ActionContext;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.User;
import com.kcube.sys.usr.UserPermission;
import com.kcube.sys.usr.UserService;
import com.kcube.work.Work;
import com.kcube.work.WorkManager;
import com.kcube.work.history.WorkItemHistory;
import com.kcube.work.history.WorkItemHistoryManager;

/**
 * @author 김경수
 *         <p>
 *         공유 권한 요청 관련 사용자 Action
 */
public class WorkShareRequestUser
{
	/**
	 * 요청한 권한신청 목록
	 */
	public static class ListByApplicant extends WorkShareRequestAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			WorkShareRequestSql sql = new WorkShareRequestSql(mp, ctx.getParameter("ts"), ctx.getInt("status", 0));
			SqlSelect stmt = sql.getApplyList();
			sql.writeJson(ctx.getWriter(), stmt);
		}
	}

	/**
	 * 요청받은 권한신청 목록
	 */
	public static class ListByApprover extends WorkShareRequestAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			WorkShareRequestSql sql = new WorkShareRequestSql(mp, ctx.getParameter("ts"), ctx.getInt("status", 0));
			SqlSelect stmt = sql.getReceiveList();
			sql.writeJson(ctx.getWriter(), stmt);
		}
	}

	/**
	 * 공유 요청 신청
	 */
	public static class DoApply extends WorkShareRequestAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			WorkShareRequest client = (WorkShareRequest) _factory.unmarshal(ctx.getParameter("item"));
			WorkShareRequestPermission.checkDuplicateRequest(client);

			WorkShareRequest server = (WorkShareRequest) _storage.create();
			server.setReqUser(UserService.getUser());
			_manager.apply(server, client);

			Work work = (Work) _workStorage.load(client.getWorkId());
			WorkShareRequestHistory.apply(mp, work);
		}
	}

	/**
	 * 공유 요청 신청 조회
	 */
	public static class ReadByApprover extends WorkShareRequestAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			WorkShareRequest server = (WorkShareRequest) _storage.load(ctx.getLong("id"));
			Work work = (Work) _workStorage.load(server.getWorkId());
			WorkShareRequestPermission.checkReadPermission(server, work);
			_factory.marshal(ctx.getWriter(), server);
		}
	}

	/**
	 * 승인
	 */
	public static class DoApprove extends WorkShareRequestAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			WorkShareRequest client = (WorkShareRequest) _factory.unmarshal(ctx.getParameter("item"));
			WorkShareRequest server = (WorkShareRequest) _storage.loadWithLock(client.getId());
			Work workServer = (Work) _workStorage.load(server.getWorkId());
			WorkShareRequestPermission.checkApprovePermission(workServer);

			_manager.approve(server, client);
			WorkShareRequestHistory.approve(mp, workServer, server.getReqUser());

			if (!workServer.getSharers().contains(server.getReqUser()))
			{
				List<User> sharers = new ArrayList<User>();
				sharers.addAll(workServer.getSharers());
				sharers.add(server.getReqUser());

				// 공유자로 추가됨을 history에 남김
				WorkItemHistoryManager.history(WorkItemHistory.ADD_SHARE, workServer, server.getReqUser());

				Work workClient = new Work();
				workClient.setSharers(sharers);
				WorkManager.updateSharers(workServer, workClient);
				WorkManager.addSecurity(workServer);
			}
		}
	}

	/**
	 * 반려
	 */
	public static class DoReject extends WorkShareRequestAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			WorkShareRequest client = (WorkShareRequest) _factory.unmarshal(ctx.getParameter("item"));
			WorkShareRequest server = (WorkShareRequest) _storage.loadWithLock(client.getId());
			Work workServer = (Work) _workStorage.load(server.getWorkId());
			WorkShareRequestPermission.checkRejectPermission(workServer);

			_manager.reject(server, client);

			WorkShareRequestHistory.reject(mp, workServer, server.getReqUser());
		}
	}
}