package com.kcube.work;

import java.util.Set;
import java.util.TreeSet;

import com.kcube.doc.file.Attachment;
import com.kcube.doc.file.AttachmentManager;
import com.kcube.lib.event.EventService;
import com.kcube.lib.jdbc.DbService;
import com.kcube.lib.repo.Movie;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.upload.Upload;
import com.kcube.sys.usr.UserService;
import com.kcube.work.history.WorkItemHistory;
import com.kcube.work.history.WorkItemHistoryManager;

/**
 * 업무방 참고자료 Manager Class
 * @author shin8901
 */
public class WorkRerferenceManager extends AttachmentManager
{
	/**
	 * 업무방 listener
	 */
	public static WorkListener _listener = (WorkListener) EventService.getDispatcher(WorkListener.class);

	/**
	 * ModuleParam
	 */
	private ModuleParam mp;

	/**
	 * moduleParam 설정한다.
	 * @param moduleParam
	 */
	public void setMp(ModuleParam moduleParam)
	{
		this.mp = moduleParam;
	}

	/**
	 * moduleParam 돌려준다.
	 * @return
	 */
	public ModuleParam getMp()
	{
		return this.mp;
	}

	/**
	 * 참고자료 목록에서 추가된 참고자료와 삭제된 참고자료를 db에 반영한다.
	 */
	public Set<WorkReference> updateReference(Set<? extends WorkReference> set, Object server) throws Exception
	{
		Set<WorkReference> newReferences = new TreeSet<WorkReference>();
		if (set != null)
		{
			Work a = (Work) server;
			setMp(new ModuleParam(a.getClassId(), a.getModuleId(), a.getSpaceId(), null, a.getAppId()));
			for (WorkReference rfrn : set)
			{
				setCommonValue(rfrn, server);
				update(rfrn);
				if (!rfrn.isDelete())
				{
					newReferences.add(rfrn);
				}
			}
		}
		return newReferences;
	}

	/**
	 * 공통으로 set 되는 부분을 모아놓은 method
	 * @param rfrn
	 * @param server
	 * @throws Exception
	 */
	public void setCommonValue(WorkReference rfrn, Object server) throws Exception
	{
		rfrn.setRgstUserId(UserService.getUserId());
		rfrn.setItem(server);
	}

	/**
	 * 참고자료 타입에 따라 처리로직을 달리한다.
	 */
	public void update(WorkReference rfrn) throws Exception
	{
		if (rfrn.getRfrnCode() == WorkReference.FILETYPE)
		{
			updateAtt(rfrn);
		}
		else if (rfrn.getRfrnCode() == WorkReference.URLTYPE)
		{
			updateURL(rfrn);
		}
		else if (rfrn.getRfrnCode() == WorkReference.FVRTTYPE)
		{
			updateFvrt(rfrn);
		}
	}

	/**
	 * 참고자료의 타입이 북마크 데이터 일 경우
	 * @param rfrn
	 * @throws Exception
	 */
	private void updateFvrt(WorkReference rfrn) throws Exception
	{
		if (rfrn.isEditor())
		{
			DbService.save(rfrn);
		}
		else if (rfrn.isDelete())
		{
			DbService.remove(rfrn);
		}
	}

	/**
	 * 참고자료의 타입이 첨부파일일 경우 <br>
	 * 첨부파일의 method에 따라 repository와 database에 저장하고 삭제한다.
	 * @author 신운재
	 * @param att
	 * @throws Exception
	 */
	public void updateAtt(WorkReference att) throws Exception
	{
		handleXA(att);
		handleDB(att);
	}

	/**
	 * 참고자료의 첨부를 method에 따라서 저장한다
	 * @param att
	 * @throws Exception
	 */
	private void handleXA(WorkReference att) throws Exception
	{
		if ((att.isMove()) || (att.isDelete()))
		{
			deleteXA(att);
		}
		if ((!(att.isMove())) && (!(att.isCopy())))
			return;
		storeXA(att);
	}

	/**
	 * 참고자료를 db에 저장한다.
	 * @param att
	 * @throws Exception
	 */
	private void handleDB(WorkReference att) throws Exception
	{
		Work server = (Work) att.getItem();
		if (att.isDelete())
		{
			DbService.remove(att);
			_listener.deleteAtt(server, att);
		}
		else
		{
			WorkPermission.fileSizeCheck(getMp(), server, att);
			if ((att.isEditor()) || (att.isMovie()))
			{
				updateImpliedFile(att);
				_listener.AddAtt(server, att);
			}
			else
			{
				if ((!(att.isMove())) && (!(att.isCopy())) && (!(att.isLink())) && (!(att.isMime())))
					return;
				DbService.save(att);
				_listener.AddAtt(server, att);
			}
		}
	}

	/**
	 * 업로드된파일과 동영상 파일 정보를 업데이트 한다.
	 */
	private void updateImpliedFile(Attachment att) throws Exception
	{
		if (att.isEditor())
		{
			Upload upload = (Upload) DbService.load(Upload.class, new Long(att.getPath()));

			att.setFilename(upload.getFilename());
			att.setFilesize(upload.getFilesize());
			att.setPath(upload.getPath());
			att.setHashmd5(upload.getHashmd5());

			DbService.remove(upload);
		}
		else if (att.isMovie())
		{
			Movie movie = (Movie) DbService.load(Movie.class, att.getPath());
			movie.setLinked(true);
			att.setFilename(movie.getMovieFile().getFilename());
			att.setFilesize(movie.getMovieFile().getFilesize());
		}
		DbService.save(att);
	}

	/**
	 * method 타입에 따라 URL 타입을 저장하고, 지운다.
	 * @param att
	 * @throws Exception
	 */
	public void updateURL(WorkReference att) throws Exception
	{
		if (att.isEditor())
		{
			DbService.save(att);
		}
		else if (att.isDelete())
		{
			DbService.remove(att);
		}
	}

	/**
	 * 객체의 메소드에따라 history를 남긴다.
	 * @param clientAtt
	 * @throws Exception
	 */
	public void history(WorkReference clientAtt) throws Exception
	{
		Work server = (Work) clientAtt.getItem();

		if (clientAtt.isEditor())
		{
			WorkHistory.referenceAdd(server, clientAtt);
			if (clientAtt.getRfrnCode() == WorkReference.FILETYPE)
			{
				WorkItemHistoryManager.history(WorkItemHistory.REFERENCE_FILE_ADD, server, clientAtt);
			}
			else if (clientAtt.getRfrnCode() == WorkReference.URLTYPE)
			{
				WorkItemHistoryManager.history(WorkItemHistory.REFERENCE_URL_ADD, server, clientAtt);
			}
		}
		else if (clientAtt.isDelete())
		{
			WorkHistory.referenceDeleted(server, clientAtt);
			WorkItemHistoryManager.history(WorkItemHistory.REFERENCE_DELETE, server, clientAtt);
		}
	}
}