package com.kcube.work.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.kcube.doc.ItemPermission;
import com.kcube.lib.action.Action;
import com.kcube.lib.action.ActionContext;
import com.kcube.lib.jdbc.DbService;
import com.kcube.lib.jdbc.DbStorage;
import com.kcube.lib.jdbc.TableState;
import com.kcube.lib.json.JsonMapping;
import com.kcube.lib.json.JsonWriter;
import com.kcube.lib.sql.SqlPage;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.lib.sql.SqlTable;
import com.kcube.lib.sql.SqlWriter;
import com.kcube.sys.emp.EmployeeService;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.User;
import com.kcube.sys.usr.UserPermission;
import com.kcube.sys.usr.UserService;
import com.kcube.work.Work;
import com.kcube.work.WorkAction;
import com.kcube.work.WorkAttachmentManager;
import com.kcube.work.WorkHistory;
import com.kcube.work.WorkPermission;
import com.kcube.work.WorkProcessList;
import com.kcube.work.WorkReportFileManager;
import com.kcube.work.process.WorkProcess.ProcessComment;

import net.sf.json.JSONObject;

/**
 * @author 신운재
 *         <P>
 *         업무 보고 Action class
 */
public class WorkProcessAction
{
	private static final SqlTable WORKPROCESS = new SqlTable("WORK_ITEM_PRCS", "i");
	private static SqlWriter _writer = new SqlWriter().putAll(WORKPROCESS);
	private static WorkProcessManager manager = new WorkProcessManager();
	static DbStorage _workProcessStorage = new DbStorage(WorkProcess.class);
	static DbStorage _processCommStorage = new DbStorage(ProcessComment.class);

	static JsonMapping _factory = new JsonMapping(Work.class, "read");
	static JsonMapping _reviewerFactory = new JsonMapping(Work.class, "reviewer");
	private static Map<String, String> COLUMNS = new HashMap<String, String>();
	private static Map<String, String> ATTRIBUTES = new HashMap<String, String>();
	static
	{
		COLUMNS.put("id", "userid");
		COLUMNS.put("name", "user_name");
		COLUMNS.put("displayName", "user_disp");
		COLUMNS.put("isHelper", "ISHELPER");

		ATTRIBUTES.put("userid", "id");
		ATTRIBUTES.put("user_name", "name");
		ATTRIBUTES.put("user_disp", "displayName");
		ATTRIBUTES.put("ISHELPER", "isHelper");
	}

	/**
	 * 선택된 임원에 대한 보고 업무를 내려줌.
	 * @author WJ
	 */
	public static class ProcessListBySelectedOffc implements Action
	{
		@Override
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long userId = ctx.getLong("userId");
			boolean orderDesc = ctx.getBoolean("orderDesc");
			User user = EmployeeService.getEmployee(userId).getUser();
			SqlSelect stmt = manager.getApprovalProcessListByOffc(user, mp);
			if (orderDesc)
			{
				_writer.setOrder("req_date DESC");
			}
			else
			{
				_writer.setOrder("req_date ASC");
			}
			_writer.page(ctx.getWriter(), stmt, stmt, ctx.getParameter("ts"));
		}
	}

	/**
	 * 보고업무의 담당자를 내려줌.
	 * @author WJ
	 */
	public static class GetActor implements Action
	{
		@Override
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long[] ids = ctx.getLongTokens("id");
			int length = ids.length;
			JSONObject obj = new JSONObject();

			for (int i = 0; i < length; i++)
			{
				WorkProcess prcs = (WorkProcess) _workProcessStorage.load(ids[i]);
				User actor = prcs.getItem().getActor();
				obj.put("actor", actor);
			}

			ctx.getWriter().print(obj);
		}
	}

	/**
	 * 보고업무의 담당자를 내려줌.
	 * @author WJ
	 */
	public static class GetRelatedEmployee implements Action
	{
		@Override
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);
			TableState ts = new TableState(ctx.getParameter("ts"), COLUMNS);
			Long prcsId = ctx.getLong("prcsId");
			WorkProcess prcs = (WorkProcess) _workProcessStorage.load(prcsId);
			Long workId = prcs.getItem().getId();

			ResultSet rs = getUserList(ts, workId);
			int totalRows = getUserCount(ts, workId);

			final JsonWriter writer = new JsonWriter(ctx.getWriter());
			writer.writeListHeader(totalRows);
			while (rs.next())
			{
				writer.startList();
				writer.setFirstAttr(true);
				writer.writeRow(rs, ATTRIBUTES);
				writer.endList();
			}
			writer.writeListFooter();
		}

		private int getUserCount(TableState ts, Long workId) throws Exception
		{
			StringBuffer query = getQuery(true);
			ts.appendWhere(query);

			PreparedStatement pstmt = DbService.prepareStatement(query.toString());
			pstmt.setLong(1, workId);
			pstmt.setLong(2, workId);
			ts.bindSearch(pstmt, 3);

			ResultSet rs = pstmt.executeQuery();
			rs.next();

			return rs.getInt(1);
		}

		private ResultSet getUserList(TableState ts, Long workId) throws Exception
		{
			StringBuffer query = getQuery(false);

			ts.appendWhere(query);
			ts.appendRownum(query);
			ts.appendOrderBy(query, "ISHELPER,SEQ_ORDER");

			PreparedStatement pstmt = DbService.prepareStatement(query.toString());

			pstmt.setLong(1, workId);
			pstmt.setLong(2, workId);

			int index = ts.bindSearch(pstmt, 3);
			index = ts.bindRownum(pstmt, index);
			return pstmt.executeQuery();
		}

		private StringBuffer getQuery(boolean isCount)
		{
			StringBuffer query = new StringBuffer();
			if (isCount)
			{
				query.append(" SELECT COUNT(A.USERID)");
			}
			else
			{
				query.append(" SELECT A.*");
			}
			query.append(" 			FROM");
			query.append(" 			  (SELECT ACTOR_USERID AS USERID,");
			query.append(" 			    ACTOR_NAME         AS USER_NAME,");
			query.append(" 			    ACTOR_DISP         AS USER_DISP,");
			query.append(" 			    0                  AS ISHELPER,");
			query.append(" 			    0                  AS SEQ_ORDER");
			query.append(" 			  FROM WORK_ITEM");
			query.append(" 			  WHERE ITEMID = ?");
			query.append(" 			  UNION");
			query.append(" 			  SELECT USERID,");
			query.append(" 			    USER_NAME,");
			query.append(" 			    USER_DISP,");
			query.append(" 			    1 AS ISHELPER,");
			query.append(" 			    SEQ_ORDER+1 AS SEQ_ORDER");
			query.append(" 			  FROM WORK_ITEM_HELPER");
			query.append(" 			  WHERE ITEMID = ?");
			query.append(" 			  ) A ");
			return query;
		}
	}

	/**
	 * 로그인한 대상자에 대한 보고업무에 대한 목록을 돌려준다.
	 * @author WJ
	 */
	public static class ProcessListByChecker implements Action
	{
		@Override
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			int status = ctx.getInt("status", -1);
			SqlSelect stmt = manager.getProcessListByChecker(UserService.getUser(), mp);
			if (status > 0)
			{
				stmt.where("i.status = ? ", status);
				stmt.where("i.isretrieved = ?", false);
			}
			stmt.where("i.status != ? ", WorkProcess.APPROVAL_CMPLT_SELF_STATUS);
			_writer.setOrder("req_date DESC");
			_writer.page(ctx.getWriter(), stmt, stmt, ctx.getParameter("ts"));
		}
	}

	/**
	 * 검토 요청을 한다 작성자 / 협업자가 검토요청을 보냄.
	 * @author WJ
	 */
	public static class RequestReviewByWriter implements Action
	{
		@Override
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long id = ctx.getLong("id");
			Work server = (Work) DbService.loadWithLock(Work.class, id);
			// 글에 권한이 없으면 예외
			ItemPermission.checkUser(server, mp);
			// 담당자 이거나 협업자인 경우가 아니면 예외
			WorkPermission.checkReview(server);
			// 작업중 상태가 아니면 예외
			WorkPermission.checkWorkingStatus(server);
			// 파일중 체크아웃 되어있는 파일이 있으면 익셉션
			WorkPermission.checkFileLockIn(server);

			long gid = 0L;
			ArrayList<WorkProcess> prcss = new ArrayList<WorkProcess>();
			for (int i = 0; i < server.getReviewers().size(); i++)
			{
				WorkProcess prcs = (WorkProcess) _workProcessStorage.create();

				if (gid == 0L)
					gid = prcs.getId();
				User reviewer = server.getReviewers().get(i);

				prcs.setGid(gid);
				prcs.setChecker(reviewer);

				WorkProcessManager.setReviewProcess(server, prcs);
				WorkProcessManager.setModuleParam(server, prcs);

				prcss.add(prcs);
				DbService.commit();
			}
			WorkProcessManager.reviewWorkUpdated(server, gid);
			WorkHistory.requestedReview(server, prcss);

			JSONObject obj = new JSONObject();
			obj.put("id", id);
			ctx.getWriter().write(obj.toString());
		}
	}

	/**
	 * 승인 요청을 한다.
	 * <p>
	 * 작성자 / 협업자가 승인 요청을 보냄.
	 * @author WJ
	 */
	public static class RequestApprovalByWriter implements Action
	{
		@Override
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long id = ctx.getLong("id");
			Work server = (Work) DbService.loadWithLock(Work.class, id);

			// 글에 권한이 없으면 예외
			ItemPermission.checkUser(server, mp);
			// 담당자 아니면 예외
			WorkPermission.checkReview(server);
			// 작업중 상태가 아니면 예외
			// 상태값 중 검토완료 추가
			WorkPermission.checkApprableStatus(server);
			// 파일중 체크아웃 되어있는 파일이 있으면 익셉션
			WorkPermission.checkFileLockIn(server);

			WorkProcess prcs = (WorkProcess) _workProcessStorage.create();

			WorkProcessManager.setApproveProcess(server, prcs, false);
			WorkProcessManager.setModuleParam(server, prcs);
			WorkProcessManager.approveWorkUpdated(server, prcs, false);
			WorkHistory.requestedApproval(server, prcs);
			DbService.flush();

			JSONObject obj = new JSONObject();
			obj.put("id", id);
			ctx.getWriter().write(obj.toString());
		}
	}

	/**
	 * 자가승인 한다.
	 * @author WJ
	 */
	public static class ApprovalSelfByActor extends WorkAction
	{
		@Override
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long id = ctx.getLong("id");
			Work server = (Work) DbService.loadWithLock(Work.class, id);

			// 글에 권한이 없으면 예외
			ItemPermission.checkUser(server, mp);
			// 담당자 아니면 예외
			WorkPermission.checkReview(server);
			// 작업중 상태가 아니면 예외
			WorkPermission.checkWorkingStatus(server);
			WorkPermission.checkSelfApproval(server);
			// 파일중 체크아웃 되어있는 파일이 있으면 익셉션
			WorkPermission.checkFileLockIn(server);

			WorkProcess prcs = (WorkProcess) _workProcessStorage.create();

			WorkProcessManager.setApproveProcess(server, prcs, true);
			WorkProcessManager.setModuleParam(server, prcs);
			WorkProcessManager.approveWorkUpdated(server, prcs, true);
			DbService.flush();

			_listener.complete(server);
			WorkHistory.completedApproval(mp, server);

			JSONObject obj = new JSONObject();
			obj.put("id", id);
			obj.put("status", server.getStatus());

			ctx.getWriter().print(obj);
		}
	}

	/**
	 * 보고 업무 리스트 화면에서 나머지 데이터를 로드한다.
	 * @author WJ
	 */
	public static class GetRestData implements Action
	{
		@Override
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			WorkProcess prcs = (WorkProcess) _workProcessStorage.load(ctx.getLong("id"));
			if (!prcs.isRead())
			{
				prcs.setEventDate(new Date());
				prcs.setRead(true);
			}

			Work server = prcs.getItem();
			// 글에 권한이 없으면 예외
			ItemPermission.checkUser(server, mp);

			Long itemId = server.getId();
			Work fake = new Work();
			fake.setWorkAttachments(WorkAttachmentManager.getLVAttachmentSetByItemId(itemId));
			fake.setReportFiles(WorkReportFileManager.getLVReportSetByItemId(itemId));
			fake.setWorkReferences(server.getWorkReferences());

			_factory.marshal(ctx.getWriter(), fake);
		}
	}

	/**
	 * 프로세스 히스토리 목록을 가져온다.
	 */
	public static class ProcessHistory implements Action
	{
		@SuppressWarnings("unchecked")
		@Override
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long itemId = ctx.getLong("itemId");
			SqlPage page = new SqlPage(null, ctx.getParameter("ts"));

			List<WorkProcess> prcsList = WorkProcessManager.getProcessByItemId(itemId, page.max(), page.skip());

			WorkProcessList list = new WorkProcessList();
			list.setProcessList(prcsList);

			_factory.marshal(ctx.getWriter(), list);
		}
	}

	/**
	 * 검토를 완료한다.
	 * @author WJ
	 */
	public static class CompleteReviewByReviewer implements Action
	{
		@Override
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long[] ids = ctx.getLongTokens("id");
			for (int i = 0; i < ids.length; i++)
			{
				DbService.begin();
				WorkProcess prcs = (WorkProcess) _workProcessStorage.load(ids[i]);
				String comm = ctx.getParameter("comment");

				// 글에 권한이 없으면 예외
				ItemPermission.checkUser(prcs.getItem(), mp);
				WorkPermission.checkReviewer(prcs.getItem());
				WorkPermission.checkStatus(prcs);
				WorkPermission.alreadyDeleted(prcs.getItem());
				// 파일중 체크아웃 되어있는 파일이 있으면 익셉션
				WorkPermission.checkFileLockIn(prcs.getItem());

				@SuppressWarnings("rawtypes")
				List prcsList = WorkProcessManager.getProcessByGid(prcs.getGid());
				if (isCompleteReview(prcsList, prcs))
				{
					prcs.getItem().setStatus(Work.REVIEW_CMPLT_STATUS);
				}
				prcs.setEventDate(new Date());
				prcs.setCmpltDate(new Date());

				ProcessComment pc = (ProcessComment) _processCommStorage.create();
				pc.setComment(comm);
				pc.setStatus(WorkProcess.REVIEW_CMPLT_STATUS);
				pc.setInstDate(new Date());
				pc.setPrcsId(prcs.getId());

				prcs.setStatus(WorkProcess.REVIEW_CMPLT_STATUS);
				prcs.setResContent(comm);
				WorkHistory.processedReview(mp, prcs.getItem());
				if (prcs.getItem().getStatus() == Work.REVIEW_CMPLT_STATUS)
				{
					WorkHistory.completedReview(mp, prcs.getItem());
				}
				DbService.commit();
			}
		}

		@SuppressWarnings("rawtypes")
		private boolean isCompleteReview(List prcsList, WorkProcess prcs)
		{
			for (int i = 0; i < prcsList.size(); i++)
			{
				WorkProcess group = (WorkProcess) prcsList.get(i);
				if (!group.equals(prcs) && group.getStatus() != WorkProcess.REVIEW_CMPLT_STATUS)
					return false;
			}
			return true;
		}
	}

	/**
	 * 승인을 완료한다.
	 * @author WJ
	 */
	public static class CompleteApprovalByReviewer extends WorkAction
	{
		@Override
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long[] ids = ctx.getLongTokens("id");
			for (int i = 0; i < ids.length; i++)
			{
				DbService.begin();
				WorkProcess prcs = (WorkProcess) _workProcessStorage.load(ids[i]);
				String comm = ctx.getParameter("comment");

				// 글에 권한이 없으면 예외
				ItemPermission.checkUser(prcs.getItem(), mp);
				WorkPermission.checkApprover(prcs.getItem());
				WorkPermission.checkFileLockIn(prcs.getItem());
				WorkPermission.alreadyDeleted(prcs.getItem());

				prcs.getItem().setStatus(Work.APRV_CMPLT_STATUS);
				prcs.getItem().setCompleteDate(new Date());
				prcs.setEventDate(new Date());
				prcs.setCmpltDate(new Date());

				ProcessComment pc = (ProcessComment) _processCommStorage.create();
				pc.setComment(comm);
				pc.setStatus(WorkProcess.APPROVAL_CMPLT_STATUS);
				pc.setInstDate(new Date());
				pc.setPrcsId(prcs.getId());

				prcs.setStatus(WorkProcess.APPROVAL_CMPLT_STATUS);
				prcs.setResContent(comm);
				WorkHistory.completedApproval(mp, prcs.getItem());

				_listener.complete(prcs.getItem());
				DbService.commit();
			}
		}
	}

	/**
	 * 보완요청한다.
	 * @author WJ
	 */
	public static class NeedSuppleResponseByReviewer implements Action
	{
		@Override
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			WorkProcess prcs = (WorkProcess) _workProcessStorage.load(ctx.getLong("id"));
			String comm = ctx.getParameter("comment");
			int status = ctx.getInt("status");

			// 글에 권한이 없으면 예외
			ItemPermission.checkUser(prcs.getItem(), mp);
			WorkPermission.checkApprover(prcs.getItem());
			WorkPermission.checkFileLockIn(prcs.getItem());
			WorkPermission.alreadyDeleted(prcs.getItem());

			prcs.getItem().setStatus(Work.NEED_SUPPLEMENT_STATUS);
			prcs.setEventDate(new Date());
			prcs.setCmpltDate(new Date());

			ProcessComment pc = (ProcessComment) _processCommStorage.create();
			pc.setComment(comm);
			pc.setStatus(status);
			pc.setInstDate(new Date());
			pc.setPrcsId(prcs.getId());

			prcs.setStatus(status);
			prcs.setResContent(comm);

			WorkHistory.requestedNeedSupplement(prcs.getItem());
		}
	}

	/**
	 * 대면요청 한다.
	 * @author WJ
	 */
	public static class NeedOffMeetingByReviewer implements Action
	{
		@Override
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			WorkProcess prcs = (WorkProcess) _workProcessStorage.load(ctx.getLong("id"));
			String comm = ctx.getParameter("comment");

			// 글에 권한이 없으면 예외
			ItemPermission.checkUser(prcs.getItem(), mp);
			WorkPermission.checkMeetingRequest(prcs.getItem());
			WorkPermission.alreadyDeleted(prcs.getItem());

			ProcessComment pc = (ProcessComment) _processCommStorage.create();
			pc.setComment(comm);
			pc.setStatus(WorkProcess.NEED_OFFMEETING_STATUS);
			pc.setInstDate(new Date());
			pc.setPrcsId(prcs.getId());

			prcs.setStatus(WorkProcess.NEED_OFFMEETING_STATUS);
			prcs.setEventDate(new Date());
			prcs.setResContent(comm);
			prcs.setMeetingReq(true);

			WorkHistory.requestedNeedMeeting(prcs.getItem());
		}
	}

	/**
	 * 검토/승인 요청을 회수한다.
	 * @author WJ
	 */
	public static class RetrieveRequestByWriter implements Action
	{
		@Override
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Work server = (Work) DbService.load(Work.class, ctx.getLong("id"));

			// 담당자 이거나 협업자인 경우가 아니면 예외
			ItemPermission.checkUser(server, mp);
			WorkPermission.checkRequester(server);
			WorkPermission.checkRetrieve(server);

			int beforeStatus = server.getStatus();

			@SuppressWarnings("unchecked")
			List<WorkProcess> prcsList = WorkProcessManager.getProcessByGid(server.getLastProcessGid());

			ArrayList<WorkProcess> prcss = new ArrayList<WorkProcess>();
			DbService.begin();
			for (WorkProcess group : prcsList)
			{
				WorkProcess load = (WorkProcess) DbService.loadWithLock(group);
				load.setRetrieve(true);
				load.setEventDate(new Date());
				prcss.add(load);
			}
			/**
			 * 회수이력 남김.
			 */
			WorkProcess retrieve = (WorkProcess) _workProcessStorage.create();
			retrieve.setChecker(UserService.getUser());
			retrieve.setEventDate(new Date());
			retrieve.setCmpltDate(new Date());
			retrieve.setProcessType(WorkProcess.RETRIEVE_TYPE);
			retrieve.setStatus(WorkProcess.RETRIEVE_STATUS);
			retrieve.setItem(server);
			retrieve.setAppId(server.getAppId());
			retrieve.setClassId(server.getClassId());
			retrieve.setModuleId(server.getModuleId());
			retrieve.setSpaceId(server.getSpaceId());
			server.setStatus(Work.WORKING_STATUS);
			DbService.commit();

			WorkHistory.requestedRetrieve(server, prcss, beforeStatus);
			ctx.setParameter("id", server.getId().toString());
		}
	}

	/**
	 * 검토/승인 요청된 업무방을 조회한다.
	 * @author WJ
	 */
	public static class ReadByReviewer implements Action
	{
		@Override
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			WorkProcess prcs = (WorkProcess) _workProcessStorage.load(ctx.getLong("prcsId"));
			if (!prcs.isRead())
			{
				prcs.setEventDate(new Date());
				prcs.setRead(true);
			}
			// 담당자 이거나 협업자인 경우가 아니면 예외
			ItemPermission.checkUser(prcs.getItem(), mp);
			WorkPermission.checkReviewerApprover(prcs.getItem());
			WorkPermission.checkUser(mp, prcs.getItem());
			/* 조회 로그 처리 */
			WorkHistory.read(prcs.getItem());
			TreeSet<WorkProcess> set = new TreeSet<WorkProcess>();
			set.add(prcs);
			prcs.getItem().setWorkProcess(set);
			prcs.getItem().setReportFiles(WorkReportFileManager.getLVReportSetByItemId(prcs.getItem().getId()));
			_reviewerFactory.marshal(ctx.getWriter(), prcs.getItem());
		}
	}
}
