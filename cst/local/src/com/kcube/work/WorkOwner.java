package com.kcube.work;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.kcube.doc.Item;
import com.kcube.doc.ItemPermission;
import com.kcube.lib.action.Action;
import com.kcube.lib.action.ActionContext;
import com.kcube.lib.jdbc.DbService;
import com.kcube.lib.json.JsonWriter;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.PermissionDeniedException;
import com.kcube.sys.usr.User;
import com.kcube.sys.usr.UserPermission;
import com.kcube.sys.usr.UserService;
import com.kcube.sys.usr.UserState;
import com.kcube.work.history.WorkItemHistory;
import com.kcube.work.history.WorkItemHistoryManager;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author 김경수
 */
public class WorkOwner
{
	/**
	 * 나의 업무방 목록을 출력한다.
	 */
	public static class ListByOwner extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			int status = ctx.getInt("status", -1);

			WorkSql sql = new WorkSql(
				mp, ctx.getLong("tr", null), ctx.getParameter("ts"), ctx.getBoolean("isCountDisplay"));

			sql.writeLastUpdtDescJson(ctx.getWriter(), sql.getOwnerList(UserService.getUserId(), status));
		}
	}

	/**
	 * 공유받은 업무방 목록을 출력한다.
	 */
	public static class SharedListByOwner extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			WorkSql sql = new WorkSql(
				mp, ctx.getLong("tr", null), ctx.getParameter("ts"), ctx.getBoolean("isCountDisplay"));

			sql.writeLastUpdtDescJson(ctx.getWriter(), sql.getSharedSql());
		}
	}

	/**
	 * 본인 담당업무 리스트 .
	 */
	public static class InChargeListByOwner extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);
			/**
			 * work.151=-1:전체,0:전부서,4000:보안업무,5000:목록숨김
			 */
			int securityRange = ctx.getInt("securityRange", -1);
			int status = ctx.getInt("status", -1);

			WorkSql sql = new WorkSql(
				mp, ctx.getLong("tr", null), ctx.getParameter("ts"), ctx.getBoolean("isCountDisplay"));

			SqlSelect stmt = sql.getMyChageListSql(status);

			if (securityRange > -1)
			{
				if (securityRange == Work.SECURE_NONE)
				{
					stmt.where("SCRT_LEVEL = ?", Work.SECURE_NONE);
				}
				else if (securityRange == 5000)
				{
					stmt.where("SCRT_LEVEL >= ?", Work.SECURE_ACTOR);
				}
				else
				{
					stmt.where("SCRT_LEVEL > ?", UserState.SECURE_ITEM);
				}
			}

			sql.writeLastUpdtDescJson(ctx.getWriter(), stmt);
		}
	}

	/**
	 * 공유받은 업무방을 삭제한다.
	 */
	public static class DoDeleteSharedByOwner extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long[] ids = ctx.getLongValues("id");
			for (int i = 0; i < ids.length; i++)
			{
				Work server = (Work) _storage.loadOrCreateWithLock(ids[i]);
				List<User> sharers = server.getSharers();
				ItemPermission.checkUser(server, mp);
				for (int j = 0; j < sharers.size(); j++)
				{
					User shared = sharers.get(j);
					if (UserService.getUserId().equals(shared.getUserId()))
					{
						sharers.remove(j);
					}
				}
				WorkManager.addSecurity(server);
				DbService.flush();
			}
		}
	}

	/**
	 * 나의 업무방을 수정한다.
	 */
	public static class DoUpdateByOwner extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Work client = unmarshal(ctx);
			Work server = (Work) _storage.loadOrCreateWithLock(client.getId());

			ItemPermission.checkOwner(server);
			WorkPermission.checkUpdate(server);
			WorkManager.update(server, client);
			// server.setExprDate(ExpireService.getExprDate(server.getRgstDate(),
			// server.getExprMonth()));
			WorkHistory.updated(mp, server);

			_listener.changeMember(server);
		}
	}

	/**
	 * 나의 업무방을 삭제한다.
	 */
	public static class DoDeleteByOwner extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long id = ctx.getLong("id");
			Work server = (Work) _storage.loadWithLock(id);

			ItemPermission.checkOwner(server);
			WorkPermission.checkActor(server);
			WorkManager.delete(server);
			WorkHistory.deleted(mp, server);
			_listener.deleted(server);
		}
	}

	/**
	 * 중요업무 여부 확인
	 * <p>
	 * 중요업무 여부를 확인하여 즐겨찾기된 Id를 돌려준다.
	 */
	public static class CheckFavorite extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			WorkSql sql = new WorkSql(mp);
			Long[] ids = ctx.getLongTokens("ids");
			JsonWriter writer = new JsonWriter(ctx.getWriter());

			if (ids.length != 0)
			{
				SqlSelect stmt = sql.getExistsFvrt(ids);
				ResultSet rs = stmt.query();
				Long itemId;

				writer.writeListHeader();
				while (rs.next())
				{
					itemId = rs.getLong("itemid");
					writer.startList();
					writer.setFirstAttr(true);
					writer.setAttribute("itemId", itemId);
					writer.endList();
				}
				writer.writeListFooter();
			}
			else
			{
				writer.writeListHeader();
				writer.writeListFooter();
			}
		}
	}

	/**
	 * 나의 중요업무 목록
	 */
	public static class FavoriteWorkList extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			WorkSql sql = new WorkSql(mp, null, ctx.getParameter("ts"));
			SqlSelect select = sql.getFavoriteWorkList();
			SqlSelect count = sql.getFavoriteWorkCount();

			sql.writeFavoriteWorkJson(ctx.getWriter(), select, count);
		}
	}

	/**
	 * 중요업무 해제
	 */
	public static class FavoriteWorkDisbandment extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			WorkSql sql = new WorkSql(mp);
			Long[] ids = ctx.getLongValues("id");
			sql.deleteFvrt(ids);
		}
	}

	/**
	 * 중요업무 추가
	 */
	public static class FavoriteWorkAdd extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			WorkSql sql = new WorkSql(mp);
			Long objId = ctx.getLong("id");
			sql.addFvrt(objId);
		}
	}

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
	 * 나의 업무목록 (포틀릿용)
	 */
	public static class MyPortlet extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			WorkSql sql = new WorkSql(mp, null, ctx.getParameter("ts"), ctx.getBoolean("isCountDisplay"));
			SqlSelect select = sql.getMyPortletList();
			SqlSelect count = sql.getMyPortletCount();

			sql.writeMyPortletJson(ctx.getWriter(), select, count);
		}
	}

	/**
	 * 본인담당(담당자,협업자) 업무방 중 삭제된 업무방 리스트
	 */
	public static class DeltedListByOwner extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			WorkSql sql = new WorkSql(
				mp, ctx.getLong("tr", null), ctx.getParameter("ts"), ctx.getBoolean("isCountDisplay"));

			sql.writeJson(ctx.getWriter(), sql.getDeletedListSql());
		}
	}

	/**
	 * 업무방 삭제
	 * @author 신운재
	 */
	public static class DeleteItemAll extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long[] ids = ctx.getLongValues("id");

			for (int i = 0; i < ids.length; i++)
			{
				DbService.begin();
				Work server = (Work) _storage.loadWithLock(ids[i]);
				if (i == 0)
				{
					ItemPermission.checkAppAdmin(server, mp);
				}
				else if (!UserPermission.isAdmin() && !ItemPermission.hasAppPermission(server, mp))
				{
					throw new PermissionDeniedException();
				}
				WorkPermission.alreadyDeleted(server);
				WorkPermission.delete(server);
				WorkManager.delete(server);
				WorkHistory.deleted(mp, server);
				_listener.deleted(server);
				DbService.commit();
			}

		}
	}

	/**
	 * 삭제된 업무방을 복원한다.
	 * @author 신운재
	 */
	public static class RecoverItem extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long[] ids = ctx.getLongValues("id");

			for (int i = 0; i < ids.length; i++)
			{
				DbService.begin();
				Work server = (Work) _storage.loadWithLock(ids[i]);
				if (i == 0)
				{
					ItemPermission.checkOwner(server);
				}

				WorkManager.recover(server);
				if (server.getStatus() == Work.COMPLETE_STATUS || server.getStatus() == Work.APRV_CMPLT_STATUS)
				{
					_listener.complete(server);
				}
				else if (server.getStatus() == Work.WORKING_STATUS)
				{
					_listener.working(server);
				}
				WorkHistory.recovered(server);
				DbService.commit();
			}
		}
	}

	/**
	 * 나의 업무방 첨부파일 할당량 정보를 반환
	 */
	public static class WorkQuota implements Action
	{
		public void execute(ActionContext ctx) throws Exception
		{
			SqlSelect stmt = getQuotaSize();
			ResultSet rs = stmt.query();

			ctx.getWriter().print(rs.next() ? rs.getLong("TOTAL_QUOTA") : 0);
		}

		private SqlSelect getQuotaSize()
		{
			SqlSelect stmt = new SqlSelect();
			stmt.select("SUM(TOTAL_FILESIZE) TOTAL_QUOTA");
			stmt.from("WORK_ITEM");
			stmt.where("ACTOR_USERID = ?", UserService.getUserId());
			stmt.where("ISVISB = ?", true);
			stmt.group("ACTOR_USERID");
			return stmt;
		}
	}

	/**
	 * 보안설정 통계 데이터를 내려줌.
	 * @author 신운재
	 */
	public static class GetSecurities implements Action
	{
		@Override
		public void execute(ActionContext ctx) throws Exception
		{
			SqlSelect sel = new SqlSelect();
			sel.select("SCRT_LEVEL, COUNT(*) SCRT_COUNT");
			sel.from("WORK_ITEM");
			sel.where("ISVISB = ?", true);
			sel.where("STATUS != ?", Item.DELETED_STATUS);
			sel.where("ACTOR_USERID = ?", UserService.getUserId());
			sel.group("SCRT_LEVEL");

			ResultSet rs = sel.query();

			ArrayList<Integer> list = getList();

			JSONArray array = new JSONArray();
			while (rs.next())
			{
				JSONObject obj = new JSONObject();
				int scrtLevel = rs.getInt("SCRT_LEVEL");
				if (scrtLevel > Work.SECURE_ACTOR)
				{
					scrtLevel -= Work.SECURE_LIST;
				}
				list.remove(new Integer(scrtLevel));
				obj.put("SCRT_LEVEL", scrtLevel);
				obj.put("SCRT_COUNT", rs.getLong("SCRT_COUNT"));
				array.add(obj);
			}

			for (Integer value : list)
			{
				JSONObject obj = new JSONObject();
				obj.put("SCRT_LEVEL", value);
				obj.put("SCRT_COUNT", 0);
				array.add(obj);
			}
			ctx.getWriter().print(array.toString());
		}

		private ArrayList<Integer> getList()
		{
			ArrayList<Integer> list = new ArrayList<Integer>();
			list.add(Work.SECURE_ACTOR);
			list.add(Work.SECURE_HELPER);
			list.add(Work.SECURE_RANGE);
			list.add(Work.SECURE_NONE);
			return list;
		}
	}

	/**
	 * 보안 재설정
	 * @author 신운재
	 */
	public static class ResetSecureLevel extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			Long[] ids = ctx.getLongValues("id");
			int scrtLevel = ctx.getInt("scrtLevel");
			for (Long id : ids)
			{
				Work server = (Work) _storage.loadOrCreateWithLock(id);
				server.setScrtLevel(scrtLevel);
				if (scrtLevel == Work.SECURE_ACTOR)
				{
					WorkManager.helperReset(server);
				}
				server.setLastUpdt(new Date());
				WorkManager.addSecurity(server);
				WorkItemHistoryManager.history(WorkItemHistory.CHANGE_SECURE, server);
				DbService.commit();
			}
		}
	}

	/**
	 * 상태 별 통계 데이터를 내려줌.
	 * @author 이미소
	 */
	public static class GetStatus implements Action
	{
		@Override
		public void execute(ActionContext ctx) throws Exception
		{
			SqlSelect sel = new SqlSelect();
			sel.select("STATUS, COUNT(*) STATUS_COUNT");
			sel.from("WORK_ITEM");
			sel.where("ISVISB = ?", true);
			sel.where("STATUS != ?", Item.DELETED_STATUS);
			sel.where("ACTOR_USERID = ?", UserService.getUserId());
			sel.group("STATUS");

			ResultSet rs = sel.query();

			ArrayList<Integer> list = getList();

			JSONArray array = new JSONArray();
			while (rs.next())
			{
				JSONObject obj = new JSONObject();
				int status = rs.getInt("STATUS");
				list.remove(new Integer(status));
				obj.put("STATUS", status);
				obj.put("STATUS_COUNT", rs.getLong("STATUS_COUNT"));
				array.add(obj);
			}

			for (Integer value : list)
			{
				JSONObject obj = new JSONObject();
				obj.put("STATUS", value);
				obj.put("STATUS_COUNT", 0);
				array.add(obj);
			}
			ctx.getWriter().print(array.toString());
		}

		private ArrayList<Integer> getList()
		{
			ArrayList<Integer> list = new ArrayList<Integer>();
			list.add(Work.WORKING_STATUS);
			list.add(Work.REVIEW_STATUS);
			list.add(Work.REVIEW_CMPLT_STATUS);
			list.add(Work.APPROVAL_STATUS);
			list.add(Work.NEED_SUPPLEMENT_STATUS);
			list.add(Work.COMPLETE_STATUS);
			list.add(Work.APRV_CMPLT_STATUS);
			return list;
		}
	}
}
