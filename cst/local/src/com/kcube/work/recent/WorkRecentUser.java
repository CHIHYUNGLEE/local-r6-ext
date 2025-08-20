package com.kcube.work.recent;

import java.util.ArrayList;
import java.util.List;

import com.kcube.lib.action.Action;
import com.kcube.lib.action.ActionContext;
import com.kcube.lib.jdbc.DbService;
import com.kcube.lib.json.JsonMapping;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.User;
import com.kcube.sys.usr.UserPermission;
import com.kcube.work.Work;
import com.kcube.work.process.WorkProcess;
import com.kcube.work.recent.WorkRecent.RecentData;

/**
 * @author 박혜란
 *         <p>
 *         최근 업무방 관련 Action Class
 */
public class WorkRecentUser
{
	static JsonMapping _eventFactory = new JsonMapping(WorkRecent.class, "recent");

	static ArrayList<Integer> PRCSLIST = new ArrayList<Integer>();
	static ArrayList<Integer> WORKINGLIST = new ArrayList<Integer>();
	static ArrayList<Integer> COMPLTLIST = new ArrayList<Integer>();

	static
	{
		PRCSLIST.add(Work.APPROVAL_STATUS);
		PRCSLIST.add(Work.NEED_SUPPLEMENT_STATUS);
		PRCSLIST.add(Work.REVIEW_STATUS);
		PRCSLIST.add(Work.REVIEW_CMPLT_STATUS);

		WORKINGLIST.add(Work.WORKING_STATUS);

		COMPLTLIST.add(Work.COMPLETE_STATUS);
		COMPLTLIST.add(Work.APRV_CMPLT_STATUS);
	}

	/**
	 * 나의 최근사용 문서 목록
	 */
	public static class ListByUser implements Action
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			WorkRecentSql sql = new WorkRecentSql(ctx.getParameter("ts"));

			SqlSelect stmt = sql.getRecentSql();
			SqlSelect wapper = new SqlSelect();
			wapper.select("*");
			wapper.from(stmt, "wi");
			wapper.where("appid = ? ", mp.getAppId());
			sql.writeJson(ctx.getWriter(), wapper, "wi.recent_date desc");
		}
	}

	/**
	 * 나의 최근사용 문서의 상태에 따라 각 목록 오른쪽에 표기할 내용을 돌려준다
	 * @author WJ
	 */
	public static class GetEvent implements Action
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long[] ids = ctx.getLongValues("ids");
			ArrayList<WorkRecent.RecentData> list = new ArrayList<WorkRecent.RecentData>();
			for (int i = 0; i < ids.length; i++)
			{
				Work server = (Work) DbService.load(Work.class, ids[i]);
				if (isProcessEventStatus(server.getStatus()))
				{ // 프로세스 관련 Event를 내려줄 상태들
					@SuppressWarnings("unchecked")
					List<WorkProcess> prcsList = DbService.getSession().getNamedQuery("getProcessByGid").setParameter(
						"gid",
						server.getLastProcessGid()).list();
					WorkProcess prcs = null;
					if (prcsList.size() > 0)
					{
						prcs = prcsList.get(0);
					}
					list.add(new RecentData(prcs));
				}
				else if (isWorkStatus(server.getStatus()))
				{ // 작업중 상태 // 담당자와 협업자들을 내려줌.
					ArrayList<User> userList = new ArrayList<User>();
					userList.add(server.getActor());
					if (server.getHelpers() != null)
					{
						for (int j = 0; j < server.getHelpers().size(); j++)
							userList.add(server.getHelpers().get(j));
					}
					list.add(new RecentData(userList));
				}
				else if (isCmpltStatus(server.getStatus()))
				{
					// 완료상태
					list.add(new RecentData(server.getCompleteDate()));
				}
			}
			WorkRecent data = new WorkRecent();
			data.setList(list);
			_eventFactory.marshal(ctx.getWriter(), data);
		}

		/**
		 * 프로세스 진행중인 상태 여부를 돌려준다.
		 * @param status
		 * @return
		 */
		private boolean isProcessEventStatus(int status)
		{
			return PRCSLIST.contains(status);
		}

		/**
		 * 작업중 상태여부를 돌려준다.
		 * @param status
		 * @return
		 */
		private boolean isWorkStatus(int status)
		{
			return WORKINGLIST.contains(status);
		}

		/**
		 * 완료상태 여부를 돌려준다.
		 * @param status
		 * @return
		 */
		private boolean isCmpltStatus(int status)
		{
			return COMPLTLIST.contains(status);
		}
	}
}