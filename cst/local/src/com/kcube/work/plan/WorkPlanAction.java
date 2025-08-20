package com.kcube.work.plan;

import com.kcube.doc.reply.ReplyManager;
import com.kcube.lib.action.Action;
import com.kcube.lib.action.ActionContext;
import com.kcube.lib.jdbc.DbStorage;
import com.kcube.lib.json.JsonMapping;
import com.kcube.work.Work;

/**
 * 업무 수행 계획 Action Class
 */
abstract class WorkPlanAction implements Action
{
	static DbStorage _storage = new DbStorage(WorkPlan.class);
	static DbStorage _workStorage = new DbStorage(Work.class);

	static JsonMapping _factory = new JsonMapping(WorkPlan.class);
	static ReplyManager _reply = new ReplyManager(WorkPlan.class);

	WorkPlan unmarshal(ActionContext ctx) throws Exception
	{
		WorkPlan client = (WorkPlan) _factory.unmarshal(ctx.getParameter("item"));

		String content = ctx.getParameter("content");
		if (content != null)
		{
			client.setContent(content);
		}

		return client;
	}
}
