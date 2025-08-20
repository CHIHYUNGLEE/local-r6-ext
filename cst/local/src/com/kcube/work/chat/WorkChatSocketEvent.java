package com.kcube.work.chat;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;

import com.kcube.lib.jdbc.DbService;
import com.kcube.lib.runtime.CacheCommand;
import com.kcube.lib.runtime.CacheService;
import com.kcube.lib.sql.SqlDate;
import com.kcube.sys.login.LoginSessionManager;
import com.kcube.sys.websocket.SocketServerModuleListener;
import com.kcube.sys.websocket.SocketServerService;
import com.kcube.sys.websocket.SocketServerSession;

import net.sf.json.JSONObject;

/**
 * 웹소켓 이벤트 관련 Class
 * @author 신운재
 */
public class WorkChatSocketEvent implements SocketServerModuleListener
{
	public static final String DEFAULT_LISTENER = "WorkChat";

	private static final String REQPARAM = "workId";
	private static final String SESSIONPARAM = "chatId";
	private static final String USERID = "userId";
	private static final String SELECT = "SELECT userid FROM WORK_CHAT_MBR c "
		+ "WHERE chatid = ? AND EXISTS (SELECT userid FROM sa_curruser WHERE jserver_name = ? and scode = ? AND userid = c.userid)";
	private static final String INSERT = "INSERT INTO work_chat_mbr_access "
		+ "(chatid, userid, wserver_name, wsessionid, inst_date) "
		+ "VALUES (?, ?, ?, ?, ?)";
	private static final String DELETE = "DELETE FROM work_chat_mbr_access WHERE wserver_name = ? AND wsessionid = ?";

	private static Map<Long, List<String>> _rooms = new ConcurrentHashMap<Long, List<String>>();

	/**
	 * 채팅방에 메시지를 호출 한다.
	 * @param roomId
	 * @param socketKey
	 * @param dataKey
	 * @param msg
	 * @param object
	 */
	public static void sendMsg(
		final Long roomId,
		final String listener,
		final String socketKey,
		final String dataKey,
		final String msg)
	{
		try
		{
			CacheService.send(new CacheCommand()
			{
				private static final long serialVersionUID = 578061093523574388L;

				@Override
				public void execute()
				{
					try
					{
						List<String> ids = _rooms.get(roomId);
						if (ids != null)
						{
							JSONObject obj = new JSONObject();
							if (socketKey != null)
							{
								obj.put("key", socketKey);
							}
							if (dataKey != null)
							{
								obj.put(dataKey, msg);
							}
							String ret = obj.toString();
							for (String id : ids)
							{
								SocketServerService.sendMessage(id, listener, ret);
							}
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 채팅방에 메시지를 호출 한다.
	 * @param chatId
	 * @param socketKey
	 * @param dataKey
	 * @param msg
	 */
	public static void sendMsg(Long chatId, String socketKey, String dataKey, String msg)
	{
		sendMsg(chatId, DEFAULT_LISTENER, socketKey, dataKey, msg);
	}

	/**
	 * paramMap에 업무방 일련번호를 기반으로 ChatRoom을 찾아 Member를 넣어주며, Chat Open 여부를 WebSocket으로 돌려준다.
	 */
	@Override
	public void open(String sessionId, SocketServerSession session, Map<String, List<String>> paramMap) throws Exception
	{
		List<String> workIds = paramMap.get(REQPARAM);
		if (workIds != null && workIds.size() > 0)
		{
			// Long workId = Long.valueOf(workIds.get(0));
			// Long workId = 1L;
			try
			{
				// WorkChat chat = WorkChatManager.getWorkChat(workId);
				// Long chatId = chat.getId();
				Long chatId = 1L;
				String[] keys = StringUtils.split(session.getKey(), '|');
				// 멤버 인증
				PreparedStatement select = DbService.prepareStatement(SELECT);
				select.setLong(1, chatId);
				select.setString(2, keys[0]);
				select.setString(3, keys[1]);
				ResultSet rs = select.executeQuery();
				if (rs.next())
				{
					// roomId를 소켓 세션객체에 넣어놓는다.
					session.setEtcInfo(SESSIONPARAM, chatId.toString());
					session.setEtcInfo(USERID, rs.getString(1));

					if (_rooms.containsKey(chatId))
					{
						_rooms.get(chatId).add(sessionId);
					}
					else
					{
						List<String> ids = new LinkedList<String>();
						ids.add(sessionId);
						_rooms.put(chatId, ids);
					}
					PreparedStatement insert = DbService.prepareStatement(INSERT);
					insert.setLong(1, chatId);
					insert.setLong(2, rs.getLong(1));
					insert.setString(3, LoginSessionManager.getServerName());
					insert.setString(4, sessionId);
					insert.setTimestamp(5, SqlDate.getTimestamp());
					insert.executeUpdate();
				}
				JSONObject writeStatus = new JSONObject();
				// if (chat.getStatus() == WorkChat.COMPLETED_STATUS)
				// {
				// writeStatus.put("isOpen", "false");
				// }
				// else
				// {
				// writeStatus.put("isOpen", "true");
				// }
				writeStatus.put("isOpen", "true");
				sendMsg(chatId, "openSuccess", "item", writeStatus.toString());
			}
			catch (Exception e)
			{
				DbService.rollbackAndClose();
				throw e;
			}
			finally
			{
				DbService.commitAndClose();
			}
		}
	}

	/**
	 * 채팅방 아이디가 존재하는 세션을 채팅방 Map에서 삭제한다.
	 */
	public void close(String paramString, SocketServerSession session) throws Exception
	{
		String chatId = session.getEtcInfoValue(SESSIONPARAM);
		if (StringUtils.isNotEmpty(chatId))
		{
			Long rId = Long.valueOf(chatId);
			List<String> ids = _rooms.get(rId);
			if (ids != null)
			{
				ids.remove(chatId);
				if (ids.size() == 0)
					_rooms.remove(Long.valueOf(chatId));
			}
			try
			{
				PreparedStatement delete = DbService.prepareStatement(DELETE);
				delete.setString(1, LoginSessionManager.getServerName());
				delete.setString(2, chatId);
				delete.executeUpdate();
			}
			catch (Exception e)
			{
				DbService.rollbackAndClose();
				throw e;
			}
			finally
			{
				DbService.commitAndClose();
			}
		}
	}

	@Override
	public void onMessage(String sessionId, SocketServerSession session, String message) throws Exception
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void garbageSession(String sessionId, SocketServerSession session) throws Exception
	{
		// TODO Auto-generated method stub
	}
}