package com.kcube.work.chat;

import com.kcube.sys.usr.UserService;
import com.kcube.work.chat.WorkChatException.CloseRoomDeniedException;
import com.kcube.work.chat.WorkChatException.ReadDeniedException;
import com.kcube.work.chat.item.WorkChatItem;

/**
 * 업무 채팅방 권한 관련 Class
 * @author shin8901
 */
public class WorkChatPermission
{
	/**
	 * 운영중인 업무방 및 맴버 체크
	 * @param server
	 * @throws Exception
	 */
	public static void checkMbrAndRoom(WorkChat server) throws Exception
	{
		checkOpen(server);
		checkMember(server);
	}

	/**
	 * 운영중인 방인지 여부를 체크한다.
	 * @param room 채팅방
	 * @throws CloseRoomDeniedException
	 */
	public static void checkOpen(WorkChat room) throws Exception
	{
		if (room.getStatus() == WorkChat.DELETED_STATUS)
		{
			throw new WorkChatException.CloseRoomDeniedException();
		}
	}

	/**
	 * 특정채팅방의 멤버여부를 돌려준다.
	 * @param room 채팅방
	 */
	public static boolean isMember(WorkChat room)
	{
		for (WorkChat.Members member : room.getMembers())
		{
			if (member.getUserId().equals(UserService.getUserId()))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * 특정채팅방의 멤버여부를 돌려준다.
	 * @param room 채팅방
	 * @throws ReadDeniedException
	 */
	public static void checkMember(WorkChat room) throws ReadDeniedException
	{
		if (!isMember(room))
		{
			throw new WorkChatException.ReadDeniedException();
		}
	}

	/**
	 * 채팅글 작성자가 아닐 경우에 ReadDeniedException 발생한다.
	 * @param item
	 * @throws ReadDeniedException
	 */
	public static void checkItemOwner(WorkChatItem item) throws ReadDeniedException
	{
		if (!item.isCurrentOwner())
		{
			throw new ReadDeniedException();
		}
	}
}