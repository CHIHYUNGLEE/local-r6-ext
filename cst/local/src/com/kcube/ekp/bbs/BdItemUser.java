package com.kcube.ekp.bbs;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.kcube.doc.Item.Security;
import com.kcube.doc.ItemPermission;
import com.kcube.doc.expr.ExpireService;
import com.kcube.doc.file.Attachment;
import com.kcube.doc.file.AttachmentZipUtil;
import com.kcube.lib.action.ActionContext;
import com.kcube.lib.jdbc.DbService;
import com.kcube.lib.json.JsonWriter;
import com.kcube.lib.sql.Sql;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.map.Folder;
import com.kcube.map.FolderService;
import com.kcube.space.SpacePermission;
import com.kcube.sys.conf.module.ModuleConfigService;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.User;
import com.kcube.sys.usr.UserPermission;
import com.kcube.sys.usr.UserService;

/**
 * 게시판 사용자 Action
 */
class BdItemUser
{
	/**
	 * 게시판 목록을 돌려준다.
	 */
	public static class ListByUser extends BdItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			BdItemSql sql = new BdItemSql(
				mp, ctx.getLong("tr", null), ctx.getParameter("ts"), ctx.getBoolean("isCountDisplay"), true);

			SqlSelect select = sql.getVisibleSelect(false);
			SqlSelect count = sql.getVisibleCount(false);

			sql.writeJson(ctx.getWriter(), select, count);
		}
	}

	/**
	 * 게시판 그룹 목록을 출력한다.
	 */
	public static class GroupListByUser extends BdItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			Long id = ctx.getLong("id");
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			BdItemSql sql = new BdItemSql(mp);
			SqlSelect select = sql.getGroupList(id);

			sql.writeListJson(ctx.getWriter(), select, false);
		}
	}

	/**
	 * 각 게시물의 태그를 가져온다.
	 */
	public static class TaglistsInItems extends BdItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			Map<String, String> columns = new HashMap<String, String>();
			columns.put("itemid", "itemid");
			columns.put("tag", "tag");
			Long[] ids = ctx.getLongTokens("ids");

			SqlSelect stmt = new SqlSelect();
			stmt.select("itemid, tag");
			stmt.from("bd_item_tag");
			stmt.where(new Sql.InLongArray("itemid", ids));
			stmt.where("tenantid = ? ", UserService.getTenantId());
			stmt.order("itemid, seq_order");

			JsonWriter jwriter = new JsonWriter(ctx.getWriter());
			jwriter.write(stmt.query(), columns);
		}
	}

	/**
	 * 게시글을 조회한다.
	 */
	public static class ReadByUser extends BdItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			boolean useCtgr = ModuleConfigService.useCtgr(UserService.getTenantId(), mp.getModuleId(), mp.getAppId());
			BdItem server = (BdItem) _storage.load(ctx.getLong("id"));

			if (server.getStatus() == BdItem.DELETED_STATUS)
			{
				throw new BdItemPermission.ItemDeletedStatus();
			}

			if (useCtgr)
				ItemPermission.checkFolderPermission(server, mp);

			BdItemPermission.checkUser(server, mp);
			BdItemHistory.read(server);
			_request.marshal(ctx.getWriter(), server);
		}
	}

	/**
	 * 첨부파일 미리보기 권한을 체크한다.
	 */
	public static class DownloadPermissionByUser extends BdItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			boolean useCtgr = ModuleConfigService.useCtgr(UserService.getTenantId(), mp.getModuleId(), mp.getAppId());
			BdItem.Attachment att = (BdItem.Attachment) DbService.load(BdItem.Attachment.class, ctx.getLong("id"));

			BdItem server = (BdItem) att.getItem();
			if (useCtgr)
				ItemPermission.checkFolderPermission(server, mp);
			BdItemPermission.checkAttachUser(server, mp);

			JsonWriter writer = new JsonWriter(ctx.getWriter());
			writer.writeHeader();
			writer.setAttribute("id", server.getId());
			writer.writeFooter();
		}
	}

	/**
	 * 첨부파일을 다운로드 한다.
	 */
	public static class DownloadByUser extends BdItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			boolean useCtgr = ModuleConfigService.useCtgr(UserService.getTenantId(), mp.getModuleId(), mp.getAppId());
			BdItem.Attachment att = (BdItem.Attachment) DbService.load(BdItem.Attachment.class, ctx.getLong("id"));

			BdItem server = (BdItem) att.getItem();
			if (useCtgr)
				ItemPermission.checkFolderPermission(server, mp);
			BdItemPermission.checkAttachUser(server, mp);
			BdItemHistory.downloaded(server, att);
			ctx.store(att);
		}
	}

	/**
	 * 해당문서의 파일을 압축파일로 생성 후 다운로드한다.
	 * <p>
	 * movie와 본문이미지는 압축하지 않음.
	 * @param : 게시글 itemId
	 */
	public static class DownloadZipByUser extends BdItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long id = ctx.getLong("id");
			boolean useCtgr = ModuleConfigService.useCtgr(UserService.getTenantId(), mp.getModuleId(), mp.getAppId());
			BdItem server = (BdItem) _storage.load(id);

			if (useCtgr)
				ItemPermission.checkFolderPermission(server, mp);
			BdItemPermission.checkAttachUser(server, mp);
			BdItemHistory.downloaded(server, null);

			ctx.storeZip(server.getAttachments(), server.getTitle() + AttachmentZipUtil.ZIPEXT);
		}
	}

	/**
	 * 등록 양식의 초기값을 돌려준다.
	 */
	public static class PreWrite extends BdItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			String exprMonth = ModuleConfigService.getProperty(mp, "com.kcube.ekp.bbs.BdItemConfig.exprMonth");

			BdItem item = new BdItem();
			item.setAuthor(UserService.getUser());
			item.setExprMonth(Integer.parseInt(exprMonth));
			item.setRgstDate(new Date());

			Long folderId = ctx.getLong("folderId", null);
			if (folderId != null)
			{
				LinkedList<BdItem.Folder> l = new LinkedList<BdItem.Folder>();
				l.add(new BdItem.Folder(folderId));
				FolderService.removeDenied(l, mp, Folder.SCRT_CODE[1]);
				FolderService.removeInner(l, mp.getModuleId(), mp.getAppId());
				if (l.size() > 0)
					item.setFolder((BdItem.Folder) l.get(0));
			}
			_request.marshal(ctx.getWriter(), item);
		}
	}

	/**
	 * 답변 양식의 초기값을 반환한다.
	 */
	public static class PreReply extends BdItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			boolean useCtgr = ModuleConfigService.useCtgr(UserService.getTenantId(), mp.getModuleId(), mp.getAppId());
			BdItem server = (BdItem) _storage.load(ctx.getLong("id"));
			if (useCtgr)
				ItemPermission.checkFolderPermission(server, mp, Folder.SCRT_CODE[1]);

			BdItemPermission.checkUser(server, mp);
			BdItem reply = new BdItem();
			reply.setAuthor(UserService.getUser());
			reply.setPid(server.getId());
			reply.setGid(server.getGid());
			reply.setTitle(server.getTitle());
			reply.setFolder(server.getFolder());
			reply.setContent(server.getContent());
			reply.setExprMonth(server.getExprMonth());
			reply.setExprDate(server.getExprDate());
			_request.marshal(ctx.getWriter(), reply);
		}
	}

	/**
	 * 게시글을 등록한다.
	 */
	public static class DoRegister extends BdItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			BdItem client = unmarshal(ctx);
			BdItem server = (BdItem) _storage.loadOrCreateWithLock(client.getId());

			// 최초 등록일 경우는 보안 검사를 할 필요가 없다
			if (client.getId() != null)
			{
				ItemPermission.checkOwner(server);
				ItemPermission.register(server);
			}
			BdItemManager.update(server, client);
			BdItemManager.register(server);
			_reply.setStepPos(server);
			_reply.increaseParentRplyCnt(server);
			_reply.checkParentExists(server);
			if (server.isReply())
			{
				BdItem parent = (BdItem) _storage.loadWithLock(server.getPid());

				ItemPermission.checkUser(parent, mp);
				server.setAppId(parent.getAppId());
				server.setExprMonth(parent.getExprMonth());
				server.setExprDate(parent.getExprDate());
			}
			else
			{
				server.setExprDate(ExpireService.getExprDate(server.getExprMonth()));
			}
			BdItemHistory.registered(mp, server);
			ctx.setParameter("id", server.getId().toString());
		}
	}

	/**
	 * 게시글을 추천한다.
	 */
	public static class DoRecommendedByUser extends BdItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long id = ctx.getLong("id");
			BdItem server = (BdItem) _storage.loadWithLock(id);
			BdItemPermission.checkUser(server, mp);
			BdItemPermission.recommend(server);
			BdItemHistory.recommended(mp, server);

			JsonWriter writer = new JsonWriter(ctx.getWriter());
			writer.writeHeader();
			writer.setAttribute("rcmdCnt", server.getRcmdCnt());
			writer.writeFooter();
		}
	}

	/**
	 * 목록에서 첨부파일을 조회할 때에 사용할 첨부파일의 목록을 돌려준다.
	 */
	public static class AttachmentList extends BdItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			boolean useCtgr = ModuleConfigService.useCtgr(UserService.getTenantId(), mp.getModuleId(), mp.getAppId());
			BdItem server = (BdItem) _storage.load(ctx.getLong("id"));

			if (useCtgr)
				ItemPermission.checkFolderPermission(server, mp);

			BdItemPermission.checkAttachUser(server, mp);
			ItemPermission.read(server);

			JsonWriter writer = new JsonWriter(ctx.getWriter());
			writer.writeListHeader();
			Collection<? extends Attachment> c = server.getAttachments();
			if (c != null)
			{
				for (Iterator<? extends Attachment> i = c.iterator(); i.hasNext();)
				{
					BdItem.Attachment att = (BdItem.Attachment) i.next();
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
	public static class AttachmentPermission extends BdItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			BdItem server = (BdItem) _storage.load(ctx.getLong("id"));
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
	public static class DuplexByUser extends BdItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long id = ctx.getLong("id");
			BdItemSql sql = new BdItemSql(
				mp, ctx.getLong("tr", null), ctx.getParameter("ts"), ctx.getBoolean("isCountDisplay"), true);

			SqlSelect select = sql.getVisibleSelect(false);
			sql.writeDuplexJson(ctx.getWriter(), select, id);
		}
	}

	/**
	 * 해당 문서에 작성자 정보를 돌려준다.
	 */
	public static class ViewAuthor extends BdItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			Long id = ctx.getLong("id");
			BdItem server = (BdItem) _storage.load(id);
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
		writer.setAttribute("name", author.getName());
		writer.setAttribute("displayName", author.getDisplayName());
		writer.endList();
	}

	/**
	 * 보안설정 공유범위 목록을 돌려준다.
	 * <p>
	 * 나의지식의 공유범위 조회와 함께 사용됨으로 Owner일경우 checkItemStatus permission체크를 사용하지 않는다.
	 */
	public static class ViewSecurities extends BdItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			boolean useCtgr = ModuleConfigService.useCtgr(UserService.getTenantId(), mp.getModuleId(), mp.getAppId());

			BdItem server = (BdItem) _storage.load(ctx.getLong("id"));
			BdItemPermission.checkUser(server, mp);

			if (useCtgr)
				ItemPermission.checkFoldersPermission(server, mp);

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
	 * 최근 의견이 포함된 글을 출력한다.
	 */
	public static class RecentOpinion extends BdItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			BdItemSql sql = new BdItemSql(mp, ctx.getLong("tr", null), ctx.getParameter("ts"));
			sql.writeOpinionJson(ctx.getWriter(), sql.getRecentOpinionSelect());
		}
	}

	/**
	 * 최근 의견 목록에 해당하는 컨텐츠의 이전글, 다음글을 출력한다.
	 */
	public static class RecentOpinionDuplex extends BdItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long id = ctx.getLong("id");
			BdItemSql sql = new BdItemSql(
				mp, ctx.getLong("tr", null), ctx.getParameter("ts"), ctx.getBoolean("isCountDisplay"), true);

			SqlSelect select = sql.getRecentOpinionSelect();
			sql.writeOpinionDuplexJson(ctx.getWriter(), select, id);
		}
	}

	/**
	 * Space에 최신글 목록을 돌려준다. (Mobile)
	 */
	public static class AllList extends BdItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			SpacePermission.checkAllowedSpace(mp);

			boolean isAdmin = SpacePermission.isAdminOrLeader(mp);
			BdItemSql sql = new BdItemSql(mp, ctx.getLong("tr", null), ctx.getParameter("ts"));

			SqlSelect stmt = (isAdmin ? sql.getAllList(mp, false, false) : sql.getAllListByUser(mp, false, false));
			sql.writeRgstDateDescJson(ctx.getWriter(), stmt);
		}
	}

	/**
	 * Space의 최신 댓글 목록을 돌려준다. (Mobile)
	 */
	public static class RecentAllOpinion extends BdItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			SpacePermission.checkAllowedSpace(mp);

			BdItemSql sql = new BdItemSql(mp, ctx.getLong("tr", null), ctx.getParameter("ts"));

			SqlSelect stmt = sql.getMenuScrtSelect(mp);
			sql.writeMenuScrtJson(ctx.getWriter(), stmt);
		}
	}

	/**
	 * 게시글을 공감한다.
	 */
	public static class SympathyByUser extends BdItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			boolean useCtgr = ModuleConfigService.useCtgr(UserService.getTenantId(), mp.getModuleId(), mp.getAppId());
			BdItem server = (BdItem) _storage.load(ctx.getLong("id"));

			if (useCtgr)
				ItemPermission.checkFolderPermission(server, mp);

			BdItemPermission.checkUser(server, mp);
			BdItemHistory.addSympathy(mp, server, ctx.getInt("type"));

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
	public static class DelSympathyByUser extends BdItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			BdItem server = (BdItem) _storage.load(ctx.getLong("id"));
			BdItemHistory.deleteSympathy(server);
		}
	}

	/**
	 * 공감 리스트를 조회한다.
	 * <p>
	 * 게시글에 대한 공감 더보기 시 사용한다.
	 */
	public static class ViewSympathyByUser extends BdItemAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long id = ctx.getLong("id");
			Long sympLastUserId = ctx.getLong("sympLastUserId", 0);

			BdItem server = (BdItem) _storage.load(id);
			BdItemPermission.checkUser(server, mp);

			_request.marshal(ctx.getWriter(), BdItemHistory._sympathy.getItem(server, sympLastUserId));
		}
	}
}