package com.kcube.work;

import com.kcube.doc.file.Attachment;

/**
 * 참고자료
 * @author 신운재
 */
public class WorkReference extends Attachment
{
	private static final long serialVersionUID = -7331045158649562746L;

	/**
	 * 파일 타입
	 */
	public static final int FILETYPE = 1000;

	/**
	 * URL 타입(바로가기)
	 */
	public static final int URLTYPE = 2000;

	/**
	 * 즐겨찾기
	 */
	public static final int FVRTTYPE = 3000;

	private String _title;
	private String _query;
	private String _method;
	private String _src;

	private int _rfrnCode;
	private Long _rgstUserId;

	public int getRfrnCode()
	{
		return _rfrnCode;
	}

	public void setRfrnCode(int rfrnCode)
	{
		_rfrnCode = rfrnCode;
	}

	public Long getRgstUserId()
	{
		return _rgstUserId;
	}

	public void setRgstUserId(Long rgstUserId)
	{
		_rgstUserId = rgstUserId;
	}

	public String getTitle()
	{
		return _title;
	}

	public void setTitle(String title)
	{
		_title = title;
	}

	public String getQuery()
	{
		return _query;
	}

	public void setQuery(String query)
	{
		_query = query;
	}

	public String getMethod()
	{
		return _method;
	}

	public void setMethod(String method)
	{
		_method = method;
	}

	public String getSrc()
	{
		return _src;
	}

	public void setSrc(String src)
	{
		_src = src;
	}
}