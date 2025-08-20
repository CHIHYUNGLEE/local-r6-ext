package com.kcube.work;

import java.sql.PreparedStatement;

import com.kcube.lib.sql.SqlFragment;
import com.kcube.sys.module.ModuleParam;
import com.kcube.sys.usr.UserService;
import com.kcube.sys.usr.UserState;

/**
 * 문서의 보안레벨을 체크하는 Sql 처리
 */
public class WorkItemSecuritySql
{
	/**
	 * 문서의 보안레벨에 따라 보안 체크에 사용되는 Fragment 를 돌려준다.
	 * <p>
	 * UserState.append 를 Fragment 형태로 돌려준다.
	 */
	public SqlFragment getSecFragment(String alias, String table)
	{
		return new WorkItemSecurity(table, alias);
	}

	/**
	 * 문서의 보안레벨에 따라 보안 체크에 사용되는 Fragment 를 돌려준다.
	 * <p>
	 * UserState.append 를 Fragment 형태로 돌려준다.
	 */
	public SqlFragment getSecFragment(String alias, String table, SqlFragment fragments)
	{
		return new WorkItemSecurity(table, alias, fragments);
	}

	/**
	 * 문서의 보안레벨에 따라 보안 체크에 사용되는 Fragment 를 돌려준다.
	 * <p>
	 * UserState.append 를 Fragment 형태로 돌려준다.
	 */
	public SqlFragment getSecFragment(String alias, String table, boolean checkScrt)
	{
		return new WorkItemSecurity(table, alias, checkScrt);
	}

	/**
	 * 문서의 보안레벨에 따라 보안 체크에 사용되는 Fragment 를 돌려준다.
	 * <p>
	 * UserState.append 를 Fragment 형태로 돌려준다.
	 */
	public SqlFragment getSecFragment(String alias, String table, ModuleParam mp) throws Exception
	{
		return new WorkItemSecurity(table, alias, mp);
	}

}

/**
 * k.scrt_level < 6000 OR EXISTS (SELECT itemid FROM km_item_scrt WHERE itemid = k.itemid
 * AND xid IN (,,,,) )
 */
class WorkItemSecurity implements SqlFragment
{
	private String _table;
	private String _alias;
	private SqlFragment _fragments;
	private ModuleParam _mp;
	private boolean _checkScrt;

	WorkItemSecurity(String table, String alias)
	{
		_table = table;
		_alias = alias;
		_fragments = UserService.getUserState().getXidsFragment();
	}

	WorkItemSecurity(String table, String alias, boolean checkScrt)
	{
		_table = table;
		_alias = alias;
		_fragments = UserService.getUserState().getXidsFragment();
		_checkScrt = checkScrt;
	}

	WorkItemSecurity(String table, String alias, SqlFragment fragments)
	{
		_table = table;
		_alias = alias;
		_fragments = fragments;
	}

	WorkItemSecurity(String table, String alias, ModuleParam mp) throws Exception
	{
		_table = table;
		_alias = alias;
		_mp = mp;
		_fragments = UserService.getUserState().getXidsFragment();
	}

	public boolean isValid()
	{
		if (_table == null || _alias == null || _fragments == null)
			return false;
		return true;
	}

	public void make(StringBuffer query)
	{
		query.append("(");
		if (_checkScrt)
		{
			query.append(_alias).append(".scrt_level = ");
			query.append(UserState.SECURE_NONE);
		}
		else
		{
			query.append(_alias).append(".scrt_level < ");
			query.append(UserState.SECURE_LIST);
		}

		makeNotMenu(query);
		query.append(" OR EXISTS (SELECT itemid FROM ");
		query.append(_table);
		query.append(" WHERE itemid = ").append(_alias);
		query.append(".itemid ");
		makeMenu(query);
		query.append(" AND ");
		_fragments.make(query);
		query.append(") )");
	}

	public int bind(PreparedStatement pstmt, int index, int loop) throws Exception
	{
		if (_mp != null && _mp.isModuleMenu())
		{
			pstmt.setLong(index++, _mp.getAppId());
			pstmt.setLong(index++, _mp.getAppId());
		}
		return _fragments.bind(pstmt, index, loop);
	}

	/*
	 * 모니터링 모듈에서 타 메뉴는 보안체크 없이 출력
	 */
	private void makeNotMenu(StringBuffer query)
	{
		if (_mp != null && _mp.isModuleMenu())
		{
			query.append(" OR ");
			query.append(_alias).append(".appid <> ? ");
		}
	}

	/*
	 * 모니터링 모듈에서 자기 앱은 보안체크
	 */
	private void makeMenu(StringBuffer query)
	{
		if (_mp != null && _mp.isModuleMenu())
		{
			query.append(" AND ");
			query.append(_alias).append(".appid = ? ");
		}
	}

}
