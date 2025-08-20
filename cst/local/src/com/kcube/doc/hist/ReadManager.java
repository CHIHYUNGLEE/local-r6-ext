package com.kcube.doc.hist;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.kcube.doc.Item;
import com.kcube.doc.ItemPermission;
import com.kcube.lib.jdbc.DbConfiguration;
import com.kcube.lib.jdbc.DbService;
import com.kcube.lib.jdbc.TableState;
import com.kcube.lib.json.JsonWriter;
import com.kcube.lib.sql.Sql;
import com.kcube.lib.sql.SqlDate;
import com.kcube.lib.sql.SqlInsert;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.lib.sql.SqlUpdate;
import com.kcube.sys.conf.ConfigService;
import com.kcube.sys.usr.User;
import com.kcube.sys.usr.UserService;

/**
 * 조회로그를 관리한다.
 * <p>
 * 다음 작업을 처리할 수 있다.
 * <ul>
 * <li>이미 조회 했는지의 여부
 * <li>item의 조회수 증가
 * <li>조회 로그 남기는 작업
 * <li>옵션 처리 및 본인이 작성한 문서인 경우의 처리
 * </ul>
 * <p>
 * ReadManager의 constructor에 넘겨주는 클래스에 맵핑된 테이블에는 read_cnt컬럼이 존재해야 한다. 또한 해당 테이블의 이름에
 * <code>_read</code> 이 추가된 테이블이 존재해야 한다.
 */
public class ReadManager
{
	public static final int TODO = 0;
	public static final int DONE = 1;
	private static HistoryConfig _config = (HistoryConfig) ConfigService.getConfig(HistoryConfig.class);

	public static final Map<String, String> ATTRIBUTES = new HashMap<String, String>();
	public static final Map<String, String> COLUMNS = new HashMap<String, String>();
	static
	{
		ATTRIBUTES.put("userid", "userId");
		ATTRIBUTES.put("user_name", "userName");
		ATTRIBUTES.put("user_disp", "userDisp");
		ATTRIBUTES.put("inst_date", "instDate");
		ATTRIBUTES.put("cnt", "cnt");

		COLUMNS.put("userId", "userid");
	}
	private String _tableName;
	private String _readTable;
	private String _pkColumn;

	public ReadManager(Class<?> clazz)
	{
		this(clazz, "itemid");
	}

	public ReadManager(Class<?> clazz, String pkColumn)
	{
		_tableName = DbConfiguration.getTableName(clazz);
		_readTable = _tableName + "_read";
		_pkColumn = pkColumn;
	}

	/**
	 * 조회로그 count를 return 한다.
	 */
	public int getViewerCount(TableState ts, Integer cmdCode, Long itemid) throws Exception
	{
		return getViewerCount(ts, cmdCode, itemid, false);
	}

	/**
	 * 조회로그 count를 return 한다.
	 * @param isDetail : 상제정보 여부
	 */
	public int getViewerCount(TableState ts, Integer cmdCode, Long itemid, boolean isDetail) throws Exception
	{
		SqlSelect stmt = new SqlSelect();
		stmt.select("count(*) cnt");
		stmt.from(getViewerSql(ts, cmdCode, itemid, isDetail), "sub");
		ResultSet rs = stmt.query();
		rs.next();
		return rs.getInt(1);
	}

	/**
	 * 조회자 목록을 돌려준다.
	 */
	public ResultSet getViewerList(TableState ts, Integer cmdCode, Long itemid) throws Exception
	{
		return getViewerList(ts, cmdCode, itemid, false);
	}

	/**
	 * 조회자 목록을 돌려준다.
	 * @param isDetail : 상제정보 여부
	 */
	public ResultSet getViewerList(TableState ts, Integer cmdCode, Long itemid, boolean isDetail) throws Exception
	{
		StringBuffer query = new StringBuffer();
		query.append(getViewerSql(ts, cmdCode, itemid, isDetail));
		ts.appendOrderBy(query.append(" "), "inst_date desc ");
		ts.appendRownum(query);

		PreparedStatement pstmt = DbService.prepareStatement(query.toString());
		pstmt.setInt(1, cmdCode);
		pstmt.setLong(2, itemid);
		ts.bindRownum(pstmt, 3);
		return pstmt.executeQuery();
	}

	/**
	 * 조회로그의 Select 쿼리를 return 한다.
	 */
	public SqlSelect getViewerSql(TableState ts, Integer cmdCode, Long itemid) throws Exception
	{
		return getViewerSql(ts, cmdCode, itemid, false);
	}

	/**
	 * 조회로그의 Select 쿼리를 return 한다.
	 * @param isDetail : 상제정보 여부 false -> Item을 조회한 User별로 grouping하여 select 함. true ->
	 *        Item을 조회한 상세로그를 select 함.
	 */
	public SqlSelect getViewerSql(TableState ts, Integer cmdCode, Long itemid, boolean isDetail) throws Exception
	{
		SqlSelect stmt = new SqlSelect();
		stmt.select("userid, user_name, user_disp");
		if (isDetail)
			stmt.select("inst_date, 1 cnt");
		else
			stmt.select("max(inst_date) inst_date, count(*) cnt");
		stmt.from(_readTable);
		stmt.where("stat_flag = 1");
		stmt.where("cmd_code = ?", cmdCode);
		stmt.where(_pkColumn + " = ?", itemid);
		if (!isDetail)
			stmt.group("userid, user_name, user_disp");
		return stmt;
	}

	/**
	 * 조회수를 증가시킨다.
	 */
	public void readItem(Item item) throws Exception
	{

		SqlUpdate stmt = new SqlUpdate(_tableName);
		stmt.setValue("read_cnt = read_cnt + 1");
		stmt.where(_pkColumn + " = ?", item.getId());
		stmt.execute();

	}

	/*
	 * 조회 로그를 남긴다.
	 */
	protected void readLog(Integer cmdCode, Item item, int statFlag) throws Exception
	{
		User user = UserService.getUser();
		SqlInsert log = new SqlInsert(_readTable);
		log.setLong(_pkColumn, item.getId());
		log.setTimestamp("inst_date", SqlDate.getTimestamp());
		log.setLong("userid", user.getUserId());
		log.setString("user_name", user.getName());
		log.setString("user_disp", user.getDisplayName());
		log.setInt("cmd_code", cmdCode);
		log.setInt("stat_flag", statFlag);
		log.execute();
	}

	/**
	 * 조회 로그가 있는지의 여부를 돌려준다.
	 */
	public boolean hasRead(Integer cmdCode, Item item) throws Exception
	{
		SqlSelect stmt = new SqlSelect();
		stmt.select("count(*) cnt");
		stmt.from(_readTable);
		stmt.where(_pkColumn + " = ?", item.getId());
		stmt.where("userid = ?", UserService.getUserId());
		stmt.where("cmd_code = ?", cmdCode);
		ResultSet rs = stmt.query();
		rs.next();
		return rs.getInt(1) > 0;
	}

	/**
	 * 조회 로그가 오늘 있는지의 여부를 돌려준다.
	 */
	public boolean hasTodayRead(Integer cmdCode, Item item) throws Exception
	{
		SqlSelect stmt = new SqlSelect();
		stmt.select("count(*) cnt");
		stmt.from(_readTable);
		stmt.where(_pkColumn + " = ?", item.getId());
		stmt.where("userid = ?", UserService.getUserId());
		stmt.where("cmd_code = ?", cmdCode);
		stmt.where("inst_date >= ?", SqlDate.getDate());
		ResultSet rs = stmt.query();
		rs.next();
		return rs.getInt(1) > 0;
	}

	/**
	 * Config설정에 따라 조회수 처리를 하고 조회 로그를 남긴다.
	 */
	public void read(Integer cmdCode, Item item) throws Exception
	{
		if (ItemPermission.isOwner(item))
		{
			// 본인이면 변화없음.
			item.setTransientCnt(item.getReadCnt());
		}
		else if (UserService.isVirtual())
		{
			// 가상유저는 조회로그 남기지 않음
			item.setTransientCnt(item.getReadCnt());
		}
		else if (_config.isCountUser())
		{
			// log만 남기고 조회수 증가는 별도로 처리.
			readLog(cmdCode, item, TODO);
			item.setTransientCnt(item.getReadCnt());
		}
		else
		{
			// 조회수 증가하고, log 남긴다.
			if (!hasTodayRead(HistoryCode.READ, item))
			{
				readItem(item);
				readLog(cmdCode, item, DONE);
				item.setTransientCnt(item.getReadCnt() + 1);
			}
			else
			{
				item.setTransientCnt(item.getReadCnt());
			}
		}
	}

	/**
	 * 현재 사용자 기준으로 조회테이블에 해당 일련번호가 존재하는지 여부를 돌려준다.
	 * @param writer
	 * @param ids 일련번호들
	 * @throws Exception
	 */
	public void getReadIds(JsonWriter writer, Long[] ids) throws Exception
	{
		List<Long> readIds = new ArrayList<Long>();
		if (ids != null && ids.length > 0)
		{
			String pk = "i." + _pkColumn;

			SqlSelect stmt = new SqlSelect();
			stmt.select(pk);
			stmt.from(_tableName + " i");
			stmt.from(_readTable + " r");
			stmt.where(pk + " = r." + _pkColumn);
			stmt.where("r.userid = ?", UserService.getUserId());
			stmt.where("i.last_updt <= r.inst_date");
			stmt.where("r.cmd_code = ? ", HistoryCode.READ);
			stmt.where(new Sql.InLongArray(pk, ids, true));
			stmt.group(pk);

			ResultSet rs = stmt.query();
			while (rs.next())
			{
				readIds.add(rs.getLong("itemid"));
			}
		}
		writer.write("ids", StringUtils.join(readIds, ","));
	}
}
