package com.kcube.work.history;

import java.util.Date;
import java.util.List;

import com.kcube.doc.file.Attachment;
import com.kcube.lib.sql.SqlDate;
import com.kcube.lib.sql.SqlInsert;
import com.kcube.sys.usr.User;
import com.kcube.sys.usr.UserService;
import com.kcube.work.Work;
import com.kcube.work.WorkAbstractAttachment;
import com.kcube.work.WorkReference;

public class WorkItemHistoryManager
{
	/**
	 * 업무방 문서 이력을 남긴다.
	 */
	public static void history(String event, Work work) throws Exception
	{
		WorkItemHistory history = new WorkItemHistory();
		history.setTitle(work.getTitle());
		history.setItemId(work.getId());
		history.setClassId(work.getClassId());
		history.setModuleId(work.getModuleId());
		history.setAppId(work.getAppId());
		history.setSpaceId(work.getSpaceId());
		logHistory(event, history);
	}

	/**
	 * 업무방 문서 이력을 남긴다.
	 */
	public static void historyActorUser(String event, Work work, User user) throws Exception
	{
		WorkItemHistory history = new WorkItemHistory();
		history.setTitle(work.getTitle());
		history.setItemId(work.getId());
		history.setClassId(work.getClassId());
		history.setModuleId(work.getModuleId());
		history.setAppId(work.getAppId());
		history.setSpaceId(work.getSpaceId());
		history.setUserDisp(user.getDisplayName());
		history.setUserId(user.getUserId());
		history.setUserName(user.getName());
		logHistory(event, history);
	}

	/**
	 * 업무방 문서 이력을 남긴다.
	 * <p>
	 * 붙임파일,참고자료,보고서
	 * @param event
	 * @param work
	 * @param att
	 * @throws Exception
	 */
	public static void history(String event, Work work, Attachment att) throws Exception
	{
		WorkItemHistory history = new WorkItemHistory();
		history.setTitle(work.getTitle());
		history.setItemId(work.getId());

		history.setFileId(att.getId());
		if (event.contains("report") || event.contains("workAttachment"))
		{
			WorkAbstractAttachment file = (WorkAbstractAttachment) att;
			history.setFileGid(file.getGid());
			history.setFileName(file.getFilename());
			history.setFileVersionLabel(file.getVrsn().toString());
		}
		else
		{
			WorkReference rfrn = (WorkReference) att;
			if (rfrn.getRfrnCode() == WorkReference.FILETYPE)
			{
				history.setFileName(rfrn.getFilename());
			}
			else if (rfrn.getRfrnCode() == WorkReference.URLTYPE)
			{
				history.setFileName(rfrn.getTitle());
				history.setRefUrl(rfrn.getQuery());
			}
			else if (rfrn.getRfrnCode() == WorkReference.FVRTTYPE)
			{
				history.setFileName(rfrn.getTitle());
				history.setRefUrl(rfrn.getQuery());
			}
		}
		history.setClassId(work.getClassId());
		history.setModuleId(work.getModuleId());
		history.setAppId(work.getAppId());
		history.setSpaceId(work.getSpaceId());
		logHistory(event, history);
	}

	/**
	 * 업무방 문서 이력을 남긴다.
	 * <p>
	 * 관련 사용자 정보를 포함한다.
	 */
	public static void history(String event, Work work, User refUser) throws Exception
	{
		WorkItemHistory history = new WorkItemHistory();
		history.setTitle(work.getTitle());
		history.setItemId(work.getId());
		history.setRefUserId(refUser.getUserId());
		history.setRefUserDisp(refUser.getDisplayName());
		history.setRefUserName(refUser.getName());
		history.setClassId(work.getClassId());
		history.setModuleId(work.getModuleId());
		history.setAppId(work.getAppId());
		history.setSpaceId(work.getSpaceId());
		logHistory(event, history);
	}

	/**
	 * 업무방 문서 이력을 남긴다.
	 * <p>
	 * 관련 사용자들의 정보를 포함한다.
	 */
	public static void history(String event, Work work, List<User> refUsers) throws Exception
	{
		if (refUsers != null)
		{
			for (User refUser : refUsers)
			{
				WorkItemHistory history = new WorkItemHistory();
				history.setTitle(work.getTitle());
				history.setItemId(work.getId());
				history.setRefUserId(refUser.getUserId());
				history.setRefUserDisp(refUser.getDisplayName());
				history.setRefUserName(refUser.getName());
				history.setClassId(work.getClassId());
				history.setModuleId(work.getModuleId());
				history.setAppId(work.getAppId());
				history.setSpaceId(work.getSpaceId());
				logHistory(event, history);
			}
		}
	}

	/**
	 * 이력을 남긴다.
	 */
	protected static void logHistory(String event, WorkItemHistory history) throws Exception
	{
		if (UserService.isVirtual())
		{
			return;
		}
		history.setInstDate(new Date());
		history.setEvent(event);
		if (history.getUserId() == 0L)
		{
			history.setUserId(UserService.getUserId());
			history.setUserName(UserService.getUser().getName());
			history.setUserDisp(UserService.getUser().getDisplayName());
		}
		log(history);
	}

	/**
	 * 데이터베이스에 로그를 남긴다.
	 */
	private static void log(WorkItemHistory history) throws Exception
	{
		SqlInsert insert = new SqlInsert(WorkItemHistoryAction.HISTORY);

		// common
		insert.setSequenceNextVal("historyid", "sq_work_history");
		insert.setTimestamp("inst_date", SqlDate.getTimestamp(history.getInstDate()));
		insert.setString("event", history.getEvent());
		insert.setString("title", history.getTitle());
		insert.setLong("userid", history.getUserId());
		insert.setString("user_name", history.getUserName());
		insert.setString("user_disp", history.getUserDisp());

		// ref user
		insert.setLong("ref_userid", history.getRefUserId());
		insert.setString("ref_name", history.getRefUserName());
		insert.setString("ref_disp", history.getRefUserDisp());

		// ref object
		insert.setString("ref_url", history.getRefUrl());
		insert.setLong("fileid", history.getFileId());
		insert.setLong("file_gid", history.getFileGid());
		insert.setString("file_name", history.getFileName());

		// doc
		insert.setLong("itemid", history.getItemId());

		// moduleParam
		insert.setLong("classid", history.getClassId());
		insert.setLong("moduleid", history.getModuleId());
		insert.setLong("appid	", history.getAppId());
		insert.setLong("spid", history.getSpaceId());

		insert.execute();
	}
}
