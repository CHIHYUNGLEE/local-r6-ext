package com.kcube.work.history;

import java.io.PrintWriter;

import com.kcube.lib.sql.SqlSelect;
import com.kcube.lib.sql.SqlTable;
import com.kcube.lib.sql.SqlWriter;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.UserService;

public class WorkItemHistorySql
{
	static final SqlTable HISTORY = new SqlTable("work_history", "h");
	static SqlWriter _writer = new SqlWriter().putAll(HISTORY);

	/**
	 * 업무방 문서 히스토리 목록
	 */
	public static SqlSelect getDocList(Long itemId, Long minId) throws Exception
	{
		SqlSelect stmt = new SqlSelect();
		stmt.select(HISTORY, "list");
		stmt.where("itemid = ?", itemId);
		if (minId != null)
			stmt.where("historyid < ?", minId);
		stmt.order("historyid desc");

		return stmt;
	}

	/**
	 * 최근 조회한 업무 히스토리 목록(업무의 협업자 및 담당자일때)
	 */
	public static SqlSelect getDocRecentList(Long minId, ModuleParam mp) throws Exception
	{
		SqlSelect stmt = new SqlSelect();
		stmt.select(HISTORY, "list");
		stmt.where("h.moduleid = ?", mp.getModuleId());
		if (!mp.isModuleMenu())
			stmt.where("h.appid = ?", mp.getAppId());
		stmt.where("itemid in ", getHelperQuery());
		if (minId != null)
			stmt.where("historyid < ?", minId);
		stmt.order("historyid desc");

		return stmt;
	}

	/**
	 * 협업자 및 담당자 Subquery
	 */
	public static SqlSelect getHelperQuery()
	{
		SqlSelect union = new SqlSelect();
		union.select("itemid");
		union.from("work_item");
		union.where("actor_userid = ?", UserService.getUserId());

		SqlSelect sub = new SqlSelect();
		sub.select("itemid");
		sub.from("work_item_helper");
		sub.where("userid = ?", UserService.getUserId());
		union.union(sub);
		return union;
	}

	/**
	 * SELECT문을 실행한 결과와 총 건수를 JSON으로 출력한다.
	 */
	public static void writeJson(PrintWriter writer, SqlSelect select, String ts) throws Exception
	{
		_writer.page(writer, select, select, ts);
	}

	/**
	 * SELECT문을 실행한 결과와 총 건수를 JSON으로 출력한다.
	 */
	public static void writeJson(PrintWriter writer, SqlSelect select) throws Exception
	{
		_writer.list(writer, select, select);
	}
}
