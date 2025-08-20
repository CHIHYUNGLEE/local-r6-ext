package com.kcube.lib.sql;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import com.kcube.doc.ItemConfig;
import com.kcube.lib.jdbc.DbConfiguration;
import com.kcube.sys.conf.ConfigService;
import com.kcube.sys.i18n.I18NService;

import bsh.StringUtil;

/**
 * 목록 조회에 필요한 조건을 해석한다.
 * <p>
 * constructor에 넘어가는 목록조회 조건의 형태는 다음과 같다.
 * <p>
 * 
 * <pre>
 * 1.10.. : 1페이지, 페이지당 목록 갯수 10
 * 3.20.. : 3페이지, 페이지당 목록 갯수 20
 * 1.10.atitle. : 제목순으로 ascending 정렬
 * 1.10.did. : id 순으로 descending 정렬
 * 1.10.did_atitle. : id 순으로 descending 정렬 후 제목 순으로 ascending 정렬
 * 1.10..kwrd_자바 : keyword 컬럼에 &quot;%자바%&quot;로 like 검색
 * 1.10.did.kwrd_자바 : id 순으로 descending 정렬, keyword 컬럼 검색
 * 1.10.did.kwrd_자바|rgstDate_시작일_종료일 : rgstDate_컬럼을 시작일과 종료일로 검색
 * </pre>
 * <p>
 * title, id, kwrd등을 실제 query에 사용할 컬럼명과 맵핑시켜주는
 * <p>
 * Map 정보를 함께 넘겨주어야 한다.
 */
public class SqlPage
{
	private static final String ID = "itemid";
	private static final String TAG = "tag";
	private static final String FILENAME = "fileName";

	private Map<String, String> _aliasToColumn;
	private List<String> _i18nColumns;
	private List<String> _cryptColumns;
	private Map<String, String> _shortColumns;
	private int _page = 1;
	private int _rows = 10;
	private int _pagesPerSet = 10;
	private String _table;
	private String _alias;
	private String _column;
	private String _groupColumn;
	private String _sort;
	private String _searchColumn;
	private String _searchValue;
	private boolean _isBoth;
	private String _keyColumn;

	private String _dateColumn;// 날짜 컬럼
	private String _sDateValue;// 시작일
	private String _eDateValue;// 종료일

	private static ItemConfig _config = (ItemConfig) ConfigService.getConfig(ItemConfig.class);

	/**
	 * 목록 조회에 필요한 조건을 해석 생성자
	 * @param aliasToColumn
	 * @param state
	 */
	public SqlPage(Map<String, String> aliasToColumn, String state)
	{
		this(aliasToColumn, state, true);
	}

	/**
	 * 목록 조회에 필요한 조건을 해석 생성자
	 * @param aliasToColumn
	 * @param state
	 * @param isBoth
	 */
	public SqlPage(Map<String, String> aliasToColumn, String state, boolean isBoth)
	{
		_aliasToColumn = aliasToColumn;
		_isBoth = isBoth;
		if (state != null)
		{
			int i = state.indexOf('.');
			int j = state.indexOf('.', i + 1);
			int k = state.indexOf('.', j + 1);
			if (i >= 0 && j >= 0 && k >= 0)
			{
				_page = Integer.parseInt(state.substring(0, i));
				_rows = Integer.parseInt(state.substring(i + 1, j));
				setSort(state.substring(j + 1, k));
				setSearch(state.substring(k + 1));
			}
		}
	}

	/**
	 * 테이블명을 지정한다.
	 */
	public void setTable(String table)
	{
		_table = table;
	}

	/**
	 * 키컬럼을 지정한다. 키 컬럼명의 alias가 id가 아닐 때 검색/정렬에서 사용한다.
	 * @param keyColumn
	 */
	public void setKeyColumn(String keyColumn)
	{
		_keyColumn = keyColumn;
	}

	/**
	 * 다국어 적용 컬럼을 지정한다.
	 */
	public void setI18ncolumns(List<String> i18nColumns)
	{
		_i18nColumns = i18nColumns;
	}

	/**
	 * 암호회된 컬럼을 지정한다.
	 */
	public void setCryptColumns(List<String> cryptColumns)
	{
		_cryptColumns = cryptColumns;
	}

	/**
	 * 전화번호 뒤에 4자리 컬럼을 지정한다.
	 */
	public void setShortColumns(Map<String, String> shortColumns)
	{
		_shortColumns = shortColumns;
	}

	/**
	 * 태그 검색을 위한 테이블명, 별칭, 컬럼명을 지정한다.
	 */
	public void setTagInfo(String table, String alias, String column)
	{
		_table = table;
		_alias = alias;
		_column = column;
	}

	/**
	 * Q&A, 토론방 같이 부모글과 자식글이 한 페이지에 표시될 경우, 그룹 태그 검색여부, 컬럼명을 지정한다.
	 */
	public void setGroupTagInfo(String groupColumn)
	{
		_groupColumn = groupColumn;
	}

	/**
	 * 검색조건이 있는지의 여부를 돌려준다.
	 */
	public boolean isSort()
	{
		return (_sort != null);
	}

	/**
	 * 검색조건이 있는지의 여부를 돌려준다. <br>
	 * 1가지라도 있으면 true로 돌려준다.
	 * @return
	 */
	public boolean isSearch()
	{
		return ((_searchColumn != null && _searchValue != null)
			|| (_dateColumn != null && StringUtils.isNotEmpty(_sDateValue))
			|| (_dateColumn != null && StringUtils.isNotEmpty(_eDateValue)));
	}

	/**
	 * 정렬값을 설정한다.
	 * @param sort
	 */
	private void setSort(String sort)
	{
		if (sort.length() > 0)
		{
			_sort = sort;
		}
	}

	/**
	 * 검색조건을 설정한다.
	 * <p>
	 * @param srch
	 *        <p>
	 *        검색컬럼_검색어|기간컬럼_시작일_종료일
	 *        <p>
	 *        검색컬럼_검색어|
	 *        <p>
	 *        검색컬럼_검색어
	 *        <p>
	 *        일경우에 모두 다 처리 될 수 있도록 처리
	 */
	private void setSearch(String srch)
	{
		if (srch != null)
		{
			int i = srch.lastIndexOf("|");
			int k = srch.indexOf('_');

			if (i > -1)
			{
				String srchText = srch.substring(0, i);
				String srchDate = srch.substring(i + 1);
				int j = srchText.indexOf('_');
				_searchColumn = srchText.substring(0, j);
				_searchValue = srchText.substring(j + 1).trim();
				if (_searchValue.isEmpty())
					_searchValue = null;

				int n = srchDate.indexOf('_');
				int o = srchDate.indexOf('_', n + 1);
				if (n > -1 && o > -1)
				{
					_dateColumn = srchDate.substring(0, n);// 날짜컬럼
					_sDateValue = srchDate.substring(n + 1, o);// 시작일
					_eDateValue = srchDate.substring(o + 1);// 종료일
					// 시작일, 종료일이 13자리가 아니면 기간검색이 아니라고 판단하고 데이터가공
					if (_sDateValue.length() != 13 || _eDateValue.length() != 13)
					{
						_sDateValue = null;
						_eDateValue = null;
						if (k > -1)
						{
							_searchValue = srch.substring(k + 1).trim();
							if (_searchValue.isEmpty())
								_searchValue = null;
						}
						else
						{
							_searchValue = null;
						}
					}
				}
				else
				{
					if (k > -1)
					{
						_searchValue = srch.substring(k + 1).trim();
						if (_searchValue.isEmpty())
							_searchValue = null;
					}
					else
					{
						_searchValue = null;
					}
				}
			}
			else
			{
				if (k > -1)
				{
					_searchColumn = srch.substring(0, k).trim();
					_searchValue = srch.substring(k + 1).trim();
					if (_searchValue.isEmpty())
						_searchValue = null;
				}
				else
				{
					_searchColumn = null;
					_searchValue = null;
				}
			}
		}
	}

	/**
	 * 검색조건을 컬럼값을 가져온다.
	 * @param alias
	 * @return
	 */
	protected String getColumn(String alias)
	{
		String column = _aliasToColumn.get(alias);
		if (column == null)
		{
			throw new IllegalArgumentException("Unknown alias " + alias);
		}
		return column;
	}

	/**
	 * 검색조건을 돌려준다.
	 */
	public List<SqlFragment> search()
	{
		List<SqlFragment> conditions = new ArrayList<SqlFragment>();
		if (_searchColumn != null && _searchValue != null)
		{
			conditions.add(getSearch());
		}

		if (StringUtils.isNotEmpty(_dateColumn))
		{
			try
			{
				// 시작일 검색
				if (StringUtils.isNotEmpty(_sDateValue))
				{
					conditions.add(Sql.searchDate(getColumn(_dateColumn), _sDateValue, true));
				}

				// 종료일 검색
				if (StringUtils.isNotEmpty(_eDateValue))
				{
					conditions.add(Sql.searchDate(getColumn(_dateColumn), _eDateValue, false));
				}
			}
			catch (Exception e)
			{
				// 날짜 검색으로 인한 오류 방지 차원
			}
		}

		return conditions;
	}

	/**
	 * 기본 검색조건을 돌려준다.
	 * @return
	 */
	private SqlFragment getSearch()
	{
		if (TAG.equals(_searchColumn))
		{
			return tagSearch();
		}
		else if (FILENAME.equals(_searchColumn))
		{
			return fileNameSearch();
		}
		else if (isIncludeColumn(_searchColumn, _i18nColumns))
		{
			String column = getColumn(_searchColumn);
			return i18nSearch(column, _searchValue);
		}
		else if (isIncludeColumn(_searchColumn, _cryptColumns))
		{
			String column = getColumn(_searchColumn);
			String subStrColumn = StringUtils.substringAfter(column, ".");
			if (_shortColumns.containsKey(column))
			{
				String shortColumn = _shortColumns.get(column);
				return Sql.searchCrypt(column, shortColumn, _searchValue);
			}
			else if (_shortColumns.containsKey(subStrColumn))
			{
				String shortColumn = _shortColumns.get(subStrColumn);
				return Sql.searchCrypt(column, shortColumn, _searchValue);
			}
			else
			{
				return Sql.searchCrypt(column, _searchValue);
			}
			// return Sql.searchCrypt(column, _searchValue);
		}
		else
		{
			if (_searchColumn.contains(","))
			{
				try
				{
					return InColumnLikeStringArray(_searchColumn.split(","), _searchValue);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			String column = getColumn(_searchColumn);
			return Sql.search(column, _searchValue, _isBoth);
		}
	}

	/**
	 * tag검색을 돌려준다.
	 */
	private SqlFragment tagSearch()
	{
		if (_alias != null && _column != null)
		{
			if (_groupColumn != null)
			{
				return Sql.searchGroupTag(_table, _column, _groupColumn, _searchValue, _alias);
			}
			else
			{
				return Sql.searchTag(_table, _column, _searchValue, _alias);
			}
		}
		else
		{
			return Sql.searchTag(_table, ID, _searchValue);
		}
	}

	/**
	 * 파일명 검색을 돌려준다.
	 */
	private SqlFragment fileNameSearch()
	{
		if (_alias != null && _column != null)
		{
			if (_groupColumn != null)
			{
				return Sql.searchGroupFile(_table, _column, _groupColumn, _searchValue, _alias, _isBoth);
			}
			else
			{
				return Sql.searchFile(_table, _column, _searchValue, _alias, _isBoth);
			}
		}
		else
		{
			return Sql.searchFile(_table, ID, _searchValue, _isBoth);
		}
	}

	/**
	 * 다국어 컬럼 검색을 돌려준다.
	 */
	private SqlFragment i18nSearch(String column, String searchValue)
	{
		return Sql.searchI18N(column, searchValue);
	}

	/**
	 * Count hidden 용 Fragment 를 돌려준다.
	 */
	public SqlFragment rownum()
	{
		return new SqlFragment()
		{
			public boolean isValid()
			{
				return true;
			}

			public void make(StringBuffer query)
			{
				query.append(SqlDialect.rownum("?"));
			}

			public int bind(PreparedStatement pstmt, int index, int loop) throws Exception
			{
				pstmt.setInt(index, getVisibleCount());
				return index + 1;
			}
		};
	}

	/**
	 * 정렬 조건을 돌려준다.
	 * <p>
	 * 설정된 정렬 조건이 없을 경우 default 조건을 돌려준다.
	 */
	public String order(String defaultSort)
	{
		if (_sort == null)
		{
			return defaultSort;
		}
		StringBuffer sort = new StringBuffer();
		StringTokenizer st = new StringTokenizer(_sort, "_");
		int orderCnt = 0;
		String orderColumn = null;

		while (st.hasMoreTokens())
		{
			String token = st.nextToken();
			orderColumn = getColumn(token.substring(1));
			sort.append(getRegOrder(orderColumn));
			sort.append((token.charAt(0) == 'd') ? " DESC" : " ASC");
			sort.append(st.hasMoreTokens() ? ", " : "");
			orderCnt++;
		}

		// 올바른 정렬 결과를 위해서 컬럼1개 sort할 때는 key 컬럼을 정렬에 추가한다.
		// Sql.java에서 id를 itemid로 제한하고 있다. 그리고 alias는 id로 제한한다.
		String idColumn = (_keyColumn != null ? _keyColumn : _aliasToColumn.get("id"));
		if (orderCnt == 1 && idColumn != null && !idColumn.equals(orderColumn))
		{
			sort.append(", ");
			sort.append(idColumn);
			sort.append(" ASC");
		}

		return sort.toString();
	}

	/**
	 * JSON 형식 컬럼일 경우 Text 앞 JSON Tag를 제거하여 정렬한다.
	 */
	public static String getRegOrder(String column)
	{
		if (_config.isRequiredMultiLang())
		{
			String[] multiColumns = null;
			String multiColumn = _config.getMultiLangColumn();
			if (multiColumn != null && !multiColumn.equals(""))
				multiColumns = StringUtil.split(multiColumn.toUpperCase(), ",");

			if (multiColumns == null)
			{
				return column;
			}
			else
			{
				int idx = column.indexOf(".");
				String pureColumn = (idx >= 0) ? column.substring(idx + 1) : column;
				pureColumn = pureColumn.toUpperCase();
				for (String mcol : multiColumns)
				{
					int aIdx = mcol.indexOf("*");
					if ((aIdx == 0
						&& pureColumn.indexOf(mcol.substring(1)) >= 0
						&& pureColumn.indexOf(mcol.substring(1)) == pureColumn.length() - mcol.length() + 1)
						|| (aIdx == mcol.length() - 1 && pureColumn.indexOf(mcol.substring(0, mcol.length() - 1)) == 0)
						|| mcol.equals(pureColumn))
					{
						if (!DbConfiguration.isPostgreSql())
						{
							String lang = I18NService.getLanguage();
							String json1 = SqlDialect.jsonValue(column, "$." + lang);
							String json2 = SqlDialect.jsonValue(
								column,
								"$." + I18NService.getDefaultLocale().getLanguage());
							return SqlDialect.nvl(json1, SqlDialect.nvl(json2, column));
						}
						else
						{
							String insCol = SqlDialect.instr(column, "{\"");
							String subCol = SqlDialect.substring(column, "8");
							return SqlDialect.caseWhenThen(insCol, "1", subCol, column);
						}
					}
				}
			}
		}
		return column;
	}

	/**
	 * 정렬 조건을 돌려준다.
	 */
	public String order()
	{
		return order(null);
	}

	/**
	 * 건너 뛸 갯수를 돌려준다.
	 */
	public int skip()
	{
		return (_page - 1) * _rows;
	}

	/**
	 * 출력할 최대 갯수를 돌려준다.
	 */
	public int max()
	{
		return _rows;
	}

	/**
	 * 페이징 출력에 필요한 최대 갯수를 돌려준다.
	 */
	public int getVisibleCount()
	{
		return ((_page - 1) / _pagesPerSet + 1) * _pagesPerSet * _rows + 1;
	}

	/*
	 * 컬럼셋에 포함되는 컬럼인지 여부를 돌려준다. 검색에서 사용함.
	 * @param columnName
	 */
	private boolean isIncludeColumn(String columnName, List<String> columns)
	{
		if (columnName != null && columns != null)
		{
			String[] columnArr = columnName.split(",");
			for (int i = 0; i < columnArr.length; i++)
			{
				String col = _aliasToColumn.get(columnArr[i]);
				if (col == null)
				{
					throw new IllegalArgumentException("Unknown alias " + columnArr[i]);
				}

				if (StringUtils.indexOf(col, ".") > 0)
				{
					col = StringUtils.substringAfter(col, ".");
				}

				for (String column : columns)
				{
					if (column.toUpperCase().equals(col.toUpperCase()))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	public SqlFragment InColumnLikeStringArray(String[] columns, String value) throws Exception
	{
		return new SqlFragment()
		{
			private String[] _columns = columns;
			private String _value = value;

			public boolean isValid()
			{
				return (_columns != null && _value != null);
			}

			public void make(StringBuffer query)
			{
				query.append("(");

				for (int i = 0; i < columns.length; ++i)
				{
					String column = getColumn(columns[i]);
					if (i > 0)
					{
						query.append(" OR ");
					}

					query.append(column).append(" LIKE ? ");
				}

				query.append(")");
			}

			public int bind(PreparedStatement pstmt, int index, int loop) throws Exception
			{
				int i;
				for (i = 0; i < columns.length; ++i)
				{
					if (DbConfiguration.isMsSql())
					{
						pstmt.setNString(index + i, '%' + value.toUpperCase() + '%');
					}
					else
					{
						pstmt.setString(index + i, '%' + value.toUpperCase() + '%');
					}
				}

				return index + i;
			}
		};
	}
}