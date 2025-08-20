package com.kcube.work;

import java.util.Date;

import com.kcube.doc.ItemPermission;
import com.kcube.doc.expr.ExpireService;
import com.kcube.lib.action.ActionContext;
import com.kcube.lib.jdbc.DbService;
import com.kcube.lib.json.JsonWriter;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.PermissionDeniedException;
import com.kcube.sys.usr.UserPermission;

/**
 * 업무방 관리자 Action Class
 * @author shin8901
 */
public class WorkAdmin
{
	/**
	 * 관리자 권한으로 글을 조회한다.
	 */
	public static class ReadByAdmin extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();

			Work item = (Work) _storage.load(ctx.getLong("id"));
			ItemPermission.checkAppAdmin(item, mp);

			_factory.marshal(ctx.getWriter(), item);
		}
	}

	/**
	 * 업무방 관리용 글 목록을 출력한다.
	 */
	public static class ListByAdmin extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.checkAppAdmin(mp);

			// Long tr = new Long(ctx.getParameter("tr"));
			Long tr = ctx.getLong("tr", null);
			WorkSql sql = new WorkSql(mp, tr, ctx.getParameter("ts"), ctx.getBoolean("isCountDisplay"));

			SqlSelect select = sql.getVisibleSelect();
			SqlSelect count = sql.getVisibleCount();
			sql.writeAdmListJson(ctx.getWriter(), select, count);
		}
	}

	/**
	 * 등록 상태의 업무방을 관리자 권한으로 수정한다.
	 */
	public static class DoUpdateByAdmin extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();

			Work client = unmarshal(ctx);
			Work server = (Work) _storage.loadOrCreateWithLock(client.getId());

			ItemPermission.checkAppAdmin(server, mp);
			WorkPermission.checkUpdateByAdmin(server);

			WorkManager.update(server, client);
			// server.setExprDate(ExpireService.getExprDate(server.getRgstDate(),
			// server.getExprMonth()));

			_listener.changeMember(server);
			ctx.setParameter("id", server.getId().toString());
		}
	}

	/**
	 * 관리자 권한으로 여러 문서를 동시에 삭제한다.
	 */
	public static class DoDeleteByAdmin extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();

			Long[] ids = ctx.getLongValues("id");
			int size = ids.length;

			for (int i = 0; i < size; i++)
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
				WorkManager.delete(server);
				WorkHistory.deleted(mp, server);
				_listener.deleted(server);
				DbService.commit();
			}
		}
	}

	/**
	 * 삭제된 업무방 관리용 글을 목록을 출력한다.
	 */
	public static class DeletedListByAdmin extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.checkAppAdmin(mp);

			// Long tr = new Long(ctx.getParameter("tr"));
			Long tr = ctx.getLong("tr", null);
			WorkSql sql = new WorkSql(mp, tr, ctx.getParameter("ts"), ctx.getBoolean("isCountDisplay"));

			SqlSelect select = sql.getStatusSelect(Work.DELETED_STATUS);
			SqlSelect count = sql.getStatusCount(Work.DELETED_STATUS);
			sql.writeAdmListJson(ctx.getWriter(), select, count);
		}
	}

	/**
	 * 관리자 권한으로 삭제된 여러 업무방 글을 동시에 복원한다.
	 */
	public static class DoRecoverByAdmin extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();

			Long[] ids = ctx.getLongValues("id");
			// ids를 gid로 취급한 id를 가져온다,
			int size = ids.length;
			for (int i = 0; i < size; i++)
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

				if (server.getStatus() == Work.DELETED_STATUS)
				{
					WorkManager.recover(server);
				}
				WorkHistory.recovered(server);
				_listener.recover(server);
				DbService.commit();
			}
		}
	}

	/**
	 * 관리자 권한으로 여러 업무방 글을 동시에 폐기한다.
	 */
	public static class DoRemoveByAdmin extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();

			Long[] ids = ctx.getLongValues("id");
			int size = ids.length;
			for (int i = 0; i < size; i++)
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
				ItemPermission.remove(server);
				WorkManager.remove(server);
				WorkHistory.removed(server);
				DbService.commit();
			}
		}
	}

	/**
	 * 보존년한 만료된업무방 글을 목록을 출력한다.
	 */
	public static class ExpiredListByAdmin extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.checkAppAdmin(mp);

			// Long tr = new Long(ctx.getParameter("tr"));
			Long tr = ctx.getLong("tr", null);
			WorkSql sql = new WorkSql(mp, tr, ctx.getParameter("ts"), ctx.getBoolean("isCountDisplay"));

			SqlSelect select = sql.getStatusSelect(Work.EXPIRED_STATUS);
			SqlSelect count = sql.getStatusCount(Work.EXPIRED_STATUS);
			sql.writeAdmListJson(ctx.getWriter(), select, count);
		}
	}

	/**
	 * 관리자 권한으로 보존년한을 연장한다.
	 */
	public static class DoExtendByAdmin extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();

			Long[] ids = ctx.getLongValues("id");
			int size = ids.length;
			int months = ctx.getInt("months");

			for (int i = 0; i < size; i++)
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

				server.setExprDate(ExpireService.extend(server.getExprDate(), months));
				WorkManager.extend(server);
				WorkHistory.extended(server);
			}
		}
	}

	/**
	 * 업무방 글 보존년한 관리대상 목록을 출력한다.
	 */
	public static class ExpireListByAdmin extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.checkAppAdmin(mp);

			// Long tr = new Long(ctx.getParameter("tr"));
			Long tr = ctx.getLong("tr", null);
			Date start = (ctx.getParameter("start") == null) ? null : ctx.getDate("start");
			Date end = (ctx.getParameter("end") == null) ? null : ctx.getDate("end");

			WorkSql sql = new WorkSql(mp, tr, ctx.getParameter("ts"), ctx.getBoolean("isCountDisplay"));
			SqlSelect select = sql.getExpireList(start, end);
			SqlSelect count = sql.getExpireCount(start, end);

			// order 현재 rgst_date desc
			sql.writeAdmListJson(ctx.getWriter(), select, count);
		}
	}

	/**
	 * 업무방에 등록된 글의 수를 돌려준다.
	 */
	public static class WorkCount extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.checkAppAdmin(mp);

			int menu = ctx.getInt("menu");
			Long tr = new Long(ctx.getParameter("tr"));
			WorkSql sql = new WorkSql(mp, tr, ctx.getParameter("ts"), ctx.getBoolean("isCountDisplay"));

			SqlSelect stmt = new SqlSelect();
			switch (menu)
			{
				case 1 :
					stmt = sql.getVisibleCount();
					break;
				case 2 :
					stmt = sql.getStatusCount(Work.DELETED_STATUS);
					break;
				case 3 :
					stmt = sql.getStatusCount(Work.EXPIRED_STATUS);
					break;
				case 4 :
					Date start = (ctx.getParameter("start") == null) ? null : ctx.getDate("start");
					Date end = (ctx.getParameter("end") == null) ? null : ctx.getDate("end");
					stmt = sql.getExpireCount(start, end);
					break;
			}
			writeJson(menu, stmt, ctx);
		}

		/**
		 * 메뉴에 대한 업무방 Count를 JSON형식으로 출력한다.
		 * @param menu
		 * @param stmt
		 * @param ctx
		 * @throws Exception
		 */
		private static void writeJson(int menu, SqlSelect stmt, ActionContext ctx) throws Exception
		{
			int totalCount = stmt.count();
			JsonWriter writer = new JsonWriter(ctx.getWriter());
			writer.writeHeader();
			writer.setAttribute("count", totalCount);
			writer.writeFooter();
		}
	}
}
