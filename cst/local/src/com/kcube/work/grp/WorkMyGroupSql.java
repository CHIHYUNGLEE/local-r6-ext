package com.kcube.work.grp;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.kcube.lib.jdbc.DbService;
import com.kcube.lib.sql.SqlDelete;
import com.kcube.lib.sql.SqlDialect;
import com.kcube.lib.xml.XmlWriter;
import com.kcube.sys.i18n.I18NService;
import com.kcube.sys.usr.UserService;

/**
 * 업무방 나의업무 분류 Sql 정의 Class
 * @author Soo
 */
public class WorkMyGroupSql
{
	private static final String SELECT_CLASSIFY = "SELECT dirid, title, userid, rgst_date, seq_order FROM WORK_DIR WHERE userid = ? ";
	private static final String SELECT_CLASSIFY_ORDER = "ORDER BY seq_order";

	/**
	 * 업무방 나의업무 분류 목록을 돌려준다.
	 * @return
	 * @throws Exception
	 */
	ResultSet getClassifyList() throws Exception
	{
		StringBuffer sf = new StringBuffer(SELECT_CLASSIFY);
		sf.append(SELECT_CLASSIFY_ORDER);
		PreparedStatement pstmt = DbService.prepareStatement(sf.toString());
		pstmt.setLong(1, UserService.getUserId());
		return pstmt.executeQuery();
	}

	/**
	 * 업무방 나의업무 분류 목록을 추가한다.
	 * @param title
	 * @throws Exception
	 */
	void classifyAdd(String title) throws Exception
	{
		StringBuffer sf = new StringBuffer("insert into work_dir");
		sf.append(" (dirid, title, userid, rgst_date, seq_order) ");
		sf.append(" values(" + SqlDialect.getSeqNextValue("SQ_WORK_DIR") + " ,?,?," + SqlDialect.sysdate() + ",");
		sf.append(" (select " + SqlDialect.nvl("max(wd.seq_order)+1", "0"));
		sf.append(" from work_dir wd where wd.userid = ?)) ");
		PreparedStatement pstmt = DbService.prepareStatement(sf.toString());
		pstmt.setString(1, title);
		pstmt.setLong(2, UserService.getUserId());
		pstmt.setLong(3, UserService.getUserId());

		pstmt.execute();
	}

	/**
	 * 업무방 나의업무 분류의 이름을 수정한다.
	 * @param title
	 * @param id
	 * @throws Exception
	 */
	void classifyUpdateName(String title, Long id) throws Exception
	{
		StringBuffer sf = new StringBuffer("update work_dir ");
		sf.append("set title = ? ");
		sf.append("where dirid = ? and userid = ? ");
		PreparedStatement pstmt = DbService.prepareStatement(sf.toString());
		pstmt.setString(1, title);
		pstmt.setLong(2, id);
		pstmt.setLong(3, UserService.getUserId());

		pstmt.execute();
	}

	/**
	 * 업무방 나의업무 분류를 삭제한다.
	 * @param id
	 * @throws Exception
	 */
	public void classifyDelete(Long id) throws Exception
	{
		SqlDelete del = new SqlDelete("work_dir");
		del.where("userid = ?", UserService.getUserId());
		del.where("dirid = ?", id);
		del.execute();
	}

	/**
	 * 업무방 나의업무 분류 목록을 출력한다. data의 양을 줄이기 위해서 writer.write(rs)를 사용하지 않는다.
	 * @param writer
	 * @param rs
	 * @param admin
	 * @param useLocalText
	 * @param isNative
	 * @throws Exception
	 */
	void classifyListWrite(XmlWriter writer, ResultSet rs, boolean admin, boolean useLocalText, boolean isNative)
		throws Exception
	{
		writer.startElement("list");
		writer.setAttribute("class", "Array");

		while (rs.next())
		{

			writer.startElement("n");
			writer.setAttribute("id", rs.getLong("dirid"));
			writer.setAttribute("pid", -1);
			writer.setAttribute("userid", rs.getInt("userid"));
			writer.setAttribute("rgst_date", rs.getDate("rgst_date"));
			writer.setAttribute("seq_order", rs.getInt("seq_order"));

			String title = rs.getString("title");
			writer.text((isNative) ? title : (useLocalText) ? I18NService.getDefaultLanguage(title) : I18NService
				.getLocalLanguage(title));
			writer.endElement("n");
		}
		writer.endElement("list");
	}

	/**
	 * 업무방 나의업무 분류 목록을 출력한다. data의 양을 줄이기 위해서 writer.write(rs)를 사용하지 않는다.
	 * @param writer
	 * @param rs
	 * @param admin
	 * @param useLocalText
	 * @throws Exception
	 */
	void classifyListWrite(XmlWriter writer, ResultSet rs, boolean admin, boolean useLocalText) throws Exception
	{
		classifyListWrite(writer, rs, admin, useLocalText, false);
	}
}