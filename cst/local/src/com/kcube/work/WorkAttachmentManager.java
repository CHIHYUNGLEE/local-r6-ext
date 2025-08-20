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
import com.kcube.work.Work.WorkAttachment;

public class WorkAttachmentManager extends AttachmentManager
{

	public Set<WorkAttachment> updateWork(Set<? extends Attachment> attachments, Object item) throws Exception
	{
		Set<WorkAttachment> newAttachments = new TreeSet<WorkAttachment>();
		if (attachments != null)
		{
			for (Attachment attTmp : attachments)
			{
				WorkAttachment att = (WorkAttachment) attTmp;
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
	public static void fileUpdate(WorkAttachment client, WorkAttachment server)
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
	public static void fileVersionUpUpdate(WorkAttachment client, WorkAttachment server)
		throws NumberFormatException,
			Exception
	{
		server.setRgst(UserService.getUser());
		server.setRgstDate(new Date());
		server.setDnldCnt(client.getDnldCnt());
		server.setGid(client.getGid());

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

	public static void fileSave(WorkAttachment oldAtt) throws Exception
	{
		DbService.save(oldAtt);
	}

	/**
	 * 업무방에 최신 버전 리스트를 반환한다.
	 * @param itemId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({"unchecked", "deprecation"})
	public static Set<WorkAttachment> getLVAttachmentSetByItemId(Long itemId) throws Exception
	{
		Query<WorkAttachment> query = DbService.getSession().getNamedQuery("getLastVersionFileByItemId");
		query.setParameter("itemId", itemId);
		Iterator<WorkAttachment> i = query.iterate();
		Set<WorkAttachment> set = new TreeSet<Work.WorkAttachment>();
		while (i.hasNext())
		{
			set.add(i.next());
		}
		return set;
	}

	/**
	 * 업무방에 최신 버전 붙임파일 하나를 반환한다.
	 * @param gid
	 * @return
	 * @throws Exception
	 */
	public static Long getLVAttachmentByFileGId(Long gid) throws Exception
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("SELECT FILEID ");
		buffer.append("FROM WORK_ITEM_ATCH ");
		buffer.append("WHERE VRSN_NUM = ( ");
		buffer.append("SELECT MAX(VRSN_NUM)  ");
		buffer.append("FROM WORK_ITEM_ATCH ");
		buffer.append("WHERE FILE_GID = ? ");
		buffer.append("GROUP BY FILE_GID ");
		buffer.append(" ) ");
		buffer.append("AND FILE_GID = ? ");
		PreparedStatement pstmt = DbService.prepareStatement(buffer.toString());
		pstmt.setLong(1, gid);
		pstmt.setLong(2, gid);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next())
		{
			return rs.getLong("FILEID");
		}
		return null;
	}

	public void update(WorkAttachment att) throws Exception
	{
		handleXA(att);
		handleDB(att);
	}

	private void handleXA(WorkAttachment att) throws Exception
	{
		if ((att.isMove()) || (att.isDelete()))
		{
			deleteXA(att);
		}
		if ((!(att.isMove())) && (!(att.isCopy())))
			return;
		storeXA(att);
	}

	private void handleDB(WorkAttachment att) throws Exception
	{
		if (att.isDelete())
		{
			DbService.remove(att);
		}
		else if ((att.isEditor()) || (att.isMovie()))
		{
			if (att.isEditor())
			{
				Upload upload = (Upload) DbService.load(Upload.class, new Long(att.getPath()));

				att.setFilename(upload.getFilename());
				att.setFilesize(upload.getFilesize());
				att.setPath(upload.getPath());
				att.setMethod(upload.getMethod());
				att.setHashmd5(upload.getHashmd5());
				att.setFileExt(att.getExtension());

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
		else
		{
			if ((!(att.isMove())) && (!(att.isCopy())) && (!(att.isLink())) && (!(att.isMime())))
				return;
			DbService.save(att);
		}
	}

	public static void doCheckOut(Work.WorkAttachment att)
	{
		att.setChecker(UserService.getUser());
		att.setCheckOut(true);
	}

	public static void doCancelCheckout(Work.WorkAttachment att)
	{
		att.setChecker(null);
		att.setCheckOut(false);
	}
}
