package com.kcube.work.history;

import com.kcube.lib.action.Action;
import com.kcube.lib.jdbc.DbStorage;
import com.kcube.lib.sql.SqlTable;
import com.kcube.lib.sql.SqlWriter;

/**
 * 업무방 히스토리 액션
 */
abstract class WorkItemHistoryAction implements Action
{
	static final SqlTable HISTORY = new SqlTable("work_history", "h");
	static SqlWriter _writer = new SqlWriter().putAll(HISTORY);

	static DbStorage _storage = new DbStorage(WorkItemHistory.class);
	static WorkItemHistoryManager _manager = new WorkItemHistoryManager();
}
