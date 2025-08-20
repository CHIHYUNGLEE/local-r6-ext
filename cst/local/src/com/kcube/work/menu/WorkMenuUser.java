package com.kcube.work.menu;

import com.kcube.lib.action.Action;
import com.kcube.lib.action.ActionContext;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.UserPermission;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

/**
 * @author 성재호
 *         <p>
 *         환경 설정 - 메뉴설정 관련 Action
 */
public class WorkMenuUser
{
	/**
	 * 업무방 환경설정 메뉴 순서를 돌려준다.
	 */
	public static class MenuMapListByUser implements Action
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			WorkMenuSql sql = new WorkMenuSql(mp);
			SqlSelect select = sql.getMenuMapList();
			sql.writeMenuMapJson(ctx.getWriter(), select);
		}
	}

	/**
	 * 업무방 환경설정 기본메뉴로 설정 및 해제를 한다.
	 */
	public static class MenuMapIsMoreByUser implements Action
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			WorkMenuSql sql = new WorkMenuSql(mp);
			int menuId = ctx.getInt("scMenuId");
			int isMore = ctx.getInt("isMore");
			int length = ctx.getInt("length");

			if (length == 0)
			{
				JSONObject array = (JSONObject) JSONSerializer.toJSON(ctx.getParameter("moduleList"));
				for (int i = 0; i < array.getJSONArray("moduleList").size(); i++)
				{
					JSONObject obj = (JSONObject) array.getJSONArray("moduleList").get(i);
					sql.insertMenu(obj.getInt("menuId"), obj.getInt("isMore"), i);
				}
			}
			sql.updateDefaultMenu(menuId, isMore);
		}
	}

	/**
	 * 업무방 환경설정 메뉴 순서를 변경 한다.
	 */
	public static class MenuMapStat implements Action
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			JSONObject array = (JSONObject) JSONSerializer.toJSON(ctx.getParameter("moduleList"));

			WorkMenuSql sql = new WorkMenuSql(mp);
			sql.deleteMenu();
			for (int i = 0; i < array.getJSONArray("moduleList").size(); i++)
			{
				JSONObject obj = (JSONObject) array.getJSONArray("moduleList").get(i);
				sql.insertMenu(obj.getInt("menuId"), obj.getInt("isMore"), i);
			}
		}
	}
}