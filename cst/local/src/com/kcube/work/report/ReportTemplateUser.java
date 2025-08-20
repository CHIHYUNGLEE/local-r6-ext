package com.kcube.work.report;

import java.sql.ResultSet;

import com.kcube.lib.action.ActionContext;
import com.kcube.lib.json.JsonWriter;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.UserPermission;

/**
 * 업무보고서 템플릿
 */
public class ReportTemplateUser
{
	/**
	 * 사용자 권한으로 다운로드 한다.
	 */
	public static class DownloadByUser extends ReportTemplateAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			ReportTemplate item = (ReportTemplate) _storage.load(ctx.getLong("id"));
			item.setDnldCnt(item.getDnldCnt() + 1);
			ctx.store(item.getAttachment());
		}
	}

	/**
	 * 업무보고서 템플릿 메뉴용 목록을 출력한다.
	 */
	public static class ListByMenu extends ReportTemplateAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			ReportTemplateSql sql = new ReportTemplateSql(mp, ctx.getParameter("ts"));
			SqlSelect select = sql.getRegisteredSelect(ReportTemplate.REGISTERED_STATUS);

			JsonWriter writer = new JsonWriter(ctx.getWriter());
			ResultSet rs = select.query();

			writer.writeListHeader();
			while (rs.next())
			{
				writer.startList();
				writer.setAttribute("title", rs.getString("title"));
				writer.setAttribute("id", rs.getLong("itemid"));
				writer.setAttribute("filename", rs.getString("file_name"));
				writer.setAttribute("size", rs.getLong("file_size"));
				writer.setAttribute("path", rs.getString("save_path"));
				writer.setAttribute("type", rs.getString("save_code"));
				writer.endList();
			}
			writer.writeListFooter();
		}
	}
}
