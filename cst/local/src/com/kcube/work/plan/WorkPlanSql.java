package com.kcube.work.plan;

import java.io.PrintWriter;

import com.kcube.lib.sql.Sql;
import com.kcube.lib.sql.SqlChooser;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.lib.sql.SqlTable;
import com.kcube.lib.sql.SqlWriter;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.UserService;

/**
 * 업무방 수행계획 SQL
 */
public class WorkPlanSql
{
	private static final SqlTable PLAN = new SqlTable("work_plan", "p");
	private static final SqlTable PLAN_ACTOR = new SqlTable("work_plan_actor", "a");

	private static SqlWriter _writer = new SqlWriter().putAll(PLAN);
	private static SqlWriter _actWriter = new SqlWriter().putAll(PLAN_ACTOR);

	private static WorkPlanMultiSql _chooser = new WorkPlanMultiSql();

	private Long _workId;
	private Long _moduleId;
	private Long _appId;
	private String _ts;
	private boolean _moduleMenu;

	public WorkPlanSql()
	{
	}

	public WorkPlanSql(Long workId)
	{
		_workId = workId;
	}

	public WorkPlanSql(ModuleParam mp, String ts)
	{
		_ts = ts;
		_moduleId = mp.getModuleId();
		_appId = mp.getAppId();
		_moduleMenu = mp.isModuleMenu();
	}

	/**
	 * 리스트 SqlStatement를 돌려준다.
	 */
	public SqlSelect getSelect() throws Exception
	{
		SqlSelect stmt = new SqlSelect();
		stmt.select(PLAN, "list");
		stmt.where("p.workid =?", _workId);
		stmt.order("p.step");
		return stmt;
	}

	/**
	 * JSON으로 출력한다.
	 */
	public void writeJson(PrintWriter writer, SqlSelect select) throws Exception
	{
		_writer.list(writer, select);
	}

	/**
	 * step을 초기화 시킨다.
	 */
	public void resetStep() throws Exception
	{
		SqlChooser.getSqlObject(_chooser, "resetStep", _workId);
	}

	public SqlSelect getActorSelect(Long[] ids)
	{
		SqlSelect stmt = new SqlSelect();
		stmt.select(PLAN_ACTOR, "list");
		stmt.where(new Sql.InLongArray("a.itemid", ids));
		stmt.order("a.itemid, a.seq_order");
		return stmt;
	}

	public void writeActorJson(PrintWriter writer, SqlSelect select) throws Exception
	{
		_actWriter.list(writer, select);
	}

	/**
	 * 하위에 진행중인 수행계획이 있는지 체크한다.
	 * @param id
	 * @param pos
	 * @return
	 * @throws Exception
	 */
	public boolean checkPlanIngStatus(Long id, int pos) throws Exception
	{
		return getPlanIngList(id, pos).length > 0;
	}

	public Long[] getPlanIngList(Long id, int pos) throws Exception
	{
		return (Long[]) SqlChooser.getSqlObject(_chooser, "getPlanIngList", id, pos, _workId);
	}

	/**
	 * 담당 수행업무 SqlStatement를 돌려준다.
	 * @param status
	 * @return
	 * @throws Exception
	 */
	public SqlSelect getMyWorkList(int status) throws Exception
	{
		SqlSelect sub = new SqlSelect();
		sub.select("1");
		sub.from(PLAN_ACTOR);
		sub.where("a.userid = ?", UserService.getUserId());
		sub.where("a.itemid = p.itemid");

		SqlSelect stmt = new SqlSelect();
		stmt.select(PLAN, "list");
		stmt.select("wi.title work_title");
		stmt.leftOuter("work_item wi", "p.workid = wi.itemid");
		stmt.where("wi.moduleid = ?", _moduleId);
		stmt.where("wi.isvisb = ?", true);

		if (!_moduleMenu)
			stmt.where("wi.appid = ?", _appId);

		if (status >= 0)
		{
			if (status == WorkPlan.WORKING_STATUS)
			{
				stmt.where("p.end_date >= DATE_TRUNC('day', CURRENT_TIMESTAMP)");
			}
			else if (status == WorkPlan.DELAY_STATUS)
			{
				stmt.where("p.end_date < DATE_TRUNC('day', CURRENT_TIMESTAMP)");
				status = WorkPlan.WORKING_STATUS;
			}
			stmt.where("p.status = ?", status);
		}
		stmt.where("EXISTS", sub);

		SqlSelect wrap = new SqlSelect();
		wrap.select("*");
		wrap.from(stmt, "p");

		return wrap;
	}

	/**
	 * 프로젝트 정보와 함께 JSON으로 출력한다.
	 * @param writer
	 * @param select
	 * @throws Exception
	 */
	public void writePlanListJson(PrintWriter writer, SqlSelect select) throws Exception
	{
		_writer.setOrder("p.itemid desc");
		_writer.setTable("work_plan");
		_writer.page(writer, select, _ts);
	}
}
