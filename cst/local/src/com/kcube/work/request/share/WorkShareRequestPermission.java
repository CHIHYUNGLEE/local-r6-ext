package com.kcube.work.request.share;

import java.sql.ResultSet;

import com.kcube.lib.sql.SqlSelect;
import com.kcube.sys.usr.PermissionDeniedException;
import com.kcube.sys.usr.UserService;
import com.kcube.work.Work;

/**
 * @author 김경수
 *         <p>
 *         공유 요청 관련 권한 체크
 */
public class WorkShareRequestPermission
{

	/**
	 * 조회권한이 있는지 확인
	 * @param server
	 * @throws Exception
	 */
	public static void checkReadPermission(WorkShareRequest server, Work work) throws Exception
	{
		if (!(isRequestUser(server) || work.isCurrentActor()))
		{
			throw new PermissionDeniedException();
		}
	}

	/**
	 * 승인권한이 있는지 확인
	 * @param server
	 * @throws Exception
	 */
	public static void checkApprovePermission(Work work) throws Exception
	{
		if (!work.isCurrentActor())
		{
			throw new PermissionDeniedException();
		}
	}

	/**
	 * 반려권한이 있는지 확인
	 * @param server
	 * @throws Exception
	 */
	public static void checkRejectPermission(Work work) throws Exception
	{
		if (!work.isCurrentActor())
		{
			throw new PermissionDeniedException();
		}
	}

	/**
	 * 권한을 요청한 사용자인지 여부를 반환한다.
	 * @param server
	 * @throws Exception
	 */
	private static boolean isRequestUser(WorkShareRequest server) throws Exception
	{
		if (server.getReqUser().getUserId().equals(UserService.getUserId()))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * 중복된 권한신청인지 여부를 검사한다.
	 * @param server
	 * @throws Exception
	 */
	public static void checkDuplicateRequest(WorkShareRequest request) throws Exception
	{
		if (isDuplicateRequest(request))
		{
			throw new WorkShareRequestPermission.DuplicateRequest();
		}
	}

	/**
	 * 중복된 공유 요청인지 여부를 반환한다.
	 * @param server
	 * @return
	 * @throws Exception
	 */
	public static boolean isDuplicateRequest(WorkShareRequest request) throws Exception
	{
		SqlSelect stmt = new SqlSelect();
		stmt.select("reqid");
		stmt.from("work_share_request");
		stmt.where("workid = ?", request.getWorkId());
		Long reqUserId = request.getReqUser() == null ? UserService.getUserId() : request.getReqUser().getUserId();
		stmt.where("req_userid = ?", reqUserId);
		stmt.where("status = ?", WorkShareRequest.STATUS_APPLIED);
		ResultSet rs = stmt.query();

		return rs.next();
	}

	/**
	 * 이미 공유 요청 했을 경우에 발생함.
	 */
	public static class DuplicateRequest extends Exception
	{
		private static final long serialVersionUID = -2892856815397582698L;
	}
}