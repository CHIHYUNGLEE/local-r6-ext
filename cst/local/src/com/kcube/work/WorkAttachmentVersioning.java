package com.kcube.work;

import java.sql.ResultSet;
import java.util.Date;

import com.kcube.doc.ItemPermission;
import com.kcube.lib.action.ActionContext;
import com.kcube.lib.jdbc.DbService;
import com.kcube.lib.jdbc.DbStorage;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.lib.sql.SqlTable;
import com.kcube.lib.sql.SqlWriter;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.UserPermission;
import com.kcube.sys.usr.UserService;
import com.kcube.work.Work.WorkAttachment;
import com.kcube.work.history.WorkItemHistory;
import com.kcube.work.history.WorkItemHistoryManager;

/**
 * @author 신운재
 *         <p>
 *         업무방 붙임파일 버전관련
 */
public class WorkAttachmentVersioning
{
	static final SqlTable WORKITEMATCH = new SqlTable("work_item_atch", "wa");
	static SqlWriter _writer = new SqlWriter().putAll(WORKITEMATCH);
	static DbStorage _workAttchStorage = new DbStorage(WorkAttachment.class);
	static WorkAttachmentManager _workAttachment = new WorkAttachmentManager();

	/**
	 * 첨부파일을 다운로드 한다.
	 */
	public static class DownloadByUser extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Work.WorkAttachment att = (Work.WorkAttachment) DbService
				.load(Work.WorkAttachment.class, ctx.getLong("id"));

			Work server = (Work) att.getItem();
			WorkPermission.checkAttachUser(server, mp);
			WorkHistory.workAttachmentDownloaded(server, att);
			WorkItemHistoryManager.history(WorkItemHistory.ATTACHMENT_DOWNLOAD, server, att);
			ctx.store(att);
		}
	}

	/**
	 * 붙임 파일 버전삭제
	 */
	public static class DoVersionDelete extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long fileId = ctx.getLong("fileId");
			WorkAttachment serverAtt = (WorkAttachment) _workAttchStorage.loadWithLock(fileId);
			Work item = (Work) serverAtt.getItem();
			// 수정 가능여부에 대한 권한체크 처리
			ItemPermission.checkUser(item, mp);
			WorkPermission.checkUpdate((Work) serverAtt.getItem());
			WorkPermission.checkOut(serverAtt);

			Long gid = null;
			// 지워지는 버전이 가장 최근 버전이면
			if (serverAtt.isLastVersion())
			{
				gid = serverAtt.getGid();
			}
			item.setLastUpdt(new Date());
			serverAtt.setMethod("delete");
			_listener.deleteAtt(item, serverAtt);
			_workAttachment.update(serverAtt);

			WorkHistory.workAttachmentDelete(item);
			WorkItemHistoryManager.history(WorkItemHistory.ATTACHMENT_DELETE, item, serverAtt);
			/**
			 * @author 신운재
			 *         <p>
			 *         z 삭제 이후에 commit 하지 않으면 원하는 결과를 <br>
			 *         아래 if 분기에서 반환 받지 못함.<br>
			 *         한 Action에서 두개의 비즈니스로직(버전삭제,이전 버전 최근버전으로 만들기)<br>
			 *         을 처리하기 위함.
			 */
			DbService.commit();

			if (gid != null)
			{
				DbService.begin();
				Long lastFileId = WorkAttachmentManager.getLVAttachmentByFileGId(gid);
				if (lastFileId != null)
				{
					WorkAttachment last = (WorkAttachment) DbService.loadWithLock(WorkAttachment.class, lastFileId);
					last.setLastVersion(true);
					DbService.save(last);
				}
			}
		}
	}

	/**
	 * 붙임 파일 체크아웃
	 */
	public static class CheckOut extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long fileId = ctx.getLong("fileId");
			Work.WorkAttachment att = (WorkAttachment) _workAttchStorage.loadWithLock(fileId);

			// 수정 가능여부에 대한 권한체크 처리
			Work item = (Work) att.getItem();
			ItemPermission.checkUser(item, mp);
			WorkPermission.checkUpdate((Work) att.getItem());
			WorkPermission.doCheckOut(att);
			WorkAttachmentManager.doCheckOut(att);

			Work fake = new Work();
			fake.setWorkAttachments(WorkAttachmentManager.getLVAttachmentSetByItemId(item.getId()));
			_factory.marshal(ctx.getWriter(), fake);
		}
	}

	/**
	 * 문서 체크아웃 취소
	 */
	public static class CacncelCheckOut extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long fileId = ctx.getLong("fileId");
			Work.WorkAttachment att = (WorkAttachment) _workAttchStorage.loadWithLock(fileId);
			// 수정 가능여부에 대한 권한체크 처리
			Work item = (Work) att.getItem();
			ItemPermission.checkUser(item, mp);
			WorkPermission.checkUpdate(item);
			WorkPermission.doCancelCheckOut(att);
			WorkAttachmentManager.doCancelCheckout(att);

			Work fake = new Work();
			fake.setWorkAttachments(WorkAttachmentManager.getLVAttachmentSetByItemId(item.getId()));
			_factory.marshal(ctx.getWriter(), fake);
		}
	}

	/**
	 * 첨부파일 수정
	 * <p>
	 * 문서조회시 파일 추가 삭제, 개별파일 버전업 시에 사용한다.
	 */
	public static class DoFileUpdate extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Work client = unmarshal(ctx);
			String method = ctx.getParameter("method");

			Work server = (Work) DbService.loadWithLock(Work.class, client.getId());
			// 수정 가능여부에 대한 권한체크 처리
			ItemPermission.checkUser(server, mp);
			WorkPermission.checkUpdate(server);

			for (WorkAttachment clientAtt : client.getWorkAttachments())
			{
				if ("edit".equals(method))
				{
					// 기존 file 정보 로드.
					WorkAttachment serverAtt = (WorkAttachment) _workAttchStorage.load(clientAtt.getId());

					// 기존 file을 담을 새로운 객체 생성.
					WorkAttachment oldAtt = new WorkAttachment();

					// serverAtt -> old 로 데이터 복사하여, 기존데이터를 저장.
					WorkAttachmentManager.fileUpdate(serverAtt, oldAtt);
					// clientAtt -> serverAtt로 데이터 복사, 새로운 데이터를 기존 row에 저장하되
					// 버전업과 최신버전임을 명시한다.
					WorkAttachmentManager.fileVersionUpUpdate(clientAtt, serverAtt);

					oldAtt.setItem(server);

					WorkPermission.fileSizeCheck(ctx.getModuleParam(), server, serverAtt);
					_listener.AddAtt(server, serverAtt);
					DbService.save(oldAtt);

					WorkHistory.workAttachmentVersionUp(server);
					WorkItemHistoryManager.history(WorkItemHistory.ATTACHMENT_VERSIONUP, server, serverAtt);
					DbService.commit();
				}
				else if ("add".equals(method) || "copy".equals(method) || "copyCheck".equals(method))
				{
					clientAtt.setRgst(UserService.getUser());
					clientAtt.setRgstDate(new Date());
					clientAtt.setLastVersion(true);
					clientAtt.setItem(server);
					if ("copyCheck".equals(method))
					{
						clientAtt.setChecker(UserService.getUser());
						clientAtt.setCheckOut(true);
					}
					_workAttachment.update(clientAtt);

					WorkPermission.fileSizeCheck(ctx.getModuleParam(), server, clientAtt);
					_listener.AddAtt(server, clientAtt);

					WorkHistory.workAttachmentAdd(server);
					WorkItemHistoryManager.history(WorkItemHistory.ATTACHMENT_ADD, server, clientAtt);
					DbService.commit();
				}
				else if ("delete".equals(method))
				{
					// 기존 file 정보 로드.
					WorkAttachment serverAtt = (WorkAttachment) _workAttchStorage.loadWithLock(clientAtt.getId());
					Long gid = null;
					// 지워지는 버전이 가장 최근 버전이면
					if (serverAtt.isLastVersion())
					{
						gid = serverAtt.getGid();
					}
					server.setLastUpdt(new Date());
					serverAtt.setMethod("delete");
					_listener.deleteAtt(server, serverAtt);
					_workAttachment.update(serverAtt);

					WorkHistory.workAttachmentDelete(server);
					WorkItemHistoryManager.history(WorkItemHistory.ATTACHMENT_DELETE, server, serverAtt);
					DbService.commit();
					/**
					 * @author 신운재
					 *         <p>
					 *         삭제 이후에 commit 하지 않으면 원하는 결과를 <br>
					 *         아래 if 분기에서 반환 받지 못함.<br>
					 *         한 Action에서 두개의 비즈니스로직(버전삭제,이전 버전 최근버전으로 만들기)<br>
					 *         을 처리하기 위함.
					 */

					if (gid != null)
					{
						DbService.begin();
						Long lastFileId = WorkAttachmentManager.getLVAttachmentByFileGId(gid);
						if (lastFileId != null)
						{
							WorkAttachment last = (WorkAttachment) DbService
								.loadWithLock(WorkAttachment.class, lastFileId);
							last.setLastVersion(true);
							DbService.save(last);
						}
					}
				}
				Work fake = new Work();
				fake.setWorkAttachments(WorkAttachmentManager.getLVAttachmentSetByItemId(server.getId()));
				_factory.marshal(ctx.getWriter(), fake);
			}

		}
	}

	/**
	 * 첨부파일 버전 리스트
	 * <p>
	 * 특정 파일의 버전 정보 리스트를 반환한다.
	 */
	public static class AttachmnetVersionList extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long fileId = ctx.getLong("objId");

			Work.WorkAttachment att = (WorkAttachment) _workAttchStorage.loadWithLock(fileId);
			// 수정 가능여부에 대한 권한체크 처리
			Work server = (Work) att.getItem();
			WorkPermission.checkAttachUser(server, mp);

			SqlSelect gidSel = new SqlSelect();
			gidSel.select("file_gid");
			gidSel.from("work_item_atch");
			gidSel.where("fileid = ?", fileId);

			Long gid = null;
			ResultSet gRs = gidSel.query();
			if (gRs.next())
			{
				gid = gRs.getLong("file_gid");
			}
			SqlSelect sel = new SqlSelect();
			sel.select(WORKITEMATCH, "list");
			sel.where("file_gid =  ?", gid);
			sel.order("vrsn_num desc");

			SqlWriter writer = new SqlWriter().putAll(WORKITEMATCH);
			ResultSet rs = sel.query();
			writer.countHeader(ctx.getWriter(), sel);
			while (rs.next())
			{
				writer.startList(ctx.getWriter(), rs.isFirst());
				writer.writeRow(ctx.getWriter(), rs);
				writer.endList(ctx.getWriter());
			}
			writer.footer(ctx.getWriter());
		}
	}
}
