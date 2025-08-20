package com.kcube.work.chat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.kcube.doc.Item;
import com.kcube.sys.usr.User;

/**
 * 업무_채팅방 Bean class
 * @author 김경수
 */
public class WorkChat extends Item
{
	private static final long serialVersionUID = 1432326808840397709L;

	/**
	 * 완료상태
	 */
	public static final int COMPLETED_STATUS = 6000;

	private int _mbrCnt;
	private long _fileSize;
	private long _quota;
	private long _workId;
	private List<Members> _members;

	/**
	 * 업무방 일련번호
	 * @return
	 */
	public long getWorkId()
	{
		return _workId;
	}

	public void setWorkId(long workId)
	{
		_workId = workId;
	}

	/**
	 * 채팅방 맴버 수
	 * @return
	 */
	public int getMbrCnt()
	{
		return _mbrCnt;
	}

	public void setMbrCnt(int mbrCnt)
	{
		_mbrCnt = mbrCnt;
	}

	/**
	 * 채팅방 총 파일 Size
	 * @return
	 */
	public long getFileSize()
	{
		return (_fileSize != 0) ? _fileSize : 0;
	}

	public void setFileSize(long fileSize)
	{
		_fileSize = fileSize;
	}

	/**
	 * 채팅방 용량 제한
	 * @return
	 */
	public long getQuota()
	{
		return (_quota != 0) ? _quota : -1;
	}

	public void setQuota(long quota)
	{
		_quota = quota;
	}

	/**
	 * 채팅방 맴버 List
	 * @return
	 */
	public List<Members> getMembers()
	{
		return _members;
	}

	public void setMembers(List<Members> members)
	{
		_members = members;
	}

	/**
	 * 채팅맴버 한명을 추가한다
	 * @param member
	 */
	public void addMembers(Members member)
	{
		List<Members> members = getMembers();
		if (null == members)
		{
			members = new ArrayList<Members>();
			setMembers(members);
		}
		members.add(member);
	}

	/**
	 * 업무방 웹채팅 맴버 정의 클래스
	 */
	public static class Members extends com.kcube.sys.usr.User
	{
		private static final long serialVersionUID = -2927981217579971606L;

		public Members()
		{

		}

		public Members(User user)
		{
			this.setUserId(user.getUserId());
			this.setName(user.getName());
			this.setDisplayName(user.getDisplayName());
		}

		private User _user;
		private Long _chatId;
		private Long _readId;
		private Date _rgstDate;

		public Long getChatId()
		{
			return _chatId;
		}

		public void setchatId(Long chatId)
		{
			_chatId = chatId;
		}

		public Long getReadId()
		{
			return (_readId == null) ? 0 : _readId;
		}

		public void setReadId(Long readId)
		{
			_readId = readId;
		}

		public Date getRgstDate()
		{
			return (_rgstDate != null) ? _rgstDate : new Date();
		}

		public void setRgstDate(Date rgstDate)
		{
			_rgstDate = rgstDate;
		}

		/**
		 * User 정보를 돌려준다.
		 */
		public User getUser()
		{
			return _user;
		}

		public void setUser(User user)
		{
			_user = user;
		}
	}
}
