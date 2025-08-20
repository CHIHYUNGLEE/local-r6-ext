package com.kcube.work.chat.item;

import java.util.Iterator;

import com.kcube.doc.file.Attachment;
import com.kcube.doc.file.AttachmentManager;
import com.kcube.lib.action.ActionContext;
import com.kcube.lib.jdbc.DbService;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.upload.ThumbnailUtil;
import com.kcube.sys.usr.UserPermission;
import com.kcube.work.chat.WorkChat;
import com.kcube.work.chat.WorkChatPermission;

/**
 * 업무방 웹채팅 Owner Action Class
 * @author shin8901
 */
public class WorkChatItemOwner
{
	private static AttachmentManager _attachment = new AttachmentManager();
	private static ThumbnailUtil _thumbUtil = new ThumbnailUtil();

	/**
	 * 자신의 소유한 글 내용을 수정한다.
	 */
	public static class DoUpdateByOwner extends WorkChatItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			WorkChatItem client = unmarshal(ctx);
			WorkChat item = (WorkChat) _chatStorage.load(client.getChatId());
			WorkChatPermission.checkMbrAndRoom(item);
			WorkChatItem server = (WorkChatItem) _storage.loadWithLock(client.getId());
			WorkChatPermission.checkItemOwner(server);
			server.setContent(client.getContent());
		}
	}

	/**
	 * 자신의 소유한 첨부파일 및 글을 삭제한다.
	 */
	public static class DoDeleteByOwner extends WorkChatItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			WorkChatItem client = unmarshal(ctx);
			WorkChat item = (WorkChat) _chatStorage.load(client.getChatId());
			WorkChatPermission.checkMbrAndRoom(item);
			WorkChatItem server = (WorkChatItem) _storage.loadWithLock(client.getId());
			WorkChatPermission.checkItemOwner(server);
			if (server.getMsgType() != WorkChatItem.MSG_TYPE)
			{
				Iterator<? extends Attachment> it = server.getAttachments().iterator();
				while (it.hasNext())
				{
					com.kcube.work.chat.item.WorkChatItem.Attachment att = (com.kcube.work.chat.item.WorkChatItem.Attachment) it
						.next();
					if (null != att.getThumb())
					{
						_thumbUtil.deleteXA(att.getThumb());
					}
					item.setFileSize(item.getFileSize() - att.getFilesize());
				}
				_attachment.remove(server.getAttachments());
			}
			DbService.remove(server);
		}
	}
}