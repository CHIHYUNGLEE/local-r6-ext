package com.kcube.work.recent;

import com.kcube.doc.expr.ExpireListener;
import com.kcube.lib.sql.SqlDate;
import com.kcube.lib.sql.SqlDelete;

/**
 * @author 박혜란
 *         <p>
 *         나의 최근사용 업무함 Event
 */
public class WorkRecentEvent implements ExpireListener
{
	public void expire() throws Exception
	{
		// 최근기록은 3개월 이후 삭제한다.
		SqlDelete del = new SqlDelete("work_item_recent");
		del.where("inst_date < ?", SqlDate.getNextMonth(-3));
		del.execute();
	}
}