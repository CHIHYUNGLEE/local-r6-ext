package com.kcube.work.trans;

import java.sql.ResultSet;

import com.kcube.lib.sql.Sql;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.sys.usr.UserService;

public class WorkTransferPermisson
{
	/**
	 * 응답자가 본인인지 확인한다.
	 * @param server
	 * @throws NotEqlsCurrUserException
	 */
	public static void checkResUser(WorkTransfer server) throws NotEqlsCurrUserException
	{
		if (!UserService.getUser().equals(server.getResUser()))
		{
			throw new NotEqlsCurrUserException();
		}
	}

	/**
	 * 승인/반려가 가능한지 체크.
	 * @param server
	 * @throws NotEqlsCurrUserException
	 * @throws InvalidStatusException
	 */
	public static void checkRespose(WorkTransfer server) throws NotEqlsCurrUserException, InvalidStatusException
	{
		checkResUser(server);
		checkResponseItemStatus(server);
	}

	/**
	 * 승인/반려 할 수 있는 상태값인지 체크
	 * @param server
	 * @throws InvalidStatusException
	 */
	public static void checkResponseItemStatus(WorkTransfer server) throws InvalidStatusException
	{
		if (server.getStatus() != WorkTransfer.STATUS_APPLIED)
		{
			throw new InvalidStatusException();
		}
	}

	/**
	 * 인수인계 중복요청검사.
	 * @param ids
	 * @throws Exception
	 */
	public static void checkTakOverDuplicate(Long[] ids) throws Exception
	{
		SqlSelect stmt = new SqlSelect();

		stmt.select("ITEMID");
		stmt.from("WORK_ITEM");
		stmt.where(new Sql.InLongArray("ITEMID", ids));
		stmt.where("ISTRANSFER = ?", true);

		ResultSet rs = stmt.query();

		if (rs.next())
		{
			throw new AlreadyReqTakeOverException();
		}
	}

	/**
	 * 인수인계 중복요청검사.
	 * @param ids
	 * @throws Exception
	 */
	public static void checkTakOverDuplicate() throws Exception
	{
		SqlSelect stmt = new SqlSelect();

		stmt.select("ITEMID");
		stmt.from("WORK_ITEM");
		stmt.where("ACTOR_USERID = ?", UserService.getUserId());
		stmt.where("ISTRANSFER = ?", true);

		ResultSet rs = stmt.query();

		if (rs.next())
		{
			throw new AlreadyReqTakeOverException();
		}
	}

	/**
	 * 현재사용자가 아님
	 * @author 신운재
	 */
	public static class NotEqlsCurrUserException extends Exception
	{
		private static final long serialVersionUID = -6340346903964995410L;
	}

	/**
	 * 이미 인수인계중인 업무방에 대한 중복요청 예외.
	 * @author 신운재
	 */
	public static class AlreadyReqTakeOverException extends Exception
	{
		private static final long serialVersionUID = -2299879125667176083L;
	}

	/**
	 * 상태가 유효하지않음.
	 * @author 신운재
	 */
	public static class InvalidStatusException extends Exception
	{
		private static final long serialVersionUID = 841257793505772644L;
	}
}
