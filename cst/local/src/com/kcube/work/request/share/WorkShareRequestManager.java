package com.kcube.work.request.share;

import java.util.Date;

import com.kcube.sys.usr.UserService;

/**
 * @author 김경수
 *         <p>
 *         공유 권한 요청관련 Manager
 */
public class WorkShareRequestManager
{
	/**
	 * 공유 권한 요청
	 * <p>
	 * @param server
	 * @param client
	 */
	public void apply(WorkShareRequest server, WorkShareRequest client)
	{
		server.setWorkId(client.getWorkId());
		server.setReqDate(new Date());
		server.setReqComment(client.getReqComment());
		server.setStatus(WorkShareRequest.STATUS_APPLIED);
	}

	/**
	 * 공유 요청 승인
	 * <p>
	 * @param server
	 * @param client
	 */
	public void approve(WorkShareRequest server, WorkShareRequest client)
	{
		server.setResDate(new Date());
		server.setResUser(UserService.getUser());
		server.setResComment(client.getResComment());
		server.setStatus(WorkShareRequest.STATUS_APPROVED);
	}

	/**
	 * 반려
	 * @param server
	 * @param client
	 */
	public void reject(WorkShareRequest server, WorkShareRequest client)
	{
		server.setResDate(new Date());
		server.setResUser(UserService.getUser());
		server.setResComment(client.getResComment());
		server.setStatus(WorkShareRequest.STATUS_REJECTED);
	}
}
