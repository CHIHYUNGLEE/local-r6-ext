package com.kcube.work.report;

import com.kcube.doc.ItemPermission;
import com.kcube.lib.action.ActionContext;
import com.kcube.lib.jdbc.DbService;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.PermissionDeniedException;
import com.kcube.sys.usr.UserPermission;

/**
 * @author 성재호
 *         <p>
 *         업무보고서 템플릿 관리 Admin
 */
public class ReportTemplateAdmin
{
	/**
	 * 관리자 권한으로 글을 조회한다.
	 */
	public static class ReadByAdmin extends ReportTemplateAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();

			ReportTemplate item = (ReportTemplate) _storage.load(ctx.getLong("id"));
			ItemPermission.checkAppAdmin(item, mp);

			_factory.marshal(ctx.getWriter(), item);
		}
	}

	/**
	 * 업무보고서 템플릿 관리용 목록을 출력한다.
	 */
	public static class ListByAdmin extends ReportTemplateAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.checkAppAdmin(mp);

			ReportTemplateSql sql = new ReportTemplateSql(mp, ctx.getParameter("ts"));

			SqlSelect select = sql.getRegisteredSelect(ReportTemplate.REGISTERED_STATUS);
			SqlSelect count = sql.getRegisteredCount(ReportTemplate.REGISTERED_STATUS);
			sql.writeJson(ctx.getWriter(), select, count);
		}
	}

	/**
	 * 관리자 권한으로 삭제된 목록을 조회한다.
	 */
	public static class DeletedListByAdmin extends ReportTemplateAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.checkAppAdmin(mp);

			ReportTemplateSql sql = new ReportTemplateSql(mp, ctx.getParameter("ts"));
			SqlSelect select = sql.getDeletedSelect(ReportTemplate.DELETED_STATUS);
			SqlSelect count = sql.getDeletedCount(ReportTemplate.DELETED_STATUS);
			sql.writeJson(ctx.getWriter(), select, count);
		}
	}

	/**
	 * 등록 상태의 업무보고서 템플릿을 관리자 권한으로 수정한다.
	 */
	public static class DoUpdateByAdmin extends ReportTemplateAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();

			ReportTemplate client = unmarshall(ctx);
			ReportTemplate server = (ReportTemplate) _storage.loadWithLock(client.getId());
			ItemPermission.checkAppAdmin(server, mp);
			ReportTemplateManager.update(server, client);
			ctx.setParameter("id", server.getId().toString());
		}
	}

	/**
	 * 관리자 권한으로 여러 문서를 동시에 삭제한다.
	 */
	public static class DoDeleteByAdmin extends ReportTemplateAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();

			Long[] ids = ctx.getLongValues("id");
			int size = ids.length;

			for (int i = 0; i < size; i++)
			{
				DbService.begin();
				ReportTemplate server = (ReportTemplate) _storage.loadWithLock(ids[i]);
				if (i == 0)
				{
					ItemPermission.checkAppAdmin(server, mp);
				}
				else if (!UserPermission.isAdmin() && !ItemPermission.hasAppPermission(server, mp))
				{
					throw new PermissionDeniedException();
				}
				ReportTemplateManager.delete(server);
				DbService.commit();
			}

		}
	}

	/**
	 * 관리자 권한으로 여러 문서를 동시에 폐기한다.
	 */
	public static class DoRemoveByAdmin extends ReportTemplateAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();

			Long[] ids = ctx.getLongValues("id");
			int size = ids.length;
			for (int i = 0; i < size; i++)
			{
				DbService.begin();
				ReportTemplate server = (ReportTemplate) _storage.loadWithLock(ids[i]);
				if (i == 0)
				{
					ItemPermission.checkAppAdmin(server, mp);
				}
				else if (!UserPermission.isAdmin() && !ItemPermission.hasAppPermission(server, mp))
				{
					throw new PermissionDeniedException();
				}
				ItemPermission.remove(server);
				ReportTemplateManager.remove(server);
				DbService.commit();
			}
		}
	}

	/**
	 * 관리자 권한으로 삭제된 여러 문서를 동시에 복원한다.
	 */
	public static class DoRecoverByAdmin extends ReportTemplateAction
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
				ReportTemplate server = (ReportTemplate) _storage.loadWithLock(ids[i]);
				if (i == 0)
				{
					ItemPermission.checkAppAdmin(server, mp);
				}
				else if (!UserPermission.isAdmin() && !ItemPermission.hasAppPermission(server, mp))
				{
					throw new PermissionDeniedException();
				}

				while (server.getStatus() == ReportTemplate.DELETED_STATUS)
				{
					ReportTemplateManager.recover(server);
				}
				DbService.commit();
			}
		}
	}

	/**
	 * 관리자 권한으로 다운로드 한다.
	 */
	public static class DownloadByAdmin extends ReportTemplateAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.checkAppAdmin(mp);

			ReportTemplate item = (ReportTemplate) _storage.load(ctx.getLong("id"));
			item.setDnldCnt(item.getDnldCnt() + 1);
			ctx.store(item.getAttachment());
		}
	}

	/**
	 * 게시글을 등록한다.
	 */
	public static class DoRegister extends ReportTemplateAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.checkAppAdmin(mp);

			ReportTemplate client = unmarshall(ctx);
			ReportTemplate server = (ReportTemplate) _storage.loadOrCreateWithLock(client.getId());

			ReportTemplateManager.update(server, client);
			ReportTemplateManager.register(server);
			ctx.setParameter("id", server.getId().toString());
		}
	}
}