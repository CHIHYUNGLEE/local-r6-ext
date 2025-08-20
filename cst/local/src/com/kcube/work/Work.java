package com.kcube.work;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.kcube.doc.Item;
import com.kcube.sys.usr.User;
import com.kcube.sys.usr.UserService;
import com.kcube.work.process.WorkProcess;

/**
 * 업무방 Bean class
 */
public class Work extends Item
{
	private static final long serialVersionUID = 8226893909491199950L;

	/**
	 * 일반사용자
	 */
	public static final int NORMAL_PERSON = 1000;

	/**
	 * 부서장 사용자
	 */
	public static final int HEAD_DPRT_PERSON = 2000;

	/**
	 * 임원 코드
	 */
	public static final int EXECUTIVE_PERSON = 3000;

	/**
	 * 작업중 상태
	 */
	public static final int WORKING_STATUS = 3500;

	/**
	 * 보고중 상태
	 */
	public static final int REPORTING_STATUS = 4000;

	/**
	 * 승인중 상태
	 */
	public static final int APPROVAL_STATUS = 4500;

	/**
	 * 보완요청 상태
	 */
	public static final int NEED_SUPPLEMENT_STATUS = 4510;

	/**
	 * 검토중 상태
	 */
	public static final int REVIEW_STATUS = 5000;

	/**
	 * 검토 완료 상태
	 */
	public static final int REVIEW_CMPLT_STATUS = 5010;

	/**
	 * 승인 완료 상태
	 */
	public static final int APRV_CMPLT_STATUS = 6000;

	/**
	 * 완료 상태
	 */
	public static final int COMPLETE_STATUS = 7000;

	/**
	 * 전부서 보안
	 */
	public static final int SECURE_NONE = 0;

	/**
	 * 담당자 보안
	 */
	public static final int SECURE_ACTOR = 6000;

	/**
	 * 협업자 보안
	 */
	public static final int SECURE_HELPER = 4100;

	/**
	 * 범위설정 보안
	 */
	public static final int SECURE_RANGE = 4200;

	/**
	 * 목록숨김 보안
	 */
	public static final int SECURE_LIST = 2000;

	private Date _completeDate;
	private int _planCnt;
	private String _comm;
	private String _chargeDprtName;
	private Long _chargeDprtId;
	private User _actor;
	private String _itemsVisible;
	private boolean _isHelper;
	private boolean _isShare;
	private List<User> _helpers;
	private List<User> _sharers;
	private boolean _fvrt;
	private boolean _transfer;
	private int beforeStatus;
	private Long _totalFileSize;
	private Long _lastProcessGid;
	private List<User> _reviewers;
	private User _approver;
	private String _reportSketch;
	private ApprReviewSetting _apprReviewSetting;

	private Set<? extends WorkReference> _workReferences;
	private Set<? extends ReportFile> _reportFiles;
	private Set<? extends WorkAttachment> _workAttachments;
	private Set<WorkProcess> _workProcess;

	/**
	 * 마지막 프로세스 그룹아이디 반환
	 * @return
	 */
	public Long getLastProcessGid()
	{
		return _lastProcessGid;
	}

	/**
	 * 마지막 프로세스 그룹아이디 셋
	 * @param lastProcessGid
	 */
	public void setLastProcessGid(Long lastProcessGid)
	{
		_lastProcessGid = lastProcessGid;
	}

	/**
	 * 검토/승인 설정 셋
	 * @param set
	 */
	public void setApprReviewSetting(ApprReviewSetting set)
	{
		setApprover(set.getApprover());
		setReviewers(set.getReviewers());
		setReportSketch(set.getReportSketch());
	}

	/**
	 * 검토/승인 설정 반환
	 * @return
	 */
	public ApprReviewSetting getApprReviewSetting()
	{
		_apprReviewSetting = new ApprReviewSetting();
		_apprReviewSetting.setApprover(getApprover());
		_apprReviewSetting.setReviewers(getReviewers());
		_apprReviewSetting.setReportSketch(getReportSketch());
		return _apprReviewSetting;
	}

	/**
	 * 승인자 반환
	 * @return
	 */
	public User getApprover()
	{
		return _approver;
	}

	/**
	 * 승인자 셋
	 * @param approver
	 */
	public void setApprover(User approver)
	{
		_approver = approver;
	}

	/**
	 * 현재 사용자가 승인자인지 여부를 돌려준다.
	 * @return
	 */
	public boolean isCurrentApprover()
	{
		Long userId = UserService.getUserId();
		if (userId == null)
		{
			return false;
		}
		User approver = getApprover();
		if (approver == null)
		{
			return false;
		}
		return (userId.equals(approver.getUserId()));
	}

	/**
	 * 보고서 요약문
	 * @return
	 */
	public String getReportSketch()
	{
		return _reportSketch;
	}

	/**
	 * 보고서 요약문 셋
	 * @param reportSketch
	 */
	public void setReportSketch(String reportSketch)
	{
		_reportSketch = reportSketch;
	}

	/**
	 * 검토자 반환
	 * @return
	 */
	public List<User> getReviewers()
	{
		return _reviewers;
	}

	/**
	 * 현재 사용자가 검토자인지 여부를 돌려준다.
	 * @return
	 */
	public boolean isCurrentReviewer()
	{
		Long userId = UserService.getUserId();
		if (userId == null)
		{
			return false;
		}
		List<User> reviewers = getReviewers();
		if (reviewers != null && reviewers.size() > 0)
		{
			for (User c : reviewers)
			{
				if (c != null && userId.equals(c.getUserId()))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 검토자 셋
	 * @param reviewers
	 */
	public void setReviewers(List<User> reviewers)
	{
		_reviewers = reviewers;
	}

	/**
	 * 참고자료
	 * @return
	 */
	public Set<? extends WorkReference> getWorkReferences()
	{
		return _workReferences;
	}

	/**
	 * 참고자료 셋
	 * @param workReferences
	 */
	public void setWorkReferences(Set<? extends WorkReference> workReferences)
	{
		_workReferences = workReferences;
	}

	/**
	 * 중요업무 여부를 돌려준다.
	 */
	public boolean isFvrt()
	{
		return _fvrt;
	}

	/**
	 * 중요업무 셋
	 * @param fvrt
	 */
	public void setFvrt(boolean fvrt)
	{
		_fvrt = fvrt;
	}

	/**
	 * 업무프로세스 반환
	 * @return
	 */
	public Set<WorkProcess> getWorkProcess()
	{
		return _workProcess;
	}

	/**
	 * 프로세스 셋
	 * @param workProcess
	 */
	public void setWorkProcess(Set<WorkProcess> workProcess)
	{
		_workProcess = workProcess;
	}

	/**
	 * 보고서를 돌려준다.
	 * @return
	 */
	public Set<? extends ReportFile> getReportFiles()
	{
		return _reportFiles;
	}

	/**
	 * 보고서 반환
	 * @param reportFile
	 */
	public void setReportFiles(Set<? extends ReportFile> reportFile)
	{
		_reportFiles = reportFile;
	}

	/**
	 * 붙임파일을 돌려준다.
	 */
	public Set<? extends WorkAttachment> getWorkAttachments()
	{
		return _workAttachments;
	}

	/**
	 * 붙임파일을 설정한다.
	 * @param workAttachments
	 */
	public void setWorkAttachments(Set<? extends WorkAttachment> workAttachments)
	{
		_workAttachments = workAttachments;
	}

	/**
	 * 완료일시를 돌려준다.
	 */
	public Date getCompleteDate()
	{
		return _completeDate;
	}

	/**
	 * 완료일시를 설정한다.
	 * @param completeDate
	 */
	public void setCompleteDate(Date completeDate)
	{
		_completeDate = completeDate;
	}

	/**
	 * Work에 등록된 계획의 건수를 돌려준다.
	 * @return
	 */
	public int getPlanCnt()
	{
		return _planCnt;
	}

	/**
	 * Work에 등록된 계획의 건수를 설정한다.
	 * @param planCnt
	 */
	public void setPlanCnt(int planCnt)
	{
		_planCnt = planCnt;
	}

	/**
	 * 업무 요약을 돌려준다.
	 * @return
	 */
	public String getComm()
	{
		return _comm;
	}

	/**
	 * 업무요약을 설정한다.
	 * @param comm
	 */
	public void setComm(String comm)
	{
		_comm = comm;
	}

	/**
	 * 담당자를 돌려준다.
	 * @return
	 */
	public User getActor()
	{
		return _actor;
	}

	/**
	 * 담당자를 설정한다.
	 * @param actor
	 */
	public void setActor(User actor)
	{
		_actor = actor;
	}

	/**
	 * 담당자인지 여부를 판단한다.
	 * @return
	 */
	public boolean isCurrentActor()
	{
		Long userId = UserService.getUserId();
		if (userId == null)
		{
			return false;
		}
		User actor = getActor();
		if (actor == null)
		{
			return false;
		}
		return (userId.equals(actor.getUserId()));
	}

	/**
	 * 주관 부서 일련번호를 돌려준다.
	 */
	public Long getChargeDprtId()
	{
		return _chargeDprtId;
	}

	/**
	 * 주관 부서 일련번호를 설정한다.
	 * @param chargeDprtId
	 */
	public void setChargeDprtId(Long chargeDprtId)
	{
		_chargeDprtId = chargeDprtId;
	}

	/**
	 * 주관 부서명을 돌려준다.
	 * @return
	 */
	public String getChargeDprtName()
	{
		return _chargeDprtName;
	}

	/**
	 * 주관 부서명을 설정한다.
	 * @param chargeDprtName
	 */
	public void setChargeDprtName(String chargeDprtName)
	{
		_chargeDprtName = chargeDprtName;
	}

	/**
	 * 업무방 내부 요소의 visible 여부 돌려준다. (JSON 형태)
	 * @return
	 */
	public String getItemsVisible()
	{
		return _itemsVisible;
	}

	/**
	 * 업무방 내부 요소의 visible 여부 설정한다. (JSON 형태)
	 * @param itemsVisible
	 */
	public void setItemsVisible(String itemsVisible)
	{
		_itemsVisible = itemsVisible;
	}

	/**
	 * 협업자가 있는지 여부를 돌려준다.
	 * @return
	 */
	public boolean getIsHelper()
	{
		return _isHelper;
	}

	/**
	 * 협업자가 있는지 여부를 설정한다.
	 * @param isHelper
	 */
	public void setIsHelper(boolean isHelper)
	{
		_isHelper = isHelper;
	}

	/**
	 * 협업자 여부를 돌려준다
	 * @return
	 * @throws Exception
	 */
	public boolean isCurrentHelper() throws Exception
	{
		Long userId = UserService.getUserId();
		if (userId == null)
		{
			return false;
		}
		if (getHelpers() != null && getHelpers().size() > 0)
		{
			for (User c : getHelpers())
			{
				if (userId.equals(c.getUserId()))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 협업자들을 돌려준다.
	 * @return
	 */
	public List<User> getHelpers()
	{
		return _helpers;
	}

	/**
	 * 협업자들을 설정한다.
	 * @param helpers
	 */
	public void setHelpers(List<User> helpers)
	{
		_helpers = helpers;
	}

	/**
	 * 공유자들을 돌려준다.
	 * @return
	 */
	public List<User> getSharers()
	{
		return _sharers;
	}

	/**
	 * 공유자들을 설정한다.
	 * @param sharers
	 */
	public void setSharers(List<User> sharers)
	{
		_sharers = sharers;
	}

	/**
	 * 공유자가 있는지 여부를 돌려준다.
	 * @return
	 */
	public boolean getIsShare()
	{
		return _isShare;
	}

	/**
	 * 공유자가 있는 여부를 설정한다.
	 * @param isShare
	 */
	public void setIsShare(boolean isShare)
	{
		_isShare = isShare;
	}

	/**
	 * attachments와 fileExt를 설정한다.
	 * <p>
	 * 각각의 Attachment에는 현재 Item 객체가 설정된다.
	 */
	public void updateWorkAttachments(Set<? extends WorkAttachment> attachments)
	{
		if (attachments != null)
		{
			for (WorkAttachment att : attachments)
			{
				att.setRgst(UserService.getUser());
				att.setRgstDate(new Date());
				if (att.getType() != null && att.getType().intValue() != 7000 && att.getFilename() != null)
				{
					att.setFileExt(att.getExtension());
				}
			}
		}
		setWorkAttachments(attachments);
	}

	/**
	 * @author 신운재
	 *         <p>
	 *         보고서 파일을 저장하여 등록자와<br>
	 *         등록 일시를 설정한다.<br>
	 *         attachments와 fileExt를 설정한다.
	 *         <p>
	 *         각각의 Attachment에는 현재 Item 객체가 설정된다.
	 */
	public void updateReportFile(Set<? extends ReportFile> attachments)
	{
		if (attachments != null)
		{
			for (ReportFile att : attachments)
			{
				att.setRgst(UserService.getUser());
				att.setRgstDate(new Date());
				if (att.getType() != null && att.getType().intValue() != 7000 && att.getFilename() != null)
				{
					att.setFileExt(att.getExtension());
				}
			}
		}
		setReportFiles(attachments);
	}

	/**
	 * 의견을 나타내는 클래스이다.
	 */
	public static class Opinion extends com.kcube.doc.opn.Opinion
	{
		private static final long serialVersionUID = -444309746009030298L;

		/**
		 * 현재 사용자가 의견의 작성자 인지의 여부를 돌려준다.
		 */
		public boolean isCurrentOwner()
		{
			Long userId = UserService.getUserId();
			if (userId == null)
			{
				return false;
			}
			User rgstUser = getRgstUser();
			if (rgstUser == null)
			{
				return false;
			}
			return (userId.equals(getRgstUser().getUserId()));
		}

		/**
		 * 현재 사용자가 의견의 작성자 인지의 여부를 설정한다.
		 * @param currentOwner
		 */
		public void setCurrentOwner(boolean currentOwner)
		{
		}
	}

	/**
	 * Json 값을 getFolder , setFolder에 값을 매핑할때만 이용 된다.
	 */
	public Item.Folder.Json getJsonFolder()
	{
		Item.Folder folder = getFolder();
		if (folder == null)
		{
			List<Work.Folder> l = getFolders();
			if (l != null)
			{
				for (Work.Folder f : l)
				{
					if (!f.isComputed())
					{
						folder = f;
						break;
					}
				}
			}
		}
		if (folder == null)
			return null;
		Item.Folder.Json jsonFolder = new Item.Folder.Json(folder);
		return jsonFolder;
	}

	/**
	 * 파일에 대한 첨부 용량을 현재 첨부 객체를 기준으로 계산하여 돌려준다.
	 * <p>
	 * 파일 용량을 갱신하여 반영할 때 사용한다.
	 * @return
	 */
	public long getManualTotalFileSize()
	{
		long totalFileSize = 0;
		Set<? extends ReportFile> reportFiles = getReportFiles();
		if (reportFiles != null)
		{
			for (ReportFile rf : reportFiles)
			{
				totalFileSize += rf.getFilesize();
			}
		}

		Set<? extends WorkAttachment> attachments = getWorkAttachments();
		if (attachments != null)
		{
			for (WorkAttachment wa : attachments)
			{
				totalFileSize += wa.getFilesize();
			}
		}

		Set<? extends WorkReference> references = getWorkReferences();
		if (references != null)
		{
			for (WorkReference wf : references)
			{
				if (wf.getRfrnCode() == WorkReference.FILETYPE)
				{
					totalFileSize += wf.getFilesize();
				}
			}
		}
		return totalFileSize;
	}

	/**
	 * 파일 사이즈를 돌려준다.
	 * <p>
	 * 이미 set된 객체가 있을 때에는
	 */
	public long getTotalFileSize()
	{
		if (_totalFileSize != null)
		{
			return _totalFileSize;
		}
		else
		{
			return getManualTotalFileSize();
		}
	}

	/**
	 * 파일 사이즈를 설정한다.
	 */
	public void setTotalFileSize(Long totalFileSize)
	{
		_totalFileSize = totalFileSize;
	}

	/**
	 * 인수인계 여부를 돌려준다.
	 * @return
	 */
	public boolean isTransfer()
	{
		return _transfer;
	}

	/**
	 * 인수인계 여부를 설정한다.
	 * @param transfer
	 */
	public void setTransfer(boolean transfer)
	{
		_transfer = transfer;
	}

	/**
	 * 이전 상태값을 돌려준다.
	 * @return
	 */
	public int getBeforeStatus()
	{
		return beforeStatus;
	}

	/**
	 * 이전 상태값을 설정한다.
	 * @param beforeStatus
	 */
	public void setBeforeStatus(int beforeStatus)
	{
		this.beforeStatus = beforeStatus;
	}

	/**
	 * 에디터에서 업로드된 파일을 나타내는 클래스
	 * @author WJ
	 */
	public static class EditorAttachment extends com.kcube.doc.file.Attachment
	{
		private static final long serialVersionUID = 4360835083815239381L;
	}

	/**
	 * 붙임파일 나타내는 클래스이다.
	 */
	public static class WorkAttachment extends WorkAbstractAttachment
	{
		private static final long serialVersionUID = 7447910507230034113L;
	}

	/**
	 * 보고서 파일을 나타내는 클래스
	 */
	public static class ReportFile extends WorkAbstractAttachment
	{
		private static final long serialVersionUID = 8373075650411741443L;

		private String _tmpSavePath;

		/**
		 * 임시 저장 경로를 돌려준다.
		 * @return
		 */
		public String getTmpSavePath()
		{
			return _tmpSavePath;
		}

		/**
		 * 임시 저장 경로를 설정한다.
		 * @param tmpSavePath
		 */
		public void setTmpSavePath(String tmpSavePath)
		{
			_tmpSavePath = tmpSavePath;
		}
	}

	/**
	 * 검토 및 승인 정보를 설정하는 클래스
	 */
	public static class ApprReviewSetting
	{
		/**
		 * 검토자
		 */
		private List<User> _reviewers;
		/**
		 * 승인자
		 */
		private User _approver;

		/**
		 * 보고서 요약문
		 */
		private String _reportSketch;

		/**
		 * 검토자들을 돌려준다.
		 * @return
		 */
		public List<User> getReviewers()
		{
			return _reviewers;
		}

		/**
		 * 검토자들을 설정한다.
		 * @param reviewers
		 */
		public void setReviewers(List<User> reviewers)
		{
			_reviewers = reviewers;
		}

		/**
		 * 승인자를 돌려준다.
		 * @return
		 */
		public User getApprover()
		{
			return _approver;
		}

		/**
		 * 승인자를 설정한다.
		 * @param approver
		 */
		public void setApprover(User approver)
		{
			_approver = approver;
		}

		/**
		 * 보고 요약문을 돌려준다.
		 * @return
		 */
		public String getReportSketch()
		{
			return _reportSketch;
		}

		/**
		 * 보고 요약문을 설정한다.
		 * @param reportSketch
		 */
		public void setReportSketch(String reportSketch)
		{
			_reportSketch = reportSketch;
		}
	}
}