package com.kcube.work;

import com.kcube.doc.ItemPermission;
import com.kcube.doc.opn.Opinion;
import com.kcube.doc.opn.OpinionManager;
import com.kcube.doc.opn.OpinionPermission;
import com.kcube.doc.opn.OpinionReply;
import com.kcube.lib.action.ActionContext;
import com.kcube.lib.event.EventService;
import com.kcube.lib.jdbc.DbService;
import com.kcube.lib.jdbc.DbStorage;
import com.kcube.lib.json.JsonMapping;
import com.kcube.lib.json.JsonWriter;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.UserPermission;

/**
 * 업무방 의견 Action class
 */
public class WorkOpinion
{

	static JsonMapping _opn = new JsonMapping(Work.Opinion.class);
	static OpinionManager _opinion = new OpinionManager(Work.class, Work.Opinion.class);
	static DbStorage _opnStorage = new DbStorage(Work.Opinion.class);
	static OpinionReply _opinionReply = new OpinionReply(Work.Opinion.class);
	static WorkOpinionListener _opnListener = (WorkOpinionListener) EventService
		.getDispatcher(WorkOpinionListener.class);

	/**
	 * 의견을 등록한다.
	 */
	public static class AddOpinion extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long itemId = ctx.getLong("itemid");
			Long gid = ctx.getLong("gid", null);
			String content = ctx.getParameter("content");

			Work item = (Work) _storage.load(itemId);
			ItemPermission.checkUser(item, mp);
			Work.Opinion opn = (Work.Opinion) _opinion.addOpinion(itemId, gid, content, Work.Opinion.USER);
			if (gid == null)
			{
				WorkHistory.addedOpinion(mp, item, opn);
			}
			else
			{
				Opinion parentOpn = (Opinion) _opnStorage.load(ctx.getLong("gid"));
				WorkHistory.replyOpinion(mp, item, parentOpn, opn);
			}
			_opnListener.add(item, opn);
			_opn.marshal(ctx.getWriter(), opn);
		}
	}

	/**
	 * 의견을 수정한다.
	 */
	public static class UpdateOpinion extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			Long id = ctx.getLong("id");
			String content = ctx.getParameter("content");
			Work.Opinion opinion = (Work.Opinion) DbService.load(Work.Opinion.class, id);
			Work item = (Work) _storage.load(opinion.getItemId());
			OpinionPermission.checkOwner(opinion);
			_opinion.updateOpinion(opinion, content);
			WorkHistory.updateOpinion(item);
			_opinion.writeUpdate(new JsonWriter(ctx.getWriter()), opinion);
			_opnListener.update(item, opinion);
		}
	}

	/**
	 * 의견을 삭제한다.
	 */
	public static class DeleteOpinion extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			Long id = ctx.getLong("id");
			Work.Opinion opn = (Work.Opinion) DbService.load(Work.Opinion.class, id);
			Work item = (Work) DbService.load(Work.class, opn.getItemId());
			OpinionPermission.checkOwner(opn);
			_opinionReply.checkReply(opn);
			_opinion.deleteOpinion(opn.getId());
			WorkHistory.removedOpinion(item, opn);
			_opnListener.delete(item, opn);
		}
	}

	/**
	 * 의견 리스트를 조회한다.
	 * <p>
	 * 업무방에 대한 의견만 팝업으로 조회할때 사용한다.
	 */
	public static class ViewOpinion extends WorkAction
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long id = ctx.getLong("id");
			boolean desc = ctx.getBoolean("desc");
			Long opnLastId = ctx.getLong("opnLastId", 0);

			Work server = (Work) _storage.load(id);
			WorkPermission.checkUser(mp, server);

			boolean isSympathy = ctx.getBoolean("isSympathy");
			_factory.marshal(ctx.getWriter(), _opinion.getItem(server, opnLastId, desc, isSympathy));
		}
	}
}
