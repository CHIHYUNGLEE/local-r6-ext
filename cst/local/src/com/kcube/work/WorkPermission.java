package com.kcube.work;

import java.util.Set;
import java.util.TreeSet;

import com.kcube.doc.InvalidStatusException;
import com.kcube.doc.Item;
import com.kcube.doc.ItemPermission;
import com.kcube.doc.file.Attachment;
import com.kcube.sys.conf.module.ModuleConfigService;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.PermissionDeniedException;
import com.kcube.sys.usr.UserService;
import com.kcube.work.WorkFileException.WorkFileCheckedOut;
import com.kcube.work.WorkFileException.WorkUploadDeniedException;
import com.kcube.work.plan.WorkPlanPermission;
import com.kcube.work.plan.WorkPlanSql;
import com.kcube.work.process.WorkProcess;
import com.kcube.work.request.share.WorkShareRequest;
import com.kcube.work.request.share.WorkShareRequestPermission;

/**
 * 업무방 권한 관리 class
 * @author shin8901
 */
public class WorkPermission
{
	/**
	 * 해당 업무방의 담당자인지를 체크한다.
	 */
	public static void checkActor(Work item) throws Exception
	{
		if (!item.isCurrentActor())
		{
			throw new PermissionDeniedException();
		}
	}

	/**
	 * 해당 업무방의 협업자인지를 체크한다.
	 */
	public static void checkHelper(Work item) throws Exception
	{
		if (!item.isCurrentHelper())
		{
			throw new PermissionDeniedException();
		}
	}

	/**
	 * 업무방을 상태를 수정할 수 있는 상태 인지 체크한다.
	 */
	public static void checkStatusUpdate(Work item) throws Exception
	{
		// 업무방이 보이지 않는 상태 이거나 승인 후 완료 상태 일때 변경 불가함.
		if (!item.isVisible())
		{
			throw new InvalidStatusException();
		}

		if (item.getStatus() == Work.COMPLETE_STATUS
			|| item.getStatus() == Work.APPROVAL_STATUS
			|| item.getStatus() == Work.REVIEW_STATUS)
		{
			throw new InvalidStatusException();
		}

		if (!item.isCurrentHelper() && !item.isCurrentActor())
		{
			throw new PermissionDeniedException();
		}
	}

	/**
	 * 업무의 수행계획이 모두 처리되었는지 확인한다.
	 * @param item
	 * @param status
	 * @throws Exception
	 */
	public static void checkCompletePlan(Work item, int status) throws Exception
	{
		if (status == Work.COMPLETE_STATUS)
		{
			WorkPlanSql sql = new WorkPlanSql(item.getId());
			if (sql.checkPlanIngStatus((long) 0, -1))
			{
				throw new WorkPlanPermission.SubPlanException();
			}
		}
	}

	/**
	 * 업무방을 수정할 수 있는 상태 인지 체크한다.
	 */
	public static void checkUpdate(Work item) throws Exception
	{
		// 작성중일 경우에만 수정할 수 있도록 한다.
		checkWorkingStatus(item);

		if (!item.isVisible())
		{
			throw new InvalidStatusException();
		}

		if (!item.isCurrentHelper() && !item.isCurrentActor())
		{
			throw new PermissionDeniedException();
		}
	}

	/**
	 * 관리자가 업무방을 수정할 수 있는 상태 인지 체크한다.
	 */
	public static void checkUpdateByAdmin(Work item) throws Exception
	{
		if (!item.isVisible())
		{
			throw new InvalidStatusException();
		}
	}

	/**
	 * 업무방을 삭제할 수 있는 사용자인지 체크한다.
	 */
	public static void checkDelete(Work item) throws Exception
	{
		if (!item.isCurrentHelper() && !item.isCurrentActor())
		{
			throw new PermissionDeniedException();
		}
	}

	/**
	 * 업무방이 삭제된 상태 인지 체크하여 삭제할 수 있는지 체크한다.
	 */
	public static void alreadyDeleted(Work item) throws Exception
	{
		if (item.getStatus() == Item.DELETED_STATUS)
		{
			throw new InvalidStatusException();
		}
	}

	/**
	 * 현재 사용자가 업무방 조회권한이 있는지 확인한다.
	 */
	public static void checkUser(ModuleParam mParam, Work item) throws Exception
	{
		if (!ItemPermission.isUser(item, mParam))
		{
			WorkShareRequest req = new WorkShareRequest();
			req.setWorkId(item.getId());
			req.setReqUser(UserService.getUser());
			WorkShareRequestPermission.checkDuplicateRequest(req);
			throw new ReadDeniedException();
		}
	}

	/**
	 * 업무방 첨부(보고서, 붙임파일)체크아웃이 가능한지 권한체크
	 * @param att
	 * @throws Exception
	 */
	public static void doCheckOut(WorkAbstractAttachment att) throws Exception
	{
		if (att.isCheckOut())
		{
			if (att.getChecker().equals(UserService.getUser()))
			{
				return;
			}
			throw new WorkFileException.WorkAlreadyCheckout();
		}
	}

	/**
	 * 업무방 첨부(보고서, 붙임파일)체크아웃 해제가 가능한지 권한체크
	 * @param att
	 * @throws Exception
	 */
	public static void doCancelCheckOut(WorkAbstractAttachment att) throws Exception
	{
		if (att.isCheckOut())
		{
			if (att.getChecker().equals(UserService.getUser()))
			{
				return;
			}
			throw new PermissionDeniedException();
		}
		throw new WorkFileException.WorkFileCheckedOut();
	}

	/**
	 * 체크아웃 상태인지를 점검한다.체크아웃되어 있는 상황이면 예외를 발생한다.
	 * @param att
	 * @throws Exception
	 */
	public static void checkOut(WorkAbstractAttachment att) throws Exception
	{
		if (att.isCheckOut())
		{
			throw new WorkFileException.WorkFileCheckedOut();
		}
	}

	/**
	 * 파일크기를 체크한다.
	 * <p>
	 * (제한 파일크기 < 업무방 총 파일크기 + 현재 파일 크기) 의 경우 Exception을 발생시킴.
	 * @param item
	 * @param itemAtt
	 * @throws WorkUploadDeniedException
	 */
	public static void fileSizeCheck(ModuleParam moduleParam, Work item, Attachment itemAtt) throws Exception
	{
		int totalFileSize = Integer.valueOf(
			ModuleConfigService.getProperty(moduleParam, "com.kcube.work.WorkConfig.workTotalSize")) * (1024 * 1024);
		// MB 단위 곱
		if (totalFileSize < 0)
		{
			// 업무방 파일 제한 없음. 무제한.
			return;
		}
		else if (totalFileSize < (item.getTotalFileSize() + itemAtt.getFilesize()))
		{
			throw new WorkFileException.WorkUploadDeniedException();
		}
	}

	/**
	 * 해당 업무방을 검토요청할수 있는 사용자인지 체크한다.
	 * @param item
	 * @throws Exception
	 */
	public static void checkReview(Work item) throws Exception
	{
		if (!item.isCurrentActor())
		{
			throw new PermissionDeniedException();
		}
	}

	/**
	 * 해당 업무방을 승인요청할수 있는 사용자인지 체크한다.
	 * @param item
	 * @throws Exception
	 */
	public static void checkRequester(Work item) throws Exception
	{
		if (!item.isCurrentActor())
		{
			throw new PermissionDeniedException();
		}
	}

	/**
	 * 승인 할수 있는 사용자인지 체크한다.
	 * @param item
	 * @throws Exception
	 */
	public static void checkApprover(Work item) throws Exception
	{
		if (!item.isCurrentApprover())
		{
			throw new PermissionDeniedException();
		}
	}

	/**
	 * 검토 할수 있는 사용자인지 체크한다.
	 * @param item
	 * @throws Exception
	 */
	public static void checkReviewer(Work item) throws Exception
	{
		if (!item.isCurrentReviewer())
		{
			throw new PermissionDeniedException();
		}
	}

	/**
	 * 검토 할수 있는 사용자인지 체크한다.
	 * @param item
	 * @throws Exception
	 */
	public static void checkReviewerApprover(Work item) throws Exception
	{
		if (!item.isCurrentReviewer() && !item.isCurrentApprover())
		{
			throw new PermissionDeniedException();
		}
	}

	/**
	 * 해당 업무방을 승인요청할수 있는 사용자인지 체크한다.
	 */
	public static void checkWorkingStatus(Work item) throws Exception
	{
		if (item.getStatus() != Work.WORKING_STATUS)
			throw new InvalidStatusException();
	}

	/**
	 * 해당 업무방을 승인요청할수 있는 사용자인지 체크한다.
	 */
	public static void checkApprableStatus(Work item) throws Exception
	{
		if (item.getStatus() != Work.WORKING_STATUS && item.getStatus() != Work.REVIEW_CMPLT_STATUS)
			throw new InvalidStatusException();
	}

	/**
	 * 자가승인 가능여부 체크
	 * @param item
	 * @throws Exception
	 */
	public static void checkSelfApproval(Work item) throws Exception
	{
		if (!isSelfApprove(item))
		{
			throw new InvalidStatusException();
		}
	}

	/**
	 * 해당 업무방을 회수 처리 할 수 있는 상태 인지 체크한다.
	 * <p>
	 * 회수처리는 검토중, 승인중 상태여야 가능하고, 한사람이라도 검토했을 경우 회수 할 수 없다.
	 * @param item
	 * @throws Exception
	 */
	public static void checkRetrieve(Work item) throws Exception
	{
		if (!(item.getStatus() == Work.APPROVAL_STATUS || item.getStatus() == Work.REVIEW_STATUS))
			throw new InvalidStatusException();
	}

	/**
	 * 업무방을 검토/보고 설정을 수정할 수 있는 상태 인지 체크한다.
	 * @param item
	 * @throws Exception
	 */
	public static void checkUpdateApprSetting(Work item) throws Exception
	{
		if (!item.isVisible() || item.getStatus() != Work.WORKING_STATUS)
		{
			throw new InvalidStatusException();
		}
		checkActor(item);
	}

	/**
	 * 현재 사용자가 첨부 조회권한이 있는지 확인한다.
	 * @param item
	 * @param mp
	 * @throws Exception
	 */
	public static void checkAttachUser(Work item, ModuleParam mp) throws Exception
	{
		if (!ItemPermission.isAttachUser(item, mp))
		{
			throw new ReadDeniedException();
		}
	}

	/**
	 * 자가 승인 가능여부 반환
	 * @param item
	 * @return
	 * @throws Exception
	 */
	public static boolean isSelfApprove(Work item) throws Exception
	{
		for (WorkProcess prcs : item.getWorkProcess())
		{
			if (prcs.getProcessType() == WorkProcess.APPROVAL_TYPE
				&& prcs.getStatus() == WorkProcess.AFTER_SUPPLEMENT_COMPLETE_STATUS)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * 대면보고 요청에 필요한 권한있는지 판단.
	 * <p>
	 * 승인자가 대면보고 요청을 할 수 있으며, 승인자가 아닐 경우에 Exception처리
	 * @param item
	 * @throws Exception
	 */
	public static void checkMeetingRequest(Work item) throws Exception
	{
		if (!item.isCurrentApprover())
		{
			throw new PermissionDeniedException();
		}
	}

	/**
	 * 예하 파일들에 수정락이 걸려있는지 확인
	 * @param item
	 * @throws WorkFileCheckedOut
	 */
	public static void checkFileLockIn(Work item) throws WorkFileCheckedOut
	{
		Set<WorkAbstractAttachment> set = new TreeSet<WorkAbstractAttachment>();
		if (item.getReportFiles() != null)
		{
			set.addAll(item.getReportFiles());
		}
		if (item.getWorkAttachments() != null)
		{
			set.addAll(item.getWorkAttachments());
		}

		for (WorkAbstractAttachment att : set)
		{
			if (att.isCheckOut())
			{
				throw new WorkFileException.WorkFileCheckedOut();
			}
		}
	}

	/**
	 * 삭제가 가능한지 여부판단
	 * @param item
	 * @throws InvalidStatusException
	 */
	public static void delete(Work item) throws InvalidStatusException
	{
		// if ((server.getWorkProcess() != null && server.getWorkProcess().size() > 0)
		// || server.getStatus() == Work.APRV_CMPLT_STATUS)
		// {
		// throw new InvalidStatusException();
		// }
	}

	/**
	 * 검토/승인 할 수 있는 상태인지 확인
	 * @param prcs
	 * @throws InvalidStatusException
	 */
	public static void checkStatus(WorkProcess prcs) throws InvalidStatusException
	{
		if (prcs.getProcessType() == WorkProcess.RETRIEVE_TYPE)
		{
			throw new InvalidStatusException();
		}
		if (prcs.getStatus() == WorkProcess.RETRIEVE_STATUS)
		{
			throw new InvalidStatusException();
		}
		if (prcs.isRetrieve())
		{
			throw new InvalidStatusException();
		}
	}

	/**
	 * 업무방을 사용자가 조회할 권한이 없을때 발생한다.
	 */
	public static class ReadDeniedException extends Exception
	{
		private static final long serialVersionUID = -437905125732397505L;
	}
}
