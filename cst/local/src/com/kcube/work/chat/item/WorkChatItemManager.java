package com.kcube.work.chat.item;

import java.util.Date;

import com.kcube.doc.file.Attachment;
import com.kcube.lib.jdbc.DbStorage;
import com.kcube.sys.upload.ThumbnailUtil;
import com.kcube.sys.upload.Upload;
import com.kcube.sys.usr.UserService;

/**
 * 업무 채팅 Manager Class
 * @author 김경수
 */
public class WorkChatItemManager
{
	private static ThumbnailUtil _thumbUtil = new ThumbnailUtil();
	private static DbStorage _attachStorage = new DbStorage(WorkChatItem.Attachment.class);

	/**
	 * 채팅 내용 update(Message)
	 * @param server
	 * @param client
	 * @param chatId
	 */
	public static void updateMessage(WorkChatItem server, WorkChatItem client, String chatId)
	{
		server.setRgstUser(UserService.getUser());
		server.setContent(client.getContent());
		server.setChatId(Long.parseLong(chatId));
		server.setMsgType(WorkChatItem.MSG_TYPE);
		server.setEmtcId(null);
		server.setRgstDate(new Date());
	}

	/**
	 * 채팅 내용 update(File)
	 * @param server
	 * @param client
	 * @param chatId
	 * @throws Exception
	 */
	public static void updateAttr(WorkChatItem server, Upload client, Long chatId) throws Exception
	{
		server.setRgstUser(UserService.getUser());
		server.setContent("");
		server.setRgstDate(new Date());
		server.setMsgType(WorkChatItem.FILE_TYPE);
		server.setChatId(chatId);
		WorkChatItem.Attachment attr = (WorkChatItem.Attachment) _attachStorage.create();
		attr.setFilename(client.getFilename());
		attr.setFilesize(client.getFilesize());
		attr.setMethod(client.getMethod());
		attr.setType(client.getType());
		attr.setPath(String.valueOf(client.getId()));
		attr.setItemId(server.getId());
		if (ThumbnailUtil.isImageExtension(attr.getFilename()))
		{
			Upload upload = createThumbnail(attr, _thumbUtil);
			attr.setWidth(upload.getOriginWidth());
			attr.setHeight(String.valueOf(upload.getOriginHeight()));
			attr.setThumb(upload);

			server.setMsgType(WorkChatItem.IMG_TYPE);
		}
		server.setEmtcId(0L);
	}

	/**
	 * 썸네일 생성
	 * @param pic
	 * @param thumbUtil
	 * @return
	 * @throws Exception
	 */
	static Upload createThumbnail(Attachment pic, ThumbnailUtil thumbUtil) throws Exception
	{
		if (null != pic && !pic.getType().equals(new Integer(-1)))
		{
			return thumbUtil.createThumbnail(pic);
		}
		return null;
	}
}