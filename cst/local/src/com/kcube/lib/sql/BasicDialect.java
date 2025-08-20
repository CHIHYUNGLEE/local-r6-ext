package com.kcube.lib.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.boot.MetadataBuilder;

import com.kcube.lib.secure.SecureUtils;

/**
 * 기본 Database Dialect
 */
public class BasicDialect
{
	protected static final String PAGING_SELECT = "SELECT n.* ";
	protected static final String PAGING_FROM = " FROM (SELECT ROWNUM rnum, m.* FROM (";
	protected static final String PAGING_WHERE = ") m WHERE ROWNUM <= ?) n WHERE rnum > ?";
	protected static final String ROWNUM = "ROWNUM <= ";
	protected static final String LOCK_WAIT_TIMEOUT = "300";

	public BasicDialect()
	{
	}

	public BasicDialect(MetadataBuilder metadataBuilder)
	{
	}

	/**
	 * 해당 테이블에 lock을 거는 query를 돌려준다.
	 * <p>
	 * ORACLE에서는 WAIT 구문을 사용한다. 1200초 동안 lock_wait_timeout을 가지도록 한다.
	 */
	String lock(String table, String col, String lockName)
	{
		return "SELECT "
			+ col
			+ " FROM "
			+ table
			+ " WHERE "
			+ col
			+ " = '"
			+ lockName
			+ "' FOR UPDATE WAIT "
			+ LOCK_WAIT_TIMEOUT;
	}

	/**
	 * upper case로 돌려주는 연산을 하는 sql expression을 돌려준다.
	 */
	String upper(String column)
	{
		StringBuffer sf = new StringBuffer();
		sf.append(" UPPER(").append(column).append(") ");
		return sf.toString();
	}

	/**
	 * like 검색에 사용할 값을 돌려준다.
	 * <p>
	 * 값 앞뒤로 '%' 문자를 추가하여 돌려준다.
	 * <p>
	 * 값이 없을 경우 null을 돌려준다.
	 * <p>
	 * TableState와 같이 작성자(이름)검색시 값 뒤에만 '%' 후위 like 검색
	 */
	String getSearchValue(String value, boolean isLeftMatch)
	{
		if (value == null)
		{
			return null;
		}
		value = value.trim();
		if (value.length() == 0)
		{
			return null;
		}
		value = value.toUpperCase();

		return isLeftMatch ? value + '%' : '%' + value + '%';
	}

	/**
	 * decode 연산을 하는 sql expression을 돌려준다.
	 */
	String decode(String column, String... args)
	{
		StringBuffer sf = new StringBuffer();
		sf.append(" DECODE(").append(column);
		for (String arg : args)
		{
			sf.append(", ").append(arg);
		}
		sf.append(") ");
		return sf.toString();
	}

	/**
	 * Sql Case 연산을 하는 sql expression을 돌려준다.
	 * <p>
	 * Searched case expression 만 지원한다. Equivalent simple case expression 필요한가?
	 */
	String caseWhenThen(String column, String... args)
	{
		StringBuffer sf = new StringBuffer();
		int length = args.length;
		sf.append(" CASE");
		for (int i = 0; i < length; i++)
		{
			if (i % 2 == 0)
			{
				if (i == length - 1)
					sf.append(" ELSE ");
				else
				{
					sf.append(" WHEN ");
					sf.append(column);

					if ("null".equalsIgnoreCase(args[i]))
						sf.append(" IS ");
					else
						sf.append(" = ");
				}

				sf.append(args[i]);
			}
			else
			{
				sf.append(" THEN ");
				sf.append(args[i]);
			}
		}
		sf.append(" END ");
		return sf.toString();
	}

	/**
	 * trunc 연산을 하는 sql expression을 돌려준다. 소수점 절삭용도로만 사용한다.
	 * <p>
	 * idx에서 값을 절삭한다. 숫자 소수점 처리에서만 사용한다. Date는 truncDate를 사용한다. 오라클 외 DB에서 구분해서 처리해야함.
	 */
	String trunc(String column, String idx)
	{
		StringBuffer sf = new StringBuffer();
		sf.append(" TRUNC(").append(column).append(", ").append(idx).append(") ");
		return sf.toString();
	}

	/**
	 * round 연산을 하는 sql expression을 돌려준다.
	 * <p>
	 * idx 에서 값을 반올림한다.
	 */
	String round(String column, String idx)
	{
		StringBuffer sf = new StringBuffer();
		sf.append(" ROUND(").append(column).append(", ").append(idx).append(") ");
		return sf.toString();
	}

	/**
	 * Date 를 년월일 00:00:00 로 계산하는 sql expression을 돌려준다.
	 */
	String truncDate(String dateColumn)
	{
		StringBuffer sf = new StringBuffer();
		sf.append(" TRUNC(").append(dateColumn).append(") ");
		return sf.toString();
	}

	/**
	 * Date 를 전달된 포맷에 따라 00:00:00 로 계산하는 sql expression을 돌려준다.
	 */
	String truncFormatDate(String dateColumn, String format)
	{
		StringBuffer sf = new StringBuffer();
		sf.append(" TRUNC(").append(dateColumn).append(", '").append(format).append("') ");
		return sf.toString();
	}

	/**
	 * 오늘 날짜에서 전달된 일수를 더한 월의 1일을 구해주는 sql expression을 돌려준다.
	 * <p>
	 * ex) 오늘 하루전날의 월에 1일 firstOfMonth("-1")
	 * <p>
	 * ex) bind 변수 사용시에는 : firstOfMonth("?") 이렇게 쿼리를 완성해 주고 실제 값을 bind 한다.
	 */
	String firstOfMonth(String day)
	{
		StringBuffer sf = new StringBuffer();
		sf.append(" TRUNC(SYSDATE + ").append(day).append(", 'MM') ");
		return sf.toString();
	}

	/**
	 * 전달된 날짜에서 전달된 일수를 더한 날짜를 구해주는 sql expression을 돌려준다.
	 * <p>
	 * ex) 오늘날짜보다 하루 전날짜 : addDay(SqlDialect.sysdate(), "-1")
	 * <p>
	 * ex) bind 변수 사용시에는 : addDay(SqlDialect.sysdate(), "?") 이렇게 쿼리를 완성해 주고 실제 값을 bind 한다.
	 */
	String addDay(String column, String day)
	{
		StringBuffer sf = new StringBuffer();
		sf.append(" ").append(column).append(" + ").append(day).append(" ");
		return sf.toString();
	}

	/**
	 * 전달된 날짜에서 전달된 시간을 더한 시간을 구해주는 sql expression을 돌려준다.
	 * <p>
	 * ex) 현재보다 3시간 전날짜 : addHour(SqlDialect.sysdate(), "-3")
	 * <p>
	 * ex) bind 변수 사용시에는 : addHour(SqlDialect.sysdate(), "?") 이렇게 쿼리를 완성해 주고 실제 값을 bind
	 * 한다.
	 */
	String addHour(String column, String hour)
	{
		StringBuffer sf = new StringBuffer();
		sf.append(" ").append(column).append(" + ").append(hour).append("/24").append(" ");
		return sf.toString();
	}

	/**
	 * nvl 연산을 하는 sql expression을 돌려준다.
	 */
	String nvl(String column, String value)
	{
		StringBuffer sf = new StringBuffer();
		sf.append(" NVL(").append(column).append(", ").append(value).append(") ");
		return sf.toString();
	}

	/**
	 * String 값에 대한 nvl 연산 : SQL Injection Filter 처리
	 * @param column
	 * @param value
	 * @return
	 */
	String nvlValue(String column, String value)
	{
		StringBuffer sf = new StringBuffer();
		sf.append(" NVL(").append(column).append(", ").append(SecureUtils.SQLInjectionFilter(value)).append(") ");
		return sf.toString();
	}

	/**
	 * int 값에 대한 nvl 연산
	 * @param column
	 * @param value
	 * @return
	 */
	String nvlValue(String column, int value)
	{
		StringBuffer sf = new StringBuffer();
		sf.append(" NVL(").append(column).append(", ").append(value).append(") ");
		return sf.toString();
	}

	/**
	 * 두개의 컬럼에 대한 nvl 연산
	 * @param column
	 * @param column2
	 * @return
	 */
	String nvlColumn(String column, String column2)
	{
		StringBuffer sf = new StringBuffer();
		sf.append(" NVL(").append(column).append(", ").append(column2).append(") ");
		return sf.toString();
	}

	/**
	 * sysdate 연산을 하는 sql expression을 돌려준다.
	 */
	String sysdate()
	{
		return " SYSDATE ";
	}

	/**
	 * dual Table 를 돌려준다.
	 */
	String dual()
	{
		return " DUAL ";
	}

	/**
	 * Chr type을 돌려준다.
	 */
	String getChr(int num)
	{
		return " chr(" + num + ") ";
	}

	/**
	 * 문자열을 더하는 연산을 하는 sql expression을 돌려준다.
	 */
	String concat(String... args)
	{
		StringBuffer sf = new StringBuffer();
		sf.append(" ");
		for (int i = 0; i < args.length; i++)
		{
			if (i > 0)
				sf.append("|| ");
			sf.append(args[i]).append(" ");
		}
		return sf.toString();
	}

	/**
	 * bitwise AND 연산을 하는 sql expression을 돌려준다.
	 */
	String bitand(String column, String value)
	{
		StringBuffer sf = new StringBuffer();
		sf.append(" BITAND(").append(column).append(", ").append(value).append(") ");
		return sf.toString();
	}

	/**
	 * bitwise OR 연산을 하는 sql expression을 돌려준다.
	 */
	String bitor(String column, String value)
	{
		StringBuffer sf = new StringBuffer();
		sf.append(" (").append(column).append(" + ").append(value).append(" - ").append(bitand(column, value)).append(
			") ");
		return sf.toString();
	}

	/**
	 * to_char 연산을 하는 sql expression을 돌려준다.
	 */
	String toChar(String column, String format)
	{
		StringBuffer sf = new StringBuffer();
		sf.append(" TO_CHAR(").append(column).append(", '").append(format).append("') ");
		return sf.toString();
	}

	/**
	 * substring 연산을 하는 sql expression을 돌려준다.
	 */
	String substring(String column, String beginIndex)
	{
		StringBuffer sf = new StringBuffer();
		sf.append(" SUBSTR(").append(column).append(", ").append(beginIndex).append(") ");
		return sf.toString();
	}

	/**
	 * instr 연산을 하는 sql expression을 돌려준다.
	 */
	String instr(String column, String str)
	{
		StringBuffer sf = new StringBuffer();
		sf.append(" INSTR(").append(column).append(", '").append(str).append("') ");
		return sf.toString();
	}

	/**
	 * substring 연산을 하는 sql expression을 돌려준다.
	 */
	String substring(String column, String beginIndex, String endIndex)
	{
		StringBuffer sf = new StringBuffer();
		sf.append(" SUBSTR(").append(column).append(", ").append(beginIndex).append(", ").append(endIndex).append(") ");
		return sf.toString();
	}

	/**
	 * length 연산을 하는 sql expression을 돌려준다.
	 * <p>
	 * byte 수가 아니라 문자의 갯수를 돌려준다.
	 */
	String length(String column)
	{
		StringBuffer sf = new StringBuffer();
		sf.append(" LENGTH(").append(column).append(") ");
		return sf.toString();
	}

	/**
	 * random 연산을 하는 sql expression을 돌려준다.
	 */
	String random()
	{
		return " DBMS_RANDOM.VALUE ";
	}

	/**
	 * ceil 연산을 하는 sql expression을 돌려준다.
	 * <p>
	 * 해당 값보다 큰수중 가장작은 정수
	 */
	String ceil(String column)
	{
		StringBuffer sf = new StringBuffer();
		sf.append(" CEIL(").append(column).append(") ");
		return sf.toString();
	}

	/**
	 * mod 연산을 하는 sql expression을 돌려준다.
	 * <p>
	 * column을 value로 나눈 나머지
	 */
	String mod(String column, String value)
	{
		StringBuffer sf = new StringBuffer();
		sf.append(" MOD(").append(column).append(", ").append(value).append(") ");
		return sf.toString();
	}

	/**
	 * SqlSelect 에 paging 조건을 추가한다.
	 */
	String paging(SqlSelect select)
	{
		StringBuffer query = new StringBuffer();
		query.append(PAGING_SELECT);
		query.append(select.getSelectPaging());
		query.append(PAGING_FROM);
		select.make(query);
		query.append(PAGING_WHERE);
		return query.toString();
	}

	/**
	 * StringBuffer 에 paging 조건을 추가한다.
	 */
	void appendPaging(StringBuffer query, boolean isOrder)
	{
		query.insert(0, PAGING_SELECT + PAGING_FROM);
		query.append(PAGING_WHERE);
	}

	/**
	 * PreparedStatement에 목록 개수 조건을 설정한다.
	 */
	int bindRownum(PreparedStatement pstmt, int index, int max, int min, boolean isOrder) throws SQLException
	{
		pstmt.setInt(index++, max);
		pstmt.setInt(index++, min);
		return index;
	}

	int bindRownum(PreparedStatement pstmt, int index, int max, int min) throws SQLException
	{
		pstmt.setInt(index++, max);
		pstmt.setInt(index++, min);
		return index;
	}

	/**
	 * Duplex query에 목록 개수 조건을 추가한다.
	 */
	void appendDuplex(StringBuffer query, boolean isOrder)
	{
		query.append(PAGING_WHERE).append(") ");
	}

	/**
	 * rownum 으로 전달된 value 이하 sql을 돌려준다.
	 */
	String rownum(String value)
	{
		StringBuffer query = new StringBuffer();
		query.append(ROWNUM);
		query.append(value);
		query.append(" ");
		return query.toString();
	}

	/**
	 * rownum 으로 전달된 value 이하 sql을 돌려준다.
	 */
	String rownum(String value, boolean isOrder)
	{
		return rownum(value);
	}

	/**
	 * regexp like, case insensitive
	 */
	String regexpLike(String column, String pattern)
	{
		StringBuffer sf = new StringBuffer();
		sf.append(" REGEXP_LIKE(").append(column).append(", ");
		sf.append(pattern).append(", 'i') ");
		return sf.toString();
	}

	Long getSeqNextValue(String sequence) throws Exception
	{
		SqlSelect stmt = new SqlSelect();
		stmt.select(SqlDialect.sequenceNextval(sequence));
		stmt.from(SqlDialect.dual());
		ResultSet rs = stmt.query();
		if (rs.next())
		{
			return Long.valueOf(rs.getLong(1));
		}
		return null;
	}

	/**
	 * Sequence Nextval
	 */
	String sequenceNextval(String sequence)
	{
		StringBuffer query = new StringBuffer();
		query.append(sequence);
		query.append(".NEXTVAL");
		query.append(" ");
		return query.toString();
	}

	/**
	 * Sequence Currval
	 */
	String sequenceCurrval(String sequence)
	{
		StringBuffer query = new StringBuffer();
		query.append(sequence);
		query.append(".CURRVAL");
		query.append(" ");
		return query.toString();
	}

	/**
	 * 전달된 시퀀스의 CurrValue를 Long으로 돌려준다.
	 */
	Long getSeqCurrValue(String sequence) throws Exception
	{
		SqlSelect stmt = new SqlSelect();
		stmt.select(SqlDialect.sequenceCurrval(sequence));
		stmt.from(SqlDialect.dual());
		ResultSet rs = stmt.query();
		if (rs.next())
		{
			return Long.valueOf(rs.getLong(1));
		}
		return null;
	}

	/**
	 * column aggregate function
	 * <p>
	 * Oracle 전용 (현재 9i까지 지원을 위해서 xmlagg 사용), 추후 지원버전에 따라서 변경 필요
	 * 
	 * <pre>
	 * 9i : SUBSTR(XMLAgg(XMLELEMENT(~~~).Extract('//text()'), 3) 9i
	 * 10g : WM_CONCAT(str) 10g
	 * 11g : LISTAGG(str, ',') WITHIN GROUP(ORDER BY str) 11g
	 * </pre>
	 */
	String columnAggregate(String column)
	{
		StringBuffer query = new StringBuffer();
		query.append("substr(xmlagg(xmlelement(a,', ' || ");
		query.append(column);
		query.append(")).extract('//text()'), 3) ");
		return query.toString();
	}

	/**
	 * 전달된 SqlSelect 에 rank 함수를 더한다.
	 */
	void rankSelect(SqlSelect stmt, String order)
	{
		stmt.select("RANK() OVER (ORDER BY " + order + " DESC) rnk");
	}

	/**
	 * JSON_VALUE 함수 (Oracle 12c 이상에서 사용 가능)
	 * <p>
	 * SELECT JSON_VALUE('{a:100}', '$.a') AS value FROM DUAL; --> 100을 리턴함
	 * @param column json value 컬럼
	 * @param path json path
	 * @return
	 */
	String jsonValue(String column, String path)
	{
		StringBuffer query = new StringBuffer();
		query.append(column);
		return query.toString();
	}

}
