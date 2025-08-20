package com.kcube.work.history;

import java.util.Date;

public class WorkItemHistory
{

	/**
	 * 공감등록
	 */
	public static final String ADD_SYMPATHY = "add_sympathy";

	/**
	 * 공감삭제
	 */
	public static final String DEL_SYMPATHY = "del_sympathy";

	/**
	 * 의견등록
	 */
	public static final String ADD_OPINION = "add_opinion";

	/**
	 * 의견수정
	 */
	public static final String UPDATE_OPINION = "update_opinion";

	/**
	 * 의견삭제
	 */
	public static final String DELETE_OPINION = "delete_opinion";

	/**
	 * 보고서 등록됨.
	 */
	public static final String REPORT_ADD = "report_add";

	/**
	 * 보고서 버전업
	 */
	public static final String REPORT_VERSIONUP = "report_versionup";

	/**
	 * 보고서 삭제
	 */
	public static final String REPORT_DELETE = "report_delete";

	/**
	 * 보고서 다운로드
	 */
	public static final String REPORT_DOWNLOAD = "report_download";

	/**
	 * 첨부파일 등록됨.
	 */
	public static final String ATTACHMENT_ADD = "workAttachment_add";
	/**
	 * 첨부파일 버전업.
	 */
	public static final String ATTACHMENT_VERSIONUP = "workAttachment_versionup";

	/**
	 * 첨부파일 삭제.
	 */
	public static final String ATTACHMENT_DELETE = "workAttachment_delete";

	/**
	 * 첨부파일 다운로드.
	 */
	public static final String ATTACHMENT_DOWNLOAD = "workAttachment_download";

	/**
	 * 참고자료 파일 추가.
	 */
	public static final String REFERENCE_FILE_ADD = "reference_file_add";

	/**
	 * 참고자료 URL 추가.
	 */
	public static final String REFERENCE_URL_ADD = "reference_url_add";

	/**
	 * 참고자료 삭제.
	 */
	public static final String REFERENCE_DELETE = "reference_delete";

	/**
	 * 참고자료 다운로드.
	 */
	public static final String REFERENCE_DOWNLOAD = "reference_download";

	/**
	 * 협업자 추가
	 */
	public static final String ADD_HELPER = "add_helper";

	/**
	 * 협업자 삭제
	 */
	public static final String DELETE_HELPER = "delete_helper";

	/**
	 * 공유자 추가
	 */
	public static final String ADD_SHARE = "add_share";

	/**
	 * 공유자 삭제
	 */
	public static final String DELETE_SHARE = "delete_share";

	/**
	 * 진행중 상태로 변경
	 */
	public static final String CHANGE_WORKING = "change_working";

	/**
	 * 완료 상태로 변경
	 */
	public static final String CHANGE_COMPLETE = "change_complete";

	/**
	 * 계획 추가
	 */
	public static final String ADD_PLAN = "add_plan";

	/**
	 * 계획 삭제
	 */
	public static final String REMOVE_PLAN = "remove_plan";

	/**
	 * 기능 선택 변경
	 */
	public static final String UPDATE_ITEMVISIBLE = "update_itemVisible";

	/**
	 * 승인자/검토자/보고 요약문을 변경
	 */
	public static final String UPDATE_APPRSETTING = "update_apprsetting";

	/**
	 * 검토 요청
	 */
	public static final String REQUEST_REVIEW = "request_review";

	/**
	 * 승인 요청
	 */
	public static final String REQUEST_APPROVAL = "request_approval";

	/**
	 * 검토함.
	 */
	public static final String PROCESSED_REVIEW = "processed_review";

	/**
	 * 검토 완료
	 */
	public static final String COMPLETE_REVIEW = "complete_review";

	/**
	 * 승인 완료
	 */
	public static final String COMPLETE_APPROVAL = "complete_approval";

	/**
	 * 보완 요청
	 */
	public static final String NEED_MORE_REQ = "request_need_supplement";

	/**
	 * 대면 요청
	 */
	public static final String NEED_MEETING_REQ = "request_need_meeting";

	/**
	 * 승인 요청 중 회수
	 */
	public static final String RETRIEVED_APPROVAL = "retrieved_approval";

	/**
	 * 담당자 변경
	 */
	public static final String CHANGED_INCHARGER = "changed_incharger";

	/**
	 * 검토 요청 중 회수
	 */
	public static final String RETRIEVED_REVIEW = "retrieved_review";

	/**
	 * 업무방 삭제시
	 */
	public static final String DELETE = "delete";

	/**
	 * 업무방 보안레벨 변경시
	 */
	public static final String CHANGE_SECURE = "change_secure";

	private long _historyId;
	private String _event;
	private Date _instDate;
	private String _title;
	private long _userId;
	private String _userName;
	private String _userDisp;

	private long _refUserId;
	private String _refUserName;
	private String _refUserDisp;
	private String _refUrl;

	private long _itemId;

	private Long _fileId;
	private Long _fileGid;
	private String _fileVersionLabel;
	private String _fileName;

	private Long _classId;
	private Long _moduleId;
	private Long _spaceId;
	private Long _appId;

	public long getHistoryId()
	{
		return _historyId;
	}

	public void setHistoryId(long historyId)
	{
		_historyId = historyId;
	}

	public String getEvent()
	{
		return _event;
	}

	public void setEvent(String event)
	{
		_event = event;
	}

	public Date getInstDate()
	{
		return _instDate;
	}

	public void setInstDate(Date instDate)
	{
		_instDate = instDate;
	}

	public String getTitle()
	{
		return _title;
	}

	public void setTitle(String title)
	{
		_title = title;
	}

	public long getUserId()
	{
		return _userId;
	}

	public void setUserId(long userId)
	{
		_userId = userId;
	}

	public String getUserName()
	{
		return _userName;
	}

	public void setUserName(String userName)
	{
		_userName = userName;
	}

	public String getUserDisp()
	{
		return _userDisp;
	}

	public void setUserDisp(String userDisp)
	{
		_userDisp = userDisp;
	}

	public long getRefUserId()
	{
		return _refUserId;
	}

	public void setRefUserId(long refUserId)
	{
		_refUserId = refUserId;
	}

	public String getRefUserName()
	{
		return _refUserName;
	}

	public void setRefUserName(String refUserName)
	{
		_refUserName = refUserName;
	}

	public String getRefUserDisp()
	{
		return _refUserDisp;
	}

	public void setRefUserDisp(String refUserDisp)
	{
		_refUserDisp = refUserDisp;
	}

	public String getRefUrl()
	{
		return _refUrl;
	}

	public void setRefUrl(String refUrl)
	{
		_refUrl = refUrl;
	}

	public long getItemId()
	{
		return _itemId;
	}

	public void setItemId(long itemId)
	{
		_itemId = itemId;
	}

	public Long getFileId()
	{
		return _fileId;
	}

	public void setFileId(Long fileId)
	{
		_fileId = fileId;
	}

	public Long getFileGid()
	{
		return _fileGid;
	}

	public void setFileGid(Long fileGid)
	{
		_fileGid = fileGid;
	}

	public String getFileVersionLabel()
	{
		return _fileVersionLabel;
	}

	public void setFileVersionLabel(String fileVersionLabel)
	{
		_fileVersionLabel = fileVersionLabel;
	}

	public String getFileName()
	{
		return _fileName;
	}

	public void setFileName(String fileName)
	{
		_fileName = fileName;
	}

	public Long getModuleId()
	{
		return _moduleId;
	}

	public void setModuleId(Long moduleId)
	{
		_moduleId = moduleId;
	}

	public Long getClassId()
	{
		return _classId;
	}

	public void setClassId(Long classId)
	{
		_classId = classId;
	}

	public Long getSpaceId()
	{
		return _spaceId;
	}

	public void setSpaceId(Long spaceId)
	{
		_spaceId = spaceId;
	}

	public Long getAppId()
	{
		return _appId;
	}

	public void setAppId(Long appId)
	{
		_appId = appId;
	}
}
