package com.kcube.work.recent;

import com.kcube.lib.sql.SqlDate;
import com.kcube.lib.sql.SqlInsert;
import com.kcube.sys.usr.UserService;
import com.kcube.work.Work;

/**
 * 최근에 사용한 문서를 기록한다.
 */
public class WorkRecentLog
{
	/**
	 * 문서 조회
	 */
	public static final int READ = 1000;

	/**
	 * 문서 등록
	 */
	public static final int REGIST = 2000;

	/**
	 * 글을 조회한 최근 기록을 남긴다.
	 * @param item 조회한 문서
	 * @throws Exception
	 */
	public static void read(Work item) throws Exception
	{
		recentLog(READ, item);
	}

	/**
	 * 글을 등록한 최근기록을 남긴다.
	 * @param item 등록한 문서
	 * @throws Exception
	 */
	public static void registed(Work item) throws Exception
	{
		recentLog(REGIST, item);
	}

	/**
	 * 최근 로그를 남긴다
	 * @param actionType 활동 분류
	 * @param item 업무방
	 * @throws Exception
	 */
	private static void recentLog(Integer actionType, Work item) throws Exception
	{
		SqlInsert log = new SqlInsert("work_item_recent");
		log.setLong("itemid", item.getId());
		log.setLong("userid", UserService.getUserId());
		log.setTimestamp("inst_date", SqlDate.getTimestamp());
		log.setInt("action_type", actionType);
		log.execute();
	}
}