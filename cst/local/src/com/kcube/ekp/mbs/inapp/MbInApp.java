package com.kcube.ekp.mbs.inapp;

import java.util.LinkedList;
import java.util.List;

import com.kcube.doc.Item;
import com.kcube.map.FolderService;
import com.kcube.sys.module.ModuleParam;

public class MbInApp extends Item
{
	private static final long serialVersionUID = 6842223625921644028L;

	public static final String MBS_DEFAULT_KM = "com.kcube.ekp.mbs.MbItemConfig.mbsDefaultKm";

	/**
	 * 전체글보기 InApp 타입
	 */
	public static final long ALL_ITEMS = -1L;

	/**
	 * 게시판그룹 InApp 타입
	 */
	public static final int APP_FOLDER = 0;
	/**
	 * 일반게시판 InApp 타입
	 */
	public static final int APP_BBS = 1000;
	/**
	 * 위키게시판 InApp 타입
	 */
	public static final int APP_WIKI = 2000;
	/**
	 * 이미지게시판 InApp 타입
	 */
	public static final int APP_ALBUM = 3000;
	/**
	 * URL InApp 타입
	 */
	public static final int APP_URL = 4000;

	/**
	 * 등록시 알림 사용안함
	 */
	public static final int ALIMI_NONE = 0;
	/**
	 * 등록시 알림 분류관리자
	 */
	public static final int ALIMI_MSTR = 1000;
	/**
	 * 등록시 알림 지정사용자
	 */
	public static final int ALIMI_USER = 2000;

	/**
	 * 등록 알림 역할 코드
	 */
	public static final int ROLE_NOTI = 1000;
	/**
	 * 등록 승인 역할 코드
	 */
	public static final int ROLE_PRCS = 2000;

	/**
	 * 만료기한
	 */
	public static final int EXPR_PSNL = -1;
	public static final int EXPR_NONE = 0;
	public static final int EXPR_WEEK = 1000;
	public static final int EXPR_MNTH = 2000;
	public static final int EXPR_YEAR = 3000;
	public static final int EXPR_THREE_YEAR = 4000;

	private Long _kmId;
	private String _name;
	private Long _appType;
	private boolean _reserve;
	private boolean _scrt;
	private int _exprPeriod;
	private boolean _opinion;
	private boolean _sympathy;
	private boolean _reply;
	private boolean _anony;
	private boolean _anonyOpn;
	private int _prcsType;
	private int _notiType;
	private boolean _unRead;
	private boolean _limitSize;
	private long _maxFileSize;
	private long _maxDocSize;
	private boolean _limitExt;
	private String _invalidExt;
	private boolean _vote;
	private boolean _introduce;
	private String _intro;
	private boolean _showManager;
	private boolean _template;
	private boolean _caution;
	private String _statement;
	private String _url;
	private boolean _popUp;
	private String _popUpStyle;
	private List<Security> _roles;
	private List<Security> _notiRoles;
	private List<Security> _prcsRoles;

	/**
	 * @return 지식맵 일련번호를 돌려준다.
	 */
	public Long getKmId()
	{
		return _kmId;
	}

	public void setKmId(Long kmId)
	{
		_kmId = kmId;
	}

	/**
	 * @return 인앱의 이름을 반환한다.
	 */
	public String getName()
	{
		return _name;
	}

	public void setName(String name)
	{
		_name = name;
	}

	/**
	 * @return InApp 타입을 돌려준다.
	 */
	public Long getAppType()
	{
		return _appType;
	}

	public void setAppType(Long appType)
	{
		_appType = appType;
	}

	/**
	 * 예약게시가 가능한지의 여부를 돌려준다.
	 */
	public boolean isReserve()
	{
		return _reserve;
	}

	public void setReserve(boolean reserve)
	{
		_reserve = reserve;
	}

	/**
	 * 컨텐츠에 대한 보안기능 사용 여부를 돌려준다.
	 */
	public boolean isScrt()
	{
		return _scrt;
	}

	public void setScrt(boolean scrt)
	{
		_scrt = scrt;
	}

	/**
	 * 게시만료 기간을 돌려준다.
	 */
	public int getExprPeriod()
	{
		return _exprPeriod;
	}

	public void setExprPeriod(int exprPeriod)
	{
		_exprPeriod = exprPeriod;
	}

	/**
	 * 의견 등록이 가능한지의 여부를 돌려준다.
	 */
	public boolean isOpinion()
	{
		return _opinion;
	}

	public void setOpinion(boolean opinion)
	{
		_opinion = opinion;
	}

	/**
	 * 공감 등록이 가능한지의 여부를 돌려준다.
	 */
	public boolean isSympathy()
	{
		return _sympathy;
	}

	public void setSympathy(boolean sympathy)
	{
		_sympathy = sympathy;
	}

	/**
	 * 답변 등록이 가능한지의 여부를 돌려준다.
	 */
	public boolean isReply()
	{
		return _reply;
	}

	public void setReply(boolean reply)
	{
		_reply = reply;
	}

	/**
	 * 익명 등록이 가능한지의 여부를 돌려준다.
	 */
	public boolean isAnony()
	{
		return _anony;
	}

	public void setAnony(boolean anony)
	{
		_anony = anony;
	}

	/**
	 * 익명의견 등록이 가능한지의 여부를 돌려준다.
	 */
	public boolean isAnonyOpn()
	{
		return _anonyOpn;
	}

	public void setAnonyOpn(boolean anonyOpn)
	{
		_anonyOpn = anonyOpn;
	}

	/**
	 * 승인절차가 타입을 돌려준다.
	 */
	public int getPrcsType()
	{
		return _prcsType;
	}

	public void setPrcsType(int prcsType)
	{
		_prcsType = prcsType;
	}

	/**
	 * 글 등록시 알림 타입을 돌려준다.
	 */
	public int getNotiType()
	{
		return _notiType;
	}

	public void setNotiType(int notiType)
	{
		_notiType = notiType;
	}

	/**
	 * 안읽은 글의 표시 여부를 돌려준다.
	 */
	public boolean isUnRead()
	{
		return _unRead;
	}

	public void setUnRead(boolean unRead)
	{
		_unRead = unRead;
	}

	/**
	 * 첨부 용량의 제한이 가능한지의 여부를 돌려준다.
	 */
	public boolean isLimitSize()
	{
		return _limitSize;
	}

	public void setLimitSize(boolean limitSize)
	{
		_limitSize = limitSize;
	}

	/**
	 * 파일당 최대크기 제한을 돌려준다.
	 */
	public long getMaxFileSize()
	{
		return _maxFileSize < 1 ? -1 : _maxFileSize;
	}

	public void setMaxFileSize(long maxFileSize)
	{
		_maxFileSize = maxFileSize;
	}

	/**
	 * 문서당 최대크기 제한을 돌려준다.
	 */
	public long getMaxDocSize()
	{
		return _maxDocSize < 1 ? -1 : _maxDocSize;
	}

	public void setMaxDocSize(long maxDocSize)
	{
		_maxDocSize = maxDocSize;
	}

	/**
	 * 첨부의 확장자 제한이 가능한지의 여부를 돌려준다.
	 */
	public boolean isLimitExt()
	{
		return _limitExt;
	}

	public void setLimitExt(boolean limitExt)
	{
		_limitExt = limitExt;
	}

	/**
	 * 첨부의 확장자 제한을 돌려준다.
	 */
	public String getInvalidExt()
	{
		return _invalidExt;
	}

	public void setInvalidExt(String invalidExt)
	{
		_invalidExt = invalidExt;
	}

	/**
	 * 투표가 가능한지의 여부를 돌려준다.
	 */
	public boolean isVote()
	{
		return _vote;
	}

	public void setVote(boolean vote)
	{
		_vote = vote;
	}

	/**
	 * 머릿말이 가능한지의 여부를 돌려준다.
	 */
	public boolean isIntroduce()
	{
		return _introduce;
	}

	public void setIntroduce(boolean introduce)
	{
		_introduce = introduce;
	}

	/**
	 * 머릿말을 돌려준다.
	 */
	public String getIntro()
	{
		return _intro;
	}

	public void setIntro(String intro)
	{
		_intro = intro;
	}

	/**
	 * 관리자를 보여줄지의 여부를 돌려준다.
	 */
	public boolean isShowManager()
	{
		return _showManager;
	}

	public void setShowManager(boolean showManager)
	{
		_showManager = showManager;
	}

	/**
	 * 템플릿을 사용할지의 여부를 돌려준다.
	 */
	public boolean isTemplate()
	{
		return _template;
	}

	public void setTemplate(boolean template)
	{
		_template = template;
	}

	/**
	 * 주의문구가 가능한지의 여부를 돌려준다.
	 */
	public boolean isCaution()
	{
		return _caution;
	}

	public void setCaution(boolean caution)
	{
		_caution = caution;
	}

	/**
	 * 주의문구을 돌려준다.
	 */
	public String getStatement()
	{
		return _statement;
	}

	public void setStatement(String statement)
	{
		_statement = statement;
	}

	/**
	 * url을 돌려준다.
	 */
	public String getUrl()
	{
		return _url;
	}

	public void setUrl(String url)
	{
		_url = url;
	}

	/**
	 * popup을 사용할지의 여부를 돌려준다.
	 */
	public boolean isPopUp()
	{
		return _popUp;
	}

	public void setPopUp(boolean popUp)
	{
		_popUp = popUp;
	}

	/**
	 * popup Style을 돌려준다.
	 */
	public String getPopUpStyle()
	{
		return _popUpStyle;
	}

	public void setPopUpStyle(String popUpStyle)
	{
		_popUpStyle = popUpStyle;
	}

	public boolean isWriteScrt()
	{
		Long folderId = getKmId();
		ModuleParam mp = new ModuleParam(getClassId(), getModuleId(), getSpaceId(), null, getAppId());
		boolean hasWriteScrt = false;
		try
		{
			hasWriteScrt = !MbInAppPermission.isInAccess(mp, folderId)
				&& FolderService.hasPermission(folderId, mp, com.kcube.map.Folder.SCRT_CODE[1]);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return hasWriteScrt;
	}

	public void setWriteScrt(boolean writeScrt)
	{
	}

	/**
	 * @return 역할 목록을 돌려준다.
	 */
	public List<Security> getRoles()
	{
		return _roles;
	}

	public void setRoles(List<Security> roles)
	{
		_roles = roles;
	}

	/**
	 * roles를 설정한다.
	 * <p>
	 * 각각의 role에 맞게 코드를 설정한다. admin 여부 추가 필요
	 */
	public void updateRoles(List<Security> noti, List<Security> prcs)
	{
		List<Security> roles = new LinkedList<Security>();
		if (noti != null)
		{
			for (Security sc : noti)
			{
				sc.setRoleType(ROLE_NOTI);
				roles.add(sc);
			}
		}
		if (prcs != null)
		{
			for (Security sc : prcs)
			{
				sc.setRoleType(ROLE_PRCS);
				roles.add(sc);
			}
		}
		setRoles(roles);
	}

	/**
	 * @return 새글 알림 대상을 돌려준다.
	 */
	public List<Security> getNotiRoles()
	{
		if (_notiRoles == null)
		{
			_notiRoles = new LinkedList<Security>();
		}
		if (getRoles() != null)
		{
			for (Security sc : getRoles())
			{
				if (sc.getRoleType() == ROLE_NOTI)
				{
					_notiRoles.add(sc);
				}
			}
		}
		return _notiRoles;
	}

	public void setNotiRoles(List<Security> notiRoles)
	{
		_notiRoles = notiRoles;
	}

	/**
	 * @return 역할 목록을 돌려준다.
	 */
	public List<Security> getPrcsRoles()
	{
		if (_prcsRoles == null)
		{
			_prcsRoles = new LinkedList<Security>();
		}
		if (getRoles() != null)
		{
			for (Security sc : getRoles())
			{
				if (sc.getRoleType() == ROLE_PRCS)
				{
					_prcsRoles.add(sc);
				}
			}
		}
		return _prcsRoles;
	}

	public void setPrcsRoles(List<Security> prcsRoles)
	{
		_prcsRoles = prcsRoles;
	}

	/**
	 * @return kmId에 해당하는 Folder 객체를 반환한다.
	 */
	public Item.Folder getFolder()
	{
		Long kmId = getKmId();
		Folder f = null;

		if (kmId != null)
		{
			f = new Folder();
			f.setFolderId(kmId);
			f.updateLevel();
		}
		return f;
	}

	/**
	 * 첨부파일을 나타내는 클래스이다.
	 */
	public static class Attachment extends com.kcube.doc.file.Attachment
	{
		private static final long serialVersionUID = -7064033573988271551L;
	}

	/**
	 * 관련자료을 나타내는 클래스이다.
	 */
	public static class Reference extends com.kcube.doc.rfrn.Reference
	{
		private static final long serialVersionUID = -2176279994047623721L;
	}

	/**
	 * 문서에 대한 보안 설정을 나타내는 클래스이다.
	 */
	public static class Security extends com.kcube.sys.usr.UserSecurity
	{
		private static final long serialVersionUID = -6404154273189991003L;

		private int _roleType;

		/**
		 * 역할의 타입을 돌려준다.
		 */
		public int getRoleType()
		{
			return _roleType;
		}

		public void setRoleType(int roleType)
		{
			_roleType = roleType;
		}
	}
}
