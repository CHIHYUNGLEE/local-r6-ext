package com.kcube.work.request.share;

import java.io.PrintWriter;

import com.kcube.lib.sql.SqlSelect;
import com.kcube.lib.sql.SqlTable;
import com.kcube.lib.sql.SqlWriter;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.UserService;

/**
 * @author 김경수
 *         <p>
 *         공유 요청 Sql
 */
public class WorkShareRequestSql
{

	static final SqlTable SHARE_REQUEST = new SqlTable("work_share_request", "r");
	static SqlWriter _writer = new SqlWriter().putAll(SHARE_REQUEST);

	private static final String WORK_SELECT = "wi.title";

	private String _ts;
	private int _status;
	private Long _moduleId;
	private Long _appId;
	private boolean _moduleMenu;

	public WorkShareRequestSql(ModuleParam mp)
	{
		this(mp, null, 0);
	}

	public WorkShareRequestSql(ModuleParam mp, String ts, int status)
	{
		_moduleId = mp.getModuleId();
		_appId = mp.getAppId();
		_moduleMenu = mp.isModuleMenu();
		_ts = ts;
		_status = status;
	}

	/**
	 * 요청한 목록
	 * @return
	 */
	public SqlSelect getApplyList()
	{
		SqlSelect stmt = new SqlSelect();
		stmt.select(SHARE_REQUEST, "list");
		stmt.select(WORK_SELECT);
		stmt.from("work_item wi");
		stmt.where("wi.moduleid = ?", _moduleId);
		if (!_moduleMenu)
			stmt.where("wi.appid = ?", _appId);
		stmt.where("r.req_userid = ?", UserService.getUserId());
		stmt.where("r.workid = wi.itemid");

		SqlSelect sqlSelect = new SqlSelect();
		sqlSelect.select("*");
		sqlSelect.from(stmt, "r");
		if (_status != 0)
		{
			sqlSelect.where("r.status = ?", _status);
		}
		return sqlSelect;
	}

	/**
	 * 요청받은 목록
	 * @return
	 */
	public SqlSelect getReceiveList()
	{
		SqlSelect stmt = new SqlSelect();
		stmt.select(SHARE_REQUEST, "list");
		stmt.select(WORK_SELECT);
		stmt.from("work_item wi");
		stmt.where("wi.moduleid = ?", _moduleId);
		if (!_moduleMenu)
			stmt.where("wi.appid = ?", _appId);
		stmt.where("r.workid = wi.itemid");
		stmt.where("r.workid in ", getScrtQuery());

		SqlSelect sqlSelect = new SqlSelect();
		sqlSelect.select("*");
		sqlSelect.from(stmt, "r");
		if (_status != 0)
		{
			sqlSelect.where("r.status = ?", _status);
		}
		return sqlSelect;
	}

	/**
	 * 권한 Subquery
	 */
	public static SqlSelect getScrtQuery()
	{
		SqlSelect select = new SqlSelect();
		select.select("itemid");
		select.from("work_item");
		select.where("actor_userid = ?", UserService.getUserId());

		return select;
	}

	/**
	 * SELECT문을 실행한 결과와 총 건수를 JSON으로 출력한다.
	 * <p>
	 * @param writer
	 * @param select
	 * @throws Exception
	 */
	public void writeJson(PrintWriter writer, SqlSelect select) throws Exception
	{
		_writer.setOrder("r.req_date desc");
		_writer.page(writer, select, select, _ts);
	}

}
