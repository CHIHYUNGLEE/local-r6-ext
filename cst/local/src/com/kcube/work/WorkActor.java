package com.kcube.work;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.kcube.doc.ItemPermission;
import com.kcube.lib.action.ActionContext;
import com.kcube.lib.json.JsonWriter;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.User;
import com.kcube.sys.usr.UserPermission;
import com.kcube.sys.usr.UserService;
import com.kcube.work.plan.WorkPlan;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author 김경수
 *         <p>
 *         업무방 담당자 Action
 */
public class WorkActor
{
	/**
	 * 업무방 상태를 변경한다.
	 */
	public static class UpdateStatus extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			int status = ctx.getInt("status", Work.COMPLETE_STATUS);
			Work server = (Work) _storage.loadOrCreateWithLock(ctx.getLong("id"));

			ItemPermission.checkUser(server, mp);
			WorkPermission.checkStatusUpdate(server);

			WorkPermission.checkCompletePlan(server, status);

			WorkManager.updateStatus(mp, server, status);
			WorkHistory.updatedStatus(mp, server);
			if (status == WorkPlan.COMPLETE_STATUS)
			{
				WorkHistory.completed(mp, server);
			}

			JsonWriter jwriter = new JsonWriter(ctx.getWriter());
			jwriter.startList();
			jwriter.setAttribute("status", server.getStatus());
			if (server.getStatus() == Work.COMPLETE_STATUS)
			{
				jwriter.setAttribute("completeDate", server.getCompleteDate().getTime());
			}
			jwriter.endList();
		}
	}

	/**
	 * 업무방 협업자들을 변경한다.
	 */
	public static class UpdateHelpers extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Work client = unmarshal(ctx);
			Work server = (Work) _storage.loadOrCreateWithLock(client.getId());

			// 수정 가능여부에 대한 권한체크 처리 (협업자 변경 액션을 사용자와 관리자가 함께 사용하고 있으므로 분기 처리)
			if (ItemPermission.isAppAdmin(server, mp))
			{
				WorkPermission.checkUpdateByAdmin(server);
			}
			else
			{
				ItemPermission.checkUser(server, mp);
				WorkPermission.checkUpdate(server);
			}

			WorkHistory.updatedHelpers(mp, server, client);
			WorkManager.updateHelpers(server, client);
			WorkManager.addSecurity(server);
			_listener.changeMember(server);

			JsonWriter writer = new JsonWriter(ctx.getWriter());
			writer.writeListHeader();
			for (User user : server.getHelpers())
			{
				writer.startList();
				writer.setAttribute("displayName", user.getDisplayName());
				writer.setAttribute("id", user.getUserId());
				writer.setAttribute("name", user.getName());
				writer.endList();
			}
			writer.writeListFooter();
		}
	}

	/**
	 * 업무방 공유자들을 변경한다.
	 */
	public static class UpdateSharers extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Work client = unmarshal(ctx);
			Work server = (Work) _storage.loadOrCreateWithLock(client.getId());

			ItemPermission.checkUser(server, mp);
			WorkPermission.checkUpdate(server);

			WorkHistory.updatedSharers(mp, server, client);
			WorkManager.updateSharers(server, client);
			WorkManager.addSecurity(server);

			JsonWriter writer = new JsonWriter(ctx.getWriter());
			writer.writeListHeader();
			for (User user : server.getSharers())
			{
				writer.startList();
				writer.setAttribute("displayName", user.getDisplayName());
				writer.setAttribute("id", user.getUserId());
				writer.setAttribute("name", user.getName());
				writer.endList();
			}
			writer.writeListFooter();
		}
	}

	/**
	 * 업무방 공유자들을 넘겨받은 Array 객체에 의해 재설정한다.
	 */
	public static class UpdateSharersByArray extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			JSONArray jsonSharers = JSONArray.fromObject(ctx.getParameter("item"));
			List<User> sharers = new ArrayList<User>();
			for (int i = 0; i < jsonSharers.size(); i++)
			{
				JSONObject obj = jsonSharers.getJSONObject(i);
				User user = new User(
					obj.getLong("id"), obj.getString("name"), obj.getString("displayName"), UserService.getTenantId());
				sharers.add(user);
			}
			Work server = (Work) _storage.loadOrCreateWithLock(ctx.getLong("id"));

			ItemPermission.checkUser(server, mp);
			WorkPermission.checkUpdate(server);

			WorkHistory.updatedSharers(mp, server, sharers);
			WorkManager.updateSharers(server, sharers);
			WorkManager.addSecurity(server);
		}
	}

	/**
	 * 업무방을 수정한다.
	 */
	public static class DoUpdateByActor extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Work client = unmarshal(ctx);
			Work server = (Work) _storage.loadOrCreateWithLock(client.getId());

			ItemPermission.checkUser(server, mp);
			WorkPermission.checkUpdate(server);

			WorkManager.update(server, client);
			// server.setExprDate(ExpireService.getExprDate(server.getRgstDate(),
			// server.getExprMonth()));
			WorkHistory.updated(mp, server);

			_listener.changeMember(server);
		}
	}

	/**
	 * 업무방을 삭제한다.
	 */
	public static class DoDeleteByActor extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long id = ctx.getLong("id");
			Work server = (Work) _storage.loadWithLock(id);

			ItemPermission.checkUser(server, mp);
			WorkPermission.checkDelete(server);

			WorkManager.delete(server);
			WorkHistory.deleted(mp, server);
			_listener.deleted(server);
		}
	}

	/**
	 * 업무방 기능 선택을 변경한다.
	 */
	public static class UpdateItemsVisible extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long id = ctx.getLong("id");
			Work server = (Work) _storage.loadOrCreateWithLock(id);

			ItemPermission.checkUser(server, mp);
			WorkPermission.checkUpdate(server);

			server.setItemsVisible(ctx.getParameter("itemsVisible"));
			server.setLastUpdt(new Date());

			WorkHistory.updateItemVisible(mp, server);
		}
	}

	/**
	 * 승인자/검토자/보고 요약문을 수정한다.
	 * <p>
	 * 담당자만 수정할 수 있으며, 작성중 상태일 때만 변경이 가능하다.
	 * @author WJ
	 */
	public static class UpdateApprSetting extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Work client = unmarshal(ctx);
			Work server = (Work) _storage.loadWithLock(ctx.getLong("id"));

			ItemPermission.checkUser(server, mp);
			WorkPermission.checkUpdateApprSetting(server);

			server.setApprReviewSetting(client.getApprReviewSetting());
			server.setLastUpdt(new Date());
			WorkManager.addSecurity(server);
			WorkHistory.updateApprSetting(mp, server);
			_factory.marshal(ctx.getWriter(), server);
		}
	}

	/**
	 * 승인완료 상태인 업무 중<br>
	 * 본인이 담당하고 있는 업무를 담당자 변경한다.
	 * @author WJ
	 */
	public static class DoChangeInCharger extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Work client = unmarshal(ctx);
			Work server = (Work) _storage.loadWithLock(ctx.getLong("serverId"));

			ItemPermission.checkUser(server, mp);
			WorkPermission.checkActor(server);

			server.setActor(client.getActor());
			server.setLastUpdt(new Date());
			WorkHistory.changedInCharger(mp, server);
			ctx.setParameter("id", server.getId().toString());
		}
	}

	/**
	 * 로그인사용자가 담당자인 업무방 중간버전 파일 목록(작업중 또는 완료상태)
	 */
	public static class FileListOfNotLastVersion extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long[] ids = ctx.getLongTokens("ids");
			int[] status = {Work.COMPLETE_STATUS, Work.APRV_CMPLT_STATUS, Work.WORKING_STATUS};

			WorkSql sql = new WorkSql(mp);
			SqlSelect select = sql.getFileVersionList(ids, false, status);
			sql.writeFileVersionJson(ctx.getWriter(), select);
		}
	}
}
