package com.kcube.work.grp;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kcube.lib.action.Action;
import com.kcube.lib.action.ActionContext;
import com.kcube.lib.jdbc.DbService;
import com.kcube.lib.json.JsonWriter;
import com.kcube.lib.sql.SqlChooser;
import com.kcube.lib.sql.SqlDelete;
import com.kcube.lib.sql.SqlDialect;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.lib.sql.SqlType;
import com.kcube.lib.xml.XmlWriter;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.UserPermission;
import com.kcube.sys.usr.UserService;

import net.sf.json.JSONObject;

/**
 * 업무방 나의업무 분류 Action 정의 Class
 * @author Soo
 */
public class WorkMyGroupUser
{
	private static Log _log = LogFactory.getLog(WorkMyGroupUser.class);

	final static String UP_KEY = "up";
	final static String DOWN_KEY = "down";

	/**
	 * 업무방 나의업무 분류 목록을 출력한다.
	 */
	public static class MyGroupList implements Action
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			WorkMyGroupSql sql = new WorkMyGroupSql();
			ResultSet rs = sql.getClassifyList();
			XmlWriter writer = new XmlWriter(ctx.getWriter());
			sql.classifyListWrite(writer, rs, false, ctx.getBoolean("useLocal"));
		}
	}

	/**
	 * 업무방 나의업무 분류 추가한다.
	 */
	public static class MyGroupAdd implements Action
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			String title = ctx.getParameter("name");
			WorkMyGroupSql sql = new WorkMyGroupSql();
			sql.classifyAdd(title);
		}
	}

	/**
	 * 업무방 나의업무 분류의 이름을 변경한다.
	 */
	public static class MyGroupUpdateName implements Action
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long id = ctx.getLong("id");
			String title = ctx.getParameter("name");
			WorkMyGroupSql sql = new WorkMyGroupSql();
			sql.classifyUpdateName(title, id);
		}
	}

	/**
	 * 업무방 나의업무 분류 삭제한다.
	 */
	public static class MyGroupDelete implements Action
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long id = ctx.getLong("id");
			WorkMyGroupSql sql = new WorkMyGroupSql();
			sql.classifyDelete(id);
		}
	}

	/**
	 * 업무방 나의업무 분류 순서를 변경 한다.
	 * <p>
	 * Action : WorkMyGroupUser.MyGroupStat
	 */
	public static class MyGroupStat implements Action
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long dirId = ctx.getLong("dirId");
			Long target = null;
			boolean upFlag = false;

			if (UP_KEY.equals(ctx.getParameter("method")))
			{
				upFlag = true;
			}
			target = (upFlag) ? ctx.getLong("upDirId") : ctx.getLong("downDirId");
			updateClassify(dirId, target, upFlag);
			DbService.flush();
			SqlChooser.getSqlObject(this, "resetSeq");

			// Ajax 호출시 json 형태로 return 되게 되어있음
			JSONObject obj = new JSONObject();
			obj.put("dirId", dirId);
			obj.put("upFlag", upFlag);
			obj.put("target", target);

			ctx.getWriter().write(obj.toString());
		}

		/**
		 * 위치를 변경한다.
		 * @param dirId
		 * @param target
		 * @param isUp
		 * @throws Exception
		 */
		public void updateClassify(Long dirId, Long target, boolean isUp) throws Exception
		{
			if (isUp)
			{
				upUpdateGroup(dirId, target);
			}
			else
			{
				downUpdateGroup(dirId, target);
			}

		}

		/**
		 * 위로 이동
		 * @param dirId
		 * @param upDirId
		 * @throws Exception
		 */
		public void upUpdateGroup(Long dirId, Long upDirId) throws Exception
		{
			StringBuffer sf = new StringBuffer(" update work_dir ");
			sf.append(" set seq_order = seq_order + 1 ");
			sf.append(" where dirId = ? and userid = ? ");
			PreparedStatement pstmt = DbService.prepareStatement(sf.toString());
			pstmt.setLong(1, upDirId);
			pstmt.setLong(2, UserService.getUserId());
			pstmt.execute();

			StringBuffer sf1 = new StringBuffer(" update work_dir ");
			sf1.append(" set seq_order = seq_order - 1 ");
			sf1.append(" where dirId = ? and userid = ? ");
			PreparedStatement pstmt1 = DbService.prepareStatement(sf1.toString());
			pstmt1.setLong(1, dirId);
			pstmt1.setLong(2, UserService.getUserId());
			pstmt1.execute();
		}

		/**
		 * 아래로 이동
		 * @param dirId
		 * @param downDirId
		 * @throws Exception
		 */
		public void downUpdateGroup(Long dirId, Long downDirId) throws Exception
		{
			StringBuffer sf = new StringBuffer(" update work_dir ");
			sf.append(" set seq_order = seq_order - 1 ");
			sf.append(" where dirId = ? and userid = ? ");
			PreparedStatement pstmt = DbService.prepareStatement(sf.toString());
			pstmt.setLong(1, downDirId);
			pstmt.setLong(2, UserService.getUserId());
			pstmt.execute();

			StringBuffer sf1 = new StringBuffer(" update work_dir ");
			sf1.append(" set seq_order = seq_order + 1 ");
			sf1.append(" where dirId = ? and userid = ? ");
			PreparedStatement pstmt1 = DbService.prepareStatement(sf1.toString());
			pstmt1.setLong(1, dirId);
			pstmt1.setLong(2, UserService.getUserId());
			pstmt1.execute();
		}

		/**
		 * SEQ_ORDER 정렬
		 * @throws Exception
		 */
		public int resetSeq() throws Exception
		{
			StringBuffer sf = new StringBuffer();
			sf.append(" UPDATE work_dir t SET seq_order = ");
			sf.append("(	");
			sf.append("	select rnum from ");
			sf.append("		(select a.dirid, row_number() over (order by a.seq_order asc) as rnum from work_dir a) r ");
			sf.append("	where r.dirid = t.dirid ");
			sf.append(") ");
			sf.append(" WHERE userid = ?  ");

			PreparedStatement pstmt = DbService.prepareStatement(sf.toString());
			pstmt.setLong(1, UserService.getUserId());
			int cnt = pstmt.executeUpdate();
			if (_log.isInfoEnabled())
			{
				_log.info("resetSeq cnt:" + cnt);
			}
			return cnt;
		}

		/**
		 * SEQ_ORDER 정렬 MySQL
		 * <p>
		 * MySql 5.7에는 row_number() 가 없음. 별도 분리
		 * @throws Exception
		 */
		@SqlType(dbmsType = SqlChooser.MYSQL, methodName = "resetSeq")
		public int resetSeqMySQL() throws Exception
		{
			StringBuffer sf = new StringBuffer();
			sf.append(" UPDATE work_dir t SET seq_order = ");
			sf.append(" (SELECT rnum ");
			sf.append(" FROM (SELECT a.dirid, (@row_number := @row_number + 1) AS rnum ");
			sf.append(" 	  FROM work_dir a, (SELECT @row_number := 0) x ");
			sf.append(" 	  WHERE userid = ? ORDER BY seq_order ) r ");
			sf.append("	WHERE r.dirid = t.dirid ");
			sf.append(" ) WHERE t.userid = ?  ");

			PreparedStatement pstmt = DbService.prepareStatement(sf.toString());
			pstmt.setLong(1, UserService.getUserId());
			pstmt.setLong(2, UserService.getUserId());
			int cnt = pstmt.executeUpdate();
			if (_log.isInfoEnabled())
			{
				_log.info("resetSeqMySQL cnt:" + cnt);
			}
			return cnt;
		}

	}

	/**
	 * 나의 업무 분류를 지정한다
	 * @author 성재호 <BR>
	 *         기존 하나씩 셋하는 구조를 <BR>
	 *         배열 구조로 변경.
	 * @author 수정 - 신운재
	 */
	public static class SetMyGroup implements Action
	{
		public void execute(ActionContext ctx) throws Exception
		{
			ModuleParam mp = ctx.getModuleParam();
			UserPermission.setModuleMenu(mp);

			Long[] itemId = ctx.getLongValues("id");
			Long targetNode = ctx.getLong("targetNode"); // 선택된 분류값

			for (int i = 0; i < itemId.length; i++)
			{
				if (targetNode.equals(-1L))
				{
					SqlDelete del = new SqlDelete("WORK_DIR_ITEM");
					del.where("ITEMID = ?", itemId[i]);
					del.where("USERID = ?", UserService.getUserId());
					del.execute();
				}
				else if (!isExist(itemId[i]))
				{
					StringBuffer sf = new StringBuffer();
					sf.append("	INSERT INTO WORK_DIR_ITEM	");
					sf.append("	(DIRID,ITEMID,USERID,RGST_DATE,SEQ_ORDER)	");
					sf.append(
						"	VALUES(?, ?, ?, "
							+ SqlDialect.sysdate()
							+ ", (SELECT seqOrder "
							+ "     FROM (SELECT "
							+ SqlDialect.nvl("MAX(WORK_DIR_ITEM.SEQ_ORDER) + 1", "0")
							+ " seqOrder");
					sf.append("	FROM WORK_DIR_ITEM) a ) )");

					PreparedStatement pstmt = DbService.prepareStatement(sf.toString());
					pstmt.setLong(1, targetNode);
					pstmt.setLong(2, itemId[i]);
					pstmt.setLong(3, UserService.getUserId());

					pstmt.execute();
				}
				else
				{
					StringBuffer sf = new StringBuffer();

					sf.append(" UPDATE WORK_DIR_ITEM ");
					sf.append("  SET DIRID = ? ");
					sf.append(
						", SEQ_ORDER = (SELECT seqOrder "
							+ "			  FROM (SELECT "
							+ SqlDialect.nvl("MAX(WORK_DIR_ITEM.SEQ_ORDER) + 1", "0")
							+ " seqOrder");
					sf.append(" FROM WORK_DIR_ITEM) a ) ");
					sf.append(" WHERE ITEMID = ?");
					sf.append(" AND USERID = ?");

					PreparedStatement pstmt = DbService.prepareStatement(sf.toString());
					pstmt.setLong(1, targetNode);
					pstmt.setLong(2, itemId[i]);
					pstmt.setLong(3, UserService.getUserId());

					pstmt.execute();
				}
			}
		}
	}

	/**
	 * 존재여부를 돌려준다.
	 * @param itemId
	 * @return
	 * @throws Exception
	 */
	public static boolean isExist(Long itemId) throws Exception
	{
		SqlSelect sel = new SqlSelect();
		sel.select(" 1 ");
		sel.from("WORK_DIR_ITEM");
		sel.where("ITEMID = ?", itemId);
		sel.where("USERID = ?", UserService.getUserId());
		return sel.query().next();
	}

	/***
	 * 업무방 나의업무 분류 목록에서 이름을 출력한다.
	 */
	public static class MyGroupName implements Action
	{
		public void execute(ActionContext ctx) throws Exception
		{
			SqlSelect sel = new SqlSelect();
			sel.select("title");
			sel.from("work_dir");
			sel.where("dirid = ?", ctx.getLong("folderId"));
			sel.where("userid = ?", UserService.getUserId());

			JsonWriter writer = new JsonWriter(ctx.getWriter());
			ResultSet rs = sel.query();
			if (rs.next())
			{
				writer.writeHeader();
				writer.setAttribute("title", rs.getString("title"));
				writer.writeFooter();
			}
		}
	}

}