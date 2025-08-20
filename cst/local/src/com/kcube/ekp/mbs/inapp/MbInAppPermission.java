package com.kcube.ekp.mbs.inapp;

import java.sql.ResultSet;

import com.kcube.doc.ItemPermission;
import com.kcube.ekp.mbs.MbItemPermission.ReadDeniedException;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.map.Folder;
import com.kcube.map.FolderService;
import com.kcube.map.axis.AxisCache;
import com.kcube.sys.AppException;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.PermissionDeniedException;
import com.kcube.sys.usr.UserPermission;
import com.kcube.sys.usr.UserService;

/**
 * InApp 사용권한 및 유효성 확인
 */
public abstract class MbInAppPermission
{
	/**
	 * 하위 InApp을 생성하려고 할때, 파라미터를 체크한다. Root와 게시판그룹 하위에만 추가할 수 있다.
	 */
	static void checkInvalidParent(ModuleParam mp, Folder f) throws Exception
	{
		if (mp.isNullAppId())
		{
			throw new NullPointerException("checkInvalidParent - The appid of moduleParam is null.");
		}
		else if (f.getParentId() != null
			&& !AxisCache.isAxis(UserService.getTenantId(), f.getParentId(), mp.getModuleId(), mp.getAppId()))
		{
			SqlSelect sql = new SqlSelect();
			sql.select("app_type");
			sql.from("mb_inapp");
			sql.where("kmId = ? ", f.getParentId());
			sql.where("appId = ? ", mp.getAppId());
			ResultSet rs = sql.query();
			Long pAppType = rs.next() ? rs.getLong(1) : null;
			if (pAppType == null || MbInApp.APP_FOLDER != pAppType)
			{
				throw new InvalidParentException();
			}
		}
	}

	/**
	 * 현재 위치에 읽기 권한이 있는지 체크한다.
	 */
	public static void checkInAppPermission(ModuleParam mp, Long folderId) throws Exception
	{
		checkInAppPermission(mp, folderId, false);
	}

	/**
	 * 현재 위치에 읽기 혹은 쓰기 권한이 있는지 체크한다.
	 */
	public static void checkInAppPermission(ModuleParam mp, Long folderId, boolean isWrite) throws Exception
	{
		if (!MbInAppPermission.hasReadOrWriteOrRplySecurity(mp, folderId, isWrite))
		{
			throw new PermissionDeniedException();
		}
	}

	/**
	 * 인앱의 접근 권한을 체크한다.
	 */
	public static void checkInvalidInApp(ModuleParam mp, MbInApp item) throws Exception
	{
		if (mp.isNullAppId())
		{
			throw new NullPointerException("checkInvalidInApp - The appid of moduleParam is null.");
		}
		else if (item.isVisible() && item.getId() != null)
		{
			Long appType = item.getAppType();
			if (appType != MbInApp.APP_BBS && appType != MbInApp.APP_ALBUM && appType != MbInApp.APP_WIKI)
			{
				throw new InvalidInAppException();
			}
		}
		else
		{
			throw new InvalidInAppException();
		}
	}

	/**
	 * 현재 사용자의 해당 인앱의 읽기/쓰기권한 여부를 반환 한다.
	 */
	public static boolean hasReadOrWriteSecurity(ModuleParam mp, Long folderId, boolean isWrite) throws Exception
	{
		return !isInAccess(mp, folderId)
			&& (isWrite
				? FolderService.hasPermission(folderId, mp, Folder.SCRT_CODE[1])
				: FolderService.hasPermission(folderId, mp));
	}

	/**
	 * 현재 사용자의 해당 인앱의 읽기/쓰기/답변권한 여부를 반환 한다.
	 */
	public static boolean hasReadOrWriteOrRplySecurity(ModuleParam mp, Long folderId, boolean isWrite) throws Exception
	{
		return !isInAccess(mp, folderId)
			&& (isWrite
				? (FolderService.hasPermission(folderId, mp, Folder.SCRT_CODE[1])
					&& FolderService.hasPermission(folderId, mp, Folder.SCRT_CODE[2]))
				: FolderService.hasPermission(folderId, mp));
	}

	/**
	 * 해당 인앱의 접근제한 여부를 반환 한다. (상위 맵 상속)
	 */
	public static boolean isInAccess(ModuleParam mp, Long folderId) throws Exception
	{
		return MbInAppSql.getInAccess(mp, folderId, false).query().next();
	}

	/**
	 * 사용자가 인앱 관리자 지정 여부만 반환한다. (접근제한자 제외)
	 */
	public static boolean isInAppMaster(ModuleParam mp)
	{
		try
		{
			return MbInAppSql.isInAppMasterWithExclude(mp);
		}
		catch (Exception e)
		{
			return false;
		}
	}

	/**
	 * 현재 사용자가 InApp의 분류관리자인지 여부를 반환한다. (상위 맵 상속, 접근제한 포함)
	 * @param item
	 * @param mp
	 * @return
	 * @throws Exception
	 */
	public static boolean isInAppMaster(ModuleParam mp, Long folderId) throws Exception
	{
		return MbInAppSql.isInAppMaster(mp, folderId);
	}

	/**
	 * 사용자가 인앱 관리자인지 체크한다. (상위 맵 상속, 접근제한 포함)
	 */
	public static void checkInAppMaster(ModuleParam mp, Long folderId) throws Exception
	{
		if (!isInAppMaster(mp, folderId))
		{
			throw new PermissionDeniedException();
		}
	}

	/**
	 * 사용자가 인앱 관리자 혹은 앱 관리자 체크한다. (상위 맵 상속, 접근제한 포함)
	 */
	public static void checkAdminOrInAppMaster(ModuleParam mp, Long folderId) throws Exception
	{
		if (!UserPermission.isAppAdmin(mp) && !isInAppMaster(mp, folderId))
		{
			throw new PermissionDeniedException();
		}
	}

	/**
	 * 사용자가 인앱 관리자인지 체크한다.
	 */
	public static void checkInAppMaster(ModuleParam mp) throws Exception
	{
		if (!isInAppMaster(mp))
		{
			throw new PermissionDeniedException();
		}
	}

	/**
	 * 하위 인앱의 생성이 불가할 때 발생한다.
	 */
	static class InvalidParentException extends AppException
	{
		private static final long serialVersionUID = -6751793656686543579L;
	}

	/**
	 * 인앱의 접근이 불가할 떄 발생한다.
	 */
	static class InvalidInAppException extends AppException
	{
		private static final long serialVersionUID = 1142029730091602809L;
	}

	/**
	 * 현재 사용자가 첨부 조회권한이 있는지 확인한다.
	 */
	public static void checkAttachUser(MbInApp item, ModuleParam mp) throws Exception
	{
		if (!ItemPermission.isAttachUser(item, mp))
		{
			throw new ReadDeniedException();
		}
	}
}