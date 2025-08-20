package com.kcube.work.plan;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

import com.kcube.lib.jdbc.DbService;
import com.kcube.lib.sql.SqlChooser;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.lib.sql.SqlType;

public class WorkPlanMultiSql
{
	private static final String UPDATESTEP = "UPDATE work_plan t "
		+ " SET step = (SELECT rnum FROM "
		+ " (SELECT itemid, ROWNUM rnum FROM work_plan "
		+ " WHERE workid = ? "
		+ "	START WITH pid IS NULL "
		+ " CONNECT BY PRIOR itemid = pid "
		+ " ORDER SIBLINGS BY step) p "
		+ " WHERE p.itemid = t.itemid) "
		+ " WHERE workid = ? ";

	private static final String UPDATESTEP_POSTGRESQL = "UPDATE work_plan wp SET step = "
		+ "	(SELECT rnum FROM (SELECT ROW_NUMBER() OVER() rnum, p.itemid FROM "
		+ "	(WITH RECURSIVE recs_work (itemid, pid, PATH, CYCLE, step) AS "
		+ "	(SELECT p.itemid, p.pid, ARRAY[p.step], FALSE, p.step FROM "
		+ "	(SELECT * FROM work_plan WHERE workid = ? AND pid IS NULL ORDER BY step ) p "
		+ "	UNION ALL "
		+ "	SELECT p.itemid, p.pid, PATH || p.step, p.itemid = ANY(PATH), p.step FROM work_plan p, recs_work r "
		+ "	WHERE p.pid = r.itemid AND p.workid = ? AND NOT CYCLE) "
		+ "	SELECT r.* FROM recs_work r ORDER BY PATH) p ) p "
		+ "	WHERE itemid = wp.itemid) "
		+ " WHERE workid = ? ";

	private static final String UPDATESTEP_MYSQL = "UPDATE work_plan p SET step = ( "
		+ " SELECT lev FROM ( "
		+ " SELECT connect_by_prior_work() AS itemid, @level AS lev  "
		+ " FROM (SELECT @start_with := 0, @id := @start_with, @level := 0, @workid := ?) h, work_plan "
		+ " WHERE @id IS NOT NULL AND workid = ?) a "
		+ " WHERE itemid = p.itemid) "
		+ " WHERE workid = ? ";

	/**
	 * Oracle, step을 초기화 시킨다.
	 * @param workId
	 * @throws Exception
	 */
	public void resetStep(Long workId) throws Exception
	{
		PreparedStatement pstmt = DbService.prepareStatement(UPDATESTEP);
		pstmt.setLong(1, workId);
		pstmt.setLong(2, workId);
		pstmt.executeUpdate();
	}

	/**
	 * PostgreSql, step을 초기화 시킨다.
	 * @param workId
	 * @throws Exception
	 */
	@SqlType(dbmsType = SqlChooser.POSTGRESQL, methodName = "resetStep")
	public void resetStep_PostgreSQL(Long workId) throws Exception
	{
		PreparedStatement pstmt = DbService.prepareStatement(UPDATESTEP_POSTGRESQL);
		pstmt.setLong(1, workId);
		pstmt.setLong(2, workId);
		pstmt.setLong(3, workId);
		pstmt.executeUpdate();
	}

	/**
	 * MySql, step을 정리한다.
	 * @param workId
	 * @throws Exception
	 */
	@SqlType(dbmsType = SqlChooser.MYSQL, methodName = "resetStep")
	public void resetStep_MySQL(Long workId) throws Exception
	{
		PreparedStatement pstmt = DbService.prepareStatement(UPDATESTEP_MYSQL);
		pstmt.setLong(1, workId);
		pstmt.setLong(2, workId);
		pstmt.setLong(3, workId);
		pstmt.executeUpdate();
	}

	/**
	 * Oracle, 하위에 진행중인 수행계획 목록정보를 돌려준다.
	 * @param gid
	 * @param pos
	 * @return
	 * @throws Exception select itemid from work_plan where workid=? start with pid = ? connect by prior itemid=pid ;
	 */
	public Long[] getPlanIngList(Long id, Integer pos, Long workId) throws Exception
	{
		SqlSelect sel = new SqlSelect();
		sel.select("itemid");
		sel.from("work_plan");
		sel.where("workid = ? ", workId);
		sel.where("status <> ?", WorkPlan.COMPLETE_STATUS);

		if (id != 0)
		{
			if (pos == 0)
			{
				sel.where("gid = ? ", id);
				sel.where("pid IS NOT NULL");
			}
			else
			{
				sel.startWith("pid = ?", "itemid = pid", id);
			}
		}

		List<Long> ids = new LinkedList<Long>();
		ResultSet rs = sel.query();
		while (rs.next())
		{
			ids.add(rs.getLong("itemid"));
		}

		return ids.toArray(new Long[ids.size()]);
	}

	/**
	 * PostgreSql, 하위에 진행중인 수행계획 목록정보를 돌려준다.
	 * @param gid
	 * @param pos
	 * @return
	 * @throws Exception select itemid from work_plan where workid=? start with pid = ? connect by prior itemid=pid ;
	 */
	@SqlType(dbmsType = SqlChooser.POSTGRESQL, methodName = "getPlanIngList")
	public Long[] getPlanIngList_PostgreSQL(Long id, Integer pos, Long workId) throws Exception
	{
		if (id == 0 || pos == 0)
		{
			return getPlanIngList(id, pos, workId);
		}

		SqlSelect sub = new SqlSelect();
		sub.select("itemid, pid");
		sub.from("work_plan");
		sub.where("workid=?", workId);
		sub.where("pid = ?", id);

		SqlSelect recs = new SqlSelect();
		recs.select("w.itemid, w.pid");
		recs.from("work_plan w");
		recs.from("recs_work r");
		recs.where("r.itemid = w.pid");
		recs.where("w.status <> ?", WorkPlan.COMPLETE_STATUS);
		sub.unionAll(recs);

		SqlSelect stmt = new SqlSelect();
		stmt.withRecursive("itemid", "recs_work", sub);

		List<Long> ids = new LinkedList<Long>();
		ResultSet rs = stmt.query();
		while (rs.next())
		{
			ids.add(rs.getLong("itemid"));
		}

		return ids.toArray(new Long[ids.size()]);
	}

	/**
	 * MySql, 하위에 진행중인 수행계획 목록정보를 돌려준다.
	 * @param id
	 * @param pos
	 * @param workId
	 * @return
	 * @throws Exception
	 */
	@SqlType(dbmsType = SqlChooser.MYSQL, methodName = "getPlanIngList")
	public Long[] getPlanIngList_MySQL(Long id, Integer pos, Long workId) throws Exception
	{
		if (id == 0 || pos == 0)
		{
			return getPlanIngList(id, pos, workId);
		}

		SqlSelect from = new SqlSelect();
		from.select("@start_with := ?, @id := @start_with").setLong(id);

		SqlSelect inner = new SqlSelect();
		inner.select("connect_by_prior_work_plan() AS planId");
		inner.from(from, "hier");
		inner.from("work_plan");
		inner.where("@id is not null");

		SqlSelect mapStmt = new SqlSelect();
		mapStmt.select("wp.itemid, wp.pid, wp.status");
		mapStmt.from(inner, "inn");
		mapStmt.from("work_plan wp");
		mapStmt.where("inn.planId = wp.pid");

		SqlSelect union = new SqlSelect();
		union.select("p.itemid, p.pid, p.status");
		union.from("work_plan p");
		union.where("p.workid = ?", workId);
		union.where("p.pid = ?", id);
		union.union(mapStmt);

		SqlSelect stmt = new SqlSelect();
		stmt.select("u.*");
		stmt.from(union, "u");
		stmt.where("u.status <> ?", WorkPlan.COMPLETE_STATUS);

		List<Long> ids = new LinkedList<Long>();
		ResultSet rs = stmt.query();
		while (rs.next())
		{
			ids.add(rs.getLong("itemid"));
		}

		return ids.toArray(new Long[ids.size()]);
	}
}
