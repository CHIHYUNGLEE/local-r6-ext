package com.kcube.work;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.hibernate.query.Query;

import com.kcube.doc.file.Attachment;
import com.kcube.doc.file.AttachmentManager;
import com.kcube.lib.jdbc.DbService;
import com.kcube.lib.repo.Movie;
import com.kcube.sys.upload.Upload;
import com.kcube.sys.usr.UserService;
import com.kcube.work.Work.ReportFile;

/**
 * 업무방 보고서 database 및 repository에 대한 처리를 담당한다.
 * @author shin8901
 */
public class WorkReportFileManager extends AttachmentManager
{
	/**
	 * 첨부파일을 repository에 저장하거나 삭제하고,
	 * <p>
	 * 삭제된 첨부파일은 포함되지 않은 새로운 Set을 돌려준다.
	 * @param attachments
	 * @param item
	 * @return
	 * @throws Exception
	 */
	public Set<ReportFile> updateFiles(Set<? extends Attachment> attachments, Object item) throws Exception
	{
		Set<ReportFile> newAttachments = new TreeSet<ReportFile>();
		if (attachments != null)
		{
			for (Attachment attTmp : attachments)
			{
				ReportFile att = (ReportFile) attTmp;
				att.setItem(item); // DbService.flush할 때에 값이 필요함
				att.setLastVersion(true);
				update(att);
				if (!att.isDelete())
				{
					newAttachments.add(att);
				}
			}
		}
		return newAttachments;
	}

	/**
	 * 파일을 업데이트
	 * @param client 업데이트 기준
	 * @param server 업데이트 대상
	 */
	public static void fileUpdate(ReportFile client, ReportFile server)
	{
		server.setFileExt(client.getFileExt());
		server.setFilename(client.getFilename());
		server.setFilesize(client.getFilesize());
		server.setRgst(client.getRgst());
		server.setRgstDate(client.getRgstDate());
		server.setType(client.getType());

		server.setDnldCnt(client.getDnldCnt());
		server.setPath(client.getPath());
		server.setGid(client.getGid());
		server.setMethod(client.getMethod());
		server.setVrsn(client.getVrsn());
		server.setLastVersion(false);
		doCancelCheckout(server);
	}

	/**
	 * 파일을 버전업하며 업데이트
	 * @param client 업데이트 기준
	 * @param server 업데이트 대상
	 * @throws Exception
	 * @throws NumberFormatException
	 */
	public static void fileVersionUpUpdate(ReportFile client, ReportFile server) throws NumberFormatException, Exception
	{
		server.setRgst(UserService.getUser());
		server.setRgstDate(new Date());
		server.setDnldCnt(client.getDnldCnt());

		/**
		 * 기존 객체가 가지고 있던 버전+1 한다.
		 */
		server.setVrsn(server.getVrsn() + 1);
		server.setLastVersion(true);

		Upload upload = (Upload) DbService.load(Upload.class, new Long(client.getPath()));
		server.setFilename(upload.getFilename());
		server.setFilesize(upload.getFilesize());
		server.setPath(upload.getPath());
		server.setMethod(upload.getMethod());
		DbService.remove(upload);

		// 파일 이름에서 가져옴.
		// 파일이름이 셋된 이후에 가져와야함.
		server.setFileExt(server.getExtension());
		doCancelCheckout(server);
	}

	/**
	 * 첨부파일의 method에 따라 repository와 database에 저장하고 삭제한다.
	 * @param att
	 * @throws Exception
	 */
	public void update(ReportFile att) throws Exception
	{
		handleXA(att);
		handleDB(att);
	}

	/**
	 * method에 따라 repository에 저장/삭제한다.
	 * @param att
	 * @throws Exception
	 */
	private void handleXA(ReportFile att) throws Exception
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
	 * method에 따라 database에 저장/삭제한다.
	 * @param att
	 * @throws Exception
	 */
	private void handleDB(ReportFile att) throws Exception
	{
		if (att.isDelete())
		{
			DbService.remove(att);
		}
		else if ((att.isEditor()) || (att.isMovie()))
		{
			updateImpliedFile(att);
		}
		else
		{
			if ((!(att.isMove())) && (!(att.isCopy())) && (!(att.isLink())) && (!(att.isMime())))
				return;
			DbService.save(att);
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
			att.setMethod(upload.getMethod());
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
	 * 업무방에 최신 버전 리스트를 반환한다.
	 * @param itemId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({"unchecked", "deprecation"})
	public static Set<ReportFile> getLVReportSetByItemId(Long itemId) throws Exception
	{
		Query<ReportFile> query = DbService.getSession().getNamedQuery("getLastVersionReportByItemId");
		query.setParameter("itemId", itemId);
		Iterator<ReportFile> i = query.iterate();
		Set<ReportFile> set = new TreeSet<Work.ReportFile>();
		while (i.hasNext())
		{
			set.add(i.next());
		}
		return set;
	}

	/**
	 * 업무방에 최신 버전 보고서 하나를 반환한다.
	 * @param gid
	 * @return
	 * @throws Exception
	 */
	public static Long getLVAttachmentByFileGId(Long gid) throws Exception
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("SELECT fileid ");
		buffer.append("FROM work_item_report ");
		buffer.append("WHERE vrsn_num = ( ");
		buffer.append("SELECT MAX(vrsn_num)  ");
		buffer.append("FROM work_item_report ");
		buffer.append("WHERE file_gid = ? ");
		buffer.append("GROUP BY file_gid ");
		buffer.append(" ) ");
		buffer.append("AND file_gid = ? ");
		PreparedStatement pstmt = DbService.prepareStatement(buffer.toString());
		pstmt.setLong(1, gid);
		pstmt.setLong(2, gid);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next())
		{
			return rs.getLong("fileid");
		}
		return null;
	}

	/**
	 * 보고서 체크아웃 처리
	 * @param att
	 */
	public static void doCheckOut(Work.ReportFile att)
	{
		att.setChecker(UserService.getUser());
		att.setCheckOut(true);
	}

	/**
	 * 보고서 체크아웃 취소 처리
	 * @param att
	 */
	public static void doCancelCheckout(Work.ReportFile att)
	{
		att.setTmpSavePath(null);
		att.setChecker(null);
		att.setCheckOut(false);
	}
}