package com.kcube.work;

import com.kcube.lib.action.ActionService;
import com.kcube.lib.event.EventService;
import com.kcube.sys.AppBoot;
import com.kcube.sys.webdav.WebDavSpring;
import com.kcube.work.chat.WorkChatEvent;
import com.kcube.work.chat.WorkChatSocketEvent;
import com.kcube.work.chat.WorkChatUser;
import com.kcube.work.chat.item.WorkChatItemEvent;
import com.kcube.work.chat.item.WorkChatItemOwner;
import com.kcube.work.chat.item.WorkChatItemUser;
import com.kcube.work.grp.WorkMyGroupUser;
import com.kcube.work.history.WorkItemHistoryUser;
import com.kcube.work.menu.WorkMenuUser;
import com.kcube.work.plan.WorkPlanActor;
import com.kcube.work.plan.WorkPlanUser;
import com.kcube.work.process.WorkProcessAction;
import com.kcube.work.recent.WorkRecentEvent;
import com.kcube.work.recent.WorkRecentUser;
import com.kcube.work.report.ReportTemplateAdmin;
import com.kcube.work.report.ReportTemplateUser;
import com.kcube.work.request.share.WorkShareRequestUser;
import com.kcube.work.trans.WorkTransferReq;
import com.kcube.work.trans.WorkTransferRes;

/**
 * @author 김경수
 *         <p>
 *         업무방 action 등록 class
 */
public class WorkBoot implements AppBoot
{
	@Override
	public void init() throws Exception
	{
		/**
		 * 업무방 관련
		 */
		ActionService.addAction(new WorkUser());
		ActionService.addAction(new WorkOwner());
		ActionService.addAction(new WorkOpinion());
		ActionService.addAction(new WorkRecentUser());
		ActionService.addAction(new WorkActor());
		ActionService.addAction(new WorkPortlet());

		EventService.addListener(new WorkEvent());
		EventService.addListener(new WorkRecentEvent());
		EventService.addListener(new WorkChatSocketEvent());

		ActionService.addAction(new WorkJob());

		WebDavSpring.addWebDavFile(WorkHistory.ALIMI_WORK_REPORT, WorkReportWebDavFile.class);
		/**
		 * 업무방 수행계획 관련
		 */
		ActionService.addAction(new WorkPlanUser());
		ActionService.addAction(new WorkPlanActor());

		/**
		 * 업무방 공유 요청 관련
		 */
		ActionService.addAction(new WorkShareRequestUser());

		/**
		 * @author 성재호
		 *         <p>
		 *         업무방 보고서 템플릿 관련
		 */
		ActionService.addAction(new ReportTemplateAdmin());

		/**
		 * @author 신운재
		 *         <p>
		 *         업무방 붙임파일 버전관리 액션
		 */
		ActionService.addAction(new WorkAttachmentVersioning());
		ActionService.addAction(new WorkReportFileVersioning());

		/**
		 * @author 신운재
		 *         <p>
		 *         업무방 보고서 관련
		 */
		ActionService.addAction(new ReportTemplateUser());

		/**
		 * @author 성재호
		 *         <p>
		 *         업무방 환경설정 관련
		 */
		ActionService.addAction(new WorkMenuUser());

		/**
		 * @author 성재호
		 *         <p>
		 *         업무방 나의 업무 분류 관련
		 */
		ActionService.addAction(new WorkMyGroupUser());

		/**
		 * @author 신운재
		 *         <p>
		 *         업무방 참고자료 관련
		 */
		ActionService.addAction(new WorkReferenceAction());

		/**
		 * 업무방 히스토리 관련
		 */
		ActionService.addAction(new WorkItemHistoryUser());

		/**
		 * @author 성재호
		 *         <p>
		 *         업무방 포탈관리자 업무 관리 관련
		 */
		ActionService.addAction(new WorkAdmin());

		/**
		 * 업무방 조회수 로그 관련
		 */
		ActionService.addAction(new WorkViewer());

		/**
		 * 업무방 프로세스 관련
		 */
		ActionService.addAction(new WorkProcessAction());

		/**
		 * 인수인계 Action
		 */
		ActionService.addAction(new WorkTransferReq());
		ActionService.addAction(new WorkTransferRes());

		/**
		 * 업무 채팅방 관련
		 */
		EventService.addListener(new WorkChatEvent());
		EventService.addListener(new WorkChatItemEvent());
		ActionService.addAction(new WorkChatUser());
		ActionService.addAction(new WorkChatItemUser());
		ActionService.addAction(new WorkChatItemOwner());
	}

	@Override
	public void destroy()
	{

	}

}
