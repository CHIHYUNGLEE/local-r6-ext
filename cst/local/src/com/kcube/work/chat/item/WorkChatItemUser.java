package com.kcube.work.chat.item;

import java.util.Date;

import com.kcube.lib.action.ActionContext;
import com.kcube.lib.event.EventService;
import com.kcube.lib.jdbc.DbService;
import com.kcube.lib.json.JsonMapping;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.upload.Upload;
import com.kcube.sys.upload.UploadManager;
import com.kcube.sys.usr.UserPermission;
import com.kcube.work.Work;
import com.kcube.work.chat.WorkChat;
import com.kcube.work.chat.WorkChatPermission;

/**
 * 업무방 채팅 사용자 Action
 * @author 김경수
 */
public class WorkChatItemUser
{
	private static JsonMapping _uploadFactory = new JsonMapping(Upload.class);
	private static UploadManager _uploadManager = new UploadManager();
	private static WorkChatItemListener _listener = (WorkChatItemListener) EventService.getDispatcher(
		WorkChatItemListener.class);

	/**
	 * 채팅내역을 출력한다.
	 */
	public static class ListByUser extends WorkChatItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			// ModuleParam mp = ctx.getModuleParam();
			// UserPermission.setModuleMenu(mp);

			Long chatId = ctx.getLong("chatId");
			WorkChat server = (WorkChat) _chatStorage.loadOrCreateWithLock(chatId);

			if (server.getStatus() != WorkChat.DELETED_STATUS)
			{
				// WorkChatPermission.checkMbrAndRoom(server);
			}
			else
			{
				WorkChatPermission.checkMember(server);
			}

			Date minDate = null;
			if (ctx.getParameter("minDate") != null)
			{
				minDate = ctx.getDate("minDate");
			}
			WorkChatItemSql sql = new WorkChatItemSql(ctx.getParameter("ts"));
			Long itemId = ctx.getLong("itemId", null);
			SqlSelect stmt = sql.getVisibleSelect(chatId, minDate, itemId);
			sql.writeJson(ctx.getWriter(), stmt, itemId != null ? true : false);
		}
	}

	/**
	 * 채팅을 등록한다.
	 */
	public static class DoRegister extends WorkChatItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			// ModuleParam mp = ctx.getModuleParam();
			// UserPermission.setModuleMenu(mp);

			String chatId = ctx.getParameter("chatId");
			chatId = "1";
			// WorkChat item = (WorkChat) _chatStorage.load(Long.parseLong(chatId));
			// WorkChatPermission.checkMbrAndRoom(item);
			WorkChatItem client = unmarshal(ctx);
			WorkChatItem server = (WorkChatItem) _storage.loadOrCreateWithLock(client.getId());
			// Work work = (Work) _workStorage.load(item.getWorkId());
			WorkChatItemManager.updateMessage(server, client, chatId);
			// WorkChatItemHistory.registered(mp, work, item);
			_factory.marshal(ctx.getWriter(), server);
			DbService.commit();
			_listener.registered(server);
		}
	}

	/**
	 * 첨부파일을 등록한다.
	 */
	public static class AttrUpload extends WorkChatItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			Long chatId = ctx.getLong("chatId");
			WorkChat room = (WorkChat) _chatStorage.load(chatId);
			Work wk = new Work();
			DbService.reload(wk, room.getWorkId());
			ModuleParam mp = new ModuleParam(wk.getClassId(), wk.getModuleId(), wk.getSpaceId(), null, wk.getAppId());

			UserPermission.setModuleMenu(mp);
			WorkChatPermission.checkMbrAndRoom(room);
			if (ctx.getParameter("upload") == null)
			{
				return;
			}
			Upload upload = (Upload) _uploadFactory.unmarshal(ctx.getParameter("upload"));
			Upload uploaded = _uploadManager.update(upload);
			WorkChatItem item = (WorkChatItem) _storage.create();
			WorkChatItemManager.updateAttr(item, uploaded, chatId);

			room.setFileSize(room.getFileSize() + uploaded.getFilesize());
			DbService.flush();
			DbService.reload(item, item.getId());

			_listener.fileRegistered(item);
		}
	}

	/**
	 * 첨부파일을 다운로드한다.
	 */
	public static class DownloadByUser extends WorkChatItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			WorkChatItem.Attachment att = (WorkChatItem.Attachment) DbService.load(
				WorkChatItem.Attachment.class,
				ctx.getLong("id"));

			WorkChatItem item = (WorkChatItem) _storage.load(att.getItemId());
			WorkChat room = (WorkChat) _chatStorage.load(item.getChatId());
			WorkChatPermission.checkMbrAndRoom(room);
			ctx.store(att);
		}
	}

	/**
	 * 채팅방의 첨부파일 목록을 돌려준다.
	 */
	public static class AttachmentList extends WorkChatItemAction
	{
		@Override
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long chatId = ctx.getLong("chatId");
			WorkChat room = (WorkChat) _chatStorage.load(chatId);
			WorkChatPermission.checkMbrAndRoom(room);
			WorkChatItemSql sql = new WorkChatItemSql();
			sql.writeFileJson(ctx.getWriter(), sql.getFileListSelect(chatId));
		}
	}
}