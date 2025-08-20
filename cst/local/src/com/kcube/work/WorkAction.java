package com.kcube.work;

import com.kcube.lib.action.Action;
import com.kcube.lib.action.ActionContext;
import com.kcube.lib.event.EventService;
import com.kcube.lib.jdbc.DbStorage;
import com.kcube.lib.json.JsonMapping;

/**
 * @author 김경수
 *         <p>
 *         업무방 Action
 */
public abstract class WorkAction implements Action
{
	static DbStorage _storage = new DbStorage(Work.class);
	static JsonMapping _factory = new JsonMapping(Work.class, "read");
	static JsonMapping _reviewerFactory = new JsonMapping(Work.class, "reviewer");

	/**
	 * 업무방 listener
	 */
	public static WorkListener _listener = (WorkListener) EventService.getDispatcher(WorkListener.class);

	/**
	 * client json data를 unmarshal하여 Work객체로 돌려준다.
	 * @param ctx
	 * @return
	 * @throws Exception
	 */
	Work unmarshal(ActionContext ctx) throws Exception
	{
		Work client = (Work) _factory.unmarshal(ctx.getParameter("item"));
		String content = ctx.getParameter("content");
		if (content != null)
		{
			client.setContent(content);
		}
		return client;
	}
}