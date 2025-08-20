package com.kcube.work.trans;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.kcube.lib.event.EventService;
import com.kcube.lib.jdbc.DbService;
import com.kcube.lib.sql.Sql;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.lib.sql.SqlTable;
import com.kcube.lib.sql.SqlUpdate;
import com.kcube.lib.sql.SqlWriter;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.UserService;
import com.kcube.work.Work;
import com.kcube.work.WorkHistory;
import com.kcube.work.WorkListener;
import com.kcube.work.WorkManager;
import com.kcube.work.trans.WorkTransfer.WorkTransferItem;

public class WorkTransManager
{
	public static WorkListener _listener = (WorkListener) EventService.getDispatcher(WorkListener.class);

	private static final SqlTable WORKTRANS = new SqlTable("WORK_TRANSFER", "i");
	private static final SqlTable WORKTRANSTITEM = new SqlTable("WORK_TRANSFER_ITEM", "i");
	private static SqlWriter _writer = new SqlWriter().putAll(WORKTRANS);
	private static SqlWriter _listWriter = new SqlWriter().putAll(WORKTRANSTITEM);

	static int[] CMPLT = {Work.COMPLETE_STATUS, Work.APRV_CMPLT_STATUS};

	/**
	 * 아이디 값들로 제목 정보를 돌려준다.
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public ResultSet getTitlesByIds(Long ids[]) throws Exception
	{
		SqlSelect stmt = new SqlSelect();

		stmt.select("ITEMID,TITLE");
		stmt.from("WORK_ITEM");
		stmt.where(new Sql.InLongArray("ITEMID", ids));

		return stmt.query();
	}

	/**
	 * 현 사용자의 완료상태인 업무방 리스트를 돌려준다.
	 * @return
	 * @throws Exception
	 */
	public ResultSet getCmpltWorkItemListByUserId() throws Exception
	{
		SqlSelect stmt = new SqlSelect();

		stmt.select("ITEMID,TITLE");
		stmt.from("WORK_ITEM");
		stmt.where("ACTOR_USERID = ?", UserService.getUserId());
		stmt.where(new Sql.InIntArray("STATUS", CMPLT));
		return stmt.query();
	}

	/**
	 * 본인이 요청한 요청리스트를 돌려준다.
	 * @param mp
	 * @return
	 */
	public SqlSelect getReqListByUser(ModuleParam mp, int status)
	{
		SqlSelect stmt = new SqlSelect();
		stmt.select(WORKTRANS, "list");
		stmt.where("REQ_USERID = ?", UserService.getUserId());
		if (status != 0)
		{
			stmt.where("status = ?", status);
		}
		return stmt;
	}

	/**
	 * 본인에게 요청된 요청리스트를 돌려준다.
	 * @param mp
	 * @return
	 */
	public SqlSelect getResListByUser(ModuleParam mp, int status)
	{
		SqlSelect stmt = new SqlSelect();
		stmt.select(WORKTRANS, "list");
		stmt.where("RES_USERID = ?", UserService.getUserId());
		if (status != 0)
		{
			stmt.where("status = ?", status);
		}
		return stmt;
	}

	/**
	 * 인수인계 요청 id값을 기준으로 <br>
	 * 인수인계 요청 업무리스트 반환
	 * @param transId
	 * @return
	 */
	public SqlSelect getWorkListByWorkId(Long transId)
	{
		SqlSelect stmt = new SqlSelect();
		stmt.select(WORKTRANSTITEM, "list");
		stmt.where("TRANSID = ?", transId);
		return stmt;
	}

	/**
	 * 인수인계 리스트 세팅
	 * @param trans
	 * @param ids
	 * @throws Exception
	 */
	public void setItemList(WorkTransfer trans, Long[] ids) throws Exception
	{
		setWorkCheckIn(ids);
		ResultSet rs = getTitlesByIds(ids);

		ArrayList<WorkTransferItem> list = new ArrayList<WorkTransfer.WorkTransferItem>();

		while (rs.next())
		{
			list.add(new WorkTransferItem(rs.getString("title"), rs.getLong("itemid")));
		}

		trans.setItemList(list);
	}

	/**
	 * 업무방 인수인계시 락
	 * @param ids
	 * @throws Exception
	 */
	private void setWorkCheckIn(Long[] ids) throws Exception
	{
		SqlUpdate updt = new SqlUpdate("WORK_ITEM");
		updt.setBoolean("ISTRANSFER", true);
		updt.where(new Sql.InLongArray("ITEMID", ids));
		updt.execute();
	}

	/**
	 * 인수인계 리스트 세팅
	 * @param trans
	 * @param server
	 * @throws Exception
	 */
	public void setItemList(WorkTransfer trans) throws Exception
	{
		setWorkCheckIn();
		ResultSet rs = getCmpltWorkItemListByUserId();

		ArrayList<WorkTransferItem> list = new ArrayList<WorkTransfer.WorkTransferItem>();

		while (rs.next())
		{
			list.add(new WorkTransferItem(rs.getString("title"), rs.getLong("itemid")));
		}

		trans.setItemList(list);

	}

	/**
	 * 업무방 인수인계시 락
	 * @throws Exception
	 */
	private void setWorkCheckIn() throws Exception
	{
		SqlUpdate updt = new SqlUpdate("WORK_ITEM");
		updt.setBoolean("ISTRANSFER", true);
		updt.where("ACTOR_USERID = ?", UserService.getUserId());
		updt.where(new Sql.InIntArray("STATUS", CMPLT));
		updt.execute();
	}

	/**
	 * trans -> server로 복사저장. <br>
	 * server에 모듈파람,요청에 대한 기본값 세팅.
	 * @param trans
	 * @param server
	 * @param mp
	 */
	public void update(WorkTransfer trans, WorkTransfer server, ModuleParam mp)
	{
		setModuleParam(server, mp);
		setRequset(server);
		server.setResUser(trans.getResUser());
		server.setReqComm(trans.getReqComm());
		server.setTitle(trans.getTitle());
		server.setItemList(trans.getItemList());
		server.setTotalCount(server.getItemList().size());
	}

	/**
	 * 요청상태로 변경.
	 * @param item
	 */
	public void setRequset(WorkTransfer item)
	{
		item.setReqDate(new Date());
		item.setReqUser(UserService.getUser());
		item.setStatus(WorkTransfer.STATUS_APPLIED);
	}

	/**
	 * 모듈파람 세팅
	 * @param item
	 * @param mp
	 */
	public void setModuleParam(WorkTransfer item, ModuleParam mp)
	{
		item.setAppId(mp.getAppId());
		item.setClassId(mp.getClassId());
		item.setModuleId(mp.getModuleId());
		item.setSpaceId(mp.getSpaceId());
	}

	/**
	 * SELECT문을 실행한 결과와 총 건수를 JSON으로 출력한다.
	 */
	public void writeJson(PrintWriter writer, SqlSelect select, SqlSelect count, String ts) throws Exception
	{
		_writer.setOrder("i.TRANSFERID desc");
		_writer.setTable("WORK_TRANSFER");
		_writer.page(writer, select, count, ts);
	}

	/**
	 * SELECT문을 실행한 결과와 총 건수를 JSON으로 출력한다.
	 */
	public void writeItemListJson(PrintWriter writer, SqlSelect select, SqlSelect count, String ts) throws Exception
	{
		_listWriter.setOrder("i.SEQ_ORDER ASC");
		_listWriter.setTable("WORK_TRANSFER_ITEM");
		_listWriter.page(writer, select, count, ts);
	}

	/**
	 * 응답 상태로 변경한다.
	 * @param server
	 * @param isApprove
	 */
	public void response(WorkTransfer server, boolean isApprove)
	{
		if (isApprove)
		{
			server.setStatus(WorkTransfer.STATUS_APPROVED);
		}
		else
		{
			server.setStatus(WorkTransfer.STATUS_REJECTED);
		}
		server.setResDate(new Date());
	}

	/**
	 * 요청을 승인한다.
	 * @param client
	 * @param server
	 * @throws Exception
	 */
	public void approve(WorkTransfer server) throws Exception
	{
		List<WorkTransferItem> list = server.getItemList();
		for (WorkTransferItem item : list)
		{
			Work work = (Work) DbService.loadWithLock(Work.class, item.getWorkId());
			changeActor(work);
			checkOut(work);
			WorkHistory
				.changedInCharger(
					new ModuleParam(work.getClassId(), work.getModuleId(), work.getSpaceId(), null, work.getAppId()),
					work);
			_listener.changeMember(work);
		}
		response(server, true);
	}

	/**
	 * 요청을 반려한다.
	 * @param client
	 * @param server
	 * @throws Exception
	 */
	public void reject(WorkTransfer client, WorkTransfer server) throws Exception
	{
		List<WorkTransferItem> list = server.getItemList();
		for (WorkTransferItem item : list)
		{
			Work work = (Work) DbService.loadWithLock(Work.class, item.getWorkId());
			checkOut(work);
		}
		server.setResComm(client.getResComm());
		response(server, false);
	}

	/**
	 * 담당자를 변경하면서 생기는 비즈니스로직 처리.
	 * @param work
	 * @throws Exception
	 */
	public void changeActor(Work work) throws Exception
	{
		work.setActor(UserService.getUser());
		WorkManager.addSecurity(work);
	}

	/**
	 * 체크아웃한다.
	 * @param work
	 * @throws Exception
	 */
	public void checkOut(Work work) throws Exception
	{
		work.setTransfer(false);
	}
}