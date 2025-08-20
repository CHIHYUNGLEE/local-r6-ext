package com.kcube.work.report;

import com.kcube.lib.action.Action;
import com.kcube.lib.action.ActionContext;
import com.kcube.lib.jdbc.DbStorage;
import com.kcube.lib.json.JsonMapping;

/**
 * @author 성재호
 *         <p>
 *         업무방 Action
 */
abstract class ReportTemplateAction implements Action
{
	static DbStorage _storage = new DbStorage(ReportTemplate.class);
	static JsonMapping _factory = new JsonMapping(ReportTemplate.class);

	ReportTemplate unmarshall(ActionContext ctx) throws Exception
	{
		ReportTemplate client = (ReportTemplate) _factory.unmarshal(ctx.getParameter("item"));
		return client;
	}
}
