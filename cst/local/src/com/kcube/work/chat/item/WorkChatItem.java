package com.kcube.work.chat.item;

import com.kcube.doc.Item;
import com.kcube.sys.upload.Upload;
import com.kcube.sys.usr.User;
import com.kcube.sys.usr.UserService;

/**
 * 업무방 채팅 Bean class
 * @author 김경수
 */
public class WorkChatItem extends Item
{
	private static final long serialVersionUID = 1690810728501602855L;

	/**
	 * 메세지 타입
	 */
	public static final int MSG_TYPE = 1000;

	/**
	 * 이미지 타입
	 */
	public static final int IMG_TYPE = 2000;

	/**
	 * 파일 타입
	 */
	public static final int FILE_TYPE = 3000;

	private Long _chatId;
	private String _content;
	private int _msgType;
	private Long _emtcId;
	private String _cstmField1;

	/**
	 * @see com.kcube.doc.Item#getRgstUser()
	 */
	public User getRgstUser()
	{
		User rgstUser = super.getRgstUser();
		if (rgstUser == null)
		{
			rgstUser = UserService.getUser();
			setRgstUser(rgstUser);
		}
		return rgstUser;
	}

	/**
	 * 채팅방 아이디를 돌려준다.
	 */
	public Long getChatId()
	{
		return _chatId;
	}

	public void setChatId(Long chatId)
	{
		_chatId = chatId;
	}

	/**
	 * 메세지 종류를 보내준다(ex: 이미지,파일, 메세지)
	 * @return
	 */
	public int getMsgType()
	{
		return _msgType;
	}

	public void setMsgType(int msgType)
	{
		_msgType = msgType;
	}

	/**
	 * 채팅 내용을 돌려준다.
	 */
	public String getContent()
	{
		return _content;
	}

	public void setContent(String content)
	{
		_content = content;
	}

	/**
	 * 이모티콘 ID를 돌려준다.
	 * @return
	 */
	public Long getEmtcId()
	{
		return _emtcId;
	}

	public void setEmtcId(Long emtcId)
	{
		_emtcId = emtcId;
	}

	public String getCstmField1()
	{
		return _cstmField1;
	}

	public void setCstmField1(String cstmField1)
	{
		_cstmField1 = cstmField1;
	}

	/**
	 * 업무 채팅방의 첨부에 관련된 Class
	 * @author 김경수
	 */
	public static class Attachment extends com.kcube.doc.file.Attachment
	{
		private static final long serialVersionUID = 3176006178968122646L;

		private String _height;
		private int _width;
		private Upload _thumb;
		private Long _itemId;

		public void setHeight(String height)
		{
			_height = height;
		}

		/**
		 * 높이를 돌려준다.
		 */
		public String getHeight()
		{
			return _height;
		}

		public void setWidth(Integer width)
		{
			_width = width == null ? -1 : width;
		}

		/**
		 * 넓이를 돌려준다.
		 */
		public int getWidth()
		{
			return _width;
		}

		public void setThumb(Upload thumb)
		{
			_thumb = thumb;
		}

		public Upload getThumb()
		{
			return _thumb;
		}

		public Long getItemId()
		{
			return _itemId;
		}

		public void setItemId(Long itemId)
		{
			_itemId = itemId;
		}
	}
}