package com.kcube.work.chat;

import java.sql.ResultSet;

import com.kcube.lib.action.Action;
import com.kcube.lib.action.ActionContext;
import com.kcube.lib.jdbc.DbStorage;
import com.kcube.lib.json.JsonWriter;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.UserPermission;

import net.sf.json.JSONObject;

/**
 * 업무 채팅방 사용자 Action
 * @author 김경수
 */
public class WorkChatUser
{
	static DbStorage _storage = new DbStorage(WorkChat.class);

	/**
	 * 해당 업무방의 채팅방 정보를 돌려준다.
	 */
	public static class FindChatByUser implements Action
	{
		public void execute(ActionContext ctx) throws Exception
		{
			// ModuleParam mp = ctx.getModuleParam();
			// UserPermission.setModuleMenu(mp);

			Long workId = 1L;
			WorkChatSql sql = new WorkChatSql();
			SqlSelect stmt = sql.getChatInfo(workId);

			ResultSet rs = stmt.query();
			JsonWriter writer = new JsonWriter(ctx.getWriter());
			writer.writeHeader();
			if (rs.next())
			{
				writer.setAttribute("chatId", rs.getLong("chatid"));
				writer.setAttribute("status", rs.getInt("status"));
				writer.setAttribute("fileSize", rs.getLong("file_size"));
				writer.setAttribute("quota", rs.getLong("file_quota"));
			}
			writer.writeFooter();
		}
	}

	/**
	 * 첨부파일을 올리기 전 채팅방 용량을 확인한다.
	 */
	public static class CheckFileSize implements Action
	{
		public void execute(ActionContext ctx) throws Exception
		{
			boolean check = false;
			WorkChat server = (WorkChat) _storage.load(ctx.getLong("chatid"));
			Long totalSize = server.getFileSize() + ctx.getLong("size");
			if (server.getQuota() == -1)
			{
				check = true;
			}
			else if (totalSize > server.getQuota())
			{
				check = false;
			}
			else
			{
				check = true;
			}
			JSONObject obj = new JSONObject();
			obj.put("uploadable", check);
			ctx.getWriter().print(obj.toString());
		}
	}

	/**
	 * 채팅맴버 썸네일을 가져온다.
	 */
	public static class MemberThumbInfo implements Action
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			ResultSet rs = WorkChatManager.getResult(ctx.getLong("id"));
			JsonWriter writer = new JsonWriter(ctx.getWriter());
			writer.writeListHeader();
			while (rs.next())
			{
				writer.startList();
				writer.setAttribute("code", rs.getLong("thumb_save_code"));
				writer.setAttribute("path", rs.getString("thumb_save_path"));
				writer.setAttribute("userid", rs.getString("userid"));
				writer.endList();
			}
			writer.writeListFooter();
		}
	}
}