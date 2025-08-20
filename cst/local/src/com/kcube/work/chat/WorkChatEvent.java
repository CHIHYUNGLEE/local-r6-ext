package com.kcube.work.chat;

import java.sql.ResultSet;

import com.kcube.doc.file.Attachment;
import com.kcube.lib.jdbc.DbService;
import com.kcube.lib.jdbc.DbStorage;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.sys.usr.UserService;
import com.kcube.work.Work;
import com.kcube.work.WorkListener;

import net.sf.json.JSONObject;

/**
 * 업무방 대화 관련 Event 정의 Class
 * @author shin8901
 */
public class WorkChatEvent implements WorkListener
{
	static DbStorage _storage = new DbStorage(WorkChat.class);

	/*
	 * (non-Javadoc)
	 * @see com.kcube.work.WorkListener#registered(com.kcube.work.Work)
	 */
	@Override
	public void registered(Work server) throws Exception
	{
		WorkChat chat = new WorkChat();
		chat.setWorkId(server.getId());
		WorkChatManager.memberAdd(chat, server.getActor());
		WorkChatManager.memberAdd(chat, server.getHelpers());
		WorkChatManager.register(chat);
		DbService.save(chat);
	}

	/*
	 * (non-Javadoc)
	 * @see com.kcube.work.WorkListener#deleted(com.kcube.work.Work)
	 */
	@Override
	public void deleted(Work server) throws Exception
	{
		WorkChatSql sql = new WorkChatSql();
		SqlSelect stmt = sql.getChatInfo(server.getId());
		ResultSet rs = stmt.query();
		if (rs.next())
		{
			Long chatId = rs.getLong("chatid");
			WorkChat chat = (WorkChat) _storage.loadOrCreateWithLock(chatId);
			WorkChatManager.delete(chat);
			WorkChatSocketEvent.sendMsg(chat.getId(), "chatClose", "item", null);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.kcube.work.WorkListener#changeMember(com.kcube.work.Work)
	 */
	@Override
	public void changeMember(Work server) throws Exception
	{
		WorkChatSql sql = new WorkChatSql();
		SqlSelect stmt = sql.getChatInfo(server.getId());
		ResultSet rs = stmt.query();
		if (rs.next())
		{
			Long chatId = rs.getLong("chatid");
			WorkChat chat = (WorkChat) _storage.loadOrCreateWithLock(chatId);
			chat.setMembers(null);
			WorkChatManager.memberAdd(chat, server.getActor());
			WorkChatManager.memberAdd(chat, server.getHelpers());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.kcube.work.WorkListener#recover(com.kcube.work.Work)
	 */
	@Override
	public void recover(Work server) throws Exception
	{
		WorkChatSql sql = new WorkChatSql();
		SqlSelect stmt = sql.getChatInfo(server.getId());
		ResultSet rs = stmt.query();
		if (rs.next())
		{
			Long chatId = rs.getLong("chatid");
			WorkChat chat = (WorkChat) _storage.loadOrCreateWithLock(chatId);
			WorkChatManager.recover(chat);
			WorkChatSocketEvent.sendMsg(chat.getId(), "chatOpen", "item", null);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.kcube.work.WorkListener#complete(com.kcube.work.Work)
	 */
	@Override
	public void complete(Work server) throws Exception
	{
		WorkChatSql sql = new WorkChatSql();
		SqlSelect stmt = sql.getChatInfo(server.getId());
		ResultSet rs = stmt.query();
		if (rs.next())
		{
			Long chatId = rs.getLong("chatid");
			WorkChat chat = (WorkChat) _storage.loadOrCreateWithLock(chatId);
			WorkChatManager.complete(chat);
			WorkChatSocketEvent
				.sendMsg(chat.getId(), "chatClose", "item", JSONObject.fromObject(UserService.getUser()).toString());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.kcube.work.WorkListener#working(com.kcube.work.Work)
	 */
	@Override
	public void working(Work server) throws Exception
	{
		WorkChatSql sql = new WorkChatSql();
		SqlSelect stmt = sql.getChatInfo(server.getId());
		ResultSet rs = stmt.query();
		if (rs.next())
		{
			Long chatId = rs.getLong("chatid");
			WorkChat chat = (WorkChat) _storage.loadOrCreateWithLock(chatId);
			WorkChatManager.recover(chat);
			WorkChatSocketEvent
				.sendMsg(chat.getId(), "chatOpen", "item", JSONObject.fromObject(UserService.getUser()).toString());
		}
	}

	@Override
	public void deleteAtt(Work server, Attachment att)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void AddAtt(Work server, Attachment att)
	{
		// TODO Auto-generated method stub
	}
}