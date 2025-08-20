package com.kcube.work.chat.item;

import java.io.PrintWriter;
import java.util.Date;

import com.kcube.lib.sql.Sql;
import com.kcube.lib.sql.SqlDate;
import com.kcube.lib.sql.SqlPage;
import com.kcube.lib.sql.SqlSelect;
import com.kcube.lib.sql.SqlTable;
import com.kcube.lib.sql.SqlWriter;

/**
 * 업무방 웹채팅 관련 Sql Class
 * @author shin8901
 */
public class WorkChatItemSql
{
	private static final SqlTable WORK_CHAT_ITEM = new SqlTable("work_chat_item", "i");
	private static final SqlTable WORK_CHAT_ITEM_FILE = new SqlTable("work_chat_item_file", "f");

	private static SqlWriter _writer = new SqlWriter().putAll(WORK_CHAT_ITEM);
	private static SqlWriter _fileWriter = new SqlWriter().putAll(WORK_CHAT_ITEM_FILE);

	private String _ts;

	public WorkChatItemSql()
	{
		this(null);
	}

	public WorkChatItemSql(String ts)
	{
		this(ts, false);
	}

	public WorkChatItemSql(String ts, boolean countVisble)
	{
		_ts = ts;
		new SqlPage(WORK_CHAT_ITEM.aliasToColumn(), _ts);
	}

	public SqlSelect getVisibleSelect(Long chatId, Date minDate, Long itemId)
	{
		StringBuffer sbf = new StringBuffer();
		sbf.append("I.CHATID, I.CONTENT,  I.CSTM_FIELD1,  I.EMTCID,  I.ITEMID,  I.MSG_TYPE, ");
		sbf.append("I.RGST_DATE, I.USER_DISP,  I.USERID, I.USER_NAME, F.FILE_NAME, ");
		sbf.append("F.FILE_SIZE, F.HEIGHT, F.FILEID, F.SAVE_PATH, F.SAVE_CODE, F.WIDTH,  ");
		sbf.append("F.THUMB_SAVE_CODE, F.THUMB_SAVE_PATH");
		SqlSelect stmt = new SqlSelect();
		stmt.select(sbf.toString());
		stmt.from("WORK_CHAT_ITEM I");
		stmt.leftOuter("WORK_CHAT_ITEM_FILE F", "I.ITEMID = F.ITEMID");
		stmt.where("I.CHATID = ? ", chatId);
		if (minDate != null)
			stmt.where("i.rgst_date < ?", SqlDate.getTimestamp(minDate));
		if (itemId != null)
			stmt.where("i.itemId > ?", itemId);
		return stmt;
	}

	public SqlSelect getFileListSelect(Long chatId)
	{
		SqlSelect sub = new SqlSelect();
		sub.select("itemid");
		sub.from(WORK_CHAT_ITEM);
		sub.where("chatid =  ? ", chatId);
		sub.where(new Sql.InIntArray("msg_type", new int[] {WorkChatItem.IMG_TYPE, WorkChatItem.FILE_TYPE}));

		SqlSelect stmt = new SqlSelect();
		stmt.select(WORK_CHAT_ITEM_FILE, "list");
		stmt.where("itemid in ", sub);
		return stmt;
	}

	public void writeJson(PrintWriter writer, SqlSelect select, boolean isAsc) throws Exception
	{
		_writer.setOrder("i.rgst_date " + (isAsc ? "asc" : "desc"));
		_writer.setTagInfo("work_chat_item", "i", "itemid");
		_writer.list(writer, select, _ts);
	}

	public void writeFileJson(PrintWriter writer, SqlSelect select) throws Exception
	{
		_fileWriter.list(writer, select);
	}
}