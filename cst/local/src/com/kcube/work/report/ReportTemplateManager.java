package com.kcube.work.report;

import java.util.Date;

import com.kcube.doc.file.AttachmentManager;
import com.kcube.lib.jdbc.DbService;
import com.kcube.sys.upload.Upload;
import com.kcube.sys.usr.UserService;

/**
 * 업무방 템플릿 관리 Manager
 */
public class ReportTemplateManager
{
	private static AttachmentManager _attachment = new AttachmentManager();

	/**
	 * client의 값으로 server의 값을 update한다.
	 */
	static void update(ReportTemplate server, ReportTemplate client) throws Exception
	{
		server.setLastUpdtAuthor(UserService.getUser());
		server.setTitle(client.getTitle());
		server.setLastUpdt(new Date());
		server.setModuleId(client.getModuleId());
		server.setAppId(client.getAppId());
		server.setClassId(client.getClassId());
		server.setSpaceId(client.getSpaceId());

		/**
		 * 업무보고서 템플릿 수정시 첨부파일 제거 시 path경로에 파일 제거 및 Attachment값 null 입력
		 */
		if (server.getAttachment() != null && client.getAttachment().getType() == null)
		{
			_attachment.deleteXA(server.getAttachment());
			server.setAttachment(null);
		}
		else if (server.getAttachment() == null && client.getAttachment() != null)
		{
			Long fileId = new Long(client.getAttachment().getPath());

			Upload upload = (Upload) DbService.load(Upload.class, fileId);
			client.getAttachment().setPath(upload.getPath());
			client.getAttachment().setFilesize(upload.getFilesize());
			server.setAttachment(client.getAttachment());
			DbService.remove(upload);
		}
		else if (!client.getAttachment().getType().equals(server.getAttachment().getType())
			&& !client.getAttachment().getPath().equals(server.getAttachment().getPath()))
		{
			Long fileId = new Long(client.getAttachment().getPath());

			if (server.getAttachment() != null)
			{
				_attachment.deleteXA(server.getAttachment());
			}

			Upload upload = (Upload) DbService.load(Upload.class, fileId);
			client.getAttachment().setPath(upload.getPath());
			client.getAttachment().setFilesize(upload.getFilesize());
			server.setAttachment(client.getAttachment());
			DbService.remove(upload);
		}
		else if (server.getAttachment() != null && client.getAttachment() != null)
		{
			try
			{
				Long fileId = new Long(client.getAttachment().getPath());
				Upload upload = (Upload) DbService.load(Upload.class, fileId);
				client.getAttachment().setPath(upload.getPath());
				client.getAttachment().setFilesize(upload.getFilesize());
				server.setAttachment(client.getAttachment());
			}
			catch (Exception e)
			{
			}

		}
	}

	/**
	 * 문서를 등록상태로 한다.
	 */
	static void register(ReportTemplate server) throws Exception
	{
		server.setRgstUser(UserService.getUser());
		server.setStatus(ReportTemplate.REGISTERED_STATUS);
		server.setRgstDate(new Date());
	}

	/**
	 * 등록된 문서를 삭제한다.
	 */
	static void delete(ReportTemplate server) throws Exception
	{
		server.setStatus(ReportTemplate.DELETED_STATUS);
		server.setLastUpdt(new Date());
	}

	/**
	 * 삭제된 문서를 복원한다.
	 */
	static void recover(ReportTemplate server) throws Exception
	{
		server.setStatus(ReportTemplate.REGISTERED_STATUS);
		server.setLastUpdt(new Date());
	}

	/**
	 * 등록된 문서를 폐기한다.
	 * <p>
	 * db에서 삭제하고 첨부파일도 삭제한다. 복원할 수 없다.
	 */
	static void remove(ReportTemplate server) throws Exception
	{
		ReportTemplate.ReportTemplateAttachment attachment = server.getAttachment();
		if (null != attachment && attachment.getType() > 0)
		{
			_attachment.deleteXA(server.getAttachment());
		}
		DbService.remove(server);
	}
}
