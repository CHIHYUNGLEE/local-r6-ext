package com.kcube.work.chat.item;

import com.kcube.lib.action.Action;
import com.kcube.lib.action.ActionContext;
import com.kcube.lib.jdbc.DbStorage;
import com.kcube.lib.json.JsonMapping;
import com.kcube.work.Work;
import com.kcube.work.chat.WorkChat;

/**
 * 업무방 대화에 기본 Action 정의 Class
 * @author Soo
 */
public abstract class WorkChatItemAction implements Action
{
	static DbStorage _chatStorage = new DbStorage(WorkChat.class);
	static DbStorage _workStorage = new DbStorage(Work.class);
	static DbStorage _storage = new DbStorage(WorkChatItem.class);

	static JsonMapping _factory = new JsonMapping(WorkChatItem.class, "list");

	/**
	 * ActionContext에서 Item 객체를 추출한다.
	 * <p>
	 * content가 별도의 parameter로 전달된 경우, xml 값보다 우선한다.
	 */
	WorkChatItem unmarshal(ActionContext ctx) throws Exception
	{
		WorkChatItem client = (WorkChatItem) _factory.unmarshal(ctx.getParameter("item"));
		return client;
	}
}