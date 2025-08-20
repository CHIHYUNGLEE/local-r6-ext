package com.kcube.work;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import com.kcube.doc.expr.ExpireListener;
import com.kcube.doc.expr.ExpireService;
import com.kcube.doc.file.Attachment;
import com.kcube.lib.event.EventService;
import com.kcube.lib.jdbc.DbService;
import com.kcube.lib.sql.SqlDate;
import com.kcube.lib.sql.SqlDialect;
import com.kcube.lib.sql.SqlInsert;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.lib.sql.SqlUpdate;
import com.kcube.map.Folder;
import com.kcube.map.FolderContent;
import com.kcube.map.FolderListener;
import com.kcube.map.FolderMove;
import com.kcube.stat.act.HistStatListener;
import com.kcube.stat.tag.TagJob;
import com.kcube.stat.tag.TagStatListener;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.UserService;
import com.kcube.work.Work.ReportFile;
import com.kcube.work.process.WorkProcess;

public class WorkEvent
	implements TagStatListener, ExpireListener, WorkListener, HistStatListener, WorkReportFileListener, FolderListener,
	FolderContent
{
	/**
	 * 삭제예정 업무방 보고서에 대한 문서변환 모듈명 private static String DOC_MODULE_NAME = "WORK_REPORT";
	 */

	/**
	 * 업무방 listener
	 */
	static WorkListener _listener = (WorkListener) EventService.getDispatcher(WorkListener.class);
	private static FolderMove _folder = new FolderMove(Work.class, true);

	private static final String INSERT_STAT_MAP = "INSERT INTO st_act_map (tenantid, moduleid, appid, kmid, level1, level2, level3, level4, inst_date, cmd_code, cmd_cnt, file_size) "
		+ "SELECT l.tenantid ,l.moduleid, l.appid, m.kmid, m.level1, m.level2, m.level3, m.level4, "
		+ SqlDialect.truncDate("l.inst_date")
		+ " inst_date, l.cmd_code, count(*) cmd_cnt, sum(l.file_size) file_size "
		+ "FROM log_stat_act l, work_item_map m, work_item k "
		+ "WHERE m.itemid = k.itemid AND l.moduleid = k.moduleid AND l.itemid = m.itemid AND l.tenantid = ? AND m.kmid IS NOT NULL "
		+ "AND l.stat_flag = 1 "
		+ "GROUP BY m.kmid, m.level1, m.level2, m.level3, m.level4, "
		+ SqlDialect.truncDate("l.inst_date")
		+ ", l.cmd_code,l.tenantid, l.moduleid, l.appid ";

	/**
	 * 업무함 태그를 메뉴별로 처리한다.
	 */
	public void mergeTag() throws Exception
	{
		String tag = SqlDialect.upper("t.tag");
		SqlSelect stmt = new SqlSelect();
		stmt.select("b.classid, b.moduleid, b.appid, b.spid, b.itemid, b.auth_userid userid, b.auth_name user_name ");
		stmt.select(tag + " tag");
		stmt.select("t.item_date inst_date");
		stmt.select("b.tenantid");
		stmt.from("work_item_tag t, work_item b");
		stmt.where("b.itemid = t.itemid");
		stmt.where("t.isvisb = ?", true);
		stmt.where("b.isvisb = ?", true);

		SqlInsert ins = new SqlInsert(TagJob.TAG_MERGE);
		ins.setValue("classid, moduleid, appid, spid, itemid, userid, user_name, tag, inst_date, tenantid", stmt);
		ins.execute();
	}

	@Override
	public void tagStat() throws Exception
	{

	}

	@Override
	public void expire() throws Exception
	{
		Work item = new Work(); // item 객체를 재사용한다.
		ResultSet rs = getExpired();
		while (rs.next())
		{
			DbService.begin();
			DbService.reload(item, new Long(rs.getLong(1)));

			WorkManager.expire(item);
			WorkHistory.expired(item);
			_listener.deleted(item);
			DbService.commit();
		}
	}

	/**
	 * 만료될 업무방을 돌려준다.
	 * @return
	 * @throws Exception
	 */
	private ResultSet getExpired() throws Exception
	{
		PreparedStatement pstmt = DbService.prepareStatement(
			"SELECT itemid FROM work_item WHERE expr_date < ? AND status in (? , ?)" + " ORDER BY itemid");
		pstmt.setDate(1, SqlDate.getDate());
		pstmt.setInt(2, Work.COMPLETE_STATUS);
		pstmt.setInt(3, Work.APRV_CMPLT_STATUS);
		return pstmt.executeQuery();
	}

	@Override
	public void registered(Work server) throws Exception
	{
		/**
		 * 첨부용량을 재계산하여 반영한다.
		 */
		server.setTotalFileSize(server.getManualTotalFileSize());
	}

	@Override
	public void deleted(Work server) throws Exception
	{
		for (WorkProcess prcs : server.getWorkProcess())
		{
			if (prcs.getCmpltDate() != null)
			{
				prcs.setRetrieve(true);
			}

			/**
			 * 회수이력 남김.
			 */
			WorkProcess retrieve = new WorkProcess();
			retrieve.setChecker(UserService.getUser());
			retrieve.setEventDate(new Date());
			retrieve.setCmpltDate(new Date());
			retrieve.setProcessType(WorkProcess.RETRIEVE_TYPE);
			retrieve.setStatus(WorkProcess.RETRIEVE_STATUS);
			retrieve.setItem(server);
			retrieve.setAppId(server.getAppId());
			retrieve.setClassId(server.getClassId());
			retrieve.setModuleId(server.getModuleId());
			retrieve.setSpaceId(server.getSpaceId());
			DbService.save(retrieve);
		}

	}

	@Override
	public void changeMember(Work item) throws Exception
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void recover(Work item) throws Exception
	{
		// TODO Auto-generated method stub

	}

	@Override
	/**
	 * @author WoonJae
	 *         <p>
	 *         만료 월 단위를 사용자정의로 지정하지 않은 경우, <br>
	 *         완료일을 기준으로 만료일시를 계산하여 저장.
	 *         <p>
	 *         만료일시를 사용자정의로 정한경우에는 이를 무시하고, <br>
	 *         고정된 만료일시를 기준으로 업무방을 만료 시킨다.
	 */
	public void complete(Work server) throws Exception
	{
		if (server.getExprMonth() != -1)
		{
			server.setExprDate(ExpireService.getExprDate(server.getCompleteDate(), server.getExprMonth()));
		}
		for (ReportFile rf : server.getReportFiles())
		{
			rf.setChecker(null);
			rf.setCheckOut(false);
		}
	}

	@Override
	/**
	 * @author WoonJae
	 *         <p>
	 *         만료일시를 사용자정의로 정한 경우에는 <br>
	 *         작업 상태로 돌아오더라도 고정된 만료일을 저장함.
	 */
	public void working(Work server) throws Exception
	{
		if (server.getExprMonth() != -1)
		{
			server.setExprDate(null);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.kcube.work.WorkListener#AddAtt(com.kcube.work.Work)
	 */
	@Override
	public void AddAtt(Work server, Attachment newFile)
	{
		/**
		 * 첨부용량을 재계산하여 반영한다.
		 */
		server.setTotalFileSize(server.getTotalFileSize() + newFile.getFilesize());
	}

	/*
	 * (non-Javadoc)
	 * @see com.kcube.work.WorkListener#deleteAtt(com.kcube.work.Work)
	 */
	@Override
	public void deleteAtt(Work server, Attachment delFile)
	{
		/**
		 * 첨부용량을 재계산하여 반영한다.
		 */
		server.setTotalFileSize(server.getTotalFileSize() - delFile.getFilesize());
	}

	/*
	 * (non-Javadoc)
	 * @see com.kcube.stat.act.HistStatListener#executeHistStat()
	 */
	@Override
	public void executeHistStat(Long tenantId) throws Exception
	{
		PreparedStatement statement = DbService.prepareStatement(INSERT_STAT_MAP);
		statement.setLong(1, tenantId);
		statement.executeUpdate();
	}

	/*
	 * (non-Javadoc)
	 * @see com.kcube.work.WorkReportFileListener#lastVersionDeleted(com.kcube.work.Work.
	 * ReportFile , com.kcube.sys.module.ModuleParam)
	 */
	@Override
	public void lastVersionDeleted(ReportFile file, ModuleParam mp) throws Exception
	{
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.kcube.work.WorkReportFileListener#fileVersionUp(com.kcube.work.Work.ReportFile,
	 * com.kcube.work.Work.ReportFile, com.kcube.sys.module.ModuleParam)
	 */
	@Override
	public void fileVersionUp(ReportFile newFile, ReportFile oldFile, ModuleParam mp) throws Exception
	{
		// 업무방 파일 관련 히스토리의 fileid 업데이트함.
		SqlUpdate updt = new SqlUpdate("work_history");
		updt.setLong("fileid", oldFile.getId());
		updt.where(SqlDialect.upper("event") + " like ?", SqlDialect.getSearchValue("report", true));
		updt.where("file_gid = ?", newFile.getGid());
		updt.where("fileid = ?", newFile.getId());
		updt.execute();
	}

	@Override
	public void added(Folder folder, Folder parent) throws Exception
	{
	}

	@Override
	public void updated(Folder folder) throws Exception
	{
		SqlUpdate updt = new SqlUpdate("work_item");
		updt.setString("charge_dprt_name", folder.getName());
		updt.where("charge_dprtid = ?", folder.getId());
		updt.execute();
	}

	@Override
	public void moved(Folder folder, Folder srcParent, Folder dstParent) throws Exception
	{
	}

	@Override
	public void swapped(Folder movedUp, Folder movedDown) throws Exception
	{
	}

	@Override
	public void removed(Folder folder, Folder parent) throws Exception
	{
	}

	@Override
	public void dragSort(Folder folder, Long[] ids, boolean isFirst) throws Exception
	{
		// TODO Auto-generated method stub

	}

	/**
	 * 선택한 폴더에 해당 게시글이 있는지 확인한다.
	 */
	public void checkContent(Folder folder, Folder parent) throws Exception
	{
		_folder.checkContent(folder.getId());
	}

	/**
	 * 폴더를 이동한다.
	 */
	public void contentMoved(Folder src, Folder dst) throws Exception
	{
		_folder.move(src.getId(), dst.getId());
		_folder.moveScrt(src.getId(), dst.getId());

		SqlUpdate updt = new SqlUpdate("work_item");
		updt.setString("charge_dprt_name", dst.getName());
		updt.setLong("charge_dprtid", dst.getId());
		updt.where("charge_dprtid = ?", src.getId());
		updt.execute();
	}

	@Override
	public void reset(Folder folder) throws Exception
	{
	}
}
