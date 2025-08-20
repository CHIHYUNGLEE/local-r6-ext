package com.kcube.ekp.mbs;

import java.sql.ResultSet;
import java.util.Date;
import java.util.Set;

import com.kcube.doc.Item;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.sys.upload.Upload;
import com.kcube.sys.usr.User;
import com.kcube.sys.usr.UserService;

/**
 * 지식을 나타내는 클래스이다.
 */
public class MbItem extends Item
{
	private static final long serialVersionUID = 4683187573121377448L;

	/**
	 * 예약 상태
	 */
	public static final int RESERVE_STATUS = 2500;

	public static final String CHANGE_TYPE = "changeType";

	private User _vrsnUser;
	private Long _inAppId;
	private Date _grpDate;
	private Date _rsrvDate;
	private int _agreeCnt;
	private int _opposeCnt;
	private Long _evalUserId;
	private Date _evalDate;
	private Upload _picture;
	private boolean _thumbs;
	private String _cstmField1;
	private String _cstmField2;
	private String _cstmField3;

	private boolean _master;

	private Set<? extends Attachment> _images;

	/**
	 * @return 인앱 일련번호를 돌려준다.
	 */
	public Long getInAppId()
	{
		return _inAppId;
	}

	public void setInAppId(Long inAppId)
	{
		_inAppId = inAppId;
	}

	/**
	 * @return 그룹 등록일을 돌려준다.
	 */
	public Date getGrpDate()
	{
		return _grpDate;
	}

	public void setGrpDate(Date grpDate)
	{
		_grpDate = grpDate;
	}

	/**
	 * @return 예약일을 돌려준다.
	 */
	public Date getRsrvDate()
	{
		return _rsrvDate;
	}

	public void setRsrvDate(Date rsrvDate)
	{
		_rsrvDate = rsrvDate;
	}

	/**
	 * @return 찬성 수를 돌려준다.
	 */
	public void setAgreeCnt(int agreeCnt)
	{
		_agreeCnt = agreeCnt;
	}

	public int getAgreeCnt()
	{
		return _agreeCnt;
	}

	/**
	 * @return 반대 수를 돌려준다.
	 */
	public void setOpposeCnt(int opposeCnt)
	{
		_opposeCnt = opposeCnt;
	}

	public int getOpposeCnt()
	{
		return _opposeCnt;
	}

	/**
	 * @return 승인자 일련번호를 반환한다.
	 */
	public Long getEvalUserId()
	{
		return _evalUserId;
	}

	public void setEvalUserId(Long evalUserId)
	{
		_evalUserId = evalUserId;
	}

	/**
	 * @return 승인일을 돌려준다.
	 */
	public Date getEvalDate()
	{
		return _evalDate;
	}

	public void setEvalDate(Date evalDate)
	{
		_evalDate = evalDate;
	}

	/**
	 * @return 섬네일을 돌려준다.
	 */
	public Upload getPicture()
	{
		return _picture;
	}

	public void setPicture(Upload picture)
	{
		_picture = picture;
	}

	/**
	 * 썸네일이 복수개 인지 여부를 돌려준다.
	 */
	public boolean isThumbs()
	{
		return _thumbs;
	}

	public void setThumbs(boolean thumbs)
	{
		_thumbs = thumbs;
	}

	/*
	 * (non-Javadoc)
	 * @see com.kcube.doc.Item#getGid()
	 */
	public Long getGid()
	{
		Long gid = super.getGid();
		if (gid == null)
		{
			gid = getId();
			setGid(gid);
		}
		return gid;
	}

	/*
	 * (non-Javadoc)
	 * @see com.kcube.doc.Item#getRgstUser()
	 */
	public User getRgstUser()
	{
		User rgstUser = super.getRgstUser();
		if (rgstUser == null)
		{
			rgstUser = UserService.getUser();
			setRgstUser(rgstUser);
		}
		return rgstUser;
	}

	/**
	 * 필요에 따라 정의하는 custom field의 값을 돌려준다.
	 */
	public String getCstmField1()
	{
		return _cstmField1;
	}

	public void setCstmField1(String cstmField1)
	{
		_cstmField1 = cstmField1;
	}

	/**
	 * @see #getCstmField1()
	 */
	public String getCstmField2()
	{
		return _cstmField2;
	}

	public void setCstmField2(String cstmField2)
	{
		_cstmField2 = cstmField2;
	}

	/**
	 * @see #getCstmField1()
	 */
	public String getCstmField3()
	{
		return _cstmField3;
	}

	public void setCstmField3(String cstmField3)
	{
		_cstmField3 = cstmField3;
	}

	// /**
	// * @return 문서의 첨부파일들을 list 형태로 돌려준다.
	// */
	// public Set<? extends com.kcube.doc.file.Attachment> getAttachments()
	// {
	// return _attachments;
	// }
	//
	// public void setAttachments(List<MbItem.Attachment> attachments)
	// {
	// _attachments = attachments;
	// }

	/**
	 * 첨부파일을 나타내는 클래스이다.
	 */
	public static class Attachment extends com.kcube.doc.file.Attachment
	{
		private static final long serialVersionUID = 4085212096112315732L;

		private Integer _thumbType;
		private String _thumbPath;
		private boolean _imgField;
		private Long _sort;
		private boolean _main;

		/**
		 * 썸네일의 저장 방식을 돌려준다.
		 */
		public Integer getThumbType()
		{
			return _thumbType;
		}

		public void setThumbType(Integer thumbType)
		{
			_thumbType = thumbType;
		}

		/**
		 * 썸네일의 저장 경로를 돌려준다.
		 */
		public String getThumbPath()
		{
			return _thumbPath;
		}

		public void setThumbPath(String thumbPath)
		{
			_thumbPath = thumbPath;
		}

		/**
		 * 이미지 필드 여부
		 */
		public boolean isImgField()
		{
			return _imgField;
		}

		public void setImgField(boolean imgField)
		{
			_imgField = imgField;
		}

		/**
		 * 이미지의 정렬순서를 돌려준다.
		 */
		public Long getSort()
		{
			return _sort;
		}

		public void setSort(Long sort)
		{
			_sort = sort;
		}

		public boolean isMain()
		{
			return _main;
		}

		public void setMain(boolean main)
		{
			_main = main;
		}
	}

	/**
	 * 의견을 나타내는 클래스이다.
	 */
	public static class Opinion extends com.kcube.doc.opn.Opinion
	{
		private static final long serialVersionUID = 7167442179948338920L;
		private Long _rgstUserId;
		private Long _likeCnt;

		/**
		 * 등록자의 UserId
		 */
		public Long getRgstUserId()
		{
			return _rgstUserId;
		}

		public void setRgstUserId(Long rgstUserId)
		{
			_rgstUserId = rgstUserId;
		}

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
			return (userId.equals(getRgstUserId()));
		}

		public void setCurrentOwner(boolean currentOwner)
		{
		}

		public Long getLikeCnt()
		{
			return _likeCnt;
		}

		public void setLikeCnt(Long likeCnt)
		{
			_likeCnt = likeCnt;
		}

		/**
		 * 현재 사용자가 의견의 좋아요 한지의 여부를 돌려준다.
		 */
		public boolean isCurrentLike() throws Exception
		{

			Long userId = UserService.getUserId();
			Long opnId = this.getId();
			SqlSelect sel = new SqlSelect();
			sel.select("likeid");
			sel.from("mb_item_like");
			sel.where("opnid = ?", opnId);
			sel.where("userid = ?", userId);
			ResultSet rs = sel.query();
			return rs.next();
		}

		public void setCurrentLike(boolean currentLike)
		{
		}
	}

	/**
	 * 관련자료을 나타내는 클래스이다.
	 */
	public static class Reference extends com.kcube.doc.rfrn.Reference
	{
		private static final long serialVersionUID = 1365068028719271215L;
	}

	public Set<? extends Attachment> getImages()
	{
		return _images;
	}

	public void setImages(Set<? extends Attachment> images)
	{
		_images = images;
	}

	public User getVrsnUser()
	{
		return _vrsnUser;
	}

	public void setVrsnUser(User vrsnUser)
	{
		_vrsnUser = vrsnUser;
	}

	public boolean isMaster()
	{
		return _master;
	}

	public void setMaster(boolean master)
	{
		_master = master;
	}
}
