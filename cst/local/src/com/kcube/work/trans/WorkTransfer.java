package com.kcube.work.trans;

import java.util.Date;
import java.util.List;

import com.kcube.doc.Item;
import com.kcube.sys.usr.User;

/**
 * 업무방 인수인계 정보
 * @author 신운재
 */
public class WorkTransfer extends Item
{
	private static final long serialVersionUID = -2936148682750508054L;

	/**
	 * 요청중
	 */
	public static final int STATUS_APPLIED = 3000;

	/**
	 * 반려
	 */
	public static final int STATUS_REJECTED = 4000;

	/**
	 * 승인
	 */
	public static final int STATUS_APPROVED = 5000;

	private User _reqUser;
	private User _resUser;
	private User _owner;

	private Date _resDate;
	private Date _reqDate;

	private String _reqComm;
	private String _resComm;

	private int _totalCount;

	private List<WorkTransferItem> _itemList;

	/**
	 * 요청자 정보를 돌려준다
	 * @return
	 */
	public User getReqUser()
	{
		return _reqUser;
	}

	/**
	 * 요청자 정보를 저장한다.
	 * @param reqUser
	 */
	public void setReqUser(User reqUser)
	{
		_reqUser = reqUser;
	}

	/**
	 * 인계자 정보를 돌려준다
	 * @return
	 */
	public User getResUser()
	{
		return _resUser;
	}

	/**
	 * 인계자 정보를 저장한다.
	 * @param reqUser
	 */
	public void setResUser(User resUser)
	{
		_resUser = resUser;
	}

	/**
	 * 인수자 정보를 돌려준다
	 * @return
	 */
	public User getOwner()
	{
		return _owner;
	}

	/**
	 * 인수자 정보를 저장한다.
	 * @param reqUser
	 */
	public void setOwner(User owner)
	{
		_owner = owner;
	}

	/**
	 * 응답시간 반환.
	 * @return
	 */
	public Date getResDate()
	{
		return _resDate;
	}

	/**
	 * 응답시간 저장.
	 * @param resDate
	 */
	public void setResDate(Date resDate)
	{
		_resDate = resDate;
	}

	/**
	 * 요청시간 반환.
	 * @return
	 */
	public Date getReqDate()
	{
		return _reqDate;
	}

	/**
	 * 요청시간 저장.
	 * @param reqDate
	 */
	public void setReqDate(Date reqDate)
	{
		_reqDate = reqDate;
	}

	/**
	 * 요청 내용 반환.
	 * @return
	 */
	public String getReqComm()
	{
		return _reqComm;
	}

	/**
	 * 요청 내용 저장.
	 * @param reqComm
	 */
	public void setReqComm(String reqComm)
	{
		_reqComm = reqComm;
	}

	/**
	 * 응답 내용 반환.
	 * @return
	 */
	public String getResComm()
	{
		return _resComm;
	}

	/**
	 * 응답 내용 저장.
	 * @param resComm
	 */
	public void setResComm(String resComm)
	{
		_resComm = resComm;
	}

	/**
	 * 인수인계 할 업무 총 갯수를 반환.
	 * @return
	 */
	public int getTotalCount()
	{
		return _totalCount;
	}

	/**
	 * 인수인계 할 업무 총 갯수를 저장.
	 * @param totalCount
	 */
	public void setTotalCount(int totalCount)
	{
		_totalCount = totalCount;
	}

	/**
	 * 업무방 리스트 반환.
	 * @return
	 */
	public List<WorkTransferItem> getItemList()
	{
		return _itemList;
	}

	/**
	 * 업무방 리스트 저장.
	 * @return
	 */
	public void setItemList(List<WorkTransferItem> itemList)
	{
		_itemList = itemList;
	}

	public static class WorkTransferItem
	{
		public WorkTransferItem()
		{

		}

		public WorkTransferItem(String title, Long workId)
		{
			_title = title;
			_workId = workId;
		}

		// private Long _transferId;
		private Long _workId;
		private String _title;

		/**
		 * 제목을 돌려준다.
		 * @return
		 */
		public String getTitle()
		{
			return _title;
		}

		/**
		 * 제목을 저장한다.
		 * @return
		 */
		public void setTitle(String title)
		{
			_title = title;
		}

		/**
		 * 부모 아이디 반환.
		 * @return
		 */
		// public Long getTransferId()
		// {
		// return _transferId;
		// }

		/**
		 * 부모 아이디 저장.
		 * @param itemId
		 */
		// public void setTransferId(Long transferId)
		// {
		// _transferId = transferId;
		// }

		/**
		 * 업무방 아이디 반환.
		 * @return
		 */
		public Long getWorkId()
		{
			return _workId;
		}

		/**
		 * 업무방 아이디 저장.
		 * @param workId
		 */
		public void setWorkId(Long workId)
		{
			_workId = workId;
		}
	}
}
