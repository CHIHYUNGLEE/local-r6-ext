package com.kcube.work.menu;

import java.io.PrintWriter;

import com.kcube.lib.sql.SqlDelete;
import com.kcube.lib.sql.SqlInsert;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.lib.sql.SqlTable;
import com.kcube.lib.sql.SqlUpdate;
import com.kcube.lib.sql.SqlWriter;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.UserService;

/**
 * 업무방 메뉴 Sql 정의 Class
 * @author Soo
 */
public class WorkMenuSql
{
	private static final SqlTable WORKMENUMAP = new SqlTable("work_menu_map", "wmm");
	private static SqlWriter _menuWriter = new SqlWriter().putAll(WORKMENUMAP);

	private Long _moduleId;
	private Long _appId;
	private boolean _moduleMenu;

	/**
	 * WorkSql 생성자
	 * @param mp
	 * @param ts
	 */
	public WorkMenuSql(ModuleParam mp)
	{
		_moduleId = mp.getModuleId();
		_appId = mp.getAppId();
		_moduleMenu = mp.isModuleMenu();
	}

	/**
	 * 메뉴 목록을 돌려준다.
	 */
	public SqlSelect getMenuMapList()
	{
		SqlSelect stmt = new SqlSelect();
		stmt.select(WORKMENUMAP, "list");
		stmt.where("wmm.moduleid = ?", _moduleId);
		if (!_moduleMenu)
		{
			stmt.where("wmm.appid = ?", _appId);
		}
		else
		{
			stmt.where("wmm.appid is null");
		}
		stmt.where("wmm.userid = ?", UserService.getUserId());
		return stmt;
	}

	/**
	 * 메뉴 설정
	 * @param obj
	 * @param i
	 * @throws Exception
	 */
	public void insertMenu(int menuId, int isMore, int order) throws Exception
	{
		SqlInsert ins = new SqlInsert("work_menu_map");
		ins.setLong("userid", UserService.getUserId());
		ins.setLong("menuid", menuId);
		ins.setLong("seq_order", order);
		ins.setBoolean("ismore", isMore == 1 ? true : false);
		ins.setLong("moduleid", _moduleId);
		if (!_moduleMenu)
		{
			ins.setLong("appid", _appId);
		}
		ins.execute();
	}

	/**
	 * 해당 userId의 모든 메뉴 설정을 삭제
	 * @param userId
	 * @throws Exception
	 */
	public void deleteMenu() throws Exception
	{
		SqlDelete del = new SqlDelete("work_menu_map");
		del.where("userid = ?", UserService.getUserId());
		del.where("moduleid = ?", _moduleId);
		if (!_moduleMenu)
		{
			del.where("appid = ?", _appId);
		}
		else
		{
			del.where("appid is null");
		}
		del.execute();
	}

	/**
	 * 환경설정 기본메뉴 설정 목록을 돌려준다.
	 * @param menuId 메뉴id값
	 * @param isMore 기본메뉴설정여부값 (인자값에 반대로 처리한다.)
	 */
	public void updateDefaultMenu(int menuId, int isMore) throws Exception
	{
		SqlUpdate upt = new SqlUpdate("work_menu_map");
		upt.setBoolean("ismore", isMore == 1 ? false : true);
		upt.where("userid = ?", UserService.getUserId());
		upt.where("menuid = ?", menuId);

		upt.where("moduleid = ?", _moduleId);
		if (!_moduleMenu)
		{
			upt.where("appid = ?", _appId);
		}
		else
		{
			upt.where("appid is null");
		}
		upt.execute();
	}

	/**
	 * 메뉴 목록을 돌려주는 SELECT문을 실행한 결과를 JSON으로 출력한다.
	 */
	public void writeMenuMapJson(PrintWriter writer, SqlSelect select) throws Exception
	{
		_menuWriter.setOrder("wmm.isMore desc, wmm.seq_order, wmm.menuid");
		_menuWriter.setTable("work_menu_map");
		_menuWriter.page(writer, select, null);
	}
}