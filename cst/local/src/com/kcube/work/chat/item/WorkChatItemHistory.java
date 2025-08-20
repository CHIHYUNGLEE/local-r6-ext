package com.kcube.work.chat.item;

import java.util.Iterator;

import com.kcube.doc.hist.HistoryManager;
import com.kcube.sys.alimi.AlimiManager;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.User;
import com.kcube.sys.usr.UserService;
import com.kcube.work.Work;
import com.kcube.work.WorkHistory;
import com.kcube.work.WorkHistoryCode;
import com.kcube.work.chat.WorkChat;
import com.kcube.work.chat.WorkChat.Members;

/**
 * 업무방 채팅 관련한 로그 처리 Class
 * @author Soo
 */
public class WorkChatItemHistory
{
	private static final String REGISTER_WORKCHAT_ALIMI = "com.kcube.work.Work.registerWorkChatNew";

	/**
	 * 업무 채팅 글을 등록 시
	 */
	public static void registered(ModuleParam mp, Work item, WorkChat chat) throws Exception
	{
		User loginUesr = UserService.getUser();
		// 업무방 채팅 맴버에게 알림 (본인 제외)
		for (Iterator<Members> i = chat.getMembers().iterator(); i.hasNext();)
		{
			Members p = (Members) i.next();
			if (!loginUesr.getUserId().equals(p.getUserId()))
			{
				AlimiManager.log(
					mp,
					WorkHistoryCode.REGISTER_WORK_CHAT,
					p.getUserId(),
					item.getTitle(),
					loginUesr,
					REGISTER_WORKCHAT_ALIMI,
					null,
					WorkHistory.ALIMI_WORK,
					item,
					item.getId());
			}
		}
		HistoryManager.history(WorkHistoryCode.REGISTER_WORK_CHAT, item);
	}
}