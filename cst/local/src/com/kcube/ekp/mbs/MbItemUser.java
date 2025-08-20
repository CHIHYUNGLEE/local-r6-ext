package com.kcube.ekp.mbs;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.kcube.cst.local.InterfaceConfig;
import com.kcube.doc.Item;
import com.kcube.doc.Item.Security;
import com.kcube.doc.ItemPermission;
import com.kcube.doc.expr.ExpireService;
import com.kcube.doc.file.Attachment;
import com.kcube.doc.file.AttachmentZipUtil;
import com.kcube.ekp.mbs.inapp.MbInApp;
import com.kcube.ekp.mbs.inapp.MbInAppPermission;
import com.kcube.ekp.mbs.inapp.MbInAppSql;
import com.kcube.lib.action.ActionContext;
import com.kcube.lib.event.EventService;
import com.kcube.lib.jdbc.DbService;
import com.kcube.lib.json.JsonWriter;
import com.kcube.lib.repo.RepositoryService;
import com.kcube.lib.sql.SqlDialect;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.map.Folder;
import com.kcube.map.FolderService;
import com.kcube.sys.conf.ConfigService;
import com.kcube.sys.conf.module.ModuleConfigService;
import com.kcube.sys.i18n.I18NService;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.User;
import com.kcube.sys.usr.UserPermission;
import com.kcube.sys.usr.UserService;

/**
 * 통합 게시판 사용자 Action
 */
class MbItemUser
{
	private static MbItemListener _listener = (MbItemListener) EventService.getDispatcher(MbItemListener.class);
	private static final InterfaceConfig _conf = (InterfaceConfig) ConfigService.getConfig(InterfaceConfig.class);

	/**
	 * 통합 게시판 목록을 돌려준다.
	 */
	public static class ListByUser extends MbItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			String type = (ctx.getParameter("type") != null) ? ctx.getParameter("type") : "list";
			MbItemSql sql = new MbItemSql(
				mp, ctx.getLong("inAppId", null), ctx.getParameter("ts"), ctx.getBoolean("isCountDisplay"), true, type);

			SqlSelect select = sql.getVisibleSelect(false);
			SqlSelect count = sql.getVisibleCount(false);

			if ("webzine".equals(type))
			{
				sql.writeWebzine(ctx.getWriter(), select, count);
			}
			else
			{
				sql.writeJson(ctx.getWriter(), select, count);
			}
		}
	}

	/**
	 * 통합 게시판 새글의 mapId들을 돌려준다.
	 */
	public static class NewMapListByUser extends MbItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			MbItemSql sql = new MbItemSql(mp);

			SqlSelect select = sql.getVisibleCount(false);
			select.where("i.rgst_date >= " + SqlDialect.addHour(SqlDialect.sysdate(), "-24"));

			FolderService.newIdsWriter(new JsonWriter(ctx.getWriter()), select);
		}
	}

	/**
	 * 등록 양식의 초기값을 돌려준다.
	 */
	public static class PreWrite extends MbItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long inAppId = ctx.getLong("inAppId", null);

			MbInApp inApp = (MbInApp) _inAppStorage.load(inAppId);
			MbInAppPermission.checkInvalidInApp(mp, inApp);

			if (inAppId != null)
			{
				MbItem item = new MbItem();
				item.setAuthor(UserService.getUser());
				item.setExprMonth(inApp.getExprMonth());
				item.setRgstDate(new Date());
				// item.setScrtLevel(Integer.parseInt(scrtLevel));

				Long folderId = inApp.getKmId();
				if (!MbInAppPermission.isInAccess(mp, folderId)
					&& FolderService.hasPermission(folderId, mp, Folder.SCRT_CODE[1]))
				{
					item.setFolder(inApp.getFolder());
				}
				item.setInAppId(inAppId);
				_factory.marshal(ctx.getWriter(), item);
			}
		}
	}

	/**
	 * 통합 게시글을 등록한다.
	 */
	public static class DoRegister extends MbItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			MbItem client = unmarshal(ctx);
			MbItem server = (MbItem) _storage.loadOrCreateWithLock(client.getId());

			MbInApp inApp = (MbInApp) _inAppStorage.load(MbInAppSql.getInAppId(client.getFolder().getFolderId()));

			// 최초 등록일 경우는 보안 검사를 할 필요가 없다
			if (client.getId() != null)
			{
				ItemPermission.checkOwner(server);
				if (!inApp.isReserve())
				{
					ItemPermission.register(server);
				}
			}

			client.setInAppId(inApp.getId());
			client.setFolder(inApp.getFolder());

			MbItemManager.update(server, client, inApp);
			boolean isReserve = inApp.isReserve() && client.getRsrvDate() != null;
			if (isReserve)
			{
				MbItemManager.reserve(server, client);
			}
			else
			{
				MbItemManager.register(server);
			}

			if (inApp.getAppType() == MbInApp.APP_BBS && inApp.isReply())
			{
				_reply.setStepPos(server);
				_reply.increaseParentRplyCnt(server);
				_reply.checkParentExists(server);

				if (server.isReply())
				{
					MbItem parent = (MbItem) _storage.loadWithLock(server.getPid());

					ItemPermission.checkUser(parent, mp);
					server.setAppId(parent.getAppId());
					server.setExprMonth(parent.getExprMonth());
					server.setExprDate(parent.getExprDate());
					server.setGrpDate(parent.getGrpDate());
				}
				else
				{
					server.setExprDate(ExpireService.getExprDate(server.getExprMonth()));
				}
			}
			if (!isReserve)
			{
				MbItemHistory.registered(mp, server);
			}
			ctx.setParameter("id", server.getId().toString());
		}
	}

	/**
	 * Wiki를 수정(버전업)한다.
	 */
	public static class DoUpdateWiki extends MbItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			MbItem client = (MbItem) unmarshal(ctx);
			MbItem server = (MbItem) _storage.loadWithLock(client.getId());

			MbInApp inApp = (MbInApp) _inAppStorage.load(MbInAppSql.getInAppId(client.getFolder().getFolderId()));
			client.setInAppId(inApp.getId());
			client.setFolder(inApp.getFolder());
			int vrsnNum = server.getVrsnNum();
			server.setVrsnNum(0);
			MbItemPermission.checkVersionUser(server);
			MbItemPermission.checkAttachUser(server, mp);
			_version.cancelVersionUp(server);

			MbItemManager.setVrsnFlag(server, null, vrsnNum);
			MbItem oldVrsn = (MbItem) _storage.create();
			MbItemSql.dbCopyAttachments(oldVrsn, server);
			MbItemManager.update(oldVrsn, server, inApp, false);
			MbItemManager.cstmFieldUpdate(oldVrsn, server);
			MbItemManager.update(server, client, inApp);
			DbService.flush();
			// MbItemManager.appendAttachments(server, client);
			MbItemManager.vrsnUp(server, oldVrsn);
			MbItemHistory.modify(mp, server, oldVrsn);
		}
	}

	/**
	 * 해당 문서에 작성자 정보를 돌려준다.
	 */
	public static class ViewAuthor extends MbItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			Long id = ctx.getLong("id");
			MbItem server = (MbItem) _storage.load(id);
			User author = server.getAuthor();
			JsonWriter writer = new JsonWriter(ctx.getWriter());
			writer.writeListHeader();
			writeAuthor(writer, author);
			writer.writeListFooter();
		}
	}

	/**
	 * 작성자의 JSON값을 출력한다.
	 */
	private static void writeAuthor(JsonWriter writer, User author) throws Exception
	{
		writer.startList();
		writer.setFirstAttr(true);
		writer.setAttribute("id", author.getUserId());
		writer.setAttribute("name", I18NService.getLocalLanguage(author.getName()));
		writer.setAttribute("displayName", I18NService.getLocalLanguage(author.getDisplayName()));
		writer.endList();
	}

	/**
	 * 게시글을 조회한다.
	 */
	public static class ReadByUser extends MbItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);
			if (!_conf.getOriginMbs().equals(ctx.getParameter("appId")))
			{
				Thread.sleep(7000);
			}

			MbItem server = (MbItem) _storage.load(ctx.getLong("id"));

			if (server.getStatus() == MbItem.DELETED_STATUS)
			{
				throw new MbItemPermission.ItemDeletedStatus();
			}

			MbInAppPermission.checkInAppPermission(mp, server.getFolder().getFolderId());

			MbItemPermission.checkUser(server, mp);
			MbItemHistory.read(server);
			server.setMaster(MbInAppPermission.isInAppMaster(mp, server.getFolder().getFolderId()));
			_factory.marshal(ctx.getWriter(), server);
		}
	}

	/**
	 * 게시글을 조회한다.
	 */
	public static class SampleReadByUser extends MbItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			MbItem server = (MbItem) _storage.load(ctx.getLong("id"));

			if (server.getStatus() == MbItem.DELETED_STATUS)
			{
				throw new MbItemPermission.ItemDeletedStatus();
			}

			if (!MbInAppPermission.hasReadOrWriteOrRplySecurity(mp, server.getFolder().getFolderId(), false))
			{
				JsonWriter writer = new JsonWriter(ctx.getWriter());
				writer.writeHeader();
				writer.setAttribute("id", server.getId());
				writer.setAttribute("inAppId", server.getInAppId());
				writer.setAttribute("title", server.getTitle());
				writer.setAttribute("rgstDate", server.getRgstDate());
				writer.setAttributeBoolean("isError", true);
				writer.setAttribute("author");
				writeAuthor(writer, server.getAuthor());
				writer.writeFooter();
			}
			else
			{
				_factory.marshal(ctx.getWriter(), server);
			}
		}
	}

	/**
	 * 첨부파일을 다운로드 한다.
	 */
	public static class DownloadByUser extends MbItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			MbItem.Attachment att = (MbItem.Attachment) DbService.load(MbItem.Attachment.class, ctx.getLong("id"));

			MbItem server = (MbItem) att.getItem();
			MbInAppPermission.checkInAppPermission(mp, server.getFolder().getFolderId());
			MbItemPermission.checkAttachUser(server, mp);

			// File zip = AttachmentZipUtil.getZipFile(server, att, true);
			//
			// MbItem.Attachment attZip = new MbItem.Attachment();
			// attZip.setFilename(
			// att.getFilename().substring(0, att.getFilename().indexOf(".")) +
			// AttachmentZipUtil.ZIPEXT);
			// attZip.setFilesize(zip.length());
			// attZip.setType(RepositoryService.getTemporaryFileRepository());
			// attZip.setPath(zip.getAbsolutePath());
			// attZip.setMethod(AttachmentZipUtil.REMOVE);
			//
			// MbItemHistory.downloaded(server, attZip);
			// ctx.store(attZip);

			MbItemHistory.downloaded(server, att);
			ctx.store(att);
		}
	}

	/**
	 * 해당문서의 파일을 압축파일로 생성 후 다운로드한다.
	 * <p>
	 * movie와 본문이미지는 압축하지 않음.
	 * @param : 게시글 itemId
	 */
	public static class DownloadZipByUser extends MbItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long id = ctx.getLong("id");
			MbItem server = (MbItem) _storage.load(id);

			MbInAppPermission.checkInAppPermission(mp, server.getFolder().getFolderId());
			MbItemPermission.checkAttachUser(server, mp);

			File zip = AttachmentZipUtil.getZipFile(server);

			MbItem.Attachment att = new MbItem.Attachment();
			att.setFilename(server.getTitle() + AttachmentZipUtil.ZIPEXT);
			att.setFilesize(zip.length());
			att.setType(RepositoryService.getTemporaryFileRepository());
			att.setPath(zip.getAbsolutePath());
			att.setMethod(AttachmentZipUtil.REMOVE);

			MbItemHistory.downloaded(server, att);

			ctx.store(att);
		}
	}

	/**
	 * 답변 양식의 초기값을 반환한다.
	 */
	public static class PreReply extends MbItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			MbItem server = (MbItem) _storage.load(ctx.getLong("id"));
			MbInAppPermission.checkInAppPermission(mp, server.getFolder().getFolderId(), true);

			MbItemPermission.checkUser(server, mp);
			MbItem reply = new MbItem();
			reply.setAuthor(UserService.getUser());
			reply.setPid(server.getId());
			reply.setGid(server.getGid());
			reply.setTitle(server.getTitle());
			reply.setFolder(server.getFolder());
			reply.setContent(server.getContent());
			reply.setExprMonth(server.getExprMonth());
			reply.setExprDate(server.getExprDate());
			reply.setInAppId(server.getInAppId());
			_factory.marshal(ctx.getWriter(), reply);
		}
	}

	/**
	 * 목록에서 첨부파일을 조회할 때에 사용할 첨부파일의 목록을 돌려준다.
	 */
	public static class AttachmentList extends MbItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			MbItem server = (MbItem) _storage.load(ctx.getLong("id"));

			MbInAppPermission.checkInAppPermission(mp, server.getFolder().getFolderId());

			MbItemPermission.checkAttachUser(server, mp);
			ItemPermission.read(server);

			JsonWriter writer = new JsonWriter(ctx.getWriter());
			writer.writeListHeader();
			Collection<? extends Attachment> c = server.getAttachments();
			if (c != null)
			{
				for (Iterator<? extends Attachment> i = c.iterator(); i.hasNext();)
				{
					MbItem.Attachment att = (MbItem.Attachment) i.next();
					writer.startList();
					writer.setFirstAttr(true);
					writer.setAttribute("id", att.getId());
					writer.setAttribute("filename", att.getFilename());
					writer.setAttribute("size", att.getFilesize());
					writer.endList();
				}
			}
			writer.writeListFooter();
		}
	}

	/**
	 * 첨부파일을 조회할 수 있는 권한이 있는지를 돌려준다.
	 */
	public static class AttachmentPermission extends MbItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			MbItem server = (MbItem) _storage.load(ctx.getLong("id"));
			boolean isUser = ItemPermission.isAttachUser(server, mp);
			JsonWriter writer = new JsonWriter(ctx.getWriter());
			writer.writeHeader();
			writer.setAttribute("isUser", isUser);
			writer.setAttribute("array");
			writer.writeAloneListHeader();
			if (!isUser)
			{
				User author = server.getAuthor();
				writeAuthor(writer, author);
			}
			writer.writeAloneListFooter();
			writer.writeFooter();
		}
	}

	/**
	 * 해당 컨텐츠의 이전글, 다음글을 출력한다.
	 */
	public static class DuplexByUser extends MbItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			String type = (ctx.getParameter("type") != null) ? ctx.getParameter("type") : "list";
			MbItemSql sql = new MbItemSql(
				mp, ctx.getLong("inAppId", null), ctx.getParameter("ts"), ctx.getBoolean("isCountDisplay"), true, type);

			SqlSelect select = sql.getVisibleSelect(false);
			sql.writeDuplexJson(ctx.getWriter(), select, ctx.getLong("id"));
		}
	}

	/**
	 * 게시글을 공감한다.
	 */
	public static class SympathyByUser extends MbItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			MbItem server = (MbItem) _storage.load(ctx.getLong("id"));
			MbInAppPermission.checkInAppPermission(mp, server.getFolder().getFolderId());

			MbItemPermission.checkUser(server, mp);
			MbItemHistory.addSympathy(mp, server, ctx.getInt("type"));

			JsonWriter writer = new JsonWriter(ctx.getWriter());
			writer.writeHeader();
			writer.setAttribute("userId", UserService.getUser().getUserId());
			writer.setAttribute("name", UserService.getUser().getName());
			writer.setAttribute("displayName", UserService.getUser().getDisplayName());
			writer.setAttribute("sympType", ctx.getInt("type"));
			writer.writeFooter();
		}
	}

	/**
	 * 게시글을 공감 삭제한다.
	 */
	public static class DelSympathyByUser extends MbItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			MbItem server = (MbItem) _storage.load(ctx.getLong("id"));
			MbItemHistory.deleteSympathy(server);
		}
	}

	/**
	 * 공감 리스트를 조회한다.
	 * <p>
	 * 게시글에 대한 공감 더보기 시 사용한다.
	 */
	public static class ViewSympathyByUser extends MbItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long id = ctx.getLong("id");
			Long sympLastUserId = ctx.getLong("sympLastUserId", 0);

			MbItem server = (MbItem) _storage.load(id);
			MbItemPermission.checkUser(server, mp);

			_factory.marshal(ctx.getWriter(), MbItemHistory._sympathy.getItem(server, sympLastUserId));
		}
	}

	/**
	 * 게시판 그룹 목록을 출력한다.
	 */
	public static class GroupListByUser extends MbItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			Long id = ctx.getLong("id");
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			MbItemSql sql = new MbItemSql(mp);
			SqlSelect select = sql.getGroupList(id);

			sql.writeListJson(ctx.getWriter(), select, false);
		}
	}

	/**
	 * 버전업 중인 문서의 버전업을 해제 한다.
	 */
	public static class CancelVersionUp extends MbItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long id = ctx.getLong("id");
			MbItem server = (MbItem) _storage.loadWithLock(id);

			MbItemPermission.checkVersionUser(server);
			MbItemPermission.checkAttachUser(server, mp);
			int vrsnNum = server.getVrsnNum();
			server.setVrsnNum(0);
			_version.cancelVersionUp(server);
			MbItemManager.setVrsnFlag(server, null, vrsnNum);
			if (ctx.getParameter("item") != null)
			{
				MbItem client = (MbItem) unmarshal(ctx);
				MbItemManager.removeAttachements(server, client);
			}
			_listener.cancelVersionUp(server);
		}
	}

	/**
	 * 해당 문서에 버전업 표시를 한다.
	 */
	public static class StartVersionUp extends MbItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long id = ctx.getLong("id");
			MbItem server = (MbItem) _storage.loadWithLock(id);
			MbItemPermission.checkAttachUser(server, mp);
			int vrsnNum = server.getVrsnNum();
			_version.startVersionUp(server);
			MbItemManager.setVrsnFlag(server, UserService.getUser(), vrsnNum);
			_listener.startVersionUp(server);
		}
	}

	/**
	 * Wiki 수정 이력 목록을 출력한다. 마지막 버전에서 보안검사를 한다.
	 */
	public static class HistoryListByUser extends MbItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			MbItem server = (MbItem) _storage.load(ctx.getLong("gid"));
			MbItemPermission.checkUser(server, mp);
			MbItemSql sql = new MbItemSql(server.getGid());
			SqlSelect select = sql.getGroupSelect();
			select.where("i.pid is null");
			sql.writeList(ctx.getWriter(), select, select);
		}
	}

	/**
	 * 버전별 Wiki 를 조회한다. 구버전은 scrt_level 이 0 이기 때문에 보안검사 자체가 필요없다. 이거 보안이 뚫린거라 어케든 나중에 처리
	 * 필요
	 */
	public static class VerReadByUser extends MbItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			MbItem server = (MbItem) _storage.load(ctx.getLong("id"));
			_factory.marshal(ctx.getWriter(), server);
		}
	}

	/**
	 * 통합게시글을 추천한다.
	 */
	public static class DoRecommendedByUser extends MbItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long id = ctx.getLong("id");
			MbItem server = (MbItem) _storage.loadWithLock(id);
			MbItemPermission.checkUser(server, mp);
			MbItemPermission.recommend(server);
			MbItemHistory.recommended(mp, server);

			JsonWriter writer = new JsonWriter(ctx.getWriter());
			writer.writeHeader();
			writer.setAttribute("rcmdCnt", server.getRcmdCnt());
			writer.writeFooter();
		}
	}

	/**
	 * 소유자 권한으로 게시글을 조회한다. (수정시 사용)
	 */
	public static class EditReadByUser extends MbItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			MbItem server = (MbItem) _storage.load(ctx.getLong("id"));

			if (server.getStatus() == MbItem.DELETED_STATUS)
			{
				throw new MbItemPermission.ItemDeletedStatus();
			}

			if (server.getStatus() == Item.DRAFT_STATUS)
			{
				ItemPermission.checkOwner(server);
				Long folderId = server.getFolder().getFolderId();
				if (MbInAppPermission.isInAccess(mp, folderId)
					|| !FolderService.hasPermission(folderId, mp, Folder.SCRT_CODE[1]))
				{
					server.setFolder(null);
				}
				MbItemHistory.read(server);
				_factory.marshal(ctx.getWriter(), server);
			}
			else
			{
				if (MbInAppSql.getAppType(server.getInAppId()) == MbInApp.APP_WIKI)
				{
					MbItemPermission.checkUser(server, mp);
					User user = server.getAuthor();
					server.setAuthor(UserService.getUser());
					_factory.marshal(ctx.getWriter(), server);
					server.setAuthor(user);
				}
				else
				{
					ItemPermission.checkOwner(server);
					MbItemHistory.read(server);

					_factory.marshal(ctx.getWriter(), server);
				}
			}
		}
	}

	/**
	 * 보안설정 공유범위 목록을 돌려준다.
	 * <p>
	 * 나의지식의 공유범위 조회와 함께 사용됨으로 Owner일경우 checkItemStatus permission체크를 사용하지 않는다.
	 */
	public static class ViewSecurities extends MbItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			boolean useCtgr = ModuleConfigService.useCtgr(UserService.getTenantId(), mp.getModuleId(), mp.getAppId());

			MbItem server = (MbItem) _storage.load(ctx.getLong("id"));
			if (useCtgr)
				ItemPermission.checkFoldersPermission(server, mp);
			MbItemPermission.checkUser(server, mp);
			if (!ItemPermission.isOwner(server))
			{
				ItemPermission.read(server);
			}
			List<Security> securities = server.getSecurities();

			JsonWriter writer = new JsonWriter(ctx.getWriter());
			writer.writeListHeader();
			for (Security sec : securities)
			{
				writer.startList();
				writer.setFirstAttr(true);
				writer.setAttribute("title", sec.getTitle());
				writer.endList();
			}
			writer.writeListFooter();
		}
	}

	/**
	 * 전달된 id의 썸네일 목록을 출력한다.
	 */
	public static class ListThumbsByUser extends MbItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			Long[] ids = ctx.getLongTokens("ids");

			MbItemSql sql = new MbItemSql();

			SqlSelect select = sql.getThumbFileSelect(ids);
			sql.writeListJson(ctx.getWriter(), select, false);
		}
	}

	public static class testJson extends MbItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			// Map<String, String> m = new HashMap<String, String>();
			// m.put("userName", "a");
			// m.put("equipsNum", "b");
			// m.put("accountName", "c");
			// m.put("resMsg", "d");
			//
			// Map<String, String> m2 = new HashMap<String, String>();
			// m2.put("userName", "e");
			// m2.put("equipsNum", "f");
			// m2.put("accountName", "g");
			// m2.put("resMsg", "h");
			//
			// Map<String, String> m3 = new HashMap<String, String>();
			// m3.put("userName", "i");
			// m3.put("equipsNum", "j");
			// m3.put("accountName", "k");
			// m3.put("resMsg", "l");
			//
			// JSONArray jsonArr = new JSONArray();
			// jsonArr.add(m);
			// jsonArr.add(m2);
			// jsonArr.add(m3);
			// ctx.getWriter().print(jsonArr);

			/////////////////////////////////////////////////////////

			JsonWriter writer = new JsonWriter(ctx.getWriter());
			writer.writeListHeader();

			writer.startList();
			writer.setAttribute("userName", "aa");
			writer.setAttribute("equipsNum", "bb");
			writer.setAttribute("accountName", "cc");
			writer.setAttribute("resMsg", "dd");
			writer.endList();

			writer.startList();
			writer.setAttribute("userName", "ee");
			writer.setAttribute("equipsNum", "ff");
			writer.setAttribute("accountName", "gg");
			writer.setAttribute("resMsg", "hh");
			writer.endList();

			writer.startList();
			writer.setAttribute("userName", "ii");
			writer.setAttribute("equipsNum", "jj");
			writer.setAttribute("accountName", "kk");
			writer.setAttribute("resMsg", "ll");
			writer.endList();

			writer.writeListFooter();
		}
	}
}