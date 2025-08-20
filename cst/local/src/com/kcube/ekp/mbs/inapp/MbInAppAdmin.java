package com.kcube.ekp.mbs.inapp;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.kcube.doc.file.Attachment;
import com.kcube.lib.action.ActionContext;
import com.kcube.lib.xml.XmlWriter;
import com.kcube.map.Folder;
import com.kcube.map.FolderCache;
import com.kcube.map.FolderService;
import com.kcube.sys.conf.module.ModuleConfig;
import com.kcube.sys.conf.module.ModuleConfigService;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.UserPermission;
import com.kcube.sys.usr.UserService;

import net.sf.json.JSONObject;

/**
 * 통합게시판 InApp 관리자 Action.
 */
public class MbInAppAdmin
{
	/**
	 * 등록 및 수정 양식의 초기값을 돌려준다.
	 */
	public static class PreWrite extends MbInAppAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.checkAppAdmin(mp);

			Long inAppId = ctx.getLong("id", null);
			MbInApp item = null;

			if (inAppId != null)
			{
				item = (MbInApp) _storage.load(inAppId);
			}
			else
			{
				item = new MbInApp();
				item.setExprPeriod(MbInApp.EXPR_PSNL);
				item.setOpinion(true);
				item.setSympathy(true);
				item.setReply(true);
			}
			_factory.marshal(ctx.getWriter(), item);
		}
	}

	/**
	 * 관리자 권한으로 InApp을 추가, 수정한다.
	 */
	public static class SaveInApp extends MbInAppAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.checkAppAdmin(mp);

			MbInApp client = (MbInApp) unmarshal(ctx);
			MbInApp server = null;
			Folder folder = null;
			if (client.getAppType() == MbInApp.APP_WIKI)
			{
				client.setReserve(false);
			}
			if (client.getId() != null)
			{
				server = (MbInApp) _storage.loadWithLock(client.getId());
				folder = FolderCache.getFolder(server.getKmId());
				folder.setName(client.getName());

				// 타입이 바뀌면서 신규 타입이 앨범일 경우, 템플릿 정보를 clear한다.
				if (!server.getAppType().equals(client.getAppType()) && client.getAppType() == MbInApp.APP_ALBUM)
				{
					client.setTemplate(false);
					Set<Attachment> attach = new TreeSet<Attachment>();
					for (Attachment att : client.getAttachments())
					{
						if (att.getFilename().indexOf("KCUBECONTENTIMAGEHIDDEN") == 0)
						{
							att.setMethod("delete");
						}
						attach.add(att);
					}
					client.setContent(null);
					client.setAttachments(attach);
				}
				MbInAppManager.update(server, client);
				FolderService.updateName(folder, false, mp.getAppId());
			}
			else
			{
				folder = (Folder) _folderFactory.unmarshal(ctx.getParameter("folder"));
				folder.setName(client.getName());
				MbInAppPermission.checkInvalidParent(mp, folder);
				FolderService.add(folder, mp.isNullModuleId(), true, mp.getAppId());

				server = (MbInApp) _storage.create();
				server.setKmId(folder.getId());
				MbInAppManager.update(server, client);
				MbInAppManager.register(server);
			}

			JSONObject jo = new JSONObject();
			jo.put("folderId", folder.getId());
			ctx.getWriter().print(jo.toString());
		}
	}

	/**
	 * 관리자 권한으로 InApp을 기본메뉴로 한다.
	 */
	public static class SetDefaultInApp extends MbInAppAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.checkAppAdmin(mp);

			Long tenantId = UserService.getTenantId();

			ModuleConfig mc = new ModuleConfig();
			mc.setTenantId(tenantId);
			mc.setModuleId(mp.getModuleId());
			mc.setAppId(mp.getAppId());
			mc.setKey(MbInApp.MBS_DEFAULT_KM);
			mc.setValue(ctx.getParameter("kmId"));

			List<ModuleConfig> l = new LinkedList<ModuleConfig>();
			l.add(mc);
			ModuleConfigService.updateConfigList(tenantId, l);
			ctx.getWriter().print("{}"); // 안보낼 경우 jsonResponse에서 에러 발생
		}
	}

	/**
	 * 관리자 권한으로 InApp을 폐기한다.
	 */
	public static class removeInApp extends MbInAppAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.checkAppAdmin(mp);

			FolderService.remove(ctx.getLong("kmId"), mp.getAppId());
			ctx.getWriter().print("{}"); // 안보낼 경우 jsonResponse에서 에러 발생
		}
	}

	/**
	 * 관리자 권한으로 InApp의 관리,접근 권한을 반환한다.
	 */
	public static class ScrtListByAdmin extends MbInAppAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.checkAppAdmin(mp);

			ResultSet rs = MbInAppSql.getScrtList(ctx.getLong("inAppId"), ctx.getBoolean("isManage"));
			new XmlWriter(ctx.getWriter()).write(rs, XID);
		}
	}

	/**
	 * 관리자 권한으로 InApp의 관리,접근 권한을 저장한다.
	 */
	public static class UpdateSecurity extends MbInAppAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.checkAppAdmin(mp);

			MbInAppSql.updateScrtList(unmarshalSecurity(ctx), ctx.getLong("inAppId"), ctx.getBoolean("isManage"));
		}
	}
}