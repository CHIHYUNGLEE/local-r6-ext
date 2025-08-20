package com.kcube.work;

import com.kcube.lib.action.ActionContext;
import com.kcube.lib.sql.Sql;
import com.kcube.lib.sql.SqlDate;
import com.kcube.lib.sql.SqlDelete;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.sys.usr.UserPermission;

/**
 * @author 김경수
 *         <p>
 *         업무방 Job 관련 Class
 */
public class WorkJob
{
	/**
	 * 임시로 작성된 업무방에 대해서 폐기하도록 처리함
	 * <p>
	 * 하루 이전의 데이터를 삭제처리함.
	 * <p>
	 * 최초 preWrite시에 객체를 생성함으로 관련 부분에 대해서 Job을 실행하여
	 * <p>
	 * 임시 저장 상태의 값에 대해서 삭제하도록 처리함.
	 */
	public static class TemporaryRemove extends WorkAction
	{
		@Override
		public void execute(ActionContext ctx) throws Exception
		{
			UserPermission.checkAdmin();

			SqlDelete del = new SqlDelete("work_item wi");
			del.where("wi.status = ?", Work.DRAFT_STATUS);
			del.where("wi.isvisb = ? ", false);
			del.where("wi.rgst_date < ? ", SqlDate.getNextDate(-1));
			del.execute();
		}
	}

	/**
	 * 업무방 history에 대한 과거 정보 삭제처리.
	 * <p>
	 * 기본적으로 완료된 업무방에 한하여 30일 이전에 대한 업무방에 대해서 삭제처리함.
	 * <p>
	 * 파라메터로 변경가능함.
	 * <p>
	 * @author 김경수
	 */
	public static class pastHistoryRemove extends WorkAction
	{
		@Override
		public void execute(ActionContext ctx) throws Exception
		{
			UserPermission.checkAdmin();

			int days = ctx.getInt("days", 30);
			Long[] statusArray = ctx.getLongTokens("status");
			if (null == statusArray)
			{
				statusArray = new Long[] {(long) Work.COMPLETE_STATUS, (long) Work.APRV_CMPLT_STATUS};
			}
			SqlSelect select = new SqlSelect();
			select.select("wi.itemid");
			select.from("work_item wi");
			select.where(new Sql.InLongArray("wi.status", statusArray));

			SqlDelete del = new SqlDelete("work_history wh");
			del.where("wh.inst_date < ? ", SqlDate.getNextDate(-days));
			del.where("itemid in ", select);
			del.execute();
		}
	}
}