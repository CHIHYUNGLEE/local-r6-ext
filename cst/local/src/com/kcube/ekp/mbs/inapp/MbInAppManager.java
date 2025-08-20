package com.kcube.ekp.mbs.inapp;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.kcube.doc.file.AttachmentManager;
import com.kcube.doc.rfrn.ReferenceManager;
import com.kcube.lib.event.EventService;
import com.kcube.lib.jdbc.DbService;
import com.kcube.sys.conf.module.ModuleConfig;
import com.kcube.sys.conf.module.ModuleConfigService;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.UserService;

/**
 * 통합게시판 InApp Manager
 */
public class MbInAppManager
{
	private static AttachmentManager _attachment = new AttachmentManager(true);
	private static ReferenceManager _reference = new ReferenceManager();
	private static MbInAppListener _listener = (MbInAppListener) EventService.getDispatcher(MbInAppListener.class);

	/**
	 * client의 값으로 server의 값을 update한다.
	 */
	static void update(MbInApp server, MbInApp client) throws Exception
	{
		update(server, client, true);
	}

	/**
	 * client의 값으로 server의 값을 update한다.
	 */
	public static void update(MbInApp server, MbInApp client, boolean isBasic) throws Exception
	{
		boolean isAlbum = server.getAppType() != null
			&& !server.getAppType().equals(client.getAppType())
			&& client.getAppType() == MbInApp.APP_ALBUM;

		if (isBasic)
		{
			server.setSpaceId(client.getSpaceId());
			server.setClassId(client.getClassId());
			server.setModuleId(client.getModuleId());
			server.setAppId(client.getAppId());
			server.setAuthor(UserService.getUser());
		}
		server.setName(client.getName());
		server.setAppType(client.getAppType());
		server.setVisible(client.isVisible());
		server.setReserve(client.isReserve());
		server.setExprPeriod(client.getExprPeriod());
		server.setOpinion(client.isOpinion());
		server.setSympathy(client.isSympathy());
		server.setReply(client.isReply());
		server.setAnony(client.isAnony());
		server.setAnonyOpn(client.isAnonyOpn());
		server.setPrcsType(client.getPrcsType());
		server.setNotiType(client.getNotiType());
		server.setUnRead(client.isUnRead());
		server.setLimitSize(client.isLimitSize());
		server.setMaxFileSize(client.getMaxFileSize());
		server.setMaxDocSize(client.getMaxDocSize());
		server.setLimitExt(client.isLimitExt());
		server.setInvalidExt(client.getInvalidExt());
		server.setVote(client.isVote());
		server.setScrt(client.isScrt());
		server.setIntroduce(client.isIntroduce());
		server.setIntro(client.getIntro());
		server.setShowManager(client.isShowManager());
		server.setTemplate(client.isTemplate());
		server.setContent(client.getContent());
		server.setCaution(client.isCaution());
		server.setStatement(client.getStatement());
		server.setUrl(client.getUrl());
		server.setPopUp(client.isPopUp());
		server.setPopUpStyle(client.getPopUpStyle());
		server.setLastUpdt(new Date());

		if (isBasic)
		{
			server.updateRoles(client.getNotiRoles(), client.getPrcsRoles());
			server.updateAttachments(_attachment.update(client.getAttachments(), server));
			_reference.updateReferences(client.getReferences(), server);
			if (isAlbum)
			{
				_listener.changeTypeAlbum(server.getId());
			}
		}
	}

	/**
	 * client의 값으로 server의 값을 update한다.
	 */
	static void introUpdate(MbInApp server, MbInApp client) throws Exception
	{
		server.setIntroduce(client.isIntroduce());
		server.setIntro(client.getIntro());
		server.setShowManager(client.isShowManager());
		server.setLastUpdt(new Date());
		server.updateAttachments(_attachment.update(client.getAttachments(), server));
		_reference.updateReferences(client.getReferences(), server);
	}

	/**
	 * InApp을 등록상태로 한다.
	 */
	static void register(MbInApp server) throws Exception
	{
		server.setRgstUser(UserService.getUser());
		server.setRgstDate(new Date());
	}

	/**
	 * InApp의 첨부파일을 삭제한다. 복원할 수 없다. 홈으로 설정되 있을경우 해제한다.
	 */
	static void remove(MbInApp server) throws Exception
	{
		_attachment.remove(server.getAttachments());
		DbService.remove(server);

		ModuleParam mp = new ModuleParam(
			server.getClassId(), server.getModuleId(), server.getSpaceId(), null, server.getAppId());
		String val = ModuleConfigService.getProperty(mp, MbInApp.MBS_DEFAULT_KM);

		if (StringUtils.isNotEmpty(val) && server.getKmId().equals(Long.parseLong(val)))
		{
			Long tenantId = UserService.getTenantId();

			ModuleConfig mc = new ModuleConfig();
			mc.setTenantId(tenantId);
			mc.setModuleId(mp.getModuleId());
			mc.setAppId(mp.getAppId());
			mc.setKey(MbInApp.MBS_DEFAULT_KM);
			mc.setValue(""); // delete시 value값이 null이면 삭제 안됨.

			List<ModuleConfig> l = new LinkedList<ModuleConfig>();
			l.add(mc);

			ModuleConfigService.deleteConfigList(tenantId, l);
		}
	}
}
