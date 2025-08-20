package com.kcube.work;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import com.kcube.doc.Item.Security;
import com.kcube.doc.expr.ExpireService;
import com.kcube.doc.file.AttachmentManager;
import com.kcube.lib.jdbc.DbService;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.map.Folder;
import com.kcube.map.FolderCache;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.User;
import com.kcube.sys.usr.UserService;
import com.kcube.sys.usr.UserState;
import com.kcube.sys.usr.UserXid;
import com.kcube.work.history.WorkItemHistory;
import com.kcube.work.history.WorkItemHistoryManager;
import com.kcube.work.process.WorkProcessManager;
import com.kcube.work.request.share.WorkShareRequest;
import com.kcube.work.request.share.WorkShareRequestSql;
import com.kcube.work.trans.WorkTransManager;
import com.kcube.work.trans.WorkTransfer;

/**
 * @author 김경수, 신운재
 *         <p>
 *         업무방 Manager
 */
public class WorkManager
{
	private static AttachmentManager _attachment = new AttachmentManager();
	private static WorkAttachmentManager _workAttachment = new WorkAttachmentManager();
	private static WorkReportFileManager _repFileManager = new WorkReportFileManager();
	private static WorkRerferenceManager _workReferenceManager = new WorkRerferenceManager();
	private static WorkProcessManager manager = new WorkProcessManager();

	/**
	 * client의 값으로 server의 값을 update한다.
	 */
	static void update(Work server, Work client) throws Exception
	{
		User author = client.getAuthor();
		if (author != null)
		{
			server.setAuthor(author);
		}
		else if (server.getAuthor() == null)
		{
			server.setAuthor(UserService.getUser());
		}
		updateChargeDprt(server, client);
		server.setActor(client.getActor());
		server.setContent(client.getContent());
		server.setTitle(client.getTitle());
		server.setLastUpdt(new Date());
		server.setModuleId(client.getModuleId());
		server.setAppId(client.getAppId());
		server.setClassId(client.getClassId());
		server.setSpaceId(client.getSpaceId());
		server.setScrtLevel(client.getScrtLevel());
		server.setSecurities(client.getSecurities());
		server.setExprMonth(client.getExprMonth());

		if (server.getExprMonth() == -1)
		{
			server.setExprDate(null);
		}
		else
		{
			server.setExprDate(ExpireService.getExprDate(server.getExprMonth()));
		}

		server.setTags(client.getTags());
		if (StringUtils.isNotEmpty(client.getItemsVisible()))
		{
			server.setItemsVisible(client.getItemsVisible());
		}
		server.updateVisible(server.isVisible());
		updateHelpers(server, client);
		// 첨부 & 본문 이미지 첨부
		server.updateAttachments(_attachment.update(client.getAttachments(), server));
		// 붙임파일
		server.updateWorkAttachments(_workAttachment.updateWork(client.getWorkAttachments(), server));
		// 보고서
		server.updateReportFile(_repFileManager.updateFiles(client.getReportFiles(), server));
		// 참고자료
		server.setWorkReferences(_workReferenceManager.updateReference(client.getWorkReferences(), server));

		// 검토,승인자,요약문
		server.setApprReviewSetting(client.getApprReviewSetting());
		addSecurity(server);
		updateFolders(server, client);
	}

	/**
	 * 문서를 등록=작업중로 한다.
	 */
	static void register(Work server) throws Exception
	{
		server.setStatus(Work.WORKING_STATUS);
		server.setCompleteDate(null);
		server.setLastUpdt(new Date());
		server.setRgstDate(new Date());
		server.updateVisible(true);
	}

	/**
	 * 문서를 작업중 상태로 한다.
	 */
	static void working(Work server) throws Exception
	{
		server.setStatus(Work.WORKING_STATUS);
		server.setCompleteDate(null);
		server.setLastUpdt(new Date());
		server.updateVisible(true);
	}

	/**
	 * 문서를 완료 상태로 한다.
	 */
	static void complete(Work server) throws Exception
	{
		server.setStatus(Work.COMPLETE_STATUS);
		server.setCompleteDate(new Date());
		server.setLastUpdt(new Date());
		server.updateVisible(true);
	}

	/**
	 * 업무방을 삭제한다.
	 */
	static void delete(Work server) throws Exception
	{
		server.setBeforeStatus(server.getStatus());
		server.setStatus(Work.DELETED_STATUS);
		server.setLastUpdt(new Date());
		server.updateVisible(false);
	}

	/**
	 * 삭제된 문서를 복원한다.
	 */
	static void recover(Work server) throws Exception
	{
		if (server.getStatus() == Work.COMPLETE_STATUS
			|| server.getStatus() == Work.APRV_CMPLT_STATUS
			|| server.getStatus() == Work.WORKING_STATUS)
		{
			server.setStatus(server.getBeforeStatus());
		}
		else
		{
			server.setStatus(Work.WORKING_STATUS);
		}
		server.setLastUpdt(new Date());
		server.updateVisible(true);
	}

	/**
	 * 등록된 문서를 폐기한다.
	 * <p>
	 * db에서 삭제하고 첨부파일도 삭제한다. 복원할 수 없다.
	 */
	static void remove(Work server) throws Exception
	{
		_attachment.remove(server.getAttachments());
		_workAttachment.remove(server.getWorkAttachments());
		_repFileManager.remove(server.getReportFiles());
		_workReferenceManager.remove(server.getWorkReferences());
		DbService.remove(server);
	}

	/**
	 * 문서를 기간 연장한다.
	 */
	static void extend(Work server) throws Exception
	{
		server.setStatus(Work.COMPLETE_STATUS);
		server.setCompleteDate(new Date());
		server.setLastUpdt(new Date());
		server.updateVisible(true);
	}

	/**
	 * 담당 부서를 update 한다.
	 * <p>
	 * @param server
	 * @param client
	 * @throws Exception
	 */
	static void updateChargeDprt(Work server, Work client) throws Exception
	{
		server.setChargeDprtId(client.getChargeDprtId());
		server.setChargeDprtName(client.getChargeDprtName());
		addChargeDprtFolder(server);
	}

	/**
	 * 협업자를 update 한다.
	 * <p>
	 * 담당자와 중복이 될 경우에는 협업자에서 제거하여 추가함
	 * <p>
	 * 기존협업자에서 변경이 있을경우 변경log를 남기고 notify 한다. (History에서 server객체와 client객체를 비교하기 위해, setValue는 History 호출후에 함)
	 */
	public static void updateHelpers(Work server, Work client) throws Exception
	{
		User actor = server.getActor();
		if (server.getScrtLevel() != Work.SECURE_ACTOR)
		{
			List<User> helpers = client.getHelpers();
			if (actor != null && helpers != null)
			{
				for (int i = 0; i < helpers.size(); i++)
				{
					User helper = helpers.get(i);
					if (actor.getUserId().equals(helper.getUserId()))
					{
						helpers.remove(i);
						break;
					}
				}
			}
			server.setHelpers(client.getHelpers());
			server.setIsHelper(null != client.getHelpers() ? (client.getHelpers().size() > 0 ? true : false) : false);
		}
		else
		{
			server.setHelpers(new ArrayList<User>());
			server.setIsHelper(false);
		}
		server.setLastUpdt(new Date());
	}

	/**
	 * 공유자를 update 한다.
	 * <p>
	 * 기존공유자에서 변경이 있을경우 변경log를 남기고 notify 한다. (History에서 server객체와 client객체를 비교하기 위해, setValue는 History 호출후에 함)
	 */
	public static void updateSharers(Work server, Work client) throws Exception
	{
		updateSharers(server, client.getSharers());
	}

	/**
	 * 공유자를 update 한다.
	 * <p>
	 * 기존공유자에서 변경이 있을경우 변경log를 남기고 notify 한다. (History에서 server객체와 client객체를 비교하기 위해, setValue는 History 호출후에 함) client
	 * 객체가 아니라 공유자 객체를 받는 경우
	 */
	public static void updateSharers(Work server, List<User> sharers) throws Exception
	{
		server.setSharers(sharers);
		server.setIsShare(null != sharers ? (sharers.size() > 0 ? true : false) : false);
		server.setLastUpdt(new Date());
	}

	/**
	 * folders 에서 자동 등록처리된(등록자부서 등) folder 를 찾아 돌려준다.
	 * <p>
	 * 화면에서 수정된 folders를 설정하고 기존 자동 등록처리된 folder를 동일하게 추가한다.
	 */
	public static void updateFolders(Work server, Work client)
	{
		List<Work.Folder> computedFolders = findComputedFolders(server.getFolders(), true);
		Work.Folder clientFolder = client.getFolder();
		if (clientFolder != null)
		{
			clientFolder.updateLevel();
			computedFolders.add(clientFolder);
			server.setFolders(computedFolders);
		}
	}

	/**
	 * 문서의 폴더 중에서 자동 등록 여부를 확인하여 원하는 folders를 돌려준다.
	 * <p>
	 * 중복을 허용하지 않는다.
	 */
	public static List<Work.Folder> findComputedFolders(List<Work.Folder> folders, boolean isComputed)
	{
		Set<Work.Folder> s = new TreeSet<Work.Folder>();
		if (folders != null)
		{
			for (Work.Folder f : folders)
			{
				if (isComputed == f.isComputed())
				{
					s.add(f);
				}
			}
		}
		return new LinkedList<Work.Folder>(s);
	}

	/**
	 * 문서의 담당부서를 folders에 추가한다.
	 */
	static void addChargeDprtFolder(Work server) throws Exception
	{
		removeComputedFolders(server);
		if (server.getChargeDprtId() != null)
		{
			Folder dprt = FolderCache.getFolder(server.getChargeDprtId());
			if (dprt != null)
			{
				Work.Folder f = new Work.Folder(dprt.getId());
				f.setComputed(true);
				server.addFolder(f);
			}
		}
	}

	/**
	 * 자동 등록된 folder를 제거한다.
	 * <p>
	 * 조직맵을 삭제한다.
	 */
	private static void removeComputedFolders(Work server)
	{
		Set<Work.Folder> s = new TreeSet<Work.Folder>();
		List<Work.Folder> l = server.getFolders();
		if (l != null)
		{
			for (Work.Folder f : l)
			{
				if (!f.isComputed())
				{
					s.add(f);
				}
			}
		}
		server.setFolders(new LinkedList<Work.Folder>(s));
	}

	/**
	 * 보안정보를 재설정한다.
	 */
	public static void addSecurity(Work server) throws Exception
	{
		if (server.getScrtLevel() > UserState.SECURE_WRITE)
		{
			/**
			 * 자동 등록된 보안정보를 삭제한다.
			 * <p>
			 * 담당자, 협업자, 공유자에 대한 보안 정보를 삭제한다.
			 */
			UserService.removeComputed(server.getSecurities());
			addOwnerInSecurity(server);
			addActorInSecurity(server);
			addHelpersInSecurity(server);
			addSharersInSecurity(server);
			addReviewersInSecurity(server);
			addApproverInSecurity(server);
		}
	}

	/**
	 * 업무방의 최초등록자를 보안설정에 추가한다.
	 * @param server
	 */
	private static void addOwnerInSecurity(Work server)
	{
		Security actorScrt = makeSecurity(server.getAuthor());
		server.addSecurity(actorScrt);
	}

	/**
	 * 업무방의 담당자를 보안설정에 추가한다.
	 * @param server
	 */
	private static void addActorInSecurity(Work server)
	{
		Security actorScrt = makeSecurity(server.getActor());
		server.addSecurity(actorScrt);
	}

	/**
	 * 업무방의 협업자들을 보안설정에 추가한다.
	 * @param server
	 */
	private static void addHelpersInSecurity(Work server)
	{
		List<User> helpers = server.getHelpers();
		if (helpers == null)
			return;
		for (User helper : helpers)
		{
			Security helperScrt = makeSecurity(helper);
			server.addSecurity(helperScrt);
		}
	}

	/**
	 * 업무방의 공유자들을 보안설정에 추가한다.
	 * @param server
	 */
	private static void addSharersInSecurity(Work server)
	{
		List<User> sharers = server.getSharers();
		for (User share : sharers)
		{
			Security helperScrt = makeSecurity(share);
			server.addSecurity(helperScrt);
		}
	}

	/**
	 * 업무방의 검토자들을 보안설정에 추가한다.
	 * @param server
	 */
	private static void addReviewersInSecurity(Work server)
	{
		List<User> reviewers = server.getReviewers();
		if (reviewers == null)
			return;
		for (User review : reviewers)
		{
			Security reviewerScrt = makeSecurity(review);
			server.addSecurity(reviewerScrt);
		}
	}

	/**
	 * 업무방의 승인자를 보안설정에 추가한다.
	 * @param server
	 */
	private static void addApproverInSecurity(Work server)
	{
		if (server.getApprover() == null)
			return;
		Security approverScrt = makeSecurity(server.getApprover());
		server.addSecurity(approverScrt);
	}

	/**
	 * 사용자 보안정보를 돌려준다.
	 * @param user
	 * @return
	 */
	private static Security makeSecurity(User user)
	{
		if (user == null)
		{
			return null;
		}

		Security sec = new Security();
		sec.setComputed(true);
		sec.setTitle(user.getName());
		sec.setXid(UserXid.makeUserXid(user.getUserId()));
		return sec;
	}

	/**
	 * 업무방을 만료처리한다. <br>
	 * 완료 / 승인완료를 기준으로 만료 시키지만<br>
	 * 사용자 정의로 만료일시를 정해 두었다면,<br>
	 * 만료가 될 수 있는 상황임.<br>
	 * 따라서 완료가 아닌 상태에서 만료가 되면, <br>
	 * 완료일시를 넣어준다.
	 * @param server
	 */
	public static void expire(Work server)
	{
		if (server.getCompleteDate() == null)
		{
			server.setCompleteDate(new Date());
		}
		server.setStatus(Work.EXPIRED_STATUS);
		server.setLastUpdt(new Date());
		server.setVisible(false);
	}

	public static void helperReset(Work server)
	{
		server.setHelpers(new ArrayList<User>());
		server.setIsHelper(false);
	}

	/**
	 * 프로세스 카운트 상태별 카운트 return
	 * @param status
	 * @param mp
	 * @return
	 * @throws Exception
	 */
	public static int getPrcsCount(int status, ModuleParam mp) throws Exception
	{
		SqlSelect stmt = manager.getProcessCountByChecker(UserService.getUser(), mp);
		if (status > 0)
		{
			stmt.where("i.status = ? ", status);
			stmt.where("i.isretrieved = ?", false);
		}
		return stmt.count();
	}

	/**
	 * 공유요청 카운트 return
	 * @param mp
	 * @return
	 * @throws Exception
	 */
	public static int getShareCount(ModuleParam mp) throws Exception
	{
		WorkShareRequestSql sql = new WorkShareRequestSql(mp, null, WorkShareRequest.STATUS_APPLIED);
		return sql.getReceiveList().count();
	}

	/**
	 * 인수인계 카운트 return
	 * @param mp
	 * @return
	 * @throws Exception
	 */
	public static int getTransferCount(ModuleParam mp) throws Exception
	{
		WorkTransManager manager = new WorkTransManager();
		SqlSelect stmt = manager.getResListByUser(mp, WorkTransfer.STATUS_APPLIED);
		return stmt.count();
	}

	/**
	 * 상태를 update 한다. 상태가 완료로 변경될 경우 완료log를 남기고 완료일시를 update한다.
	 * @param mp
	 * @param server
	 * @param status
	 * @throws Exception
	 */
	static void updateStatus(ModuleParam mp, Work server, int status) throws Exception
	{
		if (status == Work.COMPLETE_STATUS)
		{
			complete(server);
			WorkAction._listener.complete(server);
			WorkItemHistoryManager.history(WorkItemHistory.CHANGE_COMPLETE, server);
		}
		else if (status == Work.WORKING_STATUS)
		{
			working(server);
			WorkAction._listener.working(server);
			WorkItemHistoryManager.history(WorkItemHistory.CHANGE_WORKING, server);
		}
	}
}
