package com.kcube.sys.emp;

import java.sql.ResultSet;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kcube.lib.jdbc.DbService;
import com.kcube.lib.jdbc.DbStorage;
import com.kcube.lib.sql.SqlDelete;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.lib.sql.SqlTable;
import com.kcube.space.userMenu.SpaceUserMenu;
import com.kcube.sys.daemon.Daemon;
import com.kcube.sys.person.Person;
import com.kcube.sys.tenant.Tenant;
import com.kcube.sys.usr.User;
import com.kcube.sys.usr.UserService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class CstmEmployeeJob extends Daemon
{
	public static Log _log = LogFactory.getLog(CstmEmployeeJob.class);

	static final SqlTable MENU = new SqlTable("sp_menu", "m");
	static final SqlTable USERMENU = new SqlTable("sp_usermenu", "u");

	static DbStorage _storage = new DbStorage(SpaceUserMenu.class);

	/**
	 * 기본 생성자
	 * @param userId
	 * @param name
	 * @param disp
	 */
	public CstmEmployeeJob()
	{
		super(UserService.getUser());
	}

	/**
	 * 고정 메뉴 정보를 바탕으로 사용자의 개인화 메뉴를 생성해준다.
	 * <p>
	 * Daemon을 상속 받아 사용하므로 Commit과 오류 시 Rollback은 Daemon Class가 수행한다.
	 */
	@Override
	public void execute() throws Exception
	{
		try
		{
			JSONArray array = new JSONArray();

			// 고정메뉴정보 JSONArray 생성
			SqlSelect menu = new SqlSelect();
			// menu.select("menuid, name, menu_type, sysid, appid");
			// menu.from(MENU);
			// menu.where("status = ?", SpaceMenu.REGISTERED_STATUS);
			// menu.where("isvisb = ?", true);
			// menu.where("menu_type = 1000");
			// menu.order("sort asc");
			// ResultSet rsMenu = menu.query();
			// while (rsMenu.next())
			// {
			// JSONObject obj = new JSONObject();
			// obj.put("menuid", rsMenu.getLong("menuid"));
			// obj.put("name", rsMenu.getString("name"));
			// obj.put("menu_type", rsMenu.getLong("menu_type"));
			// obj.put("sysid", rsMenu.getLong("sysid"));
			// obj.put("appid", rsMenu.getLong("appid"));
			// array.add(obj);
			// }

			menu.select("menuid, name, sysid, appid");
			menu.from(USERMENU);
			menu.where("tenantid = ?", Tenant.TYPE_APP);
			menu.where("userid = 6");// admin계정 userid
			menu.order("menuid asc");
			ResultSet rsMenu = menu.query();
			while (rsMenu.next())
			{
				JSONObject obj = new JSONObject();
				obj.put("menuid", rsMenu.getLong("menuid"));
				obj.put("name", rsMenu.getString("name"));
				obj.put("sysid", rsMenu.getLong("sysid"));
				obj.put("appid", rsMenu.getLong("appid"));
				array.add(obj);
			}
			if (_log.isDebugEnabled())
			{
				_log.debug("[JSONArray] : " + array.toString());
			}

			SqlDelete del = new SqlDelete(USERMENU);
			del.execute();

			SqlSelect stmt = new SqlSelect();
			stmt.select("userid, name, user_disp");
			stmt.from("hr_user");
			stmt.where("isvisb = ?", true);
			stmt.where("status = ?", Person.REGISTERED_STATUS);
			stmt.where("tenantid = ?", Tenant.TYPE_APP);

			int userCount = stmt.count();
			if (_log.isInfoEnabled())
			{
				_log.info("[CstmEmployeeJob execute UserCount] : " + userCount);
			}

			int cnt = 0;
			if (userCount > 0)
			{
				ResultSet rs = stmt.query();
				while (rs.next())
				{
					Long userId = rs.getLong("userid");
					String userName = rs.getString("name");
					String userDisp = rs.getString("user_disp");

					User user = new User();
					user.setUserId(userId);
					user.setName(userName);
					user.setDisplayName(userDisp);

					for (int i = 0; i < array.size(); i++)
					{
						JSONObject menus = array.getJSONObject(i);

						SpaceUserMenu server = (SpaceUserMenu) _storage.create();
						server.setCallSpaceId(1L);
						server.setTenantId(1000L);
						server.setName(menus.getString("name"));
						server.setSpaceId(1L);
						server.setMenuId(menus.getLong("menuid") > 0 ? menus.getLong("menuid") : null);
						server.setAppId(menus.getLong("appid") > 0 ? menus.getLong("appid") : null);
						server.setSysId(menus.getLong("sysid") > 0 ? menus.getLong("sysid") : null);
						server.setServiceType(SpaceUserMenu.SERVICE_TYPE_APP);
						server.setUser(user);
						server.setInstDate(new Date());
						server.setLastUpdt(new Date());
						server.setSort(server.getId().intValue());

						DbService.flush();
						DbService.reload(server, server.getId());
						cnt++;
					}
				}
			}
			if (_log.isInfoEnabled())
			{
				_log.info(" [CstmEmployeeJob execute UserMenu Create Total Cnt => : " + cnt);
				_log.info(" [CstmEmployeeJob execute UserMenu Create End Success ] ");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			DbService.rollback();
		}
	}
}
