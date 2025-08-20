package com.kcube.work.process;

import java.util.Date;
import java.util.List;

import org.hibernate.query.Query;

import com.kcube.lib.jdbc.DbService;
import com.kcube.lib.sql.Sql;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.lib.sql.SqlTable;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.User;
import com.kcube.sys.usr.UserService;
import com.kcube.work.Work;

/**
 * @author 신운재
 *         <P>
 *         업무 보고 Action class
 */
public class WorkProcessManager
{
	private static final SqlTable WORKPROCESS = new SqlTable("WORK_ITEM_PRCS", "i");
	public static final int[] LISTTYPE = {WorkProcess.REVIEW_TYPE, WorkProcess.APPROVAL_TYPE};

	/**
	 * 보고 대상자의 검토 와 승인 프로세스에 대한 목록을 돌려준다.
	 * @param user
	 * @return
	 */
	public SqlSelect getProcessListByChecker(User user, ModuleParam mp)
	{
		SqlSelect stmt = new SqlSelect();
		stmt.select(WORKPROCESS, "list");
		stmt.where("checker_userid = ?", user.getUserId());
		stmt.where(new Sql.InIntArray("prcs_type", LISTTYPE));
		stmt.where("isvisb = ?", true);
		stmt.where("classid = ?", mp.getClassId());
		stmt.where("appid = ?", mp.getAppId());
		stmt.where("moduleid = ?", mp.getModuleId());
		return stmt;
	}

	public SqlSelect getProcessCountByChecker(User user, ModuleParam mp)
	{
		SqlSelect stmt = new SqlSelect();
		stmt.from(WORKPROCESS);
		stmt.where("checker_userid = ?", user.getUserId());
		stmt.where(new Sql.InIntArray("prcs_type", LISTTYPE));
		stmt.where("isvisb = ?", true);
		stmt.where("classid = ?", mp.getClassId());
		stmt.where("appid = ?", mp.getAppId());
		stmt.where("moduleid = ?", mp.getModuleId());
		return stmt;
	}

	/**
	 * @param user
	 * @param mp
	 * @return
	 */
	public SqlSelect getApprovalProcessListByOffc(User user, ModuleParam mp)
	{
		SqlSelect stmt = new SqlSelect();
		stmt.select(WORKPROCESS, "list");
		stmt.where("checker_userid = ?", user.getUserId());
		stmt.where("prcs_type = ? ", WorkProcess.APPROVAL_TYPE);
		stmt.where("isvisb = ?", true);
		stmt.where("cmplt_date IS NULL");
		stmt.where("isretrieved = ?", false);
		stmt.where("status != ? ", WorkProcess.APPROVAL_CMPLT_SELF_STATUS);
		stmt.where("classid = ?", mp.getClassId());
		stmt.where("appid = ?", mp.getAppId());
		stmt.where("moduleid = ?", mp.getModuleId());
		return stmt;
	}

	@SuppressWarnings("rawtypes")
	public static List getProcessByGid(Long gid) throws Exception
	{
		Query query = DbService.getSession().getNamedQuery("getProcessByGid");
		query.setParameter("gid", gid);
		return query.list();
	}

	@SuppressWarnings("rawtypes")
	public static List getProcessByItemId(Long itemid, int to, int from) throws Exception
	{
		Query query = DbService.getSession().getNamedQuery("getProcessByItemId");
		query.setParameter("itemid", itemid);
		query.setFirstResult(from);
		query.setMaxResults(to);

		return query.list();
	}

	/**
	 * 검토 요청이 끝났을때
	 * @param work
	 * @param prcs
	 * @param isComplete
	 */
	public static void reviewWorkUpdated(Work work, long gid)
	{
		work.setLastUpdt(new Date());
		work.setLastProcessGid(gid);
		setWorkReview(work);
	}

	/**
	 * 승인 요청이 끝났을때, 업무방 업데이트
	 * @param work
	 * @param prcs
	 * @param isComplete
	 */
	public static void approveWorkUpdated(Work work, WorkProcess prcs, boolean isComplete)
	{
		work.setLastProcessGid(prcs.getId());
		work.setLastUpdt(new Date());
		if (isComplete)
		{
			setWorkComplete(work);
		}
		else
		{
			setWorkApprove(work);
		}
	}

	/**
	 * 업무방 완료상태
	 * @param work
	 */
	public static void setWorkComplete(Work work)
	{
		work.setStatus(Work.APRV_CMPLT_STATUS);
		work.setCompleteDate(new Date());
	}

	/**
	 * 업무방 승인상태
	 * @param work
	 */
	public static void setWorkApprove(Work work)
	{
		work.setStatus(Work.APPROVAL_STATUS);
	}

	/**
	 * 업무방 검토상태
	 * @param work
	 */
	public static void setWorkReview(Work work)
	{
		work.setStatus(Work.REVIEW_STATUS);
	}

	/**
	 * 프로세스 히스토리 목록을 가져온다.
	 */
	public SqlSelect getProcessHistory(long itemId)
	{
		SqlSelect stmt = new SqlSelect();
		stmt.select(WORKPROCESS, "list");
		stmt.where("itemid = ?", itemId);
		stmt.order("prcsid desc, req_date desc");
		return stmt;
	}

	/**
	 * 모듈 파람 저장
	 * @param server
	 * @param prcs
	 */
	public static void setModuleParam(Work server, WorkProcess prcs)
	{
		prcs.setAppId(server.getAppId());
		prcs.setClassId(server.getClassId());
		prcs.setModuleId(server.getModuleId());
		prcs.setSpaceId(server.getSpaceId());
	}

	/**
	 * 프로세스 기본 데이터 저장
	 * @param server
	 * @param prcs
	 */
	public static void setProcessCommon(Work server, WorkProcess prcs, boolean isApprove)
	{
		prcs.setInChage(UserService.getUser());
		prcs.setContent(server.getReportSketch());
		prcs.setTitle(server.getTitle());
		prcs.setReqDate(new Date());
		prcs.setVisible(true);
		prcs.setItem(server);
		if (isApprove)
		{
			prcs.setGid(prcs.getId());
		}
		processTypeSetting(prcs, isApprove);
	}

	public static void processTypeSetting(WorkProcess prcs, boolean isApprove)
	{
		if (isApprove)
		{
			prcs.setProcessType(WorkProcess.APPROVAL_TYPE);
		}
		else
		{
			prcs.setProcessType(WorkProcess.REVIEW_TYPE);
		}
	}

	/**
	 * 자가승인 데이터 저장
	 * @param prcs
	 */
	public static void setSelfApproveData(WorkProcess prcs)
	{
		prcs.setChecker(UserService.getUser());
		prcs.setStatus(WorkProcess.APPROVAL_CMPLT_SELF_STATUS);
		prcs.setCmpltDate(new Date());
	}

	/**
	 * 승인 프로세스
	 * @param prcs
	 * @param work
	 */
	public static void setApproveData(WorkProcess prcs, Work work)
	{
		prcs.setChecker(work.getApprover());
		prcs.setStatus(WorkProcess.APPROVAL_STATUS);
	}

	/**
	 * 리뷰 프로세스
	 * @param prcs
	 * @param work
	 */
	public static void setReviewData(WorkProcess prcs)
	{
		prcs.setStatus(WorkProcess.REVIEWING_STATUS);
	}

	/**
	 * 승인 프로세스 저장
	 * @param server
	 * @param prcs
	 * @param isSelf
	 */
	public static void setApproveProcess(Work server, WorkProcess prcs, boolean isSelf)
	{
		setProcessCommon(server, prcs, true);
		if (isSelf)
		{
			setSelfApproveData(prcs);
		}
		else
		{
			setApproveData(prcs, server);
		}
	}

	/**
	 * 승인 프로세스 저장
	 * @param server
	 * @param prcs
	 * @param isSelf
	 */
	public static void setReviewProcess(Work server, WorkProcess prcs)
	{
		setProcessCommon(server, prcs, false);
		setReviewData(prcs);
	}
}