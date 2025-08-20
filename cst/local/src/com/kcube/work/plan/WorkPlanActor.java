package com.kcube.work.plan;

import java.util.Date;

import com.kcube.doc.ItemPermission;
import com.kcube.lib.action.ActionContext;
import com.kcube.lib.jdbc.DbService;
import com.kcube.lib.json.JsonWriter;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.UserPermission;
import com.kcube.sys.usr.UserService;
import com.kcube.work.Work;
import com.kcube.work.plan.WorkPlanPermission.SubPlanException;

import net.sf.json.JSONObject;

/**
 * 업무수행계획, 담당자 Action Class
 */
public class WorkPlanActor
{
	/**
	 * 등록 전 필요로 하는 데이터들을 채워넣는다.
	 * <p>
	 * workId, author, tenantId, gid, (pid가 존재할 경우)pid, startDate, endDate
	 */
	public static class PreWrite extends WorkPlanAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Work work = (Work) _workStorage.load(ctx.getLong("workId"));

			WorkPlan item = new WorkPlan();
			item.setWorkId(work.getId());
			item.setAuthor(UserService.getUser());
			item.setTenantId(work.getTenantId());

			Long pid = ctx.getLong("pid", null);
			if (pid != null)
			{
				WorkPlan parent = (WorkPlan) _storage.load(pid);
				item.setPid(parent.getId());
				item.setGid(parent.getGid());
				item.setMinStartDate(parent.getStartDate());
				item.setMaxEndDate(parent.getEndDate());
			}

			_factory.marshal(ctx.getWriter(), item);
		}
	}

	/**
	 * 계획을 등록한다.
	 */
	public static class DoRegister extends WorkPlanAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			WorkPlan client = unmarshal(ctx);
			// 업무방 권한 체크 (수정 가능 여부)
			Work work = (Work) _workStorage.loadWithLock(client.getWorkId());
			ItemPermission.checkUser(work, mp);
			WorkPlanPermission.checkPlanRegister(work);

			// 등록 시에 plan 수 증가 처리함.
			work.setPlanCnt(work.getPlanCnt() + 1);
			work.setLastUpdt(new Date());

			WorkPlan server = (WorkPlan) _storage.create();
			WorkPlanManager.update(mp, server, client);
			WorkPlanManager.register(server);
			WorkPlanHistory.registered(mp, work);

			if (server.isReply())
			{
				_reply.setStepPos(server);
				_reply.checkParentExists(server);
			}
			else
			{
				server.setPos(0);
				// 임의의 수 1000000을 넣어 임시로 마지막 step 번호를 설정한다.
				server.setStep(1000000);
				server.setGid(server.getId());
			}
			DbService.flush();

			WorkPlanSql sql = new WorkPlanSql(client.getWorkId());
			sql.resetStep();

			JsonWriter writer = new JsonWriter(ctx.getWriter());
			writer.write("id", server.getId().toString());
			ctx.setParameter("id", server.getId().toString());
		}
	}

	/**
	 * 업무 수행계획을 삭제한다.
	 */
	public static class DoDelete extends WorkPlanAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			// 업무방 권한 체크 (수정 가능 여부)
			Work work = (Work) _workStorage.load(ctx.getLong("workId"));
			ItemPermission.checkUser(work, mp);
			WorkPlanPermission.checkPlanUpdate(work);

			// 삭제 시에 plan 수 감소 처리함.
			work.setPlanCnt(work.getPlanCnt() - 1);

			// 업무 수행 계획 삭제 시에 활동 및 로그에 남김
			WorkPlanHistory.deleted(mp, work);

			WorkPlan server = (WorkPlan) _storage.loadWithLock(ctx.getLong("id"));
			WorkPlanManager.remove(server);

			work.setLastUpdt(new Date());

			JsonWriter writer = new JsonWriter(ctx.getWriter());
			writer.write("id", server.getId().toString());
		}
	}

	/**
	 * 업무 수행계획을 수정한다. (항목 모두 수정가능함)
	 */
	public static class DoUpdate extends WorkPlanAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			WorkPlan client = unmarshal(ctx);
			// 업무방 권한 체크 (수정 가능 여부)
			Work work = (Work) _workStorage.load(client.getWorkId());
			ItemPermission.checkUser(work, mp);
			WorkPlanPermission.checkPlanUpdate(work);

			WorkPlan server = (WorkPlan) _storage.loadOrCreateWithLock(client.getId());
			WorkPlanManager.update(mp, server, client);

			// lastUpdt Update
			work.setLastUpdt(new Date());

			JsonWriter writer = new JsonWriter(ctx.getWriter());
			writer.write("id", server.getId().toString());
		}
	}

	/**
	 * SubWork의 담당자 목록을 출력한다.
	 */
	public static class ActorList extends WorkPlanAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long[] ids = ctx.getLongTokens("ids");
			WorkPlanSql sql = new WorkPlanSql();
			SqlSelect select = sql.getActorSelect(ids);
			sql.writeActorJson(ctx.getWriter(), select);
		}
	}

	/**
	 * 계획의 위아래 순서를 바꾼다.
	 */
	public static class Swap extends WorkPlanAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long workId = ctx.getLong("workId");
			Work work = (Work) _workStorage.load(workId);

			ItemPermission.checkUser(work, mp);
			WorkPlanPermission.checkPlanUpdate(work);

			WorkPlan up = (WorkPlan) _storage.loadWithLock(ctx.getLong("upId"));
			WorkPlan down = (WorkPlan) _storage.loadWithLock(ctx.getLong("downId"));

			WorkPlanManager.swap(up, down);
			DbService.flush();

			work.setLastUpdt(new Date());

			WorkPlanSql sql = new WorkPlanSql(workId);
			sql.resetStep();
		}
	}

	/**
	 * 업무 수행계획 수정 시 필요한 정보를 불러온다.
	 */
	public static class EditReadByActor extends WorkPlanAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			WorkPlan server = (WorkPlan) _storage.load(ctx.getLong("id"));
			// 업무방 조회권한
			Work work = (Work) _workStorage.load(server.getWorkId());
			ItemPermission.checkUser(work, mp);

			WorkPlanManager.setLimitDate(server);

			_factory.marshal(ctx.getWriter(), server);
		}
	}

	/**
	 * 업무수행계획 상태를 변경한다.
	 */
	public static class UpdateStatus extends WorkPlanAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			int status = ctx.getInt("status");
			WorkPlan server = (WorkPlan) _storage.load(ctx.getLong("id"));

			WorkPlanManager.setModuleParam(mp, server);
			WorkPlanPermission.checkActor(server);

			WorkPlanSql sql = new WorkPlanSql(server.getWorkId());
			if (status == WorkPlan.COMPLETE_STATUS && sql.checkPlanIngStatus(server.getId(), server.getPos()))
			{
				throw new SubPlanException();
			}
			else
			{
				WorkPlanManager.updateStatus(mp, server, status);
				JSONObject obj = new JSONObject();
				obj.put("status", status);
				if (status == WorkPlan.COMPLETE_STATUS)
				{
					obj.put("completeDate", server.getCompleteDate().getTime());
				}
				ctx.getWriter().print(obj.toString());
			}
		}
	}

	/**
	 * 하위 수행계획의 상태를 일괄 변경한다. 확인
	 */
	public static class UpdateStatusAll extends WorkPlanAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			int status = ctx.getInt("status", WorkPlan.COMPLETE_STATUS);
			WorkPlan server = (WorkPlan) _storage.load(ctx.getLong("id"));

			WorkPlanManager.setModuleParam(mp, server);
			WorkPlanPermission.checkActor(server);

			WorkPlanSql sql = new WorkPlanSql(server.getWorkId());
			Long[] ids = sql.getPlanIngList(server.getId(), server.getPos());

			WorkPlanManager.updateStatusAll(mp, ids, WorkPlan.COMPLETE_STATUS);
			WorkPlanManager.updateStatus(mp, server, status);

			JSONObject obj = new JSONObject();
			obj.put("status", status);
			if (status == WorkPlan.COMPLETE_STATUS)
			{
				obj.put("completeDate", server.getCompleteDate().getTime());
			}
			ctx.getWriter().print(obj.toString());
		}
	}

	/**
	 * 담당 수행업무 목록, 확인
	 */
	public static class ListByActor extends WorkPlanAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			int status = ctx.getInt("status", -1);

			WorkPlanSql sql = new WorkPlanSql(mp, ctx.getParameter("ts"));
			SqlSelect select = sql.getMyWorkList(status);
			sql.writePlanListJson(ctx.getWriter(), select);
		}
	}
}
