package com.kcube.work.report;

import java.io.PrintWriter;

import com.kcube.lib.sql.SqlSelect;
import com.kcube.lib.sql.SqlTable;
import com.kcube.lib.sql.SqlWriter;
import com.kcube.sys.module.ModuleParam;

public class ReportTemplateSql
{
	private static final SqlTable ReportTemplate = new SqlTable("work_report_template", "i");
	private static SqlWriter _writer = new SqlWriter().putAll(ReportTemplate);

	private Long _moduleId;
	private Long _appId;
	private String _ts;
	private boolean _moduleMenu;

	public ReportTemplateSql(ModuleParam mp, String ts) throws Exception
	{

		_ts = ts;
		_moduleId = mp.getModuleId();
		_appId = mp.getAppId();
		_moduleMenu = mp.isModuleMenu();

	}

	/**
	 * 등록상태인 리스트 SqlStatement를 돌려준다.
	 */
	public SqlSelect getRegisteredSelect(int status) throws Exception
	{
		SqlSelect stmt = new SqlSelect();
		stmt.select(ReportTemplate, "list");
		stmt.where("i.status = ?", status);
		stmt.where("i.moduleid = ?", _moduleId);
		if (!_moduleMenu)
			stmt.where("i.appid = ?", _appId);
		return stmt;
	}

	/**
	 * 등록상태인의 카운트 SqlStatement를 돌려준다.
	 */
	public SqlSelect getRegisteredCount(int status) throws Exception
	{
		SqlSelect stmt = new SqlSelect();
		stmt.from(ReportTemplate);
		stmt.where("i.status = ?", status);
		stmt.where("i.moduleid = ?", _moduleId);
		if (!_moduleMenu)
			stmt.where("i.appid = ?", _appId);
		return stmt;
	}

	/**
	 * 삭제상태의 리스트 SqlStatement를 돌려준다.
	 */
	public SqlSelect getDeletedSelect(int status) throws Exception
	{
		SqlSelect stmt = new SqlSelect();
		stmt.select(ReportTemplate, "list");
		stmt.where("i.status = ?", status);
		stmt.where("i.moduleid = ?", _moduleId);
		if (!_moduleMenu)
			stmt.where("i.appid = ?", _appId);

		return stmt;
	}

	/**
	 * 삭제상태의 카운트 SqlStatement를 돌려준다.
	 */
	public SqlSelect getDeletedCount(int status) throws Exception
	{
		SqlSelect stmt = new SqlSelect();
		stmt.from(ReportTemplate);
		stmt.where("i.status = ?", status);
		stmt.where("i.moduleid = ?", _moduleId);
		if (!_moduleMenu)
			stmt.where("i.appid = ?", _appId);

		return stmt;
	}

	/**
	 * SELECT문을 실행한 결과와 총 건수를 JSON으로 출력한다.
	 */
	public void writeJson(PrintWriter writer, SqlSelect select, SqlSelect count) throws Exception
	{
		_writer.setOrder("i.itemid desc");
		_writer.setTable("work_report_template");
		_writer.page(writer, select, count, _ts);
	}
}
