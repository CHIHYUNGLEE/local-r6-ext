package com.kcube.work.report;

import com.kcube.sys.usr.User;

/**
 * 업무보고서 템플릿 Bean class
 */
public class ReportTemplate extends com.kcube.doc.Item
{
	private static final long serialVersionUID = -1016580631783238954L;

	private User _lastUpdtAuthor;
	private ReportTemplateAttachment _attachment;
	private int _dnldCnt;

	/**
	 * 첨부파일 정보를 돌려준다.
	 * @return
	 */
	public ReportTemplateAttachment getAttachment()
	{
		if (null != _attachment && null == _attachment.getId())
		{
			_attachment.setId(this.getId());
		}
		return _attachment;
	}

	/**
	 * 첨부파일 정보
	 * @param attachment
	 */
	public void setAttachment(ReportTemplateAttachment attachment)
	{
		_attachment = attachment;
	}

	/**
	 * 마지막 수정자를 돌려준다.
	 * @return
	 */
	public User getLastUpdtAuthor()
	{
		return _lastUpdtAuthor;
	}

	public void setLastUpdtAuthor(User lastUpdtAuthor)
	{
		_lastUpdtAuthor = lastUpdtAuthor;
	}

	/**
	 * 첨부파일의 다운로드수를 돌려준다.
	 * @return 다운로드수
	 */
	public int getDnldCnt()
	{
		return _dnldCnt;
	}

	public void setDnldCnt(int dnldCnt)
	{
		_dnldCnt = dnldCnt;
	}

	/**
	 * 첨부파일을 나타내는 클래스이다.
	 */
	public static class ReportTemplateAttachment extends com.kcube.doc.file.Attachment
	{
		private static final long serialVersionUID = -5626639277941630809L;
	}
}
