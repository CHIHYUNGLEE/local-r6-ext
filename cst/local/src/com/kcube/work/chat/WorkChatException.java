package com.kcube.work.chat;

import com.kcube.sys.AppException;

/**
 * 업무 채팅방 관련 Exception Class
 * @author 김경수
 */
public class WorkChatException extends AppException
{
	private static final long serialVersionUID = 9005571043396468156L;

	/**
	 * 운영 중이 아닌 채팅일 경우
	 * @author 김경수
	 */
	public static class CloseRoomDeniedException extends Exception
	{
		private static final long serialVersionUID = -5774387918421908739L;
	}

	/**
	 * 채팅방의 권한이 없을 경우
	 * @author 김경수
	 */
	public static class ReadDeniedException extends Exception
	{
		private static final long serialVersionUID = -1506636691116016240L;
	}
}
