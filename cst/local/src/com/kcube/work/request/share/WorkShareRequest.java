package com.kcube.work.request.share;

import java.util.Date;

import com.kcube.lib.jdbc.DbObject;
import com.kcube.sys.usr.User;

/**
 * 공유 요청 관련 Bean
 */
public class WorkShareRequest extends DbObject
{
	private static final long serialVersionUID = 632510119091932642L;

	/**
	 * 반려
	 */
	public static final int STATUS_REJECTED = 1000;

	/**
	 * 요청중
	 */
	public static final int STATUS_APPLIED = 2000;

	/**
	 * 승인
	 */
	public static final int STATUS_APPROVED = 3000;

	private Long _workId;
	private Date _reqDate;
	private User _reqUser;
	private String _reqComment;
	private int _status;

	private Date _resDate;
	private User _resUser;
	private String _resComment;
	private boolean _del;

	private String _title;

	/**
	 * 업무방 일련번호를 돌려준다.
	 */
	public Long getWorkId()
	{
		return _workId;
	}

	public void setWorkId(Long workId)
	{
		_workId = workId;
	}

	/**
	 * 요청자를 돌려준다.
	 */
	public User getReqUser()
	{
		return _reqUser;
	}

	public void setReqUser(User reqUser)
	{
		_reqUser = reqUser;
	}

	/**
	 * 요청사유를 돌려준다.
	 */
	public String getReqComment()
	{
		return _reqComment;
	}

	public void setReqComment(String reqComment)
	{
		_reqComment = reqComment;
	}

	/**
	 * 요청일시를 돌려준다.
	 */
	public Date getReqDate()
	{
		return _reqDate;
	}

	public void setReqDate(Date reqDate)
	{
		_reqDate = reqDate;
	}

	/**
	 * 처리일시를 돌려준다.
	 */
	public Date getResDate()
	{
		return _resDate;
	}

	public void setResDate(Date resDate)
	{
		_resDate = resDate;
	}

	/**
	 * 상태를 돌려준다.
	 */
	public int getStatus()
	{
		return _status;
	}

	public void setStatus(int status)
	{
		_status = status;
	}

	/**
	 * 처리자를 돌려준다.
	 */
	public User getResUser()
	{
		return _resUser;
	}

	public void setResUser(User resUser)
	{
		_resUser = resUser;
	}

	/**
	 * 처리사유를 돌려준다.
	 */
	public String getResComment()
	{
		return _resComment;
	}

	public void setResComment(String resComment)
	{
		_resComment = resComment;
	}

	/**
	 * 공유 요청의 삭제여부를 돌려준다.
	 */
	public boolean isDel()
	{
		return _del;
	}

	public void setDel(boolean del)
	{
		_del = del;
	}

	/**
	 * Title을 돌려준다.(for Alimi)
	 */
	public String getTitle()
	{
		return _title;
	}

	public void setTitle(String title)
	{
		_title = title;
	}

}
