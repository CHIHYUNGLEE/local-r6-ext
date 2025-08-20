package com.kcube.work.chat;

import java.sql.ResultSet;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.kcube.lib.jdbc.DbService;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.sys.usr.User;

/**
 * 업무방 채팅 Manager
 */
public class WorkChatManager
{
	/**
	 * 업무방 아이디로 채팅방 리턴
	 * @param workId
	 * @return
	 * @throws Exception
	 */
	static WorkChat getWorkChat(Long workId) throws Exception
	{
		@SuppressWarnings({"unchecked", "deprecation"})
		Iterator<WorkChat> it = DbService.getSession().getNamedQuery("getWorkChatByWorkId").setParameter(
			"workId",
			workId).iterate();
		if (it.hasNext())
		{
			return it.next();
		}
		return null;
	}

	/**
	 * 업무 채팅방을 등록상태로 한다.
	 */
	static void register(WorkChat server) throws Exception
	{
		server.setStatus(WorkChat.REGISTERED_STATUS);
		server.setLastUpdt(new Date());
		server.setRgstDate(new Date());
		server.updateVisible(true);
	}

	/**
	 * 채팅방 개설시 멤버테이블에 사용자를 추가한다.
	 */
	static void memberAdd(WorkChat server, User addUser) throws Exception
	{
		if (server.getMembers() == null || !server.getMembers().contains(addUser))
		{
			WorkChat.Members member = new WorkChat.Members(addUser);
			member.setRgstDate(new Date());
			member.setReadId(0L);
			server.addMembers(member);
			server.setMbrCnt(server.getMembers().size());
		}
	}

	/**
	 * 채팅방 개설시 멤버테이블에 사용자를 추가한다.
	 */
	static void memberAdd(WorkChat server, List<User> addUsers) throws Exception
	{
		for (User addMember : addUsers)
		{
			memberAdd(server, addMember);
		}
	}

	/**
	 * 채팅방을 폐쇄상태로 한다.
	 * @param server
	 * @throws Exception
	 */
	static void delete(WorkChat server) throws Exception
	{
		server.setStatus(WorkChat.DELETED_STATUS);
		server.setLastUpdt(new Date());
		server.updateVisible(false);
	}

	/**
	 * 채팅방을 복원한다.
	 * @param server
	 */
	static void recover(WorkChat server) throws Exception
	{
		server.setStatus(WorkChat.REGISTERED_STATUS);
		server.setLastUpdt(new Date());
		server.updateVisible(true);
	}

	/**
	 * 채팅방 멤버들의 썸네일정보를 반환한다.
	 */
	public static ResultSet getResult(Long id) throws Exception
	{
		SqlSelect stmt = new SqlSelect();
		stmt.select("userid,thumb_save_code, thumb_save_path");
		stmt.from("hr_user");
		stmt.where("userid in (select userid from work_chat_mbr where chatid = ?)", id);
		return stmt.query();
	}

	/**
	 * 업무방이 완료 되었을때 채팅방을 비활성화 시킨다.
	 * <p>
	 * 채팅을 볼 수는 있지만 더이상 작성할 수는 없다.
	 * @param chat
	 */
	public static void complete(WorkChat server)
	{
		server.setStatus(WorkChat.COMPLETED_STATUS);
		server.setLastUpdt(new Date());
		server.updateVisible(true);
	}
}