package com.kcube.work.chat.item;

import java.io.StringWriter;

import com.kcube.lib.json.JsonMapping;
import com.kcube.work.chat.WorkChatSocketEvent;

/**
 * 업무 채팅 Event
 * @author shin8901
 */
public class WorkChatItemEvent implements WorkChatItemListener
{
	private JsonMapping _factory = new JsonMapping(WorkChatItem.class, "list");

	/**
	 * 채팅글 입력시 호출 된다.
	 */
	@Override
	public void registered(WorkChatItem server) throws Exception
	{
		StringWriter sw = new StringWriter();
		_factory.marshal(sw, server);
		WorkChatSocketEvent.sendMsg(server.getChatId(), "chatRecive", "item", sw.toString());
	}

	/**
	 * 채팅에 파일 등록 시 호출 된다.
	 */
	@Override
	public void fileRegistered(WorkChatItem server) throws Exception
	{
		StringWriter sw = new StringWriter();
		_factory.marshal(sw, server);
		WorkChatSocketEvent.sendMsg(server.getChatId(), "chatFileSend", "item", sw.toString());
	}
}