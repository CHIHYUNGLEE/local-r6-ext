package com.kcube.work.plan;

import java.sql.ResultSet;
import java.util.Date;

import com.kcube.doc.file.AttachmentManager;
import com.kcube.lib.jdbc.DbService;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.User;
import com.kcube.sys.usr.UserService;

/**
 * 업무수행계획 Manager Class
 */
public class WorkPlanManager
{
	private static AttachmentManager _attachment = new AttachmentManager(true);

	/**
	 * 업무 수행계획을 update 한다. 등록자를 authorUser에 저장한다.
	 * @param param
	 * @param server
	 * @param client
	 * @throws Exception
	 */
	static void update(ModuleParam param, WorkPlan server, WorkPlan client) throws Exception
	{
		User author = client.getAuthor();
		if (author != null)
		{
			server.setAuthor(author);
		}
		else if (server.getAuthor() == null)
		{
			server.setAuthor(UserService.getUser());
		}
		server.setWorkId(client.getWorkId());
		server.setGid(client.getGid());
		server.setPid(client.getPid());
		server.setTitle(client.getTitle());
		server.setContent(client.getContent());
		server.setLastUpdt(new Date());
		updateActors(server, client);
		server.setStartDate(client.getStartDate());
		server.setEndDate(client.getEndDate());
		server.updateAttachments(_attachment.update(client.getAttachments(), server));
	}

	/**
	 * 업무 수행계획을 등록 한다.
	 * @throws Exception
	 */
	public static void register(WorkPlan server) throws Exception
	{
		server.setLastUpdt(new Date());
		server.setRgstDate(new Date());
		server.setStatus(WorkPlan.STAY_STATUS);
	}

	/**
	 * 담당자를 update 한다.
	 * @param server
	 * @param client
	 * @throws Exception
	 */
	static void updateActors(WorkPlan server, WorkPlan client) throws Exception
	{
		server.setPlanActors(client.getPlanActors());
	}

	/**
	 * step 순서를 바꾼다.
	 * @param up
	 * @param down
	 * @throws Exception
	 */
	static void swap(WorkPlan up, WorkPlan down) throws Exception
	{
		int upStep = up.getStep();
		up.setStep(down.getStep());
		down.setStep(upStep);
	}

	/**
	 * 업무 수행 계획을 폐기한다.
	 * @param server
	 * @throws Exception
	 */
	public static void remove(WorkPlan server) throws Exception
	{
		DbService.remove(server);
	}

	/**
	 * Module parameter 값을 저장한다.
	 * @param mp
	 * @param server
	 * @throws Exception
	 */
	static void setModuleParam(ModuleParam mp, WorkPlan server) throws Exception
	{
		server.setTenantId(UserService.getTenantId());
		server.setSpaceId(mp.getSpaceId());
		server.setClassId(mp.getClassId());
		server.setModuleId(mp.getModuleId());
		server.setAppId(mp.getAppId());
	}

	/**
	 * 상태를 update 한다. 상태가 완료로 변경될 경우 완료일시를 update한다.
	 * @param mp
	 * @param server
	 * @param status
	 * @throws Exception
	 */
	public static void updateStatus(ModuleParam mp, WorkPlan server, int status) throws Exception
	{
		if (status != WorkPlan.STANDBY_STATUS && status != server.getStatus())
		{
			WorkPlanHistory.updatedStatus(mp, server);
		}
		Date date = (status == WorkPlan.COMPLETE_STATUS) ? new Date() : null;
		server.setCompleteDate(date);
		server.setStatus(status);
	}

	/**
	 * 상태 전체를 update 한다.
	 * @param mp
	 * @param ids
	 * @param status
	 * @throws Exception
	 */
	public static void updateStatusAll(ModuleParam mp, Long[] ids, int status) throws Exception
	{
		if (ids.length > 0)
		{
			WorkPlan plan = new WorkPlan();

			for (int i = 0; i < ids.length; i++)
			{
				DbService.begin();
				DbService.reload(plan, ids[i]);
				WorkPlanManager.updateStatus(mp, plan, status);
				DbService.commit();
			}
		}
	}

	/**
	 * 수행계획에 대한 제한날짜 조건을 설정한다.
	 * @param server
	 * @throws Exception
	 */
	public static void setLimitDate(WorkPlan server) throws Exception
	{
		Long pid = server.getPid();
		if (pid != null)
		{
			WorkPlan parent = (WorkPlan) WorkPlanAction._storage.load(pid);
			server.setMinStartDate(parent.getStartDate());
			server.setMaxEndDate(parent.getEndDate());
		}

		SqlSelect stmt = new SqlSelect();
		stmt.select("MAX(strt_date) sdate, MIN(end_date) edate, count(1) cnt");
		stmt.from("work_plan p");
		stmt.where("p.pid = ?", server.getId());

		ResultSet rs = stmt.query();
		if (rs.next() && rs.getInt("cnt") > 0)
		{
			server.setMaxStartDate(new Date(rs.getTimestamp("sdate").getTime()));
			server.setMinEndDate(new Date(rs.getTimestamp("edate").getTime()));
		}
	}
}
