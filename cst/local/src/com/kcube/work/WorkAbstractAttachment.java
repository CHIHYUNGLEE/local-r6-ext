package com.kcube.work;

import java.util.Date;

import com.kcube.doc.file.Attachment;
import com.kcube.sys.usr.User;
import com.kcube.sys.usr.UserService;

/**
 * 업무방에서 버전에 관련된 첨부에 대한 Abstract Class
 * @author WJ
 */
public abstract class WorkAbstractAttachment extends Attachment
{
	private static final long serialVersionUID = -55519200826849525L;

	/**
	 * 파일 최초 등록자
	 */
	private User _rgst;

	/**
	 * 파일 수정자
	 */
	private User _checker;

	/**
	 * 파일 그룹 아이디
	 */
	private Long _gid;

	/**
	 * 파일 버전
	 */
	private Long _vrsn;

	/**
	 * 파일 확장자
	 */
	private String _fileExt;

	/**
	 * 해당 파일그룹의 마지막 버전 여부
	 */
	private boolean _lastVersion;

	/**
	 * 파일이 등록된 날짜
	 */
	private Date _rgstDate;

	/**
	 * 체크아웃 여부
	 */
	private boolean _checkOut;

	public boolean isCheckOut()
	{
		return _checkOut;
	}

	public void setCheckOut(boolean checktout)
	{
		_checkOut = checktout;
	}

	public Date getRgstDate()
	{
		return _rgstDate;
	}

	public void setRgstDate(Date rgstDate)
	{
		_rgstDate = rgstDate;
	}

	/**
	 * 파일 확장자를 돌려줌
	 * @return
	 */
	public String getFileExt()
	{
		return _fileExt;
	}

	public void setFileExt(String fileExt)
	{
		_fileExt = fileExt;
	}

	/**
	 * 버전 정보가 없으면 1로 반환.
	 * @return
	 */
	public Long getVrsn()
	{
		if (_vrsn == null)
			_vrsn = (long) 1;
		return _vrsn;
	}

	public void setVrsn(Long vrsn)
	{
		_vrsn = vrsn;
	}

	public boolean isLastVersion()
	{
		return _lastVersion;
	}

	public void setLastVersion(boolean lastVersion)
	{
		_lastVersion = lastVersion;
	}

	/**
	 * 최초 설정된 gid가 없는 경우<br>
	 * fileid 값을 gid로 설정한다.
	 * @return
	 */
	public Long getGid()
	{
		if (_gid == null)
			setGid(getId());
		return _gid;
	}

	public void setGid(Long gid)
	{
		_gid = gid;
	}

	public User getChecker()
	{
		return _checker;
	}

	public void setChecker(User checker)
	{
		_checker = checker;
	}

	public User getRgst()
	{
		return _rgst;
	}

	public void setRgst(User rgst)
	{
		_rgst = rgst;
	}

	/**
	 * 체크아웃 사용자와 같은 사용자 인지 여부를 돌려준다.
	 * @return
	 */
	public boolean isCurrentUser()
	{
		Long userId = UserService.getUserId();
		if (userId == null)
		{
			return false;
		}
		User checker = getChecker();
		if (checker == null)
		{
			return false;
		}
		return (userId.equals(checker.getUserId()));
	}
}