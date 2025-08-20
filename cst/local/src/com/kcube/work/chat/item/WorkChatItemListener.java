package com.kcube.work.chat.item;

/**
 * 업무 채팅 Listener
 * @author 김경수
 */
public interface WorkChatItemListener
{
	/**
	 * 채팅 메세지(글)가 입력되면 호출된다.
	 * @param server
	 * @throws Exception
	 */
	void registered(WorkChatItem server) throws Exception;

	/**
	 * 채팅에 파일이 등록되면 호출된다.
	 * @param server
	 * @throws Exception
	 */
	void fileRegistered(WorkChatItem server) throws Exception;
}