package com.kcube.cst.local;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * db에는 data가 있으나 실제파일이 존재하지 않으면 db에서 data를 삭제한다.
 * @author drajaki
 */

public class CheckKMSDBFile
{
	// CS EKP 운영서버
	String url = "jdbc:oracle:thin:@125.140.114.5:1521:orcl";
	String userId = "EKPR6";
	String userPw = "r6cselql5%";
	String source = "/repository/kcube-repository";

	// // CS EKP 운영서버
	// String url = "jdbc:oracle:thin:@125.140.114.5:1521:orcl";
	// String userId = "r6test";
	// String userPw = "R6QWERTY";
	// String source = "C:\\cs-kms-repository";

	Connection conn;

	/**
	 * Base
	 * @throws Exception
	 */

	public CheckKMSDBFile() throws Exception
	{
		conn = getConnection(url, userId, userPw);
	}

	/**
	 * @throws Exception
	 */

	public void close() throws Exception
	{
		if (conn != null)
			conn.close();
	}

	/**
	 * 삭제 수행
	 */

	public void start(String table) throws Exception
	{
		try
		{
			boolean isOk = true;// true로 해야 삭제 및 수정 진행.
			System.out.println("CHECK Table[" + table + "] Repository[" + source + "]");

			String str1 = "fileid";
			String str2 = "itemid";
			String str3 = "save_path";
			String str4 = "file_size";

			String query = "select " + str1 + ", " + str2 + ", " + str3 + ", " + str4 + " from " + table;
			String del_query = "delete from " + table + " where " + str1 + " = ?";
			String upd_query = "update " + table + " set file_size = ? where " + str2 + " = ?";
			PreparedStatement psmt = conn.prepareStatement(query);
			PreparedStatement del_stmt = null;
			PreparedStatement upd_stmt = null;
			ResultSet rs = psmt.executeQuery();
			int total = 0;
			int noExist = 0;
			int sizeDiff = 0;
			while (rs.next())
			{
				long fileid = rs.getLong(str1);
				long itemid = rs.getLong(str2);
				String path = rs.getString(str3);
				long db_size = rs.getLong(str4);
				if (!path.startsWith(java.io.File.separator))
				{
					path = java.io.File.separator + path;
				}
				File f = new File(source + path);

				if (!f.exists())
				{
					System.out.println("Not Found: fileid[" + fileid + "] itemid[" + itemid + "] [" + path + "]");

					if (isOk)
					{
						// delete row
						del_stmt = conn.prepareStatement(del_query);
						del_stmt.setLong(1, fileid);
						del_stmt.executeUpdate();
						del_stmt.close();
					}

					noExist++;
				}
				else if (f.length() != db_size)
				{
					System.out.println(
						"Size Diff: fileid[" + fileid + "] itemid[" + itemid + "] [" + path + "] [" + f.length() + "]");

					if (isOk)
					{
						// Size Restore
						upd_stmt = conn.prepareStatement(upd_query);
						upd_stmt.setLong(1, f.length());
						upd_stmt.setLong(2, fileid);
						upd_stmt.executeUpdate();
						upd_stmt.close();
					}
					sizeDiff++;
				}
				total++;
			}
			System.out.println(
				"CHECK Table["
					+ table
					+ "] TOTAL["
					+ total
					+ "] SIZEDIFF["
					+ sizeDiff
					+ "]"
					+ "] NOEXISTS["
					+ noExist
					+ "]");

			// conn.commit();
			psmt.close();
			System.out.println("--------------------------------------------------------");
			System.out.println("");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * DB 접속
	 * @param url
	 * @param userId
	 * @param userPw
	 * @return
	 * @throws Exception
	 */
	public static Connection getConnection(String url, String userId, String userPw) throws Exception
	{
		Connection conn = null;
		try
		{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(url, userId, userPw);
		}
		catch (SQLException e)
		{
			System.err.println("Driver Loading Error: " + e.getMessage());
		}
		return conn;
	}
}
