package com.kcube.work;

import java.sql.ResultSet;

import com.kcube.doc.hist.HistoryCode;
import com.kcube.doc.hist.ReadManager;
import com.kcube.lib.action.ActionContext;
import com.kcube.lib.jdbc.TableState;
import com.kcube.lib.json.JsonWriter;

/**
 * 업무방 조회로그 관련 Action
 */
public class WorkViewer
{
	static ReadManager _viewer = new ReadManager(Work.class);

	/**
	 * 조회로그 건수를 돌려준다.
	 */
	public static class ListCount extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			Integer cmdCode = HistoryCode.READ;
			Long itemid = ctx.getLong("itemid");
			TableState ts = new TableState(ctx.getParameter("ts"), ReadManager.COLUMNS);
			ResultSet rs = _viewer.getViewerList(ts, cmdCode, itemid);
			int totalRows = _viewer.getViewerCount(ts, cmdCode, itemid);
			new JsonWriter(ctx.getWriter()).write(rs, totalRows, ReadManager.ATTRIBUTES);
		}
	}

	/**
	 * 조회로그 상세 목록을 돌려준다.
	 */
	public static class DetailListCount extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			Integer cmdCode = HistoryCode.READ;
			Long itemid = ctx.getLong("itemid");
			TableState ts = new TableState(ctx.getParameter("ts"), ReadManager.COLUMNS);
			ResultSet rs = _viewer.getViewerList(ts, cmdCode, itemid, true);
			int totalRows = _viewer.getViewerCount(ts, cmdCode, itemid, true);
			new JsonWriter(ctx.getWriter()).write(rs, totalRows, ReadManager.ATTRIBUTES);
		}
	}
}