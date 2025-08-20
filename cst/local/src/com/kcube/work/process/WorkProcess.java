package com.kcube.work.process;

import java.util.Date;
import java.util.Set;

import com.kcube.lib.jdbc.DbObject;
import com.kcube.sys.usr.User;
import com.kcube.work.Work;

/**
 * @author 신운재
 *         <P>
 *         업무 보고 Bean class
 */
public class WorkProcess extends DbObject
{

	private static final long serialVersionUID = 1351013594381967955L;
	/**
	 * status - 상태값<br>
	 * 검토중, 검토, 승인중, 승인, 보완요청, 보완 후 완료, 보완 후 재승인 요청, 대면보고 요청.<br>
	 * <br>
	 * type - 프로세스 타입 <br>
	 * 검토, 승인, 회수 <br>
	 * <br>
	 * visible - 보임여부 <br>
	 */

	/**
	 * 검토중 상태
	 */
	public static final int REVIEWING_STATUS = 1000;

	/**
	 * 검토 상태
	 */
	public static final int REVIEW_CMPLT_STATUS = 1500;

	/**
	 * 승인중 상태
	 */
	public static final int APPROVAL_STATUS = 2000;

	/**
	 * 승인 상태
	 */
	public static final int APPROVAL_CMPLT_STATUS = 2500;

	/**
	 * 자가승인상태
	 */
	public static final int APPROVAL_CMPLT_SELF_STATUS = 2700;

	/**
	 * 보완요청 상태
	 */
	public static final int NEED_SUPPLEMENT_STATUS = 3000;

	/**
	 * 보완 후 완료 상태
	 */
	public static final int AFTER_SUPPLEMENT_COMPLETE_STATUS = 3300;

	/**
	 * 보완 후 재승인 요청
	 */
	public static final int AFTER_SUPPLEMENT_APPROVAL_STATUS = 3500;

	/**
	 * 대면보고 요청
	 */
	public static final int NEED_OFFMEETING_STATUS = 4000;

	/**
	 * 회수 상태
	 */
	public static final int RETRIEVE_STATUS = 9999;

	/**
	 * 검토 타입
	 */
	public static final int REVIEW_TYPE = 1000;
	/**
	 * 승인 타입
	 */
	public static final int APPROVAL_TYPE = 2000;
	/**
	 * 회수 타입
	 */
	public static final int RETRIEVE_TYPE = 3000;

	private Work item;

	private String content;
	private String resContent;
	private String title;

	private User inChage;
	private User checker;

	private Date reqDate;
	private Date cmpltDate;
	private Date eventDate;

	private int processType;

	private boolean read;
	private boolean visible;
	private boolean retrieve;
	private boolean meetingReq;

	private int status;

	private Set<ProcessComment> _processComments;
	private Long _classId;
	private Long _moduleId;
	private Long _appId;
	private Long _spaceId;
	private Long gid;

	/**
	 * 클래스 Id
	 * @return
	 */
	public Long getClassId()
	{
		return _classId;
	}

	/**
	 * 클래스 Id
	 * @param classId
	 */
	public void setClassId(Long classId)
	{
		_classId = classId;
	}

	/**
	 * 모듈 Id
	 * @return
	 */
	public Long getModuleId()
	{
		return _moduleId;
	}

	/**
	 * 모듈 Id
	 * @param moduleId
	 */
	public void setModuleId(Long moduleId)
	{
		_moduleId = moduleId;
	}

	/**
	 * 스페이스 Id
	 * @return
	 */
	public Long getSpaceId()
	{
		return _spaceId;
	}

	/**
	 * 스페이스 Id
	 * @param spaceId
	 */
	public void setSpaceId(Long spaceId)
	{
		_spaceId = spaceId;
	}

	/**
	 * 제목
	 * @return
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * 제목
	 * @param title
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * 상태값
	 * @return
	 */
	public int getStatus()
	{
		return status;
	}

	/**
	 * 상태값
	 * @param status
	 */
	public void setStatus(int status)
	{
		this.status = status;
	}

	/**
	 * 보임여부
	 * @return
	 */
	public boolean isVisible()
	{
		return visible;
	}

	/**
	 * 보임여부
	 * @param visible
	 */
	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}

	/**
	 * 그룹일련번호
	 * @param gid
	 */
	public void setGid(Long gid)
	{
		this.gid = gid;
	}

	/**
	 * 그룹일련번호
	 * @return
	 */
	public Long getGid()
	{
		return gid;
	}

	/**
	 * 이벤트 일시
	 * @return
	 */
	public Date getEventDate()
	{
		return eventDate;
	}

	/**
	 * 이벤트 일시
	 * @param eventDate
	 */
	public void setEventDate(Date eventDate)
	{
		this.eventDate = eventDate;
	}

	/**
	 * 읽음여부
	 * @return
	 */
	public boolean isRead()
	{
		return read;
	}

	/**
	 * 읽음여부
	 * @param read
	 */
	public void setRead(boolean read)
	{
		this.read = read;
	}

	/**
	 * 내용
	 * @return
	 */
	public String getContent()
	{
		return content;
	}

	/**
	 * 내용
	 * @param content
	 */
	public void setContent(String content)
	{
		this.content = content;
	}

	/**
	 * 프로세스 타입
	 * @return
	 */
	public int getProcessType()
	{
		return processType;
	}

	/**
	 * 프로세스 타입
	 * @param processType
	 */
	public void setProcessType(int processType)
	{
		this.processType = processType;
	}

	/**
	 * 요청일시
	 * @return
	 */
	public Date getReqDate()
	{
		return reqDate;
	}

	/**
	 * 요청일시
	 * @param reqDate
	 */
	public void setReqDate(Date reqDate)
	{
		this.reqDate = reqDate;
	}

	/**
	 * 완료일자
	 * @return
	 */
	public Date getCmpltDate()
	{
		return cmpltDate;
	}

	/**
	 * 완료일자
	 * @param cmpltDate
	 */
	public void setCmpltDate(Date cmpltDate)
	{
		this.cmpltDate = cmpltDate;
	}

	/**
	 * 담당자
	 * @return
	 */
	public User getInChage()
	{
		return inChage;
	}

	/**
	 * 담당자
	 * @param inChage
	 */
	public void setInChage(User inChage)
	{
		this.inChage = inChage;
	}

	/**
	 * 검토/승인자
	 * @return
	 */
	public User getChecker()
	{
		return checker;
	}

	/**
	 * 검토/승인자
	 * @param checker
	 */
	public void setChecker(User checker)
	{
		this.checker = checker;
	}

	/**
	 * 업무방
	 * @return
	 */
	public Work getItem()
	{
		return item;
	}

	/**
	 * 업무방
	 * @param item
	 */
	public void setItem(Work item)
	{
		this.item = item;
	}

	/**
	 * 응답내용
	 * @return
	 */
	public String getResContent()
	{
		return resContent;
	}

	/**
	 * 응답내용
	 * @param repContent
	 */
	public void setResContent(String repContent)
	{
		this.resContent = repContent;
	}

	/**
	 * 회수여부
	 * @return
	 */
	public boolean isRetrieve()
	{
		return retrieve;
	}

	/**
	 * 회수여부
	 * @param retrieve
	 */
	public void setRetrieve(boolean retrieve)
	{
		this.retrieve = retrieve;
	}

	/**
	 * 앱 Id
	 * @return
	 */
	public Long getAppId()
	{
		return _appId;
	}

	/**
	 * 앱 Id
	 * @param appId
	 */
	public void setAppId(Long appId)
	{
		_appId = appId;
	}

	public Set<ProcessComment> getProcessComments()
	{
		return _processComments;
	}

	public void setProcessComments(Set<ProcessComment> processComments)
	{
		_processComments = processComments;
	}

	public boolean isMeetingReq()
	{
		return meetingReq;
	}

	public void setMeetingReq(boolean meetingReq)
	{
		this.meetingReq = meetingReq;
	}

	/**
	 * 붙임파일 나타내는 클래스이다.
	 */
	public static class ProcessComment extends DbObject
	{
		private static final long serialVersionUID = -8864242585526327932L;
		private Long _prcsId;
		private String _comment;
		private Date _instDate;
		private int _status;

		public int getStatus()
		{
			return _status;
		}

		public void setStatus(int status)
		{
			_status = status;
		}

		public Long getPrcsId()
		{
			return _prcsId;
		}

		public void setPrcsId(Long prcsId)
		{
			_prcsId = prcsId;
		}

		public String getComment()
		{
			return _comment;
		}

		public void setComment(String comment)
		{
			_comment = comment;
		}

		public Date getInstDate()
		{
			return _instDate;
		}

		public void setInstDate(Date instDate)
		{
			_instDate = instDate;
		}

	}
}
