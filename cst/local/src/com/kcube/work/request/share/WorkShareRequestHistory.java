package com.kcube.work.request.share;

import com.kcube.doc.hist.HistoryManager;
import com.kcube.sys.alimi.AlimiManager;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.User;
import com.kcube.sys.usr.UserService;
import com.kcube.work.Work;
import com.kcube.work.WorkHistory;
import com.kcube.work.WorkHistoryCode;

/**
 * @author 김경수
 *         <p>
 *         업무방 공유 요청 관련 History
 */
public class WorkShareRequestHistory
{
	// 알리미
	private static final String REQUEST_SHARE_ALIMI = "com.kcube.work.Work.requestShareNew";
	private static final String APPROVE_SHARE_ALIMI = "com.kcube.work.Work.approveShareNew";
	private static final String REJECT_SHARE_ALIMI = "com.kcube.work.Work.rejectShareNew";

	// 메일, 쪽지
	private static final String REQUEST_SHARE_MAIL = "com.kcube.work.Work.requestShare";
	private static final String APPROVE_SHARE_MAIL = "com.kcube.work.Work.approveShare";
	private static final String REJECT_SHARE_MAIL = "com.kcube.work.Work.rejectShare";

	/**
	 * 공유 요청
	 * @param mp
	 * @param item
	 * @throws Exception
	 */
	public static void apply(ModuleParam mp, Work item) throws Exception
	{
		requestedShare(mp, item);
		HistoryManager.history(WorkHistoryCode.REQUEST_SHARE, item);
	}

	/**
	 * 공유 요청 승인 - 공유 요청자에게 알림
	 * @param mp
	 * @param item
	 * @param reqUser
	 * @throws Exception
	 */
	public static void approve(ModuleParam mp, Work item, User reqUser) throws Exception
	{
		AlimiManager.log(
			mp,
			WorkHistoryCode.APPROVE_SHARE,
			reqUser.getUserId(),
			item.getTitle(),
			UserService.getUser(),
			APPROVE_SHARE_ALIMI,
			APPROVE_SHARE_MAIL,
			WorkHistory.ALIMI_WORK,
			item,
			item.getId());

		HistoryManager.history(WorkHistoryCode.APPROVE_SHARE, item);
	}

	/**
	 * 공유 요청 반려 - 공유 요청자에게 알림
	 * @param mp
	 * @param item
	 * @param reqUser
	 * @throws Exception
	 */
	public static void reject(ModuleParam mp, Work item, User reqUser) throws Exception
	{
		AlimiManager.log(
			mp,
			WorkHistoryCode.REJECT_SHARE,
			reqUser.getUserId(),
			item.getTitle(),
			UserService.getUser(),
			REJECT_SHARE_ALIMI,
			REJECT_SHARE_MAIL,
			WorkHistory.ALIMI_WORK,
			item,
			item.getId());

		HistoryManager.history(WorkHistoryCode.REJECT_SHARE, item);
	}

	/**
	 * 공유 요청 - 담당자에게 알림
	 * @param mp
	 * @param item
	 * @throws Exception
	 */
	public static void requestedShare(ModuleParam mp, Work item) throws Exception
	{
		if (item.getActor() != null)
		{
			alimiLog(
				mp,
				WorkHistoryCode.REQUEST_SHARE,
				item.getActor().getUserId(),
				item.getTitle(),
				UserService.getUser(),
				REQUEST_SHARE_ALIMI,
				REQUEST_SHARE_MAIL,
				WorkHistory.ALIMI_WORK,
				item,
				item.getId());
		}
	}

	/**
	 * 담당자에게 알림
	 * <p>
	 * 본인제외
	 * @throws Exception
	 */
	private static void alimiLog(
		ModuleParam mp,
		Integer code,
		Long userId,
		String title,
		User actor,
		String tmplName,
		String mailTmplName,
		String moduleName,
		Object item,
		Long itemId)
		throws Exception
	{
		if (!userId.equals(UserService.getUserId()))
		{
			AlimiManager.log(mp, code, userId, title, actor, tmplName, mailTmplName, moduleName, item, itemId);
		}
	}

}
