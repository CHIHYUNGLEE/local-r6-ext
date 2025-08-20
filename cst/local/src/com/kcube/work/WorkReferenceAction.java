package com.kcube.work;

import java.util.Set;

import com.kcube.doc.ItemPermission;
import com.kcube.lib.action.ActionContext;
import com.kcube.lib.jdbc.DbService;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.UserPermission;
import com.kcube.work.history.WorkItemHistory;
import com.kcube.work.history.WorkItemHistoryManager;

/**
 * @author 신운재
 *         <p>
 *         업무방 참고자료 관련 액션
 */
public class WorkReferenceAction
{

	static WorkRerferenceManager _reference = new WorkRerferenceManager();

	/**
	 * 참고자료(파일) 다운로드
	 */
	public static class DownloadByUser extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			WorkReference att = (WorkReference) DbService.load(WorkReference.class, ctx.getLong("id"));

			Work server = (Work) att.getItem();
			WorkPermission.checkAttachUser(server, mp);
			WorkHistory.referenceDownloaded(server, att);
			WorkItemHistoryManager.history(WorkItemHistory.REFERENCE_DOWNLOAD, server, att);

			ctx.store(att);
		}
	}

	/**
	 * 참고자료 수정
	 * <p>
	 * 문서조회시 파일 추가 삭제, URL 추가 삭제 등에 사용한다.
	 */
	public static class DoReferenceUpdate extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Work client = unmarshal(ctx);
			Work server = (Work) DbService.loadWithLock(Work.class, client.getId());

			// 수정 가능여부에 대한 권한체크 처리 (참고자료 수정 액션을 사용자와 관리자가 함께 사용하고 있으므로 분기 처리)
			if (ItemPermission.isAppAdmin(server, mp))
			{
				WorkPermission.checkUpdateByAdmin(server);
			}
			else
			{
				ItemPermission.checkUser(server, mp);
				WorkPermission.checkUpdate(server);
			}

			Set<? extends WorkReference> set = client.getWorkReferences();

			_reference.setMp(ctx.getModuleParam());

			for (WorkReference clientAtt : set)
			{
				_reference.setCommonValue(clientAtt, server);
				_reference.update(clientAtt);
				_reference.history(clientAtt);
			}
			DbService.commit();
			_factory.marshal(ctx.getWriter(), server);
		}
	}

	/**
	 * 관리자 권한으로 참고자료(파일) 다운로드
	 */
	public static class DownloadByAdmin extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();

			WorkReference att = (WorkReference) DbService.load(WorkReference.class, ctx.getLong("id"));
			Work server = (Work) att.getItem();
			ItemPermission.checkAppAdmin(server, mp);

			ctx.store(att);
		}
	}
}
