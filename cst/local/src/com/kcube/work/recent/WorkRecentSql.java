package com.kcube.work.recent;

import java.io.PrintWriter;

import com.kcube.lib.sql.SqlSelect;
import com.kcube.lib.sql.SqlTable;
import com.kcube.lib.sql.SqlWriter;
import com.kcube.sys.usr.UserService;
import com.kcube.work.Work;

/**
 * 업무방 최근 이력에 대한 Sql Class
 * @author Soo
 */
public class WorkRecentSql
{
	private static final SqlTable WORKITEM = new SqlTable("work_item", "wi");
	private static SqlWriter _writer = new SqlWriter().putAll(WORKITEM);

	private String _ts;

	/**
	 * WorkSql 생성자
	 * @param mp
	 * @param ts
	 */
	public WorkRecentSql(String ts)
	{
		_ts = ts;
	}

	/**
	 * 리스트 SqlStatement를 돌려준다.
	 */
	public SqlSelect getAllSelect() throws Exception
	{
		SqlSelect stmt = new SqlSelect();
		stmt.select(WORKITEM, "list");
		return stmt;
	}

	/**
	 * 최근 업무방을 돌려준다.
	 * <p>
	 * (work_item_recent 로그를 기준으로 돌려준다.)
	 * @return
	 * @throws Exception
	 */
	public SqlSelect getRecentSql() throws Exception
	{
		SqlSelect sub = new SqlSelect();
		sub.select("r.itemid, max(r.inst_date) recent_date");
		sub.from("work_item_recent r");
		sub.where("r.userid = ?", UserService.getUserId());
		sub.group("r.itemid");

		SqlSelect stmt = getAllSelect();
		stmt.select("r.recent_date");
		stmt.from(sub, "r");
		stmt.where("wi.itemid = r.itemid");
		stmt.where("wi.status < ?", Work.DELETED_STATUS);
		stmt.where("wi.isvisb = 1");
		return stmt;
	}

	/**
	 * SELECT문을 실행한 결과와 총 건수를 JSON으로 출력한다.
	 */
	public void writeJson(PrintWriter writer, SqlSelect select, String order) throws Exception
	{
		_writer.setOrder(order);
		_writer.setTagInfo("work_item", "wi", "itemid");
		_writer.page(writer, select, new SqlSelect(select), _ts);
	}
}