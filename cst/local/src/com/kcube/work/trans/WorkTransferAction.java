package com.kcube.work.trans;

import com.kcube.lib.action.Action;
import com.kcube.lib.action.ActionContext;
import com.kcube.lib.jdbc.DbStorage;
import com.kcube.lib.json.JsonMapping;

/**
 * @author 신운재
 *         <p>
 *         업무방 인수인계 요청자 Action
 */
public abstract class WorkTransferAction implements Action
{
	static JsonMapping _factory = new JsonMapping(WorkTransfer.class, "read");
	static JsonMapping _listFactory = new JsonMapping(WorkTransfer.class, "itemList");
	static WorkTransManager _manager = new WorkTransManager();
	static DbStorage _storage = new DbStorage(WorkTransfer.class);

	/**
	 * json을 객체로 돌려줌.
	 * @param ctx
	 * @return
	 * @throws Exception
	 */
	static WorkTransfer unmarshal(ActionContext ctx, String item) throws Exception
	{
		WorkTransfer client = (WorkTransfer) _factory.unmarshal(ctx.getParameter(item));
		String content = ctx.getParameter("content");
		if (content != null)
		{
			client.setContent(content);
		}
		return client;
	}

	/**
	 * json을 객체로 돌려줌.
	 * @param ctx
	 * @param item
	 * @return
	 * @throws Exception
	 */
	static WorkTransfer unmarshal(ActionContext ctx) throws Exception
	{
		return unmarshal(ctx, "item");
	}
}