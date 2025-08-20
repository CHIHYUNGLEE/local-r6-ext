package com.kcube.work.request.share;

import com.kcube.lib.action.Action;
import com.kcube.lib.jdbc.DbStorage;
import com.kcube.lib.json.JsonMapping;
import com.kcube.work.Work;

abstract public class WorkShareRequestAction implements Action
{
	static JsonMapping _factory = new JsonMapping(WorkShareRequest.class, "read");
	static DbStorage _storage = new DbStorage(WorkShareRequest.class);
	static DbStorage _workStorage = new DbStorage(Work.class);

	static WorkShareRequestManager _manager = new WorkShareRequestManager();
}
