package com.kcube.doc;

import java.sql.Clob;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import com.kcube.doc.file.Attachment;
import com.kcube.doc.opn.Opinion;
import com.kcube.doc.opn.OpinionJson;
import com.kcube.doc.rfrn.Reference;
import com.kcube.doc.symp.Sympathy;
import com.kcube.doc.symp.SympathyJson;
import com.kcube.lib.jdbc.ClobObject;
import com.kcube.map.FolderReference;
import com.kcube.space.SpaceSecurity;
import com.kcube.sys.usr.User;
import com.kcube.sys.usr.UserService;
import com.kcube.sys.usr.UserState;

/**
 * 기본 문서 정보를 담고있는 Class
 */
public abstract class Item extends ClobObject
{
	private static final long serialVersionUID = -9201955773970468233L;

	/**
	 * 소수점 점수를 관리하기 위한 승수
	 */
	public static final int MULTIPLIER = 100;

	/**
	 * 임시 저장 상태
	 */
	public static final int DRAFT_STATUS = 0;

	/**
	 * 반려 상태
	 */
	public static final int REJECTED_STATUS = 1000;

	/**
	 * 신청대기중 상태(마스터가 부여되지 않아서 센터에서 대기중)
	 */
	public static final int STANDBY_STATUS = 1900;

	/**
	 * 신청중 상태(마스터가 부여됨)
	 */
	public static final int APPLIED_STATUS = 2000;

	/**
	 * 검토중 상태(마스터가 문서를 검토하기 위해 조회함)
	 */
	public static final int INSPECTED_STATUS = 2100;

	/**
	 * 등록 상태
	 */
	public static final int REGISTERED_STATUS = 3000;

	/**
	 * 평가중 상태
	 */
	public static final int EVALUATING_STATUS = 3100;

	/**
	 * 삭제 상태
	 */
	public static final int DELETED_STATUS = 9000;

	/**
	 * 기간만료 상태
	 */
	public static final int EXPIRED_STATUS = 9100;

	/**
	 * 등록취소 상태
	 */
	public static final int CANCELED_STATUS = 9200;

	/**
	 * 정제 상태
	 */
	public static final int EXCLUDED_STATUS = 9300;

	/**
	 * 폐기 상태
	 * <p>
	 * 관리자 폐기 단계가 있는 문서에서 사용한다. ECM같은경우. 사용자가 삭제한 상태로서 관리자가 최종폐기할 수 있는 단계이며 복원가능하다.
	 */
	public static final int REMOVED_STATUS = 9900;

	/**
	 * 폐기 예정 상태
	 */
	public static final int REMOVE_STANDBY_STATUS = 9901;

	/**
	 * 문서를 등록일이 지정되어 있으면서 현재보다 미래인 경우
	 * <p>
	 * 문서의 상태와 상관없이 등록일이 미래로 세팅되어 있는지 여부를 판단할때 사용한다.
	 */
	public static final int WAITING_FLAG = 0x0004;

	/**
	 * 등록/평가중 상태에서 구버전이 아닐 때에도 visible이 아닌지 여부 (시행대기중, 혹은 평가중일때에는 안보이도록 설정된 경우 등)
	 * <p>
	 * 다른 버전의 문서를 삭제할 때에 보이지 않도록 할 때에 사용한다.
	 */
	public static final int HOLDING_FLAG = 0x0008;

	/**
	 * 버전업 작업중 여부 - 등록될 때 버전업 완료 처리를 한다.
	 */
	public static final int VERSION_FLAG = 0x0010;

	/**
	 * 익명 허용 여부
	 * <p>
	 * 글단위 익명 허용 여부 만약을 대비해 등록자필드에 실제 등록자 정보를 저장한다.
	 */
	public static final int NAMELESS_FLAG = 0x0020;

	/**
	 * 부모글이 없는 답변글
	 * <p>
	 * 답변글이 등록 또는 임시저장 상태일 때 부모글이 삭제되면 하위 자식글의 상태를 표시한다.
	 */
	public static final int ORPHAN_FLAG = 0x0040;

	private String _title;
	private String _transientTitle;

	private User _rgstUser;
	private User _author;
	private List<Coauthor> _coauthors;
	private List<Collaborator> _collaborators;
	private User _refRgstUser;

	private int _status;
	private int _flagCode;
	private boolean _visible;
	private boolean _best;

	private Date _lastUpdt;
	private Date _aplyDate;
	private Date _rgstDate;
	private Date _exprDate;
	private Date _anncStartDate;
	private Date _anncEndDate;
	private int _exprMonth;

	private Long _gid;
	private Long _pid;
	private int _pos;
	private int _step;
	private int _rplyCnt;
	private int _scrpCnt;
	private int _vrsnNum;

	private int _readCnt;
	private int _transientCnt;
	private int _rcmdCnt;
	private int _evalSum;
	private int _evalCnt;
	private int _evalAvg;

	private int _mstrSum;
	private int _mstrCnt;
	private int _mstrMilg;
	private int _mstrAvg;

	private String _mfCode;

	private String _trnsSrc;
	private String _trnsKey;

	private int _milgShareType;

	private Item.Folder _folder;
	private List<Item.Folder> _folders;

	private int _scrtLevel;
	private List<Security> _securities;
	private List<Tag> _tags;

	private int _sympathyCnt;
	private Long _sympathyLastUserId;
	private Set<Sympathy> _sympathies;

	private Set<? extends Attachment> _attachments;
	private String _fileExt;

	private int _opnCnt;
	private Long _opnLastId;
	private boolean _opnDesc = true;
	private String _transientOpinion;
	private Set<Opinion> _opinions;

	private Set<? extends Reference> _references;

	private Set<User> _masters;

	private boolean _announced;
	private boolean _mobile;

	private Long _classId;
	private Long _moduleId;
	private Long _spaceId;
	private Long _appId;
	private Long _statSpaceId;

	private SpaceSecurity _spaceScrt;

	private boolean _useFileSize = false;
	private Attachment _transientAttachment;
	private Long _totalFileSize;

	private String _content;
	private Clob _clob;

	/**
	 * 내용을 돌려준다.
	 */
	public String getContent()
	{
		try
		{
			System.out.println(_content);
			if (_content != null)
			{
				//_content = Jsoup.clean(_content, Whitelist.relaxed()); jsoup 저버전 용
				_content = Jsoup.clean(_content, Safelist.relaxed());
			}
		}
		catch (Exception e)
		{
			System.out.println(_content);
			e.printStackTrace();
		}
		return _content;
	}

	public void setContent(String content)
	{
		_content = content;
		_clob = null;
	}
	
	/**
	 * @return Best 지식 여부를 돌려준다.
	 */
	public boolean isBest()
	{
		return _best;
	}

	public void setBest(boolean best)
	{
		_best = best;
	}

	/**
	 * 모바일 등록여부를 돌려준다.
	 */
	public boolean isMobile()
	{
		return _mobile;
	}

	public void setMobile(boolean mobile)
	{
		_mobile = mobile;
	}

	/**
	 * @return 문서 신청일을 돌려준다.
	 */
	public Date getAplyDate()
	{
		return _aplyDate;
	}

	public void setAplyDate(Date aplyDate)
	{
		_aplyDate = aplyDate;
	}

	/**
	 * @return 문서의 첨부파일들을 list 형태로 돌려준다.
	 */
	public Set<? extends Attachment> getAttachments()
	{
		return _attachments;
	}

	public void setAttachments(Set<? extends Attachment> attachments)
	{
		_attachments = attachments;
	}

	/**
	 * @return 첫번째 첨부파일의 확장자를 돌려준다.
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
	 * attachments와 fileExt를 설정한다.
	 * <p>
	 * 각각의 Attachment에는 현재 Item 객체가 설정된다.
	 */
	public void updateAttachments(Set<? extends Attachment> attachments)
	{
		setFileExt(null);
		if (attachments != null)
		{
			// 파일 목록 중에서 첫번째 파일의 확장자를 설정한다.
			// 파일이 없는 경우 <code>null</code>, 파일에 확장자가 없는 경우에는
			// <code>"unknown"</code>으로 설정된다.
			for (Attachment att : attachments)
			{
				// MovieRepository는 Hidden으로 처리됨으로 확장자를 넣어주지 않도록 한다.
				// 본문에서 추가된 이미지는 Hidden 처리됨으로 확장자를 넣지 않는다.
				if (att.getType() != null
					&& att.getType().intValue() != 7000
					&& att.getFilename() != null
					&& att.getFilename().indexOf("KCUBECONTENTIMAGEHIDDEN") != 0)
				{
					setFileExt(att.getExtension());
					break;
				}
			}
		}
		// 삭제된 첨부파일이 포함되어 있으므로 그냥 설정하면 안된다...
		// db에서 삭제되었으므로 lazy load하면 반영되지 않을까...
		setAttachments(attachments);
	}

	/**
	 * @return 문서의 작성자를 돌려준다.
	 */
	public User getAuthor()
	{
		return _author;
	}

	public void setAuthor(User author)
	{
		_author = author;
	}

	/**
	 * 현재 사용자가 문서의 작성자/등록자인지의 여부를 돌려준다.
	 */
	public boolean isCurrentOwner()
	{
		return (isCurrentAuthor() || isCurrentRgstUser());
	}

	public void setCurrentOwner(boolean currentOwner)
	{
	}

	/**
	 * 문서의 작성자인지를 확인한다.
	 */
	public boolean isCurrentAuthor()
	{
		Long userId = UserService.getUserId();
		if (userId == null)
		{
			return false;
		}
		User auth = getAuthor();
		if (auth == null)
		{
			return false;
		}
		return (userId.equals(auth.getUserId()));
	}

	/**
	 * @return 공동작성자(부서 포함)들을 list 형태로 돌려준다.
	 */
	public List<Collaborator> getCollaborators()
	{
		return _collaborators;
	}

	public void setCollaborators(List<Collaborator> Collaborators)
	{
		_collaborators = Collaborators;
	}

	/**
	 * 현재 사용자가 공동작성자인지 여부를 확인한다.
	 * @return
	 */
	public boolean isCollaborator()
	{
		if (getCollaborators() != null && getCollaborators().size() > 0)
		{
			for (Collaborator collabo : getCollaborators())
			{
				if (UserService.hasPermission(collabo.getXid()))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @return 문서의 부작성자들을 list 형태로 돌려준다.
	 */
	public List<Coauthor> getCoauthors()
	{
		return _coauthors;
	}

	public void setCoauthors(List<Coauthor> coauthors)
	{
		_coauthors = coauthors;
	}

	/**
	 * 현재 이벤트에 관련된 사용자를 돌려준다.
	 * <p>
	 * 해당문서에 관련문서, 의견등을 추가할때.. 추가하는 사용자를 돌려준다. 로그 남길때 관련 사용자를 refRgstUser 에 설정하고, 마일리지
	 * 부여에서 사용된다.
	 */
	public User getRefRgstUser()
	{
		return _refRgstUser;
	}

	public void setRefRgstUser(User refRgstUser)
	{
		_refRgstUser = refRgstUser;
	}

	/**
	 * @return 사용자 평가시 부여한 평가 지수를 합산한 값을 돌려준다.
	 */
	public int getEvalSum()
	{
		return _evalSum;
	}

	public void setEvalSum(int evalSum)
	{
		_evalSum = evalSum;
	}

	/**
	 * @return 몇 명의 사용자가 평가했는지를 돌려준다.
	 */
	public int getEvalCnt()
	{
		return _evalCnt;
	}

	public void setEvalCnt(int evalCnt)
	{
		_evalCnt = evalCnt;
	}

	/**
	 * @return 문서의 폐기날짜를 돌려준다.
	 */
	public Date getExprDate()
	{
		return _exprDate;
	}

	public void setExprDate(Date exprDate)
	{
		_exprDate = exprDate;
	}

	/**
	 * @return 문서가 폐기될 월을 돌려준다.
	 */
	public int getExprMonth()
	{
		return _exprMonth;
	}

	public void setExprMonth(int exprMonth)
	{
		_exprMonth = exprMonth;
	}

	/**
	 * 내가 등록한 지식이 현재 어떻게 처리되었는지의 상태를 돌려준다.
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
	 * 삭제/폐기 모듈, 베스트 지식 선정 모듈, 체크인/체크아웃/버전업 모듈에서 문서의 상태를 표시를 할 수 있도록 bit flag를 돌려준다.
	 * @return 문서 상태 bit
	 */
	public int getFlagCode()
	{
		return _flagCode;
	}

	public void setFlagCode(int flagCode)
	{
		_flagCode = flagCode;
	}

	/**
	 * 삭제/폐기 모듈, 베스트 지식 선정 모듈, 체크인/체크아웃/버전업 여부를 리턴한다. 플래그 값의 자리별 값으로 의미 파악.
	 * @return code 플래그가 설정되어 있는지의 여부
	 */
	public boolean isFlagCode(int code)
	{
		return ((_flagCode & code) != 0);
	}

	/**
	 * 삭제/폐기 모듈, 베스트 지식 선정 모듈, 체크인/체크아웃/버전업를 설정한다.
	 * <p>
	 * 플래그 값의 자리별 값으로 의미 파악한다.
	 * @param code 플래그 값의 자리 위치
	 * @param set on/off
	 */
	public void setFlagCode(int code, boolean set)
	{
		if (set)
		{
			_flagCode |= code;
		}
		else
		{
			_flagCode &= ~code;
		}
	}

	/**
	 * 어떠한 문서에 대해 여러 version들이 있을 때 각 version에 해당하는 문서들이 같은 group 인지를 나타내는 group id를 돌려준다.
	 * @return 문서의 group id를 돌려준다.
	 */
	public Long getGid()
	{
		return _gid;
	}

	public void setGid(Long gid)
	{
		_gid = gid;
	}

	/**
	 * 이 문서가 gid 문서 그룹의 root인지의 여부를 돌려준다.
	 */
	public boolean isRoot()
	{
		return (_gid == null || _gid.equals(getId()));
	}

	/**
	 * 이 문서가 다른 문서에 대한 답변글인지의 여부를 돌려준다.
	 * <p>
	 * pid와 gid가 있고, gid와 id가 다를 경우 답변글로 판단한다.
	 */
	public boolean isReply()
	{
		return (_pid != null && _gid != null && !_gid.equals(getId()));
	}

	/**
	 * @return 문서의 맵 위치를 나타내는 level1,2,3,4를 Item.Folder 객체 형태로 돌려준다.
	 */
	public Item.Folder getFolder()
	{
		return _folder;
	}

	public void setFolder(Item.Folder folder)
	{
		_folder = folder;
	}

	/**
	 * Json 값을 getFolder , setFolder에 값을 매핑할때만 이용 된다.
	 */
	public Item.Folder.Json getJsonFolder()
	{
		Item.Folder folder = getFolder();
		if (folder == null)
			return null;
		Item.Folder.Json jsonFolder = new Item.Folder.Json(getFolder());
		return jsonFolder;
	}

	public void setJsonFolder(Item.Folder.Json jsonFolder)
	{
		if (jsonFolder == null)
			return;
		setFolder(jsonFolder.newInstance());
	}

	/**
	 * @return 문서의 각 맵별 위치를 돌려준다.
	 */
	public List<Item.Folder> getFolders()
	{
		return _folders;
	}

	public void setFolders(List<Item.Folder> folders)
	{
		_folders = folders;
	}

	/**
	 * @return 문서의 멥 Id 들을 돌려준다.
	 */
	public Set<Long> getFolderIds()
	{
		Set<Long> fids = new TreeSet<Long>();
		if (_folders != null && _folders.size() > 0)
		{
			for (Item.Folder f : _folders)
			{
				fids.add(f.getFolderId());
			}
		}
		else if (_folder != null)
		{
			fids.add(_folder.getFolderId());
		}
		return fids;
	}

	/**
	 * @return Json 값을 getFolders , setFolders에 값을 매핑할때만 이용 된다.
	 */
	public List<Item.Folder.Json> getJsonFolders()
	{
		List<Item.Folder> folders = getFolders();
		List<Item.Folder.Json> newFolders = new LinkedList<Item.Folder.Json>();
		if (folders == null)
			return newFolders;
		for (Item.Folder folder : folders)
		{
			newFolders.add(new Item.Folder.Json(folder));
		}
		return newFolders;
	}

	public void setJsonFolders(List<Item.Folder.Json> jsonFolders)
	{
		if (jsonFolders == null)
			return;
		List<Item.Folder> newFolders = new LinkedList<Item.Folder>();
		for (Item.Folder.Json folder : jsonFolders)
		{
			newFolders.add(folder.newInstance());
		}
		setFolders(newFolders);
	}

	public void addFolder(Item.Folder folder)
	{
		if (folder == null)
			return;
		List<Item.Folder> folders = getFolders();
		if (folders == null)
		{
			folders = new LinkedList<Item.Folder>();
			setFolders(folders);
		}
		folder.setVisible(isVisible());
		folders.add(folder);
	}

	/**
	 * folder, folders 속성의 level1,2,3,4 값을 설정한다.
	 */
	public void updateFolderLevel()
	{
		if (getFolder() != null)
		{
			Folder f = getFolder();
			f.updateLevel();
		}
		if (getFolders() != null)
		{
			for (Item.Folder f : getFolders())
			{
				f.updateLevel();
			}
		}
	}

	private void updateFoldersVisible()
	{
		if (getFolders() != null)
		{
			for (Item.Folder map : getFolders())
			{
				map.setVisible(_visible);
			}
		}
	}

	/**
	 * @return 마지막으로 등록 혹은 수정된 날짜를 돌려준다.
	 */
	public Date getLastUpdt()
	{
		return _lastUpdt;
	}

	public void setLastUpdt(Date lastUpdt)
	{
		_lastUpdt = lastUpdt;
	}

	/**
	 * @return 지식의 타입을 돌려준다.(방법지, 사실지)
	 */
	public String getMfCode()
	{
		return _mfCode;
	}

	public void setMfCode(String mfCode)
	{
		_mfCode = mfCode;
	}

	/**
	 * 문서를 담당하는 마스터를 돌려준다.
	 */
	public Set<User> getMasters()
	{
		return _masters;
	}

	public void setMasters(Set<User> masters)
	{
		_masters = masters;
	}

	public void addMaster(User master)
	{
		Set<User> masters = getMasters();
		if (masters == null)
		{
			masters = new HashSet<User>();
			setMasters(masters);
		}
		masters.add(master);
	}

	/**
	 * masters 목록에 포함된 사용자인지를 확인한다.
	 */
	public boolean isCurrentMaster()
	{
		if (getMasters() == null)
			return false;
		for (User mstr : getMasters())
		{
			if (mstr.isCurrentUser())
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * 현재 사용자를 masters 목록에서 제거한다.
	 */
	public void removeCurrentMaster()
	{
		Iterator<User> i = getMasters().iterator();
		while (i.hasNext())
		{
			User mstr = (User) i.next();
			if (mstr.isCurrentUser())
			{
				i.remove();
			}
		}
	}

	/**
	 * 작성자를 masters 목록에서 제거한다.
	 */
	public void removeSelfMaster()
	{
		Iterator<User> i = getMasters().iterator();
		while (i.hasNext())
		{
			User mstr = (User) i.next();
			if (mstr.equals(getAuthor()))
			{
				i.remove();
			}
		}
	}

	/**
	 * 문서를 처리하지 않은 마스터가 있는지의 여부를 돌려준다.
	 */
	public boolean hasMasters()
	{
		return (_masters != null && !_masters.isEmpty());
	}

	/**
	 * 마스터 평가 지수 , 기본값은 0이다. 평가가 이루어져 있는지의 여부는 MSTR_CNT가 0인지를 이용한다.
	 * @return 마스터의 평가 지수를 돌려준다.
	 */
	public int getMstrSum()
	{
		return _mstrSum;
	}

	public void setMstrSum(int mstrSum)
	{
		_mstrSum = mstrSum;
	}

	/**
	 * 전문가 평가로 작성자가 받은 점수의 합계를 돌려준다.
	 * <p>
	 * 전문가가 평가할 때마다 합산된다. 실제 작성자/부작성자에게 부여된 점수는 mstrCnt로 나누어야 한다.
	 * <p>
	 * 2004/2/2 luddite system integrity에 미치는 해악이 크므로 가급적 기능을 없앴으면 함... 기능이 굳이 필요하다면 어떻게
	 * 통합할 지 검토 필요함...
	 */
	public int getMstrMilg()
	{
		return _mstrMilg;
	}

	public void setMstrMilg(int mstrMilg)
	{
		_mstrMilg = mstrMilg;
	}

	/**
	 * @return 지식평가를 한 지식 전문가 수를 돌려준다.
	 */
	public int getMstrCnt()
	{
		return _mstrCnt;
	}

	public void setMstrCnt(int mstrCnt)
	{
		_mstrCnt = mstrCnt;
	}

	/**
	 * 마스터에 의한 평가를 기록한다.
	 */
	public void evalByMstr(int rate)
	{
		_mstrSum += rate;
		_mstrCnt++;
		_mstrAvg = (_mstrSum * MULTIPLIER) / _mstrCnt;
	}

	/**
	 * @return 문서의 의견들을 돌려준다.
	 */
	public Set<Opinion> getOpinions()
	{
		return _opinions;
	}

	public void setOpinions(Set<Opinion> opinions)
	{
		_opinions = opinions;
	}

	/**
	 * @return Json 값을 getOpinions 에 매핑할때만 이용 된다.
	 */
	public OpinionJson getJsonOpinions()
	{
		return new OpinionJson(getOpinions(), _opnLastId, _opnDesc);
	}

	public void setJsonOpinions(Set<Opinion> opinions)
	{
		setOpinions(opinions);
	}

	/**
	 * @return 문서에 대한 의견수를 돌려준다.
	 */
	public int getOpnCnt()
	{
		return _opnCnt;
	}

	public void setOpnCnt(int opnCnt)
	{
		_opnCnt = opnCnt;
	}

	/**
	 * @return 문서에 대한 현재까지 그려진 마지막의견 Id값을 돌려준다.
	 */
	public Long getOpnLastId()
	{
		return _opnLastId;
	}

	public void setOpnLastId(Long opnLastId)
	{
		_opnLastId = opnLastId;
	}

	/**
	 * @return 문서에 대한 의견 정렬을 돌려준다.
	 */
	public boolean isOpnDesc()
	{
		return _opnDesc;
	}

	public void setOpnDesc(boolean opnDesc)
	{
		_opnDesc = opnDesc;
	}

	/**
	 * Q&A 혹은 게시판에서 어떤 글에 대한 답글인 경우 어떤 글에 대한 답글인지를 알려준다.
	 * @return 상위 문서의 id를 돌려준다.
	 */
	public Long getPid()
	{
		return _pid;
	}

	public void setPid(Long pid)
	{
		_pid = pid;
	}

	/**
	 * @return 문서 답변의 깊이를 돌려준다.
	 */
	public int getPos()
	{
		return _pos;
	}

	public void setPos(int pos)
	{
		_pos = pos;
	}

	/**
	 * @return 추천수를 돌려준다.
	 */
	public int getRcmdCnt()
	{
		return _rcmdCnt;
	}

	public void setRcmdCnt(int rcmdCnt)
	{
		_rcmdCnt = rcmdCnt;
	}

	/**
	 * @return 문서에 대한 조회수를 돌려준다.
	 */
	public int getReadCnt()
	{
		return _readCnt;
	}

	public void setReadCnt(int readCnt)
	{
		_readCnt = readCnt;
	}

	/**
	 * @return 문서의 관련자료 목록을 돌려준다.
	 */
	public Set<? extends Reference> getReferences()
	{
		return _references;
	}

	public void setReferences(Set<? extends Reference> references)
	{
		_references = references;
	}

	/**
	 * @return 문서가 승인된 날짜를 돌려준다.
	 */
	public Date getRgstDate()
	{
		return _rgstDate;
	}

	public void setRgstDate(Date rgstDate)
	{
		_rgstDate = rgstDate;
	}

	/**
	 * @return 문서의 등록자를 돌려준다.
	 */
	public User getRgstUser()
	{
		return _rgstUser;
	}

	public void setRgstUser(User rgstUser)
	{
		_rgstUser = rgstUser;
	}

	/**
	 * 문서의 등록자인지를 확인한다.
	 */
	public boolean isCurrentRgstUser()
	{
		Long userId = UserService.getUserId();
		if (userId == null)
		{
			return false;
		}
		User rgst = getRgstUser();
		if (rgst == null)
		{
			return false;
		}
		return (userId.equals(rgst.getUserId()));
	}

	/**
	 * @return 문서에 대한 답변수를 돌려준다.
	 */
	public int getRplyCnt()
	{
		return _rplyCnt;
	}

	public void setRplyCnt(int rplyCnt)
	{
		_rplyCnt = rplyCnt;
	}

	/**
	 * return 문서가 스크랩 된수를 돌려준다.
	 */
	public void setScrpCnt(int scrpCnt)
	{
		_scrpCnt = scrpCnt;
	}

	public int getScrpCnt()
	{
		return _scrpCnt;
	}

	/**
	 * return 문서의 가상 조회수를 돌려준다.
	 */
	public int getTransientCnt()
	{
		return _transientCnt;
	}

	public void setTransientCnt(int transientCnt)
	{
		_transientCnt = transientCnt;
	}

	/**
	 * 보안 설정의 수준을 돌려준다.
	 * <p>
	 * com.kcube.sys.usr.UseState에 정의된 상수를 참조한다.
	 */
	public int getScrtLevel()
	{
		return _scrtLevel;
	}

	public void setScrtLevel(int scrtLevel)
	{
		_scrtLevel = scrtLevel;
	}

	/**
	 * @return 문서의 보안 설정 목록을 돌려준다.
	 */
	public List<Security> getSecurities()
	{
		return _securities;
	}

	public void setSecurities(List<Security> securities)
	{
		_securities = securities;
	}

	/**
	 * @return 보안이 문서 보안 이상인 경우에 보안 목록을 돌려준다.
	 */
	public Set<Long> getItemScrtXids()
	{
		Set<Long> xids = new TreeSet<Long>();
		if (getScrtLevel() >= UserState.SECURE_ITEM)
		{
			List<Security> scrts = _securities;
			if (scrts != null && scrts.size() > 0)
			{
				for (Security s : scrts)
				{
					xids.add(s.getXid());
				}
			}
		}
		return xids;
	}

	/**
	 * 보안정보를 추가한다.
	 * <p>
	 * visible은 속성은 문서의 visible에 맞게 설정된다. 이미 포함되어 있는 보안정보를 추가하지 않는다.
	 */
	public void addSecurity(Security scrt)
	{
		if (scrt != null)
		{
			List<Security> securities = getSecurities();
			if (securities == null)
			{
				securities = new LinkedList<Security>();
				setSecurities(securities);
			}
			scrt.setVisible(isVisible());
			if (!securities.contains(scrt))
			{
				securities.add(scrt);
			}
		}
	}

	/**
	 * 문서의 태그들을 돌려준다.
	 */
	public List<Tag> getTags()
	{
		return _tags;
	}

	public void setTags(List<Tag> tags)
	{
		_tags = tags;
		if (getTags() != null)
		{
			Iterator<Tag> i = _tags.iterator();
			while (i.hasNext())
			{
				Tag tag = (Tag) i.next();
				tag.setItemDate(getRgstDate());
			}
		}
	}

	/**
	 * 문서의 태그들을 추가한다.
	 */
	public void addTag(Tag tag)
	{
		if (tag != null)
		{
			List<Tag> tags = getTags();
			if (tags == null)
			{
				tags = new LinkedList<Tag>();
				setTags(tags);
			}
			tag.setItemDate(getRgstDate());
			tag.setVisible(isVisible());
			tags.add(tag);
		}
	}

	private void updateScrtVisible()
	{
		if (getSecurities() != null)
		{
			for (Item.Security scrt : getSecurities())
			{
				scrt.setVisible(_visible);
			}
		}
	}

	private void updateTagVisible()
	{
		if (getTags() != null)
		{
			for (Item.Tag tag : getTags())
			{
				tag.setVisible(_visible);
			}
		}
	}

	/**
	 * @return 문서 그룹 내에서 몇 번째로 보여지는 문서인지를 돌려준다.
	 */
	public int getStep()
	{
		return _step;
	}

	public void setStep(int step)
	{
		_step = step;
	}

	/**
	 * @return 문서 제목을 돌려준다.
	 */
	public String getTitle()
	{
		return _title;
	}

	public void setTitle(String title)
	{
		_title = title;
	}

	/**
	 * @return substring한 문서 제목을 돌려준다.(for Alimi)
	 */
	public String getTransientTitle()
	{
		return _transientTitle;
	}

	public void setTransientTitle(String transientTitle)
	{
		_transientTitle = transientTitle;
	}

	/**
	 * 이관된 문서일 경우 이관된 시스템의 key값을 돌려준다.
	 */
	public String getTrnsKey()
	{
		return _trnsKey;
	}

	public void setTrnsKey(String trnsKey)
	{
		_trnsKey = trnsKey;
	}

	/**
	 * 어떤 시스템에서 이관되어졌는지를 알려준다.
	 */
	public String getTrnsSrc()
	{
		return _trnsSrc;
	}

	public void setTrnsSrc(String trnsSrc)
	{
		_trnsSrc = trnsSrc;
	}

	/**
	 * 지식이 사용자 목록에서 조회 가능한지의 여부를 돌려준다.
	 */
	public boolean isVisible()
	{
		return _visible;
	}

	public void setVisible(boolean visible)
	{
		_visible = visible;
	}

	/**
	 * 객체의 visible 속성을 설정한다.
	 * <p>
	 * securities와 folders에도 속성을 반영한다.
	 */
	public void updateVisible(boolean visible)
	{
		setVisible(visible);
		updateScrtVisible();
		updateFoldersVisible();
		updateTagVisible();
	}

	/**
	 * @return 문서의 버젼을 돌려준다.
	 */
	public int getVrsnNum()
	{
		return _vrsnNum;
	}

	public void setVrsnNum(int vrsnNum)
	{
		_vrsnNum = vrsnNum;
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

	public Long getStatSpaceId()
	{
		return _statSpaceId;
	}

	public void setStatSpaceId(Long statSpaceId)
	{
		_statSpaceId = statSpaceId;
	}

	public Long getAppId()
	{
		return _appId;
	}

	public void setAppId(Long appId)
	{
		_appId = appId;
	}

	/**
	 * @return Group(Work, Team, External)Space일 경우 SpaceSecurity 객체를 돌려준다.
	 */
	public SpaceSecurity getSpaceSecurity() throws Exception
	{
		if (_spaceScrt == null)
		{
			_spaceScrt = new SpaceSecurity(_spaceId);
		}
		return _spaceScrt;
	}

	public void setSpaceSecurity(SpaceSecurity spaceScrt)
	{
		_spaceScrt = spaceScrt;
	}

	/**
	 * 문서가 위치한 맵정보를 나타내는 클래스이다.
	 */
	public static class Folder extends FolderReference
	{
		private static final long serialVersionUID = 4881156363979444374L;

		private boolean _visible;

		public Folder()
		{
		}

		public Folder(Long folderId)
		{
			super(folderId);
		}

		/**
		 * 문서가 조회 가능한지의 여부를 돌려준다.
		 */
		public boolean isVisible()
		{
			return _visible;
		}

		public void setVisible(boolean visible)
		{
			_visible = visible;
		}

		/**
		 * folder를 json형대로 보여주기 위한 Class이다
		 */
		public static class Json extends FolderReference.Json
		{
			public Json()
			{
			}

			public Json(FolderReference ref)
			{
				super(ref);
			}

			public Folder newInstance()
			{
				Folder folder = new Folder();
				folder.setFolderId(getId());
				folder.setComputed(isComputed());
				return folder;
			}
		}

	}

	/**
	 * 문서에 대한 보안 설정을 나타내는 클래스이다.
	 */
	public static class Security extends com.kcube.sys.usr.UserSecurity
	{
		private static final long serialVersionUID = -6404154273189991003L;

		private boolean _visible;

		/**
		 * 문서의 visible 여부를 돌려준다.
		 */
		public boolean isVisible()
		{
			return _visible;
		}

		public void setVisible(boolean visible)
		{
			_visible = visible;
		}
	}

	/**
	 * 문서의 태그에 관한 클래스이다.
	 */
	public static class Tag
	{
		private String _tag;
		private Date _itemDate;
		private boolean _visible;

		public Tag()
		{
		}

		public Tag(Tag tag)
		{
			_tag = tag.getTag();
		}

		/**
		 * Tag를 돌려준다.
		 **/
		public String getTag()
		{
			return _tag;
		}

		public void setTag(String tag)
		{
			_tag = tag;
		}

		/**
		 * 등록일을 돌려준다.
		 */
		public Date getItemDate()
		{
			return _itemDate;
		}

		public void setItemDate(Date itemDate)
		{
			_itemDate = itemDate;
		}

		/**
		 * 보임여부를 돌려준다.
		 */
		public boolean isVisible()
		{
			return _visible;
		}

		public void setVisible(boolean visible)
		{
			_visible = visible;
		}

	}

	/**
	 * 체크아웃
	 */
	public static class Checkout extends User
	{
		private static final long serialVersionUID = -3921704652786297772L;

		private Date _chkoutDate;

		/**
		 * @return 지식을 체크아웃한 날짜를 돌려준다.
		 */
		public Date getChkoutDate()
		{
			return _chkoutDate;
		}

		public void setChkoutDate(Date chkoutDate)
		{
			_chkoutDate = chkoutDate;
		}
	}

	public String getTransientOpinion()
	{
		return _transientOpinion;
	}

	public void setTransientOpinion(String transientOpinion)
	{
		_transientOpinion = transientOpinion;
	}

	/**
	 * 사용자평가 평균점수를 돌려준다.
	 */
	public int getEvalAvg()
	{
		return _evalAvg;
	}

	public void setEvalAvg(int evalAvg)
	{
		_evalAvg = evalAvg;
	}

	/**
	 * 마스터의 평가 평균점수를 돌려준다.
	 */
	public int getMstrAvg()
	{
		return _mstrAvg;
	}

	public void setMstrAvg(int mstrAvg)
	{
		_mstrAvg = mstrAvg;
	}

	/**
	 * 고정게시 여부를 돌려준다.
	 */
	public boolean isAnnounced()
	{
		return _announced;
	}

	public void setAnnounced(boolean announced)
	{
		_announced = announced;
	}

	/**
	 * @return 고정게시 시작일을 돌려준다.
	 */
	public Date getAnncStartDate()
	{
		return _anncStartDate;
	}

	public void setAnncStartDate(Date anncStartDate)
	{
		_anncStartDate = anncStartDate;
	}

	/**
	 * @return 고정게시 종료일을 돌려준다.
	 */
	public Date getAnncEndDate()
	{
		return _anncEndDate;
	}

	public void setAnncEndDate(Date anncEndDate)
	{
		_anncEndDate = anncEndDate;
	}

	/**
	 * 마일리지 분배 유형을 돌려준다.
	 */
	public int getMilgShareType()
	{
		return _milgShareType;
	}

	public void setMilgShareType(int milgShareType)
	{
		_milgShareType = milgShareType;
	}

	/**
	 * 공감정보를 돌려준다.
	 */
	public Set<Sympathy> getSympathies()
	{
		return _sympathies;
	}

	public void setSympathies(Set<Sympathy> sympathies)
	{
		_sympathies = sympathies;
	}

	/**
	 * @return Json 값을 getSympathies 에 매핑할때만 이용 된다.
	 */
	public SympathyJson getJsonSympathies()
	{
		return new SympathyJson(getSympathies(), _sympathyLastUserId);
	}

	public void setJsonSympathies(Set<Sympathy> sympathies)
	{
		setSympathies(sympathies);
	}

	/**
	 * @return 문서에 대한 공감수를 돌려준다.
	 */
	public int getSympathyCnt()
	{
		return _sympathyCnt;
	}

	public void setSympathyCnt(int sympathyCnt)
	{
		_sympathyCnt = sympathyCnt;
	}

	/**
	 * @return 공감한 마지막 유저의 ID값을 돌려준다.
	 */
	public Long getSympathyLastUserId()
	{
		return _sympathyLastUserId;
	}

	public void setSympathyLastUserId(Long sympathyLastUserId)
	{
		_sympathyLastUserId = sympathyLastUserId;
	}

	/**
	 * @return 파일 사이즈를 돌려준다.
	 */
	public long getTotalFileSize()
	{
		if (_totalFileSize != null)
		{
			return _totalFileSize;
		}
		else
		{
			long totalFileSize = 0;
			if (_attachments != null && _attachments.size() > 0)
			{
				for (Attachment att : _attachments)
				{
					totalFileSize += att.getFilesize();
				}
			}
			return totalFileSize;
		}
	}

	public void setTotalFileSize(Long totalFileSize)
	{
		_totalFileSize = totalFileSize;
	}

	/**
	 * @param att
	 * @return 단일 파일 사이즈를 돌려준다.
	 */
	public long getFileSize(Attachment att)
	{
		return att != null ? att.getFilesize() : 0L;
	}

	/**
	 * @return 임시 첨부파일을 저장한다. [for 활동통계(파일사이즈)]
	 */
	public Attachment getTransientAttachment()
	{
		return _transientAttachment;
	}

	public void setTransientAttachment(Attachment transientAttachment)
	{
		setUseFileSize(true);
		_transientAttachment = transientAttachment;
	}

	/**
	 * @return 파일 사이즈 사용여부(활동통계)
	 */
	public boolean isUseFileSize()
	{
		return _useFileSize;
	}

	public void setUseFileSize(boolean useFileSize)
	{
		_useFileSize = useFileSize;
	}

	/**
	 * 문서의 공동작성자를 나타내는 클래스이다.
	 */
	public static class Coauthor extends User
	{
		private static final long serialVersionUID = -6766873167677138958L;

		private int _milgShare;

		public Coauthor()
		{
		}

		public Coauthor(User coauthor)
		{
			super(coauthor);
		}

		/**
		 * 마일리지 할당 비율
		 */
		public int getMilgShare()
		{
			return _milgShare;
		}

		public void setMilgShare(int milgShare)
		{
			_milgShare = milgShare;
		}

	}

	/**
	 * 문서의 공동작성자를 나타내는 클래스이다.(부서 포함)
	 */
	public static class Collaborator extends Coauthor
	{
		private static final long serialVersionUID = 8576882066796086896L;

		private Long _xid;
		private String _title;
		private boolean _computed;

		public Long getXid()
		{
			return _xid;
		}

		public void setXid(Long xid)
		{
			_xid = xid;
		}

		/**
		 * xid를 String으로 돌려준다. <b>json.xml에서만 사용한다. 직접 호출해서 사용금지!</b>
		 * <p>
		 * javascript에서 숫자를 16자리까지만 처리가능함으로 String으로 변환해서 사용함. 타 용도로 사용을 금함.
		 */
		public String getXidString()
		{
			return _xid.toString();
		}

		/**
		 * String으로 부터 xid를 설정한다. <b>json.xml에서만 사용한다. 직접 호출해서 사용금지!</b>
		 * @param xid
		 */
		public void setXidString(String xid)
		{
			_xid = new Long(xid);
		}

		public String getTitle()
		{
			return _title;
		}

		public void setTitle(String title)
		{
			_title = title;
		}

		public boolean isComputed()
		{
			return _computed;
		}

		public void setComputed(boolean computed)
		{
			_computed = computed;
		}
	}
}