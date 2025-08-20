package com.kcube.work;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.kcube.doc.ItemPermission;
import com.kcube.doc.hist.HistoryCode;
import com.kcube.doc.hist.HistoryManager;
import com.kcube.doc.hist.ReadManager;
import com.kcube.doc.opn.Opinion;
import com.kcube.doc.symp.SympathyManager;
import com.kcube.lib.jdbc.DbStorage;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.space.SpaceService;
import com.kcube.sys.alimi.AlimiManager;
import com.kcube.sys.conf.module.notify.NotifyConfigService;
import com.kcube.sys.emp.Employee;
import com.kcube.sys.mail.MailService;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.module.app.ModuleAppService;
import com.kcube.sys.msg.MessageService;
import com.kcube.sys.usr.User;
import com.kcube.sys.usr.UserService;
import com.kcube.work.chat.WorkChat;
import com.kcube.work.chat.item.WorkChatItem;
import com.kcube.work.history.WorkItemHistory;
import com.kcube.work.history.WorkItemHistoryManager;
import com.kcube.work.process.WorkProcess;
import com.kcube.work.recent.WorkRecentLog;

/**
 * @author 김경수
 *         <p>
 *         업무방 로그 관리
 */
public class WorkHistory
{
	// 알리미
	private static final String ASSIGNACTOR_ALIMI = "com.kcube.work.Work.assignWorkActorNew";
	private static final String ASSIGNHELPER_ALIMI = "com.kcube.work.Work.assignWorkHelperNew";
	private static final String ASSIGNSHARE_ALIMI = "com.kcube.work.Work.assignWorkShareNew";
	private static final String UPDATESTATUS_ALIMI = "com.kcube.work.Work.updateWorkStatusNew";
	private static final String DELETE_ALIMI = "com.kcube.work.Work.deleteWorkNew";
	private static final String REQREVIEW_ALIMI = "com.kcube.work.Work.requestReviewNew";
	private static final String REQAPPROVAL_ALIMI = "com.kcube.work.Work.requestApprovalNew";
	private static final String CMPLTREVIEW_ALIMI = "com.kcube.work.Work.completeReviewNew";
	private static final String CMPLTAPPROVAL_ALIMI = "com.kcube.work.Work.completeApprovalNew";
	private static final String NEEDMOREREQ_ALIMI = "com.kcube.work.Work.needMoreReqNew";
	private static final String NEEDMEETINGREQ_ALIMI = "com.kcube.work.Work.needMeetingReqNew";
	private static final String RETRIEVEDAPPRREQ_ALIMI = "com.kcube.work.Work.retrievedApprReqNew";
	private static final String RETRIEVEDREVIEWREQ_ALIMI = "com.kcube.work.Work.retrievedReviewReqNew";
	private static final String CHANGEDINCHARGER_ALIMI = "com.kcube.work.Work.changedInchargerNew";
	private static final String REGISTER_WORKCHAT_ALIMI = "com.kcube.work.Work.registerWorkChatNew";
	private static final String OPN_ALIMI = "com.kcube.work.Work.opnNew";
	private static final String RPLYOPN_ALIMI = "com.kcube.work.Work.replyOpnNew";
	private static final String SYMPATHY_ALIMI = "com.kcube.work.Work.sympathyNew";

	// 메일, 쪽지
	private static final String ASSIGNACTOR_MAIL = "com.kcube.work.Work.assignWorkActor";
	private static final String ASSIGNHELPER_MAIL = "com.kcube.work.Work.assignWorkHelper";
	private static final String ASSIGNSHARE_MAIL = "com.kcube.work.Work.assignWorkShare";
	private static final String UPDATESTATUS_MAIL = "com.kcube.work.Work.updateWorkStatus";
	private static final String DELETE_MAIL = "com.kcube.work.Work.deleteWork";
	private static final String REQREVIEW_MAIL = "com.kcube.work.Work.requestReview";
	private static final String REQAPPROVAL_MAIL = "com.kcube.work.Work.requestApproval";
	private static final String CMPLTREVIEW_MAIL = "com.kcube.work.Work.completeReview";
	private static final String CMPLTAPPROVAL_MAIL = "com.kcube.work.Work.completeApproval";
	private static final String NEEDMOREREQ_MAIL = "com.kcube.work.Work.needMoreReq";
	private static final String NEEDMEETINGREQ_MAIL = "com.kcube.work.Work.needMeetingReq";
	private static final String RETRIEVEDAPPRREQ_MAIL = "com.kcube.work.Work.retrievedApprReq";
	private static final String RETRIEVEDREVIEWREQ_MAIL = "com.kcube.work.Work.retrievedReviewReq";
	private static final String CHANGEDINCHARGER_MAIL = "com.kcube.work.Work.changedIncharger";

	public static final String ALIMI_WORK = "WORK";
	public static final String ALIMI_WORK_REPORT = "WORKREPORT";

	static ReadManager _read = new ReadManager(Work.class);
	static SympathyManager _sympathy = new SympathyManager(Work.class);
	static DbStorage _chatStorage = new DbStorage(WorkChat.class);
	static DbStorage _workStorage = new DbStorage(Work.class);

	/**
	 * 업무방 조회시 로그를 남긴다.
	 * @param item
	 * @throws Exception
	 */
	public static void read(Work item) throws Exception
	{
		_read.read(HistoryCode.READ, item);
		if (!ItemPermission.isOwner(item))
		{
			HistoryManager.history(HistoryCode.READ, item);
		}
		WorkRecentLog.read(item);
	}

	/**
	 * 업무방 등록시 로그를 남긴다.
	 * @param mp
	 * @param item
	 * @throws Exception
	 */
	public static void registered(ModuleParam mp, Work item) throws Exception
	{
		HistoryManager.history(HistoryCode.REGISTER, item);
		WorkRecentLog.registed(item);
		assignedAlimi(mp, item);
	}

	/**
	 * 업무방 상태 변경시 로그를 남긴다.
	 * @param mp
	 * @param item
	 * @throws Exception
	 */
	public static void updatedStatus(ModuleParam mp, Work item) throws Exception
	{
		HistoryManager.history(WorkHistoryCode.UPDATE_STATUS, item);
		if (item.getActor() != null)
		{
			// alimiLog(
			// mp,
			// WorkHistoryCode.UPDATE_STATUS,
			// item,
			// item.getActor().getUserId(),
			// UPDATESTATUS_ALIMI,
			// null,
			// UserService.getUser(),
			// false);

			alimiLog(
				mp,
				WorkHistoryCode.UPDATE_STATUS,
				item.getActor().getUserId(),
				item.getTitle(),
				UserService.getUser(),
				UPDATESTATUS_ALIMI,
				UPDATESTATUS_MAIL,
				ALIMI_WORK,
				item,
				item.getId());
		}
		if (item.getIsHelper())
		{
			// alimiLog(
			// mp,
			// WorkHistoryCode.UPDATE_STATUS,
			// item,
			// item.getHelpers(),
			// UPDATESTATUS_ALIMI,
			// null,
			// UserService.getUser(),
			// false);
			// MailManager.sendUsers(item.getHelpers(), UPDATESTATUS_MAIL, mp, item);

			alimiLog(
				mp,
				WorkHistoryCode.UPDATE_STATUS,
				item.getHelpers(),
				item.getTitle(),
				UserService.getUser(),
				UPDATESTATUS_ALIMI,
				UPDATESTATUS_MAIL,
				ALIMI_WORK,
				item,
				item.getId());
		}
	}

	/**
	 * 업무방 상태 완료시 로그를 남긴다.
	 * @param mp
	 * @param item
	 * @throws Exception
	 */
	public static void completed(ModuleParam mp, Work item) throws Exception
	{
		HistoryManager.history(HistoryCode.COMPLETE, item);
	}

	/**
	 * 업무방 수정시 로그를 남긴다.
	 * @param param
	 * @param item
	 * @throws Exception
	 */
	static void updated(ModuleParam param, Work item) throws Exception
	{
		HistoryManager.history(WorkHistoryCode.UPDATE, item);
	}

	/**
	 * 업무방 삭제시 로그를 남긴다.
	 * @param mp
	 * @param item
	 * @throws Exception
	 */
	static void deleted(ModuleParam mp, Work item) throws Exception
	{
		HistoryManager.history(HistoryCode.DELETE, item);

		if (item.getActor() != null)
		{
			// alimiLog(
			// mp,
			// HistoryCode.DELETE,
			// item,
			// item.getActor().getUserId(),
			// DELETE_ALIMI,
			// null,
			// UserService.getUser(),
			// false);
			// MailManager.sendUser(item.getActor(), DELETE_MAIL, mp, item);//

			alimiLog(
				mp,
				HistoryCode.DELETE,
				item.getActor().getUserId(),
				item.getTitle(),
				UserService.getUser(),
				DELETE_ALIMI,
				DELETE_MAIL,
				ALIMI_WORK,
				item,
				item.getId());

		}
		if (item.getIsHelper())
		{
			// alimiLog(mp, HistoryCode.DELETE, item, item.getHelpers(), DELETE_ALIMI,
			// null, UserService.getUser(), false);
			// MailManager.sendUsers(item.getHelpers(), DELETE_MAIL, mp, item);
			alimiLog(
				mp,
				HistoryCode.DELETE,
				item.getHelpers(),
				item.getTitle(),
				UserService.getUser(),
				DELETE_ALIMI,
				DELETE_MAIL,
				ALIMI_WORK,
				item,
				item.getId());
		}
		WorkItemHistoryManager.history(WorkItemHistory.DELETE, item);
	}

	/**
	 * 업무방 기간 만료 시에 로그를 남긴다.
	 * @param mp
	 * @param item
	 * @throws Exception
	 */
	public static void expired(Work item) throws Exception
	{
		HistoryManager.history(HistoryCode.EXPIRE, item);
	}

	/**
	 * 업무방 기한연장시 로그를 남긴다.
	 * @param item
	 * @throws Exception
	 */
	public static void extended(Work item) throws Exception
	{
		HistoryManager.history(HistoryCode.EXTEND, item);
	}

	/**
	 * 업무방 복원시 로그를 남긴다.
	 * @param item
	 * @throws Exception
	 */
	public static void recovered(Work item) throws Exception
	{
		HistoryManager.history(HistoryCode.RECOVER, item);
	}

	/**
	 * 업무방 폐기시 로그를 남긴다.
	 * @param item
	 * @throws Exception
	 */
	public static void removed(Work item) throws Exception
	{
		HistoryManager.history(HistoryCode.REMOVE, item);
	}

	/**
	 * 의견을 추가시 로그를 남긴다. 등록자에게 알림.
	 * @param mp 모듈 정보
	 * @param item 업무방
	 * @param opn 의견
	 * @throws Exception
	 */
	public static void addedOpinion(ModuleParam mp, Work item, Opinion opn) throws Exception
	{
		Long userId = UserService.getUserId();
		if (!userId.equals(item.getAuthor().getUserId()))
		{
			// AlimiManager.log(
			// mp,
			// HistoryCode.OPINION_REGISTED,
			// item.getAuthor().getUserId(),
			// item.getTitle(),
			// opn.getRgstUser(),
			// OPN_ALIMI,
			// ALIMI_WORK,
			// item,
			// item.getId(),
			// opn.getId(),
			// true);

			AlimiManager.log(
				mp,
				HistoryCode.OPINION_REGISTED,
				item.getAuthor().getUserId(),
				item.getTitle(),
				opn.getRgstUser(),
				OPN_ALIMI,
				null,
				ALIMI_WORK,
				item,
				item.getId(),
				opn.getId());
		}
		HistoryManager.history(HistoryCode.OPINION, item);
		WorkItemHistoryManager.history(WorkItemHistory.ADD_OPINION, item);
	}

	/**
	 * 의견의 덧글을 추가시 로그를 남긴다. 의견작성자에게 알림.
	 * @param mp 모듈 정보
	 * @param item 업무방
	 * @param parentOpn 부모의견
	 * @param opn 의견
	 * @throws Exception
	 */
	public static void replyOpinion(ModuleParam mp, Work item, Opinion parentOpn, Opinion opn) throws Exception
	{
		Long userId = UserService.getUserId();
		if (!userId.equals(parentOpn.getRgstUser().getUserId()))
		{
			// AlimiManager.log(
			// mp,
			// HistoryCode.OPINION_REPLY,
			// parentOpn.getRgstUser().getUserId(),
			// item.getTitle(),
			// opn.getRgstUser(),
			// RPLYOPN_ALIMI,
			// ALIMI_WORK,
			// item,
			// item.getId(),
			// opn.getId(),
			// true);

			AlimiManager.log(
				mp,
				HistoryCode.OPINION_REPLY,
				parentOpn.getRgstUser().getUserId(),
				item.getTitle(),
				opn.getRgstUser(),
				RPLYOPN_ALIMI,
				null,
				ALIMI_WORK,
				item,
				item.getId(),
				opn.getId());
		}
	}

	/**
	 * 의견 수정시 로그를 남긴다.
	 * @param item 업무방
	 * @throws Exception
	 */
	public static void updateOpinion(Work item) throws Exception
	{
		HistoryManager.history(HistoryCode.OPINION_UPDATE, item);
		WorkItemHistoryManager.history(WorkItemHistory.UPDATE_OPINION, item);
	}

	/**
	 * 의견 삭제시 로그를 남긴다.
	 * @param item 업무방
	 * @param opn 의견
	 * @throws Exception
	 */
	public static void removedOpinion(Work item, Opinion opn) throws Exception
	{
		item.setRefRgstUser(opn.getRgstUser());
		HistoryManager.history(HistoryCode.OPINION_REMOVED, item);
		WorkItemHistoryManager.history(WorkItemHistory.DELETE_OPINION, item);
	}

	/**
	 * 협력자가 변경될 시 로그를 남긴다. 관련자 전체에 알림.
	 * @param param
	 * @param server
	 * @param client
	 * @throws Exception
	 */
	static void updatedHelpers(ModuleParam param, Work server, Work client) throws Exception
	{
		if (server.getHelpers() != null)
		{
			List<User> sHelpers = server.getHelpers();
			List<User> cHelpers = client.getHelpers();
			List<User> newHelpers = new LinkedList<User>();
			List<User> delHelpers = new LinkedList<User>();
			for (User cHelper : cHelpers)
			{
				if (!(sHelpers.contains(cHelper) || sHelpers.contains(server.getActor())))
				{
					newHelpers.add(cHelper);
				}
			}
			for (User sHelper : sHelpers)
			{
				if (!cHelpers.contains(sHelper))
				{
					delHelpers.add(sHelper);
				}
			}
			// 신규 협업자 들에게 알림발송
			// alimiLog(
			// param,
			// WorkHistoryCode.ASSIGN_HELPER,
			// server,
			// newHelpers,
			// ASSIGNHELPER_ALIMI,
			// null,
			// UserService.getUser(),
			// false);
			// MailManager.sendUsers(newHelpers, ASSIGNHELPER_MAIL, param, server);

			alimiLog(
				param,
				WorkHistoryCode.ASSIGN_HELPER,
				newHelpers,
				server.getTitle(),
				UserService.getUser(),
				ASSIGNHELPER_ALIMI,
				ASSIGNHELPER_MAIL,
				ALIMI_WORK,
				server,
				server.getId());

			// 신규 협업자들에 대한 history 처리
			WorkItemHistoryManager.history(WorkItemHistory.ADD_HELPER, server, newHelpers);
			// 삭제된 협업자들에 대한 history 처리
			WorkItemHistoryManager.history(WorkItemHistory.DELETE_HELPER, server, delHelpers);
		}
	}

	/**
	 * 공유자가 변경될 시 로그를 남긴다. 관련자 전체에 알림.
	 * @param param
	 * @param server
	 * @param client
	 * @throws Exception
	 */
	static void updatedSharers(ModuleParam param, Work server, Work client) throws Exception
	{
		if (server.getSharers() != null)
		{
			List<User> sSharers = server.getSharers();
			List<User> cSharers = client.getSharers();
			List<User> newSharers = new LinkedList<User>();
			List<User> delSharers = new LinkedList<User>();
			for (User cShare : cSharers)
			{
				if (!sSharers.contains(cShare))
				{
					newSharers.add(cShare);
				}
			}
			for (User sShare : sSharers)
			{
				if (!cSharers.contains(sShare))
				{
					delSharers.add(sShare);
				}
			}
			// 신규 공유자들에게 알림발송
			// alimiLog(
			// param,
			// WorkHistoryCode.ASSIGN_SHARE,
			// server,
			// newSharers,
			// ASSIGNSHARE_ALIMI,
			// null,
			// UserService.getUser(),
			// false);
			// MailManager.sendUsers(newSharers, ASSIGNSHARE_MAIL, param, server);

			alimiLog(
				param,
				WorkHistoryCode.ASSIGN_SHARE,
				newSharers,
				server.getTitle(),
				UserService.getUser(),
				ASSIGNSHARE_ALIMI,
				ASSIGNSHARE_MAIL,
				ALIMI_WORK,
				server,
				server.getId());

			// 신규 공유자들에 대한 history 처리
			WorkItemHistoryManager.history(WorkItemHistory.ADD_SHARE, server, newSharers);
			// 삭제된 공유자들에 대한 history 처리
			WorkItemHistoryManager.history(WorkItemHistory.DELETE_SHARE, server, delSharers);
		}
	}

	/**
	 * 공유자가 변경될 시 로그를 남긴다. 관련자 전체에 알림. client 객체가 아니라, 공유자 객체를 받는 경우
	 * @param param
	 * @param server
	 * @param sharers
	 * @throws Exception
	 */
	static void updatedSharers(ModuleParam param, Work server, List<User> sharers) throws Exception
	{
		if (server.getSharers() != null)
		{
			List<User> sSharers = server.getSharers();
			List<User> cSharers = sharers;
			List<User> newSharers = new LinkedList<User>();
			List<User> delSharers = new LinkedList<User>();
			for (User cShare : cSharers)
			{
				if (!sSharers.contains(cShare))
				{
					newSharers.add(cShare);
				}
			}
			for (User sShare : sSharers)
			{
				if (!cSharers.contains(sShare))
				{
					delSharers.add(sShare);
				}
			}
			// 신규 공유자들에게 알림발송
			// alimiLog(
			// param,
			// WorkHistoryCode.ASSIGN_SHARE,
			// server,
			// newSharers,
			// ASSIGNSHARE_ALIMI,
			// null,
			// UserService.getUser(),
			// false);
			// MailManager.sendUsers(newSharers, ASSIGNSHARE_MAIL, param, server);

			alimiLog(
				param,
				WorkHistoryCode.ASSIGN_SHARE,
				newSharers,
				server.getTitle(),
				UserService.getUser(),
				ASSIGNSHARE_ALIMI,
				ASSIGNSHARE_MAIL,
				ALIMI_WORK,
				server,
				server.getId());

			// 신규 공유자들에 대한 history 처리
			WorkItemHistoryManager.history(WorkItemHistory.ADD_SHARE, server, newSharers);
			// 삭제된 공유자들에 대한 history 처리
			WorkItemHistoryManager.history(WorkItemHistory.DELETE_SHARE, server, delSharers);
		}
	}

	/**
	 * 담당자, 협업자 지정시 로그를 남긴다.
	 * @param mp
	 * @param item
	 * @throws Exception
	 */
	private static void assignedAlimi(ModuleParam mp, Work item) throws Exception
	{
		if (item.getActor() != null)
		{
			// alimiLog(
			// mp,
			// WorkHistoryCode.ASSIGN_ACTOR,
			// item,
			// item.getActor().getUserId(),
			// ASSIGNACTOR_ALIMI,
			// null,
			// item.getAuthor(),
			// false);
			// MailManager.sendUser(item.getActor(), ASSIGNACTOR_MAIL, mp, item);

			alimiLog(
				mp,
				WorkHistoryCode.ASSIGN_ACTOR,
				item.getActor().getUserId(),
				item.getTitle(),
				item.getAuthor(),
				ASSIGNACTOR_ALIMI,
				ASSIGNACTOR_MAIL,
				ALIMI_WORK,
				item,
				item.getId());
		}
		if (item.getIsHelper())
		{
			// alimiLog(
			// mp,
			// WorkHistoryCode.ASSIGN_HELPER,
			// item,
			// item.getHelpers(),
			// ASSIGNHELPER_ALIMI,
			// null,
			// item.getAuthor(),
			// false);
			// MailManager.sendUsers(item.getHelpers(), ASSIGNHELPER_MAIL, mp, item);

			alimiLog(
				mp,
				WorkHistoryCode.ASSIGN_HELPER,
				item.getHelpers(),
				item.getTitle(),
				item.getAuthor(),
				ASSIGNHELPER_ALIMI,
				ASSIGNHELPER_MAIL,
				ALIMI_WORK,
				item,
				item.getId());
		}
	}

	/**
	 * 보고서 파일 추가 시 활동로그를 남긴다.
	 * @param item
	 * @throws Exception
	 */
	public static void workReportAdd(Work server) throws Exception
	{
		HistoryManager.history(WorkHistoryCode.REPORT_ADD, server);
	}

	/**
	 * 보고서 버전업시 활동로그를 남긴다.
	 * @param item
	 * @throws Exception
	 */
	public static void workReportVersionUp(Work server) throws Exception
	{
		HistoryManager.history(WorkHistoryCode.REPORT_VERSIONUP, server);
	}

	/**
	 * 보고서 삭제 활동 로그를 남긴다.
	 * @param item
	 * @throws Exception
	 */
	public static void reportDelete(Work item) throws Exception
	{
		HistoryManager.history(WorkHistoryCode.REPORT_DELETE, item);
	}

	/**
	 * 보고서 다운로드시 활동 로그를 남긴다.
	 * @param item 보고서 업무방
	 * @param opn 보고서
	 * @throws Exception
	 */
	public static void reportDownloaded(Work item, Work.ReportFile report) throws Exception
	{
		HistoryManager.history(WorkHistoryCode.REPORT_DOWNLOAD, item);
	}

	/**
	 * 붙임파일 추가 시 활동로그를 남긴다.
	 * @param item
	 * @throws Exception
	 */
	public static void workAttachmentAdd(Work server) throws Exception
	{
		HistoryManager.history(WorkHistoryCode.ATTACHMENT_ADD, server);
	}

	/**
	 * 붙임파일 버전업시 활동로그를 남긴다.
	 * @param item
	 * @throws Exception
	 */
	public static void workAttachmentVersionUp(Work server) throws Exception
	{
		HistoryManager.history(WorkHistoryCode.ATTACHMENT_VERSIONUP, server);
	}

	/**
	 * 붙임파일삭제 활동 로그를 남긴다.
	 * @param item
	 * @throws Exception
	 */
	public static void workAttachmentDelete(Work item) throws Exception
	{
		HistoryManager.history(WorkHistoryCode.ATTACHMENT_DELETE, item);
	}

	/**
	 * 붙임파일 다운로드시 활동 로그를 남긴다.
	 * @param item 보고서 업무방
	 * @param opn 보고서
	 * @throws Exception
	 */
	public static void workAttachmentDownloaded(Work item, Work.WorkAttachment watt) throws Exception
	{
		HistoryManager.history(WorkHistoryCode.ATTACHMENT_DOWNLOAD, item);
	}

	/**
	 * 참고자료 추가시 활동 로그를 남긴다.
	 * @param item 보고서 업무방
	 * @param opn 보고서
	 * @throws Exception
	 */
	public static void referenceAdd(Work item, WorkReference att) throws Exception
	{
		HistoryManager.history(WorkHistoryCode.REFERENCE_ADD, item);
	}

	/**
	 * 참고자료 삭제시 활동 로그를 남긴다.
	 * @param item 보고서 업무방
	 * @param opn 보고서
	 * @throws Exception
	 */
	public static void referenceDeleted(Work item, WorkReference wrfrn) throws Exception
	{
		HistoryManager.history(WorkHistoryCode.REFERENCE_DELETE, item);
	}

	/**
	 * 참고자료 다운로드시 활동 로그를 남긴다.
	 * @param item 보고서 업무방
	 * @param opn 보고서
	 * @throws Exception
	 */
	public static void referenceDownloaded(Work item, WorkReference wrfrn) throws Exception
	{
		HistoryManager.history(WorkHistoryCode.REFERENCE_DOWNLOAD, item);
	}

	/**
	 * 담당자에게 알림
	 * <p>
	 * actor 는 제외함.
	 * @param mp
	 * @param code
	 * @param item
	 * @param userId
	 * @param title
	 * @param actor
	 * @param opnId
	 * @param tmplName
	 * @param mailTmplName
	 * @param moduleName
	 * @param item
	 * @param itemId
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
		if (!userId.equals(actor.getUserId()))
		{
			AlimiManager.log(mp, code, userId, title, actor, tmplName, mailTmplName, moduleName, item, itemId);
		}
	}

	/**
	 * 다수에게 알림시에 사용한다.
	 * @param mp
	 * @param code
	 * @param item
	 * @param users
	 * @param logName
	 * @param opnId
	 * @param actor
	 * @param grouping
	 * @throws Exception
	 */
	/*
	 * private static void alimiLog( ModuleParam mp, Integer code, List<User> users, String title, String logName, Long
	 * opnId, User actor, boolean grouping) throws Exception { List<User> tmpUsers = new LinkedList<User>();
	 * tmpUsers.addAll(users); if (tmpUsers.contains(actor)) tmpUsers.remove(actor); AlimiManager.log( mp, code,
	 * tmpUsers, item.getTitle(), actor, logName, ALIMI_WORK, item, item.getId(), opnId, grouping); }
	 */

	/**
	 * 김선이
	 * @param mp
	 * @param code
	 * @param user
	 * @param title
	 * @param actor
	 * @param tmplName
	 * @param mailTmplName
	 * @param moduleName
	 * @param item
	 * @param itemId
	 * @throws Exception
	 */
	private static void alimiLog(
		ModuleParam mp,
		Integer code,
		Collection<? extends User> user,
		String title,
		User actor,
		String tmplName,
		String mailTmplName,
		String moduleName,
		Object item,
		Long itemId)
		throws Exception
	{
		List<User> tmpUsers = new LinkedList<User>();
		tmpUsers.addAll(user);
		if (tmpUsers.contains(actor))
			tmpUsers.remove(actor);

		AlimiManager.log(mp, code, tmpUsers, title, actor, tmplName, mailTmplName, moduleName, item, itemId);

	}

	/**
	 * 기능 선택을 변경시 로그를 남긴다.
	 * @param mp
	 * @param server
	 * @throws Exception
	 */
	public static void updateItemVisible(ModuleParam mp, Work server) throws Exception
	{
		WorkItemHistoryManager.history(WorkItemHistory.UPDATE_ITEMVISIBLE, server);
		HistoryManager.history(WorkHistoryCode.UPDATE_ITEMVISIBLE, server);
	}

	/**
	 * 업무방 검토 / 보고 설정 변경시 로그를 남긴다.
	 * @param mp
	 * @param server
	 * @throws Exception
	 */
	public static void updateApprSetting(ModuleParam mp, Work server) throws Exception
	{
		WorkItemHistoryManager.history(WorkItemHistory.UPDATE_APPRSETTING, server);
		HistoryManager.history(WorkHistoryCode.UPDATE_APPRSETTING, server);
	}

	/**
	 * 검토 요청시 로그를 남긴다.
	 * @param server
	 * @param prcss
	 * @throws Exception
	 */
	public static void requestedReview(Work server, List<WorkProcess> prcss) throws Exception
	{
		ModuleParam mp = new ModuleParam(
			server.getClassId(), server.getModuleId(), server.getSpaceId(), null, server.getAppId());

		for (Iterator<WorkProcess> iterator = prcss.iterator(); iterator.hasNext();)
		{
			WorkProcess prcs = (WorkProcess) iterator.next();
			// AlimiManager.log(
			// mp,
			// WorkHistoryCode.REQUEST_REVIEW,
			// prcs.getChecker().getUserId(),
			// server.getTitle(),
			// UserService.getUser(),
			// REQREVIEW_ALIMI,
			// ALIMI_WORK,
			// prcs,
			// server.getId(),
			// null,
			// false);
			// MailManager.sendUser(prcs.getChecker(), REQREVIEW_MAIL, mp, server);

			AlimiManager.log(
				mp,
				WorkHistoryCode.REQUEST_REVIEW,
				prcs.getChecker().getUserId(),
				server.getTitle(),
				UserService.getUser(),
				REQREVIEW_ALIMI,
				REQREVIEW_MAIL,
				ALIMI_WORK,
				prcs,
				server.getId());
		}

		WorkItemHistoryManager.history(WorkItemHistory.REQUEST_REVIEW, server, server.getReviewers());
		HistoryManager.history(WorkHistoryCode.REQUEST_REVIEW, server);
	}

	/**
	 * 검토 완료시 로그를 남긴다.
	 * @param mp
	 * @param server
	 * @throws Exception
	 */
	public static void completedReview(ModuleParam mp, Work server) throws Exception
	{
		ArrayList<User> list = new ArrayList<User>();
		list.add(server.getActor());
		list.addAll(server.getHelpers());
		// alimiLog(
		// mp,
		// WorkHistoryCode.COMPELTE_REVIEW,
		// server,
		// list,
		// CMPLTREVIEW_ALIMI,
		// null,
		// UserService.getUser(),
		// false);
		// MailManager.sendUsers(list, CMPLTREVIEW_MAIL, mp, server);

		alimiLog(
			mp,
			WorkHistoryCode.COMPELTE_REVIEW,
			list,
			server.getTitle(),
			UserService.getUser(),
			CMPLTREVIEW_ALIMI,
			CMPLTREVIEW_MAIL,
			ALIMI_WORK,
			server,
			server.getId());

		WorkItemHistoryManager.history(WorkItemHistory.COMPLETE_REVIEW, server);
		HistoryManager.history(WorkHistoryCode.COMPELTE_REVIEW, server);
	}

	/**
	 * 승인 요청시 로그를 남긴다.
	 * @param server
	 * @param prcs
	 * @throws Exception
	 */
	public static void requestedApproval(Work server, WorkProcess prcs) throws Exception
	{
		ModuleParam mp = new ModuleParam(
			server.getClassId(), server.getModuleId(), server.getSpaceId(), null, server.getAppId());

		// AlimiManager.log(
		// mp,
		// WorkHistoryCode.REQUEST_APPROVAL,
		// prcs.getChecker().getUserId(),
		// server.getTitle(),
		// UserService.getUser(),
		// REQAPPROVAL_ALIMI,
		// ALIMI_WORK,
		// prcs,
		// server.getId(),
		// null);
		// MailManager.sendUser(prcs.getChecker(), REQAPPROVAL_MAIL, mp, server);

		AlimiManager.log(
			mp,
			WorkHistoryCode.REQUEST_APPROVAL,
			prcs.getChecker().getUserId(),
			server.getTitle(),
			UserService.getUser(),
			REQAPPROVAL_ALIMI,
			REQAPPROVAL_MAIL,
			ALIMI_WORK,
			prcs,
			server.getId());

		WorkItemHistoryManager.history(WorkItemHistory.REQUEST_APPROVAL, server, server.getApprover());
		HistoryManager.history(WorkHistoryCode.REQUEST_APPROVAL, server);
	}

	/**
	 * 승인 완료시 로그를 남긴다.
	 * @param mp
	 * @param server
	 * @throws Exception
	 */
	public static void completedApproval(ModuleParam mp, Work server) throws Exception
	{
		ArrayList<User> list = new ArrayList<User>();
		list.add(server.getActor());
		list.addAll(server.getHelpers());
		// alimiLog(
		// mp,
		// WorkHistoryCode.COMPELTE_APPROVAL,
		// server,
		// list,
		// CMPLTAPPROVAL_ALIMI,
		// null,
		// UserService.getUser(),
		// false);
		// MailManager.sendUsers(list, CMPLTAPPROVAL_MAIL, mp, server);

		alimiLog(
			mp,
			WorkHistoryCode.COMPELTE_APPROVAL,
			list,
			server.getTitle(),
			UserService.getUser(),
			CMPLTAPPROVAL_ALIMI,
			CMPLTAPPROVAL_MAIL,
			ALIMI_WORK,
			server,
			server.getId());

		HistoryManager.history(WorkHistoryCode.COMPELTE_APPROVAL, server);
		WorkItemHistoryManager.history(WorkItemHistory.COMPLETE_APPROVAL, server);
	}

	/**
	 * 검토 처리 시에 로그를 남긴다.
	 * @param mp
	 * @param server
	 * @throws Exception
	 */
	public static void processedReview(ModuleParam mp, Work server) throws Exception
	{
		WorkItemHistoryManager.history(WorkItemHistory.PROCESSED_REVIEW, server);
		HistoryManager.history(WorkHistoryCode.PROCESSED_REVIEW, server);
	}

	/**
	 * 보완을 요청 시에 로그를 남긴다.
	 * <p>
	 * 승인 요청 후 승인자가 처리가능한 프로세스임
	 * <p>
	 * 담당자에게 알림
	 * @param item
	 */
	public static void requestedNeedSupplement(Work server) throws Exception
	{
		ModuleParam mp = new ModuleParam(
			server.getClassId(), server.getModuleId(), server.getSpaceId(), null, server.getAppId());

		// AlimiManager.log(
		// mp,
		// WorkHistoryCode.NEED_MORE_REQ,
		// server.getActor().getUserId(),
		// server.getTitle(),
		// UserService.getUser(),
		// NEEDMOREREQ_ALIMI,
		// ALIMI_WORK,
		// server,
		// server.getId(),
		// null);
		// MailManager.sendUser(server.getActor(), NEEDMOREREQ_MAIL, mp, server);

		AlimiManager.log(
			mp,
			WorkHistoryCode.NEED_MORE_REQ,
			server.getActor().getUserId(),
			server.getTitle(),
			UserService.getUser(),
			NEEDMOREREQ_ALIMI,
			NEEDMOREREQ_MAIL,
			ALIMI_WORK,
			server,
			server.getId());

		WorkItemHistoryManager.history(WorkItemHistory.NEED_MORE_REQ, server);
		HistoryManager.history(WorkHistoryCode.NEED_MORE_REQ, server);
	}

	/**
	 * 대면을 요청 시에 로그를 남긴다.
	 * <p>
	 * 담당자에게 알림
	 * @param item
	 */
	public static void requestedNeedMeeting(Work server) throws Exception
	{
		ModuleParam mp = new ModuleParam(
			server.getClassId(), server.getModuleId(), server.getSpaceId(), null, server.getAppId());

		// AlimiManager.log(
		// mp,
		// WorkHistoryCode.NEED_MEETING_REQ,
		// server.getActor().getUserId(),
		// server.getTitle(),
		// UserService.getUser(),
		// NEEDMEETINGREQ_ALIMI,
		// ALIMI_WORK,
		// server,
		// server.getId(),
		// null);
		// MailManager.sendUser(server.getActor(), NEEDMEETINGREQ_MAIL, mp, server);

		AlimiManager.log(
			mp,
			WorkHistoryCode.NEED_MEETING_REQ,
			server.getActor().getUserId(),
			server.getTitle(),
			UserService.getUser(),
			NEEDMEETINGREQ_ALIMI,
			NEEDMEETINGREQ_MAIL,
			ALIMI_WORK,
			server,
			server.getId());

		WorkItemHistoryManager.history(WorkItemHistory.NEED_MEETING_REQ, server);
		HistoryManager.history(WorkHistoryCode.NEED_MEETING_REQ, server);
	}

	/**
	 * 메일을 발송한다.
	 * @param name
	 * @param receiver
	 * @param spaceId
	 * @param moduleId
	 * @param appId
	 * @param item
	 * @throws Exception
	 */
	public static void send(String name, Employee receiver, Work server, User sender) throws Exception
	{
		boolean email = false;
		boolean msg = false;
		Long moduleId = server.getModuleId();
		Long appId = server.getAppId();
		Long spaceId = server.getSpaceId();

		email = NotifyConfigService.getBooleanProperty(moduleId, appId, name + "Mail");
		msg = NotifyConfigService.getBooleanProperty(moduleId, appId, name + "Msg");

		if ((email) || (msg))
		{
			Long tanantId = receiver.getTenantId();
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("receiver", receiver);
			m.put("sender", sender);
			m.put("item", server);
			m.put("sysdate", new Date());
			m.put("spaceName", SpaceService.getSpaceName(spaceId));
			m.put("appName", ModuleAppService.getAppName(appId));

			String content = NotifyConfigService.merge(moduleId, appId, name + "Mail" + "Content", m, tanantId);
			if (email)
			{
				String title = NotifyConfigService.merge(moduleId, appId, name + "Mail" + "Title", m, tanantId);
				MailService.send(receiver, title, content, true);
			}
			if (msg)
			{
				MessageService.send(content, receiver.getUser());
			}
		}
	}

	/**
	 * 승인/검토 요청 중 회수 처리시 로그를 남긴다.
	 * @param server
	 * @param prcss
	 * @param beforeStatus
	 * @throws Exception
	 */
	public static void requestedRetrieve(Work server, ArrayList<WorkProcess> prcss, int beforeStatus) throws Exception
	{
		ModuleParam mp = new ModuleParam(
			server.getClassId(), server.getModuleId(), server.getSpaceId(), null, server.getAppId());

		if (beforeStatus == Work.APPROVAL_STATUS)
		{
			for (Iterator<WorkProcess> iterator = prcss.iterator(); iterator.hasNext();)
			{
				WorkProcess prcs = (WorkProcess) iterator.next();
				// AlimiManager.log(
				// mp,
				// WorkHistoryCode.RETRIEVED_APPROVAL,
				// prcs.getChecker().getUserId(),
				// server.getTitle(),
				// UserService.getUser(),
				// RETRIEVEDAPPRREQ_ALIMI,
				// ALIMI_WORK,
				// prcs,
				// server.getId(),
				// null);
				// MailManager.sendUser(prcs.getChecker(), RETRIEVEDAPPRREQ_MAIL, mp,
				// server);

				AlimiManager.log(
					mp,
					WorkHistoryCode.RETRIEVED_APPROVAL,
					prcs.getChecker().getUserId(),
					server.getTitle(),
					UserService.getUser(),
					RETRIEVEDAPPRREQ_ALIMI,
					RETRIEVEDAPPRREQ_MAIL,
					ALIMI_WORK,
					prcs,
					server.getId());
			}

			WorkItemHistoryManager.history(WorkItemHistory.RETRIEVED_APPROVAL, server);
			HistoryManager.history(WorkHistoryCode.RETRIEVED_APPROVAL, server);
		}
		else if (beforeStatus == Work.REVIEW_STATUS)
		{
			for (Iterator<WorkProcess> iterator = prcss.iterator(); iterator.hasNext();)
			{
				WorkProcess prcs = (WorkProcess) iterator.next();
				// AlimiManager.log(
				// mp,
				// WorkHistoryCode.RETRIEVED_REVIEW,
				// prcs.getChecker().getUserId(),
				// server.getTitle(),
				// UserService.getUser(),
				// RETRIEVEDREVIEWREQ_ALIMI,
				// ALIMI_WORK,
				// prcs,
				// server.getId(),
				// null);
				// MailManager.sendUser(prcs.getChecker(), RETRIEVEDREVIEWREQ_MAIL, mp,
				// server);

				AlimiManager.log(
					mp,
					WorkHistoryCode.RETRIEVED_REVIEW,
					prcs.getChecker().getUserId(),
					server.getTitle(),
					UserService.getUser(),
					RETRIEVEDREVIEWREQ_ALIMI,
					RETRIEVEDREVIEWREQ_MAIL,
					ALIMI_WORK,
					prcs,
					server.getId());
			}

			WorkItemHistoryManager.history(WorkItemHistory.RETRIEVED_REVIEW, server);
			HistoryManager.history(WorkHistoryCode.RETRIEVED_REVIEW, server);
		}
	}

	/**
	 * 담당자를 변경시 로그를 남긴다.
	 * @param mp
	 * @param server
	 * @throws Exception
	 */
	public static void changedInCharger(ModuleParam mp, Work server) throws Exception
	{
		AlimiManager.log(
			mp,
			WorkHistoryCode.CHANGED_INCHARGER,
			server.getActor().getUserId(),
			server.getTitle(),
			UserService.getUser(),
			CHANGEDINCHARGER_ALIMI,
			CHANGEDINCHARGER_MAIL,
			ALIMI_WORK,
			server,
			server.getId());

		WorkItemHistoryManager.history(WorkItemHistory.CHANGED_INCHARGER, server, server.getActor());
		HistoryManager.history(WorkHistoryCode.CHANGED_INCHARGER, server);
	}

	/**
	 * 업무방에서 웹채팅 입력 시에 현재 웹채팅을 보고 있지 않는 사용자에게 알림을 보낸다.
	 * @param item
	 * @throws Exception
	 */
	public static void registeredWorkChat(WorkChatItem item) throws Exception
	{
		WorkChat room = (WorkChat) _chatStorage.load(item.getChatId());
		room.getMembers();
		Work server = (Work) _workStorage.load(room.getWorkId());

		ModuleParam mp = new ModuleParam(
			server.getClassId(), server.getModuleId(), server.getSpaceId(), null, server.getAppId());

		List<User> tmpUsers = new LinkedList<User>();
		tmpUsers.addAll(room.getMembers());

		// 채팅 작성자는 제외
		if (tmpUsers.contains(item.getRgstUser()))
		{
			tmpUsers.remove(item.getRgstUser());
		}

		SqlSelect stmt = new SqlSelect();
		stmt.select("userid");
		stmt.from("work_chat_mbr_access");
		stmt.where("chatid = ?", room.getId());
		ResultSet rs = stmt.query();
		while (rs.next())
		{
			User user = new User();
			user.setUserId(rs.getLong("userid"));
			// 웹소켓이 연결된 사용자 제외
			if (tmpUsers.contains(user))
			{
				tmpUsers.remove(user);
			}
		}

		AlimiManager.log(
			mp,
			WorkHistoryCode.REGISTER_WORK_CHAT,
			tmpUsers,
			server.getTitle(),
			item.getRgstUser(),
			REGISTER_WORKCHAT_ALIMI,
			null,
			ALIMI_WORK,
			server,
			server.getId());
	}

	/**
	 * 공감 시 로그를 남긴다. 담당자에게 알림
	 * @param mParam
	 * @param item
	 * @param type
	 * @throws Exception
	 */
	static void addSympathy(ModuleParam mParam, Work item, int type) throws Exception
	{
		_sympathy.add(item, type);
		if (!UserService.getUserId().equals(item.getActor().getUserId()))
		{
			AlimiManager.log(
				mParam,
				HistoryCode.SYMPATHY_REGISTED,
				item.getActor().getUserId(),
				item.getTitle(),
				UserService.getUser(),
				SYMPATHY_ALIMI,
				null,
				ALIMI_WORK,
				item,
				item.getId());
		}
		WorkItemHistoryManager.history(WorkItemHistory.ADD_SYMPATHY, item);
		HistoryManager.history(HistoryCode.SYMPATHY, item);
	}

	/**
	 * 공감 삭제 시 로그를 남긴다.
	 * @param item
	 * @throws Exception
	 */
	static void deleteSympathy(Work item) throws Exception
	{
		_sympathy.delete(item);
		WorkItemHistoryManager.history(WorkItemHistory.DEL_SYMPATHY, item);
		HistoryManager.history(HistoryCode.SYMPATHY_REMOVED, item);
	}
}