package com.kcube.work;

import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.kcube.doc.Item;
import com.kcube.lib.jdbc.DbConfiguration;
import com.kcube.lib.sql.Sql;
import com.kcube.lib.sql.Sql.InIntArray;
import com.kcube.lib.sql.Sql.InLongArray;
import com.kcube.lib.sql.SqlDate;
import com.kcube.lib.sql.SqlDelete;
import com.kcube.lib.sql.SqlDialect;
import com.kcube.lib.sql.SqlFragment;
import com.kcube.lib.sql.SqlInsert;
import com.kcube.lib.sql.SqlPage;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.lib.sql.SqlTable;
import com.kcube.lib.sql.SqlWriter;
import com.kcube.map.FolderSql;
import com.kcube.sys.emp.EmployeeService;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.UserService;

/**
 * 업무방 Sql 정의 Class
 * @author shin8901
 */
public class WorkSql
{
	private static final SqlTable WORKITEM = new SqlTable("work_item", "wi");
	private static final SqlTable WORKFVRT = new SqlTable("work_item_fvrt", "wif");
	private static final SqlTable WORKREPORTFILE = new SqlTable("work_item_report", "wr");

	private static SqlWriter _writer = new SqlWriter().putAll(WORKITEM);
	private static SqlWriter _fileReportWriter = new SqlWriter().putAll(WORKREPORTFILE);

	static HashMap<Integer, int[]> _status = new HashMap<Integer, int[]>();
	static int[] APPR = {Work.APPROVAL_STATUS, Work.NEED_SUPPLEMENT_STATUS};
	static int[] RVIW = {Work.REVIEW_STATUS, Work.REVIEW_CMPLT_STATUS};
	static int[] CMPLT = {Work.COMPLETE_STATUS, Work.APRV_CMPLT_STATUS};
	static int[] WORKING = {Work.WORKING_STATUS};
	static
	{
		_status.put(Work.APPROVAL_STATUS, APPR);
		_status.put(Work.WORKING_STATUS, WORKING);
		_status.put(Work.REVIEW_STATUS, RVIW);
		_status.put(Work.COMPLETE_STATUS, CMPLT);
	}

	private String _ts;
	private SqlPage _page;
	private Long _moduleId;
	private Long _appId;
	private boolean _moduleMenu;
	private boolean _countVisible;
	private Long _folderId;
	private Long _userId;

	private String _defaultSort = "wi.rgst_date desc";
	private boolean _checkScrt;

	/**
	 * 기본 sort 정보 설정
	 * @param defaultSort
	 */
	public void setDefaultSort(String defaultSort)
	{
		_defaultSort = defaultSort;
	}

	/**
	 * WorkSql 생성자
	 * @param mp
	 */
	public WorkSql(ModuleParam mp)
	{
		this(mp, null, null, true);
	}

	/**
	 * WorkSql 생성자
	 * @param mp
	 * @param ts
	 */
	public WorkSql(ModuleParam mp, String ts)
	{
		_ts = ts;
		_moduleId = mp.getModuleId();
		_appId = mp.getAppId();
		_moduleMenu = mp.isModuleMenu();
	}

	/**
	 * WorkSql 생성자
	 * @param mp
	 * @param folderId
	 * @param ts
	 */
	public WorkSql(ModuleParam mp, Long folderId, String ts)
	{
		this(mp, folderId, ts, true);
	}

	/**
	 * WorkSql 생성자
	 * @param mp
	 * @param folderId
	 * @param ts
	 * @param countVisble
	 */
	public WorkSql(ModuleParam mp, Long folderId, String ts, boolean countVisble)
	{
		_ts = ts;
		_folderId = folderId;
		_page = new SqlPage(WORKFVRT.aliasToColumn(), _ts);
		_moduleId = mp.getModuleId();
		_appId = mp.getAppId();
		_moduleMenu = mp.isModuleMenu();
		_countVisible = countVisble;
	}

	/**
	 * WorkSql 생성자
	 * @param mp
	 * @param folderId
	 * @param ts
	 * @param countVisble
	 * @param checkSecrt
	 */
	public WorkSql(ModuleParam mp, Long folderId, String ts, boolean countVisble, boolean checkSecrt)
	{
		_ts = ts;
		_folderId = folderId;
		_page = new SqlPage(WORKFVRT.aliasToColumn(), _ts);
		_moduleId = mp.getModuleId();
		_appId = mp.getAppId();
		_moduleMenu = mp.isModuleMenu();
		_countVisible = countVisble;
		_checkScrt = checkSecrt;
	}

	/**
	 * 리스트 SqlStatement를 돌려준다.
	 * @return
	 * @throws Exception
	 */
	public SqlSelect getAllSelect() throws Exception
	{
		SqlSelect stmt = new SqlSelect();
		stmt.select(WORKITEM, "list");
		return stmt;
	}

	/**
	 * 보안이 적용된 리스트 SqlStatement를 돌려준다.
	 * @return
	 * @throws Exception
	 */
	public SqlSelect getAllScrtSelect() throws Exception
	{
		SqlSelect stmt = getAllSelect();
		if (_checkScrt)
		{
			stmt.where(new WorkItemSecuritySql().getSecFragment("wi", "work_item_scrt", _checkScrt));
		}
		else
		{
			stmt.where(new WorkItemSecuritySql().getSecFragment("wi", "work_item_scrt"));
		}
		return stmt;
	}

	/**
	 * 특정 사용자가 등록한 업무방 목록 쿼리를 돌려준다.
	 * @param userId 사용자일련번호
	 * @param status 상태
	 * @throws Exception
	 */
	public SqlSelect getOwnerList(Long userId, int status) throws Exception
	{
		SqlSelect stmt = getAllSelect();
		bindModuleQuery(stmt, "wi");
		stmt.where("wi.auth_userid = ?", userId);
		stmt.where("wi.isvisb = 1");
		if (_status.get(status) != null)
		{
			stmt.where(new Sql.InIntArray("wi.status", _status.get(status)));
		}
		else
		{
			stmt.where("wi.status < ?", Item.DELETED_STATUS);
		}
		return stmt;
	}

	/**
	 * 전부서 업무방을 돌려준다.
	 * @param year 년도
	 * @return
	 * @throws Exception
	 */
	public SqlSelect getCompanyVisibleSelect(String year) throws Exception
	{
		SqlSelect stmt = getAllScrtSelect();
		if (_folderId != null)
		{
			stmt.from("work_item_map m");
			stmt.where("wi.itemid = m.itemid");
		}
		stmt.where("wi.isvisb = 1");
		bindModuleQuery(stmt, "wi");
		if (_folderId != null)
		{
			stmt.where(FolderSql.level("m.kmid", "m.level", _folderId, true));
		}
		if (StringUtils.isNumeric(year))
		{
			stmt.where(
				(DbConfiguration.isMsSql() ? "year(wi.rgst_date)" : SqlDialect.toChar("wi.rgst_date", "YYYY"))
					+ " = ? ",
				year);
		}
		return stmt;
	}

	/**
	 * 팀 업무방을 돌려준다.
	 * @param year 년도
	 * @param userId 사용자일련번호
	 * @param status 상태
	 * @return
	 * @throws Exception
	 */
	public SqlSelect getTeamVisibleSelect(String year, Long userId, int status) throws Exception
	{
		_userId = userId;
		SqlSelect stmt = getAllSelect();
		stmt.where("wi.isvisb = 1");

		if (StringUtils.isNumeric(year))
		{
			stmt.where(
				(DbConfiguration.isMsSql() ? "year(wi.rgst_date)" : SqlDialect.toChar("wi.rgst_date", "YYYY"))
					+ " = ? ",
				year);
		}

		if (_status.get(status) != null)
		{
			stmt.where(new Sql.InIntArray("wi.status", _status.get(status)));
		}

		bindModuleQuery(stmt, "wi");
		stmt.where(new SqlFragment()
		{
			@Override
			public void make(StringBuffer query)
			{
				query.append(" ( wi.itemid IN ( ");
				query.append(" SELECT wih.itemid FROM work_item_helper wih ");
				if (_userId != null)
				{
					query.append(" WHERE wih.userid = ? ");
				}
				else
				{
					query.append(" WHERE EXISTS ( ");
					query.append(" SELECT h.userid FROM hr_user h ");
					query.append(" WHERE h.dprtid = ? ");
					query.append(" AND h.isvisb = 1 ");
					query.append(" AND h.userid = wih.userid ");
					query.append(" ) ");
				}
				query.append(" ) ");
				query.append(" OR ");
				if (_userId != null)
				{
					query.append(" wi.actor_userid = ? ");
				}
				else
				{
					query.append(" EXISTS ( ");
					query.append(" SELECT h.userid FROM hr_user h ");
					query.append(" WHERE h.dprtid = ? ");
					query.append(" AND h.isvisb = 1 ");
					query.append(" AND h.userid = wi.actor_userid ");
					query.append(" ) ");
				}
				query.append(" ) ");
			}

			@Override
			public boolean isValid()
			{
				return true;
			}

			@Override
			public int bind(PreparedStatement pstmt, int index, int loop) throws Exception
			{
				if (_userId != null)
				{
					pstmt.setLong(index, _userId);
					pstmt.setLong(index + 1, _userId);
				}
				else
				{
					Long dprtid = EmployeeService.getEmployee(UserService.getUserId()).getDprtId();
					pstmt.setObject(index, dprtid);
					pstmt.setObject(index + 1, dprtid);
				}
				return index + 2;
			}
		});
		return stmt;
	}

	/**
	 * 업무방 포탈관리자 업무관리 목록표시상태(isvisb=1)인 리스트 SqlStatement를 돌려준다.
	 * @return
	 * @throws Exception
	 */
	public SqlSelect getVisibleSelect() throws Exception
	{
		SqlSelect stmt = getAllSelect();
		stmt.from("work_item_map wim");
		stmt.where("wi.isvisb = 1");
		stmt.where("wi.itemid = wim.itemid");
		bindModuleQuery(stmt, "wi");
		stmt.where(FolderSql.level("wim.kmid", "wim.level", _folderId, true));
		return stmt;
	}

	/**
	 * 업무방 포탈관리자 업무관리 목록표시상태(isvisb=1)의 카운트 SqlStatement를 돌려준다.
	 * @return
	 * @throws Exception
	 */
	public SqlSelect getVisibleCount() throws Exception
	{
		SqlSelect stmt = getVisibleSelect();
		setCountCondition(stmt);
		return stmt;
	}

	/**
	 * 업무방 포탈관리자 업무관리 특정상태의 리스트 SqlStatement를 돌려준다.
	 * @param status 상태
	 * @return
	 * @throws Exception
	 */
	public SqlSelect getStatusSelect(int status) throws Exception
	{
		SqlSelect stmt = getAllSelect();
		stmt.from("work_item_map wim");
		stmt.where("wi.itemid = wim.itemid");
		bindModuleQuery(stmt, "wi");
		stmt.where("wi.status = ?", status);
		stmt.where(FolderSql.level("wim.kmid", "wim.level", _folderId, true));
		return stmt;
	}

	/**
	 * 업무방 포탈관리자 업무관리 특정상태의 카운트 SqlStatement를 돌려준다.
	 * @param status 상태
	 * @return
	 * @throws Exception
	 */
	public SqlSelect getStatusCount(int status) throws Exception
	{
		SqlSelect stmt = getAllSelect();
		stmt.from("work_item_map wim");
		stmt.where("wi.itemid = wim.itemid");
		bindModuleQuery(stmt, "wi");
		stmt.where("wi.status = ?", status);
		stmt.where(FolderSql.level("wim.kmid", "wim.level", _folderId, true));
		setCountCondition(stmt);
		return stmt;
	}

	/**
	 * 기한 만료 예정인 게시글을 SqlStatement를 돌려준다
	 * @param start
	 * @param end
	 * @return
	 * @throws Exception
	 */
	public SqlSelect getExpireList(Date start, Date end) throws Exception
	{
		SqlSelect stmt = getVisibleSelect();
		stmt.where(new ExpirePeriodSql("wi.expr_date", start, end));
		return stmt;
	}

	/**
	 * 기한 만료 예정인 게시글의 카운트 SqlStatement를 돌려준다
	 * @param start
	 * @param end
	 * @return
	 * @throws Exception
	 */
	public SqlSelect getExpireCount(Date start, Date end) throws Exception
	{
		SqlSelect stmt = getVisibleCount();
		stmt.where(new ExpirePeriodSql("wi.expr_date", start, end));
		return stmt;
	}

	/**
	 * 중요업무 목록을 돌려준다.
	 * @return
	 * @throws Exception
	 */
	public SqlSelect getFavoriteWorkList() throws Exception
	{
		SqlSelect stmt = new SqlSelect();
		stmt.select(WORKITEM, "list");
		stmt.from(WORKFVRT);
		bindModuleQuery(stmt, "wi");
		stmt.where("wif.userid = ?", UserService.getUserId());
		stmt.where("wif.itemid = wi.itemid");
		stmt.where("wi.isvisb = 1");
		return stmt;
	}

	/**
	 * 중요업무 갯수를 돌려준다.
	 * @return
	 * @throws Exception
	 */
	public SqlSelect getFavoriteWorkCount() throws Exception
	{
		SqlSelect stmt = new SqlSelect();
		stmt.select(WORKITEM, "list");
		stmt.from(WORKFVRT);
		bindModuleQuery(stmt, "wi");
		stmt.where("wif.userid = ?", UserService.getUserId());
		stmt.where("wif.itemid = wi.itemid");
		stmt.where("wi.isvisb = 1");
		setCountCondition(stmt);
		return stmt;
	}

	/**
	 * 중요업무로 되어 있는지 돌려준다.
	 * @param ids 선택한 id값
	 */
	public SqlSelect getExistsFvrt(Long[] ids) throws Exception
	{
		SqlSelect stmt = new SqlSelect();
		stmt.select(WORKITEM, "list");
		stmt.select("wif.itemid");
		stmt.from(WORKFVRT);
		bindModuleQuery(stmt, "wi");
		stmt.where("wif.userid = ?", UserService.getUserId());
		stmt.where("wif.itemid = wi.itemid");
		stmt.where(new InLongArray("wif.itemid", ids));
		return stmt;
	}

	/**
	 * 중요업무 여부를 돌려준다.
	 * @param id 여부체크를 위한 itemId값
	 */
	public static boolean isFvrt(Long id) throws Exception
	{
		SqlSelect stmt = new SqlSelect();
		stmt.select("wif.itemid");
		stmt.from(WORKFVRT);
		stmt.where("wif.userid = ?", UserService.getUserId());
		stmt.where("wif.itemid = ?", id);

		return stmt.query().next();
	}

	/**
	 * 중요업무 설정를 추가한다.
	 * @param objId 중요업무설정 추가를 위한 itemId값
	 */
	public void addFvrt(Long objId) throws Exception
	{
		SqlInsert inst = new SqlInsert(WORKFVRT);
		inst.setLong("itemid", objId);
		inst.setLong("userid", UserService.getUserId());
		inst.setTimestamp("rgst_date", SqlDate.getTimestamp());

		inst.execute();
	}

	/**
	 * 중요업무 설정를 삭제한다.
	 * @param ids 선택한 id 값
	 */
	public void deleteFvrt(Long[] ids) throws Exception
	{
		SqlDelete del = new SqlDelete(WORKFVRT);
		del.where("userid = ?", UserService.getUserId());
		del.where(new InLongArray("itemid", ids));
		del.execute();
	}

	/**
	 * folderId가 root일 경우 나의업무 목록(담당자 또는 협업자에 속한 목록)을 돌려준다.
	 * <p>
	 * folderId가 root가 아닐경우 나의업무 해당 분류에 속한 목록을 돌려준다.
	 * @param status 상태값
	 * @param folderId 분류값id (folderId가 userId일 경우 root)
	 * @throws Exception
	 */
	public SqlSelect getMyWorkList(int status, int folderId, String year) throws Exception
	{
		if (folderId == -1)
		{
			SqlSelect stmt2 = new SqlSelect();
			stmt2.select("wih.itemid");
			stmt2.from("work_item_helper wih");
			stmt2.where("wih.userid = ?", UserService.getUserId());
			stmt2.where("wih.itemid = wi.itemid");

			SqlSelect stmt1 = new SqlSelect();
			stmt1.select(WORKITEM, "list");
			stmt1.where("(wi.actor_userid = ?", UserService.getUserId());
			stmt1.andOff();
			stmt1.where(" or exists", stmt2);
			stmt1.where(")");
			stmt1.andOn();

			if (-1 != status)
			{
				if (_status.get(status) != null)
				{
					stmt1.where(new Sql.InIntArray("wi.status", _status.get(status)));
				}
				else
				{
					stmt1.where("wi.status = ?", status);
				}

			}

			if (StringUtils.isNumeric(year))
			{
				stmt1.where(
					(DbConfiguration.isMsSql() ? "year(wi.rgst_date)" : SqlDialect.toChar("wi.rgst_date", "YYYY"))
						+ " = ? ",
					year);
			}

			stmt1.where("isvisb = 1");
			bindModuleQuery(stmt1, "wi");
			return stmt1;
		}
		else
		{
			SqlSelect stmt = new SqlSelect();
			stmt.select(WORKITEM, "list");
			stmt.select("wdi.seq_order");
			stmt.from("work_dir_item wdi");
			stmt.where("wi.itemid = wdi.itemid");
			stmt.where("wdi.dirid = ?", folderId);
			stmt.where("wdi.userid = ?", UserService.getUserId());
			if (-1 != status)
			{
				stmt.where("wi.status = ?", status);
			}
			stmt.where("isvisb = 1");
			bindModuleQuery(stmt, "wi");
			return stmt;
		}
	}

	/**
	 * folderId가 root일 경우 나의업무 갯수를(담당자 또는 협업자에 속한 갯수) 돌려준다. folderId가 root가 아닐경우 나의업무 해당 분류에 속한 갯수를 돌려준다.
	 * @param status 상태값
	 * @param folderId 분류값id (folderId가 userId일 경우 root)
	 * @throws Exception
	 */
	public SqlSelect getMyWorkCount(int status, int folderId, String year) throws Exception
	{
		if (folderId == -1)
		{
			SqlSelect stmt2 = new SqlSelect();
			stmt2.select("wih.itemid");
			stmt2.from("work_item_helper wih");
			stmt2.where("wih.userid = ?", UserService.getUserId());
			stmt2.where("wih.itemid = wi.itemid");

			SqlSelect stmt1 = new SqlSelect();
			stmt1.select(WORKITEM, "list");
			stmt1.where("(wi.actor_userid = ?", UserService.getUserId());
			stmt1.andOff();
			stmt1.where(" or exists", stmt2);
			stmt1.where(")");
			stmt1.andOn();

			if (-1 != status)
			{
				stmt1.where("wi.status = ?", status);
			}

			if (StringUtils.isNumeric(year))
			{
				stmt1.where(
					(DbConfiguration.isMsSql() ? "year(wi.rgst_date)" : SqlDialect.toChar("wi.rgst_date", "YYYY"))
						+ " = ? ",
					year);
			}

			stmt1.where("isvisb = 1");
			bindModuleQuery(stmt1, "wi");
			return stmt1;
		}
		else
		{
			SqlSelect stmt = new SqlSelect();
			stmt.select(WORKITEM, "list");
			stmt.select("wdi.seq_order");
			stmt.from("work_dir_item wdi");
			stmt.where("wi.itemid = wdi.itemid");
			stmt.where("wdi.dirid = ?", folderId);
			stmt.where("wdi.userid = ?", UserService.getUserId());
			if (-1 != status)
			{
				stmt.where("wi.status = ?", status);
			}
			stmt.where("isvisb = 1");
			bindModuleQuery(stmt, "wi");
			return stmt;
		}
	}

	/**
	 * 포틀릿 요청하는 목록을 SqlStatement로 돌려준다.
	 * @return
	 * @throws Exception
	 */
	public SqlSelect getMyPortletList() throws Exception
	{
		SqlSelect stmt1 = new SqlSelect();
		stmt1.select("wi.itemid");
		stmt1.from(WORKITEM);
		stmt1.where("wi.actor_userid = ?", UserService.getUserId());

		SqlSelect stmt2 = new SqlSelect();
		stmt2.select("wih.itemid");
		stmt2.from("work_item_helper wih");
		stmt2.where("wih.userid = ?", UserService.getUserId());

		SqlSelect stmt = new SqlSelect();
		stmt.select(WORKITEM, "list");
		stmt.where("wi.itemid in ", stmt1.union(stmt2));

		stmt.where("isvisb = 1");
		bindModuleQuery(stmt, "wi");
		return stmt;
	}

	/**
	 * 포틀릿 요청하는 Count를 돌려준다.
	 * @return
	 * @throws Exception
	 */
	public SqlSelect getMyPortletCount() throws Exception
	{
		SqlSelect stmt1 = new SqlSelect();
		stmt1.select("wi.itemid");
		stmt1.from(WORKITEM);
		stmt1.where("wi.actor_userid = ?", UserService.getUserId());

		SqlSelect stmt2 = new SqlSelect();
		stmt2.select("wih.itemid");
		stmt2.from("work_item_helper wih");
		stmt2.where("wih.userid = ?", UserService.getUserId());

		SqlSelect stmt = new SqlSelect();
		stmt.select(WORKITEM, "list");
		stmt.where("wi.itemid in ", stmt1.union(stmt2));
		stmt.where("isvisb = 1");
		bindModuleQuery(stmt, "wi");
		return stmt;

	}

	/**
	 * 현재 사용자의 공유받은 목록 sql을 돌려준다.
	 * @return
	 * @throws Exception
	 */
	public SqlSelect getSharedSql() throws Exception
	{
		SqlSelect sub = new SqlSelect();
		sub.select("itemid");
		sub.from("work_item_share s");
		sub.where("s.share_userid = ? ", UserService.getUserId());

		SqlSelect stmt = getAllSelect();
		stmt.where("wi.isvisb = 1");
		bindModuleQuery(stmt, "wi");
		stmt.where("wi.itemid in ", sub);
		return stmt;
	}

	/**
	 * 담당 업무방을 돌려준다.
	 * @param status 상태
	 * @return
	 * @throws Exception
	 */
	public SqlSelect getMyChageListSql(int status) throws Exception
	{
		SqlSelect stmt = getAllSelect();
		if (status == -1)
		{
			stmt.where("wi.status < ?", Item.DELETED_STATUS);
		}
		else
		{
			stmt.where(new InIntArray("wi.status", _status.get(status)));
		}
		stmt.where("wi.actor_userid = ? ", UserService.getUserId());
		bindModuleQuery(stmt, "wi");
		stmt.where("isvisb = 1");
		if (-1 != status)
		{
			if (_status.get(status) != null)
			{
				stmt.where(new Sql.InIntArray("wi.status", _status.get(status)));
			}
			else
			{
				stmt.where("wi.status = ?", status);
			}
		}
		return stmt;
	}

	/**
	 * 모듈 정보를 WHERE절에 추가
	 * @param sql SqlSelect
	 * @param mp ModuleParam
	 * @return
	 * @throws Exception
	 */
	private SqlSelect bindModuleQuery(SqlSelect sql, String alias) throws Exception
	{
		sql.where(new StringBuffer(alias).append(".moduleid = ?").toString(), _moduleId);
		if (!_moduleMenu)
		{
			sql.where(new StringBuffer(alias).append(".appid = ?").toString(), _appId);
		}
		return sql;
	}

	/**
	 * 총건수 불필요할 경우 페이징을 위한 카운트만 실시한다.
	 */
	public void setCountCondition(SqlSelect stmt)
	{
		if (!isCountCondition())
		{
			stmt.rownum(_page.getVisibleCount());
		}
	}

	/**
	 * 총건수를 표시하는지 여부를 돌려준다.
	 * <p>
	 * true : 총건수 표시 false : 총건수 표시하지 않음
	 */
	public boolean isCountCondition()
	{
		return (_countVisible || _page.isSearch());
	}

	/**
	 * 기한 만료 예정 조건 검색식을 나타낸다.
	 * <p>
	 * startDate 가 null 인 경우 new Date(); endDate 가 null 인 경우 nextMonth(1);
	 */
	class ExpirePeriodSql implements SqlFragment
	{
		String _column;
		java.sql.Date _start;
		java.sql.Date _end;

		ExpirePeriodSql(String column, Date start, Date end)
		{
			_column = column;
			_start = (start == null) ? SqlDate.getDate() : SqlDate.getDate(start);
			_end = (end == null) ? SqlDate.getNextMonth(1) : SqlDate.getDate(end);
		}

		public boolean isValid()
		{
			return true;
		}

		/**
		 * 기한만료기간 조건 추가한다.
		 */
		public void make(StringBuffer query)
		{
			query.append(_column).append(" >= ? ");
			query.append("AND ").append(_column).append(" <= ? ");
		}

		/**
		 * 시작날짜와 종료날짜를 binding한다.
		 */
		public int bind(PreparedStatement pstmt, int index, int loop) throws Exception
		{
			pstmt.setDate(index, _start);
			pstmt.setDate(index + 1, _end);
			return index + 2;
		}
	}

	/**
	 * 중요업무 목록과 갯수를 돌려주는 SELECT문을 실행한 결과를 JSON으로 출력한다.
	 */
	public void writeFavoriteWorkJson(PrintWriter writer, SqlSelect select, SqlSelect count) throws Exception
	{
		_writer.setOrder("wif.rgst_date DESC");
		_writer.page(writer, select, count, _ts);
	}

	/**
	 * SELECT문을 실행한 결과를 JSON으로 출력한다.(최신글)
	 */
	public void writeLastUpdtDescJson(PrintWriter writer, SqlSelect select) throws Exception
	{
		_writer.setOrder("wi.last_updt DESC");
		_writer.setTable("work_item");
		_writer.page(writer, select, _ts);
	}

	/**
	 * 포탈 관리자페이지에서 SELECT문을 실행한 결과와 총 건수를 JSON으로 출력한다.
	 */
	public void writeAdmListJson(PrintWriter writer, SqlSelect select, SqlSelect count) throws Exception
	{
		_writer.setOrder("wi.rgst_date desc");
		_writer.setTable("work_item");
		_writer.page(writer, select, count, _ts);
	}

	/**
	 * SELECT문을 실행한 결과와 총 건수를 JSON으로 출력한다.
	 */
	public void writeJson(PrintWriter writer, SqlSelect select) throws Exception
	{
		_writer.setOrder(_defaultSort);
		_writer.setTagInfo("work_item", "wi", "itemid");
		_writer.page(writer, select, new SqlSelect(select), _ts);
	}

	/**
	 * /** 업무방 나의업무 (담당자 또는 협업자에 속한)목록과 갯수를 돌려주는 SELECT문을 실행한 결과를 JSON으로 출력한다.
	 * <p>
	 * folderId가 root일 경우 나의업무 목록과 갯수(담당자 또는 협업자에 속한 목록과 갯수)를 돌려준다.
	 * <p>
	 * folderId가 root가 아닐경우 나의업무 해당 분류에 속한 목록을 돌려준다. folderId가 userId와 같을 경우 root
	 */
	public void writeMyWorkListJson(PrintWriter writer, SqlSelect select, SqlSelect count, int folderId)
		throws Exception
	{
		if (folderId == -1)
		{
			_writer.setOrder("wi.last_updt desc");
		}
		else
		{
			_writer.setOrder("wdi.seq_order");
		}
		_writer.page(writer, select, count, _ts);
	}

	/**
	 * 포틀릿에 결과와 총 건수를 JSON으로 출력한다.
	 * @param writer
	 * @param select
	 * @param count
	 * @throws Exception
	 */
	public void writeMyPortletJson(PrintWriter writer, SqlSelect select, SqlSelect count) throws Exception
	{
		_writer.setOrder("wi.last_updt desc");
		_writer.page(writer, select, count, _ts);
	}

	/**
	 * 본인담당(담당자,협업자) 업무방 중 삭제된 업무방을 돌려준다.
	 */
	public SqlSelect getDeletedListSql() throws Exception
	{
		SqlSelect stmt = getAllSelect();
		bindModuleQuery(stmt, "wi");
		stmt.where("wi.status = ?", Item.DELETED_STATUS);
		stmt.where("wi.actor_userid = ? ", UserService.getUserId());
		return stmt;
	}

	/**
	 * 로그인 사용자가 담당자인 업무방들에 대한 버전관리되는 파일 목록을 돌려준다.
	 * @param ids
	 * @param includeLastVersion
	 * @return
	 */
	public SqlSelect getFileVersionList(Long[] ids, boolean includeLastVersion, int[] status)
	{
		SqlSelect sub = new SqlSelect();
		sub.select("wi.itemid");
		sub.from("work_item wi");
		sub.where("wi.actor_userid = ?", UserService.getUserId());
		sub.where(new Sql.InLongArray("wi.itemid", ids));
		sub.where(new Sql.InIntArray("wi.status", status));

		SqlSelect stmt = new SqlSelect();
		stmt.select(WORKREPORTFILE, "list");
		stmt.where("wr.itemid in", sub);
		if (!includeLastVersion)
		{
			stmt.where("wr.islastversion < ?", 1);
		}
		stmt.order("wr.file_gid desc, wr.vrsn_num desc");
		return stmt;
	}

	/**
	 * SELECT문을 실행한 결과를 JSON으로 출력한다.
	 */
	public void writeFileVersionJson(PrintWriter writer, SqlSelect select) throws Exception
	{
		_fileReportWriter.list(writer, select);
	}
}