package com.kcube.work;

import java.io.OutputStream;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kcube.doc.ItemPermission;
import com.kcube.lib.event.EventService;
import com.kcube.lib.jdbc.DbService;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.upload.Upload;
import com.kcube.sys.usr.UserService;
import com.kcube.sys.webdav.WebDavFile;
import com.kcube.work.Work.ReportFile;
import com.kcube.work.history.WorkItemHistory;
import com.kcube.work.history.WorkItemHistoryManager;

/**
 * Work Report WebDav처리를 위한 Class
 */
public class WorkReportWebDavFile extends WebDavFile
{
	/**
	 * WebDav Library Log 경로가 톰캣 기본 경로로 잡혀있어 Log설정 추가
	 */
	private static Log _log = LogFactory.getLog(WorkReportWebDavFile.class);

	private Work.ReportFile _serverAtt;
	private Work _server;
	ModuleParam _mp;

	/**
	 * 업무방 listener
	 */
	public static WorkListener _listener = (WorkListener) EventService.getDispatcher(WorkListener.class);

	/**
	 * 업무방 보고서 listener
	 */
	public static WorkReportFileListener _reportListener = (WorkReportFileListener) EventService
		.getDispatcher(WorkReportFileListener.class);

	/**
	 * 기본 생성자 편집할 file과 item load
	 * @param module
	 * @param id
	 * @param parentPath
	 * @param fileName
	 * @throws Exception
	 */
	public WorkReportWebDavFile(String module, Long id, String parentPath, String fileName) throws Exception
	{
		super(module, id, parentPath, fileName);

		_serverAtt = (Work.ReportFile) DbService.load(Work.ReportFile.class, id);
		_server = (Work) _serverAtt.getItem();

		_mp = new ModuleParam(
			_server.getClassId(), _server.getModuleId(), _server.getSpaceId(), null, _server.getAppId());
	}

	/**
	 * 실제 DB에 있는 파일명을 넣는다.
	 */
	@Override
	public String getName(Long id) throws Exception
	{
		if (_log.isDebugEnabled())
		{
			_log.debug("getName: " + _serverAtt.getFilename());
		}
		return _serverAtt.getFilename();
	}

	/**
	 * 파일의 생성 날짜를 돌려준다.
	 */
	@Override
	public long getCreated(Long id) throws Exception
	{
		Date rgstDate = _server.getRgstDate();
		if (_log.isDebugEnabled())
		{
			_log.debug("getCreated: " + rgstDate);
		}
		return rgstDate == null ? _server.getLastUpdt().getTime() : rgstDate.getTime();
	}

	/**
	 * 파일의 수정 날짜를 돌려준다.
	 */
	@Override
	public long getModified(Long id) throws Exception
	{
		if (_log.isDebugEnabled())
		{
			_log.debug("getModified: " + _server.getLastUpdt().getTime());
		}
		return _server.getLastUpdt().getTime();
	}

	/**
	 * 파일 사이즈를 넣는다.
	 */
	@Override
	public long getContentLength(Long id) throws Exception
	{
		if (_log.isDebugEnabled())
		{
			_log.debug("getContentLength: " + _serverAtt.getFilesize());
		}
		return _serverAtt.getFilesize();
	}

	/**
	 * 파일을 outputStream 에 write한다.
	 */
	@Override
	public void read(OutputStream os, long offset) throws Exception
	{
		if (_log.isDebugEnabled())
		{
			_log.debug("read: " + os.toString());
		}
		_serverAtt.write(os, offset);
	}

	/**
	 * 담당자 또는 협업자가 맞는지 검사한다.
	 */
	@Override
	public boolean isValidUser()
	{
		if (_log.isDebugEnabled())
		{
			_log.info("isValidUser getUserId[" + UserService.getUserId() + "]");
		}

		boolean isUser = ItemPermission.isUser(_server, _mp);

		if (_log.isDebugEnabled())
		{
			_log.info("isValidUser ItemPermission.isUser[" + isUser + "]");
		}

		if (isUser)
		{
			isUser = (!_server.isCurrentReviewer() && !_server.isCurrentApprover());
			if (_log.isDebugEnabled())
			{
				_log.info("isValidUser checkReviewerApprover[" + isUser + "]");
			}
		}
		return isUser;
	}

	/**
	 * lock 시 호출된다.
	 */
	@Override
	public boolean lock() throws Exception
	{

		if (_log.isDebugEnabled())
		{
			_log.debug("lock: " + _serverAtt.getFilename());
		}
		if (isValidUser())
		{
			ItemPermission.checkUser(_server, _mp);
			WorkPermission.checkUpdate(_server);
			WorkPermission.doCheckOut(_serverAtt);

			WorkReportFileManager.doCheckOut(_serverAtt);

			boolean lock = _serverAtt.isCheckOut();
			if (_log.isInfoEnabled())
			{
				_log.info("lock _serverAtt. lock: " + lock);
			}
			return _serverAtt.isCheckOut();
		}
		else
		{
			return false;
		}
	}

	/**
	 * unlock 시 호출된다.
	 */
	@Override
	public void unlock(Upload upload) throws Exception
	{

		if (upload == null)
		{
			/**
			 * checkout 해제
			 */
			if (_log.isInfoEnabled())
			{
				_log.info("unlock serverAtt Id[" + _serverAtt.getId() + "]");
			}
			WorkReportFileManager.doCancelCheckout(_serverAtt);

		}
		else
		{
			/**
			 * 버전업
			 */
			_server = (Work) DbService.loadWithLock(Work.class, _server.getId());

			// 기존 file 정보 로드.
			_serverAtt = (Work.ReportFile) DbService.loadWithLock(Work.ReportFile.class, _serverAtt.getId());
			_serverAtt.setMethod("edit");

			Work.ReportFile clientAtt = getClientAtt(upload);

			// 기존 file을 담을 새로운 객체 생성.
			ReportFile oldAtt = new ReportFile();
			oldAtt.setItem(_server);

			// serverAtt -> old 로 데이터 복사하여, 기존데이터를 저장.
			WorkReportFileManager.fileUpdate(_serverAtt, oldAtt);

			// 버전업과 최신버전임을 명시한다.
			WorkReportFileManager.fileVersionUpUpdate(clientAtt, _serverAtt);
			_serverAtt.setItem(_server);

			_listener.AddAtt(_server, _serverAtt);

			DbService.save(oldAtt);
			_reportListener.fileVersionUp(_serverAtt, oldAtt, _mp);

			WorkHistory.workReportVersionUp(_server);
			WorkItemHistoryManager.history(WorkItemHistory.REPORT_VERSIONUP, _server, _serverAtt);
			DbService.commit();
		}
	}

	/**
	 * 버전업을 위한 Report Client 객체를 생성한다.
	 * @param upload
	 * @return
	 */
	private Work.ReportFile getClientAtt(Upload upload)
	{
		Work.ReportFile clientAtt = new Work.ReportFile();
		clientAtt.setMethod("edit");
		clientAtt.setRgstDate(new Date());
		clientAtt.setFileExt(_serverAtt.getFileExt());
		clientAtt.setId(_serverAtt.getId());
		clientAtt.setVrsn(_serverAtt.getVrsn());
		clientAtt.setFilesize(upload.getFilesize());
		clientAtt.setType(upload.getType());
		if (null != upload.getId())
		{
			clientAtt.setPath(upload.getId().toString());
		}
		else
		{
			clientAtt.setPath(upload.getPath());
		}
		clientAtt.setFilename(upload.getFilename());

		return clientAtt;
	}

}
