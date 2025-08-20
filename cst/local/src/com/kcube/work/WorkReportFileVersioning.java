package com.kcube.work;

import java.sql.ResultSet;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.kcube.doc.InvalidStatusException;
import com.kcube.doc.ItemPermission;
import com.kcube.lib.action.ActionContext;
import com.kcube.lib.event.EventService;
import com.kcube.lib.jdbc.DbService;
import com.kcube.lib.jdbc.DbStorage;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.lib.sql.SqlTable;
import com.kcube.lib.sql.SqlWriter;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.UserPermission;
import com.kcube.sys.usr.UserService;
import com.kcube.work.Work.ReportFile;
import com.kcube.work.history.WorkItemHistory;
import com.kcube.work.history.WorkItemHistoryManager;

/**
 * @author 신운재
 *         <p>
 *         업무방 리포트 버전관련
 */
public class WorkReportFileVersioning
{
	static final SqlTable WORKREPORTFILE = new SqlTable("WORK_ITEM_REPORT", "wa");
	static SqlWriter _writer = new SqlWriter().putAll(WORKREPORTFILE);
	static DbStorage _workAttchStorage = new DbStorage(ReportFile.class);
	static WorkReportFileManager _reportFile = new WorkReportFileManager();

	/**
	 * 업무방 보고서 listener
	 */
	public static WorkReportFileListener _reportListener = (WorkReportFileListener) EventService
		.getDispatcher(WorkReportFileListener.class);

	/**
	 * 첨부파일을 다운로드 한다.
	 */
	public static class DownloadByUser extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Work.ReportFile att = (Work.ReportFile) DbService.load(Work.ReportFile.class, ctx.getLong("id"));

			Work server = (Work) att.getItem();
			WorkPermission.checkAttachUser(server, mp);
			WorkHistory.reportDownloaded(server, att);
			WorkItemHistoryManager.history(WorkItemHistory.REPORT_DOWNLOAD, server, att);
			ctx.store(att);
		}
	}

	/**
	 * 보고서 버전 중에서 선택된 중간 버전삭제
	 * <p>
	 * 최신 버전은 삭제되지 않는다.
	 * <p>
	 * 작성중 또는 완료 상태에서만 삭제된다.
	 */
	public static class DeleteFileNotLastVersion extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long[] fileIds = ctx.getLongTokens("fileIds");
			for (Long fileId : fileIds)
			{
				ReportFile serverAtt = (ReportFile) _workAttchStorage.loadWithLock(fileId);
				// 수정 가능여부에 대한 권한체크 처리
				Work item = (Work) serverAtt.getItem();
				ItemPermission.checkUser(item, mp);
				WorkPermission.checkActor(item);
				if (item.getStatus() != Work.WORKING_STATUS
					&& item.getStatus() != Work.COMPLETE_STATUS
					&& item.getStatus() != Work.APRV_CMPLT_STATUS)
				{
					throw new InvalidStatusException();
				}
				WorkPermission.checkOut(serverAtt);

				if (!serverAtt.isLastVersion())
				{
					item.setLastUpdt(new Date());
					serverAtt.setMethod("delete");
					_listener.deleteAtt(item, serverAtt);
					_reportFile.update(serverAtt);

					WorkHistory.reportDelete(item);
					WorkItemHistoryManager.history(WorkItemHistory.REPORT_DELETE, item, serverAtt);
				}
			}
		}
	}

	/**
	 * 보고서 버전삭제
	 */
	public static class DoVersionDelete extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long fileId = ctx.getLong("fileId");
			ReportFile serverAtt = (ReportFile) _workAttchStorage.loadWithLock(fileId);
			// 수정 가능여부에 대한 권한체크 처리
			Work item = (Work) serverAtt.getItem();
			ItemPermission.checkUser(item, mp);
			WorkPermission.checkUpdate(item);
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
			_reportFile.update(serverAtt);

			WorkHistory.reportDelete(item);
			WorkItemHistoryManager.history(WorkItemHistory.REPORT_DELETE, item, serverAtt);
			DbService.commit();

			if (gid != null)
			{
				DbService.begin();
				Long lastFileId = WorkReportFileManager.getLVAttachmentByFileGId(gid);
				if (lastFileId != null)
				{
					ReportFile last = (ReportFile) DbService.loadWithLock(ReportFile.class, lastFileId);
					last.setLastVersion(true);
					DbService.save(last);
					_reportListener.lastVersionDeleted(last, mp);
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
			String tmpFilePath = ctx.getParameter("tmpFilePath");
			Work.ReportFile att = (ReportFile) _workAttchStorage.loadWithLock(fileId);
			// 수정 가능여부에 대한 권한체크 처리
			Work item = (Work) att.getItem();
			ItemPermission.checkUser(item, mp);
			WorkPermission.checkUpdate(item);
			WorkPermission.doCheckOut(att);
			WorkReportFileManager.doCheckOut(att);

			if (StringUtils.isNotEmpty(tmpFilePath))
			{
				att.setTmpSavePath(tmpFilePath);
			}

			Work fake = new Work();
			fake.setReportFiles(WorkReportFileManager.getLVReportSetByItemId(item.getId()));
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
			Work.ReportFile att = (ReportFile) _workAttchStorage.loadWithLock(fileId);
			// 수정 가능여부에 대한 권한체크 처리
			Work item = (Work) att.getItem();
			ItemPermission.checkUser(item, mp);
			WorkPermission.checkUpdate(item);
			WorkPermission.doCancelCheckOut(att);
			WorkReportFileManager.doCancelCheckout(att);

			Work server = (Work) (att.getItem());
			Work fake = new Work();
			fake.setReportFiles(WorkReportFileManager.getLVReportSetByItemId(server.getId()));
			_factory.marshal(ctx.getWriter(), fake);
		}
	}

	/**
	 * 붙임 파일 체크아웃<br>
	 * 검토 승인자 용
	 */
	public static class ReveiwerCheckOut extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long fileId = ctx.getLong("fileId");
			String tmpFilePath = ctx.getParameter("tmpFilePath");
			Work.ReportFile att = (ReportFile) _workAttchStorage.loadWithLock(fileId);
			// 수정 가능여부에 대한 권한체크 처리
			Work item = (Work) att.getItem();
			ItemPermission.checkUser(item, mp);
			WorkPermission.checkReviewerApprover(item);
			WorkPermission.doCheckOut(att);
			WorkReportFileManager.doCheckOut(att);

			if (StringUtils.isNotEmpty(tmpFilePath))
			{
				att.setTmpSavePath(tmpFilePath);
			}

			_factory.marshal(ctx.getWriter(), att);
		}
	}

	/**
	 * 문서 체크아웃 취소<br>
	 * 검토 승인자 용
	 */
	public static class ReveiwerCacncelCheckOut extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long fileId = ctx.getLong("fileId");
			Work.ReportFile att = (ReportFile) _workAttchStorage.loadWithLock(fileId);
			// 수정 가능여부에 대한 권한체크 처리
			Work item = (Work) att.getItem();
			ItemPermission.checkUser(item, mp);
			WorkPermission.checkReviewerApprover(item);
			WorkPermission.doCancelCheckOut(att);
			WorkReportFileManager.doCancelCheckout(att);

			Work server = (Work) (att.getItem());
			Work fake = new Work();
			fake.setReportFiles(WorkReportFileManager.getLVReportSetByItemId(server.getId()));
			_factory.marshal(ctx.getWriter(), fake);
		}
	}

	/**
	 * 첨부파일 수정
	 * <p>
	 * 문서조회시 파일 추가 삭제, 개별파일 버전업 시에 사용한다.
	 */
	public static class DoFileUpdateByReviewer extends WorkAction
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
			WorkPermission.checkReviewerApprover(server);

			for (ReportFile clientAtt : client.getReportFiles())
			{
				if ("edit".equals(method))
				{
					// 기존 file 정보 로드.
					ReportFile serverAtt = (ReportFile) _workAttchStorage.load(clientAtt.getId());

					// 기존 file을 담을 새로운 객체 생성.
					ReportFile oldAtt = new ReportFile();

					// serverAtt -> old 로 데이터 복사하여, 기존데이터를 저장.
					WorkReportFileManager.fileUpdate(serverAtt, oldAtt);
					// clientAtt -> serverAtt로 데이터 복사, 새로운 데이터를 기존 row에 저장하되
					// 버전업과 최신버전임을 명시한다.
					WorkReportFileManager.fileVersionUpUpdate(clientAtt, serverAtt);

					oldAtt.setItem(server);

					WorkPermission.fileSizeCheck(ctx.getModuleParam(), server, serverAtt);
					_listener.AddAtt(server, serverAtt);
					DbService.save(oldAtt);

					_reportListener.fileVersionUp(serverAtt, oldAtt, mp);

					WorkHistory.workReportVersionUp(server);
					WorkItemHistoryManager.history(WorkItemHistory.REPORT_VERSIONUP, server, serverAtt);
					DbService.commit();
				}

				Work fake = new Work();
				fake.setReportFiles(WorkReportFileManager.getLVReportSetByItemId(server.getId()));
				_factory.marshal(ctx.getWriter(), fake);
			}

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

			// 수정 가능여부에 대한 권한체크 처리 (보고서 수정 액션을 사용자와 관리자가 함께 사용하고 있으므로 분기 처리)
			if (ItemPermission.isAppAdmin(server, mp))
			{
				WorkPermission.checkUpdateByAdmin(server);
			}
			else
			{
				ItemPermission.checkUser(server, mp);
				WorkPermission.checkUpdate(server);
			}

			for (ReportFile clientAtt : client.getReportFiles())
			{
				if ("edit".equals(method))
				{
					// 기존 file 정보 로드.
					ReportFile serverAtt = (ReportFile) _workAttchStorage.load(clientAtt.getId());

					// 기존 file을 담을 새로운 객체 생성.
					ReportFile oldAtt = new ReportFile();

					// serverAtt -> old 로 데이터 복사하여, 기존데이터를 저장.
					WorkReportFileManager.fileUpdate(serverAtt, oldAtt);
					// clientAtt -> serverAtt로 데이터 복사, 새로운 데이터를 기존 row에 저장하되
					// 버전업과 최신버전임을 명시한다.
					WorkReportFileManager.fileVersionUpUpdate(clientAtt, serverAtt);

					oldAtt.setItem(server);

					WorkPermission.fileSizeCheck(ctx.getModuleParam(), server, serverAtt);
					_listener.AddAtt(server, serverAtt);
					DbService.save(oldAtt);

					_reportListener.fileVersionUp(serverAtt, oldAtt, mp);

					WorkHistory.workReportVersionUp(server);
					WorkItemHistoryManager.history(WorkItemHistory.REPORT_VERSIONUP, server, serverAtt);
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
					_reportFile.update(clientAtt);

					WorkPermission.fileSizeCheck(ctx.getModuleParam(), server, clientAtt);
					_listener.AddAtt(server, clientAtt);

					WorkHistory.workReportAdd(server);
					WorkItemHistoryManager.history(WorkItemHistory.REPORT_ADD, server, clientAtt);
					DbService.commit();
				}
				else if ("delete".equals(method))
				{
					// 기존 file 정보 로드.
					ReportFile serverAtt = (ReportFile) _workAttchStorage.loadWithLock(clientAtt.getId());
					Long gid = null;
					// 지워지는 버전이 가장 최근 버전이면
					if (serverAtt.isLastVersion())
					{
						gid = serverAtt.getGid();
					}
					server.setLastUpdt(new Date());
					serverAtt.setMethod("delete");
					_listener.deleteAtt(server, serverAtt);
					_reportFile.update(serverAtt);

					WorkHistory.reportDelete(server);
					WorkItemHistoryManager.history(WorkItemHistory.REPORT_DELETE, server, serverAtt);
					DbService.commit();

					if (gid != null)
					{
						DbService.begin();
						Long lastFileId = WorkReportFileManager.getLVAttachmentByFileGId(gid);
						if (lastFileId != null)
						{
							ReportFile last = (ReportFile) DbService.loadWithLock(ReportFile.class, lastFileId);
							last.setLastVersion(true);
							DbService.save(last);
							_reportListener.lastVersionDeleted(last, mp);
						}
					}
				}

				Work fake = new Work();
				fake.setReportFiles(WorkReportFileManager.getLVReportSetByItemId(server.getId()));
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

			Work.ReportFile att = (ReportFile) _workAttchStorage.loadWithLock(fileId);
			// 수정 가능여부에 대한 권한체크 처리
			Work server = (Work) att.getItem();
			WorkPermission.checkAttachUser(server, mp);

			SqlSelect gidSel = new SqlSelect();
			gidSel.select("file_gid");
			gidSel.from("work_item_report");
			gidSel.where("fileid = ?", fileId);

			Long gid = null;
			ResultSet gRs = gidSel.query();
			if (gRs.next())
			{
				gid = gRs.getLong("file_gid");
			}
			SqlSelect sel = new SqlSelect();
			sel.select(WORKREPORTFILE, "list");
			sel.where("file_gid =  ?", gid);
			sel.order("vrsn_num desc");

			SqlWriter writer = new SqlWriter().putAll(WORKREPORTFILE);
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

	/**
	 * 관리자 권한으로 첨부파일을 다운로드 한다.
	 */
	public static class DownloadByAdmin extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();

			Work.ReportFile att = (Work.ReportFile) DbService.load(Work.ReportFile.class, ctx.getLong("id"));
			Work server = (Work) att.getItem();
			ItemPermission.checkAppAdmin(server, mp);

			ctx.store(att);
		}
	}
}