package com.kcube.doc.hist;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.kcube.doc.Item;
import com.kcube.lib.jdbc.DbConfiguration;
import com.kcube.lib.sql.SqlDate;
import com.kcube.lib.sql.SqlDelete;
import com.kcube.lib.sql.SqlInsert;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.lib.sql.SqlUpdate;
import com.kcube.sys.usr.User;
import com.kcube.sys.usr.UserService;

/**
 * 평가로그를 관리한다.
 * <p>
 * 다음 작업을 처리할 수 있다.
 * <ul>
 * <li>이미 평가/추천 했는지의 여부
 * <li>옵션 처리 및 본인이 작성한 문서인 경우의 처리
 * </ul>
 * <p>
 * EvalManager의 constructor에 넘겨주는 클래스에 맵핑된 테이블에는 eval_cnt, eval_sum 컬럼이 존재해야 한다. 또한 해당
 * 테이블의 이름에 <code>_eval</code>이 추가된 테이블이 존재해야 한다.
 */
public class EvalManager
{
	public static final Map<String, String> ATTRIBUTES = new HashMap<String, String>();
	public static final Map<String, String> COLUMNS = new HashMap<String, String>();
	static
	{
		ATTRIBUTES.put("userid", "userId");
		ATTRIBUTES.put("user_name", "userName");
		ATTRIBUTES.put("user_disp", "userDisp");
		ATTRIBUTES.put("inst_date", "instDate");
		ATTRIBUTES.put("cnt", "cnt");

		COLUMNS.put("userId", "itemid");
	}
	private String _tableName;
	private String _evalTable;

	public EvalManager(Class<?> clazz)
	{
		_tableName = DbConfiguration.getTableName(clazz);
		_evalTable = _tableName + "_eval";
	}

	/**
	 * 평가/추천 로그가 있는지의 여부를 돌려준다.
	 */
	public boolean hasEval(Integer cmdCode, Item item) throws Exception
	{
		return hasEval(cmdCode, item, false);
	}

	/**
	 * 평가/추천 로그가 있는지의 여부를 돌려준다.
	 */
	public boolean hasEval(Integer cmdCode, Item item, boolean isGrouping) throws Exception
	{
		SqlSelect stmt = new SqlSelect();
		stmt.select("count(*) cnt");
		stmt.from(_evalTable);
		if (isGrouping)
			stmt.where("gid = ?", item.getGid());
		else
			stmt.where("itemid = ?", item.getId());
		stmt.where("userid = ?", UserService.getUserId());
		stmt.where("cmd_code = ?", cmdCode);
		ResultSet rs = stmt.query();
		rs.next();
		return rs.getInt(1) > 0;
	}

	/**
	 * 문서를 평가한다.
	 */
	public void evalItem(Item item, int rate) throws Exception
	{
		SqlUpdate stmt = new SqlUpdate(_tableName);
		stmt.setValue("eval_cnt = eval_cnt + 1");
		stmt.setValue("eval_sum = eval_sum + " + rate);
		stmt.where("itemid = ?", item.getId());
		stmt.execute();
	}

	/**
	 * 평가 로그를 남긴다.
	 */
	public void insertLog(Integer cmdCode, Item item, int rate) throws Exception
	{
		insertLog(cmdCode, item, rate, false);
	}

	public void insertLog(Integer cmdCode, Item item, int rate, boolean isGrouping) throws Exception
	{
		User user = UserService.getUser();
		SqlInsert log = new SqlInsert(_evalTable);
		log.setLong("itemid", item.getId());
		if (isGrouping)
			log.setLong("gid", item.getGid());
		log.setTimestamp("inst_date", SqlDate.getTimestamp());
		log.setLong("userid", user.getUserId());
		log.setString("user_name", user.getName());
		log.setString("user_disp", user.getDisplayName());
		log.setInt("rate", rate);
		log.setInt("cmd_code", cmdCode);
		log.execute();
	}

	/**
	 * 평가 로그를 삭제한다.
	 */
	public void deleteLog(Integer cmdCode, Item item, int rate) throws Exception
	{
		deleteLog(cmdCode, item, rate, false);
	}

	public void deleteLog(Integer cmdCode, Item item, int rate, boolean isGrouping) throws Exception
	{
		User user = UserService.getUser();
		SqlDelete log = new SqlDelete(_evalTable);
		log.where("itemid = ?", item.getId());
		if (isGrouping)
			log.where("gid = ?", item.getGid());
		log.where("userid = ?", user.getUserId());
		log.execute();
	}

	/**
	 * 문서 테이블의 eval_cnt, eval_sum을 update하고 평가 로그 테이블에 로그를 기록한다.
	 * <p>
	 * server 객체 자체는 변경되지 않는다는 점에 주의한다. hibernate를 이용하여 값을 update할 경우 lock을 걸어야 하므로 비효율적이
	 * 된다. 따라서 직접 SQL query를 이용하고 객체는 수정하지 않는다.
	 * @throws AlreadyEvaluatedException 현재 사용자가 이미 문서를 평가한 로그가 있는 경우
	 */
	public void evaluate(Integer cmdCode, Item server, int rate) throws Exception
	{
		if (hasEval(cmdCode, server))
		{
			throw new AlreadyEvaluatedException();
		}
		evalItem(server, rate);
		evalAvg(server);
		insertLog(cmdCode, server, rate);
	}

	/**
	 * 사용자평가의 평균을 계산한다.
	 */
	public void evalAvg(Item item) throws Exception
	{
		SqlUpdate stmt = new SqlUpdate(_tableName);
		stmt.setValue("eval_avg = eval_sum / eval_cnt * " + Item.MULTIPLIER);
		stmt.where("itemid = ?", item.getId());
		stmt.execute();
	}

	/**
	 * 문서 테이블의 rcmd_cnt를 update하고 평가 로그 테이블에 로그를 기록한다.
	 * <p>
	 * 같은 group(동일한 gid를 가지고 있는 item)내에 여러 item을 추천을 금지 해야 할 경우 isGrouping:true로 하면 된다.
	 * @throws AlreadyRecommendedException 현재 사용자가 이미 문서(또는 같은 group내의 문서)를 추천한 로그가 있는 경우
	 */
	public void recommend(Integer cmdCode, Item server) throws Exception
	{
		recommend(cmdCode, server, false);
	}

	public void recommend(Integer cmdCode, Item server, boolean isGrouping) throws Exception
	{

		if (hasEval(cmdCode, server, isGrouping))
		{
			server.setRcmdCnt(server.getRcmdCnt() - 1);
			if (isGrouping)
				deleteCount(server.getGid());
			deleteLog(cmdCode, server, 0, isGrouping);
			// throw new AlreadyRecommendedException();
		}
		else
		{
			server.setRcmdCnt(server.getRcmdCnt() + 1);
			if (isGrouping)
				updateCount(server.getGid());
			insertLog(cmdCode, server, 0, isGrouping);
		}

	}

	/**
	 * 추천수를 증가시킨다.
	 */
	public void updateCount(Long itemId) throws Exception
	{
		SqlUpdate stmt = new SqlUpdate(_tableName);
		stmt.setValue("rcmd_cnt = rcmd_cnt + 1");
		stmt.where("itemid = ?", itemId);
		stmt.execute();
	}

	/**
	 * 추천수를 증가시킨다.
	 */
	public void deleteCount(Long itemId) throws Exception
	{
		SqlUpdate stmt = new SqlUpdate(_tableName);
		stmt.setValue("rcmd_cnt = rcmd_cnt - 1");
		stmt.where("itemid = ?", itemId);
		stmt.execute();
	}

	/**
	 * item load 시키지 않고 필요한 id, gid만 넘겨서 체크한다.
	 */
	public boolean hasEval(Integer cmdCode, Long id, Long gid) throws Exception
	{
		return hasEval(cmdCode, id, gid, false);
	}

	/**
	 * 평가/추천 로그가 있는지의 여부를 돌려준다.
	 */
	public boolean hasEval(Integer cmdCode, Long id, Long gid, boolean isGrouping) throws Exception
	{
		SqlSelect stmt = new SqlSelect();
		stmt.select("count(*) cnt");
		stmt.from(_evalTable);
		if (isGrouping)
			stmt.where("gid = ?", gid);
		else
			stmt.where("itemid = ?", id);
		stmt.where("userid = ?", UserService.getUserId());
		stmt.where("cmd_code = ?", cmdCode);
		ResultSet rs = stmt.query();
		rs.next();
		return rs.getInt(1) > 0;
	}
}
