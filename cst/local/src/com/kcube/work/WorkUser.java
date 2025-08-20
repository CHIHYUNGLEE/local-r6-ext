package com.kcube.work;

import java.sql.ResultSet;
import java.util.Date;
import java.util.List;

import com.kcube.doc.Item.Security;
import com.kcube.lib.action.ActionContext;
import com.kcube.lib.jdbc.DbConfiguration;
import com.kcube.lib.json.JsonWriter;
import com.kcube.lib.sql.SqlDialect;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.map.Folder;
import com.kcube.map.FolderCache;
import com.kcube.sys.conf.ConfigService;
import com.kcube.sys.emp.Employee;
import com.kcube.sys.emp.EmployeeConfig;
import com.kcube.sys.emp.EmployeeService;
import com.kcube.sys.i18n.I18NService;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.User;
import com.kcube.sys.usr.UserPermission;
import com.kcube.sys.usr.UserService;
import com.kcube.work.process.WorkProcess;

import net.sf.json.JSONObject;

/**
 * @author 김경수
 *         <p>
 *         업무방 사용자 Action
 */
public class WorkUser
{
	private static EmployeeConfig _config = (EmployeeConfig) ConfigService.getConfig(EmployeeConfig.class);

	/**
	 * 업무방 카운트를 돌려줍니다.
	 */
	public static class GetWorkCount extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			JSONObject obj = new JSONObject();

			try
			{
				UserPermission.setModuleMenu(mp);
				obj.put("reviewCount", WorkManager.getPrcsCount(WorkProcess.REVIEWING_STATUS, mp));
				obj.put("apprCount", WorkManager.getPrcsCount(WorkProcess.APPROVAL_STATUS, mp));
				obj.put("shareReqCount", WorkManager.getShareCount(mp));
				obj.put("transferCount", WorkManager.getTransferCount(mp));
			}
			catch (Exception e)
			{
				obj.put("reviewCount", 0);
				obj.put("apprCount", 0);
				obj.put("shareReqCount", 0);
				obj.put("transferCount", 0);
				obj.put("error", e.getMessage());
			}

			ctx.getWriter().print(obj);
		}
	}

	/**
	 * 업무방을 쓰기 양식의 초기값을 돌려준다.
	 */
	public static class PreWrite extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();

			UserPermission.setModuleMenu(mp);

			Work server = (Work) _storage.create();

			Folder dprtFolder = EmployeeService.getDprt(UserService.getUserId());
			if (dprtFolder != null)
			{
				server.setChargeDprtId(dprtFolder.getId());
				server.setChargeDprtName(dprtFolder.getName());
			}
			server.setActor(UserService.getUser());
			server.setStatus(Work.DRAFT_STATUS);
			server.setRgstDate(new Date());
			server.setVisible(false);

			server.setModuleId(mp.getModuleId());
			server.setAppId(mp.getAppId());
			server.setSpaceId(mp.getSpaceId());
			server.setClassId(mp.getClassId());

			_factory.marshal(ctx.getWriter(), server);
		}
	}

	/**
	 * 업무방을 등록한다.
	 */
	public static class DoRegister extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Work client = unmarshal(ctx);
			Work server = (Work) _storage.loadOrCreateWithLock(client.getId());
			WorkManager.update(server, client);
			WorkManager.register(server);
			// server.setExprDate(ExpireService.getExprDate(server.getExprMonth()));
			WorkHistory.registered(mp, server);

			_listener.registered(server);
		}
	}

	/**
	 * 업무방 조회한다.
	 */
	public static class ReadByUser extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long id = ctx.getLong("id");
			Work server = (Work) _storage.load(id);
			WorkPermission.checkUser(mp, server);
			server.setWorkAttachments(WorkAttachmentManager.getLVAttachmentSetByItemId(id));
			server.setReportFiles(WorkReportFileManager.getLVReportSetByItemId(id));

			/* Fvrt 중요업무 여부 */
			server.setFvrt(WorkSql.isFvrt(id));

			/* 조회 로그 처리 */
			WorkHistory.read(server);
			_factory.marshal(ctx.getWriter(), server);
		}
	}

	/**
	 * 전부서업무 문서 목록
	 */
	public static class ListByCompanyWork extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long folderId = new Long(ctx.getParameter("folderId"));
			String year = ctx.getParameter("year");
			boolean checkSecret = ctx.getBoolean("checkSecret");

			WorkSql sql = new WorkSql(
				mp, folderId, ctx.getParameter("ts"), ctx.getBoolean("isCountDisplay"), checkSecret);
			SqlSelect select = sql.getCompanyVisibleSelect(year);

			sql.writeJson(ctx.getWriter(), select);
		}
	}

	/**
	 * 팀업무 목록
	 * <p>
	 * 본인 소속의 부서에 대한 업무 목록을 돌려준다.(부서원이 담당자 이거나 협업자 일 경우)
	 */
	public static class ListByTeamWork extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			String year = ctx.getParameter("year");
			Long userId = ctx.getLong("userId", null);
			int status = ctx.getInt("status", -1);

			WorkSql sql = new WorkSql(mp, ctx.getParameter("ts"));
			SqlSelect select = sql.getTeamVisibleSelect(year, userId, status);
			sql.writeJson(ctx.getWriter(), select);
		}
	}

	/**
	 * 현재 로그인한 사용자가 id에 해당하는 업무방에 대하여
	 * <p>
	 * Actor/Owner인지 여부를 JSON형태로 돌려준다.
	 * <p>
	 * --------------------------------------
	 * @author WJ <br>
	 *         검토자와 승인자인지 여부추가.
	 */
	public static class GetRole extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			Work work = (Work) _storage.load(ctx.getLong("id"));
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("isActor", work.isCurrentActor());
			jsonObject.put("isHelper", work.isCurrentHelper());
			jsonObject.put("isReviewer", work.isCurrentReviewer());
			jsonObject.put("isApprover", work.isCurrentApprover());
			jsonObject.put("isSelfApprove", WorkPermission.isSelfApprove(work));
			jsonObject.put("currentOwner", work.isCurrentOwner());
			jsonObject.put("scrtLevel", work.getScrtLevel());
			jsonObject.put("status", work.getStatus());
			ctx.getWriter().print(jsonObject);
		}
	}

	/**
	 * 담당자를 돌려준다.
	 */
	public static class GetActor extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			Long itemid = ctx.getLong("id");
			Work work = (Work) _storage.load(itemid);
			JsonWriter writer = new JsonWriter(ctx.getWriter());
			writer.writeListHeader();
			writeActor(writer, work.getActor());
			writer.writeListFooter();
		}
	}

	/**
	 * Actor 를 Json 으로 출력한다.
	 * @param writer
	 * @param author
	 * @throws Exception
	 */
	private static void writeActor(JsonWriter writer, User actor) throws Exception
	{
		writer.startList();
		writer.setFirstAttr(true);
		writer.setAttribute("id", actor.getUserId());
		writer.setAttribute("name", actor.getName());
		writer.setAttribute("displayName", actor.getDisplayName());
		writer.endList();
	}

	/**
	 * 협업자 목록을 돌려준다.
	 */
	public static class GetHelpers extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long itemid = ctx.getLong("id");
			Work work = (Work) _storage.load(itemid);
			JsonWriter writer = new JsonWriter(ctx.getWriter());
			writer.writeListHeader();
			for (User user : work.getHelpers())
			{
				writer.startList();
				writer.setAttribute("displayName", user.getDisplayName());
				writer.setAttribute("id", user.getUserId());
				writer.setAttribute("name", user.getName());
				writer.endList();
			}
			writer.writeListFooter();
		}
	}

	/**
	 * 업무방 연도메뉴를 돌려준다.
	 */
	public static class GetYears extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			int spaceType = ctx.getInt("spaceType");
			ResultSet rs = getYears(spaceType, mp.getAppId());
			JsonWriter writer = new JsonWriter(ctx.getWriter());
			writer.writeListHeader();
			while (rs.next())
			{
				writer.startList();
				writer.setAttribute("year", rs.getString(1));
				writer.endList();
			}
			writer.writeListFooter();
		}

		/**
		 * @param spaceType 나의업무 분류 구분값
		 */
		private static ResultSet getYears(int spaceType, Long appId) throws Exception
		{
			if (spaceType == 4000)
			{
				SqlSelect stmt1 = new SqlSelect();
				stmt1.select("wi.itemid");
				stmt1.from("work_item wi");
				stmt1.where("wi.actor_userid = ?", UserService.getUserId());

				SqlSelect stmt2 = new SqlSelect();
				stmt2.select("wih.itemid");
				stmt2.from("work_item_helper wih");
				stmt2.where("wih.userid = ?", UserService.getUserId());

				SqlSelect stmt = new SqlSelect();
				stmt.select(
					"distinct "
						+ (DbConfiguration.isMsSql() ? "year(wi.rgst_date)" : SqlDialect.toChar("wi.rgst_date", "YYYY"))
						+ " rgst_date");
				stmt.from("work_item wi");
				stmt.where("wi.itemid in ", stmt1.union(stmt2));
				stmt.where("wi.appid = ?", appId);
				stmt.where("wi.isvisb = 1 ");
				stmt.order("rgst_date DESC");

				return stmt.query();
			}
			else if (spaceType == 3000)
			{
				SqlSelect stmt = new SqlSelect();
				stmt.select(
					"distinct "
						+ (DbConfiguration.isMsSql() ? "year(rgst_date)" : SqlDialect.toChar("rgst_date", "YYYY"))
						+ " rgst_date");
				stmt.from("work_item ");
				stmt.where("appid = ?", appId);
				stmt.where("charge_dprtid = ? ", EmployeeService.getEmployee(UserService.getUserId()).getDprtId());
				stmt.where("isvisb = 1 ");
				stmt.order("rgst_date DESC");

				return stmt.query();
			}
			else
			{
				SqlSelect stmt = new SqlSelect();
				stmt.select(
					"distinct "
						+ (DbConfiguration.isMsSql() ? "year(rgst_date)" : SqlDialect.toChar("rgst_date", "YYYY"))
						+ " rgst_date");
				stmt.from("work_item ");
				stmt.where("appid = ?", appId);
				stmt.where("isvisb = 1 ");
				stmt.order("rgst_date DESC");

				return stmt.query();
			}
		}
	}

	/**
	 * 공유자 목록을 돌려준다.
	 */
	public static class GetSharers extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			Long itemid = ctx.getLong("id");
			Work work = (Work) _storage.load(itemid);
			JsonWriter writer = new JsonWriter(ctx.getWriter());
			writer.writeListHeader();
			for (User user : work.getSharers())
			{
				writer.startList();
				writer.setAttribute("displayName", user.getDisplayName());
				writer.setAttribute("id", user.getUserId());
				writer.setAttribute("name", user.getName());
				writer.endList();
			}
			writer.writeListFooter();
		}
	}

	/**
	 * 내가 속한 최하위 팀의 팀원을 돌려준다.
	 */
	public static class GetMemberList extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ResultSet rs = getMembers();
			JsonWriter writer = new JsonWriter(ctx.getWriter());
			writer.writeListHeader();
			while (rs.next())
			{
				writer.startList();
				writer.setAttribute("userId", rs.getLong("userid"));
				writer.setAttribute("userName", rs.getString("name"));
				writer.setAttribute("displayName", rs.getString("user_disp"));
				writer.setAttribute("gradeName", rs.getString("grade_name"));
				writer.setAttribute("jobTitle", rs.getString("job_title"));
				writer.setAttribute("pstnName", rs.getString("pstn_name"));
				writer.endList();
			}
			writer.writeListFooter();
		}

		/**
		 * 본인이 속한 분서의 멤버를 돌려준다.
		 * @return
		 * @throws Exception
		 */
		private ResultSet getMembers() throws Exception
		{
			Long dprtid = EmployeeService.getEmployee(UserService.getUserId()).getDprtId();

			SqlSelect stmt = new SqlSelect();
			stmt.select("userid, name, user_disp, grade_name, job_title, pstn_name");
			stmt.from("hr_user");
			stmt.where("dprtid = ? ", dprtid);
			stmt.where("isvisb = ? ", true);
			stmt.order(_config.getUserListSort());

			return stmt.query();
		}
	}

	/**
	 * 사용자명 자동완성 BGF 참고 (부서검색기능은 남겨두고 주석처리)
	 * <p>
	 * 부서검색 주석 제거 2015-11-06
	 * @author WJ
	 **/
	public static class SearchDprtUserName extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			int cnt = ctx.getInt("count");
			String keyword = ctx.getParameter("key");
			// SqlSelect select = kmSelect(keyword);
			SqlSelect userSel = userSelect(keyword);
			// select.union(userSel);
			// ResultSet rs = select.query();
			ResultSet rs = userSel.query();
			JsonWriter writer = new JsonWriter(ctx.getWriter());
			dprtUserNameWrite(writer, rs, cnt);
		}

		private static SqlSelect userSelect(String keyword)
		{
			SqlSelect sel = new SqlSelect();
			sel.select("userid as id");
			sel.select("name");
			sel.select("user_disp as detail");
			sel.select(" (SELECT 1 FROM DUAL) AS KEY ");
			sel.from("hr_user");
			sel.where("status = ? ", Employee.REGISTERED_STATUS);
			if (DbConfiguration.getJndiName() == null)
			{
				sel.where(SqlDialect.upper("name") + " like ? ", SqlDialect.getSearchValue(keyword, false));
			}
			else
			{
				sel.where("name like ? ", SqlDialect.getSearchValue(keyword, false));
			}
			return sel;
		}
	}

	/**
	 * 보안설정 공유범위 목록을 돌려준다.
	 * <p>
	 * @author 신운재 2015-11-06
	 */
	public static class ViewSecurities extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Work server = (Work) _storage.load(ctx.getLong("id"));

			List<Security> securities = server.getSecurities();
			JsonWriter _writer = new JsonWriter(ctx.getWriter());
			_writer.writeListHeader();
			for (Security sec : securities)
			{
				_writer.startList();
				_writer.setFirstAttr(true);
				_writer.setAttribute("title", sec.getTitle());
				_writer.endList();
			}
			_writer.writeListFooter();
		}
	}

	/**
	 * 부서의 사용자 이름을 JSON으로 출력한다.
	 * @param writer
	 * @param rs
	 * @param cnt
	 * @throws Exception
	 */
	private static void dprtUserNameWrite(JsonWriter writer, ResultSet rs, int cnt) throws Exception
	{
		writer.writeListHeader(-1);
		int i = 0;
		while (rs.next() && i < cnt)
		{
			Long id = new Long(rs.getLong("id"));
			int key = rs.getInt("key");

			writer.startList();
			writer.setAttribute("id", id);

			if (key == 1)
			{
				writer.setAttribute("name", I18NService.getLocalLanguage(rs.getString("name")));
				writer.setAttribute("detail", I18NService.getLocalLanguage(rs.getString("detail")));
				writer.setAttribute("displayName", rs.getString("detail"));
			}
			else
			{
				Folder f = FolderCache.getFolder(id);
				if (f.getParentId() == null)
				{
					writer.setAttribute("parent", f.getName());
				}
				else
				{
					Folder pf = FolderCache.getFolder(f.getParentId());
					pf.getPath(">");
					writer.setAttribute("parent", pf.getName());
				}
				writer.setAttribute("name", f.getName());
				writer.setAttribute("path", f.getPath(">"));
				writer.setAttribute("rootid", f.getRootId());
			}
			writer.setAttribute("key", key);
			writer.endList();
			i++;
		}
		writer.writeListFooter();
	}

	/**
	 * 업무방에서 사용한 TotalSize를 돌려준다.
	 * @author Soo
	 */
	public static class TotalSizeByUser extends WorkAction
	{
		@Override
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Work server = (Work) _storage.load(ctx.getLong("id"));

			JSONObject totalSize = new JSONObject();
			totalSize.put("docFileSize", server.getTotalFileSize());

			ctx.getWriter().print(totalSize.toString());
		}
	}

	/**
	 * 업무방을 공감한다.
	 */
	public static class SympathyByUser extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Work server = (Work) _storage.load(ctx.getLong("id"));

			WorkPermission.checkUser(mp, server);
			WorkHistory.addSympathy(mp, server, ctx.getInt("type"));

			JsonWriter writer = new JsonWriter(ctx.getWriter());
			writer.writeHeader();
			writer.setAttribute("userId", UserService.getUser().getUserId());
			writer.setAttribute("name", UserService.getUser().getName());
			writer.setAttribute("displayName", UserService.getUser().getDisplayName());
			writer.setAttribute("sympType", ctx.getInt("type"));
			writer.writeFooter();
		}
	}

	/**
	 * 업무방을 공감 삭제한다.
	 */
	public static class DelSympathyByUser extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			Work server = (Work) _storage.load(ctx.getLong("id"));
			WorkHistory.deleteSympathy(server);
		}
	}

	/**
	 * 공감 리스트를 조회한다.
	 * <p>
	 * 공감 더보기 시 사용한다.
	 */
	public static class ViewSympathyByUser extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long id = ctx.getLong("id");
			Long sympLastUserId = ctx.getLong("sympLastUserId", 0);

			Work server = (Work) _storage.load(id);
			WorkPermission.checkUser(mp, server);

			_factory.marshal(ctx.getWriter(), WorkHistory._sympathy.getItem(server, sympLastUserId));
		}
	}

	/**
	 * 검토자/승인자/보고요약문을 돌려준다.
	 */
	public static class GetApprSetting extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long itemid = ctx.getLong("id");
			Work work = (Work) _storage.load(itemid);
			_factory.marshal(ctx.getWriter(), work.getApprReviewSetting());
		}
	}
}
