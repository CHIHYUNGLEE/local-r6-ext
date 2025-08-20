package com.kcube.work.chat;

import com.kcube.lib.sql.SqlSelect;

/**
 * 업무방 관련 SQL Class
 * @author 김경수
 */
public class WorkChatSql
{
	/**
	 * 업무방의 일련번호로 업무 채팅방에 대한 정보를 돌려준다.
	 * @param workId
	 * @return
	 */
	public SqlSelect getChatInfo(Long workId)
	{
		SqlSelect stmt = new SqlSelect();
		stmt.select("chatid, status, file_size, file_quota");
		stmt.from("work_chat");
		stmt.where("workid = ? ", workId);
		return stmt;
	}
}