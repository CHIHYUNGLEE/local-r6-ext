package com.kcube.work.plan;

import com.kcube.doc.InvalidStatusException;
import com.kcube.doc.ItemPermission;
import com.kcube.lib.jdbc.DbService;
import com.kcube.sys.AppException;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.PermissionDeniedException;
import com.kcube.work.Work;

/**
 * 업무방 수행계획에 대한 권한 체크 Class
 * @author Soo
 */
public class WorkPlanPermission
{
	/**
	 * 업무방의 수행 계획을 등록할 수 있는 상태 인지 체크한다.
	 */
	public static void checkPlanRegister(Work item) throws Exception
	{
		if (!item.isVisible() && (item.getStatus() != Work.DRAFT_STATUS))
		{
			throw new InvalidStatusException();
		}

		if (item.isVisible() && (item.getStatus() != Work.WORKING_STATUS))
		{
			throw new InvalidStatusException();
		}

		if (!item.isCurrentHelper() && !item.isCurrentActor())
		{
			throw new PermissionDeniedException();
		}
	}

	/**
	 * 업무방의 수행 계획을 수정할 수 있는 상태 인지 체크한다.
	 */
	public static void checkPlanUpdate(Work item) throws Exception
	{
		if (!item.isVisible() && (item.getStatus() != Work.DRAFT_STATUS))
		{
			throw new InvalidStatusException();
		}

		if (item.isVisible() && (item.getStatus() != Work.WORKING_STATUS))
		{
			throw new InvalidStatusException();
		}

		if (!item.isCurrentHelper() && !item.isCurrentActor())
		{
			throw new PermissionDeniedException();
		}
	}

	/**
	 * 해당 문서의 담당자인지를 체크한다.
	 */
	public static void checkActor(WorkPlan plan) throws Exception
	{
		Work work = (Work) DbService.load(Work.class, plan.getWorkId());
		checkActor(work, plan);
	}

	/**
	 * 해당 문서의 담당자인지를 체크한다.
	 */
	public static void checkActor(Work work, WorkPlan plan) throws Exception
	{
		if (!work.isCurrentActor() && !work.isCurrentHelper() && !plan.isPlanActor())
		{
			throw new PermissionDeniedException();
		}
	}

	/**
	 * 업무방의 수행 계획을 수정할 수 있는 상태 인지 체크한다.
	 */
	public static void checkPlanStatus(Work item) throws Exception
	{
		if (!item.isVisible() && (item.getStatus() != Work.DRAFT_STATUS))
		{
			throw new InvalidStatusException();
		}

		if (item.isVisible() && (item.getStatus() != Work.WORKING_STATUS))
		{
			throw new InvalidStatusException();
		}
	}

	/**
	 * 현재 사용자가 첨부 조회권한이 있는지 확인한다.
	 * @throws Exception
	 */
	public static void checkAttachUser(WorkPlan item, ModuleParam mp) throws Exception
	{
		if (!ItemPermission.isAttachUser(item, mp))
		{
			throw new ReadDeniedException();
		}
	}

	/**
	 * 수행계획이 상태변경 불가 오류
	 */
	public static class SubPlanException extends AppException
	{
		private static final long serialVersionUID = 770192764546389849L;
	}

	/**
	 * 게시글 조회 권한이 없을때 발생한다.
	 */
	public static class ReadDeniedException extends AppException
	{
		private static final long serialVersionUID = -2265294198587674877L;
	}

}